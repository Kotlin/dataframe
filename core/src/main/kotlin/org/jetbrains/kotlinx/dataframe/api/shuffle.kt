package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.random.Random

// region DataColumn

/**
 * Returns a new [DataColumn] with the same values in random order using the provided [random] source.
 *
 * @param [random] Source of randomness to ensure reproducible shuffles when needed.
 * @return A new [DataColumn] with values reordered randomly.
 */
public fun <T> DataColumn<T>.shuffle(random: Random): DataColumn<T> = get(indices.shuffled(random))

/**
 * Returns a new [DataColumn] with values in random order using the default randomness.
 *
 * @return A new [DataColumn] with values reordered randomly.
 */
public fun <T> DataColumn<T>.shuffle(): DataColumn<T> = get(indices.shuffled())

// endregion

// region DataFrame

/**
 * Returns a new [DataFrame] with rows reordered randomly using the provided [random] source.
 *
 * @param [random] Source of randomness to ensure reproducible shuffles when needed.
 * @return A new [DataFrame] with rows in random order.
 */
public fun <T> DataFrame<T>.shuffle(random: Random): DataFrame<T> = getRows(indices.shuffled(random))

/**
 * Returns a new [DataFrame] with rows in random order using the default randomness.
 *
 * @return A new [DataFrame] with rows in random order.
 */
public fun <T> DataFrame<T>.shuffle(): DataFrame<T> = getRows(indices.shuffled())

// endregion
