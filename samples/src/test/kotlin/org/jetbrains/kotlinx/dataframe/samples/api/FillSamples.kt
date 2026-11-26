package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
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
    @DataSchema
    interface PersonWithWeight {
        val name: String
        val weight: Double?
    }

    private val df: DataFrame<PersonWithWeight> = dataFrameOf(
        "name",
        "weight",
    )(
        "Alice",
        54.0,
        "Charlie",
        Double.NaN,
        "Bob",
        null,
    ).cast()

    @Test
    fun fillDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { weight }.with { -1.0 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun fillNullsAsUpdate() {
        // SampleStart
        df.update { weight }.where { it == null }.with { -1.0 }
        // SampleEnd
    }

    @Test
    fun fillNaNs() {
        // SampleStart
        df.fillNaNs { weight }.withZero()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun fillNA() {
        // SampleStart
        df.fillNA { weight }.with { -1.0 }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
