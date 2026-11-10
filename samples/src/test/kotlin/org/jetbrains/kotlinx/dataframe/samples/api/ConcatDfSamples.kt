package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ConcatDfSamples : DataFrameSampleHelper("concatDf", "api") {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Charlie"),
        "age" to columnOf(20, 15, 25),
        "city" to columnOf("London", "Paris", "London"),
    )

    @Test
    fun concatDfDataFrames() {
        val df1 = df
        val df2 = df
        // SampleStart
        df.concat(df1, df2)
            // SampleEnd
            .rowsCount() shouldBe 9
    }

    @Test
    fun concatDfInfix() {
        val df1 = df
        val result =
            // SampleStart
            df concat df1
        // SampleEnd
        result.rowsCount() shouldBe 6
    }

    @Test
    fun concatDfDataFrameIterable() {
        val df1 = df
        val df2 = df
        // SampleStart
        df.concat(listOf(df1, df2))
            // SampleEnd
            .rowsCount() shouldBe 9
    }

    @Test
    fun concatDfIterable() {
        val df1 = df
        val df2 = df
        // SampleStart
        listOf(df1, df2).concat()
            // SampleEnd
            .rowsCount() shouldBe 6
    }
}
