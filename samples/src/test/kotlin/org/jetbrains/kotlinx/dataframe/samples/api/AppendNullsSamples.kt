package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.appendNulls
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class AppendNullsSamples : DataFrameSampleHelper("appendNulls", "api") {

    @DataSchema
    data class Person(val name: String, val age: Int)

    private val df: DataFrame<Person> = dataFrameOf(
        "name" to columnOf("Alice"),
        "age" to columnOf(20),
    ).cast()

    private val columnGroupDf: DataFrame<*> = dataFrameOf(
        "name" to columnOf(
            "firstName" to columnOf("Alice"),
            "lastName" to columnOf("Cooper"),
        ),
        "age" to columnOf(20),
    )

    private val emptyPersonDf: DataFrame<*> = DataFrame.empty(df.schema())
    private val frameColumnDf: DataFrame<*> = dataFrameOf(columnOf(df) named "people")
    private val appendedRowColor = RgbColor(189, 206, 233)

    private fun DataFrame<*>.highlightAppendedRows(firstAppendedRow: Int) =
        format().perRowCol { row, _ ->
            if (row.index() >= firstAppendedRow) {
                background(appendedRowColor) and textColor(black)
            } else {
                null
            }
        }

    @Test
    fun appendNullsDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsOneRow() {
        // SampleStart
        df.appendNulls()
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, null, null) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsSeveralRows() {
        // SampleStart
        df.appendNulls(numberOfRows = 3)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf("name", "age")("Alice", 20, null, null, null, null, null, null)
            }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsZeroRows() {
        // SampleStart
        df.appendNulls(numberOfRows = 0)
            // SampleEnd
            .also { (it === df) shouldBe true }
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsColumnGroupDf() {
        // SampleStart
        columnGroupDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsColumnGroup() {
        // SampleStart
        columnGroupDf.appendNulls()
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf(
                        "firstName" to columnOf("Alice", null),
                        "lastName" to columnOf("Cooper", null),
                    ),
                    "age" to columnOf(20, null),
                )
            }
            .highlightAppendedRows(columnGroupDf.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsFrameColumnDf() {
        // SampleStart
        frameColumnDf
            // SampleEnd
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsFrameColumn() {
        // SampleStart
        frameColumnDf.appendNulls()
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(columnOf(df, emptyPersonDf) named "people")
            }
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }
}
