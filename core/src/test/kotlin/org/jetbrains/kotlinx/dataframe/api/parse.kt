package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.time.LocalTime
import java.time.Month
import java.util.Locale
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
            .group("a", "b").into("c")
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
    fun `parse duration`() {
        columnOf("1d 15m", "20h 35m 11s").parse() shouldBe columnOf(1.days + 15.minutes, 20.hours + 35.minutes + 11.seconds)
    }
}
