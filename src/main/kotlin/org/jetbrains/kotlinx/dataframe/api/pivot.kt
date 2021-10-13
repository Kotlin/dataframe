package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.MapColumnReference
import org.jetbrains.kotlinx.dataframe.PivotedDataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateReceiver
import org.jetbrains.kotlinx.dataframe.aggregation.GroupByReceiver
import org.jetbrains.kotlinx.dataframe.aggregation.PivotReceiver
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.DataFramePivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateReceiverInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.PivotReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.toColumnPath
import kotlin.reflect.KType

public fun <T> DataFrame<T>.pivot(columns: ColumnsSelector<T, *>): PivotedDataFrame<T> = DataFramePivotImpl(this, columns)
public fun <T> DataFrame<T>.pivot(vararg columns: String): PivotedDataFrame<T> = pivot { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column): PivotedDataFrame<T> = pivot { columns.toColumns() }

public fun <T, P : GroupedPivot<T>> P.withGrouping(group: MapColumnReference): P = withGrouping(group.path()) as P
public fun <T, P : GroupedPivot<T>> P.withGrouping(groupName: String): P = withGrouping(pathOf(groupName)) as P

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

public typealias AggregateBody<T, R> = AggregateReceiver<T>.(AggregateReceiver<T>) -> R

public typealias PivotAggregateBody<T, R> = PivotReceiver<T>.(PivotReceiver<T>) -> R

public data class ValueWithName(val value: Any?, val name: String)

@Suppress("DataClassPrivateConstructor")
public data class NamedValue private constructor(
    val path: ColumnPath,
    val value: Any?,
    val type: KType?,
    var default: Any?,
    val guessType: Boolean = false
) {
    public companion object {
        public fun create(path: ColumnPath, value: Any?, type: KType?, defaultValue: Any?, guessType: Boolean = false): NamedValue = when (value) {
            is ValueWithDefault<*> -> create(path, value.value, type, value.default, guessType)
            is ValueWithName -> create(path.replaceLast(value.name).toColumnPath(), value.value, type, defaultValue, guessType)
            else -> NamedValue(path, value, type, defaultValue, guessType)
        }
        public fun aggregator(builder: GroupByReceiver<*>): NamedValue = NamedValue(emptyPath(), builder, null, null, false)
    }

    val name: String get() = path.last()
}
