package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.rowSum
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.junit.Test
import java.math.BigDecimal

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
    fun `test multiple columns`() {
        val value1 by columnOf(1, 2, 3)
        val value2 by columnOf(4.0, 5.0, 6.0)
        val value3: DataColumn<Number?> by columnOf(7.0, 8, null)
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

    // Issue #1068
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

        dataFrameOf("a", "b")(1.0, 2L)[0].rowSum().let {
            it shouldBe (3.0.toBigDecimal())
            it::class shouldBe BigDecimal::class
        }
    }
}
