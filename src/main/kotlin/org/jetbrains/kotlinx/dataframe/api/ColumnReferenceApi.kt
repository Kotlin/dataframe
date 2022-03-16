package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import kotlin.reflect.typeOf

internal val ColumnReference<*>.name: String get() = name()
public inline fun <reified T> ColumnReference<T>.withValues(vararg values: T): ValueColumn<T> = withValues(values.asIterable())
public inline fun <reified T> ColumnReference<T>.withValues(values: Iterable<T>): ValueColumn<T> =
    DataColumn.createValueColumn(name(), values.asList(), typeOf<T>())

public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

public infix fun <C : Comparable<C>> ColumnReference<C>.gt(value: C): ColumnReference<Boolean> = map { it > value }
public infix fun <C : Comparable<C>> ColumnReference<C>.lt(value: C): ColumnReference<Boolean> = map { it < value }
public infix fun <C> ColumnReference<C>.eq(value: C): ColumnReference<Boolean> = map { it == value }
public infix fun <C> ColumnReference<C>.neq(value: C): ColumnReference<Boolean> = map { it != value }

public fun ColumnReference<String?>.length(): ColumnReference<Int> = map { it?.length ?: 0 }
public fun ColumnReference<String?>.lowercase(): ColumnReference<String?> = map { it?.lowercase() }
public fun ColumnReference<String?>.uppercase(): ColumnReference<String?> = map { it?.uppercase() }
