package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData

operator fun ColumnData<Int>.plus(value: Int) = map { it + value }
operator fun ColumnData<Int>.minus(value: Int) = map { it - value }
operator fun Int.plus(column: ColumnData<Int>) = column.map { this + it }
operator fun Int.minus(column: ColumnData<Int>) = column.map { this - it }
operator fun ColumnData<Int>.unaryMinus() = map { -it }
operator fun ColumnData<Int>.times(value: Int) = map {it * value }
operator fun ColumnData<Int>.div(value: Int) = map { it / value }
operator fun Int.div(column: ColumnData<Int>) = column.map { this / it }