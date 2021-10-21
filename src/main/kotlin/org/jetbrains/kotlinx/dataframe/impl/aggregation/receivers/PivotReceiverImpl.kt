package org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.KType

internal class PivotReceiverImpl<T>(override val df: DataFrame<T>) : AggregateDsl<T>(), AggregateInternalDsl<T>, DataFrame<T> by df, AggregatableInternal<T> by df.internal() {

    internal val values = mutableListOf<NamedValue>()

    override fun yield(value: NamedValue) = value.also { values.add(it) }

    override fun pathForSingleColumn(column: AnyCol) = emptyPath()

    override fun <R> yield(path: ColumnPath, value: R, type: KType?, default: R?) = yield(path, value, type, default, true)
}
