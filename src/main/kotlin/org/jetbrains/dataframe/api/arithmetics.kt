package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataCol

operator fun DataCol<Int>.plus(value: Int) = map { it + value }
operator fun DataCol<Int>.minus(value: Int) = map { it - value }
operator fun Int.plus(column: DataCol<Int>) = column.map { this + it }
operator fun Int.minus(column: DataCol<Int>) = column.map { this - it }
operator fun DataCol<Int>.unaryMinus() = map { -it }
operator fun DataCol<Int>.times(value: Int) = map {it * value }
operator fun DataCol<Int>.div(value: Int) = map { it / value }
operator fun Int.div(column: DataCol<Int>) = column.map { this / it }