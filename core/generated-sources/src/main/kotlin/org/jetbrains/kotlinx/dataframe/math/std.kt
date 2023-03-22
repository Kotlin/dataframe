package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.ddof_default
import org.jetbrains.kotlinx.dataframe.api.skipNA_default
import java.math.BigDecimal
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

@PublishedApi
internal fun <T : Number> Iterable<T?>.std(type: KType, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): Double {
    if (type.isMarkedNullable) {
        if (skipNA) {
            return filterNotNull().std(type.withNullability(false), true, ddof)
        } else {
            if (contains(null)) return Double.NaN
            return std(type.withNullability(false), skipNA, ddof)
        }
    }
    return when (type.classifier) {
        Double::class -> (this as Iterable<Double>).std(skipNA, ddof)
        Float::class -> (this as Iterable<Float>).std(skipNA, ddof)
        Int::class, Short::class, Byte::class -> (this as Iterable<Int>).std(ddof)
        Long::class -> (this as Iterable<Long>).std(ddof)
        BigDecimal::class -> (this as Iterable<BigDecimal>).std(ddof)
        else -> throw IllegalArgumentException("Unsupported type ${type.classifier}")
    }
}

@JvmName("doubleStd")
public fun Iterable<Double>.std(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): Double = varianceAndMean(skipNA)?.std(ddof) ?: Double.NaN

@JvmName("floatStd")
public fun Iterable<Float>.std(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): Double = varianceAndMean(skipNA)?.std(ddof) ?: Double.NaN

@JvmName("intStd")
public fun Iterable<Int>.std(ddof: Int = ddof_default): Double = varianceAndMean().std(ddof)

@JvmName("longStd")
public fun Iterable<Long>.std(ddof: Int = ddof_default): Double = varianceAndMean().std(ddof)

@JvmName("bigDecimalStd")
public fun Iterable<BigDecimal>.std(ddof: Int = ddof_default): Double = varianceAndMean().std(ddof)
