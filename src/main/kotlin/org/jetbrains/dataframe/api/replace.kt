package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataCol

fun <T, C> DataFrame<T>.replace(selector: ColumnsSelector<T, C>) = ReplaceCause(this, selector)

data class ReplaceCause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

fun <T, C> ReplaceCause<T, C>.with(transform: (DataCol<C>)->DataCol<*>): DataFrame<T> {

    val removed = df.doRemove(selector)
    val toInsert = removed.removedColumns.map {
        val newCol = transform(it.data.column as DataCol<C>)
        ColumnToInsert(it.pathFromRoot(), it, newCol)
    }
    return removed.df.doInsert(toInsert)
}