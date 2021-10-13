package org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers

import org.jetbrains.dataframe.NamedValue
import org.jetbrains.kotlinx.dataframe.ColumnPath
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.PivotReceiver
import org.jetbrains.kotlinx.dataframe.columns.AnyCol
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toInternal
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.KType

internal class PivotReceiverImpl<T>(override val df: DataFrame<T>) : PivotReceiver<T>(), AggregateReceiverInternal<T>, DataFrame<T> by df, AggregatableInternal<T> by df.toInternal() {

    internal val values = mutableListOf<NamedValue>()

    override fun yield(value: NamedValue) = value.also { values.add(it) }

    override fun pathForSingleColumn(column: AnyCol) = emptyPath()

    override fun <R> yield(path: ColumnPath, value: R, type: KType?, default: R?) = yield(path, value, type, default, true)
}
