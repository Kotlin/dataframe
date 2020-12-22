package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import kotlin.reflect.KProperty

fun <T, D : Comparable<D>> DataFrame<T>.maxBy(col: KProperty<D?>) = rows.maxByOrNull { it[col] as D }!!
fun <T> DataFrame<T>.maxBy(col: String) = rows.maxByOrNull { it[col] as Comparable<Any?> }!!
fun <T, D : Comparable<D>> DataFrame<T>.maxBy(col: ColumnDef<D>) = rows.maxByOrNull { col(it) }!!
fun <T, D : Comparable<D>> DataFrame<T>.maxBy(selector: RowSelector<T, D>) = rows.maxByOrNull { selector(it, it) }!!
fun <T> DataFrame<T>.minBy(col: String) = rows.minByOrNull { it[col] as Comparable<Any?> }
fun <T, D : Comparable<D>> DataFrame<T>.minBy(col: ColumnDef<D>) = rows.minByOrNull { col(it) }
fun <T, D : Comparable<D>> DataFrame<T>.minBy(selector: RowSelector<T, D>) = rows.minByOrNull { selector(it, it) }!!

fun <T, R : Comparable<R>> ColumnData<T>.minBy(selector: (T) -> R) = values.asSequence().minByOrNull(selector)
fun <T, R : Comparable<R>> ColumnData<T>.maxBy(selector: (T) -> R) = values.asSequence().maxByOrNull(selector)
