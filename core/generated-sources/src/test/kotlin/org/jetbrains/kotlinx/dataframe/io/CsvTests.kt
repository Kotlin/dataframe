package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.apache.commons.csv.CSVFormat
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
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.testCsv
import org.jetbrains.kotlinx.dataframe.testResource
import org.jetbrains.kotlinx.dataframe.util.DOUBLE
import org.jetbrains.kotlinx.dataframe.util.INT
import org.jetbrains.kotlinx.dataframe.util.LOCAL_DATE_TIME
import org.jetbrains.kotlinx.dataframe.util.NULLABLE_DOUBLE
import org.jetbrains.kotlinx.dataframe.util.NULLABLE_STRING
import org.jetbrains.kotlinx.dataframe.util.STRING
import org.jetbrains.kotlinx.dataframe.util.URL
import org.junit.Test
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.util.Locale
import kotlin.reflect.KClass

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
        val df = DataFrame.readDelimStr(src)
        df.nrow shouldBe 2
        df.ncol shouldBe 2
        df["first"].type() shouldBe INT
        df["second"].allNulls() shouldBe true
        df["second"].type() shouldBe NULLABLE_STRING
    }

    @Test
    fun write() {
        val df = dataFrameOf("col1", "col2")(
            1, null,
            2, null,
        ).convert("col2").toStr()

        val str = StringWriter()
        df.writeCSV(str)

        val res = DataFrame.readDelimStr(str.buffer.toString())

        res shouldBe df
    }

    @Test
    fun readCSV() {
        val df = DataFrame.read(simpleCsv)

        df.ncol shouldBe 11
        df.nrow shouldBe 5
        df.columnNames()[5] shouldBe "duplicate1"
        df.columnNames()[6] shouldBe "duplicate11"
        df["duplicate1"].type() shouldBe NULLABLE_STRING
        df["double"].type() shouldBe NULLABLE_DOUBLE
        df["time"].type() shouldBe LOCAL_DATE_TIME

        println(df)
    }

    @Test
    fun readCsvWithFrenchLocaleAndAlternativeDelimiter() {
        val df = DataFrame.readCSV(
            url = csvWithFrenchLocale,
            delimiter = ';',
            parserOptions = ParserOptions(locale = Locale.FRENCH),
        )

        df.ncol shouldBe 11
        df.nrow shouldBe 5
        df.columnNames()[5] shouldBe "duplicate1"
        df.columnNames()[6] shouldBe "duplicate11"
        df["duplicate1"].type() shouldBe NULLABLE_STRING
        df["double"].type() shouldBe NULLABLE_DOUBLE
        df["number"].type() shouldBe DOUBLE
        df["time"].type() shouldBe LOCAL_DATE_TIME

        println(df)
    }

    @Test
    fun readCsvWithFloats() {
        val df = DataFrame.readCSV(wineCsv, delimiter = ';')
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
            val df = DataFrame.readCSV(wineCsv, delimiter = ';')
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
        val df = DataFrame.readCSV(simpleCsv, header = header, skipLines = 1)
        df.columnNames() shouldBe header
        df["B"].type() shouldBe INT

        val headerShort = ('A'..'E').map { it.toString() }
        val dfShort = DataFrame.readCSV(simpleCsv, header = headerShort, skipLines = 1)
        dfShort.ncol shouldBe 5
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
        val dfHeader = DataFrame.readCSV(simpleCsv, readLines = 0)
        dfHeader.nrow shouldBe 0
        dfHeader.columnNames() shouldBe expected

        val dfThree = DataFrame.readCSV(simpleCsv, readLines = 3)
        dfThree.nrow shouldBe 3

        val dfFull = DataFrame.readCSV(simpleCsv, readLines = 10)
        dfFull.nrow shouldBe 5
    }

    @Test
    fun `if string starts with a number, it should be parsed as a string anyway`() {
        val df = DataFrame.readCSV(durationCsv)
        df["duration"].type() shouldBe STRING
        df["floatDuration"].type() shouldBe STRING
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
            DataFrame.readDelimStr(csvContent)
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
        val str = grouped.toCsv()
        val res = DataFrame.readDelimStr(str)
        res shouldBe grouped
    }

    @Test
    fun `write and read column group`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 3, 2,
        )
        val grouped = df.group("b", "c").into("d")
        val str = grouped.toCsv()
        val res = DataFrame.readDelimStr(str)
        res shouldBe grouped
    }

    @Test
    fun `CSV String of saved dataframe starts with column name`() {
        val df = dataFrameOf("a")(1)
        df.toCsv().first() shouldBe 'a'
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
        df.writeCSV(
            "src/test/resources/without_header.csv",
            CSVFormat.DEFAULT.builder()
                .setSkipHeaderRecord(true)
                .build(),
        )
        val producedFile = File("src/test/resources/without_header.csv")
        producedFile.exists() shouldBe true
        producedFile.readText() shouldBe "1,2,3\r\n1,3,2\r\n"
        producedFile.delete()
    }

    @Test
    fun `check integrity of example data`() {
        val df = DataFrame.readCSV("../data/jetbrains_repositories.csv")
        df.columnNames() shouldBe listOf("full_name", "html_url", "stargazers_count", "topics", "watchers")
        df.columnTypes() shouldBe listOf(STRING, URL, INT, STRING, INT)
        df shouldBe DataFrame.readCSV("../data/jetbrains repositories.csv")
    }

    @Test
    fun `readDelimStr delimiter`() {
        val tsv =
            """
            a	b	c
            1	2	3
            """.trimIndent()
        val df = DataFrame.readDelimStr(tsv, '\t')
        df shouldBe dataFrameOf("a", "b", "c")(1, 2, 3)
    }

    @Test
    fun `file with BOM`() {
        val df = DataFrame.readCSV(withBomCsv, delimiter = ';')
        df.columnNames() shouldBe listOf("Column1", "Column2")
    }

    @Test
    fun `read empty delimStr or CSV`() {
        val emptyDelimStr = DataFrame.readDelimStr("")
        emptyDelimStr shouldBe DataFrame.empty()

        val emptyDelimFile = DataFrame.readDelim(File.createTempFile("empty", "csv").reader())
        emptyDelimFile shouldBe DataFrame.empty()

        val emptyCsvFile = DataFrame.readCSV(File.createTempFile("empty", "csv"))
        emptyCsvFile shouldBe DataFrame.empty()

        val emptyCsvFileManualHeader = DataFrame.readCSV(
            file = File.createTempFile("empty", "csv"),
            header = listOf("a", "b", "c"),
        )
        emptyCsvFileManualHeader.apply {
            isEmpty() shouldBe true
            columnNames() shouldBe listOf("a", "b", "c")
            columnTypes() shouldBe listOf(STRING, STRING, STRING)
        }

        val emptyCsvFileWithHeader = DataFrame.readCSV(
            file = File.createTempFile("empty", "csv").also { it.writeText("a,b,c") },
        )
        emptyCsvFileWithHeader.apply {
            isEmpty() shouldBe true
            columnNames() shouldBe listOf("a", "b", "c")
            columnTypes() shouldBe listOf(STRING, STRING, STRING)
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
