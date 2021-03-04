package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.ColumnReference
import org.jetbrains.dataframe.impl.columns.toColumnSet
import org.jetbrains.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

fun <T, C> DataFrame<T>.fillNulls(cols: ColumnsSelector<T, C>) = update(cols).where { it == null }
fun <T> DataFrame<T>.fillNulls(vararg cols: String) = fillNulls { cols.toColumns() }
fun <T, C> DataFrame<T>.fillNulls(vararg cols: KProperty<C>) = fillNulls { cols.toColumns() }
fun <T, C> DataFrame<T>.fillNulls(vararg cols: ColumnReference<C>) = fillNulls { cols.toColumns() }
fun <T, C> DataFrame<T>.fillNulls(cols: Iterable<ColumnReference<C>>) = fillNulls { cols.toColumnSet() }