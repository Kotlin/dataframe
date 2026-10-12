package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.QuantileMethod
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.quantile
import org.jetbrains.kotlinx.dataframe.api.quantileOrNull
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import kotlin.random.Random
import kotlin.reflect.typeOf

class SelectableQuantileTests {

    @Test
    fun `R1 selects inverse empirical distribution`() {
        val result: Int = columnOf(4, 1, 3, 2).quantile(0.25, QuantileMethod.R1)

        result shouldBe 1
        columnOf(4, 1, 3, 2).quantile(0.6, QuantileMethod.R1) shouldBe 3
    }

    @Test
    fun `R3 uses nearest observation with ties to even position`() {
        val values = columnOf(4, 1, 3, 2)

        values.quantile(0.625, QuantileMethod.R3) shouldBe 2
        values.quantile(0.875, QuantileMethod.R3) shouldBe 4
    }

    @Test
    fun `R7 interpolates modes of order statistics`() {
        val result: Double = columnOf(4, 1, 3, 2).quantile(0.25, QuantileMethod.R7)

        result shouldBe 1.75
    }

    @Test
    fun `R8 interpolates approximate medians of order statistics`() {
        columnOf(4, 1, 3, 2).quantile(0.25, QuantileMethod.R8) shouldBe (17.0 / 12).plusOrMinus(1e-12)
    }

    @Test
    fun `selecting singleton retains each primitive value type`() {
        val byte: Byte = columnOf<Byte>(7).quantile(0.5, QuantileMethod.R1)
        val short: Short = columnOf<Short>(7).quantile(0.5, QuantileMethod.R3)
        val int: Int = columnOf(7).quantile(0.5, QuantileMethod.R1)
        val long: Long = columnOf(9007199254740993L).quantile(0.5, QuantileMethod.R3)
        val float: Float = columnOf(7.0f).quantile(0.5, QuantileMethod.R1)
        val double: Double = columnOf(7.0).quantile(0.5, QuantileMethod.R3)

        listOf(byte, short, int, long, float, double) shouldBe listOf(7.toByte(), 7.toShort(), 7, 9007199254740993L, 7.0f, 7.0)
    }

    @Test
    fun `selection preserves large Long precision without Double conversion`() {
        columnOf(9007199254740993L, 9007199254740992L).quantile(1.0, QuantileMethod.R1) shouldBe 9007199254740993L
    }

    @Test
    fun `selection works for exact decimals and big integers`() {
        val smaller = BigDecimal("1.00000000000000000001")
        val larger = BigDecimal("1.00000000000000000002")

        columnOf(larger, smaller).quantile(0.0, QuantileMethod.R1) shouldBe smaller
        columnOf(BigInteger.TEN, BigInteger.ONE).quantile(1.0, QuantileMethod.R3) shouldBe BigInteger.TEN
    }

    @Test
    fun `selection supports strings and dates`() {
        columnOf("pear", "apple", "orange").quantile(0.5, QuantileMethod.R1) shouldBe "orange"
        val first = LocalDate.of(2026, 10, 12)

        columnOf(first.plusDays(1), first, first.minusDays(1)).quantile(0.5, QuantileMethod.R3) shouldBe first
    }

    @Test
    fun `interpolation returns Double for all primitive inputs`() {
        columnOf<Byte>(1, 3).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
        columnOf<Short>(1, 3).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
        columnOf(1, 3).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
        columnOf(1L, 3L).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
        columnOf(1.0f, 3.0f).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
        columnOf(1.0, 3.0).quantile(0.5, QuantileMethod.R7) shouldBe 2.0
    }

    @Test
    fun `interpolating singleton also returns Double`() {
        val result: Double = columnOf(7).quantile(0.5, QuantileMethod.R8)

        result shouldBe 7.0
    }

    @Test
    fun `nulls are ignored by both method families`() {
        val selected: Int? = columnOf(null, 1, null, 3).quantileOrNull(0.5, QuantileMethod.R1)
        val interpolated: Double? = columnOf(null, 1, null, 3).quantileOrNull(0.5, QuantileMethod.R7)

        selected shouldBe 1
        interpolated shouldBe 2.0
    }

    @Test
    fun `empty and all-null columns return null from nullable forms`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())
        val allNull = DataColumn.createValueColumn<Int?>("empty", listOf(null, null), typeOf<Int?>())

        empty.quantileOrNull(0.5, QuantileMethod.R1) shouldBe null
        empty.quantileOrNull(0.5, QuantileMethod.R7) shouldBe null
        allNull.quantileOrNull(0.5, QuantileMethod.R3) shouldBe null
        allNull.quantileOrNull(0.5, QuantileMethod.R8) shouldBe null
    }

    @Test
    fun `non-null forms report empty input with NoSuchElementException`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())

        shouldThrow<NoSuchElementException> { empty.quantile(0.5, QuantileMethod.R1) }
        shouldThrow<NoSuchElementException> { empty.quantile(0.5, QuantileMethod.R7) }
    }

    @Test
    fun `NaN propagates by default and selecting Float retains Float NaN`() {
        columnOf(1.0, Double.NaN, 3.0).quantile(0.5, QuantileMethod.R1).isNaN() shouldBe true
        columnOf(1.0, Double.NaN, 3.0).quantile(0.5, QuantileMethod.R7).isNaN() shouldBe true
        val selected: Float = columnOf(1.0f, Float.NaN).quantile(0.5, QuantileMethod.R3)

        selected.isNaN() shouldBe true
    }

    @Test
    fun `NaN can be skipped in both method families`() {
        columnOf(1.0, Double.NaN, 3.0).quantile(0.5, QuantileMethod.R1, skipNaN = true) shouldBe 1.0
        columnOf(1.0, Double.NaN, 3.0).quantile(0.5, QuantileMethod.R7, skipNaN = true) shouldBe 2.0
    }

    @Test
    fun `all-skipped NaN yields null or empty exception`() {
        val values = columnOf(Double.NaN, Double.NaN)

        values.quantileOrNull(0.5, QuantileMethod.R1, skipNaN = true) shouldBe null
        values.quantileOrNull(0.5, QuantileMethod.R8, skipNaN = true) shouldBe null
        shouldThrow<NoSuchElementException> { values.quantile(0.5, QuantileMethod.R3, skipNaN = true) }
        shouldThrow<NoSuchElementException> { values.quantile(0.5, QuantileMethod.R7, skipNaN = true) }
    }

    @Test
    fun `probability boundaries select minimum and maximum`() {
        val values = columnOf(4, 1, 3, 2)

        for (method in QuantileMethod.Selecting.entries) {
            values.quantile(0.0, method) shouldBe 1
            values.quantile(1.0, method) shouldBe 4
        }
        for (method in QuantileMethod.Interpolating.entries) {
            values.quantile(0.0, method) shouldBe 1.0
            values.quantile(1.0, method) shouldBe 4.0
        }
    }

    @Test
    fun `invalid probability is rejected even for empty input`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())

        for (p in listOf(-0.1, 1.1, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)) {
            shouldThrow<IllegalArgumentException> { empty.quantileOrNull(p, QuantileMethod.R1) }
            shouldThrow<IllegalArgumentException> { empty.quantileOrNull(p, QuantileMethod.R7) }
        }
    }

    @Test
    fun `interpolation rejects arbitrary precision even for singleton and empty input`() {
        val empty = DataColumn.createValueColumn<BigDecimal>("empty", emptyList(), typeOf<BigDecimal>())

        shouldThrow<IllegalArgumentException> { columnOf(BigDecimal.ONE).quantile(0.5, QuantileMethod.R7) }
        shouldThrow<IllegalArgumentException> { columnOf(BigInteger.ONE).quantile(0.5, QuantileMethod.R8) }
        shouldThrow<IllegalArgumentException> { empty.quantileOrNull(0.5, QuantileMethod.R7) }
    }

    @Test
    fun `interpolation preserves exact infinite endpoints and identical infinite values`() {
        val values = columnOf(Double.NEGATIVE_INFINITY, 1.0, Double.POSITIVE_INFINITY)

        for (method in QuantileMethod.Interpolating.entries) {
            values.quantile(0.0, method) shouldBe Double.NEGATIVE_INFINITY
            values.quantile(1.0, method) shouldBe Double.POSITIVE_INFINITY
            columnOf(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).quantile(0.5, method) shouldBe
                Double.POSITIVE_INFINITY
        }
    }

    @Test
    fun `interpolation of opposite infinities remains arithmetic NaN`() {
        columnOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).quantile(0.5, QuantileMethod.R7)
            .isNaN() shouldBe true
    }

    @Test
    fun `selecting signed zeros uses Kotlin Comparable ordering`() {
        columnOf(0.0, -0.0).quantile(0.0, QuantileMethod.R1).toBits() shouldBe (-0.0).toBits()
        columnOf(0.0, -0.0).quantile(1.0, QuantileMethod.R1).toBits() shouldBe 0.0.toBits()
    }

    @Test
    fun `input values remain unchanged`() {
        val values = columnOf(4, 1, 3, 2)

        values.quantile(0.25, QuantileMethod.R7)
        values.toList() shouldBe listOf(4, 1, 3, 2)
    }

    @Test
    fun `existing percentile numeric and comparable defaults remain unchanged`() {
        val values = columnOf(4, 1, 3, 2)

        values.percentile(25.0) shouldBe values.quantile(0.25, QuantileMethod.R8)
        columnOf("pear", "apple", "orange").percentile(50.0) shouldBe
            columnOf("pear", "apple", "orange").quantile(0.5, QuantileMethod.R3)
        values.percentile(0.0) shouldBe 1.0
        values.percentile(100.0) shouldBe 4.0
    }

    @Test
    fun `selecting methods match sorted order statistic reference on seeded arrays`() {
        val random = Random(1121)
        repeat(100) {
            val input = List(random.nextInt(1, 50)) { random.nextInt(-100, 101) }
            val sorted = input.sorted()
            val values = DataColumn.createValueColumn("values", input, typeOf<Int>())
            for (p in listOf(0.0, 0.01, 0.1, 0.25, 0.5, 0.625, 0.75, 0.875, 0.99, 1.0)) {
                val r1 = kotlin.math.ceil(input.size * p).toInt().coerceIn(1, input.size) - 1
                val r3 = kotlin.math.round(input.size * p).toInt().coerceIn(1, input.size) - 1

                values.quantile(p, QuantileMethod.R1) shouldBe sorted[r1]
                values.quantile(p, QuantileMethod.R3) shouldBe sorted[r3]
            }
        }
    }
}
