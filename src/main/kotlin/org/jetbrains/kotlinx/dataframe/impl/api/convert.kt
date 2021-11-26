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
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C, R> ConvertClause<T, C>.withRowCellImpl(type: KType?, rowConverter: RowValueExpression<T, C, R>): DataFrame<T> =
    to { col -> df.newColumn(type, col.name) { rowConverter(it, it[col]) } }

@PublishedApi
internal fun <T, C, R> ConvertClause<T, C>.convertRowColumnImpl(
    type: KType?,
    rowConverter: RowColumnExpression<T, C, R>
): DataFrame<T> =
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
                            val conv = getConverter(type, newType) ?: error("Can't find converter from $type to $newType")
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

internal fun Any.convertTo(type: KType): Any? {
    val clazz = javaClass.kotlin
    if (clazz.isSubclassOf(type.jvmErasure)) return this
    val converter = getConverter(clazz.createStarProjectedType(false), type)
    require(converter != null) { "Can not convert $this to type $type" }
    return converter(this)
}

internal inline fun <T> convert(crossinline converter: (T) -> Any?): TypeConverter = { converter(it as T) }

internal fun createConverter(from: KType, to: KType, options: ParserOptions? = null): TypeConverter? {
    if (from.arguments.isNotEmpty() || to.arguments.isNotEmpty()) return null
    if (from.isMarkedNullable) {
        val res = createConverter(from.withNullability(false), to) ?: return null
        return { res(it) }
    }
    val fromClass = from.jvmErasure
    val toClass = to.jvmErasure

    if (fromClass == toClass) return { it }

    return when {
        fromClass == String::class -> Parsers[to.withNullability(false)]?.toConverter(options)
        toClass == String::class -> convert<Any> { it.toString() }
        fromClass == Boolean::class -> when (toClass) {
            Float::class -> convert<Boolean> { if (it) 1.0f else 0.0f }
            Double::class -> convert<Boolean> { if (it) 1.0 else 0.0 }
            Int::class -> convert<Boolean> { if (it) 1 else 0 }
            Long::class -> convert<Boolean> { if (it) 1L else 0L }
            Short::class -> convert<Boolean> {
                val one: Short = 1
                val zero: Short = 0
                if (it) one else zero
            }
            Byte::class -> convert<Boolean> {
                val one: Byte = 1
                val zero: Byte = 0
                if (it) one else zero
            }
            BigDecimal::class -> convert<Boolean> { if (it) BigDecimal.ONE else BigDecimal.ZERO }
            else -> null
        }
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
            Int::class -> convert<Double> { it.roundToInt() }
            Float::class -> convert<Double> { it.toFloat() }
            Long::class -> convert<Double> { it.roundToLong() }
            Short::class -> convert<Double> { it.roundToInt().toShort() }
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
            Long::class -> convert<Float> { it.roundToLong() }
            Int::class -> convert<Float> { it.roundToInt() }
            Short::class -> convert<Float> { it.roundToInt().toShort() }
            BigDecimal::class -> convert<Float> { it.toBigDecimal() }
            else -> null
        }
        fromClass == BigDecimal::class -> when (toClass) {
            Double::class -> convert<BigDecimal> { it.toDouble() }
            Int::class -> convert<BigDecimal> { it.toInt() }
            Float::class -> convert<BigDecimal> { it.toFloat() }
            Long::class -> convert<BigDecimal> { it.toLong() }
            else -> null
        }
        else -> null
    }
}

internal fun Long.toLocalDateTime(zone: ZoneId) = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zone)
internal fun Long.toLocalDate(zone: ZoneId) = toLocalDateTime(zone).toLocalDate()
internal fun Long.toLocalTime(zone: ZoneId) = toLocalDateTime(zone).toLocalTime()

internal val defaultTimeZone = TimeZone.getDefault().toZoneId()
