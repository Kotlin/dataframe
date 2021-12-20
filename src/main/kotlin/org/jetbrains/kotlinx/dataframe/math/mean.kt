package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.defaultSkipNA
import java.math.BigDecimal
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal fun <T : Number> Iterable<T>.mean(type: KType, skipNA: Boolean = defaultSkipNA): Double = asSequence().mean(type, skipNA)

internal fun <T : Number> Sequence<T>.mean(type: KType, skipNA: Boolean = defaultSkipNA): Double {
    if (type.isMarkedNullable) {
        return filterNotNull().mean(type.withNullability(false), skipNA)
    }
    return when (type.classifier) {
        Double::class -> (this as Sequence<Double>).mean(skipNA)
        Float::class -> (this as Sequence<Float>).mean(skipNA)
        Int::class -> (this as Sequence<Int>).map { it.toDouble() }.mean(false) // for integer values NA is not possible
        Short::class -> (this as Sequence<Short>).map { it.toDouble() }.mean(false)
        Byte::class -> (this as Sequence<Byte>).map { it.toDouble() }.mean(false)
        Long::class -> (this as Sequence<Long>).map { it.toDouble() }.mean(false)
        BigDecimal::class -> (this as Sequence<BigDecimal>).map { it.toDouble() }.mean(skipNA)
        Number::class -> (this as Sequence<Number>).map { it.toDouble() }.mean(skipNA)
        else -> throw IllegalArgumentException("Unable to compute mean for type $type")
    }
}

public fun Sequence<Double>.mean(skipNA: Boolean = defaultSkipNA): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) continue
            else return Double.NaN
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else Double.NaN
}

@JvmName("meanFloat")
public fun Sequence<Float>.mean(skipNA: Boolean = defaultSkipNA): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) continue
            else return Double.NaN
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else Double.NaN
}

@JvmName("doubleMean")
public fun Iterable<Double>.mean(skipNA: Boolean = defaultSkipNA): Double = asSequence().mean(skipNA)

@JvmName("floatMean")
public fun Iterable<Float>.mean(skipNA: Boolean = defaultSkipNA): Double = asSequence().mean(skipNA)

@JvmName("intMean")
public fun Iterable<Int>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf { count++; it.toDouble() }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("shortMean")
public fun Iterable<Short>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf { count++; it.toDouble() }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("byteMean")
public fun Iterable<Byte>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf { count++; it.toDouble() }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("longMean")
public fun Iterable<Long>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sumOf { it.toDouble() } / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf { count++; it.toDouble() }
        if (count > 0) sum / count else Double.NaN
    }

@JvmName("bigDecimalMean")
public fun Iterable<BigDecimal>.mean(): Double =
    if (this is Collection) {
        if (size > 0) sum().toDouble() / size else Double.NaN
    } else {
        var count = 0
        val sum = sumOf { count++; it.toDouble() }
        if (count > 0) sum / count else Double.NaN
    }
