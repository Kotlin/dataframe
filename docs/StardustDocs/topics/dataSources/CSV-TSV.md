# CSV / TSV

<web-summary>
Work with CSV and TSV files — read, analyze, and export tabular data using Kotlin DataFrame.
</web-summary>

<card-summary>
Seamlessly load and write CSV or TSV files in Kotlin — perfect for common tabular data workflows.
</card-summary>

<link-summary>
Kotlin DataFrame support for reading and writing CSV and TSV files with simple, type-safe APIs.
</link-summary>


Kotlin DataFrame supports reading from and writing to CSV and TSV files.

Requires the [`dataframe-csv` module](Modules.md#dataframe-csv), 
which is included by default in the general [`dataframe`](Modules.md#dataframe-general) 
artifact and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

## Read

You can read a [`DataFrame`](DataFrame.md) from a CSV or TSV file (via a file path or URL) 
using the [`readCsv()`](read.md#read-from-csv) or `readTsv()` methods:

```kotlin
val df = DataFrame.readCsv("example.csv")
```

```kotlin
val df = DataFrame.readCsv("https://kotlin.github.io/dataframe/resources/example.csv")
```

## Write

You can write a [`DataFrame`](DataFrame.md) to a CSV file using the [`writeCsv()`](write.md#writing-to-csv) method:

```kotlin
df.writeCsv("example.csv")
```

## Deephaven CSV

The [`dataframe-csv`](Modules.md#dataframe-csv) module uses the high-performance
[Deephaven CSV library](https://github.com/deephaven/deephaven-csv) under the hood
for fast and efficient CSV reading and writing.

If you're working with large CSV files, you can adjust the parser manually
by [configuring Deephaven-specific parameters](https://kotlin.github.io/dataframe/read.html#unlocking-deephaven-csv-features)
to get the best performance for your use case.

