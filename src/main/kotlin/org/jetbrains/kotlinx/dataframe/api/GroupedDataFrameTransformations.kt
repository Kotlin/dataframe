package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupedPivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupedPivotInsideAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region pivot GroupedDataFrame

public fun <G> GroupedDataFrame<*, G>.pivot(columns: ColumnsSelector<G, *>): GroupedPivot<G> = GroupedPivotImpl(this, columns)
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: Column): GroupedPivot<G> = pivot { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: String): GroupedPivot<G> = pivot { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: KProperty<*>): GroupedPivot<G> = pivot { columns.toColumns() }

// endregion

// region pivot inside GroupedDataFrame aggregate

public fun <T> AggregateGroupedDsl<T>.pivot(columns: ColumnsSelector<T, *>): GroupedPivot<T> =
    GroupedPivotInsideAggregateImpl(this, columns)
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: String): GroupedPivot<T> = pivot { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: Column): GroupedPivot<T> = pivot { columns.toColumns() }

// endregion
