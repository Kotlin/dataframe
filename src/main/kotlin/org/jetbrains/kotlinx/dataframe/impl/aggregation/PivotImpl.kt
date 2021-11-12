package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.groupBy

internal data class PivotImpl<T>(
    val df: DataFrame<T>,
    val columns: PivotColumnsSelector<T, *>,
    val inward: Boolean?
) : Pivot<T> {

    fun toGroupedPivot(moveToTop: Boolean, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
        PivotGroupByImpl(df.groupBy(moveToTop, columns), this.columns, inward)
}
