package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.reflect.KProperty

// region DataFrame

public fun AnyFrame.countDistinct(): Int = countDistinct { all() }

public fun <T, C> DataFrame<T>.countDistinct(columns: ColumnsSelector<T, C>): Int {
    val cols = get(columns)
    return indices.distinctBy { i -> cols.map { it[i] } }.size
}

public fun <T> DataFrame<T>.countDistinct(vararg columns: String): Int = countDistinct { columns.toColumns() }
public fun <T, C> DataFrame<T>.countDistinct(vararg columns: KProperty<C>): Int = countDistinct { columns.toColumns() }
public fun <T> DataFrame<T>.countDistinct(vararg columns: Column): Int = countDistinct { columns.toColumns() }

// endregion
