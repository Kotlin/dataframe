package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.ddof_default
import org.jetbrains.kotlinx.dataframe.api.skipNaN_default
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Number> Sequence<T?>.std(type: KType, skipNaN: Boolean, ddof: Int): Double {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in std function. This should not occur.")
    }
    return when (type) {
        typeOf<Double>() -> (this as Iterable<Double>).std(skipNaN, ddof)
        Float::class -> (this as Iterable<Float>).std(skipNaN, ddof)
        Int::class, Short::class, Byte::class -> (this as Iterable<Int>).std(ddof)
        Long::class -> (this as Iterable<Long>).std(ddof)
        BigDecimal::class -> (this as Iterable<BigDecimal>).std(ddof)
        BigInteger::class -> (this as Iterable<BigInteger>).std(ddof)
        Number::class -> (this as Iterable<Number>).map { it.toDouble() }.std(skipNaN, ddof)
        Nothing::class -> Double.NaN
        else -> throw IllegalArgumentException("Unable to compute the std for type ${renderType(type)}")
    }
}

/** T: Number? -> Double */
internal val stdTypeConversion: CalculateReturnType = { _, _ ->
    typeOf<Double>()
}

@JvmName("doubleStd")
internal fun Iterable<Double>.std(skipNaN: Boolean = skipNaN_default, ddof: Int = ddof_default): Double =
    calculateBasicStatsOrNull(skipNaN)?.std(ddof) ?: Double.NaN

@JvmName("floatStd")
internal fun Iterable<Float>.std(skipNaN: Boolean = skipNaN_default, ddof: Int = ddof_default): Double =
    calculateBasicStatsOrNull(skipNaN)?.std(ddof) ?: Double.NaN

@JvmName("intStd")
internal fun Iterable<Int>.std(ddof: Int = ddof_default): Double = calculateBasicStatsOrNull().std(ddof)

@JvmName("longStd")
internal fun Iterable<Long>.std(ddof: Int = ddof_default): Double = calculateBasicStatsOrNull().std(ddof)

@JvmName("bigDecimalStd")
internal fun Iterable<BigDecimal>.std(ddof: Int = ddof_default): Double = calculateBasicStatsOrNull().std(ddof)

@JvmName("bigIntegerStd")
internal fun Iterable<BigInteger>.std(ddof: Int = ddof_default): Double = calculateBasicStatsOrNull().std(ddof)
