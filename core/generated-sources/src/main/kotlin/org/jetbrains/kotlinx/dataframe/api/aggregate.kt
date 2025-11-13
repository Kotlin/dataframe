package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.aggregateGroupBy

// region Pivot

public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<T> =
    delegate {
        aggregate(separate, body)
    }

// endregion

@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy(
        df = (this as GroupBy<*, *>).toDataFrame(),
        selector = { groups.cast() },
        body = body,
    ).cast()
