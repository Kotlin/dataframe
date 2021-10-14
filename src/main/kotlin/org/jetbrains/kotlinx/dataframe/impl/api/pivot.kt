package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NamedValue
import org.jetbrains.kotlinx.dataframe.api.PivotAggregateBody
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateReceiverInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.PivotReceiverImpl

internal class AggregatedPivot<T>(private val df: DataFrame<T>, internal var aggregator: GroupByReceiverImpl<T>) :
    DataFrame<T> by df

internal fun <T, R> aggregatePivot(
    aggregator: AggregateReceiverInternal<T>,
    columns: ColumnsSelector<T, *>,
    separate: Boolean,
    groupPath: ColumnPath,
    globalDefault: Any? = null,
    body: PivotAggregateBody<T, R>
) {
    aggregator.df.groupBy(columns).forEach { key, group ->

        val keyValue = key.values()
        val path = keyValue.map { it.toString() }
        val builder = PivotReceiverImpl(group!!)
        val result = body(builder, builder)
        val hasResult = result != null && result != Unit

        fun NamedValue.apply(path: ColumnPath) = copy(path = path, value = this.value ?: default ?: globalDefault, default = default ?: globalDefault)

        val values = builder.values
        when {
            values.size == 1 && values[0].path.isEmpty() -> aggregator.yield(values[0].apply(groupPath + path))
            values.isEmpty() -> aggregator.yield(groupPath + path, if (hasResult) result else globalDefault, null, globalDefault, true)
            else -> {
                values.forEach {
                    val targetPath = groupPath + if (separate) it.path + path else path + it.path
                    aggregator.yield(it.apply(targetPath))
                }
            }
        }
    }
}
