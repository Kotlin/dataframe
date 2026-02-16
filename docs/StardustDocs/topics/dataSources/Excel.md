# Excel

<web-summary>
Read from and write to Excel files in `.xls` or `.xlsx` formats with Kotlin DataFrame for seamless spreadsheet integration.
</web-summary>

<card-summary>
Kotlin DataFrame makes it easy to load and save data from Excel files — perfect for working with spreadsheet-based workflows.
</card-summary>

<link-summary>
Learn how to read and write Excel files using Kotlin DataFrame with just a single line of code.
</link-summary>


Kotlin DataFrame supports reading from and writing to Excel files in both `.xls` and `.xlsx` formats.

Requires the [`dataframe-excel` module](Modules.md#dataframe-excel), 
which is included by default in the general [`dataframe`](Modules.md#dataframe-general) 
artifact and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

## Read

You can read a [`DataFrame`](DataFrame.md) from an Excel file (via a file path or URL) 
using the [`readExcel()`](read.md#read-from-excel) method:

```kotlin
val df = DataFrame.readExcel("example.xlsx")
```

```kotlin
val df = DataFrame.readExcel("https://kotlin.github.io/dataframe/resources/example.xlsx")
```

## Write

You can write a [`DataFrame`](DataFrame.md) to an Excel file using the 
[`writeExcel()`](write.html#write-to-excel-spreadsheet) method:

```kotlin
df.writeExcel("example.xlsx")
```
