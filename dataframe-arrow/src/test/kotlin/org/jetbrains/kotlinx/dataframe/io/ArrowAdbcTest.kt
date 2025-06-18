@file:Suppress("SqlDialectInspection")

package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.adbc.core.AdbcDriver
import org.apache.arrow.adbc.driver.jdbc.JdbcConnection
import org.apache.arrow.adbc.driver.jdbc.JdbcDriver
import org.apache.arrow.memory.RootAllocator
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.Ignore
import org.junit.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.util.UUID
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class ArrowAdbcTest {
    /**
     * We can connect to JDBC databases from arrow using [ADBC](https://arrow.apache.org/adbc/current/driver/jdbc.html).
     */
    @Test
    fun `JDBC integration H2 MySQL`() {
        val url = "jdbc:h2:mem:test5;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false"

        val db = JdbcDriver(RootAllocator())
            .open(
                buildMap {
                    AdbcDriver.PARAM_URI.set(this, url)
                },
            )

        val df = db.connect().use { connection ->
            // Create table Customer
            @Language("SQL")
            val createCustomerTableQuery = """
                CREATE TABLE Customer (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    age INT
                )
            """
            connection.createStatement().apply { setSqlQuery(createCustomerTableQuery) }.executeUpdate()

            // Create table Sale
            @Language("SQL")
            val createSaleTableQuery = """
                CREATE TABLE Sale (
                    id INT PRIMARY KEY,
                    customerId INT,
                    amount DECIMAL(10, 2) NOT NULL
                )
            """
            connection.createStatement().apply { setSqlQuery(createSaleTableQuery) }.executeUpdate()

            // add data to the Customer table
            listOf(
                "INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40)",
                "INSERT INTO Customer (id, name, age) VALUES (2, 'Alice', 25)",
                "INSERT INTO Customer (id, name, age) VALUES (3, 'Bob', 47)",
                "INSERT INTO Customer (id, name, age) VALUES (4, NULL, NULL)",
            ).forEach {
                connection.createStatement().apply { setSqlQuery(it) }.executeUpdate()
            }

            // add data to the Sale table
            listOf(
                "INSERT INTO Sale (id, customerId, amount) VALUES (1, 1, 100.50)",
                "INSERT INTO Sale (id, customerId, amount) VALUES (2, 2, 50.00)",
                "INSERT INTO Sale (id, customerId, amount) VALUES (3, 1, 75.25)",
                "INSERT INTO Sale (id, customerId, amount) VALUES (4, 3, 35.15)",
            ).forEach {
                connection.createStatement().apply { setSqlQuery(it) }.executeUpdate()
            }

            val query = connection.createStatement().apply {
                setSqlQuery("SELECT * FROM Customer")
            }.executeQuery()

            DataFrame.readArrow(query.reader)
        }

        df.print(borders = true, columnTypes = true)
    }

    /**
     * We can connect to JDBC databases from arrow using [ADBC](https://arrow.apache.org/adbc/current/driver/jdbc.html).
     * TODO hard to define calendar stuff
     */
    @Test
    @Ignore
    fun `JDBC integration H2 PostgreSQL`() {
        val url =
            "jdbc:h2:mem:test3;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"

//        val config = JdbcToArrowConfigBuilder()
//            .setArraySubTypeByColumnNameMap(
//                mapOf(
//                    "dateArrayCol" to JdbcFieldInfo(Types.ARRAY),
//                ),
//            ).build()
//        val quirks = JdbcQuirks.builder("h2")
//            .typeConverter {
//                if (it.jdbcType == Types.ARRAY) {
//                    ArrowType.Date(DateUnit.DAY)
//                } else {
//                    JdbcToArrowUtils.getArrowTypeFromJdbcType(it.fieldInfo, null)
//                }
//            }
//            .build()

        val db = JdbcDriver(RootAllocator())
            .open(
                buildMap {
                    AdbcDriver.PARAM_URI.set(this, url)
//                    put(JdbcDriver.PARAM_JDBC_QUIRKS, quirks)
                },
            )

        val df = db.connect().use { connection ->

            @Language("SQL")
            val createTableStatement =
                """
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
                """.trimIndent()
            connection.createStatement().apply { setSqlQuery(createTableStatement) }.executeUpdate()

            @Language("SQL")
            val createTableQuery =
                """
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
                """.trimIndent()
            connection.createStatement().apply { setSqlQuery(createTableQuery) }.executeUpdate()

            @Language("SQL")
            val insertData1 =
                """
                INSERT INTO table1 (
                    bigintCol, smallintCol, bigserialCol,  booleanCol, 
                    byteaCol, characterCol, characterNCol, charCol, 
                    dateCol, doubleCol, 
                    integerCol, intArrayCol,
                    doubleArrayCol, dateArrayCol, textArrayCol, booleanArrayCol
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

            @Language("SQL")
            val insertData2 =
                """
                INSERT INTO table2 (
                    moneyCol, numericCol, 
                    realCol, smallintCol, 
                    serialCol, textCol, timeCol, 
                    timeWithZoneCol, timestampCol, timestampWithZoneCol, 
                    uuidCol
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

            // temporary workaround to create arrays
            val jdbcConnection = JdbcConnection::class
                .memberProperties
                .find { it.name == "connection" }!!
                .also { it.isAccessible = true }
                .get(connection as JdbcConnection) as Connection

            val intArray = jdbcConnection.createArrayOf("INTEGER", arrayOf(1, 2, 3))
            val doubleArray = jdbcConnection.createArrayOf("DOUBLE", arrayOf(1.1, 2.2, 3.3))
            val dateArray = jdbcConnection.createArrayOf(
                "DATE",
                arrayOf(Date.valueOf("2023-08-01"), Date.valueOf("2023-08-02")),
            )
            val textArray = jdbcConnection.createArrayOf("TEXT", arrayOf("Hello", "World"))
            val booleanArray = jdbcConnection.createArrayOf("BOOLEAN", arrayOf(true, false, true))

            jdbcConnection.prepareStatement(insertData1).use {
                for (i in 1..3) {
                    it.setLong(1, i * 1000L)
                    it.setShort(2, 11.toShort())
                    it.setLong(3, 1000000000L + i)
                    it.setBoolean(4, i % 2 == 1)
                    it.setBytes(5, byteArrayOf(1, 2, 3))
                    it.setString(6, "A")
                    it.setString(7, "Hello")
                    it.setString(8, "A")
                    it.setDate(9, Date.valueOf("2023-08-01"))
                    it.setDouble(10, 12.34)
                    it.setInt(11, 12345 * i)
                    it.setArray(12, intArray)
                    it.setArray(13, doubleArray)
                    it.setArray(14, dateArray)
                    it.setArray(15, textArray)
                    it.setArray(16, booleanArray)
                    it.executeUpdate()
                }
            }

            jdbcConnection.prepareStatement(insertData2).use {
                // Insert data into table2
                for (i in 1..3) {
                    it.setBigDecimal(1, BigDecimal("123.45"))
                    it.setBigDecimal(2, BigDecimal("12.34"))
                    it.setFloat(3, 12.34f)
                    it.setInt(4, 1000 + i)
                    it.setInt(5, 1000000 + i)
                    it.setString(6, null)
                    it.setTime(7, Time.valueOf("12:34:56"))
                    it.setTimestamp(8, Timestamp(System.currentTimeMillis()))
                    it.setTimestamp(9, Timestamp(System.currentTimeMillis()))
                    it.setTimestamp(10, Timestamp(System.currentTimeMillis()))
                    it.setObject(11, UUID.randomUUID(), Types.OTHER)
                    it.executeUpdate()
                }
            }

            val query = connection.createStatement().apply {
                setSqlQuery("SELECT * FROM table1")
            }.executeQuery()

            DataFrame.readArrow(query.reader)
        }

        df.print(borders = true, columnTypes = true)
    }
}
