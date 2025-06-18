package org.jetbrains.kotlinx.dataframe.io

import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.mapToFrame
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.junit.Rule
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.math.BigDecimal
import java.sql.Date
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.util.UUID
import kotlin.reflect.KType

private const val URL = "jdbc:duckdb:"

object DuckDb : DbType("duckdb") {
    override val driverClassName = "org.duckdb.DuckDBDriver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? = null

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean =
        tableMetadata.schemaName?.lowercase()?.contains("information_schema") == true ||
            tableMetadata.schemaName?.lowercase()?.contains("system") == true

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? = null
}

class DuckDbTest {

    @Test
    fun `read dataframe from duckdb`() {
        val df = DriverManager.getConnection(URL).use { connection ->
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS test_table (
                    id INTEGER PRIMARY KEY,
                    name VARCHAR,
                    age INTEGER,
                    salary DOUBLE,
                    hire_date DATE
                )
                """.trimIndent(),
            ).executeUpdate()

            connection.prepareStatement(
                """
                INSERT INTO test_table (id, name, age, salary, hire_date)
                VALUES 
                    (1, 'John Doe', 30, 50000.00, '2020-01-15'),
                    (2, 'Jane Smith', 28, 55000.00, '2021-03-20'),
                    (3, 'Bob Johnson', 35, 65000.00, '2019-11-10'),
                    (4, 'Alice Brown', 32, 60000.00, '2020-07-01')
                """.trimIndent(),
            ).executeUpdate()

            DataFrame.readSqlTable(connection, "test_table", dbType = DuckDb)
//            DataFrame.readSqlQuery(connection, "SELECT * from test_table;", dbType = DuckDb)
        }

        df.print(borders = true, columnTypes = true)
    }

    @field:[JvmField Rule]
    val pg: SingleInstancePostgresRule = EmbeddedPostgresRules.singleInstance()

    @Test // TODO
    fun `read postgres to duckdb to dataframe`() {
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
                st.setTimestamp(8, Timestamp(System.currentTimeMillis()))
                st.setTimestamp(9, Timestamp(System.currentTimeMillis()))
                st.setTimestamp(10, Timestamp(System.currentTimeMillis()))
                st.setObject(11, UUID.randomUUID(), Types.OTHER)
                st.executeUpdate()
            }
        }

        // endregion

        var df1: AnyFrame
        var df2: AnyFrame

        DriverManager.getConnection(URL).use { connection ->

            // install and load PostgreSQL
            connection.createStatement().execute("INSTALL postgres; LOAD postgres;")

            // attach the database and USE it
            connection.createStatement().execute(
                "ATTACH 'dbname=$dbname user=$username host=$host port=$port' AS db (TYPE postgres, SCHEMA 'public'); USE db;",
            )

//            df1 = DataFrame.readSqlTable(connection, "table1", dbType = DuckDb)
            df1 = DataFrame.readSqlQuery(connection, "SELECT * from table1", dbType = DuckDb)
//            df2 = DataFrame.readSqlTable(connection, "table2", dbType = DuckDb)
            df2 = DataFrame.readSqlQuery(connection, "SELECT * from table2", dbType = DuckDb)
        }

        df1.print(columnTypes = true, borders = true)
        df1.mapToFrame {
            expr { "colA"<Int>() + "colB"<Double>() } into "sum"
            "sum" from { "colA"<Int>() + "colB"<Double>() }
        }

        df2.print(columnTypes = true, borders = true)
    }
}
