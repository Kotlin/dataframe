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


@DataSchema
interface CustomerSQLite {
    val id: Int
    val name: String
    val age: Int
    val salary: Double
    val profilePicture: ByteArray
}

@DataSchema
interface OrderSQLite {
    val id: Int
    val customerName: String
    val orderDate: String
    val totalAmount: Double
    val orderDetails: ByteArray
}

@DataSchema
interface CustomerOrderSQLite {
    val customerId: Int
    val customerName: String
    val customerAge: Int
    val customerSalary: Double
    val customerProfilePicture: ByteArray
    val orderId: Int
    val orderDate: String
    val totalAmount: Double
    val orderDetails: ByteArray
}

class SqliteTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection("jdbc:sqlite:")

            connection.createStatement().execute(
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
            connection.createStatement().execute(
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

            connection.prepareStatement("INSERT INTO Customers (name, age, salary, profile_picture) VALUES (?, ?, ?, ?)")
                .use {
                    it.setString(1, "John Doe")
                    it.setInt(2, 30)
                    it.setDouble(3, 2500.50)
                    it.setBytes(4, profilePicture)
                    it.executeUpdate()
                }

            connection.prepareStatement("INSERT INTO Orders (customer_name, order_date, total_amount, order_details) VALUES (?, ?, ?, ?)")
                .use {
                    it.setString(1, "John Doe")
                    it.setString(2, "2023-07-21")
                    it.setDouble(3, 150.75)
                    it.setBytes(4, orderDetails)
                    it.executeUpdate()
                }
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
    fun `read from tables`() {
        val df = DataFrame.readSqlTable(connection, "dsdfs", "Customers").cast<CustomerSQLite>()
        df.print()
        assertEquals(1, df.rowsCount())

        val df2 = DataFrame.readSqlTable(connection, "dsdfs", "Orders").cast<CustomerSQLite>()
        df2.print()
        assertEquals(1, df2.rowsCount())
    }

    @Test
    fun `read from sql query`() {
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
        val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<CustomerOrderSQLite>()
        df.print()
        assertEquals(1, df.rowsCount())

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
        schema.print()
        assertEquals(8, schema.columns.entries.size)
    }
}
