@file:Suppress("ktlint", "UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.deephaven.csv.parsers.Parsers
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.StringColumns
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readCsvStr
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.testArrowFeather
import org.jetbrains.kotlinx.dataframe.testCsv
import org.jetbrains.kotlinx.dataframe.testExcel
import org.jetbrains.kotlinx.dataframe.testJson
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.net.URI
import java.util.Locale
import kotlin.reflect.typeOf

class Read {
    @Ignore
    @Test
    fun read() {
        // SampleStart
        DataFrame.read("input.csv")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readCsvFromFile() {
        // SampleStart
        DataFrame.readCsv("input.csv")
        // Alternatively
        DataFrame.readCsv(File("input.csv"))
        // SampleEnd
    }

    @Ignore
    @Test
    fun readCsvFromUrl() {
        // SampleStart
        DataFrame.readCsv(URI("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv").toURL())
        // SampleEnd
    }

    @Test
    fun readCsvFromString() {
        // SampleStart
        val csv = """
            A,B,C,D
            12,tuv,0.12,true
            41,xyz,3.6,not assigned
            89,abc,7.1,false
        """.trimIndent()

        DataFrame.readCsvStr(csv)
        // SampleEnd
    }

    @Test
    fun readCsvCustom() {
        val file = testCsv("syntheticSample")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            delimiter = '|',
            header = listOf("A", "B", "C", "D"),
            parserOptions = ParserOptions(nullStrings = setOf("not assigned")),
        )
        // SampleEnd
        df.rowsCount() shouldBe 3
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }

    @Test
    fun readJson() {
        val file = testJson("synthetic")
        // SampleStart
        val df = DataFrame.readJson(file)
        // SampleEnd
        df.rowsCount() shouldBe 4
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<String>()
        df["B"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }

    @Test
    fun readJsonRow() {
        val file = testJson("syntheticObj")
        // SampleStart
        val row = DataRow.readJson(file)
        // SampleEnd
        row.columnNames() shouldBe listOf("A", "B", "C", "D")
        row.columnTypes() shouldBe listOf(typeOf<String>(), typeOf<Int>(), typeOf<Float>(), typeOf<Boolean>())
    }

    @Ignore
    @Test
    fun readJsonFromUrl() {
        // SampleStart
        DataFrame.readJson("https://covid.ourworldindata.org/data/owid-covid-data.json")
        // SampleEnd
    }

    @Test
    fun readJsonFromString() {
        val myJson = """
        {
            "dogs": {},
            "cats": {}
        }
    """.trimIndent()
        // SampleStart
        DataFrame.readJsonStr(
            text = myJson,
            keyValuePaths = listOf(
                JsonPath().append("dogs"), // which will result in '$["dogs"]'
                JsonPath().append("cats"), // which will result in '$["cats"]'
            ),
        )
        // SampleEnd
    }

    @Test
    fun readExcelFromFile() {
        val file = testExcel("example")
        // SampleStart
        val df = DataFrame.readExcel(file)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readExcelFromUrl() {
        // SampleStart
        DataFrame.readExcel("https://example.com/data.xlsx")
        // SampleEnd
    }

    @Test
    @Ignore
    fun fixMixedColumn() {
        // SampleStart
        val df = DataFrame.readExcel("mixed_column.xlsx", stringColumns = StringColumns("A"))
        // SampleEnd
    }

    @Test
    fun readArrowFeather() {
        val file = testArrowFeather("data-arrow_2.0.0_uncompressed")
        // SampleStart
        val df = DataFrame.readArrowFeather(file)
        // SampleEnd
        df.rowsCount() shouldBe 1
        df.columnsCount() shouldBe 4
    }

    @Test
    fun readNumbersWithSpecificLocale() {
        val file = testCsv("numbers")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(locale = Locale.UK),
        )
        // SampleEnd
    }

    @Test
    fun readNumbersWithColType() {
        val file = testCsv("numbers")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            colTypes = mapOf("colName" to ColType.String),
        )
        // SampleEnd
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun readDatesWithSpecificDateTimePattern() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(
                dateTime = DateTimeParserOptions.Java.withPattern("dd/MMM/yy h:mm a"),
            ),
        )
        // SampleEnd
    }

    @Test
    fun readDatesWithSpecificDateTimeFormatter() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(
                dateTime = DateTimeParserOptions.Kotlin.withFormat(
                    LocalDate.Format {
                        monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
                    },
                ),
            ),
        )
        // SampleEnd
    }

    @Test
    fun readDatesWithDefaultType() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            colTypes = mapOf(ColType.DEFAULT to ColType.String),
        )
        // SampleEnd
    }

    @Test
    fun readDatesWithDeephavenDateTimeParser() {
        val file = testCsv("dates")
        try {
            // SampleStart
            val df = DataFrame.readCsv(
                inputStream = file.openStream(),
                adjustCsvSpecs = { // it: CsvSpecs.Builder
                    it.putParserForName("date", Parsers.DATETIME)
                },
            )
            // SampleEnd
        } catch (_: Exception) {
        }
    }
}
