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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.zero
import org.jetbrains.kotlinx.dataframe.math.sum
import org.jetbrains.kotlinx.dataframe.math.sumOf
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region DataColumn

@JvmName("sumT")
public fun <T : Number> DataColumn<T>.sum(): T = values.sum(type())

@JvmName("sumTNullable")
public fun <T : Number> DataColumn<T?>.sum(): T = values.sum(type())

public inline fun <T, reified R : Number> DataColumn<T>.sumOf(crossinline expression: (T) -> R): R? =
    (Aggregators.sum as Aggregator<*, *>).cast<R>().of(this, expression)

// endregion

// region DataRow

public fun AnyRow.rowSum(): Number = org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators.sum.aggregateMixed(values().filterIsInstance<Number>()) ?: 0
public inline fun <reified T : Number> AnyRow.rowSumOf(): T = values().filterIsInstance<T>().sum(typeOf<T>())

// endregion

// region DataFrame

public fun <T> DataFrame<T>.sum(): DataRow<T> = sumFor(numberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> = Aggregators.sum.aggregateFor(this, columns)
public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> = sumFor { columns.toColumnsOf() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: ColumnReference<C?>): DataRow<T> = sumFor { columns.toColumns() }
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: KProperty<C?>): DataRow<T> = sumFor { columns.toColumns() }

public inline fun <T, reified C : Number> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C?>): C = (Aggregators.sum.aggregateAll(this, columns) as C?) ?: C::class.zero()
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: ColumnReference<C?>): C = sum { columns.toColumns() }
public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum { columns.toColumnsOf() }
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: KProperty<C?>): C = sum { columns.toColumns() }

public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): C = rows().sumOf(
    typeOf<C>()
) { expression(it, it) }

// endregion

// region GroupBy

public fun <T> Grouped<T>.sum(): DataFrame<T> = sumFor(numberColumns())

public fun <T, C : Number> Grouped<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> = Aggregators.sum.aggregateFor(this, columns)
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
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.sum.aggregateOf(this, resultName, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> Pivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> =
    delegate { sumFor(separate, columns) }
public fun <T> Pivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataRow<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataRow<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> Pivot<T>.sum(columns: ColumnsSelector<T, C?>): DataRow<T> =
    delegate { sum(columns) }
public fun <T> Pivot<T>.sum(vararg columns: String): DataRow<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> Pivot<T>.sum(vararg columns: ColumnReference<C?>): DataRow<T> = sum { columns.toColumns() }
public fun <T, C : Number> Pivot<T>.sum(vararg columns: KProperty<C?>): DataRow<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    delegate { sumOf(expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.sum.aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toNumberColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false
): DataFrame<T> = sumFor(separate) { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataFrame<T> = sumFor(separate) { columns.toColumns() }

public fun <T, C : Number> PivotGroupBy<T>.sum(columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.sum(vararg columns: String): DataFrame<T> = sum { columns.toNumberColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: ColumnReference<C?>): DataFrame<T> = sum { columns.toColumns() }
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: KProperty<C?>): DataFrame<T> = sum { columns.toColumns() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.sumOf(crossinline expression: RowExpression<T, R>): DataFrame<T> =
    Aggregators.sum.aggregateOf(this, expression)

// endregion
