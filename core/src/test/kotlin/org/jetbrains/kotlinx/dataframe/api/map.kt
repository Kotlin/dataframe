package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.junit.Test
import kotlin.reflect.typeOf

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

    // DataColumn<T>.map(reified)
    @Test
    fun `DataColumn map reified`() {
        val col by columnOf(1, 2, 3)
        val mapped = col.map { it * 2 }
        mapped.name() shouldBe "col"
        mapped.toList() shouldContainExactly listOf(2, 4, 6)
    }

    // DataColumn<T>.map(type: KType)
    @Test
    fun `DataColumn map with explicit KType`() {
        val col by columnOf(1, 2)
        val mapped = col.map(typeOf<String>()) { it.toString() }
        mapped.toList() shouldContainExactly listOf("1", "2")
    }

    // DataColumn<T>.mapIndexed(reified)
    @Test
    fun `DataColumn mapIndexed reified`() {
        val col by columnOf(10, 20)
        val mapped = col.mapIndexed { i, v -> i + v }
        mapped.toList() shouldContainExactly listOf(10, 21)
    }

    // DataColumn<T>.mapIndexed(type: KType)
    @Test
    fun `DataColumn mapIndexed with explicit KType`() {
        val col by columnOf(10, 20)
        val mapped = col.mapIndexed(typeOf<String>()) { i, v -> "$i:$v" }
        mapped.toList() shouldContainExactly listOf("0:10", "1:20")
    }

    // ColumnGroup<T> helpers
    private fun simpleGroup(rows: Int = 3): ColumnGroup<*> {
        val nested: DataFrame<*> = dataFrameOf("a", "b")(1, 10, 2, 20, 3, 30).take(rows)
        return DataColumn.createColumnGroup("g", nested)
    }

    // ColumnGroup<T>.map(reified)
    @Test
    fun `ColumnGroup map reified`() {
        val group = simpleGroup(3) as ColumnGroup<Nothing?>
        val f: (DataRow<Nothing?>) -> Int = { row -> (row["a"] as Int) + (row["b"] as Int) }
        val mapped = group.map(infer = Infer.Nulls, transform = f)
        mapped.name() shouldBe "g"
        mapped.toList() shouldContainExactly listOf(11, 22, 33)
    }

    // ColumnGroup<T>.map(type: KType)
    @Test
    fun `ColumnGroup map with explicit KType`() {
        val group = simpleGroup(2)
        val mapped = group.map(typeOf<String>()) { row ->
            val a = row["a"] as Int
            val b = row["b"] as Int
            "$a+$b"
        }
        mapped.toList() shouldContainExactly listOf("1+10", "2+20")
    }

    // ColumnGroup<T>.mapIndexed(reified)
    @Test
    fun `ColumnGroup mapIndexed reified`() {
        val group = simpleGroup(2) as ColumnGroup<Nothing?>
        val f: (Int, DataRow<Nothing?>) -> Int = { i, row -> i + (row["a"] as Int) }
        val mapped = group.mapIndexed(infer = Infer.Nulls, transform = f)
        mapped.toList() shouldContainExactly listOf(0 + 1, 1 + 2)
    }

    // ColumnGroup<T>.mapIndexed(type: KType)
    @Test
    fun `ColumnGroup mapIndexed with explicit KType`() {
        val group = simpleGroup(2)
        val mapped = group.mapIndexed(typeOf<String>()) { i, row ->
            "$i:${row["b"] as Int}"
        }
        mapped.toList() shouldContainExactly listOf("0:10", "1:20")
    }

    // DataFrame<T>.map(RowExpression)
    @Test
    fun `DataFrame map rows to list`() {
        val df = dataFrameOf("a")(1, 2, 3)
        val result = df.map { it["a"] as Int * 10 }
        result shouldContainExactly listOf(10, 20, 30)
    }

    // GroupBy<T, G>.map(Selector)
    @Test
    fun `GroupBy map selector`() {
        val df = dataFrameOf("g", "v")("x", 1, "x", 2, "y", 3)
        val grouped = df.groupBy("g")
        val sizes = grouped.map { group.rowsCount() }
        // groups: x -> 2 rows, y -> 1 row; order corresponds to keys order
        sizes shouldContainExactly listOf(2, 1)
    }
}
