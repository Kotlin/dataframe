package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T> PivotedDataFrame<T>.groupBy(columns: ColumnsSelector<T, *>): GroupedPivot<T> = (this as PivotedDataFrameImpl<T>).toGroupedPivot(columns)
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: Column): GroupedPivot<T> = groupBy { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: String): GroupedPivot<T> = groupBy { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: KProperty<*>): GroupedPivot<T> = groupBy { columns.toColumns() }
