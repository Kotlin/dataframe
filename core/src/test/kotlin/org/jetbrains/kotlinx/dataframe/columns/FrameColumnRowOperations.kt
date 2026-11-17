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
 * Row operations that leave no row keep the schema of a [FrameColumn].
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

    private fun AnyFrame.shouldKeepSchema() {
        rowsCount() shouldBe 0
        getFrameColumn("group").schema.value.columns.keys shouldBe setOf("k", "v")
        getFrameColumn("group").schema.value shouldBe df.getFrameColumn("group").schema.value
    }

    @Test
    fun `take with zero rows keeps the schema`() {
        df.take(0).shouldKeepSchema()
    }

    @Test
    fun `takeLast with zero rows keeps the schema`() {
        df.takeLast(0).shouldKeepSchema()
    }

    @Test
    fun `takeWhile that keeps no row keeps the schema`() {
        df.takeWhile { false }.shouldKeepSchema()
    }

    @Test
    fun `dropWhile that drops every row keeps the schema`() {
        df.dropWhile { true }.shouldKeepSchema()
    }

    @Test
    fun `drop of all rows keeps the schema`() {
        df.drop(2).shouldKeepSchema()
    }

    @Test
    fun `dropLast of all rows keeps the schema`() {
        df.dropLast(2).shouldKeepSchema()
    }

    @Test
    fun `drop with a predicate that matches every row keeps the schema`() {
        df.drop { true }.shouldKeepSchema()
    }

    @Test
    fun `getRows with no indices keeps the schema`() {
        df.getRows(emptyList()).shouldKeepSchema()
        df[emptyList<Int>()].shouldKeepSchema()
    }

    @Test
    fun `getRows with an empty range keeps the schema`() {
        df.getRows(0 until 0).shouldKeepSchema()
        df[0 until 0].shouldKeepSchema()
    }

    @Test
    fun `dropNulls that removes every row keeps the schema`() {
        df.dropNulls { "w"() }.shouldKeepSchema()
    }

    @Test
    fun `dropNA that removes every row keeps the schema`() {
        df.dropNA { "w"() }.shouldKeepSchema()
    }

    @Test
    fun `dropNaNs that removes every row keeps the schema`() {
        df.dropNaNs { "d"() }.shouldKeepSchema()
    }

    @Test
    fun `xs with a key that matches no row keeps the schema`() {
        df.xs(99).shouldKeepSchema()
    }
}
