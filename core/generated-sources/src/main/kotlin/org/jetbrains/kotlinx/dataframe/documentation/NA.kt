package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.fillNA

/**
 * ## `NA`
 * `NA` in Dataframe can be seen as "[NaN] or `null`".
 *
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * A [DataRow] can also be considered `NA` if each value inside is `NA`.
 *
 * A [DataFrame] is considered `NA` if it has no rows or columns, so if it's empty.
 *
 * You can also use [fillNA][fillNA] to replace `NAs` in certain columns with a given value or expression
 * or [dropNA][dropNA] to drop rows with `NAs` in them.
 *
 * For more information: [See `NA` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html#na)
 *
 * @see NaN
 */
internal typealias NA = Nothing
