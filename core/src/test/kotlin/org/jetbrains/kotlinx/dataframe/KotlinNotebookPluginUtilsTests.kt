package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils
import org.junit.Test
import kotlin.random.Random

/**
 * Other tests are located in Jupyter module:
 * org.jetbrains.kotlinx.dataframe.jupyter.RenderingTests
 */
class KotlinNotebookPluginUtilsTests {
    @Test
    fun `sort lists by size descending`() {
        val random = Random(123)
        val lists = List(20) { List(random.nextInt(1, 100)) { it } } + null
        val df = dataFrameOf("listColumn" to lists)

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("listColumn")), desc = listOf(true))

        res["listColumn"].values() shouldBe lists.sortedByDescending { it?.size ?: 0 }
    }

    @Test
    fun `sort lists by size ascending`() {
        val lists = listOf(listOf(1, 2, 3), listOf(1), listOf(1, 2), null)
        val df = dataFrameOf("listColumn" to lists)

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("listColumn")), desc = listOf(false))

        res["listColumn"].values() shouldBe listOf(null, listOf(1), listOf(1, 2), listOf(1, 2, 3))
    }

    @Test
    fun `sort empty lists`() {
        val lists = listOf(listOf(1, 2), emptyList(), listOf(1), emptyList())
        val df = dataFrameOf("listColumn" to lists)

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("listColumn")), desc = listOf(true))

        res["listColumn"].values() shouldBe listOf(listOf(1, 2), listOf(1), emptyList(), emptyList())
    }

    @Test
    fun `sort lists with equal sizes preserves stability`() {
        val lists = listOf(listOf("a"), listOf("b"), listOf("c"))
        val df = dataFrameOf("listColumn" to lists)

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("listColumn")), desc = listOf(true))

        res["listColumn"].values() shouldBe lists
    }

    @Test
    fun `sort frame column by row count descending`() {
        val frames = listOf(
            dataFrameOf("x" to listOf(1)),
            dataFrameOf("x" to listOf(1, 2, 3)),
            dataFrameOf("x" to listOf(1, 2)),
            DataFrame.empty(),
        )
        val df = dataFrameOf("nested" to frames.toColumn())

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("nested")), desc = listOf(true))

        res["nested"].values().map { (it as DataFrame<*>).rowsCount() } shouldBe listOf(3, 2, 1, 0)
    }

    @Test
    fun `sort frame column by row count ascending`() {
        val frames = listOf(
            dataFrameOf("x" to listOf(1, 2, 3)),
            dataFrameOf("x" to listOf(1)),
            DataFrame.empty(),
        )
        val df = dataFrameOf("nested" to frames.toColumn())

        val res = KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("nested")), desc = listOf(false))

        res["nested"].values().map { (it as DataFrame<*>).rowsCount() } shouldBe listOf(0, 1, 3)
    }
}
