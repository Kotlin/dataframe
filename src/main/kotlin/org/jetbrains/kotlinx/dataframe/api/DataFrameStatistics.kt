package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.api.corrImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.indexOfMax
import org.jetbrains.kotlinx.dataframe.impl.indexOfMin
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.impl.zero
import org.jetbrains.kotlinx.dataframe.math.sumOf
import kotlin.reflect.KProperty

// region count

public fun <T> DataFrame<T>.count(): Int = rowsCount()

public fun <T> DataFrame<T>.count(predicate: RowFilter<T>): Int = rows().count { predicate(it, it) }

// endregion

// region min

public fun <T> DataFrame<T>.min(): DataRow<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.minFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.min.aggregateFor(this, columns)
public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> = minFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: ColumnReference<C?>): DataRow<T> = minFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: KProperty<C?>): DataRow<T> = minFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.min(columns: ColumnsSelector<T, C?>): C = minOrNull(columns).suggestIfNull("min")
public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> = minOrNull(*columns).suggestIfNull("min")
public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: ColumnReference<C?>): C = minOrNull(*columns).suggestIfNull("min")
public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: KProperty<C?>): C = minOrNull(*columns).suggestIfNull("min")

public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.min.aggregateAll(this, columns)
public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any?>? = minOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C?>): C? = minOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: KProperty<C?>): C? = minOrNull { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.minOf(expression: RowExpression<T, C>): C = minOfOrNull(expression).suggestIfNull("minOf")
public fun <T, C : Comparable<C>> DataFrame<T>.minOfOrNull(expression: RowExpression<T, C>): C? = rows().minOfOrNull { expression(it, it) }

public fun <T, C : Comparable<C>> DataFrame<T>.minBy(expression: RowExpression<T, C?>): DataRow<T> = minByOrNull(expression).suggestIfNull("minBy")
public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minByOrNull(column).suggestIfNull("minBy")
public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: ColumnReference<C?>): DataRow<T> = minByOrNull(column).suggestIfNull("minBy")
public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: KProperty<C?>): DataRow<T> = minByOrNull(column).suggestIfNull("minBy")

public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(expression: RowExpression<T, C?>): DataRow<T>? = getOrNull(rows().asSequence().map { expression(it, it) }.indexOfMin())
public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? = minByOrNull(column.toColumnOf<Comparable<Any?>?>())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: ColumnReference<C?>): DataRow<T>? = getOrNull(get(column).asSequence().indexOfMin())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: KProperty<C?>): DataRow<T>? = minByOrNull(column.toColumnAccessor())

// endregion

// region max

public fun <T> DataFrame<T>.max(): DataRow<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.max.aggregateFor(this, columns)
public fun <T> DataFrame<T>.maxFor(vararg columns: String): DataRow<T> = maxFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: ColumnReference<C?>): DataRow<T> = maxFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: KProperty<C?>): DataRow<T> = maxFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.max(columns: ColumnsSelector<T, C?>): C = maxOrNull(columns).suggestIfNull("max")
public fun <T> DataFrame<T>.max(vararg columns: String): Comparable<Any?> = maxOrNull(*columns).suggestIfNull("max")
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: ColumnReference<C?>): C = maxOrNull(*columns).suggestIfNull("max")
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: KProperty<C?>): C = maxOrNull(*columns).suggestIfNull("max")

public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.max.aggregateAll(this, columns)
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String): Comparable<Any?>? = maxOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: ColumnReference<C?>): C? = maxOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: KProperty<C?>): C? = maxOrNull { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.maxOf(expression: RowExpression<T, C>): C = maxOfOrNull(expression).suggestIfNull("maxOf")
public fun <T, C : Comparable<C>> DataFrame<T>.maxOfOrNull(expression: RowExpression<T, C>): C? = rows().maxOfOrNull { expression(it, it) }

public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(expression: RowExpression<T, C?>): DataRow<T> = maxByOrNull(expression).suggestIfNull("maxBy")
public fun <T> DataFrame<T>.maxBy(column: String): DataRow<T> = maxByOrNull(column).suggestIfNull("maxBy")
public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(column: ColumnReference<C?>): DataRow<T> = maxByOrNull(column).suggestIfNull("maxBy")
public fun <T, C : Comparable<C>> DataFrame<T>.maxBy(column: KProperty<C?>): DataRow<T> = maxByOrNull(column).suggestIfNull("maxBy")

public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(expression: RowExpression<T, C?>): DataRow<T>? = getOrNull(rows().asSequence().map { expression(it, it) }.indexOfMax())
public fun <T> DataFrame<T>.maxByOrNull(column: String): DataRow<T>? = maxByOrNull(column.toColumnOf<Comparable<Any?>?>())
public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(column: ColumnReference<C?>): DataRow<T>? = getOrNull(get(column).asSequence().indexOfMax())
public fun <T, C : Comparable<C>> DataFrame<T>.maxByOrNull(column: KProperty<C?>): DataRow<T>? = maxByOrNull(column.toColumnAccessor())

// endregion

// region sum

public fun <T> DataFrame<T>.sum(): DataRow<T> = sumFor(numberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.sum.aggregateFor(this, columns)
public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> = sumFor { columns.toColumnsOf() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: ColumnReference<C?>): DataRow<T> = sumFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: KProperty<C?>): DataRow<T> = sumFor { columns.toColumns() }

public inline fun <T, reified C : Number> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C?>): C = Aggregators.sum.aggregateAll(this, columns) ?: C::class.zero()
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: ColumnReference<C?>): C = sum { columns.toColumns() }
public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum { columns.toColumnsOf() }
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: KProperty<C?>): C = sum { columns.toColumns() }

public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): C = rows().sumOf(
    getType<C>()
) { expression(it, it) }

// endregion

// region mean

public fun <T> DataFrame<T>.mean(skipNA: Boolean = defaultSkipNA): DataRow<T> = meanFor(skipNA, numberColumns())

public fun <T, C : Number> DataFrame<T>.meanFor(
    skipNA: Boolean = defaultSkipNA,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)
public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNA: Boolean = defaultSkipNA): DataRow<T> = meanFor(skipNA) { columns.toNumberColumns() }
public fun <T, C : Number> DataFrame<T>.meanFor(vararg columns: ColumnReference<C?>, skipNA: Boolean = defaultSkipNA): DataRow<T> = meanFor(skipNA) { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.meanFor(vararg columns: KProperty<C?>, skipNA: Boolean = defaultSkipNA): DataRow<T> = meanFor(skipNA) { columns.toColumns() }

public fun <T, C : Number> DataFrame<T>.mean(skipNA: Boolean = defaultSkipNA, columns: ColumnsSelector<T, C?>): Double = Aggregators.mean(skipNA).aggregateAll(this, columns) as Double? ?: Double.NaN
public fun <T> DataFrame<T>.mean(vararg columns: String, skipNA: Boolean = defaultSkipNA): Double = mean(skipNA) { columns.toNumberColumns() }
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: ColumnReference<C?>, skipNA: Boolean = defaultSkipNA): Double = mean(skipNA) { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNA: Boolean = defaultSkipNA): Double = mean(skipNA) { columns.toColumns() }

public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNA: Boolean = defaultSkipNA,
    noinline expression: RowExpression<T, D?>
): Double = Aggregators.mean(skipNA).of(this, expression) ?: Double.NaN

// endregion

// region median

public fun <T> DataFrame<T>.median(): DataRow<T> = medianFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.median.aggregateFor(this, columns)
public fun <T> DataFrame<T>.medianFor(vararg columns: String): DataRow<T> = medianFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(vararg columns: ColumnReference<C?>): DataRow<T> = medianFor { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianFor(vararg columns: KProperty<C?>): DataRow<T> = medianFor { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.median(columns: ColumnsSelector<T, C?>): C = medianOrNull(columns).suggestIfNull("median")
public fun <T> DataFrame<T>.median(vararg columns: String): Any = median { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.median(vararg columns: ColumnReference<C?>): C = median { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.median(vararg columns: KProperty<C?>): C = median { columns.toColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.median.aggregateAll(this, columns)
public fun <T> DataFrame<T>.medianOrNull(vararg columns: String): Any? = medianOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(vararg columns: ColumnReference<C?>): C? = medianOrNull { columns.toColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.medianOrNull(vararg columns: KProperty<C?>): C? = medianOrNull { columns.toColumns() }

public inline fun <T, reified R : Comparable<R>> DataFrame<T>.medianOf(crossinline expression: RowExpression<T, R?>): R? = Aggregators.median.of(this, expression) as R?

// endregion

// region std

public fun <T> DataFrame<T>.std(): DataRow<T> = stdFor(numberColumns())

public fun <T> DataFrame<T>.stdFor(columns: ColumnsForAggregateSelector<T, Number?>): DataRow<T> = Aggregators.std.aggregateFor(this, columns)
public fun <T> DataFrame<T>.stdFor(vararg columns: String): DataRow<T> = stdFor { columns.toColumnsOf() }
public fun <T, C : Number> DataFrame<T>.stdFor(vararg columns: ColumnReference<C?>): DataRow<T> = stdFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.stdFor(vararg columns: KProperty<C?>): DataRow<T> = stdFor { columns.toColumns() }

public fun <T> DataFrame<T>.std(columns: ColumnsSelector<T, Number?>): Double = aggregateAll(Aggregators.std, columns) ?: .0
public fun <T> DataFrame<T>.std(vararg columns: ColumnReference<Number?>): Double = std { columns.toColumns() }
public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsOf() }
public fun <T> DataFrame<T>.std(vararg columns: KProperty<Number?>): Double = std { columns.toColumns() }

public inline fun <T, reified R : Number> DataFrame<T>.stdOf(crossinline expression: RowExpression<T, R?>): Double = Aggregators.std.aggregateOf(this, expression) ?: .0

// endregion

// region corr

internal fun AnyCol.isSuitableForCorr() = isSubtypeOf<Number>() || type() == getType<Boolean>()

public data class Corr<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>
)

public fun <T> DataFrame<T>.corr(): DataFrame<T> = corr { dfs { it.isSuitableForCorr() } }.withItself()

public fun <T, C> DataFrame<T>.corr(columns: ColumnsSelector<T, C>): Corr<T, C> = Corr(this, columns)
public fun <T> DataFrame<T>.corr(vararg columns: String): Corr<T, Any?> = corr { columns.toColumns() }
public fun <T, C> DataFrame<T>.corr(vararg columns: KProperty<C>): Corr<T, C> = corr { columns.toColumns() }
public fun <T, C> DataFrame<T>.corr(vararg columns: ColumnReference<C>): Corr<T, C> = corr { columns.toColumns() }

public fun <T, C, R> Corr<T, C>.with(otherColumns: ColumnsSelector<T, R>): DataFrame<T> = corrImpl(otherColumns)
public fun <T, C> Corr<T, C>.with(vararg otherColumns: String): DataFrame<T> = with { otherColumns.toColumns() }
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: KProperty<R>): DataFrame<T> = with { otherColumns.toColumns() }
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: ColumnReference<R>): DataFrame<T> = with { otherColumns.toColumns() }

public fun <T, C> Corr<T, C>.withItself(): DataFrame<T> = with(columns)

// endregion

// region valueCounts

public fun <T> DataFrame<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
    columns: ColumnsSelector<T, *>? = null
): DataFrame<T> {
    var df = if (columns != null) select(columns) else this
    if (dropNA) df = df.dropNA()

    val rows by columnGroup()
    val countName = nameGenerator().addUnique(resultColumn)
    return df.asColumnGroup(rows).asDataColumn().valueCounts(sort, ascending, dropNA, countName).ungroup(rows).cast()
}

public fun <T> DataFrame<T>.valueCounts(
    vararg columns: String,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: Column,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: KProperty<*>,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }

// endregion
