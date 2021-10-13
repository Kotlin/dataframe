package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.getColumns
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.new
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.select(selector: ColumnsSelector<T, *>): DataFrame<T> = new(get(selector))
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select(columns.map { it.name })
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select(columns.asIterable())
public fun <T> DataFrame<T>.select(vararg columns: Column): DataFrame<T> = select { columns.toColumns() }
@JvmName("selectT")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> = new(getColumns(columns.asList()))
public fun <T> DataFrame<T>.select(columns: Iterable<Column>): DataFrame<T> = select { columns.toColumnSet() }
