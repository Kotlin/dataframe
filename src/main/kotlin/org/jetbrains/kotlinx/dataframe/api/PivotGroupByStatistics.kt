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

// region count

public fun <T> PivotGroupBy<T>.count(): DataFrame<T> = aggregate { count() default 0 }
public fun <T> PivotGroupBy<T>.count(predicate: RowFilter<T>): DataFrame<T> = aggregate { count(predicate) default 0 }

// endregion

// region matches

public fun <T> PivotGroupBy<T>.matches(): DataFrame<T> = matches(yes = true, no = false)
public fun <T, R> PivotGroupBy<T>.matches(yes: R, no: R): DataFrame<T> = aggregate { yes default no }

// endregion

public inline fun <T, reified V> PivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = getType<V>()
    return aggregateInternal {
        val values = df.rows().map {
            val value = expression(it, it)
            if (value is ColumnReference<*>) it[value]
            else value
        }
        yieldOneOrMany(emptyPath(), values, type)
    }
}

public fun <T> PivotGroupBy<T>.toDataFrame(): DataFrame<T> = aggregate { this }

// region values

public fun <T> PivotGroupBy<T>.values(dropNA: Boolean = false, distinct: Boolean = false, separate: Boolean = false): DataFrame<T> = values(dropNA, distinct, separate, remainingColumnsSelector())

public fun <T> PivotGroupBy<T>.values(
    vararg columns: Column,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumns() }
public fun <T> PivotGroupBy<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumns() }
public fun <T> PivotGroupBy<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumns() }
public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>
): DataFrame<T> =
    separateStatistics(separate).aggregateInternal { columnValues(columns, false, dropNA, distinct) }

// endregion

// region min

public fun <T> PivotGroupBy<T>.min(separate: Boolean = false): DataFrame<T> = minFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.min.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = minFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataFrame<T> = minFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataFrame<T> = minFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(columns: ColumnsSelector<T, R?>): DataFrame<T> = Aggregators.min.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.min(vararg columns: String): DataFrame<T> = min { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(vararg columns: ColumnReference<R?>): DataFrame<T> = min { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(vararg columns: KProperty<R?>): DataFrame<T> = min { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minOf(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { minOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minBy(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { minBy(rowExpression) }
public fun <T> PivotGroupBy<T>.minBy(column: String): DataFrame<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.minBy(column: ColumnReference<C?>): DataFrame<T> = aggregate { minBy(column) }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.minBy(column: KProperty<C?>): DataFrame<T> = aggregate { minBy(column) }

// endregion

// region max

public fun <T> PivotGroupBy<T>.max(separate: Boolean = false): DataFrame<T> = maxFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.max.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.maxFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(columns: ColumnsSelector<T, R?>): DataFrame<T> = Aggregators.max.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.max(vararg columns: String): DataFrame<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(vararg columns: ColumnReference<R?>): DataFrame<T> = max { columns.toColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(vararg columns: KProperty<R?>): DataFrame<T> = max { columns.toColumns() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxOf(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { maxOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxBy(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { maxBy(rowExpression) }
public fun <T> PivotGroupBy<T>.maxBy(column: String): DataFrame<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.maxBy(column: ColumnReference<C?>): DataFrame<T> = aggregate { maxBy(column) }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.maxBy(column: KProperty<C?>): DataFrame<T> = aggregate { maxBy(column) }

// endregion

// region sum

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.sum.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> PivotGroupBy<T>.sum(columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.sum(vararg columns: String): DataFrame<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: ColumnReference<C?>): DataFrame<T> = sum { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: KProperty<C?>): DataFrame<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.sumOf(crossinline expression: RowExpression<T, R>): DataFrame<T> =
    Aggregators.sum.aggregateOf(this, expression)

// endregion

// region mean

public fun <T> PivotGroupBy<T>.mean(skipNA: Boolean = defaultSkipNA, separate: Boolean = false): DataFrame<T> = meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    skipNA: Boolean = defaultSkipNA,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    skipNA: Boolean = defaultSkipNA,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = defaultSkipNA,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = defaultSkipNA,
    separate: Boolean = false
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumns() }

public fun <T, R : Number> PivotGroupBy<T>.mean(skipNA: Boolean = defaultSkipNA, columns: ColumnsSelector<T, R?>): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNA: Boolean = defaultSkipNA): DataFrame<T> = mean(skipNA) { columns.toColumnsOf() }
public fun <T, R : Number> PivotGroupBy<T>.mean(vararg columns: ColumnReference<R?>, skipNA: Boolean = defaultSkipNA): DataFrame<T> = mean(skipNA) { columns.toColumns() }
public fun <T, R : Number> PivotGroupBy<T>.mean(vararg columns: KProperty<R?>, skipNA: Boolean = defaultSkipNA): DataFrame<T> = mean(skipNA) { columns.toColumns() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.meanOf(
    skipNA: Boolean = defaultSkipNA,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateOf(this, expression)

// endregion

// region median

public fun <T> PivotGroupBy<T>.median(separate: Boolean = false): DataFrame<T> = medianFor(separate, comparableColumns())

public fun <T, C : Comparable<C>> PivotGroupBy<T>.medianFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.median.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.medianFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = medianFor(separate) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.medianFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = medianFor(separate) { columns.toColumns() }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.medianFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false
): DataFrame<T> = medianFor(separate) { columns.toColumns() }

public fun <T, C : Comparable<C>> PivotGroupBy<T>.median(columns: ColumnsSelector<T, C?>): DataFrame<T> = Aggregators.median.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.median(vararg columns: String): DataFrame<T> = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.median(
    vararg columns: ColumnReference<C?>
): DataFrame<T> = median { columns.toColumns() }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.median(vararg columns: KProperty<C?>): DataFrame<T> = median { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> PivotGroupBy<T>.medianOf(
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.median.aggregateOf(this, expression)

// endregion

// region std

public fun <T> PivotGroupBy<T>.std(separate: Boolean = false): DataFrame<T> = stdFor(separate, numberColumns())

public fun <T, R : Number> PivotGroupBy<T>.stdFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.std.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.stdFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = stdFor(separate) { columns.toColumnsOf() }
public fun <T, C : Number> PivotGroupBy<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = stdFor(separate) { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.stdFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataFrame<T> = stdFor(separate) { columns.toColumns() }

public fun <T> PivotGroupBy<T>.std(columns: ColumnsSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.std(vararg columns: ColumnReference<Number?>): DataFrame<T> = std { columns.toColumns() }
public fun <T> PivotGroupBy<T>.std(vararg columns: String): DataFrame<T> = std { columns.toColumnsOf() }
public fun <T> PivotGroupBy<T>.std(vararg columns: KProperty<Number?>): DataFrame<T> = std { columns.toColumns() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.stdOf(crossinline expression: RowExpression<T, R?>): DataFrame<T> = Aggregators.std.aggregateOf(this, expression)

// endregion
