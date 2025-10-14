package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.rowSum
import org.jetbrains.kotlinx.dataframe.api.rowSumOf
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.junit.Test
import kotlin.reflect.typeOf

class SumTests {

    @Test
    fun `test single column`() {
        val value by columnOf(1, 2, 3)
        val df = dataFrameOf(value)
        val expected = 6

        value.values().sum() shouldBe expected
        value.sum() shouldBe expected
        df[value].sum() shouldBe expected
        df.sum { value } shouldBe expected
        df.sum()[value] shouldBe expected
        df.sumOf { value() } shouldBe expected
    }

    @Test
    fun `test single short column`() {
        val value by columnOf(1.toShort(), 2.toShort(), 3.toShort())
        val df = dataFrameOf(value)
        val expected = 6

        value.values().sum() shouldBe expected
        value.sum() shouldBe expected
        df[value].sum() shouldBe expected
        df.sum { value } shouldBe expected
        df.sum()[value] shouldBe expected
        df.sumOf { value() } shouldBe expected
    }

    @Test
    fun `empty column with types`() {
        val emptyIntCol by columnOf<Int?>(null, null)
        emptyIntCol.sum() shouldBe 0

        // empty column with Number type
        val emptyNumberColumn = DataColumn.createValueColumn<Number?>(
            "emptyNumberColumn",
            listOf(null, null),
            typeOf<Number?>(),
        )
        emptyNumberColumn.sum() shouldBe 0.0

        // empty column with nullable Nothing type
        val emptyNothingColumn = DataColumn.createValueColumn(
            "emptyNothingColumn",
            listOf(null, null),
            nullableNothingType,
        )
        emptyNothingColumn.cast<Number?>().sum() shouldBe 0.0
    }

    @Test
    fun `test multiple columns`() {
        val value1 by columnOf(1, 2, 3)
        val value2 by columnOf(4.0, 5.0, 6.0)
        val value3 by columnOf<Number?>(7.0, 8, null)
        val df = dataFrameOf(value1, value2, value3)
        val expected1 = 6
        val expected2 = 15.0
        val expected3 = 15.0

        df.sum()[value1] shouldBe expected1
        df.sum()[value2] shouldBe expected2
        df.sum()[value3] shouldBe expected3
        df.sumOf { value1() } shouldBe expected1
        df.sumOf { value2() } shouldBe expected2
        df.sumOf { value3() } shouldBe expected3
        df.sum(value1) shouldBe expected1
        df.sum(value2) shouldBe expected2
        df.sum(value3) shouldBe expected3
        df.sum { value1 } shouldBe expected1
        df.sum { value2 } shouldBe expected2
        df.sum { value3 } shouldBe expected3
    }

    /** [Issue #1068](https://github.com/Kotlin/dataframe/issues/1068) */
    @Test
    fun `rowSum mixed number types`() {
        dataFrameOf("a", "b")(1, 2f)[0].rowSum().let {
            it shouldBe 3.0
            it::class shouldBe Double::class
        }

        // NOTE! unsigned numbers are not Number, they are skipped for now
        dataFrameOf("a", "b")(1, 2u)[0].rowSum().let {
            it shouldBe 1
            it::class shouldBe Int::class
        }

        // NOTE! lossy conversion from long -> double happens
        dataFrameOf("a", "b")(1.0, 2L)[0].rowSum().let {
            it shouldBe 3.0
            it::class shouldBe Double::class
        }
    }

    @Suppress("DEPRECATION_ERROR")
    @Test
    fun `unknown number type`() {
        columnOf(1.toBigDecimal(), 2.toBigDecimal()).toDataFrame()
            .sum()
            .isEmpty() shouldBe true
    }

    @Test
    fun `mixed numbers`() {
        // mixed number types are picked up implicitly
        columnOf<Number>(1.0, 2).toDataFrame()
            .sum()[0] shouldBe 3.0

        // in the slight case a mixed number column contains unsupported numbers
        // we give a helpful exception telling about primitive support only
        shouldThrow<IllegalArgumentException> {
            columnOf<Number>(1.0, 2, 3.0.toBigDecimal()).toDataFrame().sum()[0]
        }.message?.lowercase() shouldContain "primitive"
    }

    @Test
    fun `test skipNaN with float column`() {
        val value by columnOf(1.0f, 2.0f, Float.NaN, 3.0f)
        val df = dataFrameOf(value)

        // With skipNaN = true (default is false)
        value.sum(skipNaN = true) shouldBe 6.0f
        df[value].sum(skipNaN = true) shouldBe 6.0f
        df.sum(skipNaN = true)[value] shouldBe 6.0f
        df.sumOf(skipNaN = true) { value().toInt() } shouldBe 6

        // With skipNaN = false (default)
        value.sum().shouldBeNaN()
        df[value].sum().shouldBeNaN()
        df.sum()[value].shouldBeNaN()
        df.sumOf { value().toDouble() }.shouldBeNaN()
    }

    @Test
    fun `test skipNaN with double column`() {
        val value by columnOf(1.0, 2.0, Double.NaN, 3.0)
        val df = dataFrameOf(value)

        // With skipNaN = true (default is false)
        value.sum(skipNaN = true) shouldBe 6.0
        df[value].sum(skipNaN = true) shouldBe 6.0
        df.sum(skipNaN = true)[value] shouldBe 6.0
        df.sumOf(skipNaN = true) { value().toLong() } shouldBe 6L

        // With skipNaN = false (default)
        value.sum().shouldBeNaN()
        df[value].sum().shouldBeNaN()
        df.sum()[value].shouldBeNaN()
        df.sumOf { value().toFloat() }.shouldBeNaN()
    }

    @Test
    fun `test rowSum with skipNaN`() {
        val row1 = dataFrameOf("a", "b", "c")(1.0, 2.0, 3.0)[0]
        val row2 = dataFrameOf("a", "b", "c")(1.0, Double.NaN, 3)[0]

        // With skipNaN = true
        row1.rowSum(skipNaN = true) shouldBe 6.0
        row2.rowSum(skipNaN = true) shouldBe 4.0

        // With skipNaN = false (default)
        row1.rowSum() shouldBe 6.0
        (row2.rowSum() as Double).shouldBeNaN()

        // Test rowSumOf
        row1.rowSumOf<Double>(skipNaN = true) shouldBe 6.0
        row2.rowSumOf<Double>(skipNaN = true) shouldBe 1.0
        row1.rowSumOf<Double>() shouldBe 6.0
        row2.rowSumOf<Double>().shouldBeNaN()
    }

    @Test
    fun `test sumFor with skipNaN`() {
        val value1 by columnOf(1.0, 2.0, 3.0)
        val value2 by columnOf<Number>(4.0, Float.NaN, 6)
        val df = dataFrameOf(value1, value2)

        // With skipNaN = true
        df.sumFor(skipNaN = true) { value1 and value2 }[value1] shouldBe 6.0
        df.sumFor(skipNaN = true) { value1 and value2 }[value2] shouldBe 10.0

        // With skipNaN = false (default)
        df.sumFor { value1 and value2 }[value1] shouldBe 6.0
        (df.sumFor { value1 and value2 }[value2] as Double).shouldBeNaN()
    }
}
