package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext

class MeanSamples : DataFrameSampleHelper("mean", "api") {

    private val df = ordersDf

    @Test
    fun meanBigNumbersManually() {
        // SampleStart
        // exact mean, computed with Java `BigDecimal` arithmetic
        val amounts = df.amount.toList()
        amounts.fold(BigDecimal.ZERO, BigDecimal::add)
            .divide(amounts.size.toBigDecimal(), MathContext.DECIMAL128)
        // SampleEnd
    }

    @Test
    fun meanBigNumbersConverted() {
        // SampleStart
        // approximate mean, computed after converting the column to `Double`
        df.convert { amount }.toDouble().mean { amount }
        // SampleEnd
    }
}
