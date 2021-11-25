package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.convertRowColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.api.convertToTypeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.io.toDataFrame
import org.jetbrains.kotlinx.dataframe.typeClass
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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

public fun AnyCol.toDateTime(): DataColumn<LocalDateTime> = convertTo()
public fun AnyCol.toDate(): DataColumn<LocalDate> = convertTo()
public fun AnyCol.toTime(): DataColumn<LocalTime> = convertTo()
public fun AnyCol.toInt(): DataColumn<Int> = convertTo()
public fun AnyCol.toStr(): DataColumn<String> = convertTo()
public fun AnyCol.toDouble(): DataColumn<Double> = convertTo()

public fun AnyCol.convertTo(newType: KType): AnyCol = convertToTypeImpl(newType)

public fun <T> ConvertClause<T, *>.toInt(): DataFrame<T> = to<Int>()
public fun <T> ConvertClause<T, *>.toDouble(): DataFrame<T> = to<Double>()
public fun <T> ConvertClause<T, *>.toFloat(): DataFrame<T> = to<Float>()
public fun <T> ConvertClause<T, *>.toStr(): DataFrame<T> = to<String>()
public fun <T> ConvertClause<T, *>.toLong(): DataFrame<T> = to<Long>()
public fun <T> ConvertClause<T, *>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()

public fun <T> ConvertClause<T, *>.toDate(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalDate(zone) }
public fun <T> ConvertClause<T, *>.toTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalTime(zone) }
public fun <T> ConvertClause<T, *>.toDateTime(zone: ZoneId = defaultTimeZone): DataFrame<T> =
    to { it.toLocalDateTime(zone) }

public fun <T, C> ConvertClause<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    to { it.toDataFrames(containsColumns) }

public fun AnyCol.toLocalDate(zone: ZoneId = org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone): DataColumn<LocalDate> = when (typeClass) {
    kotlin.Long::class -> cast<Long>().map { it.toLocalDate(zone) }
    kotlin.Int::class -> cast<Int>().map { it.toLong().toLocalDate(zone) }
    else -> convertTo(getType<LocalDate>()).cast()
}

public fun AnyCol.toLocalDateTime(zone: ZoneId = org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone): DataColumn<LocalDateTime> = when (typeClass) {
    kotlin.Long::class -> cast<Long>().map { it.toLocalDateTime(zone) }
    kotlin.Int::class -> cast<Int>().map { it.toLong().toLocalDateTime(zone) }
    else -> convertTo(getType<LocalDateTime>()).cast()
}

public fun AnyCol.toLocalTime(zone: ZoneId = org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone): DataColumn<LocalTime> = when (typeClass) {
    kotlin.Long::class -> cast<Long>().map { it.toLocalDateTime(zone).toLocalTime() }
    kotlin.Int::class -> cast<Int>().map { it.toLong().toLocalDateTime(zone).toLocalTime() }
    else -> convertTo(getType<LocalTime>()).cast()
}

public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }
