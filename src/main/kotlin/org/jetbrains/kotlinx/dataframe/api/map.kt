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
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnReference

public inline fun <C, reified R> ColumnReference<C>.map(infer: Infer = Infer.Nulls, noinline transform: (C) -> R): ColumnReference<R> =
    createComputedColumnReference(name(), typeOf<R>(), infer) { transform(this@map()) }

// endregion

// region DataColumn

public inline fun <T, reified R> DataColumn<T>.map(
    infer: Infer = Infer.Nulls,
    crossinline transform: (T) -> R
): DataColumn<R> {
    val newValues = Array(size()) { transform(get(it)) }.asList()
    return DataColumn.create(name(), newValues, typeOf<R>(), infer)
}

public fun <T, R> DataColumn<T>.mapTo(
    type: KType,
    infer: Infer = Infer.Nulls,
    transform: (T) -> R
): DataColumn<R> {
    val values = Array<Any?>(size()) { transform(get(it)) }.asList()
    return DataColumn.create(name, values, type, infer).cast()
}

// endregion

// region DataFrame

public fun <T> DataFrame<T>.map(body: AddDsl<T>.() -> Unit): AnyFrame {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(dsl.columns)
}

public inline fun <T, reified R> ColumnsContainer<T>.map(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>
): DataColumn<R> = newColumn(typeOf<R>(), name, infer, body)

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
