package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.unfoldImpl
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.unfold(
    vararg props: KProperty<*>,
    maxDepth: Int = 0,
): AnyCol = unfoldImpl(skipPrimitive = true) { properties(roots = props, maxDepth = maxDepth) }

public fun <T> DataColumn<T>.unfold(body: CreateDataFrameDsl<T>.() -> Unit): AnyCol =
    unfoldImpl(skipPrimitive = false, body)

// endregion
// region DataFrame

public fun <T, C> DataFrame<T>.unfold(columns: ColumnsSelector<T, C>): UnfoldingDataFrame<T, C> =
    UnfoldingDataFrame(
        originalDf = this,
        unfoldedDf = replace(columns).with { it.unfold() },
        columns = columns,
    )

public fun <T> DataFrame<T>.unfold(vararg columns: String): UnfoldingDataFrame<T, *> = unfold { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.unfold(vararg columns: ColumnReference<C>): UnfoldingDataFrame<T, C> =
    unfold { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.unfold(vararg columns: KProperty<C>): UnfoldingDataFrame<T, C> =
    unfold { columns.toColumnSet() }

public class UnfoldingDataFrame<T, C>(
    internal val originalDf: DataFrame<T>,
    private val unfoldedDf: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
) : DataFrame<T> by unfoldedDf

public fun <T, C> UnfoldingDataFrame<T, C>.by(body: CreateDataFrameDsl<C>.() -> Unit): DataFrame<T> =
    originalDf.replace(columns).with { it.unfoldImpl(skipPrimitive = false, body) }

public fun <T, C> UnfoldingDataFrame<T, C>.by(
    vararg props: KProperty<*>,
    maxDepth: Int = 0,
): DataFrame<T> = originalDf.replace(*props).with { it.unfold(props = props, maxDepth) }

// endregion
// region replace

public fun <T, C> ReplaceClause<T, C>.byUnfolding(
    vararg props: KProperty<*>,
    maxDepth: Int = 0,
): DataFrame<T> = with { it.unfold(props = props, maxDepth) }

@Refine
@Interpretable("ReplaceUnfold1")
public fun <T, C> ReplaceClause<T, C>.byUnfolding(body: CreateDataFrameDsl<C>.() -> Unit): DataFrame<T> =
    with { it.unfoldImpl(skipPrimitive = false, body) }

// endregion
