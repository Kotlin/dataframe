@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.percentileByOrNull
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.api.percentileOrNull
import org.jetbrains.kotlinx.dataframe.api.rowPercentileOf
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Test
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class PercentileTests {

    val personsDf = dataFrameOf("name", "age", "city", "weight", "height", "yearsToRetirement")(
        "Alice", 15, "London", 99.5, "1.85", 50,
        "Bob", 20, "Paris", 140.0, "1.35", 45,
        "Charlie", 100, "Dubai", 75.0, "1.95", 0,
        "Rose", 1, "Moscow", 45.33, "0.79", 64,
        "Dylan", 35, "London", 23.4, "1.83", 30,
        "Eve", 40, "Paris", 56.72, "1.85", 25,
        "Frank", 55, "Dubai", 78.9, "1.35", 10,
        "Grace", 29, "Moscow", 67.8, "1.65", 36,
        "Hank", 60, "Paris", 80.22, "1.75", 5,
        "Isla", 22, "London", 75.1, "1.85", 43,
    )

    @Test
    fun `percentileOf test`() {
        val d = personsDf.groupBy("city").percentileOf(75.0, "newAge") { "age"<Int>() * 10 }
        d["newAge"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `percentile of two columns`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 4, "a",
            2, 6, "b",
            7, 7, "c",
        )
        df.percentile(60.0, "a", "b") shouldBe 6.133333333333333
        df.percentile(60.0) { "a"<Int>() and "b"<Int>() } shouldBe 6.133333333333333
        df.percentileOrNull(60.0) { "a"<Int>() and "b"<Int>() } shouldBe 6.133333333333333
        df.percentile(50.0, "c") shouldBe "b"

        df.percentile<_, String>(50.0) { "c"<String>() } shouldBe "b"
        df.percentileOrNull<_, String>(50.0) { "c"<String>() } shouldBe "b"
    }

    @Test
    fun `row percentile`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 3, 3,
            2, 4, 10,
            7, 7, 1,
        )
        df.mapToColumn("", Infer.Type) { it.rowPercentileOf<Int>(25.0) } shouldBe columnOf(1, 2, 2)
        df.mapToColumn("", Infer.Type) { it.rowPercentileOf<Int>(50.0) } shouldBe columnOf(3, 4, 7)
        df.mapToColumn("", Infer.Type) { it.rowPercentileOf<Int>(75.0) } shouldBe columnOf(3, 9, 7)
    }

    @Test
    fun `percentile with regular values`() {
        val col = columnOf(5, 2, 8, 1, 9)
        col.percentile(25.0) shouldBe 1.6666666666666665
        col.percentile(50.0) shouldBe 5
        col.percentile(75.0) shouldBe 8.333333333333332
        col.percentile(90.0) shouldBe 9
    }

    @Test
    fun `percentile with null`() {
        val colWithNull = columnOf(5, 2, null, 1, 9)
        colWithNull.percentile(25.0) shouldBe 1.4166666666666665
        colWithNull.percentile(50.0) shouldBe 3.5
        colWithNull.percentile(75.0) shouldBe 7.333333333333334
    }

    @Test
    fun `percentile with different numeric types`() {
        // Integer types
        columnOf(5, 2, 8, 1, 9).percentile(50.0) shouldBe 5.0
        columnOf(5L, 2L, 8L, 1L, 9L).percentile(50.0) shouldBe 5.0

        // Floating point types
        columnOf(5.0, 2.0, 8.0, 1.0, 9.0).percentile(50.0) shouldBe 5.0
        columnOf(5.0f, 2.0f, 8.0f, 1.0f, 9.0f).percentile(50.0) shouldBe 5.0

        // Test with different percentile values
        columnOf(5, 2, 8, 1, 9).percentile(25.0) shouldBe 1.6666666666666665
        columnOf(5, 2, 8, 1, 9).percentile(75.0) shouldBe 8.333333333333332
    }

    @Test
    fun `percentile with empty column`() {
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).percentileOrNull(50.0).shouldBeNull()
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).percentileOrNull(25.0).shouldBeNull()
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).percentileOrNull(75.0).shouldBeNull()
    }

    @Test
    fun `percentile with just nulls`() {
        val column = DataColumn.createValueColumn<Int?>("", listOf(null, null), nothingType(true))
        column.percentileOrNull(50.0).shouldBeNull()
        column.percentileOrNull(25.0).shouldBeNull()
        column.percentileOrNull(75.0).shouldBeNull()
    }

    @Test
    fun `percentile with just NaNs`() {
        columnOf(Double.NaN, Double.NaN).percentile(50.0).shouldBeNaN()
        columnOf(Double.NaN, Double.NaN).percentileOrNull(50.0)!!.shouldBeNaN()

        // With skipNaN=true and only NaN values, result should be null
        columnOf(Double.NaN, Double.NaN).percentileOrNull(50.0, skipNaN = true).shouldBeNull()
    }

    @Test
    fun `percentile with nans and nulls`() {
        // Percentile functions should return NaN if any value is NaN
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).percentile(50.0).shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).percentile(50.0, skipNaN = true) shouldBe 2.0
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).percentile(25.0, skipNaN = true) shouldBe 1.1666666666666667
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).percentile(75.0, skipNaN = true) shouldBe 4.5
    }

    @Test
    fun `percentileBy with selector function`() {
        // Test with a data class
        data class Person(val name: String, val age: Int)

        val people = columnOf(
            Person("Charlie", 35),
            Person("Bob", 25),
            Person("Alice", 30),
        )

        // Find person with percentile age
        people.percentileBy(0.0) { it.age } shouldBe Person("Bob", 25)
        people.percentileBy(25.0) { it.age } shouldBe Person("Bob", 25)
        people.percentileBy(50.0) { it.age } shouldBe Person("Alice", 30)
        people.percentileBy(75.0) { it.age } shouldBe Person("Alice", 30)
        people.percentileBy(100.0) { it.age } shouldBe Person("Charlie", 35)

        // With null values
        val peopleWithNull = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            null,
            Person("Charlie", 35),
        )

        peopleWithNull.percentileBy(50.0) { it?.age ?: Int.MAX_VALUE } shouldBe Person("Alice", 30)
        peopleWithNull.percentileByOrNull(50.0) { it?.age ?: Int.MAX_VALUE } shouldBe Person("Alice", 30)
    }

    @Test
    fun `percentileOf with transformer function`() {
        // Test with strings that can be converted to numbers
        val strings = columnOf("5", "2", "8", "1", "9")
        strings.percentileOf(50.0) { it.toInt() } shouldBe 5
        strings.percentileOf(25.0) { it.toInt() } shouldBe 1.6666666666666665
        strings.percentileOf(75.0) { it.toInt() } shouldBe 8.333333333333332
    }

    @Test
    fun `percentileOf with transformer function with nulls`() {
        val stringsWithNull = columnOf("5", "2", null, "1", "9")
        stringsWithNull.percentileOf(50.0) { it?.toInt() } shouldBe 3.5
        stringsWithNull.percentileOf(25.0) { it?.toInt() } shouldBe 1.4166666666666665
        stringsWithNull.percentileOf(75.0) { it?.toInt() } shouldBe 7.333333333333334
    }

    @Test
    fun `percentileOf with transformer function with NaNs`() {
        // Percentile functions should return NaN if any value is NaN
        val mixedValues = columnOf("5.0", "2.0", "NaN", "1.0", "9.0")
        mixedValues.percentileOf(50.0) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        mixedValues.percentileOf(50.0, skipNaN = true) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        } shouldBe 3.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowPercentileOf with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, 5, 6,
            7f, 8, 9,
        )

        // Find percentile value in each row
        df[0].rowPercentileOf<Int>(25.0) shouldBe 2.0
        df[0].rowPercentileOf<Int>(50.0) shouldBe 2.5
        df[0].rowPercentileOf<Int>(75.0) shouldBe 3.0

        df[1].rowPercentileOf<Float>(50.0) shouldBe 4.0
        df[2].rowPercentileOf<Int>(50.0) shouldBe 8.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe percentile`() {
        val df = dataFrameOf(
            "a", "b", "c", "d",
        )(
            1, 2f, 3.0, 1.toBigInteger(),
            4, 5f, 6.0, 2.toBigInteger(),
            7, 8f, 9.0, 4.toBigInteger(),
        )

        // Get row with percentile values for each column
        val percentiles50 = df.percentile(50.0)
        percentiles50["a"] shouldBe 4.0
        percentiles50["b"] shouldBe 5.0
        percentiles50["c"] shouldBe 6.0
        percentiles50["d"] shouldBe 2.toBigInteger() // not interpolated!

        val percentiles25 = df.percentile(25.0)
        percentiles25["a"] shouldBe 1.5000000000000002
        percentiles25["b"] shouldBe 2.5
        percentiles25["c"] shouldBe 3.5
        percentiles25["d"] shouldBe 1.toBigInteger() // not interpolated!

        val percentiles75 = df.percentile(75.0)
        percentiles75["a"] shouldBe 6.5
        percentiles75["b"] shouldBe 7.5
        percentiles75["c"] shouldBe 8.5
        percentiles75["d"] shouldBe 2.toBigInteger() // not interpolated!

        // Test percentile for specific columns
        val percentileFor50 = df.percentileFor(50.0, "a", "c", "d")
        percentileFor50["a"] shouldBe 4.0
        percentileFor50["c"] shouldBe 6.0
        percentileFor50["d"] shouldBe 2.toBigInteger() // not interpolated!
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe percentileBy and percentileOf`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Find row with percentile value of column "a"
        val percentileByA50 = df.percentileBy(50.0, "a")
        percentileByA50["a"] shouldBe 4
        percentileByA50["b"] shouldBe 5
        percentileByA50["c"] shouldBe 6

        val percentileByA25 = df.percentileBy(25.0, "a")
        percentileByA25["a"] shouldBe 1
        percentileByA25["b"] shouldBe 2
        percentileByA25["c"] shouldBe 3

        val percentileByA75 = df.percentileBy(75.0, "a")
        percentileByA75["a"] shouldBe 4
        percentileByA75["b"] shouldBe 5
        percentileByA75["c"] shouldBe 6

        // Find percentile value of a + c for each row, [1+3, 4+6, 7+9] => [4, 10, 16]
        df.percentileOf(50.0) { "a"<Int>() + "c"<Int>() } shouldBe 10.0
        df.percentileOf(25.0) { "a"<Int>() + "c"<Int>() } shouldBe 5.0
        df.percentileOf(75.0) { "a"<Int>() + "c"<Int>() } shouldBe 15.0
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `percentile with NaN values for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(5.0f, 2.0f, Float.NaN, 1.0f, 9.0f)
        floatWithNaN.percentile(50.0).shouldBeNaN() // Percentile functions should return NaN if any value is NaN
        floatWithNaN.percentile(50.0, skipNaN = true) shouldBe 3.5 // With skipNaN=true, NaN values should be ignored
        floatWithNaN.percentile(25.0, skipNaN = true) shouldBe 1.4166666666666665
        floatWithNaN.percentile(75.0, skipNaN = true) shouldBe 7.333333333333334

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(5.0, 2.0, Double.NaN, 1.0, 9.0)
        doubleWithNaN.percentile(50.0).shouldBeNaN() // Percentile functions should return NaN if any value is NaN
        doubleWithNaN.percentile(50.0, skipNaN = true) shouldBe 3.5 // With skipNaN=true, NaN values should be ignored
    }
}
