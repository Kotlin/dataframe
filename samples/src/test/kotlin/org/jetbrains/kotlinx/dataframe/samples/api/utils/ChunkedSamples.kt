package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ChunkedSamples : DataFrameSampleHelper("chunked", "api") {

    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Diana", "Eve"),
        "age" to listOf(15, 20, 25, 30, 35),
    ).cast<SimplePerson>()

    @Test
    fun notebook_test_chunked_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_chunked_2() {
        // SampleStart
        // Split DataFrame into chunks of size 2
        df.chunked(size = 2)
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_chunked_3() {
        // SampleStart
        // Split DataFrame using custom start indices
        df.chunked(startIndices = listOf(0, 2, 4), name = "segments")
        // SampleEnd
            .saveDfHtmlSample()
    }
}
