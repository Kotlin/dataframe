package org.jetbrains.dataframe

fun <T> DataFrame<T>.flatten() = flatten { all() }

fun <T, C> DataFrame<T>.flatten(selector: ColumnsSelector<T, C>): DataFrame<T> {

    val columns = getColumnsWithData(selector)
    val groupedColumns = columns.mapNotNull { if (it.isGrouped()) it.asGrouped() else null }
    val prefixes = groupedColumns.map { it.getPath() }.toSet()
    val result = move { ColumnGroup(groupedColumns.map { it.colsDfs { !it.isGrouped() } }) }
            .into {
                var first = it.path.size - 1
                while (first > 0 && !prefixes.contains(it.path.subList(0, first)))
                    first--
                if (first == 0)
                    throw Exception()
                val collapsedPath = it.path.drop(first - 1).joinToString(".")
                it.path.subList(0, first - 1) + collapsedPath
            }
    return result
}