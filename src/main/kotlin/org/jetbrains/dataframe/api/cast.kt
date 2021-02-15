package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.impl.columns.DataColumnInternal
import org.jetbrains.dataframe.impl.createDataCollector
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.withNullability

fun <T> DataFrame<T>.cast(selector: ColumnsSelector<T, *>) = CastClause(this, selector)
fun <T> DataFrame<T>.cast(vararg columns: KProperty<*>) = cast { columns.toColumns() }
fun <T> DataFrame<T>.cast(vararg columns: String) = cast { columns.toColumns() }
fun <T> DataFrame<T>.cast(vararg columns: Column) = cast { columns.toColumns() }

data class CastClause<T>(val df: DataFrame<T>, val selector: ColumnsSelector<T, *>) {
    inline fun <reified C> to() = to(getType<C>())
}

fun <T> CastClause<T>.to(type: KType) = df.replace(selector).with { it.castTo(type) }

inline fun <reified C> AnyCol.cast(): DataColumn<C> = castTo(getType<C>()) as DataColumn<C>

internal val convertersCache = mutableMapOf<Pair<KType, KType>, TypeConverter?>()

fun AnyCol.castTo(newType: KType): AnyCol {
    val from = type
    if (from == newType) return this
    if (!from.isSubtypeOf(newType)) {
        val converter = convertersCache.getOrPut(from to newType) { createConverter(from, newType) }
        if(converter != null) {
            var hasNulls = hasNulls
            val values = values.map { if (it == null) null else converter(it).also { if (it == null) hasNulls = true } }
            return DataColumn.create(name, values, newType.withNullability(hasNulls))
        }
    }
    return (this as DataColumnInternal<*>).changeType(newType.withNullability(hasNulls))
}

internal typealias TypeConverter = (Any) -> Any?

internal inline fun <T> convert(crossinline converter: (T)->Any?): TypeConverter = { converter(it as T) }

internal fun createConverter(from: KType, to: KType): TypeConverter? {
    if (from.arguments.isNotEmpty() || to.arguments.isNotEmpty()) return null
    if(from.isMarkedNullable) {
        val res = createConverter(from.withNullability(false), to) ?: return null
        return { if (it == null) null else res(it) }
    }
    val fromClass = from.classifier as KClass<*>
    val toClass = to.classifier as KClass<*>

    if(fromClass == toClass) return { it }

    return when {
        fromClass == String::class -> Parsers[to]?.toConverter()
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
            else -> null
        }
        fromClass == Double::class -> when(toClass){
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

fun <T> CastClause<T>.toInt() = to<Int>()
fun <T> CastClause<T>.toDouble() = to<Double>()
fun <T> CastClause<T>.toFloat() = to<Float>()
fun <T> CastClause<T>.toStr() = to<String>()
fun <T> CastClause<T>.toLong() = to<Long>()
fun <T> CastClause<T>.toBigDecimal() = to<BigDecimal>()