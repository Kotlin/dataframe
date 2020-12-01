package org.jetbrains.dataframe

fun <T, C> DataFrame<T>.ungroup(selector: ColumnsSelector<T, C>): DataFrame<T> {

    val columns = getColumnsWithData(selector)
    val groupedColumns = columns.mapNotNull { if (it.isGrouped()) it.asGrouped() else null }
    val result = move { ColumnGroup(groupedColumns.map { it.all() }) }.into { it.path.subList(0, it.path.size - 2) + it.path.last() }
    return result
}