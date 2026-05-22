@file:Suppress("ktlint", "UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.deephaven.csv.parsers.Parsers
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.io.StringColumns
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readCsvStr
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.testArrowFeather
import org.jetbrains.kotlinx.dataframe.testCsv
import org.jetbrains.kotlinx.dataframe.testExcel
import org.jetbrains.kotlinx.dataframe.testJson
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.net.URI
import java.util.Locale
import kotlin.reflect.typeOf

class Read : DataFrameSampleHelper("read", "api") {
    @Ignore
    @Test
    fun read() {
        // SampleStart
        DataFrame.read("input.csv")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readCsvFromFile() {
        // SampleStart
        DataFrame.readCsv("input.csv")
        // Alternatively
        DataFrame.readCsv(File("input.csv"))
        // SampleEnd
    }

    @Ignore
    @Test
    fun readCsvFromUrl() {
        // SampleStart
        DataFrame.readCsv(URI("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv").toURL())
        // SampleEnd
    }

    @Test
    fun readCsvFromString() {
        // SampleStart
        val csv = """
            A,B,C,D
            12,tuv,0.12,true
            41,xyz,3.6,not assigned
            89,abc,7.1,false
        """.trimIndent()

        DataFrame.readCsvStr(csv)
        // SampleEnd
    }

    @Test
    fun readCsvCustom() {
        val file = testCsv("syntheticSample")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            delimiter = '|',
            header = listOf("A", "B", "C", "D"),
            parserOptions = ParserOptions(nullStrings = setOf("not assigned")),
        )
        // SampleEnd
        df.rowsCount() shouldBe 3
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }

    @Test
    fun csvTypeInference() {
        val df = dataFrameOf(
            "A" to columnOf(12, 41, 89),
            "B" to columnOf("tuv", "xyz", "abc"),
            "C" to columnOf(0.12, 3.6, 7.1),
            "D" to columnOf("true", "not assigned", "false")
        ).saveDfHtmlSample()
    }

    @Test
    fun readCsvTypeInference() {
        val file = testCsv("typeInference")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            delimiter = ',',
            parserOptions = ParserOptions(nullStrings = setOf("not assigned")),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun csvWithJsonColumns() {
        val df = dataFrameOf(
            "A" to columnOf(12, 41),
            "D" to columnOf(
                """{"B":2,"C":3}""",
                """{"B":3,"C":2}"""
            )
        ).saveDfHtmlSample()
    }

    @Test
    fun readCsvWithJsonColumns() {
        val file = testCsv("jsonColumns")
        // SampleStart
        val df = DataFrame.readCsv(file)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun csvWithJsonListsColumns() {
        val df = dataFrameOf(
            "A" to columnOf(12, 41),
            "D" to columnOf(
                """[{"B":1,"C":2,"D":3},{"B":1,"C":3,"D":2}]""",
                """[{"B":2,"C":1,"D":3}]"""
            )
        ).saveDfHtmlSample()
    }

    @Test
    fun readCsvWithJsonListsColumns() {
        val file = testCsv("jsonListsColumns")
        // SampleStart
        val df = DataFrame.readCsv(file)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun csvLocale() {
        val df = dataFrameOf("numbers" to columnOf("12,123", "41,111"))
            .saveDfHtmlSample()
    }

    @Test
    fun readJson() {
        val file = testJson("synthetic")
        // SampleStart
        val df = DataFrame.readJson(file)
        // SampleEnd
        df.rowsCount() shouldBe 4
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<String>()
        df["B"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }

    @Test
    fun readJsonRow() {
        val file = testJson("syntheticObj")
        // SampleStart
        val row = DataRow.readJson(file)
        // SampleEnd
        row.columnNames() shouldBe listOf("A", "B", "C", "D")
        row.columnTypes() shouldBe listOf(typeOf<String>(), typeOf<Int>(), typeOf<Float>(), typeOf<Boolean>())
    }

    @Ignore
    @Test
    fun readJsonFromUrl() {
        // SampleStart
        DataFrame.readJson("https://raw.githubusercontent.com/Kotlin/dataframe/refs/heads/master/data/participants.json")
        // SampleEnd
    }

    @Test
    fun readJsonFromString() {
        // SampleStart
        val text = """
            [
                {
                    "A": "1",
                    "B": 1,
                    "C": 1.0,
                    "D": true
                },
                {
                    "A": "2",
                    "B": 2,
                    "C": 1.1,
                    "D": null
                },
                {
                    "A": "3",
                    "B": 3,
                    "C": 1,
                    "D": false
                },
                {
                    "A": "4",
                    "B": 4,
                    "C": 1.3,
                    "D": true
                }
            ]
        """.trimIndent()

        val df = DataFrame.readJsonStr(text)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readJsonFromStringWithoutKeyValuePairs() {
        val pets = """
        {
            "dogs": {
                "fido": {
                    "age": 3,
                    "breed": "poodle"
                },
                "spot": {
                    "age": 5,
                    "breed": "labrador"
                },
                "rex": {
                    "age": 2,
                    "breed": "golden retriever"
                },
                "lucky": {
                    "age": 1,
                    "breed": "poodle"
                },
                "rover": {
                    "age": 4,
                    "breed": "beagle"
                },
                "max": {
                    "age": 6,
                    "breed": "german shepherd"
                },
                "buster": {
                    "age": 2,
                    "breed": "bulldog"
                }
            },
            "cats": {
                "whiskers": {
                    "age": 2,
                    "breed": "siamese"
                },
                "mittens": {
                    "age": 4,
                    "breed": "maine coon"
                },
                "shadow": {
                    "age": 3,
                    "breed": "british shorthair"
                },
                "luna": {
                    "age": 1,
                    "breed": "ragdoll"
                },
                "simba": {
                    "age": 5,
                    "breed": "bengal"
                },
                "cleo": {
                    "age": 2,
                    "breed": "persian"
                }
            }
        }
        """.trimIndent()
        // SampleStart
        val df = DataFrame.readJsonStr(pets)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readJsonFromStringWithKeyValuePairs() {
        val pets = """
        {
            "dogs": {
                "fido": {
                    "age": 3,
                    "breed": "poodle"
                },
                "spot": {
                    "age": 5,
                    "breed": "labrador"
                },
                "rex": {
                    "age": 2,
                    "breed": "golden retriever"
                },
                "lucky": {
                    "age": 1,
                    "breed": "poodle"
                },
                "rover": {
                    "age": 4,
                    "breed": "beagle"
                },
                "max": {
                    "age": 6,
                    "breed": "german shepherd"
                },
                "buster": {
                    "age": 2,
                    "breed": "bulldog"
                }
            },
            "cats": {
                "whiskers": {
                    "age": 2,
                    "breed": "siamese"
                },
                "mittens": {
                    "age": 4,
                    "breed": "maine coon"
                },
                "shadow": {
                    "age": 3,
                    "breed": "british shorthair"
                },
                "luna": {
                    "age": 1,
                    "breed": "ragdoll"
                },
                "simba": {
                    "age": 5,
                    "breed": "bengal"
                },
                "cleo": {
                    "age": 2,
                    "breed": "persian"
                }
            }
        }
        """.trimIndent()
        // SampleStart
        DataFrame.readJsonStr(
            text = pets,
            keyValuePaths = listOf(
                JsonPath().append("dogs"), // which will result in '$["dogs"]'
                JsonPath().append("cats"), // which will result in '$["cats"]'
            ),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    private val json = """
        [{ // some comment
           a: 123,
           b: hello,
         },
         { // some other comment
           a: 456,
           b: world,
         }]""".trimIndent()

    @Test
    fun readJsonWithJsonInstance() {
        // SampleStart
        DataFrame.readJson(
            stream = json.byteInputStream(),
            format = Json {
                isLenient = true
                allowTrailingComma = true
                allowComments = true
            },
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readJsonTypeClash() {
        // SampleStart
        val text = """
            [
                { "a": "text" },
                { "a": { "b": 2 } },
                { "a": [ 6, 7, 8 ] }
            ]
        """.trimIndent()

        val df = DataFrame.readJsonStr(text)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readJsonTypeClashTactic() {
        val text = """
            [
                { "a": "text" },
                { "a": { "b": 2 } },
                { "a": [ 6, 7, 8 ] }
            ]
        """.trimIndent()
        // SampleStart
        val df = DataFrame.readJsonStr(text, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readExcelFromFile() {
        val file = testExcel("example")
        // SampleStart
        val df = DataFrame.readExcel(file)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readExcelFromUrl() {
        // SampleStart
        DataFrame.readExcel("https://example.com/data.xlsx")
        // SampleEnd
    }

    @Test
    @Ignore
    fun fixMixedColumn() {
        // SampleStart
        val df = DataFrame.readExcel("mixed_column.xlsx", stringColumns = StringColumns("A"))
        // SampleEnd
    }

    @Test
    fun readArrowFeather() {
        val file = testArrowFeather("data-arrow_2.0.0_uncompressed")
        // SampleStart
        val df = DataFrame.readArrowFeather(file)
        // SampleEnd
        df.rowsCount() shouldBe 1
        df.columnsCount() shouldBe 4
    }

    @Test
    fun readNumbersWithSpecificLocale() {
        val file = testCsv("numbers")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(locale = Locale.UK),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readNumbersWithColType() {
        val file = testCsv("numbers")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            colTypes = mapOf("numbers" to ColType.String),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dateTimeSample() {
        val df = dataFrameOf("date" to columnOf("13/Jan/23 11:49 AM", "14/Mar/23 5:35 PM"))
            .saveDfHtmlSample()
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun readDatesWithSpecificDateTimePattern() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(
                dateTime = DateTimeParserOptions.Java.withPattern("dd/MMM/yy h:mm a"),
            ),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readDatesWithSpecificDateTimeFormatter() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            parserOptions = ParserOptions(
                dateTime = DateTimeParserOptions.Kotlin.withFormat(
                    LocalDate.Format {
                        monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
                    },
                ),
            ),
        )
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readDatesWithDefaultType() {
        val file = testCsv("dates")
        // SampleStart
        val df = DataFrame.readCsv(
            file,
            colTypes = mapOf(ColType.DEFAULT to ColType.String),
        )
        // SampleEnd
    }

    @Test
    fun readDatesWithDeephavenDateTimeParser() {
        val file = testCsv("dates")
        try {
            // SampleStart
            val df = DataFrame.readCsv(
                inputStream = file.openStream(),
                adjustCsvSpecs = { // it: CsvSpecs.Builder
                    it.putParserForName("date", Parsers.DATETIME)
                },
            )
            // SampleEnd
        } catch (_: Exception) {
        }
    }
}
