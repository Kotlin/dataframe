package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.between

// region DataColumn

/**
 * Returns a [<code>DataColumn</code>][DataColumn] of [<code>Boolean</code>][Boolean] values indicating whether each element
 * in this column lies between [<code>left</code>][left] and [<code>right</code>][right].
 *
 * If [<code>includeBoundaries</code>][includeBoundaries] is `true` (default), values equal to [<code>left</code>][left] or [<code>right</code>][right] are also considered in range.
 *
 * For more information: [See `between` on the documentation website.](https://kotlin.github.io/dataframe/between.html)
 *
 * @param left The lower boundary of the range.
 * @param right The upper boundary of the range.
 * @param includeBoundaries Whether to include [<code>left</code>][left] and [<code>right</code>][right] values in the range check. Defaults to `true`.
 * @return A [<code>DataColumn</code>][DataColumn] of [<code>Boolean</code>][Boolean] values where each element indicates if the corresponding
 *         value is within the specified range.
 */
public fun <T : Comparable<T>> DataColumn<T>.between(
    left: T,
    right: T,
    includeBoundaries: Boolean = true,
): DataColumn<Boolean> = map { it.between(left, right, includeBoundaries) }

// endregion
