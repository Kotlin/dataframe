package org.jetbrains.kotlinx.dataframe.columns

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

class ColumnGroupTests {

    @Test
    fun emptyColumnGroup() {
        val df = DataFrame.empty(2)
        val group = DataColumn.createColumnGroup("a", df)
        group.size() shouldBe 2
        group.columnsCount() shouldBe 0
        group.rowsCount() shouldBe 2
        group.distinct().rowsCount() shouldBe 0
    }
}
