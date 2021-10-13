package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.GroupWithKey
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.typed
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.toFrameColumn
import kotlin.reflect.KType

public fun <T> DataFrame<T>.mapColumns(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): AnyFrame {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}

public fun <T, R> DataColumn<T>.map(transform: (T) -> R): DataColumn<R> {
    val collector = createDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}

public fun <T, R> DataColumn<T>.map(type: KType?, transform: (T) -> R): DataColumn<R> {
    if (type == null) return map(transform)
    val collector = createDataCollector<R>(size, type)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as DataColumn<R>
}

public inline fun <C, reified R> ColumnReference<C>.map(noinline transform: (C) -> R): ColumnReference<R> =
    map(getType<R>(), transform)

public fun <C, R> ColumnReference<C>.map(targetType: KType?, transform: (C) -> R): ColumnReference<R> =
    TransformedColumnReference(this, transform, targetType)

public fun <T, G, R> GroupedDataFrame<T, G>.mapNotNullGroups(transform: DataFrame<G>.() -> DataFrame<R>?): GroupedDataFrame<T, R> = mapGroups { if (it == null) null else transform(it) }

public fun <T, G, R> GroupedDataFrame<T, G>.map(body: GroupWithKey<T, G>.(GroupWithKey<T, G>) -> R): List<R> = keys.mapIndexedNotNull { index, row ->
    val group = groups[index]
    if (group == null) null
    else {
        val g = GroupWithKey(row, group)
        body(g, g)
    }
}

public fun <T, G> GroupedDataFrame<T, G>.mapToRows(body: GroupWithKey<T, G>.(GroupWithKey<T, G>) -> DataRow<G>?): DataFrame<G> =
    map(body).union()

public fun <T, G> GroupedDataFrame<T, G>.mapToFrames(body: GroupWithKey<T, G>.(GroupWithKey<T, G>) -> DataFrame<G>?): FrameColumn<G> =
    map(body).toFrameColumn(groups.name)
