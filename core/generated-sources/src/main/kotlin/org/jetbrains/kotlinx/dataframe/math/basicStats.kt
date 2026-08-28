@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.skipNaNDefault

internal data class BasicStats(val count: Int, val mean: Double, val variance: Double)

/**
 * Creates [<code>BasicStats</code>][BasicStats] instance for [<code>this</code>][this] sequence.
 *
 * This contains the [<code>count</code>][BasicStats.count], [<code>mean</code>][BasicStats.mean], and [<code>variance</code>][BasicStats.variance] and
 * can be used to efficiently calculate the [<code>standard deviation</code>][std].
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
