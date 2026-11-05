package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.addDateTimeFormat
import org.jetbrains.kotlinx.dataframe.api.addDateTimeUnicodePattern
import org.jetbrains.kotlinx.dataframe.api.addJavaDateTimeFormatter
import org.jetbrains.kotlinx.dataframe.api.addJavaDateTimePattern
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.renderType
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale
import kotlin.reflect.typeOf

class Parse : DataFrameSampleHelper(subFolder = "api", sampleName = "parse") {

    private fun getDf() =
        dataFrameOf(
            "date" to columnOf("2025-11-22", "2025-11-23", "2025-11-24"),
            "value" to columnOf("1.0", "200,000.0", "1.123"),
            "code" to columnOf('1', '2', '3'),
            "info" to columnOf(
                "city" to columnOf("Moscow", "Berlin", "Paris"),
                "age" to columnOf("30", "25", "41"),
            ),
            "history" to columnOf(
                dataFrameOf("visit")("2024-05-01"),
                dataFrameOf("visit")("2024-06-15"),
                dataFrameOf("visit")("2024-07-30"),
            ),
        )

    private fun AnyFrame.withTypes() =
        this
            .rename { colsAtAnyDepth() }.to { "${it.name()}: ${renderType(it.type())}" }

    @Test
    fun dfParse() {
        getDf()
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parseAll() {
        val df = getDf()
        // SampleStart
        df.parse()
            // SampleEnd
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parseSome() {
        val df = getDf()
        // SampleStart
        df.parse { date and value }
            // SampleEnd
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parseSomeByName() {
        val df = getDf()
        // SampleStart
        df.parse("date", "value")
            // SampleEnd
            .also {
                it["date"].type() shouldBe typeOf<LocalDate>()
                it["value"].type() shouldBe typeOf<Double>()
            }
    }

    @Test
    fun parseFrameColumn() {
        val frames = DataColumn.createValueColumn(
            "frames",
            listOf(dataFrameOf("value")("1.0"), null, dataFrameOf("value")("2.0")),
        )
        // SampleStart
        frames.parse()
            // SampleEnd
            .also {
                (it[0] as DataFrame<*>)["value"].type() shouldBe typeOf<Double>()
                (it[0] as DataFrame<*>)["value"].toList() shouldBe listOf(1.0)
                it[1] shouldBe null
                (it[2] as DataFrame<*>)["value"].toList() shouldBe listOf(2.0)
            }
            .saveDfHtmlSample()
    }

    private fun getDfParseWithOptions() =
        dataFrameOf(
            "date" to columnOf("2012-W48-6", "2012-W49-1", "2012-W50-1"),
            "value" to columnOf("1,0", "200.000,0", "1,123"),
        )

    @Test
    fun dfParseWithOptions() {
        getDfParseWithOptions()
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parseWithOptions() {
        val df = getDfParseWithOptions()
        // SampleStart
        df.parse(
            options = ParserOptions(
                locale = Locale.GERMAN,
                dateTime = DateTimeParserOptions.Java
                    .withFormatter<java.time.LocalDate>(formatter = DateTimeFormatter.ISO_WEEK_DATE),
            ),
        )
            // SampleEnd
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun globalParserOptions() {
        // SampleStart
        DataFrame.parser.locale = Locale.FRANCE
        DataFrame.parser.addJavaDateTimePattern("dd.MM.uuuu HH:mm:ss")
        // SampleEnd
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun globalParserOptionsConvertCombination() {
        val stringCol by columnOf("550e8400-e29b-41d4-a716-446655440000")
        // SampleStart
        DataFrame.parser.parseExperimentalUuid = false
        stringCol.convertTo<kotlin.uuid.Uuid>() // will still parse to `kotlin.uuid.Uuid`, as expected
        // SampleEnd
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun resetGlobalParserOptions() {
        // SampleStart
        DataFrame.parser.resetToDefault()
        // SampleEnd
    }

    @Test
    fun globalParserOptionsAddDateTimeFormatKotlin() {
        // SampleStart
        // Adding a custom DateTimeFormat using the kotlinx-datetime Format-DSL
        val format = LocalDate.Format {
            monthNumber(padding = Padding.SPACE)
            char('/')
            day()
            char(' ')
            year()
        }
        DataFrame.parser.addDateTimeFormat(format)

        // now this will succeed!
        columnOf("12/24 2023").parse()
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun globalParserOptionsAddDateTimeFormatJava() {
        // SampleStart
        val formatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter()

        // Adding a custom DateTimeFormatter type-safely for LocalDate only
        DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDate>(formatter)

        // or, adding it for all java-types: Local(Date)(Time), and Instant
        DataFrame.parser.addJavaDateTimeFormatter(formatter)

        // setting the locale to US
        DataFrame.parser.locale = Locale.US

        // now this will succeed!
        columnOf("12/24 2023").parse()
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun globalParserOptionsAddPatternKotlin() {
        // SampleStart
        // Adding a custom DateTimeFormat using the kotlinx-datetime Unicode pattern
        // This requires explicitly opting in and providing a type
        @OptIn(FormatStringsInDatetimeFormats::class)
        DataFrame.parser.addDateTimeUnicodePattern<LocalDate>("MM/dd yyyy")

        // now this will succeed!
        columnOf("12/24 2023").parse()
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun globalParserOptionsAddPatternJava() {
        // SampleStart
        // Adding a custom DateTimeFormatter by pattern type-safely for LocalDate only
        DataFrame.parser.addJavaDateTimePattern<java.time.LocalDate>("MM/dd yyyy")

        // or, adding it for all java-types: Local(Date)(Time), and Instant
        DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")

        // setting the locale to US
        DataFrame.parser.locale = Locale.US

        // now this will succeed!
        columnOf("12/24 2023").parse()
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun parserOptionsWithDateTimeFormatKotlin() {
        // SampleStart
        val format = LocalDate.Format {
            monthNumber(padding = Padding.SPACE)
            char('/')
            day()
            char(' ')
            year()
        }

        // now this will succeed!
        columnOf("12/24 2023")
            .parse(
                options = ParserOptions(
                    dateTime = DateTimeParserOptions.Kotlin
                        .withFormat(format),
                ),
            )
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parserOptionsWithDateTimeFormatJava() {
        // SampleStart
        val formatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter()

        // Adding a custom DateTimeFormatter type-safely for LocalDate only
        DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDate>(formatter)

        // or, adding it for all java-types: Local(Date)(Time), and Instant
        DataFrame.parser.addJavaDateTimeFormatter(formatter)

        // now this will succeed!
        columnOf("12/24 2023").parse(
            options = ParserOptions(
                dateTime = DateTimeParserOptions.Java
                    // Supplying a custom DateTimeFormatter type-safely for LocalDate only
                    .withFormatter<java.time.LocalDate>(formatter)
                    // or, supplying it for all java-types: Local(Date)(Time), and Instant
                    .withFormatter(formatter)
                    // setting the locale to US
                    .withLocale(Locale.US),
            ),
        )
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parserOptionsWithPatternKotlin() {
        // SampleStart
        // Now this will succeed!
        // This requires explicitly opting in and providing a type
        @OptIn(FormatStringsInDatetimeFormats::class)
        columnOf("12/24 2023")
            .parse(
                options = ParserOptions(
                    dateTime = DateTimeParserOptions.Kotlin
                        .withPattern<LocalDate>("MM/dd yyyy"),
                ),
            )
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
    }

    @Test
    fun parserOptionsWithPatternJava() {
        // SampleStart
        // Now this will succeed!
        columnOf("12/24 2023")
            .parse(
                options = ParserOptions(
                    dateTime = DateTimeParserOptions.Java
                        // Supplying a custom pattern type-safely for LocalDate only
                        .withPattern<java.time.LocalDate>("MM/dd yyyy")
                        // or, supplying it for all java-types: Local(Date)(Time), and Instant
                        .withPattern("MM/dd yyyy")
                        // setting the locale to US
                        .withLocale(Locale.US),
                ),
            )
            // SampleEnd
            .named("date")
            .toDataFrame()
            .withTypes()
            .saveDfHtmlSample()
    }
}
