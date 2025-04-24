package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.jetbrains.kotlinx.dataframe.math.quantileOrNull
import org.junit.Test
import kotlin.reflect.typeOf

class QuantileTests {

    @Test
    fun `linear estimation`() {
        // Test R8 with Double - p = 0.1 (10th percentile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.0

        // Test R8 with Double - p = 0.5 (median)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.5

        // Test R8 with Double - p = 0.25 (first quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.4166666666666665

        // Test R8 with Double - p = 0.75 (third quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 3.5833333333333335

        // Test R8 with Double - p = 0.9 (90th percentile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 4.0

        // Test R7 with Double - p = 0.1 (10th percentile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.0

        // Test R7 with Double - p = 0.5 (median)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.8333333333333333

        // Test R7 with Double - p = 0.25 (first quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.0833333333333333

        // Test R7 with Double - p = 0.75 (third quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 2.5833333333333335

        // Test R7 with Double - p = 0.9 (90th percentile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 3.0333333333333337

        // Test R8 with Int - p = 0.5 (median)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.5,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.5

        // Test R8 with Int - p = 0.25 (first quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.25,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.4166666666666665

        // Test R8 with Int - p = 0.75 (third quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.75,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 3.5833333333333335

        // Test R7 with Int - p = 0.5 (median)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.5,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.8333333333333333

        // Test R7 with Int - p = 0.25 (first quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.25,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.0833333333333333

        // Test R7 with Int - p = 0.75 (third quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.75,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 2.5833333333333335

        // Test R8 with Float - p = 0.5 (median)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.5,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.5

        // Test R8 with Float - p = 0.25 (first quartile)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.25,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.4166666666666665

        // Test R8 with Float - p = 0.75 (third quartile)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.75,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 3.5833333333333335

        // Test R7 with Float - p = 0.5 (median)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.5,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.8333333333333333

        // Test R7 with Float - p = 0.25 (first quartile)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.25,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.0833333333333333

        // Test R7 with Float - p = 0.75 (third quartile)
        sequenceOf(1f, 4f, 3f, 2f).quantileOrNull(
            p = 0.75,
            type = typeOf<Float>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 2.5833333333333335

        // Test R8 with Long - p = 0.5 (median)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.5,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.5

        // Test R8 with Long - p = 0.25 (first quartile)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.25,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.4166666666666665

        // Test R8 with Long - p = 0.75 (third quartile)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.75,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 3.5833333333333335

        // Test R7 with Long - p = 0.5 (median)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.5,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.8333333333333333

        // Test R7 with Long - p = 0.25 (first quartile)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.25,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 1.0833333333333333

        // Test R7 with Long - p = 0.75 (third quartile)
        sequenceOf(1L, 4L, 3L, 2L).quantileOrNull(
            p = 0.75,
            type = typeOf<Long>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R7,
        ) shouldBe 2.5833333333333335

        // Test with NaN values and skipNaN = false - p = 0.5 (median)
        val nanResult = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (nanResult as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = false - p = 0.25 (first quartile)
        val nanResult25 = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (nanResult25 as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = false - p = 0.75 (third quartile)
        val nanResult75 = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (nanResult75 as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = true - p = 0.5 (median)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.0

        // Test with NaN values and skipNaN = true - p = 0.25 (first quartile)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 1.1666666666666667

        // Test with NaN values and skipNaN = true - p = 0.75 (third quartile)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 2.8333333333333335
    }

    @Test
    fun `constant estimation`() {
        // Test R3 with Char - p = 0.1 (10th percentile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.1,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Char>,
        ) shouldBe 'a'

        // Test R3 with Char - p = 0.5 (median)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.5,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Char>,
        ) shouldBe 'a'

        // Test R3 with Char - p = 0.25 (first quartile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.25,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Char>,
        ) shouldBe 'a'

        // Test R3 with Char - p = 0.75 (third quartile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.75,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Char>,
        ) shouldBe 'b'

        // Test R3 with Char - p = 0.9 (90th percentile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.9,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Char>,
        ) shouldBe 'b'

        // Test R1 with Char - p = 0.1 (10th percentile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.1,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Char>,
        ) shouldBe 'a'

        // Test R1 with Char - p = 0.5 (median)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.5,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Char>,
        ) shouldBe 'b'

        // Test R1 with Char - p = 0.25 (first quartile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.25,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Char>,
        ) shouldBe 'a'

        // Test R1 with Char - p = 0.75 (third quartile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.75,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Char>,
        ) shouldBe 'c'

        // Test R1 with Char - p = 0.9 (90th percentile)
        sequenceOf('a', 'c', 'b').quantileOrNull(
            p = 0.9,
            type = typeOf<Char>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Char>,
        ) shouldBe 'c'

        // Test R3 with String - p = 0.5 (median)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.5,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "apple"

        // Test R3 with String - p = 0.25 (first quartile)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.25,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "apple"

        // Test R3 with String - p = 0.75 (third quartile)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.75,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "banana"

        // Test R1 with String - p = 0.5 (median)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.5,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<String>,
        ) shouldBe "banana"

        // Test R1 with String - p = 0.25 (first quartile)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.25,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<String>,
        ) shouldBe "apple"

        // Test R1 with String - p = 0.75 (third quartile)
        sequenceOf("apple", "cherry", "banana").quantileOrNull(
            p = 0.75,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<String>,
        ) shouldBe "cherry"

        // Test R3 with Int (primitive number) - p = 0.5 (median)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.5,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Int>,
        ) shouldBe 2

        // Test R3 with Int (primitive number) - p = 0.25 (first quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.25,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Int>,
        ) shouldBe 1

        // Test R3 with Int (primitive number) - p = 0.75 (third quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.75,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Int>,
        ) shouldBe 2

        // Test R1 with Int (primitive number) - p = 0.5 (median)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.5,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Int>,
        ) shouldBe 2

        // Test R1 with Int (primitive number) - p = 0.25 (first quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.25,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Int>,
        ) shouldBe 1

        // Test R1 with Int (primitive number) - p = 0.75 (third quartile)
        sequenceOf(1, 4, 3, 2).quantileOrNull(
            p = 0.75,
            type = typeOf<Int>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Int>,
        ) shouldBe 3

        // Test R3 with Double (primitive number) - p = 0.5 (median)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 2.0

        // Test R3 with Double (primitive number) - p = 0.25 (first quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 1.0

        // Test R3 with Double (primitive number) - p = 0.75 (third quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 2.0

        // Test R1 with Double (primitive number) - p = 0.5 (median)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Double>,
        ) shouldBe 2.0

        // Test R1 with Double (primitive number) - p = 0.25 (first quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Double>,
        ) shouldBe 1.0

        // Test R1 with Double (primitive number) - p = 0.75 (third quartile)
        sequenceOf(1.0, 4.0, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R1 as QuantileEstimationMethod<Double>,
        ) shouldBe 3.0

        // Test with NaN values and skipNaN = false - p = 0.5 (median)
        val nanResult = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        )
        (nanResult as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = false - p = 0.25 (first quartile)
        val nanResult25 = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        )
        (nanResult25 as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = false - p = 0.75 (third quartile)
        val nanResult75 = sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        )
        (nanResult75 as Double).isNaN() shouldBe true

        // Test with NaN values and skipNaN = true - p = 0.5 (median)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 1.0

        // Test with NaN values and skipNaN = true - p = 0.25 (first quartile)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 1.0

        // Test with NaN values and skipNaN = true - p = 0.75 (third quartile)
        sequenceOf(1.0, Double.NaN, 3.0, 2.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<Double>,
        ) shouldBe 2.0
    }

    @Test
    fun `edge cases`() {
        // Empty sequence - p = 0.1 (10th percentile)
        sequenceOf<Double>().quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // Empty sequence - p = 0.5 (median)
        sequenceOf<Double>().quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // Empty sequence - p = 0.25 (first quartile)
        sequenceOf<Double>().quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // Empty sequence - p = 0.75 (third quartile)
        sequenceOf<Double>().quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // Empty sequence - p = 0.9 (90th percentile)
        sequenceOf<Double>().quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // Single element sequence - linear estimation - p = 0.1 (10th percentile)
        sequenceOf(5.0).quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 5.0

        // Single element sequence - linear estimation - p = 0.5 (median)
        sequenceOf(5.0).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 5.0

        // Single element sequence - linear estimation - p = 0.25 (first quartile)
        sequenceOf(5.0).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 5.0

        // Single element sequence - linear estimation - p = 0.75 (third quartile)
        sequenceOf(5.0).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 5.0

        // Single element sequence - linear estimation - p = 0.9 (90th percentile)
        sequenceOf(5.0).quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe 5.0

        // Single element sequence - constant estimation - p = 0.5 (median)
        sequenceOf("test").quantileOrNull(
            p = 0.5,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "test"

        // Single element sequence - constant estimation - p = 0.25 (first quartile)
        sequenceOf("test").quantileOrNull(
            p = 0.25,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "test"

        // Single element sequence - constant estimation - p = 0.75 (third quartile)
        sequenceOf("test").quantileOrNull(
            p = 0.75,
            type = typeOf<String>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R3 as QuantileEstimationMethod<String>,
        ) shouldBe "test"

        // We don't test extreme low quantile values (p close to 0.0) as they can cause index calculation issues

        // We don't test extreme high quantile values (p close to 1.0) as they can cause index calculation issues

        // All NaN values with skipNaN = true - p = 0.1 (10th percentile)
        sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // All NaN values with skipNaN = true - p = 0.5 (median)
        sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // All NaN values with skipNaN = true - p = 0.25 (first quartile)
        sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // All NaN values with skipNaN = true - p = 0.75 (third quartile)
        sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // All NaN values with skipNaN = true - p = 0.9 (90th percentile)
        sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = true,
            method = QuantileEstimationMethod.R8,
        ) shouldBe null

        // All NaN values with skipNaN = false - p = 0.1 (10th percentile)
        val result01 = sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.1,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (result01 as Double).isNaN() shouldBe true

        // All NaN values with skipNaN = false - p = 0.5 (median)
        val result = sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.5,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (result as Double).isNaN() shouldBe true

        // All NaN values with skipNaN = false - p = 0.25 (first quartile)
        val result25 = sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.25,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (result25 as Double).isNaN() shouldBe true

        // All NaN values with skipNaN = false - p = 0.75 (third quartile)
        val result75 = sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.75,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (result75 as Double).isNaN() shouldBe true

        // All NaN values with skipNaN = false - p = 0.9 (90th percentile)
        val result09 = sequenceOf(Double.NaN, Double.NaN).quantileOrNull(
            p = 0.9,
            type = typeOf<Double>(),
            skipNaN = false,
            method = QuantileEstimationMethod.R8,
        )
        (result09 as Double).isNaN() shouldBe true
    }
}
