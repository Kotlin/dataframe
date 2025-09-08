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
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

/* TODO KDocs
 * numbers -> Double or null
 * comparable -> itself or null
 *
 * TODO cases where the lambda dictates the return type require explicit type arguments for
 *  non-number, comparable overloads: https://youtrack.jetbrains.com/issue/KT-76683
 *  so, `df.median { intCol }` works, but needs `df.median<_, String> { stringCol }` or `df.median({ dateCol })`
 *  This needs to be explained by KDocs
 *
 * medianBy is new for all overloads :)
 * Uses [QuantileEstimationMethod.R8] for primitive numbers, else [QuantileEstimationMethod.R3].
 * MedianBy also uses [QuantileEstimationMethod.R3].
 */

// region DataColumn

public fun <T : Comparable<T & Any>?> DataColumn<T>.median(): T & Any = medianOrNull().suggestIfNull("median")

public fun <T : Comparable<T & Any>?> DataColumn<T>.medianOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateSingleColumn(this)

public fun <T> DataColumn<T>.median(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? = medianOrNull(skipNaN = skipNaN).suggestIfNull("median")

public fun <T> DataColumn<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.medianNumbers<T>(skipNaN).aggregateSingleColumn(this)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = medianByOrNull(skipNaN, selector).suggestIfNull("medianBy")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.medianCommon<R>(skipNaN).aggregateByOrNull(this, selector)

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOf(
    crossinline expression: (T) -> R,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOfOrNull(
    crossinline expression: (T) -> R,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.medianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.medianNumbers<R>(skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

@Deprecated(ROW_MEDIAN_OR_NULL, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMedianOrNull(): Nothing? = error(ROW_MEDIAN_OR_NULL)

@Deprecated(ROW_MEDIAN, level = DeprecationLevel.ERROR)
public fun AnyRow.rowMedian(): Nothing = error(ROW_MEDIAN)

public inline fun <reified T : Comparable<T>> AnyRow.rowMedianOfOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T : Comparable<T>> AnyRow.rowMedianOf(): T =
    rowMedianOfOrNull<T>().suggestIfNull("rowMedianOf")

public inline fun <reified T> AnyRow.rowMedianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.medianNumbers<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

public inline fun <reified T> AnyRow.rowMedianOf(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number = rowMedianOfOrNull<T>(skipNaN).suggestIfNull("rowMedianOf")

// endregion

// region DataFrame
@Refine
@Interpretable("Median0")
public fun <T> DataFrame<T>.median(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("Median1")
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.medianFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(skipNaN) { columns.toColumnSet() }

// TODO, requires explicit type C due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(columns: ColumnsSelector<T, C>): C & Any =
    medianOrNull(columns).suggestIfNull("median")

// TODO, requires explicit type C due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(columns: ColumnsSelector<T, C>): C? =
    Aggregators.medianComparables<C>().aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? = medianOrNull(skipNaN, columns).suggestIfNull("median")

@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.medianNumbers<C>(skipNaN).aggregateAll(this, columns)

public fun <T> DataFrame<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Any =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

public fun <T> DataFrame<T>.medianOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Any? =
    Aggregators.medianCommon<Comparable<Any>?>(skipNaN).aggregateAll(this) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(vararg columns: ColumnReference<C>): C & Any =
    medianOrNull(*columns).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(vararg columns: ColumnReference<C>): C? =
    medianOrNull<T, C> { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.medianOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? = medianOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(vararg columns: KProperty<C>): C & Any =
    medianOrNull(*columns).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(vararg columns: KProperty<C>): C? =
    medianOrNull<T, C> { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.medianOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? = medianOrNull(skipNaN) { columns.toColumnSet() }

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOf(
    crossinline expression: RowExpression<T, R>,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

// TODO, requires explicit type R due to https://youtrack.jetbrains.com/issue/KT-76683
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOfOrNull(
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.medianNumbers<R>(skipNaN).aggregateOf(this, expression)

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = medianByOrNull(skipNaN, expression).suggestIfNull("medianBy")

public fun <T> DataFrame<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.medianCommon<C>(skipNaN).aggregateByOrNull(this, expression)

public fun <T> DataFrame<T>.medianByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    medianByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.medianCommon<C>(skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = medianByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy
@Refine
@Interpretable("GroupByMedian1")
public fun <T> Grouped<T>.median(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(skipNaN, intraComparableColumns())

@Refine
@Interpretable("GroupByMedian0")
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.medianFor(vararg columns: String): DataFrame<T> = medianFor { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMedian0")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.median(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupByMedianOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.medianOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, name, expression)

@Interpretable("GroupByReduceExpression") // TODO?
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { medianByOrNull(column, skipNaN) }

public fun <T, G> GroupBy<T, G>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = medianBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

public fun <T> Pivot<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { medianFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.medianFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { median(skipNaN, columns) }

public fun <T> Pivot<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    median(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = median(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = median(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { medianOf(skipNaN, expression) }

public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { medianByOrNull(column, skipNaN) }

public fun <T> Pivot<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = medianBy(column.toColumnAccessor(), skipNaN)
// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.medianFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    median(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, expression)

public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { medianByOrNull(column, skipNaN) }

public fun <T> PivotGroupBy<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = medianBy(column.toColumnAccessor(), skipNaN)

// endregion
