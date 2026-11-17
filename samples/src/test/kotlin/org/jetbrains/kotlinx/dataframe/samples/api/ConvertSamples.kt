package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.asFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.convertToDouble
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrames
import org.jetbrains.kotlinx.dataframe.api.toDateTimeComponents
import org.jetbrains.kotlinx.dataframe.api.toDuration
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toIFrame
import org.jetbrains.kotlinx.dataframe.api.toImg
import org.jetbrains.kotlinx.dataframe.api.toJavaDuration
import org.jetbrains.kotlinx.dataframe.api.toJavaLocalDate
import org.jetbrains.kotlinx.dataframe.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.api.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.api.toUtcOffset
import org.jetbrains.kotlinx.dataframe.api.toYearMonth
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.net.URL
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.stream.Collectors
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Samples for the `convert` website page.
 */
class ConvertSamples : DataFrameSampleHelper("convert", "api") {

    // the first seven people, so that the page, the KDoc and the tests show one dataset
    val df = peopleDf.take(7)

    @DataSchema
    interface StringDates {
        val date: String
        val value: String
    }

    val stringDf = dataFrameOf(
        "date" to columnOf("2025-11-22", "2025-11-23", "2025-11-24"),
        "value" to columnOf("1,0", "200.000,0", "-"),
    ).cast<StringDates>()

    @DataSchema
    interface Timestamps {
        val timestamp: String?
    }

    val timestampsDf = dataFrameOf("timestamp")(
        columnOf("2026-05-05T16:42:00Z", "2026-11-05T09:30:00Z", null),
    ).cast<Timestamps>()

    @DataSchema
    interface Dates {
        val date: String
        val dateTime: String
        val millis: Long
    }

    val datesDf = dataFrameOf(
        "date" to columnOf("22.11.2025", "05.12.2025"),
        "dateTime" to columnOf("2025-11-22 11:30", "2025-12-05 09:15"),
        "millis" to columnOf(0L, 86400000L),
    ).cast<Dates>()

    @DataSchema
    interface Parsed {
        val components: DateTimeComponents
    }

    val componentsDf = dataFrameOf("components")(
        columnOf(
            DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00+03:00"),
            DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-11-22T09:30:00-05:00"),
        ),
    ).cast<Parsed>()

    @DataSchema
    interface Durations {
        val duration: String?
    }

    val durationsDf = dataFrameOf("duration")(
        columnOf("PT1H30M", "PT45S", null),
    ).cast<Durations>()

    @DataSchema
    interface WithUserData {
        val userData: List<List<Any>>
    }

    @DataSchema
    interface Media {
        val logo: URL
        val page: URL?
    }

    val mediaDf = dataFrameOf(
        "logo" to columnOf(
            URL("https://kotlin.github.io/dataframe/images/kotlin-dataframe-logo.svg"),
            URL("https://kotlin.github.io/dataframe/images/kotlin-dataframe-logo.svg"),
        ),
        "page" to columnOf(URL("https://kotlin.github.io/dataframe/"), null),
    ).cast<Media>()

    @Test
    fun convertDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun datesDfInput() {
        // SampleStart
        datesDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun stringDfInput() {
        // SampleStart
        stringDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun timestampsDfInput() {
        // SampleStart
        timestampsDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun durationsDfInput() {
        // SampleStart
        durationsDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mediaDfInput() {
        // SampleStart
        mediaDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun convert_properties() {
        // SampleStart
        df.convert { age }.with { it.toDouble() }
            // SampleEnd
            .also { it.age.toList() shouldBe listOf(15.0, 45.0, 20.0, 40.0, 30.0, 20.0, 30.0) }
            .saveDfHtmlSample()
    }

    @Test
    fun convert_strings() {
        // SampleStart
        df.convert { "age"<Int>() }.with { it.toDouble() }
            // SampleEnd
            .also { it["age"].values() shouldBe listOf(15.0, 45.0, 20.0, 40.0, 30.0, 20.0, 30.0) }
    }

    @Test
    fun convertRunningTotal() {
        // SampleStart
        df.convert { age }.with {
            (prev()?.newValue<Int>() ?: 0) + it
        }
            // SampleEnd
            .also { it.age.toList() shouldBe listOf(15, 60, 80, 120, 150, 170, 200) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertChars_properties() {
        // SampleStart
        df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
            // SampleEnd
            .also {
                it.getColumnGroup("name")["firstName"].values().first() shouldBe listOf('A', 'l', 'i', 'c', 'e')
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertChars_strings() {
        // SampleStart
        df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
            // SampleEnd
            .also {
                it.getColumnGroup("name")["firstName"].values().first() shouldBe listOf('A', 'l', 'i', 'c', 'e')
            }
    }

    @Test
    fun convertColumnTo() {
        // SampleStart
        df.weight.convertTo<Float?>()
            // SampleEnd
            .also { it.values() shouldBe listOf(54.0f, 87.0f, null, null, 68.0f, 55.0f, 90.0f) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertColumnToDouble() {
        // SampleStart
        df.age.convertToDouble()
            // SampleEnd
            .also { it.values() shouldBe listOf(15.0, 45.0, 20.0, 40.0, 30.0, 20.0, 30.0) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertNotNull() {
        // SampleStart
        // `weight` contains nulls; `notNull` applies the expression only to non-null values
        df.convert { weight }.notNull { it / 10 }
            // SampleEnd
            .also { it.weight.toList() shouldBe listOf(5, 8, null, null, 6, 5, 9) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertPerRowCol() {
        // SampleStart
        // appends the column name to every value of the selected columns
        df.convert { age and weight }.perRowCol { row, col ->
            col[row.index()].toString() + col.name()
        }
            // SampleEnd
            .also {
                it.age.toList() shouldBe listOf("15age", "45age", "20age", "40age", "30age", "20age", "30age")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertAsFrame_properties() {
        // SampleStart
        df.convert { name }.asFrame { it.asDataFrame().add("fullName") { "$firstName $lastName" } }
            // SampleEnd
            .also { it.getColumnGroup("name")["fullName"].values().first() shouldBe "Alice Cooper" }
            .saveDfHtmlSample()
    }

    @Test
    fun convertAsFrame_strings() {
        // SampleStart
        df.convert { colGroup("name") }.asFrame {
            it.asDataFrame().add("fullName") {
                getValue<String>("firstName") + " " + getValue<String>("lastName")
            }
        }
            // SampleEnd
            .also { it.getColumnGroup("name")["fullName"].values().first() shouldBe "Alice Cooper" }
    }

    @Test
    fun convertAsColumn() {
        // SampleStart
        df.convert { name }.asColumn { col ->
            col.toList()
                .parallelStream().map { it.toString() }.collect(Collectors.toList())
                .toColumn()
        }
            // SampleEnd
            .also { it["name"].values().first() shouldBe "{ firstName:Alice, lastName:Cooper }" }
            .saveDfHtmlSample()
    }

    @Test
    fun convertCast() {
        // SampleStart
        // `numbers` is an `AnyFrame`: the compiler doesn't know the column type,
        // so `cast` adjusts the static type before the conversion
        val numbers: AnyFrame = dataFrameOf("value")(1, 2, 3)
        numbers.convert("value").cast<Int>().with { it * 2 }
            // SampleEnd
            .also { it["value"].values() shouldBe listOf(2, 4, 6) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertTo() {
        // SampleStart
        df.convert { age }.to<Double>()
            // SampleEnd
            .also { it.age.toList() shouldBe listOf(15.0, 45.0, 20.0, 40.0, 30.0, 20.0, 30.0) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertColsToString() {
        // SampleStart
        df.convert { colsOf<Number>() }.to<String>()
            // SampleEnd
            .also { it["age"].values() shouldBe listOf("15", "45", "20", "40", "30", "20", "30") }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToFloat() {
        // SampleStart
        df.convert { weight }.toFloat()
            // SampleEnd
            .also { it["weight"].values() shouldBe listOf(54.0f, 87.0f, null, null, 68.0f, 55.0f, 90.0f) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertStringToLocalDate() {
        // SampleStart
        // String -> LocalDate conversion
        stringDf.convert { date }.to<LocalDate>(
            parserOptions = ParserOptions(
                dateTime = DateTimeParserOptions.Kotlin.withFormat(LocalDate.Formats.ISO),
            ),
        )
            // SampleEnd
            .also {
                it.date.toList() shouldBe listOf(
                    LocalDate(2025, 11, 22),
                    LocalDate(2025, 11, 23),
                    LocalDate(2025, 11, 24),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertStringToLocalDateShortcut() {
        // SampleStart
        // shortcut for String -> LocalDate conversion
        stringDf.convert { date }.toLocalDate(LocalDate.Formats.ISO)
            // SampleEnd
            .also {
                it["date"].values() shouldBe listOf(
                    LocalDate(2025, 11, 22),
                    LocalDate(2025, 11, 23),
                    LocalDate(2025, 11, 24),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertStringTo() {
        // SampleStart
        // String -> Double? conversion
        stringDf.convert { value }.to<Double?>(
            parserOptions = ParserOptions(locale = Locale.GERMAN, nullStrings = setOf("-")),
        )
            // SampleEnd
            .also { it.value.toList() shouldBe listOf(1.0, 200000.0, null) }
            .saveDfHtmlSample()
    }

    enum class Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST,
    }

    @Test
    fun convertToEnum() {
        // SampleStart
        dataFrameOf("direction")("NORTH", "WEST")
            .convert("direction").to<Direction>()
            // SampleEnd
            .also { it["direction"].values() shouldBe listOf(Direction.NORTH, Direction.WEST) }
            .saveDfHtmlSample()
    }

    @JvmInline
    value class IntClass(val value: Int)

    @Test
    fun convertToValueClass() {
        // SampleStart
        dataFrameOf("value")("1", "2") // note that values are strings; conversion is done automatically
            .convert("value").to<IntClass>()
            // SampleEnd
            .also { it["value"].values() shouldBe listOf(IntClass(1), IntClass(2)) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToLocalDateMillis() {
        // SampleStart
        // Long epoch milliseconds -> kotlinx.datetime.LocalDate in the given time zone
        datesDf.convert { millis }.toLocalDate(TimeZone.UTC)
            // SampleEnd
            .also { it.millis.toList() shouldBe listOf(LocalDate(1970, 1, 1), LocalDate(1970, 1, 2)) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToLocalDate() {
        // SampleStart
        // String -> kotlinx.datetime.LocalDate with a pattern
        // the pattern-string overload requires an explicit opt-in
        @OptIn(FormatStringsInDatetimeFormats::class)
        datesDf.convert { date }.toLocalDate("dd.MM.yyyy")
            // SampleEnd
            .also { it.date.toList() shouldBe listOf(LocalDate(2025, 11, 22), LocalDate(2025, 12, 5)) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToLocalDateTime() {
        // SampleStart
        // String -> kotlinx.datetime.LocalDateTime with a DateTimeFormat
        val format = LocalDateTime.Format {
            year()
            chars("-")
            monthNumber()
            chars("-")
            day()
            chars(" ")
            hour()
            chars(":")
            minute()
        }
        datesDf.convert { dateTime }.toLocalDateTime(format)
            // SampleEnd
            .also {
                it["dateTime"].values() shouldBe listOf(
                    LocalDateTime(2025, 11, 22, 11, 30),
                    LocalDateTime(2025, 12, 5, 9, 15),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToJavaLocalDateFormatter() {
        // SampleStart
        // or with a DateTimeFormatter
        datesDf.convert { date }.toJavaLocalDate(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            // SampleEnd
            .also {
                it["date"].values() shouldBe
                    listOf(java.time.LocalDate.of(2025, 11, 22), java.time.LocalDate.of(2025, 12, 5))
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToJavaLocalDate() {
        // SampleStart
        // String -> java.time.LocalDate with a pattern and a locale
        datesDf.convert { date }.toJavaLocalDate("dd.MM.yyyy", Locale.GERMANY)
            // SampleEnd
            .also {
                it.date.toList() shouldBe
                    listOf(java.time.LocalDate.of(2025, 11, 22), java.time.LocalDate.of(2025, 12, 5))
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToStdlibInstant() {
        // SampleStart
        // String -> kotlin.time.Instant
        timestampsDf.convert { timestamp }.toStdlibInstant()
            // SampleEnd
            .also {
                it.timestamp.toList() shouldBe listOf(
                    Instant.parse("2026-05-05T16:42:00Z"),
                    Instant.parse("2026-11-05T09:30:00Z"),
                    null,
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToDateTimeComponents() {
        // SampleStart
        // String -> DateTimeComponents; date and time fields stay accessible for later conversions
        timestampsDf.convert { timestamp }.toDateTimeComponents()
            // SampleEnd
            .also {
                it.timestamp[0]?.year shouldBe 2026
                it.timestamp[1]?.monthNumber shouldBe 11
                it.timestamp[2] shouldBe null
            }
    }

    @Test
    fun convertFromDateTimeComponents() {
        // SampleStart
        // DateTimeComponents -> UtcOffset
        componentsDf.convert { components }.toUtcOffset()
            // SampleEnd
            .also { it.components.toList() shouldBe listOf(UtcOffset.parse("+03:00"), UtcOffset.parse("-05:00")) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToYearMonth() {
        // SampleStart
        // DateTimeComponents -> YearMonth
        componentsDf.convert { components }.toYearMonth()
            // SampleEnd
            .also { it.components.toList() shouldBe listOf(YearMonth(2026, 5), YearMonth(2025, 11)) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToDuration() {
        // SampleStart
        // String -> kotlin.time.Duration
        durationsDf.convert { duration }.toDuration()
            // SampleEnd
            .also { it.duration.toList() shouldBe listOf(Duration.parse("PT1H30M"), Duration.parse("PT45S"), null) }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToJavaDuration() {
        // SampleStart
        // String -> java.time.Duration
        durationsDf.convert { duration }.toJavaDuration()
            // SampleEnd
            .also {
                it.duration.toList() shouldBe
                    listOf(java.time.Duration.parse("PT1H30M"), java.time.Duration.parse("PT45S"), null)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToDataFrames() {
        // SampleStart
        // row-oriented: the first inner list is the header, the remaining lists are rows
        val rowLists = dataFrameOf("userData")(
            listOf(listOf("name", "age"), listOf("Alice", 15), listOf("Bob", 45)),
        ).cast<WithUserData>()

        rowLists.convert { userData }.toDataFrames()
            // SampleEnd
            .also {
                it["userData"][0] shouldBe dataFrameOf("name", "age")("Alice", 15, "Bob", 45)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToDataFramesContainsColumns() {
        // SampleStart
        // column-oriented: each inner list is one column, its first element is the column name
        val columnLists = dataFrameOf("userData")(
            listOf(listOf("name", "Alice", "Bob"), listOf("age", 15, 45)),
        ).cast<WithUserData>()

        columnLists.convert { userData }.toDataFrames(containsColumns = true)
            // SampleEnd
            .also {
                it["userData"][0] shouldBe dataFrameOf("name", "age")("Alice", 15, "Bob", 45)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToImg() {
        // SampleStart
        mediaDf.convert { logo }.toImg(width = 120)
            // SampleEnd
            .also {
                it["logo"][0] shouldBe
                    IMG("https://kotlin.github.io/dataframe/images/kotlin-dataframe-logo.svg", 120)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun convertToIFrame() {
        // SampleStart
        mediaDf.convert { page }.toIFrame(border = true, width = 640, height = 200)
            // SampleEnd
            .also {
                it["page"][0] shouldBe
                    IFRAME("https://kotlin.github.io/dataframe/", border = true, width = 640, height = 200)
                it["page"][1] shouldBe null
            }
            .saveDfHtmlSample()
    }
}
