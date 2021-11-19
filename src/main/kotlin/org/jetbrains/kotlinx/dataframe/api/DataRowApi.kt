package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.prev
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability

public fun AnyRow.isEmpty(): Boolean = owner.columns().all { it[index] == null }
public fun AnyRow.isNotEmpty(): Boolean = !isEmpty()

public inline fun <T, reified R : Number> DataRow<T>.diff(expression: RowExpression<T, R>): Number = when (R::class) {
    Double::class -> prev()?.let { (expression(this) as Double) - (expression(it) as Double) } ?: .0
    Int::class -> prev()?.let { (expression(this) as Int) - (expression(it) as Int) } ?: 0
    Long::class -> prev()?.let { (expression(this) as Long) - (expression(it) as Long) } ?: 0
    else -> throw NotImplementedError()
}

public fun <T> DataRow<T>.movingAverage(k: Int, expression: RowExpression<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumByDouble {
        count++
        expression(it).toDouble()
    } / count
}

public fun <T> DataRow<T>.duplicate(n: Int): DataFrame<T> = this.owner.columns().mapIndexed { colIndex, col ->
    when (col) {
        is ColumnGroup<*> -> DataColumn.createColumnGroup(col.name, col[index].duplicate(n))
        else -> {
            val value = col[index]
            if (value is AnyFrame) {
                DataColumn.createFrameColumn(col.name, MutableList(n) { value })
            } else DataColumn.createValueColumn(col.name, MutableList(n) { value }, col.type.withNullability(value == null))
        }
    }
}.toDataFrame().cast()
