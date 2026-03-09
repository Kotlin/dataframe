package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

// File with KDF common KDoc-topics and -snippets.

/*
 * Auto-renaming in [DataFrame] KDoc-topic.
 * Link to it with `@include [AutoRenamingLink]`.
 */

/**
 * ## Auto-renaming in [DataFrame]
 *
 * In some operations, multiple columns with the same name may appear
 * in the resulting [DataFrame].
 *
 * In such cases, columns with duplicate names are automatically renamed
 * using the pattern `"$name$n"`, where `name` is the original column name
 * and `n` is a unique index (1, 2, 3, and so on);
 * the first time the name of the column is encountered, no number is appended.
 *
 * It is recommended to [rename][org.jetbrains.kotlinx.dataframe.api.rename] them
 * to maintain clarity and improve code readability.
 */
internal typealias AutoRenamingColumnsInDataFrame = Nothing

/*
 * Column Path auto creation KDoc-snippet.
 * Include it in KDoc with `@include [ColumnPathCreationSnippet]`.
 */
