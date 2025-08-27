package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class AnySamples : DataFrameSampleHelper("any", "api") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(15, 20)
    ).cast<SimplePerson>()

    @Test
    fun notebook_test_any_3() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_any_4() {
        // SampleStart
        df.any { age > 21 }
        // SampleEnd
    }

    @Test
    fun notebook_test_any_5() {
        // SampleStart
        df.any { age == 15 && name == "Alice" }
        // SampleEnd
    }

}
