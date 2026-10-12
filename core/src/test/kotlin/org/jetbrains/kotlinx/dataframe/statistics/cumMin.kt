package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.cumMin
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.math.BigDecimal
import kotlin.reflect.typeOf

class CumMinTests {

    @Test
    fun `non-null column preserves type and name`() {
        val prices by columnOf(8, 5, 7, 2, 2, 4)
        val result: DataColumn<Int> = prices.cumMin()

        result.toList() shouldBe listOf(8, 5, 5, 2, 2, 2)
        result.name() shouldBe prices.name()
        result.type() shouldBe typeOf<Int>()
        prices.toList() shouldBe listOf(8, 5, 7, 2, 2, 4)
    }

    @Test
    fun `nullable column skips gaps without filling them`() {
        val result: DataColumn<Int?> = columnOf(null, 8, null, null, 5, 7, null).cumMin()

        result.toList() shouldBe listOf(null, 8, null, null, 5, 5, null)
        result.type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `non-skipping null propagates through subsequent values`() {
        columnOf(8, 5, null, 2, 1).cumMin(skipNA = false).toList() shouldBe
            listOf(8, 5, null, null, null)
        columnOf<Int?>(null, 5, 2).cumMin(skipNA = false).toList() shouldBe listOf(null, null, null)
    }

    @Test
    fun `empty and all-null columns preserve declared type`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())
        val emptyNullable = DataColumn.createValueColumn<Int?>("empty", emptyList(), typeOf<Int?>())

        empty.cumMin().toList() shouldBe emptyList()
        empty.cumMin().type() shouldBe typeOf<Int>()
        emptyNullable.cumMin().type() shouldBe typeOf<Int?>()
        columnOf<Int?>(null, null).cumMin().toList() shouldBe listOf(null, null)
    }

    @Test
    fun `nullable declaration is retained when result has no nulls`() {
        val nullable = DataColumn.createValueColumn("values", listOf(5, 3), typeOf<Int?>())

        nullable.cumMin().type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `double NaN is retained but does not replace the minimum when skipping`() {
        val result = columnOf(Double.NaN, 8.0, Double.NaN, 5.0, 7.0).cumMin().toList()

        result[0].isNaN() shouldBe true
        result[1] shouldBe 8.0
        result[2].isNaN() shouldBe true
        result.drop(3) shouldBe listOf(5.0, 5.0)
    }

    @Test
    fun `double NaN propagates without skipping`() {
        val result = columnOf(8.0, Double.NaN, 5.0, 2.0).cumMin(skipNA = false).toList()

        result.first() shouldBe 8.0
        result.drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `nullable doubles retain null and NaN separately`() {
        val result: DataColumn<Double?> = columnOf(8.0, null, Double.NaN, 5.0).cumMin()
        val values = result.toList()

        values[0] shouldBe 8.0
        values[1] shouldBe null
        values[2]!!.isNaN() shouldBe true
        values[3] shouldBe 5.0
        result.type() shouldBe typeOf<Double?>()
        columnOf(8.0, null, Double.NaN, 5.0).cumMin(skipNA = false).toList() shouldBe
            listOf(8.0, null, null, null)
    }

    @Test
    fun `float NaN can be skipped or propagated`() {
        val column = columnOf(8.0f, Float.NaN, 5.0f, 7.0f)

        column.cumMin().toList().drop(2) shouldBe listOf(5.0f, 5.0f)
        column.cumMin(skipNA = false).toList().drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `primitive comparable types are not promoted`() {
        columnOf<Byte>(8, 5, 7).cumMin().type() shouldBe typeOf<Byte>()
        columnOf<Short>(8, 5, 7).cumMin().type() shouldBe typeOf<Short>()
        columnOf(8L, 5L, 7L).cumMin().toList() shouldBe listOf(8L, 5L, 5L)
        columnOf('c', 'a', 'b').cumMin().toList() shouldBe listOf('c', 'a', 'a')
    }

    @Test
    fun `strings and exact decimal comparisons work without conversion`() {
        columnOf("pear", "apple", "orange").cumMin().toList() shouldBe listOf("pear", "apple", "apple")
        val values = listOf(BigDecimal("1.00000000000000000002"), BigDecimal("1.00000000000000000001"))
        val column = DataColumn.createValueColumn("amount", values, typeOf<BigDecimal>())

        column.cumMin().toList() shouldBe values
        column.cumMin().type() shouldBe typeOf<BigDecimal>()
    }

    @Test
    fun `equal values retain the first object`() {
        val first = Score(2, "first")
        val tied = Score(2, "tied")

        columnOf(first, tied).cumMin().toList() shouldBe listOf(first, first)
    }

    @Test
    fun `infinities follow comparable ordering`() {
        columnOf(Double.POSITIVE_INFINITY, 5.0, Double.NEGATIVE_INFINITY, 3.0).cumMin().toList() shouldBe
            listOf(Double.POSITIVE_INFINITY, 5.0, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
    }

    @Test
    fun `default value metadata is preserved`() {
        val column = DataColumn.createValueColumn("values", listOf(5, 3), typeOf<Int>(), defaultValue = 42)

        column.cumMin().defaultValue() shouldBe 42
    }

    private data class Score(val value: Int, val label: String) : Comparable<Score> {
        override fun compareTo(other: Score): Int = value.compareTo(other.value)
    }
}
