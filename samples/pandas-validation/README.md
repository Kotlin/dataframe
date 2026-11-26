# Validating the "Coming from Pandas" guide

Two pages are checked: the tour (`docs/StardustDocs/topics/guides/migratingFromPandas.md`) and the reference
(`docs/StardustDocs/topics/guides/pandasToKotlinDataFrameReference.md`). Everything is checked against the **same data**:
`samples/src/test/resources/sales.csv`, six rows.

| What | Kotlin DataFrame half | Pandas half |
|---|---|---|
| **Tour**: every task is shown twice (a Pandas tab and a Kotlin tab) | `guides/MigratingFromPandas.kt`: JUnit tests; Korro injects the code and the result tables into the page | `validate_pandas.py` runs **every ```` ```python ```` block of the page** and checks the numbers |
| **Reference**, Kotlin cells: does every cell of every table compile? | `guides/PandasReferenceCells.kt` is compiled; `guides/PandasReferenceGuardTest.kt` fails when a cell of the page is not in that file | - |
| **Reference**, results: does a row do what it says, in both libraries? | `guides/PandasReferenceCasesTest.kt` runs the cases of `src/test/resources/pandas-reference-cases.json` | `validate_pandas.py` runs the same cases |
| **Reference**, reading from PostgreSQL | `guides/PandasPostgresCasesTest.kt` runs `pandas-postgres-cases.json` (task `pandasPostgresTest`, needs a database) | `validate_pandas.py` runs the same cases when `PANDAS_POSTGRES_URL` is set |

A number is checked on both sides on purpose. If either library changes behaviour, one half goes red.
Rows where the two libraries give **different** results have `expectKotlin` and a `note` in the cases file; those
differences are described on the pages.

Not covered: the *Pandas* column of the reference tables is inline code, so only the cases of the JSON file are executed.
The "Do not write" column (names that do not exist) is not checked by a test: it was compiled by hand once.

Cost: the three Kotlin classes run in about 2 seconds; the Pandas script takes 1-3 seconds per Pandas version.

The Kotlin half is part of the normal `:samples:test`. The Pandas half is **not**: it needs Python with pandas, which a
Kotlin CI agent may not have, so it is a separate Gradle task, `:samples:runPandasValidation`, that nothing else depends
on. Run it in a CI job that has Python, or by hand. Where Python is missing the task fails (it never skips silently).

Shared files, all in `samples/src/test/resources/`: `sales.csv` (the table), `sales-pandas.parquet` (the same table,
**written by Pandas** because Kotlin DataFrame cannot write Parquet; regenerate it with
`pd.read_csv("sales.csv").to_parquet("sales-pandas.parquet", index=False)`), and `pandas-reference-cases.json`.

## For a human

**One-time setup.** Keep the virtual environment outside the repository. The script needs Python 3.7+ and the packages
in `requirements.txt` (pandas, openpyxl for Excel, pyarrow for Parquet). It was checked on pandas 1.3.5, 2.3.3 and 3.1.1.

```text
python3 -m venv /tmp/pandas-venv
/tmp/pandas-venv/bin/pip install -r samples/pandas-validation/requirements.txt     # or add "pandas<3" to stay on 2.x
```

On Windows use `py -m venv %TEMP%\pandas-venv` and `%TEMP%\pandas-venv\Scripts\python`.

**Run the Pandas side.** The last line must say `every page block runs, every check and every case holds`; exit code 0.

```text
./gradlew :samples:runPandasValidation -PpandasPython=/tmp/pandas-venv/bin/python
```

Without `-PpandasPython` the task uses the `PANDAS_PYTHON` environment variable, then `python3` (`python` on Windows).
The script can also be run directly: `/tmp/pandas-venv/bin/python samples/pandas-validation/validate_pandas.py`.

Run it on every Pandas version you want the guide to be true for. Only one thing differs between versions today:
the text type of a column is `object` up to Pandas 2.x and `str` since 3.0. The page says so. Pandas moves: pin the
versions in the CI job that runs this task, otherwise a new Pandas release can turn it red with no change here.

**Run the Kotlin side:**

```text
./gradlew :samples:test --tests "*guides.MigratingFromPandas*" --tests "*PandasReference*" -Pkotlin.dataframe.debug=true
```

Expect 21 + 1 + 1 tests and 0 failures. In `MigratingFromPandas` one test is skipped: `pandasReadCsv` is `@Ignore`d
on purpose, it is the line shown on the page (a path in the reader's own project); the real read is
`pandasReadCsvResource`.

**PostgreSQL cases (optional, need a database).** `pandas-postgres-cases.json` reads the table of `sales-postgres.sql`
(text, integer and double precision columns) from PostgreSQL in both libraries: `read_sql_table` / `read_sql_query` and
`readSqlTable` / `readSqlQuery`. They run only when `PANDAS_POSTGRES_URL` names a **throwaway local** PostgreSQL, for
example `postgresql://user:password@127.0.0.1:5432/postgres`. Each half creates a schema of its own with a random name,
loads the table and drops the schema at the end; both refuse an address that is not local unless
`PANDAS_POSTGRES_ALLOW_REMOTE=1`. Never point them at a database that matters.

The `initdb` route below is the one that was run (there was no Docker on that machine); the Docker command is the
usual image invocation and has not been run here.

```text
# 1. a throwaway PostgreSQL: with Docker ...
docker run --rm -d --name pandas-pg -e POSTGRES_HOST_AUTH_METHOD=trust -p 127.0.0.1:55432:5432 postgres:18-alpine
# ... or from PostgreSQL binaries you already have (the user is "pandas", only 127.0.0.1, port 55432):
PGBIN=/path/to/postgresql/bin
$PGBIN/initdb -D /tmp/pandas-pg-data -U pandas --auth=trust -E UTF8 --no-locale
$PGBIN/pg_ctl -D /tmp/pandas-pg-data -o "-p 55432 -c listen_addresses=127.0.0.1" -l /tmp/pandas-pg.log -w start

# 2. both halves, on the same address
export PANDAS_POSTGRES_URL=postgresql://pandas@127.0.0.1:55432/postgres
./gradlew :samples:pandasPostgresTest                                  # the Kotlin half
./gradlew :samples:runPandasValidation -PpandasPython=/tmp/pandas-venv/bin/python   # the Pandas half

# 3. clean up: docker stop pandas-pg   or   $PGBIN/pg_ctl -D /tmp/pandas-pg-data stop && rm -rf /tmp/pandas-pg-data
```

`:samples:pandasPostgresTest` is not part of `:samples:test`, and it fails when the variable is missing. Without the
variable the Pandas script prints `NOT RUN  PostgreSQL cases` and repeats it in its last line. That is "not run", not
"passed". The PostgreSQL cases were run against PostgreSQL 18.1 with SQLAlchemy 1.4 (pandas 1.3.5) and 2.1 (pandas 2.3.3,
3.1.1); other databases were not run.

`:samples:test` declares the reference page as an input, so editing only that page re-runs
`PandasReferenceGuardTest` (the cases file, the cells file and the samples are tracked as ordinary inputs).

**Refresh the page after changing a sample.** This is the procedure of `docs/StardustDocs/AGENTS.md`, restricted to
this guide's tests so that the other pages are not rewritten:

```text
git status --porcelain > /tmp/before.txt
rm -rf docs/StardustDocs/resources/guides/migratingFromPandas
./gradlew :samples:clean :samples:test --tests "*guides.MigratingFromPandas*" --tests "*PandasReference*"
./gradlew :samples:korro
./gradlew :samples:updateShadowResources
git status --porcelain | diff /tmp/before.txt -     # only your page, your iframes and _shadow_resources.md may change
```

Read Korro's own output for `Cannot resolve FUN` lines naming your samples: with `ignoreMissing` they do not fail the
build. `updateShadowResources` rewrites `docs/StardustDocs/topics/_shadow_resources.md` with unrelated lines from your
local `resources/` folder: copy the file aside, `git checkout --` it, and insert only the `<resource>` lines of your
own iframes at the position the generator gave them (this guide's go right before `quickstart.ipynb`).

**Add an example to the tour**

1. Add a `@Test` to `MigratingFromPandas.kt`. Put the shown code between `// SampleStart` and `// SampleEnd`,
   chain the assertion and `.saveDfHtmlSample()` after `// SampleEnd` (it runs, Korro does not inject it).
   One asserted statement per sample.
2. Prefix the function with `pandas`: iframe files are found by file name only, a duplicate shows the wrong table.
3. In the page add `<!---FUN name-->` and `<!---END-->` inside the Kotlin `<tab>`; never type a `kotlin` block.
4. Write the Pandas block in the same section as a `python` block that **assigns its result to a variable**, and add a
   `check(...)` for it in `validate_pandas.py` with the same number the Kotlin test asserts.

**Add or change a row of the reference**

1. Write the Kotlin cell in the table, in a column headed `Kotlin DataFrame...` or `Write instead`. The columns the
   tables use (`a`, `b`, `s`, `y`, `r`, `p`, `rev`, `cost`, `v`, `revenue`, `d`) are the fields of `RefRow` in
   `PandasReferenceCells.kt`.
2. Copy the cell, verbatim, into a function of `PandasReferenceCells.kt` (spacing does not matter). A cell with `...`
   is a template and is skipped.
3. If the row's *result* is easy to get wrong, add a case to `pandas-reference-cases.json` (`id`, a `pandas` expression,
   `expect`) **and** the Kotlin counterpart in `PandasReferenceCasesTest.kt`. If the libraries differ, add
   `expectKotlin` and a `note`, and describe the difference on the page. A `pandas` expression may use `pd`, `df`
   (the table), `managers`, `RES` (the resources folder, for the Parquet file) and `excel_roundtrip(frame)`; a missing
   value (`NaN`) is compared as `null`. Expressions that depend on randomness are checked by their size only.
4. Take every expected value from a real run of that side. Never copy one side's output into the other.
5. Run both sides. To see that a new guard really guards, break the expectation once, see red, put it back.

**When something fails**

| Symptom | Likely cause |
|---|---|
| `FAIL: page block N raised` | the Pandas code on the page no longer runs on this pandas version |
| `FAIL  <name> -> <value>` / `FAIL  case <id>` | the Pandas result changed; compare with the Kotlin assertion of the same name |
| `List should be empty but has ... "df...."` in `PandasReferenceGuardTest` | a table cell has no compiled twin in `PandasReferenceCells.kt` |
| `<id>: expected ... but was ...` in `PandasReferenceCasesTest` | Kotlin DataFrame now gives another result: fix the page text, not only the number |
| a Kotlin test fails after a library change | the page text is probably wrong now: fix the sentence, not only the number |
| the table on the page is empty | the iframe is missing from `_shadow_resources.md`, or its name clashes with another file |

## For an agent

Follow in order. Do not skip a step and do not report a step as passed without its output.

1. Read `AGENTS.md`, `samples/AGENTS.md`, `docs/StardustDocs/AGENTS.md`. Never run `git commit`, `git add`, `push`,
   `reset`, `rebase`. Files that were dirty before you started are not yours.
2. Snapshot: `git status --porcelain > /tmp/before.txt`.
3. Pandas environment: create a venv **outside** the repository and install `requirements.txt` (see above). If the
   packages or a Python 3.9+ (for pandas 2.x and 3.x) are not available, say so and report the Pandas side as
   **not run** for that version; do not mark it as passing.
4. Run `./gradlew :samples:runPandasValidation -PpandasPython=<venv>/bin/python` once for each pandas version you have
   and quote, for each, the last line of the script's output. The task fails when Python or a package is missing;
   that is "not run", not "passed". If the last line says `NOT RUN`, report exactly that group as not run.
4a. PostgreSQL cases: only against a throwaway **local** database you started yourself (see "PostgreSQL cases"), never
   against an existing one. Run `:samples:pandasPostgresTest` and the Pandas script with the same `PANDAS_POSTGRES_URL`,
   then stop and delete the database you started. If you cannot start one, say so and report these cases as not run.
5. Run the Kotlin side. Quote `tests/skipped/failures` from `samples/build/test-results/test/TEST-*.xml` for the three
   classes.
6. Only if you changed samples or the tour page: follow "Refresh the page" above, including the `rm -rf` of the iframe
   folder and `:samples:clean`. Then `git status --porcelain | diff /tmp/before.txt -` and revert every change that is not
   your page, your sample, your iframes or your own lines of `_shadow_resources.md` (`git checkout --` for tracked files).
7. After editing a Kotlin file here run `./gradlew :samples:ktlintTestSourceSetCheck`; do **not** run `ktlintFormat`
   on `MigratingFromPandas.kt`: it may rewrite code that the page shows.
8. Rules that hold for every edit:
   - Kotlin on the tour page comes only from `<!---FUN-->` blocks. Python blocks are hand-written and must assign results.
   - Every Kotlin claim in prose needs a test with literal values; every Pandas claim needs a `check(...)` or a case.
   - A new test must be shown to fail once: break the expectation, see red, put it back.
   - Do not edit `*/generated-sources`, `docs/StardustDocs/resources/snippets/**`, or other topics.
   - Never invent a Kotlin DataFrame function name. If it is not in `pandasToKotlinDataFrameReference.md`, compile it in a
     scratch sample first, then delete the scratch file.
9. Report in the shape: what you ran, what it printed, what you changed, what you did **not** run.
