package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import kotlin.reflect.KProperty

public fun <T> Pivot<T>.frames(): DataRow<T> = aggregate { this }

public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<T> = delegate { aggregate(separate, body) }

public fun <T> Pivot<T>.count(): DataRow<T> = delegate { count() }
public fun <T> Pivot<T>.count(predicate: RowFilter<T>): DataRow<T> = delegate { count(predicate) }

public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> = delegate { with(expression) }

// region values

public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>
): DataRow<T> = delegate { values(dropNA, distinct, separate, columns) }
public fun <T> Pivot<T>.values(
    vararg columns: Column,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumns() }
public fun <T> Pivot<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumns() }
public fun <T> Pivot<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumns() }

public fun <T> Pivot<T>.values(dropNA: Boolean = false, distinct: Boolean = false, separate: Boolean = false): DataRow<T> = delegate { values(dropNA, distinct, separate) }

// endregion

// region reducers

public data class ReducedPivot<T>(
    @PublishedApi internal val pivot: Pivot<T>,
    @PublishedApi internal val reducer: Selector<DataFrame<T>, DataRow<T>?>
)

internal fun <T> Pivot<T>.reduce(reducer: Selector<DataFrame<T>, DataRow<T>?>) = ReducedPivot(this, reducer)

public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

public fun <T> Pivot<T>.last(): ReducedPivot<T> = reduce { lastOrNull() }

public fun <T> Pivot<T>.last(predicate: RowFilter<T>): ReducedPivot<T> = reduce { lastOrNull(predicate) }

public fun <T, R : Comparable<R>> Pivot<T>.minBy(rowExpression: RowExpression<T, R>): ReducedPivot<T> = reduce { minByOrNull(rowExpression) }
public fun <T, C : Comparable<C>> Pivot<T>.minBy(column: ColumnReference<C?>): ReducedPivot<T> = reduce { minByOrNull(column) }
public fun <T> Pivot<T>.minBy(column: String): ReducedPivot<T> = minBy(column.toColumnAccessor().cast<Comparable<Any?>>())
public fun <T, C : Comparable<C>> Pivot<T>.minBy(column: KProperty<C?>): ReducedPivot<T> = minBy(column.toColumnAccessor())

public fun <T, R : Comparable<R>> Pivot<T>.maxBy(rowExpression: RowExpression<T, R>): ReducedPivot<T> = reduce { maxByOrNull(rowExpression) }
public fun <T, C : Comparable<C>> Pivot<T>.maxBy(column: ColumnReference<C?>): ReducedPivot<T> = reduce { maxByOrNull(column) }
public fun <T> Pivot<T>.maxBy(column: String): ReducedPivot<T> = maxBy(column.toColumnAccessor().cast<Comparable<Any?>>())
public fun <T, C : Comparable<C>> Pivot<T>.maxBy(column: KProperty<C?>): ReducedPivot<T> = maxBy(column.toColumnAccessor())

// region values

public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate) }

public fun <T> ReducedPivot<T>.values(
    vararg columns: Column,
    separate: Boolean = false
): DataRow<T> = values(separate) { columns.toColumns() }

public fun <T> ReducedPivot<T>.values(
    vararg columns: String,
    separate: Boolean = false
): DataRow<T> = values(separate) { columns.toColumns() }

public fun <T> ReducedPivot<T>.values(
    vararg columns: KProperty<*>,
    separate: Boolean = false
): DataRow<T> = values(separate) { columns.toColumns() }

public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region with

public inline fun <T, reified V> ReducedPivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> = pivot.delegate { reduce(reducer).with(expression) }

// endregion

// endregion

// region min

public fun <T> Pivot<T>.min(separate: Boolean = false): DataRow<T> = delegate { min(separate) }

public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { minFor(separate, columns) }
public fun <T> Pivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> = minFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataRow<T> = minFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataRow<T> = minFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.min(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { min(columns) }
public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: String): DataRow<T> = min { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: ColumnReference<R?>): DataRow<T> = min { columns.toColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: KProperty<R?>): DataRow<T> = min { columns.toColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.minOf(rowExpression: RowExpression<T, R>): DataRow<T> = delegate { minOf(rowExpression) }

// endregion

// region max

public fun <T> Pivot<T>.max(separate: Boolean = false): DataRow<T> = delegate { max(separate) }

public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { maxFor(separate, columns) }
public fun <T> Pivot<T>.maxFor(vararg columns: String, separate: Boolean = false): DataRow<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.max(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { max(columns) }
public fun <T> Pivot<T>.max(vararg columns: String): DataRow<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.max(vararg columns: ColumnReference<R?>): DataRow<T> = max { columns.toColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.max(vararg columns: KProperty<R?>): DataRow<T> = max { columns.toColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.maxOf(rowExpression: RowExpression<T, R>): DataRow<T> = delegate { maxOf(rowExpression) }

// endregion

// region sum

public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> Pivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> =
    delegate { sumFor(separate, columns) }
public fun <T> Pivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> Pivot<T>.sum(columns: ColumnsSelector<T, C?>): DataRow<T> =
    delegate { sum(columns) }
public fun <T> Pivot<T>.sum(vararg columns: String): DataRow<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> Pivot<T>.sum(vararg columns: ColumnReference<C?>): DataRow<T> = sum { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.sum(vararg columns: KProperty<C?>): DataRow<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    delegate { sumOf(expression) }

// endregion

// region mean

public fun <T> Pivot<T>.mean(skipNA: Boolean = skipNA_default, separate: Boolean = false): DataRow<T> = meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> Pivot<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = delegate { meanFor(skipNA, separate, columns) }
public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumns() }

public fun <T, R : Number> Pivot<T>.mean(skipNA: Boolean = skipNA_default, columns: ColumnsSelector<T, R?>): DataRow<T> =
    delegate { mean(skipNA, columns) }

public inline fun <T, reified R : Number> Pivot<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>
): DataRow<T> =
    delegate { meanOf(skipNA, expression) }

// endregion

// region median

public fun <T> Pivot<T>.median(separate: Boolean = false): DataRow<T> = medianFor(separate, comparableColumns())

public fun <T, C : Comparable<C>> Pivot<T>.medianFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = delegate { medianFor(separate, columns) }
public fun <T> Pivot<T>.medianFor(vararg columns: String, separate: Boolean = false): DataRow<T> = medianFor(separate) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Pivot<T>.medianFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = medianFor(separate) { columns.toColumns() }
public fun <T, C : Comparable<C>> Pivot<T>.medianFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false
): DataRow<T> = medianFor(separate) { columns.toColumns() }

public fun <T, C : Comparable<C>> Pivot<T>.median(columns: ColumnsSelector<T, C?>): DataRow<T> = delegate { median(columns) }
public fun <T> Pivot<T>.median(vararg columns: String): DataRow<T> = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Pivot<T>.median(
    vararg columns: ColumnReference<C?>
): DataRow<T> = median { columns.toColumns() }
public fun <T, C : Comparable<C>> Pivot<T>.median(vararg columns: KProperty<C?>): DataRow<T> = median { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> Pivot<T>.medianOf(
    crossinline expression: RowExpression<T, R?>
): DataRow<T> = delegate { medianOf(expression) }

// endregion

// region std

public fun <T> Pivot<T>.std(separate: Boolean = false, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = stdFor(separate, skipNA, ddof, numberColumns())

public fun <T, R : Number> Pivot<T>.stdFor(
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { stdFor(separate, skipNA, ddof, columns) }
public fun <T> Pivot<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumnsOf() }
public fun <T, C : Number> Pivot<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.stdFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumns() }

public fun <T> Pivot<T>.std(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsSelector<T, Number?>
): DataRow<T> = delegate { std(skipNA, ddof, columns) }
public fun <T> Pivot<T>.std(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = std(skipNA, ddof) { columns.toColumns() }
public fun <T> Pivot<T>.std(vararg columns: String, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = std(skipNA, ddof) { columns.toColumnsOf() }
public fun <T> Pivot<T>.std(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = std(skipNA, ddof) { columns.toColumns() }

public inline fun <reified T : Number> Pivot<T>.stdOf(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    crossinline expression: RowExpression<T, T?>
): DataRow<T> = delegate { stdOf(skipNA, ddof, expression) }

// endregion

@PublishedApi
internal inline fun <T> Pivot<T>.delegate(crossinline body: PivotGroupBy<T>.() -> DataFrame<T>): DataRow<T> = body(groupBy { none() })[0]
