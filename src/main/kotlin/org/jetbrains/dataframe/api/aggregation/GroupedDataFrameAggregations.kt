package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.Aggregatable
import org.jetbrains.dataframe.aggregation.AggregateColumnsSelector
import org.jetbrains.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.dataframe.columns.ColumnReference
import org.jetbrains.dataframe.impl.aggregation.*
import org.jetbrains.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateBy
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateOfDelegated
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateValue
import org.jetbrains.dataframe.impl.columns.toColumns
import org.jetbrains.dataframe.impl.columns.toColumnsOf
import org.jetbrains.dataframe.impl.columns.toComparableColumns
import org.jetbrains.dataframe.impl.columns.toNumberColumns
import kotlin.reflect.KProperty

public interface Grouped<out T> : Aggregatable<T> {

    public fun <R> aggregate(body: GroupByAggregateBody<T, R>): DataFrame<T>
}

public fun <T> Grouped<T>.count(resultName: String = "count", predicate: RowFilter<T>? = null): DataFrame<T> =
    aggregateValue(resultName) { count(predicate) default 0 }

public fun <T> Grouped<T>.values(vararg columns: Column): DataFrame<T> = values { columns.toColumns() }
public fun <T> Grouped<T>.values(vararg columns: String): DataFrame<T> = values { columns.toColumns() }
public fun <T> Grouped<T>.values(columns: AggregateColumnsSelector<T, *>): DataFrame<T> = aggregateInternal { columnValues(columns) { it.toList() } }
public fun <T> Grouped<T>.values(): DataFrame<T> = values(remainingColumnsSelector())

// region pivot

public fun <G> GroupedDataFrame<*, G>.pivot(columns: ColumnsSelector<G, *>): GroupedPivot<G> = GroupedPivotImpl(this, columns)
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: Column): GroupedPivot<G> = pivot { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: String): GroupedPivot<G> = pivot { columns.toColumns() }
public fun <G> GroupedDataFrame<*, G>.pivot(vararg columns: KProperty<*>): GroupedPivot<G> = pivot { columns.toColumns() }

// endregion

// region min

public fun <T> Grouped<T>.min(): DataFrame<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.minFor(columns: AggregateColumnsSelector<T, C?>): DataFrame<T> = Aggregators.min.aggregateFor(this, columns)
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

public fun <T, C : Comparable<C>> Grouped<T>.minOf(name: String? = null, selector: RowSelector<T, C>): DataFrame<T> =
    Aggregators.min.aggregateOfDelegated(this, name) { minOfOrNull(selector) }

public fun <T, C : Comparable<C>> Grouped<T>.minBy(selector: RowSelector<T, C?>): DataFrame<T> =
    aggregateBy { minByOrNull(selector) }

// endregion

// region max

public fun <T> Grouped<T>.max(): DataFrame<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.maxFor(columns: AggregateColumnsSelector<T, C?>): DataFrame<T> = Aggregators.max.aggregateFor(this, columns)
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

public fun <T, C : Comparable<C>> Grouped<T>.maxOf(name: String? = null, selector: RowSelector<T, C>): DataFrame<T> =
    Aggregators.max.aggregateOfDelegated(this, name) { maxOfOrNull(selector) }

public fun <T, C : Comparable<C>> Grouped<T>.maxBy(selector: RowSelector<T, C?>): DataFrame<T> =
    aggregateBy { maxByOrNull(selector) }

// endregion

// region sum

public fun <T> Grouped<T>.sum(): DataFrame<T> = sumFor(numberColumns())

public fun <T, C : Number> Grouped<T>.sumFor(columns: AggregateColumnsSelector<T, C?>): DataFrame<T> = Aggregators.sum.aggregateFor(this, columns)
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
    crossinline selector: RowSelector<T, R?>
): DataFrame<T> = Aggregators.sum.aggregateOf(this, resultName, selector)

// endregion

// region mean

public fun <T> Grouped<T>.mean(skipNa: Boolean = false): DataFrame<T> = meanFor(skipNa, numberColumns())

public fun <T, C : Number> Grouped<T>.meanFor(
    skipNa: Boolean = false,
    columns: AggregateColumnsSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNa).aggregateFor(this, columns)
public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNa: Boolean = false): DataFrame<T> = meanFor(skipNa) { columns.toNumberColumns() }
public fun <T, C : Number> Grouped<T>.meanFor(vararg columns: ColumnReference<C?>, skipNa: Boolean = false): DataFrame<T> = meanFor(skipNa) { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.meanFor(vararg columns: KProperty<C?>, skipNa: Boolean = false): DataFrame<T> = meanFor(skipNa) { columns.toColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    name: String? = null,
    skipNa: Boolean = false,
    columns: ColumnsSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNa).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.mean(vararg columns: String, name: String? = null, skipNa: Boolean = false): DataFrame<T> = mean(name, skipNa) { columns.toNumberColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNa: Boolean = false
): DataFrame<T> = mean(name, skipNa) { columns.toColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNa: Boolean = false
): DataFrame<T> = mean(name, skipNa) { columns.toColumns() }

public inline fun <T, reified R : Number> Grouped<T>.meanOf(
    name: String? = null,
    skipNa: Boolean = false,
    crossinline selector: RowSelector<T, R?>
): DataFrame<T> =
    Aggregators.mean(skipNa).aggregateOf(this, name, selector)

// endregion

// region median

public fun <T> Grouped<T>.median(): DataFrame<T> = medianFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.medianFor(columns: AggregateColumnsSelector<T, C?>): DataFrame<T> = Aggregators.median.aggregateFor(this, columns)
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
    crossinline selector: RowSelector<T, R?>
): DataFrame<T> = Aggregators.median.aggregateOf(this, name, selector)

// endregion

// region std

public fun <T> Grouped<T>.std(): DataFrame<T> = stdFor(numberColumns())

public fun <T> Grouped<T>.stdFor(columns: AggregateColumnsSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateFor(this, columns)
public fun <T> Grouped<T>.stdFor(vararg columns: String): DataFrame<T> = stdFor { columns.toColumnsOf() }
public fun <T, C : Number> Grouped<T>.stdFor(vararg columns: ColumnReference<C?>): DataFrame<T> = stdFor { columns.toColumns() }
public fun <T, C : Number> Grouped<T>.stdFor(vararg columns: KProperty<C?>): DataFrame<T> = stdFor { columns.toColumns() }

public fun <T> Grouped<T>.std(name: String? = null, columns: ColumnsSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.std(vararg columns: ColumnReference<Number?>, name: String? = null): DataFrame<T> = std(name) { columns.toColumns() }
public fun <T> Grouped<T>.std(vararg columns: String, name: String? = null): DataFrame<T> = std(name) { columns.toColumnsOf() }
public fun <T> Grouped<T>.std(vararg columns: KProperty<Number?>, name: String? = null): DataFrame<T> = std(name) { columns.toColumns() }

public fun <T> Grouped<T>.stdOf(name: String? = null, selector: RowSelector<T, Number?>): DataFrame<T> = Aggregators.std.aggregateOf(this, name, selector)

// endregion
