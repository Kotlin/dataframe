package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.ddofDefault
import org.jetbrains.kotlinx.dataframe.api.rowStd
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.stdOf
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class StdTests {

    @Test
    fun `std one column`() {
        val value by columnOf(1, 2, 3)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Int>(), false, ddofDefault) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
        df.std().columnTypes().single() shouldBe typeOf<Double>()
    }

    @Test
    fun `std one byte column`() {
        val value by columnOf(1.toByte(), 2.toByte(), 3.toByte())
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Byte>(), false, ddofDefault) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
        df.std().columnTypes().single() shouldBe typeOf<Double>()
    }

    @Test
    fun `std one double column`() {
        val value by columnOf(1.0, 2.0, 3.0)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Double>(), false, ddofDefault) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
    }

    @Test
    fun `std with different numeric types`() {
        // Integer types
        columnOf(1, 2, 3, 4, 5).std() shouldBe 1.5811388300841898
        columnOf(1L, 2L, 3L, 4L, 5L).std() shouldBe 1.5811388300841898
        columnOf(1.toShort(), 2.toShort(), 3.toShort(), 4.toShort(), 5.toShort()).std() shouldBe 1.5811388300841898
        columnOf(1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte()).std() shouldBe 1.5811388300841898

        // Floating point types
        columnOf(1.0, 2.0, 3.0, 4.0, 5.0).std() shouldBe 1.5811388300841898
        columnOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f).std() shouldBe 1.5811388300841898
    }

    @Test
    fun `std with null`() {
        val colWithNull = columnOf(1, 2, null, 4, 5)
        colWithNull.std() shouldBe 1.8257418583505538
    }

    @Test
    fun `std with mixed numeric type`() {
        columnOf<Number>(1, 2L, 3.0f, 4.0, 5.toShort()).std() shouldBe 1.5811388300841898
    }

    @Test
    fun `std with just NaNs`() {
        columnOf(Double.NaN, Double.NaN).std().shouldBeNaN()

        // With skipNaN=true and only NaN values, result should be NaN
        columnOf(Double.NaN, Double.NaN).std(skipNaN = true).shouldBeNaN()
    }

    @Test
    fun `std with nans and nulls`() {
        // Std functions should return NaN if any value is NaN
        columnOf(1.0, 2.0, Double.NaN, 4.0, null).std().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        columnOf(1.0, 2.0, Double.NaN, 4.0, null).std(skipNaN = true) shouldBe 1.5275252316519465
    }

    @Test
    fun `stdOf with transformer function`() {
        // Test with strings that can be converted to numbers
        val strings = columnOf("1", "2", "3", "4", "5")
        strings.stdOf { it.toInt() } shouldBe 1.5811388300841898
    }

    @Test
    fun `stdOf with transformer function with nulls`() {
        val stringsWithNull = columnOf("1", "2", null, "4", "5")
        stringsWithNull.stdOf { it?.toInt() } shouldBe 1.8257418583505538
    }

    @Test
    fun `stdOf with transformer function with NaNs`() {
        // Std functions should return NaN if any value is NaN
        val mixedValues = columnOf("1.0", "2.0", "NaN", "4.0", "5.0")
        mixedValues.stdOf {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        mixedValues.stdOf(skipNaN = true) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        } shouldBe 1.8257418583505538
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowStd with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Calculate standard deviation across each row
        df[0].rowStd() shouldBe 1.0
        df[1].rowStd() shouldBe 1.0
        df[2].rowStd() shouldBe 1.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowStd with dataframe and nulls`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, null, 6,
            7, 8, 9,
        )

        // Calculate standard deviation across each row
        df[0].rowStd() shouldBe 1.0
        df[1].rowStd() shouldBe 1.4142135623730951 // std of [4, 6]
        df[2].rowStd() shouldBe 1.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowStd with dataframe and NaNs`() {
        // Std functions should return NaN if any value is NaN
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            Double.NaN, 5.0, 6.0,
            7.0, 8.0, Double.NaN,
        )

        dfWithNaN[0].rowStd().shouldBeNaN()
        dfWithNaN[1].rowStd().shouldBeNaN()
        dfWithNaN[2].rowStd().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        dfWithNaN[0].rowStd(skipNaN = true) shouldBe 1.4142135623730951 // std of [1.0, 3.0]
        dfWithNaN[1].rowStd(skipNaN = true) shouldBe 0.7071067811865476 // std of [5.0, 6.0]
        dfWithNaN[2].rowStd(skipNaN = true) shouldBe 0.7071067811865476 // std of [7.0, 8.0]
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe std`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Get row with standard deviations for each column
        val stds = df.std()
        stds["a"] shouldBe 3.0
        stds["b"] shouldBe 3.0
        stds["c"] shouldBe 3.0

        // Test std for specific columns
        val stdFor = df.stdFor("a", "c")
        stdFor["a"] shouldBe 3.0
        stdFor["c"] shouldBe 3.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe stdOf`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Calculate standard deviation of a + c for each row
        df.stdOf { "a"<Int>() + "c"<Int>() } shouldBe 6.0 // std of [4, 10, 16]
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `std with skipNaN for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(1.0f, 2.0f, Float.NaN, 4.0f, 5.0f)
        floatWithNaN.std().shouldBeNaN() // Default behavior: NaN propagates
        floatWithNaN.std(skipNaN = true) shouldBe 1.8257418583505538 // Skip NaN values

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(1.0, 2.0, Double.NaN, 4.0, 5.0)
        doubleWithNaN.std().shouldBeNaN() // Default behavior: NaN propagates
        doubleWithNaN.std(skipNaN = true) shouldBe 1.8257418583505538 // Skip NaN values

        // Test with multiple NaN values in different positions
        val multipleNaN = columnOf(Float.NaN, 2.0f, Float.NaN, 4.0f, Float.NaN)
        multipleNaN.std().shouldBeNaN() // Default behavior: NaN propagates
        multipleNaN.std(skipNaN = true) shouldBe 1.4142135623730951 // Skip NaN values

        // Test with all NaN values
        val allNaN = columnOf(Float.NaN, Float.NaN, Float.NaN)
        allNaN.std().shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.std(skipNaN = true).shouldBeNaN() // With skipNaN=true and only NaN values, result should be NaN

        // Test with DataFrame containing NaN values
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            4.0, 5.0, Float.NaN,
            Double.NaN, 8.0, 9.0,
        )

        // Test DataFrame std with NaN values
        val stdsWithNaN = dfWithNaN.std() // Default behavior
        (stdsWithNaN["a"] as Double).shouldBeNaN() // Contains NaN
        (stdsWithNaN["b"] as Double).shouldBeNaN() // Contains NaN
        (stdsWithNaN["c"] as Double).shouldBeNaN() // Contains NaN

        // Test DataFrame std with skipNaN=true
        val stdsSkipNaN = dfWithNaN.std(skipNaN = true) // Skip NaN values
        stdsSkipNaN["a"] shouldBe 2.1213203435596424 // std of [1.0, 4.0]
        stdsSkipNaN["b"] shouldBe 2.1213203435596424 // std of [5.0, 8.0]
        stdsSkipNaN["c"] shouldBe 4.242640687119285 // std of [3.0, 9.0]

        // Test stdFor with skipNaN
        val stdForWithNaN = dfWithNaN.stdFor("a", "c") // Default behavior
        (stdForWithNaN["a"] as Double).shouldBeNaN() // Contains NaN
        (stdForWithNaN["c"] as Double).shouldBeNaN() // Contains NaN

        val stdForSkipNaN = dfWithNaN.stdFor("a", "c", skipNaN = true) // Skip NaN values
        stdForSkipNaN["a"] shouldBe 2.1213203435596424 // std of [1.0, 4.0]
        stdForSkipNaN["c"] shouldBe 4.242640687119285 // std of [3.0, 9.0]

        // Test stdOf with transformation that might produce NaN values
        val dfForTransform = dataFrameOf(
            "a", "b",
        )(
            1.0, 0.0,
            4.0, 2.0,
            0.0, 0.0,
        )

        // Division by zero produces NaN
        dfForTransform.stdOf {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        }.shouldBeNaN() // Default behavior: NaN propagates

        // Skip NaN values from division by zero
        dfForTransform.stdOf(skipNaN = true) {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        }.shouldBeNaN() // Only 4.0/2.0 = 2.0 is valid, std of a single value is NaN
    }

    @Test
    fun `std on empty or nullable column`() {
        val empty = DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false))
        val nullable = DataColumn.createValueColumn("", listOf(null), nothingType(true))

        empty.asSequence().std(empty.type, false, ddofDefault).shouldBeNaN()
        shouldThrow<IllegalStateException> {
            nullable.asSequence().std(nullable.type, false, ddofDefault).shouldBeNaN()
        }
        empty.std().shouldBeNaN()
        nullable.std().shouldBeNaN()
    }
}
