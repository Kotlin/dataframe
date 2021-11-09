package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.PivotedDataFrame
import org.jetbrains.kotlinx.dataframe.api.groupBy

internal data class PivotedDataFrameImpl<T>(
    val df: DataFrame<T>,
    val columns: PivotColumnsSelector<T, *>,
    val inward: Boolean?
) : PivotedDataFrame<T> {

    fun toGroupedPivot(moveToTop: Boolean, columns: ColumnsSelector<T, *>): GroupedPivot<T> =
        GroupedPivotImpl(df.groupBy(moveToTop, columns), this.columns, inward)
}
