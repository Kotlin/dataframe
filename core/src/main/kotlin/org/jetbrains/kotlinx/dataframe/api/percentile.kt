@file:OptIn(ExperimentalTypeInference::class)

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
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

/* TODO KDocs
 * numbers -> Double or null
 * comparable -> itself or null
 *
 * TODO cases where the lambda dictates the return type require explicit type arguments for
 *  non-number, comparable overloads: https://youtrack.jetbrains.com/issue/KT-76683
 *  so, `df.percentile { intCol }` works, but needs `df.percentile<_, String> { stringCol }` or `df.percentile({ dateCol })`
 *  This needs to be explained by KDocs
 *
 * percentileBy is new for all overloads :)
 *
 * Uses [QuantileEstimationMethod.R8] for primitive numbers, else [QuantileEstimationMethod.R3].
 * PercentileBy also uses [QuantileEstimationMethod.R3].
 */

// region DataColumn

public fun <T : Comparable<T & Any>?> DataColumn<T>.percentile(percentile: Double): T & Any =
    percentileOrNull(percentile).suggestIfNull("percentile")

public fun <T : Comparable<T & Any>?> DataColumn<T>.percentileOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateSingleColumn(this)

public fun <T> DataColumn<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? =
    percentileOrNull(percentile = percentile, skipNaN = skipNaN).suggestIfNull("percentile")

public fun <T> DataColumn<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateSingleColumn(this)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = percentileByOrNull(percentile, skipNaN, selector).suggestIfNull("percentileBy")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateByOrNull(this, selector)

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOf(
    percentile: Double,
    crossinline expression: (T) -> R,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: (T) -> R,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

@Deprecated(ROW_PERCENTILE_OR_NULL, level = DeprecationLevel.ERROR)
public fun AnyRow.rowPercentileOrNull(): Nothing? = error(ROW_PERCENTILE_OR_NULL)

@Deprecated(ROW_PERCENTILE, level = DeprecationLevel.ERROR)
public fun AnyRow.rowPercentile(): Nothing = error(ROW_PERCENTILE)

public inline fun <reified T : Comparable<T>> AnyRow.rowPercentileOfOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T : Comparable<T>> AnyRow.rowPercentileOf(percentile: Double): T =
    rowPercentileOfOrNull<T>(percentile).suggestIfNull("rowPercentileOf")

public inline fun <reified T> AnyRow.rowPercentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T> AnyRow.rowPercentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number =
    rowPercentileOfOrNull<T>(percentile, skipNaN).suggestIfNull("rowPercentileOf")

// endregion

// region DataFrame
@Refine
@Interpretable("Percentile0")
public fun <T> DataFrame<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

@Refine
@Interpretable("Percentile1")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

// TODO, requires explicit type C due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C & Any = percentileOrNull(percentile, columns).suggestIfNull("percentile")

// TODO, requires explicit type C due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.percentileComparables<C>(percentile).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? =
    percentileOrNull(percentile, skipNaN, columns).suggestIfNull("percentile")

@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.percentileNumbers<C>(percentile, skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any = percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

public fun <T> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any? =
    Aggregators.percentileCommon<Comparable<Any>?>(percentile, skipNaN).aggregateAll(this) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
): C & Any = percentileOrNull(percentile, *columns).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: ColumnReference<C>,
): C? = percentileOrNull<T, C>(percentile) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
): C & Any = percentileOrNull(percentile, *columns).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: KProperty<C>,
): C? = percentileOrNull<T, C>(percentile) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, skipNaN) { columns.toColumnSet() }

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOf(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN).aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = percentileByOrNull(percentile, skipNaN, expression).suggestIfNull("percentileBy")

public fun <T> DataFrame<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByOrNull(percentile, column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByOrNull(percentile, column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy
@Refine
@Interpretable("GroupByPercentile1")
public fun <T> Grouped<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByPercentile0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.percentileFor(percentile: Double, vararg columns: String): DataFrame<T> =
    percentileFor(percentile) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByPercentile0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByPercentileOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.percentileOf(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression") // TODO?
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, column, skipNaN) }

public fun <T, G> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { percentileFor(percentile, separate, skipNaN, columns) }

public fun <T> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { percentile(percentile, skipNaN, columns) }

public fun <T> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { percentileOf(percentile, skipNaN, expression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

public fun <T> Pivot<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)
// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, expression)

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

public fun <T> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)

// endregion
