package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.ColumnWithPath
import kotlin.reflect.KProperty

data class GroupClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

fun <T> DataFrame<T>.group(cols: Iterable<Column>) = group { cols.toColumnSet() }
fun <T> DataFrame<T>.group(vararg cols: KProperty<*>) = group { cols.toColumns() }
fun <T> DataFrame<T>.group(vararg cols: String) = group { cols.toColumns() }
fun <T> DataFrame<T>.group(vararg cols: Column) = group { cols.toColumns() }
fun <T, C> DataFrame<T>.group(cols: ColumnsSelector<T, C>) = GroupClause(this, cols)

fun <T, C> GroupClause<T, C>.into(groupName: String) = into { groupName }
fun <T, C> GroupClause<T, C>.into(groupRef: MapColumnReference) = df.move(selector).under(groupRef)
fun <T, C> GroupClause<T, C>.into(groupName: ColumnWithPath<C>.(ColumnWithPath<C>) -> String) = df.move(selector).under { path(groupName(it, it))}