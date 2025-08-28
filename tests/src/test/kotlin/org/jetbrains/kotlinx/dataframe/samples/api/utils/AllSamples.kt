package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class AllSamples : DataFrameSampleHelper("all", "api") {

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(15, 20)
    ).cast<AnySamples.SimplePerson>()

    @Test
    fun notebook_test_all_3() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_all_4() {
        // SampleStart
        df.all { age > 21 }
        // SampleEnd
    }

    @Test
    fun notebook_test_all_5() {
        // SampleStart
        df.all { name.first().isUpperCase() && age >= 15 }
        // SampleEnd
    }
}
