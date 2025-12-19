package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl

// region DataFrame

@[Refine Interpretable("DataFrameXs")]
public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

@[Refine Interpretable("DataFrameXs")]
public fun <T, C> DataFrame<T>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): DataFrame<T> =
    xsImpl(keyColumns, false, *keyValues)

// endregion

// region GroupBy

@[Refine Interpretable("GroupByXs")]
public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

@[Refine Interpretable("GroupByXs")]
public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> =
    xsImpl(*keyValues, keyColumns = keyColumns)

// endregion
