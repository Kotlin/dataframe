package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.cumMinImpl

// Adapted from the cumulative-operation documentation in cumSum.kt.

/**
 * Computes the cumulative minimum from the first value to the last value in this [DataColumn].
 *
 * The result preserves the column name, size, element type, and declared nullability.
 * Values must be self-comparable; no numeric conversion is performed.
 * Empty columns remain empty. On comparison ties, the first minimum is retained.
 *
 * When [skipNA\] is `true`, `null` and `NaN` are retained at their original positions and ignored
 * when computing subsequent minima. When `false`, the first missing value is repeated at every
 * subsequent position. For example, `3, null, 1` becomes `3, null, 1` with skipping and
 * `3, null, null` without skipping.
 *
 * @param [skipNA\] Whether to skip `null` and `NaN` while computing subsequent minima (default: `true`).
 * @return A new column containing the cumulative minima.
 * @see [min]
 * @see [cumSum]
 * @see {@include [DocumentationUrls.CumMinOnColumn]}
 */
@ExcludeFromSources
private interface CumMinDocs

/** @include [CumMinDocs] */
public fun <T : Comparable<T>> DataColumn<T>.cumMin(skipNA: Boolean = true): DataColumn<T> = cumMinImpl(skipNA).cast()

/** @include [CumMinDocs] */
@JvmName("cumMinNullable")
public fun <T : Comparable<T>> DataColumn<T?>.cumMin(skipNA: Boolean = true): DataColumn<T?> = cumMinImpl(skipNA)
