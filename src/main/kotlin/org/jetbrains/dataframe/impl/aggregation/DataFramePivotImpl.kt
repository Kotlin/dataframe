package org.jetbrains.dataframe.impl.aggregation

import org.jetbrains.dataframe.*

internal data class DataFramePivotImpl<T>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, *>
) : PivotedDataFrame<T> {

    override fun groupBy(columns: ColumnsSelector<T, *>): GroupedPivot<T> =
        GroupedPivotImpl(df.groupBy(columns), this.columns)
}
