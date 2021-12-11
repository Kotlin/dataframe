package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl

// region xs

public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> = xs(*keyValues) { allDfs().take(keyValues.size) }

public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> = xsImpl(*keyValues, keyColumns = keyColumns)

// endregion
