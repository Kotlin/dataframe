@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.deephaven.csv.parsers.Parsers
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.StringColumns
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.testArrowFeather
import org.jetbrains.kotlinx.dataframe.testCsv
import org.jetbrains.kotlinx.dataframe.testJson
import org.junit.Ignore
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.typeOf

class Read {
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

    @Test
    fun readDatesWithSpecificDateTimePattern() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(dateTimePattern = "dd/MMM/yy h:mm a")
        )
        // SampleEnd
    }

    @Test
    fun readDatesWithSpecificDateTimeFormatter() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yy h:mm a"))
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
        } catch (_: Exception) {}
    }
}
