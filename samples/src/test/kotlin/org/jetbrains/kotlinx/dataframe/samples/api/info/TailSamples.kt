package org.jetbrains.kotlinx.dataframe.samples.api.info

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.tail
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class TailSamples : DataFrameSampleHelper("tail", "api") {

    private val df = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
        "lastName" to listOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
        "age" to listOf(15, 45, 20, 40, 30, 20, 30),
        "city" to listOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
        "weight" to listOf(54, 87, null, null, 68, 55, 90),
        "isHappy" to listOf(true, true, false, true, true, false, true),
    )

    @Test
    fun notebook_test_tail_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_2() {
        // SampleStart
        df.tail()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_3() {
        // SampleStart
        df.tail(numRows = 2)
            // SampleEnd
            .saveDfHtmlSample()
    }
}
