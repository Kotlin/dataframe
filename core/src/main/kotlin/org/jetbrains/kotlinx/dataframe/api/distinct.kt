package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.reflect.KProperty

// region DataFrame

// region distinct

public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> = distinct {
    val set = columns.toColumns()
    set
}

public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumns() }

public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumns() }

@JvmName("distinctT")
public fun <T> DataFrame<T>.distinct(columns: Iterable<String>): DataFrame<T> = distinct { columns.toColumns() }

public fun <T> DataFrame<T>.distinct(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    distinct { columns.toColumnSet() }

// endregion

// region distinctBy

public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> = distinctBy { columns.toColumns() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumns() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumns() }

@JvmName("distinctByT")
public fun <T> DataFrame<T>.distinctBy(columns: Iterable<String>): DataFrame<T> = distinctBy { columns.toColumns() }

public fun <T> DataFrame<T>.distinctBy(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}
// endregion

// endregion
