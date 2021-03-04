package org.jetbrains.dataframe

import org.jetbrains.dataframe.AnyFrame
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.TypedColumnsFromDataRowBuilder
import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.impl.columns.typed
import org.jetbrains.dataframe.impl.createDataCollector
import org.jetbrains.dataframe.name
import kotlin.reflect.KType

fun <T> DataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): AnyFrame {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}

fun <T, R> DataColumn<T>.map(transform: (T) -> R): DataColumn<R> {
    val collector = createDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}

fun <T, R> DataColumn<T>.map(type: KType?, transform: (T) -> R): DataColumn<R> {
    if (type == null) return map(transform)
    val collector = createDataCollector<R>(size, type)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as DataColumn<R>
}