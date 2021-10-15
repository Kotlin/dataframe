package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.api.PivotedDataFrame
import org.jetbrains.kotlinx.dataframe.api.groupBy

internal data class PivotedDataFrameImpl<T>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, *>
) : PivotedDataFrame<T> {

    fun toGroupedPivot(columns: ColumnsSelector<T, *>): GroupedPivot<T> =
        GroupedPivotImpl(df.groupBy(columns), this.columns)
}
