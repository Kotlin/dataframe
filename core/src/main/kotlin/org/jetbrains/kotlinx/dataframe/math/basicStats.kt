@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.ddof_default
import org.jetbrains.kotlinx.dataframe.api.skipNaN_default
import kotlin.math.sqrt

internal data class BasicStats(val count: Int, val mean: Double, val variance: Double)

/**
 * Calculates the standard deviation from a [BasicStats] with optional delta degrees of freedom.
 *
 * @param ddof delta degrees of freedom, the bias-correction of std.
 *   Default is [ddof_default], so `ddof = 1`, the "unbiased sample standard deviation", but alternatively,
 *   the "population standard deviation", so `ddof = 0`, can be used.
 */
internal fun BasicStats.std(ddof: Int): Double =
    if (count <= ddof) {
        Double.NaN
    } else {
        sqrt(variance / (count - ddof))
    }

/**
 * Creates [BasicStats] instance for [this] sequence.
 *
 * This contains the [count][BasicStats.count], [mean][BasicStats.mean], and [variance][BasicStats.variance] and
 * can be used to efficiently calculate the [standard deviation][std].
 */
internal fun Sequence<Double>.calculateBasicStatsOrNull(skipNaN: Boolean = skipNaN_default): BasicStats? {
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
