package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

public interface Many<out T> : List<T>

public typealias Predicate<T> = (T) -> Boolean

public typealias Selector<T, R> = T.(T) -> R

// region selectors

public typealias DataFrameExpression<T, R> = Selector<DataFrame<T>, R>

public typealias RowExpression<T, R> = Selector<DataRow<T>, R>

public typealias RowValueExpression<T, C, R> = DataRow<T>.(C) -> R

public typealias RowColumnExpression<T, C, R> = (DataRow<T>, DataColumn<C>) -> R

public typealias ColumnSelector<T, C> = Selector<ColumnsSelectionDsl<T>, SingleColumn<C>>

public typealias ColumnsSelector<T, C> = Selector<ColumnsSelectionDsl<T>, ColumnSet<C>>

// endregion

// region filters

public typealias RowFilter<T> = RowExpression<T, Boolean>

public typealias ColumnFilter<T> = (ColumnWithPath<T>) -> Boolean

public typealias RowValueFilter<T, C> = RowValueExpression<T, C, Boolean>

// endregion

// region columns

public typealias Column = ColumnReference<*>

public typealias ColumnGroupReference = ColumnReference<AnyRow>

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

public typealias AnyBaseColumn = BaseColumn<*>

public typealias AnyMany = Many<*>

// endregion
