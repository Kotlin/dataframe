package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.h2.jdbc.JdbcSQLSyntaxErrorException
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import org.jetbrains.kotlinx.dataframe.api.print
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

class JdbcTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection =
                DriverManager.getConnection(URL)


            // Crate table Customer
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
                    amount DECIMAL(10, 2)
                )
            """

            connection.createStatement().execute(
                createSaleTableQuery
            )

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
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
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
        df.rowsCount() shouldBe 3
        df.filter { it[TestTableData::integerCol] > 1000}.rowsCount() shouldBe 2
        //TODO: add test for JSON column
    }

    @Test
    fun `read from table`() {
        val tableName = "Customer"
        val df = DataFrame.readSqlTable(connection, tableName).cast<Customer>()

        df.rowsCount() shouldBe 4
        df.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
        df[0][1] shouldBe "John"

        val df1 = DataFrame.readSqlTable(connection, tableName, 1).cast<Customer>()

        df1.rowsCount() shouldBe 1
        df1.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
        df1[0][1] shouldBe "John"

        val dataSchema = DataFrame.getSchemaForSqlTable(connection, tableName)
        dataSchema.columns.size shouldBe 3
        dataSchema.columns["name"]!!.type shouldBe typeOf<String>()

        val dbConfig = DatabaseConfiguration(url = URL)
        val df2 = DataFrame.readSqlTable(dbConfig, tableName).cast<Customer>()

        df2.rowsCount() shouldBe 4
        df2.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
        df2[0][1] shouldBe "John"

        val df3 = DataFrame.readSqlTable(dbConfig, tableName, 1).cast<Customer>()

        df3.rowsCount() shouldBe 1
        df3.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
        df3[0][1] shouldBe "John"

        val dataSchema1 = DataFrame.getSchemaForSqlTable(dbConfig, tableName)
        dataSchema1.columns.size shouldBe 3
        dataSchema.columns["name"]!!.type shouldBe typeOf<String>()
    }

    @Test
    fun `read from ResultSet`() {
        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).use { st ->
            @Language("SQL")
            val selectStatement = "SELECT * FROM Customer"

            st.executeQuery(selectStatement).use { rs ->
                val df = DataFrame.readResultSet(rs, H2).cast<Customer>()

                df.rowsCount() shouldBe 4
                df.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
                df[0][1] shouldBe "John"

                rs.beforeFirst()

                val df1 =  DataFrame.readResultSet(rs, H2, 1).cast<Customer>()

                df1.rowsCount() shouldBe 1
                df1.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
                df1[0][1] shouldBe "John"

                rs.beforeFirst()

                val dataSchema = DataFrame.getSchemaForResultSet(rs, H2)
                dataSchema.columns.size shouldBe 3
                dataSchema.columns["name"]!!.type shouldBe typeOf<String>()

                rs.beforeFirst()

                val df2 = DataFrame.readResultSet(rs, connection).cast<Customer>()

                df2.rowsCount() shouldBe 4
                df2.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
                df2[0][1] shouldBe "John"

                rs.beforeFirst()

                val df3 = DataFrame.readResultSet(rs, connection, 1).cast<Customer>()

                df3.rowsCount() shouldBe 1
                df3.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
                df3[0][1] shouldBe "John"

                rs.beforeFirst()

                val dataSchema1 = DataFrame.getSchemaForResultSet(rs, connection)
                dataSchema1.columns.size shouldBe 3
                dataSchema.columns["name"]!!.type shouldBe typeOf<String>()
            }
        }
    }

    @Test
    fun `read from non-existing table`() {
        shouldThrow<JdbcSQLSyntaxErrorException> {
            DataFrame.readSqlTable(connection, "WrongTableName").cast<Customer>()
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
        @Language("SQL")
        val sqlQuery = """
            SELECT c.name as customerName, SUM(s.amount) as totalSalesAmount
            FROM Sale s
            INNER JOIN Customer c ON s.customerId = c.id
            WHERE c.age > 35
            GROUP BY s.customerId, c.name
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<CustomerSales>()

        df.rowsCount() shouldBe 2
        df.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount() shouldBe 1
        df[0][0] shouldBe "John"

        val df1 = DataFrame.readSqlQuery(connection, sqlQuery, 1).cast<CustomerSales>()

        df1.rowsCount() shouldBe 1
        df1.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount() shouldBe 1
        df1[0][0] shouldBe "John"

        val dataSchema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
        dataSchema.columns.size shouldBe 2
        dataSchema.columns["name"]!!.type shouldBe typeOf<String>()

        val dbConfig = DatabaseConfiguration(url = URL)
        val df2 = DataFrame.readSqlQuery(dbConfig, sqlQuery).cast<CustomerSales>()

        df2.rowsCount() shouldBe 2
        df2.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount() shouldBe 1
        df2[0][0] shouldBe "John"

        val df3 = DataFrame.readSqlQuery(dbConfig, sqlQuery, 1).cast<CustomerSales>()

        df3.rowsCount() shouldBe 1
        df3.filter { it[CustomerSales::totalSalesAmount] > 100 }.rowsCount() shouldBe 1
        df3[0][0] shouldBe "John"

        val dataSchema1 = DataFrame.getSchemaForSqlQuery(dbConfig, sqlQuery)
        dataSchema1.columns.size shouldBe 2
        dataSchema.columns["name"]!!.type shouldBe typeOf<String>()
    }

    @Test
    fun `read from sql query with repeated columns` () {
        @Language("SQL")
        val sqlQuery = """
            SELECT c1.name, c2.name
            FROM Customer c1
            INNER JOIN Customer c2 ON c1.id = c2.id
        """.trimIndent()

        shouldThrow<IllegalStateException> {
            DataFrame.readSqlQuery(connection, sqlQuery)
        }
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection)

        val customerDf = dataframes[0].cast<Customer>()

        customerDf.rowsCount() shouldBe 4
        customerDf.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
        customerDf[0][1] shouldBe "John"

        val saleDf = dataframes[1].cast<Sale>()

        saleDf.rowsCount() shouldBe 4
        saleDf.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 3
        saleDf[0][2] shouldBe 100.5f

        val dataframes1 = DataFrame.readAllSqlTables(connection, 1)

        val customerDf1 = dataframes1[0].cast<Customer>()

        customerDf1.rowsCount() shouldBe 1
        customerDf1.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
        customerDf1[0][1] shouldBe "John"

        val saleDf1 = dataframes1[1].cast<Sale>()

        saleDf1.rowsCount() shouldBe 1
        saleDf1.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 1
        saleDf1[0][2] shouldBe 100.5f

        val dataSchemas = DataFrame.getSchemaForAllSqlTables(connection)

        val customerDataSchema = dataSchemas[0]
        customerDataSchema.columns.size shouldBe 3
        customerDataSchema.columns["name"]!!.type shouldBe typeOf<String>()

        val saleDataSchema = dataSchemas[1]
        saleDataSchema.columns.size shouldBe 3
        saleDataSchema.columns["amount"]!!.type shouldBe typeOf<Float>()

        val dbConfig = DatabaseConfiguration(url = URL)
        val dataframes2 = DataFrame.readAllSqlTables(dbConfig)

        val customerDf2 = dataframes2[0].cast<Customer>()

        customerDf2.rowsCount() shouldBe 4
        customerDf2.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 2
        customerDf2[0][1] shouldBe "John"

        val saleDf2 = dataframes2[1].cast<Sale>()

        saleDf2.rowsCount() shouldBe 4
        saleDf2.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 3
        saleDf2[0][2] shouldBe 100.5f

        val dataframes3 = DataFrame.readAllSqlTables(dbConfig, 1)

        val customerDf3 = dataframes3[0].cast<Customer>()

        customerDf3.rowsCount() shouldBe 1
        customerDf3.filter { it[Customer::age] > 30 }.rowsCount() shouldBe 1
        customerDf3[0][1] shouldBe "John"

        val saleDf3 = dataframes3[1].cast<Sale>()

        saleDf3.rowsCount() shouldBe 1
        saleDf3.filter { it[Sale::amount] > 40 }.rowsCount() shouldBe 1
        saleDf3[0][2] shouldBe 100.5f

        val dataSchemas1 = DataFrame.getSchemaForAllSqlTables(dbConfig)

        val customerDataSchema1 = dataSchemas1[0]
        customerDataSchema1.columns.size shouldBe 3
        customerDataSchema1.columns["name"]!!.type shouldBe typeOf<String>()

        val saleDataSchema1 = dataSchemas1[1]
        saleDataSchema1.columns.size shouldBe 3
        saleDataSchema1.columns["amount"]!!.type shouldBe typeOf<Float>()
    }
}

