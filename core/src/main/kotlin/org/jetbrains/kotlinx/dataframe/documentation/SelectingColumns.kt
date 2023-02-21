package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import kotlin.reflect.KProperty

/** {@comment
 * In this file we provide documentation for high-level operations such as
 * the operation of selecting columns.
 * }
 */

/** [Selecting Columns][SelectingColumns] */
internal interface SelectingColumnsLink

/**
 * ## Selecting Columns
 * Selecting columns for various operations (including but not limited to
 * [DataFrame.select], [DataFrame.update], [DataFrame.gather], and [DataFrame.fillNulls])
 * can be done in the following ways:
 * - {@include [Dsl.WithExample]}
 * - {@include [ColumnNames.WithExample]}
 * - {@include [ColumnAccessors.WithExample]}
 * - {@include [KProperties.WithExample]}
 */
internal interface SelectingColumns {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@arg [OperationArg] operation} */
    interface SetDefaultOperationArg

    /** Select or express columns using the Column(s) Selection DSL.
     * (Any {@include [AccessApiLink]}).
     *
     * This DSL comes in the form of either a [Column Selector][ColumnSelector]- or [Columns Selector][ColumnsSelector] lambda,
     * which operate in the {@include [ColumnSelectionDslLink]} or the {@include [ColumnsSelectionDslLink]} and
     * expect you to return a [SingleColumn] or [ColumnSet], respectively.
     */
    interface Dsl {

        /** {@include [Dsl]}
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}` { length `[and][ColumnsSelectionDsl.and]` age }`{@comment TODO this links up like "kotlin.String.and"}
         *
         * `df.`{@includeArg [OperationArg]}` { `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * `df.`{@includeArg [OperationArg]}` { `[colsOf][colsOf]`<Double>() }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** Select columns using their column names
     * ({@include [AccessApi.StringApiLink]}).
     */
    interface ColumnNames {

        /** {@include [ColumnNames]}
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}`("length", "age")`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** Select columns using column accessors
     * ({@include [AccessApi.ColumnAccessorsApiLink]}).
     */
    interface ColumnAccessors {

        /** {@include [ColumnAccessors]}
         *
         * For example:
         *
         * `val length by `[column]`<Double>()`
         *
         * `val age by `[column]`<Double>()`
         *
         * `df.`{@includeArg [OperationArg]}`(length, age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** Select columns using [KProperties][KProperty] ({@include [AccessApi.KPropertiesApiLink]}). */
    interface KProperties {

        /** {@include [KProperties]}
         *
         * For example:
         * ```kotlin
         * data class Person(val length: Double, val age: Double)
         * ```
         *
         * `df.`{@includeArg [OperationArg]}`(Person::length, Person::age)`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }
}
