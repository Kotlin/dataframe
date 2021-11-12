package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateBy
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfDelegated
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateValue
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import kotlin.reflect.KProperty

public interface Grouped<out T> : Aggregatable<T> {

    public fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T>
}

public fun <T> Grouped<T>.count(resultName: String = "count", predicate: RowFilter<T>? = null): DataFrame<T> =
    aggregateValue(resultName) { count(predicate) default 0 }

public fun <T> Grouped<T>.values(vararg columns: Column): DataFrame<T> = values { columns.toColumns() }
public fun <T> Grouped<T>.values(vararg columns: String): DataFrame<T> = values { columns.toColumns() }
public fun <T> Grouped<T>.values(columns: ColumnsForAggregateSelector<T, *>): DataFrame<T> = aggregateInternal { columnValues(columns) { it.toList() } }
public fun <T> Grouped<T>.values(): DataFrame<T> = values(remainingColumnsSelector())

// region min

public fun <T> Grouped<T>.min(): DataFrame<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.minFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.min.aggregateFor(this, columns)
public fun <T> Grouped<T>.minFor(vararg columns: String): DataFrame<T> = minFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.minFor(vararg columns: ColumnReference<C?>): DataFrame<T> = minFor { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.minFor(vararg columns: KProperty<C?>): DataFrame<T> = minFor { columns.toColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.min(
    name: String? = null,
    columns: ColumnsSelector<T, C?>
): DataFrame<T> =
    Aggregators.min.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.min(vararg columns: String, name: String? = null): DataFrame<T> = min(name) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.min(vararg columns: ColumnReference<C?>, name: String? = null): DataFrame<T> = min(name) { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.min(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> = min(name) { columns.toColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.minOf(name: String? = null, expression: RowExpression<T, C>): DataFrame<T> =
    Aggregators.min.aggregateOfDelegated(this, name) { minOfOrNull(expression) }

public fun <T, C : Comparable<C>> Grouped<T>.minBy(expression: RowExpression<T, C?>): DataFrame<T> =
    aggregateBy { minByOrNull(expression) }

// endregion

// region max

public fun <T> Grouped<T>.max(): DataFrame<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.maxFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.max.aggregateFor(this, columns)
public fun <T> Grouped<T>.maxFor(vararg columns: String): DataFrame<T> = maxFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.maxFor(vararg columns: ColumnReference<C?>): DataFrame<T> = maxFor { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.maxFor(vararg columns: KProperty<C?>): DataFrame<T> = maxFor { columns.toColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.max(
    name: String? = null,
    columns: ColumnsSelector<T, C?>
): DataFrame<T> =
    Aggregators.max.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.max(vararg columns: String, name: String? = null): DataFrame<T> = max(name) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.max(vararg columns: ColumnReference<C?>, name: String? = null): DataFrame<T> = max(name) { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.max(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> = max(name) { columns.toColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.maxOf(name: String? = null, expression: RowExpression<T, C>): DataFrame<T> =
    Aggregators.max.aggregateOfDelegated(this, name) { maxOfOrNull(expression) }

public fun <T, C : Comparable<C>> Grouped<T>.maxBy(expression: RowExpression<T, C?>): DataFrame<T> =
    aggregateBy { maxByOrNull(expression) }

// endregion

// region sum

public fun <T> Grouped<T>.sum(): DataFrame<T> = sumFor(numberColumns())

public fun <T, C : Number> Grouped<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.sum.aggregateFor(this, columns)
public fun <T> Grouped<T>.sumFor(vararg columns: String): DataFrame<T> = sumFor { columns.toNumberColumns() }
public fun <T, C : Number> Grouped<T>.sumFor(vararg columns: ColumnReference<C?>): DataFrame<T> = sumFor { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.sumFor(vararg columns: KProperty<C?>): DataFrame<T> = sumFor { columns.toColumns() }

public fun <T, C : Number> Grouped<T>.sum(name: String? = null, columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.sum(vararg columns: String, name: String? = null): DataFrame<T> = sum(name) { columns.toNumberColumns() }
public fun <T, C : Number> Grouped<T>.sum(vararg columns: ColumnReference<C?>, name: String? = null): DataFrame<T> = sum(name) { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.sum(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> = sum(name) { columns.toColumns() }

public inline fun <T, reified R : Number> Grouped<T>.sumOf(
    resultName: String? = null,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.sum.aggregateOf(this, resultName, expression)

// endregion

// region mean

public fun <T> Grouped<T>.mean(skipNA: Boolean = false): DataFrame<T> = meanFor(skipNA, numberColumns())

public fun <T, C : Number> Grouped<T>.meanFor(
    skipNA: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)
public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNA: Boolean = false): DataFrame<T> = meanFor(skipNA) { columns.toNumberColumns() }
public fun <T, C : Number> Grouped<T>.meanFor(vararg columns: ColumnReference<C?>, skipNA: Boolean = false): DataFrame<T> = meanFor(skipNA) { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.meanFor(vararg columns: KProperty<C?>, skipNA: Boolean = false): DataFrame<T> = meanFor(skipNA) { columns.toColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    name: String? = null,
    skipNA: Boolean = false,
    columns: ColumnsSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNA).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.mean(vararg columns: String, name: String? = null, skipNA: Boolean = false): DataFrame<T> = mean(name, skipNA) { columns.toNumberColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNA: Boolean = false
): DataFrame<T> = mean(name, skipNA) { columns.toColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNA: Boolean = false
): DataFrame<T> = mean(name, skipNA) { columns.toColumns() }

public inline fun <T, reified R : Number> Grouped<T>.meanOf(
    name: String? = null,
    skipNA: Boolean = false,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateOf(this, name, expression)

// endregion

// region median

public fun <T> Grouped<T>.median(): DataFrame<T> = medianFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.medianFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.median.aggregateFor(this, columns)
public fun <T> Grouped<T>.medianFor(vararg columns: String): DataFrame<T> = medianFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.medianFor(vararg columns: ColumnReference<C?>): DataFrame<T> = medianFor { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.medianFor(vararg columns: KProperty<C?>): DataFrame<T> = medianFor { columns.toColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.median(name: String? = null, columns: ColumnsSelector<T, C?>): DataFrame<T> = Aggregators.median.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.median(vararg columns: String, name: String? = null): DataFrame<T> = median(name) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.median(
    vararg columns: ColumnReference<C?>,
    name: String? = null
): DataFrame<T> = median(name) { columns.toColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.median(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> = median(name) { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> Grouped<T>.medianOf(
    name: String? = null,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.median.aggregateOf(this, name, expression)

// endregion

// region std

public fun <T> Grouped<T>.std(): DataFrame<T> = stdFor(numberColumns())

public fun <T> Grouped<T>.stdFor(columns: ColumnsForAggregateSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateFor(this, columns)
public fun <T> Grouped<T>.stdFor(vararg columns: String): DataFrame<T> = stdFor { columns.toColumnsOf() }
public fun <T, C : Number> Grouped<T>.stdFor(vararg columns: ColumnReference<C?>): DataFrame<T> = stdFor { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.stdFor(vararg columns: KProperty<C?>): DataFrame<T> = stdFor { columns.toColumns() }

public fun <T> Grouped<T>.std(name: String? = null, columns: ColumnsSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.std(vararg columns: ColumnReference<Number?>, name: String? = null): DataFrame<T> = std(name) { columns.toColumns() }
public fun <T> Grouped<T>.std(vararg columns: String, name: String? = null): DataFrame<T> = std(name) { columns.toColumnsOf() }
public fun <T> Grouped<T>.std(vararg columns: KProperty<Number?>, name: String? = null): DataFrame<T> = std(name) { columns.toColumns() }

public fun <T> Grouped<T>.stdOf(name: String? = null, expression: RowExpression<T, Number?>): DataFrame<T> = Aggregators.std.aggregateOf(this, name, expression)

// endregion
