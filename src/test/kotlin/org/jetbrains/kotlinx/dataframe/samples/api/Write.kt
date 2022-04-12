package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.string.shouldStartWith
import org.apache.commons.csv.CSVFormat
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
            df.writeExcel(file)
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
