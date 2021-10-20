package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KType

public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()
public fun <T> DataColumn<T>.asSequence(): Sequence<T> = asIterable().asSequence()

public fun <T> DataColumn<T>.all(predicate: Predicate<T>): Boolean = values.all(predicate)

public fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }

public fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> = slice(isMatching(predicate))

public fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values.forEach(action)

public fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values.forEachIndexed(action)

public fun <T> DataColumn<T>.groupBy(cols: Iterable<AnyCol>): GroupedDataFrame<Unit, Unit> =
    (cols + this).toDataFrame<Unit>().groupBy { cols(0 until ncol() - 1) }

public fun <T> DataColumn<T>.groupBy(vararg cols: AnyCol): GroupedDataFrame<Unit, Unit> = groupBy(cols.toList())

public fun <T, R> DataColumn<T>.map(transform: (T) -> R): DataColumn<R> {
    val collector = createDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}

public inline fun <T, reified R> DataColumn<T>.mapInline(crossinline transform: (T) -> R): DataColumn<R> {
    val newValues = Array(size()) { transform(get(it)) }.asList()
    return guessColumnType(name, newValues, suggestedType = getType<R>(), suggestedTypeIsUpperBound = false) as DataColumn<R>
}

public fun <T, R> DataColumn<T>.map(type: KType?, transform: (T) -> R): DataColumn<R> {
    if (type == null) return map(transform)
    val collector = createDataCollector<R>(size, type)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as DataColumn<R>
}

public fun <T> DataColumn<T>.first(): T = get(0)
public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null
public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)
public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)
public fun <T> DataColumn<T>.last(): T = get(size - 1)
public fun <T> DataColumn<T>.lastOrNull(): T? = if (size > 0) last() else null
public fun <C> DataColumn<C>.allNulls(): Boolean = size == 0 || all { it == null }
public fun <C> DataColumn<C>.single(): C = values.single()
