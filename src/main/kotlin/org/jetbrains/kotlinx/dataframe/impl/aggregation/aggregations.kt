package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toMany
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateInternalDsl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType

@PublishedApi
internal fun <T, V> AggregateInternalDsl<T>.yieldOneOrMany(
    path: ColumnPath,
    values: List<V>,
    type: KType,
    default: V? = null
) {
    if (values.size == 1) yield(path, values[0], type, default)
    else yield(path, values.toMany(), getListType(type), default)
}

@JvmName("toColumnSetForAggregate")
internal fun <T, C> ColumnsForAggregateSelector<T, C>.toColumns(): ColumnSet<C> = toColumns {
    object : DataFrameReceiver<T>(it.df.cast(), true), ColumnsForAggregateSelectionDsl<T> {}
}

internal fun <T, C, R> AggregateInternalDsl<T>.columnValues(
    columns: ColumnsForAggregateSelector<T, C>,
    aggregator: (DataColumn<C>) -> List<R>
) {
    val cols = df.getAggregateColumns(columns)
    val isSingle = cols.size == 1
    cols.forEach { col ->
        val path = getPath(col, isSingle)
        yieldOneOrMany(path, aggregator(col.data), col.type, col.default)
    }
}
