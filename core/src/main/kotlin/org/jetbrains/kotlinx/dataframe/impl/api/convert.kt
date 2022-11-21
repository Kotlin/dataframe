package org.jetbrains.kotlinx.dataframe.impl.api

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.type
import java.math.BigDecimal
import java.net.URL
import java.time.LocalTime
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C, R> Convert<T, C>.withRowCellImpl(
    type: KType,
    infer: Infer,
    rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> =
    to { col -> df.newColumn(type, col.name, infer) { rowConverter(it, it[col]) } }

@PublishedApi
internal fun <T, C, R> Convert<T, C>.convertRowColumnImpl(
    type: KType,
    infer: Infer,
    rowConverter: RowColumnExpression<T, C, R>,
): DataFrame<T> =
    to { col -> df.newColumn(type, col.name, infer) { rowConverter(it, col) } }

internal fun AnyCol.convertToTypeImpl(to: KType): AnyCol {
    val from = type

    val nullsAreAllowed = to.isMarkedNullable

    var nullsFound = false

    fun Any?.checkNulls() = when {
        this != null -> this
        nullsAreAllowed -> {
            nullsFound = true
            null
        }

        else -> throw TypeConversionException(null, from, to)
    }

    if (from == to) return this

    // catch for ColumnGroup and FrameColumn since they don't have changeType,
    // but user converters can still exist
    if (from.isSubtypeOf(to)) try {
        return (this as DataColumnInternal<*>).changeType(to.withNullability(hasNulls()))
    } catch (_: UnsupportedOperationException) { /* */
    }

    return when (val converter = getConverter(from, to, ParserOptions(locale = Locale.getDefault()))) {
        null -> when (from.classifier) {
            Any::class, Number::class, java.io.Serializable::class, Comparable::class -> {
                // find converter for every value
                val values = values.map {
                    it?.let {
                        val clazz = it.javaClass.kotlin
                        val type = clazz.createStarProjectedType(false)
                        val converter = getConverter(type, to, ParserOptions(locale = Locale.getDefault()))
                            ?: throw TypeConverterNotFoundException(from, to)
                        converter(it)
                    }.checkNulls()
                }
                DataColumn.createValueColumn(name, values, to.withNullability(nullsFound))
            }

            else -> throw TypeConverterNotFoundException(from, to)
        }

        else -> {
            val values = values.map {
                it?.let { converter(it) }.checkNulls()
            }
            DataColumn.createValueColumn(name, values, to.withNullability(nullsFound))
        }
    }
}

internal val convertersCache = mutableMapOf<Triple<KType, KType, ParserOptions?>, TypeConverter?>()

internal fun getConverter(from: KType, to: KType, options: ParserOptions? = null): TypeConverter? =
    convertersCache.getOrPut(Triple(from, to, options)) { createConverter(from, to, options) }

internal typealias TypeConverter = (Any) -> Any?

internal fun Any.convertTo(type: KType): Any? {
    val clazz = javaClass.kotlin
    if (clazz.isSubclassOf(type.jvmErasure)) return this
    val from = clazz.createStarProjectedType(false)
    val converter = getConverter(from, type) ?: throw TypeConverterNotFoundException(from, type)
    return converter(this)
}

internal inline fun <T> convert(crossinline converter: (T) -> Any?): TypeConverter = { converter(it as T) }

private enum class DummyEnum

internal fun createConverter(from: KType, to: KType, options: ParserOptions? = null): TypeConverter? {
    if (from.arguments.isNotEmpty() || to.arguments.isNotEmpty()) return null
    if (from.isMarkedNullable) {
        val res = createConverter(from.withNullability(false), to, options) ?: return null
        return { res(it) }
    }
    val fromClass = from.jvmErasure
    val toClass = to.jvmErasure

    if (fromClass == toClass) return { it }

    if (toClass.isValue) {
        val constructor =
            toClass.primaryConstructor ?: error("Value type $toClass doesn't have primary constructor")
        val underlyingType = constructor.parameters.single().type
        val converter = getConverter(from, underlyingType)
            ?: throw TypeConverterNotFoundException(from, underlyingType)
        return convert<Any> {
            val converted = converter(it)
            if (converted == null && !underlyingType.isMarkedNullable) {
                throw TypeConversionException(it, from, underlyingType)
            }
            constructor.call(converted)
        }
    }

    return when {
        fromClass == String::class -> {
            val parser = Parsers[to.withNullability(false)]
            when {
                parser != null -> parser.toConverter(options)

                // convert enums by name (or by `value` if they implement DataSchemaEnum)
                toClass.isSubclassOf(Enum::class) -> convert<String> { string ->
                    if (toClass.isSubclassOf(DataSchemaEnum::class)) {
                        val enumValues = toClass.java.enumConstants as Array<out DataSchemaEnum>
                        enumValues.firstOrNull { it.value == string }
                            ?: java.lang.Enum.valueOf(toClass.java as Class<DummyEnum>, string)
                    } else {
                        java.lang.Enum.valueOf(toClass.java as Class<DummyEnum>, string)
                    }
                }

                else -> null
            }
        }

        toClass == String::class -> convert<Any> {
            when {
                // convert enums to String by `value` if they implement DataSchemaEnum
                fromClass.isSubclassOf(DataSchemaEnum::class) ->
                    (it as? DataSchemaEnum?)?.value ?: it.toString()

                else -> it.toString()
            }
        }

        fromClass.isValue -> {
            val constructor =
                fromClass.primaryConstructor ?: error("Value type $fromClass doesn't have primary constructor")
            val constructorParameter = constructor.parameters.single()
            val underlyingType = constructorParameter.type
            val converter = getConverter(underlyingType, to)
                ?: throw TypeConverterNotFoundException(underlyingType, to)
            val property =
                fromClass.memberProperties.single { it.name == constructorParameter.name } as kotlin.reflect.KProperty1<Any, *>
            if (property.visibility != kotlin.reflect.KVisibility.PUBLIC) {
                throw TypeConversionException(
                    "Not public member property in primary constructor of value type",
                    from,
                    to
                )
            }

            convert<Any> {
                property.get(it)?.let {
                    converter(it)
                }
            }
        }

        else -> when (fromClass) {
            Boolean::class -> when (toClass) {
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

            Number::class -> when (toClass) {
                Double::class -> convert<Number> { it.toDouble() }
                Int::class -> convert<Number> { it.toInt() }
                Float::class -> convert<Number> { it.toFloat() }
                Byte::class -> convert<Number> { it.toByte() }
                Short::class -> convert<Number> { it.toShort() }
                Long::class -> convert<Number> { it.toLong() }
                Boolean::class -> convert<Number> { it.toDouble() != 0.0 }
                else -> null
            }

            Int::class -> when (toClass) {
                Double::class -> convert<Int> { it.toDouble() }
                Float::class -> convert<Int> { it.toFloat() }
                Byte::class -> convert<Int> { it.toByte() }
                Short::class -> convert<Int> { it.toShort() }
                Long::class -> convert<Int> { it.toLong() }
                BigDecimal::class -> convert<Int> { it.toBigDecimal() }
                Boolean::class -> convert<Int> { it != 0 }
                LocalDateTime::class -> convert<Int> { it.toLong().toLocalDateTime(defaultTimeZone) }
                LocalDate::class -> convert<Int> { it.toLong().toLocalDate(defaultTimeZone) }
                java.time.LocalDateTime::class -> convert<Long> {
                    it.toLocalDateTime(defaultTimeZone).toJavaLocalDateTime()
                }

                java.time.LocalDate::class -> convert<Long> { it.toLocalDate(defaultTimeZone).toJavaLocalDate() }
                LocalTime::class -> convert<Int> { it.toLong().toLocalTime(defaultTimeZone) }
                else -> null
            }

            Double::class -> when (toClass) {
                Int::class -> convert<Double> { it.roundToInt() }
                Float::class -> convert<Double> { it.toFloat() }
                Long::class -> convert<Double> { it.roundToLong() }
                Short::class -> convert<Double> { it.roundToInt().toShort() }
                BigDecimal::class -> convert<Double> { it.toBigDecimal() }
                Boolean::class -> convert<Double> { it != 0.0 }
                else -> null
            }

            Long::class -> when (toClass) {
                Double::class -> convert<Long> { it.toDouble() }
                Float::class -> convert<Long> { it.toFloat() }
                Byte::class -> convert<Long> { it.toByte() }
                Short::class -> convert<Long> { it.toShort() }
                Int::class -> convert<Long> { it.toInt() }
                BigDecimal::class -> convert<Long> { it.toBigDecimal() }
                Boolean::class -> convert<Long> { it != 0L }
                LocalDateTime::class -> convert<Long> { it.toLocalDateTime(defaultTimeZone) }
                LocalDate::class -> convert<Long> { it.toLocalDate(defaultTimeZone) }
                Instant::class -> convert<Long> { Instant.fromEpochMilliseconds(it) }
                java.time.LocalDateTime::class -> convert<Long> {
                    it.toLocalDateTime(defaultTimeZone).toJavaLocalDateTime()
                }

                java.time.LocalDate::class -> convert<Long> { it.toLocalDate(defaultTimeZone).toJavaLocalDate() }
                LocalTime::class -> convert<Long> { it.toLocalTime(defaultTimeZone) }
                else -> null
            }

            Instant::class -> when (toClass) {
                Long::class -> convert<Instant> { it.toEpochMilliseconds() }
                LocalDateTime::class -> convert<Instant> { it.toLocalDateTime(defaultTimeZone) }
                LocalDate::class -> convert<Instant> { it.toLocalDate(defaultTimeZone) }
                java.time.LocalDateTime::class -> convert<Instant> {
                    it.toLocalDateTime(defaultTimeZone).toJavaLocalDateTime()
                }

                java.time.LocalDate::class -> convert<Instant> { it.toLocalDate(defaultTimeZone).toJavaLocalDate() }
                java.time.Instant::class -> convert<Instant> { it.toJavaInstant() }
                LocalTime::class -> convert<Instant> { it.toLocalTime(defaultTimeZone) }
                else -> null
            }

            java.time.Instant::class -> when (toClass) {
                Long::class -> convert<java.time.Instant> { it.toEpochMilli() }
                LocalDateTime::class -> convert<java.time.Instant> {
                    it.toKotlinInstant().toLocalDateTime(defaultTimeZone)
                }

                LocalDate::class -> convert<java.time.Instant> { it.toKotlinInstant().toLocalDate(defaultTimeZone) }
                java.time.LocalDateTime::class -> convert<java.time.Instant> {
                    it.toKotlinInstant().toLocalDateTime(defaultTimeZone).toJavaLocalDateTime()
                }

                java.time.LocalDate::class -> convert<java.time.Instant> {
                    it.toKotlinInstant().toLocalDate(defaultTimeZone).toJavaLocalDate()
                }

                Instant::class -> convert<java.time.Instant> { it.toKotlinInstant() }
                LocalTime::class -> convert<java.time.Instant> { it.toKotlinInstant().toLocalTime(defaultTimeZone) }
                else -> null
            }

            Float::class -> when (toClass) {
                Double::class -> convert<Float> { it.toDouble() }
                Long::class -> convert<Float> { it.roundToLong() }
                Int::class -> convert<Float> { it.roundToInt() }
                Short::class -> convert<Float> { it.roundToInt().toShort() }
                BigDecimal::class -> convert<Float> { it.toBigDecimal() }
                Boolean::class -> convert<Float> { it != 0.0F }
                else -> null
            }

            BigDecimal::class -> when (toClass) {
                Double::class -> convert<BigDecimal> { it.toDouble() }
                Int::class -> convert<BigDecimal> { it.toInt() }
                Float::class -> convert<BigDecimal> { it.toFloat() }
                Long::class -> convert<BigDecimal> { it.toLong() }
                Boolean::class -> convert<BigDecimal> { it != BigDecimal.ZERO }
                else -> null
            }

            LocalDateTime::class -> when (toClass) {
                LocalDate::class -> convert<LocalDateTime> { it.date }
                Instant::class -> convert<LocalDateTime> { it.toInstant(defaultTimeZone) }
                Long::class -> convert<LocalDateTime> { it.toInstant(defaultTimeZone).toEpochMilliseconds() }
                java.time.LocalDateTime::class -> convert<LocalDateTime> { it.toJavaLocalDateTime() }
                java.time.LocalDate::class -> convert<LocalDateTime> { it.date.toJavaLocalDate() }
                java.time.LocalTime::class -> convert<LocalDateTime> { it.toJavaLocalDateTime().toLocalTime() }
                else -> null
            }

            java.time.LocalDateTime::class -> when (toClass) {
                LocalDate::class -> convert<java.time.LocalDateTime> { it.toKotlinLocalDateTime().date }
                LocalDateTime::class -> convert<java.time.LocalDateTime> { it.toKotlinLocalDateTime() }
                Instant::class -> convert<java.time.LocalDateTime> {
                    it.toKotlinLocalDateTime().toInstant(defaultTimeZone)
                }

                Long::class -> convert<java.time.LocalDateTime> {
                    it.toKotlinLocalDateTime().toInstant(defaultTimeZone).toEpochMilliseconds()
                }

                java.time.LocalDate::class -> convert<java.time.LocalDateTime> { it.toLocalDate() }
                java.time.LocalTime::class -> convert<java.time.LocalDateTime> { it.toLocalTime() }
                else -> null
            }

            LocalDate::class -> when (toClass) {
                LocalDateTime::class -> convert<LocalDate> { it.atTime(0, 0) }
                Instant::class -> convert<LocalDate> { it.atStartOfDayIn(defaultTimeZone) }
                Long::class -> convert<LocalDate> { it.atStartOfDayIn(defaultTimeZone).toEpochMilliseconds() }
                java.time.LocalDate::class -> convert<LocalDate> { it.toJavaLocalDate() }
                java.time.LocalDateTime::class -> convert<LocalDate> { it.atTime(0, 0).toJavaLocalDateTime() }
                else -> null
            }

            java.time.LocalDate::class -> when (toClass) {
                LocalDate::class -> convert<java.time.LocalDate> { it.toKotlinLocalDate() }
                LocalDateTime::class -> convert<java.time.LocalDate> { it.atTime(0, 0).toKotlinLocalDateTime() }
                Instant::class -> convert<java.time.LocalDate> {
                    it.toKotlinLocalDate().atStartOfDayIn(defaultTimeZone)
                }

                Long::class -> convert<java.time.LocalDate> {
                    it.toKotlinLocalDate().atStartOfDayIn(defaultTimeZone).toEpochMilliseconds()
                }

                java.time.LocalDateTime::class -> convert<java.time.LocalDate> { it.atStartOfDay() }
                else -> null
            }

            URL::class -> when (toClass) {
                IMG::class -> convert<URL> { IMG(it.toString()) }
                IFRAME::class -> convert<URL> { IFRAME(it.toString()) }
                else -> null
            }

            else -> null
        }
    }
}

internal fun Long.toLocalDateTime(zone: TimeZone = defaultTimeZone) =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(zone)

internal fun Long.toLocalDate(zone: TimeZone = defaultTimeZone) = toLocalDateTime(zone).date
internal fun Long.toLocalTime(zone: TimeZone = defaultTimeZone) =
    toLocalDateTime(zone).toJavaLocalDateTime().toLocalTime()

internal fun Instant.toLocalDate(zone: TimeZone = defaultTimeZone) = toLocalDateTime(zone).date
internal fun Instant.toLocalTime(zone: TimeZone = defaultTimeZone) =
    toLocalDateTime(zone).toJavaLocalDateTime().toLocalTime()

internal val defaultTimeZone = TimeZone.currentSystemDefault()
