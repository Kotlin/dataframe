package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Test
import org.postgresql.geometric.PGbox
import org.postgresql.geometric.PGcircle
import org.postgresql.geometric.PGline
import org.postgresql.geometric.PGlseg
import org.postgresql.geometric.PGpath
import org.postgresql.geometric.PGpoint
import org.postgresql.geometric.PGpolygon
import org.postgresql.util.PGInterval
import org.postgresql.util.PGmoney
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.SQLXML
import java.sql.Types
import java.util.UUID
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.uuid.Uuid
import java.sql.Time as SqlTime
import java.sql.Timestamp as SqlTimestamp

@DataSchema
interface Table1Postgres {
    val id: Int
    val bigintcol: Long
    val smallintcol: Int
    val bigserialcol: Long
    val booleancol: Boolean
    val boxcol: PGbox
    val byteacol: ByteArray
    val charactercol: String
    val characterncol: String
    val charcol: String
    val circlecol: PGcircle
    val datecol: java.util.Date
    val doublecol: Double
    val integercol: Int
    val intervalcol: PGInterval
    val jsoncol: String
    val jsonbcol: String
}

@DataSchema
interface Table2Postgres {
    val id: Int
    val linecol: PGline
    val lsegcol: PGlseg
    val macaddrcol: String
    val moneycol: PGmoney
    val numericcol: BigDecimal
    val pathcol: PGpath
    val pointcol: PGpoint
    val polygoncol: PGpolygon
    val realcol: Float
    val smallintcol: Int
    val smallserialcol: Int
    val serialcol: Int
    val textcol: String?
    val timecol: SqlTime
    val timewithzonecol: SqlTime
    val timestampcol: Instant
    val timestampwithzonecol: Instant
    val uuidcol: Uuid
    val xmlcol: SQLXML
}

@DataSchema
interface ViewTablePostgres {
    val id: Int
    val bigintcol: Long
    val linecol: PGline
    val textcol: String?
}

internal fun createPostgresTestData(connection: Connection) {
    connection.createStatement().use { st ->
        st.execute("SET lc_monetary TO 'C'")
        st.execute("SET client_encoding TO 'UTF8'")
    }
    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }

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
        jsonbCol jsonb not null,
        intArrayCol integer[],
        doubleArrayCol double precision array,
        dateArrayCol date array,
        textArrayCol text array,
        booleanArrayCol boolean array
        )
        """
    connection.createStatement().execute(createTableStatement.trimIndent())

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
    connection.createStatement().execute(createTableQuery.trimIndent())

    @Language("SQL")
    val insertData1 = """
        INSERT INTO table1 (
            bigintCol, smallintCol, bigserialCol,  booleanCol,
            boxCol, byteaCol, characterCol, characterNCol, charCol,
            circleCol, dateCol, doubleCol,
            integerCol, intervalCol, jsonCol, jsonbCol, intArrayCol,
            doubleArrayCol, dateArrayCol, textArrayCol, booleanArrayCol
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

    val intArray = connection.createArrayOf("INTEGER", arrayOf(1, 2, 3))
    val doubleArray = connection.createArrayOf("DOUBLE", arrayOf(1.1, 2.2, 3.3))
    val dateArray = connection.createArrayOf("DATE", arrayOf(Date.valueOf("2023-08-01"), Date.valueOf("2023-08-02")))
    val textArray = connection.createArrayOf("TEXT", arrayOf("Hello", "World"))
    val booleanArray = connection.createArrayOf("BOOLEAN", arrayOf(true, false, true))

    connection.prepareStatement(insertData1).use { st ->
        // Insert data into table1
        for (i in 1..3) {
            st.setLong(1, i * 1000L)
            st.setShort(2, 11.toShort())
            st.setLong(3, 1000000000L + i)
            st.setBoolean(4, i % 2 == 1)
            st.setObject(5, PGbox("(1,1),(2,2)"))
            st.setBytes(6, byteArrayOf(1, 2, 3))
            st.setString(7, "A")
            st.setString(8, "Hello")
            st.setString(9, "A")
            st.setObject(10, PGcircle("<(1,2),3>"))
            st.setDate(11, Date.valueOf("2023-08-01"))
            st.setDouble(12, 12.34)
            st.setInt(13, 12345 * i)
            st.setObject(14, PGInterval("1 year"))

            val jsonbObject = PGobject()
            jsonbObject.type = "jsonb"
            jsonbObject.value = "{\"key\": \"value\"}"

            st.setObject(15, jsonbObject)
            st.setObject(16, jsonbObject)
            st.setArray(17, intArray)
            st.setArray(18, doubleArray)
            st.setArray(19, dateArray)
            st.setArray(20, textArray)
            st.setArray(21, booleanArray)
            st.executeUpdate()
        }
    }

    connection.prepareStatement(insertData2).use { st ->
        // Insert data into table2
        for (i in 1..3) {
            st.setObject(1, PGline("{1,2,3}"))
            st.setObject(2, PGlseg("[(-1,0),(1,0)]"))

            val macaddrObject = PGobject()
            macaddrObject.type = "macaddr"
            macaddrObject.value = "00:00:00:00:00:0$i"

            st.setObject(3, macaddrObject)
            st.setBigDecimal(4, BigDecimal("123.45"))
            st.setBigDecimal(5, BigDecimal("12.34"))
            st.setObject(6, PGpath("((1,2),(3,$i))"))
            st.setObject(7, PGpoint("(1,2)"))
            st.setObject(8, PGpolygon("((1,1),(2,2),(3,3))"))
            st.setFloat(9, 12.34f)
            st.setShort(10, (i * 100).toShort())
            st.setInt(11, 1000 + i)
            st.setInt(12, 1000000 + i)
            st.setString(13, null)
            st.setTime(14, SqlTime.valueOf("12:34:56"))

            st.setTimestamp(15, SqlTimestamp(System.currentTimeMillis()))
            st.setTimestamp(16, SqlTimestamp(System.currentTimeMillis()))
            st.setTimestamp(17, SqlTimestamp(System.currentTimeMillis()))

            st.setObject(18, UUID.randomUUID(), Types.OTHER)
            val xmlObject = PGobject()
            xmlObject.type = "xml"
            xmlObject.value = "<root><element>data</element></root>"

            st.setObject(19, xmlObject)
            st.executeUpdate()
        }
    }
}

internal fun clearPostgresTestData(connection: Connection) {
    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table2") }
}

internal fun tearDownPostgresTestData(connection: Connection) {
    try {
        clearPostgresTestData(connection)
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

abstract class PostgresTestBase {
    protected abstract val connection: Connection

    @Test
    fun `read from tables`() {
        val tableName1 = "table1"
        val df1 = DataFrame.readSqlTable(connection, tableName1).cast<Table1Postgres>(verify = true)
        val result = df1.filter { "id"<Int>() == 1 }

        result[0][2] shouldBe 11
        result[0][13] shouldBe 12345
        result[0][17] shouldBe arrayOf(1, 2, 3)
        result[0][18] shouldBe arrayOf(1.1, 2.2, 3.3)
        result[0][19] shouldBe arrayOf(Date.valueOf("2023-08-01"), Date.valueOf("2023-08-02"))
        result[0][20] shouldBe arrayOf("Hello", "World")
        result[0][21] shouldBe arrayOf(true, false, true)

        val schema = DataFrameSchema.readSqlTable(connection, tableName1)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["integercol"]!!.type shouldBe typeOf<Int?>()
        schema.columns["smallintcol"]!!.type shouldBe typeOf<Int>()
        schema.columns["circlecol"]!!.type shouldBe typeOf<PGcircle>()
        schema.columns["intarraycol"]!!.type.classifier shouldBe Array::class
        schema.columns["doublearraycol"]!!.type.classifier shouldBe Array::class
        schema.columns["datearraycol"]!!.type.classifier shouldBe Array::class
        schema.columns["textarraycol"]!!.type.classifier shouldBe Array::class
        schema.columns["booleanarraycol"]!!.type.classifier shouldBe Array::class

        val tableName2 = "table2"
        val df2 = DataFrame.readSqlTable(connection, tableName2).cast<Table2Postgres>(verify = true)
        val result2 = df2.filter { "id"<Int>() == 1 }
        result2[0][11] shouldBe 1001
        result2[0][13] shouldBe null

        val schema2 = DataFrameSchema.readSqlTable(connection, tableName2)
        schema2.columns["id"]!!.type shouldBe typeOf<Int>()
        schema2.columns["pathcol"]!!.type shouldBe typeOf<PGpath>()
        schema2.columns["textcol"]!!.type shouldBe typeOf<String?>()
        schema2.columns["linecol"]!!.type shouldBe typeOf<PGline>()
    }

    @Test
    fun `read from sql query`() {
        @Language("SQL")
        val sqlQuery =
            """
            SELECT
                t1.id,
                t1.bigintCol,
                t2.lineCol,
                t2.textCol
            FROM table1 t1
            JOIN table2 t2 ON t1.id = t2.id
            """.trimIndent()

        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<ViewTablePostgres>(verify = true)
        val result = df.filter { "id"<Int>() == 1 }
        result[0][3] shouldBe null

        val schema = DataFrameSchema.readSqlQuery(connection, sqlQuery = sqlQuery)
        schema.columns["id"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigintcol"]!!.type shouldBe typeOf<Long>()
        schema.columns["textcol"]!!.type shouldBe typeOf<String?>()
    }

    @Test
    fun `read from all tables`() {
        val dataframes = DataFrame.readAllSqlTables(connection).values.toList()

        val table1Df = dataframes[0].cast<Table1Postgres>(verify = true)

        table1Df.rowsCount() shouldBe 3
        table1Df.filter { "integercol"<Int?>()?.let { it > 12345 } ?: false }.rowsCount() shouldBe 2
        table1Df[0][1] shouldBe 1000L
        table1Df[0][2] shouldBe 11

        val table2Df = dataframes[1].cast<Table2Postgres>(verify = true)

        table2Df.rowsCount() shouldBe 3
        table2Df.filter {
            "pathcol"<PGpath>() == PGpath("((1,2),(3,1))")
        }.rowsCount() shouldBe 1
        table2Df[0][11] shouldBe 1001
    }

    @Test
    fun `read columns of different types to check type mapping`() {
        val tableName1 = "table1"
        val df1 = DataFrame.readSqlTable(connection, tableName1).cast<Table1Postgres>(verify = true)
        val result = df1.select("smallintcol")
            .add("smallintcol2") { "smallintcol"<Int>() }
        result[0][1] shouldBe 11

        val result1 = df1.select("bigserialcol")
            .add("bigserialcol2") { "bigserialcol"<Long>() }
        result1[0][1] shouldBe 1000000001L

        val result2 = df1.select("doublecol")
            .add("doublecol2") { "doublecol"<Double>() }
        result2[0][1] shouldBe 12.34

        val tableName2 = "table2"
        val df2 = DataFrame.readSqlTable(connection, tableName2).cast<Table2Postgres>(verify = true)

        val result3 = df2.select("moneycol")
            .add("moneycol2") { "moneycol"<PGmoney?>() }
        (result3[0][1] as PGmoney).`val` shouldBe 123.45

        val result4 = df2.select("numericcol")
            .add("numericcol2") { "numericcol"<BigDecimal>() }
        result4[0][1] shouldBe BigDecimal("12.34")

        val result5 = df2.select("realcol")
            .add("realcol2") { "realcol"<Float>() }
        result5[0][1] shouldBe 12.34f

        val result7 = df2.select("smallserialcol")
            .add("smallserialcol2") { "smallserialcol"<Int>() }
        result7[0][1] shouldBe 1001

        val result8 = df2.select("serialcol")
            .add("serialcol2") { "serialcol"<Int>() }
        result8[0][1] shouldBe 1000001

        val schema = DataFrameSchema.readSqlTable(connection, tableName1)
        schema.columns["smallintcol"]!!.type shouldBe typeOf<Int>()
        schema.columns["bigserialcol"]!!.type shouldBe typeOf<Long>()
        schema.columns["doublecol"]!!.type shouldBe typeOf<Double>()

        val schema1 = DataFrameSchema.readSqlTable(connection, tableName2)
        schema1.columns["moneycol"]!!.type shouldBe typeOf<PGmoney>()
        schema1.columns["numericcol"]!!.type shouldBe typeOf<BigDecimal>()
        schema1.columns["realcol"]!!.type shouldBe typeOf<Float>()
        schema1.columns["smallserialcol"]!!.type shouldBe typeOf<Int>()
        schema1.columns["serialcol"]!!.type shouldBe typeOf<Int>()
    }

    @Test
    fun `infer nullability`() {
        inferNullability(connection)
    }

    @Test
    fun `an SQL alias names the column`() {
        aliasesNameColumns(connection)
    }

    @Test
    fun `json, network, bit-string, text-search and range columns are read as the text PostgreSQL prints`() {
        @Language("SQL")
        val createTable =
            """
            CREATE TABLE text_form_types(
              jsonCol JSON, jsonbCol JSONB, inetCol INET, cidrCol CIDR,
              macaddrCol MACADDR, macaddr8Col MACADDR8, varbitCol VARBIT(3),
              tsvectorCol TSVECTOR, tsqueryCol TSQUERY,
              int4rangeCol INT4RANGE, int8rangeCol INT8RANGE, numrangeCol NUMRANGE,
              tsrangeCol TSRANGE, tstzrangeCol TSTZRANGE, daterangeCol DATERANGE,
              int4multirangeCol INT4MULTIRANGE, int8multirangeCol INT8MULTIRANGE,
              nummultirangeCol NUMMULTIRANGE, tsmultirangeCol TSMULTIRANGE,
              tstzmultirangeCol TSTZMULTIRANGE, datemultirangeCol DATEMULTIRANGE
            )
            """.trimIndent()

        @Language("SQL")
        val insertRows =
            """
            INSERT INTO text_form_types VALUES
              ('{"k":1}','{"k":1}','10.0.0.1','10.0.0.0/8',
               '08:00:2b:01:02:03','08:00:2b:01:02:03:04:05',B'101',
               'fat cat','fat & cat',
               '(1,3]','[1,2)','[1.0,2.0)',
               '[2024-01-01,2024-01-02)','[2024-01-01,2024-01-02)','[2024-01-01,2024-01-02]',
               '{[1,2)}','{[1,2)}','{[1.0,2.0)}',
               '{[2024-01-01,2024-01-02)}','{[2024-01-01,2024-01-02)}','{[2024-01-01,2024-01-02)}'),
              (NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,
               NULL,NULL)
            """.trimIndent()

        try {
            connection.createStatement().use { st ->
                // a tstzrange is both parsed and printed in the session time zone
                st.execute("SET TIME ZONE 'UTC'")
                st.execute("DROP TABLE IF EXISTS text_form_types")
                st.execute(createTable)
                st.execute(insertRows)
            }

            val df = DataFrame.readSqlTable(connection, "text_form_types")

            // lists every column that is not read as String?, not only the first one
            df.columns().associate { it.name() to it.type() }.filterValues { it != typeOf<String?>() } shouldBe
                emptyMap()
            // the schema read without the data declares the same types
            DataFrameSchema.readSqlTable(connection, "text_form_types").columns.mapValues { it.value.type } shouldBe
                df.columns().associate { it.name() to it.type() }
            df[0].toMap() shouldBe mapOf(
                // json keeps the text as written, jsonb prints PostgreSQL's normalized form
                "jsoncol" to """{"k":1}""",
                "jsonbcol" to """{"k": 1}""",
                "inetcol" to "10.0.0.1",
                "cidrcol" to "10.0.0.0/8",
                "macaddrcol" to "08:00:2b:01:02:03",
                "macaddr8col" to "08:00:2b:01:02:03:04:05",
                "varbitcol" to "101",
                "tsvectorcol" to "'cat' 'fat'",
                "tsquerycol" to "'fat' & 'cat'",
                // a range of integers or dates is printed in its canonical form, [lower,upper)
                "int4rangecol" to "[2,4)",
                "int8rangecol" to "[1,2)",
                "numrangecol" to "[1.0,2.0)",
                "tsrangecol" to """["2024-01-01 00:00:00","2024-01-02 00:00:00")""",
                "tstzrangecol" to """["2024-01-01 00:00:00+00","2024-01-02 00:00:00+00")""",
                "daterangecol" to "[2024-01-01,2024-01-03)",
                "int4multirangecol" to "{[1,2)}",
                "int8multirangecol" to "{[1,2)}",
                "nummultirangecol" to "{[1.0,2.0)}",
                "tsmultirangecol" to """{["2024-01-01 00:00:00","2024-01-02 00:00:00")}""",
                "tstzmultirangecol" to """{["2024-01-01 00:00:00+00","2024-01-02 00:00:00+00")}""",
                "datemultirangecol" to "{[2024-01-01,2024-01-02)}",
            )
            df[1].values() shouldBe List(df.columnsCount()) { null }
        } finally {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS text_form_types")
                st.execute("RESET TIME ZONE")
            }
        }
    }

    @Test
    fun `parse turns json objects into a column group and json arrays into a frame column`() {
        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS json_parse")
                st.execute("CREATE TABLE json_parse(objectCol JSONB, arrayCol JSON)")
                st.execute(
                    """
                    INSERT INTO json_parse VALUES
                      ('{"a": 1, "b": "x"}', '[{"a": 1}, {"a": 2}]'),
                      ('{"a": 2, "b": "y"}', '[{"a": 3}]')
                    """.trimIndent(),
                )
            }

            // needs dataframe-json on the classpath, as this module's tests have it
            val parsed = DataFrame.readSqlTable(connection, "json_parse").parse()

            parsed["objectcol"].kind() shouldBe ColumnKind.Group
            parsed.getColumnGroup("objectcol")["a"].toList() shouldBe listOf(1, 2)
            parsed.getColumnGroup("objectcol")["b"].toList() shouldBe listOf("x", "y")
            parsed["arraycol"].kind() shouldBe ColumnKind.Frame
            parsed.getFrameColumn("arraycol").toList().map { it["a"].toList() } shouldBe listOf(listOf(1, 2), listOf(3))
        } finally {
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS json_parse") }
        }
    }

    @Test
    fun `a tstzrange is printed in the session time zone`() {
        fun readRange(timeZone: String): Any? {
            connection.createStatement().use { it.execute("SET TIME ZONE '$timeZone'") }
            return DataFrame.readSqlTable(connection, "tstzrange_type")["rangecol"][0]
        }

        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS tstzrange_type")
                st.execute("CREATE TABLE tstzrange_type(rangeCol TSTZRANGE)")
                // the bounds carry an explicit offset, so the stored range does not depend on the session
                st.execute("INSERT INTO tstzrange_type VALUES ('[2024-01-01 00:00:00+00,2024-01-02 00:00:00+00)')")
            }

            readRange("UTC") shouldBe """["2024-01-01 00:00:00+00","2024-01-02 00:00:00+00")"""
            readRange("Asia/Tokyo") shouldBe """["2024-01-01 09:00:00+09","2024-01-02 09:00:00+09")"""
        } finally {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS tstzrange_type")
                st.execute("RESET TIME ZONE")
            }
        }
    }

    @Test
    fun `bit columns wider than one bit are read as their bits`() {
        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS bit_type")
                st.execute("CREATE TABLE bit_type(bit1Col BIT(1), bit3Col BIT(3))")
                st.execute("INSERT INTO bit_type VALUES (B'1', B'101')")
            }

            val df = DataFrame.readSqlTable(connection, "bit_type")

            df["bit1col"].type() shouldBe typeOf<Boolean>()
            df["bit3col"].type() shouldBe typeOf<String>()
            df[0].values() shouldBe listOf(true, "101")
        } finally {
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS bit_type") }
        }
    }

    @Test
    fun `composite columns are read as Any holding a PGobject`() {
        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS composite_type")
                st.execute("DROP TYPE IF EXISTS pair_type")
                st.execute("CREATE TYPE pair_type AS (a INT, b TEXT)")
                st.execute("CREATE TABLE composite_type(pairCol pair_type)")
                st.execute("INSERT INTO composite_type VALUES (ROW(1, 'x'))")
            }

            val df = DataFrame.readSqlTable(connection, "composite_type")

            df["paircol"].type() shouldBe typeOf<Any>()
            val value = df["paircol"][0] as PGobject
            value.type shouldBe "pair_type"
            value.value shouldBe "(1,x)"

            // an anonymous ROW(...) has no type of its own
            val anonymous = DataFrame.readSqlQuery(connection, "SELECT ROW(1, 'x') AS anonCol")
            anonymous["anoncol"].type() shouldBe typeOf<Any>()
            (anonymous["anoncol"][0] as PGobject).value shouldBe "(1,x)"
        } finally {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS composite_type")
                st.execute("DROP TYPE IF EXISTS pair_type")
            }
        }
    }

    @Test
    fun `array elements of uuid, json and network types keep the driver's classes`() {
        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS array_types")
                st.execute("CREATE TABLE array_types(uuidArrayCol UUID[], jsonbArrayCol JSONB[], inetArrayCol INET[])")
                st.execute(
                    """
                    INSERT INTO array_types VALUES (
                      ARRAY['a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid],
                      ARRAY['{"k":1}'::jsonb],
                      ARRAY['10.0.0.1'::inet])
                    """.trimIndent(),
                )
            }

            val df = DataFrame.readSqlTable(connection, "array_types")

            // pins the current behaviour: unlike the scalar columns, array elements are not converted,
            // and the driver itself is not consistent about them — jsonb elements come as String, inet ones as PGobject
            df.columns().associate { col ->
                col.name() to "${col.type()} of ${(col[0] as Array<*>).single()!!.javaClass.name}"
            } shouldBe mapOf(
                "uuidarraycol" to "kotlin.Array<*> of java.util.UUID",
                "jsonbarraycol" to "kotlin.Array<*> of java.lang.String",
                "inetarraycol" to "kotlin.Array<*> of org.postgresql.util.PGobject",
            )
        } finally {
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS array_types") }
        }
    }

    @Test
    fun `uuid columns are read as Uuid`() {
        try {
            connection.createStatement().use { st ->
                st.execute("DROP TABLE IF EXISTS uuid_type")
                st.execute("CREATE TABLE uuid_type(uuidCol UUID)")
                st.execute("INSERT INTO uuid_type VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), (NULL)")
            }

            val df = DataFrame.readSqlTable(connection, "uuid_type")

            df["uuidcol"].type() shouldBe typeOf<Uuid?>()
            // the schema read without the data declares the same type
            DataFrameSchema.readSqlTable(connection, "uuid_type").columns["uuidcol"]!!.type shouldBe typeOf<Uuid?>()
            df["uuidcol"].toList() shouldBe listOf(Uuid.parse("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"), null)
        } finally {
            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS uuid_type") }
        }
    }

    /**
     * Guards the invariant behind #2087 across a wide surface of types,
     * see [assertColumnTypesMatchValues].
     */
    @Test
    fun `declared column types accept the values the driver returns`() {
        connection.assertColumnTypesMatchValues(
            dbType = PostgreSql,
            createTableSql = POSTGRES_AUDIT_DDL,
            insertRowSql = POSTGRES_AUDIT_INSERT,
        )
    }

    /**
     * Pins the column type each SQL type is read as — the claim the type-mapping page publishes,
     * see [assertColumnTypes].
     */
    @Test
    fun `columns are read as the type-mapping page documents`() {
        connection.assertColumnTypes(
            dbType = PostgreSql,
            createTableSql = POSTGRES_AUDIT_DDL,
            insertRowSql = POSTGRES_AUDIT_INSERT,
            expected = POSTGRES_EXPECTED_TYPES,
        )
    }
}
