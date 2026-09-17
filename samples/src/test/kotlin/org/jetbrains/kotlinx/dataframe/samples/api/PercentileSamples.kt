package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.math.round

class PercentileSamples : DataFrameSampleHelper("percentile", "api") {

    private val df = ordersDf

    @Test
    fun percentileBigNumbersManually() {
        // SampleStart
        // exact 25th percentile: the closest rank (R3) of the sorted values
        val amounts = df.amount.toList().sorted()
        val rank = round(0.25 * amounts.size).toInt().coerceIn(1, amounts.size)
        amounts[rank - 1]
        // SampleEnd
    }

    @Test
    fun percentileBigNumbersConverted() {
        // SampleStart
        // approximate, interpolated 25th percentile, computed after converting the column to `Double`
        df.convert { amount }.toDouble().percentile(25.0) { amount }
        // SampleEnd
    }
}
