package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath

public interface GroupedPivot<out T> : Aggregatable<T> {

    public fun <R> aggregate(separate: Boolean = false, body: AggregateBody<T, R>): DataFrame<T>

    public fun separateAggregatedValues(flag: Boolean = true): GroupedPivot<T>
    public fun default(value: Any?): GroupedPivot<T>
    public fun withGrouping(groupPath: ColumnPath): GroupedPivot<T>
}
