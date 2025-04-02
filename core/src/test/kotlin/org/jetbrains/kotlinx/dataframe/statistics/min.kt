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
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minByOrNull
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.minOf
import org.jetbrains.kotlinx.dataframe.api.minOfOrNull
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import org.jetbrains.kotlinx.dataframe.api.rowMinOf
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Ignore
import org.junit.Test

class MinTests {

    @Test
    fun `min with regular values`() {
        val col = columnOf(5, 2, 8, 1, 9)
        col.min() shouldBe 1
    }

    @Test
    fun `min with null`() {
        val colWithNull = columnOf(5, 2, null, 1, 9)
        colWithNull.min() shouldBe 1
    }

    @Test
    fun `min with different numeric types`() {
        // Integer types
        columnOf(5, 2, 8, 1, 9).min() shouldBe 1
        columnOf(5L, 2L, 8L, 1L, 9L).min() shouldBe 1L
        columnOf(5.toShort(), 2.toShort(), 8.toShort(), 1.toShort(), 9.toShort()).min() shouldBe 1.toShort()
        columnOf(5.toByte(), 2.toByte(), 8.toByte(), 1.toByte(), 9.toByte()).min() shouldBe 1.toByte()

        // Floating point types
        columnOf(5.0, 2.0, 8.0, 1.0, 9.0).min() shouldBe 1.0
        columnOf(5.0f, 2.0f, 8.0f, 1.0f, 9.0f).min() shouldBe 1.0f
    }

    @Ignore
    @Test
    fun `min with mixed numeric type`() {
        // Mixed number types todo https://github.com/Kotlin/dataframe/issues/1113
        // columnOf<Number>(5, 2L, 8.0f, 1.0, 9.toShort()).min() shouldBe 1.0
    }

    @Test
    fun `min with empty column`() {
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).minOrNull().shouldBeNull()
    }

    @Test
    fun `min with just nulls`() {
        DataColumn.createValueColumn("", listOf(null, null), nothingType(true)).minOrNull().shouldBeNull()
    }

    @Test
    fun `min with just NaNs`() {
        columnOf(Double.NaN, Double.NaN).min().shouldBeNaN()
        columnOf(Double.NaN, Double.NaN).minOrNull()!!.shouldBeNaN()

        // With skipNaN=true and only NaN values, result should be null
        columnOf(Double.NaN, Double.NaN).minOrNull(skipNaN = true).shouldBeNull()
    }

    @Test
    fun `min with nans and nulls`() {
        // Min functions should return NaN if any value is NaN
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).min().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).min(skipNaN = true) shouldBe 1.0
    }

    @Test
    fun `minBy with selector function`() {
        // Test with a data class
        data class Person(val name: String, val age: Int)

        val people = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            Person("Charlie", 35),
        )

        // Find person with minimum age
        people.minBy { it.age } shouldBe Person("Bob", 25)

        // With null values
        val peopleWithNull = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            null,
            Person("Charlie", 35),
        )

        peopleWithNull.minBy { it?.age ?: Int.MAX_VALUE } shouldBe Person("Bob", 25)
        peopleWithNull.minByOrNull { it?.age ?: Int.MAX_VALUE } shouldBe Person("Bob", 25)
        // can sort by null, as it will be filtered out
        peopleWithNull.minBy { it?.age } shouldBe Person("Bob", 25)
        peopleWithNull.minByOrNull { it?.age } shouldBe Person("Bob", 25)
    }

    @Test
    fun `minOf with transformer function`() {
        // Test with strings that can be converted to numbers
        val strings = columnOf("5", "2", "8", "1", "9")
        strings.minOf { it.toInt() } shouldBe 1
        strings.minOfOrNull { it.toInt() } shouldBe 1
    }

    @Test
    fun `minOf with transformer function with nulls`() {
        val stringsWithNull = columnOf("5", "2", null, "1", "9")
        stringsWithNull.minOf { it?.toInt() } shouldBe 1
        stringsWithNull.minOfOrNull { it?.toInt() } shouldBe 1
    }

    @Test
    fun `minOf with transformer function with NaNs`() {
        // Min functions should return NaN if any value is NaN
        val mixedValues = columnOf("5.0", "2.0", "NaN", "1.0", "9.0")
        mixedValues.minOf {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        mixedValues.minOf(skipNaN = true) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        } shouldBe 1.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMinOf with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, 5, 6,
            7f, 8, 9,
        )

        // Find minimum value in each row
        df[0].rowMinOf<Int>() shouldBe 2
        df[1].rowMinOf<Float>() shouldBe 4f
        df[2].rowMinOf<Int>() shouldBe 8
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMinOf with dataframe and nulls`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, null, 6,
            7f, 8, 9,
        )

        // Find minimum value in each row
        df[0].rowMinOf<Int>() shouldBe 3
        df[0].rowMinOf<Int?>() shouldBe 2 // TODO?

        df[1].rowMinOf<Float>() shouldBe 4f
        df[1].rowMinOf<Int>() shouldBe 6
        df[1].rowMinOf<Int?>() shouldBe 6

        df[2].rowMinOf<Int>() shouldBe 9
        df[2].rowMinOf<Int?>() shouldBe 8 // TODO?
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMinOf with dataframe and NaNs`() {
        // Min functions should return NaN if any value is NaN
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            Double.NaN, 5.0, 6.0,
            7.0, 8.0, Double.NaN,
        )

        dfWithNaN[0].rowMinOf<Double>().shouldBeNaN()
        dfWithNaN[1].rowMinOf<Double>().shouldBeNaN()
        dfWithNaN[2].rowMinOf<Double>().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        dfWithNaN[0].rowMinOf<Double>(skipNaN = true) shouldBe 1.0
        dfWithNaN[1].rowMinOf<Double>(skipNaN = true) shouldBe 5.0
        dfWithNaN[2].rowMinOf<Double>(skipNaN = true) shouldBe 7.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe min`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2f, 3.0,
            4, 5f, 6.0,
            7, 8f, 9.0,
        )

        // Get row with minimum values for each column
        val mins = df.min()
        mins["a"] shouldBe 1
        mins["b"] shouldBe 2f
        mins["c"] shouldBe 3.0

        // Test min for specific columns
        val minFor = df.minFor("a", "c")
        minFor["a"] shouldBe 1
        minFor["c"] shouldBe 3.0
    }

    @Ignore
    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe min mixed number types`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2f, 3.0,
            4, 5f, 6.0,
            7, 8f, 9.0,
        )

        // Test min of all columns as a single value
        // TODO https://github.com/Kotlin/dataframe/issues/1113
        df.min("a", "b", "c") shouldBe 1
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe minBy and minOf`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Find row with minimum value of column "a"
        val minByA = df.minBy("a")
        minByA["a"] shouldBe 1
        minByA["b"] shouldBe 2
        minByA["c"] shouldBe 3

        // Find minimum value of a + c for each row
        df.minOf { "a"<Int>() + "c"<Int>() } shouldBe 4 // 1 + 3 = 4
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `min with NaN values for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(5.0f, 2.0f, Float.NaN, 1.0f, 9.0f)
        floatWithNaN.min().shouldBeNaN() // Min functions should return NaN if any value is NaN
        floatWithNaN.min(skipNaN = true) shouldBe 1.0f // With skipNaN=true, NaN values should be ignored

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(5.0, 2.0, Double.NaN, 1.0, 9.0)
        doubleWithNaN.min().shouldBeNaN() // Min functions should return NaN if any value is NaN
        doubleWithNaN.min(skipNaN = true) shouldBe 1.0 // With skipNaN=true, NaN values should be ignored

        // Test with multiple NaN values in different positions
        val multipleNaN = columnOf(Float.NaN, 2.0f, Float.NaN, 1.0f, Float.NaN)
        multipleNaN.min().shouldBeNaN() // Min functions should return NaN if any value is NaN
        multipleNaN.min(skipNaN = true) shouldBe 1.0f // With skipNaN=true, NaN values should be ignored

        // Test with all NaN values
        val allNaN = columnOf(Float.NaN, Float.NaN, Float.NaN)
        allNaN.min().shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.minOrNull()!!.shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.minOrNull(skipNaN = true).shouldBeNull() // With skipNaN=true and only NaN values, result should be null

        // Test with DataFrame containing NaN values
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            5.0, Double.NaN, 3.0,
            4.0, 2.0, Float.NaN,
            Double.NaN, 8.0, 1.0,
        )

        // Test DataFrame min with NaN values
        val minsWithNaN = dfWithNaN.min() // Min functions should return NaN if any value is NaN
        (minsWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (minsWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value
        "c" shouldNotBeIn minsWithNaN.columnNames() // Column 'c' should be excluded due to mixed number types todo

        // Test DataFrame min with skipNaN=true
        val minsWithSkipNaN = dfWithNaN.min(skipNaN = true)
        minsWithSkipNaN["a"] shouldBe 4.0 // Min of 5.0 and 4.0, skipping NaN
        minsWithSkipNaN["b"] shouldBe 2.0 // Min of 2.0 and 8.0, skipping NaN
        // todo minsWithSkipNaN["c"] shouldBe 1.0 https://github.com/Kotlin/dataframe/issues/1113

        // Test minFor with NaN values
        val minForWithNaN = dfWithNaN.minFor("a", "b") // Min functions should return NaN if any value is NaN
        (minForWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (minForWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value

        // Test minFor with skipNaN=true
        val minForWithSkipNaN = dfWithNaN.minFor("a", "b", skipNaN = true)
        minForWithSkipNaN["a"] shouldBe 4.0 // Min of 5.0 and 4.0, skipping NaN
        minForWithSkipNaN["b"] shouldBe 2.0 // Min of 2.0 and 8.0, skipping NaN

        // Test min of all columns as a single value
        (dfWithNaN.min("a", "b") as Double).isNaN() shouldBe true // Min functions should return NaN if any value is NaN
        dfWithNaN.min("a", "b", skipNaN = true) shouldBe 2.0 // With skipNaN=true, NaN values should be ignored

        // Test minOf with transformation that might produce NaN values
        val dfForTransform = dataFrameOf(
            "a", "b",
        )(
            4.0, 0.0,
            1.0, 2.0,
            0.0, 0.0,
        )

        // Min functions should return NaN if any value is NaN
        dfForTransform.minOf {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        }.isNaN() shouldBe true

        // With skipNaN=true, NaN values should be ignored
        dfForTransform.minOf(skipNaN = true) {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        } shouldBe 0.5 // Only 1.0/2.0 = 0.5 is valid
    }
}
