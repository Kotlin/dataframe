@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.skipNA_default
import java.math.BigDecimal
import kotlin.math.sqrt

public data class BasicStats(val count: Int, val mean: Double, val variance: Double) {

    public fun std(ddof: Int): Double {
        if (count <= ddof) return Double.NaN
        return sqrt(variance / (count - ddof))
    }
}

@JvmName("doubleVarianceAndMean")
public fun Iterable<Double>.varianceAndMean(skipNA: Boolean = skipNA_default): BasicStats? {
    var count = 0
    var sum = .0
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) continue
            else return null
        }
        sum += element
        count++
    }
    val mean = sum / count
    var variance = .0
    for (element in this) {
        if (element.isNaN()) continue
        val diff = element - mean
        variance += diff * diff
    }
    return BasicStats(count, mean, variance)
}

@JvmName("floatVarianceAndMean")
public fun Iterable<Float>.varianceAndMean(skipNA: Boolean = skipNA_default): BasicStats? {
    var count = 0
    var sum = .0
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) continue
            else return null
        }
        sum += element
        count++
    }
    val mean = sum / count
    var variance = .0
    for (element in this) {
        if (element.isNaN()) continue
        val diff = element - mean
        variance += diff * diff
    }
    return BasicStats(count, mean, variance)
}

@JvmName("intVarianceAndMean")
public fun Iterable<Int>.varianceAndMean(): BasicStats {
    var count = 0
    var sum = .0
    for (element in this) {
        sum += element
        count++
    }
    val mean = sum / count
    var variance = .0
    for (element in this) {
        val diff = element - mean
        variance += diff * diff
    }
    return BasicStats(count, mean, variance)
}

@JvmName("longVarianceAndMean")
public fun Iterable<Long>.varianceAndMean(): BasicStats {
    var count = 0
    var sum = .0
    for (element in this) {
        sum += element
        count++
    }
    val mean = sum / count
    var variance = .0
    for (element in this) {
        val diff = element - mean
        variance += diff * diff
    }
    return BasicStats(count, mean, variance)
}

@JvmName("bigDecimalVarianceAndMean")
public fun Iterable<BigDecimal>.varianceAndMean(): BasicStats {
    var count = 0
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += element
        count++
    }
    val mean = sum.div(count.toBigDecimal())
    var variance = BigDecimal.ZERO
    for (element in this) {
        val diff = element - mean
        variance += diff * diff
    }
    return BasicStats(count, mean.toDouble(), variance.toDouble())
}
