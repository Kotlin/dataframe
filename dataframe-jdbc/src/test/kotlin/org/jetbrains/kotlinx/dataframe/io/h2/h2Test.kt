package org.jetbrains.kotlinx.dataframe.io.h2

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.io.db.H2.Mode
import org.jetbrains.kotlinx.dataframe.io.db.MySql
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.io.db.TableMetadata
import org.jetbrains.kotlinx.dataframe.io.db.driverClassNameFromUrl
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromUrl
import org.jetbrains.kotlinx.dataframe.io.inferNullability
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.jetbrains.kotlinx.dataframe.io.readDataFrameSchema
import org.jetbrains.kotlinx.dataframe.io.readResultSet
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.io.withReadOnlyConnection
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.typeOf

private const val URL = "jdbc:h2:mem:test5;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false"

private const val MAXIMUM_POOL_SIZE = 5

private const val QUERY_SELECT_ONE = "SELECT 1"

@DataSchema
interface Customer {
    val id: Int?
    val name: String?
    val age: Int?
}

@DataSchema
interface Sale {
    val id: Int?
    val customerId: Int?
    val amount: Double
}

@DataSchema
interface CustomerSales {
    val customerName: String?
    val totalSalesAmount: Double?
}

@DataSchema
interface TestTableData {
    val characterCol: String?
    val characterVaryingCol: String?
    val characterLargeObjectCol: String?
    val mediumTextCol: String?
    val varcharIgnoreCaseCol: String?
    val binaryCol: ByteArray?
    val binaryVaryingCol: ByteArray?
    val binaryLargeObjectCol: ByteArray?
    val booleanCol: Boolean?
    val tinyIntCol: Int?
    val smallIntCol: Int?
    val integerCol: Int?
    val bigIntCol: Long?
    val numericCol: BigDecimal?
    val realCol: Float?
    val doublePrecisionCol: Double?
    val decFloatCol: BigDecimal?
    val dateCol: String?
    val timeCol: String?
    val timeWithTimeZoneCol: String?
    val timestampCol: String?
    val timestampWithTimeZoneCol: String?
    val intervalCol: String?
    val javaObjectCol: Any?
    val enumCol: String?
    val jsonCol: String?
    val uuidCol: String?
}

class JdbcTest {
    companion object {
        private lateinit var connection: Connection
        private lateinit var dataSource: HikariDataSource

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            initializeConnection()
            initializeDataSource()
            createTablesAndData()
        }

        private fun initializeConnection() {
            connection = DriverManager.getConnection(URL)
        }

        private fun initializeDataSource() {
            val config = HikariConfig().apply {
                jdbcUrl = URL
                maximumPoolSize = MAXIMUM_POOL_SIZE
                minimumIdle = 2
            }
            dataSource = HikariDataSource(config)
        }

        private fun createTablesAndData() {
            // Create table Customer
            @Language("SQL")
            val createCustomerTableQuery = """
                CREATE TABLE Customer (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    age INT
                )
            """

            connection.createStatement().execute(createCustomerTableQuery)

            // Create table Sale
            @Language("SQL")
            val createSaleTableQuery = """
                CREATE TABLE Sale (
                    id INT PRIMARY KEY,
                    customerId INT,
                    amount DECIMAL(10, 2) NOT NULL
                )
            """

            connection.createStatement().execute(createSaleTableQuery)

            // add data to the Customer table
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (2, 'Alice', 25)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (3, 'Bob', 47)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (4, NULL, NULL)")

            // add data to the Sale table
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (1, 1, 100.50)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (2, 2, 50.00)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (3, 1, 75.25)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (4, 3, 35.15)")
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                dataSource.close()
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        // Helper assertion functions
        private fun assertCustomerData(df: AnyFrame, expectedRows: Int = 4) {
            val casted = df.cast<Customer>()
            casted.rowsCount() shouldBe expectedRows
            val expectedOlderThan30 = when (expectedRows) {
                4 -> 2
                2 -> 1
                else -> 1 // for 1 row or other small limits in tests
            }
            casted.filter {
                it[Customer::age] != null && it[Customer::age]!! > 30
            }.rowsCount() shouldBe expectedOlderThan30
            casted[0][1] shouldBe "John"
        }

        private fun assertCustomerSchema(schema: DataFrameSchema) {
            schema.columns.size shouldBe 3
            schema.columns["name"]!!.type shouldBe typeOf<String?>()
        }

        private fun assertCustomerSalesData(df: AnyFrame, expectedRows: Int = 2) {
            val casted = df.cast<CustomerSales>()
            casted.rowsCount() shouldBe expectedRows
            // In current tests, regardless of limit (2 or 1), the count of totalSalesAmount > 100 is 1
            casted.filter { it[CustomerSales::totalSalesAmount]!! > 100 }.rowsCount() shouldBe 1
            casted[0][0] shouldBe "John"
        }

        private fun assertCustomerSalesSchema(schema: DataFrameSchema) {
            schema.columns.size shouldBe 2
            schema.columns["name"]!!.type shouldBe typeOf<String?>()
        }

        private fun assertAllTablesData(dataFrameMap: Map<String, AnyFrame>) {
            dataFrameMap.containsKey("Customer") shouldBe true
            dataFrameMap.containsKey("Sale") shouldBe true

            val dataframes = dataFrameMap.values.toList()

            val customerDf = dataframes[0].cast<Customer>()
            customerDf.rowsCount() shouldBe 4
            customerDf.filter { it[Customer::age] != null && it[Customer::age]!! > 30 }.rowsCount() shouldBe 2
            customerDf[0][1] shouldBe "John"

            val saleDf = dataframes[1].cast<Sale>()
            saleDf.rowsCount() shouldBe 4
            saleDf.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 3
            (saleDf[0][2] as BigDecimal).compareTo(BigDecimal(100.50)) shouldBe 0
        }

        private fun assertAllTablesDataWithLimit(dataFrameMap: Map<String, AnyFrame>) {
            val dataframes = dataFrameMap.values.toList()

            val customerDf = dataframes[0].cast<Customer>()
            customerDf.rowsCount() shouldBe 1
            customerDf.filter { it[Customer::age] != null && it[Customer::age]!! > 30 }.rowsCount() shouldBe 1
            customerDf[0][1] shouldBe "John"

            val saleDf = dataframes[1].cast<Sale>()
            saleDf.rowsCount() shouldBe 1
            saleDf.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 1
            (saleDf[0][2] as BigDecimal).compareTo(BigDecimal(100.50)) shouldBe 0
        }

        private fun assertAllTablesSchema(dataFrameSchemaMap: Map<String, DataFrameSchema>) {
            dataFrameSchemaMap.containsKey("Customer") shouldBe true
            dataFrameSchemaMap.containsKey("Sale") shouldBe true

            val dataSchemas = dataFrameSchemaMap.values.toList()

            val customerDataSchema = dataSchemas[0]
            customerDataSchema.columns.size shouldBe 3
            customerDataSchema.columns["name"]!!.type shouldBe typeOf<String?>()

            val saleDataSchema = dataSchemas[1]
            saleDataSchema.columns.size shouldBe 3
            saleDataSchema.columns["amount"]!!.type shouldBe typeOf<BigDecimal>()
        }

        @Language("SQL")
        private val CUSTOMER_SALES_QUERY =
            """
            SELECT c.name as customerName, SUM(s.amount) as totalSalesAmount
            FROM Sale s
            INNER JOIN Customer c ON s.customerId = c.id
            WHERE c.age > 35
            GROUP BY s.customerId, c.name
            """.trimIndent()
    }

    // ========== Connection API Tests ==========

    @Test
    fun `read from empty table`() {
        @Language("SQL")
        val createTableQuery = """
                CREATE TABLE EmptyTestTable (
                    characterCol CHAR(10),
                    characterVaryingCol VARCHAR(20)
                )
            """

        connection.createStatement().execute(createTableQuery.trimIndent())

        val tableName = "EmptyTestTable"

        val df = DataFrame.readSqlTable(connection, tableName)
        df.rowsCount() shouldBe 0

        val dataSchema = DataFrameSchema.readSqlTable(connection, tableName)
        dataSchema.columns.size shouldBe 2
        dataSchema.columns["characterCol"]!!.type shouldBe typeOf<String?>()

        connection.createStatement().execute("DROP TABLE EmptyTestTable")
    }

    @Test
    fun `read from huge table`() {
        @Language("SQL")
        val createTableQuery = """
                CREATE TABLE TestTable (
                    characterCol CHAR(10),
                    characterVaryingCol VARCHAR(20),
                    characterLargeObjectCol CLOB,
                    mediumTextCol CLOB,
                    varcharIgnoreCaseCol VARCHAR_IGNORECASE(30),
                    binaryCol BINARY(8),
                    binaryVaryingCol VARBINARY(16),
                    binaryLargeObjectCol BLOB,
                    booleanCol BOOLEAN,
                    tinyIntCol TINYINT,
                    smallIntCol SMALLINT,
                    integerCol INT,
                    bigIntCol BIGINT,
                    numericCol NUMERIC(10, 2),
                    realCol REAL,
                    doublePrecisionCol DOUBLE PRECISION,
                    decFloatCol DECFLOAT(16),
                    dateCol DATE,
                    timeCol TIME,
                    timeWithTimeZoneCol TIME WITH TIME ZONE,
                    timestampCol TIMESTAMP,
                    timestampWithTimeZoneCol TIMESTAMP WITH TIME ZONE,
                    javaObjectCol OBJECT,
                    enumCol VARCHAR(10),
                    jsonCol JSON,
                    uuidCol UUID
                )
            """

        connection.createStatement().execute(createTableQuery.trimIndent())

        connection.prepareStatement(
            """
            INSERT INTO TestTable VALUES (
                'ABC', 'XYZ', 'Long text data for CLOB', 'Medium text data for CLOB',
                'Varchar IgnoreCase', X'010203', X'040506', X'070809',
                TRUE, 1, 100, 1000, 100000,
                123.45, 1.23, 3.14, 2.71,
                '2023-07-20', '08:30:00', '18:15:00', '2023-07-19 12:45:30',
                '2023-07-18 12:45:30', NULL,
                'Option1', '{"key": "value"}', '123e4567-e89b-12d3-a456-426655440000'
            )
            """.trimIndent(),
        ).executeUpdate()

        connection.prepareStatement(
            """
            INSERT INTO TestTable VALUES (
                'DEF', 'LMN', 'Another CLOB data', 'Different CLOB data',
                'Another Varchar', X'101112', X'131415', X'161718',
                FALSE, 2, 200, 2000, 200000,
                234.56, 2.34, 4.56, 3.14,
                '2023-07-21', '14:30:00', '22:45:00', '2023-07-20 18:15:30',
                '2023-07-19 18:15:30', NULL,
                'Option2', '{"key": "another_value"}', '234e5678-e89b-12d3-a456-426655440001'
            )
            """.trimIndent(),
        ).executeUpdate()

        connection.prepareStatement(
            """
            INSERT INTO TestTable VALUES (
                'GHI', 'OPQ', 'Third CLOB entry', 'Yet another CLOB data',
                'Yet Another Varchar', X'192021', X'222324', X'252627',
                TRUE, 3, 300, 3000, 300000,
                345.67, 3.45, 5.67, 4.71,
                '2023-07-22', '20:45:00', '03:30:00', '2023-07-21 23:45:15',
                '2023-07-20 23:45:15', NULL,
                'Option3', '{ "person": { "name": "John Doe", "age": 30 }, ' ||
                '"address": { "street": "123 Main St", "city": "Exampleville", "zipcode": "12345"}}', 
                '345e6789-e89b-12d3-a456-426655440002'
            )
            """.trimIndent(),
        ).executeUpdate()

        val tableName = "TestTable"
        val df = DataFrame.readSqlTable(connection, tableName).cast<TestTableData>()
        df.rowsCount() shouldBe 3
        df.filter { it[TestTableData::integerCol]!! > 1000 }.rowsCount() shouldBe 2

        // testing numeric columns
        val result = df.select("tinyIntCol")
            .add("tinyIntCol2") { it[TestTableData::tinyIntCol] }

        result[0][1] shouldBe 1

        val result1 = df.select("smallIntCol")
            .add("smallIntCol2") { it[TestTableData::smallIntCol] }

        result1[0][1] shouldBe 100

        val result2 = df.select("bigIntCol")
            .add("bigIntCol2") { it[TestTableData::bigIntCol] }

        result2[0][1] shouldBe 100000

        val result3 = df.select("numericCol")
            .add("numericCol2") { it[TestTableData::numericCol] }

        BigDecimal("123.45").compareTo(result3[0][1] as BigDecimal) shouldBe 0

        val result4 = df.select("realCol")
            .add("realCol2") { it[TestTableData::realCol] }

        result4[0][1] shouldBe 1.23f

        val result5 = df.select("doublePrecisionCol")
            .add("doublePrecisionCol2") { it[TestTableData::doublePrecisionCol] }

        result5[0][1] shouldBe 3.14

        val result6 = df.select("decFloatCol")
            .add("decFloatCol2") { it[TestTableData::decFloatCol] }

        BigDecimal("2.71").compareTo(result6[0][1] as BigDecimal) shouldBe 0

        val schema = DataFrameSchema.readSqlTable(connection, tableName)

        schema.columns["characterCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["tinyIntCol"]!!.type shouldBe typeOf<Int?>()
        schema.columns["smallIntCol"]!!.type shouldBe typeOf<Int?>()
        schema.columns["bigIntCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["numericCol"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["realCol"]!!.type shouldBe typeOf<Float?>()
        schema.columns["doublePrecisionCol"]!!.type shouldBe typeOf<Double?>()
        schema.columns["decFloatCol"]!!.type shouldBe typeOf<BigDecimal?>()

        connection.createStatement().execute("DROP TABLE $tableName")
    }

    @Test
    fun `read from table`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(connection, tableName)
        assertCustomerData(df)

        val df1 = DataFrame.readSqlTable(connection, tableName, 1)
        assertCustomerData(df1, 1)

        val dataSchema = DataFrameSchema.readSqlTable(connection, tableName)
        assertCustomerSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = DataFrame.readSqlTable(dbConfig, tableName)
        assertCustomerData(df2)

        val df3 = DataFrame.readSqlTable(dbConfig, tableName, 1)
        assertCustomerData(df3, 1)

        val dataSchema1 = DataFrameSchema.readSqlTable(dbConfig, tableName)
        assertCustomerSchema(dataSchema1)
    }

    @Test
    fun `read from table with extension functions`() {
        val tableName = "Customer"
        val df = connection.readDataFrame(tableName)
        assertCustomerData(df)

        val df1 = connection.readDataFrame(tableName, 1)
        assertCustomerData(df1, 1)

        val dataSchema = connection.readDataFrameSchema(tableName)
        assertCustomerSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = dbConfig.readDataFrame(tableName)
        assertCustomerData(df2)

        val df3 = dbConfig.readDataFrame(tableName, 1)
        assertCustomerData(df3, 1)

        val dataSchema1 = dbConfig.readDataFrameSchema(tableName)
        assertCustomerSchema(dataSchema1)
    }

    @Test
    fun `repeated read from table with limit`() {
        val tableName = "Customer"

        repeat(10) {
            val df1 = DataFrame.readSqlTable(connection, tableName, 2)
            assertCustomerData(df1, 2)

            val dbConfig = DbConnectionConfig(url = URL)
            val df2 = DataFrame.readSqlTable(dbConfig, tableName, 2)
            assertCustomerData(df2, 2)
        }
    }

    @Test
    fun `read from ResultSet`() {
        val dbType = H2(Mode.MySql)

        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).use { st ->
            @Language("SQL")
            val selectStatement = "SELECT * FROM Customer"

            st.executeQuery(selectStatement).use { rs ->
                val df = DataFrame.readResultSet(rs, dbType)
                assertCustomerData(df)

                rs.beforeFirst()

                val df1 = DataFrame.readResultSet(rs, dbType, 1)
                assertCustomerData(df1, 1)

                rs.beforeFirst()

                val dataSchema = DataFrameSchema.readResultSet(rs, dbType)
                assertCustomerSchema(dataSchema)

                rs.beforeFirst()

                val df2 = DataFrame.readResultSet(rs, connection)
                assertCustomerData(df2)

                rs.beforeFirst()

                val df3 = DataFrame.readResultSet(rs, connection, 1)
                assertCustomerData(df3, 1)

                rs.beforeFirst()

                val dataSchema1 = DataFrameSchema.readResultSet(rs, dbType)
                assertCustomerSchema(dataSchema1)
            }
        }
    }

    @Test
    fun `read from extension function on ResultSet`() {
        val dbType = H2(Mode.MySql)

        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).use { st ->
            @Language("SQL")
            val selectStatement = "SELECT * FROM Customer"

            st.executeQuery(selectStatement).use { rs ->
                val df = rs.readDataFrame(dbType)
                assertCustomerData(df)

                rs.beforeFirst()

                val df1 = rs.readDataFrame(dbType, 1)
                assertCustomerData(df1, 1)

                rs.beforeFirst()

                val dataSchema = rs.readDataFrameSchema(dbType)
                assertCustomerSchema(dataSchema)

                rs.beforeFirst()

                val df2 = rs.readDataFrame(connection)
                assertCustomerData(df2)

                rs.beforeFirst()

                val df3 = rs.readDataFrame(connection, 1)
                assertCustomerData(df3, 1)

                rs.beforeFirst()

                val dataSchema1 = rs.readDataFrameSchema(dbType)
                assertCustomerSchema(dataSchema1)
            }
        }
    }

    // to cover a reported case from https://github.com/Kotlin/dataframe/issues/494
    @Test
    fun `repeated read from ResultSet with limit`() {
        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).use { st ->
            @Language("SQL")
            val selectStatement = "SELECT * FROM Customer"

            st.executeQuery(selectStatement).use { rs ->
                repeat(10) {
                    rs.beforeFirst()

                    val df1 = DataFrame.readResultSet(rs, H2(Mode.MySql), 2)
                    assertCustomerData(df1, 2)

                    rs.beforeFirst()

                    val df2 = DataFrame.readResultSet(rs, connection, 2)
                    assertCustomerData(df2, 2)
                }
            }
        }
    }

    @Test
    fun `read from non-existing table`() {
        shouldThrow<IllegalStateException> {
            DataFrame.readSqlTable(connection, "WrongTableName").cast<Customer>()
        }
    }

    // to cover a reported case from https://github.com/Kotlin/dataframe/issues/498
    @Test
    fun `read from incorrect SQL query`() {
        @Language("SQL")
        val createSQL = """
            CREATE TABLE Orders (
            order_id INT PRIMARY KEY,
            customer_id INT,
            order_date DATE,
            total_amount DECIMAL(10, 2))
            """

        @Language("SQL")
        val dropSQL = """
            DROP TABLE Customer
            """

        @Language("SQL")
        val alterSQL = """
            ALTER TABLE Customer
            ADD COLUMN email VARCHAR(100)
            """

        @Language("SQL")
        val deleteSQL = """
            DELETE FROM Customer
            WHERE id = 1
            """

        @Language("SQL")
        val repeatedSQL = """
            SELECT * FROM Customer
            WHERE id = 1;
            SELECT * FROM Customer
            WHERE id = 1;
            """

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(connection, createSQL)
        }

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(connection, dropSQL)
        }

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(connection, alterSQL)
        }

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(connection, deleteSQL)
        }

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(connection, repeatedSQL)
        }
    }

    @Test
    fun `readFromTable should reject invalid table names to prevent SQL injections`() {
        // Invalid table names that attempt SQL injection
        val invalidTableNames = listOf(
            "Customer; DROP TABLE Customer", // Injection using semicolon
            "Sale -- Comment", // Injection using single-line comment
            "/* Multi-line comment */ Customer", // Injection using multi-line comment
            "Sale WHERE 1=1", // Injection using always-true condition
            "Sale UNION SELECT * FROM Customer", // UNION injection
        )

        invalidTableNames.forEach { tableName ->
            shouldThrow<IllegalArgumentException> {
                DataFrame.readSqlTable(connection, tableName)
            }
        }
    }

    @Test
    fun `readSqlQuery should reject malicious SQL queries to prevent SQL injections`() {
        // Malicious SQL queries attempting injection
        @Language("SQL")
        val injectionComment = """
            SELECT * FROM Sale WHERE amount = 100.0 -- AND id = 5
            """

        @Language("SQL")
        val injectionMultilineComment = """
            SELECT * FROM Customer /* Possible malicious comment */ WHERE id = 1
            """

        @Language("SQL")
        val injectionSemicolon = """
            SELECT * FROM Sale WHERE amount = 500.0; DROP TABLE Customer
            """

        @Language("SQL")
        val injectionSQLWithSingleQuote = """
            SELECT * FROM Sale WHERE id = 1 AND amount = 100.0 OR '1'='1
            """

        @Language("SQL")
        val injectionUsingDropCommand = """
            DROP TABLE Customer; SELECT * FROM Sale
            """

        val sqlInjectionQueries = listOf(
            injectionComment,
            injectionMultilineComment,
            injectionSemicolon,
            injectionSQLWithSingleQuote,
            injectionUsingDropCommand,
        )

        sqlInjectionQueries.forEach { query ->
            shouldThrow<IllegalArgumentException> {
                DataFrame.readSqlQuery(connection, query)
            }
        }
    }

    @Test
    fun `readFromTable should work with non-standard table names when strictValidation is disabled`() {
        // Non-standard table names that are still valid but may appear strange
        val nonStandardTableNames = listOf(
            "`Customer With Space`", // Table name with spaces
            "`Important-Data`", // Table name with hyphens
            "`[123TableName]`", // Table name that resembles a special syntax
        )

        try {
            // Create these tables to ensure they exist for the test
            connection.createStatement().use { stmt ->
                nonStandardTableNames.forEach { tableName ->
                    stmt.execute("CREATE TABLE IF NOT EXISTS $tableName (id INT, name VARCHAR(255))")
                }
            }

            // Read from these tables with strictValidation disabled
            nonStandardTableNames.forEach { tableName ->
                DataFrame.readSqlTable(connection, tableName, strictValidation = false)
            }
        } finally {
            // Clean up by deleting all created tables
            connection.createStatement().use { stmt ->
                nonStandardTableNames.forEach { tableName ->
                    stmt.execute("DROP TABLE IF EXISTS $tableName")
                }
            }
        }
    }

    @Test
    fun `read from Unicode table names`() {
        val unicodeTableNames = listOf(
            "Таблица", // Russian Cyrillic
            "表", // Chinese character
            "テーブル", // Japanese Katakana
            "عربي", // Arabic
            "Δοκιμή", // Greek
        )

        try {
            // Create tables with Unicode names
            connection.createStatement().use { stmt ->
                unicodeTableNames.forEach { tableName ->
                    stmt.execute("CREATE TABLE IF NOT EXISTS `$tableName` (id INT PRIMARY KEY, name VARCHAR(255))")
                    stmt.execute("INSERT INTO `$tableName` (id, name) VALUES (1, 'TestName')")
                }
            }

            // Read from the tables and validate correctness
            unicodeTableNames.forEach { tableName ->
                val df = DataFrame.readSqlTable(connection, tableName)
                df.rowsCount() shouldBe 1
                df[0][1] shouldBe "TestName"
            }
        } finally {
            // Drop the Unicode tables
            connection.createStatement().use { stmt ->
                unicodeTableNames.forEach { tableName ->
                    stmt.execute("DROP TABLE IF EXISTS `$tableName`")
                }
            }
        }
    }

    @Test
    fun `readSqlQuery should execute DROP TABLE when validation is disabled`() {
        // Query to create a temporary test table
        @Language("SQL")
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS TestTable (
            id INT PRIMARY KEY,
            data VARCHAR(255)
            )
            """

        // Query to drop the test table
        @Language("SQL")
        val dropTableQuery = """
            SELECT * FROM TestTable; DROP TABLE TestTable;

            """

        try {
            // Create the test table
            connection.createStatement().use { stmt ->
                stmt.execute(createTableQuery) // Create table for the test case
            }

            // Execute the DROP TABLE command with validation disabled
            DataFrame.readSqlQuery(connection, dropTableQuery, strictValidation = false)

            // Verify that the table has been successfully dropped
            connection.createStatement().use { stmt ->
                shouldThrow<SQLException> {
                    stmt.executeQuery("SELECT * FROM TestTable")
                }
            }
        } finally {
            // Cleanup: Ensure the table is removed in case of failure
            connection.createStatement().use { stmt ->
                stmt.execute("DROP TABLE IF EXISTS TestTable")
            }
        }
    }

    @Test
    fun `read from table with name from reserved SQL keywords`() {
        @Language("SQL")
        val createAlterTableQuery = """
            CREATE TABLE "ALTER" (
            id INT PRIMARY KEY,
            description TEXT
            )
            """

        @Language("SQL")
        val selectFromWeirdTableSQL = """SELECT * from "ALTER""""

        try {
            connection.createStatement().execute(createAlterTableQuery)
            // with enabled strictValidation
            shouldThrow<IllegalArgumentException> {
                DataFrame.readSqlQuery(connection, selectFromWeirdTableSQL)
            }
            // with disabled strictValidation
            DataFrame.readSqlQuery(connection, selectFromWeirdTableSQL, strictValidation = false).rowsCount() shouldBe 0
        } finally {
            connection.createStatement().execute("DROP TABLE IF EXISTS \"ALTER\"")
        }
    }

    @Test
    fun `read from table with column name containing the reserved SQL keywords`() {
        @Language("SQL")
        val createAlterTableQuery = """
            CREATE TABLE HELLO_ALTER (
            id INT PRIMARY KEY,
            last_update TEXT
            )
            """

        @Language("SQL")
        val selectFromWeirdTableSQL = """SELECT last_update from HELLO_ALTER"""

        try {
            connection.createStatement().execute(createAlterTableQuery)
            DataFrame.readSqlQuery(connection, selectFromWeirdTableSQL).rowsCount() shouldBe 0
        } finally {
            connection.createStatement().execute("DROP TABLE IF EXISTS HELLO_ALTER")
        }
    }

    @Test
    fun `read from non-existing jdbc url`() {
        shouldThrow<SQLException> {
            DataFrame.readSqlTable(DriverManager.getConnection("ddd"), "WrongTableName")
        }
    }

    @Test
    fun `read from sql query`() {
        val df = DataFrame.readSqlQuery(connection, CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df)

        val df1 = DataFrame.readSqlQuery(connection, CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df1, 1)

        val dataSchema = DataFrameSchema.readSqlQuery(connection, CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = DataFrame.readSqlQuery(dbConfig, CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df2)

        val df3 = DataFrame.readSqlQuery(dbConfig, CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df3, 1)

        val dataSchema1 = DataFrameSchema.readSqlQuery(dbConfig, CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema1)
    }

    @Test
    fun `read from sql query with extension functions`() {
        val df = connection.readDataFrame(CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df)

        val df1 = connection.readDataFrame(CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df1, 1)

        val dataSchema = connection.readDataFrameSchema(CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = dbConfig.readDataFrame(CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df2)

        val df3 = dbConfig.readDataFrame(CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df3, 1)

        val dataSchema1 = dbConfig.readDataFrameSchema(CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema1)
    }

    @Test
    fun `read from sql query with two repeated columns`() {
        @Language("SQL")
        val sqlQuery =
            """
            SELECT c1.name, c2.name
            FROM Customer c1
            INNER JOIN Customer c2 ON c1.id = c2.id
            """.trimIndent()

        val schema = DataFrameSchema.readSqlQuery(connection, sqlQuery)
        schema.columns.size shouldBe 2
        schema.columns.toList()[0].first shouldBe "name"
        schema.columns.toList()[1].first shouldBe "name1"
    }

    @Test
    fun `read from sql query with three repeated columns`() {
        @Language("SQL")
        val sqlQuery =
            """
            SELECT c1.name as name, c2.name as name_1, c1.name as name_1
            FROM Customer c1
            INNER JOIN Customer c2 ON c1.id = c2.id
            """.trimIndent()

        val schema = DataFrameSchema.readSqlQuery(connection, sqlQuery)
        schema.columns.size shouldBe 3
        schema.columns.toList()[0].first shouldBe "name"
        schema.columns.toList()[1].first shouldBe "name1"
        schema.columns.toList()[2].first shouldBe "name2"
    }

    @Test
    fun `read from all tables`() {
        val dataFrameMap = DataFrame.readAllSqlTables(connection)
        assertAllTablesData(dataFrameMap)

        val dataframes1 = DataFrame.readAllSqlTables(connection, limit = 1)
        assertAllTablesDataWithLimit(dataframes1)

        val dataFrameSchemaMap = DataFrameSchema.readAllSqlTables(connection)
        assertAllTablesSchema(dataFrameSchemaMap)

        val dbConfig = DbConnectionConfig(url = URL)
        val dataframes2 = DataFrame.readAllSqlTables(dbConfig)
        assertAllTablesData(dataframes2)

        val dataframes3 = DataFrame.readAllSqlTables(dbConfig, limit = 1)
        assertAllTablesDataWithLimit(dataframes3)

        val dataSchemas1 = DataFrameSchema.readAllSqlTables(dbConfig)
        assertAllTablesSchema(dataSchemas1)
    }

    @Test
    fun `infer nullability`() {
        inferNullability(connection)
    }

    @Test
    fun `check require throws exception when specifying H2 database with H2 dialect`() {
        val exception = shouldThrowExactly<IllegalArgumentException> {
            H2(H2())
        }
        exception.message shouldBe "H2 database could not be specified with H2 dialect!"
    }

    @Test
    fun `regular mode for H2 with DbConnectionConfig`() {
        val url = "jdbc:h2:mem:testDatabase"

        val dbConfig = DbConnectionConfig(url)

        val df = DataFrame.readSqlQuery(dbConfig, QUERY_SELECT_ONE)
        df.rowsCount() shouldBe 1
    }

    @Test
    fun `regular mode for H2 with Connection`() {
        val url = "jdbc:h2:mem:testDatabase"

        DriverManager.getConnection(url).use { connection ->
            val df = DataFrame.readSqlQuery(connection, QUERY_SELECT_ONE)
            df.rowsCount() shouldBe 1
        }
    }

    // ========== H2 Mode Tests ==========

    private fun testH2ModeWithDbConnectionConfig(modeUrl: String) {
        val dbConfig = DbConnectionConfig(modeUrl)
        val df = DataFrame.readSqlQuery(dbConfig, QUERY_SELECT_ONE)
        df.rowsCount() shouldBe 1
    }

    private fun testH2ModeWithConnection(modeUrl: String) {
        DriverManager.getConnection(modeUrl).use { connection ->
            val df = DataFrame.readSqlQuery(connection, QUERY_SELECT_ONE)
            df.rowsCount() shouldBe 1
        }
    }

    @Test
    fun `MySQL mode for H2 with DbConnectionConfig`() {
        testH2ModeWithDbConnectionConfig("jdbc:h2:mem:testMySql;MODE=MySQL")
    }

    @Test
    fun `MySQL mode for H2 with Connection`() {
        testH2ModeWithConnection("jdbc:h2:mem:testMySql;MODE=MySQL")
    }

    @Test
    fun `PostgreSQL mode for H2 with DbConnectionConfig`() {
        testH2ModeWithDbConnectionConfig("jdbc:h2:mem:testPostgres;MODE=PostgreSQL")
    }

    @Test
    fun `PostgreSQL mode for H2 with Connection`() {
        testH2ModeWithConnection("jdbc:h2:mem:testPostgres;MODE=PostgreSQL")
    }

    @Test
    fun `MSSQLServer mode for H2 with DbConnectionConfig`() {
        testH2ModeWithDbConnectionConfig("jdbc:h2:mem:testMsSql;MODE=MSSQLServer")
    }

    @Test
    fun `MSSQLServer mode for H2 with Connection`() {
        testH2ModeWithConnection("jdbc:h2:mem:testMsSql;MODE=MSSQLServer")
    }

    @Test
    fun `MariaDB mode for H2 with DbConnectionConfig`() {
        testH2ModeWithDbConnectionConfig("jdbc:h2:mem:testMariaDb;MODE=MariaDB")
    }

    @Test
    fun `MariaDB mode for H2 with Connection`() {
        testH2ModeWithConnection("jdbc:h2:mem:testMariaDb;MODE=MariaDB")
    }

    @Test
    fun `H2 with unsupported mode throws exception`() {
        val url = "jdbc:h2:mem:testUnsupported;MODE=DB2"

        DriverManager.getConnection(url).use { connection ->
            shouldThrow<IllegalArgumentException> {
                DataFrame.readSqlQuery(connection, QUERY_SELECT_ONE)
            }
        }
    }

    @Test
    fun `H2 with unsupported mode throws exception using DbConnectionConfig`() {
        val url = "jdbc:h2:mem:testUnsupported;MODE=Oracle"
        val dbConfig = DbConnectionConfig(url)

        shouldThrow<IllegalArgumentException> {
            DataFrame.readSqlQuery(dbConfig, QUERY_SELECT_ONE)
        }
    }

    @Test
    fun `H2 Regular mode extraction and fallbacks`() {
        // 1. Create a connection without explicit MODE in URL.
        // H2 defaults to Regular mode. extractDBTypeFromConnection should detect this by querying settings.
        DriverManager.getConnection("jdbc:h2:mem:testRegularFallback").use { conn ->
            val dbType = extractDBTypeFromConnection(conn)

            (dbType is H2) shouldBe true
            (dbType as H2).mode shouldBe Mode.Regular

            // 2. Verify fallback behaviors (when delegate is null)

            // buildSqlQueryWithLimit: Check fallback to super implementation (standard LIMIT syntax)
            val query = "SELECT * FROM table"
            dbType.buildSqlQueryWithLimit(query, 10) shouldBe "SELECT * FROM table LIMIT 10"

            // isSystemTable: Check fallback to H2-specific logic (INFORMATION_SCHEMA)
            val systemTable = TableMetadata("SETTINGS", "INFORMATION_SCHEMA", "TEST_DB")
            dbType.isSystemTable(systemTable) shouldBe true

            val userTable = TableMetadata("USERS", "PUBLIC", "TEST_DB")
            dbType.isSystemTable(userTable) shouldBe false

            // buildTableMetadata: Check fallback to reading from ResultSet directly
            conn.createStatement().use { st ->
                st.execute("CREATE TABLE MY_FALLBACK_TABLE (ID INT)")
            }
            conn.metaData.getTables(null, null, "MY_FALLBACK_TABLE", null).use { rs ->
                if (rs.next()) {
                    val metadata = dbType.buildTableMetadata(rs)
                    metadata.name shouldBe "MY_FALLBACK_TABLE"
                    metadata.schemaName shouldBe "PUBLIC"
                    metadata.catalogue shouldNotBe null
                } else {
                    throw IllegalStateException("Could not find created table metadata")
                }
            }
        }
    }

    @Test
    fun `database type extraction utils`() {
        // 1. Test direct extraction from URL for various DBs
        (extractDBTypeFromUrl("jdbc:mysql://localhost:3306/db") is MySql) shouldBe true
        (extractDBTypeFromUrl("jdbc:postgresql://localhost:5432/db") is PostgreSql) shouldBe true
        (extractDBTypeFromUrl("jdbc:sqlite:sample.db") is Sqlite) shouldBe true

        // Test driverClassNameFromUrl
        driverClassNameFromUrl("jdbc:mysql://localhost:3306/db") shouldBe "com.mysql.jdbc.Driver"
        driverClassNameFromUrl("jdbc:postgresql://localhost:5432/db") shouldBe "org.postgresql.Driver"
        driverClassNameFromUrl("jdbc:h2:mem:test") shouldBe "org.h2.Driver"

        // 2. Test unsupported Database URL
        shouldThrow<IllegalArgumentException> {
            extractDBTypeFromUrl("jdbc:oracle:thin:@localhost:1521:xe")
        }

        // 3. Test null URL
        shouldThrow<SQLException> {
            extractDBTypeFromUrl(null)
        }

        // 4. Test H2 specific mode extraction from Connection (End-to-End)

        // Case A: MySQL Mode via URL
        DriverManager.getConnection("jdbc:h2:mem:testExtractMySql;MODE=MySQL").use { conn ->
            val dbType = extractDBTypeFromConnection(conn)
            (dbType is H2) shouldBe true
            (dbType as H2).mode shouldBe H2.Mode.MySql
        }

        // Case B: PostgreSQL Mode via URL
        DriverManager.getConnection("jdbc:h2:mem:testExtractPostgres;MODE=PostgreSQL").use { conn ->
            val dbType = extractDBTypeFromConnection(conn)
            (dbType is H2) shouldBe true
            (dbType as H2).mode shouldBe H2.Mode.PostgreSql
        }

        // Case C: MSSQLServer Mode via URL
        DriverManager.getConnection("jdbc:h2:mem:testExtractMsSql;MODE=MSSQLServer").use { conn ->
            val dbType = extractDBTypeFromConnection(conn)
            (dbType is H2) shouldBe true
            (dbType as H2).mode shouldBe H2.Mode.MsSqlServer
        }
    }

    // helper object created for API testing purposes
    object CustomDB : H2(Mode.MySql)

    @Test
    fun `read from table from custom database`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(connection, tableName, dbType = CustomDB)
        assertCustomerData(df)

        val dataSchema = DataFrameSchema.readSqlTable(connection, tableName, dbType = CustomDB)
        assertCustomerSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = DataFrame.readSqlTable(dbConfig, tableName, dbType = CustomDB)
        assertCustomerData(df2)

        val dataSchema1 = DataFrameSchema.readSqlTable(dbConfig, tableName, dbType = CustomDB)
        assertCustomerSchema(dataSchema1)
    }

    @Test
    fun `read from query from custom database`() {
        val df = DataFrame.readSqlQuery(connection, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesData(df)

        val dataSchema = DataFrameSchema.readSqlQuery(connection, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesSchema(dataSchema)

        val dbConfig = DbConnectionConfig(url = URL)
        val df2 = DataFrame.readSqlQuery(dbConfig, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesData(df2)

        val dataSchema1 = DataFrameSchema.readSqlQuery(dbConfig, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesSchema(dataSchema1)
    }

    @Test
    fun `read from all tables from custom database`() {
        val dataFrameMap = DataFrame.readAllSqlTables(connection, dbType = CustomDB)
        assertAllTablesData(dataFrameMap)

        val dataFrameSchemaMap = DataFrameSchema.readAllSqlTables(connection, dbType = CustomDB)
        assertAllTablesSchema(dataFrameSchemaMap)

        val dbConfig = DbConnectionConfig(url = URL)
        val dataframes2 = DataFrame.readAllSqlTables(dbConfig, dbType = CustomDB)
        assertAllTablesData(dataframes2)

        val dataSchemas1 = DataFrameSchema.readAllSqlTables(dbConfig, dbType = CustomDB)
        assertAllTablesSchema(dataSchemas1)
    }

    @Test
    fun `withReadOnlyConnection sets readOnly and rolls back after execution`() {
        val config = DbConnectionConfig("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1", readOnly = true)

        var wasExecuted = false
        val result = withReadOnlyConnection(config) { conn ->
            wasExecuted = true
            conn.autoCommit shouldBe false
            42
        }

        wasExecuted shouldBe true
        result shouldBe 42
    }

    // ========== DataSource API Tests ==========

    @Test
    fun `read from table using DataSource`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(dataSource, tableName)
        assertCustomerData(df)

        val df1 = DataFrame.readSqlTable(dataSource, tableName, 1)
        assertCustomerData(df1, 1)

        val dataSchema = DataFrameSchema.readSqlTable(dataSource, tableName)
        assertCustomerSchema(dataSchema)
    }

    @Test
    fun `read from table with extension functions using DataSource`() {
        val tableName = "Customer"
        val df = dataSource.readDataFrame(tableName)
        assertCustomerData(df)

        val df1 = dataSource.readDataFrame(tableName, 1)
        assertCustomerData(df1, 1)

        val dataSchema = dataSource.readDataFrameSchema(tableName)
        assertCustomerSchema(dataSchema)
    }

    @Test
    fun `read from sql query using DataSource`() {
        val df = DataFrame.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df)

        val df1 = DataFrame.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df1, 1)

        val dataSchema = DataFrameSchema.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema)
    }

    @Test
    fun `read from sql query with extension functions using DataSource`() {
        val df = dataSource.readDataFrame(CUSTOMER_SALES_QUERY)
        assertCustomerSalesData(df)

        val df1 = dataSource.readDataFrame(CUSTOMER_SALES_QUERY, 1)
        assertCustomerSalesData(df1, 1)

        val dataSchema = dataSource.readDataFrameSchema(CUSTOMER_SALES_QUERY)
        assertCustomerSalesSchema(dataSchema)
    }

    @Test
    fun `read from all tables using DataSource`() {
        val dataFrameMap = DataFrame.readAllSqlTables(dataSource)
        assertAllTablesData(dataFrameMap)

        val dataframes1 = DataFrame.readAllSqlTables(dataSource, limit = 1)
        assertAllTablesDataWithLimit(dataframes1)

        val dataFrameSchemaMap = DataFrameSchema.readAllSqlTables(dataSource)
        assertAllTablesSchema(dataFrameSchemaMap)
    }

    @Test
    fun `read from table from custom database using DataSource`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(dataSource, tableName, dbType = CustomDB)
        assertCustomerData(df)

        val dataSchema = DataFrameSchema.readSqlTable(dataSource, tableName, dbType = CustomDB)
        assertCustomerSchema(dataSchema)
    }

    @Test
    fun `read from query from custom database using DataSource`() {
        val df = DataFrame.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesData(df)

        val dataSchema = DataFrameSchema.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY, dbType = CustomDB)
        assertCustomerSalesSchema(dataSchema)
    }

    @Test
    fun `read from all tables from custom database using DataSource`() {
        val dataFrameMap = DataFrame.readAllSqlTables(dataSource, dbType = CustomDB)
        assertAllTablesData(dataFrameMap)

        val dataFrameSchemaMap = DataFrameSchema.readAllSqlTables(dataSource, dbType = CustomDB)
        assertAllTablesSchema(dataFrameSchemaMap)
    }

    // ========== Connection Pool Tests ==========

    @Test
    fun `repeated read from table with limit using DataSource`() {
        // Verify DataSource integration handles repeated sequential reads correctly.
        // Covers issue #494 where repeated reads with limit produced incorrect results.
        val tableName = "Customer"
        repeat(MAXIMUM_POOL_SIZE * 2) {
            val df = DataFrame.readSqlTable(dataSource, tableName, 2)
            assertCustomerData(df, 2)
        }
    }

    @Test
    fun `DataSource sequential reads return connections to pool`() {
        // Verify connections are properly closed and returned to the pool after each read.
        // Would fail on iteration 6 if connections leak (maximumPoolSize=5).
        repeat(MAXIMUM_POOL_SIZE * 2) {
            val df = DataFrame.readSqlTable(dataSource, "Customer", limit = 1)
            df.rowsCount() shouldBe 1
            assertCustomerData(df, 1)
        }
    }

    @Test
    fun `DataSource sequential reads with alternating tables`() {
        // Test connection reuse when sequentially reading from different tables.
        // Ensures no state pollution when switching between table schemas.
        repeat(MAXIMUM_POOL_SIZE * 2) { i ->
            val tableName = if (i % 2 == 0) "Customer" else "Sale"
            val df = DataFrame.readSqlTable(dataSource, tableName, limit = 1)
            df.rowsCount() shouldBe 1
        }
    }

    @Test
    fun `DataSource sequential reads with mixed query and table operations`() {
        // Verify both readSqlTable and readSqlQuery properly manage the connection lifecycle.
        // Tests that different code paths can alternate sequentially without resource leaks.
        repeat(MAXIMUM_POOL_SIZE * 2) {
            val dfTable = DataFrame.readSqlTable(dataSource, "Customer", limit = 1)
            dfTable.rowsCount() shouldBe 1
            assertCustomerData(dfTable, 1)

            val dfQuery = DataFrame.readSqlQuery(dataSource, CUSTOMER_SALES_QUERY, limit = 1)
            dfQuery.rowsCount() shouldBe 1
            assertCustomerSalesData(dfQuery, 1)
        }
    }
}
