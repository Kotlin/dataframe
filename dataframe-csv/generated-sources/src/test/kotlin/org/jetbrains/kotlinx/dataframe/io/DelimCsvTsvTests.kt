package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.parsers.Parsers
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.allNulls
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toStr
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.StringWriter
import java.math.BigDecimal
import java.net.URL
import java.util.Locale
import java.util.zip.GZIPInputStream
import kotlin.reflect.KClass
import kotlin.reflect.typeOf
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

//  can be enabled for showing logs for these tests
private const val SHOW_LOGS = false

@Suppress("ktlint:standard:argument-list-wrapping")
class DelimCsvTsvTests {

    private val logLevel = "org.slf4j.simpleLogger.log.${FastDoubleParser::class.qualifiedName}"
    private var loggerBefore: String? = null

    @Before
    fun setLogger() {
        if (!SHOW_LOGS) return
        loggerBefore = System.getProperty(logLevel)
        System.setProperty(logLevel, "trace")
    }

    @After
    fun restoreLogger() {
        if (!SHOW_LOGS) return
        if (loggerBefore != null) {
            System.setProperty(logLevel, loggerBefore)
        }
    }

    @Test
    fun readNulls() {
        @Language("CSV")
        val src =
            """
            first,second
            2,,
            3,,
            """.trimIndent()
        val df = DataFrame.readCsvStr(src)
        df.rowsCount() shouldBe 2
        df.columnsCount() shouldBe 2
        df["first"].type() shouldBe typeOf<Int>()
        df["second"].allNulls() shouldBe true
        df["second"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun write() {
        val df = dataFrameOf("col1", "col2")(
            1, null,
            2, null,
        ).convert("col2").toStr()

        val str = StringWriter()
        df.writeCsv(str)

        val res = DataFrame.readCsvStr(str.buffer.toString())

        res shouldBe df
    }

    @Test
    fun readCsv() {
        val df = DataFrame.read(simpleCsv)

        df.columnsCount() shouldBe 11
        df.rowsCount() shouldBe 5
        df.columnNames()[5] shouldBe "duplicate1"
        df.columnNames()[6] shouldBe "duplicate11"
        df["duplicate1"].type() shouldBe typeOf<Char?>()
        df["double"].type() shouldBe typeOf<Double?>()
        df["number"].type() shouldBe typeOf<Double>()
        df["time"].type() shouldBe typeOf<LocalDateTime>()

        df.print(columnTypes = true, borders = true, title = true)
    }

    @Test
    fun `readCsv different charset`() {
        val df = DataFrame.readCsv(simpleCsv)

        DataFrame.readCsv(simpleCsvUtf16le) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16le, Charsets.UTF_16LE) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16le, Charsets.UTF_16BE) shouldNotBe df
        DataFrame.readCsv(simpleCsvUtf16le, Charsets.UTF_8) shouldNotBe df
    }

    @Test
    fun `readCsv gz compressed different charset`() {
        val df = DataFrame.readCsv(simpleCsv)

        DataFrame.readCsv(simpleCsvUtf16leGz) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16leGz, Charsets.UTF_16LE) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16leGz, Charsets.UTF_16BE) shouldNotBe df
        DataFrame.readCsv(simpleCsvUtf16leGz, Charsets.UTF_8) shouldNotBe df
    }

    @Test
    fun `readCsv zip compressed different charset`() {
        val df = DataFrame.readCsv(simpleCsv)

        DataFrame.readCsv(simpleCsvUtf16leZip) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16leZip, Charsets.UTF_16LE) shouldBe df
        DataFrame.readCsv(simpleCsvUtf16leZip, Charsets.UTF_16BE) shouldNotBe df
        DataFrame.readCsv(simpleCsvUtf16leZip, Charsets.UTF_8) shouldNotBe df
    }

    @Test
    fun `read ZIP Csv`() {
        DataFrame.readCsv(simpleCsvZip) shouldBe DataFrame.readCsv(simpleCsv)

        shouldThrow<IllegalStateException> {
            DataFrame.readCsv(notCsv)
        }
    }

    @Test
    fun `read GZ Csv`() {
        DataFrame.readCsv(simpleCsvGz) shouldBe DataFrame.readCsv(simpleCsv)
    }

    @Test
    fun `read custom compression Csv`() {
        DataFrame.readCsv(
            simpleCsvGz,
            compression = Compression(::GZIPInputStream),
        ) shouldBe DataFrame.readCsv(simpleCsv)
    }

    @Test
    fun `read 2 compressed Csv`() {
        shouldThrow<IllegalArgumentException> { DataFrame.readCsv(twoCsvsZip) }
    }

    @Test
    fun readCsvWithFrenchLocaleAndAlternativeDelimiter() {
        val df = DataFrame.readCsv(
            url = csvWithFrenchLocale,
            delimiter = ';',
            parserOptions = ParserOptions(locale = Locale.FRENCH),
        )

        df.columnsCount() shouldBe 11
        df.rowsCount() shouldBe 5
        df.columnNames()[5] shouldBe "duplicate1"
        df.columnNames()[6] shouldBe "duplicate11"
        df["duplicate1"].type() shouldBe typeOf<Char?>()
        df["double"].type() shouldBe typeOf<Double?>()
        df["number"].type() shouldBe typeOf<Double>()
        df["time"].type() shouldBe typeOf<LocalDateTime>()

        println(df)
    }

    private fun assertColumnType(columnName: String, kClass: KClass<*>, schema: DataFrameSchema) {
        val col = schema.columns[columnName]
        col.shouldNotBeNull()
        col.type.classifier shouldBe kClass
    }

    @Test
    fun readCsvWithFloats() {
        val df = DataFrame.readCsv(wineCsv, delimiter = ';')
        val schema = df.schema()

        assertColumnType("citric acid", Double::class, schema)
        assertColumnType("alcohol", Double::class, schema)
        assertColumnType("quality", Int::class, schema)
    }

    @Test
    fun `read standard CSV with floats when user has alternative locale`() {
        val currentLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("ru-RU"))
            val df = DataFrame.readCsv(wineCsv, delimiter = ';')
            val schema = df.schema()

            assertColumnType("citric acid", Double::class, schema)
            assertColumnType("alcohol", Double::class, schema)
            assertColumnType("quality", Int::class, schema)
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    fun `read with custom header`() {
        val header = ('A'..'K').map { it.toString() }
        val df = DataFrame.readCsv(simpleCsv, header = header, skipLines = 1)
        df.columnNames() shouldBe header
        df["B"].type() shouldBe typeOf<Int>()

        val headerShort = ('A'..'E').map { it.toString() }
        val dfShort = DataFrame.readCsv(simpleCsv, header = headerShort, skipLines = 1)
        dfShort.columnsCount() shouldBe 5
        dfShort.columnNames() shouldBe headerShort
    }

    @Test
    fun `read first rows`() {
        val expected =
            listOf(
                "untitled",
                "user_id",
                "name",
                "duplicate",
                "username",
                "duplicate1",
                "duplicate11",
                "double",
                "number",
                "time",
                "empty",
            )
        val dfHeader = DataFrame.readCsv(simpleCsv, readLines = 0)
        dfHeader.rowsCount() shouldBe 0
        dfHeader.columnNames() shouldBe expected

        val dfThree = DataFrame.readCsv(simpleCsv, readLines = 3)
        dfThree.rowsCount() shouldBe 3

        val dfFull = DataFrame.readCsv(simpleCsv, readLines = 10)
        dfFull.rowsCount() shouldBe 5
    }

    @Test
    fun `if string starts with a number, it should be parsed as a string anyway`() {
        @Language("CSV")
        val df = DataFrame.readCsvStr(
            """
            duration,floatDuration
            12 min,1.0
            15,12.98 sec
            1 Season,0.9 parsec
            """.trimIndent(),
        )
        df["duration"].type() shouldBe typeOf<String>()
        df["floatDuration"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `if record has fewer columns than header then pad it with nulls`() {
        @Language("CSV")
        val csvContent =
            """
            col1,col2,col3
            568,801,587
            780,588
            """.trimIndent()

        val df = shouldNotThrowAny {
            DataFrame.readCsvStr(csvContent)
        }

        df shouldBe dataFrameOf("col1", "col2", "col3")(
            568, 801, 587,
            780, 588, null,
        )
    }

    @Test
    fun `write and read frame column`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 3, 2,
            2, 1, 3,
        )
        val grouped = df.groupBy("a").into("g")
        val str = grouped.toCsvStr(escapeChar = null)
        val res = DataFrame.readCsvStr(str, quote = '"')
        res shouldBe grouped
    }

    @Test
    fun `write and read column group`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 3, 2,
        )
        val grouped = df.group("b", "c").into("d")
        val str = grouped.toCsvStr()
        val res = DataFrame.readCsvStr(str)
        res shouldBe grouped
    }

    @Test
    fun `CSV String of saved dataframe starts with column name`() {
        val df = dataFrameOf("a")(1)
        df.toCsvStr().first() shouldBe 'a'
    }

    @Test
    fun `guess tsv`() {
        val df = DataFrame.read(testResource("abc.tsv"))
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
    }

    @Test
    fun `write csv without header produce correct file`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 3, 2,
        )
        df.writeCsv(
            path = "src/test/resources/without_header.csv",
            includeHeader = false,
            recordSeparator = "\r\n",
        )
        val producedFile = File("src/test/resources/without_header.csv")
        producedFile.exists() shouldBe true
        producedFile.readText() shouldBe "1,2,3\r\n1,3,2\r\n"
        producedFile.delete()
    }

    @Test
    fun `check integrity of example data`() {
        shouldThrow<IllegalStateException> {
            // cannot read file with blank line at the start
            DataFrame.readCsv("../data/jetbrains repositories.csv")
        }
        shouldThrow<IllegalStateException> {
            // ignoreEmptyLines only ignores intermediate empty lines
            DataFrame.readCsv("../data/jetbrains repositories.csv", ignoreEmptyLines = true)
        }

        val df = DataFrame.readCsv(
            "../data/jetbrains repositories.csv",
            skipLines = 1, // we need to skip the empty lines manually
        )
        df.columnNames() shouldBe listOf("full_name", "html_url", "stargazers_count", "topics", "watchers")
        df.columnTypes() shouldBe listOf(
            typeOf<String>(),
            typeOf<URL>(),
            typeOf<Int>(),
            typeOf<String>(),
            typeOf<Int>(),
        )
        // same file without empty line at the beginning
        df shouldBe DataFrame.readCsv("../data/jetbrains_repositories.csv")
    }

    @Test
    fun `readCsvStr delimiter`() {
        @Language("TSV")
        val tsv =
            """
            a	b	c
            1	2	3
            """.trimIndent()
        val df = DataFrame.readCsvStr(tsv, '\t')
        df shouldBe dataFrameOf("a", "b", "c")(1, 2, 3)
    }

    @Test
    fun `file with BOM`() {
        val df = DataFrame.readCsv(withBomCsv, delimiter = ';')
        df.columnNames() shouldBe listOf("Column1", "Column2")
    }

    @Test
    fun `read empty CSV`() {
        val emptyDelimStr = DataFrame.readCsvStr("")
        emptyDelimStr shouldBe DataFrame.empty()

        val emptyWidthStr = DataFrame.readCsvStr("", hasFixedWidthColumns = true)
        emptyWidthStr shouldBe DataFrame.empty()

        val emptyCsvFile = DataFrame.readCsv(File.createTempFile("empty", "csv"))
        emptyCsvFile shouldBe DataFrame.empty()

        val emptyCsvFileManualHeader = DataFrame.readCsv(
            file = File.createTempFile("empty", "csv"),
            header = listOf("a", "b", "c"),
        )
        emptyCsvFileManualHeader.apply {
            isEmpty() shouldBe true
            columnNames() shouldBe listOf("a", "b", "c")
            columnTypes() shouldBe listOf(typeOf<String>(), typeOf<String>(), typeOf<String>())
        }

        val emptyCsvFileWithHeader = DataFrame.readCsv(
            file = File.createTempFile("empty", "csv").also { it.writeText("a,b,c") },
        )
        emptyCsvFileWithHeader.apply {
            isEmpty() shouldBe true
            columnNames() shouldBe listOf("a", "b", "c")
            columnTypes() shouldBe listOf(typeOf<String>(), typeOf<String>(), typeOf<String>())
        }

        val emptyTsvStr = DataFrame.readTsv(File.createTempFile("empty", "tsv"))
        emptyTsvStr shouldBe DataFrame.empty()
    }

    @Test
    fun `read Csv with comments`() {
        @Language("CSV")
        val csv =
            """
            # This is a comment
            a,b,c
            1,2,3
            """.trimIndent()
        val df = DataFrame.readCsvStr(csv, skipLines = 1L)
        df shouldBe dataFrameOf("a", "b", "c")(1, 2, 3)
    }

    @Test
    fun `csv with empty lines`() {
        @Language("CSV")
        val csv =
            """
            a,b,c
            1,2,3
            
            4,5,6
            """.trimIndent()
        val df1 = DataFrame.readCsvStr(csv)
        df1 shouldBe dataFrameOf("a", "b", "c")(
            1, 2, 3,
            null, null, null,
            4, 5, 6,
        )

        val df2 = DataFrame.readCsvStr(csv, ignoreEmptyLines = true)
        df2 shouldBe dataFrameOf("a", "b", "c")(
            1, 2, 3,
            4, 5, 6,
        )

        shouldThrow<IllegalStateException> { DataFrame.readCsvStr(csv, allowMissingColumns = false) }
    }

    @Test
    fun `don't read folder`() {
        shouldThrow<IllegalArgumentException> { DataFrame.readCsv("") }
        shouldThrow<IllegalArgumentException> { DataFrame.readCsv("NON EXISTENT FILE") }
    }

    @Test
    fun `cannot auto-parse specific date string`() {
        @Language("csv")
        val frenchCsv =
            """
            name; price; date;
            a;12,45; 05/06/2021;
            b;-13,35;14/07/2025;
            c;100 123,35;;
            d;-204 235,23;;
            e;NaN;;
            f;null;;
            """.trimIndent()

        val dfDeephaven = DataFrame.readCsvStr(
            text = frenchCsv,
            delimiter = ';',
        )

        // could not parse, remains String
        dfDeephaven["date"].type() shouldBe typeOf<String?>()

        val dfDataFrame = DataFrame.readCsvStr(
            text = frenchCsv,
            delimiter = ';',
            // setting any locale skips deephaven's date parsing
            parserOptions = ParserOptions(locale = Locale.ROOT),
        )

        // could not parse, remains String
        dfDataFrame["date"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `parse with other locales`() {
        @Language("csv")
        val frenchCsv =
            """
            name; price; date;
            a;12,45; 05/06/2021;
            b;-13,35;14/07/2025;
            c;100 123,35;;
            d;-204 235,23;;
            e;NaN;;
            f;null;;
            """.trimIndent()

        val frenchDf = DataFrame.readCsvStr(
            text = frenchCsv,
            delimiter = ';',
            parserOptions = ParserOptions(
                dateTimePattern = "dd/MM/yyyy",
                locale = Locale.FRENCH,
            ),
        )

        frenchDf["price"].type() shouldBe typeOf<Double?>()
        frenchDf["date"].type() shouldBe typeOf<LocalDate?>()

        @Language("csv")
        val dutchCsv =
            """
            name; price;
            a;12,45;
            b;-13,35;
            c;100.123,35;
            d;-204.235,23;
            e;NaN;
            f;null;
            """.trimIndent()

        val dutchDf = DataFrame.readCsvStr(
            text = dutchCsv,
            delimiter = ';',
            parserOptions = ParserOptions(
                locale = Locale.forLanguageTag("nl-NL"),
            ),
        )

        dutchDf["price"].type() shouldBe typeOf<Double?>()

        // skipping this test on windows due to lack of support for Arabic locales
        if (!System.getProperty("os.name").startsWith("Windows")) {
            // while negative numbers in RTL languages cannot be parsed thanks to Java, others work
            @Language("csv")
            val arabicCsv =
                """
                الاسم; السعر;
                أ;١٢٫٤٥;
                ب;١٣٫٣٥;
                ج;١٠٠٫١٢٣;
                د;٢٠٤٫٢٣٥;
                هـ;ليس رقم;
                و;null;
                """.trimIndent()

            val easternArabicDf = DataFrame.readCsvStr(
                arabicCsv,
                delimiter = ';',
                parserOptions = ParserOptions(
                    locale = Locale.forLanguageTag("ar-001"),
                ),
            )

            easternArabicDf["السعر"].type() shouldBe typeOf<Double?>()
            easternArabicDf["الاسم"].type() shouldBe typeOf<String>() // apparently not a char
        }
    }

    @Test
    fun `handle slightly mixed locales`() {
        @Language("csv")
        val estonianWrongMinus =
            """
            name; price;
            a;12,45;
            b;-13,35;
            c;100 123,35;
            d;-204 235,23;
            e;NaN;
            f;null;
            """.trimIndent()

        val estonianDf1 = DataFrame.readCsvStr(
            text = estonianWrongMinus,
            delimiter = ';',
            parserOptions = ParserOptions(
                locale = Locale.forLanguageTag("et-EE"),
            ),
        )

        estonianDf1["price"].type() shouldBe typeOf<Double?>()

        // also test the global setting
        DataFrame.parser.locale = Locale.forLanguageTag("et-EE")

        val estonianDf2 = DataFrame.readCsvStr(
            text = estonianWrongMinus,
            delimiter = ';',
        )
        estonianDf2 shouldBe estonianDf1

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun `NA and custom null string in double column`() {
        val df1 = DataFrame.readCsv(
            msleepCsv,
            parserOptions = ParserOptions(
                nullStrings = DEFAULT_DELIM_NULL_STRINGS + "nothing",
            ),
        )

        df1["name"].type() shouldBe typeOf<String>()
        df1["genus"].type() shouldBe typeOf<String>()
        df1["vore"].type() shouldBe typeOf<String?>()
        df1["order"].type() shouldBe typeOf<String>()
        df1["conservation"].type() shouldBe typeOf<String?>()
        df1["sleep_total"].type() shouldBe typeOf<Double>()
        df1["sleep_rem"].type() shouldBe typeOf<Double?>()
        df1["sleep_cycle"].type() shouldBe typeOf<Double?>()
        df1["awake"].type() shouldBe typeOf<Double>()
        df1["brainwt"].type() shouldBe typeOf<Double?>()
        df1["bodywt"].type() shouldBe typeOf<Double?>()

        // Also test the global setting
        DataFrame.parser.addNullString("nothing")
        DEFAULT_DELIM_NULL_STRINGS.forEach {
            DataFrame.parser.addNullString(it)
        }

        val df2 = DataFrame.readCsv(msleepCsv)
        df2 shouldBe df1

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun `multiple spaces as delimiter`() {
        @Language("csv")
        val csv =
            """
            NAME                     STATUS   AGE      NUMBER   LABELS
            argo-events              Active   2y77d    1234     app.kubernetes.io/instance=argo-events,kubernetes.io/metadata.name=argo-events
            argo-workflows           Active   2y77d    1234     app.kubernetes.io/instance=argo-workflows,kubernetes.io/metadata.name=argo-workflows
            argocd                   Active   5y18d    1234     kubernetes.io/metadata.name=argocd
            beta                     Active   4y235d   1234     kubernetes.io/metadata.name=beta
            """.trimIndent()

        val df1 = DataFrame.readCsvStr(
            text = csv,
            hasFixedWidthColumns = true,
        )

        df1["NAME"].type() shouldBe typeOf<String>()
        df1["STATUS"].type() shouldBe typeOf<String>()
        df1["AGE"].type() shouldBe typeOf<String>()
        df1["NUMBER"].type() shouldBe typeOf<Int>()
        df1["LABELS"].type() shouldBe typeOf<String>()

        val df2 = DataFrame.readCsvStr(
            text = csv,
            hasFixedWidthColumns = true,
            fixedColumnWidths = listOf(25, 9, 9, 9, 100),
            skipLines = 1,
            header = listOf("name", "status", "age", "number", "labels"),
        )

        df2["name"].type() shouldBe typeOf<String>()
        df2["status"].type() shouldBe typeOf<String>()
        df2["age"].type() shouldBe typeOf<String>()
        df2["number"].type() shouldBe typeOf<Int>()
        df2["labels"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `handle default coltype with other parameters`() {
        val df = DataFrame.readCsv(
            simpleCsv,
            header = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"),
            skipLines = 2,
            colTypes = mapOf(
                "a" to ColType.Int,
                "b" to ColType.Double,
                ColType.DEFAULT to ColType.String,
            ),
        )

        df.columnTypes().shouldContainInOrder(
            typeOf<Int>(),
            typeOf<Double>(),
            typeOf<String>(),
            typeOf<String?>(),
            typeOf<String>(),
            typeOf<String?>(),
            typeOf<String?>(),
            typeOf<String?>(),
            typeOf<String>(),
            typeOf<String>(),
            typeOf<String?>(),
        )
        df.rowsCount() shouldBe 4
    }

    @Test
    fun `skipping types`() {
        val df1 = DataFrame.readCsv(
            irisDataset,
            colTypes = mapOf("sepal.length" to ColType.Double),
            parserOptions = ParserOptions(
                skipTypes = setOf(typeOf<Double>()),
            ),
        )

        df1["sepal.length"].type() shouldBe typeOf<Double>()
        df1["sepal.width"].type() shouldBe typeOf<BigDecimal>()
        df1["petal.length"].type() shouldBe typeOf<BigDecimal>()
        df1["petal.width"].type() shouldBe typeOf<BigDecimal>()
        df1["variety"].type() shouldBe typeOf<String>()

        // Also test the global setting
        DataFrame.parser.addSkipType(typeOf<Double>())

        val df2 = DataFrame.readCsv(
            irisDataset,
            colTypes = mapOf("sepal.length" to ColType.Double),
        )
        df2 shouldBe df1

        DataFrame.parser.resetToDefault()
    }

    // Issue #921
    @Test
    fun `read csv with custom null strings and given type`() {
        @Language("CSV")
        val csv =
            """
            a,b
            noppes,2
            1.2,
            3,45
            ,noppes
            1.3,1
            """.trimIndent()

        val df1 = DataFrame.readCsvStr(
            csv,
            parserOptions = ParserOptions(
                nullStrings = setOf("noppes", ""),
            ),
            colTypes = mapOf("a" to ColType.Double, "b" to ColType.Int),
        )
        df1 shouldBe dataFrameOf("a", "b")(
            null, 2,
            1.2, null,
            3.0, 45,
            null, null,
            1.3, 1,
        )

        // Also test the global setting
        DataFrame.parser.addNullString("noppes")
        DataFrame.parser.addNullString("")

        val df2 = DataFrame.readCsvStr(
            csv,
            colTypes = mapOf("a" to ColType.Double, "b" to ColType.Int),
        )

        df2 shouldBe df1

        DataFrame.parser.resetToDefault()
    }

    // Issue #1047
    @Test
    fun `Only use Deephaven datetime parser with custom csv specs`() {
        @Language("csv")
        val csvContent =
            """
            with_timezone_offset,without_timezone_offset
            2024-12-12T13:00:00+01:00,2024-12-12T13:00:00
            """.trimIndent()

        // use DFs parsers by default for datetime-like columns
        val df1 = DataFrame.readCsvStr(csvContent)
        df1["with_timezone_offset"].let {
            it.type() shouldBe typeOf<DeprecatedInstant>()
            it[0] shouldBe DeprecatedInstant.parse("2024-12-12T13:00:00+01:00")
        }
        df1["without_timezone_offset"].let {
            it.type() shouldBe typeOf<LocalDateTime>()
            it[0] shouldBe LocalDateTime.parse("2024-12-12T13:00:00")
        }

        // enable fast datetime parser for the first column with adjustCsvSpecs
        val df2 = DataFrame.readCsv(
            inputStream = csvContent.byteInputStream(),
            adjustCsvSpecs = {
                putParserForName("with_timezone_offset", Parsers.DATETIME)
            },
        )
        df2["with_timezone_offset"].let {
            it.type() shouldBe typeOf<LocalDateTime>()
            it[0] shouldBe LocalDateTime.parse("2024-12-12T12:00:00")
        }
        df2["without_timezone_offset"].let {
            it.type() shouldBe typeOf<LocalDateTime>()
            it[0] shouldBe LocalDateTime.parse("2024-12-12T13:00:00")
        }
    }

    @Test
    fun `test parsing kotlin-time-Instant`() {
        @Language("csv")
        val csvContent =
            """
            with_timezone_offset,without_timezone_offset
            2024-12-12T13:00:00+01:00,2024-12-12T13:00:00
            """.trimIndent()

        DataFrame.parser.parseExperimentalInstant = true

        // use DFs parsers by default for datetime-like columns
        val df1 = DataFrame.readCsvStr(csvContent)
        df1["with_timezone_offset"].let {
            it.type() shouldBe typeOf<StdlibInstant>()
            it[0] shouldBe StdlibInstant.parse("2024-12-12T13:00:00+01:00")
        }

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun `json dependency test`() {
        val df = dataFrameOf("firstName", "lastName")(
            "John", "Doe",
            "Jane", "Doe",
        ).group { "firstName" and "lastName" }.into { "name" }

        df.toCsvStr(quote = '\'') shouldBe
            """
            name
            '{"firstName":"John","lastName":"Doe"}'
            '{"firstName":"Jane","lastName":"Doe"}'
            
            """.trimIndent()
    }

    companion object {
        private val irisDataset = testCsv("irisDataset")
        private val simpleCsv = testCsv("testCSV")
        private val simpleCsvUtf16le = testCsv("testCSV-utf-16-le-bom")
        private val simpleCsvUtf16leGz = testResource("testCSV-utf16le-bom.csv.gz")
        private val simpleCsvUtf16leZip = testResource("testCSV-utf-16-le-bom.zip")
        private val simpleCsvZip = testResource("testCSV.zip")
        private val twoCsvsZip = testResource("two csvs.zip")
        private val simpleCsvGz = testResource("testCSV.csv.gz")
        private val csvWithFrenchLocale = testCsv("testCSVwithFrenchLocale")
        private val wineCsv = testCsv("wine")
        private val withBomCsv = testCsv("with-bom")
        private val msleepCsv = testCsv("msleep")
        private val notCsv = testResource("not-csv.zip")
    }
}

fun testResource(resourcePath: String): URL = DelimCsvTsvTests::class.java.classLoader.getResource(resourcePath)!!

fun testCsv(csvName: String) = testResource("$csvName.csv")
