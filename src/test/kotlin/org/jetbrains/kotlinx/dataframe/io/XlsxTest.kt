package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.testResource
import org.junit.Test
import java.nio.file.Files
import kotlin.reflect.typeOf

class XlsxTest {

    @Test
    fun `numerical columns`() {
        DataFrame.readExcel(testResource("sample.xls"), "Sheet1") shouldBe dataFrameOf("col1", "col2")(1.0, 2.0)
    }

    @Test
    fun `column with empty values`() {
        val readExcel = DataFrame.readExcel(testResource("empty_cell.xls"), "Sheet1")
        readExcel shouldBe dataFrameOf("col1", "col2")(1.0, null)
        readExcel["col2"].hasNulls() shouldBe true
    }

    @Test
    fun `empty cell is null`() {
        val wb = WorkbookFactory.create(testResource("empty_cell.xls").openStream())
        wb.getSheetAt(0).getRow(1).getCell(1) shouldBe null
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

    @Test
    fun `read and write are isomorphic for string, double and null values`() {
        val temp = Files.createTempFile("excel", ".xlsx").toFile()
        val df = dataFrameOf("col1", "col2")(
            "string value", 3.2,
            "string value 1", null
        )
        val extendedDf = List(10) { df }.concat()
        extendedDf.writeExcel(temp)
        val extendedDf1 = DataFrame.readExcel(temp)
        extendedDf shouldBe extendedDf1
    }

    @Test
    fun `read date time`() {
        val df = DataFrame.read(testResource("datetime.xlsx"))
        df["time"].type() shouldBe typeOf<LocalDateTime>()
    }

    @Test
    fun `write date time`() {
        val df = DataFrame.read(testResource("datetime.xlsx"))
        val temp = Files.createTempFile("excel", ".xlsx").toFile()
        df.writeExcel(temp)
        DataFrame.readExcel(temp) shouldBe df
    }
}
