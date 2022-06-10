package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T, C> DataFrame<T>.replace(columns: ColumnsSelector<T, C>): ReplaceCause<T, C> = ReplaceCause(this, columns)
public fun <T> DataFrame<T>.replace(vararg columns: String): ReplaceCause<T, Any?> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(vararg columns: ColumnReference<C>): ReplaceCause<T, C> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(vararg columns: KProperty<C>): ReplaceCause<T, C> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(columns: Iterable<ColumnReference<C>>): ReplaceCause<T, C> = replace { columns.toColumnSet() }

public fun <T> DataFrame<T>.replaceAll(
    vararg valuePairs: Pair<Any?, Any?>,
    columns: ColumnsSelector<T, *> = { allDfs() }
): DataFrame<T> {
    val map = valuePairs.toMap()
    return update(columns).with { map[it] ?: it }
}

public data class ReplaceCause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public fun <T, C> ReplaceCause<T, C>.with(vararg columns: AnyCol): DataFrame<T> = with(columns.toList())

public fun <T, C> ReplaceCause<T, C>.with(newColumns: List<AnyCol>): DataFrame<T> {
    var index = 0
    return with {
        require(index < newColumns.size) { "Insufficient number of new columns in 'replace': ${newColumns.size} instead of ${df[columns].size}" }
        newColumns[index++]
    }
}

public fun <T, C> ReplaceCause<T, C>.with(transform: ColumnsContainer<T>.(DataColumn<C>) -> AnyBaseCol): DataFrame<T> {
    val removeResult = df.removeImpl(columns = columns)
    val toInsert = removeResult.removedColumns.map {
        val newCol = transform(df, it.data.column as DataColumn<C>)
        ColumnToInsert(it.pathFromRoot().dropLast(1) + newCol.name, newCol, it)
    }
    return removeResult.df.insertImpl(toInsert)
}
