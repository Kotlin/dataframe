# Parquet

<web-summary>
Read Parquet files via Apache Arrow in Kotlin DataFrame — high‑performance columnar storage for analytics.
</web-summary>

<card-summary>
Use Kotlin DataFrame to read Parquet datasets using Apache Arrow for fast, typed, columnar I/O.
</card-summary>

<link-summary>
Kotlin DataFrame can read Parquet files through Apache Arrow’s Dataset API. Learn how and when to use it.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.io.Parquet-->

Kotlin DataFrame supports reading [Apache Parquet](https://parquet.apache.org/) files through the Apache Arrow integration.

Requires the [`dataframe-arrow` module](Modules.md#dataframe-arrow), which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact and in and when using `%use dataframe` for Kotlin Notebook.

> We currently only support READING Parquet via Apache Arrow; writing Parquet is not supported in Kotlin DataFrame.
> {style="note"}

> Apache Arrow is not supported on Android, so reading Parquet files on Android is not available.
> {style="warning"}

> Unlike the Arrow IPC and Feather formats, which are read in pure Java, reading Parquet relies on Arrow Dataset's
> **native** library: `readParquet` extracts `arrow_dataset_jni` from the `arrow-dataset` jar and loads it. If your
> platform or JDK build cannot load it, the call fails with
> `UnsatisfiedLinkError: … A dynamic link library (DLL) initialization routine failed` before any data is read.
> The failure is in Arrow's native loader rather than in the reader, and reading the same data as Arrow IPC or
> Feather is unaffected.
> {style="note"}

> Nested Arrow `Struct` types are read as a [`ColumnGroup`](DataColumn.md#columngroup). An **optional (nullable)**
> group is read as a `ColumnGroup` whose child columns become nullable, holding `null` in the rows where the group is
> absent. This is a deliberate limitation: a `ColumnGroup` is never `null` per row, so Kotlin DataFrame cannot
> represent a whole nullable group cell (a type like `{x: Int, y: Int}?`) — instead the nullability is pushed down to
> the leaf columns. As a consequence, an absent group and a present group whose children are all `null` are
> represented the same way. The same applies to nested `Struct`s read from Arrow IPC and Feather.
> {style="note"}

## Reading Parquet Files

Kotlin DataFrame provides four `readParquet()` methods that can read from different source types.
All overloads accept optional `nullability` inference settings and `batchSize` for Arrow scanning.

<!---FUN ReadParquetOverloads-->

```kotlin
// 1) URLs
public fun DataFrame.Companion.readParquet(
    vararg urls: URL,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame

// 2) Strings (interpreted as file paths or URLs, e.g., "data/file.parquet", "file://", or "http(s)://")
public fun DataFrame.Companion.readParquet(
    vararg strUrls: String,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame

// 3) Paths
public fun DataFrame.Companion.readParquet(
    vararg paths: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame

// 4) Files
public fun DataFrame.Companion.readParquet(
    vararg files: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame
```

<!---END-->

These overloads are defined in the `dataframe-arrow` module and internally use `FileFormat.PARQUET` from Apache Arrow’s
Dataset API to scan the data and materialize it as a Kotlin `DataFrame`.

`ARROW_PARQUET_DEFAULT_BATCH_SIZE` is **32768** rows — the number of rows Arrow reads per batch while scanning.
It is a public constant, so you can reference it when tuning `batchSize` relative to the default.

### Examples

<!---FUN readParquet-->

```kotlin
// Read from file paths (as strings)
val df = DataFrame.readParquet("data/sales.parquet")
```

<!---END-->

<!---FUN readParquetFilePath-->

```kotlin
// Read from Path objects
val df = DataFrame.readParquet(path)
```

<!---END-->

<!---FUN readParquetURL-->

```kotlin
// Read from URLs
val df = DataFrame.readParquet(url)
```

<!---END-->

<!---FUN readParquetFile-->

```kotlin
// Read from File objects
val df = DataFrame.readParquet(file)
```

<!---END-->


<!---FUN readParquetFileWithParameters-->

```kotlin
val df = DataFrame.readParquet(
    file,
    nullability = NullabilityOptions.Infer,
    batchSize = 64L * 1024,
)
```

<!---END-->


If you want to see a complete, realistic data‑engineering example using Spark and Parquet with Kotlin DataFrame,
check out the [example project](https://github.com/Kotlin/dataframe/tree/master/examples/projects/spark-parquet-dataframe).

### Multiple Files

It's possible to read multiple Parquet files:

<!---FUN readMultipleParquetFiles-->

```kotlin
val df = DataFrame.readParquet(file, file1, file2)
```

<!---END-->

**Requirements:**

- All files must have compatible schemas
- Files are vertically concatenated (union of rows)
- Column types must match exactly
- Missing columns in some files will result in null values

### Timestamps and time zones

Parquet stores a timestamp column as an `int64` plus a flag, and that flag decides which Kotlin type you get:

| Parquet logical type | Kotlin type |
|----------------------|-------------|
| `Timestamp(isAdjustedToUTC = true, …)` | `kotlin.time.Instant` |
| `Timestamp(isAdjustedToUTC = false, …)` | [`kotlinx.datetime.LocalDateTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date-time/) |

The distinction is not cosmetic. With `isAdjustedToUTC = true` the number counts time units since
`1970-01-01T00:00:00Z`, so it identifies one point on the time-line — an instant. With `isAdjustedToUTC = false`
it is a bare calendar-and-clock reading with no zone, which identifies no single point in time. This is the case
[the Parquet specification](https://github.com/apache/parquet-format/blob/master/LogicalTypes.md#timestamp)
describes in detail.

Writers such as PyArrow, Polars and pandas set `isAdjustedToUTC = true` for every time-zone-aware column, so those
columns are read as `Instant`. `MILLIS`, `MICROS` and `NANOS` are all supported, and the full precision is kept —
a `NANOS` column keeps all nine fractional digits.

> Normalizing to UTC is lossy by design: the original zone is **not** stored in the file, so a column written from
> `Europe/Brussels` values and one written from UTC values are indistinguishable after the fact — only the instant
> survives. Parquet also has no seconds precision (`MILLIS`, `MICROS` and `NANOS` only).
> {style="note"}

An `Instant` names a point on the time-line but no wall clock, so reading one on somebody's clock takes an explicit
zone:

<!---FUN convertParquetInstantToLocalDateTime-->

```kotlin
val df = DataFrame.readParquet("events.parquet")
    .convert { "timestamp"<Instant>() }.with { it.toLocalDateTime(TimeZone.of("Europe/Berlin")) }
```

<!---END-->

### Performance tips

- **Column selection**: Because the `readParquet` method reads all columns, use DataFrame operations like `select()` immediately after reading to reduce memory usage in later operations
- **Predicate pushdown**: Currently not supported—filtering happens after data is loaded into memory
- Use Arrow‑compatible JVMs as documented in
  [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility).
- Adjust `batchSize` if you read huge files and need to tune throughput vs. memory.

### See also

- [](ApacheArrow.md) — reading/writing Arrow IPC formats.
- [Parquet official site](https://parquet.apache.org/).
- Example: [Spark + Parquet + Kotlin DataFrame](https://github.com/Kotlin/dataframe/tree/master/examples/projects/spark-parquet-dataframe)
- [](Data-Sources.md) — Overview of all supported formats
