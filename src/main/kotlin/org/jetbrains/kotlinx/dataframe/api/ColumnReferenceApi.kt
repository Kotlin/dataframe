package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KType

public inline fun <C, reified R> ColumnReference<C>.map(noinline transform: (C) -> R): ColumnReference<R> =
    map(getType<R>(), transform)

public fun <C, R> ColumnReference<C>.map(targetType: KType?, transform: (C) -> R): ColumnReference<R> =
    createComputedColumnReference(this.name, targetType) { transform(this@map()) }

internal val ColumnReference<*>.name: String get() = name()
public inline fun <reified T> ColumnReference<T>.withValues(vararg values: T): ValueColumn<T> = withValues(values.asIterable())
public inline fun <reified T> ColumnReference<T>.withValues(values: Iterable<T>): ValueColumn<T> =
    DataColumn.createValueColumn(name(), values.asList(), getType<T>())

public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

public infix fun <C : Comparable<C>> ColumnReference<C>.gt(value: C): ColumnReference<Boolean> = map { it > value }
public infix fun <C : Comparable<C>> ColumnReference<C>.lt(value: C): ColumnReference<Boolean> = map { it < value }
public infix fun <C> ColumnReference<C>.eq(value: C): ColumnReference<Boolean> = map { it == value }
public infix fun <C> ColumnReference<C>.neq(value: C): ColumnReference<Boolean> = map { it != value }

public fun ColumnReference<String?>.length(): ColumnReference<Int> = map { it?.length ?: 0 }
public fun ColumnReference<String?>.lowercase(): ColumnReference<String?> = map { it?.lowercase() }
public fun ColumnReference<String?>.uppercase(): ColumnReference<String?> = map { it?.uppercase() }
