package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.skipNA_default
import org.jetbrains.kotlinx.dataframe.impl.api.toBigDecimal
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/** @include [Sequence.meanOrNull] */
@PublishedApi
internal fun <T : Number> Iterable<T>.meanOrNull(type: KType, skipNA: Boolean = skipNA_default): Number? =
    asSequence().meanOrNull(type, skipNA)

/**
 * Returns the mean of the numbers in [this].
 *
 * If the input is empty, the return value will be `null`.
 *
 * If the [type] given or input consists of only [Int], [Short], [Byte], [Long], [Double], or [Float],
 * the return type will be [Double].
 *
 * If the [type] given or the input contains [BigInteger] or [BigDecimal],
 * the return type will be [BigDecimal].
 * @param type The type of the numbers in the sequence.
 * @param skipNA Whether to skip `NaN` values (default: `false`). Only relevant for [Double] and [Float].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T : Number> Sequence<T>.meanOrNull(type: KType, skipNA: Boolean = skipNA_default): Number? {
    if (type.isMarkedNullable) {
        return filterNotNull().meanOrNull(type.withNullability(false), skipNA)
    }
    return when (type.classifier) {
        // Double -> Double
        Double::class -> (this as Sequence<Double>).meanOrNull(skipNA)

        // Float -> Double
        Float::class -> (this as Sequence<Float>).meanOrNull(skipNA)

        // Int -> Double
        Int::class -> (this as Sequence<Int>).map { it.toDouble() }.meanOrNull(false)

        // Short -> Double
        Short::class -> (this as Sequence<Short>).map { it.toDouble() }.meanOrNull(false)

        // Byte -> Double
        Byte::class -> (this as Sequence<Byte>).map { it.toDouble() }.meanOrNull(false)

        // Long -> Double
        Long::class -> (this as Sequence<Long>).map { it.toDouble() }.meanOrNull(false)

        // BigInteger -> BigDecimal
        BigInteger::class -> (this as Sequence<BigInteger>).meanOrNull()

        // BigDecimal -> BigDecimal
        BigDecimal::class -> (this as Sequence<BigDecimal>).meanOrNull()

        // Number -> Conversion(Common number type) -> Number? (Double or BigDecimal?)
        // fallback case, heavy as it needs to collect all types at runtime
        Number::class -> {
            val numberTypes = (this as Sequence<Number>).asIterable().types()
            val unifiedType = numberTypes.unifiedNumberType()
            if (unifiedType.withNullability(false) == typeOf<Number>()) {
                error("Cannot find unified number type for $numberTypes")
            }
            this.convertToUnifiedNumberType(unifiedType)
                .meanOrNull(unifiedType, skipNA)
        }

        // this means the sequence is empty
        Nothing::class -> null

        else -> throw IllegalArgumentException("Unable to compute the mean for type ${renderType(type)}")
    }
}

internal fun Sequence<Double>.meanOrNull(skipNA: Boolean = skipNA_default): Double? {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) {
                continue
            } else {
                return null
            }
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else null
}

@JvmName("meanFloat")
internal fun Sequence<Float>.meanOrNull(skipNA: Boolean = skipNA_default): Double? {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) {
                continue
            } else {
                return null
            }
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else null
}

@JvmName("bigIntegerMean")
internal fun Sequence<BigInteger>.meanOrNull(): BigDecimal? {
    var count = 0
    val sum = sumOf {
        count++
        it
    }
    return if (count > 0) sum.toBigDecimal() / count.toBigDecimal() else null
}

@JvmName("bigDecimalMean")
internal fun Sequence<BigDecimal>.meanOrNull(): BigDecimal? {
    var count = 0
    val sum = sumOf {
        count++
        it
    }
    return if (count > 0) sum.toBigDecimal() / count.toBigDecimal() else null
}

@JvmName("doubleMean")
internal fun Iterable<Double>.meanOrNull(skipNA: Boolean = skipNA_default): Double? = asSequence().meanOrNull(skipNA)

@JvmName("floatMean")
internal fun Iterable<Float>.meanOrNull(skipNA: Boolean = skipNA_default): Double? = asSequence().meanOrNull(skipNA)

@JvmName("bigDecimalMean")
internal fun Iterable<BigDecimal>.meanOrNull(): BigDecimal? = asSequence().meanOrNull()

@JvmName("bigIntegerMean")
internal fun Iterable<BigInteger>.meanOrNull(): BigDecimal? = asSequence().meanOrNull()

@JvmName("intMean")
internal fun Iterable<Int>.meanOrNull(): Double? =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else null
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else null
    }

@JvmName("shortMean")
internal fun Iterable<Short>.meanOrNull(): Double? =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else null
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else null
    }

@JvmName("byteMean")
internal fun Iterable<Byte>.meanOrNull(): Double? =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else null
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else null
    }

@JvmName("longMean")
internal fun Iterable<Long>.meanOrNull(): Double? =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else null
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else null
    }
