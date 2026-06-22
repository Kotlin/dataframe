@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.compileTimeSchema
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toBigDecimal
import org.jetbrains.kotlinx.dataframe.api.toBigInteger
import org.jetbrains.kotlinx.dataframe.api.toBoolean
import org.jetbrains.kotlinx.dataframe.api.toDateTimeComponents
import org.jetbrains.kotlinx.dataframe.api.toDeprecatedInstant
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.api.toDuration
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toIFrame
import org.jetbrains.kotlinx.dataframe.api.toImg
import org.jetbrains.kotlinx.dataframe.api.toInstant
import org.jetbrains.kotlinx.dataframe.api.toInt
import org.jetbrains.kotlinx.dataframe.api.toJavaDuration
import org.jetbrains.kotlinx.dataframe.api.toJavaInstant
import org.jetbrains.kotlinx.dataframe.api.toJavaLocalDate
import org.jetbrains.kotlinx.dataframe.api.toJavaLocalDateTime
import org.jetbrains.kotlinx.dataframe.api.toJavaLocalTime
import org.jetbrains.kotlinx.dataframe.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.api.toLocalTime
import org.jetbrains.kotlinx.dataframe.api.toLong
import org.jetbrains.kotlinx.dataframe.api.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.api.toStr
import org.jetbrains.kotlinx.dataframe.api.toUtcOffset
import org.jetbrains.kotlinx.dataframe.api.toYearMonth
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Instant

class Convert {
    @Test
    fun `convert to IFrame preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(URL("https://kotlinlang.org"), URL("https://kotlinlang.org")),
            "b" to columnOf(URL("https://github.com/Kotlin/dataframe"), null),
        )

        val converted = df.convert { a and b }.toIFrame()

        assertNullabilityPreserved(converted, typeOf<IFRAME>())
    }

    @Test
    fun `convert to Img preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(URL("https://example.com/photo1.png"), URL("https://example.com/photo1.png")),
            "b" to columnOf(URL("https://example.com/photo3.jpg"), null),
        )

        val converted = df.convert { a and b }.toImg()

        assertNullabilityPreserved(converted, typeOf<IMG>())
    }

    // TODO: Add tests for toUrl (and maybe for toURL) when #1903 is resolved

    @Test
    fun `convert to Instant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15T10:30:00Z", "2025-01-15T10:30:00Z"),
            "b" to columnOf("2026-01-15T10:30:00Z", null),
        )

        val converted = df.convert { a and b }.toInstant()

        assertNullabilityPreserved(converted, typeOf<kotlinx.datetime.Instant>())
    }

    @Test
    fun `convert to DeprecatedInstant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15T10:30:00Z", "2025-01-15T10:30:00Z"),
            "b" to columnOf("2026-01-15T10:30:00Z", null),
        )

        val converted = df.convert { a and b }.toDeprecatedInstant()

        assertNullabilityPreserved(converted, typeOf<kotlinx.datetime.Instant>())
    }

    @Test
    fun `convert String to StdlibInstant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15T10:30:00Z", "2025-01-15T10:30:00Z"),
            "b" to columnOf("2026-01-15T10:30:00Z", null),
        )

        val converted = df.convert { a and b }.toStdlibInstant()

        assertNullabilityPreserved(converted, typeOf<Instant>())
    }

    @Test
    fun `convert DeprecatedInstant to StdlibInstant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                kotlinx.datetime.Instant.parse("2024-01-15T10:30:00Z"),
                kotlinx.datetime.Instant.parse("2025-01-15T10:30:00Z"),
            ),
            "b" to columnOf(
                kotlinx.datetime.Instant.parse("2026-01-15T10:30:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toStdlibInstant()

        assertNullabilityPreserved(converted, typeOf<Instant>())
    }

    @Test
    fun `convert DateTimeComponents to StdlibInstant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toStdlibInstant()

        assertNullabilityPreserved(converted, typeOf<Instant>())
    }

    @Test
    fun `convert to UtcOffset preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toUtcOffset()

        assertNullabilityPreserved(converted, typeOf<UtcOffset>())
    }

    @Test
    fun `convert to YearMonth preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toYearMonth()

        assertNullabilityPreserved(converted, typeOf<YearMonth>())
    }

    @Test
    fun `convert Long to LocalDate preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1715927320000L, 1714927620000L),
            "b" to columnOf(1701917720200L, null),
        )

        val converted = df.convert { a and b }.toLocalDate()

        assertNullabilityPreserved(converted, typeOf<LocalDate>())
    }

    @Test
    fun `convert Int to LocalDate preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1991592732, 171492762),
            "b" to columnOf(170191772, null),
        )

        val converted = df.convert { a and b }.toLocalDate()

        assertNullabilityPreserved(converted, typeOf<LocalDate>())
    }

    @Test
    fun `convert String to LocalDate (format) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05", "2024-06-01"),
            "b" to columnOf("2024-05-07", null),
        )

        val converted = df.convert { a and b }.toLocalDate()

        assertNullabilityPreserved(converted, typeOf<LocalDate>())
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun `convert String to LocalDate (pattern) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05", "2024-06-01"),
            "b" to columnOf("2024-05-07", null),
        )

        val converted = df.convert { a and b }.toLocalDate("yyyy-MM-dd")

        assertNullabilityPreserved(converted, typeOf<LocalDate>())
    }

    @Test
    fun `convert DateTimeComponents to LocalDate preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toLocalDate()

        assertNullabilityPreserved(converted, typeOf<LocalDate>())
    }

    @Test
    fun `convert Long to LocalTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1715927320000L, 1714927620000L),
            "b" to columnOf(1701917720200L, null),
        )

        val converted = df.convert { a and b }.toLocalTime()

        assertNullabilityPreserved(converted, typeOf<LocalTime>())
    }

    @Test
    fun `convert Int to LocalTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1991592732, 171492762),
            "b" to columnOf(170191772, null),
        )

        val converted = df.convert { a and b }.toLocalTime()

        assertNullabilityPreserved(converted, typeOf<LocalTime>())
    }

    @Test
    fun `convert String to LocalTime (format) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("11:22:00", "12:22:00"),
            "b" to columnOf("13:22:00", null),
        )

        val converted = df.convert { a and b }.toLocalTime()

        assertNullabilityPreserved(converted, typeOf<LocalTime>())
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun `convert String to LocalTime (pattern) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("11:22:00", "12:22:00"),
            "b" to columnOf("13:22:00", null),
        )

        val converted = df.convert { a and b }.toLocalTime("HH:mm:ss")

        assertNullabilityPreserved(converted, typeOf<LocalTime>())
    }

    @Test
    fun `convert DateTimeComponents to LocalTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toLocalTime()

        assertNullabilityPreserved(converted, typeOf<LocalTime>())
    }

    @Test
    fun `convert Long to LocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1715927320000L, 1714927620000L),
            "b" to columnOf(1701917720200L, null),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert DeprecatedInstant to LocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                kotlinx.datetime.Instant.parse("2024-01-15T10:30:00Z"),
                kotlinx.datetime.Instant.parse("2025-01-15T10:30:00Z"),
            ),
            "b" to columnOf(
                kotlinx.datetime.Instant.parse("2026-01-15T10:30:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert StdlibInstant to LocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                Instant.parse("2024-01-15T10:30:00Z"),
                Instant.parse("2024-06-20T12:00:00Z"),
            ),
            "b" to columnOf(
                Instant.parse("2024-03-10T08:00:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert Int to LocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1991592732, 171492762),
            "b" to columnOf(170191772, null),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert String to LocalDateTime (format) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05 11:22:10", "2024-05-05 11:22:00"),
            "b" to columnOf("2024-05-05 11:22:00", null),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun `convert String to LocalDateTime (pattern) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05 11:22:10", "2024-05-05 11:22:00"),
            "b" to columnOf("2024-05-05 11:22:00", null),
        )

        val converted = df.convert { a and b }.toLocalDateTime("yyyy-MM-dd HH:mm:ss")

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert DateTimeComponents to LocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2024-05-05T16:42:00Z"),
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2025-05-05T16:42:00Z"),
            ),
            "b" to columnOf(
                DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse("2026-05-05T16:42:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<LocalDateTime>())
    }

    @Test
    fun `convert String to DateTimeComponents (format) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05T16:42:00Z", "2024-05-05T16:42:00Z"),
            "b" to columnOf("2024-05-05T16:42:00Z", null),
        )

        val converted = df.convert { a and b }.toDateTimeComponents()

        assertNullabilityPreserved(converted, typeOf<DateTimeComponents>())
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun `convert String to DateTimeComponents (pattern) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05T16:42:00Z", "2024-05-05T16:42:00Z"),
            "b" to columnOf("2024-05-05T16:42:00Z", null),
        )

        val converted = df.convert { a and b }.toDateTimeComponents("yyyy-MM-dd'T'HH:mm:ss'Z'")

        assertNullabilityPreserved(converted, typeOf<DateTimeComponents>())
    }

    @Test
    fun `convert to Duration preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("PT1H30M", "PT45S"),
            "b" to columnOf("PT1H30M", null),
        )

        val converted = df.convert { a and b }.toDuration()

        assertNullabilityPreserved(converted, typeOf<Duration>())
    }

    @Test
    fun `convert to JavaInstant preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-05-05T16:42:00Z", "2024-05-05T16:42:00Z"),
            "b" to columnOf("2024-05-05T16:42:00Z", null),
        )

        val converted = df.convert { a and b }.toJavaInstant()

        assertNullabilityPreserved(converted, typeOf<java.time.Instant>())
    }

    @Test
    fun `convert to JavaDuration preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("PT1H30M", "PT45S"),
            "b" to columnOf("PT1H30M", null),
        )

        val converted = df.convert { a and b }.toJavaDuration()

        assertNullabilityPreserved(converted, typeOf<java.time.Duration>())
    }

    @Test
    fun `convert to JavaLocalDate preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15", "2024-06-20"),
            "b" to columnOf("2024-03-10", null),
        )

        val converted = df.convert { a and b }.toJavaLocalDate()

        assertNullabilityPreserved(converted, typeOf<java.time.LocalDate>())
    }

    @Test
    fun `convert to JavaLocalTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("11:22:33", "14:30:00"),
            "b" to columnOf("09:15:00", null),
        )

        val converted = df.convert { a and b }.toJavaLocalTime()

        assertNullabilityPreserved(converted, typeOf<java.time.LocalTime>())
    }

    @Test
    fun `convert StdlibInstant to JavaLocalDateTime preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(
                Instant.parse("2024-01-15T10:30:00Z"),
                Instant.parse("2024-06-20T12:00:00Z"),
            ),
            "b" to columnOf(
                Instant.parse("2024-03-10T08:00:00Z"),
                null,
            ),
        )

        val converted = df.convert { a and b }.toJavaLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<java.time.LocalDateTime>())
    }

    @Test
    fun `convert String to JavaLocalDateTime (formatter) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15 11:22:33", "2024-06-20 14:30:00"),
            "b" to columnOf("2024-03-10 09:15:00", null),
        )

        val converted = df.convert { a and b }.toJavaLocalDateTime()

        assertNullabilityPreserved(converted, typeOf<java.time.LocalDateTime>())
    }

    @Test
    fun `convert String to JavaLocalDateTime (pattern) preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf("2024-01-15 11:22:33", "2024-06-20 14:30:00"),
            "b" to columnOf("2024-03-10 09:15:00", null),
        )

        val converted = df.convert { a and b }.toJavaLocalDateTime("yyyy-MM-dd HH:mm:ss")

        assertNullabilityPreserved(converted, typeOf<java.time.LocalDateTime>())
    }

    @Test
    fun `convert to Int preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1.0, 2.0),
            "b" to columnOf(3.0, null),
        )

        val converted = df.convert { a and b }.toInt()

        assertNullabilityPreserved(converted, typeOf<Int>())
    }

    @Test
    fun `convert to Long preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1.0, 2.0),
            "b" to columnOf(3.0, null),
        )

        val converted = df.convert { a and b }.toLong()

        assertNullabilityPreserved(converted, typeOf<Long>())
    }

    @Test
    fun `convert to Str preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1.0, 2.0),
            "b" to columnOf(3.0, null),
        )

        val converted = df.convert { a and b }.toStr()

        assertNullabilityPreserved(converted, typeOf<String>())
    }

    @Test
    fun `convert to Double preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(123, 322),
            "b" to columnOf(1.0, null),
        )

        val converted = df.convert { a and b }.toDouble()

        assertNullabilityPreserved(converted, typeOf<Double>())
    }

    @Test
    fun `convert to Float preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(123, 322),
            "b" to columnOf(1.0, null),
        )

        val converted = df.convert { a and b }.toFloat()

        assertNullabilityPreserved(converted, typeOf<Float>())
    }

    @Test
    fun `convert to BigDecimal preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(123, 322),
            "b" to columnOf(1.0, null),
        )

        val converted = df.convert { a and b }.toBigDecimal()

        assertNullabilityPreserved(converted, typeOf<BigDecimal>())
    }

    @Test
    fun `convert to BigInteger preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(123, 322),
            "b" to columnOf(1.0, null),
        )

        val converted = df.convert { a and b }.toBigInteger()

        assertNullabilityPreserved(converted, typeOf<BigInteger>())
    }

    @Test
    fun `convert to Boolean preserves nullability of the column type`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 0),
            "b" to columnOf(1, null),
        )

        val converted = df.convert { a and b }.toBoolean()

        assertNullabilityPreserved(converted, typeOf<Boolean>())
    }

    private inline fun <reified T> assertNullabilityPreserved(converted: DataFrame<T>, nonNullType: KType) {
        val expected = DataFrameSchemaImpl(
            columns = mapOf(
                "a" to ColumnSchema.Value(nonNullType),
                "b" to ColumnSchema.Value(nonNullType.withNullability(true)),
            ),
        )
        converted.schema() shouldBe expected
        converted.compileTimeSchema() shouldBe expected
    }
}
