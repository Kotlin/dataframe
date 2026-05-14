package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.junit.Test
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource
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

        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
            DataFrame.readFromData(wb) shouldBe expected
        }
        DataFrame.readFromData(xlsFile.inputStream()) shouldBe expected
        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
            DataFrame.readFromData(wb) shouldBe expected
        }
    }

    private fun h2Url(name: String) = "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"

    private fun seed(connection: Connection) {
        connection.createStatement().use { st ->
            st.execute("CREATE TABLE Customer (id INT, name VARCHAR(255), age INT)")
            st.execute("INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40), (2, 'Alice', 25), (3, 'Bob', 47)")
        }
    }

    @Test
    fun `read JDBC in memory`() {
        val url = h2Url("guess2_inmem")
        DriverManager.getConnection(url).use { conn ->
            seed(conn)

            val expected = DataFrame.readSqlTable(conn, "Customer")
            val tableOpts = Jdbc2.Options(sqlQueryOrTableName = "Customer")
            val queryOpts = Jdbc2.Options(sqlQueryOrTableName = "SELECT * FROM Customer")

            // Connection — exclusive type, but query/table name must come from options.
            DataFrame.readFromData(conn, tableOpts) shouldBe expected
            DataFrame.readFromData(conn, queryOpts) shouldBe expected

            // DbConnectionConfig as InMemory.
            val config = DbConnectionConfig(url = url)
            DataFrame.readFromData(config, tableOpts) shouldBe expected
            DataFrame.readFromData(config, queryOpts) shouldBe expected

            // DataSource — opens a fresh connection each call (DataSource.readDataFrame closes it via `use`).
            val dataSource = object : DataSource {
                override fun getConnection() = DriverManager.getConnection(url)
                override fun getConnection(u: String?, p: String?) = DriverManager.getConnection(url)
                override fun getLogWriter() = null
                override fun setLogWriter(out: java.io.PrintWriter?) {}
                override fun setLoginTimeout(seconds: Int) {}
                override fun getLoginTimeout() = 0
                override fun getParentLogger() = throw UnsupportedOperationException()
                override fun <T : Any?> unwrap(iface: Class<T>?): T = throw UnsupportedOperationException()
                override fun isWrapperFor(iface: Class<*>?) = false
            }
            DataFrame.readFromData(dataSource, tableOpts) shouldBe expected

            // ResultSet — no sqlQueryOrTableName needed; just dbType (or a Connection to derive it).
            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    DataFrame.readFromData(
                        rs,
                        Jdbc2.Options(dbType = H2()),
                    ) shouldBe expected
                }
            }
            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    DataFrame.readFromData(
                        rs,
                        Jdbc2.Options(resultSetConnection = conn),
                    ) shouldBe expected
                }
            }
        }
    }

    @Test
    fun `read JDBC reference`() {
        val url = h2Url("guess2_ref")
        DriverManager.getConnection(url).use { conn -> seed(conn) }

        val config = DbConnectionConfig(url = url)
        val expected = DataFrame.readSqlTable(config, "Customer")
        val tableOpts = Jdbc2.Options(sqlQueryOrTableName = "Customer")

        DataFrame.readReference(config, tableOpts) shouldBe expected
        DataFrame.readReference(config, Jdbc2.Options(sqlQueryOrTableName = "SELECT * FROM Customer")) shouldBe expected
    }
}
