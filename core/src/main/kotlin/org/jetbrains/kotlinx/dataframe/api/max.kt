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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.intraComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateByOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MAX_NO_SKIPNAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MAX
import org.jetbrains.kotlinx.dataframe.util.ROW_MAX_OR_NULL
import kotlin.reflect.KProperty

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T?>.max(skipNaN: Boolean = skipNaNDefault): T =
    maxOrNull(skipNaN).suggestIfNull("max")

public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.max<T>(skipNaN).aggregateSingleColumn(this)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = maxByOrNull(skipNaN, selector).suggestIfNull("maxBy")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.max<R>(skipNaN).aggregateByOrNull(this, selector)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R & Any = maxOfOrNull(skipNaN, selector).suggestIfNull("maxOf")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R? = Aggregators.max<R>(skipNaN).aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated(ROW_MAX_OR_NULL, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMaxOrNull(): Nothing? = error(ROW_MAX_OR_NULL)

@Deprecated(ROW_MAX, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMax(): Nothing = error(ROW_MAX)

public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOfOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.max<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOf(skipNaN: Boolean = skipNaNDefault): T =
    rowMaxOfOrNull<T>(skipNaN).suggestIfNull("rowMaxOf")

// endregion

// region DataFrame
@Refine
@Interpretable("Max0")
public fun <T> DataFrame<T>.max(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("Max1")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.max<C>(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = maxOrNull(skipNaN, columns).suggestIfNull("max")

public fun <T> DataFrame<T>.max(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any> =
    maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.maxOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any>? =
    maxOrNull(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = maxOfOrNull(skipNaN, expression).suggestIfNull("maxOf")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = maxByOrNull(skipNaN, expression).suggestIfNull("maxBy")

public fun <T> DataFrame<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.maxByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    maxByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = maxByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

@Refine
@Interpretable("GroupByMax1")
public fun <T> Grouped<T>.max(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByMax0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMax0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.max(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMaxOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.maxOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(column, skipNaN) }

public fun <T, G> GroupBy<T, G>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    delegate { max(separate, skipNaN) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { maxFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { max(skipNaN, columns) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = max(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { maxOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { maxByOrNull(column, skipNaN) }

public fun <T> Pivot<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(separate, skipNaN, intraComparableColumns())

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.max<R>(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.max<R>(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.max(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    max(skipNaN) { columns.toComparableColumns() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { maxOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(column, skipNaN) }

public fun <T> PivotGroupBy<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.max(): T = max(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(): T? = maxOrNull(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxBy(noinline selector: (T) -> R): T & Any =
    maxBy(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxByOrNull(noinline selector: (T) -> R): T? =
    maxByOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOf(crossinline selector: (T) -> R): R & Any =
    maxOf(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOfOrNull(crossinline selector: (T) -> R): R? =
    maxOfOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOfOrNull(): T? = rowMaxOfOrNull<T>(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMaxOf(): T & Any = rowMaxOf(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.max(): DataRow<T> = max(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    maxFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxFor(vararg columns: String): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(vararg columns: ColumnReference<C>): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(vararg columns: KProperty<C>): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(columns: ColumnsSelector<T, C>): C & Any =
    max(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.max(vararg columns: String): Comparable<Any> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(vararg columns: ColumnReference<C>): C & Any =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(vararg columns: KProperty<C>): C & Any =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(columns: ColumnsSelector<T, C>): C? =
    maxOrNull(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String): Comparable<Any>? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(vararg columns: ColumnReference<C>): C? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(vararg columns: KProperty<C>): C? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOf(
    crossinline expression: RowExpression<T, C>,
): C & Any = maxOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOfOrNull(
    crossinline expression: RowExpression<T, C>,
): C? = maxOfOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = maxBy(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxBy(column: String): DataRow<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(column: ColumnReference<C>): DataRow<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(column: KProperty<C>): DataRow<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = maxByOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxByOrNull(column: String): DataRow<T>? = maxByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: ColumnReference<C>,
): DataRow<T>? = maxByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(column: KProperty<C>): DataRow<T>? =
    maxByOrNull(column, skipNaN = skipNaNDefault)

@Refine
@Interpretable("GroupByMax1")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.max(): DataFrame<T> = max(skipNaN = skipNaNDefault)

@Refine
@Interpretable("GroupByMax0")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    maxFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.maxFor(vararg columns: String): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(vararg columns: KProperty<C>): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@Refine
@Interpretable("GroupByMax0")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = max(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.max(vararg columns: String, name: String? = null): DataFrame<T> =
    max(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: ColumnReference<C>,
    name: String? = null,
): DataFrame<T> = max(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: KProperty<C>,
    name: String? = null,
): DataFrame<T> = max(columns = columns, name = name, skipNaN = skipNaNDefault)

@Refine
@Interpretable("GroupByMaxOf")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.maxOf(
    name: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = maxOf(name, skipNaN = skipNaNDefault, expression = expression)

@Interpretable("GroupByReduceExpression")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.maxBy(
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: ColumnReference<C>,
): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<T, G>.maxBy(column: String): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: KProperty<C>,
): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.max(separate: Boolean = false): DataRow<T> = max(separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = maxFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.maxFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataRow<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataRow<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(columns: ColumnsSelector<T, R>): DataRow<T> =
    max(skipNaN = skipNaNDefault, columns = columns)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(vararg columns: ColumnReference<R>): DataRow<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(vararg columns: KProperty<R>): DataRow<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = maxOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(column: ColumnReference<C>): ReducedPivot<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.maxBy(column: String): ReducedPivot<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(column: KProperty<C>): ReducedPivot<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.max(separate: Boolean = false): DataFrame<T> = max(separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = maxFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.maxFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataFrame<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataFrame<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(columns: ColumnsSelector<T, R>): DataFrame<T> =
    max(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.max(vararg columns: String): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(vararg columns: ColumnReference<R>): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(vararg columns: KProperty<R>): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = maxOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: ColumnReference<C>,
): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.maxBy(column: String): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: KProperty<C>,
): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

// endregion
