package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn

operator fun DataColumn<Int>.plus(value: Int) = map { it + value }
operator fun DataColumn<Int>.minus(value: Int) = map { it - value }
operator fun Int.plus(column: DataColumn<Int>) = column.map { this + it }
operator fun Int.minus(column: DataColumn<Int>) = column.map { this - it }
operator fun DataColumn<Int>.unaryMinus() = map { -it }
operator fun DataColumn<Int>.times(value: Int) = map {it * value }
operator fun DataColumn<Int>.div(value: Int) = map { it / value }
operator fun Int.div(column: DataColumn<Int>) = column.map { this / it }