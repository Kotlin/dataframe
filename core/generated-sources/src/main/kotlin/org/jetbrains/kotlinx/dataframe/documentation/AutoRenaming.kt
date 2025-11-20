package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ## Auto-renaming in [DataFrame]
 *
 * In some operations, multiple columns with the same name may appear
 * in the resulting [DataFrame].
 *
 * In such cases, columns with duplicate names are automatically renamed
 * using the pattern `"$name$n"`, where `name` is the original column name
 * and `n` is a unique index (1, 2, 3, and so on); the first name column goes without a number.
 *
 * It is recommended to [rename][org.jetbrains.kotlinx.dataframe.api.rename] them
 * to maintain clarity and improve code readability.
 */
internal interface AutoRenaming
