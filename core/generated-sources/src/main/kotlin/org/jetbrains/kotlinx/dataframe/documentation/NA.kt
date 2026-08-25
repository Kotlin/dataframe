package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.fillNA

/**
 *
 *
 * ## `NA`
 * `NA` in Dataframe can be seen as "[<code>NaN</code>][NaN] or `null`".
 *
 * [<code>Floats</code>][Float] or [<code>Doubles</code>][Double] can be represented as [<code>Float.NaN</code>][Float.NaN] or [<code>Double.NaN</code>][Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * A [<code>DataRow</code>][DataRow] can also be considered `NA` if each value inside is `NA`.
 *
 * A [<code>DataFrame</code>][DataFrame] is considered `NA` if it has no rows or columns, so if it's empty.
 *
 * You can also use [<code>fillNA</code>][fillNA] to replace `NAs` in certain columns with a given value or expression
 * or [<code>dropNA</code>][dropNA] to drop rows with `NAs` in them.
 *
 * For more information: [See `NA` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html#na)
 *
 * @see [NaN]
 */
internal typealias NA = Nothing
