package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.junit.Test

class MoveTests {

    val columnNames = listOf("q", "a.b", "b.c", "w", "a.c.d", "e.f", "b.d", "r")
    val columns = columnNames.map { emptyList<Int>().toColumn(it) }
    val df = columns.toDataFrame()
    val grouped = df.move { cols { it.name.contains(".") } }.into { it.name.split(".").toPath() }

    @Test
    fun `batch grouping`() {
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
    fun `select all atAnyDepth`() {
        val selected = grouped
            .getColumnsWithPaths { colGroups().colsAtAnyDepth().filter { !it.isColumnGroup() } }
            .map { it.path.joinToString(".") }
        selected shouldBe listOf("a.b", "a.c.d", "b.c", "b.d", "e.f")
    }

    @Test
    fun `batch ungrouping`() {
        val ungrouped = grouped.move {
            colsAtAnyDepth().filter { it.depth() > 0 && !it.isColumnGroup() }
        }.into { pathOf(it.path.joinToString(".")) }
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
        flattened.columnNames() shouldBe listOf("q", "b1", "d", "b", "w", "e", "r")
    }

    @Test
    fun `flatten several`() {
        val flattened = grouped.flatten { it["a"]["c"] and it["a"] and it["b"] }
        flattened.columnNames() shouldBe listOf("q", "b", "d", "c", "d1", "w", "e", "r")
    }

    @Test
    fun `flatten all`() {
        val flattened = grouped.flatten()
        flattened.columnNames() shouldBe listOf("q", "b", "d", "c", "d1", "w", "f", "r")
    }

    @Test
    fun `select recursively`() {
        val selected = grouped.select {
            it["a"].asColumnGroup().colsAtAnyDepth().filter { !it.isColumnGroup() }
        }
        selected.columnNames() shouldBe listOf("b", "d")
    }

    @Test
    fun `columnsWithPath in selector`() {
        val selected = grouped.getColumnsWithPaths { it["a"] }
        val actual = grouped.getColumnsWithPaths {
            selected.map { it.asColumnGroup().colsAtAnyDepth().filter { !it.isColumnGroup() } }.toColumnSet()
        }
        actual.map { it.path.joinToString(".") } shouldBe listOf("a.b", "a.c.d")
    }

    @Test
    fun `move after last`() {
        val df = dataFrameOf("1", "2")(1, 2)
        shouldNotThrowAny {
            df.move("1").after("2") shouldBe dataFrameOf("2", "1")(2, 1)
        }
    }

    @Test
    fun `move after in nested structure`() {
        val df = grouped.move { "a"["b"] }
            .after { "a"["c"]["d"] }
        df.columnNames() shouldBe listOf("q", "a", "b", "w", "e", "r")
        df["a"].asColumnGroup().columnNames() shouldBe listOf("c")
        df["a"]["c"].asColumnGroup().columnNames() shouldBe listOf("d", "b")
    }

    @Test
    fun `move after multiple columns`() {
        val df = grouped.move { "a"["b"] and "b"["c"] }
            .after { "a"["c"]["d"] }
        df.columnNames() shouldBe listOf("q", "a", "b", "w", "e", "r")
        df["a"].asColumnGroup().columnNames() shouldBe listOf("c")
        df["a"]["c"].asColumnGroup().columnNames() shouldBe listOf("d", "b", "c")
        df["b"].asColumnGroup().columnNames() shouldBe listOf("d")
    }

    @Test
    fun `move after with column selector`() {
        val df = grouped.move { colsAtAnyDepth().filter { it.name == "r" || it.name == "w" } }
            .after { "a"["c"]["d"] }
        df.columnNames() shouldBe listOf("q", "a", "b", "e")
        df["a"]["c"].asColumnGroup().columnNames() shouldBe listOf("d", "w", "r")
    }

    @Test
    fun `move after between groups`() {
        val df = grouped.move { "a"["b"] }.after { "b"["c"] }
        df.columnNames() shouldBe listOf("q", "a", "b", "w", "e", "r")
        df["a"].asColumnGroup().columnNames() shouldBe listOf("c")
        df["b"].asColumnGroup().columnNames() shouldBe listOf("c", "b", "d")
    }

    @Test
    fun `should throw when moving parent after child`() {
        // Simple case: direct parent-child relationship
        shouldThrow<IllegalArgumentException> {
            grouped.move("a").after { "a"["b"] }
        }.message shouldBe "Cannot move column 'a' after its own child column 'a/b'"

        // Nested case: deeper parent-child relationship
        shouldThrow<IllegalArgumentException> {
            grouped.move("a").after { "a"["c"]["d"] }
        }.message shouldBe "Cannot move column 'a' after its own child column 'a/c/d'"

        // Group case: moving group after its nested column
        shouldThrow<IllegalArgumentException> {
            grouped.move { "a"["c"] }.after { "a"["c"]["d"] }
        }.message shouldBe "Cannot move column 'a/c' after its own child column 'a/c/d'"
    }

    @Test
    fun `should throw when moving column after itself`() {
        shouldThrow<IllegalArgumentException> {
            grouped.move { "a"["b"] }.after { "a"["b"] }
        }.message shouldBe "Cannot move column 'a/b' after its own child column 'a/b'"
    }

    @Test
    fun `move before first`() {
        val df = dataFrameOf("1", "2")(1, 2)
        shouldNotThrowAny {
            df.move("2").before("1") shouldBe dataFrameOf("2", "1")(2, 1)
        }
    }

    //val columnNames = listOf("q", "a.b", "b.c", "w", "a.c.d", "e.f", "b.d", "r")
    @Test
    fun `move before in nested structure`() {
        val df = grouped.move { "a"["b"] }
            .before { "a"["c"]["d"] }
        df.columnNames() shouldBe listOf("q", "a", "b", "w", "e", "r")
        df["a"].asColumnGroup().columnNames() shouldBe listOf("c")
        df["a"]["c"].asColumnGroup().columnNames() shouldBe listOf("b", "d")
    }
}
