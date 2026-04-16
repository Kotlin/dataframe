package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
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
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.ByteArrayReadableSeekableByteChannel
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertToBoolean
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.nio.channels.Channels
import java.sql.DriverManager
import java.util.Locale
import kotlin.io.path.toPath
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaInstant

internal class ArrowKtTest {

    fun testResource(resourcePath: String): URL = ArrowKtTest::class.java.classLoader.getResource(resourcePath)!!

    fun testArrowFeather(name: String) = testResource("$name.feather")

    fun testArrowIPC(name: String) = testResource("$name.ipc")

    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = DataFrame.readArrowFeather(feather)
        val a by columnOf("one")
        val b by columnOf(2.0)
        val c by columnOf(
            "c1" to columnOf("inner"),
            "c2" to columnOf(4.0),
            "c3" to columnOf(50.0),
        )
        val d by columnOf("four")
        val expected = dataFrameOf(a, b, c, d)
        df shouldBe expected
    }

    @Test
    fun testReadingMultipleBatches() {
        val df = DataFrame.readArrowFeather(testArrowFeather("multiple_batches_concat"))
        df.schema().print()
        df.schema().asClue {
            df["id"].type() shouldBe typeOf<Int>()
            val person = df["person"].shouldBeInstanceOf<ColumnGroup<*>>()
            person["name"].type() shouldBe typeOf<String>()
            person["age"].type() shouldBe typeOf<Int>()
        }
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
        val frameWithoutRequiredField = citiesExampleFrame.remove("settled")

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
        val frameRenaming = citiesExampleFrame.remove("settled")
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
                    typeOf<kotlinx.datetime.LocalDateTime?>(),
                    pathOf("settled"),
                ),
            ).toString(),
        )
        DataFrame.readArrowFeather(testLoyalType)["settled"].type() shouldBe typeOf<Boolean>()
    }

    @Test
    fun testStrictNullable() {
        val frameRenaming = citiesExampleFrame.remove("settled")
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

        DataFrame.readArrowFeather(data)["bigMixedColumn"] shouldBe dataFrame["bigMixedColumn"].map { it.toString() }
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

    @Test
    fun testReadParquetPath() {
        val resourceUrl = testResource("test.arrow.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val dataFrame = DataFrame.readParquet(resourcePath)

        dataFrame.rowsCount() shouldBe 300
        assertEstimations(
            exampleFrame = dataFrame,
            expectedNullable = false,
            hasNulls = false,
            fromParquet = true,
        )
    }

    @Test
    fun testReadParquetFile() {
        val resourceUrl = testResource("test.arrow.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val dataFrame = DataFrame.readParquet(resourcePath.toFile())

        dataFrame.rowsCount() shouldBe 300
        assertEstimations(
            exampleFrame = dataFrame,
            expectedNullable = false,
            hasNulls = false,
            fromParquet = true,
        )
    }

    @Test
    fun testReadParquetStringPath() {
        val resourceUrl = testResource("test.arrow.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val dataFrame = DataFrame.readParquet("$resourcePath")

        dataFrame.rowsCount() shouldBe 300
        assertEstimations(
            exampleFrame = dataFrame,
            expectedNullable = false,
            hasNulls = false,
            fromParquet = true,
        )
    }

    @Test
    fun testReadParquetUrl() {
        val resourceUrl = testResource("test.arrow.parquet")
        val resourcePath = resourceUrl.toURI().toPath()
        val fileUrl = resourcePath.toUri().toURL()

        val dataFrame = DataFrame.readParquet(fileUrl)

        dataFrame.rowsCount() shouldBe 300
        assertEstimations(
            exampleFrame = dataFrame,
            expectedNullable = false,
            hasNulls = false,
            fromParquet = true,
        )
    }

    @Test
    fun testReadMultipleParquetFiles() {
        val resourceUrl = testResource("test.arrow.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val dataFrame = DataFrame.readParquet(resourcePath, resourcePath, resourcePath)

        dataFrame.rowsCount() shouldBe 900
    }

    @Test
    fun testColumnGroupRoundtrip() {
        val original = dataFrameOf(
            "outer" to columnOf("x", "y", "z"),
            "inner" to columnOf(
                "nested1" to columnOf("a", "b", "c"),
                "nested2" to columnOf(1, 2, 3),
            ),
        )

        val featherBytes = original.saveArrowFeatherToByteArray()
        val fromFeather = DataFrame.readArrowFeather(featherBytes)
        fromFeather shouldBe original

        val ipcBytes = original.saveArrowIPCToByteArray()
        val fromIpc = DataFrame.readArrowIPC(ipcBytes)
        fromIpc shouldBe original
    }

    @Test
    fun testNestedColumnGroupRoundtrip() {
        val deeplyNested by columnOf(
            "level2" to columnOf(
                "level3" to columnOf(1, 2, 3),
            ),
        )
        val original = dataFrameOf(deeplyNested)

        val bytes = original.saveArrowFeatherToByteArray()
        val restored = DataFrame.readArrowFeather(bytes)

        restored shouldBe original
    }

    @Test
    fun testColumnGroupWithNulls() {
        val group by columnOf(
            "a" to columnOf("x", null, "z"),
            "b" to columnOf(1, 2, null),
        )
        val original = dataFrameOf(group)

        val bytes = original.saveArrowFeatherToByteArray()
        val restored = DataFrame.readArrowFeather(bytes)

        restored shouldBe original
    }

    @Test
    fun testReadParquetWithNestedStruct() {
        val resourceUrl = testResource("books.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val df = DataFrame.readParquet(resourcePath)

        df.columnNames() shouldBe listOf("id", "title", "author", "genre", "publisher")

        val authorGroup = df["author"] as ColumnGroup<*>
        authorGroup.columnNames() shouldBe listOf("id", "firstName", "lastName")

        df["id"].type() shouldBe typeOf<Int>()
        df["title"].type() shouldBe typeOf<String>()
        df["genre"].type() shouldBe typeOf<String>()
        df["publisher"].type() shouldBe typeOf<String>()
        authorGroup["id"].type() shouldBe typeOf<Int>()
        authorGroup["firstName"].type() shouldBe typeOf<String>()
        authorGroup["lastName"].type() shouldBe typeOf<String>()
    }

    @Test
    fun testParquetNestedStructRoundtrip() {
        val resourceUrl = testResource("books.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val original = DataFrame.readParquet(resourcePath)

        val featherBytes = original.saveArrowFeatherToByteArray()
        val fromFeather = DataFrame.readArrowFeather(featherBytes)
        fromFeather shouldBe original

        val ipcBytes = original.saveArrowIPCToByteArray()
        val fromIpc = DataFrame.readArrowIPC(ipcBytes)
        fromIpc shouldBe original
    }

    @Test
    fun testReadParquetWithListColumns() {
        val resourceUrl = testResource("lists.parquet")
        val resourcePath = resourceUrl.toURI().toPath()

        val df = DataFrame.readParquet(resourcePath)

        df.columnNames() shouldBe listOf("id", "numbers", "strings", "nullable_list", "list_with_nulls")
        df.rowsCount() shouldBe 3

        df["id"].type() shouldBe typeOf<Long>()
        df["numbers"].type() shouldBe typeOf<List<Long>>()
        df["strings"].type() shouldBe typeOf<List<String>>()
        df["nullable_list"].type() shouldBe typeOf<List<Long>?>()
        df["list_with_nulls"].type() shouldBe typeOf<List<Long?>>()

        df["id"].values().toList() shouldBe listOf(1L, 2L, 3L)
        df["numbers"].values().toList() shouldBe listOf(listOf(1L, 2L, 3L), listOf(4L, 5L), listOf(6L))
        df["strings"].values().toList() shouldBe listOf(listOf("a", "b"), listOf("c"), listOf("d", "e", "f"))
        df["nullable_list"].values().toList() shouldBe listOf(listOf(1L, 2L), null, listOf(3L))
        df["list_with_nulls"].values().toList() shouldBe listOf(listOf(1L, null, 3L), listOf(4L, 5L), listOf(null))
    }

    @Test
    fun `read parquet with nested struct inside list`() {
        val resourceUrl = testResource("orders_nested.parquet")
        val resourcePath = resourceUrl.toURI().toPath()
        val df = DataFrame.readParquet(resourcePath)

        // structure
        df.rowsCount() shouldBe 3
        df.columnNames() shouldBe listOf("id", "orders", "note")
        df["orders"].shouldBeInstanceOf<FrameColumn<*>>()

        // top-level types
        df["id"].type() shouldBe typeOf<Int>()
        df["note"].type() shouldBe typeOf<String?>()

        // top-level values
        df["id"].values().toList() shouldBe listOf(1, 2, 3)
        df["note"].values().toList() shouldBe listOf("first", null, "empty")

        // frame column
        val orders = df["orders"] as FrameColumn<*>
        orders[0].rowsCount() shouldBe 2
        orders[1].rowsCount() shouldBe 1
        orders[2].rowsCount() shouldBe 0

        // nested values — row 0
        orders[0]["item"].values().toList() shouldBe listOf("Laptop", "Mouse")
        orders[0]["qty"].values().toList() shouldBe listOf(1, 2)
        val details0 = orders[0]["details"] as ColumnGroup<*>
        details0["price"].values().toList() shouldBe listOf(999.99, 25.5)
        details0["currency"].values().toList() shouldBe listOf("EUR", "EUR")

        // nested values — row 1
        orders[1]["item"].values().toList() shouldBe listOf("Keyboard")
        orders[1]["qty"].values().toList() shouldBe listOf(1)
        val details1 = orders[1]["details"] as ColumnGroup<*>
        details1["price"].values().toList() shouldBe listOf(75.0)
        details1["currency"].values().toList() shouldBe listOf("USD")

        // schema
        df.schema().toString().trim() shouldBe
            """
            id: Int
            orders: *
                item: String
                qty: Int
                details:
                    price: Double
                    currency: String
            
            note: String?
            """.trimIndent()
    }

    @Test
    fun `read parquet with two batches and nulls in second batch`() {
        val resourceUrl = testResource("orders_two_batches.parquet")
        val resourcePath = resourceUrl.toURI().toPath()
        val df = DataFrame.readParquet(resourcePath)

        // structure
        df.rowsCount() shouldBe 4
        df.columnNames() shouldBe listOf("id", "orders", "note")
        df["orders"].shouldBeInstanceOf<FrameColumn<*>>()

        // top-level types & values
        df["id"] shouldBe columnOf(1, 2, 3, 4).named("id")
        df["note"] shouldBe columnOf("first", "second", null, "fourth").named("note")

        // frame column sizes
        val orders = df["orders"] as FrameColumn<*>
        orders[0].rowsCount() shouldBe 2
        orders[1].rowsCount() shouldBe 1
        orders[2].rowsCount() shouldBe 2
        orders[3].rowsCount() shouldBe 0

        // batch 1 — no nulls
        orders[0]["item"].values().toList() shouldBe listOf("Laptop", "Mouse")
        // we're deliberately skipping our usual nullability narrowing for ListVector<StructVector> FrameColumns
        orders[0]["item"].type() shouldBe typeOf<String?>()
        orders[0]["qty"].values().toList() shouldBe listOf(1, 2)
        val details0 = orders[0]["details"] as ColumnGroup<*>
        details0["price"].values().toList() shouldBe listOf(999.99, 25.5)
        details0["currency"].values().toList() shouldBe listOf("EUR", "EUR")

        // batch 2 — nulls at every level
        orders[2]["item"].values().toList() shouldBe listOf(null, "Cable")
        orders[2]["qty"].values().toList() shouldBe listOf(3, null)
        val details2 = orders[2]["details"] as ColumnGroup<*>
        details2["price"].values().toList() shouldBe listOf(10.0, null)
        details2["currency"].values().toList() shouldBe listOf(null, null)

        // schema — nullable types from batch 2
        df.schema().toString().trim() shouldBe
            """
            id: Int
            orders: *
                item: String?
                qty: Int?
                details:
                    price: Double?
                    currency: String?
            
            note: String?
            """.trimIndent()
    }

    @Test
    fun `read parquet with lists of all primitive types`() {
        val resourceUrl = testResource("lists_all_types.parquet")
        val resourcePath = resourceUrl.toURI().toPath()
        val df = DataFrame.readParquet(resourcePath)

        df.rowsCount() shouldBe 2

        // === Types ===

        // signed ints
        df["list_int8"].type() shouldBe typeOf<List<Byte>>()
        df["list_int16"].type() shouldBe typeOf<List<Short>>()
        df["list_int32"].type() shouldBe typeOf<List<Int>>()
        df["list_int64"].type() shouldBe typeOf<List<Long>>()

        // unsigned ints (widened per toKType)
        df["list_uint8"].type() shouldBe typeOf<List<Short>>()
        df["list_uint16"].type() shouldBe typeOf<List<Int>>()
        df["list_uint32"].type() shouldBe typeOf<List<Long>>()
        df["list_uint64"].type() shouldBe typeOf<List<BigInteger>>()

        // float / double
        df["list_float"].type() shouldBe typeOf<List<Float>>()
        df["list_double"].type() shouldBe typeOf<List<Double>>()

        // decimal
        df["list_decimal"].type() shouldBe typeOf<List<BigDecimal>>()

        // string (large_string also becomes String through Parquet)
        df["list_string"].type() shouldBe typeOf<List<String>>()
        df["list_large_string"].type() shouldBe typeOf<List<String>>()

        // bool
        df["list_bool"].type() shouldBe typeOf<List<Boolean>>()

        // binary (large_binary also becomes ByteArray through Parquet)
        df["list_binary"].type() shouldBe typeOf<List<ByteArray>>()
        df["list_large_binary"].type() shouldBe typeOf<List<ByteArray>>()

        // date (both date32 and date64 become LocalDate in Parquet)
        df["list_date_day"].type() shouldBe typeOf<List<LocalDate>>()
        df["list_date_ms"].type() shouldBe typeOf<List<LocalDate>>()

        // time
        df["list_time"].type() shouldBe typeOf<List<LocalTime>>()

        // timestamp
        df["list_timestamp"].type() shouldBe typeOf<List<LocalDateTime>>()

        // duration loses logical type in Parquet → falls back to Long
        df["list_duration"].type() shouldBe typeOf<List<Duration>>()

        // nullability combos
        df["nullable_list"].type() shouldBe typeOf<List<Int>?>()
        df["nullable_elements"].type() shouldBe typeOf<List<Int?>>()
        df["both_nullable"].type() shouldBe typeOf<List<Int?>?>()

        // === Values ===

        // signed ints
        df["list_int8"].values().toList() shouldBe listOf(
            listOf(1.toByte(), (-1).toByte()),
            emptyList<Byte>(),
        )
        df["list_int16"].values().toList() shouldBe listOf(
            listOf(1000.toShort(), (-1000).toShort()),
            listOf(42.toShort()),
        )
        df["list_int32"].values().toList() shouldBe listOf(listOf(100000, -100000), emptyList<Int>())
        df["list_int64"].values().toList() shouldBe listOf(listOf(10000000000L, -10000000000L), listOf(0L))

        // unsigned ints (widened types)
        df["list_uint8"].values().toList() shouldBe listOf(
            listOf(0.toShort(), 255.toShort()),
            listOf(128.toShort()),
        )
        df["list_uint16"].values().toList() shouldBe listOf(listOf(0, 65535), emptyList<Int>())
        df["list_uint32"].values().toList() shouldBe listOf(listOf(0L, 4294967295L), listOf(1L))
        df["list_uint64"].values().toList() shouldBe listOf(
            listOf(BigInteger.ZERO, BigInteger("9223372036854775808")),
            emptyList<BigInteger>(),
        )

        // float / double
        df["list_float"].values().toList() shouldBe listOf(listOf(1.5f, -1.5f), listOf(0.0f))
        df["list_double"].values().toList() shouldBe listOf(listOf(1.5, -1.5), emptyList<Double>())

        // decimal
        df["list_decimal"].values().toList() shouldBe listOf(
            listOf(BigDecimal("123.45"), BigDecimal("-67.89")),
            listOf(BigDecimal("0.00")),
        )

        // string
        df["list_string"].values().toList() shouldBe listOf(listOf("hello", "world"), emptyList<String>())
        df["list_large_string"].values().toList() shouldBe listOf(listOf("foo", "bar"), listOf("baz"))

        // bool
        df["list_bool"].values().toList() shouldBe listOf(listOf(true, false), listOf(true))

        // date
        df["list_date_day"].values().toList() shouldBe listOf(
            listOf(LocalDate(2024, 1, 15), LocalDate(2025, 6, 30)),
            emptyList<LocalDate>(),
        )
        df["list_date_ms"].values().toList() shouldBe listOf(
            listOf(LocalDate(2024, 1, 15), LocalDate(2025, 6, 30)),
            listOf(LocalDate(2000, 1, 1)),
        )

        // time
        df["list_time"].values().toList() shouldBe listOf(
            listOf(LocalTime(9, 30, 0), LocalTime(14, 15, 30)),
            emptyList<LocalTime>(),
        )

        // timestamp
        df["list_timestamp"].values().toList() shouldBe listOf(
            listOf(LocalDateTime(2024, 1, 15, 9, 30), LocalDateTime(2025, 6, 30, 14, 0)),
            listOf(LocalDateTime(2000, 1, 1, 0, 0)),
        )

        // duration (raw microseconds as Long)
        df["list_duration"].values().toList() shouldBe listOf(
            listOf(1.hours, 30.minutes),
            emptyList<Duration>(),
        )

        // null vs empty list
        df["nullable_list"].values().toList() shouldBe listOf(listOf(10, 20), null)
        df["nullable_elements"].values().toList() shouldBe listOf(listOf(1, null, 3), listOf(null, null))
        df["both_nullable"].values().toList() shouldBe listOf(listOf(1, null), null)
    }

    @Test
    fun `read LargeListVector`() {
        val resourceUrl = testResource("large_list_sample.parquet")
        val resourcePath = resourceUrl.toURI().toPath()
        val df = DataFrame.readParquet(resourcePath)
        df["numbers"].type() shouldBe typeOf<List<Long>>()
        df["tags"].type() shouldBe typeOf<List<String>?>()
        df["numbers"].values() shouldBe listOf(listOf(10L, 20L, 30L), listOf(40L), listOf(50L, 60L))
    }
}
