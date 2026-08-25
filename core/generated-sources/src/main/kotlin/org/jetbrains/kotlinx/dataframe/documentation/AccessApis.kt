package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 *
 *
 * ## Access APIs
 *
 * Accessing and specifying columns is the one of the most important parts of the API,
 * used in the most of [<code>DataFrame</code>][DataFrame] operations.
 *
 * In the Kotlin DataFrame library, we provide two different ways to access columns —
 * the [<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi] and the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi].
 *
 * For more information: [See Access APIs on the documentation website.](https://kotlin.github.io/dataframe/apilevels.html)
 */
internal interface AccessApis {

    /**
     *
     *
     * ## String API
     *
     * In this [<code>AccessApis</code>][AccessApis], columns are accessed by a [<code>String</code>][String] representing their name.
     * Type-checking and name-checking are done at runtime, too.
     *
     * ### String Column Accessors
     *
     * You can also specify a column using a [<code>String</code>][String] representing their name
     * and path inside the [<code>Columns Selection DSL</code>][SelectingColumns.ColumnsSelectionDsl] and
     * [<code>Row Expressions</code>][ExpressionsGivenRow].
     *
     * For more information: [See String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html)
     */
    typealias StringApi = Nothing

    /**
     *
     *
     * ## Extension Properties API
     *
     * When working with a [<code>DataFrame</code>][DataFrame], the most convenient and reliable way to [<code>access its columns</code>][AccessApis] —
     * including for operations and retrieving column values in row expressions —
     * is through auto-generated extension properties.
     *
     * These properties are generated based on the
     * [<code>dataframe schema</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema],
     * with their names and types inferred from the names and types of the corresponding columns.
     * This also works for hierarchical [<code>DataFrame</code>][DataFrame] structures
     * (i.e., [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * and [<code>frame columns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]).
     *
     * ### Example
     *
     * Given the following [<code>DataFrame</code>][DataFrame]:
     *
     * | name | age | height |
     * |-------|-----|--------|
     * | Alice | 23  | 175.5  |
     * | Bob   | 27  | 160.2  |
     *
     * You can access columns using extension properties in a type-safe way, avoiding typos and relying on autocompletion.
     * These properties can be used in:
     * - [<code>Columns Selection DSL</code>][SelectingColumns.ColumnsSelectionDsl]
     * - [<code>Row Expressions</code>][ExpressionsGivenRow]
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
     * For more information: [See Extension Properties API on the documentation website.](https://kotlin.github.io/dataframe/extensionpropertiesapi.html)
     */
    typealias ExtensionPropertiesApi = Nothing
}
