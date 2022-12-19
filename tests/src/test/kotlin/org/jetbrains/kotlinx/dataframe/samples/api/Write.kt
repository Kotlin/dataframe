package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.string.shouldStartWith
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.commons.csv.CSVFormat
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.io.ArrowWriter
import org.jetbrains.kotlinx.dataframe.io.arrowWriter
import org.jetbrains.kotlinx.dataframe.io.saveArrowFeatherToByteArray
import org.jetbrains.kotlinx.dataframe.io.saveArrowIPCToByteArray
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.jetbrains.kotlinx.dataframe.io.writeArrowFeather
import org.jetbrains.kotlinx.dataframe.io.writeArrowIPC
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.jetbrains.kotlinx.dataframe.io.writeMismatchMessage
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
            file.outputStream().use { wb.write(it) }
            wb.close()
            // SampleEnd
        }
    }

    @Test
    fun writeArrowFile() {
        useTempFile { file ->
            // SampleStart
            df.writeArrowIPC(file)
            // or
            df.writeArrowFeather(file)
            // SampleEnd
        }
    }

    @Test
    fun writeArrowByteArray() {
        // SampleStart
        val ipcByteArray: ByteArray = df.saveArrowIPCToByteArray()
        // or
        val featherByteArray: ByteArray = df.saveArrowFeatherToByteArray()
        // SampleEnd
    }

    @Test
    fun writeArrowPerSchema() {
        useTempFile { file ->
            val schemaJson =
"""{
  "fields" : [ {
    "name" : "name",
    "nullable" : true,
    "type" : {
      "name" : "utf8"
    },
    "children" : [ ]
  }, {
    "name" : "age",
    "nullable" : false,
    "type" : {
      "name" : "int",
      "bitWidth" : 32,
      "isSigned" : true
    },
    "children" : [ ]
  }, {
    "name" : "city",
    "nullable" : false,
    "type" : {
      "name" : "utf8"
    },
    "children" : [ ]
  }, {
    "name" : "weight",
    "nullable" : true,
    "type" : {
      "name" : "floatingpoint",
      "precision" : "DOUBLE"
    },
    "children" : [ ]
  } ]
}
"""

            // SampleStart
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
