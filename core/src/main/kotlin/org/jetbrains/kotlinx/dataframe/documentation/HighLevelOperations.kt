package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import kotlin.reflect.KProperty

/** The key for an @arg that will define the operation name for the examples below. */
internal interface OperationArg

/** [Selecting Columns][SelectingColumns] */
internal interface SelectingColumnsLink

/**
 * Selecting columns for various operations (including but not limited to
 * [DataFrame.select], [DataFrame.update], [DataFrame.gather], and [DataFrame.fillNulls])
 * can be done in the following ways:
 * - {@include [Dsl]}
 * - {@include [ColumnNames]}
 * - {@include [ColumnAccessors]}
 * - {@include [KProperties]}
 */
internal interface SelectingColumns {

    /** {@arg [OperationArg] <operation>} */
    interface SetDefaultOperationArg

    /** Select columns using the {@include [ColumnSelectionDslLink]} (Any {@include [AccessApiLink]}).
     *
     * For example:
     * ```kotlin
     * df.{@includeArg [OperationArg]} { length and age }
     * df.{@includeArg [OperationArg]} { cols(1..5) }
     * df.{@includeArg [OperationArg]} { colsOf<Double>() }
     * ```
     * @include [SetDefaultOperationArg]
     */
    interface Dsl

    /** Select columns using their column names ({@include [AccessApi.StringApiLink]}).
     *
     * For example:
     * ```kotlin
     * df.{@includeArg [OperationArg]}("length", "age")
     * df.{@includeArg [OperationArg]}(listOf("length", "age"))
     * ```
     * @include [SetDefaultOperationArg]
     */
    interface ColumnNames

    /** Select columns using column accessors ({@include [AccessApi.ColumnAccessorsApiLink]}).
     *
     * For example:
     * ```kotlin
     * val length by column<Double>()
     * val age by column<Double>()
     * df.{@includeArg [OperationArg]}(length, age)
     * df.{@includeArg [OperationArg]}(listOf(length, age))
     * ```
     * @include [SetDefaultOperationArg]
     */
    interface ColumnAccessors

    /** Select columns using [KProperty]'s ({@include [AccessApi.KPropertiesApiLink]}).
     *
     * For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     *
     * df.{@includeArg [OperationArg]}(Person::length, Person::age)
     * df.{@includeArg [OperationArg]}(listOf(Person::length, Person::age))
     * ```
     * @include [SetDefaultOperationArg]
     */
    interface KProperties

}
