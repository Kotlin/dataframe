package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.jetbrains.kotlinx.dataframe.math.quantileOrNull
import org.junit.Test
import kotlin.reflect.typeOf

class QuantileEngineRegressionTests {

    @Test
    fun `selecting singleton primitive retains its type`() {
        val result = sequenceOf(7).quantileOrNull(0.5, typeOf<Int>(), false, QuantileEstimationMethod.R1)

        (result is Int) shouldBe true
        result shouldBe 7
    }

    @Test
    fun `interpolating singleton rejects nonprimitive type before shortcut`() {
        shouldThrow<IllegalArgumentException> {
            sequenceOf("single").quantileOrNull(0.5, typeOf<String>(), false, QuantileEstimationMethod.R7)
        }
    }

    @Test
    fun `interpolated minimum endpoint preserves negative infinity`() {
        sequenceOf(Double.NEGATIVE_INFINITY, 1.0).quantileOrNull(
            0.0,
            typeOf<Double>(),
            false,
            QuantileEstimationMethod.R7,
        ) shouldBe Double.NEGATIVE_INFINITY
    }

    @Test
    fun `interpolated maximum endpoint preserves positive infinity`() {
        sequenceOf(1.0, Double.POSITIVE_INFINITY).quantileOrNull(
            1.0,
            typeOf<Double>(),
            false,
            QuantileEstimationMethod.R8,
        ) shouldBe Double.POSITIVE_INFINITY
    }

    @Test
    fun `identical infinite observations do not become NaN during interpolation`() {
        sequenceOf(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).quantileOrNull(
            0.5,
            typeOf<Double>(),
            false,
            QuantileEstimationMethod.R7,
        ) shouldBe Double.POSITIVE_INFINITY
    }
}
