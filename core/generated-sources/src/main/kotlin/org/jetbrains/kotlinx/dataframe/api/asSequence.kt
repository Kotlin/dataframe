package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataColumn

/**
 * Returns a [Sequence] over the values of this [DataColumn].
 *
 * For more information: [See `asSequence` on the documentation website.](https://kotlin.github.io/dataframe//assequencecolumn.html)
 *
 * @see [asIterable]
 */
public fun <T> DataColumn<T>.asSequence(): Sequence<T> = asIterable().asSequence()

// endregion

// region DataFrame

/**
 * Returns a [Sequence] of [DataRow] over this [DataFrame].
 *
 * For more information: [See `asSequence` on the documentation website.](https://kotlin.github.io/dataframe//assequence.html)
 */
public fun <T> DataFrame<T>.asSequence(): Sequence<DataRow<T>> = rows().asSequence()

// endregion
