package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.fillNA
import org.jetbrains.kotlinx.dataframe.api.fillNaNs
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withZero
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class FillSamples : DataFrameSampleHelper("fill", "api") {
    val df = peopleDf

    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { colsOf<Int?>() }.with { -1 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun fillNullsAsUpdate() {
        // SampleStart
        df.update { colsOf<Int?>() }.where { it == null }.with { -1 }
            // SampleEnd
    }

    @Test
    fun fillNaNs() {
        // SampleStart
        df.fillNaNs { colsOf<Double>() }.withZero()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun fillNA() {
        // SampleStart
        df.fillNA { weight }.with { -1 }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
