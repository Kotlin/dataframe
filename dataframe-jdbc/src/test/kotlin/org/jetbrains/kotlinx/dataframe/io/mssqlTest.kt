package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.reflect.typeOf

private const val URL = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true"
private const val USER_NAME = "root"
private const val PASSWORD = "pass"
private const val TEST_DATABASE_NAME = "testKDFdatabase"

@DataSchema
interface Table1MSSSQL {
    val id: Int
    val bitCol: Boolean
    val tinyintCol: Int
    val smallintCol: Short?
    val mediumintCol: Int
    val mediumintUnsignedCol: Int
    val integerCol: Int
    val intCol: Int
    val integerUnsignedCol: Long
    val bigintCol: Long
    val floatCol: Float
    val doubleCol: Double
    val decimalCol: BigDecimal
    val dateCol: String
    val datetimeCol: String
    val timestampCol: String
    val timeCol: String
    val yearCol: String
    val varcharCol: String
    val charCol: String
    val binaryCol: ByteArray
    val varbinaryCol: ByteArray
    val tinyblobCol: ByteArray
    val blobCol: ByteArray
    val mediumblobCol: ByteArray
    val longblobCol: ByteArray
    val textCol: String
    val mediumtextCol: String
    val longtextCol: String
    val enumCol: String
    val setCol: Char
    val jsonCol: String
}

class MSSQLTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)

            connection.createStatement().use { st ->
                // Drop the test database if it exists
             //   val dropDatabaseQuery = "DROP DATABASE IF EXISTS $TEST_DATABASE_NAME"
           //     st.executeUpdate(dropDatabaseQuery)

                // Create the test database
            //    val createDatabaseQuery = "CREATE DATABASE $TEST_DATABASE_NAME"
            //    st.executeUpdate(createDatabaseQuery)

                // Use the newly created database
                val useDatabaseQuery = "USE $TEST_DATABASE_NAME"
                st.executeUpdate(useDatabaseQuery)
            }

           // connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
           // connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }
// TODO: create if not exists is too complex https://forums.sqlteam.com/t/create-table-if-not-exists/22596/4
            @Language("SQL")
            val createTableQuery = """
  CREATE TABLE Table1 (
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

            // TODO: timestamp column could be removed
/*            connection.createStatement().execute(
                createTableQuery.trimIndent()
            )*/
/*
            @Language("SQL")
            val createTableQuery2 = """
                CREATE TABLE IF NOT EXISTS table2 (
                id INT AUTO_INCREMENT PRIMARY KEY,
                bitCol BIT,
                tinyintCol TINYINT,
                smallintCol SMALLINT,
                mediumintCol MEDIUMINT,
                mediumintUnsignedCol MEDIUMINT UNSIGNED,
                integerCol INTEGER,
                intCol INT,
                integerUnsignedCol INTEGER UNSIGNED,
                bigintCol BIGINT,
                floatCol FLOAT,
                doubleCol DOUBLE,
                decimalCol DECIMAL,
                dateCol DATE,
                datetimeCol DATETIME,
                timestampCol TIMESTAMP,
                timeCol TIME,
                yearCol YEAR,
                varcharCol VARCHAR(255),
                charCol CHAR(10),
                binaryCol BINARY(64),
                varbinaryCol VARBINARY(128),
                tinyblobCol TINYBLOB,
                blobCol BLOB,
                mediumblobCol MEDIUMBLOB,
                longblobCol LONGBLOB,
                textCol TEXT,
                mediumtextCol MEDIUMTEXT,
                longtextCol LONGTEXT,
                enumCol ENUM('Value1', 'Value2', 'Value3'),
                setCol SET('Option1', 'Option2', 'Option3')
            )
            """
            connection.createStatement().execute(
                createTableQuery2.trimIndent()
            )
*/
            @Language("SQL")
            val insertData1 = """
    INSERT INTO Table1 (
        bigintColumn, binaryColumn, bitColumn, charColumn, dateColumn, datetime3Column, datetime2Column,
        datetimeoffset2Column, decimalColumn, floatColumn, imageColumn, intColumn, moneyColumn, ncharColumn,
        ntextColumn, numericColumn, nvarcharColumn, nvarcharMaxColumn, realColumn, smalldatetimeColumn,
        smallintColumn, smallmoneyColumn, textColumn, timeColumn, timestampColumn, tinyintColumn,
        uniqueidentifierColumn, varbinaryColumn, varbinaryMaxColumn, varcharColumn, varcharMaxColumn,
        xmlColumn, sqlvariantColumn, geometryColumn, geographyColumn
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""".trimIndent()


          /*  @Language("SQL")
            val insertData2 = """
            INSERT INTO table2 (
                bitCol, tinyintCol, smallintCol, mediumintCol, mediumintUnsignedCol, integerCol, intCol, 
                integerUnsignedCol, bigintCol, floatCol, doubleCol, decimalCol, dateCol, datetimeCol, timestampCol,
                timeCol, yearCol, varcharCol, charCol, binaryCol, varbinaryCol, tinyblobCol, blobCol,
                mediumblobCol, longblobCol, textCol, mediumtextCol, longtextCol, enumCol, setCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()*/

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
                    st.setString(15, "Sample text") // ntextColumn
                    st.setBigDecimal(16, BigDecimal("1234.56")) // numericColumn
                    st.setString(17, "Sample") // nvarcharColumn
                    st.setString(18, "Sample text") // nvarcharMaxColumn
                    st.setFloat(19, 123.45f) // realColumn
                    st.setTimestamp(20, java.sql.Timestamp(System.currentTimeMillis())) // smalldatetimeColumn
                    st.setInt(21, 123) // smallintColumn
                    st.setBigDecimal(22, BigDecimal("123.45")) // smallmoneyColumn
                    st.setString(23, "Sample text") // textColumn
                    st.setTime(24, java.sql.Time(System.currentTimeMillis())) // timeColumn
                    st.setTimestamp(25, java.sql.Timestamp(System.currentTimeMillis())) // timestampColumn
                    st.setInt(26, 123) // tinyintColumn
                    //st.setObject(27, null) // udtColumn (assuming nullable)
                    st.setObject(27, UUID.randomUUID()) // uniqueidentifierColumn
                    st.setBytes(28, byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x67, 0x67, 0x67, 0x67)) // varbinaryColumn
                    st.setBytes(29, byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x67, 0x67, 0x67, 0x67)) // varbinaryMaxColumn
                    st.setString(30, "Sample") // varcharColumn
                    st.setString(31, "Sample text") // varcharMaxColumn
                    st.setString(32, "<xml>Sample</xml>") // xmlColumn
                    st.setString(33, "SQL_VARIANT") // sqlvariantColumn
                    st.setBytes(34,
                        byteArrayOf(0xE6.toByte(), 0x10, 0x00, 0x00, 0x01, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x44, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x05, 0x4C, 0x0)) // geometryColumn
                    st.setString(35, "POINT(1 1)") // geographyColumn
                   // st.executeUpdate()
                }
            }

           /* connection.prepareStatement(insertData2).use { st ->
                // Insert data into table2
                for (i in 1..3) {
                    st.setBoolean(1, false)
                    st.setByte(2, (i * 2).toByte())
                    st.setShort(3, (i * 20).toShort())
                    st.setInt(4, i * 200)
                    st.setInt(5, i * 200)
                    st.setInt(6, i * 200)
                    st.setInt(7, i * 200)
                    st.setInt(8, i * 200)
                    st.setInt(9, i * 200)
                    st.setFloat(10, i * 20.0f)
                    st.setDouble(11, i * 20.0)
                    st.setBigDecimal(12, BigDecimal(i * 20))
                    st.setDate(13, java.sql.Date(System.currentTimeMillis()))
                    st.setTimestamp(14, java.sql.Timestamp(System.currentTimeMillis()))
                    st.setTimestamp(15, java.sql.Timestamp(System.currentTimeMillis()))
                    st.setTime(16, java.sql.Time(System.currentTimeMillis()))
                    st.setInt(17, 2023)
                    st.setString(18, "varcharValue$i")
                    st.setString(19, "charValue$i")
                    st.setBytes(20, "binaryValue".toByteArray())
                    st.setBytes(21, "varbinaryValue".toByteArray())
                    st.setBytes(22, "tinyblobValue".toByteArray())
                    st.setBytes(23, "blobValue".toByteArray())
                    st.setBytes(24, "mediumblobValue".toByteArray())
                    st.setBytes(25, "longblobValue".toByteArray())
                    st.setString(26, null)
                    st.setString(27, null)
                    st.setString(28, "longtextValue$i")
                    st.setString(29, "Value$i")
                    st.setString(30, "Option$i")
                    st.executeUpdate()
                }
            }*/
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            /*try {
                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }
                connection.createStatement().use { st -> st.execute("DROP DATABASE IF EXISTS $TEST_DATABASE_NAME") }
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }*/
        }
    }

    @Test
    fun `basic test for reading sql tables`() {
        val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1MSSSQL>()
        val result = df1.filter { it[Table1MSSSQL::id] == 1 }
        result[0][26] shouldBe "textValue1"

        /*val schema = DataFrame.getSchemaForSqlTable(connection, "table1")
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["textCol"]!!.type shouldBe typeOf<String>()

        val df2 = DataFrame.readSqlTable(connection, "table2").cast<Table2MSSQL>()
        val result2 = df2.filter { it[Table2MSSQL::id] == 1 }
        result2[0][26] shouldBe null

        val schema2 = DataFrame.getSchemaForSqlTable(connection, "table2")
        schema2.columns["id"]!!.type shouldBe typeOf<Int>()
        schema2.columns["textCol"]!!.type shouldBe typeOf<String?>()*/
    }

    @Test
    fun `read from sql query`() {
     /*   @Language("SQL")
        val sqlQuery = """
            SELECT
               t1.id,
               t1.enumCol,
               t2.setCol
            FROM table1 t1
            JOIN table2 t2 ON t1.id = t2.id
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<Table3MSSQL>()
        val result = df.filter { it[Table3MSSQL::id] == 1 }
        result[0][2] shouldBe "Option1"

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["enumCol"]!!.type shouldBe typeOf<Char>()
        schema.columns["setCol"]!!.type shouldBe typeOf<Char?>()*/
    }

    @Test
    fun `read from all tables`() {
       /* val dataframes = DataFrame.readAllSqlTables(connection, TEST_DATABASE_NAME, 1000)

        val table1Df = dataframes[0].cast<Table1MSSSQL>()

        table1Df.rowsCount() shouldBe 3
        table1Df.filter { it[Table1MSSSQL::integerCol] > 100 }.rowsCount() shouldBe 2
        table1Df[0][11] shouldBe 10.0
        table1Df[0][26] shouldBe "textValue1"
        table1Df[0][31] shouldBe JSON_STRING // TODO: https://github.com/Kotlin/dataframe/issues/462

        val table2Df = dataframes[1].cast<Table2MSSQL>()

        table2Df.rowsCount() shouldBe 3
        table2Df.filter { it[Table2MSSQL::integerCol] != null && it[Table2MSSQL::integerCol]!! > 400 }
            .rowsCount() shouldBe 1
        table2Df[0][11] shouldBe 20.0
        table2Df[0][26] shouldBe null*/
    }

    @Test
    fun `reading numeric types`() {
     /*   val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1MSSSQL>()

        val result = df1.select("tinyintCol")
            .add("tinyintCol2") { it[Table1MSSSQL::tinyintCol] }

        result[0][1] shouldBe 1

        val result1 = df1.select("smallintCol")
            .add("smallintCol2") { it[Table1MSSSQL::smallintCol] }

        result1[0][1] shouldBe 10

        val result2 = df1.select("mediumintCol")
            .add("mediumintCol2") { it[Table1MSSSQL::mediumintCol] }

        result2[0][1] shouldBe 100

        val result3 = df1.select("mediumintUnsignedCol")
            .add("mediumintUnsignedCol2") { it[Table1MSSSQL::mediumintUnsignedCol] }

        result3[0][1] shouldBe 100

        val result4 = df1.select("integerUnsignedCol")
            .add("integerUnsignedCol2") { it[Table1MSSSQL::integerUnsignedCol] }

        result4[0][1] shouldBe 100L

        val result5 = df1.select("bigintCol")
            .add("bigintCol2") { it[Table1MSSSQL::bigintCol] }

        result5[0][1] shouldBe 100

        val result6 = df1.select("floatCol")
            .add("floatCol2") { it[Table1MSSSQL::floatCol] }

        result6[0][1] shouldBe 10.0f

        val result7 = df1.select("doubleCol")
            .add("doubleCol2") { it[Table1MSSSQL::doubleCol] }

        result7[0][1] shouldBe 10.0

        val result8 = df1.select("decimalCol")
            .add("decimalCol2") { it[Table1MSSSQL::decimalCol] }

        result8[0][1] shouldBe BigDecimal("10")

        val schema = DataFrame.getSchemaForSqlTable(connection, "table1")

        schema.columns["tinyintCol"]!!.type shouldBe typeOf<Int>()
        schema.columns["smallintCol"]!!.type shouldBe typeOf<Short?>()
        schema.columns["mediumintCol"]!!.type shouldBe typeOf<Int>()
        schema.columns["mediumintUnsignedCol"]!!.type shouldBe typeOf<Int>()
        schema.columns["integerUnsignedCol"]!!.type shouldBe typeOf<Long>()
        schema.columns["bigintCol"]!!.type shouldBe typeOf<Long>()
        schema.columns["floatCol"]!!.type shouldBe typeOf<Float>()
        schema.columns["doubleCol"]!!.type shouldBe typeOf<Double>()
        schema.columns["decimalCol"]!!.type shouldBe typeOf<BigDecimal>()*/
    }
}
