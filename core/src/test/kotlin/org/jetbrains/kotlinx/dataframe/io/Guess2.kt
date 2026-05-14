package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

class Guess2 {

    @Test
    fun `read JSON reference`() {
        val expected = DataFrame.readJson("../data/participants.json")

        DataFrame.readReference("../data/participants.json") shouldBe expected
        DataFrame.readReference(Path("../data/participants.json")) shouldBe expected
        DataFrame.readReference(File("../data/participants.json")) shouldBe expected
        DataFrame.readReference(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readReference("../data/participants.json", options) shouldBe expected
        DataFrame.readReference(Path("../data/participants.json"), options) shouldBe expected
        DataFrame.readReference(File("../data/participants.json"), options) shouldBe expected
        DataFrame.readReference(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read JSON in memory`() {
        val expected = DataFrame.readJson("../data/participants.json")

        val file = File("../data/participants.json")

        DataFrame.readFromData(file.readText()) shouldBe expected
        DataFrame.readFromData(file.inputStream()) shouldBe expected
        DataFrame.readFromData(Json.decodeFromString<JsonElement>(file.readText())) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readFromData(file.readText(), options) shouldBe expected
        DataFrame.readFromData(file.inputStream(), options) shouldBe expected
        DataFrame.readFromData(Json.decodeFromString<JsonElement>(file.readText()), options) shouldBe expected
    }

    @Test
    fun `read CSV reference`() {
        val csvPath = "../data/movies.csv"
        val expected = DataFrame.readCsv(csvPath)

        DataFrame.readReference(csvPath) shouldBe expected
        DataFrame.readReference(Path(csvPath)) shouldBe expected
        DataFrame.readReference(File(csvPath)) shouldBe expected
        DataFrame.readReference(
            Path(csvPath).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = Csv.Options(delimiter = ',')

        DataFrame.readReference(csvPath, options) shouldBe expected
        DataFrame.readReference(Path(csvPath), options) shouldBe expected
        DataFrame.readReference(File(csvPath), options) shouldBe expected
        DataFrame.readReference(
            Path(csvPath).absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read CSV in memory`() {
        val file = File("../data/movies.csv")
        val expected = DataFrame.readCsv(file)

        // String content has no extension hint, so we pin the format via options.
        val options = Csv.Options(delimiter = ',')

        DataFrame.readFromData(file.readText(), options) shouldBe expected
        DataFrame.readFromData(file.inputStream(), options) shouldBe expected
    }

    @Test
    fun `read TSV reference`() {
        val tsvFile = File("src/test/resources/abc.tsv")
        val expected = DataFrame.readTsv(tsvFile)

        DataFrame.readReference(tsvFile.path) shouldBe expected
        DataFrame.readReference(Path(tsvFile.path)) shouldBe expected
        DataFrame.readReference(tsvFile) shouldBe expected
        DataFrame.readReference(
            Path(tsvFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = Tsv.Options(delimiter = '\t')

        DataFrame.readReference(tsvFile.path, options) shouldBe expected
        DataFrame.readReference(Path(tsvFile.path), options) shouldBe expected
        DataFrame.readReference(tsvFile, options) shouldBe expected
        DataFrame.readReference(
            Path(tsvFile.path).absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read TSV in memory`() {
        val tsvFile = File("src/test/resources/abc.tsv")
        val expected = DataFrame.readTsv(tsvFile)
        val options = Tsv.Options(delimiter = '\t')

        // Binary/text without extension — options pin Tsv over Csv/Json/Xlsx.
        DataFrame.readFromData(tsvFile.readText(), options) shouldBe expected
        DataFrame.readFromData(tsvFile.inputStream(), options) shouldBe expected
    }

    @Test
    fun `read XLSX reference`() {
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val expected = DataFrame.readExcel(xlsxFile)

        DataFrame.readReference(xlsxFile.path) shouldBe expected
        DataFrame.readReference(Path(xlsxFile.path)) shouldBe expected
        DataFrame.readReference(xlsxFile) shouldBe expected
        DataFrame.readReference(
            Path(xlsxFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = ExcelNEW.Options(sheetName = "Sheet1")

        DataFrame.readReference(xlsxFile.path, options) shouldBe expected
        DataFrame.readReference(Path(xlsxFile.path), options) shouldBe expected
        DataFrame.readReference(xlsxFile, options) shouldBe expected
        DataFrame.readReference(
            Path(xlsxFile.path).absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read XLS reference`() {
        val xlsFile = File("src/test/resources/sample.xls")
        val expected = DataFrame.readExcel(xlsFile)

        DataFrame.readReference(xlsFile.path) shouldBe expected
        DataFrame.readReference(Path(xlsFile.path)) shouldBe expected
        DataFrame.readReference(xlsFile) shouldBe expected
        DataFrame.readReference(
            Path(xlsFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected
    }

    @Test
    fun `read XLSX in memory`() {
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val expected = DataFrame.readExcel(xlsxFile)

        // Workbook and Sheet are exclusive to ExcelNEW, so type-based dispatch works without options.
        WorkbookFactory.create(xlsxFile.inputStream()).use { wb ->
            DataFrame.readFromData(wb) shouldBe expected
            DataFrame.readFromData(wb.getSheetAt(0)) shouldBe expected
        }

        val options = ExcelNEW.Options()

        // Binary streams have no extension and are accepted by every format,
        // so options are needed to pin ExcelNEW for the InputStream variant.
        DataFrame.readFromData(xlsxFile.inputStream(), options) shouldBe expected

        WorkbookFactory.create(xlsxFile.inputStream()).use { wb ->
            DataFrame.readFromData(wb, options) shouldBe expected
            DataFrame.readFromData(wb.getSheetAt(0), options) shouldBe expected
        }
    }

    @Test
    fun `read XLS in memory`() {
        val xlsFile = File("src/test/resources/sample.xls")
        val expected = DataFrame.readExcel(xlsFile)

//        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
//            DataFrame.readFromData(wb) shouldBe expected
//        }

        val options = ExcelNEW.Options()

        DataFrame.readFromData(xlsFile.inputStream()) shouldBe expected

//        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
//            DataFrame.readFromData(wb) shouldBe expected
//        }
    }
}
