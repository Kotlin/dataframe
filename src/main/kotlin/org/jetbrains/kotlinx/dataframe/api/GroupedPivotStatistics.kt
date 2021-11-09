package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.yieldOneOrMany
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KProperty

public fun <T> GroupedPivot<T>.count(predicate: RowFilter<T>? = null): DataFrame<T> = aggregate { count(predicate) default 0 }

public fun <T> GroupedPivot<T>.matches(): DataFrame<T> = matches(yes = true, no = false)
public fun <T, R> GroupedPivot<T>.matches(yes: R, no: R): DataFrame<T> = aggregate { yes default no }

public inline fun <T, reified V> GroupedPivot<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = getType<V>()
    return aggregateInternal {
        val values = df.map {
            val value = expression(it, it)
            if (value is ColumnReference<*>) it[value]
            else value
        }
        yieldOneOrMany(emptyPath(), values, type)
    }
}

public fun <T> GroupedPivot<T>.toDataFrame(): DataFrame<T> = aggregate { this }

// region values

public fun <T> GroupedPivot<T>.values(separate: Boolean = false): DataFrame<T> = values(separate, remainingColumnsSelector())

public fun <T> GroupedPivot<T>.values(vararg columns: Column, separate: Boolean = false): DataFrame<T> = values(separate) { columns.toColumns() }
public fun <T> GroupedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataFrame<T> = values(separate) { columns.toColumns() }
public fun <T> GroupedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataFrame<T> = values(separate) { columns.toColumns() }
public fun <T> GroupedPivot<T>.values(separate: Boolean = false, columns: ColumnsForAggregateSelector<T, *>): DataFrame<T> =
    separateStatistics(separate).aggregateInternal { columnValues(columns) { it.toList() } }

// endregion

// region min

public fun <T> GroupedPivot<T>.min(separate: Boolean = false): DataFrame<T> = minFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> GroupedPivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.min.aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = minFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataFrame<T> = minFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataFrame<T> = minFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> GroupedPivot<T>.min(columns: ColumnsSelector<T, R?>): DataFrame<T> = Aggregators.min.aggregateAll(this, columns)
public fun <T> GroupedPivot<T>.min(vararg columns: String): DataFrame<T> = min { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.min(vararg columns: ColumnReference<R?>): DataFrame<T> = min { columns.toColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.min(vararg columns: KProperty<R?>): DataFrame<T> = min { columns.toColumns() }

public fun <T, R : Comparable<R>> GroupedPivot<T>.minOf(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { minOf(rowExpression) }

public fun <T, R : Comparable<R>> GroupedPivot<T>.minBy(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { minBy(rowExpression) }
public fun <T> GroupedPivot<T>.minBy(column: String): DataFrame<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> GroupedPivot<T>.minBy(column: ColumnReference<C?>): DataFrame<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> GroupedPivot<T>.minBy(column: KProperty<C?>): DataFrame<T> = aggregate { minBy(column) }

// endregion

// region max

public fun <T> GroupedPivot<T>.max(separate: Boolean = false): DataFrame<T> = maxFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> GroupedPivot<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.max.aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.maxFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> GroupedPivot<T>.max(columns: ColumnsSelector<T, R?>): DataFrame<T> = Aggregators.max.aggregateAll(this, columns)
public fun <T> GroupedPivot<T>.max(vararg columns: String): DataFrame<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.max(vararg columns: ColumnReference<R?>): DataFrame<T> = max { columns.toColumns() }
public fun <T, R : Comparable<R>> GroupedPivot<T>.max(vararg columns: KProperty<R?>): DataFrame<T> = max { columns.toColumns() }

public fun <T, R : Comparable<R>> GroupedPivot<T>.maxOf(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { maxOf(rowExpression) }

public fun <T, R : Comparable<R>> GroupedPivot<T>.maxBy(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { maxBy(rowExpression) }
public fun <T> GroupedPivot<T>.maxBy(column: String): DataFrame<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> GroupedPivot<T>.maxBy(column: ColumnReference<C?>): DataFrame<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> GroupedPivot<T>.maxBy(column: KProperty<C?>): DataFrame<T> = aggregate { maxBy(column) }

// endregion

// region sum

public fun <T> GroupedPivot<T>.sum(separate: Boolean = false): DataFrame<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> GroupedPivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.sum.aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> GroupedPivot<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> GroupedPivot<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> GroupedPivot<T>.sum(columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, columns)
public fun <T> GroupedPivot<T>.sum(vararg columns: String): DataFrame<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> GroupedPivot<T>.sum(vararg columns: ColumnReference<C?>): DataFrame<T> = sum { columns.toColumns() }
public fun <T, C : Number> GroupedPivot<T>.sum(vararg columns: KProperty<C?>): DataFrame<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> GroupedPivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataFrame<T> =
    Aggregators.sum.aggregateOf(this, expression)

// endregion

// region mean

public fun <T> GroupedPivot<T>.mean(skipNa: Boolean = false, separate: Boolean = false): DataFrame<T> = meanFor(skipNa, separate, numberColumns())

public fun <T, C : Number> GroupedPivot<T>.meanFor(
    skipNa: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNa).aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.meanFor(
    vararg columns: String,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNa, separate) { columns.toNumberColumns() }
public fun <T, C : Number> GroupedPivot<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNa, separate) { columns.toColumns() }
public fun <T, C : Number> GroupedPivot<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNa: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNa, separate) { columns.toColumns() }

public fun <T, R : Number> GroupedPivot<T>.mean(skipNa: Boolean = true, columns: ColumnsSelector<T, R?>): DataFrame<T> =
    Aggregators.mean(skipNa).aggregateAll(this, columns)

public inline fun <T, reified R : Number> GroupedPivot<T>.meanOf(
    skipNa: Boolean = false,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> =
    Aggregators.mean(skipNa).aggregateOf(this, expression)

// endregion

// region median

public fun <T> GroupedPivot<T>.median(separate: Boolean = false): DataFrame<T> = medianFor(separate, comparableColumns())

public fun <T, C : Comparable<C>> GroupedPivot<T>.medianFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.median.aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.medianFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = medianFor(separate) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> GroupedPivot<T>.medianFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = medianFor(separate) { columns.toColumns() }
public fun <T, C : Comparable<C>> GroupedPivot<T>.medianFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false
): DataFrame<T> = medianFor(separate) { columns.toColumns() }

public fun <T, C : Comparable<C>> GroupedPivot<T>.median(columns: ColumnsSelector<T, C?>): DataFrame<T> = Aggregators.median.aggregateAll(this, columns)
public fun <T> GroupedPivot<T>.median(vararg columns: String): DataFrame<T> = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> GroupedPivot<T>.median(
    vararg columns: ColumnReference<C?>
): DataFrame<T> = median { columns.toColumns() }
public fun <T, C : Comparable<C>> GroupedPivot<T>.median(vararg columns: KProperty<C?>): DataFrame<T> = median { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> GroupedPivot<T>.medianOf(
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.median.aggregateOf(this, expression)

// endregion

// region std

public fun <T> GroupedPivot<T>.std(separate: Boolean = false): DataFrame<T> = stdFor(separate, numberColumns())

public fun <T, R : Number> GroupedPivot<T>.stdFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.std.aggregateFor(this, separate, columns)
public fun <T> GroupedPivot<T>.stdFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = stdFor(separate) { columns.toColumnsOf() }
public fun <T, C : Number> GroupedPivot<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = stdFor(separate) { columns.toColumns() }
public fun <T, C : Number> GroupedPivot<T>.stdFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataFrame<T> = stdFor(separate) { columns.toColumns() }

public fun <T> GroupedPivot<T>.std(columns: ColumnsSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateAll(this, columns)
public fun <T> GroupedPivot<T>.std(vararg columns: ColumnReference<Number?>): DataFrame<T> = std { columns.toColumns() }
public fun <T> GroupedPivot<T>.std(vararg columns: String): DataFrame<T> = std { columns.toColumnsOf() }
public fun <T> GroupedPivot<T>.std(vararg columns: KProperty<Number?>): DataFrame<T> = std { columns.toColumns() }

public fun <T> GroupedPivot<T>.stdOf(expression: RowExpression<T, Number?>): DataFrame<T> = Aggregators.std.aggregateOf(this, expression)

// endregion
