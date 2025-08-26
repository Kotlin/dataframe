package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.between

// region DataColumn

/**
 * Returns a [DataColumn] of [Boolean] values indicating whether each element
 * lies between [left] and [right].
 *
 * If [includeBoundaries] is `true` (default), values equal to [left] or [right] are also considered in range.
 *
 * @param left The lower boundary of the range.
 * @param right The upper boundary of the range.
 * @param includeBoundaries Whether to include [left] and [right] values in the range check. Defaults to `true`.
 * @return A [DataColumn] of [Boolean] values where each element indicates if the corresponding
 *         value is within the specified range.
 */
public fun <T : Comparable<T>> DataColumn<T>.between(
    left: T,
    right: T,
    includeBoundaries: Boolean = true,
): DataColumn<Boolean> = map { it.between(left, right, includeBoundaries) }

// endregion
