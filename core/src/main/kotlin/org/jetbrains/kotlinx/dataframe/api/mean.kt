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
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MEAN_NO_SKIPNAN
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/* TODO KDocs:
 * Calculating the mean is supported for all primitive number types.
 * Nulls are filtered out.
 * The return type is always Double, Double.NaN for empty input, never null.
 * (May introduce loss of precision for Longs).
 * For mixed primitive number types, the aggregator unifies the numbers before calculating the mean.
 */

// region DataColumn

public fun DataColumn<Number?>.mean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateSingleColumn(this)

public inline fun <T, reified R : Number?> DataColumn<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

public fun AnyRow.rowMean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

public inline fun <reified T : Number> AnyRow.rowMeanOf(skipNaN: Boolean = skipNaNDefault): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.mean(skipNaN).aggregateOfRow(this) { colsOf<T?>() }
}

// endregion

// region DataFrame
@Refine
@Interpretable("Mean0")
public fun <T> DataFrame<T>.mean(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("Mean1")
public fun <T, C : Number?> DataFrame<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    meanFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = meanFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = meanFor(skipNaN) { columns.toColumnSet() }

public fun <T, C : Number?> DataFrame<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double = Aggregators.mean(skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.mean(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Double =
    mean(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.mean(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double = mean(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.mean(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double = mean(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified D : Number?> DataFrame<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, D>,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region GroupBy
@Refine
@Interpretable("GroupByMean1")
public fun <T> Grouped<T>.mean(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("GroupByMean0")
public fun <T, C : Number?> Grouped<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMean2")
public fun <T, C : Number?> Grouped<T>.mean(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.mean(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.mean(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.mean(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMeanOf")
public inline fun <T, reified R : Number?> Grouped<T>.meanOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.mean(skipNaN: Boolean = skipNaNDefault, separate: Boolean = false): DataRow<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

public fun <T, C : Number?> Pivot<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { meanFor(skipNaN, separate, columns) }

public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

public fun <T, R : Number?> Pivot<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { mean(skipNaN, columns) }

public inline fun <T, reified R : Number?> Pivot<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { meanOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

public fun <T, R : Number?> PivotGroupBy<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    mean(skipNaN) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Number?> PivotGroupBy<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region binary compatibility

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun DataColumn<Number?>.mean(): Double = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> DataColumn<T>.meanOf(crossinline expression: (T) -> R): Double =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun AnyRow.rowMean(): Double = rowMean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Number> AnyRow.rowMeanOf(): Double = rowMeanOf<T>(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.mean(): DataRow<T> = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number> DataFrame<T>.meanFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> =
    meanFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.meanFor(vararg columns: String): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.meanFor(vararg columns: ColumnReference<C>): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.meanFor(vararg columns: KProperty<C>): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(columns: ColumnsSelector<T, C>): Double =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.mean(vararg columns: String): Double = mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(vararg columns: ColumnReference<C>): Double =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(vararg columns: KProperty<C>): Double =
    mean(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified D : Number?> DataFrame<T>.meanOf(crossinline expression: RowExpression<T, D>): Double =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.mean(): DataFrame<T> = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    meanFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.meanFor(vararg columns: String): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(vararg columns: KProperty<C>): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(name: String? = null, columns: ColumnsSelector<T, C>): DataFrame<T> =
    mean(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.mean(vararg columns: String, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(vararg columns: ColumnReference<C>, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(vararg columns: KProperty<C>, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Grouped<T>.meanOf(
    name: String? = null,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = meanOf(name, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.mean(separate: Boolean = false): DataRow<T> =
    mean(skipNaN = skipNaNDefault, separate = separate)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = meanFor(skipNaN = skipNaNDefault, separate = separate, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.meanFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataRow<T> = meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(vararg columns: KProperty<C>, separate: Boolean = false): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> Pivot<T>.mean(columns: ColumnsSelector<T, R>): DataRow<T> =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Pivot<T>.meanOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false): DataFrame<T> = mean(separate, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = meanFor(skipNaN = skipNaNDefault, separate = separate, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.meanFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataFrame<T> = meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
): DataFrame<T> = meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(columns: ColumnsSelector<T, R>): DataFrame<T> =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.mean(vararg columns: String): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(vararg columns: ColumnReference<R>): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(vararg columns: KProperty<R>): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> PivotGroupBy<T>.meanOf(
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = meanOf(skipNaN = skipNaNDefault, expression = expression)

// endregion
