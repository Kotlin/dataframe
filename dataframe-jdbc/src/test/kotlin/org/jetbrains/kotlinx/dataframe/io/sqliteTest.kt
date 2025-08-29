package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
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
        val result = df.filter { it[CustomerSQLite::name] == "John Doe" }
        result[0][2] shouldBe 30

        val schema = DataFrame.getSchemaForSqlTable(connection, customerTableName)
        schema.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema.columns["name"]!!.type shouldBe typeOf<String?>()
        schema.columns["salary"]!!.type shouldBe typeOf<Double>()
        schema.columns["profilePicture"]!!.type shouldBe typeOf<ByteArray?>()

        val orderTableName = "Orders"
        val df2 = DataFrame.readSqlTable(connection, orderTableName).cast<OrderSQLite>()
        val result2 = df2.filter { it[OrderSQLite::totalAmount] > 10 }
        result2[0][2] shouldBe "2023-07-21"

        val schema2 = DataFrame.getSchemaForSqlTable(connection, orderTableName)
        schema2.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema2.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema2.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from tables with DBConnectionConfig`() {
        val customerTableName = "Customers"

        val dbConnectionConfig = DbConnectionConfig(databaseUrl)

        val df = DataFrame.readSqlTable(dbConnectionConfig, customerTableName).cast<CustomerSQLite>()
        val result = df.filter { it[CustomerSQLite::name] == "John Doe" }
        result[0][2] shouldBe 30

        val schema = DataFrame.getSchemaForSqlTable(dbConnectionConfig, customerTableName)
        schema.columns["id"]!!.type shouldBe typeOf<Int?>()
        schema.columns["name"]!!.type shouldBe typeOf<String?>()
        schema.columns["salary"]!!.type shouldBe typeOf<Double>()
        schema.columns["profilePicture"]!!.type shouldBe typeOf<ByteArray?>()

        val orderTableName = "Orders"
        val df2 = DataFrame.readSqlTable(dbConnectionConfig, orderTableName).cast<OrderSQLite>()
        val result2 = df2.filter { it[OrderSQLite::totalAmount] > 10 }
        result2[0][2] shouldBe "2023-07-21"

        val schema2 = DataFrame.getSchemaForSqlTable(dbConnectionConfig, orderTableName)
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
        val result = df.filter { it[CustomerOrderSQLite::customerSalary] > 1 }
        result[0][3] shouldBe 2500.5

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["customerId"]!!.type shouldBe typeOf<Int?>()
        schema.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema.columns["customerAge"]!!.type shouldBe typeOf<Int?>()
        schema.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from sql query with DBConnectionConfig`() {
        val dbConnectionConfig = DbConnectionConfig(databaseUrl)

        val df = DataFrame.readSqlQuery(dbConnectionConfig, sqlQuery).cast<CustomerOrderSQLite>()
        val result = df.filter { it[CustomerOrderSQLite::customerSalary] > 1 }
        result[0][3] shouldBe 2500.5

        val schema = DataFrame.getSchemaForSqlQuery(dbConnectionConfig, sqlQuery = sqlQuery)
        schema.columns["customerId"]!!.type shouldBe typeOf<Int?>()
        schema.columns["customerName"]!!.type shouldBe typeOf<String?>()
        schema.columns["customerAge"]!!.type shouldBe typeOf<Int?>()
        schema.columns["totalAmount"]!!.type shouldBe typeOf<Double>()
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection).values.toList()

        val customerDf = dataframes[0].cast<CustomerSQLite>()

        customerDf.rowsCount() shouldBe 2
        customerDf.filter { it[CustomerSQLite::age] != null && it[CustomerSQLite::age]!! > 30 }.rowsCount() shouldBe 1
        customerDf[0][1] shouldBe "John Doe"

        val orderDf = dataframes[1].cast<OrderSQLite>()

        orderDf.rowsCount() shouldBe 2
        orderDf.filter { it[OrderSQLite::totalAmount] > 200 }.rowsCount() shouldBe 1
        orderDf[0][1] shouldBe null
    }
}
