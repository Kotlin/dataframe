package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import java.time.LocalTime
import java.time.Month

class ParseTests {

    @Test
    fun parseJson1() {
        val json = """[
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.ncol shouldBe 3
        df.nrow shouldBe 2
        df["a"].type() shouldBe getType<Int>()
        df["b"].type() shouldBe getType<Comparable<*>>()
        df["c"].type() shouldBe getType<Double?>()
    }

    @Test
    fun parseJson2() {
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        println(df)
        df.ncol shouldBe 1
        df.nrow shouldBe 3
        val group = df["a"] as ColumnGroup<*>
        group.ncol shouldBe 3
        group["b"].type() shouldBe getType<Int?>()
        group["value"].type() shouldBe getType<String?>()
        group["array"].type() shouldBe getType<List<Int>>()
    }

    @Test
    fun parseJson3() {
        val json = """[
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.ncol shouldBe 1
        df.nrow shouldBe 3
        df["a"].type() shouldBe getType<List<Number>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson4() {
        val json = """[
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4}, {"d":5} ]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.ncol shouldBe 1
        df.nrow shouldBe 2
        println(df)
        val group = df["a"] as FrameColumn<*>
    }

    @Test
    fun parseDate() {
        val date by columnOf("January 1, 2020")
        val pattern = "MMMM d, yyyy"

        val parsed = date.parse(ParserOptions(dateTimePattern = pattern)).cast<LocalDate>()

        parsed.type() shouldBe getType<LocalDate>()
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
    }

    @Test
    fun parseDateTime() {
        val dateTime by columnOf("3 Jun 2008 13:05:30")
        val pattern = "d MMM yyyy HH:mm:ss"

        val parsed = dateTime.parse(ParserOptions(dateTimePattern = pattern)).cast<LocalDateTime>()

        parsed.type() shouldBe getType<LocalDateTime>()
        with(parsed[0]) {
            month shouldBe Month.JUNE
            dayOfMonth shouldBe 3
            year shouldBe 2008
            hour shouldBe 13
            minute shouldBe 5
            second shouldBe 30
        }

        dateTime.convertToLocalDateTime(pattern) shouldBe parsed
        with(dateTime.toDataFrame()) {
            convert { dateTime }.toLocalDateTime(pattern)[dateTime] shouldBe parsed
            parse(ParserOptions(dateTimePattern = pattern))[dateTime] shouldBe parsed
        }

        DataFrame.parser.addDateTimePattern(pattern)

        dateTime.parse() shouldBe parsed
        dateTime.convertToLocalDateTime() shouldBe parsed

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun parseTime() {
        val time by columnOf(" 13-05-30")
        val pattern = "HH-mm-ss"

        val parsed = time.parse(ParserOptions(dateTimePattern = pattern)).cast<LocalTime>()

        parsed.type() shouldBe getType<LocalTime>()
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
        casted[time].type() shouldBe getType<LocalDate>()
    }
}
