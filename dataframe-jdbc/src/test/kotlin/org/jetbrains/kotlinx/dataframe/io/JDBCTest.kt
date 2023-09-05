package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import org.h2.jdbc.JdbcSQLSyntaxErrorException
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.reflect.typeOf

private const val URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false"

@DataSchema
interface Customer {
    val id: Int
    val name: String
    val age: Int
}

@DataSchema
interface Sale {
    val id: Int
    val customerId: Int
    val amount: Double
}

@DataSchema
interface CustomerSales {
    val customerName: String
    val totalSalesAmount: Double
}

@DataSchema
interface TestTableData {
    val characterCol: String
    val characterVaryingCol: String
    val characterLargeObjectCol: String
    val mediumTextCol: String
    val varcharIgnoreCaseCol: String
    val binaryCol: ByteArray
    val binaryVaryingCol: ByteArray
    val binaryLargeObjectCol: ByteArray
    val booleanCol: Boolean
    val tinyIntCol: Byte
    val smallIntCol: Short
    val integerCol: Int
    val bigIntCol: Long
    val numericCol: Double
    val realCol: Float
    val doublePrecisionCol: Double
    val decFloatCol: Double
    val dateCol: String
    val timeCol: String
    val timeWithTimeZoneCol: String
    val timestampCol: String
    val timestampWithTimeZoneCol: String
    val intervalCol: String
    val javaObjectCol: Any?
    val enumCol: String
    val jsonCol: String
    val uuidCol: String
}

class JDBCTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection =
                DriverManager.getConnection(URL)

            // Crate table Customer
            connection.createStatement().execute(
                """
                CREATE TABLE Customer (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    age INT
                )
            """.trimIndent()
            )

            // Create table Sale
            connection.createStatement().execute(
                """
                CREATE TABLE Sale (
                    id INT PRIMARY KEY,
                    customerId INT,
                    amount DECIMAL(10, 2)
                )
            """.trimIndent()
            )

            // add data to the Customer table
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (2, 'Alice', 25)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (3, 'Bob', 47)")

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
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun `read from huge table`() {
        connection.createStatement().execute(
            """
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
            """.trimIndent()
        )

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
            """.trimIndent()
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
            """.trimIndent()
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
                    'Option3', '{"key": "yet_another_value"}', '345e6789-e89b-12d3-a456-426655440002'
                )
            """.trimIndent()
        ).executeUpdate()

        val df = DataFrame.readSqlTable(connection, "TestTable").cast<TestTableData>()
        assertEquals(3, df.rowsCount())
        assertEquals(2, df.filter { it[TestTableData::integerCol] > 1000}.rowsCount())
    }

    @Test
    fun `read from table`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(connection, tableName).cast<Customer>()

        assertEquals(3, df.rowsCount())
        assertEquals(2, df.filter { it[Customer::age] > 30 }.rowsCount())
        assertEquals("John", df[0][1])

        val df1 = DataFrame.readSqlTable(connection, tableName, 1).cast<Customer>()

        assertEquals(1, df1.rowsCount())
        assertEquals(1, df1.filter { it[Customer::age] > 30 }.rowsCount())
        assertEquals("John", df1[0][1])

        val dataSchema = DataFrame.getSchemaForSqlTable(connection, tableName)
        assertEquals(3, dataSchema.columns.size)
        assertEquals(typeOf<String>(), dataSchema.columns["name"]!!.type)

        val dbConfig = DatabaseConfiguration(url = URL)
        val df2 = DataFrame.readSqlTable(dbConfig, tableName).cast<Customer>()

        assertEquals(3, df2.rowsCount())
        assertEquals(2, df2.filter { it[Customer::age] > 30 }.rowsCount())
        assertEquals("John", df2[0][1])

        val df3 = DataFrame.readSqlTable(dbConfig, tableName, 1).cast<Customer>()

        assertEquals(1, df3.rowsCount())
        assertEquals(1, df3.filter { it[Customer::age] > 30 }.rowsCount())
        assertEquals("John", df3[0][1])

        val dataSchema1 = DataFrame.getSchemaForSqlTable(dbConfig, tableName)
        assertEquals(3, dataSchema1.columns.size)
        assertEquals(typeOf<String>(), dataSchema.columns["name"]!!.type)
    }

    @Test
    fun `read from non-existing table`() {
        shouldThrow<JdbcSQLSyntaxErrorException> {
            DataFrame.readSqlTable(connection, "WrongTableName").cast<Customer>()
        }
    }

    // TODO: a bug, probably from jdbc side, the synonym for column is "customerName", but it returns just "name"
    @Test
    fun `read from sql query`() {
        val sqlQuery = """
            SELECT c.name as customerName, SUM(s.amount) as totalSalesAmount
            FROM Sale s
            INNER JOIN Customer c ON s.customerId = c.id
            WHERE c.age > 35
            GROUP BY s.customerId, c.name
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<CustomerSales>()

        assertEquals(2, df.rowsCount())
        assertEquals(1, df.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount())
        assertEquals("John", df[0][0])

        val df1 = DataFrame.readSqlQuery(connection, sqlQuery, 1).cast<CustomerSales>()

        assertEquals(1, df1.rowsCount())
        assertEquals(1, df1.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount())
        assertEquals("John", df1[0][0])

        val dataSchema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
        assertEquals(2, dataSchema.columns.size)
        assertEquals(typeOf<String>(), dataSchema.columns["name"]!!.type)

        val dbConfig = DatabaseConfiguration(url = URL)
        val df2 = DataFrame.readSqlQuery(dbConfig, sqlQuery).cast<CustomerSales>()

        assertEquals(2, df2.rowsCount())
        assertEquals(1, df2.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount())
        assertEquals("John", df2[0][0])

        val df3 = DataFrame.readSqlQuery(dbConfig, sqlQuery, 1).cast<CustomerSales>()

        assertEquals(1, df3.rowsCount())
        assertEquals(1, df3.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount())
        assertEquals("John", df3[0][0])

        val dataSchema1 = DataFrame.getSchemaForSqlQuery(dbConfig, sqlQuery)
        assertEquals(2, dataSchema1.columns.size)
        assertEquals(typeOf<String>(), dataSchema.columns["name"]!!.type)
    }

    // TODO: add table with unknown types
    // TODO: add test with reverse mapping
    // TODO: add tests for ResultSets
}

