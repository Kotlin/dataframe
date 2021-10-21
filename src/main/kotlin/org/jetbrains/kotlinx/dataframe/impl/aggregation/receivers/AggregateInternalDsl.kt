package org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import kotlin.reflect.KType

@PublishedApi
internal interface AggregateInternalDsl<out T> {

    val df: DataFrame<T>

    fun yield(value: NamedValue): NamedValue

    fun <R> yield(path: ColumnPath, value: R, type: KType?, default: R?, guessType: Boolean) =
        yield(NamedValue.create(path, value, type, default, guessType))

    fun <R> yield(path: ColumnPath, value: R, type: KType? = null, default: R? = null): NamedValue

    fun pathForSingleColumn(column: AnyCol): ColumnPath
}

@PublishedApi
internal fun <T> AggregateDsl<T>.internal(): AggregateInternalDsl<T> = this as AggregateInternalDsl<T>

internal fun <T, R> AggregateBodyInternal<T, R>.public() = this as AggregateBody<T, R>

internal fun <T, R> AggregateBody<T, R>.internal() = this as AggregateBodyInternal<T, R>

internal typealias AggregateBodyInternal<T, R> = Selector<AggregateInternalDsl<T>, R>
