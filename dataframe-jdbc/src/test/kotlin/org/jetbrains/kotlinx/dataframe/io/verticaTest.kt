//package org.jetbrains.kotlinx.dataframe.io
//
//import io.kotest.matchers.shouldBe
//import org.intellij.lang.annotations.Language
//import org.jetbrains.kotlinx.dataframe.DataFrame
//import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
//import org.jetbrains.kotlinx.dataframe.api.cast
//import org.jetbrains.kotlinx.dataframe.api.filter
//import org.junit.AfterClass
//import org.junit.BeforeClass
//import org.junit.Test
//import java.math.BigDecimal
//import java.sql.Connection
//import java.sql.DriverManager
//import java.sql.SQLException
//import org.jetbrains.kotlinx.dataframe.api.add
//import org.jetbrains.kotlinx.dataframe.api.select
//import org.junit.Ignore
//import java.util.UUID
//import kotlin.reflect.typeOf
//
//private const val URL = "jdbc:vertica://localhost:5433"
//private const val USER_NAME = "root"
//private const val PASSWORD = "pass"
//private const val TEST_DATABASE_NAME = "testKDFdatabase"
//
//@DataSchema
//interface Table1Vertica {
//    val id: Int
//    val bitCol: Boolean
//    val tinyintCol: Int
//    val smallintCol: Int
//    val mediumintCol: Int
//    val mediumintUnsignedCol: Int
//    val integerCol: Int
//    val intCol: Int
//    val integerUnsignedCol: Long
//    val bigintCol: Long
//    val floatCol: Float
//    val doubleCol: Double
//    val decimalCol: BigDecimal
//    val dateCol: String
//    val datetimeCol: String
//    val timestampCol: String
//    val timeCol: String
//    val yearCol: String
//    val varcharCol: String
//    val charCol: String
//    val binaryCol: ByteArray
//    val varbinaryCol: ByteArray
//    val tinyblobCol: ByteArray
//    val blobCol: ByteArray
//    val mediumblobCol: ByteArray
//    val longblobCol: ByteArray
//    val textCol: String
//    val mediumtextCol: String
//    val longtextCol: String
//    val enumCol: String
//    val setCol: Char
//}
//
//@Ignore
//class VerticaTest {
//    companion object {
//        private lateinit var connection: Connection
//
//        @BeforeClass
//        @JvmStatic
//        fun setUpClass() {
//            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)
//
//            connection.createStatement().use { st ->
//                // Drop the test database if it exists
//                val dropDatabaseQuery = "DROP DATABASE IF EXISTS $TEST_DATABASE_NAME"
//                st.executeUpdate(dropDatabaseQuery)
//
//                // Create the test database
//                val createDatabaseQuery = "CREATE DATABASE $TEST_DATABASE_NAME"
//                st.executeUpdate(createDatabaseQuery)
//
//                // Use the newly created database
//                val useDatabaseQuery = "USE $TEST_DATABASE_NAME"
//                st.executeUpdate(useDatabaseQuery)
//            }
//
//            connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
//
//            @Language("SQL")
//            val createTableQuery = """
//            CREATE TABLE IF NOT EXISTS table1 (
//                integerCol INTEGER,
//                intCol INT,
//                bigintCol BIGINT,
//                int8Col INT8,
//                smallintCol SMALLINT,
//                tinyintCol TINYINT,
//                decimalCol DECIMAL,
//                numericCol NUMERIC,
//                numberCol NUMBER,
//                moneyCol MONEY,
//                uuidCol UUID
//            )
//            """
//
//            val createTableQueryBackup = """
//            CREATE TABLE IF NOT EXISTS table1 (
////                timewithtimezoneCol TIMETZ,
////                timestampwithtimezoneCol TIMESTAMPTZ,
////                intervalCol INTERVAL,
////                intervaldaytosecondCol INTERVAL DAY TO SECOND,
////                intervalyeartomonthCol INTERVAL YEAR TO MONTH,
//                uuidCol UUID,
////                arrayCol ARRAY[VARCHAR(50)],
////                rowCol ROW(street VARCHAR, city VARCHAR)),
////                setCol SET[VARCHAR]
//            )
//            """
//
//            connection.createStatement().execute(
//                createTableQuery.trimIndent()
//            )
//
//            @Language("SQL")
//            val insertData1 = """
//            INSERT INTO table1 (
//                boolCol,
//                charCol,
//                varcharCol,
//                longvarcharCol,
//                binaryCol,
//                varbinaryCol,
//                longvarbinaryCol,
//                dateCol,
//                timeCol,
////                timewithtimezoneCol,
//                timestampCol,
////                timestampwithtimezoneCol,
////                intervalCol,
////                intervaldaytosecondCol,
////                intervalyeartomonthCol,
//                doubleprecisionCol,
//                floatCol,
//                floatncol,
//                float8Col,
//                realCol,
//                integerCol,
//                intCol,
//                bigintCol,
//                int8Col,
//                smallintCol,
//                tinyintCol,
//                decimalCol,
//                numericCol,
//                numberCol,
//                moneyCol,
////                geometryCol,
////                geographyCol,
//                uuidCol,
////                arrayCol,
////                rowCol,
////                setCol
//            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//        """.trimIndent()
//
//            @Language("SQL")
//            val insertDatatest = """
//            INSERT INTO table1 (
//                boolCol,
//                charCol,
//                varcharCol,
//                longvarcharCol,
//                binaryCol,
//                varbinaryCol,
//                longvarbinaryCol,
//                dateCol,
//                timeCol,
////                timewithtimezoneCol,
//                timestampCol,
////                timestampwithtimezoneCol,
////                intervalCol,
////                intervaldaytosecondCol,
////                intervalyeartomonthCol,
//                doubleprecisionCol,
//                floatCol,
//                floatncol,
//                float8Col,
//                realCol,
//                integerCol,
//                intCol,
//                bigintCol,
//                int8Col,
//                smallintCol,
//                tinyintCol,
//                decimalCol,
//                numericCol,
//                numberCol,
//                moneyCol,
////                geometryCol,
////                geographyCol,
//                uuidCol,
////                arrayCol,
////                rowCol,
////                setCol
//            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//        """.trimIndent()
//
//            connection.prepareStatement(insertData1).use { st ->
//                // Insert data into table1
//                for (i in 1..3) {
//                    st.setTimestamp(10, java.sql.Timestamp(System.currentTimeMillis()))
////                    st.setBigDecimal(12, BigDecimal(i * 10))
////                    st.setDate(13, java.sql.Date(System.currentTimeMillis()))
////                    st.setTimestamp(14, java.sql.Timestamp(System.currentTimeMillis()))
////                    st.setTimestamp(15, java.sql.Timestamp(System.currentTimeMillis()))
//                    st.setDouble(11, i * 10.0)
//                    st.setFloat(12, i * 10.0f)
//                    st.setFloat(13, i * 10.0f)
//                    st.setFloat(14, i * 10.0f)
//                    st.setFloat(15, i * 10.0f)
//                    st.setInt(16, i * 100)
//                    st.setInt(17, i * 100)
//                    st.setInt(18, i * 100)
//                    st.setInt(19, i * 100)
//                    st.setInt(20, i * 100)
//                    st.setInt(21, i * 100)
//                    st.setBigDecimal(22, BigDecimal(i * 10))
//                    st.setBigDecimal(23, BigDecimal(i * 10))
//                    st.setBigDecimal(24, BigDecimal(i * 10))
//                    st.setBigDecimal(25, BigDecimal(i * 10))
////                    st.setString(31, "{\"key\": \"value\"}")
////                    st.setString(32, "{\"key\": \"value\"}")
////                    st.setString(33, "{\"key\": \"value\"}")
//                    st.setString(26, "cee3754a-e2f2-4020-b43f-aba52b10c827")
////                    st.setString(35, "{\"key\": \"value\"}")
////                    st.setString(36, "{\"key\": \"value\"}")
////                    st.setString(37, "{\"key\": \"value\"}")
//                    st.executeUpdate()
//                }
//            }
//        }
//
//        @AfterClass
//        @JvmStatic
//        fun tearDownClass() {
//            try {
//                connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS table1") }
//                connection.createStatement().use { st -> st.execute("DROP DATABASE IF EXISTS $TEST_DATABASE_NAME") }
//                connection.close()
//            } catch (e: SQLException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    @Ignore
//    @Test
//    fun `basic test for reading sql tables`() {
//        val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1Vertica>()
//        val result = df1.filter { it[Table1Vertica::id] == 1 }
//        result[0][26] shouldBe "textValue1"
//
//        val schema = DataFrame.getSchemaForSqlTable(connection, "table1")
//        schema.columns["id"]!!.type shouldBe typeOf<Int>()
//        schema.columns["textCol"]!!.type shouldBe typeOf<String>()
//    }
//
////    @Test
////    fun `read from sql query`() {
////        @Language("SQL")
////        val sqlQuery = """
////            SELECT
////               t1.id,
////               t1.enumCol,
////               t2.setCol
////            FROM table1 t1
////            JOIN table2 t2 ON t1.id = t2.id
////        """.trimIndent()
////
////        val df = DataFrame.readSqlQuery(connection, sqlQuery = sqlQuery).cast<Table3MySql>()
////        val result = df.filter { it[Table3MySql::id] == 1 }
////        result[0][2] shouldBe "Option1"
////
////        val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery = sqlQuery)
////        schema.columns["id"]!!.type shouldBe typeOf<Int>()
////        schema.columns["enumCol"]!!.type shouldBe typeOf<Char>()
////        schema.columns["setCol"]!!.type shouldBe typeOf<Char?>()
////    }
////
////    @Test
////    fun `read from all tables`() {
////        val dataframes = DataFrame.readAllSqlTables(connection)
////
////        val table1Df = dataframes[0].cast<Table1MySql>()
////
////        table1Df.rowsCount() shouldBe 3
////        table1Df.filter { it[Table1MySql::integerCol] > 100 }.rowsCount() shouldBe 2
////        table1Df[0][11] shouldBe 10.0
////        table1Df[0][26] shouldBe "textValue1"
////
////        val table2Df = dataframes[1].cast<Table2MySql>()
////
////        table2Df.rowsCount() shouldBe 3
////        table2Df.filter { it[Table2MySql::integerCol] != null && it[Table2MySql::integerCol]!! > 400 }
////            .rowsCount() shouldBe 1
////        table2Df[0][11] shouldBe 20.0
////        table2Df[0][26] shouldBe null
////    }
////
////    @Test
////    fun `reading numeric types`() {
////        val df1 = DataFrame.readSqlTable(connection, "table1").cast<Table1MySql>()
////
////        val result = df1.select("tinyintCol").add("tinyintCol2") { it[Table1MySql::tinyintCol] }
////
////        result[0][1] shouldBe 1.toByte()
////
////        val result1 = df1.select("smallintCol")
////            .add("smallintCol2") { it[Table1MySql::smallintCol] }
////
////        result1[0][1] shouldBe 10.toShort()
////
////        val result2 = df1.select("mediumintCol")
////            .add("mediumintCol2") { it[Table1MySql::mediumintCol] }
////
////        result2[0][1] shouldBe 100
////
////        val result3 = df1.select("mediumintUnsignedCol")
////            .add("mediumintUnsignedCol2") { it[Table1MySql::mediumintUnsignedCol] }
////
////        result3[0][1] shouldBe 100
////
////        val result4 = df1.select("integerUnsignedCol")
////            .add("integerUnsignedCol2") { it[Table1MySql::integerUnsignedCol] }
////
////        result4[0][1] shouldBe 100L
////
////        val result5 = df1.select("bigintCol")
////            .add("bigintCol2") { it[Table1MySql::bigintCol] }
////
////        result5[0][1] shouldBe 100
////
////        val result6 = df1.select("floatCol")
////            .add("floatCol2") { it[Table1MySql::floatCol] }
////
////        result6[0][1] shouldBe 10.0f
////
////        val result7 = df1.select("doubleCol")
////            .add("doubleCol2") { it[Table1MySql::doubleCol] }
////
////        result7[0][1] shouldBe 10.0
////
////        val result8 = df1.select("decimalCol")
////            .add("decimalCol2") { it[Table1MySql::decimalCol] }
////
////        result8[0][1] shouldBe BigDecimal("10")
////
////        val schema = DataFrame.getSchemaForSqlTable(connection, "table1")
////
////        schema.columns["tinyintCol"]!!.type shouldBe typeOf<Int>()
////        schema.columns["smallintCol"]!!.type shouldBe typeOf<Int>()
////        schema.columns["mediumintCol"]!!.type shouldBe typeOf<Int>()
////        schema.columns["mediumintUnsignedCol"]!!.type shouldBe typeOf<Int>()
////        schema.columns["integerUnsignedCol"]!!.type shouldBe typeOf<Long>()
////        schema.columns["bigintCol"]!!.type shouldBe typeOf<Long>()
////        schema.columns["floatCol"]!!.type shouldBe typeOf<Float>()
////        schema.columns["doubleCol"]!!.type shouldBe typeOf<Double>()
////        schema.columns["decimalCol"]!!.type shouldBe typeOf<BigDecimal>()
////        // TODO: all unsigned types
////        // TODO: new mapping system based on class names
////        // validation after mapping in getObject
////        // getObject(i+1, type) catch getObject catch getString
////        // add direct mapping to getString and other methods
////
////    }
//}
