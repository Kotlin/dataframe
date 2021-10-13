package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.getType
import java.math.BigDecimal
import kotlin.math.sqrt
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

public inline fun <reified T : Number> Iterable<T>.std(): Double = std(getType<T>())

public fun <T : Number> Iterable<T?>.std(type: KType): Double {
    if (type.isMarkedNullable) {
        return filterNotNull().std(type.withNullability(false))
    }
    return when (type.classifier) {
        Double::class -> (this as Iterable<Double>).std()
        Float::class -> (this as Iterable<Float>).std()
        Int::class, Short::class, Byte::class -> (this as Iterable<Int>).std()
        Long::class -> (this as Iterable<Long>).std()
        BigDecimal::class -> (this as Iterable<BigDecimal>).std()
        else -> throw IllegalArgumentException()
    }
}

@JvmName("doubleStd")
public fun Iterable<Double>.std(): Double = stdMean().first

@JvmName("floatStd")
public fun Iterable<Float>.std(): Double = stdMean().first

@JvmName("intStd")
public fun Iterable<Int>.std(): Double = stdMean().first

@JvmName("longStd")
public fun Iterable<Long>.std(): Double = stdMean().first

@JvmName("bigDecimalStd")
public fun Iterable<BigDecimal>.std(): Double = stdMean().first

@JvmName("doubleStdMean")
public fun Iterable<Double>.stdMean(): Pair<Double, Double> {
    val m = mean(false)
    return sqrt(
        fold(0.0) { acc, el ->
            val diff = el - m
            acc + diff * diff
        }
    ) to m
}

@JvmName("floatStdMean")
public fun Iterable<Float>.stdMean(): Pair<Double, Double> {
    val m = mean(false)
    return sqrt(
        fold(0.0) { acc, el ->
            val diff = el - m
            acc + diff * diff
        }
    ) to m
}

@JvmName("intStdMean")
public fun Iterable<Int>.stdMean(): Pair<Double, Double> {
    val m = mean()
    return sqrt(
        fold(0.0) { acc, el ->
            val diff = el - m
            acc + diff * diff
        }
    ) to m
}

@JvmName("longStdMean")
public fun Iterable<Long>.stdMean(): Pair<Double, Double> {
    val m = mean()
    return sqrt(
        fold(0.0) { acc, el ->
            val diff = el - m
            acc + diff * diff
        }
    ) to m
}

@JvmName("bigDecimalStdMean")
public fun Iterable<BigDecimal>.stdMean(): Pair<Double, Double> {
    val m = mean()
    return sqrt(
        fold(0.0) { acc, el ->
            val diff = el.toDouble() - m
            acc + diff * diff
        }
    ) to m
}
