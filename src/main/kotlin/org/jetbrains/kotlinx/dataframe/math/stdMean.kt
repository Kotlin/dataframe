package org.jetbrains.kotlinx.dataframe.math

import java.math.BigDecimal
import kotlin.math.sqrt

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
