package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.indices
import org.junit.Test

class IndicesSamples : TestBase {
    val df = peopleDf

    @Test
    fun indices() {
        // SampleStart
        df.indices() // 0..9
        // SampleEnd
        df.indices() willBe 0..9
    }

    @Test
    fun indicesCondition_properties() {
        // SampleStart
        df.indices { city == "Moscow" } // [2, 6]
        // SampleEnd
        df.indices { city == "Moscow" } willBe listOf(2, 6)
    }

    @Test
    fun indicesCondition_strings() {
        // SampleStart
        df.indices { "city"<String?>() == "Moscow" } // [2, 6]
        // SampleEnd
        df.indices { "city"<String?>() == "Moscow" } willBe listOf(2, 6)
    }
}
