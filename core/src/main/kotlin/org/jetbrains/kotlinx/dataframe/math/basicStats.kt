@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.skipNaNDefault

internal data class BasicStats(val count: Int, val mean: Double, val variance: Double)

/**
 * Creates [BasicStats] instance for [this] sequence.
 *
 * This contains the [count][BasicStats.count], [mean][BasicStats.mean], and [variance][BasicStats.variance] and
 * can be used to efficiently calculate the [standard deviation][std].
 */
internal fun Sequence<Double>.calculateBasicStatsOrNull(skipNaN: Boolean = skipNaNDefault): BasicStats? {
    var count = 0
    var sum = .0
    for (element in this) {
        if (element.isNaN()) {
            if (skipNaN) {
                continue
            } else {
                return null
            }
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
    return BasicStats(count = count, mean = mean, variance = variance)
}
