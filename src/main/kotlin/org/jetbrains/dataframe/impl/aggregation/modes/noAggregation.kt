package org.jetbrains.dataframe.impl.aggregation.modes

import org.jetbrains.dataframe.AggregateBody
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.Grouped
import org.jetbrains.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.dataframe.pathOf

@PublishedApi
internal fun <T, R> Grouped<T>.aggregateValue(
    resultName: String,
    body: AggregateBody<T, R>
): DataFrame<T> = aggregateInternal {
    val value = body.internal()(this)
    yield(pathOf(resultName), value)
}
