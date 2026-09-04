package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

/**
 * Create Arrow [Field] (note: this is part of [Schema], does not contain data itself) that has the same
 * name, type and nullable as [this]
 */
public fun AnyCol.toArrowField(mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage): Field {
    val column = this
    val columnType = column.type()
    return when (column) {
        is ColumnGroup<*> -> {
            ColumnSchema.Group(schema(), type()).toArrowField(column.name(), mismatchSubscriber)
        }

        else -> columnType.toArrowField(column.name(), mismatchSubscriber)
    }
}

internal fun ColumnSchema.toArrowField(name: String, mismatchSubscriber: (ConvertingMismatch) -> Unit): Field =
    when (this) {
        is ColumnSchema.Value -> type.toArrowField(name, mismatchSubscriber)

        is ColumnSchema.Group -> {
            val childFields = schema.columns.map { (childName, childSchema) ->
                childSchema.toArrowField(childName, mismatchSubscriber)
            }
            Field(name, FieldType(nullable, ArrowType.Struct(), null), childFields)
        }

        is ColumnSchema.Frame -> {
            val childFields = schema.columns.map { (childName, childSchema) ->
                childSchema.toArrowField(childName, mismatchSubscriber)
            }
            val childStructField = Field("item", FieldType(true, ArrowType.Struct(), null), childFields)
            Field(name, FieldType(nullable, ArrowType.List(), null), listOf(childStructField))
        }
    }

/**
 * `kotlinx.datetime.Instant` is superseded by [kotlin.time.Instant] but still resolvable, so columns holding it are
 * mapped too. Kept in a `val` to keep the deprecation suppression off the whole `when`. See issue #1350.
 */
@Suppress("DEPRECATION")
internal val deprecatedInstantType: KType = typeOf<DeprecatedInstant?>()

internal const val NANOS_PER_SECOND: Long = 1_000_000_000L

/**
 * How many of these units fit into one second.
 *
 * Every Arrow timestamp vector stores a `Long` counting this many parts of a second since the epoch, so this is the
 * divisor that splits such a value into whole seconds plus a sub-second remainder — and the multiplier that puts it
 * back together. Shared by the reader ([DataFrame.readArrowFeather]) and the writer ([AnyFrame.arrowWriter]) so the
 * two can never disagree about a unit.
 */
internal val TimeUnit.perSecond: Long
    get() = when (this) {
        TimeUnit.SECOND -> 1L
        TimeUnit.MILLISECOND -> 1_000L
        TimeUnit.MICROSECOND -> 1_000_000L
        TimeUnit.NANOSECOND -> NANOS_PER_SECOND
    }

/**
 * The precision an [Instant][StdlibInstant] column is written with when no target [Schema] is supplied.
 *
 * Microseconds, not nanoseconds, for range: an Arrow nanosecond timestamp is an `int64` count of nanoseconds since
 * the epoch, which only spans 1677–2262, while a microsecond one covers any [StdlibInstant]. It is also the
 * precision pandas, Polars and PyArrow emit by default. The trade-off is that a sub-microsecond instant loses its
 * last three digits on write; that is reported as [ConvertingMismatch.PrecisionReduced], and an explicit target
 * [Schema] with `Timestamp(NANOSECOND, "UTC")` avoids it.
 */
internal val DEFAULT_INSTANT_UNIT: TimeUnit = TimeUnit.MICROSECOND

internal fun KType.toArrowField(name: String, mismatchSubscriber: (ConvertingMismatch) -> Unit): Field {
    val nullable = isMarkedNullable
    return when {
        this == nullableNothingType -> Field(name, FieldType(true, ArrowType.Null(), null), emptyList())

        isSubtypeOf(typeOf<String?>()) ->
            Field(name, FieldType(nullable, ArrowType.Utf8(), null), emptyList())

        isSubtypeOf(typeOf<Boolean?>()) ->
            Field(name, FieldType(nullable, ArrowType.Bool(), null), emptyList())

        isSubtypeOf(typeOf<Byte?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(8, true), null), emptyList())

        isSubtypeOf(typeOf<Short?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(16, true), null), emptyList())

        isSubtypeOf(typeOf<Int?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(32, true), null), emptyList())

        isSubtypeOf(typeOf<Long?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(64, true), null), emptyList())

        isSubtypeOf(typeOf<Float?>()) ->
            Field(name, FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null), emptyList())

        isSubtypeOf(typeOf<Double?>()) ->
            Field(name, FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalDate?>()) || isSubtypeOf(typeOf<LocalDate?>()) ->
            Field(name, FieldType(nullable, ArrowType.Date(DateUnit.DAY), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalDateTime?>()) || isSubtypeOf(typeOf<LocalDateTime?>()) ->
            Field(name, FieldType(nullable, ArrowType.Date(DateUnit.MILLISECOND), null), emptyList())

        // An instant is written as a timestamp *with* a timezone: the `"UTC"` marker says the values are already
        // normalized to UTC, which is what Parquet records as `isAdjustedToUTC = true`, and is what makes the
        // column read back as an instant rather than as a local date-time. For the unit see
        // [DEFAULT_INSTANT_UNIT] — it is deliberately coarser than an instant can be.
        isSubtypeOf(typeOf<StdlibInstant?>()) ||
            isSubtypeOf(typeOf<JavaInstant?>()) ||
            isSubtypeOf(deprecatedInstantType) ->
            Field(name, FieldType(nullable, ArrowType.Timestamp(DEFAULT_INSTANT_UNIT, "UTC"), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalTime?>()) || isSubtypeOf(typeOf<LocalTime?>()) ->
            Field(name, FieldType(nullable, ArrowType.Time(TimeUnit.NANOSECOND, 64), null), emptyList())

        else -> {
            val clazz = (classifier as? kotlin.reflect.KClass<*>)?.java ?: Any::class.java
            mismatchSubscriber(ConvertingMismatch.SavedAsString(name, clazz))
            Field(name, FieldType(true, ArrowType.Utf8(), null), emptyList())
        }
    }
}

/**
 * Create Arrow [Schema] matching [this] actual data.
 * Columns with not supported types will be interpreted as String
 *
 * Note the two ways a date-time column is mapped: a `LocalDateTime` column becomes `Date(MILLISECOND)`, while an
 * `Instant` column becomes `Timestamp(MICROSECOND, "UTC")` — a timestamp *with* a time zone, which is how Arrow
 * and Parquet mark values already normalized to UTC. Sub-microsecond instants lose their last three digits at that
 * precision, reported as [ConvertingMismatch.PrecisionReduced]; see [DEFAULT_INSTANT_UNIT] for why.
 */
public fun List<AnyCol>.toArrowSchema(
    mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage,
): Schema {
    val fields = this.map { it.toArrowField(mismatchSubscriber) }
    return Schema(fields)
}
