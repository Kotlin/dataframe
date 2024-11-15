package org.jetbrains.kotlinx.dataframe.math

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType

@PublishedApi
internal fun <T, R : Number> Iterable<T>.sumOf(type: KType, selector: (T) -> R?): R {
    if (type.isMarkedNullable) {
        val seq = asSequence().mapNotNull(selector).asIterable()
        return seq.sum(type)
    }
    return when (type.classifier) {
        Double::class -> sumOf(selector as ((T) -> Double)) as R

        // careful, conversion to Double to Float occurs! TODO, Issue #558
        Float::class -> sumOf { (selector as ((T) -> Float))(it).toDouble() }.toFloat() as R

        Int::class -> sumOf(selector as ((T) -> Int)) as R

        // careful, conversion to Int occurs! TODO, Issue #558
        Short::class -> sumOf { (selector as ((T) -> Short))(it).toInt() }.toShort() as R

        // careful, conversion to Int occurs! TODO, Issue #558
        Byte::class -> sumOf { (selector as ((T) -> Byte))(it).toInt() }.toByte() as R

        Long::class -> sumOf(selector as ((T) -> Long)) as R

        BigDecimal::class -> sumOf(selector as ((T) -> BigDecimal)) as R

        BigInteger::class -> sumOf(selector as ((T) -> BigInteger)) as R

        Number::class -> sumOf { (selector as ((T) -> Number))(it).toDouble() } as R

        Nothing::class -> 0.0 as R

        else -> throw IllegalArgumentException("sumOf is not supported for $type")
    }
}

@PublishedApi
internal fun <T : Number> Iterable<T>.sum(type: KType): T =
    when (type.classifier) {
        Double::class -> (this as Iterable<Double>).sum() as T

        Float::class -> (this as Iterable<Float>).sum() as T

        Int::class -> (this as Iterable<Int>).sum() as T

        // TODO result should be Int, but same type as input is returned, Issue #558
        Short::class -> (this as Iterable<Short>).sum().toShort() as T

        // TODO result should be Int, but same type as input is returned, Issue #558
        Byte::class -> (this as Iterable<Byte>).sum().toByte() as T

        Long::class -> (this as Iterable<Long>).sum() as T

        BigDecimal::class -> (this as Iterable<BigDecimal>).sum() as T

        BigInteger::class -> (this as Iterable<BigInteger>).sum() as T

        Number::class -> (this as Iterable<Number>).map { it.toDouble() }.sum() as T

        Nothing::class -> 0.0 as T

        else -> throw IllegalArgumentException("sum is not supported for $type")
    }

@JvmName("sumNullableT")
@PublishedApi
internal fun <T : Number> Iterable<T?>.sum(type: KType): T =
    when (type.classifier) {
        Double::class -> (this as Iterable<Double?>).asSequence().filterNotNull().sum() as T

        Float::class -> (this as Iterable<Float?>).asSequence().filterNotNull().sum() as T

        Int::class -> (this as Iterable<Int?>).asSequence().filterNotNull().sum() as T

        // TODO result should be Int, but same type as input is returned, Issue #558
        Short::class -> (this as Iterable<Short?>).asSequence().filterNotNull().sum().toShort() as T

        // TODO result should be Int, but same type as input is returned, Issue #558
        Byte::class -> (this as Iterable<Short?>).asSequence().filterNotNull().sum().toByte() as T

        Long::class -> (this as Iterable<Long?>).asSequence().filterNotNull().sum() as T

        BigDecimal::class -> (this as Iterable<BigDecimal?>).asSequence().filterNotNull().sum() as T

        BigInteger::class -> (this as Iterable<BigInteger?>).asSequence().filterNotNull().sum() as T

        Number::class -> (this as Iterable<Number?>).asSequence().filterNotNull().map { it.toDouble() }.sum() as T

        Nothing::class -> 0.0 as T

        else -> throw IllegalArgumentException("sum is not supported for $type")
    }

@PublishedApi
internal fun Iterable<BigDecimal>.sum(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

@PublishedApi
internal fun Sequence<BigDecimal>.sum(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

@PublishedApi
internal fun Iterable<BigInteger>.sum(): BigInteger {
    var sum: BigInteger = BigInteger.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

@PublishedApi
internal fun Sequence<BigInteger>.sum(): BigInteger {
    var sum: BigInteger = BigInteger.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}
