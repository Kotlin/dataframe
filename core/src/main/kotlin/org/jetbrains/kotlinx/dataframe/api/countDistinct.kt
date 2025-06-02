package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

public fun AnyFrame.countDistinct(): Int = countDistinct { all() }

public fun <T, C> DataFrame<T>.countDistinct(columns: ColumnsSelector<T, C>): Int {
    val cols = get(columns)
    return indices.distinctBy { i -> cols.map { it[i] } }.size
}

public fun <T> DataFrame<T>.countDistinct(vararg columns: String): Int = countDistinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.countDistinct(vararg columns: KProperty<C>): Int =
    countDistinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.countDistinct(vararg columns: AnyColumnReference): Int =
    countDistinct { columns.toColumnSet() }

// endregion
