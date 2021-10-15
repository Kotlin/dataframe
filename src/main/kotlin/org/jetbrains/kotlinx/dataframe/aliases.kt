package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

public interface Many<out T> : List<T>

public typealias Predicate<T> = (T) -> Boolean

public typealias Selector<T, R> = T.(T) -> R

// region selectors

public typealias DataFrameSelector<T, R> = Selector<DataFrame<T>, R>

public typealias RowSelector<T, R> = Selector<DataRow<T>, R>

public typealias ColumnSelector<T, C> = Selector<ColumnSelectionDsl<T>, SingleColumn<C>>

public typealias ColumnsSelector<T, C> = Selector<ColumnSelectionDsl<T>, Columns<C>>

public typealias RowCellSelector<T, C, R> = DataRow<T>.(C) -> R

public typealias RowColumnSelector<T, C, R> = (DataRow<T>, DataColumn<C>) -> R

// endregion

// region filters

public typealias RowFilter<T> = RowSelector<T, Boolean>

public typealias ColumnFilter<T> = (ColumnWithPath<T>) -> Boolean

public typealias VectorizedRowFilter<T> = Selector<DataFrameBase<T>, BooleanArray>

public typealias RowCellFilter<T, C> = RowCellSelector<T, C, Boolean>

// endregion

// region columns

public typealias Column = ColumnReference<*>

public typealias MapColumnReference = ColumnReference<AnyRow>

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

public typealias AnyColumn = BaseColumn<*>

public typealias AnyMany = Many<*>

// endregion
