package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.size
import org.junit.Test
import java.net.URL
import java.nio.file.Files
import kotlin.reflect.typeOf

class XlsxTest {

    fun testResource(resourcePath: String): URL = this::class.java.classLoader.getResource(resourcePath)!!

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
        val df = DataFrame.readExcel(testResource("sample2.xlsx"), "Sheet1", columns = "A:C")
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

    @Test
    fun `read header on second row`() {
        val df = DataFrame.readExcel(testResource("custom_header_position.xlsx"), skipRows = 1)
        df.columnNames() shouldBe listOf("header1", "header2")
        df.size() shouldBe DataFrameSize(2, 3)
    }

    @Test
    fun `consider skipRows when obtaining column indexes`() {
        val df = DataFrame.readExcel(testResource("header.xlsx"), skipRows = 6, rowsCount = 1)
        df.columnNames() shouldBe listOf("Well", "Well Position", "Omit", "Sample Name", "Target Name", "Task", "Reporter", "Quencher")
        df shouldBe dataFrameOf("Well", "Well Position", "Omit", "Sample Name", "Target Name", "Task", "Reporter", "Quencher")(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0,)
    }

    @Test
    fun `use indexes math to skip rows`() {
        val df = DataFrame.readExcel(testResource("repro.xls"), skipRows = 4)
        df.columnNames() shouldBe listOf("a")
        df.rowsCount() shouldBe 2
    }

    @Test
    fun `throw when there are no defined cells on header row`() {
        shouldThrow<IllegalStateException> {
            DataFrame.readExcel(testResource("xlsx6.xlsx"), skipRows = 4)
        }
    }

    @Test
    fun `write to new sheet when erase is false`() {
        val df = DataFrame.readExcel(testResource("sample4.xls"), skipRows = 6, rowsCount = 1)
        val fileLoc = testResource("generated_wb.xlsx").toURI().toString().removeRange(0, 6)
println(fileLoc)
        df.writeExcel(fileLoc, sheetName = "TestSheet1")
        df.writeExcel(fileLoc, sheetName = "TestSheet2")

        val testSheet1Df = DataFrame.readExcel(testResource("generated_wb.xlsx"), sheetName = "TestSheet1")
        val testSheet2Df = DataFrame.readExcel(testResource("generated_wb.xlsx"), sheetName = "TestSheet2")

        testSheet1Df.columnNames() shouldBe testSheet2Df.columnNames()
    }
}
