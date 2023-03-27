package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.fillNaNs

/**
 * ## `NaN`
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * You can also use [fillNaNs][fillNaNs] to replace `NaNs` in certain columns with a given value or expression
 * or [dropNaNs][dropNaNs] to drop rows with `NaNs` in them.
 *
 * @see NA
 */
internal interface NaN
