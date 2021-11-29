package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class PivotTests {

    val a by columnOf(0, 1, 1)
    val b by columnOf("q", "q", "w")
    val c by columnOf('x', 'y', 'z')

    val df = dataFrameOf(a, b, c)

    @Test
    fun `simple pivot`() {
        val pivoted = df.pivot(b, inward = false).groupBy(a).values(c)
        pivoted.columnNames() shouldBe listOf("a", "q", "w")
        pivoted.nrow shouldBe 2
        pivoted["q"].values() shouldBe listOf('x', 'y')
        pivoted["w"].values() shouldBe listOf(null, 'z')
    }

    @Test
    fun `pivot with rename`() {
        val pivoted = df.pivot(b).groupBy(a).values { c default '?' into "d" and (c into "e") }
        pivoted.columnNames() shouldBe listOf("a", "b")
        pivoted.nrow shouldBe 2

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
        pivoted.ncol shouldBe 3
        pivoted.nrow shouldBe 2
        val cols = pivoted.getColumns { except(a).allDfs() }
        cols.size shouldBe 4
        cols.forEach {
            it.type() shouldBe getType<Char>()
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
}
