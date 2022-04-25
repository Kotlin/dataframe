package org.jetbrains.kotlinx.dataframe.puzzles

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import kotlin.math.round
import kotlin.random.Random

class HardTests {

    @Test
    fun `count difference back to previous zero`() {
        val x = columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4).named("X")
        val df = x.toDataFrame()
        val y = columnOf(1, 2, 0, 1, 2, 3, 4, 0, 1, 2).named("Y")

        df.map("Y") {
            if (it[x] == 0) 0 else (prev()?.new() ?: 0) + 1
        } shouldBe y

        df.map("Y") {
            if (it["X"] == 0) 0 else (prev()?.new() ?: 0) + 1
        } shouldBe y
    }

    @Test
    fun `3 largest values`() {
        val names = ('a'..'h').map { it.toString() }
        val random = Random(30)
        val list = List(64) { random.nextInt(1, 101) }
        val df = dataFrameOf(names)(*list.toTypedArray())
        val index by column<Int>()
        val vals by column<Int>()
        val name by column<String>()

        val expected = dataFrameOf("index", "name")(0, "d", 2, "c", 3, "f")

        df.add("index") { index() }
            .gather { dropLast() }.into("name", "vals")
            .sortByDesc { vals }.take(3)[index, name] shouldBe expected

        df.add("index") { index() }
            .gather { dropLast() }.into("name", "vals")
            .sortByDesc("vals").take(3)["index", "name"] shouldBe expected
    }

    @Test
    fun `group mean and negative values`() {
        val random = Random(31)
        val lab = listOf("A", "B")

        val vals by columnOf(*Array(15) { random.nextInt(-30, 30) })
        val grps by columnOf(*Array(15) { lab[random.nextInt(0, 2)] })

        val df = dataFrameOf(vals, grps)

        val expected = dataFrameOf("vals", "grps", "patched_values")(
            -17, "B", 21.0,
            -7, "B", 21.0,
            16, "A", 16.0,
            28, "B", 28.0,
            9, "A", 9.0,
            16, "B", 16.0,
            -21, "B", 21.0,
            -14, "A", 16.0,
            -19, "A", 16.0,
            -22, "A", 16.0,
            19, "B", 19.0,
            -2, "B", 21.0,
            -1, "A", 16.0,
            -19, "B", 21.0,
            23, "A", 23.0
        )

        val means = df.filter { vals >= 0 }
            .groupBy { grps }.mean()
            .pivot { grps }.values { vals }

        df.add("patched_values") {
            if (vals() < 0) means[grps()] as Double else vals().toDouble()
        } shouldBe expected

        val meansStr = df.filter { "vals"<Int>() >= 0 }
            .groupBy("grps").mean()
            .pivot("grps").values("vals")

        df.add("patched_values") {
            if ("vals"<Int>() < 0) meansStr["grps"<String>()] as Double else "vals"<Int>().toDouble()
        } shouldBe expected
    }

    @Test
    fun `rolling mean`() {
        val groups by columnOf("a", "a", "b", "b", "a", "b", "b", "b", "a", "b", "a", "b")
        val value by columnOf(1.0, 2.0, 3.0, Double.NaN, 2.0, 3.0, Double.NaN, 1.0, 7.0, 3.0, Double.NaN, 8.0)
        val df = dataFrameOf(groups, value)

        val expected = dataFrameOf("groups", "value", "res")(
            "a", 1.0, 1.0,
            "a", 2.0, 2.0,
            "b", 3.0, 3.0,
            "b", Double.NaN, 3.0,
            "a", 2.0, 2.0,
            "b", 3.0, 3.0,
            "b", Double.NaN, 3.0,
            "b", 1.0, 2.0,
            "a", 7.0, 4.0,
            "b", 3.0, 2.0,
            "a", Double.NaN, 4.0,
            "b", 8.0, 4.0,
        )

        df.add("id") { index() }
            .groupBy { groups }.add("res") {
                round(relative(-2..0)[value].filter { !it.isNaN() }.mean())
            }.concat()
            .sortBy("id")
            .remove("id") shouldBe expected
    }
}
