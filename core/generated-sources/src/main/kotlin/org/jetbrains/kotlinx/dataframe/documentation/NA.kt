package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.fillNA

/**
 * ## `NA`
 * `NA` in Dataframe can be seen as "[NaN] or `null`".
 *
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * You can also use [fillNA][fillNA] to replace `NAs` in certain columns with a given value or expression
 * or [dropNA][dropNA] to drop rows with `NAs` in them.
 *
 * @see NaN
 */
internal interface NA
