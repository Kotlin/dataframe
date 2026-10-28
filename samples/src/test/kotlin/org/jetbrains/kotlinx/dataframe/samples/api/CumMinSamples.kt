package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.cumMin
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class CumMinSamples : DataFrameSampleHelper("cumMin", "api") {

    @Test
    fun cumMinOnColumn() {
        // SampleStart
        val lows = columnOf(8, 5, null, 7, 2).cumMin().toList()
        // SampleEnd
        lows shouldBe listOf(8, 5, null, 5, 2)
        lows.toString().saveTextSample()
    }

    @Test
    fun cumMinWithoutSkipping() {
        // SampleStart
        val lows = columnOf(8, 5, null, 7, 2).cumMin(skipNA = false).toList()
        // SampleEnd
        lows shouldBe listOf(8, 5, null, null, null)
        lows.toString().saveTextSample()
    }
}
