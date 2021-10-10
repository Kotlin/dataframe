package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.Aggregatable
import org.jetbrains.dataframe.aggregation.AggregateColumnsSelector
import org.jetbrains.dataframe.aggregation.PivotAggregateBody
import org.jetbrains.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public interface PivotAggregations<T> : Aggregatable<T> {

    public fun <R> aggregate(body: PivotAggregateBody<T, R>): DataRow<T> = asGrouped().aggregate(body)[0]

    public fun frames(): DataRow<T> = aggregate { this }

    public fun groupBy(columns: ColumnsSelector<T, *>): GroupedPivotAggregations<T>
    public fun groupBy(vararg columns: String): GroupedPivotAggregations<T> = groupBy { columns.toColumns() }
    public fun groupBy(vararg columns: Column): GroupedPivotAggregations<T> = groupBy { columns.toColumns() }

    public fun count(predicate: RowFilter<T>? = null): DataRow<T> = asGrouped().count(predicate)[0]

    public fun values(vararg columns: Column, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }
    public fun values(vararg columns: String, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }
    public fun values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }
    public fun values(separate: Boolean = false, columns: AggregateColumnsSelector<T, *>): DataRow<T> = asGrouped()
        .values(separate, columns)[0]

    public fun values(separate: Boolean = false): DataRow<T> = asGrouped().values(separate)[0]
}

// region min

public fun <T> PivotedDataFrame<T>.min(separate: Boolean = false): DataRow<T> = asGrouped().min(separate)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> = asGrouped()
    .minFor(separate, columns)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.min(columns: ColumnsSelector<T, R?>): DataRow<T> = asGrouped()
    .min(columns)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minOf(rowExpression: RowSelector<T, R>): DataRow<T> = asGrouped()
    .minOf(rowExpression)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minBy(rowExpression: RowSelector<T, R>): DataRow<T> = asGrouped()
    .minBy(rowExpression)[0]

// endregion

// region max

public fun <T> PivotedDataFrame<T>.max(separate: Boolean = false): DataRow<T> = asGrouped().max(separate)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> = asGrouped()
    .maxFor(separate, columns)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.max(columns: ColumnsSelector<T, R?>): DataRow<T> = asGrouped()
    .max(columns)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxOf(rowExpression: RowSelector<T, R>): DataRow<T> = asGrouped()
    .maxOf(rowExpression)[0]

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxBy(rowExpression: RowSelector<T, R>): DataRow<T> = asGrouped()
    .maxBy(rowExpression)[0]

// endregion

// region sum

public fun <T> PivotedDataFrame<T>.sum(separate: Boolean = false): DataRow<T> = asGrouped().sum(separate)[0]

public fun <T, R : Number> PivotedDataFrame<T>.sumFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R>
): DataRow<T> =
    asGrouped().sumFor(separate, columns)[0]

// endregion

@PublishedApi
internal fun <T> PivotAggregations<T>.asGrouped(): GroupedPivotAggregations<T> = groupBy { none() }

// region inlines

public inline fun <T, reified V> PivotAggregations<T>.with(noinline selector: RowSelector<T, V>): DataRow<T> = asGrouped()
    .with(selector)[0]

public inline fun <T, reified R : Number> PivotAggregations<T>.sumOf(crossinline selector: RowSelector<T, R>): DataRow<T> = asGrouped()
    .sumOf(selector)[0]

// endregion
