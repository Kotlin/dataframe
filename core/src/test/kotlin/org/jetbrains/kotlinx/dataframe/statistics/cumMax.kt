package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumMax
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random
import kotlin.reflect.typeOf

class CumMaxTests {

    @Test
    fun `non-null column preserves type name size and input`() {
        val prices by columnOf(2, 5, 3, 8, 8, 4)
        val result: DataColumn<Int> = prices.cumMax()

        result.toList() shouldBe listOf(2, 5, 5, 8, 8, 8)
        result.name() shouldBe prices.name()
        result.size() shouldBe prices.size()
        result.type() shouldBe typeOf<Int>()
        prices.toList() shouldBe listOf(2, 5, 3, 8, 8, 4)
    }

    @Test
    fun `nullable column skips leading interior and trailing gaps without filling`() {
        val result: DataColumn<Int?> = columnOf(null, 2, null, null, 5, 3, null).cumMax()

        result.toList() shouldBe listOf(null, 2, null, null, 5, 5, null)
        result.type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `null propagates without skipping`() {
        columnOf(2, 5, null, 8, 9).cumMax(skipNA = false).toList() shouldBe listOf(2, 5, null, null, null)
        columnOf<Int?>(null, 5, 8).cumMax(skipNA = false).toList() shouldBe listOf(null, null, null)
    }

    @Test
    fun `empty columns preserve declared type and metadata in either mode`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>(), defaultValue = 42)
        val emptyNullable = DataColumn.createValueColumn<Int?>("empty", emptyList(), typeOf<Int?>())

        for (skipNA in listOf(true, false)) {
            empty.cumMax(skipNA).toList() shouldBe emptyList()
            empty.cumMax(skipNA).type() shouldBe typeOf<Int>()
            empty.cumMax(skipNA).defaultValue() shouldBe 42
            emptyNullable.cumMax(skipNA).type() shouldBe typeOf<Int?>()
        }
    }

    @Test
    fun `all-null columns remain all-null in either mode`() {
        val column = columnOf<Int?>(null, null)

        column.cumMax().toList() shouldBe listOf(null, null)
        column.cumMax(skipNA = false).toList() shouldBe listOf(null, null)
    }

    @Test
    fun `nullable declaration is retained when there are no null values`() {
        val column = DataColumn.createValueColumn("values", listOf(2, 5), typeOf<Int?>())

        column.cumMax().type() shouldBe typeOf<Int?>()
        column.cumMax(skipNA = false).type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `double NaN is retained without replacing maximum when skipping`() {
        val result = columnOf(Double.NaN, 2.0, Double.NaN, 5.0, 3.0).cumMax().toList()

        result[0].isNaN() shouldBe true
        result[1] shouldBe 2.0
        result[2].isNaN() shouldBe true
        result.drop(3) shouldBe listOf(5.0, 5.0)
    }

    @Test
    fun `double NaN propagates without skipping`() {
        val result = columnOf(2.0, Double.NaN, 5.0, 8.0).cumMax(skipNA = false).toList()

        result.first() shouldBe 2.0
        result.drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `initial and all-NaN columns stay NaN without skipping`() {
        columnOf(Double.NaN, 2.0, 5.0).cumMax(skipNA = false).toList().all { it.isNaN() } shouldBe true
        columnOf(Double.NaN, Double.NaN).cumMax().toList().all { it.isNaN() } shouldBe true
    }

    @Test
    fun `nullable doubles retain null and NaN separately when skipping`() {
        val result: DataColumn<Double?> = columnOf(2.0, null, Double.NaN, 5.0).cumMax()
        val values = result.toList()

        values[0] shouldBe 2.0
        values[1] shouldBe null
        values[2]!!.isNaN() shouldBe true
        values[3] shouldBe 5.0
        result.type() shouldBe typeOf<Double?>()
    }

    @Test
    fun `first null takes precedence over later NaN when propagating`() {
        columnOf(2.0, null, Double.NaN, 5.0).cumMax(skipNA = false).toList() shouldBe
            listOf(2.0, null, null, null)
    }

    @Test
    fun `first NaN takes precedence over later null when propagating`() {
        val result = columnOf(2.0, Double.NaN, null, 5.0).cumMax(skipNA = false).toList()

        result.first() shouldBe 2.0
        result.drop(1).all { it?.isNaN() == true } shouldBe true
    }

    @Test
    fun `float NaN can be skipped or propagated`() {
        val column = columnOf(2.0f, Float.NaN, 5.0f, 3.0f)

        column.cumMax().toList().drop(2) shouldBe listOf(5.0f, 5.0f)
        column.cumMax(skipNA = false).toList().drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `primitive comparable types are preserved without promotion`() {
        columnOf<Byte>(2, 5, 3).cumMax().type() shouldBe typeOf<Byte>()
        columnOf<Short>(2, 5, 3).cumMax().type() shouldBe typeOf<Short>()
        columnOf(2L, 5L, 3L).cumMax().toList() shouldBe listOf(2L, 5L, 5L)
        columnOf('a', 'c', 'b').cumMax().toList() shouldBe listOf('a', 'c', 'c')
    }

    @Test
    fun `strings use natural ordering`() {
        columnOf("apple", "pear", "orange").cumMax().toList() shouldBe listOf("apple", "pear", "pear")
    }

    @Test
    fun `decimal comparisons do not lose precision through floating conversion`() {
        val values = listOf(BigDecimal("1.00000000000000000001"), BigDecimal("1.00000000000000000002"))
        val column = DataColumn.createValueColumn("amount", values, typeOf<BigDecimal>())

        column.cumMax().toList() shouldBe values
        column.cumMax().type() shouldBe typeOf<BigDecimal>()
    }

    @Test
    fun `equal values retain the first object`() {
        val first = Score(2, "first")
        val tied = Score(2, "tied")

        columnOf(first, tied).cumMax().toList() shouldBe listOf(first, first)
    }

    @Test
    fun `infinities follow natural ordering`() {
        columnOf(Double.NEGATIVE_INFINITY, 5.0, Double.POSITIVE_INFINITY, 3.0).cumMax().toList() shouldBe
            listOf(Double.NEGATIVE_INFINITY, 5.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    }

    @Test
    fun `default value metadata is preserved`() {
        val column = DataColumn.createValueColumn("values", listOf(2, 5), typeOf<Int>(), defaultValue = 42)

        column.cumMax().defaultValue() shouldBe 42
    }

    @Test
    fun `dates use their natural ordering`() {
        val first = LocalDate.of(2026, 10, 12)
        val later = first.plusDays(1)

        columnOf(first, later, first.minusDays(1)).cumMax().toList() shouldBe listOf(first, later, later)
    }

    @Test
    fun `signed zeros follow Comparable ordering`() {
        val result = columnOf(-0.0, 0.0, -0.0).cumMax().toList()

        result.map { it.toBits() } shouldBe listOf((-0.0).toBits(), 0.0.toBits(), 0.0.toBits())
    }

    @Test
    fun `dataframe conversion preserves unselected columns and input`() {
        val values by column<Int?>()
        val df = dataFrameOf("label", "values")("a", 2, "b", null, "c", 5, "d", 3)
        val result = df.convert { values }.asColumn { it.cumMax() }

        result["values"].toList() shouldBe listOf(2, null, 5, 5)
        result["label"] shouldBe df["label"]
        df["values"].toList() shouldBe listOf(2, null, 5, 3)
    }

    @Test
    fun `group transformations reset maxima and missing propagation`() {
        val values by column<Int?>()
        val df = dataFrameOf("group", "values")("a", 2, "b", 7, "a", null, "b", 4, "a", 5)
        val groups = df.groupBy("group")

        val skipping = groups.updateGroups { convert { values }.asColumn { it.cumMax() } }.concat()
        val propagating = groups.updateGroups { convert { values }.asColumn { it.cumMax(skipNA = false) } }.concat()

        skipping["values"].toList() shouldBe listOf(2, null, 5, 7, 7)
        propagating["values"].toList() shouldBe listOf(2, null, null, 7, 7)
    }

    @Test
    fun `seeded nullable inputs match independent prefix maxima`() {
        val random = Random(2145)
        repeat(100) {
            val input = List(random.nextInt(0, 100)) { if (random.nextInt(4) == 0) null else random.nextInt(-100, 101) }
            val column = DataColumn.createValueColumn("values", input, typeOf<Int?>())
            val skipping = input.indices.map { i ->
                if (input[i] == null) null else input.take(i + 1).filterNotNull().maxOrNull()
            }
            val firstNull = input.indexOf(null).let { if (it < 0) input.size else it }
            val propagating = input.indices.map { i ->
                if (i >= firstNull) null else input.take(i + 1).filterNotNull().maxOrNull()
            }

            column.cumMax().toList() shouldBe skipping
            column.cumMax(skipNA = false).toList() shouldBe propagating
        }
    }

    private data class Score(val value: Int, val label: String) : Comparable<Score> {
        override fun compareTo(other: Score): Int = value.compareTo(other.value)
    }
}
