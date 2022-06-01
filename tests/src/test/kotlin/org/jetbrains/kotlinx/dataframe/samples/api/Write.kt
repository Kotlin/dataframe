package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.string.shouldStartWith
import org.apache.commons.csv.CSVFormat
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.junit.Test
import java.io.File
import kotlin.io.path.deleteExisting

class Write : TestBase() {

    @Test
    fun writeCsv() {
        useTempFile { file ->
            // SampleStart
            df.writeCSV(file)
            // SampleEnd
        }
    }

    @Test
    fun writeJson() {
        useTempFile { file ->
            // SampleStart
            df.writeJson(file)
            // SampleEnd
        }
    }

    @Test
    fun writeCsvStr() {
        // SampleStart
        val csvStr = df.toCsv(CSVFormat.DEFAULT.withDelimiter(';').withRecordSeparator(System.lineSeparator()))
        // SampleEnd
        csvStr shouldStartWith """
            name;age;city;weight;isHappy
            "{""firstName"":""Alice"",""lastName"":""Cooper""}";15;London;54;true
        """.rejoinWithSystemLineSeparator()
    }

    @Test
    fun writeJsonStr() {
        // SampleStart
        val jsonStr = df.toJson(prettyPrint = true)
        // SampleEnd
        jsonStr shouldStartWith """
            [{
              "name": {
                "firstName": "Alice",
                "lastName": "Cooper"
              },
              "age": 15,
              "city": "London",
              "weight": 54,
              "isHappy": true
            }
        """.rejoinWithSystemLineSeparator()
    }

    @Test
    fun writeXls() {
        useTempFile { file ->
            // SampleStart
            df.writeExcel(file)
            // SampleEnd
        }
    }

    @Test
    fun writeXlsAppendAndPostProcessing() {
        useTempFile { file ->
            // SampleStart
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
            // SampleEnd
        }
    }

    companion object {
        private fun String.rejoinWithSystemLineSeparator() = rejoinWithLineSeparator(System.lineSeparator())

        private fun String.rejoinWithLineSeparator(separator: String) = trimIndent().lines().joinToString(separator)

        private fun useTempFile(action: (File) -> Unit) {
            val file = kotlin.io.path.createTempFile("dataframeWriteTest")
            action(file.toFile())
            file.deleteExisting()
        }
    }
}
