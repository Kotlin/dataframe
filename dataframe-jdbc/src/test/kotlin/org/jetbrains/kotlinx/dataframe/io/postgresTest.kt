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
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.Ignore
import kotlin.reflect.typeOf

private const val URL = "jdbc:postgresql://localhost:5432/test"
private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"

@DataSchema
interface Table1 {
    val id: Int
    val bigintcol: Long
    val smallintcol: Int
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
    val integercol: Int?
    val intervalcol: String
    val jsoncol: String
    val jsonbcol: String
}

@DataSchema
interface Table2 {
    val id: Int
    val linecol: org.postgresql.geometric.PGline
    val lsegcol: String
    val macaddrcol: String
    val moneycol: String
    val numericcol: BigDecimal
    val pathcol: org.postgresql.geometric.PGpath
    val pointcol: String
    val polygoncol: String
    val realcol: Float
    val smallintcol: Int
    val smallserialcol: Int
    val serialcol: Int
    val textcol: String?
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
    val textCol: String?
}

//@Ignore
class PostgresTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)

            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }

            @Language("SQL")
            val createTableStatement = """
                CREATE TABLE IF NOT EXISTS table1 (
                id serial PRIMARY KEY,
                bigintCol bigint not null,
                smallintCol smallint not null,
                bigserialCol bigserial not null,
                booleanCol boolean not null,
                boxCol box not null,
                byteaCol bytea not null,
                characterCol character not null,
                characterNCol character(10) not null,
                charCol char not null,
                circleCol circle not null,
                dateCol date not null,
                doubleCol double precision not null,
                integerCol integer,
                intervalCol interval not null,
                jsonCol json not null,
                jsonbCol jsonb not null
            )
            """
            connection.createStatement().execute(
                createTableStatement.trimIndent()
            )

            @Language("SQL")
            val createTableQuery = """
                CREATE TABLE IF NOT EXISTS table2 (
                id serial PRIMARY KEY,
                lineCol line not null,
                lsegCol lseg not null,
                macaddrCol macaddr not null,
                moneyCol money not null,
                numericCol numeric not null,
                pathCol path not null,
                pointCol point not null,
                polygonCol polygon not null,
                realCol real not null,
                smallintCol smallint not null,
                smallserialCol smallserial not null,
                serialCol serial not null,
                textCol text,
                timeCol time not null,
                timeWithZoneCol time with time zone not null,
                timestampCol timestamp not null,
                timestampWithZoneCol timestamp with time zone not null,
                uuidCol uuid not null,
                xmlCol xml not null
            )
            """
            connection.createStatement().execute(
                createTableQuery.trimIndent()
            )

            @Language("SQL")
            val insertData1 = """
            INSERT INTO table1 (
                bigintCol, smallintCol, bigserialCol,  booleanCol, 
                boxCol, byteaCol, characterCol, characterNCol, charCol, 
                 circleCol, dateCol, doubleCol, 
                integerCol, intervalCol, jsonCol, jsonbCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """

            @Language("SQL")
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
                    st.setShort(2, 11.toShort())
                    st.setLong(3, 1000000000L + i)
                    st.setBoolean(4, i % 2 == 1)
                    st.setObject(5, org.postgresql.geometric.PGbox("(1,1),(2,2)"))
                    st.setBytes(6, byteArrayOf(1, 2, 3))
                    st.setString(7, "A")
                    st.setString(8, "Hello")
                    st.setString(9, "A")
                    st.setObject(10, org.postgresql.geometric.PGcircle("<(1,2),3>"))
                    st.setDate(11, java.sql.Date.valueOf("2023-08-01"))
                    st.setDouble(12, 12.34)
                    st.setInt(13, 12345 * i)
                    st.setObject(14, org.postgresql.util.PGInterval("1 year"))

                    val jsonbObject = PGobject()
                    jsonbObject.type = "jsonb"
                    jsonbObject.value = "{\"key\": \"value\"}"

                    st.setObject(15, jsonbObject)
                    st.setObject(16, jsonbObject)
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
                    st.setString(13, null)
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
        val tableName1 = "table1"
        val df1 = DataFrame.readSqlTable(connection, tableName1).cast<Table1>()
        val result = df1.filter { it[Table1::id] == 1 }

        result[0][2] shouldBe 11
        result[0][13] shouldBe 12345

        val schema = DataFrame.getSchemaForSqlTable(connection, tableName1)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["integercol"]!!.type shouldBe typeOf<Int?>()
        schema.columns["smallintcol"]!!.type shouldBe typeOf<Int>()
        schema.columns["circlecol"]!!.type shouldBe typeOf<Any>()

        val tableName2 = "table2"
        val df2 = DataFrame.readSqlTable(connection, tableName2).cast<Table2>()
        val result2 = df2.filter { it[Table2::id] == 1 }
        result2[0][11] shouldBe 1001
        result2[0][13] shouldBe null

        val schema2 = DataFrame.getSchemaForSqlTable(connection, tableName2)
        schema2.columns["id"]!!.type shouldBe typeOf<Int>()
        schema2.columns["pathcol"]!!.type shouldBe typeOf<Any>() // TODO: https://github.com/Kotlin/dataframe/issues/537
        schema2.columns["textcol"]!!.type shouldBe typeOf<String?>()
        schema2.columns["linecol"]!!.type shouldBe typeOf<Any>() // TODO: https://github.com/Kotlin/dataframe/issues/537
    }

    @Test
    fun `read from sql query`() {
        @Language("SQL")
        val sqlQuery = """
            SELECT
                t1.id,
                t1.bigintCol,
                t2.lineCol,
                t2.textCol
            FROM table1 t1
            JOIN table2 t2 ON t1.id = t2.id
        """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<ViewTable>()
        val result = df.filter { it[ViewTable::id] == 1 }
        result[0][3] shouldBe null

        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigintcol"]!!.type shouldBe typeOf<Long>()
        schema.columns["textcol"]!!.type shouldBe typeOf<String?>()
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection)

        val table1Df = dataframes[0].cast<Table1>()

        table1Df.rowsCount() shouldBe 3
        table1Df.filter { it[Table1::integercol] != null && it[Table1::integercol]!! > 12345 }.rowsCount() shouldBe 2
        table1Df[0][1] shouldBe 1000L
        table1Df[0][2] shouldBe 11

        val table2Df = dataframes[1].cast<Table2>()

        table2Df.rowsCount() shouldBe 3
        table2Df.filter { it[Table2::pathcol] == org.postgresql.geometric.PGpath("((1,2),(3,1))") }
            .rowsCount() shouldBe 1
        table2Df[0][11] shouldBe 1001
    }

    @Test
    fun `read columns of different types to check type mapping`() {
        val tableName1 = "table1"
        val df1 = DataFrame.readSqlTable(connection, tableName1).cast<Table1>()
        val result = df1.select ( "smallintcol" ).add("smallintcol2") {it[Table1::smallintcol]}
        result[0][1] shouldBe 11

        val result1 = df1.select ( "bigserialcol" ).add("bigserialcol2") {it[Table1::bigserialcol]}
        result1[0][1] shouldBe 1000000001L

        val result2 = df1.select ( "doublecol" ).add("doublecol2") {it[Table1::doublecol]}
        result2[0][1] shouldBe 12.34

        val tableName2 = "table2"
        val df2 = DataFrame.readSqlTable(connection, tableName2).cast<Table2>()

        val result3 = df2.select ( "moneycol" ).add("moneycol2") {it[Table2::moneycol]}
        result3[0][1] shouldBe "123,45 ?" // TODO: weird mapping

        val result4 = df2.select ( "numericcol" ).add("numericcol2") {it[Table2::numericcol]}
        result4[0][1] shouldBe BigDecimal("12.34")

        val result5 = df2.select ( "realcol" ).add("realcol2") {it[Table2::realcol]}
        result5[0][1] shouldBe 12.34f

        val result7 = df2.select ( "smallserialcol" ).add("smallserialcol2") {it[Table2::smallserialcol]}
        result7[0][1] shouldBe 1001

        val result8 = df2.select ( "serialcol" ).add("serialcol2") {it[Table2::serialcol]}
        result8[0][1] shouldBe 1000001

        val schema = DataFrame.getSchemaForSqlTable(connection, tableName1)
        schema.columns["smallintcol"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigserialcol"]!!.type shouldBe typeOf<Long>()
        schema.columns["doublecol"]!!.type shouldBe typeOf<Double>()

        val schema1 = DataFrame.getSchemaForSqlTable(connection, tableName2)
        schema1.columns["moneycol"]!!.type shouldBe typeOf<String>()
        schema1.columns["numericcol"]!!.type shouldBe typeOf<BigDecimal>()
        schema1.columns["realcol"]!!.type shouldBe typeOf<Float>()
        schema1.columns["smallserialcol"]!!.type shouldBe typeOf<Int>()
        schema1.columns["serialcol"]!!.type shouldBe typeOf<Int>()
    }
}
