package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.defaultSkipNA
import java.math.BigDecimal
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public inline fun <reified T : Number> Iterable<T>.std(): Double = std(typeOf<T>())

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
        else -> throw IllegalArgumentException("Unsupported type ${type.classifier}")
    }
}

@JvmName("doubleStd")
public fun Iterable<Double>.std(skipNA: Boolean = defaultSkipNA): Double = stdMean(skipNA).first

@JvmName("floatStd")
public fun Iterable<Float>.std(skipNA: Boolean = defaultSkipNA): Double = stdMean(skipNA).first

@JvmName("intStd")
public fun Iterable<Int>.std(): Double = stdMean().first

@JvmName("longStd")
public fun Iterable<Long>.std(): Double = stdMean().first

@JvmName("bigDecimalStd")
public fun Iterable<BigDecimal>.std(): Double = stdMean().first
