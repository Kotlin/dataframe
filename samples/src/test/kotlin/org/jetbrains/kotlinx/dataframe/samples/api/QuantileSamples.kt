package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.QuantileMethod
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.quantile
import org.jetbrains.kotlinx.dataframe.api.quantileOrNull
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.reflect.typeOf

class QuantileSamples : DataFrameSampleHelper("quantile", "api") {

    @Test
    fun quantileSelectingMethods() {
        // SampleStart
        val values = columnOf(4, 1, 3, 2)
        val r1: Int = values.quantile(0.6, QuantileMethod.R1)
        val r3: Int = values.quantile(0.6, QuantileMethod.R3)
        // SampleEnd
        r1 shouldBe 3
        r3 shouldBe 2
        listOf(r1, r3).toString().saveTextSample()
    }

    @Test
    fun quantileInterpolatingMethods() {
        // SampleStart
        val values = columnOf(4, 1, 3, 2)
        val r7: Double = values.quantile(0.25, QuantileMethod.R7)
        val r8: Double = values.quantile(0.25, QuantileMethod.R8)
        // SampleEnd
        r7 shouldBe 1.75
        r8 shouldBe (17.0 / 12).plusOrMinus(1e-12)
        listOf(r7, r8).toString().saveTextSample()
    }

    @Test
    fun quantileExactSelection() {
        // SampleStart
        val exact: Long = columnOf(9007199254740993L, 9007199254740992L).quantile(1.0, QuantileMethod.R1)
        val fruit: String = columnOf("pear", "apple", "orange").quantile(0.5, QuantileMethod.R1)
        // SampleEnd
        exact shouldBe 9007199254740993L
        fruit shouldBe "orange"
        "$exact, $fruit".saveTextSample()
    }

    @Test
    fun quantileSkippingNaN() {
        // SampleStart
        val result = columnOf(1.0, null, Double.NaN, 3.0).quantile(0.5, QuantileMethod.R7, skipNaN = true)
        // SampleEnd
        result shouldBe 2.0
        result.toString().saveTextSample()
    }

    @Test
    fun quantileEmptyColumn() {
        // SampleStart
        val values = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())
        val result: Double? = values.quantileOrNull(0.5, QuantileMethod.R7)
        // SampleEnd
        result shouldBe null
        result.toString().saveTextSample()
    }

    @Test
    fun quantileAndPercentileDefaults() {
        // SampleStart
        val values = columnOf(4, 1, 3, 2)
        val percentile = values.percentile(25.0)
        val quantile = values.quantile(0.25, QuantileMethod.R8)
        // SampleEnd
        percentile shouldBe quantile
        quantile.toString().saveTextSample()
    }
}
