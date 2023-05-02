package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.toIndices
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> = indices.filter {
    predicate(get(it))
}.let { get(it) }

// endregion

// region DataFrame

public fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices.filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

public fun <T> DataFrame<T>.filterBy(column: ColumnSelector<T, Boolean>): DataFrame<T> = getRows(getColumn(column).toList().toIndices())
public fun <T> DataFrame<T>.filterBy(column: ColumnReference<Boolean>): DataFrame<T> = filterBy { column }
public fun <T> DataFrame<T>.filterBy(column: String): DataFrame<T> = filterBy { column.toColumnOf() }
public fun <T> DataFrame<T>.filterBy(column: KProperty<Boolean>): DataFrame<T> = filterBy { column.toColumnAccessor() }

// endregion
