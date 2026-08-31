package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.random.Random

// region DataColumn

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with the same values in random order using the provided [<code>random</code>][random] source.
 *
 * For more information: [See `shuffle` on the documentation website.](https://kotlin.github.io/dataframe/shuffle.html)
 *
 * @param [random] Source of randomness to ensure reproducible shuffles when needed.
 * @return A new [<code>DataColumn</code>][DataColumn] with values reordered randomly.
 */
public fun <T> DataColumn<T>.shuffle(random: Random): DataColumn<T> = get(indices.shuffled(random))

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with values in random order using the default randomness.
 *
 * For more information: [See `shuffle` on the documentation website.](https://kotlin.github.io/dataframe/shuffle.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with values reordered randomly.
 */
public fun <T> DataColumn<T>.shuffle(): DataColumn<T> = get(indices.shuffled())

// endregion

// region DataFrame

/**
 * Returns a new [<code>DataFrame</code>][DataFrame] with rows reordered randomly using the provided [<code>random</code>][random] source.
 *
 * For more information: [See `shuffle` on the documentation website.](https://kotlin.github.io/dataframe/shuffle.html)
 *
 * @param [random] Source of randomness to ensure reproducible shuffles when needed.
 * @return A new [<code>DataFrame</code>][DataFrame] with rows in random order.
 */
public fun <T> DataFrame<T>.shuffle(random: Random): DataFrame<T> = getRows(indices.shuffled(random))

/**
 * Returns a new [<code>DataFrame</code>][DataFrame] with rows in random order using the default randomness.
 *
 * For more information: [See `shuffle` on the documentation website.](https://kotlin.github.io/dataframe/shuffle.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with rows in random order.
 */
public fun <T> DataFrame<T>.shuffle(): DataFrame<T> = getRows(indices.shuffled())

// endregion
