package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule
import kotlinx.datetime.LocalDateTime
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.ipc.ArrowReader
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.io.File
import java.math.BigDecimal
import java.sql.Date
import java.sql.DriverManager
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.util.UUID

class ArrowDuckDbTest {
    /**
     * https://arrow.apache.org/adbc/current/driver/duckdb.html
     */
    @Test
    fun testDuckDBArrowIntegration() {
        val expected = expectedSimpleDataFrame()
        val query =
            """
            select 'a' as string, 1 as int, CAST(1.0 as FLOAT) as float, CAST(1.0 as DOUBLE) as double, TIMESTAMP '2020-11-23 09:30:25'  as datetime
            UNION ALL SELECT 'b', 2, 2.0, 2.0, TIMESTAMP '2015-05-25 14:20:13'
            UNION ALL SELECT 'c', 3, 3.0, 3.0, TIMESTAMP '2013-06-19 11:20:13'
            UNION ALL SELECT 'd', 4, 4.0, 4.0, TIMESTAMP '2000-01-01 00:00:00'
            """.trimIndent()

        Class.forName("org.duckdb.DuckDBDriver")
        val conn = DriverManager.getConnection("jdbc:duckdb:") as DuckDBConnection
        conn.use {
            val resultSet = it.createStatement().executeQuery(query) as DuckDBResultSet
            val dbArrowReader = resultSet.arrowExportStream(RootAllocator(), 256) as ArrowReader

            Assert.assertTrue(dbArrowReader.javaClass.name.equals("org.apache.arrow.c.ArrowArrayStreamReader"))

            DataFrame.readArrow(dbArrowReader) shouldBe expected
        }
    }

    private fun expectedSimpleDataFrame(): AnyFrame {
        val dates = listOf(
            LocalDateTime(2020, 11, 23, 9, 30, 25),
            LocalDateTime(2015, 5, 25, 14, 20, 13),
            LocalDateTime(2013, 6, 19, 11, 20, 13),
            LocalDateTime(2000, 1, 1, 0, 0, 0),
        )

        return dataFrameOf(
            "string" to listOf("a", "b", "c", "d"),
            "int" to listOf(1, 2, 3, 4),
            "float" to listOf(1.0f, 2.0f, 3.0f, 4.0f),
            "double" to listOf(1.0, 2.0, 3.0, 4.0),
            "datetime" to dates,
        )
    }

    @field:[JvmField Rule]
    val pg: SingleInstancePostgresRule = EmbeddedPostgresRules.singleInstance()

    @Suppress("SqlDialectInspection")
    @Test
    fun `DuckDB Postgres`() {
        val embeddedPg = pg.embeddedPostgres
        val dataSource = embeddedPg.postgresDatabase as PGSimpleDataSource

        val dbname = dataSource.databaseName
        val username = dataSource.user
        val host = dataSource.serverNames.first()
        val port = dataSource.portNumbers.first()

        val connection = dataSource.connection

        // region filling the db

        @Language("SQL")
        val createTableStatement = """
                CREATE TABLE IF NOT EXISTS table1 (
                id serial PRIMARY KEY,
                bigintCol bigint not null,
                smallintCol smallint not null,
                bigserialCol bigserial not null,
                booleanCol boolean not null,
                byteaCol bytea not null,
                characterCol character not null,
                characterNCol character(10) not null,
                charCol char not null,
                dateCol date not null,
                doubleCol double precision not null,
                integerCol integer,
                intArrayCol integer array,
                doubleArrayCol double precision array,
                dateArrayCol date array,
                textArrayCol text array,
                booleanArrayCol boolean array
            )
            """
        connection.createStatement().execute(createTableStatement.trimIndent())

        @Language("SQL")
        val createTableQuery = """
                CREATE TABLE IF NOT EXISTS table2 (
                id serial PRIMARY KEY,
                moneyCol money not null,
                numericCol numeric not null,
                realCol real not null,
                smallintCol smallint not null,
                serialCol serial not null,
                textCol text,
                timeCol time not null,
                timeWithZoneCol time with time zone not null,
                timestampCol timestamp not null,
                timestampWithZoneCol timestamp with time zone not null,
                uuidCol uuid not null
            )
            """
        connection.createStatement().execute(createTableQuery.trimIndent())

        @Language("SQL")
        val insertData1 = """
            INSERT INTO table1 (
                bigintCol, smallintCol, bigserialCol,  booleanCol, 
                byteaCol, characterCol, characterNCol, charCol, 
                dateCol, doubleCol, 
                integerCol, intArrayCol,
                doubleArrayCol, dateArrayCol, textArrayCol, booleanArrayCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """

        @Language("SQL")
        val insertData2 = """
            INSERT INTO table2 (
                moneyCol, numericCol, 
                realCol, smallintCol, 
                serialCol, textCol, timeCol, 
                timeWithZoneCol, timestampCol, timestampWithZoneCol, 
                uuidCol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """

        // TODO these require added support of Arrow's ListVector #1256
        val intArray = connection.createArrayOf("INTEGER", arrayOf(1, 2, 3))
        val doubleArray = connection.createArrayOf("DOUBLE", arrayOf(1.1, 2.2, 3.3))
        val dateArray = connection.createArrayOf(
            "DATE",
            arrayOf(Date.valueOf("2023-08-01"), Date.valueOf("2023-08-02")),
        )
        val textArray = connection.createArrayOf("TEXT", arrayOf("Hello", "World"))
        val booleanArray = connection.createArrayOf("BOOLEAN", arrayOf(true, false, true))

        connection.prepareStatement(insertData1).use { st ->
            // Insert data into table1
            for (i in 1..3) {
                st.setLong(1, i * 1000L)
                st.setShort(2, 11.toShort())
                st.setLong(3, 1000000000L + i)
                st.setBoolean(4, i % 2 == 1)
                st.setBytes(5, byteArrayOf(1, 2, 3))
                st.setString(6, "A")
                st.setString(7, "Hello")
                st.setString(8, "A")
                st.setDate(9, Date.valueOf("2023-08-01"))
                st.setDouble(10, 12.34)
                st.setInt(11, 12345 * i)
                st.setArray(12, intArray)
                st.setArray(13, doubleArray)
                st.setArray(14, dateArray)
                st.setArray(15, textArray)
                st.setArray(16, booleanArray)
                st.executeUpdate()
            }
        }

        connection.prepareStatement(insertData2).use { st ->
            // Insert data into table2
            for (i in 1..3) {
                st.setBigDecimal(1, BigDecimal("123.45"))
                st.setBigDecimal(2, BigDecimal("12.34"))
                st.setFloat(3, 12.34f)
                st.setInt(4, 1000 + i)
                st.setInt(5, 1000000 + i)
                st.setString(6, null)
                st.setTime(7, Time.valueOf("12:34:56"))

                // TODO these require added support of Arrow's TZ TimeStamp Vectors #1257
                st.setTimestamp(8, Timestamp(System.currentTimeMillis()))
                st.setTimestamp(9, Timestamp(System.currentTimeMillis()))
                st.setTimestamp(10, Timestamp(System.currentTimeMillis()))
                st.setObject(11, UUID.randomUUID(), Types.OTHER)
                st.executeUpdate()
            }
        }

        // endregion

        // check whether DuckDB available and loaded
        Class.forName("org.duckdb.DuckDBDriver")

        // Create the connection with duckdb via JDBC DriverManager
        var df1: AnyFrame
        var df2: AnyFrame

        DriverManager.getConnection("jdbc:duckdb:").use {
            it as DuckDBConnection

            // install and load PostgreSQL
            it.createStatement().execute("INSTALL postgres; LOAD postgres;")

            // attach the database and USE it
            it.createStatement().execute(
                "ATTACH 'dbname=$dbname user=$username host=$host port=$port' AS db (TYPE postgres, SCHEMA 'public'); USE db;",
            )

            // and read out the reader from DataFrame!
            df1 = DataFrame.readArrow(
                it.createStatement()
                    .executeQuery("select * from table1;").let { it as DuckDBResultSet }
                    .arrowExportStream(RootAllocator(), 256) as ArrowReader,
            )

            df2 = DataFrame.readArrow(
                it.createStatement()
                    .executeQuery("select * from table2;").let { it as DuckDBResultSet }
                    .arrowExportStream(RootAllocator(), 256) as ArrowReader,
            )
        }

        df1.print(columnTypes = true, borders = true)
        df2.print(columnTypes = true, borders = true)
    }

    @Suppress("SqlDialectInspection")
    @Test
    fun `DuckDB SQLite`() {
        val resourceDb = "chinook.db"
        val dbPath = File(object {}.javaClass.classLoader.getResource(resourceDb)!!.toURI()).absolutePath

        // check whether DuckDB available and loaded
        Class.forName("org.duckdb.DuckDBDriver")

        // Create the connection with duckdb via JDBC DriverManager
        val df = DriverManager.getConnection("jdbc:duckdb:").use {
            it as DuckDBConnection

            // install and load SQLite
            it.createStatement().execute("INSTALL sqlite; LOAD sqlite;")

            // attach the database and USE it
            it.createStatement().execute("ATTACH '$dbPath' as db (TYPE sqlite); USE db;")

            // query it
            val resultSet = it.createStatement()
                .executeQuery(
                    """select * from Customers;""",
                )

            // since we are reading via DuckDB, we can safely cast resultSet to DuckDBResultSet
            resultSet as DuckDBResultSet

            // turn the DuckDBResultSet into an ArrowReader
            val dbArrowReader = resultSet.arrowExportStream(RootAllocator(), 256) as ArrowReader

            // and read out the reader from DataFrame!
            DataFrame.readArrow(dbArrowReader)
        }

        df.print(columnTypes = true, borders = true)
    }
}
