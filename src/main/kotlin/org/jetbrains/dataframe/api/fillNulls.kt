package org.jetbrains.dataframe

import kotlin.reflect.KProperty

fun <T, C> DataFrame<T>.fillNulls(cols: ColumnsSelector<T, C>) = update(cols).where { it == null }
fun <T> DataFrame<T>.fillNulls(vararg cols: String) = fillNulls { cols.toColumnSet() }
fun <T, C> DataFrame<T>.fillNulls(vararg cols: KProperty<C>) = fillNulls { cols.toColumnSet() }
fun <T, C> DataFrame<T>.fillNulls(vararg cols: ColumnDef<C>) = fillNulls { cols.toColumnSet() }