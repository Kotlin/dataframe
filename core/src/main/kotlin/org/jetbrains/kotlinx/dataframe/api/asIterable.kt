package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn

// region DataColumn

/**
 * Returns an [Iterable] over the values of this [DataColumn].
 *
 * @see [asSequence]
 */
public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

// endregion
