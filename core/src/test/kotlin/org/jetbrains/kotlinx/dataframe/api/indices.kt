package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

/**
 * Tests the behavior of the `indices` API:
 *
 * - on a [org.jetbrains.kotlinx.dataframe.DataFrame] without arguments: the index of every row,
 * including a [org.jetbrains.kotlinx.dataframe.DataFrame] without rows.
 *
 * - on a [org.jetbrains.kotlinx.dataframe.DataFrame] with a
 * [org.jetbrains.kotlinx.dataframe.RowFilter]: the indices of the matching rows,
 * their order, and the cases where nothing matches.
 *
 * - on a column, as the `indices()` function and the `indices` property: the index of every
 * element, and what an element is for a [org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * and for a [FrameColumn].
 */
class IndicesTests : TestBase() {

    // `df` comes from `TestBase` and has seven rows;
    // "Moscow" is the city in rows 2 and 6, "Charlie" the first name in rows 2, 3 and 6.

    // region on a DataFrame

    @Test
    fun `indices returns the index of every row`() {
        // the type annotation asserts the return type, which is part of the contract
        val indices: IntRange = df.indices()
        indices shouldBe 0..6
    }

    @Test
    fun `indices of a dataframe without rows is empty`() {
        df.take(0).indices().toList() shouldBe emptyList()
    }

    @Test
    fun `KDoc example -- indices with a filter returns the indices of the matching rows`() {
        // the type annotation asserts the return type, which is part of the contract
        val moscow: List<Int> = df.indices { city == "Moscow" }
        moscow shouldBe listOf(2, 6)
    }

    @Test
    fun `indices with a filter lists them in row order`() {
        df.indices { name.firstName == "Charlie" } shouldBe listOf(2, 3, 6)
    }

    @Test
    fun `the filter receives the row as both this and it`() {
        // `age` alone reads the value from `this`, `it.age` from the lambda argument
        df.indices { age > 18 } shouldBe listOf(1, 2, 3, 4, 5, 6)
        df.indices { it.age > 18 } shouldBe listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `indices with a filter that no row satisfies is empty`() {
        df.indices { city == "Paris" } shouldBe emptyList()
    }

    @Test
    fun `indices with a filter on a dataframe without rows is empty`() {
        df.take(0).indices { city == "Moscow" } shouldBe emptyList()
    }

    // endregion

    // region on a column

    // The column `indices` is an accessor on `DataColumn`/`BaseColumn`, not the `DataFrame`
    // operation, and it carries no KDoc yet; these tests pin its behaviour down regardless.

    @Test
    fun `indices of a column is the index of every element`() {
        // the type annotation asserts the return type, which is part of the contract
        val indices: IntRange = df.age.indices()
        indices shouldBe 0..6
    }

    @Test
    fun `the indices property gives the same range as the indices function`() {
        // both are asserted against the expected value, not against each other
        df.age.indices shouldBe 0..6
        df.age.indices() shouldBe 0..6
    }

    @Test
    fun `indices of a column without elements is empty`() {
        df.take(0).age.indices().toList() shouldBe emptyList()
    }

    @Test
    fun `indices of a column group are the row indices of the dataframe it forms`() {
        // typed as a BaseColumn, so the receiver leaves only the column overload applicable
        val group: AnyBaseCol = df.name
        group.indices() shouldBe 0..6
    }

    @Test
    fun `indices of a frame column has one index per cell`() {
        // three first names, so three cells holding 2, 2 and 3 of the seven rows
        val frames: FrameColumn<*> = df.groupBy { name.firstName }.into("frames")["frames"].asFrameColumn()
        frames.indices() shouldBe 0..2
        // the seven rows are still there, spread over the three cells
        frames.toList().sumOf { it.rowsCount() } shouldBe 7
    }

    // endregion
}
