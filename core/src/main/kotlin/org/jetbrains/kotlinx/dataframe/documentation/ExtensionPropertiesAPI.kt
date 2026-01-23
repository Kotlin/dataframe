package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ## Extension Properties API
 *
 * When working with a [DataFrame], the most convenient and reliable way to access its columns —
 * including for operations and retrieving column values in row expressions — is through auto-generated extension properties.
 *
 * These properties are generated based on the
 * [DataFrame schema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema],
 * with their names and types inferred from the names and types of the corresponding columns.
 * This also works for hierarchical [DataFrame] structures (i.e., column groups).
 *
 * ### Example
 *
 * Given the following [DataFrame]:
 *
 * | name | age | height |
 * |-------|-----|--------|
 * | Alice | 23  | 175.5  |
 * | Bob   | 27  | 160.2  |
 *
 * You can access columns using extension properties in a type-safe way, avoiding typos and relying on autocompletion.
 * These properties can be used in:
 * - [Columns Selection DSL][SelectingColumns.Dsl.WithExample]
 * - [DataRow Expressions][ExpressionsGivenRow]
 *
 * ```kotlin
 * // Access the "name" column
 * df.name
 *
 * // Select the "age" and "height" columns
 * df.select { age and height }
 *
 * // Filter rows where "age" > 18 and "name" starts with 'A'
 * df.filter { age > 18 && name.startsWith("A") }
 * ```
 *
 * For more information, see: {@include [DocumentationUrls.ExtensionPropertiesApi]}
 */
internal typealias `Extension Properties API` = Nothing
