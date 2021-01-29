package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import kotlin.reflect.KProperty

fun <T, D : Comparable<D>> DataFrame<T>.max(col: KProperty<D?>): D? = get(col).max()
fun <T, D : Comparable<D>> DataFrame<T>.max(col: ColumnReference<D?>): D? = get(col).max()
fun <T, D : Comparable<D>> DataFrame<T>.max(selector: RowSelector<T, D?>): D? = rows().asSequence().map { selector(it, it) }.filterNotNull().maxOrNull()
fun <T> DataFrame<T>.max(col: String): Any? = get(col).maxUnsafe()

fun <T, D : Comparable<D>> DataFrame<T>.min(col: KProperty<D?>): D? = get(col).min()
fun <T, D : Comparable<D>> DataFrame<T>.min(col: ColumnReference<D?>): D? = get(col).min()
fun <T, D : Comparable<D>> DataFrame<T>.min(selector: RowSelector<T, D?>): D? = rows().asSequence().map { selector(it, it) }.filterNotNull().minOrNull()
fun <T> DataFrame<T>.min(col: String): Any? = get(col).minUnsafe()

inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.min(resultColumnName: String = "min", noinline selector: RowSelector<G, R?>) = aggregate { min(selector) into resultColumnName }
inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.max(resultColumnName: String = "max", noinline selector: RowSelector<G, R?>) = aggregate { max(selector) into resultColumnName }

fun <T, G> GroupedDataFrame<T, G>.min(column: String) = aggregate { min(column) into column }
fun <T, G> GroupedDataFrame<T, G>.max(column: String) = aggregate { max(column) into column }

internal fun <T> DataColumn<T?>.minUnsafe(): T? {
    val casted = assertIsComparable() as DataColumn<Comparable<Any>?>
    return casted.min() as T?
}

internal fun <T> DataColumn<T?>.maxUnsafe(): T? {
    val casted = assertIsComparable() as DataColumn<Comparable<Any>?>
    return casted.max() as T?
}

fun <T: Comparable<T>> DataColumn<T?>.min() = asSequence().filterNotNull().minOrNull()

fun <T: Comparable<T>> DataColumn<T?>.max() = asSequence().filterNotNull().maxOrNull()
