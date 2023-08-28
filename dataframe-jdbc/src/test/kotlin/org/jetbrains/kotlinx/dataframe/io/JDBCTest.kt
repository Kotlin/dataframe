package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

const val URL = "jdbc:mariadb://localhost:3306/imdb"
const val USER_NAME = "root"
const val PASSWORD = "pass"

@DataSchema
interface ActorKDF {
    val id: Int
    val firstName: String
    val lastName: String
    val gender: String
}

@DataSchema
interface RankedMoviesWithGenres {
    val name: String
    val year: Int
    val rank: Float
    val genres: String
}

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

data class TestTableData(
    val characterCol: String,
    val characterVaryingCol: String,
    val characterLargeObjectCol: String,
    val mediumTextCol: String,
    val varcharIgnoreCaseCol: String,
    val binaryCol: ByteArray,
    val binaryVaryingCol: ByteArray,
    val binaryLargeObjectCol: ByteArray,
    val booleanCol: Boolean,
    val tinyIntCol: Byte,
    val smallIntCol: Short,
    val integerCol: Int,
    val bigIntCol: Long,
    val numericCol: Double,
    val realCol: Float,
    val doublePrecisionCol: Double,
    val decFloatCol: Double,
    val dateCol: String,
    val timeCol: String,
    val timeWithTimeZoneCol: String,
    val timestampCol: String,
    val timestampWithTimeZoneCol: String,
    val intervalCol: String,
    val javaObjectCol: Any?,
    val enumCol: String,
    val jsonCol: String,
    val uuidCol: String,
)

data class CustomerSQLite(
    val id: Int,
    val name: String,
    val age: Int,
    val salary: Double,
    val profilePicture: ByteArray,
)

data class OrderSQLite(
    val id: Int,
    val customerName: String,
    val orderDate: String,
    val totalAmount: Double,
    val orderDetails: ByteArray,
)

data class CustomerOrderSQLite(
    val customerId: Int,
    val customerName: String,
    val customerAge: Int,
    val customerSalary: Double,
    val customerProfilePicture: ByteArray,
    val orderId: Int,
    val orderDate: String,
    val totalAmount: Double,
    val orderDetails: ByteArray,
)

class JDBCTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection =
                DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false")

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
    fun `h2 sql native types mapping to JDBC types`() {
        connection.use { connection ->
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

            val df = DataFrame.readSqlTable(connection, "dsdfs", "TestTable").cast<TestTableData>()
            df.print()
            assertEquals(3, df.rowsCount())
        }
    }

    @Test
    fun `basic test for reading one table`() {
        connection.use { connection ->
            val df = DataFrame.readSqlTable(connection, "dsdfs", "Customer").cast<Customer>()
            df.print()
            assertEquals(3, df.rowsCount())
        }
    }

    @Test
    fun `try to read from not existing table`() {
        connection.use { connection ->
            val df = DataFrame.readSqlTable(connection, "dsdfs", "WrongTableName").cast<Customer>()
            df.print()
            assertEquals(3, df.rowsCount())
        }
    }

    @Test
    fun `sqlite native types mapping to JDBC types`() {
        DriverManager.getConnection("jdbc:sqlite:").use {
            val statement = connection.createStatement()

            // Create the first table with various column types
            statement.execute(
                """
            CREATE TABLE Customers (
                id INTEGER AUTO_INCREMENT PRIMARY KEY,
                name TEXT,
                age INTEGER,
                salary REAL,
                profile_picture BLOB
            )
        """
            )

            // Create the second table with various column types
            statement.execute(
                """
            CREATE TABLE Orders (
                id INTEGER AUTO_INCREMENT PRIMARY KEY,
                customer_name TEXT,
                order_date TEXT,
                total_amount NUMERIC,
                order_details BLOB
            )
        """
            )

            val profilePicture = "SampleProfilePictureData".toByteArray()
            val orderDetails = "OrderDetailsData".toByteArray()

// Insert sample data into Customers
            connection.prepareStatement(
                """
    INSERT INTO Customers (name, age, salary, profile_picture)
    VALUES (?, ?, ?, ?)
"""
            ).use {
                it.setString(1, "John Doe")
                it.setInt(2, 30)
                it.setDouble(3, 2500.50)
                it.setBytes(4, profilePicture)
                it.executeUpdate()
            }

// Insert sample data into Orders
            connection.prepareStatement(
                """
    INSERT INTO Orders (customer_name, order_date, total_amount, order_details)
    VALUES (?, ?, ?, ?)
"""
            ).use {
                it.setString(1, "John Doe")
                it.setString(2, "2023-07-21")
                it.setDouble(3, 150.75)
                it.setBytes(4, orderDetails)
                it.executeUpdate()
            }

            val df = DataFrame.readSqlTable(connection, "dsdfs", "Customers").cast<CustomerSQLite>()
            df.print()
            assertEquals(1, df.rowsCount())

            val df2 = DataFrame.readSqlTable(connection, "dsdfs", "Orders").cast<CustomerSQLite>()
            df2.print()
            assertEquals(1, df2.rowsCount())

            val sqlQuery = """
    SELECT
        c.id AS customerId,
        c.name AS customerName,
        c.age AS customerAge,
        c.salary AS customerSalary,
        c.profile_picture AS customerProfilePicture,
        o.id AS orderId,
        o.order_date AS orderDate,
        o.total_amount AS totalAmount,
        o.order_details AS orderDetails
    FROM Customers c
    INNER JOIN Orders o ON c.name = o.customer_name
"""
            val df3 = DataFrame.readSqlQuery(connection, sqlQuery).cast<CustomerOrderSQLite>()
            df3.print()
            assertEquals(1, df3.rowsCount())

            val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
            schema.print()
            assertEquals(8, schema.columns.entries.size)
        }
    }

    @Test
    fun `basic test for reading join from two table`() {
        connection.use { connection ->
            val sqlQuery = """
            SELECT c.name as customerName, SUM(s.amount) as totalSalesAmount
            FROM Sale s
            INNER JOIN Customer c ON s.customerId = c.id
            WHERE c.age > 35
            GROUP BY s.customerId, c.name
        """.trimIndent()

            val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<CustomerSales>()
            df.print()
            assertEquals(3, df.rowsCount())
        }
    }


    @Test
    fun `imdb setup connection and select from one table`() {
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readSqlTable(connection, "", "actors", 100).cast<ActorKDF>()
            df.print()
        }
    }

    @Test
    fun `imdb convert result of SQL-query`() {
        val sqlQuery = "select name, year, rank,\n" +
            "GROUP_CONCAT (genre) as \"genres\"\n" +
            "from movies join movies_directors on  movie_id = movies.id\n" +
            "     join directors on directors.id=director_id left join movies_genres on movies.id = movies_genres.movie_id \n" +
            "where directors.first_name = \"Quentin\" and directors.last_name = \"Tarantino\"\n" +
            "group by name, year, rank\n" +
            "order by year"
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<RankedMoviesWithGenres>()
            //df.filter { year > 2000 }.print()
            df.print()

            val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
            schema.print()
        }
    }
}

