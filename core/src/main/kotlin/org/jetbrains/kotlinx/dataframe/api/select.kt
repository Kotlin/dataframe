package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> =
    get(columns).toDataFrame().cast()

public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> =
    select(columns.asIterable())

@JvmName("selectKPropertyIterable")
public fun <T> DataFrame<T>.select(columns: Iterable<KProperty<*>>): DataFrame<T> =
    select(columns.map { it.columnName })

public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> =
    select(columns.asIterable())

@JvmName("selectStringIterable")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> =
    columns.map { get(it) }.toDataFrame().cast()

public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> =
    select { columns.toColumns() }

@JvmName("selectAnyColumnReferenceIterable")
public fun <T> DataFrame<T>.select(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    select { columns.toColumnSet() }

// endregion
