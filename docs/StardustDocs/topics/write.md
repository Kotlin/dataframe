[//]: # (title: Write)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Write-->

`DataFrame` can be saved into CSV or JSON formats.

### Writing to CSV

You can write `DataFrame` in CSV format to file, to `String` or to `Appendable`
(i.e. to `Writer`).

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
