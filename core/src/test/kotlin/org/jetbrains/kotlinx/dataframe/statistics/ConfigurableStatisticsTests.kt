package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.medianByOrNull
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.medianOfOrNull
import org.jetbrains.kotlinx.dataframe.api.medianOrNull
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.percentileByOrNull
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.api.percentileOfOrNull
import org.jetbrains.kotlinx.dataframe.api.percentileOrNull
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.rowMedianOf
import org.jetbrains.kotlinx.dataframe.api.rowMedianOfOrNull
import org.jetbrains.kotlinx.dataframe.api.rowPercentileOf
import org.jetbrains.kotlinx.dataframe.api.rowPercentileOfOrNull
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.junit.Test
import java.math.BigDecimal
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.random.Random
import kotlin.reflect.typeOf

class ConfigurableStatisticsTests {

    private val r1 = QuantileEstimationMethod.R1
    private val r3 = QuantileEstimationMethod.R3
    private val r7 = QuantileEstimationMethod.R7
    private val r8 = QuantileEstimationMethod.R8

    @Test
    fun `column selecting methods preserve their compile time types`() {
        val ints = columnOf(4, 1, 3, 2)
        val first: Int = ints.percentile(25.0, r1)
        val nearest: Int = ints.median(r3)
        first shouldBe 1
        nearest shouldBe 2
        val exact = 9_007_199_254_740_993L
        val selected: Long = columnOf(exact, exact + 2).median(r1)
        selected shouldBe exact
        val singleton: Int = columnOf(7).median(r1)
        singleton shouldBe 7
    }

    @Test
    fun `column interpolation supports the existing estimators`() {
        val col = columnOf(4, 1, 3, 2)
        val interpolated: Double = col.percentile(25.0, r7)
        interpolated shouldBe 1.75
        col.percentile(25.0, r8) shouldBe col.percentile(25.0)
        col.median(r7) shouldBe 2.5
        columnOf(7).median(r8) shouldBe 7.0
    }

    @Test
    fun `self comparables and precise decimals are selected without conversion`() {
        columnOf("e", "a", "c", "b", "d").median(r1) shouldBe "c"
        columnOf("e", "a", "c", "b", "d").median(r3) shouldBe "b"
        val exact = BigDecimal("9007199254740993.01")
        columnOf(exact, exact + BigDecimal.ONE).median(r1) shouldBe exact
    }

    @Test
    fun `null NaN and empty column behavior is consistent`() {
        columnOf(1, null, 4, 2, 3).percentile(25.0, r7) shouldBe 1.75
        columnOf(Float.NaN, 2f).median(r1).isNaN() shouldBe true
        columnOf(Float.NaN, 2f).median(r1, skipNaN = true) shouldBe 2f
        columnOf(Double.NaN, 2.0).median(r7).shouldBeNaN()
        columnOf(Double.NaN).medianOrNull(r7, skipNaN = true) shouldBe null
        columnOf(values = emptyArray<Int>()).percentileOrNull(25.0, r1) shouldBe null
        shouldThrow<NoSuchElementException> { columnOf(values = emptyArray<Int>()).median(r1) }
        for (p in listOf(-1.0, 101.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            shouldThrow<IllegalArgumentException> { columnOf(values = emptyArray<Int>()).percentileOrNull(p, r1) }
        }
    }

    @Test
    fun `column expression overloads use the selected estimator`() {
        val col = columnOf("dddd", "a", "ccc", "bb")
        val selected: Int = col.percentileOf(25.0, r1) { it.length }
        selected shouldBe 1
        col.percentileOf(25.0, r7) { it.length } shouldBe 1.75
        col.medianOf(r1) { it.length } shouldBe 2
        col.medianOf(r7) { it.length } shouldBe 2.5
        col.percentileBy(50.0, r1) { it.length } shouldBe "bb"
        col.medianBy(r3) { it.length } shouldBe "bb"
    }

    @Test
    fun `by operations retain original indices after null and NaN filtering`() {
        val col = columnOf<Double?>(null, 9.0, Double.NaN, 1.0, 4.0, 2.0)
        col.percentileBy(50.0, r1, skipNaN = true) { it } shouldBe 2.0
        col.medianBy(r3, skipNaN = true) { it } shouldBe 2.0
        col.percentileBy(50.0, r1) { it }.isNaN() shouldBe true
        col.medianBy(r1) { it }.isNaN() shouldBe true
        columnOf<Double?>(null, Double.NaN).medianByOrNull(r1, skipNaN = true) { it } shouldBe null
        columnOf<Double?>(null, Double.NaN).percentileByOrNull(50.0, r1, skipNaN = true) { it } shouldBe null
    }

    @Test
    fun `dataframe for all selector and string modes retain result schemas`() {
        val df = dataFrameOf("x", "label")(4, "d", 1, "a", 3, "c", 2, "b")
        df.percentile(25.0, r1)["x"] shouldBe 1
        df.percentile(25.0, r1)["label"] shouldBe "a"
        df.percentileFor(25.0, r7, "x")["x"] shouldBe 1.75
        df.percentileFor(25.0, r1) { "x"<Int>() }["x"] shouldBe 1
        df.percentile(25.0, r7, "x") shouldBe 1.75
        val selected: Int = df.percentile(25.0, r1) { "x"<Int>() }
        selected shouldBe 1
        df.percentile(25.0, r7) { "x"<Int>() } shouldBe 1.75
        df.median(r1)["x"] shouldBe 2
        df.medianFor(r7, "x")["x"] shouldBe 2.5
        df.median(r1, "x") shouldBe 2
        df.median(r7) { "x"<Int>() } shouldBe 2.5
        shouldThrow<IllegalArgumentException> { df.median(r7) }
    }

    @Test
    fun `dataframe row and expression overloads use selected types`() {
        val df = dataFrameOf("x", "y")(1, 4, 3, 2)
        val selected: Int = df[0].rowPercentileOf<Int>(25.0, r1)
        selected shouldBe 1
        df[0].rowPercentileOf<Int>(25.0, r7) shouldBe 1.75
        df[0].rowMedianOf<Int>(r1) shouldBe 1
        df[0].rowMedianOf<Int>(r7) shouldBe 2.5
        df.percentileOf(25.0, r1) { "x"<Int>() * 2 } shouldBe 2
        df.percentileOf(25.0, r7) { "x"<Int>() * 2 } shouldBe 3.0
        df.medianOf(r1) { "x"<Int>() * 2 } shouldBe 2
        df.medianOf(r7) { "x"<Int>() * 2 } shouldBe 4.0
    }

    @Test
    fun `grouped aggregation forwards method names and output types`() {
        val df = dataFrameOf("g", "x")("a", 4, "a", 1, "a", 3, "a", 2)
        val groups = df.groupBy("g")
        val selected = groups.percentileFor(25.0, r1, "x")
        selected["x"].type() shouldBe typeOf<Int>()
        selected[0]["x"] shouldBe 1
        groups.percentile(25.0, r7, "x", name = "q")[0]["q"] shouldBe 1.75
        groups.percentileOf(25.0, r7, name = "q") { "x"<Int>() }[0]["q"] shouldBe 1.75
        groups.medianFor(r1, "x")[0]["x"] shouldBe 2
        groups.median(r7, "x", name = "m")[0]["m"] shouldBe 2.5
        groups.medianOf(r1, name = "m") { "x"<Int>() }[0]["m"] shouldBe 2
        groups.percentileBy(50.0, r1, "x").concat()[0]["x"] shouldBe 2
        groups.medianBy(r1) { "x"<Int>() }.concat()[0]["x"] shouldBe 2
    }

    @Test
    fun `pivot and pivot group by forward methods through delegate and reduce`() {
        val df = dataFrameOf("g", "p", "x")("a", "v", 4, "a", "v", 1, "a", "v", 3, "a", "v", 2)
        df.pivot("p").percentileFor(25.0, r7, "x")["v"] shouldBe 1.75
        val pivot = df.groupBy("g").pivot("p")
        val selected = pivot.percentileFor(25.0, r1, "x")
        selected.getColumnGroup("p")["v"][0] shouldBe 1
        pivot.medianFor(r7, "x").getColumnGroup("p")["v"][0] shouldBe 2.5
    }

    @Test
    fun `pivot scalar expression and by modes use explicit methods`() {
        val df = dataFrameOf("g", "p", "x")("a", "v", 4, "a", "v", 1, "a", "v", 3, "a", "v", 2)
        val pivot = df.pivot("p")
        pivot.percentile(25.0, r7, "x")["v"] shouldBe 1.75
        pivot.median(r1, "x")["v"] shouldBe 2
        pivot.percentileOf(25.0, r7) { "x"<Int>() }["v"] shouldBe 1.75
        pivot.medianOf(r1) { "x"<Int>() }["v"] shouldBe 2
        pivot.percentileBy(50.0, r1, "x").with { "x"<Int>() }["v"] shouldBe 2
        pivot.medianBy(r3) { "x"<Int>() }.with { "x"<Int>() }["v"] shouldBe 2
        val groupedPivot = df.groupBy("g").pivot("p")
        groupedPivot.percentile(25.0, r7, "x").getColumnGroup("p")["v"][0] shouldBe 1.75
        groupedPivot.median(r1, "x").getColumnGroup("p")["v"][0] shouldBe 2
        groupedPivot.percentileOf(25.0, r7) { "x"<Int>() }.getColumnGroup("p")["v"][0] shouldBe 1.75
        groupedPivot.medianOf(r1) { "x"<Int>() }.getColumnGroup("p")["v"][0] shouldBe 2
        groupedPivot.percentileBy(50.0, r1) { "x"<Int>() }.with { "x"<Int>() }
            .getColumnGroup("p")["v"][0] shouldBe 2
        groupedPivot.medianBy(r3, "x").with { "x"<Int>() }.getColumnGroup("p")["v"][0] shouldBe 2
    }

    @Test
    fun `separate pivot results match existing schemas for matching estimators`() {
        val df = dataFrameOf("g", "p", "x", "y")(
            "a",
            "v",
            1,
            10,
            "a",
            "v",
            2,
            20,
            "a",
            "v",
            3,
            30,
        )
        val pivot = df.pivot("p")
        pivot.percentileFor(25.0, r8, "x", "y", separate = true) shouldBe
            pivot.percentileFor(25.0, "x", "y", separate = true)
        pivot.medianFor(r8, separate = true) { "x"<Int>() and "y"<Int>() } shouldBe
            pivot.medianFor(separate = true) { "x"<Int>() and "y"<Int>() }
        val grouped = df.groupBy("g").pivot("p")
        grouped.percentileFor(25.0, r8, "x", "y", separate = true) shouldBe
            grouped.percentileFor(25.0, "x", "y", separate = true)
        grouped.medianFor(r8, "x", "y", separate = true) shouldBe
            grouped.medianFor("x", "y", separate = true)
    }

    @Test
    fun `existing by defaults correctly propagate and skip NaN`() {
        val col = columnOf<Double?>(null, 9.0, Double.NaN, 1.0, 4.0, 2.0)
        col.percentileBy(50.0, skipNaN = true) { it } shouldBe 2.0
        col.medianBy(skipNaN = true) { it } shouldBe 2.0
        col.percentileBy(50.0) { it }.isNaN() shouldBe true
        col.medianBy { it }.isNaN() shouldBe true
        val df = dataFrameOf("id", "x")(0, null, 1, 9.0, 2, Double.NaN, 3, 1.0, 4, 4.0, 5, 2.0)
        df.percentileBy(50.0, r1, "x", skipNaN = true)["id"] shouldBe 5
        df.percentileBy(50.0, "x", skipNaN = true)["id"] shouldBe 5
        df.medianBy("x", skipNaN = true)["id"] shouldBe 5
        df.percentileBy(50.0, "x")["id"] shouldBe 2
        df.medianBy("x")["id"] shouldBe 2
        df.medianBy(r1, "x")["id"] shouldBe 2
    }

    @Test
    fun `nullable scalar and expression variants retain empty results`() {
        val empty = columnOf(values = emptyArray<Int>())
        empty.percentileOrNull(50.0, r7) shouldBe null
        empty.medianOrNull(r1) shouldBe null
        empty.medianOfOrNull(r1) { it } shouldBe null
        empty.medianOfOrNull(r7) { it } shouldBe null
        empty.percentileOfOrNull(50.0, r1) { it } shouldBe null
        empty.percentileOfOrNull(50.0, r7) { it } shouldBe null
        val df = dataFrameOf("x", "y")(null, null)
        df[0].rowMedianOfOrNull<Int>(r1) shouldBe null
        df[0].rowMedianOfOrNull<Int>(r7) shouldBe null
        df[0].rowPercentileOfOrNull<Int>(25.0, r1) shouldBe null
        df[0].rowPercentileOfOrNull<Int>(25.0, r7) shouldBe null
    }

    @Test
    fun `selecting nullable group columns retain their schema`() {
        val df = dataFrameOf("g", "x")("a", null, "a", null, "b", 1, "b", 2)
        val result = df.groupBy("g").medianFor(r1, "x")
        result[0]["x"] shouldBe null
        result[1]["x"] shouldBe 1
        result["x"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `all estimators agree with independent sorted references and preserve input`() {
        val random = Random(2151)
        repeat(250) {
            val values = List(random.nextInt(1, 60)) { random.nextInt(-100, 101) }
            val sorted = values.sorted()
            val col = columnOf(*values.toTypedArray())
            for (p in listOf(0.0, 0.01, 0.25, 0.5, 0.75, 0.99, 1.0)) {
                val n = values.size
                col.percentile(p * 100.0, r1) shouldBe sorted[(ceil(n * p).toInt() - 1).coerceIn(sorted.indices)]
                col.percentile(p * 100.0, r3) shouldBe sorted[(round(n * p).toInt() - 1).coerceIn(sorted.indices)]
                for (method in listOf(r7, r8)) {
                    val h = (if (method == r7) (n - 1.0) * p else (n + 1.0 / 3.0) * p - 2.0 / 3.0)
                        .coerceIn(0.0, n - 1.0)
                    val low = sorted[floor(h).toInt()].toDouble()
                    val high = sorted[ceil(h).toInt()].toDouble()
                    val expected = low + (h - floor(h)) * (high - low)
                    kotlin.math.abs(col.percentile(p * 100.0, method) - expected).let { it < 1e-10 } shouldBe true
                }
            }
            col.toList() shouldBe values
        }
    }

    @Test
    fun `interpolation rejects arbitrary precision and heterogeneous numeric columns`() {
        val decimals = columnOf(BigDecimal.ONE, BigDecimal.TEN)
        shouldThrow<IllegalArgumentException> { decimals.median(r7) }
        val mixed = dataFrameOf("x", "y")(1, 2.0, 3, 4.0)
        shouldThrow<IllegalStateException> { mixed.percentile(50.0, r7, "x", "y") }
    }

    @Test
    fun `public estimation methods validate their low level inputs`() {
        val methods = listOf<QuantileEstimationMethod<*, *>>(r1, r3, r7, r8)
        for (method in methods) {
            shouldThrow<IllegalArgumentException> { method.oneBasedIndexOfQuantile(Double.NaN, 4) }
            shouldThrow<IllegalArgumentException> { method.oneBasedIndexOfQuantile(-0.1, 4) }
            shouldThrow<IllegalArgumentException> { method.oneBasedIndexOfQuantile(1.1, 4) }
            shouldThrow<IllegalArgumentException> { method.oneBasedIndexOfQuantile(0.5, 0) }
            shouldThrow<IllegalArgumentException> { method.oneBasedIndexOfQuantile(0.5, -1) }
        }
    }

    @Test
    fun `public interpolation preserves exact endpoints infinities and signed zero`() {
        val col = columnOf(Double.NEGATIVE_INFINITY, 1.0, Double.POSITIVE_INFINITY)
        col.percentile(0.0, r7) shouldBe Double.NEGATIVE_INFINITY
        col.percentile(100.0, r8) shouldBe Double.POSITIVE_INFINITY
        columnOf(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).median(r7) shouldBe Double.POSITIVE_INFINITY
        columnOf(-0.0, 1.0).percentile(0.0, r7).toBits() shouldBe (-0.0).toBits()
        columnOf(-0.0, -0.0).median(r8).toBits() shouldBe (-0.0).toBits()
        columnOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).median(r7).shouldBeNaN()
    }
}
