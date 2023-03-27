package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast2
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.mean
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region DataColumn

public fun <T : Number> DataColumn<T?>.mean(skipNA: Boolean = skipNA_default): Double = meanOrNull(skipNA).suggestIfNull("mean")
public fun <T : Number> DataColumn<T?>.meanOrNull(skipNA: Boolean = skipNA_default): Double? = Aggregators.mean(skipNA).aggregate(this)

public inline fun <T, reified R : Number> DataColumn<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: (T) -> R?
): Double = Aggregators.mean(skipNA).cast2<R?, Double>().aggregateOf(this, expression) ?: Double.NaN

// endregion

// region DataRow

public fun AnyRow.rowMean(skipNA: Boolean = org.jetbrains.kotlinx.dataframe.api.skipNA_default): Double = values().filterIsInstance<Number>().map { it.toDouble() }.mean(skipNA)
public inline fun <reified T : Number> AnyRow.rowMeanOf(): Double = values().filterIsInstance<T>().mean(typeOf<T>())

// endregion

// region DataFrame

public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default): DataRow<T> = meanFor(skipNA, numberColumns())

public fun <T, C : Number> DataFrame<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)

public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataRow<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> DataFrame<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): Double = Aggregators.mean(skipNA).aggregateAll(this, columns) as Double? ?: Double.NaN

public fun <T> DataFrame<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toNumberColumns() }

public fun <T, C : Number> DataFrame<T>.mean(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): Double = mean(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: RowExpression<T, D?>,
): Double = Aggregators.mean(skipNA).of(this, expression) ?: Double.NaN

// endregion

// region GroupBy

public fun <T> Grouped<T>.mean(skipNA: Boolean = skipNA_default): DataFrame<T> = meanFor(skipNA, numberColumns())

public fun <T, C : Number> Grouped<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)

public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> Grouped<T>.mean(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.mean(
    vararg columns: String,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toNumberColumns() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

public inline fun <T, reified R : Number> Grouped<T>.meanOf(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.mean(skipNA: Boolean = skipNA_default, separate: Boolean = false): DataRow<T> = meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> Pivot<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = delegate { meanFor(skipNA, separate, columns) }

public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> Pivot<T>.mean(skipNA: Boolean = skipNA_default, columns: ColumnsSelector<T, R?>): DataRow<T> =
    delegate { mean(skipNA, columns) }

public inline fun <T, reified R : Number> Pivot<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>
): DataRow<T> =
    delegate { meanOf(skipNA, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNA: Boolean = skipNA_default): DataFrame<T> = meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> PivotGroupBy<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, R?>,
): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    mean(skipNA) { columns.toColumnsSetOf() }

public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: ColumnReference<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: KProperty<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> =
    Aggregators.mean(skipNA).aggregateOf(this, expression)

// endregion
