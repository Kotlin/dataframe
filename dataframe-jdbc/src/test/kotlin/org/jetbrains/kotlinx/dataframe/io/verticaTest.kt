package org.jetbrains.kotlinx.dataframe.io

import com.vertica.dsi.dataengine.utilities.TimeTz
import com.vertica.dsi.dataengine.utilities.TimestampTz
import com.vertica.jdbc.VerticaDayTimeInterval
import com.vertica.jdbc.jdbc42.S42Array
import com.vertica.util.VerticaStruct
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
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
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.reflect.typeOf

// Run with https://hub.docker.com/r/vertica/vertica-ce
private const val URL = "jdbc:vertica://localhost:5433"
private const val USER_NAME = "dbadmin"
private const val PASSWORD = ""
private const val TEST_SCHEMA_NAME = "testschema"

@DataSchema
interface Table1Vertica {
    val id: Int
    val varcharCol: String
}

@DataSchema
interface Table2Vertica {
    val id: Int
    val boolCol: Boolean
}

@DataSchema
interface Table3Vertica {
    val id: Int
    val varcharCol: String
    val boolCol: Boolean
}

@Ignore
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
                st.execute("DROP TABLE IF EXISTS table2")
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
                geographyCol GEOGRAPHY,
                timewithtimezoneCol TIMETZ,
                timestampwithtimezoneCol TIMESTAMPTZ,
                uuidCol UUID,
                arrayCol ARRAY[VARCHAR(50)],
                rowCol ROW(street VARCHAR, city VARCHAR),
                setCol SET[VARCHAR],
                intervalCol INTERVAL
            )
            """

            @Language("SQL")
            val createTable2Query = """
            CREATE TABLE IF NOT EXISTS table2 (
                id INT NOT NULL PRIMARY KEY,
                boolCol BOOLEAN
            )
            """

            connection.createStatement().execute(
                createTableQuery.trimIndent()
            )

            connection.createStatement().execute(
                createTable2Query.trimIndent()
            )

            @Language("SQL")
            val insertData1 = """
            INSERT INTO table1 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, public.ST_GeomFromText('POINT(1 1)'), public.ST_GeographyFromText('POLYGON((1 2,3 4,2 3,1 2))'), ?, ?, ?, ARRAY['Test', 'Test1'], ROW('aStreet', 'aCity'), SET['aStreet', 'aCity'], INTERVAL '1 12:59:10:05')
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
                    st.setTimestamp(11, Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 10, 0, 0)))
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
                    st.setBigDecimal(25, BigDecimal(i * 10))
                    st.setTime(26, TimeTz(Time.valueOf(LocalTime.of(10, 0, 0, 0)), Calendar.getInstance()))
                    st.setTimestamp(
                        27,
                        TimestampTz(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 10, 0, 0)), Calendar.getInstance())
                    )
                    st.setString(28, "4a866db2-baa6-442a-a371-1f4b5ee627ba")
                    st.executeUpdate()
                }
            }

            @Language("SQL")
            val insertData2 = """
            INSERT INTO table2 VALUES (?, ?)
        """.trimIndent()

            connection.prepareStatement(insertData2).use { st ->
                // Insert data into table2
                for (i in 1..3) {
                    st.setInt(1, i)
                    st.setBoolean(2, true)
                    st.executeUpdate()
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.createStatement().use { st ->
                    st.execute("DROP TABLE IF EXISTS table1")
                    st.execute("DROP TABLE IF EXISTS table2")
                }
                connection.createStatement().use { st -> st.execute("DROP SCHEMA IF EXISTS $TEST_SCHEMA_NAME") }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        }
    }

    @Test
    fun `basic test for reading sql tables`() {
        connection.createStatement().use { st ->
//                 Set the schema as the default schema
            val setDefaultSchemaQuery = "SET SEARCH_PATH TO $TEST_SCHEMA_NAME"
            st.execute(setDefaultSchemaQuery)
        }

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
        result[0][10] shouldBe Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
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
        result[0][27] shouldBe TimeTz(Time.valueOf(LocalTime.of(10, 0, 0, 0)), Calendar.getInstance())
        (result[0][28] as TimestampTz).time shouldBe TimestampTz(Timestamp.valueOf(LocalDateTime.of(2024, 1,1, 10,0,0)), Calendar.getInstance()).time
        result[0][29] shouldBe UUID.fromString("4a866db2-baa6-442a-a371-1f4b5ee627ba")
        (result[0][30] as S42Array).toString() shouldBe "[\"Test\",\"Test1\"]"
        (result[0][31] as VerticaStruct).toString() shouldBe "{\"street\":\"aStreet\",\"city\":\"aCity\"}"
        (result[0][32] as S42Array).toString() shouldBe "[\"aCity\",\"aStreet\"]"
        (result[0][33] as VerticaDayTimeInterval).toString() shouldBe "1 12:59:10.005000"

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
        schema.columns["geometryCol"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["geographyCol"]!!.type shouldBe typeOf<ByteArray?>()
        schema.columns["timewithtimezoneCol"]!!.type shouldBe typeOf<Time?>()
        schema.columns["timestampwithtimezoneCol"]!!.type shouldBe typeOf<Timestamp?>()
        schema.columns["uuidCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["arrayCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["rowCol"]!!.type shouldBe typeOf<Any?>()
        schema.columns["setCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["intervalCol"]!!.type shouldBe typeOf<Any?>()
    }

        @Test
    fun `read from sql query`() {
            connection.createStatement().use { st ->
//                 Set the schema as the default schema
                val setDefaultSchemaQuery = "SET SEARCH_PATH TO $TEST_SCHEMA_NAME"
                st.execute(setDefaultSchemaQuery)
            }

        @Language("SQL")
        val sqlQuery = """
            SELECT
               t1.id,
               t1.varcharCol,
               t2.boolCol
            FROM table1 t1
            JOIN table2 t2 ON t1.id = t2.id
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<Table3Vertica>()
        val result = df.filter { it[Table3Vertica::id] == 1 }
        result[0][2] shouldBe true

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["id"]!!.type shouldBe typeOf<Long>()
        schema.columns["varcharCol"]!!.type shouldBe typeOf<String?>()
        schema.columns["boolCol"]!!.type shouldBe typeOf<Boolean?>()
    }

    @Test
    fun `read from all tables`() {
        connection.createStatement().use { st ->
//                 Set the schema as the default schema
            val setDefaultSchemaQuery = "SET SEARCH_PATH TO DEFAULT"
            st.execute(setDefaultSchemaQuery)
        }

        val dataframes = DataFrame.readAllSqlTables(connection, limit = 1)

        val table1Df = dataframes.first { it.columnNames().any {column -> column == "geometryCol" } }

        table1Df.columnsCount() shouldBe 34
    }
}
