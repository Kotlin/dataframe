package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame

/*
 * Access APIs anchor.
 * Link to here whenever you want to explain the different access APIs
 * with [AccessApiLink].
 */

/**
 * ## Access APIs
 *
 * Accessing column is the one of the most important parts of the API,
 * used in the most of [DataFrame] operations.
 *
 * In the Kotlin DataFrame library, we provide two different ways to access columns —
 * the {@include [StringApiLink]} and the {@include [ExtensionPropertiesApiLink]}.
 *
 * For more information: {@include [DocumentationUrls.AccessApis]}
 *
 * @comment We can link to here whenever we want to explain the different access APIs.
 */
@Suppress("ClassName")
internal interface `Access APIs` {

    /*
     * String API anchor.
     * Link with [StringApiLink].
     */

    /**
     * ## String API
     *
     * In this [`Access APIs`], columns are accessed by a [String] representing their name.
     * Type-checking is done at runtime, name-checking too.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.StringApi]}
     *
     * For example: {@comment This works if you include the test module when running KoDEx}
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.strings]
     */
    typealias `String API` = Nothing

    /** [String API][`String API`] */
    typealias StringApiLink = Nothing

    /*
     * Extension Properties API anchor.
     * Link with [ExtensionPropertiesApiLink].
     */

    /**
     * ## Extension Properties API
     *
     * When working with a [DataFrame], the most convenient and reliable way to [access its columns][`Access APIs`] —
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
     * For more information: {@include [DocumentationUrls.AccessApis.ExtensionPropertiesApi]}
     */
    typealias `Extension Properties API` = Nothing

    /** [Extension Properties API][`Extension Properties API`] */
    typealias ExtensionPropertiesApiLink = Nothing
}

/** [Access APIs][`Access APIs`] */
internal typealias AccessApiLink = Nothing
