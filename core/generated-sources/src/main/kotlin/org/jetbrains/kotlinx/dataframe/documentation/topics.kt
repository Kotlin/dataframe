package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

// File with KDF common KDoc-topics.
// Don't exclude from sources!!!

/**
 *
 *
 * ## Auto-renaming columns in [DataFrame]
 *
 * [DataFrame] cannot contain columns with duplicate names.
 * However, sometimes after reading dataframes from sources or
 * after some operations, columns with duplicate names may appear in the result.
 *
 * In such cases, columns with duplicate names are automatically renamed in the resulting [DataFrame]
 * using the pattern `"$name$n"`, where `name` is the original column name
 * and `n` is a unique index (1, 2, 3, and so on);
 * the first time the name of the column is encountered, no number is appended:
 *
 * Before:
 *
 * | `name` | `name` | `name` |
 * |--------|--------|--------|
 * | `"a"`  | `"b"`  | `"c"`  |
 *
 * After:
 *
 * | `name` | `name1` | `name2` |
 * |---------|---------|---------|
 * | `"a"` | `"b"` | `"c"` |
 *
 * It is recommended to [rename][org.jetbrains.kotlinx.dataframe.api.rename] them
 * to maintain clarity and improve code readability.
 */
internal typealias AutoRenamingColumnsInDataFrame = Nothing
