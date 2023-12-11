package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.UUID
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.postgresql.util.PGobject
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


private const val URL = "jdbc:postgresql://localhost:5432/test"
private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"


interface PostgreSQLTestImages {
    companion object {
        val POSTGRES_TEST_IMAGE = DockerImageName.parse("postgres:9.6.12")
    }
}

class PostgresContainerTest {

    @Test
    fun `read from tables`() {
        PostgreSQLContainer(PostgreSQLTestImages.POSTGRES_TEST_IMAGE).use { postgres ->
            postgres.start()

            val connection = postgres.createConnection("")

            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }


            @Language("SQL")
            val createTableStatement = """
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
            """
            connection.createStatement().execute(
                createTableStatement.trimIndent()
            )

            @Language("SQL")
            val createTableQuery = """
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
            """
            connection.createStatement().execute(
                createTableQuery.trimIndent()
            )

            @Language("SQL")
            val insertData1 = """
            INSERT INTO table1 (
                bigintCol, bigserialCol,  booleanCol, 
                boxCol, byteaCol, characterCol, characterNCol, charCol, 
                 circleCol, dateCol, doubleCol, 
                integerCol, intervalCol, jsonCol, jsonbCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1>()
            df1.rowsCount() shouldBe 3

            val df2 = DataFrame.readSqlTable(connection, "table2").cast<Table2>()
            df2.rowsCount() shouldBe 3

        }
    }
}
