package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toStr
import org.junit.Test
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.util.Locale
import java.util.zip.GZIPInputStream
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@OptIn(ExperimentalCsv::class)
@Suppress("ktlint:standard:argument-list-wrapping")
class DelimCsvTsvTests {

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
    fun `read ZIP Csv`() {
        val df = DataFrame.readCsv(simpleCsvZip)

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
    fun `read GZ Csv`() {
        val df = DataFrame.readCsv(simpleCsvGz)

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
    fun `read custom compression Csv`() {
        val df = DataFrame.readCsv(
            simpleCsvGz,
            compression = Compression.Custom { GZIPInputStream(it) },
        )

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

    @Test
    fun readCsvWithFloats() {
        val df = DataFrame.readCsv(wineCsv, delimiter = ';')
        val schema = df.schema()

        fun assertColumnType(columnName: String, kClass: KClass<*>) {
            val col = schema.columns[columnName]
            col.shouldNotBeNull()
            col.type.classifier shouldBe kClass
        }

        assertColumnType("citric acid", Double::class)
        assertColumnType("alcohol", Double::class)
        assertColumnType("quality", Int::class)
    }

    @Test
    fun `read standard CSV with floats when user has alternative locale`() {
        val currentLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("ru-RU"))
            val df = DataFrame.readCsv(wineCsv, delimiter = ';')
            val schema = df.schema()

            fun assertColumnType(columnName: String, kClass: KClass<*>) {
                val col = schema.columns[columnName]
                col.shouldNotBeNull()
                col.type.classifier shouldBe kClass
            }

            assertColumnType("citric acid", Double::class)
            assertColumnType("alcohol", Double::class)
            assertColumnType("quality", Int::class)
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
        val df = DataFrame.readCsv(durationCsv)
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
        val df = DataFrame.readCsv(
            "../data/jetbrains_repositories.csv",
            skipLines = 1, // now needs this, ignoreEmptyLines cannot catch it
        )
        df.columnNames() shouldBe listOf("full_name", "html_url", "stargazers_count", "topics", "watchers")
        df.columnTypes() shouldBe listOf(
            typeOf<String>(),
            typeOf<URL>(),
            typeOf<Int>(),
            typeOf<String>(),
            typeOf<Int>(),
        )
        df shouldBe DataFrame.readCsv(
            "../data/jetbrains repositories.csv",
            skipLines = 1, // now needs this, ignoreEmptyLines cannot catch it
        )
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

        // while negative numbers in RTL languages cannot be parsed, thanks to Java, others work
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

        val estonianDf = DataFrame.readCsvStr(
            text = estonianWrongMinus,
            delimiter = ';',
            parserOptions = ParserOptions(
                locale = Locale.forLanguageTag("et-EE"),
            ),
        )

        estonianDf["price"].type() shouldBe typeOf<Double?>()
    }

    @Test
    fun `NA and custom null string in double column`() {
        val df = DataFrame.readCsv(
            msleepCsv,
            parserOptions = ParserOptions(
                nullStrings = DEFAULT_NULL_STRINGS + "nothing",
            ),
        )

        df.print(columnTypes = true, borders = true, title = true)

        df["name"].type() shouldBe typeOf<String>()
        df["genus"].type() shouldBe typeOf<String>()
        df["vore"].type() shouldBe typeOf<String?>()
        df["order"].type() shouldBe typeOf<String>()
        df["conservation"].type() shouldBe typeOf<String?>()
        df["sleep_total"].type() shouldBe typeOf<Double>()
        df["sleep_rem"].type() shouldBe typeOf<Double?>()
        df["sleep_cycle"].type() shouldBe typeOf<Double?>()
        df["awake"].type() shouldBe typeOf<Double>()
        df["brainwt"].type() shouldBe typeOf<Double?>()
        df["bodywt"].type() shouldBe typeOf<Double?>()
    }

    companion object {
        private val simpleCsv = testCsv("testCSV")
        private val simpleCsvZip = testResource("testCSV.zip")
        private val twoCsvsZip = testResource("two csvs.zip")
        private val simpleCsvGz = testResource("testCSV.csv.gz")
        private val csvWithFrenchLocale = testCsv("testCSVwithFrenchLocale")
        private val wineCsv = testCsv("wine")
        private val durationCsv = testCsv("duration")
        private val withBomCsv = testCsv("with-bom")
        private val msleepCsv = testCsv("msleep")
    }
}

fun testResource(resourcePath: String): URL = DelimCsvTsvTests::class.java.classLoader.getResource(resourcePath)!!

fun testCsv(csvName: String) = testResource("$csvName.csv")
