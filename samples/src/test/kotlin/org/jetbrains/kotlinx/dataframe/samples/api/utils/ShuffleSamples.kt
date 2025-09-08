package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.shuffle
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.random.Random

class ShuffleSamples : DataFrameSampleHelper("shuffle", "api") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Diana", "Eve"),
        "age" to listOf(15, 20, 25, 30, 35),
    ).cast<SimplePerson>()

    @Test
    fun notebook_test_shuffle_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_shuffle_2() {
        // SampleStart
        df.shuffle(Random(42))
            // SampleEnd
            .saveDfHtmlSample()
    }
}
