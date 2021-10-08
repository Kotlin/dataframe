package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.AggregateColumnsSelector
import org.jetbrains.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.dataframe.columns.ColumnReference
import org.jetbrains.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.dataframe.impl.aggregation.modes.of
import org.jetbrains.dataframe.impl.aggregation.numberColumns
import org.jetbrains.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.dataframe.impl.aggregation.remainingColumns
import org.jetbrains.dataframe.impl.columns.*
import org.jetbrains.dataframe.impl.mapRows
import org.jetbrains.dataframe.impl.zero
import kotlin.reflect.KProperty

public fun <T, R> DataFrame<T>.aggregate(body: GroupByAggregateBody<T, R>): DataRow<T> = aggregateInternal(body as AggregateBodyInternal<T, R>)[0]

public fun <T> DataFrame<T>.count(predicate: RowFilter<T>? = null): Int =
    if (predicate == null) nrow() else rows().count { predicate(it, it) }

// region min

public fun <T> DataFrame<T>.min(): DataRow<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.minFor(columns: AggregateColumnsSelector<T, C?>): DataRow<T> = Aggregators.min.aggregateFor(this, columns)
public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> = minFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: ColumnReference<C?>): DataRow<T> = minFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: KProperty<C?>): DataRow<T> = minFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.min(columns: ColumnsSelector<T, C?>): C = minOrNull(columns)!!
public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> = minOrNull(*columns)!!
public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: ColumnReference<C?>): C = minOrNull(*columns)!!
public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: KProperty<C?>): C = minOrNull(*columns)!!

public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.min.aggregateAll(this, columns)
public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any?>? = minOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C?>): C? = minOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: KProperty<C?>): C? = minOrNull { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.minOf(selector: RowSelector<T, C>): C = minOfOrNull(selector)!!
public fun <T, C : Comparable<C>> DataFrame<T>.minOfOrNull(selector: RowSelector<T, C>): C? = rows().minOfOrNull { selector(it, it) }

public fun <T, C : Comparable<C>> DataFrame<T>.minBy(selector: RowSelector<T, C?>): DataRow<T> = minByOrNull(selector)!!
public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minByOrNull(column)!!
public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: ColumnReference<C?>): DataRow<T> = minByOrNull(column)!!
public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: KProperty<C?>): DataRow<T> = minByOrNull(column)!!

public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(selector: RowSelector<T, C?>): DataRow<T>? = getOrNull(mapRows(selector).indexOfMin())
public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? = minByOrNull(column.toColumnOf<Comparable<Any?>?>())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: ColumnReference<C?>): DataRow<T>? = getOrNull(get(column).asSequence().indexOfMin())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: KProperty<C?>): DataRow<T>? = minByOrNull(column.toColumnAccessor())

// endregion

// region max

public fun <T> DataFrame<T>.max(): DataRow<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(columns: AggregateColumnsSelector<T, C?>): DataRow<T> = Aggregators.max.aggregateFor(this, columns)
public fun <T> DataFrame<T>.maxFor(vararg columns: String): DataRow<T> = maxFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: ColumnReference<C?>): DataRow<T> = maxFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: KProperty<C?>): DataRow<T> = maxFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.max(columns: ColumnsSelector<T, C?>): C = maxOrNull(columns)!!
public fun <T> DataFrame<T>.max(vararg columns: String): Comparable<Any?> = maxOrNull(*columns)!!
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: ColumnReference<C?>): C = maxOrNull(*columns)!!
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: KProperty<C?>): C = maxOrNull(*columns)!!

public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.max.aggregateAll(this, columns)
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String): Comparable<Any?>? = maxOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: ColumnReference<C?>): C? = maxOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: KProperty<C?>): C? = maxOrNull { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.maxOf(selector: RowSelector<T, C>): C = maxOfOrNull(selector)!!
public fun <T, C : Comparable<C>> DataFrame<T>.maxOfOrNull(selector: RowSelector<T, C>): C? = rows().maxOfOrNull { selector(it, it) }

public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(selector: RowSelector<T, C?>): DataRow<T> = maxByOrNull(selector)!!
public fun <T> DataFrame<T>.maxBy(column: String): DataRow<T> = maxByOrNull(column)!!
public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(column: ColumnReference<C?>): DataRow<T> = maxByOrNull(column)!!
public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(column: KProperty<C?>): DataRow<T> = maxByOrNull(column)!!

public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(selector: RowSelector<T, C?>): DataRow<T>? = getOrNull(mapRows(selector).indexOfMax())
public fun <T> DataFrame<T>.maxByOrNull(column: String): DataRow<T>? = maxByOrNull(column.toColumnOf<Comparable<Any?>?>())
public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(column: ColumnReference<C?>): DataRow<T>? = getOrNull(get(column).asSequence().indexOfMax())
public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(column: KProperty<C?>): DataRow<T>? = maxByOrNull(column.toColumnAccessor())

// endregion

// region sum

public fun <T> DataFrame<T>.sum(): DataRow<T> = sumFor(numberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(columns: AggregateColumnsSelector<T, C?>): DataRow<T> = Aggregators.sum.aggregateFor(this, columns)
public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> = sumFor { columns.toColumnsOf() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: ColumnReference<C?>): DataRow<T> = sumFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: KProperty<C?>): DataRow<T> = sumFor { columns.toColumns() }

public inline fun <T, reified C : Number> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C?>): C = Aggregators.sum.aggregateAll(this, columns) ?: C::class.zero()
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: ColumnReference<C?>): C = sum { columns.toColumns() }
public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum { columns.toColumnsOf() }
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: KProperty<C?>): C = sum { columns.toColumns() }

public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline selector: RowSelector<T, C>): C = rows().sumOf(getType<C>()) { selector(it, it) }

// endregion

// region std

public fun <T> DataFrame<T>.std(): DataRow<T> = stdFor(remainingColumns { it.isNumber() } as ColumnsSelector<T, Number>)

public fun <T> DataFrame<T>.stdFor(columns: AggregateColumnsSelector<T, Number?>): DataRow<T> = Aggregators.std.aggregateFor(this, columns)
public fun <T> DataFrame<T>.stdFor(vararg columns: String): DataRow<T> = stdFor { columns.toColumnsOf() }
public fun <T, C : Number> DataFrame<T>.stdFor(vararg columns: ColumnReference<C?>): DataRow<T> = stdFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.stdFor(vararg columns: KProperty<C?>): DataRow<T> = stdFor { columns.toColumns() }

public fun <T> DataFrame<T>.std(columns: ColumnsSelector<T, Number?>): Double = aggregateAll(Aggregators.std, columns) ?: .0
public fun <T> DataFrame<T>.std(vararg columns: ColumnReference<Number?>): Double = std { columns.toColumns() }
public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsOf() }
public fun <T> DataFrame<T>.std(vararg columns: KProperty<Number?>): Double = std { columns.toColumns() }

public fun <T> DataFrame<T>.stdOf(selector: RowSelector<T, Number?>): Double = Aggregators.std.aggregateOf(this, selector) ?: .0

// endregion

// region mean

public fun <T> DataFrame<T>.mean(skipNa: Boolean = false): DataRow<T> = meanFor(skipNa, numberColumns())

public fun <T, C : Number> DataFrame<T>.meanFor(skipNa: Boolean = false, columns: AggregateColumnsSelector<T, C?>): DataRow<T> = Aggregators.mean(skipNa).aggregateFor(this, columns)
public fun <T> DataFrame<T>.meanFor(vararg columns: String): DataRow<T> = meanFor { columns.toNumberColumns() }
public fun <T, C : Number> DataFrame<T>.meanFor(vararg columns: ColumnReference<C?>): DataRow<T> = meanFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.meanFor(vararg columns: KProperty<C?>): DataRow<T> = meanFor { columns.toColumns() }

public fun <T, C : Number> DataFrame<T>.mean(skipNa: Boolean = false, columns: ColumnsSelector<T, C?>): Double = Aggregators.mean(skipNa).aggregateAll(this, columns) as Double? ?: Double.NaN
public fun <T> DataFrame<T>.mean(vararg columns: String, skipNa: Boolean = false): Double = mean(skipNa) { columns.toNumberColumns() }
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: ColumnReference<C?>, skipNa: Boolean = false): Double = mean(skipNa) { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNa: Boolean = false): Double = mean(skipNa) { columns.toColumns() }

public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNa: Boolean = false,
    noinline selector: RowSelector<T, D?>
): Double = Aggregators.mean(skipNa).of(this, selector) ?: Double.NaN

// endregion

// region median

public fun <T> DataFrame<T>.median(): DataRow<T> = medianFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(columns: AggregateColumnsSelector<T, C?>): DataRow<T> = Aggregators.median.aggregateFor(this, columns)
public fun <T> DataFrame<T>.medianFor(vararg columns: String): DataRow<T> = medianFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(vararg columns: ColumnReference<C?>): DataRow<T> = medianFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(vararg columns: KProperty<C?>): DataRow<T> = medianFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.median(columns: ColumnsSelector<T, C?>): C = medianOrNull(columns)!!
public fun <T> DataFrame<T>.median(vararg columns: String): Any = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.median(vararg columns: ColumnReference<C?>): C = median { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.median(vararg columns: KProperty<C?>): C = median { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.median.aggregateAll(this, columns)
public fun <T> DataFrame<T>.medianOrNull(vararg columns: String): Any? = medianOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(vararg columns: ColumnReference<C?>): C? = medianOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(vararg columns: KProperty<C?>): C? = medianOrNull { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> DataFrame<T>.medianOf(crossinline selector: RowSelector<T, R?>): R? = Aggregators.median.of(this, selector) as R?

// endregion
