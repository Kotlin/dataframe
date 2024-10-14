package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import kotlin.reflect.KProperty

public const val FLATTEN_DEFAULT_SEPARATOR: String = "_"

// region DataFrame

@Refine
@Interpretable("FlattenDefault")
public fun <T> DataFrame<T>.flatten(
    keepParentNameForColumns: Boolean = false,
    separator: String = FLATTEN_DEFAULT_SEPARATOR,
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { all() }

@Refine
@Interpretable("Flatten0")
public fun <T, C> DataFrame<T>.flatten(
    keepParentNameForColumns: Boolean = false,
    separator: String = FLATTEN_DEFAULT_SEPARATOR,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = flattenImpl(columns, keepParentNameForColumns, separator)

public fun <T> DataFrame<T>.flatten(
    vararg columns: String,
    keepParentNameForColumns: Boolean = false,
    separator: String = FLATTEN_DEFAULT_SEPARATOR,
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.flatten(
    vararg columns: ColumnReference<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = FLATTEN_DEFAULT_SEPARATOR,
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.flatten(
    vararg columns: KProperty<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = FLATTEN_DEFAULT_SEPARATOR,
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

// endregion
