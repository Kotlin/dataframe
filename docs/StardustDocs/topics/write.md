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
wb.write(file.outputStream())
wb.close()
```

<!---END-->
