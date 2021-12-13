package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.convertRowColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.api.convertToTypeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalTime
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.io.toDataFrame
import java.math.BigDecimal
import java.time.LocalTime
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public fun <T, C> DataFrame<T>.convert(columns: ColumnsSelector<T, C>): ConvertClause<T, C> =
    ConvertClause(this, columns)

public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): ConvertClause<T, C> =
    convert { columns.toColumns() }

public fun <T> DataFrame<T>.convert(vararg columns: String): ConvertClause<T, Any?> = convert { columns.toColumns() }
public fun <T, C> DataFrame<T>.convert(vararg columns: ColumnReference<C>): ConvertClause<T, C> =
    convert { columns.toColumns() }

public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    noinline expression: RowValueExpression<T, C, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    noinline expression: RowValueExpression<T, C, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, reified R> DataFrame<T>.convert(
    firstCol: String,
    vararg cols: String,
    noinline expression: RowValueExpression<T, Any?, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, C, reified R> ConvertClause<T, C?>.notNull(crossinline expression: RowValueExpression<T, C, R>): DataFrame<T> =
    with {
        if (it == null) null
        else expression(this, it)
    }

public data class ConvertClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>) {
    public fun <R> cast(): ConvertClause<T, R> = ConvertClause(df, columns as ColumnsSelector<T, R>)

    public inline fun <reified D> to(): DataFrame<T> = to(typeOf<D>())
}

public fun <T> ConvertClause<T, *>.to(type: KType): DataFrame<T> = to { it.convertTo(type) }

public inline fun <T, C, reified R> ConvertClause<T, C>.with(
    inferType: Boolean = false,
    noinline rowConverter: RowValueExpression<T, C, R>
): DataFrame<T> =
    withRowCellImpl(if (inferType) null else typeOf<R>(), rowConverter)

public inline fun <T, C, reified R> ConvertClause<T, C>.perRowCol(
    inferType: Boolean = false,
    noinline expression: RowColumnExpression<T, C, R>
): DataFrame<T> =
    convertRowColumnImpl(if (inferType) null else typeOf<R>(), expression)

public fun <T, C> ConvertClause<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> AnyCol): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(typeOf<C>()) as DataColumn<C>
public fun AnyCol.convertTo(newType: KType): AnyCol = convertToTypeImpl(newType)

@JvmName("convertToLocalDateTimeT")
public fun <T : Any> DataColumn<T>.convertToLocalDateTime(): DataColumn<LocalDateTime> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToLocalDateTime(): DataColumn<LocalDateTime?> = convertTo()

@JvmName("convertToLocalTimeT")
public fun <T : Any> DataColumn<T>.convertToLocalTime(): DataColumn<LocalTime> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToLocalTime(): DataColumn<LocalTime?> = convertTo()

@JvmName("convertToIntT")
public fun <T : Any> DataColumn<T>.convertToInt(): DataColumn<Int> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToInt(): DataColumn<Int?> = convertTo()

@JvmName("convertToLongT")
public fun <T : Any> DataColumn<T>.convertToLong(): DataColumn<Long> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToLong(): DataColumn<Long?> = convertTo()

@JvmName("convertToStringT")
public fun <T : Any> DataColumn<T>.convertToString(): DataColumn<String> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToString(): DataColumn<String?> = convertTo()

@JvmName("convertToDoubleT")
public fun <T : Any> DataColumn<T>.convertToDouble(): DataColumn<Double> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToDouble(): DataColumn<Double?> = convertTo()

@JvmName("convertToFloatT")
public fun <T : Any> DataColumn<T>.convertToFloat(): DataColumn<Float> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToFloat(): DataColumn<Float?> = convertTo()

@JvmName("convertToBigDecimalT")
public fun <T : Any> DataColumn<T>.convertToBigDecimal(): DataColumn<BigDecimal> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToBigDecimal(): DataColumn<BigDecimal?> = convertTo()

@JvmName("convertToBooleanT")
public fun <T : Any> DataColumn<T>.convertToBoolean(): DataColumn<Boolean> = convertTo()
public fun <T : Any> DataColumn<T?>.convertToBoolean(): DataColumn<Boolean?> = convertTo()

// region toLocalDate

@JvmName("convertToLocalDateLong")
public fun DataColumn<Long>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> = map { it.toLocalDate(zone) }
public fun DataColumn<Long?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> = map { it?.toLocalDate(zone) }

@JvmName("convertToLocalDateInt")
public fun DataColumn<Int>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> = map { it.toLong().toLocalDate(zone) }
@JvmName("convertToLocalDateInt?")
public fun DataColumn<Int?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> = map { it?.toLong()?.toLocalDate(zone) }

@JvmName("convertToLocalDateString")
public fun DataColumn<String>.convertToLocalDate(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDate> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") }
}
@JvmName("convertToLocalDateString?")
public fun DataColumn<String?>.convertToLocalDate(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDate?> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") } }
}

@JvmName("toLocalDateTLong")
public fun <T, R : Long?> ConvertClause<T, R>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDate(zone) }
@JvmName("toLocalDateTInt")
public fun <T, R : Int?> ConvertClause<T, R>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDate(zone) }

public fun <T, R : String?> ConvertClause<T, R>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalDate(pattern, locale) }

public fun <T> ConvertClause<T, *>.toLocalDate(): DataFrame<T> = to { it.convertTo<LocalDate>() }

// endregion

// region toLocalTime

@JvmName("convertToLocalTimeLong")
public fun DataColumn<Long>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> = map { it.toLocalTime(zone) }
public fun DataColumn<Long?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> = map { it?.toLocalTime(zone) }

@JvmName("convertToLocalTimeInt")
public fun DataColumn<Int>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> = map { it.toLong().toLocalTime(zone) }
@JvmName("convertToLocalTimeInt?")
public fun DataColumn<Int?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> = map { it?.toLong()?.toLocalTime(zone) }

@JvmName("convertToLocalTimeString")
public fun DataColumn<String>.convertToLocalTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalTime> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") }
}
@JvmName("convertToLocalTimeString?")
public fun DataColumn<String?>.convertToLocalTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalTime?> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") } }
}

@JvmName("toLocalTimeTLong")
public fun <T, R : Long?> ConvertClause<T, R>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalTime(zone) }
@JvmName("toLocalTimeTInt")
public fun <T, R : Int?> ConvertClause<T, R>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalTime(zone) }

public fun <T, R : String?> ConvertClause<T, R>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalTime(pattern, locale) }

public fun <T> ConvertClause<T, *>.toLocalTime(): DataFrame<T> = to { it.convertTo<LocalTime>() }

// endregion

// region toLocalDateTime

@JvmName("convertToLocalDateTimeLong")
public fun DataColumn<Long>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }
public fun DataColumn<Long?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeInt")
public fun DataColumn<Int>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> = map { it.toLong().toLocalDateTime(zone) }
@JvmName("convertToLocalDateTimeInt?")
public fun DataColumn<Int?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> = map { it?.toLong()?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeString")
public fun DataColumn<String>.convertToLocalDateTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDateTime> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") }
}
@JvmName("convertToLocalDateTimeString?")
public fun DataColumn<String?>.convertToLocalDateTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDateTime?> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") } }
}

@JvmName("toLocalDateTimeTLong")
public fun <T, R : Long?> ConvertClause<T, R>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDateTime(zone) }
@JvmName("toLocalDateTimeTInt")
public fun <T, R : Int?> ConvertClause<T, R>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDateTime(zone) }

public fun <T, R : String?> ConvertClause<T, R>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalDateTime(pattern, locale) }

public fun <T> ConvertClause<T, *>.toLocalDateTime(): DataFrame<T> = to { it.convertTo<LocalDateTime>() }

// endregion

public fun <T> ConvertClause<T, *>.toInt(): DataFrame<T> = to<Int>()
public fun <T> ConvertClause<T, *>.toLong(): DataFrame<T> = to<Long>()
public fun <T> ConvertClause<T, *>.toStr(): DataFrame<T> = to<String>()
public fun <T> ConvertClause<T, *>.toDouble(): DataFrame<T> = to<Double>()
public fun <T> ConvertClause<T, *>.toFloat(): DataFrame<T> = to<Float>()
public fun <T> ConvertClause<T, *>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()
public fun <T> ConvertClause<T, *>.toBoolean(): DataFrame<T> = to<Boolean>()

public fun <T, C> ConvertClause<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    to { it.toDataFrames(containsColumns) }

public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }
