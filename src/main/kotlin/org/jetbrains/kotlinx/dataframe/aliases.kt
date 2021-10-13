package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.SelectReceiver
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

public typealias Predicate<T> = (T) -> Boolean

public typealias DataFrameSelector<T, R> = DataFrame<T>.(DataFrame<T>) -> R

public typealias ColumnsSelector<T, C> = SelectReceiver<T>.(SelectReceiver<T>) -> Columns<C>

public typealias ColumnSelector<T, C> = SelectReceiver<T>.(SelectReceiver<T>) -> SingleColumn<C>

public typealias Column = ColumnReference<*>

public typealias MapColumnReference = ColumnReference<AnyRow>

public typealias AnyFrame = DataFrame<*>

public typealias AnyRow = DataRow<*>

public typealias AnyColumn = BaseColumn<*>
