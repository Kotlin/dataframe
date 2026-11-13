package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class AppendSamples : DataFrameSampleHelper("append", "api") {

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
    fun appendDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendOneRow() {
        // SampleStart
        df.append("Bob", 30)
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendSeveralRows() {
        // SampleStart
        df.append(
            // the first new row
            "Bob",
            30,
            // the second new row
            "Charlie",
            25,
        )
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30, "Charlie", 25) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullValue() {
        // SampleStart
        df.append("Bob", null)
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", null) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNoValues() {
        val df = dataFrameOf(
            "name" to columnOf("Alice"),
            "age" to columnOf(20),
        )
        // SampleStart
        df.append()
            // SampleEnd
            .also { (it === df) shouldBe true }
            .saveDfHtmlSample()
    }

    @Test
    fun appendDataSchema() {
        // SampleStart
        df.append(Person("Bob", 30))
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendExplicitDataRowSchema() {
        // SampleStart
        data class Person(val name: String, val age: Int) : DataRowSchema

        val df = dataFrameOf(Person("Alice", 20))

        df.append(Person("Bob", 30))
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30) }
            .highlightAppendedRows(df.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendColumnGroupDf() {
        // SampleStart
        columnGroupDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendColumnGroupList() {
        // SampleStart
        columnGroupDf.append(listOf("Bob", "Dylan"), 30)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf(
                        "firstName" to columnOf("Alice", "Bob"),
                        "lastName" to columnOf("Cooper", "Dylan"),
                    ),
                    "age" to columnOf(20, 30),
                )
            }
            .highlightAppendedRows(columnGroupDf.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendColumnGroupRow() {
        // SampleStart
        val bobRow = dataFrameOf("lastName", "firstName")("Dylan", "Bob")[0]
        columnGroupDf.append(bobRow, 30)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf(
                        "firstName" to columnOf("Alice", "Bob"),
                        "lastName" to columnOf("Cooper", "Dylan"),
                    ),
                    "age" to columnOf(20, 30),
                )
            }
            .highlightAppendedRows(columnGroupDf.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendColumnGroupRowWithMissingColumn() {
        // SampleStart
        val bobRow = dataFrameOf("firstName")("Bob")[0]
        columnGroupDf.append(bobRow, 30)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf(
                        "firstName" to columnOf("Alice", "Bob"),
                        "lastName" to columnOf("Cooper", null),
                    ),
                    "age" to columnOf(20, 30),
                )
            }
            .highlightAppendedRows(columnGroupDf.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullToColumnGroup() {
        // SampleStart
        columnGroupDf.append(null, 30)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf(
                        "firstName" to columnOf("Alice", null),
                        "lastName" to columnOf("Cooper", null),
                    ),
                    "age" to columnOf(20, 30),
                )
            }
            .highlightAppendedRows(columnGroupDf.rowsCount())
            .saveDfHtmlSample()
    }

    @Test
    fun appendFrameColumnDf() {
        // SampleStart
        frameColumnDf
            // SampleEnd
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }

    @Test
    fun appendFrameColumn() {
        // SampleStart
        val bobDf = dataFrameOf(
            "name" to columnOf("Bob"),
            "age" to columnOf(30),
        )
        frameColumnDf.append(bobDf)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(columnOf(df, bobDf) named "people")
            }
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullToFrameColumn() {
        // SampleStart
        frameColumnDf.append(null)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(columnOf(df, emptyPersonDf) named "people")
            }
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }
}
