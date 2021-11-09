package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T> PivotedDataFrame<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): GroupedPivot<T> = (this as PivotedDataFrameImpl<T>).toGroupedPivot(moveToTop, columns)
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: Column): GroupedPivot<T> = groupBy { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: String): GroupedPivot<T> = groupBy { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.groupBy(vararg columns: KProperty<*>): GroupedPivot<T> = groupBy { columns.toColumns() }

public fun <T> PivotedDataFrame<T>.groupByOther(): GroupedPivot<T> {
    val impl = this as PivotedDataFrameImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { except(pivotColumns) }
}
