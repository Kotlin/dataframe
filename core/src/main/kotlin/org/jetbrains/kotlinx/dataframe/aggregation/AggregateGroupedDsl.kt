package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.AnyRow

public abstract class AggregateGroupedDsl<out T> : AggregateDsl<T>() {
    public abstract val keys: AnyRow
}
