package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.toColumnPath
import org.junit.Test

class MoveTests {

    val columnNames = listOf("q", "a.b", "b.c", "w", "a.c.d", "e.f", "b.d", "r")
    val columns = columnNames.map { column(it, emptyList<Int>()) }
    val df = columns.toDataFrame()
    val grouped = df.move { cols { it.name.contains(".") } }.into { it.name.split(".").toColumnPath() }

    @Test
    fun batchGrouping() {
        grouped.columnNames() shouldBe listOf("q", "a", "b", "w", "e", "r")
        grouped["a"].asColumnGroup().columnNames() shouldBe listOf("b", "c")
        grouped["a"]["c"].asColumnGroup().columnNames() shouldBe listOf("d")
        grouped["b"].asColumnGroup().columnNames() shouldBe listOf("c", "d")
        grouped["e"].asColumnGroup().columnNames() shouldBe listOf("f")
    }

    @Test
    fun `select all`() {
        grouped.getColumnsWithPaths { all() }.map { it.path.joinToString(".") } shouldBe grouped.columnNames()
    }

    @Test
    fun `select all dfs`() {
        val selected = grouped.getColumnsWithPaths { all().dfs() }.map { it.path.joinToString(".") }
        selected shouldBe listOf("a.b", "a.c.d", "b.c", "b.d", "e.f")
    }

    @Test
    fun batchUngrouping() {
        val ungrouped = grouped.move { dfs { it.depth() > 0 && !it.isColumnGroup() } }.into { pathOf(it.path.joinToString(".")) }
        ungrouped.columnNames() shouldBe listOf("q", "a.b", "a.c.d", "b.c", "b.d", "w", "e.f", "r")
    }

    @Test
    fun `ungroup one`() {
        val ungrouped = grouped.remove("b").ungroup { it["a"] }
        ungrouped.columnNames() shouldBe listOf("q", "b", "c", "w", "e", "r")
        ungrouped["c"].asColumnGroup().columnNames() shouldBe listOf("d")
    }

    @Test
    fun `flatten one`() {
        val flattened = grouped.flatten { it["a"] }
        flattened.columnNames() shouldBe listOf("q", "a_b", "a_c_d", "b", "w", "e", "r")
    }

    @Test
    fun `flatten several`() {
        val flattened = grouped.flatten { it["a"]["c"] and it["a"] and it["b"] }
        flattened.columnNames() shouldBe listOf("q", "a_b", "a_c_d", "b_c", "b_d", "w", "e", "r")
    }

    @Test
    fun `flatten all`() {
        val flattened = grouped.flatten(".")
        flattened.columnNames() shouldBe listOf("q", "a.b", "a.c.d", "b.c", "b.d", "w", "e.f", "r")
    }

    @Test
    fun `selectDfs`() {
        val selected = grouped.select { it["a"].dfs { !it.isColumnGroup() } }
        selected.columnNames() shouldBe listOf("b", "d")
    }

    @Test
    fun `columnsWithPath in selector`() {
        val selected = grouped.getColumnsWithPaths { it["a"] }
        val actual = grouped.getColumnsWithPaths { selected.map { it.dfs() }.toColumnSet() }
        actual.map { it.path.joinToString(".") } shouldBe listOf("a.b", "a.c.d")
    }
}
