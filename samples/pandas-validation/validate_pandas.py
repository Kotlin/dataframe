#!/usr/bin/env python3
"""Validates the Pandas side of the "Coming from Pandas" guide.

What it does:
  1. Takes every ```python block of docs/StardustDocs/topics/guides/migratingFromPandas.md, in page order,
     and executes them in one shared namespace, in a temporary directory that holds a copy of
     samples/src/test/resources/sales.csv. A typo, a missing name or a changed Pandas API fails here.
  2. Checks the numbers the page claims. Each check mirrors an assertion of the Kotlin sample with the same
     meaning in samples/.../guides/MigratingFromPandas.kt (the comment on each check names it).

  3. Runs every case of samples/src/test/resources/pandas-reference-cases.json: one Pandas expression per case,
     evaluated on sales.csv, compared with the value in `expect`. The Kotlin half of the same file is
     samples/.../guides/PandasReferenceCasesTest.kt. These cases back the rows of the reference page
     (pandasToKotlinDataFrameReference.md) whose result is easy to get wrong, in particular the rows where
     the two libraries give different results.

  4. If PANDAS_POSTGRES_URL is set (for example postgresql://user:password@127.0.0.1:5432/postgres), or
     PANDAS_POSTGRES_DOCKER=1 (a container started with Testcontainers for Python, image PANDAS_POSTGRES_IMAGE),
     runs the cases of samples/src/test/resources/pandas-postgres-cases.json against that PostgreSQL: it creates
     a schema of its own with a random name, loads sales-postgres.sql into it, evaluates the Pandas expressions
     through SQLAlchemy and drops the schema. The Kotlin half is samples/.../guides/PandasPostgresCasesTest.kt.
     It refuses an address that is not local, or has parameters, unless PANDAS_POSTGRES_ALLOW_REMOTE=1. With
     neither variable these cases are reported as NOT RUN, loudly, in the last line too.

What it does NOT do: it does not run every Pandas cell of the reference page - those are inline code cells,
not runnable blocks. Only the cases of the JSON files are executed.

Usage:  python validate_pandas.py [--page PATH] [--csv PATH] [--cases PATH] [--postgres-cases PATH]
Exit code: 0 - everything that ran holds, 1 - a block failed to run or a check failed.
Needs:  the packages of requirements.txt, and a Python that the Pandas you test supports: 3.7+ for pandas 1.x,
        3.9+ for 2.2 and later 2.x, 3.11+ for 3.x (the `requires_python` of pandas itself). Checked on pandas
        1.3.5, 2.3.3 and 3.1.1 (SQLAlchemy 1.4 with pandas 1.3.5, 2.x with the others).
"""
import argparse
import atexit
import io
import json
import os
import re
import shutil
import sys
import tempfile
import traceback
from pathlib import Path

import pandas as pd

REPO = Path(__file__).resolve().parents[2]
DEFAULT_PAGE = REPO / "docs/StardustDocs/topics/guides/migratingFromPandas.md"
DEFAULT_CSV = REPO / "samples/src/test/resources/sales.csv"
DEFAULT_CASES = REPO / "samples/src/test/resources/pandas-reference-cases.json"
DEFAULT_POSTGRES_CASES = REPO / "samples/src/test/resources/pandas-postgres-cases.json"
POSTGRES_SQL = REPO / "samples/src/test/resources/sales-postgres.sql"
LOCAL_HOSTS = ("localhost", "127.0.0.1", "::1")
BLOCK = re.compile(r"^```python\n(.*?)^```", re.S | re.M)


def extract_blocks(page: Path):
    blocks = BLOCK.findall(page.read_text(encoding="utf-8"))
    if not blocks:
        sys.exit("FAIL: no ```python blocks found in %s" % page)
    return blocks


def run_blocks(blocks, csv: Path):
    """Run all blocks in one namespace; return it, plus a copy of the frame as the first block built it."""
    ns = {}
    created = None
    written = []
    cwd = os.getcwd()
    with tempfile.TemporaryDirectory(prefix="pandas-guide-") as tmp:
        shutil.copy(csv, Path(tmp) / "sales.csv")
        os.chdir(tmp)
        try:
            for i, code in enumerate(blocks, 1):
                try:
                    exec(compile(code, "<page block %d>" % i, "exec"), ns)
                except Exception:
                    print("FAIL: page block %d raised:\n%s\n%s" % (i, code, traceback.format_exc()))
                    sys.exit(1)
                if created is None and "df" in ns:
                    created = ns["df"].copy()
            written = sorted(p.name for p in Path(tmp).iterdir() if p.name != "sales.csv")
        finally:
            os.chdir(cwd)
    return ns, created, written


failures = []
not_run = []  # groups of checks that were not run; the last line says so


def check(name, condition, detail=""):
    print(("ok    " if condition else "FAIL  ") + name + ("" if condition else "  -> " + str(detail)))
    if not condition:
        failures.append(name)


def plain(value):
    """Turn numpy / pandas scalars and containers into plain Python values so they compare with JSON."""
    if isinstance(value, (list, tuple)):
        return [plain(v) for v in value]
    if hasattr(value, "item") and not isinstance(value, (str, bytes)):
        try:
            value = value.item()
        except (ValueError, AttributeError):
            pass
    if isinstance(value, float) and value != value:  # NaN is Pandas' "missing"; Kotlin DataFrame has null
        return None
    return value


def same(expected, actual):
    """Numbers compare as doubles (1e-9), lists element by element, everything else by equality."""
    if isinstance(expected, list) and isinstance(actual, list):
        return len(expected) == len(actual) and all(same(e, a) for e, a in zip(expected, actual))
    if isinstance(expected, (int, float)) and isinstance(actual, (int, float)) \
            and not isinstance(expected, bool) and not isinstance(actual, bool):
        return abs(float(expected) - float(actual)) < 1e-9
    return expected == actual


def run_cases(cases_path: Path, csv: Path):
    """Evaluate the `pandas` expression of every case on sales.csv and compare it with `expect`."""
    cases = json.loads(cases_path.read_text(encoding="utf-8"))
    tmp = tempfile.mkdtemp(prefix="pandas-cases-")
    atexit.register(shutil.rmtree, tmp, True)

    def excel_roundtrip(frame):
        """Write `frame` to an .xlsx file and read it back: what `writeExcel` + `readExcel` do on the Kotlin side."""
        path = os.path.join(tmp, "roundtrip.xlsx")
        frame.to_excel(path, index=False)
        return pd.read_excel(path)

    # Names a case expression may use. RES is the folder of sales.csv: the other resource files live there too.
    ns = {
        "pd": pd,
        "df": pd.read_csv(csv),
        "managers": pd.DataFrame({"region": ["North", "South", "East"], "manager": ["Ann", "Bob", "Eve"]}),
        "RES": str(csv.parent) + os.sep,
        "excel_roundtrip": excel_roundtrip,
    }
    ids = [c["id"] for c in cases]
    check("cases: ids are unique", len(ids) == len(set(ids)), ids)
    for case in cases:
        try:
            actual = plain(eval(case["pandas"], dict(ns)))
        except Exception:
            check("case %s: expression runs" % case["id"], False, traceback.format_exc().strip().splitlines()[-1])
            continue
        kotlin_differs = "expectKotlin" in case
        check("case %s%s" % (case["id"], " (differs from Kotlin DataFrame)" if kotlin_differs else ""),
              same(case["expect"], actual), "expected %s, pandas gave %s" % (case["expect"], actual))
    differing = [c["id"] for c in cases if "expectKotlin" in c]
    check("cases: every difference has a note", all(c.get("note") for c in cases if "expectKotlin" in c), differing)
    print("      %d cases, %d of them differ from Kotlin DataFrame: %s" % (len(cases), len(differing), ", ".join(differing)))


def run_postgres_cases(cases_path: Path):
    """Run pandas-postgres-cases.json against a PostgreSQL, or report NOT RUN.

    The database comes from PANDAS_POSTGRES_URL (a throwaway local PostgreSQL) or, with PANDAS_POSTGRES_DOCKER=1,
    from a container started with Testcontainers for Python; its image is PANDAS_POSTGRES_IMAGE.
    """
    address = os.environ.get("PANDAS_POSTGRES_URL", "").strip()
    if address:
        _run_postgres_cases_at(address, cases_path)
    elif os.environ.get("PANDAS_POSTGRES_DOCKER") == "1":
        image = os.environ.get("PANDAS_POSTGRES_IMAGE", "").strip()
        if not image:
            check("PostgreSQL cases: PANDAS_POSTGRES_IMAGE is set", False,
                  "set it, for example postgres:18-alpine, or run through :samples:runPandasValidation")
            return
        try:
            try:
                from testcontainers.community.postgres import PostgresContainer
            except ImportError:  # older Testcontainers for Python keep it one level up
                from testcontainers.postgres import PostgresContainer
        except ImportError as e:
            check("PostgreSQL cases: testcontainers is installed", False,
                  '%s (pip install "testcontainers[postgres]")' % e)
            return
        try:
            container = PostgresContainer(image)  # the constructor already connects to the Docker engine
            container.start()
        except Exception as e:  # no Docker engine, no image, no permission: say so, do not dump a traceback
            check("PostgreSQL cases: the container started", False, "%s: %s" % (type(e).__name__, str(e)[:160]))
            return
        try:
            _run_postgres_cases_at(container.get_connection_url(), cases_path)
        finally:
            container.stop()
    else:
        not_run.append("PostgreSQL cases (neither PANDAS_POSTGRES_URL nor PANDAS_POSTGRES_DOCKER=1 is set)")
        print("NOT RUN  PostgreSQL cases: neither PANDAS_POSTGRES_URL nor PANDAS_POSTGRES_DOCKER=1 is set")


def _run_postgres_cases_at(address: str, cases_path: Path):
    from urllib.parse import urlparse
    parsed = urlparse(address)
    host = parsed.hostname or ""
    # Local only, and no parameters: `?host=other` could send the driver somewhere else than the host shows.
    if os.environ.get("PANDAS_POSTGRES_ALLOW_REMOTE") != "1" and (host not in LOCAL_HOSTS or parsed.query):
        check("PostgreSQL cases: the address is local and has no parameters", False,
              "%r is not local or has parameters; these cases create and drop a schema. Use a throwaway local "
              "database, or set PANDAS_POSTGRES_ALLOW_REMOTE=1 if you mean it" % address.split("@")[-1])
        return
    try:
        import uuid
        from sqlalchemy import create_engine
        import psycopg2  # noqa: F401  (the driver SQLAlchemy loads)
    except ImportError as e:
        check("PostgreSQL cases: sqlalchemy and psycopg2 are installed", False, "%s (pip install -r requirements.txt)" % e)
        return

    url = address.replace("postgresql://", "postgresql+psycopg2://", 1)
    schema = "pandas_guide_" + uuid.uuid4().hex[:8]
    admin = create_engine(url)
    with admin.begin() as connection:
        connection.exec_driver_sql("CREATE SCHEMA " + schema)
    engine = create_engine(url, connect_args={"options": "-c search_path=" + schema})
    try:
        with engine.begin() as connection:
            connection.exec_driver_sql(POSTGRES_SQL.read_text(encoding="utf-8"))
        cases = json.loads(cases_path.read_text(encoding="utf-8"))
        for case in cases:
            try:
                actual = plain(eval(case["pandas"], {"pd": pd, "engine": engine}))
            except Exception:
                check("postgres case %s: expression runs" % case["id"], False,
                      traceback.format_exc().strip().splitlines()[-1])
                continue
            differs = " (differs from Kotlin DataFrame)" if "expectKotlin" in case else ""
            check("postgres case %s%s" % (case["id"], differs), same(case["expect"], actual),
                  "expected %s, pandas gave %s" % (case["expect"], actual))
        check("postgres cases: every difference has a note",
              all(c.get("note") for c in cases if "expectKotlin" in c))
        print("      %d PostgreSQL cases" % len(cases))
    finally:
        engine.dispose()
        with admin.begin() as connection:
            connection.exec_driver_sql("DROP SCHEMA " + schema + " CASCADE")
        admin.dispose()


def main():
    ap = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    ap.add_argument("--page", type=Path, default=DEFAULT_PAGE)
    ap.add_argument("--csv", type=Path, default=DEFAULT_CSV)
    ap.add_argument("--cases", type=Path, default=DEFAULT_CASES)
    ap.add_argument("--postgres-cases", type=Path, default=DEFAULT_POSTGRES_CASES)
    args = ap.parse_args()

    blocks = extract_blocks(args.page)
    print("pandas %s, %d python blocks from %s" % (pd.__version__, len(blocks), args.page.name))
    ns, created, written = run_blocks(blocks, args.csv)
    df = ns["df"]

    # createDataFrame / readCsvResource: the frame built in code and the CSV hold the same data
    try:
        pd.testing.assert_frame_equal(created, df)
        frames_equal = True
    except AssertionError as e:
        frames_equal = False
        print(e)
    check("createDataFrame == sales.csv (same rows, same dtypes)", frames_equal)
    check("createDataFrame: 6 rows x 6 columns", df.shape == (6, 6), df.shape)

    # filterRows
    check("filterRows: 2 rows", len(ns["big_north"]) == 2, len(ns["big_north"]))
    # addColumn
    check("addColumn: profit", list(ns["with_profit"].profit) == [40, 60, 10, 80, 50, 40])
    # countNulls / fillNulls / dropNulls
    check("countNulls: 2", ns["n_missing"] == 2, ns["n_missing"])
    check("fillNulls: 6 rows, no gaps", len(ns["filled"]) == 6 and ns["filled"].discount.isna().sum() == 0)
    check("dropNulls: 4 rows", len(ns["dropped"]) == 4, len(ns["dropped"]))
    # nullableArithmetic: pandas silently carries NaN, Kotlin forces a decision
    check("nullableArithmetic: NaN for the 2 rows without discount", ns["net"].isna().sum() == 2)
    filled_net = list(df.revenue * (1 - df.discount.fillna(0.0)))
    check("nullableArithmetic: value with null treated as 0.0",
          filled_net == [90.0, 150.0, 76.0, 200.0, 96.0, 90.0], filled_net)
    # groupByAggregate
    check("groupByAggregate: total 370 / 370", list(ns["summary"].total) == [370, 370])
    # pivotRevenueByYear
    pv = ns["pivoted"]
    check("pivotRevenueByYear: columns 2023, 2024", list(pv.columns) == [2023, 2024], list(pv.columns))
    check("pivotRevenueByYear: North 250/120, South 80/290",
          list(pv.loc["North"]) == [250, 120] and list(pv.loc["South"]) == [80, 290])
    # leftJoin
    check("leftJoin: 7 rows (East kept)", len(ns["joined"]) == 7, len(ns["joined"]))
    # writeData
    check("writeData: out.csv and out.json were written", written == ["out.csv", "out.json"], written)

    # "Things that will surprise you" - the Pandas half of each sentence
    check("describe: only the 4 numeric columns",
          list(df.describe().columns) == ["year", "revenue", "cost", "discount"], list(df.describe().columns))
    check("percentile: quantile(0.25) == 92.5 (Kotlin DataFrame: 89.16666666666666)",
          df.revenue.quantile(0.25) == 92.5, df.revenue.quantile(0.25))
    one_letter = pd.read_csv(io.StringIO("product\nA\nB\n"))
    # The text dtype is version dependent: `object` up to pandas 2.x, `str` since pandas 3.0. Never a char type.
    check("one-letter CSV values: a string dtype (object, or str in pandas 3), not a char type",
          str(one_letter["product"].dtype) in ("object", "str"), one_letter["product"].dtype)
    # "Many operations can change the frame in place": a real inplace=True on a copy, the original is untouched.
    scratch = df.copy()
    returned = scratch.drop(columns=["cost"], inplace=True)
    check("in place: drop(inplace=True) changes the frame itself and returns None",
          returned is None and "cost" not in scratch.columns and "cost" in df.columns)
    # drop(n): Pandas removes by LABEL, Kotlin DataFrame's drop(n) removes the first n rows
    dropped = df.drop(4)
    check("drop(4): the row labelled 4 is gone, 5 rows remain", len(dropped) == 5 and 4 not in dropped.index,
          list(dropped.index))
    check("drop(columns=[...]) removes columns", "cost" not in df.drop(columns=["cost"]).columns)

    # the reference page: the cases shared with PandasReferenceCasesTest
    run_cases(args.cases, args.csv)

    # reading from PostgreSQL: needs a running database, so it is only run when asked for
    run_postgres_cases(args.postgres_cases)

    print()
    if failures:
        print("FAILED %d check(s): %s" % (len(failures), ", ".join(failures)))
        sys.exit(1)
    suffix = "" if not not_run else " (NOT RUN: %s)" % "; ".join(not_run)
    print("pandas %s - every page block runs, every check and every case that ran holds%s" % (pd.__version__, suffix))


if __name__ == "__main__":
    main()
