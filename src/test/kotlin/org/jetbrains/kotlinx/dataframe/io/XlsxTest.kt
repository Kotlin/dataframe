package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.testResource
import org.junit.Test

class XlsxTest {

    @Test
    fun `numerical columns`() {
        DataFrame.readExcel(testResource("sample.xls"), "Sheet1") shouldBe dataFrameOf("col1", "col2")(1.0, 2.0)
    }

    @Test
    fun `column with empty values`() {
        val readExcel = DataFrame.readExcel(testResource("sample1.xls"), "Sheet1")
        readExcel shouldBe dataFrameOf("col1", "col2")(1.0, null)
        readExcel["col2"].hasNulls() shouldBe true
    }

    @Test
    fun `column with empty header`() {
        val df = DataFrame.readExcel(testResource("sample2.xlsx"), "Sheet1", "A:C")
        df shouldBe dataFrameOf("col1", "col2", "C")(1.0, null, 3.0)
    }

    @Test
    fun `limit row number`() {
        val df = DataFrame.readExcel(testResource("sample4.xls"), "Sheet1", rowsCount = 5)
        val column = List(5) { (it + 1).toDouble() }.toColumn("col1")
        df shouldBe dataFrameOf(column)
    }

    @Test
    fun `first sheet is default sheet`() {
        DataFrame.readExcel(testResource("sample.xls"), "Sheet1") shouldBe DataFrame.readExcel(testResource("sample.xls"))
    }
}
