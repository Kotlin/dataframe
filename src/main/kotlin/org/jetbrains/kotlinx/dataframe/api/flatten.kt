package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl

// region DataFrame

public fun <T> DataFrame<T>.flatten(): DataFrame<T> = flatten { all() }

public fun <T, C> DataFrame<T>.flatten(
    columns: ColumnsSelector<T, C>
): DataFrame<T> = flattenImpl(columns)

// endregion
