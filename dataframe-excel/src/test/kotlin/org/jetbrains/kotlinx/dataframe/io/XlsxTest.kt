package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toInt
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.net.URL
import java.nio.file.Files
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class XlsxTest {

    fun testResource(resourcePath: String): URL = this::class.java.classLoader.getResource(resourcePath)!!

    @Test
    fun `numerical columns`() {
        DataFrame.readExcel(testResource("sample.xls"), "Sheet1") shouldBe
            dataFrameOf("col1", "col2")(1.0, 2.0)
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
    fun `column with empty header and with formatting`() {
        val df = DataFrame.readExcel(
            testResource("sample2.xlsx"),
            "Sheet1",
            columns = "A:C",
            stringColumns = StringColumns("A:C"),
            parseEmptyAsNull = false,
        )
        df shouldBe dataFrameOf("col1", "col2", "C")("1", "", "3")
    }

    @Test
    fun `limit row number`() {
        val df = DataFrame.readExcel(testResource("sample4.xls"), "Sheet1", rowsCount = 5)
        val column = List(5) { (it + 1).toDouble() }.toColumn("col1")
        df shouldBe dataFrameOf(column)
    }

    @Test
    fun `first sheet is default sheet`() {
        DataFrame.readExcel(testResource("sample.xls"), "Sheet1") shouldBe
            DataFrame.readExcel(testResource("sample.xls"))
    }

    @Test
    fun `read and write are isomorphic for string, double and null values`() {
        val temp = Files.createTempFile("excel", ".xlsx").toFile()
        val df = dataFrameOf("col1", "col2")(
            "string value", 3.2,
            "string value 1", null,
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
        df.columnNames() shouldBe listOf(
            "Well",
            "Well Position",
            "Omit",
            "Sample Name",
            "Target Name",
            "Task",
            "Reporter",
            "Quencher",
        )
        df shouldBe dataFrameOf(
            "Well",
            "Well Position",
            "Omit",
            "Sample Name",
            "Target Name",
            "Task",
            "Reporter",
            "Quencher",
        )(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
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
    fun `write to new sheet when keepFile is true`() {
        val names = (1..5).map { "column$it" }
        val df = dataFrameOf(names).randomDouble(7)
        val fileLoc = Files.createTempFile("generated_wb", ".xlsx").toFile()

        df.writeExcel(fileLoc, sheetName = "TestSheet1")
        df.writeExcel(fileLoc, sheetName = "TestSheet2", keepFile = true)

        val testSheet1Df = DataFrame.readExcel(fileLoc, sheetName = "TestSheet1")
        val testSheet2Df = DataFrame.readExcel(fileLoc, sheetName = "TestSheet2")

        testSheet1Df.columnNames() shouldBe testSheet2Df.columnNames()
    }

    @Test
    fun `read xlsx file with duplicated columns and repair column names`() {
        shouldThrow<DuplicateColumnNamesException> {
            DataFrame.readExcel(testResource("iris_duplicated_column.xlsx"))
        }

        val df = DataFrame.readExcel(
            testResource("iris_duplicated_column.xlsx"),
            nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
        )
        df.columnNames() shouldBe
            listOf(
                "Sepal.Length",
                "Sepal.Width",
                "C",
                "Petal.Length",
                "Petal.Width",
                "Species",
                "Other.Width",
                "Species1",
                "I",
                "Other.Width1",
                "Species2",
            )
    }

    @Test
    fun `read xlsx file that has cells with formulas that return numbers and strings`() {
        val df = DataFrame.readExcel(testResource("formula_cell.xlsx"))
        df.columnNames() shouldBe listOf("Number", "Greater than 5", "Multiplied by 10", "Divided by 5")
    }

    @Test
    fun `read mixed column`() {
        val df = DataFrame.readExcel(
            testResource("mixed_column.xlsx"),
            stringColumns = StringColumns("A"),
        )
        df["col1"].type() shouldBe typeOf<String>()
        df shouldBe dataFrameOf("col1")("100", "A100", "B100", "C100")
    }

    @Test
    fun `read with default header unstructured excel file`() {
        val df = DataFrame.readExcel(
            testResource("unstructured_example.xlsx"),
            firstRowIsHeader = false,
        )
        df.columnNames() shouldBe listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    }

    @Test
    fun `should work read with default header unstructured excel file with skipRow params`() {
        val df = DataFrame.readExcel(
            testResource("unstructured_example.xlsx"),
            firstRowIsHeader = false,
            skipRows = 2,
            rowsCount = 1,
            parseEmptyAsNull = false,
        )

        df shouldBe dataFrameOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        )(
            "Field 3: ", "", "TEAM 1", "", "", "", "", "Staff Code:", "Staff 1", "",
        )
    }

    @Test
    fun `read columns with nulls`() {
        val df = DataFrame.readExcel(
            testResource("withNulls.xlsx"),
        ).convert("age").toInt()
        df shouldBe dataFrameOf(
            "name" to listOf("Alice", null, "Bob"),
            "age" to listOf(23, 27, null),
        )
        df["name"].type shouldBe typeOf<String?>()
        df["age"].type shouldBe typeOf<Int?>()
    }
}
