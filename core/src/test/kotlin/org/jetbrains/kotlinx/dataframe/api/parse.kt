package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.catchSilent
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.util.Locale
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import java.time.Instant as JavaInstant

class ParseTests {
    @Test
    fun parseDate() {
        val currentLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("en-US"))
            val date by columnOf("January 1, 2020")
            val pattern = "MMMM d, yyyy"

            val parsed = date.parse(ParserOptions(dateTimePattern = pattern)).cast<LocalDate>()

            parsed.type() shouldBe typeOf<LocalDate>()
            with(parsed[0]) {
                month shouldBe Month.JANUARY
                dayOfMonth shouldBe 1
                year shouldBe 2020
            }

            date.convertToLocalDate(pattern) shouldBe parsed
            with(date.toDataFrame()) {
                convert { date }.toLocalDate(pattern)[date] shouldBe parsed
                parse(ParserOptions(dateTimePattern = pattern))[date] shouldBe parsed
            }

            DataFrame.parser.addDateTimePattern(pattern)

            date.parse() shouldBe parsed
            date.convertToLocalDate() shouldBe parsed

            DataFrame.parser.resetToDefault()
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    fun parseDateTime() {
        val currentLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("en-US"))
            val dateTime by columnOf("3 Jun 2008 13:05:30")
            val pattern = "d MMM yyyy HH:mm:ss"
            val locale = Locale.forLanguageTag("en-US")

            val parsed = dateTime.parse(ParserOptions(dateTimePattern = pattern, locale = locale)).cast<LocalDateTime>()

            parsed.type() shouldBe typeOf<LocalDateTime>()
            with(parsed[0]) {
                month shouldBe Month.JUNE
                dayOfMonth shouldBe 3
                year shouldBe 2008
                hour shouldBe 13
                minute shouldBe 5
                second shouldBe 30
            }

            dateTime.convertToLocalDateTime(pattern, locale) shouldBe parsed
            with(dateTime.toDataFrame()) {
                convert { dateTime }.toLocalDateTime(pattern)[dateTime] shouldBe parsed
                parse(ParserOptions(dateTimePattern = pattern))[dateTime] shouldBe parsed
            }

            DataFrame.parser.addDateTimePattern(pattern)

            dateTime.parse(ParserOptions(locale = locale)) shouldBe parsed
            dateTime.convertToLocalDateTime(pattern, locale) shouldBe parsed

            DataFrame.parser.resetToDefault()
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    fun parseTime() {
        val time by columnOf(" 13-05-30")
        val pattern = "HH-mm-ss"

        val parsed = time.parse(ParserOptions(dateTimePattern = pattern)).cast<LocalTime>()

        parsed.type() shouldBe typeOf<LocalTime>()
        with(parsed[0]) {
            hour shouldBe 13
            minute shouldBe 5
            second shouldBe 30
        }
        time.convertToLocalTime(pattern) shouldBe parsed
        with(time.toDataFrame()) {
            convert { time }.toLocalTime(pattern)[time] shouldBe parsed
            parse(ParserOptions(dateTimePattern = pattern))[time] shouldBe parsed
        }

        DataFrame.parser.addDateTimePattern(pattern)

        time.parse() shouldBe parsed
        time.convertToLocalTime() shouldBe parsed

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun `parse date without formatter`() {
        val time by columnOf(" 2020-01-06", "2020-01-07 ")
        val df = dataFrameOf(time)
        val casted = df.convert(time).toLocalDate()
        casted[time].type() shouldBe typeOf<LocalDate>()
    }

    @Test
    fun `parse column group`() {
        val df = dataFrameOf("a", "b")("1", "2")
        df
            .group("a", "b")
            .into("c")
            .parse("c")
            .ungroup("c") shouldBe dataFrameOf("a", "b")(1, 2)
    }

    @Test
    fun `parse instant`() {
        columnOf("2022-01-23T04:29:40Z").parse().type shouldBe typeOf<Instant>()
        columnOf("2022-01-23T04:29:40+01:00").parse().type shouldBe typeOf<Instant>()

        columnOf("2022-01-23T04:29:40").parse().type shouldBe typeOf<LocalDateTime>()
    }

    @Test
    fun `can parse instants`() {
        val instantParser = Parsers[typeOf<Instant>()]!!
        val javaInstantParser = Parsers[typeOf<JavaInstant>()]!!

        // from the kotlinx-datetime tests, java instants treat leap seconds etc. like this
        fun parseInstantLikeJavaDoesOrNull(input: String): Instant? = catchSilent {
            DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parseOrNull(input)?.apply {
                when {
                    hour == 24 && minute == 0 && second == 0 && nanosecond == 0 -> {
                        setDate(toLocalDate().plus(1, DateTimeUnit.DAY))
                        hour = 0
                    }

                    hour == 23 && minute == 59 && second == 60 -> second = 59
                }
            }?.toInstantUsingOffset()
        }

        fun formatTwoDigits(i: Int) = if (i < 10) "0$i" else "$i"

        for (hour in 23..25) {
            for (minute in listOf(0..5, 58..62).flatten()) {
                for (second in listOf(0..5, 58..62).flatten()) {
                    val input = "2020-03-16T$hour:${formatTwoDigits(minute)}:${formatTwoDigits(second)}Z"

                    val myParserRes = instantParser.applyOptions(null)(input) as Instant?
                    val myJavaParserRes = javaInstantParser.applyOptions(null)(input) as JavaInstant?
                    val instantRes = catchSilent { Instant.parse(input) }
                    val instantLikeJava = parseInstantLikeJavaDoesOrNull(input)
                    val javaInstantRes = catchSilent { JavaInstant.parse(input) }

                    // our parser has a fallback mechanism built in, like this
                    myParserRes shouldBe (instantRes ?: javaInstantRes?.toKotlinInstant())
                    myParserRes shouldBe instantLikeJava

                    myJavaParserRes shouldBe javaInstantRes

                    myParserRes?.toJavaInstant() shouldBe instantLikeJava?.toJavaInstant()
                    instantLikeJava?.toJavaInstant() shouldBe myJavaParserRes
                    myJavaParserRes shouldBe javaInstantRes
                }
            }
        }
    }

    @Test
    fun `parse duration`() {
        columnOf("1d 15m", "20h 35m 11s").parse() shouldBe
            columnOf(1.days + 15.minutes, 20.hours + 35.minutes + 11.seconds)
    }

    @Test
    fun `Parse normal string column`() {
        val df = dataFrameOf(List(5_000) { "_$it" }).fill(100) {
            Random.nextInt().toChar().toString() + Random.nextInt().toChar()
        }

        df.parse()
    }

    /**
     * Asserts that all elements of the iterable are equal to each other
     */
    private fun <T> Iterable<T>.shouldAllBeEqual(): Iterable<T> {
        this should {
            it.reduce { a, b ->
                a shouldBe b
                b
            }
        }
        return this
    }
}
