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

/** [Selecting Columns][SelectingColumns] */
@ExcludeFromSources
internal interface SelectingColumnsLink

/**
 * ## Selecting Columns
 * Selecting columns for various operations (including but not limited to
 * [DataFrame.select], [DataFrame.update], [DataFrame.gather], and [DataFrame.fillNulls])
 * can be done in the following ways:
 * ### 1. {@include [DslLink]}
 * {@include [Dsl.WithExample]}
 * #### NOTE: There's also a 'single column' variant used sometimes: {@include [DslSingleLink]}.
 * ### 2. {@include [ColumnNamesLink]}
 * {@include [ColumnNames.WithExample]}
 * ### 3. {@include [ColumnAccessorsLink]}
 * {@include [ColumnAccessors.WithExample]}
 * ### 4. {@include [KPropertiesLink]}
 * {@include [KProperties.WithExample]}
 */
internal interface SelectingColumns {

    /**
     * This can include [column groups][ColumnGroup] and nested columns.
     */
    @ExcludeFromSources
    interface ColumnGroupsAndNestedColumnsMention

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    @ExcludeFromSources
    interface OPERATION

    // Using <code>` notation to not create double `` when including

    /** {@set [OPERATION] <code>`operation`</code>} */
    @ExcludeFromSources
    interface SetDefaultOperationArg

    /**
     * Select or express columns using the {@include [ColumnsSelectionDslLink]}.
     * (Any (combination of) {@include [AccessApiLink]}).
     *
     * This DSL is initiated by a [Columns Selector][ColumnsSelector] lambda,
     * which operates in the context of the {@include [ColumnsSelectionDslLink]} and
     * expects you to return a [SingleColumn] or [ColumnSet] (so, a [ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     * This also allows you to use [Extension Properties API][ExtensionPropertiesAPIDocs]
     * for type- and name-safe columns selection.
     *
     * #### NOTE:
     * While you can use the {@include [AccessApi.StringApiLink]} and {@include [AccessApi.KPropertiesApiLink]}
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Columns Selector][ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference] first, for instance
     * with a function like [`col("name")`][ColumnsSelectionDsl.col].
     *
     * ### Check out: [Columns Selection DSL Grammar][ColumnsSelectionDsl.DslGrammar]
     * {@include [LineBreak]}
     * @include [DocumentationUrls.ColumnSelectors]
     */
    interface Dsl {

        /**
         * {@include [Dsl]}
         *
         * #### For example:
         *
         * `df.`{@get [OPERATION]}` { length `[and][ColumnsSelectionDsl.and]` age }`
         *
         * `df.`{@get [OPERATION]}`  {  `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * `df.`{@get [OPERATION]}`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
         *
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Columns Selection DSL][Dsl.WithExample] */
    @ExcludeFromSources
    interface DslLink

    /**
     * Select or express a single column using the Column Selection DSL.
     * (Any {@include [AccessApiLink]}).
     *
     * This DSL is initiated by a [Column Selector][ColumnSelector] lambda,
     * which operates in context of the {@include [ColumnSelectionDslLink]} and
     * expects you to return a [SingleColumn].
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into a single column.
     *
     * #### NOTE:
     * While you can use the {@include [AccessApi.StringApiLink]} and {@include [AccessApi.KPropertiesApiLink]}
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Column Selector][ColumnSelector]/[Columns Selector][ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference] first, for instance
     * with a function like [`col("name")`][ColumnsSelectionDsl.col].
     *
     * {@include [LineBreak]}
     * @include [DocumentationUrls.ColumnSelectors]
     */
    interface DslSingle {

        /**
         * {@include [DslSingle]}
         *
         * #### For example:
         *
         * `df.`{@get [OPERATION]}` { length }`
         *
         * `df.`{@get [OPERATION]}`  {  `[col][ColumnsSelectionDsl.col]`(1) }`
         *
         * `df.`{@get [OPERATION]}`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>().`[first][ColumnsSelectionDsl.first]`() }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column Selection DSL][DslSingle.WithExample] */
    @ExcludeFromSources
    interface DslSingleLink

    /**
     * Select columns using their [column names][String]
     * ({@include [AccessApi.StringApiLink]}).
     */
    interface ColumnNames {

        /**
         * {@include [ColumnNames]}
         *
         * #### For example:
         *
         * `df.`{@get [OPERATION]}`("length", "age")`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column names][ColumnNames.WithExample] */
    @ExcludeFromSources
    interface ColumnNamesLink

    /**
     * Select columns using [column accessors][ColumnReference]
     * ({@include [AccessApi.ColumnAccessorsApiLink]}).
     */
    interface ColumnAccessors {

        /**
         * {@include [ColumnAccessors]}
         *
         * #### For example:
         *
         * `val length by `[column][column]`<`[Double][Double]`>()`
         *
         * `val age by `[column][column]`<`[Double][Double]`>()`
         *
         * `df.`{@get [OPERATION]}`(length, age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column references][ColumnAccessors.WithExample] */
    @ExcludeFromSources
    interface ColumnAccessorsLink

    /** Select columns using [KProperties][KProperty] ({@include [AccessApi.KPropertiesApiLink]}). */
    interface KProperties {

        /**
         * {@include [KProperties]}
         *
         * #### For example:
         * ```kotlin
         * data class Person(val length: Double, val age: Double)
         * ```
         *
         * `df.`{@get [OPERATION]}`(Person::length, Person::age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [KProperties][KProperties.WithExample] */
    @ExcludeFromSources
    interface KPropertiesLink
}
