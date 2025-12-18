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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveOrMixedNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/* TODO KDocs:
 * Calculating the std is supported for all primitive number types.
 * Nulls are filtered out.
 * The return type is always Double, Double.NaN for empty input, never null.
 * (May introduce loss of precision for Longs).
 * For mixed primitive number types, the aggregator unifies the numbers before calculating the mean.
 * ddof (Delta degrees of freedom) defaults to 1; "Bessel's correction", similar to R, "unbiased sample standard deviation".
 * Numpy uses 0, "population standard deviation".
 */

// region DataColumn

public fun DataColumn<Number?>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateSingleColumn(this)

public inline fun <T, reified R : Number?> DataColumn<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    noinline expression: (T) -> R,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region DataRow

public fun AnyRow.rowStd(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateOfRow(this, primitiveOrMixedNumberColumns())

public inline fun <reified T : Number?> AnyRow.rowStdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Std only supports primitive number types."
    }
    return Aggregators.std(skipNaN, ddof).aggregateOfRow(this) { colsOf<T>() }
}

// endregion

// region DataFrame
@Refine
@Interpretable("Std0")
public fun <T> DataFrame<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataRow<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("Std1")
public fun <T, C : Number?> DataFrame<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

public fun <T> DataFrame<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

public fun <T> DataFrame<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, Number?>,
): Double = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: ColumnReference<C>): Double = std { columns.toColumnSet() }

public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: KProperty<C>): Double = std { columns.toColumnSet() }

public inline fun <T, reified R : Number?> DataFrame<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region GroupBy
@Refine
@Interpretable("GroupByStd1")
public fun <T> Grouped<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataFrame<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("GroupByStd0")
public fun <T, C : Number?> Grouped<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

public fun <T> Grouped<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByStd2")
public fun <T, C : Number?> Grouped<T>.std(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, name, columns)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

public fun <T> Grouped<T>.std(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByStdOf")
public inline fun <T, reified R : Number?> Grouped<T>.stdOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

public fun <T, R : Number?> Pivot<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { stdFor(separate, skipNaN, ddof, columns) }

public fun <T> Pivot<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

public fun <T, C : Number?> Pivot<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { std(skipNaN, ddof, columns) }

public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

public fun <T> Pivot<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

public inline fun <reified T : Number?> Pivot<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, T>,
): DataRow<T> = delegate { stdOf(skipNaN, ddof, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

public fun <T, R : Number?> PivotGroupBy<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

public fun <T, C : Number?> PivotGroupBy<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

public fun <T> PivotGroupBy<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

public inline fun <T, reified R : Number?> PivotGroupBy<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion
