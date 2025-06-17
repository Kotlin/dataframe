package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import org.apache.arrow.adapter.jdbc.JdbcFieldInfo
import org.apache.arrow.adapter.jdbc.JdbcToArrowConfigBuilder
import org.apache.arrow.adapter.jdbc.JdbcToArrowUtils
import org.apache.arrow.adbc.core.AdbcDriver
import org.apache.arrow.adbc.driver.jdbc.JdbcConnection
import org.apache.arrow.adbc.driver.jdbc.JdbcDriver
import org.apache.arrow.adbc.driver.jdbc.JdbcQuirks
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.TimeStampMicroVector
import org.apache.arrow.vector.TimeStampMilliVector
import org.apache.arrow.vector.TimeStampNanoVector
import org.apache.arrow.vector.TimeStampSecVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ipc.ArrowFileReader
import org.apache.arrow.vector.ipc.ArrowFileWriter
import org.apache.arrow.vector.ipc.ArrowReader
import org.apache.arrow.vector.ipc.ArrowStreamReader
import org.apache.arrow.vector.ipc.ArrowStreamWriter
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.ByteArrayReadableSeekableByteChannel
import org.apache.arrow.vector.util.Text
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertToBoolean
import org.jetbrains.kotlinx.dataframe.api.copy
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import java.net.URL
import java.nio.channels.Channels
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.util.Locale
import java.util.UUID
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

class ArrowKtTest {

    fun testResource(resourcePath: String): URL = ArrowKtTest::class.java.classLoader.getResource(resourcePath)!!

    fun testArrowFeather(name: String) = testResource("$name.feather")

    fun testArrowIPC(name: String) = testResource("$name.ipc")

    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = DataFrame.readArrowFeather(feather)
        val a by columnOf("one")
        val b by columnOf(2.0)
        val c by listOf(
            mapOf(
                "c1" to Text("inner"),
                "c2" to 4.0,
                "c3" to 50.0,
            ) as Map<String, Any?>,
        ).toColumn()
        val d by columnOf("four")
        val expected = dataFrameOf(a, b, c, d)
        df shouldBe expected
    }

    @Test
    fun testReadingAllTypesAsEstimated() {
        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Infer),
            expectedNullable = false,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Infer),
            expectedNullable = false,
            hasNulls = false,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Checking),
            expectedNullable = true,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Checking),
            expectedNullable = true,
            hasNulls = false,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Widening),
            expectedNullable = true,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Widening),
            expectedNullable = true,
            hasNulls = false,
        )
    }

    @Test
    fun testReadingAllTypesAsEstimatedWithNulls() {
        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-with-nulls.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-with-nulls.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = true,
            hasNulls = true,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-with-nulls.arrow"),
                NullabilityOptions.Checking,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-with-nulls.arrow"),
                NullabilityOptions.Checking,
            ),
            expectedNullable = true,
            hasNulls = true,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-with-nulls.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-with-nulls.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullable() {
        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-not-nullable.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = false,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-not-nullable.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = false,
            hasNulls = false,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-not-nullable.arrow"),
                NullabilityOptions.Checking,
            ),
            expectedNullable = false,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-not-nullable.arrow"),
                NullabilityOptions.Checking,
            ),
            expectedNullable = false,
            hasNulls = false,
        )

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-not-nullable.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = false,
            hasNulls = false,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-not-nullable.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = false,
            hasNulls = false,
        )
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullableWithNulls() {
        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-illegal.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-illegal.arrow"),
                NullabilityOptions.Infer,
            ),
            expectedNullable = true,
            hasNulls = true,
        )

        shouldThrow<IllegalArgumentException> {
            assertEstimations(
                exampleFrame = DataFrame.readArrowFeather(
                    testArrowFeather("test-illegal.arrow"),
                    NullabilityOptions.Checking,
                ),
                expectedNullable = false,
                hasNulls = true,
            )
        }
        shouldThrow<IllegalArgumentException> {
            assertEstimations(
                exampleFrame = DataFrame.readArrowIPC(
                    testArrowIPC("test-illegal.arrow"),
                    NullabilityOptions.Checking,
                ),
                expectedNullable = false,
                hasNulls = true,
            )
        }

        assertEstimations(
            exampleFrame = DataFrame.readArrowFeather(
                testArrowFeather("test-illegal.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
        assertEstimations(
            exampleFrame = DataFrame.readArrowIPC(
                testArrowIPC("test-illegal.arrow"),
                NullabilityOptions.Widening,
            ),
            expectedNullable = true,
            hasNulls = true,
        )
    }

    @Test
    fun testWritingGeneral() {
        fun assertEstimation(citiesDeserialized: DataFrame<*>) {
            citiesDeserialized["name"] shouldBe citiesExampleFrame["name"]
            citiesDeserialized["affiliation"] shouldBe citiesExampleFrame["affiliation"]
            citiesDeserialized["is_capital"] shouldBe citiesExampleFrame["is_capital"]
            citiesDeserialized["population"] shouldBe citiesExampleFrame["population"]
            citiesDeserialized["area"] shouldBe citiesExampleFrame["area"]
            // cities["settled"].type() refers to FlexibleTypeImpl(LocalDate..LocalDate?)
            // and does not match typeOf<LocalDate>()
            citiesDeserialized["settled"].type() shouldBe typeOf<LocalDate>()
            citiesDeserialized["settled"].values() shouldBe citiesExampleFrame["settled"].values()
            // cities["page_in_wiki"].type() is URI, not supported by Arrow directly
            citiesDeserialized["page_in_wiki"].type() shouldBe typeOf<String>()
            citiesDeserialized["page_in_wiki"].values() shouldBe
                citiesExampleFrame["page_in_wiki"].values().map { it.toString() }
        }

        val testFile = File.createTempFile("cities", "arrow")
        citiesExampleFrame.writeArrowFeather(testFile)
        assertEstimation(DataFrame.readArrowFeather(testFile))

        val testByteArray = citiesExampleFrame.saveArrowIPCToByteArray()
        assertEstimation(DataFrame.readArrowIPC(testByteArray))
    }

    @Test
    fun testWritingBySchema() {
        val testFile = File.createTempFile("cities", "arrow")
        citiesExampleFrame.arrowWriter(Schema.fromJSON(citiesExampleSchema)).use { it.writeArrowFeather(testFile) }
        val citiesDeserialized = DataFrame.readArrowFeather(testFile, NullabilityOptions.Checking)
        citiesDeserialized["population"].type() shouldBe typeOf<Long?>()
        citiesDeserialized["area"].type() shouldBe typeOf<Float>()
        citiesDeserialized["settled"].type() shouldBe typeOf<LocalDateTime>()
        shouldThrow<IllegalArgumentException> { citiesDeserialized["page_in_wiki"] }
        citiesDeserialized["film_in_youtube"] shouldBe
            DataColumn.createValueColumn(
                name = "film_in_youtube",
                values = arrayOfNulls<String>(citiesExampleFrame.rowsCount()).asList(),
            )
    }

    @Test
    fun testWidening() {
        val warnings = ArrayList<ConvertingMismatch>()
        val testRestrictWidening = citiesExampleFrame.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode.STRICT,
        ) { warning ->
            warnings.add(warning)
        }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain(ConvertingMismatch.WideningMismatch.RejectedColumn("page_in_wiki"))
        shouldThrow<IllegalArgumentException> { DataFrame.readArrowFeather(testRestrictWidening)["page_in_wiki"] }

        val testAllowWidening = citiesExampleFrame.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode(
                restrictWidening = false,
                restrictNarrowing = true,
                strictType = true,
                strictNullable = true,
            ),
        ).use { it.saveArrowFeatherToByteArray() }
        DataFrame.readArrowFeather(testAllowWidening)["page_in_wiki"].values() shouldBe
            citiesExampleFrame["page_in_wiki"]
                .values()
                .map { it.toString() }
    }

    @Test
    fun testNarrowing() {
        val frameWithoutRequiredField = citiesExampleFrame.copy().remove("settled")

        frameWithoutRequiredField.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode.STRICT,
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testAllowNarrowing = frameWithoutRequiredField.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = false,
                strictType = true,
                strictNullable = true,
            ),
        ) { warning ->
            warnings.add(warning)
        }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain(ConvertingMismatch.NarrowingMismatch.NotPresentedColumnIgnored("settled"))
        shouldThrow<IllegalArgumentException> { DataFrame.readArrowFeather(testAllowNarrowing)["settled"] }
    }

    @Test
    fun testStrictType() {
        val frameRenaming = citiesExampleFrame.copy().remove("settled")
        val frameWithIncompatibleField =
            frameRenaming.add(
                frameRenaming["is_capital"]
                    .map { value -> value ?: false }
                    .rename("settled")
                    .convertToBoolean(),
            )

        frameWithIncompatibleField.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode.STRICT,
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testLoyalType = frameWithIncompatibleField.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = true,
                strictType = false,
                strictNullable = true,
            ),
        ) { warning ->
            warnings.add(warning)
        }.use { it.saveArrowFeatherToByteArray() }
        warnings.map { it.toString() }.shouldContain(
            ConvertingMismatch.TypeConversionNotFound.ConversionNotFoundIgnored(
                "settled",
                TypeConverterNotFoundException(
                    typeOf<Boolean>(),
                    typeOf<LocalDateTime?>(),
                    pathOf("settled"),
                ),
            ).toString(),
        )
        DataFrame.readArrowFeather(testLoyalType)["settled"].type() shouldBe typeOf<Boolean>()
    }

    @Test
    fun testStrictNullable() {
        val frameRenaming = citiesExampleFrame.copy().remove("settled")
        val frameWithNulls = frameRenaming.add(
            DataColumn.createValueColumn(
                "settled",
                arrayOfNulls<LocalDate>(frameRenaming.rowsCount()).asList(),
            ),
        )

        frameWithNulls.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode.STRICT,
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testLoyalNullable = frameWithNulls.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = true,
                strictType = true,
                strictNullable = false,
            ),
        ) { warning ->
            warnings.add(warning)
        }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain(ConvertingMismatch.NullableMismatch.NullValueIgnored("settled", 0))
        DataFrame.readArrowFeather(testLoyalNullable)["settled"].type() shouldBe typeOf<LocalDateTime?>()
        DataFrame.readArrowFeather(testLoyalNullable)["settled"].values() shouldBe
            arrayOfNulls<LocalDate>(frameRenaming.rowsCount()).asList()
    }

    @Test
    fun testParsing() {
        val columnStringDot = columnOf("12.345", "67.890")
        val columnStringComma = columnOf("12,345", "67,890")
        val frameString = dataFrameOf("columnDot", "columnComma")(columnStringDot, columnStringComma)
        val columnDoubleFraction = columnOf(12.345, 67.890)
        val columnDoubleRound = columnOf(12345.0, 67890.0)
        val targetType = FieldType.notNullable(ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE))
        val targetSchema = Schema(
            listOf(
                Field("columnDot", targetType, emptyList()),
                Field("columnComma", targetType, emptyList()),
            ),
        )

        val currentLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("en-US"))
            val serializedAsUs = frameString.arrowWriter(targetSchema).saveArrowFeatherToByteArray()
            DataFrame.readArrowFeather(serializedAsUs) shouldBe dataFrameOf("columnDot", "columnComma")(
                columnDoubleFraction,
                columnDoubleRound,
            )
            Locale.setDefault(Locale.forLanguageTag("ru-RU"))
            val serializedAsRu = frameString.arrowWriter(targetSchema).saveArrowFeatherToByteArray()
            DataFrame.readArrowFeather(serializedAsRu) shouldBe
                dataFrameOf("columnDot", "columnComma")(
                    columnDoubleFraction,
                    columnDoubleFraction,
                )
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    fun testBigStringColumn() {
        val dataFrame = dataFrameOf(bigStringColumn)
        val data = dataFrame.saveArrowFeatherToByteArray()
        DataFrame.readArrowFeather(data) shouldBe dataFrame
    }

    @Test
    fun testBigMixedColumn() {
        val dataFrame = dataFrameOf(bigMixedColumn)
        val warnings = ArrayList<ConvertingMismatch>()
        val writer = dataFrame.arrowWriter(
            targetSchema = Schema(
                listOf(
                    Field("bigMixedColumn", FieldType.nullable(ArrowType.Int(64, true)), emptyList()),
                ),
            ),
            mode = ArrowWriter.Mode.LOYAL,
        ) {
            warnings.add(it)
        }
        val stream = ByteArrayOutputStream()
        writer.writeArrowFeather(stream)
        val data = stream.toByteArray()

        assert(warnings.filterIsInstance<ConvertingMismatch.TypeConversionFail.ConversionFailIgnored>().size == 1)
        assert(warnings.filterIsInstance<ConvertingMismatch.SavedAsString>().size == 1)

        DataFrame.readArrowFeather(data)["bigMixedColumn"] shouldBe dataFrame[bigMixedColumn].map { it.toString() }
    }

    @Test
    fun testTimeStamp() {
        val dates = listOf(
            LocalDateTime(2023, 11, 23, 9, 30, 25),
            LocalDateTime(2015, 5, 25, 14, 20, 13),
            LocalDateTime(2013, 6, 19, 11, 20, 13),
        )

        val dataFrame = dataFrameOf(
            "ts_nano" to dates,
            "ts_micro" to dates,
            "ts_milli" to dates,
            "ts_sec" to dates,
        )

        DataFrame.readArrowFeather(writeArrowTimestamp(dates)) shouldBe dataFrame
        DataFrame.readArrowIPC(writeArrowTimestamp(dates, true)) shouldBe dataFrame
    }

    private fun writeArrowTimestamp(dates: List<LocalDateTime>, streaming: Boolean = false): ByteArray {
        RootAllocator().use { allocator ->
            val timeStampMilli = Field(
                "ts_milli",
                FieldType.nullable(ArrowType.Timestamp(TimeUnit.MILLISECOND, null)),
                null,
            )

            val timeStampMicro = Field(
                "ts_micro",
                FieldType.nullable(ArrowType.Timestamp(TimeUnit.MICROSECOND, null)),
                null,
            )

            val timeStampNano = Field(
                "ts_nano",
                FieldType.nullable(ArrowType.Timestamp(TimeUnit.NANOSECOND, null)),
                null,
            )

            val timeStampSec = Field(
                "ts_sec",
                FieldType.nullable(ArrowType.Timestamp(TimeUnit.SECOND, null)),
                null,
            )
            val schemaTimeStamp = Schema(
                listOf(timeStampNano, timeStampMicro, timeStampMilli, timeStampSec),
            )
            VectorSchemaRoot.create(schemaTimeStamp, allocator).use { vectorSchemaRoot ->
                val timeStampMilliVector = vectorSchemaRoot.getVector("ts_milli") as TimeStampMilliVector
                val timeStampNanoVector = vectorSchemaRoot.getVector("ts_nano") as TimeStampNanoVector
                val timeStampMicroVector = vectorSchemaRoot.getVector("ts_micro") as TimeStampMicroVector
                val timeStampSecVector = vectorSchemaRoot.getVector("ts_sec") as TimeStampSecVector
                timeStampMilliVector.allocateNew(dates.size)
                timeStampNanoVector.allocateNew(dates.size)
                timeStampMicroVector.allocateNew(dates.size)
                timeStampSecVector.allocateNew(dates.size)

                dates.forEachIndexed { index, localDateTime ->
                    val instant = localDateTime.toInstant(UtcOffset.ZERO).toJavaInstant()
                    timeStampNanoVector[index] = instant.toEpochMilli() * 1_000_000L + instant.nano
                    timeStampMicroVector[index] = instant.toEpochMilli() * 1_000L
                    timeStampMilliVector[index] = instant.toEpochMilli()
                    timeStampSecVector[index] = instant.toEpochMilli() / 1_000L
                }
                vectorSchemaRoot.setRowCount(dates.size)
                val bos = ByteArrayOutputStream()
                bos.use { out ->
                    val arrowWriter = if (streaming) {
                        ArrowStreamWriter(vectorSchemaRoot, null, Channels.newChannel(out))
                    } else {
                        ArrowFileWriter(vectorSchemaRoot, null, Channels.newChannel(out))
                    }
                    arrowWriter.use { writer ->
                        writer.start()
                        writer.writeBatch()
                    }
                }
                return bos.toByteArray()
            }
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

    @Test
    fun testArrowReaderExtension() {
        val expected = expectedSimpleDataFrame()
        val featherChannel = ByteArrayReadableSeekableByteChannel(expected.saveArrowFeatherToByteArray())
        val arrowFileReader = ArrowFileReader(featherChannel, RootAllocator())
        arrowFileReader.toDataFrame() shouldBe expected

        val ipcInputStream = ByteArrayInputStream(expected.saveArrowIPCToByteArray())
        val arrowStreamReader = ArrowStreamReader(ipcInputStream, RootAllocator())
        arrowStreamReader.toDataFrame() shouldBe expected
    }

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

            // query it
            val resultSet = it.createStatement()
                .executeQuery(
                    """select * from table1;""",
                )

            // since we are reading via DuckDB, we can safely cast resultSet to DuckDBResultSet
            resultSet as DuckDBResultSet

            // turn the DuckDBResultSet into an ArrowReader
            val dbArrowReader = resultSet.arrowExportStream(RootAllocator(), 256) as ArrowReader

            // and read out the reader from DataFrame!
            df1 = DataFrame.readArrow(dbArrowReader)

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
    fun `JDBC integration H2 PostgreSQL`() {
        val url =
            "jdbc:h2:mem:test3;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"

        val config = JdbcToArrowConfigBuilder()
            .setArraySubTypeByColumnNameMap(
                mapOf(
                    "dateArrayCol" to JdbcFieldInfo(Types.ARRAY),
                ),
            ).build()

        val quirks = JdbcQuirks.builder("h2")
            .typeConverter {
                if (it.jdbcType == Types.ARRAY) {
                    ArrowType.Date(DateUnit.DAY)
                } else {
                    JdbcToArrowUtils.getArrowTypeFromJdbcType(it.fieldInfo, null)
                }
            }
            .build()

        val db = JdbcDriver(RootAllocator())
            .open(
                buildMap {
                    AdbcDriver.PARAM_URI.set(this, url)
                    put(JdbcDriver.PARAM_JDBC_QUIRKS, quirks)
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
