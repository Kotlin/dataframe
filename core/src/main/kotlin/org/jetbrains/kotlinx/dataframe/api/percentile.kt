@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ConfigurableStatisticsDocs
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.intraComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateByOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.api.percentileByWithMethodOrNullImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

/* TODO KDocs
 * primitive numbers -> Double or null
 * comparable -> itself or null
 *
 * Careful! non-primitive numbers will thus follow comparable rules
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

/** @include [ConfigurableStatisticsDocs.InterpolationArithmetic] */
public fun <T> DataColumn<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? =
    percentileOrNull(percentile = percentile, skipNaN = skipNaN).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.InterpolationArithmetic] */
public fun <T> DataColumn<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateSingleColumn(this)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = percentileByOrNull(percentile, skipNaN, selector).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.ByNaN] */
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
public fun DataRow<*>.rowPercentileOrNull(): Nothing? = error(ROW_PERCENTILE_OR_NULL)

@Deprecated(ROW_PERCENTILE, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowPercentile(): Nothing = error(ROW_PERCENTILE)

public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOfOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOf(percentile: Double): T =
    rowPercentileOfOrNull<T>(percentile).suggestIfNull("rowPercentileOf")

public inline fun <reified T> DataRow<*>.rowPercentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T> DataRow<*>.rowPercentileOf(
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
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

@Refine
@StringApiInterpretable(interpreter = "Percentile1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
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

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = percentileByOrNull(percentile, skipNaN, expression).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public fun <T> DataFrame<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, expression)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public fun <T> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByOrNull(percentile, column.toColumnOf<Comparable<Any>?>(), skipNaN)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, column)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
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
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

@Refine
@StringApiInterpretable(interpreter = "GroupByPercentile0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.percentileFor(percentile: Double, vararg columns: String): DataFrame<T> =
    percentileFor(percentile) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByPercentile2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, name, columns)

@Refine
@StringApiInterpretable(interpreter = "GroupByPercentile2", stringArgument = "columns", targetArgument = "columns")
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

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Interpretable("GroupByReduceExpression") // TODO?
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public fun <T, G> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
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

public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
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
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
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

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public fun <T> Pivot<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
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

public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
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

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/** @include [ConfigurableStatisticsDocs.ByNaN] */
public fun <T> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

/** @include [ConfigurableStatisticsDocs.ByNaN] */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)

// endregion

// region Configurable estimation methods

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T & Any = percentileOrNull(percentile, method, skipNaN = skipNaN).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentileOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T? = Aggregators.percentileComparables<T>(percentile, skipNaN, method).aggregateSingleColumn(this)

/** @include [ConfigurableStatisticsDocs.Interpolating] */
public fun <T> DataColumn<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? =
    percentileOrNull(percentile, method, skipNaN = skipNaN).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.Interpolating] */
public fun <T> DataColumn<T>.percentileOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.percentileNumbers<T>(percentile, skipNaN, method).aggregateSingleColumn(this)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = percentileByOrNull(percentile, method, skipNaN, selector).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileByOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.percentileCommon<R>(percentile, skipNaN, method).aggregateByOrNull(this, selector)

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): R & Any = percentileOfOrNull(percentile, method, skipNaN, expression).suggestIfNull("percentileOf")

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): R? = Aggregators.percentileComparables<R>(percentile, skipNaN, method).aggregateOf(this, expression)

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, method, skipNaN, expression).suggestIfNull("percentileOf")

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN, method).aggregateOf(this, expression)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T? = Aggregators.percentileComparables<T>(percentile, skipNaN, method).aggregateOfRow(this) { colsOf<T?>() }

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T = rowPercentileOfOrNull<T>(percentile, method, skipNaN = skipNaN).suggestIfNull("rowPercentileOf")

/** @include [ConfigurableStatisticsDocs.Interpolating] */
public inline fun <reified T> DataRow<*>.rowPercentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.percentileNumbers<T>(percentile, skipNaN, method).aggregateOfRow(this) { colsOf<T?>() }

/** @include [ConfigurableStatisticsDocs.Interpolating] */
public inline fun <reified T> DataRow<*>.rowPercentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number =
    rowPercentileOfOrNull<T>(percentile, method, skipNaN).suggestIfNull("rowPercentileOf")

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> DataFrame<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, method, skipNaN, intraComparableColumns())

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.percentileWithMethod(percentile, skipNaN, method).aggregateFor(this, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> DataFrame<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, method, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = percentileOrNull(percentile, method, skipNaN, columns).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.percentileComparables<C>(percentile, skipNaN, method).aggregateAll(this, columns)

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? =
    percentileOrNull(percentile, method, skipNaN, columns).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.percentileNumbers<C>(percentile, skipNaN, method).aggregateAll(this, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
public fun <T> DataFrame<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any = percentileOrNull(percentile, method, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

/** @include [ConfigurableStatisticsDocs.Common] */
public fun <T> DataFrame<T>.percentileOrNull(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any? =
    Aggregators.percentileCommon<Comparable<Any>?>(percentile, skipNaN, method).aggregateAll(this) {
        columns.toColumnSet()
    }

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): R & Any = percentileOfOrNull(percentile, method, skipNaN, expression).suggestIfNull("percentileOf")

/** @include [ConfigurableStatisticsDocs.Selecting] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.percentileComparables<R>(percentile, skipNaN, method).aggregateOf(this, expression)

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, method, skipNaN, expression).suggestIfNull("percentileOf")

/** @include [ConfigurableStatisticsDocs.Interpolating] */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN, method).aggregateOf(this, expression)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = percentileByOrNull(percentile, method, skipNaN, expression).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T> DataFrame<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, method, column, skipNaN).suggestIfNull("percentileBy")

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN, method).aggregateByOrNull(this, expression)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByWithMethodOrNullImpl(percentile, method, column, skipNaN)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Grouped<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, method, skipNaN, intraComparableColumns())

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentileWithMethod(percentile, skipNaN, method).aggregateFor(this, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Grouped<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, method, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN, method).aggregateAll(this, name, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Grouped<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, method, name, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN, method).aggregateOf(this, name, expression)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, method, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T, G> GroupBy<T, G>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, method, column, skipNaN) }

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Pivot<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, method, separate, skipNaN, intraComparableColumns())

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { percentileFor(percentile, method, separate, skipNaN, columns) }

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Pivot<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, method, separate, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { percentile(percentile, method, skipNaN, columns) }

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> Pivot<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, method, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { percentileOf(percentile, method, skipNaN, expression) }

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, method, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T> Pivot<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, method, column, skipNaN) }

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, method, separate, skipNaN, intraComparableColumns())

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentileWithMethod(percentile, skipNaN, method).aggregateFor(this, separate, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, method, separate, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN, method).aggregateAll(this, columns)

/** @include [ConfigurableStatisticsDocs.Common] */
@Refine
public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, method, skipNaN) { columns.toComparableColumns() }

/** @include [ConfigurableStatisticsDocs.Common] */
@OverloadResolutionByLambdaReturnType
@Refine
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileOf(
    percentile: Double,
    method: QuantileEstimationMethod<*, *>,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN, method).aggregateOf(this, expression)

/** @include [ConfigurableStatisticsDocs.Selecting] */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, method, skipNaN, rowExpression) }

/** @include [ConfigurableStatisticsDocs.Selecting] */
public fun <T> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, method, column, skipNaN) }

// endregion
