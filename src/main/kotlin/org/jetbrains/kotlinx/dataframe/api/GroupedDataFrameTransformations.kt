package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupedPivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupedPivotInsideAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region pivot GroupedDataFrame

public fun <G> GroupedDataFrame<*, G>.pivot(inward: Boolean? = null, columns: ColumnsSelector<G, *>): GroupedPivot<G> = GroupedPivotImpl(this, columns, inward)
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: Column, inward: Boolean? = null): GroupedPivot<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: String, inward: Boolean? = null): GroupedPivot<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): GroupedPivot<G> = pivot(inward) { columns.toColumns() }

// endregion

// region pivot inside GroupedDataFrame aggregate

public fun <T> AggregateGroupedDsl<T>.pivot(inward: Boolean? = null, columns: ColumnsSelector<T, *>): GroupedPivot<T> =
    GroupedPivotInsideAggregateImpl(this, columns, inward)
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: String, inward: Boolean? = null): GroupedPivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: Column, inward: Boolean? = null): GroupedPivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): GroupedPivot<T> = pivot(inward) { columns.toColumns() }

// endregion
