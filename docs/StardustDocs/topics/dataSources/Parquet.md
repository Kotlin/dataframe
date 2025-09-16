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

Kotlin DataFrame supports reading [Apache Parquet](https://parquet.apache.org/) files through the Apache Arrow integration.

Requires the [`dataframe-arrow` module](Modules.md#dataframe-arrow), which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

> We currently support READING Parquet via Apache Arrow only; writing Parquet is not supported in Kotlin DataFrame.
> {style="note"}

> Apache Arrow is not supported on Android, so reading Parquet files on Android is not available.
> {style="warning"}

> Structured (nested) Arrow types such as Struct are not supported yet in Kotlin DataFrame.
> See an issue: [Add inner / Struct type support in Arrow](https://github.com/Kotlin/dataframe/issues/536)
> {style="warning"}

## Reading Parquet Files

Kotlin DataFrame provides four `readParquet()` methods that can read from different source types.
All overloads accept optional `nullability` inference settings and `batchSize` for Arrow scanning.

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

These overloads are defined in the `dataframe-arrow` module and internally use `FileFormat.PARQUET` from Apache Arrow’s
Dataset API to scan the data and materialize it as a Kotlin `DataFrame`.

### Examples

```kotlin
// Read from file paths (as strings)
val df1 = DataFrame.readParquet("data/sales.parquet")

// Read from File objects
val file = File("data/sales.parquet")
val df2 = DataFrame.readParquet(file)

// Read from Path objects
val path = Paths.get("data/sales.parquet")
val df3 = DataFrame.readParquet(path)

// Read from URLs
val url = URL("https://example.com/data/sales.parquet")
val df4 = DataFrame.readParquet(url)

// Customize nullability inference and batch size
val df5 = DataFrame.readParquet(
    File("data/sales.parquet"),
    nullability = NullabilityOptions.Infer,
    batchSize = 64L * 1024 // tune Arrow scan batch size if needed
)
```

### Multiple Files

It's possible to read multiple Parquet files:

```kotlin
val df = DataFrame.readParquet("file1.parquet", "file2.parquet", "file3.parquet")
```
**Requirements:**

- All files must have compatible schemas
- Files are vertically concatenated (union of rows)
- Column types must match exactly
- Missing columns in some files will result in null values

### Batch Size Tuning

- **Default**: (typically 1024) `ARROW_PARQUET_DEFAULT_BATCH_SIZE`
- **Small files** (< 100MB): Use default
- **Large files** (> 1GB): Increase to `64 * 1024` or `128 * 1024`
- **Memory constrained**: Decrease to `256` or `512`

```kotlin
// For large files with enough memory
DataFrame.readParquet("large_file.parquet", batchSize = 64L * 1024)

// For memory-constrained environments  
DataFrame.readParquet("file.parquet", batchSize = 256L)
```

### Nullability Inference

Controls how nullable columns are handled:

```kotlin
// Infer nullability from data (default)
DataFrame.readParquet("file.parquet", nullability = NullabilityOptions.Infer)

// Treat all columns as nullable
DataFrame.readParquet("file.parquet", nullability = NullabilityOptions.Enable)

// Treat all columns as non-null (may cause runtime errors)
DataFrame.readParquet("file.parquet", nullability = NullabilityOptions.Disable)
```

## About Parquet

[Apache Parquet](https://parquet.apache.org/) is an open-source, column-oriented data file format designed for efficient data storage and retrieval. It provides several advantages:

- **Columnar storage**: Data is stored column-by-column, which enables efficient compression and encoding schemes
- **Schema evolution**: Supports adding new columns without breaking existing data readers
- **Efficient querying**: Optimized for analytics workloads where you typically read a subset of columns
- **Cross-platform**: Works across different programming languages and data processing frameworks
- **Compression**: Built-in support for various compression algorithms (GZIP, Snappy, etc.)

Parquet files are commonly used in data lakes, data warehouses, and big data processing pipelines. They're frequently created by tools like Apache Spark, Pandas, Dask, and various cloud data services.

## Typical use cases

- Exchanging columnar datasets between Spark and Kotlin/JVM applications.
- Analytical workloads where columnar compression and predicate pushdown matter.
- Reading data exported from data lakes and lakehouse tables (e.g., from Spark, Hive, or Delta/Iceberg exports).

If you want to see a complete, realistic data‑engineering example using Spark and Parquet with Kotlin DataFrame,
check out the [example project](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/spark-parquet-dataframe).

### Performance tips

- **Column selection**: Because the ` readParquet ` method reads all columns, use DataFrame operations like `select()` immediately after reading to reduce memory usage in later operations
- **Predicate pushdown**: Currently not supported—filtering happens after data is loaded into memory
- Use Arrow‑compatible JVMs as documented in
  [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility).
- Adjust `batchSize` if you read huge files and need to tune throughput vs. memory.

## Limitations

### Structured Data Support

> **Important**: We currently don't support reading nested/structured data from Parquet files. Complex types like nested objects, arrays of structs, and maps are not yet supported.
>
> This limitation is tracked in issue [#536: Add inner/Struct type support in Arrow](https://github.com/Kotlin/dataframe/issues/536).
> {style="warning"}

If your Parquet file contains nested structures, you may need to flatten the data before processing or use alternative tools for initial data preparation.

### Android Compatibility

> **Note**: Parquet file reading is **not available on Android** because Apache Arrow is not supported on the Android platform.
> {style="warning"}

If you need to process Parquet files in an Android application, consider:
- Processing files on a server and exposing the data via an API
- Converting Parquet files to a supported format (JSON, CSV) for Android consumption
- Using cloud-based data processing services

### See also

- [](ApacheArrow.md) — reading/writing Arrow IPC formats.
- [Parquet official site](https://parquet.apache.org/).
- Example: [Spark + Parquet + Kotlin DataFrame](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/spark-parquet-dataframe)
- [](Data-Sources.md) — Overview of all supported formats
