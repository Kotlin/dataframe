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
import kotlin.reflect.KProperty

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T?>.max(skipNaN: Boolean = skipNaN_default): T =
    maxOrNull(skipNaN).suggestIfNull("max")

public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(skipNaN: Boolean = skipNaN_default): T? =
    Aggregators.max<T>(skipNaN).aggregateSingleColumn(this)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxBy(
    skipNaN: Boolean = skipNaN_default,
    noinline selector: (T) -> R,
): T & Any = maxByOrNull(skipNaN, selector).suggestIfNull("maxBy")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxByOrNull(
    skipNaN: Boolean = skipNaN_default,
    noinline selector: (T) -> R,
): T? = Aggregators.max<R>(skipNaN).aggregateByOrNull(this, selector)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline selector: (T) -> R,
): R & Any = maxOfOrNull(skipNaN, selector).suggestIfNull("maxOf")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline selector: (T) -> R,
): R? = Aggregators.max<R>(skipNaN).aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMaxOrNull(): Any? =
    error("") // values().filterIsInstance<Comparable<*>>().maxWithOrNull(compareBy { it })

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMax(): Any = error("") // rowMaxOrNull().suggestIfNull("rowMax")

// todo add rowMaxBy?

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMaxOfOrNull(skipNaN: Boolean = skipNaN_default): T? =
    Aggregators.max<T>(skipNaN).aggregateOfRow(this) { colsOf<T>() }

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMaxOf(skipNaN: Boolean = skipNaN_default): T & Any =
    rowMaxOfOrNull<T>(skipNaN).suggestIfNull("rowMaxOf")

// endregion

// region DataFrame

// TODO intraComparableOrNumber
public fun <T> DataFrame<T>.max(skipNaN: Boolean = skipNaN_default): DataRow<T> =
    maxFor(skipNaN, intraComparableColumns())

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.max<C>(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C>,
): C & Any = maxOrNull(skipNaN, columns).suggestIfNull("max")

public fun <T> DataFrame<T>.max(vararg columns: String, skipNaN: Boolean = skipNaN_default): Comparable<Any> =
    maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.maxOrNull(vararg columns: String, skipNaN: Boolean = skipNaN_default): Comparable<Any>? =
    maxOrNull(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): C & Any = maxOfOrNull(skipNaN, expression).suggestIfNull("maxOf")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = maxByOrNull(skipNaN, expression).suggestIfNull("maxBy")

public fun <T> DataFrame<T>.maxBy(column: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.maxByOrNull(column: String, skipNaN: Boolean = skipNaN_default): DataRow<T>? =
    maxByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, column)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T>? = maxByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

// TODO intraComparableOrNumber
@Refine
@Interpretable("GroupByMax1")
public fun <T> Grouped<T>.max(skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    maxFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByMax0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMax0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.max(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = max(name, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMaxOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.maxOf(
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.maxBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(column, skipNaN) }

public fun <T, G> GroupBy<T, G>.maxBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedGroupBy<T, G> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedGroupBy<T, G> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    delegate { max(separate, skipNaN) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { maxFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { max(skipNaN, columns) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: String,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = max(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { maxOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivot<T> = reduce { maxByOrNull(column, skipNaN) }

public fun <T> Pivot<T>.maxBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedPivot<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivot<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

// TODO intraComparableOrNumber
public fun <T> PivotGroupBy<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    maxFor(separate, skipNaN, intraComparableColumns())

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.max<R>(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.max<R>(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.max(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    max(skipNaN) { columns.toComparableColumns() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { maxOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(column, skipNaN) }

public fun <T> PivotGroupBy<T>.maxBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedPivotGroupBy<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivotGroupBy<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion
