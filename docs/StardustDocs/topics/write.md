[//]: # (title: Writing dataframes)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Write-->

DataFrames can be saved in CSV or JSON formats.

### Writing to CSV

You can write your dataframe in CSV format to file, to string or to `Appendable`
(i.e. to `Writer`).

<!---FUN writeCsv-->

```kotlin
df.writeCSV(file)
```

<!---END-->

<!---FUN writeCsvStr-->

```kotlin
val csvStr = df.writeCSVStr(CSVFormat.DEFAULT.withDelimiter(';').withRecordSeparator(System.lineSeparator()))
```

<!---END-->

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
val jsonStr = df.writeJsonStr(prettyPrint = true)
```

<!---END-->
