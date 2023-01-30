package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*

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

    /**
     * The column selector DSL (Any {@include [AccessApiLink]}).
     * See {@include [ColumnSelectionDslLink]} for more details.
     *
     * For example:
     * ```kotlin
     * df.select { length and age }
     * df.select { cols(1..5) }
     * df.select { colsOf<Double>() }
     * ```
     */
    interface Dsl

    /**
     * Column names ({@include [AccessApi.StringApiLink]}).
     *
     * For example:
     * ```kotlin
     * df.select("length", "age")
     * df.select(listOf("length", "age"))
     * ```
     */
    interface ColumnNames

    /**
     * Column accessors ({@include [AccessApi.ColumnAccessorsApiLink]}).
     *
     * For example:
     * ```kotlin
     * val length by column<Double>()
     * val age by column<Double>()
     * df.select(length, age)
     * df.select(listOf(length, age))
     * ```
     */
    interface ColumnAccessors

    /**
     * KProperties ({@include [AccessApi.KPropertiesApiLink]}).
     *
     * For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     *
     * df.select(Person::length, Person::age)
     * df.select(listOf(Person::length, Person::age))
     * ```
     */
    interface KProperties

}
