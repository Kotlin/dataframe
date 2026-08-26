package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataColumn

/**
 * Returns an [Iterable] over the values of this [DataColumn].
 *
 * For more information: [See `asIterable` on the documentation website.](https://kotlin.github.io/dataframe/asiterable.html)
 *
 * @see [asSequence]
 */
public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

// endregion
