package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
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

/** @include [Sequence.mean] */
@PublishedApi
internal fun <T : Number> Iterable<T>.mean(type: KType, skipNA: Boolean = skipNA_default): Number? =
    asSequence().mean(type, skipNA)

/**
 * Returns the mean of the numbers in [this].
 *
 * If the input is empty, the return value will be `null`.
 *
 * If the [type] given or input consists of only [Int], [Short], [Byte], [Long], [Double], or [Float],
 * the return type will be [Double]`?` (Never `NaN`).
 *
 * If the [type] given or the input contains [BigInteger] or [BigDecimal], the return type will be [BigDecimal]`?`.
 * @param type The type of the numbers in the sequence.
 * @param skipNA Whether to skip `NaN` values (default: `false`). Only relevant for [Double] and [Float].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T : Number> Sequence<T>.mean(type: KType, skipNA: Boolean = skipNA_default): Number? {
    if (type.isMarkedNullable) {
        return filterNotNull().mean(type.withNullability(false), skipNA)
    }
    return when (type.classifier) {
        // Double -> Double?
        Double::class -> (this as Sequence<Double>).mean(skipNA).takeUnless { it.isNaN }

        // Float -> Double?
        Float::class -> (this as Sequence<Float>).mean(skipNA).takeUnless { it.isNaN }

        // Int -> Double?
        Int::class -> (this as Sequence<Int>).map { it.toDouble() }.mean(false).takeUnless { it.isNaN }

        // Short -> Double?
        Short::class -> (this as Sequence<Short>).map { it.toDouble() }.mean(false).takeUnless { it.isNaN }

        // Byte -> Double?
        Byte::class -> (this as Sequence<Byte>).map { it.toDouble() }.mean(false).takeUnless { it.isNaN }

        // Long -> Double?
        Long::class -> (this as Sequence<Long>).map { it.toDouble() }.mean(false).takeUnless { it.isNaN }

        // BigInteger -> BigDecimal?
        BigInteger::class -> (this as Sequence<BigInteger>).mean()

        // BigDecimal -> BigDecimal?
        BigDecimal::class -> (this as Sequence<BigDecimal>).mean()

        // Number -> Conversion(Common number type) -> Number? (Double or BigDecimal?)
        // fallback case, heavy as it needs to collect all types at runtime
        Number::class -> {
            val numberTypes = (this as Sequence<Number>).asIterable().types()
            val unifiedType = numberTypes.unifiedNumberType()
            if (unifiedType.withNullability(false) == typeOf<Number>()) {
                error("Cannot find unified number type for $numberTypes")
            }
            this.convertToUnifiedNumberType(unifiedType)
                .mean(unifiedType, skipNA)
        }

        // this means the sequence is empty
        Nothing::class -> null

        else -> throw IllegalArgumentException("Unable to compute the mean for type ${renderType(type)}")
    }
}

internal fun Sequence<Double>.mean(skipNA: Boolean = skipNA_default): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) {
                continue
            } else {
                return Double.NaN
            }
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else Double.NaN
}

@JvmName("meanFloat")
internal fun Sequence<Float>.mean(skipNA: Boolean = skipNA_default): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) {
                continue
            } else {
                return Double.NaN
            }
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else Double.NaN
}

@JvmName("bigIntegerMean")
internal fun Sequence<BigInteger>.mean(): BigDecimal? {
    var count = 0
    val sum = sumOf {
        count++
        it
    }
    return if (count > 0) sum.toBigDecimal() / count.toBigDecimal() else null
}

@JvmName("bigDecimalMean")
internal fun Sequence<BigDecimal>.mean(): BigDecimal? {
    var count = 0
    val sum = sumOf {
        count++
        it
    }
    return if (count > 0) sum.toBigDecimal() / count.toBigDecimal() else null
}

@JvmName("doubleMean")
internal fun Iterable<Double>.mean(skipNA: Boolean = skipNA_default): Double = asSequence().mean(skipNA)

@JvmName("floatMean")
internal fun Iterable<Float>.mean(skipNA: Boolean = skipNA_default): Double = asSequence().mean(skipNA)

@JvmName("bigDecimalMean")
internal fun Iterable<BigDecimal>.mean(): BigDecimal? = asSequence().mean()

@JvmName("bigIntegerMean")
internal fun Iterable<BigInteger>.mean(): BigDecimal? = asSequence().mean()

@JvmName("intMean")
internal fun Iterable<Int>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("shortMean")
internal fun Iterable<Short>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("byteMean")
internal fun Iterable<Byte>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("longMean")
internal fun Iterable<Long>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf {
            count++
            it.toDouble()
        }
        if (count > 0) sum / count else Double.NaN
    }
