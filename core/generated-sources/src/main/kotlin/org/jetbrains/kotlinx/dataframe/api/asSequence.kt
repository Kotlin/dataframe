package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

// region DataColumn

/**
 * Returns a [Sequence] over the values of this [DataColumn].
 *
 * @see [asIterable]
 */
public fun <T> DataColumn<T>.asSequence(): Sequence<T> = asIterable().asSequence()

// endregion

// region DataFrame

/**
 * Returns a [Sequence] of [DataRow] over this [DataFrame].
 */
public fun <T> DataFrame<T>.asSequence(): Sequence<DataRow<T>> = rows().asSequence()

// endregion
