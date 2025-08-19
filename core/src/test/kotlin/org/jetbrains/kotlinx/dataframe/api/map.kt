package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.Test

class MapTests {

    @Test
    fun `map frame column with empty frames`() {
        val frames by columnOf(dataFrameOf("a")(1), emptyDataFrame())
        frames.map { it.firstOrNull() }.size() shouldBe frames.size()
    }

    @Test
    fun `map ColumnsContainer`() {
        val df = dataFrameOf("a")(1, 2).add {
            expr { "a"<Int>() + 1 }.cumSum() into "b"
        }
        df["b"][1] shouldBe 5
    }

    @Test
    fun `ColumnGroup map`() {
        val group = dataFrameOf("x", "y")(1, 10, 2, 20, 3, 30).asColumnGroup("g")
        val sums = group.asDataFrame().map { row -> row["x"] as Int + row["y"] as Int }
        sums shouldBe listOf(11, 22, 33)
    }

    @Test
    fun `ColumnGroup asDataColumn map`() {
        val group = dataFrameOf("x", "y")(1, 10, 2, 20, 3, 30).asColumnGroup("g")
        val col: DataColumn<Int> = group.asDataColumn().map { it["x"] as Int + it["y"] as Int }
        col.name() shouldBe "g"
        col.toList() shouldBe listOf(11, 22, 33)
    }
}
