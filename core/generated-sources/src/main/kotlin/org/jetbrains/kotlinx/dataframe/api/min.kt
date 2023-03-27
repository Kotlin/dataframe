package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.comparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfDelegated
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.indexOfMin
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import kotlin.reflect.KProperty

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T?>.min(): T = minOrNull().suggestIfNull("min")
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(): T? = asSequence().filterNotNull().minOrNull()

public fun <T, R : Comparable<R>> DataColumn<T>.minBy(selector: (T) -> R): T = minByOrNull(selector).suggestIfNull("minBy")
public fun <T, R : Comparable<R>> DataColumn<T>.minByOrNull(selector: (T) -> R): T? = values.minByOrNull(selector)

public fun <T, R : Comparable<R>> DataColumn<T>.minOf(selector: (T) -> R): R = minOfOrNull(selector).suggestIfNull("minOf")
public fun <T, R : Comparable<R>> DataColumn<T>.minOfOrNull(selector: (T) -> R): R? = values.minOfOrNull(selector)

// endregion

// region DataRow

public fun AnyRow.rowMinOrNull(): Any? = values().filterIsInstance<Comparable<*>>().minWithOrNull(compareBy { it })
public fun AnyRow.rowMin(): Any = rowMinOrNull().suggestIfNull("rowMin")
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOfOrNull(): T? = values().filterIsInstance<T>().minOrNull()
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOf(): T = rowMinOfOrNull<T>().suggestIfNull("rowMinOf")

// endregion

// region DataFrame

public fun <T> DataFrame<T>.min(): DataRow<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.minFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> =
    Aggregators.min.aggregateFor(this, columns)

public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> = minFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: ColumnReference<C?>): DataRow<T> =
    minFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> DataFrame<T>.minFor(vararg columns: KProperty<C?>): DataRow<T> =
    minFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> DataFrame<T>.min(columns: ColumnsSelector<T, C?>): C =
    minOrNull(columns).suggestIfNull("min")

public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> = minOrNull(*columns).suggestIfNull("min")
public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: ColumnReference<C?>): C =
    minOrNull(*columns).suggestIfNull("min")

public fun <T, C : Comparable<C>> DataFrame<T>.min(vararg columns: KProperty<C?>): C =
    minOrNull(*columns).suggestIfNull("min")

public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C?>): C? =
    Aggregators.min.aggregateAll(this, columns) as C?

public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any?>? =
    minOrNull { columns.toComparableColumns() }

public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C?>): C? =
    minOrNull { columns.toColumnSet() }

public fun <T, C : Comparable<C>> DataFrame<T>.minOrNull(vararg columns: KProperty<C?>): C? =
    minOrNull { columns.toColumnSet() }

public fun <T, C : Comparable<C>> DataFrame<T>.minOf(expression: RowExpression<T, C>): C =
    minOfOrNull(expression).suggestIfNull("minOf")

public fun <T, C : Comparable<C>> DataFrame<T>.minOfOrNull(expression: RowExpression<T, C>): C? =
    rows().minOfOrNull { expression(it, it) }

public fun <T, C : Comparable<C>> DataFrame<T>.minBy(expression: RowExpression<T, C?>): DataRow<T> =
    minByOrNull(expression).suggestIfNull("minBy")

public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minByOrNull(column).suggestIfNull("minBy")
public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: ColumnReference<C?>): DataRow<T> =
    minByOrNull(column).suggestIfNull("minBy")

public fun <T, C : Comparable<C>> DataFrame<T>.minBy(column: KProperty<C?>): DataRow<T> =
    minByOrNull(column).suggestIfNull("minBy")

public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(expression: RowExpression<T, C?>): DataRow<T>? =
    getOrNull(rows().asSequence().map { expression(it, it) }.indexOfMin())
public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? = minByOrNull(column.toColumnOf<Comparable<Any?>?>())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: ColumnReference<C?>): DataRow<T>? = getOrNull(get(column).asSequence().indexOfMin())
public fun <T, C : Comparable<C>> DataFrame<T>.minByOrNull(column: KProperty<C?>): DataRow<T>? =
    minByOrNull(column.toColumnAccessor())

// endregion

// region GroupBy

public fun <T> Grouped<T>.min(): DataFrame<T> = minFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.minFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> =
    Aggregators.min.aggregateFor(this, columns)

public fun <T> Grouped<T>.minFor(vararg columns: String): DataFrame<T> = minFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.minFor(vararg columns: ColumnReference<C?>): DataFrame<T> =
    minFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.minFor(vararg columns: KProperty<C?>): DataFrame<T> =
    minFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.min(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> =
    Aggregators.min.aggregateAll(this, name, columns)

public fun <T> Grouped<T>.min(vararg columns: String, name: String? = null): DataFrame<T> =
    min(name) { columns.toComparableColumns() }

public fun <T, C : Comparable<C>> Grouped<T>.min(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
): DataFrame<T> = min(name) { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.min(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> =
    min(name) { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.minOf(
    name: String? = null,
    expression: RowExpression<T, C>,
): DataFrame<T> =
    Aggregators.min.aggregateOfDelegated(this, name) { minOfOrNull(expression) }

public fun <T, G, R : Comparable<R>> GroupBy<T, G>.minBy(rowExpression: RowExpression<G, R?>): ReducedGroupBy<T, G> =
    reduce { minByOrNull(rowExpression) }

public fun <T, G, C : Comparable<C>> GroupBy<T, G>.minBy(column: ColumnReference<C?>): ReducedGroupBy<T, G> =
    reduce { minByOrNull(column) }

public fun <T, G> GroupBy<T, G>.minBy(column: String): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor().cast<Comparable<Any?>>())

public fun <T, G, C : Comparable<C>> GroupBy<T, G>.minBy(column: KProperty<C?>): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor())

// endregion

// region Pivot

public fun <T> Pivot<T>.min(separate: Boolean = false): DataRow<T> = delegate { min(separate) }

public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { minFor(separate, columns) }

public fun <T> Pivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    minFor(separate) { columns.toComparableColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false,
): DataRow<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false,
): DataRow<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.min(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { min(columns) }
public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: String): DataRow<T> =
    min { columns.toComparableColumns() }

public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: ColumnReference<R?>): DataRow<T> =
    min { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.min(vararg columns: KProperty<R?>): DataRow<T> =
    min { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.minOf(rowExpression: RowExpression<T, R>): DataRow<T> =
    delegate { minOf(rowExpression) }

public fun <T, R : Comparable<R>> Pivot<T>.minBy(rowExpression: RowExpression<T, R>): ReducedPivot<T> =
    reduce { minByOrNull(rowExpression) }

public fun <T, C : Comparable<C>> Pivot<T>.minBy(column: ColumnReference<C?>): ReducedPivot<T> =
    reduce { minByOrNull(column) }

public fun <T> Pivot<T>.minBy(column: String): ReducedPivot<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any?>>())

public fun <T, C : Comparable<C>> Pivot<T>.minBy(column: KProperty<C?>): ReducedPivot<T> =
    minBy(column.toColumnAccessor())

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.min(separate: Boolean = false): DataFrame<T> = minFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.min.aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    minFor(separate) { columns.toComparableColumns() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false,
): DataFrame<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false,
): DataFrame<T> = minFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(columns: ColumnsSelector<T, R?>): DataFrame<T> =
    Aggregators.min.aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.min(vararg columns: String): DataFrame<T> = min { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(vararg columns: ColumnReference<R?>): DataFrame<T> =
    min { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.min(vararg columns: KProperty<R?>): DataFrame<T> =
    min { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minOf(rowExpression: RowExpression<T, R>): DataFrame<T> =
    aggregate { minOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.minBy(rowExpression: RowExpression<T, R>): ReducedPivotGroupBy<T> =
    reduce { minByOrNull(rowExpression) }

public fun <T, C : Comparable<C>> PivotGroupBy<T>.minBy(column: ColumnReference<C?>): ReducedPivotGroupBy<T> =
    reduce { minByOrNull(column) }

public fun <T> PivotGroupBy<T>.minBy(column: String): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any?>>())

public fun <T, C : Comparable<C>> PivotGroupBy<T>.minBy(column: KProperty<C?>): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor())

// endregion
