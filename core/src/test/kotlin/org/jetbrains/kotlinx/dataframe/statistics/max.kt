package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxByOrNull
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.maxOfOrNull
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.rowMaxOf
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Ignore
import org.junit.Test

class MaxTests {

    @Test
    fun `max with regular values`() {
        val col = columnOf(5, 2, 8, 1, 9)
        println(col::class.simpleName)
        col.max() shouldBe 9
    }

    @Test
    fun `max with null`() {
        val colWithNull = columnOf(5, 2, null, 1, 9)
        colWithNull.max() shouldBe 9
    }

    @Test
    fun `max with different numeric types`() {
        // Integer types
        columnOf(5, 2, 8, 1, 9).max() shouldBe 9
        columnOf(5L, 2L, 8L, 1L, 9L).max() shouldBe 9L
        columnOf(5.toShort(), 2.toShort(), 8.toShort(), 1.toShort(), 9.toShort()).max() shouldBe 9.toShort()
        columnOf(5.toByte(), 2.toByte(), 8.toByte(), 1.toByte(), 9.toByte()).max() shouldBe 9.toByte()

        // Floating point types
        columnOf(5.0, 2.0, 8.0, 1.0, 9.0).max() shouldBe 9.0
        columnOf(5.0f, 2.0f, 8.0f, 1.0f, 9.0f).max() shouldBe 9.0f
    }

    @Ignore
    @Test
    fun `max with mixed numeric type`() {
        // Mixed number types todo https://github.com/Kotlin/dataframe/issues/1113
        // columnOf<Number>(5, 2L, 8.0f, 1.0, 9.toShort()).max() shouldBe 9.0
    }

    @Test
    fun `max with empty column`() {
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).maxOrNull().shouldBeNull()
    }

    @Test
    fun `max with just nulls`() {
        DataColumn.createValueColumn("", listOf(null, null), nothingType(true)).maxOrNull().shouldBeNull()
    }

    @Test
    fun `max with just NaNs`() {
        columnOf(Double.NaN, Double.NaN).max().shouldBeNaN()
        columnOf(Double.NaN, Double.NaN).maxOrNull()!!.shouldBeNaN()

        // With skipNaN=true and only NaN values, result should be null
        columnOf(Double.NaN, Double.NaN).maxOrNull(skipNaN = true).shouldBeNull()
    }

    @Test
    fun `max with nans and nulls`() {
        // Max functions should return NaN if any value is NaN
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).max().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).max(skipNaN = true) shouldBe 5.0
    }

    @Test
    fun `maxBy with selector function`() {
        // Test with a data class
        data class Person(val name: String, val age: Int)

        val people = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            Person("Charlie", 35),
        )

        // Find person with maximum age
        people.maxBy { it.age } shouldBe Person("Charlie", 35)

        // With null values
        val peopleWithNull = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            null,
            Person("Charlie", 35),
        )

        peopleWithNull.maxBy { it?.age ?: Int.MIN_VALUE } shouldBe Person("Charlie", 35)
        peopleWithNull.maxByOrNull { it?.age ?: Int.MIN_VALUE } shouldBe Person("Charlie", 35)
        // can sort by null, as it will be filtered out
        peopleWithNull.maxBy { it?.age } shouldBe Person("Charlie", 35)
        peopleWithNull.maxByOrNull { it?.age } shouldBe Person("Charlie", 35)
    }

    @Test
    fun `maxOf with transformer function`() {
        // Test with strings that can be converted to numbers
        val strings = columnOf("5", "2", "8", "1", "9")
        strings.maxOf { it.toInt() } shouldBe 9
        strings.maxOfOrNull { it.toInt() } shouldBe 9
    }

    @Test
    fun `maxOf with transformer function with nulls`() {
        val stringsWithNull = columnOf("5", "2", null, "1", "9")
        stringsWithNull.maxOf { it?.toInt() } shouldBe 9
        stringsWithNull.maxOfOrNull { it?.toInt() } shouldBe 9
    }

    @Test
    fun `maxOf with transformer function with NaNs`() {
        // Max functions should return NaN if any value is NaN
        val mixedValues = columnOf("5.0", "2.0", "NaN", "1.0", "9.0")
        mixedValues.maxOf {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        mixedValues.maxOf(skipNaN = true) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        } shouldBe 9.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMaxOf with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, 5, 6,
            7f, 8, 9,
        )

        // Find maximum value in each row
        df[0].rowMaxOf<Int>() shouldBe 3
        df[1].rowMaxOf<Float>() shouldBe 4f
        df[2].rowMaxOf<Int>() shouldBe 9
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMaxOf with dataframe and nulls`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, null, 6,
            7f, 8, 9,
        )

        // Find maximum value in each row
        df[0].rowMaxOf<Int>() shouldBe 3

        df[1].rowMaxOf<Float>() shouldBe 4f
        df[1].rowMaxOf<Int>() shouldBe 6

        df[2].rowMaxOf<Int>() shouldBe 9
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMaxOf with dataframe and NaNs`() {
        // Max functions should return NaN if any value is NaN
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            Double.NaN, 5.0, 6.0,
            7.0, 8.0, Double.NaN,
        )

        dfWithNaN[0].rowMaxOf<Double>().shouldBeNaN()
        dfWithNaN[1].rowMaxOf<Double>().shouldBeNaN()
        dfWithNaN[2].rowMaxOf<Double>().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        dfWithNaN[0].rowMaxOf<Double>(skipNaN = true) shouldBe 3.0
        dfWithNaN[1].rowMaxOf<Double>(skipNaN = true) shouldBe 6.0
        dfWithNaN[2].rowMaxOf<Double>(skipNaN = true) shouldBe 8.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe max`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2f, 3.0,
            4, 5f, 6.0,
            7, 8f, 9.0,
        )

        // Get row with maximum values for each column
        val maxs = df.max()
        maxs["a"] shouldBe 7
        maxs["b"] shouldBe 8f
        maxs["c"] shouldBe 9.0

        // Test max for specific columns
        val maxFor = df.maxFor("a", "c")
        maxFor["a"] shouldBe 7
        maxFor["c"] shouldBe 9.0
    }

    @Ignore
    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe max mixed number types`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2f, 3.0,
            4, 5f, 6.0,
            7, 8f, 9.0,
        )

        // Test max of all columns as a single value
        // TODO https://github.com/Kotlin/dataframe/issues/1113
        df.max("a", "b", "c") shouldBe 9
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe maxBy and maxOf`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Find row with maximum value of column "a"
        val maxByA = df.maxBy("a")
        maxByA["a"] shouldBe 7
        maxByA["b"] shouldBe 8
        maxByA["c"] shouldBe 9

        // Find maximum value of a + c for each row
        df.maxOf { "a"<Int>() + "c"<Int>() } shouldBe 16 // 7 + 9 = 16
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `max with NaN values for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(5.0f, 2.0f, Float.NaN, 1.0f, 9.0f)
        floatWithNaN.max().shouldBeNaN() // Max functions should return NaN if any value is NaN
        floatWithNaN.max(skipNaN = true) shouldBe 9.0f // With skipNaN=true, NaN values should be ignored

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(5.0, 2.0, Double.NaN, 1.0, 9.0)
        doubleWithNaN.max().shouldBeNaN() // Max functions should return NaN if any value is NaN
        doubleWithNaN.max(skipNaN = true) shouldBe 9.0 // With skipNaN=true, NaN values should be ignored

        // Test with multiple NaN values in different positions
        val multipleNaN = columnOf(Float.NaN, 2.0f, Float.NaN, 1.0f, Float.NaN)
        multipleNaN.max().shouldBeNaN() // Max functions should return NaN if any value is NaN
        multipleNaN.max(skipNaN = true) shouldBe 2.0f // With skipNaN=true, NaN values should be ignored

        // Test with all NaN values
        val allNaN = columnOf(Float.NaN, Float.NaN, Float.NaN)
        allNaN.max().shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.maxOrNull()!!.shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.maxOrNull(skipNaN = true).shouldBeNull() // With skipNaN=true and only NaN values, result should be null

        // Test with mixed number types including NaN todo https://github.com/Kotlin/dataframe/issues/1113
//        val mixedWithNaN = columnOf<Number>(5, 2.0f, Double.NaN, 1L, 9.0)
//        mixedWithNaN.max().shouldBeNaN() // Default behavior: NaN propagates
//        mixedWithNaN.max(skipNaN = true) shouldBe 1.0 // Skip NaN values

        // Test with DataFrame containing NaN values
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            5.0, Double.NaN, 3.0,
            4.0, 2.0, Float.NaN,
            Double.NaN, 8.0, 1.0,
        )

        // Test DataFrame max with NaN values
        val maxsWithNaN = dfWithNaN.max() // Max functions should return NaN if any value is NaN
        (maxsWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (maxsWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value
        "c" shouldNotBeIn maxsWithNaN.columnNames() // Column 'c' should be excluded due to mixed number types todo

        // Test DataFrame max with skipNaN=true
        val maxsWithSkipNaN = dfWithNaN.max(skipNaN = true)
        maxsWithSkipNaN["a"] shouldBe 5.0 // Max of 5.0 and 4.0, skipping NaN
        maxsWithSkipNaN["b"] shouldBe 8.0 // Max of 2.0 and 8.0, skipping NaN
        // todo maxsWithSkipNaN["c"] shouldBe 1.0 https://github.com/Kotlin/dataframe/issues/1113

        // Test maxFor with NaN values
        val maxForWithNaN = dfWithNaN.maxFor("a", "b") // Max functions should return NaN if any value is NaN
        (maxForWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (maxForWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value

        // Test maxFor with skipNaN=true
        val maxForWithSkipNaN = dfWithNaN.maxFor("a", "b", skipNaN = true)
        maxForWithSkipNaN["a"] shouldBe 5.0 // Max of 5.0 and 4.0, skipping NaN
        maxForWithSkipNaN["b"] shouldBe 8.0 // Max of 2.0 and 8.0, skipping NaN

        // Test max of all columns as a single value
        (dfWithNaN.max("a", "b") as Double).isNaN() shouldBe true // Max functions should return NaN if any value is NaN
        dfWithNaN.max("a", "b", skipNaN = true) shouldBe 8.0 // With skipNaN=true, NaN values should be ignored

        // Test maxOf with transformation that might produce NaN values
        val dfForTransform = dataFrameOf(
            "a", "b",
        )(
            4.0, 0.0,
            1.0, 2.0,
            0.0, 0.0,
        )

        // Max functions should return NaN if any value is NaN
        dfForTransform.maxOf {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        dfForTransform.maxOf(skipNaN = true) {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        } shouldBe 0.5 // Only 1.0/2.0 = 0.5 is valid
    }
}
