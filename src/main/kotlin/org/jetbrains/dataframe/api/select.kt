package org.jetbrains.dataframe

import kotlin.reflect.KProperty

fun <T> DataFrame<T>.select(selector: ColumnsSelector<T, *>): DataFrame<T> = new(getColumns(selector))
fun <T> DataFrame<T>.select(vararg columns: KProperty<*>) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: String) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: Column) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(columns: Iterable<Column>) = select { columns.toColumnSet() }

