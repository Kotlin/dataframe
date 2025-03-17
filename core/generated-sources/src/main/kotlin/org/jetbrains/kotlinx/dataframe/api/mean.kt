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
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast2
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/*
 * TODO KDocs:
 * Calculating the mean is supported for all primitive number types.
 * Nulls are filtered from columns.
 * The return type is always Double, Double.NaN for empty input, never null.
 * (May introduce loss of precision for Longs).
 * For mixed primitive number types, [TwoStepNumbersAggregator] unifies the numbers before calculating the mean.
 */

// region DataColumn

public fun <T : Number> DataColumn<T?>.mean(skipNA: Boolean = skipNA_default): Double =
    Aggregators.mean(skipNA).aggregate(this)!!

public inline fun <T, reified R : Number> DataColumn<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: (T) -> R?,
): Double =
    Aggregators.mean(skipNA)
        .cast2<R?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

// endregion

// region DataRow

public fun AnyRow.rowMean(skipNA: Boolean = skipNA_default): Double =
    Aggregators.mean(skipNA).aggregateOfRow(this) {
        colsOf<Number?> { it.isPrimitiveNumber() }
    } ?: Double.NaN

public inline fun <reified T : Number> AnyRow.rowMeanOf(skipNA: Boolean = skipNA_default): Double {
    require(typeOf<T>() in primitiveNumberTypes) {
        "Type ${T::class.simpleName} is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.mean(skipNA)
        .aggregateOfRow(this) { colsOf<T>() }
        ?: Double.NaN
}

// endregion

// region DataFrame

public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default): DataRow<T> =
    meanFor(skipNA, primitiveNumberColumns())

public fun <T, C : Number> DataFrame<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)

public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataRow<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> DataFrame<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): Double = Aggregators.mean(skipNA).aggregateAll(this, columns) ?: Double.NaN

public fun <T> DataFrame<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): Double = mean(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: RowExpression<T, D?>,
): Double = Aggregators.mean(skipNA).of(this, expression) ?: Double.NaN

// endregion

// region GroupBy
@Refine
@Interpretable("GroupByMean1")
public fun <T> Grouped<T>.mean(skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA, primitiveNumberColumns())

@Refine
@Interpretable("GroupByMean0")
public fun <T, C : Number> Grouped<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, columns)

public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMean0")
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

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMeanOf")
public inline fun <T, reified R : Number> Grouped<T>.meanOf(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.mean(skipNA: Boolean = skipNA_default, separate: Boolean = false): DataRow<T> =
    meanFor(skipNA, separate, primitiveNumberColumns())

public fun <T, C : Number> Pivot<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = delegate { meanFor(skipNA, separate, columns) }

public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> Pivot<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, R?>,
): DataRow<T> = delegate { mean(skipNA, columns) }

public inline fun <T, reified R : Number> Pivot<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataRow<T> = delegate { meanOf(skipNA, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA, separate, primitiveNumberColumns())

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> PivotGroupBy<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, R?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    mean(skipNA) { columns.toColumnsSetOf() }

@AccessApiOverload
public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: ColumnReference<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: KProperty<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.mean(skipNA).aggregateOf(this, expression)

// endregion
