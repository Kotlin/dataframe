package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.math.BigDecimal

class CumSumSamples : DataFrameSampleHelper("cumSum", "api") {

    private val df = ordersDf

    @Test
    fun cumSumBigNumbersManually() {
        // SampleStart
        // exact cumulative sum, computed with Java `BigDecimal` arithmetic
        df.amount.toList().runningReduce(BigDecimal::add)
        // SampleEnd
    }

    @Test
    fun cumSumBigNumbersConverted() {
        // SampleStart
        // approximate cumulative sum, computed after converting the column to `Double`
        df.convert { amount }.toDouble().cumSum { amount }
        // SampleEnd
    }
}
