package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import kotlin.reflect.KProperty

fun <T, D : Comparable<D>> DataFrame<T>.max(col: KProperty<D?>): D? = get(col).max()
fun <T, D : Comparable<D>> DataFrame<T>.max(col: ColumnDef<D?>): D? = get(col).max()
fun <T, D : Comparable<D>> DataFrame<T>.max(selector: RowSelector<T, D?>): D? = rows().asSequence().map { selector(it, it) }.filterNotNull().max()
fun <T, D : Comparable<D>> DataFrame<T>.min(col: KProperty<D?>): D? = get(col).min()
fun <T, D : Comparable<D>> DataFrame<T>.min(col: ColumnDef<D?>): D? = get(col).min()
fun <T, D : Comparable<D>> DataFrame<T>.min(selector: RowSelector<T, D?>): D? = rows().asSequence().map { selector(it, it) }.filterNotNull().min()

inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.min(columnName: String = "min", noinline selector: RowSelector<G, R>) = aggregate { min(selector) into columnName }
inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.max(columnName: String = "max", noinline selector: RowSelector<G, R>) = aggregate { max(selector) into columnName }

fun <T : Comparable<T>> ColumnData<T?>.min() = values.asSequence().filterNotNull().minOrNull()
fun <T : Comparable<T>> ColumnData<T?>.max() = values.asSequence().filterNotNull().maxOrNull()