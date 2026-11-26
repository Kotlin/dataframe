package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/**
 *
 *
 * ## Selecting Columns
 *
 * Selecting columns for various [<code>DataFrame</code>][DataFrame] operations
 * can be done in the following ways:
 * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`<code>`operation`</code>` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
 * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`<code>`operation`</code>`("length", "age")`
 *
 *
 *
 */
internal interface SelectingColumns {

    // Using <code>` notation to not create double `` when including

    /**
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][SingleColumn] or [<code>ColumnSet</code>][ColumnSet] (so, a [<code>ColumnsResolver</code>][ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     */
    interface ColumnsSelectionDsl {

        /**
         *
         *
         *
         *
         * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
         *
         * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
         * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
         * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into one or more columns.
         *
         * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
         * for specifying columns type- and name-safe.
         *
         * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;
         *
         * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
         *
         * #### For example:
         *
         * <code>`df`</code>`.`<code>`operation`</code>` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
         *
         *
         *
         */
        typealias ColumnsSelectionDslWithExample = Nothing
    }

    /**
     *
     *
     * Select or express a single column using the Column Selection DSL.
     * (Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This DSL is initiated by a [<code>Column Selector</code>][ColumnSelector] lambda,
     * which operates in context of the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][SingleColumn].
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into a single column.
     *
     * The Column Selection DSL allows using [<code>Extension Properties</code>][AccessApis.ExtensionPropertiesApi]
     * for specifying column type- and name-safe.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     */
    interface ColumnSelectionDsl {

        /**
         *
         *
         *
         *
         * Select or express a single column using the Column Selection DSL.
         * (Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
         *
         * This DSL is initiated by a [<code>Column Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda,
         * which operates in context of the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
         * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into a single column.
         *
         * The Column Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
         * for specifying column type- and name-safe.
         *
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;
         *
         * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
         *
         * #### For example:
         *
         * <code>`df`</code>`.`<code>`operation`</code>` { length }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>col</code>][ColumnsSelectionDsl.col]`(1) }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>().`[<code>first</code>][ColumnsSelectionDsl.first]`() }`
         *
         *
         *
         */
        typealias ColumnsSelectionDslWithExample = Nothing
    }

    /**
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     */
    interface ColumnNamesApi {

        /**
         *
         *
         *
         *
         * Select single or multiple columns using their names as [<code>String</code>][String]s.
         * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
         *
         * #### For example:
         *
         * <code>`df`</code>`.`<code>`operation`</code>`("length", "age")`
         *
         *
         *
         */
        typealias ColumnNamesApiWithExample = Nothing
    }
}
