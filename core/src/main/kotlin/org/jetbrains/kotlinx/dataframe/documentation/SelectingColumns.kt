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
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessorsLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingleLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KPropertiesLink
import kotlin.reflect.KProperty

/** [Selecting Columns][SelectingColumns] */
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
     * The key for an @setArg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@setArg [OperationArg] operation} */
    interface SetDefaultOperationArg

    /**
     * Select or express columns using the {@include [ColumnsSelectionDslLink]}.
     * (Any (combination of) {@include [AccessApiLink]}).
     *
     * This DSL comes in the form a [Columns Selector][ColumnsSelector] lambda,
     * which operates on the {@include [ColumnsSelectionDslLink]} and
     * expects you to return a [ColumnsResolver]; an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     * ### Check out: [Columns Selection DSL Usage][ColumnsSelectionDsl.Usage]
     * {@include [LineBreak]}
     * @include [DocumentationUrls.ColumnSelectors]
     */
    interface Dsl {

        /**
         * {@include [Dsl]}
         *
         * #### For example:
         *
         * `df.`{@getArg [OperationArg]}` { length `[and][ColumnsSelectionDsl.and]` age }`
         *
         * `df.`{@getArg [OperationArg]}` { `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * `df.`{@getArg [OperationArg]}` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
         *
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Columns Selection DSL][Dsl.WithExample] */
    interface DslLink

    /**
     * Select or express a single column using the Column Selection DSL.
     * (Any {@include [AccessApiLink]}).
     *
     * This DSL comes in the form of a [Column Selector][ColumnSelector] lambda,
     * which operates in the {@include [ColumnSelectionDslLink]} and
     * expects you to return a [SingleColumn].
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
         * `df.`{@getArg [OperationArg]}` { length }`
         *
         * `df.`{@getArg [OperationArg]}` { `[col][ColumnsSelectionDsl.col]`(1) }`
         *
         * `df.`{@getArg [OperationArg]}` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>().`[first][ColumnsSelectionDsl.first]`() }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column Selection DSL][DslSingle.WithExample] */
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
         * `df.`{@getArg [OperationArg]}`("length", "age")`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column names][ColumnNames.WithExample] */
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
         * `df.`{@getArg [OperationArg]}`(length, age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column references][ColumnAccessors.WithExample] */
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
         * `df.`{@getArg [OperationArg]}`(Person::length, Person::age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [KProperties][KProperties.WithExample] */
    interface KPropertiesLink
}
