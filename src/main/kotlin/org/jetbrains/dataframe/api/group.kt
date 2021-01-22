package org.jetbrains.dataframe

import kotlin.reflect.KProperty

data class GroupClause<T>(val df: DataFrame<T>, val selector: ColumnsSelector<T, *>)

fun <T> DataFrame<T>.group(cols: Iterable<Column>) = group { cols.toColumnSet() }
fun <T> DataFrame<T>.group(vararg cols: KProperty<*>) = group { cols.toColumns() }
fun <T> DataFrame<T>.group(vararg cols: String) = group { cols.toColumns() }
fun <T> DataFrame<T>.group(vararg cols: Column) = group { cols.toColumns() }
fun <T> DataFrame<T>.group(cols: ColumnsSelector<T, *>) = GroupClause(this, cols)

fun <T> GroupClause<T>.into(groupName: String) = df.move(selector).intoGroup(groupName)
fun <T> GroupClause<T>.into(groupRef: MapColumnReference) = df.move(selector).intoGroup(groupRef)