package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T> Pivot<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> = (this as PivotImpl<T>).toGroupedPivot(moveToTop, columns)
public fun <T> Pivot<T>.groupBy(vararg columns: Column): PivotGroupBy<T> = groupBy { columns.toColumns() }
public fun <T> Pivot<T>.groupBy(vararg columns: String): PivotGroupBy<T> = groupBy { columns.toColumns() }
public fun <T> Pivot<T>.groupBy(vararg columns: KProperty<*>): PivotGroupBy<T> = groupBy { columns.toColumns() }

public fun <T> Pivot<T>.groupByOther(): PivotGroupBy<T> {
    val impl = this as PivotImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { except(pivotColumns) }
}
