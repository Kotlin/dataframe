package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import kotlin.reflect.KProperty

// region DataFrame

public fun <T, C> DataFrame<T>.ungroup(columns: ColumnsSelector<T, C>): DataFrame<T> {
    return move { columns.toColumnSet().children() }
        .into { it.path.removeAt(it.path.size - 2).toPath() }
}

public fun <T> DataFrame<T>.ungroup(vararg columns: String): DataFrame<T> = ungroup { columns.toColumnSet() }
public fun <T> DataFrame<T>.ungroup(vararg columns: AnyColumnReference): DataFrame<T> =
    ungroup { columns.toColumnSet() }

public fun <T> DataFrame<T>.ungroup(vararg columns: KProperty<*>): DataFrame<T> = ungroup { columns.toColumnSet() }

// endregion
