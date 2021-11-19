package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByAggregatePivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotGroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region pivot GroupedDataFrame

public fun <G> GroupBy<*, G>.pivot(inward: Boolean? = null, columns: ColumnsSelector<G, *>): PivotGroupBy<G> = PivotGroupByImpl(this, columns, inward)
public fun <G> GroupBy<*, G>.pivot(vararg columns: Column, inward: Boolean? = null): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivot(vararg columns: String, inward: Boolean? = null): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }

// endregion

// region pivot inside GroupedDataFrame aggregate

public fun <T> AggregateGroupedDsl<T>.pivot(inward: Boolean? = null, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
    GroupByAggregatePivotImpl(this, columns, inward)
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: String, inward: Boolean? = null): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: Column, inward: Boolean? = null): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }

// endregion

public fun <T, G> GroupBy<T, G>.into(column: String): DataFrame<T> = toDataFrame(column)
public fun <T> GroupBy<T, *>.into(column: ColumnAccessor<AnyFrame>): DataFrame<T> = toDataFrame(column.name())
