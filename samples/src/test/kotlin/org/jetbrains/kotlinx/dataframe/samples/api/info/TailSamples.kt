package org.jetbrains.kotlinx.dataframe.samples.api.info

import org.jetbrains.kotlinx.dataframe.api.tail
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class TailSamples : DataFrameSampleHelper("tail", "api") {

    private val df = peopleDf

    @Test
    fun notebook_test_tail_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_2() {
        // SampleStart
        df.tail()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_3() {
        // SampleStart
        df.tail(numRows = 2)
            // SampleEnd
            .saveDfHtmlSample()
    }
}
