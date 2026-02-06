package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import kotlin.reflect.KProperty

/**
 * ## Selecting Columns
 * Selecting columns for various operations (including but not limited to
 * [DataFrame.select], [DataFrame.update], [DataFrame.gather], and [DataFrame.fillNulls])
 * can be done in the following ways:
 * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample]
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`<code>`operation`</code>` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`<code>`operation`</code>`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
 * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`<code>`operation`</code>`("length", "age")`
 *
 * ### 3. [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample]
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`<code>`operation`</code>`(length, age)`
 *
 * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`<code>`operation`</code>`(Person::length, Person::age)`
 *
 */
internal interface SelectingColumns {

    // Using <code>` notation to not create double `` when including

    /**
     * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This DSL is initiated by a [Columns Selector][ColumnsSelector] lambda,
     * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [SingleColumn] or [ColumnSet] (so, a [ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     * This also allows you to use [Extension Properties API][ExtensionPropertiesAPIDocs]
     * for type- and name-safe columns selection.
     *
     * #### NOTE:
     * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Columns Selector][ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference] first, for instance
     * with a function like [`col("name")`][ColumnsSelectionDsl.col].
     *
     * ### Check out: [Columns Selection DSL Grammar][ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     */
    interface Dsl {

        /**
         * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
         * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
         *
         * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
         * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
         * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into one or more columns.
         * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
         * for type- and name-safe columns selection.
         *
         * #### NOTE:
         * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
         * in this DSL directly with any function, they are NOT valid return types for the
         * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
         * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
         *
         * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
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
        typealias WithExample = Nothing
    }

    /**
     * Select or express a single column using the Column Selection DSL.
     * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This DSL is initiated by a [Column Selector][ColumnSelector] lambda,
     * which operates in context of the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
     * expects you to return a [SingleColumn].
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into a single column.
     *
     * #### NOTE:
     * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Column Selector][ColumnSelector]/[Columns Selector][ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference] first, for instance
     * with a function like [`col("name")`][ColumnsSelectionDsl.col].
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     */
    interface DslSingle {

        /**
         * Select or express a single column using the Column Selection DSL.
         * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
         *
         * This DSL is initiated by a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda,
         * which operates in context of the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] and
         * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
         * This is an entity formed by calling any (combination) of the functions
         * in the DSL that is or can be resolved into a single column.
         *
         * #### NOTE:
         * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
         * in this DSL directly with any function, they are NOT valid return types for the
         * [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]/[Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
         * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
         *
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;
         *
         * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
         *
         * #### For example:
         *
         * `df.`<code>`operation`</code>` { length }`
         *
         * `df.`<code>`operation`</code>`  {  `[col][ColumnsSelectionDsl.col]`(1) }`
         *
         * `df.`<code>`operation`</code>`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>().`[first][ColumnsSelectionDsl.first]`() }`
         *
         */
        typealias WithExample = Nothing
    }

    /**
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     */
    interface ColumnNames {

        /**
         * Select columns using their [column names][String]
         * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
         *
         * #### For example:
         *
         * `df.`<code>`operation`</code>`("length", "age")`
         *
         */
        typealias WithExample = Nothing
    }

    /**
     * Select columns using [column accessors][ColumnReference]
     * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
     */
    interface ColumnAccessors {

        /**
         * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
         * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
         *
         * #### For example:
         *
         * `val length by `[column][column]`<`[Double][Double]`>()`
         *
         * `val age by `[column][column]`<`[Double][Double]`>()`
         *
         * `df.`<code>`operation`</code>`(length, age)`
         *
         */
        typealias WithExample = Nothing
    }

    /** Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]). */
    interface KProperties {

        /**
         * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
         *
         * #### For example:
         * ```kotlin
         * data class Person(val length: Double, val age: Double)
         * ```
         *
         * `df.`<code>`operation`</code>`(Person::length, Person::age)`
         *
         */
        typealias WithExample = Nothing
    }
}
