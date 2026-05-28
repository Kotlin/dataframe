package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.Json.WriteOptions
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
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

        val readOptions = org.jetbrains.kotlinx.dataframe.io.Json.ReadOptions(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readSource("../data/participants.json", readOptions) shouldBe expected
        DataFrame.readSource(Path("../data/participants.json"), readOptions) shouldBe expected
        DataFrame.readSource(File("../data/participants.json"), readOptions) shouldBe expected
        DataFrame.readSource(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
            readOptions,
        ) shouldBe expected
    }

    @Test
    fun `read JSON in memory`() {
        val expected = DataFrame.readJson("../data/participants.json")

        val file = File("../data/participants.json")

        DataFrame.readSource(file.readText()) shouldBe expected
        DataFrame.readSource(file.inputStream()) shouldBe expected
        DataFrame.readSource(Json.decodeFromString<JsonElement>(file.readText())) shouldBe expected

        val readOptions = org.jetbrains.kotlinx.dataframe.io.Json.ReadOptions(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readSource(file.readText(), readOptions) shouldBe expected
        DataFrame.readSource(file.inputStream(), readOptions) shouldBe expected
        DataFrame.readSource(Json.decodeFromString<JsonElement>(file.readText()), readOptions) shouldBe expected
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

        val options = Csv.ReadOptions(delimiter = ',')

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
        val options = Csv.ReadOptions(delimiter = ',')

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

        val options = Tsv.ReadOptions(delimiter = '\t')

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
        val options = Tsv.ReadOptions(delimiter = '\t')

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

        val options = ExcelNEW.ReadOptions(sheetName = "Sheet1")

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

        val options = ExcelNEW.ReadOptions()

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
            val tableOpts = Jdbc2.ReadOptions(sqlQueryOrTableName = "Customer")
            val queryOpts = Jdbc2.ReadOptions(sqlQueryOrTableName = "SELECT * FROM Customer")

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
                        Jdbc2.ReadOptions(dbType = H2()),
                    ) shouldBe expected
                }
            }
            conn.prepareStatement("SELECT * FROM Customer").use { ps ->
                ps.executeQuery().use { rs ->
                    DataFrame.readSource(
                        rs,
                        Jdbc2.ReadOptions(resultSetConnection = conn),
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
        val tableOpts = Jdbc2.ReadOptions(sqlQueryOrTableName = "Customer")

        DataFrame.readSource(config, tableOpts) shouldBe expected
        DataFrame.readSource(config, Jdbc2.ReadOptions(sqlQueryOrTableName = "SELECT * FROM Customer")) shouldBe expected
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
            val tableOpts = Jdbc2.ReadOptions(sqlQueryOrTableName = "Customer")
            val queryOpts = Jdbc2.ReadOptions(sqlQueryOrTableName = "SELECT * FROM Customer")

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
                    val schema = DataFrameSchema.readSource(rs, Jdbc2.ReadOptions(dbType = H2()))
                    schema shouldBe expected
                    rs.isBeforeFirst shouldBe true
                }
            }
        }
    }

    @Test
    fun `read Arrow Feather reference`() {
        val featherFile = File("src/test/resources/test.feather")
        val expected = DataFrame.readArrowFeather(featherFile)

        DataFrame.readSource(featherFile.path) shouldBe expected
        DataFrame.readSource(Path(featherFile.path)) shouldBe expected
        DataFrame.readSource(featherFile) shouldBe expected
        DataFrame.readSource(
            Path(featherFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = ArrowFeatherNEW.ReadOptions()

        DataFrame.readSource(featherFile.path, options) shouldBe expected
        DataFrame.readSource(featherFile, options) shouldBe expected
    }

    @Test
    fun `read Arrow Feather in memory`() {
        val featherFile = File("src/test/resources/test.feather")
        val expected = DataFrame.readArrowFeather(featherFile)
        val options = ArrowFeatherNEW.ReadOptions()

        // ByteArray, InputStream, SeekableByteChannel all need options to disambiguate (no extension).
        DataFrame.readSource(featherFile.readBytes(), options) shouldBe expected
        DataFrame.readSource(featherFile.inputStream(), options) shouldBe expected
        java.nio.file.Files.newByteChannel(featherFile.toPath()).use { channel ->
            DataFrame.readSource<java.nio.channels.SeekableByteChannel>(channel, options) shouldBe expected
        }
    }

    @Test
    fun `read Arrow IPC reference`() {
        val ipcFile = File("src/test/resources/test.arrow")
        val expected = DataFrame.readArrowIPC(ipcFile)

        DataFrame.readSource(ipcFile.path) shouldBe expected
        DataFrame.readSource(Path(ipcFile.path)) shouldBe expected
        DataFrame.readSource(ipcFile) shouldBe expected
        DataFrame.readSource(
            Path(ipcFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = ArrowIPC.ReadOptions()
        DataFrame.readSource(ipcFile, options) shouldBe expected
    }

    @Test
    fun `read Parquet reference`() {
        val parquetFile = File("src/test/resources/test.parquet")
        val expected = DataFrame.readParquet(parquetFile)

        DataFrame.readSource(parquetFile.path) shouldBe expected
        DataFrame.readSource(Path(parquetFile.path)) shouldBe expected
        DataFrame.readSource(parquetFile) shouldBe expected
        DataFrame.readSource(
            Path(parquetFile.path).absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = Parquet.ReadOptions()
        DataFrame.readSource(parquetFile, options) shouldBe expected
    }

    @Test
    fun `read OpenAPI yaml as code`() {
        val openApiFile = File("src/test/resources/petstore.yaml")

        // The reference call from the existing helper, used as the ground truth.
        val expected = readOpenApiAsString(
            openApiAsString = openApiFile.readText(),
            name = "Petstore",
            extensionProperties = false,
            generateHelperCompanionObject = false,
        )

        // String path / File / Path / URL all route through readSourceImpl to OpenApi2.
        CodeString.readSource(openApiFile.path, name = "Petstore").value shouldBe expected
        CodeString.readSource(openApiFile, name = "Petstore").value shouldBe expected
        CodeString.readSource(Path(openApiFile.path), name = "Petstore").value shouldBe expected
        CodeString.readSource(
            Path(openApiFile.path).absolute().normalize().toUri().toURL(),
            name = "Petstore",
        ).value shouldBe expected

        // String content path (raw spec text) also works.
        CodeString.readSource(openApiFile.readText(), name = "Petstore").value shouldBe expected
    }

    @Test
    fun `OpenAPI does not steal plain JSON DataFrame reads`() {
        // A regular JSON file (not an OpenAPI spec) still goes to Json, even though OpenApi2 runs first.
        // OpenApi2.readDataSchemaCode returns a failed Result for non-OpenAPI content, but more importantly
        // OpenApi2.readDataFrame returns a failed Result, so DataFrame reads fall through.
        val expected = DataFrame.readJson("../data/participants.json")
        DataFrame.readSource(File("../data/participants.json")) shouldBe expected
    }

    @Test
    fun `default DataSchema code generation works for JSON via interface default`() {
        // The interface default reads the schema and calls generateInterfaces — exercise it on a JSON file.
        val jsonFile = File("../data/participants.json")
        val schemaCode = CodeString.readSource(jsonFile, name = "Participants")
        // The output is non-empty and includes the marker name.
        schemaCode.value shouldContain "Participants"
    }

    // region DataRow.readSource — single-row inputs across formats

    @Test
    fun `read DataRow from CSV string`() {
        val csvText = "a,b,c\n1,2,3"
        val expected = DataFrame.readCsvStr(csvText).single()
        DataRow.readSource(csvText, Csv.ReadOptions()) shouldBe expected
    }

    @Test
    fun `read DataRow from TSV string`() {
        val tsvText = "a\tb\tc\n1\t2\t3"
        val expected = DataFrame.readTsvStr(tsvText).single()
        DataRow.readSource(tsvText, Tsv.ReadOptions()) shouldBe expected
    }

    @Test
    fun `read DataRow from JSON string`() {
        // A single-element JSON array yields a one-row DataFrame.
        val jsonText = """[{"a": 1, "b": 2}]"""
        val expected = DataFrame.readJsonStr(jsonText).single()
        DataRow.readSource(jsonText) shouldBe expected
    }

    @Test
    fun `read DataRow from single-row XLSX file`() {
        // sample2.xlsx has exactly one data row.
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val expected = DataFrame.readExcel(xlsxFile).single()
        DataRow.readSource(xlsxFile) shouldBe expected
    }

    @Test
    fun `read DataRow from JDBC with single-row query`() {
        val url = h2Url("guess2_datarow")
        DriverManager.getConnection(url).use { conn ->
            seed(conn)
            val query = "SELECT * FROM Customer WHERE id = 1"
            val expected = DataFrame.readSqlQuery(conn, query).single()
            DataRow.readSource(conn, Jdbc2.ReadOptions(sqlQueryOrTableName = query)) shouldBe expected
        }
    }

    @Test
    fun `read DataRow throws when source has multiple rows`() {
        // movies.csv has many rows — DataRow.single() should fail, surfaced as the framework's
        // "Unknown DataRow source" since the exception is caught and converted.
        val movies = File("../data/movies.csv")
        try {
            DataRow.readSource(movies)
            error("Expected DataRow.readSource to fail on a multi-row CSV")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }

    // endregion

    // region convert API integration — convert { col }.to<DataFrame/DataRow/DataFrameSchema>()
    //
    // Frame columns are typed by their schema, so each column being converted must contain sources of the
    // same shape. Mixing, say, a CSV-shaped source and a JSON-shaped source in the same column would yield
    // a FrameColumn with no coherent single schema — these tests keep each column homogeneous and put
    // differently-shaped sources into separate columns.

    @Test
    fun `convert column of CSV files to DataFrame`() {
        // Two cells, both pointing at the same CSV → uniform shape in the resulting FrameColumn.
        val csvFile = File("../data/movies.csv")
        val df = dataFrameOf("source")(csvFile, csvFile)

        val converted = df.convert("source").to<DataFrame<*>>()

        val expected = DataFrame.readCsv(csvFile)
        converted["source"][0] shouldBe expected
        converted["source"][1] shouldBe expected
    }

    @Test
    fun `convert column of CSV files to DataFrameSchema`() {
        val csvFile = File("../data/movies.csv")
        val df = dataFrameOf("source")(csvFile, csvFile)

        val converted = df.convert("source").to<DataFrameSchema>()

        val expected = DataFrame.readCsv(csvFile).schema()
        converted["source"][0] shouldBe expected
        converted["source"][1] shouldBe expected
    }

    @Test
    fun `convert column of single-row XLSX files to DataRow`() {
        // sample2.xlsx has exactly one data row, so .to<DataRow<*>>() works for each cell.
        val xlsxFile = File("src/test/resources/sample2.xlsx")
        val df = dataFrameOf("source")(xlsxFile, xlsxFile)

        val converted = df.convert("source").to<DataRow<*>>()

        val expected = DataFrame.readExcel(xlsxFile).single()
        converted["source"][0] shouldBe expected
        converted["source"][1] shouldBe expected
    }

    @Test
    fun `convert column of String content to DataFrame`() {
        // Multiple parallel JSON content strings (same shape) → uniform FrameColumn.
        val text = """[{"a": 1, "b": 2}]"""
        val df = dataFrameOf("source")(text, text)

        val converted = df.convert("source").to<DataFrame<*>>()

        val expected = DataFrame.readJsonStr(text)
        converted["source"][0] shouldBe expected
        converted["source"][1] shouldBe expected
    }

    @Test
    fun `convert two homogeneous source columns at once`() {
        // Each column is internally uniform: csvCol has CSV-shaped cells, jsonCol has JSON-shaped cells.
        // The result is two FrameColumns, each with its own coherent schema.
        val csvFile = File("../data/movies.csv")
        val jsonFile = File("../data/participants.json")
        val df = dataFrameOf("csvCol", "jsonCol")(csvFile, jsonFile, csvFile, jsonFile)

        val converted = df.convert("csvCol", "jsonCol").to<DataFrame<*>>()

        val expectedCsv = DataFrame.readCsv(csvFile)
        val expectedJson = DataFrame.readJson(jsonFile)
        converted["csvCol"][0] shouldBe expectedCsv
        converted["csvCol"][1] shouldBe expectedCsv
        converted["jsonCol"][0] shouldBe expectedJson
        converted["jsonCol"][1] shouldBe expectedJson
    }

    @Test
    fun `convert column of URLs to DataFrame`() {
        // Two URLs pointing at the same JSON file → uniform schema in the FrameColumn.
        val jsonUrl = File("../data/participants.json").toURI().toURL()
        val urls = columnOf(jsonUrl, jsonUrl) named "source"
        val df = urls.toDataFrame()

        val converted = df.convert("source").to<DataFrame<*>>()
        val expected = DataFrame.readJson(jsonUrl)
        converted["source"][0] shouldBe expected
        converted["source"][1] shouldBe expected
    }

    // endregion

    // region DataFrame.write / DataRow.write — write to various JSON targets

    @Test
    fun `write DataFrame as JSON to Path`() {
        val df = DataFrame.readJson("../data/participants.json")
        val tempPath = Files.createTempFile("guess2-write-df", ".json")
            .also { it.toFile().deleteOnExit() }
        df.write(tempPath)
        DataFrame.readJson(tempPath) shouldBe df
    }

    @Test
    fun `write DataFrame as JSON to File`() {
        val df = DataFrame.readJson("../data/participants.json")
        val tempFile = Files.createTempFile("guess2-write-df", ".json").toFile()
            .also { it.deleteOnExit() }
        df.write(tempFile)
        DataFrame.readJson(tempFile) shouldBe df
    }

    @Test
    fun `write DataFrame as JSON to String pointing at existing file`() {
        // doStringToPathConversion in writeTargetImpl only fires when the path already exists;
        // createTempFile creates the file, so the String → Path routing kicks in.
        val df = DataFrame.readJson("../data/participants.json")
        val tempFile = Files.createTempFile("guess2-write-df-str", ".json").toFile()
            .also { it.deleteOnExit() }
        df.write(tempFile.path)
        DataFrame.readJson(tempFile) shouldBe df
    }

    @Test
    fun `write DataFrame as JSON to Appendable`() {
        val df = DataFrame.readJson("../data/participants.json")
        val sb = StringBuilder()
        // StringBuilder is reified — pin Appendable so the framework dispatches to that branch.
        df.write(sb)
        DataFrame.readJsonStr(sb.toString()) shouldBe df
    }

    @Test
    fun `write DataFrame as JSON to OutputStream`() {
        val df = DataFrame.readJson("../data/participants.json")
        val baos = ByteArrayOutputStream()
        df.write(baos)
        DataFrame.readJsonStr(baos.toString()) shouldBe df
    }

    @Test
    fun `write DataFrame as JSON to Function1 of JsonArray`() {
        val df = DataFrame.readJson("../data/participants.json")
        var captured: JsonArray? = null
        df.write({ it: JsonArray -> captured = it })
        captured shouldBe df.toJsonElement()
    }

    @Test
    fun `write DataFrame as JSON to Function1 of String`() {
        val df = DataFrame.readJson("../data/participants.json")
        var captured: String? = null
        df.write({ it: String -> captured = it })
        captured shouldBe df.toJson()
    }

    @Test
    fun `write DataFrame as JSON to Function1 of JsonObject fails`() {
        // A DataFrame can only be converted to a JsonArray, not a JsonObject.
        val df = DataFrame.readJson("../data/participants.json")
        shouldThrow<IllegalArgumentException> { df.write({ _: JsonObject -> }) }
    }

    @Test
    fun `write DataRow as JSON to Path`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        val tempPath = Files.createTempFile("guess2-write-row", ".json")
            .also { it.toFile().deleteOnExit() }
        row.write(tempPath)
        DataRow.readJson(tempPath) shouldBe row
    }

    @Test
    fun `write DataRow as JSON to File`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        val tempFile = Files.createTempFile("guess2-write-row", ".json").toFile()
            .also { it.deleteOnExit() }
        row.write(tempFile)
        DataRow.readJson(tempFile) shouldBe row
    }

    @Test
    fun `write DataRow as JSON to Appendable`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        val sb = StringBuilder()
        row.write(sb)
        sb.toString() shouldBe row.toJson()
    }

    @Test
    fun `write DataRow as JSON to OutputStream`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        val baos = ByteArrayOutputStream()
        row.write(baos)
        baos.toString() shouldBe row.toJson()
    }

    @Test
    fun `write DataRow as JSON to Function1 of JsonObject`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        var captured: JsonObject? = null
        row.write({ it: JsonObject -> captured = it })
        captured shouldBe row.toJsonElement()
    }

    @Test
    fun `write DataRow as JSON to Function1 of String`() {
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        var captured: String? = null
        row.write({ it: String -> captured = it })
        captured shouldBe row.toJson()
    }

    @Test
    fun `write DataRow as JSON to Function1 of JsonArray fails`() {
        // A single DataRow can only be turned into a JsonObject, not a JsonArray.
        val row = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""").single()
        shouldThrow<IllegalArgumentException> { row.write({ _: JsonArray -> }) }
    }

    @Test
    fun `write DataFrame as JSON with prettyPrint option produces multi-line output`() {
        val df = DataFrame.readJsonStr("""[{"a": 1, "b": "x"}]""")
        val sb = StringBuilder()
        df.write(sb, WriteOptions(prettyPrint = true))
        sb.toString() shouldContain "\n"
        DataFrame.readJsonStr(sb.toString()) shouldBe df
    }

    @Test
    fun `write DataFrame with unsupported target type fails`() {
        // Int is not a supported writing type for any registered format → no format accepts it,
        // and writeTargetImpl reports "Failed to find a suitable format".
        val df = DataFrame.readJsonStr("""[{"a": 1}]""")
        shouldThrow<IllegalStateException> { df.write(42) }
    }

    // endregion
}
