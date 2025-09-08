package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.intraComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateByOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MIN_NO_SKIPNAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MIN
import org.jetbrains.kotlinx.dataframe.util.ROW_MIN_OR_NULL
import kotlin.reflect.KProperty

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T?>.min(skipNaN: Boolean = skipNaNDefault): T =
    minOrNull(skipNaN).suggestIfNull("min")

public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateSingleColumn(this)

public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T = minByOrNull(skipNaN, selector).suggestIfNull("minBy")

public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.min.invoke(skipNaN).cast<R>().aggregateByOrNull(this, selector)

public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R = minOfOrNull(skipNaN, selector).suggestIfNull("minOf")

public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R? = Aggregators.min.invoke(skipNaN).cast<R>().aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated(ROW_MIN_OR_NULL, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMinOrNull(): Nothing? = error(ROW_MIN_OR_NULL)

@Deprecated(ROW_MIN, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMin(): Nothing = error(ROW_MIN)

public inline fun <reified T : Comparable<T>> AnyRow.rowMinOfOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T : Comparable<T>> AnyRow.rowMinOf(skipNaN: Boolean = skipNaNDefault): T =
    rowMinOfOrNull<T>(skipNaN).suggestIfNull("rowMinOf")

// endregion

// region DataFrame
@Refine
@Interpretable("Min0")
public fun <T> DataFrame<T>.min(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("Min1")
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<*>?> DataFrame<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C = minOrNull(skipNaN, columns).suggestIfNull("min")

public fun <T> DataFrame<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any> =
    minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.min(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.min(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.min.invoke(skipNaN).cast<C>().aggregateAll(this, columns)

public fun <T> DataFrame<T>.minOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any>? =
    minOrNull(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C = minOfOrNull(skipNaN, expression).suggestIfNull("minOf")

public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.min.invoke(skipNaN).cast<C>().aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minByOrNull(skipNaN, expression).suggestIfNull("minBy")

public fun <T> DataFrame<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minByOrNull(column, skipNaN).suggestIfNull("minBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.min.invoke(skipNaN).cast<C>().aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.minByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    minByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.min.invoke(skipNaN).cast<C>().aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = minByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

@Refine
@Interpretable("GroupByMin1")
public fun <T> Grouped<T>.min(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMin2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).cast<C>().aggregateAll(this, name, columns)

public fun <T> Grouped<T>.min(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.min(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMinOf")
public inline fun <T, reified C : Comparable<*>?> Grouped<T>.minOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).cast<C>().aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<*>?> GroupBy<T, G>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<*>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { minByOrNull(column, skipNaN) }

public fun <T, G> GroupBy<T, G>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<*>?> GroupBy<T, G>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    delegate { min(separate, skipNaN) }

public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { minFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<*>?> Pivot<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { min(skipNaN, columns) }

public fun <T> Pivot<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    min(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<*>?> Pivot<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { minOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<*>?> Pivot<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> Pivot<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { minByOrNull(column, skipNaN) }

public fun <T> Pivot<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> Pivot<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(separate, skipNaN, intraComparableColumns())

public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).cast<R>().aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    min(skipNaN) { columns.toComparableColumns() }

public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<*>?> PivotGroupBy<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { minOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<*>?> PivotGroupBy<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(column, skipNaN) }

public fun <T> PivotGroupBy<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<*>?> PivotGroupBy<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.min(): T = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(): T? = minOrNull(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minBy(noinline selector: (T) -> R): T =
    minBy(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minByOrNull(noinline selector: (T) -> R): T? =
    minByOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minOf(crossinline selector: (T) -> R): R =
    minOf(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> DataColumn<T>.minOfOrNull(crossinline selector: (T) -> R): R? =
    minOfOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOfOrNull(): T? = rowMinOfOrNull<T>(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOf(): T = rowMinOf(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.min(): DataRow<T> = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    minFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(vararg columns: ColumnReference<C>): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(vararg columns: KProperty<C>): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.min(columns: ColumnsSelector<T, C>): C =
    min(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.min(vararg columns: ColumnReference<C>): C =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.min(vararg columns: KProperty<C>): C =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C>): C? =
    minOrNull(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any>? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C>): C? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minOrNull(vararg columns: KProperty<C>): C? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minOf(crossinline expression: RowExpression<T, C>): C =
    minOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minOfOrNull(
    crossinline expression: RowExpression<T, C>,
): C? = minOfOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minBy(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(column: ColumnReference<C>): DataRow<T> =
    minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minBy(column: KProperty<C>): DataRow<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = minByOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? = minByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(column: ColumnReference<C>): DataRow<T>? =
    minByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> DataFrame<T>.minByOrNull(column: KProperty<C>): DataRow<T>? =
    minByOrNull(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.min(): DataFrame<T> = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    minFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.minFor(vararg columns: String): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(vararg columns: KProperty<C>): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.min(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = min(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.min(vararg columns: String, name: String? = null): DataFrame<T> =
    min(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
): DataFrame<T> = min(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.min(vararg columns: KProperty<C>, name: String? = null): DataFrame<T> =
    min(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> Grouped<T>.minOf(
    name: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = minOf(name, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified R : Comparable<*>?> GroupBy<T, G>.minBy(
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<*>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
): ReducedGroupBy<T, G> = minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<T, G>.minBy(column: String): ReducedGroupBy<T, G> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<*>?> GroupBy<T, G>.minBy(column: KProperty<C>): ReducedGroupBy<T, G> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.min(separate: Boolean = false): DataRow<T> = min(separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = minFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.min(columns: ColumnsSelector<T, R>): DataRow<T> =
    min(skipNaN = skipNaNDefault, columns = columns)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.min(vararg columns: ColumnReference<R>): DataRow<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.min(vararg columns: KProperty<R>): DataRow<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> Pivot<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = minOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> Pivot<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> Pivot<T>.minBy(column: ColumnReference<C>): ReducedPivot<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.minBy(column: String): ReducedPivot<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> Pivot<T>.minBy(column: KProperty<C>): ReducedPivot<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false): DataFrame<T> = min(separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = minFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(columns: ColumnsSelector<T, R>): DataFrame<T> =
    min(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.min(vararg columns: String): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(vararg columns: ColumnReference<R>): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.min(vararg columns: KProperty<R>): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> PivotGroupBy<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = minOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<*>?> PivotGroupBy<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
): ReducedPivotGroupBy<T> = minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.minBy(column: String): ReducedPivotGroupBy<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<*>?> PivotGroupBy<T>.minBy(column: KProperty<C>): ReducedPivotGroupBy<T> =
    minBy(column, skipNaN = skipNaNDefault)

// endregion
