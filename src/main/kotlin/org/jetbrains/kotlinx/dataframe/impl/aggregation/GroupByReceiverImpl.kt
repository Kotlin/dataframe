package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.api.toMany
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.shortPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateInternalDsl
import org.jetbrains.kotlinx.dataframe.impl.api.AggregatedPivot
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.impl.getListType
import kotlin.reflect.KType

internal class GroupByReceiverImpl<T>(override val df: DataFrame<T>) :
    AggregateGroupedDsl<T>(),
    AggregateInternalDsl<T>,
    AggregatableInternal<T> by df as AggregatableInternal<T>,
    DataFrame<T> by df {

    private val values = mutableListOf<NamedValue>()

    internal fun child(): GroupByReceiverImpl<T> {
        val child = GroupByReceiverImpl(df)
        values.add(NamedValue.aggregator(child))
        return child
    }

    internal fun compute(): AnyRow? {
        val allValues = mutableListOf<NamedValue>()
        values.forEach {
            when (it.value) {
                is GroupByReceiverImpl<*> -> {
                    it.value.values.forEach {
                        allValues.add(it)
                    }
                }
                is ValueColumn<*> -> {
                    allValues.add(NamedValue.create(it.path, it.value.toMany(), getListType(it.value.type()), emptyList<Unit>()))
                }
                is ColumnGroup<*> -> {
                    val frameType = it.value.type().arguments.singleOrNull()?.type
                    allValues.add(NamedValue.create(it.path, it.value.asDataFrame(), DataFrame::class.createTypeWithArgument(frameType), DataFrame.Empty))
                }
                else -> {
                    allValues.add(it)
                }
            }
        }
        val columns = allValues.map { it.toColumnWithPath() }
        return if (columns.isEmpty()) null
        else columns.toDataFrameFromPairs<T>()[0]
    }

    override fun pathForSingleColumn(column: AnyCol) = column.shortPath()

    override fun <R> yield(path: ColumnPath, value: R, type: KType?, default: R?) =
        yield(path, value, type, default, false)

    override fun yield(value: NamedValue): NamedValue {
        when (value.value) {
            is AggregatedPivot<*> -> {
                value.value.aggregator.values.forEach {
                    yield(value.path + it.path, it.value, it.type, it.default, it.guessType)
                }
                value.value.aggregator.values.clear()
            }
            is AggregateInternalDsl<*> -> yield(value.copy(value = value.value.df))
            else -> values.add(value)
        }
        return value
    }
}
