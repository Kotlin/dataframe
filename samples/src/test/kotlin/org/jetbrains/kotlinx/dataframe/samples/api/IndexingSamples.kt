package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class IndexingSamples : DataFrameSampleHelper("indexing", "api") {
    val df = peopleDf

    @Test
    fun getCell_properties() {
        // SampleStart
        df.age[1]
        df[1].age
        // SampleEnd
        df.age[1] willBe 45
        df[1].age willBe 45
    }

    @Test
    fun getCell_strings() {
        // SampleStart
        df["age"][1]
        df[1]["age"]
        // SampleEnd
        df["age"][1] willBe 45
        df[1]["age"] willBe 45
    }

    @Test
    fun indexingDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

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
        val moscow = df.indices { city == "Moscow" } // [2, 6]
        // SampleEnd
        moscow willBe listOf(2, 6)
    }

    @Test
    fun indicesCondition_strings() {
        // SampleStart
        val moscow = df.indices { "city"<String?>() == "Moscow" } // [2, 6]
        // SampleEnd
        moscow willBe listOf(2, 6)
    }

    @Test
    fun indicesGetRows() {
        // SampleStart
        df.getRows(df.indices { city == "Moscow" })
            // SampleEnd
            .saveDfHtmlSample()
    }
}
