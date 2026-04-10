@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.parser

// File with KDF common KDoc-topics and -snippets.

/**
 * {@comment
 *    Auto-renaming in [DataFrame] KDoc-topic.
 *    Link to it with `@include [AutoRenamingLink]`.
 * }
 *
 * ## Auto-renaming in [DataFrame]
 *
 * In some operations, multiple columns with the same name may appear
 * in the resulting [DataFrame].
 *
 * In such cases, columns with duplicate names are automatically renamed
 * using the pattern `"\$name\$n"`, where `name` is the original column name
 * and `n` is a unique index (1, 2, 3, and so on);
 * the first time the name of the column is encountered, no number is appended.
 *
 * It is recommended to [rename][org.jetbrains.kotlinx.dataframe.api.rename] them
 * to maintain clarity and improve code readability.
 */
internal typealias AutoRenamingColumnsInDataFrame = Nothing

/** [Auto-renaming columns in DataFrame][AutoRenamingColumnsInDataFrame] */
internal typealias AutoRenamingLink = Nothing

/**
 * {@comment
 *    Column Path auto creation KDoc-snippet.
 *    Include it in KDoc with `@include [ColumnPathCreationSnippet]`.
 * }
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 */
internal typealias ColumnPathCreationSnippet = Nothing

/**
 * kotlinx-datetime lacks localization support: https://github.com/Kotlin/kotlinx-datetime/discussions/253.
 *
 * If you need to provide a custom [java.util.Locale], we recommend parsing
 * to a [java.time]-based class first before converting it to [kotlinx.datetime].
 *
 * See also: [ParserOptions], [DataFrame.parser][DataFrame.Companion.parser]
 */
internal typealias KotlinxDateTimeLocaleSnippet = Nothing
