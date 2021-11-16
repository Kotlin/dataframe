package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.api.ConvertClause
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.type
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.TimeZone
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability

@PublishedApi
internal fun <T, C, R> ConvertClause<T, C>.convertRowCellImpl(type: KType, rowConverter: RowValueExpression<T, C, R>): DataFrame<T> =
    to { col -> df.newColumn(type, col.name) { rowConverter(it, it[col]) } }

@PublishedApi
internal fun <T, C, R> ConvertClause<T, C>.convertRowColumnImpl(type: KType, rowConverter: RowColumnExpression<T, C, R>): DataFrame<T> =
    to { col -> df.newColumn(type, col.name) { rowConverter(it, col) } }

internal fun AnyCol.convertToTypeImpl(newType: KType): AnyCol {
    val from = type
    val targetType = newType.withNullability(hasNulls)
    return when {
        from == newType -> this
        from.isSubtypeOf(newType) -> (this as DataColumnInternal<*>).changeType(targetType)
        else -> when (val converter = getConverter(from, newType)) {
            null -> when (from.classifier) {
                Any::class, Number::class, java.io.Serializable::class -> {
                    val values = values.map {
                        if (it == null) null else {
                            val clazz = it.javaClass.kotlin
                            val type = clazz.createStarProjectedType(false)
                            val conv = getConverter(type, newType) ?: error("Can't find converter from '$type' to '$newType'")
                            conv(it) ?: error("Can't convert '$it' to '$newType'")
                        }
                    }
                    DataColumn.createValueColumn(name, values, targetType)
                }
                else -> error("Can't find converter from $from to $newType")
            }
            else -> {
                val values = values.map {
                    if (it == null) null
                    else converter(it) ?: error("Can't convert '$it' to '$newType'")
                }
                DataColumn.createValueColumn(name, values, targetType)
            }
        }
    }
}

internal val convertersCache = mutableMapOf<Pair<KType, KType>, TypeConverter?>()

internal fun getConverter(from: KType, to: KType): TypeConverter? = convertersCache.getOrPut(from to to) { createConverter(from, to) }

internal typealias TypeConverter = (Any) -> Any?

internal inline fun <T> convert(crossinline converter: (T) -> Any?): TypeConverter = { converter(it as T) }

internal fun createConverter(from: KType, to: KType, options: ParserOptions? = null): TypeConverter? {
    if (from.arguments.isNotEmpty() || to.arguments.isNotEmpty()) return null
    if (from.isMarkedNullable) {
        val res = createConverter(from.withNullability(false), to) ?: return null
        return { res(it) }
    }
    val fromClass = from.classifier as KClass<*>
    val toClass = to.classifier as KClass<*>

    if (fromClass == toClass) return { it }

    return when {
        fromClass == String::class -> Parsers[to]?.toConverter(options)
        toClass == String::class -> convert<Any> { it.toString() }
        fromClass == Number::class -> when (toClass) {
            Double::class -> convert<Number> { it.toDouble() }
            Int::class -> convert<Number> { it.toInt() }
            Float::class -> convert<Number> { it.toFloat() }
            Byte::class -> convert<Number> { it.toByte() }
            Short::class -> convert<Number> { it.toShort() }
            Long::class -> convert<Number> { it.toLong() }
            else -> null
        }
        fromClass == Int::class -> when (toClass) {
            Double::class -> convert<Int> { it.toDouble() }
            Float::class -> convert<Int> { it.toFloat() }
            Byte::class -> convert<Int> { it.toByte() }
            Short::class -> convert<Int> { it.toShort() }
            Long::class -> convert<Int> { it.toLong() }
            BigDecimal::class -> convert<Int> { it.toBigDecimal() }
            LocalDateTime::class -> convert<Int> { it.toLong().toLocalDateTime(defaultTimeZone) }
            LocalDate::class -> convert<Int> { it.toLong().toLocalDate(defaultTimeZone) }
            LocalTime::class -> convert<Int> { it.toLong().toLocalTime(defaultTimeZone) }
            else -> null
        }
        fromClass == Double::class -> when (toClass) {
            Int::class -> convert<Double> { it.toInt() }
            Float::class -> convert<Double> { it.toFloat() }
            Long::class -> convert<Double> { it.toLong() }
            BigDecimal::class -> convert<Double> { it.toBigDecimal() }
            else -> null
        }
        fromClass == Long::class -> when (toClass) {
            Double::class -> convert<Long> { it.toDouble() }
            Float::class -> convert<Long> { it.toFloat() }
            Byte::class -> convert<Long> { it.toByte() }
            Short::class -> convert<Long> { it.toShort() }
            Int::class -> convert<Long> { it.toInt() }
            BigDecimal::class -> convert<Long> { it.toBigDecimal() }
            LocalDateTime::class -> convert<Long> { it.toLocalDateTime(defaultTimeZone) }
            LocalDate::class -> convert<Long> { it.toLocalDate(defaultTimeZone) }
            LocalTime::class -> convert<Long> { it.toLocalTime(defaultTimeZone) }
            else -> null
        }
        fromClass == Float::class -> when (toClass) {
            Double::class -> convert<Float> { it.toDouble() }
            Long::class -> convert<Float> { it.toLong() }
            Int::class -> convert<Float> { it.toInt() }
            BigDecimal::class -> convert<Float> { it.toBigDecimal() }
            else -> null
        }
        else -> null
    }
}

internal fun Long.toLocalDateTime(zone: ZoneId) = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zone)
internal fun Long.toLocalDate(zone: ZoneId) = toLocalDateTime(zone).toLocalDate()
internal fun Long.toLocalTime(zone: ZoneId) = toLocalDateTime(zone).toLocalTime()

internal val defaultTimeZone = TimeZone.getDefault().toZoneId()
