package org.jetbrains.dataframe

fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> {
    val map = mappings.toMap()
    return columns.map {
        val newName = map[it.name] ?: it.name
        it.doRename(newName)
    }.asDataFrame()
}