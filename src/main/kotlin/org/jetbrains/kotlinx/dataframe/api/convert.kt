package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.io.toDataFrame
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType

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

    public inline fun <reified D> to(): DataFrame<T> = to(getType<D>())
}

public fun <T> ConvertClause<T, *>.to(type: KType): DataFrame<T> = to { it.convertTo(type) }

public inline fun <T, C, reified R> ConvertClause<T, C>.with(
    inferType: Boolean = false,
    noinline rowConverter: RowValueExpression<T, C, R>
): DataFrame<T> =
    withRowCellImpl(if (inferType) null else getType<R>(), rowConverter)

public inline fun <T, C, reified R> ConvertClause<T, C>.perRowCol(
    inferType: Boolean = false,
    noinline expression: RowColumnExpression<T, C, R>
): DataFrame<T> =
    convertRowColumnImpl(if (inferType) null else getType<R>(), expression)

public fun <T, C> ConvertClause<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> AnyCol): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(getType<C>()) as DataColumn<C>
public fun AnyCol.convertTo(newType: KType): AnyCol = convertToTypeImpl(newType)

public fun AnyCol.convertToLocalDateTime(): DataColumn<LocalDateTime> = convertTo()
public fun AnyCol.convertToLocalTime(): DataColumn<LocalTime> = convertTo()
public fun AnyCol.convertToInt(): DataColumn<Int> = convertTo()
public fun AnyCol.convertToLong(): DataColumn<Long> = convertTo()
public fun AnyCol.convertToString(): DataColumn<String> = convertTo()
public fun AnyCol.convertToDouble(): DataColumn<Double> = convertTo()
public fun AnyCol.convertToFloat(): DataColumn<Float> = convertTo()
public fun AnyCol.convertToBigDecimal(): DataColumn<BigDecimal> = convertTo()
public fun AnyCol.convertToBoolean(): DataColumn<Boolean> = convertTo()

// region toLocalDate

@JvmName("convertToLocalDateLong")
public fun DataColumn<Long>.convertToLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate> = map { it.toLocalDate(zone) }
public fun DataColumn<Long?>.convertToLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate?> = map { it?.toLocalDate(zone) }

@JvmName("convertToLocalDateInt")
public fun DataColumn<Int>.convertToLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate> = map { it.toLong().toLocalDate(zone) }
@JvmName("convertToLocalDateInt?")
public fun DataColumn<Int?>.convertToLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate?> = map { it?.toLong()?.toLocalDate(zone) }

@JvmName("convertToLocalDateString")
public fun DataColumn<String>.convertToLocalDate(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDate> {
    val converter = Parsers.getConverter(LocalDate::class, pattern, locale)
    return map { converter(it)!! }
}
@JvmName("convertToLocalDateString?")
public fun DataColumn<String?>.convertToLocalDate(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDate?> {
    val converter = Parsers.getConverter(LocalDate::class, pattern, locale)
    return map { it?.let { converter(it)!! } }
}

@JvmName("toLocalDateTLong")
public fun <T> ConvertClause<T, Long>.toLocalDate(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDate(zone) }
@JvmName("toLocalDateTInt")
public fun <T> ConvertClause<T, Int>.toLocalDate(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDate(zone) }

public fun <T> ConvertClause<T, String>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalDate(pattern, locale) }

public fun <T> ConvertClause<T, *>.toLocalDate(): DataFrame<T> = to { it.convertTo<LocalDate>() }

// endregion

// region toLocalTime

@JvmName("convertToLocalTimeLong")
public fun DataColumn<Long>.convertToLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime> = map { it.toLocalTime(zone) }
public fun DataColumn<Long?>.convertToLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime?> = map { it?.toLocalTime(zone) }

@JvmName("convertToLocalTimeInt")
public fun DataColumn<Int>.convertToLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime> = map { it.toLong().toLocalTime(zone) }
@JvmName("convertToLocalTimeInt?")
public fun DataColumn<Int?>.convertToLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime?> = map { it?.toLong()?.toLocalTime(zone) }

@JvmName("convertToLocalTimeString")
public fun DataColumn<String>.convertToLocalTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalTime> {
    val converter = Parsers.getConverter(LocalTime::class, pattern, locale)
    return map { converter(it)!! }
}
@JvmName("convertToLocalTimeString?")
public fun DataColumn<String?>.convertToLocalTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalTime?> {
    val converter = Parsers.getConverter(LocalTime::class, pattern, locale)
    return map { it?.let { converter(it)!! } }
}

@JvmName("toLocalTimeTLong")
public fun <T> ConvertClause<T, Long>.toLocalTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalTime(zone) }
@JvmName("toLocalTimeTInt")
public fun <T> ConvertClause<T, Int>.toLocalTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalTime(zone) }

public fun <T> ConvertClause<T, String>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalTime(pattern, locale) }

public fun <T> ConvertClause<T, *>.toLocalTime(): DataFrame<T> = to { it.convertTo<LocalTime>() }

// endregion

// region toLocalDateTime

@JvmName("convertToLocalDateTimeLong")
public fun DataColumn<Long>.convertToLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }
public fun DataColumn<Long?>.convertToLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeInt")
public fun DataColumn<Int>.convertToLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime> = map { it.toLong().toLocalDateTime(zone) }
@JvmName("convertToLocalDateTimeInt?")
public fun DataColumn<Int?>.convertToLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime?> = map { it?.toLong()?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeString")
public fun DataColumn<String>.convertToLocalDateTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDateTime> {
    val converter = Parsers.getConverter(LocalDateTime::class, pattern, locale)
    return map { converter(it)!! }
}
@JvmName("convertToLocalDateTimeString?")
public fun DataColumn<String?>.convertToLocalDateTime(pattern: String? = null, locale: Locale? = null): DataColumn<LocalDateTime?> {
    val converter = Parsers.getConverter(LocalDateTime::class, pattern, locale)
    return map { it?.let { converter(it)!! } }
}

@JvmName("toLocalDateTimeTLong")
public fun <T> ConvertClause<T, Long>.toLocalDateTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDateTime(zone) }
@JvmName("toLocalDateTimeTInt")
public fun <T> ConvertClause<T, Int>.toLocalDateTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.convertToLocalDateTime(zone) }

public fun <T> ConvertClause<T, String>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> = to { it.convertToLocalDateTime(pattern, locale) }

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
