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

public fun <T : Comparable<T>> DataColumn<T?>.min(): T = minOrNull().suggestIfNull("min")

public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(): T? = Aggregators.min<T>().aggregateSingleColumn(this)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minBy(noinline selector: (T) -> R): T =
    minByOrNull(selector).suggestIfNull("minBy")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minByOrNull(noinline selector: (T) -> R): T? =
    Aggregators.min<R>().aggregateByOrNull(this, selector)

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOf(crossinline selector: (T) -> R): R & Any =
    minOfOrNull(selector).suggestIfNull("minOf")

public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOfOrNull(crossinline selector: (T) -> R): R? =
    Aggregators.min<R>().aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMinOrNull(): Any? =
    error("") // values().filterIsInstance<Comparable<*>>().minWithOrNull(compareBy { it })

@Deprecated("", level = DeprecationLevel.ERROR)
public fun AnyRow.rowMin(): Any = error("") // rowMinOrNull().suggestIfNull("rowMin")

// todo add rowMinBy?

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMinOfOrNull(): T? =
    Aggregators.min<T>().aggregateOfRow(this) { colsOf<T>() }

public inline fun <reified T : Comparable<T & Any>?> AnyRow.rowMinOf(): T & Any =
    rowMinOfOrNull<T>().suggestIfNull("rowMinOf")

// endregion

// region DataFrame

// TODO intraComparableOrNumber
public fun <T> DataFrame<T>.min(): DataRow<T> = minFor(intraComparableColumns())

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    Aggregators.min<C>().aggregateFor(this, columns)

public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> = minFor { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(vararg columns: ColumnReference<C>): DataRow<T> =
    minFor { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minFor(vararg columns: KProperty<C>): DataRow<T> =
    minFor { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(columns: ColumnsSelector<T, C>): C & Any =
    minOrNull(columns).suggestIfNull("min")

public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> = minOrNull(*columns).suggestIfNull("min")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(vararg columns: ColumnReference<C>): C & Any =
    minOrNull(*columns).suggestIfNull("min")

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(vararg columns: KProperty<C>): C & Any =
    minOrNull(*columns).suggestIfNull("min")

public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C>): C? =
    Aggregators.min<C>().aggregateAll(this, columns)

public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any>? =
    minOrNull { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C>): C? =
    minOrNull { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(vararg columns: KProperty<C>): C? =
    minOrNull { columns.toColumnSet() }

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOf(
    crossinline expression: RowExpression<T, C>,
): C & Any = minOfOrNull(expression).suggestIfNull("minOf")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOfOrNull(
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.min<C>().aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minByOrNull(expression).suggestIfNull("minBy")

public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minByOrNull(column).suggestIfNull("minBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(column: ColumnReference<C>): DataRow<T> =
    minByOrNull(column).suggestIfNull("minBy")

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(column: KProperty<C>): DataRow<T> =
    minByOrNull(column).suggestIfNull("minBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.min<C>().aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? =
    minByOrNull(column.toColumnOf<Comparable<Any>?>())

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: ColumnReference<C>,
): DataRow<T>? = Aggregators.min<C>().aggregateByOrNull(this, column)

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(column: KProperty<C>): DataRow<T>? =
    minByOrNull(column.toColumnAccessor())

// endregion

// region GroupBy

// TODO intraComparableOrNumber
@Refine
@Interpretable("GroupByMin1")
public fun <T> Grouped<T>.min(): DataFrame<T> = minFor(intraComparableColumns())

@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    Aggregators.min<C>().aggregateFor(this, columns)

public fun <T> Grouped<T>.minFor(vararg columns: String): DataFrame<T> = minFor { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    minFor { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.minFor(vararg columns: KProperty<C>): DataFrame<T> =
    minFor { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.min<C>().aggregateAll(this, name, columns)

public fun <T> Grouped<T>.min(vararg columns: String, name: String? = null): DataFrame<T> =
    min(name) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
): DataFrame<T> = min(name) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: KProperty<C>,
    name: String? = null,
): DataFrame<T> = min(name) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMinOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.minOf(
    name: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.min<C>().aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.minBy(
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { minByOrNull(rowExpression) }

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
): ReducedGroupBy<T, G> = reduce { minByOrNull(column) }

public fun <T, G> GroupBy<T, G>.minBy(column: String): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>())

@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: KProperty<C>,
): ReducedGroupBy<T, G> = minBy(column.toColumnAccessor())

// endregion

// region Pivot

public fun <T> Pivot<T>.min(separate: Boolean = false): DataRow<T> = delegate { min(separate) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { minFor(separate, columns) }

public fun <T> Pivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    minFor(separate) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(columns: ColumnsSelector<T, R>): DataRow<T> =
    delegate { min(columns) }

public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(vararg columns: String): DataRow<T> =
    min { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(vararg columns: ColumnReference<R>): DataRow<T> =
    min { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(vararg columns: KProperty<R>): DataRow<T> =
    min { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { minOf(rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { minByOrNull(rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(column: ColumnReference<C>): ReducedPivot<T> =
    reduce { minByOrNull(column) }

public fun <T> Pivot<T>.minBy(column: String): ReducedPivot<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any?>?>())

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(column: KProperty<C>): ReducedPivot<T> =
    minBy(column.toColumnAccessor())

// endregion

// region PivotGroupBy

// TODO intraComparableOrNumber
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false): DataFrame<T> = minFor(separate, intraComparableColumns())

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.min<R>().aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    minFor(separate) { columns.toComparableColumns() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(columns: ColumnsSelector<T, R>): DataFrame<T> =
    Aggregators.min<R>().aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.min(vararg columns: String): DataFrame<T> = min { columns.toComparableColumns() }

public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(vararg columns: ColumnReference<R>): DataFrame<T> =
    min { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(vararg columns: KProperty<R>): DataFrame<T> =
    min { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { minOf(rowExpression) }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(rowExpression) }

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(column) }

public fun <T> PivotGroupBy<T>.minBy(column: String): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any?>>())

@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: KProperty<C>,
): ReducedPivotGroupBy<T> = minBy(column.toColumnAccessor())

// endregion
