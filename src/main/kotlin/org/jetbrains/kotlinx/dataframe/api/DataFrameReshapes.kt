package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.pathOf
import kotlin.reflect.KProperty

// region pivot

public fun <T> DataFrame<T>.pivot(columns: ColumnsSelector<T, *>): PivotedDataFrame<T> = PivotedDataFrameImpl(this, columns)
public fun <T> DataFrame<T>.pivot(vararg columns: String): PivotedDataFrame<T> = pivot { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column): PivotedDataFrame<T> = pivot { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: KProperty<*>): PivotedDataFrame<T> = pivot { columns.toColumns() }

public fun <T, P : GroupedPivot<T>> P.withGrouping(group: ColumnGroupReference): P = withGrouping(group.path()) as P
public fun <T, P : GroupedPivot<T>> P.withGrouping(groupName: String): P = withGrouping(pathOf(groupName)) as P

// endregion

// region gather

public fun <T, C> DataFrame<T>.gather(dropNulls: Boolean = true, selector: ColumnsSelector<T, C?>): GatherClause<T, C, String, C> = GatherClause(this, selector as ColumnsSelector<T, C>, null, dropNulls, { it }, null)
public fun <T> DataFrame<T>.gather(vararg columns: String, dropNulls: Boolean = true): GatherClause<T, Any?, String, Any?> = gather(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: ColumnReference<C>, dropNulls: Boolean = true): GatherClause<T, C, String, C> = gather(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: KProperty<C>, dropNulls: Boolean = true): GatherClause<T, C, String, C> = gather(dropNulls) { columns.toColumns() }

public fun <T, C, K, R> GatherClause<T, C, K, R>.where(filter: Predicate<C>): GatherClause<T, C, K, R> = copy(filter = filter)
public fun <T, C, K, R> GatherClause<T, C, *, R>.mapNames(transform: (String) -> K): GatherClause<T, C, K, R> = GatherClause(df, columns, filter, dropNulls, transform, valueTransform)
public fun <T, C, K, R> GatherClause<T, C, K, *>.map(transform: (C) -> R): GatherClause<T, C, K, R> = GatherClause(df, columns, filter, dropNulls, nameTransform, transform)

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
