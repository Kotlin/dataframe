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

Requires the [`dataframe-arrow` module](Modules.md#dataframe-arrow), which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact and in and when using `%use dataframe` for Kotlin Notebook.

> We currently only support READING Parquet via Apache Arrow; writing Parquet is not supported in Kotlin DataFrame.
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
```

<!---FUN readParquetFilePath-->

```kotlin
// Read from Path objects
val path = Paths.get("data/sales.parquet")
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
val file = File("data/sales.parquet")
val df = DataFrame.readParquet(file)
```

<!---END-->


<!---FUN readParquetFileWithParameters-->

```kotlin
// Read from File objects
val file = File("data/sales.parquet")

val df = DataFrame.readParquet(
    file,
    nullability = NullabilityOptions.Infer,
    batchSize = 64L * 1024
)
```

<!---END-->


If you want to see a complete, realistic data‑engineering example using Spark and Parquet with Kotlin DataFrame,
check out the [example project](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/spark-parquet-dataframe).

### Multiple Files

It's possible to read multiple Parquet files:

<!---FUN readMultipleParquetFiles-->

```kotlin
val file = File("data/sales.parquet")
val file1 = File("data/sales1.parquet")
val file2 = File("data/sales2.parquet")

val df = DataFrame.readParquet(file, file1, file2)
```

<!---END-->

**Requirements:**

- All files must have compatible schemas
- Files are vertically concatenated (union of rows)
- Column types must match exactly
- Missing columns in some files will result in null values

### Performance tips

- **Column selection**: Because the `readParquet` method reads all columns, use DataFrame operations like `select()` immediately after reading to reduce memory usage in later operations
- **Predicate pushdown**: Currently not supported—filtering happens after data is loaded into memory
- Use Arrow‑compatible JVMs as documented in
  [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility).
- Adjust `batchSize` if you read huge files and need to tune throughput vs. memory.

### See also

- [](ApacheArrow.md) — reading/writing Arrow IPC formats.
- [Parquet official site](https://parquet.apache.org/).
- Example: [Spark + Parquet + Kotlin DataFrame](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/spark-parquet-dataframe)
- [](Data-Sources.md) — Overview of all supported formats
