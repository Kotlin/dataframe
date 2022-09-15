package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.reflect.typeOf

class PivotTests {

    val a by columnOf(0, 1, 1)
    val b by columnOf("q", "q", "w")
    val c by columnOf('x', 'y', 'z')

    val df = dataFrameOf(a, b, c)

    @Test
    fun `simple pivot`() {
        val pivoted = df.pivot(b, inward = false).groupBy(a).values(c)
        pivoted.columnNames() shouldBe listOf("a", "q", "w")
        pivoted.rowsCount() shouldBe 2
        pivoted["q"].values() shouldBe listOf('x', 'y')
        pivoted["w"].values() shouldBe listOf(null, 'z')
    }

    @Test
    fun `pivot with rename`() {
        val pivoted = df.pivot(b).groupBy(a).values { c default '?' into "d" and (c into "e") }
        pivoted.columnNames() shouldBe listOf("a", "b")
        pivoted.rowsCount() shouldBe 2

        pivoted["b"]["q"]["d"].values() shouldBe listOf('x', 'y')
        pivoted["b"]["q"]["e"].values() shouldBe listOf('x', 'y')
        pivoted["b"]["w"]["d"].values() shouldBe listOf('?', 'z')
        pivoted["b"]["w"]["e"].values() shouldBe listOf(null, 'z')
    }

    @Test
    fun `pivot aggregate with default`() {
        val pivoted = df.pivot(b, inward = false).groupBy(a).aggregate {
            get(c).first() default '-' into "first"
            get(c).last() into "last" default '?'
        }
        pivoted.columnsCount() shouldBe 3
        pivoted.rowsCount() shouldBe 2
        val cols = pivoted.getColumns { except(a).allDfs() }
        cols.size shouldBe 4
        cols.forEach {
            it.type() shouldBe typeOf<Char>()
        }
        pivoted["w"]["first"][0] shouldBe '-'
        pivoted["w"]["last"][0] shouldBe '?'
    }

    @Test
    fun `pivot groupBy inward by default`() {
        val df = dataFrameOf("a", "b")(
            2, "a",
            4, "b",
            3, "c",
        )
        val pivoted = df.pivot("b").groupBy("a").matches()
        pivoted.columnNames() shouldBe listOf("a", "b")
        pivoted.getColumnGroup("b").columnNames() shouldBe listOf("a", "b", "c")
    }

    @Test
    fun `pivot values with nulls`() {
        val df = dataFrameOf("a" to listOf(1, 2, 2, 3), "b" to listOf(1, 1, null, null))

        df.pivot("a").values("b") shouldBe
            dataFrameOf("1", "2", "3")(1, listOf(1, null), null)[0]

        df.pivot("a").values("b", dropNA = true) shouldBe
            dataFrameOf("1" to listOf(1), "2" to listOf(1), "3" to listOf<Int?>(null))[0]
    }

    @Test
    fun `pivot in aggregate`() {
        val df = dataFrameOf("a" to listOf(1, 2, 2), "b" to listOf("q", "w", "q"))

        val expected = dataFrameOf("a", "q", "w")(
            1, 1, 0,
            2, 1, 1
        ).group("q", "w").into("b")

        df.groupBy("a").aggregate {
            pivot("b").count()
        } shouldBe expected

        df.groupBy("a").aggregate {
            pivot("b").count() into "c"
        } shouldBe expected.rename("b" to "c")
    }

    @Test
    fun `pivot two in aggregate`() {
        val df = dataFrameOf("a" to listOf(1, 2, 2), "b" to listOf("q", "w", "q"), "c" to listOf("w", "q", "w"))

        val expected = dataFrameOf(
            columnOf(1, 2) named "a",
            columnOf(
                columnOf(
                    columnOf(1, 1) named "q",
                    columnOf(0, 1) named "w"
                ) named "b",
                columnOf(
                    columnOf(1, 1) named "w",
                    columnOf(0, 1) named "q"
                ) named "c",
            ) named "d"
        )

        df.groupBy("a").aggregate {
            pivot("b", "c").count() into "d"
        } shouldBe expected
    }

    @Test
    fun `pivot minBy values`() {
        val df = dataFrameOf("a", "b", "c", "d")(
            1, 2, 3, 5,
            1, 0, 2, 4,
            2, 1, 3, 2,
            2, 5, 5, 3
        )
        df.pivot("a").minBy("b").values("c", "d", separate = true) shouldBe
            dataFrameOf("c1", "c2", "d1", "d2")(
                2, 3, 4, 2
            ).move { all() }.into { pathOf(it.name()[0].toString(), it.name()[1].toString()) }[0]
    }

    @Test
    fun `pivot groupBy last with`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 0, 2,
            2, 1, 3,
            2, 1, 5
        )
        df.pivot("a", inward = false).groupBy("b").default(-1).last().with { "c"<Int>() } shouldBe
            dataFrameOf("b", "1", "2")(
                2, 3, -1,
                0, 2, -1,
                1, -1, 5
            )
    }
}
