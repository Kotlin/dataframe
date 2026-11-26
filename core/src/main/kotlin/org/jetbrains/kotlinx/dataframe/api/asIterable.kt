package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataColumn

/**
 * Returns an [Iterable] over the values of this [DataColumn].
 *
 * For more information: {@include [DocumentationUrls.AsIterable]}
 *
 * @see [asSequence]
 */
public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

// endregion
