package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.AggregateColumnsSelector
import org.jetbrains.dataframe.aggregation.PivotAggregateBody
import org.jetbrains.dataframe.columns.ColumnReference
import org.jetbrains.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.dataframe.impl.aggregation.numberColumns
import org.jetbrains.dataframe.impl.columns.toColumns
import org.jetbrains.dataframe.impl.columns.toColumnsOf
import org.jetbrains.dataframe.impl.columns.toComparableColumns
import org.jetbrains.dataframe.impl.columns.toNumberColumns
import kotlin.reflect.KProperty

public fun <T> PivotedDataFrame<T>.asDataRow(): DataRow<T> = aggregate { this }

public fun <T, R> PivotedDataFrame<T>.aggregate(body: PivotAggregateBody<T, R>): DataRow<T> = delegate { aggregate(body) }

public fun <T> PivotedDataFrame<T>.count(predicate: RowFilter<T>? = null): DataRow<T> = delegate { count(predicate) }

public inline fun <T, reified V> PivotedDataFrame<T>.with(noinline selector: RowSelector<T, V>): DataRow<T> = delegate { with(selector) }

// region values

public fun <T> PivotedDataFrame<T>.values(separate: Boolean = false, columns: AggregateColumnsSelector<T, *>): DataRow<T> = delegate { values(separate, columns) }
public fun <T> PivotedDataFrame<T>.values(vararg columns: Column, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.values(vararg columns: String, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<T> = values(separate) { columns.toColumns() }

public fun <T> PivotedDataFrame<T>.values(separate: Boolean = false): DataRow<T> = delegate { values(separate) }

// endregion

// region min

public fun <T> PivotedDataFrame<T>.min(separate: Boolean = false): DataRow<T> = delegate { min(separate) }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> = delegate { minFor(separate, columns) }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> = minFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataRow<T> = minFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataRow<T> = minFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.min(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { min(columns) }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.min(vararg columns: String): DataRow<T> = min { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.min(vararg columns: ColumnReference<R?>): DataRow<T> = min { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.min(vararg columns: KProperty<R?>): DataRow<T> = min { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minOf(rowExpression: RowSelector<T, R>): DataRow<T> = delegate { minOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.minBy(rowExpression: RowSelector<T, R>): DataRow<T> = delegate { minBy(rowExpression) }
public fun <T> PivotedDataFrame<T>.minBy(column: String): DataRow<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.minBy(column: ColumnReference<C?>): DataRow<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.minBy(column: KProperty<C?>): DataRow<T> = aggregate { minBy(column) }

// endregion

// region max

public fun <T> PivotedDataFrame<T>.max(separate: Boolean = false): DataRow<T> = delegate { max(separate) }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> = delegate { maxFor(separate, columns) }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxFor(vararg columns: String, separate: Boolean = false): DataRow<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.max(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { max(columns) }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.max(vararg columns: String): DataRow<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.max(vararg columns: ColumnReference<R?>): DataRow<T> = max { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotedDataFrame<T>.max(vararg columns: KProperty<R?>): DataRow<T> = max { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxOf(rowExpression: RowSelector<T, R>): DataRow<T> = delegate { maxOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotedDataFrame<T>.maxBy(rowExpression: RowSelector<T, R>): DataRow<T> = delegate { maxBy(rowExpression) }
public fun <T> PivotedDataFrame<T>.maxBy(column: String): DataRow<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.maxBy(column: ColumnReference<C?>): DataRow<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.maxBy(column: KProperty<C?>): DataRow<T> = aggregate { maxBy(column) }

// endregion

// region sum

public fun <T> PivotedDataFrame<T>.sum(separate: Boolean = false): DataRow<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> PivotedDataFrame<T>.sumFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> =
    delegate { sumFor(separate, columns) }
public fun <T> PivotedDataFrame<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> PivotedDataFrame<T>.sum(columns: ColumnsSelector<T, C?>): DataRow<T> =
    delegate { sum(columns) }
public fun <T> PivotedDataFrame<T>.sum(vararg columns: String): DataRow<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.sum(vararg columns: ColumnReference<C?>): DataRow<T> = sum { columns.toColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.sum(vararg columns: KProperty<C?>): DataRow<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> PivotedDataFrame<T>.sumOf(crossinline selector: RowSelector<T, R>): DataRow<T> =
    delegate { sumOf(selector) }

// endregion

// region mean

public fun <T> PivotedDataFrame<T>.mean(skipNa: Boolean = false, separate: Boolean = false): DataRow<T> = meanFor(skipNa, separate, numberColumns())

public fun <T, C : Number> PivotedDataFrame<T>.meanFor(
    skipNa: Boolean = false,
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, C?>
): DataRow<T> = delegate { meanFor(skipNa, separate, columns) }
public fun <T> PivotedDataFrame<T>.meanFor(
    vararg columns: String,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNa, separate) { columns.toNumberColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNa, separate) { columns.toColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataRow<T> = meanFor(skipNa, separate) { columns.toColumns() }

public fun <T, R : Number> PivotedDataFrame<T>.mean(skipNa: Boolean = true, columns: ColumnsSelector<T, R?>): DataRow<T> =
    delegate { mean(skipNa, columns) }

public inline fun <T, reified R : Number> PivotedDataFrame<T>.meanOf(
    skipNa: Boolean = false,
    crossinline selector: RowSelector<T, R?>
): DataRow<T> =
    delegate { meanOf(skipNa, selector) }

// endregion

// region median

public fun <T> PivotedDataFrame<T>.median(separate: Boolean = false): DataRow<T> = medianFor(separate, comparableColumns())

public fun <T, C : Comparable<C>> PivotedDataFrame<T>.medianFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, C?>
): DataRow<T> = delegate { medianFor(separate, columns) }
public fun <T> PivotedDataFrame<T>.medianFor(vararg columns: String, separate: Boolean = false): DataRow<T> = medianFor(separate) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.medianFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = medianFor(separate) { columns.toColumns() }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.medianFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false
): DataRow<T> = medianFor(separate) { columns.toColumns() }

public fun <T, C : Comparable<C>> PivotedDataFrame<T>.median(columns: ColumnsSelector<T, C?>): DataRow<T> = delegate { median(columns) }
public fun <T> PivotedDataFrame<T>.median(vararg columns: String): DataRow<T> = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.median(
    vararg columns: ColumnReference<C?>
): DataRow<T> = median { columns.toColumns() }
public fun <T, C : Comparable<C>> PivotedDataFrame<T>.median(vararg columns: KProperty<C?>): DataRow<T> = median { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> PivotedDataFrame<T>.medianOf(
    crossinline selector: RowSelector<T, R?>
): DataRow<T> = delegate { medianOf(selector) }

// endregion

// region std

public fun <T> PivotedDataFrame<T>.std(separate: Boolean = false): DataRow<T> = stdFor(separate, numberColumns())

public fun <T, R : Number> PivotedDataFrame<T>.stdFor(
    separate: Boolean = false,
    columns: AggregateColumnsSelector<T, R?>
): DataRow<T> = delegate { stdFor(separate, columns) }
public fun <T> PivotedDataFrame<T>.stdFor(vararg columns: String, separate: Boolean = false): DataRow<T> = stdFor(separate) { columns.toColumnsOf() }
public fun <T, C : Number> PivotedDataFrame<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = stdFor(separate) { columns.toColumns() }
public fun <T, C : Number> PivotedDataFrame<T>.stdFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataRow<T> = stdFor(separate) { columns.toColumns() }

public fun <T> PivotedDataFrame<T>.std(columns: ColumnsSelector<T, Number?>): DataRow<T> = delegate { std(columns) }
public fun <T> PivotedDataFrame<T>.std(vararg columns: ColumnReference<Number?>): DataRow<T> = std { columns.toColumns() }
public fun <T> PivotedDataFrame<T>.std(vararg columns: String): DataRow<T> = std { columns.toColumnsOf() }
public fun <T> PivotedDataFrame<T>.std(vararg columns: KProperty<Number?>): DataRow<T> = std { columns.toColumns() }

public fun <T> PivotedDataFrame<T>.stdOf(selector: RowSelector<T, Number?>): DataRow<T> = delegate { stdOf(selector) }

// endregion

@PublishedApi
internal inline fun <T> PivotedDataFrame<T>.delegate(crossinline body: GroupedPivotAggregations<T>.() -> DataFrame<T>): DataRow<T> = body(groupBy { none() })[0]
