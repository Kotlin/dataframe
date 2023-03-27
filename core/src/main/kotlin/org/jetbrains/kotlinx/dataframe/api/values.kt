package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.valuesImpl
import kotlin.reflect.KProperty

// region DataFrame

public fun <T, C> DataFrame<T>.values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C> =
    valuesImpl(byRow, columns)

public fun <T> DataFrame<T>.values(byRows: Boolean = false): Sequence<Any?> = values(byRows) { all() }

public fun <T, C> DataFrame<T>.valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> =
    values(byRow, columns).filterNotNull()

public fun <T> DataFrame<T>.valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

// endregion

// region GroupBy

public fun <T> Grouped<T>.values(
    vararg columns: AnyColumnReference,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

public fun <T> Grouped<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<T> = aggregate { internal().columnValues(columns, true, dropNA, distinct) }

public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

public fun <T, G> ReducedGroupBy<T, G>.values(
    vararg columns: AnyColumnReference,
): DataFrame<G> = values { columns.toColumnSet() }

public fun <T, G> ReducedGroupBy<T, G>.values(
    vararg columns: String,
): DataFrame<G> = values { columns.toColumnSet() }

public fun <T, G> ReducedGroupBy<T, G>.values(
    vararg columns: KProperty<*>,
): DataFrame<G> = values { columns.toColumnSet() }

public fun <T, G> ReducedGroupBy<T, G>.values(
    columns: ColumnsForAggregateSelector<G, *>
): DataFrame<G> = groupBy.aggregate { internal().columnValues(columns, reducer) }

// endregion

// region Pivot

public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<T> = delegate { values(dropNA, distinct, separate, columns) }

public fun <T> Pivot<T>.values(
    vararg columns: AnyColumnReference,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> Pivot<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> Pivot<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> Pivot<T>.values(dropNA: Boolean = false, distinct: Boolean = false, separate: Boolean = false): DataRow<T> = delegate { values(dropNA, distinct, separate) }

// endregion

// region ReducedPivot

public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate) }

public fun <T> ReducedPivot<T>.values(
    vararg columns: AnyColumnReference,
    separate: Boolean = false,
): DataRow<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivot<T>.values(
    vararg columns: String,
    separate: Boolean = false,
): DataRow<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivot<T>.values(
    vararg columns: KProperty<*>,
    separate: Boolean = false,
): DataRow<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.values(dropNA: Boolean = false, distinct: Boolean = false, separate: Boolean = false): DataFrame<T> = values(dropNA, distinct, separate, remainingColumnsSelector())

public fun <T> PivotGroupBy<T>.values(
    vararg columns: AnyColumnReference,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> PivotGroupBy<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> PivotGroupBy<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<T> =
    aggregate(separate = separate) { internal().columnValues(columns, false, dropNA, distinct) }

// endregion

// region ReducedPivotGroupBy

public fun <T> ReducedPivotGroupBy<T>.values(
    separate: Boolean = false
): DataFrame<T> = values(separate, pivot.remainingColumnsSelector())

public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: AnyColumnReference,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: String,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: KProperty<*>,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivotGroupBy<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>
): DataFrame<T> = pivot.aggregate(separate = separate) { internal().columnValues(columns, reducer) }

// endregion
