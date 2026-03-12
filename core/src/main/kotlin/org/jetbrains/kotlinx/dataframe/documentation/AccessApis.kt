package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * {@comment
 *    Access APIs KDoc-topic.
 *    Link to here whenever you want to explain the different access APIs
 *    with `@include [AccessApiLink]`.
 * }
 *
 * ## Access APIs
 *
 * Accessing and specifying columns is the one of the most important parts of the API,
 * used in the most of [DataFrame] operations.
 *
 * In the Kotlin DataFrame library, we provide two different ways to access columns —
 * the {@include [StringApiLink]} and the {@include [ExtensionPropertiesApiLink]}.
 *
 * For more information: {@include [DocumentationUrls.AccessApis]}
 */
internal interface AccessApis {

    /**
     * {@comment
     *    String API KDoc-topic.
     *    Link to it with `@include [StringApiLink]`.
     * }
     *
     * ## String API
     *
     * In this [AccessApis], columns are accessed by a [String] representing their name.
     * Type-checking and name-checking are done at runtime, too.
     *
     * ### String Column Accessors
     *
     * You can also specify a column using a [String] representing their name
     * and path inside the [Columns Selection DSL][SelectingColumns.ColumnsSelectionDsl] and
     * [Row Expressions][ExpressionsGivenRow].
     *
     * For more information: {@include [DocumentationUrls.AccessApis.StringApi]}
     */
    typealias StringAPI = Nothing

    /** [String API][`StringAPI`] */
    typealias StringApiLink = Nothing

    /**
     * {@comment
     *    Extension Properties API KDoc topic.
     *    Link to it with `@include [ExtensionPropertiesApiLink]`.
     * }
     *
     * ## Extension Properties API
     *
     * When working with a [DataFrame], the most convenient and reliable way to [access its columns][AccessApis] —
     * including for operations and retrieving column values in row expressions —
     * is through auto-generated extension properties.
     *
     * These properties are generated based on the
     * [dataframe schema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema],
     * with their names and types inferred from the names and types of the corresponding columns.
     * This also works for hierarchical [DataFrame] structures
     * (i.e., [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * and [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]).
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
     * - [Columns Selection DSL][SelectingColumns.ColumnsSelectionDsl]
     * - [Row Expressions][ExpressionsGivenRow]
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
     * For more information: {@include [DocumentationUrls.AccessApis.ExtensionPropertiesApi]}
     */
    typealias ExtensionPropertiesApi = Nothing

    /** [Extension Properties API][ExtensionPropertiesApi] */
    typealias ExtensionPropertiesApiLink = Nothing
}

/** [Access APIs][AccessApis] */
internal typealias AccessApiLink = Nothing
