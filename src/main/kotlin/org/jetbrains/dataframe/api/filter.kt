package org.jetbrains.dataframe

import kotlin.reflect.KProperty

fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
        (0 until nrow).filter {
            val row = get(it)
            predicate(row, row)
        }.let { get(it) }

fun <T> DataFrame<T>.filterNotNull(selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = getColumnsWithData(selector)
    return filter { row -> cols.all { col -> col[row] != null } }
}

fun <T> DataFrame<T>.filterNotNull(vararg cols: KProperty<*>) = filterNotNull { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNull(vararg cols: String) = filterNotNull { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNull(vararg cols: Column) = filterNotNull { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNull(cols: Iterable<Column>) = filterNotNull { cols.toColumnSet() }

fun <T> DataFrame<T>.filterNotNullAny(selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = getColumnsWithData(selector)
    return filter { row -> cols.any { col -> col[row] != null } }
}

fun <T> DataFrame<T>.filterNotNullAny(vararg cols: KProperty<*>) = filterNotNullAny { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNullAny(vararg cols: String) = filterNotNullAny { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNullAny(vararg cols: Column) = filterNotNullAny { cols.toColumnSet() }
fun <T> DataFrame<T>.filterNotNullAny(cols: Iterable<Column>) = filterNotNullAny { cols.toColumnSet() }


