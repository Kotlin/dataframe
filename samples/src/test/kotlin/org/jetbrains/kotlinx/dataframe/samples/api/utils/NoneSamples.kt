package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.none
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class NoneSamples : DataFrameSampleHelper("none", "api") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(15, 20),
    ).cast<SimplePerson>()

    @Test
    fun noneDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun noneSample1() {
        // SampleStart
        df.none { age > 21 }
        // SampleEnd
    }

    @Test
    fun noneSample2() {
        // SampleStart
        df.none { age == 15 && name == "Alice" }
        // SampleEnd
    }

    @Test
    fun noneSample3() {
        // SampleStart
        df.name.none { it == "Charlie" }
        // SampleEnd
    }
}
