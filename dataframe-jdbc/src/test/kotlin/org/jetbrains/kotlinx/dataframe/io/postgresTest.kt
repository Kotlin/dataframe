package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*


private const val URL = "jdbc:postgresql://localhost:5432/test"
private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"

@DataSchema
interface Table1 {
    val id: Int
    val bigintcol: Long
    val bigserialcol: Long
    val booleancol: Boolean
    val boxcol: String
    val byteacol: ByteArray
    val charactercol: String
    val characterncol: String
    val charcol: String
    val circlecol: String
    val datecol: java.sql.Date
    val doublecol: Double
    val integercol: Int
    val intervalcol: String
    val jsoncol: String
    val jsonbcol: String
}

@DataSchema
interface Table2 {
    val id: Int
    val linecol: String
    val lsegcol: String
    val macaddrcol: String
    val moneycol: String
    val numericcol: String
    val pathcol: String
    val pointcol: String
    val polygoncol: String
    val realcol: Float
    val smallintcol: Short
    val smallserialcol: Int
    val serialcol: Int
    val textcol: String
    val timecol: String
    val timewithzonecol: String
    val timestampcol: String
    val timestampwithzonecol: String
    val uuidcol: String
    val xmlcol: String
}

@DataSchema
interface ViewTable {
    val id: Int
    val bigintcol: Long
    val linecol: String
    val numericcol: String
}

class PostgresTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)

            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }

            connection.createStatement().execute(
                """
                  CREATE TABLE IF NOT EXISTS table1 (
                id serial PRIMARY KEY,
                bigintCol bigint,
                bigserialCol bigserial,
                booleanCol boolean,
                boxCol box,
                byteaCol bytea,
                characterCol character,
                characterNCol character(10),
                charCol char,
                circleCol circle,
                dateCol date,
                doubleCol double precision,
                integerCol integer,
                intervalCol interval,
                jsonCol json,
                jsonbCol jsonb
            )
            """.trimIndent()
            )

            // Create table Sale
            connection.createStatement().execute(
                """
                CREATE TABLE IF NOT EXISTS table2 (
                id serial PRIMARY KEY,
                lineCol line,
                lsegCol lseg,
                macaddrCol macaddr,
                moneyCol money,
                numericCol numeric,
                pathCol path,
                pointCol point,
                polygonCol polygon,
                realCol real,
                smallintCol smallint,
                smallserialCol smallserial,
                serialCol serial,
                textCol text,
                timeCol time,
                timeWithZoneCol time with time zone,
                timestampCol timestamp,
                timestampWithZoneCol timestamp with time zone,
                uuidCol uuid,
                xmlCol xml
            )
            """.trimIndent()
            )

            val insertData1 = """
            INSERT INTO table1 (
                bigintCol, bigserialCol,  booleanCol, 
                boxCol, byteaCol, characterCol, characterNCol, charCol, 
                 circleCol, dateCol, doubleCol, 
                integerCol, intervalCol, jsonCol, jsonbCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
            val insertData2 = """
            INSERT INTO table2 (
                lineCol, lsegCol, macaddrCol, moneyCol, numericCol, 
                pathCol, pointCol, polygonCol, realCol, smallintCol, 
                smallserialCol, serialCol, textCol, timeCol, 
                timeWithZoneCol, timestampCol, timestampWithZoneCol, 
                uuidCol, xmlCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

            connection.prepareStatement(insertData1).use { st ->
                // Insert data into table1
                for (i in 1..3) {
                    st.setLong(1, i * 1000L)
                    st.setLong(2, 1000000000L + i)
                    st.setBoolean(3, i % 2 == 1)
                    st.setObject(4, org.postgresql.geometric.PGbox("(1,1),(2,2)"))
                    st.setBytes(5, byteArrayOf(1, 2, 3))
                    st.setString(6, "A")
                    st.setString(7, "Hello")
                    st.setString(8, "A")
                    st.setObject(9, org.postgresql.geometric.PGcircle("<(1,2),3>"))
                    st.setDate(10, java.sql.Date.valueOf("2023-08-01"))
                    st.setDouble(11, 12.34)
                    st.setInt(12, 12345 * i)
                    st.setObject(13, org.postgresql.util.PGInterval("1 year"))

                    val jsonbObject = PGobject()
                    jsonbObject.type = "jsonb"
                    jsonbObject.value = "{\"key\": \"value\"}"

                    st.setObject(14, jsonbObject)
                    st.setObject(15, jsonbObject)
                    st.executeUpdate()
                }
            }

            connection.prepareStatement(insertData2).use { st ->
                // Insert data into table2
                for (i in 1..3) {
                    st.setObject(1, org.postgresql.geometric.PGline("{1,2,3}"))
                    st.setObject(2, org.postgresql.geometric.PGlseg("[(-1,0),(1,0)]"))

                    val macaddrObject = PGobject()
                    macaddrObject.type = "macaddr"
                    macaddrObject.value = "00:00:00:00:00:0$i"

                    st.setObject(3, macaddrObject)
                    st.setBigDecimal(4, BigDecimal("123.45"))
                    st.setBigDecimal(5, BigDecimal("12.34"))
                    st.setObject(6, org.postgresql.geometric.PGpath("((1,2),(3,$i))"))
                    st.setObject(7, org.postgresql.geometric.PGpoint("(1,2)"))
                    st.setObject(8, org.postgresql.geometric.PGpolygon("((1,1),(2,2),(3,3))"))
                    st.setFloat(9, 12.34f)
                    st.setShort(10, (i * 100).toShort())
                    st.setInt(11, 1000 + i)
                    st.setInt(12, 1000000 + i)
                    st.setString(13, "Text data $i")
                    st.setTime(14, java.sql.Time.valueOf("12:34:56"))

                    st.setTimestamp(15, java.sql.Timestamp(System.currentTimeMillis()))
                    st.setTimestamp(16, java.sql.Timestamp(System.currentTimeMillis()))
                    st.setTimestamp(17, java.sql.Timestamp(System.currentTimeMillis()))

                    st.setObject(18, UUID.randomUUID(), java.sql.Types.OTHER)
                    val xmlObject = PGobject()
                    xmlObject.type = "xml"
                    xmlObject.value = "<root><element>data</element></root>"

                    st.setObject(19, xmlObject)
                    st.executeUpdate()
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun `read from tables`() {
        val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1>()
        df1.print()
        assertEquals(3, df1.rowsCount())

        val df2 = DataFrame.readSqlTable(connection, "table2").cast<Table2>()
        df2.print()
        assertEquals(3, df2.rowsCount())
    }

    @Test
    fun `read from sql query`() {
        val sqlQuery = """
SELECT
    t1.id AS t1_id,
    t1.bigintCol,
    t2.lineCol,
    t2.numericCol
FROM table1 t1
JOIN table2 t2 ON t1.id = t2.id;
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<TestTableData>()
        df.print()
        assertEquals(3, df.rowsCount())
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllTables(connection)

        val table1Df = dataframes[0].cast<Table1>()

        assertEquals(3, table1Df.rowsCount())
        assertEquals(2, table1Df.filter { it[Table1::integercol] > 12345 }.rowsCount())
        assertEquals(1000L, table1Df[0][1])

        val table2Df = dataframes[1].cast<Table2>()

        assertEquals(3, table2Df.rowsCount())
        assertEquals(1, table2Df.filter { it[Table2::pathcol] == "((1,2),(3,1))" }.rowsCount())
        assertEquals(1001, table2Df[0][11])
    }
}
