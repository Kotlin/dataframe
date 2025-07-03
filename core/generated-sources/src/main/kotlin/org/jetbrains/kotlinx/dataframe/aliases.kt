package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/**
 * ## Predicate
 *
 * [Predicate] is a lambda function expecting a [Boolean] result given an instance of `T` as `it`.
 *
 * Shorthand for:
 * ```kotlin
 * (it: T) -> Boolean
 * ```
 */
public typealias Predicate<T> = (it: T) -> Boolean

/**
 * ## Selector
 *
 * [Selector] is a lambda function expecting an `R` result given an instance of `T` as context (`this` and `it`).
 *
 * Shorthand for:
 * ```kotlin
 * T.(it: T) -> R
 * ```
 */
public typealias Selector<T, R> = T.(it: T) -> R

// region selectors

/**
 * ## DataFrame Expression
 *
 * [DataFrameExpression] is a lambda function expecting an `R` result given an instance of [DataFrame]`<T>` as context
 * (`this` and `it`).
 * `R` can be selected or expressed.
 *
 * Shorthand for:
 * ```kotlin
 * DataFrame<T>.(it: DataFrame<T>) -> R
 * ```
 */
public typealias DataFrameExpression<T, R> = Selector<DataFrame<T>, R>

/**
 * ## Row Expression
 *
 * [RowExpression] is a lambda function expecting an `R` result given an instance of [DataRow]`<T>` as context
 * (`this` and `it`). `R` can be selected or expressed.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: DataRow<T>) -> R
 * ```
 */
public typealias RowExpression<T, R> = Selector<DataRow<T>, R>

/**
 * ## Row Value Expression
 *
 * [RowValueExpression] is a lambda function expecting an `R` result given the value `it: C` and an
 * instance of [DataRow]`<T>` as context (`this`). `R` can be selected or expressed.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: C) -> R
 * ```
 */
public typealias RowValueExpression<T, C, R> = DataRow<T>.(it: C) -> R

/**
 * ## Row Column Expression
 *
 * [RowColumnExpression] is a lambda function expecting an `R` result given an instance of [DataRow]`<T>` as
 * `row` and [DataColumn]`<C>` as `col`. `R` can be selected or expressed.
 *
 * Shorthand for:
 * ```kotlin
 * (row: DataRow<T>, col: DataColumn<C>) -> R
 * ```
 */
public typealias RowColumnExpression<T, C, R> = (row: DataRow<T>, col: DataColumn<C>) -> R

/**
 * ## Column Expression
 *
 * [ColumnExpression] is a lambda function expecting an `R` result given an instance of [DataColumn]`<C>` as context
 * (`this` and `it`). `R` can be selected or expressed.
 *
 * Shorthand for:
 * ```kotlin
 * DataColumn<C>.(it: DataColumn<C>) -> R
 * ```
 */
public typealias ColumnExpression<C, R> = Selector<DataColumn<C>, R>

/**
 * ## Column Selector
 *
 * [ColumnSelector] is a lambda function expecting a [SingleColumn]<`C`> result given an instance of [ColumnsSelectionDsl]`<T>`
 * as context (`this` and `it`). [SingleColumn]`<C>` can be selected or expressed.
 *
 * See [Columns Selection DSL][ColumnsSelectionDsl] for more information.
 *
 * Shorthand for:
 * ```kotlin
 * ColumnsSelectionDsl<T>.(it: ColumnsSelectionDsl<T>) -> SingleColumn<C>
 * ```
 */
public typealias ColumnSelector<T, C> = Selector<ColumnsSelectionDsl<T>, SingleColumn<C>>

/**
 * ## Columns Selector
 *
 * [ColumnsSelector] is a lambda function expecting a [ColumnsResolver]<`C`> ([SingleColumn]<`C`> or [ColumnSet]<`C`>)
 * result given an instance of [ColumnsSelectionDsl]`<T>` as context (`this` and `it`).
 * [ColumnsResolver]<`C`> can be selected or expressed.
 *
 * See [Columns Selection DSL][ColumnsSelectionDsl] for more information.
 *
 * Shorthand for:
 * ```kotlin
 * ColumnsSelectionDsl<T>.(it: ColumnsSelectionDsl<T>) -> ColumnsResolver<C>
 * ```
 */
public typealias ColumnsSelector<T, C> = Selector<ColumnsSelectionDsl<T>, ColumnsResolver<C>>

// endregion

// region filters

/**
 * A lambda expression that evaluates a row of the [DataFrame]
 * and returns a [Boolean] indicating whether the row should be included in the result.
 *
 * The lambda has access to the [`DataRow<T>`][DataRow] both as `this` and as `it`,
 * enabling concise and readable conditions.
 *
 * Commonly used in operations such as [filter][org.jetbrains.kotlinx.dataframe.api.filter],
 * [drop][org.jetbrains.kotlinx.dataframe.api.drop], and others.
 *
 * Equivalent to:
 * ```kotlin
 * DataRow<T>.(it: DataRow<T>) -> Boolean
 * ```
 */
public typealias RowFilter<T> = RowExpression<T, Boolean>

/**
 * ## Column Filter
 *
 * [ColumnFilter] is a lambda function expecting a [Boolean] result given an instance of [DataColumn]`<C>` as context
 * (`this` and `it`).
 *
 * Return `true` if the column should be included in the result.
 *
 * Shorthand for:
 * ```kotlin
 * (it: ColumnWithPath<T>) -> Boolean
 * ```
 */
public typealias ColumnFilter<T> = Predicate<ColumnWithPath<T>>

/**
 * ## Row Value Filter
 *
 * [RowValueFilter] is a lambda function expecting a [Boolean] result given the value `it: C` and an instance
 * of [DataRow]`<T>` as context (`this`).
 *
 * Return `true` if the row should be included in the result.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: C) -> Boolean
 * ```
 */
public typealias RowValueFilter<T, C> = RowValueExpression<T, C, Boolean>

// endregion

// region columns

public typealias AnyColumnReference = ColumnReference<*>

public typealias ColumnGroupReference = ColumnReference<AnyRow>
public typealias ColumnGroupAccessor<T> = ColumnAccessor<DataRow<T>>
public typealias AnyColumnGroupAccessor = ColumnGroupAccessor<*>

public typealias DoubleCol = DataColumn<Double?>
public typealias BooleanCol = DataColumn<Boolean?>
public typealias IntCol = DataColumn<Int?>
public typealias NumberCol = DataColumn<Number?>
public typealias StringCol = DataColumn<String?>
public typealias AnyCol = DataColumn<*>

// endregion

// region Any*

public typealias AnyFrame = DataFrame<*>

public typealias AnyRow = DataRow<*>

public typealias AnyBaseCol = BaseColumn<*>

// endregion
