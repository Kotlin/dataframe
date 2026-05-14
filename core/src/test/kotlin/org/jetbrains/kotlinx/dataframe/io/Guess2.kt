package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
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

        DataFrame.readSource("../data/participants.json") shouldBe expected
        DataFrame.readSource(Path("../data/participants.json")) shouldBe expected
        DataFrame.readSource(File("../data/participants.json")) shouldBe expected
        DataFrame.readSource(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readSource("../data/participants.json", options) shouldBe expected
        DataFrame.readSource(Path("../data/participants.json"), options) shouldBe expected
        DataFrame.readSource(File("../data/participants.json"), options) shouldBe expected
        DataFrame.readSource(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read JSON in memory`() {
        val expected = DataFrame.readJson("../data/participants.json")

        val file = File("../data/participants.json")

        DataFrame.readSource(file.readText()) shouldBe expected
        DataFrame.readSource(file.inputStream()) shouldBe expected
        DataFrame.readSource(Json.decodeFromString<JsonElement>(file.readText())) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readSource(file.readText(), options) shouldBe expected
        DataFrame.readSource(file.inputStream(), options) shouldBe expected
        DataFrame.readSource(Json.decodeFromString<JsonElement>(file.readText()), options) shouldBe expected
    }

    @Test
    fun `read CSV reference`() {
        val csvPath = "../data/movies.csv"
        val expected = DataFrame.readCsv(csvPath)

        DataFrame.readSource(csvPath) shouldBe expected
        DataFrame.readSource(Path(csvPath)) shouldBe expected
        DataFrame.readSource(File(csvPath)) shouldBe expected
        DataFrame.readSource(
            Path(csvPath).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = Csv.Options(delimiter = ',')

        DataFrame.readSource(csvPath, options) shouldBe expected
        DataFrame.readSource(Path(csvPath), options) shouldBe expected
        DataFrame.readSource(File(csvPath), options) shouldBe expected
        DataFrame.readSource(
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

        DataFrame.readSource(file.readText(), options) shouldBe expected
        DataFrame.readSource(file.inputStream(), options) shouldBe expected
    }

    @Test
    fun `read TSV reference`() {
        val tsvFile = File("src/test/resources/abc.tsv")
        val expected = DataFrame.readTsv(tsvFile)

        DataFrame.readSource(tsvFile.path) shouldBe expected
        DataFrame.readSource(Path(tsvFile.path)) shouldBe expected
        DataFrame.readSource(tsvFile) shouldBe expected
        DataFrame.readSource(
            Path(tsvFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = Tsv.Options(delimiter = '\t')

        DataFrame.readSource(tsvFile.path, options) shouldBe expected
        DataFrame.readSource(Path(tsvFile.path), options) shouldBe expected
        DataFrame.readSource(tsvFile, options) shouldBe expected
        DataFrame.readSource(
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
        DataFrame.readSource(tsvFile.readText(), options) shouldBe expected
        DataFrame.readSource(tsvFile.inputStream(), options) shouldBe expected
    }

    @Test
    fun `read XLSX reference`() {
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val expected = DataFrame.readExcel(xlsxFile)

        DataFrame.readSource(xlsxFile.path) shouldBe expected
        DataFrame.readSource(Path(xlsxFile.path)) shouldBe expected
        DataFrame.readSource(xlsxFile) shouldBe expected
        DataFrame.readSource(
            Path(xlsxFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = ExcelNEW.Options(sheetName = "Sheet1")

        DataFrame.readSource(xlsxFile.path, options) shouldBe expected
        DataFrame.readSource(Path(xlsxFile.path), options) shouldBe expected
        DataFrame.readSource(xlsxFile, options) shouldBe expected
        DataFrame.readSource(
            Path(xlsxFile.path).absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read XLS reference`() {
        val xlsFile = File("src/test/resources/sample.xls")
        val expected = DataFrame.readExcel(xlsFile)

        DataFrame.readSource(xlsFile.path) shouldBe expected
        DataFrame.readSource(Path(xlsFile.path)) shouldBe expected
        DataFrame.readSource(xlsFile) shouldBe expected
        DataFrame.readSource(
            Path(xlsFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected
    }

    @Test
    fun `read XLSX in memory`() {
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val expected = DataFrame.readExcel(xlsxFile)

        // Workbook and Sheet are exclusive to ExcelNEW, so type-based dispatch works without options.
        WorkbookFactory.create(xlsxFile.inputStream()).use { wb ->
            DataFrame.readSource(wb) shouldBe expected
            DataFrame.readSource(wb.getSheetAt(0)) shouldBe expected
        }

        val options = ExcelNEW.Options()

        // Binary streams have no extension and are accepted by every format,
        // so options are needed to pin ExcelNEW for the InputStream variant.
        DataFrame.readSource(xlsxFile.inputStream(), options) shouldBe expected

        WorkbookFactory.create(xlsxFile.inputStream()).use { wb ->
            DataFrame.readSource(wb, options) shouldBe expected
            DataFrame.readSource(wb.getSheetAt(0), options) shouldBe expected
        }
    }

    @Test
    fun `read XLS in memory`() {
        val xlsFile = File("src/test/resources/sample.xls")
        val expected = DataFrame.readExcel(xlsFile)

        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
            DataFrame.readSource(wb) shouldBe expected
        }
        DataFrame.readSource(xlsFile.inputStream()) shouldBe expected
        WorkbookFactory.create(xlsFile.inputStream()).use { wb ->
            DataFrame.readSource(wb) shouldBe expected
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
            DataFrame.readSource(conn, tableOpts) shouldBe expected
            DataFrame.readSource(conn, queryOpts) shouldBe expected

            // DbConnectionConfig as InMemory.
            val config = DbConnectionConfig(url = url)
            DataFrame.readSource(config, tableOpts) shouldBe expected
            DataFrame.readSource(config, queryOpts) shouldBe expected

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
            DataFrame.readSource(dataSource, tableOpts) shouldBe expected

            // ResultSet — no sqlQueryOrTableName needed; just dbType (or a Connection to derive it).
            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    DataFrame.readSource(
                        rs,
                        Jdbc2.Options(dbType = H2()),
                    ) shouldBe expected
                }
            }
            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    DataFrame.readSource(
                        rs,
                        Jdbc2.Options(resultSetConnection = conn),
                    ) shouldBe expected
                }
            }
        }
    }

    @Test
    fun `unified readSource auto-detects references vs content`() {
        // String that points to an existing file → routed through URL → JSON wins on extension
        val jsonExpected = DataFrame.readJson("../data/participants.json")
        DataFrame.readSource("../data/participants.json") shouldBe jsonExpected

        // Same idea for CSV/XLSX
        val csvExpected = DataFrame.readCsv("../data/movies.csv")
        DataFrame.readSource("../data/movies.csv") shouldBe csvExpected

        val xlsxExpected = DataFrame.readExcel(File("src/test/resources/sample2.xlsx"))
        DataFrame.readSource("src/test/resources/sample2.xlsx") shouldBe xlsxExpected

        // String that doesn't resolve to a file → treated as raw content (JSON content here)
        val file = File("../data/participants.json")
        DataFrame.readSource(file.readText()) shouldBe jsonExpected

        // Non-String types: still work, no special handling needed
        DataFrame.readSource(file) shouldBe jsonExpected
        DataFrame.readSource(Path("../data/participants.json")) shouldBe jsonExpected
    }

    @Test
    fun `read JDBC reference`() {
        val url = h2Url("guess2_ref")
        DriverManager.getConnection(url).use { conn -> seed(conn) }

        val config = DbConnectionConfig(url = url)
        val expected = DataFrame.readSqlTable(config, "Customer")
        val tableOpts = Jdbc2.Options(sqlQueryOrTableName = "Customer")

        DataFrame.readSource(config, tableOpts) shouldBe expected
        DataFrame.readSource(config, Jdbc2.Options(sqlQueryOrTableName = "SELECT * FROM Customer")) shouldBe expected
    }

    @Test
    fun `read schema via default fallback (file-based formats)`() {
        // JSON
        val jsonExpected = DataFrame.readJson("../data/participants.json").schema()
        DataFrameSchema.readSource(
            File("../data/participants.json"),
        ) shouldBe jsonExpected
        DataFrameSchema.readSource(
            "../data/participants.json",
        ) shouldBe jsonExpected

        // CSV
        val csvExpected = DataFrame.readCsv("../data/movies.csv").schema()
        DataFrameSchema.readSource(
            File("../data/movies.csv"),
        ) shouldBe csvExpected

        // TSV
        val tsvFile = File("src/test/resources/abc.tsv")
        val tsvExpected = DataFrame.readTsv(tsvFile).schema()
        DataFrameSchema.readSource(tsvFile) shouldBe tsvExpected

        // XLSX
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val xlsxExpected = DataFrame.readExcel(xlsxFile).schema()
        DataFrameSchema.readSource(xlsxFile) shouldBe xlsxExpected
    }

    @Test
    fun `read JDBC schema via override`() {
        val url = h2Url("guess2_schema")
        DriverManager.getConnection(url).use { conn ->
            seed(conn)
            val expected = DataFrameSchema.readSqlTable(conn, "Customer")
            val tableOpts = Jdbc2.Options(sqlQueryOrTableName = "Customer")
            val queryOpts = Jdbc2.Options(sqlQueryOrTableName = "SELECT * FROM Customer")

            DataFrameSchema.readSource(conn, tableOpts) shouldBe expected
            DataFrameSchema.readSource(conn, queryOpts) shouldBe expected

            val config = DbConnectionConfig(url = url)
            DataFrameSchema.readSource(config, tableOpts) shouldBe expected
        }
    }

    @Test
    fun `read JDBC schema from ResultSet does not advance cursor`() {
        val url = h2Url("guess2_rs_schema")
        DriverManager.getConnection(url).use { conn ->
            seed(conn)

            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    // Schema-from-ResultSet uses JDBC metadata only — no rows are fetched, so the
                    // cursor stays at "before first row". (And nullability comes from the column metadata,
                    // which is conservatively nullable for columns without NOT NULL constraints; this is
                    // why we don't compare against the data-inferred schema directly.)
                    val expected = DataFrameSchema.readResultSet(
                        conn.prepareStatement("SELECT * FROM Customer").executeQuery(),
                        H2(),
                    )
                    val schema = DataFrameSchema.readSource(rs, Jdbc2.Options(dbType = H2()))
                    schema shouldBe expected
                    rs.isBeforeFirst shouldBe true
                }
            }
        }
    }
}
