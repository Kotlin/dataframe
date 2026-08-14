package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ValueCountsSamples : DataFrameSampleHelper("valueCounts", "api") {
    val df = peopleDf

    @Test
    fun valueCountsDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valueCounts() {
        // SampleStart
        df.valueCounts()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsWithNA() {
        // SampleStart
        df.valueCounts(dropNA = false)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsSelector_properties() {
        // SampleStart
        df.valueCounts(dropNA = false) { name.firstName and isHappy }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsSelector_strings() {
        // SampleStart
        df.valueCounts(dropNA = false) { "name"["firstName"]<String>() and isHappy }
        // SampleEnd
    }

    @Test
    fun valueCountsColumn() {
        // SampleStart
        df.city.valueCounts()
            // SampleEnd
            .saveDfHtmlSample()
    }
}
