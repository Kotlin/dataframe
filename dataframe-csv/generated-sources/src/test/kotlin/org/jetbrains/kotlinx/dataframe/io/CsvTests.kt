package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.allNulls
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toStr
import org.junit.Test
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class CsvTests {

    @Test
    fun readNulls() {
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
        df["duplicate1"].type() shouldBe typeOf<String?>()
        df["double"].type() shouldBe typeOf<Double?>()
        df["time"].type() shouldBe typeOf<LocalDateTime>()

        println(df)
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
        df["duplicate1"].type() shouldBe typeOf<String?>()
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
        val str = grouped.toCsvStr()
        val res = DataFrame.readCsvStr(str)
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
            "src/test/resources/without_header.csv",
//            CSVFormat.DEFAULT.builder()
//                .setSkipHeaderRecord(true)
//                .build(),
        )
        val producedFile = File("src/test/resources/without_header.csv")
        producedFile.exists() shouldBe true
        producedFile.readText() shouldBe "1,2,3\r\n1,3,2\r\n"
        producedFile.delete()
    }

    @Test
    fun `check integrity of example data`() {
        val df = DataFrame.readCsv("../data/jetbrains_repositories.csv")
        df.columnNames() shouldBe listOf("full_name", "html_url", "stargazers_count", "topics", "watchers")
        df.columnTypes() shouldBe
            listOf(typeOf<String>(), typeOf<URL>(), typeOf<Int>(), typeOf<String>(), typeOf<Int>())
        df shouldBe DataFrame.readCsv("../data/jetbrains repositories.csv")
    }

    @Test
    fun `readCsvStr delimiter`() {
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

        val emptyTsvStr = DataFrame.readTSV(File.createTempFile("empty", "tsv"))
        emptyTsvStr shouldBe DataFrame.empty()
    }

    companion object {
        private val simpleCsv = testCsv("testCSV")
        private val csvWithFrenchLocale = testCsv("testCSVwithFrenchLocale")
        private val wineCsv = testCsv("wine")
        private val durationCsv = testCsv("duration")
        private val withBomCsv = testCsv("with-bom")
    }
}

fun testResource(resourcePath: String): URL = CsvTests::class.java.classLoader.getResource(resourcePath)!!

fun testCsv(csvName: String) = testResource("$csvName.csv")
