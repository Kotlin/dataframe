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
 * [<code>Predicate</code>][Predicate] is a lambda function expecting a [<code>Boolean</code>][Boolean] result given an instance of `T` as `it`.
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
 * [<code>Selector</code>][Selector] is a lambda function expecting an `R` result given an instance of `T` as context (`this` and `it`).
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
 * [<code>DataFrameExpression</code>][DataFrameExpression] is a lambda function expecting an `R` result given an instance of [<code>DataFrame</code>][DataFrame]`<T>` as context
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
 * [<code>RowExpression</code>][RowExpression] is a lambda function expecting an `R` result given an instance of [<code>DataRow</code>][DataRow]`<T>` as context
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
 * [<code>RowValueExpression</code>][RowValueExpression] is a lambda function expecting an `R` result given the value `it: C` and an
 * instance of [<code>DataRow</code>][DataRow]`<T>` as context (`this`). `R` can be selected or expressed.
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
 * [<code>RowColumnExpression</code>][RowColumnExpression] is a lambda function expecting an `R` result given an instance of [<code>DataRow</code>][DataRow]`<T>` as
 * `row` and [<code>DataColumn</code>][DataColumn]`<C>` as `col`. `R` can be selected or expressed.
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
 * [<code>ColumnExpression</code>][ColumnExpression] is a lambda function expecting an `R` result given an instance of [<code>DataColumn</code>][DataColumn]`<C>` as context
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
 * [<code>ColumnSelector</code>][ColumnSelector] is a lambda function expecting a [<code>SingleColumn</code>][SingleColumn]<`C`> result given an instance of [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl]`<T>`
 * as context (`this` and `it`). [<code>SingleColumn</code>][SingleColumn]`<C>` can be selected or expressed.
 *
 * See [<code>Columns Selection DSL</code>][ColumnsSelectionDsl] for more information.
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
 * [<code>ColumnsSelector</code>][ColumnsSelector] is a lambda function expecting a [<code>ColumnsResolver</code>][ColumnsResolver]<`C`> ([<code>SingleColumn</code>][SingleColumn]<`C`> or [<code>ColumnSet</code>][ColumnSet]<`C`>)
 * result given an instance of [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl]`<T>` as context (`this` and `it`).
 * [<code>ColumnsResolver</code>][ColumnsResolver]<`C`> can be selected or expressed.
 *
 * See [<code>Columns Selection DSL</code>][ColumnsSelectionDsl] for more information.
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
 * A lambda expression that evaluates a row of the [<code>DataFrame</code>][DataFrame]
 * and returns a [<code>Boolean</code>][Boolean] indicating whether the row should be included in the result.
 *
 * The lambda has access to the [<code>`DataRow<T>`</code>][DataRow] both as `this` and as `it`,
 * enabling concise and readable conditions.
 *
 * Commonly used in operations such as [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.filter],
 * [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.drop], and others.
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
 * [<code>ColumnFilter</code>][ColumnFilter] is a lambda function expecting a [<code>Boolean</code>][Boolean] result given an instance of [<code>DataColumn</code>][DataColumn]`<C>` as context
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
 * [<code>RowValueFilter</code>][RowValueFilter] is a lambda function expecting a [<code>Boolean</code>][Boolean] result given the value `it: C` and an instance
 * of [<code>DataRow</code>][DataRow]`<T>` as context (`this`).
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
