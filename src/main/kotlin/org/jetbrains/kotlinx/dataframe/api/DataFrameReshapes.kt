package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KProperty

// region pivot

public interface PivotDsl<out T> : ColumnsSelectionDsl<T> {

    public infix fun <C> ColumnSet<C>.then(other: ColumnSet<C>): ColumnSet<C> = PivotChainColumnSet(this, other)

    public infix fun <C> String.then(other: ColumnSet<C>): ColumnSet<C> = toColumnOf<C>() then other

    public infix fun <C> ColumnSet<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

    public infix fun String.then(other: String): ColumnSet<Any?> = toColumnAccessor() then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: ColumnSet<C>): ColumnSet<C> = toColumnAccessor() then other

    public infix fun <C> ColumnSet<C>.then(other: KProperty<C>): ColumnSet<C> = this then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: KProperty<C>): ColumnSet<C> = toColumnAccessor() then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: String): ColumnSet<C> = toColumnAccessor() then other.toColumnOf()

    public infix fun <C> String.then(other: KProperty<C>): ColumnSet<C> = toColumnOf<C>() then other.toColumnAccessor()
}

public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnSet<C>>

public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): PivotedDataFrame<T> = PivotedDataFrameImpl(this, columns, inward)
public fun <T> DataFrame<T>.pivot(vararg columns: String, inward: Boolean? = false): PivotedDataFrame<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column, inward: Boolean? = false): PivotedDataFrame<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = false): PivotedDataFrame<T> = pivot(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotMatches(inward: Boolean? = null, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().matches()
public fun <T> DataFrame<T>.pivotMatches(vararg columns: String, inward: Boolean? = null): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: Column, inward: Boolean? = null): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean? = null): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotCount(inward: Boolean? = null, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().count()
public fun <T> DataFrame<T>.pivotCount(vararg columns: String, inward: Boolean? = null): DataFrame<T> = pivotCount(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCount(vararg columns: Column, inward: Boolean? = null): DataFrame<T> = pivotCount(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCount(vararg columns: KProperty<*>, inward: Boolean? = null): DataFrame<T> = pivotCount(inward) { columns.toColumns() }

// endregion

// region gather

public fun <T, C> DataFrame<T>.gather(dropNulls: Boolean = true, selector: ColumnsSelector<T, C?>): GatherClause<T, C, String, C> = GatherClause(this, selector as ColumnsSelector<T, C>, null, dropNulls, { it }, null)
public fun <T> DataFrame<T>.gather(vararg columns: String, dropNulls: Boolean = true): GatherClause<T, Any?, String, Any?> = gather(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: ColumnReference<C>, dropNulls: Boolean = true): GatherClause<T, C, String, C> = gather(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: KProperty<C>, dropNulls: Boolean = true): GatherClause<T, C, String, C> = gather(dropNulls) { columns.toColumns() }

public fun <T, C, K, R> GatherClause<T, C, K, R>.where(filter: Predicate<C>): GatherClause<T, C, K, R> = copy(filter = filter)
public fun <T, C, K, R> GatherClause<T, C, *, R>.mapKeys(transform: (String) -> K): GatherClause<T, C, K, R> = GatherClause(df, columns, filter, dropNulls, transform, valueTransform)
public fun <T, C, K, R> GatherClause<T, C, K, *>.mapValues(transform: (C) -> R): GatherClause<T, C, K, R> = GatherClause(df, columns, filter, dropNulls, nameTransform, transform)

public data class GatherClause<T, C, K, R>(
    val df: DataFrame<T>,
    val columns: ColumnsSelector<T, C>,
    val filter: ((C) -> Boolean)? = null,
    val dropNulls: Boolean = true,
    val nameTransform: ((String) -> K),
    val valueTransform: ((C) -> R)? = null
)

public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(
    keyColumn: String,
    valueColumn: String? = null
): DataFrame<T> = gatherImpl(this, keyColumn, valueColumn, getType<K>(), getType<R>())
public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(
    keyColumn: ColumnAccessor<K>,
    valueColumn: ColumnAccessor<R>? = null
): DataFrame<T> = into(keyColumn.name(), valueColumn?.name)
public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(
    keyColumn: KProperty<K>,
    valueColumn: KProperty<R>? = null
): DataFrame<T> = into(keyColumn.columnName, valueColumn?.columnName)

// endregion
