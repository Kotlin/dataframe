package org.jetbrains.dataframe

fun <T, C> DataFrame<T>.ungroup(selector: ColumnsSelector<T, C>): DataFrame<T> {

    return move { selector.toColumns().children() }
            .into { it.path.removeAt(it.path.size - 2) }
}