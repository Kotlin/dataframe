package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.appendNulls
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class Append : DataFrameSampleHelper("append", "api") {

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

    @Test
    fun appendDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendDataSchema() {
        // SampleStart
        df.append(Person("Bob", 30))
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30) }
            .saveDfHtmlSample()
    }

    @Test
    fun appendOneRow() {
        // SampleStart
        df.append("Bob", 30)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendSeveralRows() {
        // SampleStart
        df.append(
            "Bob", // name in the first new row
            30, // age in the first new row
            "Charlie", // name in the second new row
            25, // age in the second new row
        )
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", 30, "Charlie", 25) }
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullValue() {
        // SampleStart
        df.append("Bob", null)
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, "Bob", null) }
            .saveDfHtmlSample()
    }

    @Test
    fun appendNoValues() {
        // SampleStart
        df.append()
            // SampleEnd
            .also { (it === df) shouldBe true }
            .saveDfHtmlSample()
    }

    @Test
    fun columnGroupDf() {
        // SampleStart
        columnGroupDf
            // SampleEnd
            .toHtmlWithOpenedNestedDfs()
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
            .toHtmlWithOpenedNestedDfs()
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
            .toHtmlWithOpenedNestedDfs()
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
            .toHtmlWithOpenedNestedDfs()
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
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }

    @Test
    fun frameColumnDf() {
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

    @Test
    fun appendOneNullRow() {
        // SampleStart
        df.appendNulls()
            // SampleEnd
            .also { it shouldBe dataFrameOf("name", "age")("Alice", 20, null, null) }
            .saveDfHtmlSample()
    }

    @Test
    fun appendSeveralNullRows() {
        // SampleStart
        df.appendNulls(numberOfRows = 3)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf("name", "age")("Alice", 20, null, null, null, null, null, null)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun appendZeroNullRows() {
        // SampleStart
        df.appendNulls(numberOfRows = 0)
            // SampleEnd
            .also { (it === df) shouldBe true }
            .saveDfHtmlSample()
    }

    @Test
    fun appendNullsDoesNotModifyOriginal() {
        // SampleStart
        val withNullRow = df.appendNulls()

        df // the original dataframe still contains only Alice
            // SampleEnd
            .also {
                it shouldBe dataFrameOf("name", "age")("Alice", 20)
                withNullRow shouldBe dataFrameOf("name", "age")("Alice", 20, null, null)
            }
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
