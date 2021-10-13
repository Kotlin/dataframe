package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns

public interface PivotedDataFrame<T> : Aggregatable<T> {

    public fun groupBy(columns: ColumnsSelector<T, *>): GroupedPivot<T>
    public fun groupBy(vararg columns: String): GroupedPivot<T> = groupBy { columns.toColumns() }
    public fun groupBy(vararg columns: Column): GroupedPivot<T> = groupBy { columns.toColumns() }
}
