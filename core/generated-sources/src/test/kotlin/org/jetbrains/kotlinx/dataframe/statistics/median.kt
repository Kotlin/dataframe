@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.medianByOrNull
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.medianOrNull
import org.jetbrains.kotlinx.dataframe.api.rowMedianOf
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Test
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class MedianTests {

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `median on personsDf with Float and BigInteger`() {
        // Align with box tests: include a Float and a BigInteger column; BigInteger should be excluded from median results
        val personsDf = dataFrameOf(
            "name", "age", "city", "weight", "height", "yearsToRetirement", "workExperienceYears", "dependentsCount", "annualIncome", "bigNumber",
        )(
            "Alice", 15, "London", 99.5, "1.85", 50f, 0.toShort(), 0.toByte(), 0L, 1.toBigInteger(),
            "Bob", 20, "Paris", 140.0, "1.35", 45f, 2.toShort(), 0.toByte(), 12_000L, 2.toBigInteger(),
            "Charlie", 100, "Dubai", 75.0, "1.95", 0f, 70.toShort(), 0.toByte(), 0L, 3.toBigInteger(),
            "Rose", 1, "Moscow", 45.33, "0.79", 64f, 0.toShort(), 2.toByte(), 0L, 4.toBigInteger(),
            "Dylan", 35, "London", 23.4, "1.83", 30f, 15.toShort(), 1.toByte(), 90_000L, 5.toBigInteger(),
            "Eve", 40, "Paris", 56.72, "1.85", 25f, 18.toShort(), 3.toByte(), 125_000L, 6.toBigInteger(),
            "Frank", 55, "Dubai", 78.9, "1.35", 10f, 35.toShort(), 2.toByte(), 145_000L, 7.toBigInteger(),
            "Grace", 29, "Moscow", 67.8, "1.65", 36f, 5.toShort(), 1.toByte(), 70_000L, 8.toBigInteger(),
            "Hank", 60, "Paris", 80.22, "1.75", 5f, 40.toShort(), 4.toByte(), 200_000L, 9.toBigInteger(),
            "Isla", 22, "London", 75.1, "1.85", 43f, 1.toShort(), 0.toByte(), 30_000L, 10.toBigInteger(),
        )

        val medians = personsDf.median()

        // Should include numeric and string columns, but not BigInteger
        ("bigNumber" in medians.columnNames()) shouldBe false

        medians["age"] shouldBe 32.0
        medians["weight"] shouldBe 75.05
        medians["yearsToRetirement"] shouldBe 33.0
        medians["workExperienceYears"] shouldBe 10.0
        medians["dependentsCount"] shouldBe 1.0
        medians["annualIncome"] shouldBe 50_000.0

        // Also ensure string columns are present (values depend on ordering rules; avoid asserting exact value)
        ("name" in medians.columnNames()) shouldBe true
        ("city" in medians.columnNames()) shouldBe true
        ("height" in medians.columnNames()) shouldBe true
    }

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
    fun `medianOf test`() {
        val d = personsDf.groupBy("city").medianOf("newAge") { "age"<Int>() * 10 }
        d["newAge"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `median of two columns`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 4, "a",
            2, 6, "b",
            7, 7, "c",
        )
        df.median("a", "b") shouldBe 5.0
        df.median { "a"<Int>() and "b"<Int>() } shouldBe 5.0
        df.medianOrNull { "a"<Int>() and "b"<Int>() } shouldBe 5.0
        df.median("c") shouldBe "b"

        df.median<_, String> { "c"<String>() } shouldBe "b"
        df.medianOrNull<_, String> { "c"<String>() } shouldBe "b"

        df.median({ "c"<String>() }) shouldBe "b"
        df.medianOrNull({ "c"<String>() }) shouldBe "b"
        df.median<_, String> { "c"<String>() } shouldBe "b"
        df.medianOrNull<_, String> { "c"<String>() } shouldBe "b"
    }

    @Test
    fun `row median`() {
        val df = dataFrameOf("a", "b")(
            1, 3,
            2, 4,
            7, 7,
        )
        df.mapToColumn("", Infer.Type) { it.rowMedianOf<Int>() } shouldBe columnOf(2, 3, 7)
    }

    @Test
    fun `median with regular values`() {
        val col = columnOf(5, 2, 8, 1, 9)
        col.median() shouldBe 5
    }

    @Test
    fun `median with null`() {
        val colWithNull = columnOf(5, 2, null, 1, 9)
        colWithNull.median() shouldBe 3.5
    }

    @Test
    fun `median with different numeric types`() {
        // Integer types
        columnOf(5, 2, 8, 1, 9).median() shouldBe 5.0
        columnOf(5L, 2L, 8L, 1L, 9L).median() shouldBe 5.0
        columnOf(5.toShort(), 2.toShort(), 8.toShort(), 1.toShort(), 9.toShort()).median() shouldBe 5.0
        columnOf(5.toByte(), 2.toByte(), 8.toByte(), 1.toByte(), 9.toByte()).median() shouldBe 5.0

        // Floating point types
        columnOf(5.0, 2.0, 8.0, 1.0, 9.0).median() shouldBe 5.0
        columnOf(5.0f, 2.0f, 8.0f, 1.0f, 9.0f).median() shouldBe 5.0
    }

    @Test
    fun `median with empty column`() {
        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).medianOrNull().shouldBeNull()
    }

    @Test
    fun `median with just nulls`() {
        val column = DataColumn.createValueColumn<Int?>("", listOf(null, null), nothingType(true))
        column.medianOrNull().shouldBeNull()
    }

    @Test
    fun `median with just NaNs`() {
        columnOf(Double.NaN, Double.NaN).median().shouldBeNaN()
        columnOf(Double.NaN, Double.NaN).medianOrNull()!!.shouldBeNaN()

        // With skipNaN=true and only NaN values, result should be null
        columnOf(Double.NaN, Double.NaN).medianOrNull(skipNaN = true).shouldBeNull()
    }

    @Test
    fun `median with nans and nulls`() {
        // Median functions should return NaN if any value is NaN
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).median().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        columnOf(5.0, 2.0, Double.NaN, 1.0, null).median(skipNaN = true) shouldBe 2.0
    }

    @Test
    fun `medianBy with selector function`() {
        // Test with a data class
        data class Person(val name: String, val age: Int)

        val people = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            Person("Charlie", 35),
        )

        // Find person with median age
        people.medianBy { it.age } shouldBe Person("Alice", 30)

        // With null values
        val peopleWithNull = columnOf(
            Person("Alice", 30),
            Person("Bob", 25),
            null,
            Person("Charlie", 35),
        )

        peopleWithNull.medianBy { it?.age ?: Int.MAX_VALUE } shouldBe Person("Alice", 30)
        peopleWithNull.medianByOrNull { it?.age ?: Int.MAX_VALUE } shouldBe Person("Alice", 30)
        // can sort by null, as it will be filtered out
        peopleWithNull.medianBy { it?.age } shouldBe Person("Alice", 30)
        peopleWithNull.medianByOrNull { it?.age } shouldBe Person("Alice", 30)
    }

    @Test
    fun `medianOf with transformer function`() {
        // Test with strings that can be converted to numbers
        val strings = columnOf("5", "2", "8", "1", "9")
        strings.medianOf { it.toInt() } shouldBe 5
    }

    @Test
    fun `medianOf with transformer function with nulls`() {
        val stringsWithNull = columnOf("5", "2", null, "1", "9")
        stringsWithNull.medianOf { it?.toInt() } shouldBe 3.5
    }

    @Test
    fun `medianOf with transformer function with NaNs`() {
        // Median functions should return NaN if any value is NaN
        val mixedValues = columnOf("5.0", "2.0", "NaN", "1.0", "9.0")
        mixedValues.medianOf {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        }.shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        mixedValues.medianOf(skipNaN = true) {
            val num = it.toDoubleOrNull()
            if (num == null || num.isNaN()) Double.NaN else num
        } shouldBe 3.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMedianOf with dataframe`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, 5, 6,
            7f, 8, 9,
        )

        // Find median value in each row
        df[0].rowMedianOf<Int>() shouldBe 2.5
        df[1].rowMedianOf<Float>() shouldBe 4.0
        df[2].rowMedianOf<Int>() shouldBe 8.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMedianOf with dataframe and nulls`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1f, 2, 3,
            4f, null, 6,
            7f, 8, 9,
        )

        // Find median value in each row
        df[0].rowMedianOf<Int>() shouldBe 2.5

        df[1].rowMedianOf<Float>() shouldBe 4.0
        df[1].rowMedianOf<Int>() shouldBe 6.0

        df[2].rowMedianOf<Int>() shouldBe 8.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `rowMedianOf with dataframe and NaNs`() {
        // Median functions should return NaN if any value is NaN
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            1.0, Double.NaN, 3.0,
            Double.NaN, 5.0, 6.0,
            7.0, 8.0, Double.NaN,
        )

        dfWithNaN[0].rowMedianOf<Double>().shouldBeNaN()
        dfWithNaN[1].rowMedianOf<Double>().shouldBeNaN()
        dfWithNaN[2].rowMedianOf<Double>().shouldBeNaN()

        // With skipNaN=true, NaN values should be ignored
        dfWithNaN[0].rowMedianOf<Double>(skipNaN = true) shouldBe 2.0
        dfWithNaN[1].rowMedianOf<Double>(skipNaN = true) shouldBe 5.5
        dfWithNaN[2].rowMedianOf<Double>(skipNaN = true) shouldBe 7.5
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe median`() {
        val df = dataFrameOf(
            "a", "b", "c", "d",
        )(
            1, 2f, 3.0, 1.toBigInteger(),
            4, 5f, 6.0, 2.toBigInteger(),
            7, 8f, 9.0, 4.toBigInteger(),
        )

        // Get row with median values for each column
        val medians = df.median()
        medians["a"] shouldBe 4.0
        medians["b"] shouldBe 5.0
        medians["c"] shouldBe 6.0
        medians["d"] shouldBe 2.toBigInteger() // not interpolated!

        // Test median for specific columns
        val medianFor = df.medianFor("a", "c", "d")
        medianFor["a"] shouldBe 4.0
        medianFor["c"] shouldBe 6.0
        medianFor["d"] shouldBe 2.toBigInteger() // not interpolated!
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `dataframe medianBy and medianOf`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
            7, 8, 9,
        )

        // Find row with median value of column "a"
        val medianByA = df.medianBy("a")
        medianByA["a"] shouldBe 4
        medianByA["b"] shouldBe 5
        medianByA["c"] shouldBe 6

        // Find median value of a + c for each row
        df.medianOf { "a"<Int>() + "c"<Int>() } shouldBe 10 // 4 + 6 = 10
    }

    @[Test Suppress("ktlint:standard:argument-list-wrapping")]
    fun `median with NaN values for floating point numbers`() {
        // Test with Float.NaN values
        val floatWithNaN = columnOf(5.0f, 2.0f, Float.NaN, 1.0f, 9.0f)
        floatWithNaN.median().shouldBeNaN() // Median functions should return NaN if any value is NaN
        floatWithNaN.median(skipNaN = true) shouldBe 3.5 // With skipNaN=true, NaN values should be ignored

        // Test with Double.NaN values
        val doubleWithNaN = columnOf(5.0, 2.0, Double.NaN, 1.0, 9.0)
        doubleWithNaN.median().shouldBeNaN() // Median functions should return NaN if any value is NaN
        doubleWithNaN.median(skipNaN = true) shouldBe 3.5 // With skipNaN=true, NaN values should be ignored

        // Test with multiple NaN values in different positions
        val multipleNaN = columnOf(Float.NaN, 2.0f, Float.NaN, 1.0f, Float.NaN)
        multipleNaN.median().shouldBeNaN() // Median functions should return NaN if any value is NaN
        multipleNaN.median(skipNaN = true) shouldBe 1.5f // With skipNaN=true, NaN values should be ignored

        // Test with all NaN values
        val allNaN = columnOf(Float.NaN, Float.NaN, Float.NaN)
        allNaN.median().shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.medianOrNull()!!.shouldBeNaN() // All values are NaN, so result is NaN
        allNaN.medianOrNull(skipNaN = true)
            .shouldBeNull() // With skipNaN=true and only NaN values, result should be null

        // Test with DataFrame containing NaN values
        val dfWithNaN = dataFrameOf(
            "a", "b", "c",
        )(
            5.0, Double.NaN, 3.0,
            4.0, 2.0, Float.NaN,
            Double.NaN, 8.0, 1.0,
        )

        // Test DataFrame median with NaN values
        val mediansWithNaN = dfWithNaN.median() // Median functions should return NaN if any value is NaN
        (mediansWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (mediansWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value
        "c" shouldNotBeIn mediansWithNaN.columnNames() // Column 'c' should be excluded due to mixed number types

        // Test DataFrame median with skipNaN=true
        val mediansWithSkipNaN = dfWithNaN.median(skipNaN = true)
        mediansWithSkipNaN["a"] shouldBe 4.5 // Median of 5.0 and 4.0, skipping NaN
        mediansWithSkipNaN["b"] shouldBe 5.0 // Median of 2.0 and 8.0, skipping NaN

        // Test medianFor with NaN values
        val medianForWithNaN = dfWithNaN.medianFor("a", "b") // Median functions should return NaN if any value is NaN
        (medianForWithNaN["a"] as Double).isNaN() shouldBe true // Column 'a' has a NaN value
        (medianForWithNaN["b"] as Double).isNaN() shouldBe true // Column 'b' has a NaN value

        // Test medianFor with skipNaN=true
        val medianForWithSkipNaN = dfWithNaN.medianFor("a", "b", skipNaN = true)
        medianForWithSkipNaN["a"] shouldBe 4.5 // Median of 5.0 and 4.0, skipping NaN
        medianForWithSkipNaN["b"] shouldBe 5.0 // Median of 2.0 and 8.0, skipping NaN

        // Test median of all columns as a single value
        (
            dfWithNaN.median(
                "a",
                "b",
            ) as Double
        ).isNaN() shouldBe true // Median functions should return NaN if any value is NaN
        dfWithNaN.median("a", "b", skipNaN = true) shouldBe 4.5 // With skipNaN=true, NaN values should be ignored

        // Test medianOf with transformation that might produce NaN values
        val dfForTransform = dataFrameOf(
            "a", "b",
        )(
            4.0, 0.0,
            1.0, 2.0,
            0.0, 0.0,
        )

        // Median functions should return NaN if any value is NaN
        dfForTransform.medianOf {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        }.isNaN() shouldBe true

        // With skipNaN=true, NaN values should be ignored
        dfForTransform.medianOf(skipNaN = true) {
            val b = "b"<Double>()
            if (b == 0.0) Double.NaN else "a"<Double>() / b
        } shouldBe 0.5 // Only 1.0/2.0 = 0.5 is valid
    }
}
