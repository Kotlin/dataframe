[//]: # (title: Write)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Write-->

`DataFrame` instances can be saved in the following formats: CSV, TSV, JSON, XLS(X) and Apache Arrow.

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

### Write to Excel spreadsheet

Add dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-excel:$dataframe_version")
```

You can write your dataframe in XLS, XLSX format to a file, `OutputStream` or Workbook object.

<!---FUN writeXls-->

```kotlin
df.writeExcel(file)
```

<!---END-->

Values of ColumnGroup, FrameColumn, i.e. AnyRow, AnyFrame will be serialized as JSON objects. 

If you work directly with Apache POI, you can use created Workbook and Sheets in your code:

<!---FUN writeXlsAppendAndPostProcessing-->

```kotlin
/**
 * Do something with generated sheets. Here we set bold style for headers and italic style for first data column
 */
fun setStyles(sheet: Sheet) {
    val headerFont = sheet.workbook.createFont()
    headerFont.bold = true
    val headerStyle = sheet.workbook.createCellStyle()
    headerStyle.setFont(headerFont)

    val indexFont = sheet.workbook.createFont()
    indexFont.italic = true
    val indexStyle = sheet.workbook.createCellStyle()
    indexStyle.setFont(indexFont)

    sheet.forEachIndexed { index, row ->
        if (index == 0) {
            for (cell in row) {
                cell.cellStyle = headerStyle
            }
        } else {
            row.first().cellStyle = indexStyle
        }
    }
}

// Create a workbook (or use existing)
val wb = WorkbookFactory.create(true)

// Create different sheets from different data frames in the workbook
val allPersonsSheet = df.writeExcel(wb, sheetName = "allPersons")
val happyPersonsSheet = df.filter { person -> person.isHappy }.remove("isHappy").writeExcel(wb, sheetName = "happyPersons")
val unhappyPersonsSheet = df.filter { person -> !person.isHappy }.remove("isHappy").writeExcel(wb, sheetName = "unhappyPersons")

// Do anything you want by POI
listOf(happyPersonsSheet, unhappyPersonsSheet).forEach { setStyles(it) }

// Save the result
file.outputStream().use { wb.write(it) }
wb.close()
```

<!---END-->

### Writing to Apache Arrow formats

Add dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-arrow:$dataframe_version")
```

<warning>
Make sure to follow [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility) guide when using Java 9+
</warning>

Dataframe supports writing [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format)
and [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files)
to raw WritableByteChannel, OutputStream, File or ByteArray.

Data may be saved "as is" (like exporting to new Excel file) or converted to match some target [Schema](https://arrow.apache.org/docs/java/reference/org/apache/arrow/vector/types/pojo/Schema.html)
if you have it (like inserting into existing SQL table).

The first approach is quite easy:
<!---FUN writeArrowFile-->

```kotlin
df.writeArrowIPC(file)
// or
df.writeArrowFeather(file)
```

<!---END-->
(writing to file, opened stream or channel),
<!---FUN writeArrowByteArray-->

```kotlin
val ipcByteArray: ByteArray = df.saveArrowIPCToByteArray()
// or
val featherByteArray: ByteArray = df.saveArrowFeatherToByteArray()
```

<!---END-->
(creating byte array). Nested frames and columns with mixed or unsupported types will be saved as String.

The second approach is a bit more tricky. You have to specify schema itself and casting behavior mode as `ArrowWriter` parameters.
Behavior `Mode` has four independent switchers: `restrictWidening`, `restrictNarrowing`, `strictType`, `strictNullable`.
You can use `Mode.STRICT` (this is default), `Mode.LOYAL` or any combination you want.
The `ArrowWriter` object should be closed after using because Arrow uses random access buffers not managed by Java GC.
Finally, you can specify a callback to be invoked if some data is lost or can not be saved according to your schema.

Here is full example:
<!---FUN writeArrowPerSchema-->

```kotlin
// Get schema from anywhere you want. It can be deserialized from JSON, generated from another dataset
// (including the DataFrame.columns().toArrowSchema() method), created manually, and so on.
val schema = Schema.fromJSON(schemaJson)

df.arrowWriter(

    // Specify your schema
    targetSchema = schema,

    // Specify desired behavior mode
    mode = ArrowWriter.Mode(
        restrictWidening = true,
        restrictNarrowing = true,
        strictType = true,
        strictNullable = false,
    ),

    // Specify mismatch subscriber
    mismatchSubscriber = writeMismatchMessage,

    ).use { writer: ArrowWriter ->

    // Save to any format and sink, like in the previous example
    writer.writeArrowFeather(file)
}
```

<!---END-->
On executing you should get two warnings:
>Column "city" contains nulls but expected not nullable

and

> Column "isHappy" is not described in the target schema and was ignored
