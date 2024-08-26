package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.util.TypeOf
import org.junit.Test

class EmptyDataFrameTests {

    @Test
    fun simple() {
        with(DataFrame.empty()) {
            rowsCount() shouldBe 0
            columnsCount() shouldBe 0
        }
    }

    @Test
    fun emptyWithRows() {
        with(DataFrame.empty(3)) {
            rowsCount() shouldBe 3
            columnsCount() shouldBe 0
        }
    }

    @DataSchema
    data class FrameSchema(val e: Double)

    @DataSchema
    data class GroupSchema(val c: Int, val d: String)

    @DataSchema
    data class Schema(val a: Int, val group: GroupSchema, val frame: List<FrameSchema>)

    @Test
    fun emptyWithColumns() {
        with(DataFrame.emptyOf<Schema>()) {
            rowsCount() shouldBe 0
            columnsCount() shouldBe 3
            columnNames() shouldBe listOf("a", "group", "frame")
            get("a").type() shouldBe TypeOf.INT
            getColumnGroup("group").let {
                it.columnNames() shouldBe listOf("c", "d")
                it["c"].type() shouldBe TypeOf.INT
            }
            getFrameColumn("frame").let {
                it.schema.value.columns.keys shouldBe listOf("e")
            }
        }
    }
}
