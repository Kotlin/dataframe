package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.flatten(): DataFrame<T> = flatten { all() }

public fun <T, C> DataFrame<T>.flatten(columns: ColumnsSelector<T, C>): DataFrame<T> = flattenImpl(columns)

public fun <T> DataFrame<T>.flatten(vararg columns: String): DataFrame<T> = flattenImpl { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.flatten(vararg columns: KProperty<C>): DataFrame<T> =
    flattenImpl { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.flatten(vararg columns: ColumnReference<C>): DataFrame<T> =
    flattenImpl { columns.toColumnSet() }

// endregion
