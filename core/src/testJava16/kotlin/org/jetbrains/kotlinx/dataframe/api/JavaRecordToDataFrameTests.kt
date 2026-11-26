package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.Test
import kotlin.reflect.typeOf

/**
 * These tests require language version 16+ and are compiled separately from the main test suite.
 */
class JavaRecordToDataFrameTests {

    @Test
    fun `convert Java Record to DF`() {
        val record = JavaRecord(42, "test", 3.14)
        val df = listOf(record).toDataFrame()

        df shouldBe dataFrameOf(
            DataColumn.createValueColumn("property1", listOf(42), typeOf<Int>()),
            DataColumn.createValueColumn("property2", listOf("test"), typeOf<String>()),
            DataColumn.createValueColumn("property3", listOf(3.14), typeOf<Double>()),
        )
    }

    @Test
    fun `convert nested Java Record to DF with maxDepth`() {
        val record = JavaRecord(1, "nested", 2.5)
        val wrapper = JavaRecordWrapper("wrapper", record)
        val df = listOf(wrapper).toDataFrame(maxDepth = 2)

        df.columnNames() shouldBe listOf("name", "record")
        df["name"][0] shouldBe "wrapper"

        val recordCol = df.getColumnGroup("record")
        recordCol.columnNames() shouldBe listOf("property1", "property2", "property3")
        recordCol["property1"][0] shouldBe 1
        recordCol["property2"][0] shouldBe "nested"
        recordCol["property3"][0] shouldBe 2.5
    }
}
