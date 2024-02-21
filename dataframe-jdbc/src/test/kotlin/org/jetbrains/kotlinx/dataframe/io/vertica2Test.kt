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
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.reflect.typeOf

// Run with https://hub.docker.com/r/vertica/vertica-ce
private const val URL = "jdbc:vertica://localhost:5433"
private const val USER_NAME = "dbadmin"
private const val PASSWORD = ""
private const val TEST_SCHEMA_NAME = "testschema"

@DataSchema
interface Table1Vertica {
    val id: Int
    val boolCol: Boolean
    val charCol: Char
    val varcharCol: String
    val longvarcharCol: String
    val dateCol: Date
    val timeCol: Time
    val timestampCol: Timestamp
    val doubleprecisionCol: Float
    val floatCol: Float
    val float8Col: Float
    val realCol: Float
    val integerCol: Long
    val intCol: Long
    val bigintCol: Long
    val int8Col: Long
    val smallintCol: Long
    val tinyintCol: Long
    val decimalCol: BigDecimal
    val numericCol: BigDecimal
    val numberCol: BigDecimal
    val moneyCol: BigDecimal
    val geometryCol: String
    val geographyCol: String
}

class VerticaTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)

            connection.createStatement().use { st ->
                // Drop the test schema if it exists
                val dropSchemaQuery = "DROP SCHEMA IF EXISTS $TEST_SCHEMA_NAME"
                st.executeUpdate(dropSchemaQuery)

                // Create the test schema
                val createSchemaQuery = "CREATE SCHEMA $TEST_SCHEMA_NAME"
                st.executeUpdate(createSchemaQuery)

//                 Set the schema as the default schema
                val setDefaultSchemaQuery = "SET SEARCH_PATH TO $TEST_SCHEMA_NAME"
                st.execute(setDefaultSchemaQuery)
            }

            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS table1")
            }

            @Language("SQL")
            val createTableQuery = """
            CREATE TABLE IF NOT EXISTS table1 (
                id INT NOT NULL PRIMARY KEY,
                boolCol BOOLEAN,
                charCol CHAR(10),
                varcharCol VARCHAR,
                longvarcharCol LONG VARCHAR,
                binaryCol BINARY(11),
                varbinaryCol VARBINARY,
                longvarbinaryCol LONG VARBINARY,
                dateCol DATE,
                timeCol TIME,
                timestampCol TIMESTAMP,
                doubleprecisionCol DOUBLE PRECISION,
                floatCol FLOAT,
                float8Col FLOAT8,
                realCol REAL,
                integerCol INTEGER,
                intCol INT,
                bigintCol BIGINT,
                int8Col INT8,
                smallintCol SMALLINT,
                tinyintCol TINYINT,
                decimalCol DECIMAL,
                numericCol NUMERIC,
                numberCol NUMBER,
                moneyCol MONEY,
                geometryCol GEOMETRY,
                geographyCol GEOGRAPHY
            )
            """

            connection.createStatement().execute(
                createTableQuery.trimIndent()
            )

            @Language("SQL")
            val insertData1 = """
            INSERT INTO table1 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText('POINT(1 1)'), ST_GeographyFromText('POLYGON((1 2,3 4,2 3,1 2))'))
        """.trimIndent()

            connection.prepareStatement(insertData1).use { st ->
                // Insert data into table1
                for (i in 1..3) {
                    st.setInt(1, i)
                    st.setBoolean(2, true)
                    st.setString(3, "charValue$i")
                    st.setString(4, "varcharValue$i")
                    st.setString(5, "longvarcharValue$i")
                    st.setBytes(6, "binaryValue".toByteArray())
                    st.setBytes(7, "varbinaryValue".toByteArray())
                    st.setBytes(8, "longvarbinaryValue".toByteArray())
                    st.setDate(9, java.sql.Date.valueOf(LocalDate.of(2024, 1, 1)))
                    st.setTime(10, Time.valueOf(LocalTime.of(10, 0, 0)))
                    st.setTimestamp(11, Timestamp.valueOf(LocalDateTime.of(2024, 1,1, 10,0,0)))
                    st.setFloat(12, i * 10.0f)
                    st.setFloat(13, i * 10.0f)
                    st.setFloat(14, i * 10.0f)
                    st.setFloat(15, i * 10.0f)
                    st.setInt(16, i * 100)
                    st.setInt(17, i * 100)
                    st.setInt(18, i * 100)
                    st.setInt(19, i * 100)
                    st.setInt(20, i * 100)
                    st.setInt(21, i * 100)
                    st.setBigDecimal(22, BigDecimal(i * 10))
                    st.setBigDecimal(23, BigDecimal(i * 10))
                    st.setBigDecimal(24, BigDecimal(i * 10))
                    st.setBigDecimal(25, BigDecimal(i * 20))
                    st.setBigDecimal(26, BigDecimal(i * 30))
                    st.executeUpdate()
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
                connection.createStatement().use { st -> st.execute("DROP SCHEMA IF EXISTS $TEST_SCHEMA_NAME") }
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun `basic test for reading sql tables`() {
        val df = DataFrame.readSqlTable(connection, "table1").cast<Table1Vertica>()
        df.rowsCount() shouldBe 3
        val result = df.filter { it[Table1Vertica::id] == 1 }
        result[0][0] shouldBe 1L
        result[0][1] shouldBe true
        result[0][2] shouldBe "charValue1"
        result[0][3] shouldBe "varcharValue1"
        result[0][4] shouldBe "longvarcharValue1"
        result[0][5] shouldBe "binaryValue".toByteArray()
        result[0][6] shouldBe "varbinaryValue".toByteArray()
        result[0][7] shouldBe "longvarbinaryValue".toByteArray()
        result[0][8] shouldBe java.sql.Date.valueOf(LocalDate.of(2024, 1, 1))
        result[0][9] shouldBe Time.valueOf(LocalTime.of(10, 0, 0))
        result[0][10] shouldBe Timestamp.valueOf(LocalDateTime.of(2024, 1,1, 10,0,0))
        result[0][11] shouldBe 1 * 10.0f
        result[0][12] shouldBe 1 * 10.0f
        result[0][13] shouldBe 1 * 10.0f
        result[0][14] shouldBe 1 * 10.0f
        result[0][15] shouldBe 1 * 100
        result[0][16] shouldBe 1 * 100
        result[0][17] shouldBe 1 * 100
        result[0][18] shouldBe 1 * 100
        result[0][19] shouldBe 1 * 100
        result[0][20] shouldBe 1 * 100
        result[0][21] shouldBe BigDecimal("10.000000000000000")
        result[0][22] shouldBe BigDecimal("10.000000000000000")
        result[0][23] shouldBe BigDecimal("10")
        result[0][24] shouldBe BigDecimal("10.0000")
        result[0][25] shouldBe BigDecimal("11.0000")
        result[0][26] shouldBe BigDecimal("12.0000")

        val schema = DataFrame.getSchemaForSqlTable(connection, "table1")
        schema.columns["id"]!!.type shouldBe typeOf<Long>()
        schema.columns["boolCol"]!!.type shouldBe typeOf<Boolean?>()
        schema.columns["charCol"]!!.type shouldBe typeOf<Char?>()
        schema.columns["varcharCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["longvarcharCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["binaryCol"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["varbinaryCol"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["longvarbinaryCol"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["dateCol"]!!.type shouldBe typeOf<Date?>()
        schema.columns["timeCol"]!!.type shouldBe typeOf<Time?>()
        schema.columns["timestampCol"]!!.type shouldBe typeOf<Timestamp?>()
        schema.columns["doubleprecisionCol"]!!.type shouldBe typeOf<Double?>()
        schema.columns["floatCol"]!!.type shouldBe typeOf<Double?>()
        schema.columns["float8Col"]!!.type shouldBe typeOf<Double?>()
        schema.columns["realCol"]!!.type shouldBe typeOf<Double?>()
        schema.columns["integerCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["intCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["bigintCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["int8Col"]!!.type shouldBe typeOf<Long?>()
        schema.columns["smallintCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["tinyintCol"]!!.type shouldBe typeOf<Long?>()
        schema.columns["decimalCol"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["numericCol"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["numberCol"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["moneyCol"]!!.type shouldBe typeOf<BigDecimal?>()
        schema.columns["geometryCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["geographyCol"]!!.type shouldBe typeOf<String?>()
    }

//    @Test
//    fun `read from sql query`() {
//        @Language("SQL")
//        val sqlQuery = """
//            SELECT
//               t1.id,
//               t1.enumCol,
//               t2.setCol
//            FROM table1 t1
//            JOIN table2 t2 ON t1.id = t2.id
//        """.trimIndent()
//
//        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<Table3MySql>()
//        val result = df.filter { it[Table3MySql::id] == 1 }
//        result[0][2] shouldBe "Option1"
//
//        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
//        schema.columns["id"]!!.type shouldBe typeOf<Int>()
//        schema.columns["enumCol"]!!.type shouldBe typeOf<Char>()
//        schema.columns["setCol"]!!.type shouldBe typeOf<Char?>()
//    }
//
//    @Test
//    fun `read from all tables`() {
//        val dataframes = DataFrame.readAllSqlTables(connection)
//
//        val table1Df = dataframes[0].cast<Table1MySql>()
//
//        table1Df.rowsCount() shouldBe 3
//        table1Df.filter { it[Table1MySql::integerCol] > 100 }.rowsCount() shouldBe 2
//        table1Df[0][11] shouldBe 10.0
//        table1Df[0][26] shouldBe "textValue1"
//
//        val table2Df = dataframes[1].cast<Table2MySql>()
//
//        table2Df.rowsCount() shouldBe 3
//        table2Df.filter { it[Table2MySql::integerCol] != null && it[Table2MySql::integerCol]!! > 400 }
//            .rowsCount() shouldBe 1
//        table2Df[0][11] shouldBe 20.0
//        table2Df[0][26] shouldBe null
//    }
}
