package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl

// region into

public fun <T, G> GroupBy<T, G>.into(column: String): DataFrame<T> = toDataFrame(column)
public fun <T> GroupBy<T, *>.into(column: ColumnAccessor<AnyFrame>): DataFrame<T> = toDataFrame(column.name())

// endregion

// region xs

public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> = xs(*keyValues) { dfsLeafs().take(keyValues.size) }

public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> = xsImpl(*keyValues, keyColumns = keyColumns)

// endregion

// region concat

public fun <T, G> GroupBy<T, G>.concat(): DataFrame<G> = groups.concat().cast()

// endregion
