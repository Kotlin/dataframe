package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import kotlin.reflect.KProperty

// region toColumnSet

// region Array

public fun Array<out ColumnsResolver<*>>.toColumnSet(): ColumnSet<Any?> = ColumnListImpl(asList())

@JvmName("toColumnSetString")
public fun Array<out String>.toColumnSet(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()

@JvmName("toColumnSetColumnPath")
public fun Array<out ColumnPath>.toColumnSet(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()

public fun <C> Array<out KProperty<C>>.toColumnSet(): ColumnSet<C> = map { it.toColumnAccessor() }.toColumnSet()

@JvmName("toColumnSetC")
public fun <C> Array<out ColumnsResolver<C>>.toColumnSet(): ColumnSet<C> = ColumnListImpl(asList())

public fun <C> Array<out ColumnReference<C>>.toColumnSet(): ColumnSet<C> = asIterable().toColumnSet()

// endregion

// region Iterable

public fun Iterable<ColumnsResolver<*>>.toColumnSet(): ColumnSet<Any?> = ColumnListImpl(asList())

@JvmName("toColumnSetString")
public fun Iterable<String>.toColumnSet(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()

@JvmName("toColumnSetColumnPath")
public fun Iterable<ColumnPath>.toColumnSet(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()

@JvmName("toColumnSetKProperty")
public fun <C> Iterable<KProperty<C>>.toColumnSet(): ColumnSet<C> = map { it.toColumnAccessor() }.toColumnSet()

@JvmName("toColumnSetC")
public fun <C> Iterable<ColumnsResolver<C>>.toColumnSet(): ColumnSet<C> = ColumnListImpl(toList())

@JvmName("toColumnSetColumnReference")
public fun <C> Iterable<ColumnReference<C>>.toColumnSet(): ColumnSet<C> = ColumnListImpl(toList())

// endregion

// endregion

// region toColumnSetOf

// region Array

public fun <C> Array<out String>.toColumnsSetOf(): ColumnSet<C> = toColumnSet() as ColumnSet<C>

// endregion

// region Iterable

public fun <C> Iterable<String>.toColumnsSetOf(): ColumnSet<C> = toColumnSet() as ColumnSet<C>

// endregion

// endregion
