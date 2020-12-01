package org.jetbrains.dataframe

fun <T> DataFrame<T>.mergeRows(selector: ColumnsSelector<T, *>): DataFrame<T> {

    return groupBy { allExcept(selector) }.modify {
        val updated = update(selector).with2 { row, column -> if(row.index == 0) column.toList() else emptyList() }
        updated[0..0]
    }.ungroup()
}