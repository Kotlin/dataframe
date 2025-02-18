package org.jetbrains.kotlinx.dataframe.io.local

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlQuery
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlTable
import org.jetbrains.kotlinx.dataframe.io.inferNullability
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Date
import java.util.UUID
import kotlin.reflect.typeOf

private const val URL = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true"
private const val USER_NAME = "root"
private const val PASSWORD = "pass"
private const val TEST_DATABASE_NAME = "testKDFdatabase"

@DataSchema
interface Table1MSSSQL {
    val id: Int
    val bigintColumn: Long
    val binaryColumn: ByteArray
    val bitColumn: Boolean
    val charColumn: Char
    val dateColumn: Date
    val datetime3Column: java.sql.Timestamp
    val datetime2Column: java.sql.Timestamp
    val datetimeoffset2Column: String
    val decimalColumn: BigDecimal
    val floatColumn: Double
    val imageColumn: ByteArray?
    val intColumn: Int
    val moneyColumn: BigDecimal
    val ncharColumn: Char
    val ntextColumn: String
    val numericColumn: BigDecimal
    val nvarcharColumn: String
    val nvarcharMaxColumn: String
    val realColumn: Float
    val smalldatetimeColumn: java.sql.Timestamp
    val smallintColumn: Int
    val smallmoneyColumn: BigDecimal
    val timeColumn: java.sql.Time
    val timestampColumn: java.sql.Timestamp
    val tinyintColumn: Int
    val uniqueidentifierColumn: Char
    val varbinaryColumn: ByteArray
    val varbinaryMaxColumn: ByteArray
    val varcharColumn: String
    val varcharMaxColumn: String
    val xmlColumn: String
    val sqlvariantColumn: String
    val geometryColumn: String
    val geographyColumn: String
}

@Ignore
class MSSQLTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)

            connection.createStatement().use { st ->
                // Drop the test database if it exists
                val dropDatabaseQuery = "IF DB_ID('$TEST_DATABASE_NAME') IS NOT NULL\n" +
                    "DROP DATABASE $TEST_DATABASE_NAME"
                st.executeUpdate(dropDatabaseQuery)

                // Create the test database
                val createDatabaseQuery = "CREATE DATABASE $TEST_DATABASE_NAME"
                st.executeUpdate(createDatabaseQuery)

                // Use the newly created database
                val useDatabaseQuery = "USE $TEST_DATABASE_NAME"
                st.executeUpdate(useDatabaseQuery)
            }

            @Language("SQL")
            val createTableQuery = """
                CREATE TABLE Table1 (
                id INT NOT NULL IDENTITY PRIMARY KEY,
                bigintColumn BIGINT,
                binaryColumn BINARY(50),
                bitColumn BIT,
                charColumn CHAR(10),
                dateColumn DATE,
                datetime3Column DATETIME2(3),
                datetime2Column DATETIME2,
                datetimeoffset2Column DATETIMEOFFSET(2),
                decimalColumn DECIMAL(10,2),
                floatColumn FLOAT,
                imageColumn IMAGE,
                intColumn INT,
                moneyColumn MONEY,
                ncharColumn NCHAR(10),
                ntextColumn NTEXT,
                numericColumn NUMERIC(10,2),
                nvarcharColumn NVARCHAR(50),
                nvarcharMaxColumn NVARCHAR(MAX),
                realColumn REAL,
                smalldatetimeColumn SMALLDATETIME,
                smallintColumn SMALLINT,
                smallmoneyColumn SMALLMONEY,
                textColumn TEXT,
                timeColumn TIME,
                timestampColumn DATETIME2,
                tinyintColumn TINYINT,
                uniqueidentifierColumn UNIQUEIDENTIFIER,
                varbinaryColumn VARBINARY(50),
                varbinaryMaxColumn VARBINARY(MAX),
                varcharColumn VARCHAR(50),
                varcharMaxColumn VARCHAR(MAX),
                xmlColumn XML,
                sqlvariantColumn SQL_VARIANT,
                geometryColumn GEOMETRY,
                geographyColumn GEOGRAPHY
            );
            """

            connection.createStatement().execute(createTableQuery.trimIndent())

            @Language("SQL")
            val insertData1 =
                """
                INSERT INTO Table1 (
                bigintColumn, binaryColumn, bitColumn, charColumn, dateColumn, datetime3Column, datetime2Column,
                datetimeoffset2Column, decimalColumn, floatColumn, imageColumn, intColumn, moneyColumn, ncharColumn,
                ntextColumn, numericColumn, nvarcharColumn, nvarcharMaxColumn, realColumn, smalldatetimeColumn,
                smallintColumn, smallmoneyColumn, textColumn, timeColumn, timestampColumn, tinyintColumn,
                uniqueidentifierColumn, varbinaryColumn, varbinaryMaxColumn, varcharColumn, varcharMaxColumn,
                xmlColumn, sqlvariantColumn, geometryColumn, geographyColumn
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

            connection.prepareStatement(insertData1).use { st ->
                for (i in 1..5) {
                    st.setLong(1, 123456789012345L) // bigintColumn
                    st.setBytes(2, byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x67, 0x67, 0x67, 0x67)) // binaryColumn
                    st.setBoolean(3, true) // bitColumn
                    st.setString(4, "Sample") // charColumn
                    st.setDate(5, java.sql.Date(System.currentTimeMillis())) // dateColumn
                    st.setTimestamp(6, java.sql.Timestamp(System.currentTimeMillis())) // datetime3Column
                    st.setTimestamp(7, java.sql.Timestamp(System.currentTimeMillis())) // datetime2Column
                    st.setTimestamp(8, java.sql.Timestamp(System.currentTimeMillis())) // datetimeoffset2Column
                    st.setBigDecimal(9, BigDecimal("12345.67")) // decimalColumn
                    st.setFloat(10, 123.45f) // floatColumn
                    st.setNull(11, java.sql.Types.NULL) // imageColumn (assuming nullable)
                    st.setInt(12, 123456) // intColumn
                    st.setBigDecimal(13, BigDecimal("123.45")) // moneyColumn
                    st.setString(14, "Sample") // ncharColumn
                    st.setString(15, "Sample$i text") // ntextColumn
                    st.setBigDecimal(16, BigDecimal("1234.56")) // numericColumn
                    st.setString(17, "Sample") // nvarcharColumn
                    st.setString(18, "Sample$i text") // nvarcharMaxColumn
                    st.setFloat(19, 123.45f) // realColumn
                    st.setTimestamp(20, java.sql.Timestamp(System.currentTimeMillis())) // smalldatetimeColumn
                    st.setInt(21, 123) // smallintColumn
                    st.setBigDecimal(22, BigDecimal("123.45")) // smallmoneyColumn
                    st.setString(23, "Sample$i text") // textColumn
                    st.setTime(24, java.sql.Time(System.currentTimeMillis())) // timeColumn
                    st.setTimestamp(25, java.sql.Timestamp(System.currentTimeMillis())) // timestampColumn
                    st.setInt(26, 123) // tinyintColumn
                    // st.setObject(27, null) // udtColumn (assuming nullable)
                    st.setObject(27, UUID.randomUUID()) // uniqueidentifierColumn
                    st.setBytes(28, byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x67, 0x67, 0x67, 0x67)) // varbinaryColumn
                    st.setBytes(29, byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x67, 0x67, 0x67, 0x67)) // varbinaryMaxColumn
                    st.setString(30, "Sample$i") // varcharColumn
                    st.setString(31, "Sample$i text") // varcharMaxColumn
                    st.setString(32, "<xml>Sample$i</xml>") // xmlColumn
                    st.setString(33, "SQL_VARIANT") // sqlvariantColumn
                    st.setBytes(
                        34,
                        @Suppress("ktlint:standard:argument-list-wrapping")
                        byteArrayOf(
                            0xE6.toByte(), 0x10, 0x00, 0x00, 0x01, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                            0x44, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x05, 0x4C, 0x0,
                        ),
                    ) // geometryColumn
                    st.setString(35, "POINT(1 1)") // geographyColumn
                    st.executeUpdate()
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.createStatement().use { st -> st.execute("DROP DATABASE IF EXISTS $TEST_DATABASE_NAME") }
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun `basic test for reading sql tables`() {
        val df1 = DataFrame.readSqlTable(connection, "table1", limit = 5).cast<Table1MSSSQL>()

        val result = df1.filter { it[Table1MSSSQL::id] == 1 }
        result[0][30] shouldBe "Sample1"
        result[0][Table1MSSSQL::bigintColumn] shouldBe 123456789012345L
        result[0][Table1MSSSQL::bitColumn] shouldBe true
        result[0][Table1MSSSQL::intColumn] shouldBe 123456
        result[0][Table1MSSSQL::ntextColumn] shouldBe "Sample1 text"

        val schema = DataFrame.getSchemaForSqlTable(connection, "table1")
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigintColumn"]!!.type shouldBe typeOf<Long?>()
        schema.columns["binaryColumn"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["bitColumn"]!!.type shouldBe typeOf<Boolean?>()
        schema.columns["charColumn"]!!.type shouldBe typeOf<Char?>()
        schema.columns["dateColumn"]!!.type shouldBe typeOf<Date?>()
        schema.columns["datetime3Column"]!!.type shouldBe typeOf<java.sql.Timestamp?>()
        schema.columns["datetime2Column"]!!.type shouldBe typeOf<java.sql.Timestamp?>()
        schema.columns["datetimeoffset2Column"]!!.type shouldBe typeOf<String?>()
        schema.columns["decimalColumn"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["floatColumn"]!!.type shouldBe typeOf<Double?>()
        schema.columns["imageColumn"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["intColumn"]!!.type shouldBe typeOf<Int?>()
        schema.columns["moneyColumn"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["ncharColumn"]!!.type shouldBe typeOf<Char?>()
        schema.columns["ntextColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["numericColumn"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["nvarcharColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["nvarcharMaxColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["realColumn"]!!.type shouldBe typeOf<Float?>()
        schema.columns["smalldatetimeColumn"]!!.type shouldBe typeOf<java.sql.Timestamp?>()
        schema.columns["smallintColumn"]!!.type shouldBe typeOf<Int?>()
        schema.columns["smallmoneyColumn"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["timeColumn"]!!.type shouldBe typeOf<java.sql.Time?>()
        schema.columns["timestampColumn"]!!.type shouldBe typeOf<java.sql.Timestamp?>()
        schema.columns["tinyintColumn"]!!.type shouldBe typeOf<Int?>()
        schema.columns["uniqueidentifierColumn"]!!.type shouldBe typeOf<Char?>()
        schema.columns["varbinaryColumn"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["varbinaryMaxColumn"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["varcharColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["varcharMaxColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["xmlColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["sqlvariantColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["geometryColumn"]!!.type shouldBe typeOf<String?>()
        schema.columns["geographyColumn"]!!.type shouldBe typeOf<String?>()
    }

    @Test
    fun `read from sql query`() {
        @Language("SQL")
        val sqlQuery =
            """
            SELECT
            Table1.id,
            Table1.bigintColumn
            FROM Table1
            """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery, limit = 3).cast<Table1MSSSQL>()
        val result = df.filter { it[Table1MSSSQL::id] == 1 }
        result[0][Table1MSSSQL::bigintColumn] shouldBe 123456789012345L

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigintColumn"]!!.type shouldBe typeOf<Long?>()
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection, TEST_DATABASE_NAME, 4).values.toList()

        val table1Df = dataframes[0].cast<Table1MSSSQL>()

        table1Df.rowsCount() shouldBe 4
        table1Df.filter { it[Table1MSSSQL::id] > 2 }.rowsCount() shouldBe 2
        table1Df[0][Table1MSSSQL::bigintColumn] shouldBe 123456789012345L
    }

    @Test
    fun `infer nullability`() {
        inferNullability(connection)
    }
}
