package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.reorderImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

public data class Reorder<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val inFrameColumns: Boolean,
) {
    public fun <R> cast(): Reorder<T, R> = this as Reorder<T, R>
}

@Interpretable("Reorder")
public fun <T, C> DataFrame<T>.reorder(selector: ColumnsSelector<T, C>): Reorder<T, C> = Reorder(this, selector, false)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.reorder(vararg columns: ColumnReference<C>): Reorder<T, C> =
    reorder { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.reorder(vararg columns: KProperty<C>): Reorder<T, C> = reorder { columns.toColumnSet() }

public fun <T> DataFrame<T>.reorder(vararg columns: String): Reorder<T, *> = reorder { columns.toColumnSet() }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.by(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(false, expression)

@Refine
@Interpretable("ByName")
public fun <T, C> Reorder<T, C>.byName(desc: Boolean = false): DataFrame<T> =
    if (desc) byDesc { it.name } else by { it.name }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.byDesc(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(true, expression)

public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    atAnyDepth: Boolean = true,
    desc: Boolean = false,
    expression: Selector<AnyCol, V>,
): DataFrame<T> =
    Reorder(
        df = this,
        columns = { if (atAnyDepth) colsAtAnyDepth() else all() },
        inFrameColumns = atAnyDepth,
    ).reorderImpl(desc, expression)

@Refine
@Interpretable("ReorderColumnsByName")
public fun <T> DataFrame<T>.reorderColumnsByName(atAnyDepth: Boolean = true, desc: Boolean = false): DataFrame<T> =
    reorderColumnsBy(atAnyDepth, desc) { name() }

// endregion
