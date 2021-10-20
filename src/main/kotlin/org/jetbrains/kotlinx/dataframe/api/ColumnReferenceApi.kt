package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KType

public inline fun <C, reified R> ColumnReference<C>.map(noinline transform: (C) -> R): ColumnReference<R> =
    map(getType<R>(), transform)

public fun <C, R> ColumnReference<C>.map(targetType: KType?, transform: (C) -> R): ColumnReference<R> =
    TransformedColumnReference(this, transform, targetType)

public val ColumnReference<*>.name: String get() = name()
public inline fun <reified T> ColumnReference<T>.withValues(vararg values: T): ValueColumn<T> = withValues(values.asIterable())
public inline fun <reified T> ColumnReference<T>.withValues(values: Iterable<T>): ValueColumn<T> =
    DataColumn.createValueColumn(name(), values.asList(), getType<T>())

public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C
