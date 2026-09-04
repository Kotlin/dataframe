package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test

class IsEmptyTests {

    /** The dataframe from the `isEmpty` / `isNotEmpty` KDoc examples and from the `isEmpty` documentation page. */
    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Charlie"),
        "age" to columnOf(15, 40),
    )

    @Test
    fun `dataframe with rows and columns is not empty`() {
        df.isEmpty() shouldBe false
        df.isNotEmpty() shouldBe true
    }

    @Test
    fun `dataframe with columns but no rows is empty`() {
        val noRows = df.filter { "age"<Int>() > 100 }
        // the columns are still there, only the rows are gone
        noRows.columnsCount() shouldBe 2
        noRows.rowsCount() shouldBe 0

        noRows.isEmpty() shouldBe true
        noRows.isNotEmpty() shouldBe false
    }

    @Test
    fun `dataframe with rows but no columns is empty`() {
        val noColumns = df.remove { all() }
        // removing all columns keeps the number of rows
        noColumns.columnsCount() shouldBe 0
        noColumns.rowsCount() shouldBe 2

        noColumns.isEmpty() shouldBe true
        noColumns.isNotEmpty() shouldBe false
    }

    @Test
    fun `dataframe created with rows but no columns is empty`() {
        val rowsOnly = DataFrame.empty(nrow = 5)
        rowsOnly.columnsCount() shouldBe 0
        rowsOnly.rowsCount() shouldBe 5

        rowsOnly.isEmpty() shouldBe true
        rowsOnly.isNotEmpty() shouldBe false
    }

    @Test
    fun `dataframe without rows and columns is empty`() {
        DataFrame.Empty.columnsCount() shouldBe 0
        DataFrame.Empty.rowsCount() shouldBe 0

        DataFrame.Empty.isEmpty() shouldBe true
        DataFrame.Empty.isNotEmpty() shouldBe false
    }

    @Test
    fun `column group with nested columns and rows is not empty`() {
        // the explicit type makes sure the call resolves to the DataFrame extension
        val group: ColumnGroup<*> = df.group { all() }.into("person")["person"].asColumnGroup()
        group.isEmpty() shouldBe false
        group.isNotEmpty() shouldBe true
    }

    @Test
    fun `column group without nested columns is empty`() {
        val group: ColumnGroup<*> = DataColumn.createColumnGroup("person", DataFrame.Empty)
        group.columnsCount() shouldBe 0

        group.isEmpty() shouldBe true
        group.isNotEmpty() shouldBe false
    }

    @Test
    fun `column group in a dataframe without rows is empty`() {
        val group: ColumnGroup<*> = df
            .group { all() }.into("person")
            .filter { false }["person"].asColumnGroup()
        // the nested columns are still there, the group just has no rows
        group.columnsCount() shouldBe 2
        group.rowsCount() shouldBe 0

        group.isEmpty() shouldBe true
        group.isNotEmpty() shouldBe false
    }

    @Test
    fun `dataframe whose only column is an empty column group is empty`() {
        val group = DataColumn.createColumnGroup("person", DataFrame.Empty)
        val withEmptyGroup = dataFrameOf(group.asDataColumn())
        // the column is there, but an empty group has no rows, so the dataframe has none either
        withEmptyGroup.columnsCount() shouldBe 1
        withEmptyGroup.rowsCount() shouldBe 0

        withEmptyGroup.isEmpty() shouldBe true
        withEmptyGroup.isNotEmpty() shouldBe false
    }

    @Test
    fun `dataframe with empty nested dataframes is not empty`() {
        val emptiedGroups = df
            .groupBy { "name"<String>() }.into("groups")
            .convert { "groups"<AnyFrame>() }.with { it.filter { false } }

        // every value of the frame column is an empty dataframe
        val groups: FrameColumn<*> = emptiedGroups["groups"].asFrameColumn()
        groups.values().map { it.isEmpty() } shouldBe listOf(true, true)

        // the outer dataframe keeps its own two columns and two rows, so it is not empty
        emptiedGroups.columnsCount() shouldBe 2
        emptiedGroups.rowsCount() shouldBe 2
        emptiedGroups.isEmpty() shouldBe false
        emptiedGroups.isNotEmpty() shouldBe true
    }

    @Test
    fun `KDoc example isEmpty`() {
        df.isEmpty() shouldBe false
        df.filter { "age"<Int>() > 100 }.isEmpty() shouldBe true
        df.remove { all() }.isEmpty() shouldBe true
    }

    @Test
    fun `KDoc example isNotEmpty`() {
        df.isNotEmpty() shouldBe true
        df.filter { "age"<Int>() > 100 }.isNotEmpty() shouldBe false
    }
}
