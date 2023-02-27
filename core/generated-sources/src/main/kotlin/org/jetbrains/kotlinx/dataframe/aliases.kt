package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/**
 * [Predicate] is used to reach a [Boolean] result using the given instance of `T` as `it`.
 *
 * Shorthand for:
 * ```kotlin
 * (it: T) -> Boolean
 * ```
 */
public typealias Predicate<T> = (it: T) -> Boolean

/**
 * [Selector] is used to express or select any instance of `R` using the given instance of `T` as `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * T.(it: T) -> R
 * ```
 */
public typealias Selector<T, R> = T.(it: T) -> R

// region selectors

/**
 * [DataFrameExpression] is used to express or select any instance of `R` using the given instance of [DataFrame]`<T>`
 * as `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * DataFrame<T>.(it: DataFrame<T>) -> R
 * ```
 */
public typealias DataFrameExpression<T, R> = Selector<DataFrame<T>, R>

/**
 * [RowExpression] is used to express or select any instance of `R` using the given instance of [DataRow]`<T>` as
 * `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: DataRow<T>) -> R
 * ```
 */
public typealias RowExpression<T, R> = Selector<DataRow<T>, R>

/**
 * [RowValueExpression] is used to express or select any instance of `R` using the given value `it: C` and the given
 * instance of [DataRow]`<T>` as `this`.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: C) -> R
 * ```
 */
public typealias RowValueExpression<T, C, R> = DataRow<T>.(it: C) -> R

/**
 * [RowColumnExpression] is used to express or select any instance of `R` using the given instances of
 * [DataRow]`<T>` as `row` and [DataColumn]`<C>` as `col`.
 *
 * Shorthand for:
 * ```kotlin
 * (row: DataRow<T>, col: DataColumn<C>) -> R
 * ```
 */
public typealias RowColumnExpression<T, C, R> = (row: DataRow<T>, col: DataColumn<C>) -> R

/**
 * [ColumnExpression] is used to express or select any instance of `R` using the given instance of [DataColumn]`<C>` as
 * `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * DataColumn<C>.(it: DataColumn<C>) -> R
 * ```
 */
public typealias ColumnExpression<C, R> = Selector<DataColumn<C>, R>

/**
 * [ColumnSelector] is used to express or select a single column, represented by [SingleColumn]`<C>`, using the
 * context of [ColumnsSelectionDsl]`<T>` as `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * ColumnsSelectionDsl<T>.(it: ColumnsSelectionDsl<T>) -> SingleColumn<C>
 * ```
 */
public typealias ColumnSelector<T, C> = Selector<ColumnsSelectionDsl<T>, SingleColumn<C>>

/**
 * [ColumnsSelector] is used to express or select multiple columns, represented by [ColumnSet]`<C>`, using the
 * context of [ColumnsSelectionDsl]`<T>` as `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * ColumnsSelectionDsl<T>.(it: ColumnsSelectionDsl<T>) -> ColumnSet<C>
 * ```
 */
public typealias ColumnsSelector<T, C> = Selector<ColumnsSelectionDsl<T>, ColumnSet<C>>

// endregion

// region filters

/**
 * [RowFilter] is used to filter or find rows using the given instance of [DataRow]`<T>` as `this` and `it`.
 * Return `true` if the row should be included in the result.
 *
 * Shorthand for:
 * ```kotlin
 * DataRow<T>.(it: DataRow<T>) -> Boolean
 * ```
 */
public typealias RowFilter<T> = RowExpression<T, Boolean>

/**
 * [ColumnFilter] is used to filter or find columns using the given instance of [ColumnWithPath]`<T>` as `it`.
 * Return `true` if the column should be included in the result.
 *
 * Shorthand for:
 * ```kotlin
 * (it: ColumnWithPath<T>) -> Boolean
 * ```
 */
public typealias ColumnFilter<T> = Predicate<ColumnWithPath<T>>

/**
 * [RowValueFilter] is used to filter or find rows using the given value of `it: C` and the given instance of
 * [DataRow]`<T>` as `this`.
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

public typealias Column = ColumnReference<*>

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
