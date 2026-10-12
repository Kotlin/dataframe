package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.RankMethod
import org.jetbrains.kotlinx.dataframe.api.RankNAOption
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.rank
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random
import kotlin.reflect.typeOf

class RankTests {

    @Test
    fun `average ranks preserve row order name size and input`() {
        val values by columnOf(20, 10, 20, 30)
        val result: DataColumn<Double?> = values.rank()

        result.toList() shouldBe listOf(2.5, 1.0, 2.5, 4.0)
        result.name() shouldBe values.name()
        result.size() shouldBe values.size()
        result.type() shouldBe typeOf<Double?>()
        values.toList() shouldBe listOf(20, 10, 20, 30)
    }

    @Test
    fun `minimum ranks use first position in each tie`() {
        columnOf(20, 10, 20, 30).rank(RankMethod.Min).toList() shouldBe listOf(2.0, 1.0, 2.0, 4.0)
    }

    @Test
    fun `maximum ranks use last position in each tie`() {
        columnOf(20, 10, 20, 30).rank(RankMethod.Max).toList() shouldBe listOf(3.0, 1.0, 3.0, 4.0)
    }

    @Test
    fun `first ranks break ties by original row order`() {
        columnOf(20, 10, 20, 30).rank(RankMethod.First).toList() shouldBe listOf(2.0, 1.0, 3.0, 4.0)
    }

    @Test
    fun `dense ranks leave no gaps between comparison groups`() {
        columnOf(20, 10, 20, 30).rank(RankMethod.Dense).toList() shouldBe listOf(2.0, 1.0, 2.0, 3.0)
    }

    @Test
    fun `descending ranks handle every tie method`() {
        val values = columnOf(20, 10, 20, 30)
        val expected = mapOf(
            RankMethod.Average to listOf(2.5, 4.0, 2.5, 1.0),
            RankMethod.Min to listOf(2.0, 4.0, 2.0, 1.0),
            RankMethod.Max to listOf(3.0, 4.0, 3.0, 1.0),
            RankMethod.First to listOf(2.0, 4.0, 3.0, 1.0),
            RankMethod.Dense to listOf(2.0, 3.0, 2.0, 1.0),
        )

        for ((method, ranks) in expected) values.rank(method, ascending = false).toList() shouldBe ranks
    }

    @Test
    fun `keep excludes nulls and does not fill missing cells`() {
        columnOf(20, null, 10, null, 20).rank().toList() shouldBe listOf(2.5, null, 1.0, null, 2.5)
    }

    @Test
    fun `keep converts double NaN to null rank`() {
        columnOf(20.0, Double.NaN, 10.0, Double.NaN, 20.0).rank().toList() shouldBe
            listOf(2.5, null, 1.0, null, 2.5)
    }

    @Test
    fun `keep converts float NaN to null rank`() {
        columnOf(20.0f, Float.NaN, 10.0f).rank().toList() shouldBe listOf(2.0, null, 1.0)
    }

    @Test
    fun `top merges null and NaN into one missing group`() {
        columnOf(20.0, null, 10.0, Double.NaN, 20.0).rank(naOption = RankNAOption.Top).toList() shouldBe
            listOf(4.5, 1.5, 3.0, 1.5, 4.5)
    }

    @Test
    fun `bottom merges null and NaN into one missing group`() {
        columnOf(20.0, null, 10.0, Double.NaN, 20.0).rank(naOption = RankNAOption.Bottom).toList() shouldBe
            listOf(2.5, 4.5, 1.0, 4.5, 2.5)
    }

    @Test
    fun `top and bottom are independent of descending order`() {
        val values = columnOf(20.0, null, 10.0, Double.NaN, 20.0)

        values.rank(ascending = false, naOption = RankNAOption.Top).toList() shouldBe
            listOf(3.5, 1.5, 5.0, 1.5, 3.5)
        values.rank(ascending = false, naOption = RankNAOption.Bottom).toList() shouldBe
            listOf(1.5, 4.5, 3.0, 4.5, 1.5)
    }

    @Test
    fun `first preserves row order within missing group when descending`() {
        val values = columnOf(20.0, null, 10.0, Double.NaN, 20.0)

        values.rank(RankMethod.First, ascending = false, naOption = RankNAOption.Top).toList() shouldBe
            listOf(3.0, 1.0, 5.0, 2.0, 4.0)
    }

    @Test
    fun `empty input remains empty with stable nullable Double schema`() {
        val empty = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())

        for (method in RankMethod.entries) {
            for (option in RankNAOption.entries) {
                val result = empty.rank(method, naOption = option, pct = true)
                result.toList() shouldBe emptyList()
                result.type() shouldBe typeOf<Double?>()
                result.name() shouldBe "empty"
            }
        }
    }

    @Test
    fun `all-missing input stays null with keep including normalized ranks`() {
        val values = columnOf(null, Double.NaN, null)

        for (method in RankMethod.entries) values.rank(method, pct = true).toList() shouldBe listOf(null, null, null)
    }

    @Test
    fun `all-missing included values follow each tie method`() {
        val values = columnOf(null, Double.NaN, null)
        val expected = mapOf(
            RankMethod.Average to listOf(2.0, 2.0, 2.0),
            RankMethod.Min to listOf(1.0, 1.0, 1.0),
            RankMethod.Max to listOf(3.0, 3.0, 3.0),
            RankMethod.First to listOf(1.0, 2.0, 3.0),
            RankMethod.Dense to listOf(1.0, 1.0, 1.0),
        )

        for (option in listOf(RankNAOption.Top, RankNAOption.Bottom)) {
            for ((method, ranks) in expected) values.rank(method, naOption = option).toList() shouldBe ranks
        }
    }

    @Test
    fun `singleton gets rank one or remains missing`() {
        columnOf(42).rank(pct = true).toList() shouldBe listOf(1.0)
        columnOf<Int?>(null).rank(pct = true).toList() shouldBe listOf(null)
        columnOf<Int?>(null).rank(naOption = RankNAOption.Top, pct = true).toList() shouldBe listOf(1.0)
    }

    @Test
    fun `normalized ranks exclude kept missing cells from denominator`() {
        val values = columnOf(20, null, 10, 20, 30)

        values.rank(pct = true).toList() shouldBe listOf(0.625, null, 0.25, 0.625, 1.0)
        values.rank(RankMethod.Max, pct = true).toList() shouldBe listOf(0.75, null, 0.25, 0.75, 1.0)
        values.rank(RankMethod.First, pct = true).toList() shouldBe listOf(0.5, null, 0.25, 0.75, 1.0)
    }

    @Test
    fun `dense normalization divides by distinct ranked groups`() {
        columnOf(20, null, 10, 20, 30).rank(RankMethod.Dense, pct = true).toList() shouldBe
            listOf(2.0 / 3, null, 1.0 / 3, 2.0 / 3, 1.0)
        columnOf(20, null, 10, 20).rank(RankMethod.Dense, naOption = RankNAOption.Top, pct = true).toList() shouldBe
            listOf(1.0, 1.0 / 3, 2.0 / 3, 1.0)
    }

    @Test
    fun `normalization includes top and bottom missing cells`() {
        columnOf(20, null, 10, 20).rank(naOption = RankNAOption.Bottom, pct = true).toList() shouldBe
            listOf(0.625, 1.0, 0.25, 0.625)
        columnOf<Int?>(null, null).rank(naOption = RankNAOption.Top, pct = true).toList() shouldBe listOf(0.75, 0.75)
    }

    @Test
    fun `long comparison does not lose precision through Double conversion`() {
        columnOf(9007199254740993L, 9007199254740992L).rank().toList() shouldBe listOf(2.0, 1.0)
    }

    @Test
    fun `decimal ties use compareTo rather than equals`() {
        val column = columnOf(BigDecimal("2.00"), BigDecimal("1.0"), BigDecimal("2.0"))

        column.rank().toList() shouldBe listOf(2.5, 1.0, 2.5)
    }

    @Test
    fun `exact decimal comparisons keep distinct values distinct`() {
        columnOf(BigDecimal("1.00000000000000000002"), BigDecimal("1.00000000000000000001"))
            .rank().toList() shouldBe listOf(2.0, 1.0)
    }

    @Test
    fun `strings and dates use natural ordering`() {
        columnOf("pear", "apple", "pear").rank().toList() shouldBe listOf(2.5, 1.0, 2.5)
        val first = LocalDate.of(2026, 10, 12)

        columnOf(first, first.minusDays(1), first.plusDays(1)).rank().toList() shouldBe listOf(2.0, 1.0, 3.0)
    }

    @Test
    fun `signed zeros follow Comparable ordering instead of numerical equality`() {
        columnOf(0.0, -0.0, 0.0).rank().toList() shouldBe listOf(2.5, 1.0, 2.5)
        columnOf(0.0f, -0.0f, 0.0f).rank().toList() shouldBe listOf(2.5, 1.0, 2.5)
    }

    @Test
    fun `infinities are ranked while NaN is excluded`() {
        columnOf(Double.POSITIVE_INFINITY, Double.NaN, Double.NEGATIVE_INFINITY, 5.0).rank().toList() shouldBe
            listOf(3.0, null, 1.0, 2.0)
    }

    @Test
    fun `descending comparator handles Int MIN VALUE return without overflow`() {
        columnOf(ExtremeOrder(1), ExtremeOrder(3), ExtremeOrder(2)).rank(ascending = false).toList() shouldBe
            listOf(3.0, 1.0, 2.0)
    }

    @Test
    fun `default metadata is cleared when converting values to ranks`() {
        val values = DataColumn.createValueColumn("values", listOf("b", "a"), typeOf<String>(), defaultValue = "z")

        values.rank().defaultValue() shouldBe null
    }

    @Test
    fun `dataframe conversion changes selected column to Double ranks and keeps other columns`() {
        val values by column<Int?>()
        val df = dataFrameOf("label", "values")("a", 20, "b", null, "c", 10, "d", 20)
        val result = df.convert { values }.asColumn { it.rank() }

        result["values"].toList() shouldBe listOf(2.5, null, 1.0, 2.5)
        result["values"].type() shouldBe typeOf<Double?>()
        result["label"] shouldBe df["label"]
        df["values"].toList() shouldBe listOf(20, null, 10, 20)
    }

    @Test
    fun `group transformations rank and normalize within each group`() {
        val values by column<Int?>()
        val df = dataFrameOf("group", "values")("a", 20, "b", 7, "a", null, "b", 4, "a", 10, "a", 20)
        val result = df.groupBy("group").updateGroups { convert { values }.asColumn { it.rank(pct = true) } }.concat()

        result["values"].toList() shouldBe listOf(2.5 / 3, null, 1.0 / 3, 2.5 / 3, 1.0, 0.5)
    }

    @Test
    fun `seeded arrays match independent pairwise rank reference across all options`() {
        val random = Random(2149)
        repeat(50) {
            val input = List(random.nextInt(0, 25)) { if (random.nextInt(4) == 0) null else random.nextInt(-3, 4) }
            val values = DataColumn.createValueColumn("values", input, typeOf<Int?>())
            for (method in RankMethod.entries) {
                for (option in RankNAOption.entries) {
                    for (ascending in listOf(true, false)) {
                        for (pct in listOf(true, false)) {
                            values.rank(method, ascending, option, pct).toList() shouldBe
                                pairwiseReference(input, method, ascending, option, pct)
                        }
                    }
                }
            }
        }
    }

    private fun pairwiseReference(
        input: List<Int?>,
        method: RankMethod,
        ascending: Boolean,
        option: RankNAOption,
        pct: Boolean,
    ): List<Double?> {
        val included = input.indices.filter { option != RankNAOption.Keep || input[it] != null }
        val distinct = included.map { input[it] }.distinct()

        fun precedes(a: Int?, b: Int?): Boolean =
            when {
                a == null && b == null -> false
                a == null -> option == RankNAOption.Top
                b == null -> option == RankNAOption.Bottom
                ascending -> a < b
                else -> a > b
            }

        return input.indices.map { index ->
            if (index !in included) {
                null
            } else {
                val before = included.count { precedes(input[it], input[index]) }
                val ties = included.count { input[it] == input[index] }
                val rank = when (method) {
                    RankMethod.Min -> before + 1.0
                    RankMethod.Max -> before + ties.toDouble()
                    RankMethod.Average -> before + (ties + 1.0) / 2.0
                    RankMethod.First -> before + included.count { it <= index && input[it] == input[index] }.toDouble()
                    RankMethod.Dense -> distinct.count { precedes(it, input[index]) } + 1.0
                }
                val denominator = if (method == RankMethod.Dense) distinct.size else included.size
                if (pct) rank / denominator else rank
            }
        }
    }

    private data class ExtremeOrder(val value: Int) : Comparable<ExtremeOrder> {
        override fun compareTo(other: ExtremeOrder): Int =
            when {
                value < other.value -> Int.MIN_VALUE
                value > other.value -> Int.MAX_VALUE
                else -> 0
            }
    }
}
