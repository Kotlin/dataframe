package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.dataframe.*
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.PivotedDataFrame

internal data class DataFramePivotImpl<T>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, *>
) : PivotedDataFrame<T> {

    override fun groupBy(columns: ColumnsSelector<T, *>): GroupedPivot<T> =
        GroupedPivotImpl(df.groupBy(columns), this.columns)
}
