package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.reorderImpl
import kotlin.reflect.KProperty

// region DataFrame

public data class Reorder<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val inFrameColumns: Boolean,
) {
    public fun <R> cast(): Reorder<T, R> = this as Reorder<T, R>
}

public fun <T, C> DataFrame<T>.reorder(selector: ColumnsSelector<T, C>): Reorder<T, C> = Reorder(this, selector, false)
public fun <T, C> DataFrame<T>.reorder(vararg columns: ColumnReference<C>): Reorder<T, C> =
    reorder { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.reorder(vararg columns: KProperty<C>): Reorder<T, C> = reorder { columns.toColumnSet() }
public fun <T> DataFrame<T>.reorder(vararg columns: String): Reorder<T, *> = reorder { columns.toColumnSet() }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.by(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(false, expression)

public fun <T, C> Reorder<T, C>.byName(desc: Boolean = false): DataFrame<T> =
    if (desc) byDesc { it.name } else by { it.name }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.byDesc(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(true, expression)

public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    dfs: Boolean = true,
    desc: Boolean = false,
    expression: Selector<AnyCol, V>
): DataFrame<T> = Reorder(this, { if (dfs) allDfs(true) else all() }, dfs).reorderImpl(desc, expression)

public fun <T> DataFrame<T>.reorderColumnsByName(dfs: Boolean = true, desc: Boolean = false): DataFrame<T> =
    reorderColumnsBy(dfs, desc) { name() }

// endregion
