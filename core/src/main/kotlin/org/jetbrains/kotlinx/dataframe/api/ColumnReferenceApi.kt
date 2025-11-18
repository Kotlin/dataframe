package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.typeOf

internal val ColumnReference<*>.name: String get() = name()

@Suppress("DEPRECATION_ERROR")
@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public inline fun <reified T> ColumnReference<T>.withValues(vararg values: T): ValueColumn<T> =
    withValues(values.asIterable())

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public inline fun <reified T> ColumnReference<T>.withValues(values: Iterable<T>): ValueColumn<T> =
    DataColumn.createValueColumn(name(), values.asList(), typeOf<T>())

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public infix fun <C : Comparable<C>> ColumnReference<C>.gt(value: C): ColumnReference<Boolean> = map { it > value }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public infix fun <C : Comparable<C>> ColumnReference<C>.lt(value: C): ColumnReference<Boolean> = map { it < value }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public infix fun <C> ColumnReference<C>.eq(value: C): ColumnReference<Boolean> = map { it == value }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public infix fun <C> ColumnReference<C>.neq(value: C): ColumnReference<Boolean> = map { it != value }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public fun ColumnReference<String?>.length(): ColumnReference<Int> = map { it?.length ?: 0 }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public fun ColumnReference<String?>.lowercase(): ColumnReference<String?> = map { it?.lowercase() }

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public fun ColumnReference<String?>.uppercase(): ColumnReference<String?> = map { it?.uppercase() }
