package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.kotlinx.dataframe.pathOf

@PublishedApi
internal fun <T, R> Grouped<T>.aggregateValue(
    resultName: String,
    body: AggregateBody<T, R>
): DataFrame<T> = aggregateInternal {
    val value = body.internal()(this)
    yield(pathOf(resultName), value)
}
