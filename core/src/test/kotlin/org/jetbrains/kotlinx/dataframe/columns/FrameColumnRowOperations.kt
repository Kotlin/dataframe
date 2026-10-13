package org.jetbrains.kotlinx.dataframe.columns

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.dropWhile
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.api.takeWhile
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.xs
import org.junit.Test

/**
 * Row operations that leave no row keep the nested columns of a [FrameColumn].
 *
 * The schema of a frame column is computed from its non-empty dataframes, so with no row left
 * there is nothing to compute it from, and the schema of the original column is kept.
 * `head`, `tail` and `filter` are tested next to their own tests (`HeadTests`, `TailTests`, `DataColumns`).
 */
class FrameColumnRowOperations {

    // `group` is a frame column whose dataframes have the columns `k` and `v`;
    // `w` holds only nulls, `d` holds only NaNs, so the drop-nulls operations remove every row
    private val df = dataFrameOf("k", "v")(1, "x", 2, "y").groupBy("k").toDataFrame()
        .add("w") { null as Int? }
        .add("d") { Double.NaN }

    private fun AnyFrame.shouldKeepNestedColumns() {
        rowsCount() shouldBe 0
        getFrameColumn("group").schema.value.columns.keys shouldBe setOf("k", "v")
        getFrameColumn("group").schema.value shouldBe df.getFrameColumn("group").schema.value
    }

    @Test
    fun `take with zero rows keeps the nested columns`() {
        df.take(0).shouldKeepNestedColumns()
    }

    @Test
    fun `takeLast with zero rows keeps the nested columns`() {
        df.takeLast(0).shouldKeepNestedColumns()
    }

    @Test
    fun `takeWhile that keeps no row keeps the nested columns`() {
        df.takeWhile { false }.shouldKeepNestedColumns()
    }

    @Test
    fun `dropWhile that drops every row keeps the nested columns`() {
        df.dropWhile { true }.shouldKeepNestedColumns()
    }

    @Test
    fun `drop of all rows keeps the nested columns`() {
        df.drop(2).shouldKeepNestedColumns()
    }

    @Test
    fun `dropLast of all rows keeps the nested columns`() {
        df.dropLast(2).shouldKeepNestedColumns()
    }

    @Test
    fun `drop with a predicate that matches every row keeps the nested columns`() {
        df.drop { true }.shouldKeepNestedColumns()
    }

    @Test
    fun `getRows with no indices keeps the nested columns`() {
        df.getRows(emptyList()).shouldKeepNestedColumns()
        df[emptyList<Int>()].shouldKeepNestedColumns()
    }

    @Test
    fun `getRows with an empty range keeps the nested columns`() {
        df.getRows(0 until 0).shouldKeepNestedColumns()
        df[0 until 0].shouldKeepNestedColumns()
    }

    @Test
    fun `dropNulls that removes every row keeps the nested columns`() {
        df.dropNulls { "w"() }.shouldKeepNestedColumns()
    }

    @Test
    fun `dropNA that removes every row keeps the nested columns`() {
        df.dropNA { "w"() }.shouldKeepNestedColumns()
    }

    @Test
    fun `dropNaNs that removes every row keeps the nested columns`() {
        df.dropNaNs { "d"() }.shouldKeepNestedColumns()
    }

    @Test
    fun `xs with a key that matches no row keeps the nested columns`() {
        df.xs(99).shouldKeepNestedColumns()
    }
}
