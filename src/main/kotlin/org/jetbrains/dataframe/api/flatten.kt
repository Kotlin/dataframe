package org.jetbrains.dataframe

fun <T> DataFrame<T>.flatten() = flatten { all() }

fun <T, C> DataFrame<T>.flatten(selector: ColumnsSelector<T, C>): DataFrame<T> {

    val columns = getColumnsWithPaths(selector).filter { it.isGroup() }
    val prefixes = columns.map { it.path }.toSet()
    val result = move { columns.toColumnSet().colsDfs { !it.isGroup() } }
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