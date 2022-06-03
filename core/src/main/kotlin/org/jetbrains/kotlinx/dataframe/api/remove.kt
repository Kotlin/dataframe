package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> = removeImpl(allowMissingColumns = true, columns = columns).df
public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(vararg columns: Column): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(columns: Iterable<Column>): DataFrame<T> = remove { columns.toColumnSet() }

public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)
public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(column: Column): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(columns: Iterable<Column>): DataFrame<T> = remove(columns)

// endregion
