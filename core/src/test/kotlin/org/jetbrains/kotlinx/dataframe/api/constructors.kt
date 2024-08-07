package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test

class ConstructorsTests {

    @Test
    fun `untitled column naming`() {
        val builder = DynamicDataFrameBuilder()
        repeat(5) {
            builder.add(columnOf(1, 2, 3))
        }
        builder.toDataFrame() shouldBe dataFrameOf(List(5) { columnOf(1, 2, 3) })
    }

    @Test
    fun `duplicated name`() {
        val builder = DynamicDataFrameBuilder()
        val column by columnOf(1, 2, 3)
        builder.add(column)
        builder.add(column)
        val df = builder.toDataFrame()
        df.columnsCount() shouldBe 2
        df.columnNames() shouldBe listOf(column.name(), "${column.name()}1")
    }

    @Test
    fun `dataFrameOf with nothing columns`() {
        dataFrameOf("a" to emptyList())["a"].type shouldBe nothingType(false)
        dataFrameOf("a" to listOf(null))["a"].type shouldBe nothingType(true)
    }
}
