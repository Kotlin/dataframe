package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.meanOf
import org.jetbrains.kotlinx.dataframe.api.rowMean
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Test
import kotlin.reflect.typeOf

class MeanTests {

    @Test
    fun `type for column with mixed numbers`() {
        val col = columnOf<Number?>(10, 10.0, null)
        col.type() shouldBe typeOf<Number?>()
    }

    @Test
    fun `mean with nans and nulls`() {
        columnOf<Number?>(10, 20, Double.NaN, null).mean().shouldBeNaN()
        columnOf<Number?>(10, 20, Double.NaN, null).mean(skipNaN = true) shouldBe 15

        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).mean().shouldBeNaN()
        DataColumn.createValueColumn("", listOf(null), nothingType(true)).mean().shouldBeNaN()
    }

    @Test
    fun `mean with int values`() {
        val col = columnOf(1, 2, 3, 4, 5)
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Int?>(1, 2, 3, 4, 5, null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with long values`() {
        val col = columnOf(1L, 2L, 3L, 4L, 5L)
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Long?>(1L, 2L, 3L, 4L, 5L, null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with float values`() {
        val col = columnOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Float?>(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with double values`() {
        val col = columnOf(1.0, 2.0, 3.0, 4.0, 5.0)
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Double?>(1.0, 2.0, 3.0, 4.0, 5.0, null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with short values`() {
        val col = columnOf(1.toShort(), 2.toShort(), 3.toShort(), 4.toShort(), 5.toShort())
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Short?>(1.toShort(), 2.toShort(), 3.toShort(), 4.toShort(), 5.toShort(), null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with byte values`() {
        val col = columnOf(1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte())
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Byte?>(1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte(), null)
        colWithNull.mean() shouldBe 3.0
    }

    @Test
    fun `mean with mixed number types`() {
        val col = columnOf<Number>(1, 2L, 3.0f, 4.0, 5.toShort())
        col.mean() shouldBe 3.0

        val colWithNull = columnOf<Number?>(1, 2L, 3.0f, 4.0, 5.toShort(), null)
        colWithNull.mean() shouldBe 3.0

        // Mix of different integer types
        val intMix = columnOf<Number>(1, 2L, 3.toShort(), 4.toByte())
        intMix.mean() shouldBe 2.5

        // Mix of different floating point types
        val floatMix = columnOf<Number>(1.0f, 2.0, 3.0f, 4.0)
        floatMix.mean() shouldBe 2.5

        // Mix of integer and floating point types
        val mixedTypes = columnOf<Number>(1, 2.0f, 3L, 4.0)
        mixedTypes.mean() shouldBe 2.5
    }

    @Test
    fun `meanOf with transformation`() {
        val col = columnOf("1", "2", "3", "4", "5")
        col.meanOf { it.toInt() } shouldBe 3.0

        val colWithNull = columnOf("1", "2", "3", null, "5")
        colWithNull.meanOf { it?.toInt() } shouldBe 2.75
    }

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `rowMean with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        df[0].rowMean() shouldBe 2.0
        df[1].rowMean() shouldBe 5.0
        df[2].rowMean() shouldBe 8.0
    }

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `dataframe mean`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2.0, 3f,
            4, 5.0, 6f,
            7, 8.0, 9f,
        )

        val means = df.mean()
        means["a"] shouldBe 4.0
        means["b"] shouldBe 5.0
        means["c"] shouldBe 6.0

        // Test mean for specific columns
        val meanFor = df.meanFor("a", "c")
        meanFor["a"] shouldBe 4.0
        meanFor["c"] shouldBe 6.0

        // Test mean of all columns as a single value
        df.mean("a", "b", "c") shouldBe 5.0
    }

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `dataframe meanOf with transformation`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        df.meanOf { "a"<Int>() + "c"<Int>() } shouldBe 10.0
    }

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `mean with skipNaN for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(1.0f, 2.0f, Float.NaN, 4.0f, 5.0f)
        floatWithNaN.mean().shouldBeNaN() // Default behavior: NaN propagates
        floatWithNaN.mean(skipNaN = true) shouldBe 3.0 // Skip NaN values

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(1.0, 2.0, Double.NaN, 4.0, 5.0)
        doubleWithNaN.mean().shouldBeNaN() // Default behavior: NaN propagates
        doubleWithNaN.mean(skipNaN = true) shouldBe 3.0 // Skip NaN values

        // Test with multiple NaN values in different positions
        val multipleNaN = columnOf(Float.NaN, 2.0f, Float.NaN, 4.0f, Float.NaN)
        multipleNaN.mean().shouldBeNaN() // Default behavior: NaN propagates
        multipleNaN.mean(skipNaN = true) shouldBe 3.0 // Skip NaN values

        // Test with all NaN values
        val allNaN = columnOf(Float.NaN, Float.NaN, Float.NaN)
        allNaN.mean().shouldBeNaN() // Default behavior: NaN propagates
        allNaN.mean(skipNaN = true).shouldBeNaN() // No valid values, result is NaN

        // Test with mixed number types including NaN
        val mixedWithNaN = columnOf<Number>(1, 2.0f, Double.NaN, 4L, 5.0)
        mixedWithNaN.mean().shouldBeNaN() // Default behavior: NaN propagates
        mixedWithNaN.mean(skipNaN = true) shouldBe 3.0 // Skip NaN values

        // Test with DataFrame containing NaN values
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            4.0, 5.0, Float.NaN,
            Double.NaN, 8.0, 9.0,
        )

        // Test DataFrame mean with skipNaN
        val meansWithNaN = dfWithNaN.mean() // Default behavior
        (meansWithNaN["a"] as Double).shouldBeNaN() // Contains NaN
        (meansWithNaN["b"] as Double).shouldBeNaN() // Contains NaN
        (meansWithNaN["c"] as Double).shouldBeNaN() // Contains NaN

        val meansSkipNaN = dfWithNaN.mean(skipNaN = true) // Skip NaN values
        meansSkipNaN["a"] shouldBe 2.5 // (1.0 + 4.0) / 2
        meansSkipNaN["b"] shouldBe 6.5 // (5.0 + 8.0) / 2
        meansSkipNaN["c"] shouldBe 6.0 // (3.0 + 9.0) / 2

        // Test meanFor with skipNaN
        val meanForWithNaN = dfWithNaN.meanFor("a", "c") // Default behavior
        (meanForWithNaN["a"] as Double).shouldBeNaN() // Contains NaN
        (meanForWithNaN["c"] as Double).shouldBeNaN() // Contains NaN

        val meanForSkipNaN = dfWithNaN.meanFor("a", "c", skipNaN = true) // Skip NaN values
        meanForSkipNaN["a"] shouldBe 2.5 // (1.0 + 4.0) / 2
        meanForSkipNaN["c"] shouldBe 6.0 // (3.0 + 9.0) / 2

        // Test mean of all columns as a single value with skipNaN
        dfWithNaN.mean("a", "b", "c").shouldBeNaN() // Default behavior: NaN propagates
        dfWithNaN.mean("a", "b", "c", skipNaN = true) shouldBe 5.0 // Skip NaN values

        // Test meanOf with transformation that might produce NaN values
        val dfForTransform = dataFrameOf(
            "a", "b",
        )(
            1.0, 0.0,
            4.0, 2.0,
            0.0, 0.0,
        )

        // Division by zero produces NaN
        dfForTransform.meanOf { "a"<Double>() / "b"<Double>() }.shouldBeNaN() // Default behavior: NaN propagates

        // Skip NaN values from division by zero
        dfForTransform.meanOf(skipNaN = true) {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        } shouldBe 2.0 // Only 4.0/2.0 = 2.0 is valid
    }
}
