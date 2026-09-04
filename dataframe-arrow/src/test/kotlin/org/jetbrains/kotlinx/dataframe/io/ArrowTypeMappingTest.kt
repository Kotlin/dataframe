package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.apache.arrow.vector.DurationVector
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime

/**
 * Pins the Arrow ↔ Kotlin type mapping that is published as a table in
 * `docs/StardustDocs/topics/dataSources/ApacheArrow.md` (section "Type mapping").
 *
 * The table is hand-written, so without this test it can silently drift away from the code. **If a case here
 * changes, update that table in the same commit** — and vice versa. Each case below is one row of it.
 *
 * The source of truth is `readField` in `arrowReadingImpl.kt` for reading and `KType.toArrowField` in
 * `arrowTypesMatching.kt` for writing. `Struct` / `List` / `LargeList` rows are deliberately not covered here:
 * they map to [org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] / [org.jetbrains.kotlinx.dataframe.columns.FrameColumn]
 * rather than to a value type, and are already covered by `ArrowKtTest` (`books.parquet`, `lists.parquet`,
 * `orders_nested.parquet`, `large_list_sample.parquet`) and `ArrowNullableStructTest`.
 */
internal class ArrowTypeMappingTest {

    /** One row of the documented **read** table: an Arrow type, and the Kotlin type it must produce. */
    private data class ReadCase(val arrowType: ArrowType, val expected: KType)

    /** One row of the documented **write** table: a Kotlin type, and the Arrow type it must produce. */
    private data class WriteCase(val kotlinType: KType, val expected: ArrowType)

    private val readCases = listOf(
        ReadCase(ArrowType.Null(), typeOf<Nothing?>()),
        ReadCase(ArrowType.Bool(), typeOf<Boolean?>()),
        // Signed integers keep their width; unsigned ones widen, because Kotlin has no unsigned column types here.
        ReadCase(ArrowType.Int(8, true), typeOf<Byte?>()),
        ReadCase(ArrowType.Int(16, true), typeOf<Short?>()),
        ReadCase(ArrowType.Int(32, true), typeOf<Int?>()),
        ReadCase(ArrowType.Int(64, true), typeOf<Long?>()),
        ReadCase(ArrowType.Int(8, false), typeOf<Short?>()),
        ReadCase(ArrowType.Int(16, false), typeOf<Int?>()),
        ReadCase(ArrowType.Int(32, false), typeOf<Long?>()),
        ReadCase(ArrowType.Int(64, false), typeOf<BigInteger?>()),
        ReadCase(ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), typeOf<Float?>()),
        ReadCase(ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), typeOf<Double?>()),
        ReadCase(ArrowType.Decimal(10, 2, 128), typeOf<BigDecimal?>()),
        ReadCase(ArrowType.Decimal(10, 2, 256), typeOf<BigDecimal?>()),
        ReadCase(ArrowType.Utf8(), typeOf<String?>()),
        ReadCase(ArrowType.LargeUtf8(), typeOf<String?>()),
        ReadCase(ArrowType.Utf8View(), typeOf<String?>()),
        ReadCase(ArrowType.Binary(), typeOf<ByteArray?>()),
        ReadCase(ArrowType.LargeBinary(), typeOf<ByteArray?>()),
        ReadCase(ArrowType.BinaryView(), typeOf<ByteArray?>()),
        ReadCase(ArrowType.Date(DateUnit.DAY), typeOf<LocalDate?>()),
        ReadCase(ArrowType.Date(DateUnit.MILLISECOND), typeOf<LocalDateTime?>()),
        // A time-of-day never carries a date, so every unit is a LocalTime.
        ReadCase(ArrowType.Time(TimeUnit.SECOND, 32), typeOf<LocalTime?>()),
        ReadCase(ArrowType.Time(TimeUnit.MILLISECOND, 32), typeOf<LocalTime?>()),
        ReadCase(ArrowType.Time(TimeUnit.MICROSECOND, 64), typeOf<LocalTime?>()),
        ReadCase(ArrowType.Time(TimeUnit.NANOSECOND, 64), typeOf<LocalTime?>()),
        // A zone-less timestamp identifies no point on the time-line, so it stays a local date-time...
        ReadCase(ArrowType.Timestamp(TimeUnit.SECOND, null), typeOf<LocalDateTime?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.MILLISECOND, null), typeOf<LocalDateTime?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.MICROSECOND, null), typeOf<LocalDateTime?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.NANOSECOND, null), typeOf<LocalDateTime?>()),
        // ...while one with a zone is normalized to UTC and does identify a single instant (issue #926).
        ReadCase(ArrowType.Timestamp(TimeUnit.SECOND, "UTC"), typeOf<Instant?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.MILLISECOND, "UTC"), typeOf<Instant?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.MICROSECOND, "UTC"), typeOf<Instant?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.NANOSECOND, "UTC"), typeOf<Instant?>()),
        // The zone name is display metadata only, so it does not affect the resulting type.
        ReadCase(ArrowType.Timestamp(TimeUnit.MICROSECOND, "Europe/Berlin"), typeOf<Instant?>()),
        ReadCase(ArrowType.Timestamp(TimeUnit.MICROSECOND, "+05:30"), typeOf<Instant?>()),
        ReadCase(ArrowType.Duration(TimeUnit.MILLISECOND), typeOf<Duration?>()),
    )

    private val writeCases = listOf(
        WriteCase(typeOf<Nothing?>(), ArrowType.Null()),
        WriteCase(typeOf<String>(), ArrowType.Utf8()),
        WriteCase(typeOf<Boolean>(), ArrowType.Bool()),
        WriteCase(typeOf<Byte>(), ArrowType.Int(8, true)),
        WriteCase(typeOf<Short>(), ArrowType.Int(16, true)),
        WriteCase(typeOf<Int>(), ArrowType.Int(32, true)),
        WriteCase(typeOf<Long>(), ArrowType.Int(64, true)),
        WriteCase(typeOf<Float>(), ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE)),
        WriteCase(typeOf<Double>(), ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE)),
        WriteCase(typeOf<LocalDate>(), ArrowType.Date(DateUnit.DAY)),
        WriteCase(typeOf<JavaLocalDate>(), ArrowType.Date(DateUnit.DAY)),
        WriteCase(typeOf<LocalDateTime>(), ArrowType.Date(DateUnit.MILLISECOND)),
        WriteCase(typeOf<JavaLocalDateTime>(), ArrowType.Date(DateUnit.MILLISECOND)),
        WriteCase(typeOf<LocalTime>(), ArrowType.Time(TimeUnit.NANOSECOND, 64)),
        WriteCase(typeOf<JavaLocalTime>(), ArrowType.Time(TimeUnit.NANOSECOND, 64)),
        // An instant is written as a UTC-flagged timestamp, so that reading it back yields an instant again.
        WriteCase(typeOf<Instant>(), ArrowType.Timestamp(TimeUnit.MICROSECOND, "UTC")),
        WriteCase(typeOf<JavaInstant>(), ArrowType.Timestamp(TimeUnit.MICROSECOND, "UTC")),
        // Not in the table: unmapped types degrade to their `toString()`, reported via ConvertingMismatch.
        WriteCase(typeOf<BigDecimal>(), ArrowType.Utf8()),
        WriteCase(typeOf<Duration>(), ArrowType.Utf8()),
    )

    /**
     * Reads a single all-null row per case. Only the column *type* is under test, and
     * [NullabilityOptions.Checking] takes it from the (nullable) Arrow field rather than from the data, so every
     * expectation above is simply the nullable form of the documented type.
     */
    @Test
    fun `reading maps every documented arrow type to the documented kotlin type`() {
        readCases.forEach { (arrowType, expected) ->
            val bytes = arrowBytes(
                Field("value", FieldType.nullable(arrowType), null),
                feather = true,
            ) { root ->
                root.allocateNew()
                root.setRowCount(1)
            }

            val df = DataFrame.readArrowFeather(bytes, NullabilityOptions.Checking)

            arrowType.asClue {
                df["value"].type() shouldBe expected
                df["value"].values().toList() shouldBe listOf(null)
            }
        }
    }

    /**
     * A nullable `Duration` column used to throw `NullPointerException`: its reader called
     * `getObject(i).toKotlinDuration()` without the null guard every neighbouring reader has. The type mapping
     * test above only reads nulls, so this pins the mixed case that actually regressed.
     */
    @Test
    fun `a duration column with both values and nulls is read`() {
        val bytes = arrowBytes(
            Field("value", FieldType.nullable(ArrowType.Duration(TimeUnit.MILLISECOND)), null),
            feather = true,
        ) { root ->
            val vector = root.getVector("value") as DurationVector
            vector.allocateNew(2)
            vector.set(0, 1_500L)
            vector.setNull(1)
            root.setRowCount(2)
        }

        val df = DataFrame.readArrowFeather(bytes)

        df["value"].type() shouldBe typeOf<Duration?>()
        df["value"].values().toList() shouldBe listOf(1_500.milliseconds, null)
    }

    @Test
    fun `writing maps every documented kotlin type to the documented arrow type`() {
        writeCases.forEach { (kotlinType, expected) ->
            kotlinType.asClue {
                kotlinType.toArrowField("value", ignoreMismatchMessage).type shouldBe expected
            }
        }
    }

    /**
     * The two tables have to agree with each other where a round trip is possible, otherwise
     * `writeArrowFeather` followed by `readArrowFeather` would silently change a column's type.
     */
    @Test
    fun `the documented mappings round-trip`() {
        val roundTripped = mapOf(
            typeOf<String>() to typeOf<String?>(),
            typeOf<Boolean>() to typeOf<Boolean?>(),
            typeOf<Byte>() to typeOf<Byte?>(),
            typeOf<Short>() to typeOf<Short?>(),
            typeOf<Int>() to typeOf<Int?>(),
            typeOf<Long>() to typeOf<Long?>(),
            typeOf<Float>() to typeOf<Float?>(),
            typeOf<Double>() to typeOf<Double?>(),
            typeOf<LocalDate>() to typeOf<LocalDate?>(),
            typeOf<LocalDateTime>() to typeOf<LocalDateTime?>(),
            typeOf<LocalTime>() to typeOf<LocalTime?>(),
            typeOf<Instant>() to typeOf<Instant?>(),
        )

        roundTripped.forEach { (written, readBack) ->
            val arrowType = written.toArrowField("value", ignoreMismatchMessage).type
            val matching = readCases.singleOrNull { it.arrowType == arrowType }

            written.asClue {
                // Every writable type must appear in the read table, mapping back to the same Kotlin type.
                matching?.expected shouldBe readBack
            }
        }
    }
}
