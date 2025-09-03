package org.jetbrains.kotlinx.dataframe.samples.api.column

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.between
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class BetweenSamples : DataFrameSampleHelper("between", "api") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Diana"),
        "age" to listOf(15, 20, 25, 30),
    ).cast<SimplePerson>()

    @Test
    fun notebook_test_between_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_between_2() {
        // SampleStart
        // Create a Boolean column indicating whether ages are between 18 and 25 (inclusive)
        df.age.between(left = 18, right = 25)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_between_3() {
        // SampleStart
        // Exclude boundaries: strictly between 18 and 25 (i.e., 19..24)
        df.age.between(left = 18, right = 25, includeBoundaries = false)
            // SampleEnd
            .saveDfHtmlSample()
    }
}
