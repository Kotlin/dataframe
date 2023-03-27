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
import org.jetbrains.kotlinx.dataframe.impl.indexOfMax
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import kotlin.reflect.KProperty

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T?>.max(): T = maxOrNull().suggestIfNull("max")
public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(): T? = asSequence().filterNotNull().maxOrNull()

public fun <T, R : Comparable<R>> DataColumn<T>.maxBy(selector: (T) -> R): T = maxByOrNull(selector).suggestIfNull("maxBy")
public fun <T, R : Comparable<R>> DataColumn<T>.maxByOrNull(selector: (T) -> R): T? = values.maxByOrNull(selector)

public fun <T, R : Comparable<R>> DataColumn<T>.maxOf(selector: (T) -> R): R = maxOfOrNull(selector).suggestIfNull("maxOf")
public fun <T, R : Comparable<R>> DataColumn<T>.maxOfOrNull(selector: (T) -> R): R? = values.maxOfOrNull(selector)

// endregion

// region DataRow

public fun AnyRow.rowMaxOrNull(): Any? = values().filterIsInstance<Comparable<*>>().maxWithOrNull(compareBy { it })
public fun AnyRow.rowMax(): Any = rowMaxOrNull().suggestIfNull("rowMax")
public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOfOrNull(): T? = values().filterIsInstance<T>().maxOrNull()
public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOf(): T = rowMaxOfOrNull<T>().suggestIfNull("rowMaxOf")

// endregion

// region DataFrame

public fun <T> DataFrame<T>.max(): DataRow<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.max.aggregateFor(this, columns)
public fun <T> DataFrame<T>.maxFor(vararg columns: String): DataRow<T> = maxFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: ColumnReference<C?>): DataRow<T> = maxFor { columns.toColumnSet() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxFor(vararg columns: KProperty<C?>): DataRow<T> = maxFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> DataFrame<T>.max(columns: ColumnsSelector<T, C?>): C = maxOrNull(columns).suggestIfNull("max")
public fun <T> DataFrame<T>.max(vararg columns: String): Comparable<Any?> = maxOrNull(*columns).suggestIfNull("max")
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: ColumnReference<C?>): C = maxOrNull(*columns).suggestIfNull("max")
public fun <T, C : Comparable<C>> DataFrame<T>.max(vararg columns: KProperty<C?>): C = maxOrNull(*columns).suggestIfNull("max")

public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(columns: ColumnsSelector<T, C?>): C? = Aggregators.max.aggregateAll(this, columns) as C?
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String): Comparable<Any?>? = maxOrNull { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: ColumnReference<C?>): C? = maxOrNull { columns.toColumnSet() }
public fun <T, C : Comparable<C>> DataFrame<T>.maxOrNull(vararg columns: KProperty<C?>): C? = maxOrNull { columns.toColumnSet() }

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

// region GroupBy

public fun <T> Grouped<T>.max(): DataFrame<T> = maxFor(comparableColumns())

public fun <T, C : Comparable<C>> Grouped<T>.maxFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.max.aggregateFor(this, columns)
public fun <T> Grouped<T>.maxFor(vararg columns: String): DataFrame<T> = maxFor { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.maxFor(vararg columns: ColumnReference<C?>): DataFrame<T> = maxFor { columns.toColumnSet() }
public fun <T, C : Comparable<C>> Grouped<T>.maxFor(vararg columns: KProperty<C?>): DataFrame<T> = maxFor { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.max(
    name: String? = null,
    columns: ColumnsSelector<T, C?>
): DataFrame<T> =
    Aggregators.max.aggregateAll(this, name, columns)
public fun <T> Grouped<T>.max(vararg columns: String, name: String? = null): DataFrame<T> = max(name) { columns.toComparableColumns() }
public fun <T, C : Comparable<C>> Grouped<T>.max(vararg columns: ColumnReference<C?>, name: String? = null): DataFrame<T> = max(name) { columns.toColumnSet() }
public fun <T, C : Comparable<C>> Grouped<T>.max(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> = max(name) { columns.toColumnSet() }

public fun <T, C : Comparable<C>> Grouped<T>.maxOf(name: String? = null, expression: RowExpression<T, C>): DataFrame<T> =
    Aggregators.max.aggregateOfDelegated(this, name) { maxOfOrNull(expression) }

public fun <T, G, R : Comparable<R>> GroupBy<T, G>.maxBy(rowExpression: RowExpression<G, R?>): ReducedGroupBy<T, G> = reduce { maxByOrNull(rowExpression) }
public fun <T, G, C : Comparable<C>> GroupBy<T, G>.maxBy(column: ColumnReference<C?>): ReducedGroupBy<T, G> = reduce { maxByOrNull(column) }
public fun <T, G> GroupBy<T, G>.maxBy(column: String): ReducedGroupBy<T, G> = maxBy(column.toColumnAccessor().cast<Comparable<Any?>>())
public fun <T, G, C : Comparable<C>> GroupBy<T, G>.maxBy(column: KProperty<C?>): ReducedGroupBy<T, G> = maxBy(column.toColumnAccessor())

// endregion

// region Pivot

public fun <T> Pivot<T>.max(separate: Boolean = false): DataRow<T> = delegate { max(separate) }

public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { maxFor(separate, columns) }
public fun <T> Pivot<T>.maxFor(vararg columns: String, separate: Boolean = false): DataRow<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumnSet() }
public fun <T, R : Comparable<R>> Pivot<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataRow<T> = maxFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.max(columns: ColumnsSelector<T, R?>): DataRow<T> = delegate { max(columns) }
public fun <T> Pivot<T>.max(vararg columns: String): DataRow<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> Pivot<T>.max(vararg columns: ColumnReference<R?>): DataRow<T> = max { columns.toColumnSet() }
public fun <T, R : Comparable<R>> Pivot<T>.max(vararg columns: KProperty<R?>): DataRow<T> = max { columns.toColumnSet() }

public fun <T, R : Comparable<R>> Pivot<T>.maxOf(rowExpression: RowExpression<T, R>): DataRow<T> = delegate { maxOf(rowExpression) }

public fun <T, R : Comparable<R>> Pivot<T>.maxBy(rowExpression: RowExpression<T, R>): ReducedPivot<T> = reduce { maxByOrNull(rowExpression) }
public fun <T, C : Comparable<C>> Pivot<T>.maxBy(column: ColumnReference<C?>): ReducedPivot<T> = reduce { maxByOrNull(column) }
public fun <T> Pivot<T>.maxBy(column: String): ReducedPivot<T> = maxBy(column.toColumnAccessor().cast<Comparable<Any?>>())
public fun <T, C : Comparable<C>> Pivot<T>.maxBy(column: KProperty<C?>): ReducedPivot<T> = maxBy(column.toColumnAccessor())

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.max(separate: Boolean = false): DataFrame<T> = maxFor(separate, comparableColumns())

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.max.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.maxFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = maxFor(separate) { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumnSet() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R?>,
    separate: Boolean = false
): DataFrame<T> = maxFor(separate) { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(columns: ColumnsSelector<T, R?>): DataFrame<T> = Aggregators.max.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.max(vararg columns: String): DataFrame<T> = max { columns.toComparableColumns() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(vararg columns: ColumnReference<R?>): DataFrame<T> = max { columns.toColumnSet() }
public fun <T, R : Comparable<R>> PivotGroupBy<T>.max(vararg columns: KProperty<R?>): DataFrame<T> = max { columns.toColumnSet() }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxOf(rowExpression: RowExpression<T, R>): DataFrame<T> = aggregate { maxOf(rowExpression) }

public fun <T, R : Comparable<R>> PivotGroupBy<T>.maxBy(rowExpression: RowExpression<T, R>): ReducedPivotGroupBy<T> = reduce { maxByOrNull(rowExpression) }
public fun <T, C : Comparable<C>> PivotGroupBy<T>.maxBy(column: ColumnReference<C?>): ReducedPivotGroupBy<T> = reduce { maxByOrNull(column) }
public fun <T> PivotGroupBy<T>.maxBy(column: String): ReducedPivotGroupBy<T> = maxBy(column.toColumnAccessor().cast<Comparable<Any?>>())
public fun <T, C : Comparable<C>> PivotGroupBy<T>.maxBy(column: KProperty<C?>): ReducedPivotGroupBy<T> = maxBy(column.toColumnAccessor())

// endregion
