# dataframe-arrow test fixtures

Reference for the binary test fixtures under `dataframe-arrow/src/test/resources/`. When adding a fixture, record its
schema and **how it was produced** here so the data stays reproducible and reviewable.

## Parquet

| File | Purpose | Schema (summary) | Provenance |
|------|---------|------------------|------------|
| `nullable_nested_struct.parquet` | Reading **optional (nullable) groups** with `required` children (reproduces [#2041](https://github.com/Kotlin/dataframe/issues/2041)). | `timestamp: required int64 (TIMESTAMP NANOS)`, `record_channel_1: optional group { required int32 x; required int32 y }`, `record_channel_2: optional group { required int32 x; required int32 y }`. 2 rows: row0 `ch1={1,2}, ch2=null`; row1 `ch1=null, ch2={3,4}`. | Generated in code by `ArrowNullableStructTest.regenerate nullable struct parquet fixture` (`@Ignore`d; remove `@Ignore` and run `dataframe-arrow:test --tests "*regenerate nullable struct*"`). It builds an Arrow root (`notNullable` int children → parquet `required`) and writes Parquet with `arrow-dataset`'s `DatasetFileWriter` (`writeNullableStructParquet`). Pure JVM — runs on Windows/Mac/Linux. |
| `test.arrow.parquet` | General `readParquet` smoke/estimation test (300 rows, all supported primitive types). | Flat schema of primitives (string/int/float/double/date…); see `assertEstimations`. | Pre-generated externally. |
| `books.parquet` | Nested **required** struct read as a non-nullable `ColumnGroup`. | `id, title, author: group { id, firstName, lastName }, genre, publisher`. | Pre-generated externally. |
| `orders_nested.parquet` | Struct nested inside a list (`FrameColumn` with a nested `ColumnGroup`). | `id, orders: list<struct{ item, qty, details: struct{ price, currency } }>, note`. | Pre-generated externally. |
| `orders_two_batches.parquet` | Multi-batch read; nullability inferred across batches (nulls appear in batch 2). | Same shape as `orders_nested.parquet`, 2 row groups. | Pre-generated externally. |
| `lists.parquet` | List columns incl. nullable list and list-with-nulls. | `id, numbers: list<long>, strings: list<string>, nullable_list: list<long>?, list_with_nulls: list<long?>`. | Pre-generated externally. |
| `lists_all_types.parquet` | Lists of every supported primitive element type. | `list<...>` per primitive. | Pre-generated externally. |
| `large_list_sample.parquet` | `LargeListVector` read path. | `numbers: list<long>, tags: list<string>?`. | Pre-generated externally. |
| `timestamps_utc_and_local.parquet` | Timestamps flagged `isAdjustedToUTC = true`, i.e. Arrow `TimeStamp*TZVector`, read as `kotlin.time.Instant` (reproduces [#926](https://github.com/Kotlin/dataframe/issues/926)). | `timestamp_utc: optional int64 (TIMESTAMP(isAdjustedToUTC=true, MICROS))`, `timestamp_local: … (isAdjustedToUTC=false, MICROS)`, `timestamp_brussels: … (true, MICROS)`, `timestamp_nanos: … (true, NANOS)`, `timestamp_millis: … (true, MILLIS)`. 3 rows: normal, all-null, pre-1970. | Written by **Polars/PyArrow** — deliberately a third-party writer, so the test proves interop rather than round-tripping our own encoder. Regenerate with `dataframe-arrow/src/test/generators/timestamps_tz.py` (see its header for the exact versions and command). That one script produces **three** fixtures: this file, `timestamps_zoned.parquet` below, and `timestamps_utc_and_local.feather` in the Feather/IPC table. |
| `timestamps_zoned.parquet` | A **non-UTC** zone name surviving in the Parquet `ARROW:schema` metadata; the instants must still read back unchanged. | `timestamp_berlin: optional int64 (TIMESTAMP(isAdjustedToUTC=true, MICROS))`, Arrow schema `timestamp[us, tz=Europe/Berlin]`. 3 rows, same instants as `timestamp_utc` above. | Same generator script. |

### `nullable_nested_struct.parquet` — schema & head

Schema:

```
timestamp:        required int64 (TIMESTAMP NANOS)
record_channel_1: optional group { required int32 x; required int32 y }
record_channel_2: optional group { required int32 x; required int32 y }
```

Head (2 rows):

```
timestamp    record_channel_1   record_channel_2
2000-01-01   { x:1, y:2 }        (null)
2000-01-02   (null)              { x:3, y:4 }
```

## Arrow Feather (`*.feather`) / IPC (`*.ipc`)

Mostly loaded via `testArrowFeather(name)` / `testArrowIPC(name)`, which append `.feather` / `.ipc`; fixtures whose
name already carries the extension are read by full name instead.

| File | Purpose |
|------|---------|
| `data-arrow_2.0.0_uncompressed.feather` | Reading a Feather file produced by Arrow 2.0.0 (incl. a nested `ColumnGroup`). |
| `test.arrow.feather` / `test.arrow.ipc` | Baseline round-trip fixtures. |
| `test-not-nullable.arrow.feather` / `.ipc` | Non-nullable-schema variants. |
| `test-with-nulls.arrow.feather` / `.ipc` | Nullable columns containing nulls. |
| `test-illegal.arrow.feather` / `.ipc` | Malformed/edge-case input handling. |
| `multiple_batches_concat.feather` | Multi-batch Feather concatenation (nested `person` group). |
| `timestamps_utc_and_local.feather` | The `timestamps_utc_and_local.parquet` columns in Arrow random-access form, so the same PyArrow-interop assertions still run where the `arrow-dataset` JNI library is unavailable (Android, or any machine where the native library fails to load). Written **uncompressed** — the module has no `arrow-compression` dependency. Same generator script as the Parquet fixture. |

Note: several Arrow/nested cases are exercised **without** a committed file — they are built in code with `RootAllocator`
+ `VectorSchemaRoot` and round-tripped through in-memory Feather/IPC bytes (see `ArrowNullableStructTest` for the
nullable-struct cases, and `ArrowKtTest` for the column-group round-trip tests).
