package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.fillNaNs

/**
 *
 *
 * ## `NaN`
 *
 * [<code>Floats</code>][Float] or [<code>Doubles</code>][Double] can be represented as [<code>Float.NaN</code>][Float.NaN] or [<code>Double.NaN</code>][Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * You can also use [<code>fillNaNs</code>][fillNaNs] to replace `NaNs` in certain columns with a given value or expression
 * or [<code>dropNaNs</code>][dropNaNs] to drop rows with `NaNs` in them.
 *
 * For more information: [See `NaN` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html#nan)
 *
 * @see [NA]
 */
internal typealias NaN = Nothing
