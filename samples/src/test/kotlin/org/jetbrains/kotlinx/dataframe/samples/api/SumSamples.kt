package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.math.BigDecimal

class SumSamples : DataFrameSampleHelper("sum", "api") {

    private val df = ordersDf

    @Test
    fun sumBigNumbersManually() {
        // SampleStart
        // exact sum, computed with Java `BigDecimal` arithmetic
        df.amount.toList().fold(BigDecimal.ZERO, BigDecimal::add)
        // SampleEnd
    }

    @Test
    fun sumBigNumbersConverted() {
        // SampleStart
        // approximate sum, computed after converting the column to `Double`
        df.convert { amount }.toDouble().sum { amount }
        // SampleEnd
    }
}
