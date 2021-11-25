package org.jetbrains.kotlinx.dataframe.math

import java.math.BigDecimal
import kotlin.reflect.KType

@PublishedApi
internal fun <T, R : Number> Iterable<T>.sumOf(type: KType, selector: (T) -> R?): R {
    if (type.isMarkedNullable) {
        val seq = asSequence().mapNotNull(selector).asIterable()
        return seq.sum(type)
    }
    return when (type.classifier) {
        Double::class -> sumOf(selector as ((T) -> Double)) as R
        Int::class -> sumOf(selector as ((T) -> Int)) as R
        Long::class -> sumOf(selector as ((T) -> Long)) as R
        BigDecimal::class -> sumOf(selector as ((T) -> BigDecimal)) as R
        else -> TODO()
    }
}

@PublishedApi
internal fun <T : Number> Iterable<T>.sum(type: KType): T = when (type.classifier) {
    Double::class -> (this as Iterable<Double>).sum() as T
    Float::class -> (this as Iterable<Float>).sum() as T
    Int::class, Short::class, Byte::class -> (this as Iterable<Int>).sum() as T
    Long::class -> (this as Iterable<Long>).sum() as T
    BigDecimal::class -> (this as Iterable<BigDecimal>).sum() as T
    else -> throw IllegalArgumentException("Sum is not supported for $type")
}

@JvmName("sumT?")
@PublishedApi
internal fun <T : Number> Iterable<T?>.sum(type: KType): T = when (type.classifier) {
    Double::class -> (this as Iterable<Double?>).asSequence().filterNotNull().sum() as T
    Float::class -> (this as Iterable<Float?>).asSequence().filterNotNull().sum() as T
    Int::class, Short::class, Byte::class -> (this as Iterable<Int?>).asSequence().filterNotNull().sum() as T
    Long::class -> (this as Iterable<Long?>).asSequence().filterNotNull().sum() as T
    BigDecimal::class -> (this as Iterable<BigDecimal?>).asSequence().filterNotNull().sum() as T
    else -> TODO()
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
