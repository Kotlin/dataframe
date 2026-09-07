package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.appendNulls
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class Append : DataFrameSampleHelper("append", "api") {

    @DataSchema
    data class Person(val name: String, val age: Int)

    private val df: DataFrame<Person> = dataFrameOf(Person("Alice", 20))

    @Test
    fun appendDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun `append uses the compiler plugin overload`() {
        val df = dataFrameOf(Person("Alice", 20))

        val result = df.append(Person("Bill", 30))

        result shouldBe dataFrameOf("name", "age")("Alice", 20, "Bill", 30)
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
            .saveDfHtmlSample()
    }

    @Test
    fun appendOneNullRow() {
        // SampleStart
        df.appendNulls()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun appendSeveralNullRows() {
        // SampleStart
        df.appendNulls(numberOfRows = 3)
            // SampleEnd
            .saveDfHtmlSample()
    }
}
