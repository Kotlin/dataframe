package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
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
 * - on a [DataFrame] without arguments: the index of every row, including a [DataFrame] without rows.
 *
 * - on a [DataFrame] with a [RowFilter]: the indices of the matching rows, their order,
 * and the cases where nothing matches.
 *
 * - on a column, as the `indices()` function and the `indices` property: the index of every
 * element for a value column, and one index per cell for a [FrameColumn].
 *
 * - on a [ColumnGroup]: a group is not a `DataColumn`, so `indices()` resolves to the
 * [DataFrame] overload and returns the row indices of the dataframe the group forms.
 *
 * - on the old `BaseColumn` overload: it is hidden from overload resolution but kept in the jar,
 * as binary compatibility for code compiled against earlier versions.
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
        // asserted as a range, like the non-empty case, so the return type stays part of the contract
        df.take(0).indices() shouldBe IntRange.EMPTY
    }

    @Test
    fun `KDoc example -- indices with a filter returns the indices of the matching rows`() {
        // the type annotation asserts the return type, which is part of the contract
        val moscow: List<Int> = df.indices { city == "Moscow" }
        moscow shouldBe listOf(2, 6)
    }

    @Test
    fun `indices with a filter written through the string API`() {
        df.indices { "city"<String?>() == "Moscow" } shouldBe listOf(2, 6)
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

    // The column `indices` is an accessor on `DataColumn`, not the `DataFrame` operation,
    // and it carries no KDoc yet; these tests pin its behaviour down regardless.

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
        df.take(0).age.indices() shouldBe IntRange.EMPTY
    }

    @Test
    fun `indices of a column group are the row indices of the dataframe it forms`() {
        // a ColumnGroup is not a DataColumn, so this resolves to the DataFrame overload
        df.name.indices() shouldBe 0..6
    }

    @Test
    fun `indices of a frame column has one index per cell`() {
        // three first names, so three cells holding 2, 2 and 3 of the seven rows
        val frames: FrameColumn<*> = df.groupBy { name.firstName }.into("frames")["frames"].asFrameColumn()
        frames.indices() shouldBe 0..2
        // the seven rows are still there, spread over the three cells
        frames.toList().sumOf { it.rowsCount() } shouldBe 7
    }

    @Test
    fun `the hidden BaseColumn overload stays in the jar for binary compatibility`() {
        // code compiled against the old receiver links DataColumnKt.indices(BaseColumn); the shim keeps that
        // JVM method present while HIDDEN removes it from resolution. The facade class is synthetic, so it
        // is reached by name.
        Class.forName("org.jetbrains.kotlinx.dataframe.DataColumnKt")
            .getDeclaredMethod("indices", BaseColumn::class.java)
    }

    // endregion
}
