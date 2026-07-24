package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Types
import kotlin.reflect.typeOf

@DataSchema
interface CustomerSQLite {
    val id: Int?
    val name: String?
    val age: Int?
    val salary: Double
    val profilePicture: ByteArray?
}

@DataSchema
interface OrderSQLite {
    val id: Int?
    val customerName: String?
    val orderDate: String?
    val totalAmount: Double
    val orderDetails: ByteArray?
}

@DataSchema
interface CustomerOrderSQLite {
    val customerId: Int?
    val customerName: String?
    val customerAge: Int?
    val customerSalary: Double
    val customerProfilePicture: ByteArray?
    val orderId: Int?
    val orderDate: String?
    val totalAmount: Double
    val orderDetails: ByteArray?
}

@DataSchema
interface FlagSQLite {
    val id: Int?
    val enabled: Boolean
    val optional: Boolean?
}

class SqliteTest {
    companion object {
        private lateinit var connection: Connection

        /**
         * We are using a temporary file because we need to test requests with DBConnectionConfig,
         * which creates a connection under the hood and need to have access to the shared SQLite database
         */
        private lateinit var testDbFile: File
        private lateinit var databaseUrl: String

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            testDbFile = Files.createTempFile("dataframe_sqlite_test_", ".db").toFile()
            testDbFile.deleteOnExit() // if fails

            databaseUrl = "jdbc:sqlite:${testDbFile.absolutePath}"
            connection = DriverManager.getConnection(databaseUrl)

            @Language("SQL")
            val createCustomersTableQuery = """
            CREATE TABLE Customers (
                id INTEGER PRIMARY KEY,
                name TEXT,
                age INTEGER,
                salary REAL NOT NULL,
                profilePicture BLOB
            )
            """

            connection.createStatement().execute(createCustomersTableQuery)

            @Language("SQL")
            val createOrderTableQuery = """
            CREATE TABLE Orders (
                id INTEGER PRIMARY KEY,
                customerName TEXT,
                orderDate TEXT,
                totalAmount NUMERIC NOT NULL,
                orderDetails BLOB
            )
            """

            connection.createStatement().execute(createOrderTableQuery)

            @Language("SQL")
            val createFlagsTableQuery = """
            CREATE TABLE Flags (
                id INTEGER PRIMARY KEY,
                enabled BOOLEAN NOT NULL,
                optional BOOLEAN
            )
            """

            connection.createStatement().execute(createFlagsTableQuery)

            connection.prepareStatement("INSERT INTO Flags (enabled, optional) VALUES (?, ?)").use {
                it.setBoolean(1, true)
                it.setBoolean(2, false)
                it.executeUpdate()
            }

            connection.prepareStatement("INSERT INTO Flags (enabled, optional) VALUES (?, ?)").use {
                it.setBoolean(1, false)
                it.setNull(2, Types.BOOLEAN)
                it.executeUpdate()
            }

            // Dates and timestamps: SQLite stores each in one of three encodings — ISO text,
            // Unix seconds (INTEGER), or Julian days (REAL). The library returns the raw stored
            // value; downstream code is responsible for parsing.
            @Language("SQL")
            val createTemporalTableQuery = """
            CREATE TABLE Temporal (
                id INTEGER PRIMARY KEY,
                isoDate DATE,
                isoDateTime DATETIME,
                isoTimestamp TIMESTAMP,
                unixTimestamp TIMESTAMP,
                julianDate DATE,
                julianTimestamp TIMESTAMP
            )
            """

            connection.createStatement().execute(createTemporalTableQuery)

            // Julian day 2460146.5 = 2023-07-21 00:00:00 UTC.
            // The `.5` fraction forces SQLite to store it as REAL (Julian day convention).
            connection.createStatement().execute(
                """
                INSERT INTO Temporal
                    (isoDate, isoDateTime, isoTimestamp, unixTimestamp, julianDate, julianTimestamp)
                    VALUES
                    ('2023-07-21', '2023-07-21 10:30:00', '2023-07-21T10:30:00Z', 1690000000,
                     2460146.5, 2460146.5)
                """.trimIndent(),
            )

            val profilePicture = "SampleProfilePictureData".toByteArray()
            val orderDetails = "OrderDetailsData".toByteArray()

            connection.prepareStatement("INSERT INTO Customers (name, age, salary, profilePicture) VALUES (?, ?, ?, ?)")
                .use {
                    it.setString(1, "John Doe")
                    it.setInt(2, 30)
                    it.setDouble(3, 2500.50)
                    it.setBytes(4, profilePicture)
                    it.executeUpdate()
                }

            connection.prepareStatement("INSERT INTO Customers (name, age, salary, profilePicture) VALUES (?, ?, ?, ?)")
                .use {
                    it.setString(1, null)
                    it.setInt(2, 40)
                    it.setDouble(3, 1500.50)
                    it.setBytes(4, profilePicture)
                    it.executeUpdate()
                }

            connection.prepareStatement(
                "INSERT INTO Orders (customerName, orderDate, totalAmount, orderDetails) VALUES (?, ?, ?, ?)",
            ).use {
                it.setString(1, null)
                it.setString(2, "2023-07-21")
                it.setDouble(3, 150.75)
                it.setBytes(4, orderDetails)
                it.executeUpdate()
            }

            connection.prepareStatement(
                "INSERT INTO Orders (customerName, orderDate, totalAmount, orderDetails) VALUES (?, ?, ?, ?)",
            ).use {
                it.setString(1, "John Doe")
                it.setString(2, "2023-08-21")
                it.setDouble(3, 250.75)
                it.setBytes(4, orderDetails)
                it.executeUpdate()
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.close()
                if (::testDbFile.isInitialized && testDbFile.exists()) {
                    testDbFile.delete()
                }
            } catch (e: Exception) {
                // Log, but not fail
                println("Warning: Could not clean up test database file: ${e.message}")
            }
        }
    }

    @Test
    fun `read from tables`() {
        val customerTableName = "Customers"
        val df = DataFrame.readSqlTable(connection, customerTableName).cast<CustomerSQLite>()
        val result = df.filter { "name"<String?>() == "John Doe" }
        result[0][2] shouldBe 30

        val schema = DataFrameSchema.readSqlTable(connection, customerTableName)
        schema.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema.columns["name"]!!.type shouldBe typeOf<String?>()
        schema.columns["salary"]!!.type shouldBe typeOf<Double>()
        schema.columns["profilePicture"]!!.type shouldBe typeOf<ByteArray?>()

        val orderTableName = "Orders"
        val df2 = DataFrame.readSqlTable(connection, orderTableName).cast<OrderSQLite>()
        val result2 = df2.filter { "totalAmount"<Double>() > 10 }
        result2[0][2] shouldBe "2023-07-21"

        val schema2 = DataFrameSchema.readSqlTable(connection, orderTableName)
        schema2.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema2.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema2.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from tables with DBConnectionConfig`() {
        val customerTableName = "Customers"

        val dbConnectionConfig = DbConnectionConfig(databaseUrl)

        val df = DataFrame.readSqlTable(dbConnectionConfig, customerTableName).cast<CustomerSQLite>()
        val result = df.filter { "name"<String?>() == "John Doe" }
        result[0][2] shouldBe 30

        val schema = DataFrameSchema.readSqlTable(dbConnectionConfig, customerTableName)
        schema.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema.columns["name"]!!.type shouldBe typeOf<String?>()
        schema.columns["salary"]!!.type shouldBe typeOf<Double>()
        schema.columns["profilePicture"]!!.type shouldBe typeOf<ByteArray?>()

        val orderTableName = "Orders"
        val df2 = DataFrame.readSqlTable(dbConnectionConfig, orderTableName).cast<OrderSQLite>()
        val result2 = df2.filter { "totalAmount"<Double>() > 10 }
        result2[0][2] shouldBe "2023-07-21"

        val schema2 = DataFrameSchema.readSqlTable(dbConnectionConfig, orderTableName)
        schema2.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema2.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema2.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Language("SQL")
    private val sqlQuery = """
            SELECT
                c.id AS customerId,
                c.name AS customerName,
                c.age AS customerAge,
                c.salary AS customerSalary,
                c.profilePicture AS customerProfilePicture,
                o.id AS orderId,
                o.orderDate AS orderDate,
                o.totalAmount AS totalAmount,
                o.orderDetails AS orderDetails
            FROM Customers c
            INNER JOIN Orders o ON c.name = o.customerName
            """

    @Test
    fun `read from sql query`() {
        val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<CustomerOrderSQLite>()
        val result = df.filter { "customerSalary"<Double>() > 1 }
        result[0][3] shouldBe 2500.5

        val schema = DataFrameSchema.readSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["customerId"]!!.type shouldBe typeOf<Int?>()
        schema.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema.columns["customerAge"]!!.type shouldBe typeOf<Int?>()
        schema.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from sql query with DBConnectionConfig`() {
        val dbConnectionConfig = DbConnectionConfig(databaseUrl)

        val df = DataFrame.readSqlQuery(dbConnectionConfig, sqlQuery).cast<CustomerOrderSQLite>()
        val result = df.filter { "customerSalary"<Double>() > 1 }
        result[0][3] shouldBe 2500.5

        val schema = DataFrameSchema.readSqlQuery(dbConnectionConfig, sqlQuery = sqlQuery)
        schema.columns["customerId"]!!.type shouldBe typeOf<Int?>()
        schema.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema.columns["customerAge"]!!.type shouldBe typeOf<Int?>()
        schema.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection)

        val customerDf = dataframes.getValue("Customers").cast<CustomerSQLite>()

        customerDf.rowsCount() shouldBe 2
        customerDf.filter { "age"<Int?>()?.let { it > 30 } ?: false }.rowsCount() shouldBe 1
        customerDf[0][1] shouldBe "John Doe"

        val orderDf = dataframes.getValue("Orders").cast<OrderSQLite>()

        orderDf.rowsCount() shouldBe 2
        orderDf.filter { "totalAmount"<Double>() > 200 }.rowsCount() shouldBe 1
        orderDf[0][1] shouldBe null
    }

    @Test
    fun `read boolean column`() {
        val flagsTableName = "Flags"
        val df = DataFrame.readSqlTable(connection, flagsTableName).cast<FlagSQLite>()

        df.rowsCount() shouldBe 2
        df["enabled"][0] shouldBe true
        df["enabled"][1] shouldBe false
        df["optional"][0] shouldBe false
        df["optional"][1] shouldBe null

        val schema = DataFrameSchema.readSqlTable(connection, flagsTableName)
        schema.columns["enabled"]!!.type shouldBe typeOf<Boolean>()
        schema.columns["optional"]!!.type shouldBe typeOf<Boolean?>()
    }

    @Test
    fun `read date and timestamp columns converts storage class to declared type`() {
        // SQLite doesn't have native DATE/TIMESTAMP storage — values may be TEXT (ISO), INTEGER
        // (Unix seconds), or REAL (Julian day). The library preserves an idiomatic Kotlin
        // date-time type in the schema and converts each value in preprocessing based on its
        // runtime storage class.
        val df = DataFrame.readSqlTable(connection, "Temporal")

        df.rowsCount() shouldBe 1
        // TEXT storage — ISO strings.
        df["isoDate"][0] shouldBe kotlinx.datetime.LocalDate.parse("2023-07-21")
        df["isoDateTime"][0] shouldBe kotlinx.datetime.LocalDateTime.parse("2023-07-21T10:30:00")
        df["isoTimestamp"][0] shouldBe kotlin.time.Instant.parse("2023-07-21T10:30:00Z")
        // INTEGER storage — Unix seconds.
        df["unixTimestamp"][0] shouldBe kotlin.time.Instant.fromEpochSeconds(1690000000)
        // REAL storage — Julian day 2460146.5 = 2023-07-21 00:00:00 UTC.
        df["julianDate"][0] shouldBe kotlinx.datetime.LocalDate.parse("2023-07-21")
        df["julianTimestamp"][0] shouldBe kotlin.time.Instant.parse("2023-07-21T00:00:00Z")

        val schema = DataFrameSchema.readSqlTable(connection, "Temporal")
        schema.columns["isoDate"]!!.type shouldBe typeOf<kotlinx.datetime.LocalDate?>()
        schema.columns["isoDateTime"]!!.type shouldBe typeOf<kotlinx.datetime.LocalDateTime?>()
        schema.columns["isoTimestamp"]!!.type shouldBe typeOf<kotlin.time.Instant?>()
        schema.columns["unixTimestamp"]!!.type shouldBe typeOf<kotlin.time.Instant?>()
        schema.columns["julianDate"]!!.type shouldBe typeOf<kotlinx.datetime.LocalDate?>()
        schema.columns["julianTimestamp"]!!.type shouldBe typeOf<kotlin.time.Instant?>()
    }
}
