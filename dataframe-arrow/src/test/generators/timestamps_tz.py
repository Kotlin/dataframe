"""Generates the timezone-aware Parquet timestamp fixtures for `dataframe-arrow` tests.

Regenerates, into `dataframe-arrow/src/test/resources/`:

  * `timestamps_utc_and_local.parquet` — the exact column set reported in
    https://github.com/Kotlin/dataframe/issues/926 (a UTC column, a zone-less column, a Brussels column
    converted to UTC, and nanosecond/millisecond variants), extended with a null row and a pre-1970 row.
  * `timestamps_utc_and_local.feather` — the same columns in the Arrow random-access (Feather v2) format,
    written uncompressed because the reader has no `arrow-compression` dependency. Parquet reading needs the
    `arrow-dataset` JNI library, which is unavailable on some platforms (Android, and any machine where the
    native library fails to load); this fixture keeps the same third-party-producer coverage there.
  * `timestamps_zoned.parquet` — a single `timestamp('us', tz='Europe/Berlin')` column, to check that a
    non-UTC zone in the Arrow schema still reads back as the correct instant.

These are written by Polars/PyArrow on purpose: `isAdjustedToUTC = true` timestamps are what real-world
producers emit, and reading them used to fail with `NotImplementedError` (issue #926). Committing files
produced by a third-party writer is what makes the test an interop test rather than a round-trip of our
own encoder.

Produced with:

    python -m venv .venv
    .venv/Scripts/python -m pip install polars==1.44.1 pyarrow==25.0.1 pandas==3.0.5
    .venv/Scripts/python dataframe-arrow/src/test/generators/timestamps_tz.py

Run it from the repository root; it writes next to the other fixtures and prints the resulting schemas.
Record any change in `dataframe-arrow/src/test/resources/TestFiles.md`.
"""

from datetime import datetime, timezone
from pathlib import Path

import polars as pl
import pyarrow as pa
import pyarrow.parquet as pq
from pyarrow import feather

RESOURCES = Path(__file__).resolve().parents[1] / "resources"


def utc(*args: int) -> datetime:
    return datetime(*args, tzinfo=timezone.utc)


def naive(*args: int) -> datetime:
    return datetime(*args)


def write_utc_and_local() -> Path:
    """The issue #926 column set: three rows — a normal one, an all-null one, and one before the epoch."""
    target = RESOURCES / "timestamps_utc_and_local.parquet"

    # Brussels is UTC+1 both on 2024-01-01 and in June 1962, so 12:00 local is 11:00 UTC. Written normalized
    # to UTC, as Parquet requires — this is the column that shows the original zone is not recoverable.
    brussels = pl.Series(
        [naive(2024, 1, 1, 12, 0, 0, 123456), None, naive(1962, 6, 5, 4, 3, 2, 123456)],
        dtype=pl.Datetime("us"),
    ).dt.replace_time_zone("Europe/Brussels").dt.convert_time_zone("UTC")

    frame = pl.DataFrame(
        {
            "timestamp_utc": pl.Series(
                [utc(2024, 1, 1, 12, 0, 0, 123456), None, utc(1962, 6, 5, 4, 3, 2, 123456)],
                dtype=pl.Datetime("us", "UTC"),
            ),
            "timestamp_local": pl.Series(
                [naive(2024, 1, 1, 12, 0, 0, 123456), None, naive(1962, 6, 5, 4, 3, 2, 123456)],
                dtype=pl.Datetime("us"),
            ),
            "timestamp_brussels": pl.Series(
                [brussels[0], None, utc(1962, 6, 5, 3, 3, 2, 123456)],
                dtype=pl.Datetime("us", "UTC"),
            ),
            "timestamp_nanos": pl.Series(
                ["2024-01-01 12:00:00.123456789", None, "1962-06-05 04:03:02.123456789"],
            ).str.to_datetime("%F %X%.9f", time_unit="ns").dt.replace_time_zone("UTC"),
            "timestamp_millis": pl.Series(
                ["2024-01-01 12:00:00.123", None, "1962-06-05 04:03:02.123"],
            ).str.to_datetime("%F %X%.3f", time_unit="ms").dt.replace_time_zone("UTC"),
        }
    )
    frame.write_parquet(target)

    # Same data, Arrow random-access format. Uncompressed: the reader does not depend on arrow-compression.
    feather.write_feather(frame.to_arrow(), RESOURCES / "timestamps_utc_and_local.feather", compression="uncompressed")
    return target


def write_zoned() -> Path:
    """A non-UTC zone name in the Arrow schema. PyArrow persists it in the `ARROW:schema` metadata."""
    target = RESOURCES / "timestamps_zoned.parquet"

    table = pa.table(
        {
            "timestamp_berlin": pa.array(
                [utc(2024, 1, 1, 12, 0, 0, 123456), None, utc(1962, 6, 5, 4, 3, 2, 123456)],
                type=pa.timestamp("us", tz="Europe/Berlin"),
            ),
        }
    )
    pq.write_table(table, target)
    return target


if __name__ == "__main__":
    for path in (write_utc_and_local(), write_zoned()):
        parquet_file = pq.ParquetFile(path)
        print(f"=== {path.name} ({path.stat().st_size} bytes, {parquet_file.metadata.num_rows} rows)")
        print(parquet_file.schema)
        print(parquet_file.schema_arrow)
