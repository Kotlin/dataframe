package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.Aggregatable
import org.jetbrains.dataframe.impl.columns.toColumns

public interface PivotedDataFrame<T> : Aggregatable<T> {

    public fun groupBy(columns: ColumnsSelector<T, *>): GroupedPivotAggregations<T>
    public fun groupBy(vararg columns: String): GroupedPivotAggregations<T> = groupBy { columns.toColumns() }
    public fun groupBy(vararg columns: Column): GroupedPivotAggregations<T> = groupBy { columns.toColumns() }
}
