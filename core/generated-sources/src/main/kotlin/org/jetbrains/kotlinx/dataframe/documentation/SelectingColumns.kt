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
 * Selecting columns for various [DataFrame] operations
 * can be done in the following ways:
 * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
 *
 *
 *
 *
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`<code>`operation`</code>` { length `[and][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 * > There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
 * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
 *
 *
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
     * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [Columns Selector][ColumnsSelector] lambda,
     * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [SingleColumn] or [ColumnSet] (so, a [ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * Check out: [Columns Selection DSL Grammar][ColumnsSelectionDsl.DslGrammar]
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
         * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
         *
         * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
         * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
         * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into one or more columns.
         *
         * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;
         *
         * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
         *
         * #### For example:
         *
         * <code>`df`</code>`.`<code>`operation`</code>` { length `[and][ColumnsSelectionDsl.and]` age }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
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
     * (Any [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This DSL is initiated by a [Column Selector][ColumnSelector] lambda,
     * which operates in context of the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
     * expects you to return a [SingleColumn].
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into a single column.
     *
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
         * (Any [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
         *
         * This DSL is initiated by a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda,
         * which operates in context of the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
         * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into a single column.
         *
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
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[col][ColumnsSelectionDsl.col]`(1) }`
         *
         * <code>`df`</code>`.`<code>`operation`</code>`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>().`[first][ColumnsSelectionDsl.first]`() }`
         *
         *
         *
         */
        typealias ColumnsSelectionDslWithExample = Nothing
    }

    /**
     *
     *
     * Select single or multiple columns using their names as [String]s.
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     */
    interface ColumnNamesApi {

        /**
         *
         *
         *
         *
         * Select single or multiple columns using their names as [String]s.
         * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
