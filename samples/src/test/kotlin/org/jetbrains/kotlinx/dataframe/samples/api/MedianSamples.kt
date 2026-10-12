package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class MedianSamples : DataFrameSampleHelper("median", "api") {

    private val df = ordersDf

    @Test
    fun medianBigNumbersManually() {
        // SampleStart
        // exact median: the lower of the two middle values, selected from the sorted values
        val amounts = df.amount.toList().sorted()
        amounts[(amounts.size - 1) / 2]
        // SampleEnd
    }

    @Test
    fun medianBigNumbersConverted() {
        // SampleStart
        // approximate, interpolated median, computed after converting the column to `Double`
        df.convert { amount }.toDouble().median { amount }
        // SampleEnd
    }
}
