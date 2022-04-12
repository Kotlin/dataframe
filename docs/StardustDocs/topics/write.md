[//]: # (title: Write)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Write-->

`DataFrame` can be saved into CSV, TSV, JSON and XLS, XLSX formats.

### Writing to CSV

You can write `DataFrame` in CSV format to file, to `String` or to `Appendable`
(i.e. to `Writer`).

Values of ColumnGroup, FrameColumn, i.e. AnyRow, AnyFrame will be serialized as JSON objects. 

<!---FUN writeCsv-->

```kotlin
df.writeCSV(file)
```

<!---END-->

<!---FUN writeCsvStr-->

```kotlin
val csvStr = df.toCsv(CSVFormat.DEFAULT.withDelimiter(';').withRecordSeparator(System.lineSeparator()))
```

<!---END-->

`ColumnGroup` and `FrameColumn` values will be serialized as JSON strings.

### Writing to JSON

You can write your dataframe in JSON format to file, to string or to `Appendable`
(i.e. to `Writer`).

<!---FUN writeJson-->

```kotlin
df.writeJson(file)
```

<!---END-->

<!---FUN writeJsonStr-->

```kotlin
val jsonStr = df.toJson(prettyPrint = true)
```

<!---END-->

### Writing spreadsheets

You can write your dataframe in XLS, XLSX format to a file or `OutputStream`

Values of ColumnGroup, FrameColumn, i.e. AnyRow, AnyFrame will be serialized as JSON objects. 

