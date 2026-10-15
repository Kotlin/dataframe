package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC

/**
 *
 */
internal interface CommonMedianPercentileDocs : CommonStatisticsDocs {

    /**
     * ## Quantile Estimation Methods
     *
     * Both the [<code>median</code>][DataFrame.median] and the [<code>percentile</code>][DataFrame.percentile] are
     * [quantiles](https://en.wikipedia.org/wiki/Quantile); the median is simply the 50th percentile.
     * Unless a quantile lands exactly on one of the values, it needs to be estimated from the values around it.
     * There are [several common methods](https://en.wikipedia.org/wiki/Quantile#Estimating_quantiles_from_a_sample)
     * to do this. DataFrame follows the definitions of
     * [Hyndman, R. J. & Fan, Y. (1996). Sample Quantiles in Statistical Packages](https://doi.org/10.1080/00031305.1996.10473566)
     * and [Apache Commons Statistics](https://commons.apache.org/proper/commons-statistics/commons-statistics-descriptive/javadocs/api-1.1/org/apache/commons/statistics/descriptive/Quantile.EstimationMethod.html),
     * which call these methods R1 to R9.
     *
     * DataFrame uses:
     * - **R8** for primitive numbers ([<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double]).
     *   It interpolates linearly between the two values around the quantile, so the result is a [<code>Double</code>][Double].
     *   R8 is the method recommended by Hyndman and Fan.
     *   Other libraries, like [Numpy](https://numpy.org/doc/2.1/reference/generated/numpy.quantile.html),
     *   default to R7, so slightly different results are to be expected.
     *   For the median, R8 gives the middle value, or the mean of the two middle values
     *   if the number of values is even.
     * - **R3** for all other self-comparable values (like strings or dates), since those cannot be interpolated,
     *   and for all `-By` operations (like [<code>medianBy</code>][DataFrame.medianBy] and
     *   [<code>percentileBy</code>][DataFrame.percentileBy]), since those select an existing row or element,
     *   even if they compare numbers.
     *   R3 selects the value whose rank (its 1-based position among the sorted values) is the closest to `N * p`,
     *   where `N` is the number of values and `p` is the requested quantile, like `0.5` for the median.
     *   Ties are rounded to the even rank. The result is thus always one of the values itself.
     *
     *
     *
     * Currently, only the default quantile estimation methods can be used.
     * In the future, it might become possible to choose a different one, see #1121.
     *
     * For more information: [See "Quantile Estimation Methods" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#quantile-estimation-methods)
     */
    typealias QuantileEstimationMethods = Nothing
}
