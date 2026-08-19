package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.dropWhile
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.api.takeWhile
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class SliceRowsSamples: DataFrameSampleHelper("sliceRows", "api") {
    val df = peopleDf

    @Test
    fun getSeveralRowsByIndices() {
        // SampleStart
        df[0, 3, 4]
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun getSeveralRowsByRanges1() {
        // SampleStart
        df[1..2]
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun getSeveralRowsByRanges2() {
        // SampleStart
        df[0..2, 4..5]
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun take() {
        // SampleStart
        df.take(5)
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun takeLast() {
        // SampleStart
        df.takeLast(5)
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun takeWhile() {
        // SampleStart
        df.takeWhile { isHappy }
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun drop() {
        // SampleStart
        df.drop(5)
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropLast1() {
        // SampleStart
        df.dropLast() // default 1
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropLast2() {
        // SampleStart
        df.dropLast(5)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropWhile() {
        // SampleStart
        df.dropWhile { !isHappy }
        // SampleEnd
        .saveDfHtmlSample()
    }
}
