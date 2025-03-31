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

public fun <T : Comparable<T>> DataColumn<T?>.min(skipNaN: Boolean = skipNaN_default): T =
    minOrNull(skipNaN).suggestIfNull("min")

public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(skipNaN: Boolean = skipNaN_default): T? =
    Aggregators.min<T>(skipNaN).aggregateSingleColumn(this)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minBy(
    skipNaN: Boolean = skipNaN_default,
    noinline selector: (T) -> R,
): T & Any = minByOrNull(skipNaN, selector).suggestIfNull("minBy")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minByOrNull(
    skipNaN: Boolean = skipNaN_default,
    noinline selector: (T) -> R,
): T? = Aggregators.min<R>(skipNaN).aggregateByOrNull(this, selector)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline selector: (T) -> R,
): R & Any = minOfOrNull(skipNaN, selector).suggestIfNull("minOf")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOfOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline selector: (T) -> R,
): R? = Aggregators.min<R>(skipNaN).aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMinOrNull(): Any? =
    error("") // values().filterIsInstance<Comparable<*>>().minWithOrNull(compareBy { it })

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMin(): Any = error("") // rowMinOrNull().suggestIfNull("rowMin")

// todo add rowMinBy?

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMinOfOrNull(skipNaN: Boolean = skipNaN_default): T? =
    Aggregators.min<T>(skipNaN).aggregateOfRow(this) { colsOf<T>() }

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMinOf(skipNaN: Boolean = skipNaN_default): T & Any =
    rowMinOfOrNull<T>(skipNaN).suggestIfNull("rowMinOf")

// endregion

// region DataFrame

// TODO intraComparableOrNumber
public fun <T> DataFrame<T>.min(skipNaN: Boolean = skipNaN_default): DataRow<T> =
    minFor(skipNaN, intraComparableColumns())

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.min<C>(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C>,
): C & Any = minOrNull(skipNaN, columns).suggestIfNull("min")

public fun <T> DataFrame<T>.min(vararg columns: String, skipNaN: Boolean = skipNaN_default): Comparable<Any> =
    minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): C & Any = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): C & Any = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.minOrNull(vararg columns: String, skipNaN: Boolean = skipNaN_default): Comparable<Any>? =
    minOrNull(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): C & Any = minOfOrNull(skipNaN, expression).suggestIfNull("minOf")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOfOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minByOrNull(skipNaN, expression).suggestIfNull("minBy")

public fun <T> DataFrame<T>.minBy(column: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    minByOrNull(column, skipNaN).suggestIfNull("minBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.min<C>(skipNaN).aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.minByOrNull(column: String, skipNaN: Boolean = skipNaN_default): DataRow<T>? =
    minByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T>? = Aggregators.min<C>(skipNaN).aggregateByOrNull(this, column)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T>? = minByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

// TODO intraComparableOrNumber
@Refine
@Interpretable("GroupByMin1")
public fun <T> Grouped<T>.min(skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    minFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.min(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = min(name, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMinOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.minOf(
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.minBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { minByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedGroupBy<T, G> = reduce { minByOrNull(column, skipNaN) }

public fun <T, G> GroupBy<T, G>.minBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedGroupBy<T, G> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    delegate { min(separate, skipNaN) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { minFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { min(skipNaN, columns) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    vararg columns: String,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = min(skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { minOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivot<T> = reduce { minByOrNull(column, skipNaN) }

public fun <T> Pivot<T>.minBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedPivot<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivot<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

// TODO intraComparableOrNumber
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    minFor(separate, skipNaN, intraComparableColumns())

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.min<R>(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.min<R>(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.min(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    min(skipNaN) { columns.toComparableColumns() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { minOf(skipNaN, rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minBy(
    skipNaN: Boolean = skipNaN_default,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(column, skipNaN) }

public fun <T> PivotGroupBy<T>.minBy(column: String, skipNaN: Boolean = skipNaN_default): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaN_default,
): ReducedPivotGroupBy<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion
