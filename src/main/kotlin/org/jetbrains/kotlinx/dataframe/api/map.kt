package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumnWithActualType
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnReference

public inline fun <C, reified R> ColumnReference<C>.map(noinline transform: (C) -> R): ColumnReference<R> =
    map(typeOf<R>(), transform)

public fun <C, R> ColumnReference<C>.map(tartypeOf: KType?, transform: (C) -> R): ColumnReference<R> =
    createComputedColumnReference(this.name, tartypeOf) { transform(this@map()) }

// endregion

// region DataColumn

public fun <T, R> DataColumn<T>.map(transform: (T) -> R): DataColumn<R> {
    val collector = createDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).cast()
}

public inline fun <T, reified R> DataColumn<T>.mapInline(crossinline transform: (T) -> R): DataColumn<R> {
    val newValues = Array(size()) { transform(get(it)) }.asList()
    val resType = typeOf<R>()
    return guessColumnType(
        name(),
        newValues,
        suggestedType = resType,
        suggestedTypeIsUpperBound = false,
        nullable = if (!resType.isMarkedNullable) false else null
    )
}

public fun <T, R> DataColumn<T>.map(type: KType?, transform: (T) -> R): DataColumn<R> {
    if (type == null) return map(transform)
    val collector = createDataCollector<R>(size, type)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as DataColumn<R>
}

// endregion

// region DataFrame

// region map

public fun <T> DataFrame<T>.map(body: AddDsl<T>.() -> Unit): AnyFrame {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(dsl.columns)
}

public inline fun <T, reified R> ColumnsContainer<T>.map(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>
): DataColumn<R> = when (infer) {
    Infer.Type -> newColumnWithActualType(name, body)
    Infer.Nulls -> newColumn(typeOf<R>(), name, true, body)
    Infer.None -> newColumn(typeOf<R>(), name, false, body)
}

public inline fun <T, reified R> ColumnsContainer<T>.map(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>
): DataColumn<R> = map(column.name(), infer, body)

public inline fun <T, reified R> ColumnsContainer<T>.map(
    column: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>
): DataColumn<R> = map(column.columnName, infer, body)

// endregion

// region GroupBy

internal fun <T, G, R> GroupBy<T, G>.map(body: Selector<GroupWithKey<T, G>, R>): List<R> = keys.rows().mapIndexedNotNull { index, row ->
    val group = groups[index]
    val g = GroupWithKey(row, group)
    body(g, g)
}

public fun <T, G> GroupBy<T, G>.mapToRows(body: Selector<GroupWithKey<T, G>, DataRow<G>?>): DataFrame<G> =
    map(body).concat()

public fun <T, G> GroupBy<T, G>.mapToFrames(body: Selector<GroupWithKey<T, G>, DataFrame<G>>): FrameColumn<G> =
    DataColumn.createFrameColumn(groups.name, map(body))

// endregion
