package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext

class StdSamples : DataFrameSampleHelper("std", "api") {

    private val df = ordersDf

    @Test
    fun stdBigNumbersManually() {
        // SampleStart
        // exact unbiased sample standard deviation (ddof = 1), computed with Java `BigDecimal` arithmetic
        val amounts = df.amount.toList()
        val mean = amounts.fold(BigDecimal.ZERO, BigDecimal::add)
            .divide(amounts.size.toBigDecimal(), MathContext.DECIMAL128)
        amounts.fold(BigDecimal.ZERO) { acc, value -> acc + (value - mean).pow(2) }
            .divide((amounts.size - 1).toBigDecimal(), MathContext.DECIMAL128)
            .sqrt(MathContext.DECIMAL128)
        // SampleEnd
    }

    @Test
    fun stdBigNumbersConverted() {
        // SampleStart
        // approximate standard deviation, computed after converting the column to `Double`
        df.convert { amount }.toDouble().std { amount }
        // SampleEnd
    }
}
