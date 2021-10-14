package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.DataRowBase
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.owner
import org.jetbrains.kotlinx.dataframe.prev
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability

public fun AnyRow.isEmpty(): Boolean = owner.columns().all { it[index] == null }
public fun AnyRow.isNotEmpty(): Boolean = !isEmpty()

public inline fun <T, reified R : Number> DataRow<T>.diff(selector: RowSelector<T, R>): Number = when (R::class) {
    Double::class -> prev?.let { (selector(this) as Double) - (selector(it) as Double) } ?: .0
    Int::class -> prev?.let { (selector(this) as Int) - (selector(it) as Int) } ?: 0
    Long::class -> prev?.let { (selector(this) as Long) - (selector(it) as Long) } ?: 0
    else -> throw NotImplementedError()
}

public fun <T> DataRow<T>.movingAverage(k: Int, selector: RowSelector<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumByDouble {
        count++
        selector(it).toDouble()
    } / count
}

public fun <T> DataRowBase<T>.duplicate(n: Int): DataFrame<T> = (this as DataRow<T>).owner.columns().mapIndexed { colIndex, col ->
    when (col) {
        is ColumnGroup<*> -> DataColumn.create(col.name, col[index].duplicate(n))
        else -> {
            val value = col[index]
            if (value is AnyFrame) {
                DataColumn.create(col.name, MutableList(n) { value })
            } else DataColumn.create(col.name, MutableList(n) { value }, col.type.withNullability(value == null))
        }
    }
}.toDataFrame()
