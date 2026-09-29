package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.duplicate
import org.jetbrains.kotlinx.dataframe.api.duplicateRows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class DuplicateSamples : DataFrameSampleHelper("duplicate", "api") {

    @DataSchema
    interface NameAndAge {
        val name: String
        val age: Int
    }

    // the same dataframe as in the KDoc examples of `duplicate` and `duplicateRows`
    private val df: DataFrame<NameAndAge> = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie"),
        "age" to listOf(15, 20, 30),
    ).cast()

    @Test
    fun duplicateDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun duplicateRows() {
        // SampleStart
        df.duplicateRows(3)
            // SampleEnd
            .also {
                it.name.toList() shouldBe
                    listOf("Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Charlie", "Charlie", "Charlie")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun duplicateRowsWhere_properties() {
        // SampleStart
        df.duplicateRows(3) { age > 18 }
            // SampleEnd
            .also {
                it.name.toList() shouldBe listOf("Alice", "Bob", "Bob", "Bob", "Charlie", "Charlie", "Charlie")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun duplicateRowsWhere_strings() {
        // SampleStart
        df.duplicateRows(3) { "age"<Int>() > 18 }
            // SampleEnd
            .also {
                it["name"].toList() shouldBe listOf("Alice", "Bob", "Bob", "Bob", "Charlie", "Charlie", "Charlie")
            }
    }

    @Test
    fun duplicateRow() {
        // SampleStart
        df[1].duplicate(3)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Bob", "Bob", "Bob") }
            .saveDfHtmlSample()
    }

    @Test
    fun duplicateRowNullable() {
        // SampleStart
        val people = dataFrameOf(
            "name" to listOf("Alice", null),
            "age" to listOf(15, 20),
        )
        people[0].duplicate(2).schema()
            // SampleEnd
            .toString()
            .also {
                people.schema().toString() shouldBe "name: String?\nage: Int"
                it shouldBe "name: String\nage: Int"
            }
            .saveTextSample()
    }

    @Test
    fun duplicateDataFrame() {
        // SampleStart
        df.duplicate(3)
            // SampleEnd
            .also {
                it.name() shouldBe ""
                it.values() shouldBe listOf(df, df, df)
                // a dataframe gives a column without a name the name `untitled`, so the table shows it this way
                it.toDataFrame().columnNames() shouldBe listOf("untitled")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun duplicateDataFrameConcat() {
        // SampleStart
        df.duplicate(2).concat()
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice", "Bob", "Charlie", "Alice", "Bob", "Charlie") }
            .saveDfHtmlSample()
    }
}
