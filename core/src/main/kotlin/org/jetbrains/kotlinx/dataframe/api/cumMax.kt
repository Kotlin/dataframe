package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.cumMaxImpl

// Adapted from the cumulative-operation documentation in cumSum.kt.

/**
 * Computes the cumulative maximum from the first value to the last value in this [DataColumn].
 *
 * The result preserves the column name, size, element type, declared nullability, and default value.
 * Values must be self-comparable; no numeric conversion is performed.
 * Empty columns remain empty. On comparison ties, the first maximum is retained.
 * Floating-point signed zeros follow [Comparable] ordering: positive zero is greater than negative zero.
 *
 * When [skipNA\] is `true`, `null` and `NaN` are retained at their original positions and ignored
 * when computing subsequent maxima. When `false`, the first missing value is repeated at every
 * subsequent position. For example, `3, null, 5` becomes `3, null, 5` with skipping and
 * `3, null, null` without skipping.
 *
 * @param [skipNA\] Whether to skip `null` and `NaN` while computing subsequent maxima (default: `true`).
 * @return A new column containing the cumulative maxima.
 * @see [max]
 * @see [cumSum]
 * @see {@include [DocumentationUrls.CumMaxOnColumn]}
 */
@ExcludeFromSources
private interface CumMaxDocs

/** @include [CumMaxDocs] */
public fun <T : Comparable<T>> DataColumn<T>.cumMax(skipNA: Boolean = true): DataColumn<T> = cumMaxImpl(skipNA).cast()

/** @include [CumMaxDocs] */
@JvmName("cumMaxNullable")
public fun <T : Comparable<T>> DataColumn<T?>.cumMax(skipNA: Boolean = true): DataColumn<T?> = cumMaxImpl(skipNA)
