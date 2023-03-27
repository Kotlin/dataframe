package org.jetbrains.kotlinx.dataframe.puzzles

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

class MediumTests {

    private val random = Random(42)

    @Test
    fun `filter rows that contain same value as row above`() {
        val df = dataFrameOf("A")(1, 2, 2, 3, 4, 5, 5, 5, 6, 7, 7)
        val a by column<Int>("A")

        val expected = dataFrameOf("A")(1, 2, 3, 4, 5, 6, 7)

        df.filter { prev()?.get(a) != a() } shouldBe expected
        df.filter { prev()?.get("A") != "A"() } shouldBe expected

        df.filter { diff { a() } != 0 } shouldBe expected
        df.filter { diff { "A"<Int>() } != 0 } shouldBe expected
    }

    @Test
    fun `subtract row mean from each element in row`() {
        val df = dataFrameOf("a", "b", "c")(
            1.0, 2.0, 3.0,
            1.3, 2.3, 3.3,
            1.57, 2.57, 3.57
        )

        val expected = dataFrameOf("a", "b", "c")(
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1
        )

        df.convert { colsOf<Double>() }.with { (it - rowMean()).roundToInt() } shouldBe expected
    }

    @Test
    fun `smallest sum`() {
        val names = ('a'..'j').map { it.toString() }
        val df = dataFrameOf(names) { List(5) { random.nextDouble() } }

        df.sum().transposeTo<Double>().minBy { value }.name shouldBe "b"
        df.sum().transpose().minBy("value")["name"] shouldBe "b"
    }

    @Test
    fun `count unique rows`() {
        val df = dataFrameOf("a", "b", "c") { List(30) { random.nextInt(0..2) } }
        df.countDistinct() shouldBe 19
    }

    @Test
    fun `find column which contains third NaN value`() {
        val nan = Double.NaN
        val names = ('a'..'j').map { it.toString() }
        val df = dataFrameOf(names)(
            0.04, nan, nan, 0.25, nan, 0.43, 0.71, 0.51, nan, nan,
            nan, nan, nan, 0.04, 0.76, nan, nan, 0.67, 0.76, 0.16,
            nan, nan, 0.5, nan, 0.31, 0.4, nan, nan, 0.24, 0.01,
            0.49, nan, nan, 0.62, 0.73, 0.26, 0.85, nan, nan, nan,
            nan, nan, 0.41, nan, 0.05, nan, 0.61, nan, 0.48, 0.68
        )

        val expected = columnOf("e", "c", "d", "h", "d").named("res")

        df.mapToColumn("res") {
            namedValuesOf<Double>().filter { it.value.isNaN }.drop(2).firstOrNull()?.name
        } shouldBe expected
    }

    @Test
    fun `sum of three greatest values`() {
        val grps by columnOf("a", "a", "a", "b", "b", "c", "a", "a", "b", "c", "c", "c", "b", "b", "c")
        val vals by columnOf(12, 345, 3, 1, 45, 14, 4, 52, 54, 23, 235, 21, 57, 3, 87)
        val df = dataFrameOf(grps, vals)

        val expected = dataFrameOf("grps", "res")("a", 409, "b", 156, "c", 345)

        df.groupBy { grps }.aggregate {
            vals().sortDesc().take(3).sum() into "res"
        } shouldBe expected

        df.groupBy { grps }.aggregate {
            "vals"<Int>().sortDesc().take(3).sum() into "res"
        } shouldBe expected
    }

    @Test
    fun `sum bins`() {
        val list = List(200) { random.nextInt(1, 101) }
        val df = dataFrameOf("A", "B")(*list.toTypedArray())
        val a by column<Int>("A")
        val b by column<Int>("B")

        val expected = dataFrameOf("A", "B")(
            "(0, 10]", 353,
            "(10, 20]", 873,
            "(20, 30]", 321,
            "(30, 40]", 322,
            "(40, 50]", 432,
            "(50, 60]", 754,
            "(60, 70]", 405,
            "(70, 80]", 561,
            "(80, 90]", 657,
            "(90, 100]", 527
        )

        df.groupBy { a.map { (it - 1) / 10 } }.sum { b }
            .sortBy { a }.convert { a }.with { "(${it * 10}, ${it * 10 + 10}]" } shouldBe expected

        df.groupBy { "A"<Int>().map { (it - 1) / 10 } }.sum("B").sortBy("A")
            .convert { "A"<Int>() }.with { "(${it * 10}, ${it * 10 + 10}]" } shouldBe expected
    }
}
