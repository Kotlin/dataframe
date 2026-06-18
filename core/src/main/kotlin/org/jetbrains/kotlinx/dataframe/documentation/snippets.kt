@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions

// File with KDF common KDoc-snippets.

/** [Auto-renaming columns in DataFrame][AutoRenamingColumnsInDataFrame] */
internal typealias AutoRenamingLink = Nothing

/**
 * Note that if input dataframe contains duplicate column names,
 * they will be [automatically renamed][AutoRenamingColumnsInDataFrame]
 * in the resulting [DataFrame].
 */
internal typealias AutoRenameInputSnippet = Nothing

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
 * kotlinx-datetime [lacks localization support](https://github.com/Kotlin/kotlinx-datetime/discussions/253).
 *
 * If you need to provide a custom [java.util.Locale], we recommend parsing
 * to a [java.time]-based class first by adjusting the parser options before converting it to [kotlinx.datetime].
 *
 * See also: [ParserOptions], [DataFrame.parser.dateTimeLibrary][GlobalParserOptions.dateTimeLibrary]
 */
internal typealias KotlinxDateTimeLocaleSnippet = Nothing
