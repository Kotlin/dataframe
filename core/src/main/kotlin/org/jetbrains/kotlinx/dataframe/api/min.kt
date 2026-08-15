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
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.CommonMinMaxDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonMinMaxDocs.InputValuesSnippet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
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
import org.jetbrains.kotlinx.dataframe.util.MIN_NO_SKIPNAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MIN
import org.jetbrains.kotlinx.dataframe.util.ROW_MIN_OR_NULL
import kotlin.reflect.KProperty

// region docs

/**
 * {@comment
 *    The Min Operation KDoc-topic; it also holds all common `min` KDoc-snippets.
 *    Link to it with `{@include [MinDocsLink]}`.
 * }
 *
 * ## The Min Operation
 *
 * Computes the [minimum](https://en.wikipedia.org/wiki/Maximum_and_minimum) of values.
 *
 * @include [InputValuesSnippet]
 *
 * ### Min Modes
 *
 * Depending on what exactly you want the minimum of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [min][DataFrame.min]`()` — the minimum of each suitable column separately.
 * - [min][DataFrame.min]` { columns }` — a single minimum of all values in all selected columns.
 * - [minFor][DataFrame.minFor]` { columns }` — the minimum of each selected column separately.
 * - [minOf][DataFrame.minOf]` { expression }` — the minimum of the values that the given expression
 *   returns for each row.
 * - [minBy][DataFrame.minBy]` { expression }` — the first row for which the given expression returns
 *   the minimum value.
 *
 * [min][DataFrame.min], [minOf][DataFrame.minOf], and [minBy][DataFrame.minBy] all have an `-OrNull`
 * counterpart which returns `null` instead of throwing an exception when there's nothing to compare.
 *
 * Mirror operation: [max][DataFrame.max].
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * See all summary statistics: {@include [DocumentationUrls.Statistics]}
 */
internal interface MinDocs : CommonMinMaxDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMinOperationArg]}
     */
    typealias MinSelectingOptions = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMinForOperationArg]}
     */
    typealias MinForSelectingOptions = Nothing
}

/** [The Min Operation][MinDocs] */
@ExcludeFromSources
private typealias MinDocsLink = Nothing

/** {@set [SelectingColumns.OPERATION] [min][min]} */
@ExcludeFromSources
private typealias SetMinOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [minFor][minFor]} */
@ExcludeFromSources
private typealias SetMinForOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [minOrNull][minOrNull]} */
@ExcludeFromSources
private typealias SetMinOrNullOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the minimum of the values in this [DataColumn].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * See also:
 * - [minOrNull][DataColumn.minOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [minOf][DataColumn.minOf] — the minimum of the values a selector returns for each element.
 * - [minBy][DataColumn.minBy] — the element for which a selector returns the minimum value.
 * - [max][DataColumn.max] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The smallest age in the "age" column
 * df.age.min()
 * // The smallest weight in the "weight" column, ignoring `null` values
 * df.weight.min()
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value in this column.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.min(skipNaN: Boolean = skipNaNDefault): T =
    minOrNull(skipNaN).suggestIfNull("min")

/**
 * Returns the minimum of the values in this [DataColumn], or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * See also:
 * - [min][DataColumn.min] — throws instead of returning `null` for a column with nothing to compare.
 * - [minOfOrNull][DataColumn.minOfOrNull] — the minimum of the values a selector returns
 *   for each element.
 * - [minByOrNull][DataColumn.minByOrNull] — the element for which a selector returns
 *   the minimum value.
 * - [maxOrNull][DataColumn.maxOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The smallest weight in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.minOrNull()
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value in this column, or `null` if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the first element of this [DataColumn] for which the given [selector]
 * returns the minimum value.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [minBy] with [minOf][DataColumn.minOf], which returns the minimum [selector] value itself
 * instead of the element it belongs to.
 *
 * See also:
 * - [minByOrNull][DataColumn.minByOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [maxBy][DataColumn.maxBy] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The shortest first name in the "name"/"firstName" column
 * df.name.firstName.minBy { it.length }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [selector] returns the minimum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = minByOrNull(skipNaN, selector).suggestIfNull("minBy")

/**
 * Returns the first element of this [DataColumn] for which the given [selector]
 * returns the minimum value, or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [minByOrNull] with [minOfOrNull][DataColumn.minOfOrNull], which returns the minimum
 * [selector] value itself instead of the element it belongs to.
 *
 * See also:
 * - [minBy][DataColumn.minBy] — throws instead of returning `null` for a column with nothing to compare.
 * - [maxByOrNull][DataColumn.maxByOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The shortest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.minByOrNull { it.length }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [selector] returns the minimum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.min<R>(skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the minimum of the values that the given [selector] returns
 * for each element of this [DataColumn].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [minOf] with [minBy][DataColumn.minBy], which returns the element the minimum
 * [selector] value belongs to instead of that value.
 *
 * See also:
 * - [minOfOrNull][DataColumn.minOfOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [maxOf][DataColumn.maxOf] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The length of the shortest first name in the "name"/"firstName" column
 * df.name.firstName.minOf { it.length }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The minimum of the values [selector] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R & Any = minOfOrNull(skipNaN, selector).suggestIfNull("minOf")

/**
 * Returns the minimum of the values that the given [selector] returns
 * for each element of this [DataColumn], or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [minOfOrNull] with [minByOrNull][DataColumn.minByOrNull], which returns the element
 * the minimum [selector] value belongs to instead of that value.
 *
 * See also:
 * - [minOf][DataColumn.minOf] — throws instead of returning `null` for a column with nothing to compare.
 * - [maxOfOrNull][DataColumn.maxOfOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The length of the shortest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.minOfOrNull { it.length }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The minimum of the values [selector] returns,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R? = Aggregators.min<R>(skipNaN).aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated(ROW_MIN_OR_NULL, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMinOrNull(): Nothing? = error(ROW_MIN_OR_NULL)

@Deprecated(ROW_MIN, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMin(): Nothing = error(ROW_MIN)

/**
 * Returns the minimum of the values of type [T] in this [DataRow],
 * or `null` if there is nothing to compare.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * See also:
 * - [rowMinOf][DataRow.rowMinOf] — throws instead of returning `null` when there's nothing to compare.
 * - [rowMaxOfOrNull][DataRow.rowMaxOfOrNull] — the mirror operation.
 * - [minOrNull][DataFrame.minOrNull] — the minimum of the values in specific columns of a [DataFrame].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all `Int` values in the first row, or `null` if there are none
 * df[0].rowMinOfOrNull<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value of type [T] in this row, or `null` if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMinOfOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the minimum of the values of type [T] in this [DataRow].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * See also:
 * - [rowMinOfOrNull][DataRow.rowMinOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compare.
 * - [rowMaxOf][DataRow.rowMaxOf] — the mirror operation.
 * - [min][DataFrame.min] — the minimum of the values in specific columns of a [DataFrame].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all `Int` values in the first row
 * df[0].rowMinOf<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value of type [T] in this row.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMinOf(skipNaN: Boolean = skipNaNDefault): T =
    rowMinOfOrNull<T>(skipNaN).suggestIfNull("rowMinOf")

// endregion

// region DataFrame

/**
 * Returns the minimum of the values of each suitable column of this [DataFrame] separately.
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * See also:
 * - [minFor][DataFrame.minFor] — the same, but for an explicit selection of columns.
 * - [min][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [max][DataFrame.max] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest value of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.min()
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @return A single [DataRow] with the minimum of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Min0")
public fun <T> DataFrame<T>.min(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minFor(skipNaN, intraComparableColumns())

/**
 * Returns the minimum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MinDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [min][DataFrame.min]`()` — the same, but for all suitable columns at once.
 * - [min][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [maxFor][DataFrame.maxFor] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest "age" and the smallest "weight"
 * df.minFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.minFor(skipNaN = true) { age into "minAge" and (weight into "minWeight") }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns of this [DataFrame]
 *   to compute the minimum of.
 * @return A single [DataRow] with the minimum of each selected column.
 */
@Refine
@Interpretable("Min1")
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Returns the minimum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [min][DataFrame.min]`()` — the same, but for all suitable columns at once.
 * - [min][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [maxFor][DataFrame.maxFor] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest "age" and the smallest "weight"
 * df.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return A single [DataRow] with the minimum of each selected column.
 */
public fun <T> DataFrame<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single minimum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [minOrNull][DataFrame.minOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [minFor][DataFrame.minFor] — the minimum of each selected column separately.
 * - [minOf][DataFrame.minOf] — the minimum of the values a row expression returns for each row.
 * - [max][DataFrame.max] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMinOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns
 * df.min { age and weight }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the minimum of.
 * @return The smallest value among all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = minOrNull(skipNaN, columns).suggestIfNull("min")

/**
 * Returns a single minimum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [minOrNull][DataFrame.minOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [minFor][DataFrame.minFor] — the minimum of each selected column separately.
 * - [minOf][DataFrame.minOf] — the minimum of the values a row expression returns for each row.
 * - [max][DataFrame.max] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMinOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns
 * df.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value among all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T> DataFrame<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any> =
    minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = minOrNull(*columns, skipNaN = skipNaN).suggestIfNull("min")

/**
 * Returns a single minimum of all the values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [min][DataFrame.min] — throws instead of returning `null` when there's nothing to compare.
 * - [minFor][DataFrame.minFor] — the minimum of each selected column separately.
 * - [minOfOrNull][DataFrame.minOfOrNull] — the minimum of the values a row expression
 *   returns for each row.
 * - [maxOrNull][DataFrame.maxOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMinOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.minOrNull { age and weight }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the minimum of.
 * @return The smallest value among all the values in the selected columns,
 *   or `null` if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single minimum of all the values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [min][DataFrame.min] — throws instead of returning `null` when there's nothing to compare.
 * - [minFor][DataFrame.minFor] — the minimum of each selected column separately.
 * - [minOfOrNull][DataFrame.minOfOrNull] — the minimum of the values a row expression
 *   returns for each row.
 * - [maxOrNull][DataFrame.maxOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMinOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.minOrNull("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return The smallest value among all the values in the selected columns,
 *   or `null` if there are no values to compare.
 */
public fun <T> DataFrame<T>.minOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any>? =
    minOrNull(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = minOrNull(skipNaN) { columns.toColumnSet() }

/**
 * Returns the minimum of the values that the given [expression] returns
 * for each row of this [DataFrame].
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [minOf] with [minBy][DataFrame.minBy], which returns the row the minimum
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [minOfOrNull][DataFrame.minOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [min][DataFrame.min] — a single minimum of all values in the selected columns.
 * - [maxOf][DataFrame.maxOf] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The smallest weight-to-age ratio of all rows
 * df.minOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The minimum of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = minOfOrNull(skipNaN, expression).suggestIfNull("minOf")

/**
 * Returns the minimum of the values that the given [expression] returns
 * for each row of this [DataFrame], or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [minOfOrNull] with [minByOrNull][DataFrame.minByOrNull], which returns the row the
 * minimum [expression] value belongs to instead of that value.
 *
 * See also:
 * - [minOf][DataFrame.minOf] — throws instead of returning `null` when there's nothing to compare.
 * - [minOrNull][DataFrame.minOrNull] — a single minimum of all values in the selected columns.
 * - [maxOfOrNull][DataFrame.maxOfOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The smallest weight-to-age ratio of all rows,
 * // or `null` if this dataframe is empty
 * df.minOfOrNull { (weight ?: 0) / age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The minimum of the values [expression] returns,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateOf(this, expression)

/**
 * Returns the first row of this [DataFrame] for which the given [expression]
 * returns the minimum value.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [minBy] with [minOf][DataFrame.minOf], which returns the minimum [expression] value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [minByOrNull][DataFrame.minByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [maxBy][DataFrame.maxBy] — the mirror operation.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just the smallest one.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age"
 * df.minBy { age }
 * // The row with the smallest weight-to-age ratio
 * df.minBy { (weight ?: 0) / age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The first [DataRow] for which [expression] returns the minimum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minByOrNull(skipNaN, expression).suggestIfNull("minBy")

/**
 * Returns the first row of this [DataFrame] that has the smallest value
 * in the column with the given name.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [minBy] with [minOf][DataFrame.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [minByOrNull][DataFrame.minByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [min][DataFrame.min] — returns the smallest value itself instead of the row it belongs to.
 * - [maxBy][DataFrame.maxBy] — the mirror operation.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just the smallest one.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age"
 * df.minBy("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MinDocs.SkipNanParam]
 * @return The first [DataRow] with the smallest value in the given column.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T> DataFrame<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minByOrNull(column, skipNaN).suggestIfNull("minBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minByOrNull(column, skipNaN).suggestIfNull("minBy")

/**
 * Returns the first row of this [DataFrame] for which the given [expression] returns
 * the minimum value, or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [minByOrNull] with [minOfOrNull][DataFrame.minOfOrNull], which returns the minimum
 * [expression] value itself instead of the row it belongs to.
 *
 * See also:
 * - [minBy][DataFrame.minBy] — throws instead of returning `null` when there's nothing to compare.
 * - [maxByOrNull][DataFrame.maxByOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age", or `null` if this dataframe is empty
 * df.minByOrNull { age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The first [DataRow] for which [expression] returns the minimum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.min<C>(skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the first row of this [DataFrame] that has the smallest value in the column with
 * the given name, or `null` if there is nothing to compare.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [minByOrNull] with [minOfOrNull][DataFrame.minOfOrNull], which returns the minimum
 * value a row expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [minBy][DataFrame.minBy] — throws instead of returning `null` when there's nothing to compare.
 * - [minOrNull][DataFrame.minOrNull] — returns the smallest value itself instead of
 *   the row it belongs to.
 * - [maxByOrNull][DataFrame.maxByOrNull] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age", or `null` if this dataframe is empty
 * df.minByOrNull("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MinDocs.SkipNanParam]
 * @return The first [DataRow] with the smallest value in the given column,
 *   or `null` if there are no values to compare.
 */
public fun <T> DataFrame<T>.minByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    minByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.min<C>(skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = minByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each suitable column.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * See also:
 * - [minFor][Grouped.minFor] — the same, but for an explicit selection of columns.
 * - [min][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [max][Grouped.max] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest value of each comparable column
 * df.groupBy { city }.min()
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the minimum of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMin1")
public fun <T> Grouped<T>.min(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(skipNaN, intraComparableColumns())

/**
 * Aggregates this [GroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each selected column.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MinDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [min][Grouped.min]`()` — the same, but for all suitable columns at once.
 * - [min][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][Grouped.maxFor] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.groupBy { city }.minFor { age and weight }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the minimum of.
 * @return A new [DataFrame] with the group keys and the minimum of each selected column per group.
 */
@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Aggregates this [GroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each selected column.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [min][Grouped.min]`()` — the same, but for all suitable columns at once.
 * - [min][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][Grouped.maxFor] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.groupBy { city }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the minimum of each selected column per group.
 */
public fun <T> Grouped<T>.minFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"min"` otherwise.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions].
 *
 * See also:
 * - [minFor][Grouped.minFor] — the minimum of each selected column separately, per group.
 * - [minOf][Grouped.minOf] — the minimum of the values a row expression returns
 *   for each row of a group.
 * - [max][Grouped.max] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns,
 * // in a column called "minValue"
 * df.groupBy { city }.min("minValue") { age and weight }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"min"` otherwise.
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A new [DataFrame] with the group keys and a single minimum per group.
 */
@Refine
@Interpretable("GroupByMin2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateAll(this, name, columns)

/**
 * Aggregates this [GroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"min"` otherwise.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions].
 *
 * See also:
 * - [minFor][Grouped.minFor] — the minimum of each selected column separately, per group.
 * - [minOf][Grouped.minOf] — the minimum of the values a row expression returns
 *   for each row of a group.
 * - [max][Grouped.max] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns,
 * // in a column called "minValue"
 * df.groupBy { city }.min("age", "weight", name = "minValue")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"min"` otherwise.
 * @include [MinDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and a single minimum per group.
 */
public fun <T> Grouped<T>.min(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the minimum of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group, named [name] (or `"min"` if [name] is `null`).
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [minOf] with [minBy][GroupBy.minBy], which returns the row of each group for which
 * the expression returns the minimum value, instead of that value.
 *
 * See also:
 * - [min][Grouped.min] — a single minimum of all values in the selected columns, per group.
 * - [maxOf][Grouped.maxOf] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest weight-to-age ratio, in a column called "minRatio"
 * df.groupBy { city }.minOf("minRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column. If `null` (the default), `"min"` is used.
 * @include [MinDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return A new [DataFrame] with the group keys and a single minimum per group.
 */
@Refine
@Interpretable("GroupByMinOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.minOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [GroupBy] to the first row for which the given [rowExpression]
 * returns the minimum value.
 *
 * {@include [MinDocs.ReducedGroupBySnippet]}
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [minBy] with [minOf][Grouped.minOf], which returns the minimum value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [maxBy][GroupBy.maxBy] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the smallest "age"
 * df.groupBy { city }.minBy { age }.concat()
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare for each row.
 * @return A [ReducedGroupBy] with, for each group, the first row
 *   for which [rowExpression] returns the minimum value.
 */
@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { minByOrNull(column, skipNaN) }

/**
 * Reduces each group of this [GroupBy] to the first row that has the smallest value
 * in the column with the given name.
 *
 * {@include [MinDocs.ReducedGroupBySnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [minBy] with [minOf][Grouped.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [min][Grouped.min] — the minimum value itself instead of the row it belongs to.
 * - [maxBy][GroupBy.maxBy] — the mirror operation.
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the smallest "age"
 * df.groupBy { city }.minBy("age").concat()
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MinDocs.SkipNanParam]
 * @return A [ReducedGroupBy] with, for each group, the first row
 *   that has the smallest value in the given column.
 */
public fun <T, G> GroupBy<T, G>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the minimum
 * of each suitable column of the corresponding group.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [minFor][Pivot.minFor] — the same, but for an explicit selection of columns.
 * - [min][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [max][Pivot.max] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest value of each comparable column
 * df.pivot { city }.min()
 * ```
 *
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @return A single [DataRow] with the minimum of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    delegate { min(separate, skipNaN) }

/**
 * Aggregates this [Pivot] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the minimum
 * of each selected column of the corresponding group.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MinDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]`()` — the same, but for all suitable columns at once.
 * - [min][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][Pivot.maxFor] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.pivot { city }.minFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.minFor(separate = true) { age and weight }
 * ```
 *
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the minimum of.
 * @return A single [DataRow] with the minimum of each selected column per [pivot] group.
 */
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { minFor(separate, skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the minimum
 * of each selected column of the corresponding group.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]`()` — the same, but for all suitable columns at once.
 * - [min][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][Pivot.maxFor] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.pivot { city }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @return A single [DataRow] with the minimum of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the smallest
 * value among all the values in the selected columns of the corresponding group.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]`()` — the minimum of each suitable column separately, per group.
 * - [minFor][Pivot.minFor] — the minimum of each selected column separately, per group.
 * - [max][Pivot.max] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.min { age and weight }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A single [DataRow] with, per [pivot] group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { min(skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the smallest
 * value among all the values in the selected columns of the corresponding group.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]`()` — the minimum of each suitable column separately, per group.
 * - [minFor][Pivot.minFor] — the minimum of each selected column separately, per group.
 * - [max][Pivot.max] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return A single [DataRow] with, per [pivot] group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    min(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = min(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing the minimum of the values that the given [rowExpression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the minimum
 * of the expression's results for the rows of the corresponding group.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [minOf] with [minBy][Pivot.minBy], which returns the first row of each group for
 * which the expression returns the minimum value, instead of that value.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxOf][Pivot.maxOf] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest weight-to-age ratio
 * df.pivot { city }.minOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A single [DataRow] with, per [pivot] group, the minimum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { minOf(skipNaN, rowExpression) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the first [row][DataRow]
 * for which the given [rowExpression] returns the minimum value.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MinDocs.ReducedPivotSnippet]}
 *
 * Don't confuse [minBy] with [minOf][Pivot.minOf], which returns the minimum value the expression
 * returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [maxBy][Pivot.maxBy] — the mirror operation.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the smallest weight-to-age ratio
 * df.pivot { city }.minBy { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [ReducedPivot] holding, per group,
 *   the first row where the [rowExpression] produced the minimum result.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { minByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the first [row][DataRow]
 * that has the smallest value in the given [column].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MinDocs.ReducedPivotSnippet]}
 *
 * Don't confuse [minBy] with [minOf][Pivot.minOf], which returns the minimum value a row expression
 * returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [min][Pivot.min]` { columns }` — the minimum value itself, instead of the row.
 * - [maxBy][Pivot.maxBy] — the mirror operation.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the youngest person
 * df.pivot { city }.minBy("age").with { name }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MinDocs.SkipNanParam]
 * @return A [ReducedPivot] holding, per group, the first row with the smallest value
 *   in the given column.
 */
public fun <T> Pivot<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the minimum of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [minFor][PivotGroupBy.minFor] — the same, but for an explicit selection of columns.
 * - [min][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [max][PivotGroupBy.max] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest value of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.min()
 * ```
 *
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @return A [DataFrame] with the minimum of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(separate, skipNaN, intraComparableColumns())

/**
 * Aggregates this [PivotGroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the minimum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MinDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]`()` — the same, but for all suitable columns at once.
 * - [min][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][PivotGroupBy.maxFor] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest "age" and the smallest "weight"
 * df.pivot { city }.groupBy { name.lastName }.minFor { age and weight }
 * ```
 *
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the minimum of.
 * @return A [DataFrame] with the minimum of each selected column per group.
 */
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, separate, columns)

/**
 * Aggregates this [PivotGroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the minimum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]`()` — the same, but for all suitable columns at once.
 * - [min][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxFor][PivotGroupBy.maxFor] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest "age" and the smallest "weight"
 * df.pivot { city }.groupBy { name.lastName }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @include [MinDocs.SeparateParam]
 * @include [MinDocs.SkipNanParam]
 * @return A [DataFrame] with the minimum of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.minFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = minFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the smallest value among all the values in the
 * selected columns of the group corresponding to that [pivot] key (column)
 * and [groupBy] key (row).
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]`()` — the minimum of each suitable column separately, per group.
 * - [minFor][PivotGroupBy.minFor] — the minimum of each selected column separately, per group.
 * - [max][PivotGroupBy.max] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.min { age and weight }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A [DataFrame] with, per group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.min<R>(skipNaN).aggregateAll(this, columns)

/**
 * Aggregates this [PivotGroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the smallest value among all the values in the
 * selected columns of the group corresponding to that [pivot] key (column)
 * and [groupBy] key (row).
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MinDocs.MinSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]`()` — the minimum of each suitable column separately, per group.
 * - [minFor][PivotGroupBy.minFor] — the minimum of each selected column separately, per group.
 * - [max][PivotGroupBy.max] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 * @include [MinDocs.SkipNanParam]
 * @return A [DataFrame] with, per group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T> PivotGroupBy<T>.min(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    min(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = min(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the minimum of the values that the given
 * [rowExpression] returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the minimum of the expression's results for the
 * rows of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * {@include [MinDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [minOf] with [minBy][PivotGroupBy.minBy], which returns the first row of each group
 * for which the expression returns the minimum value, instead of that value.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [maxOf][PivotGroupBy.maxOf] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.minOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [DataFrame] with, per group, the minimum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { minOfOrNull(skipNaN, rowExpression) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the first [row][DataRow] for which the given [rowExpression] returns the minimum value.
 *
 * {@include [MinDocs.RowExpressionSnippet]}
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MinDocs.ReducedPivotGroupBySnippet]}
 *
 * Don't confuse [minBy] with [minOf][PivotGroupBy.minOf], which returns the minimum value the
 * expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [maxBy][PivotGroupBy.maxBy] — the mirror operation.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the smallest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.minBy { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @include [MinDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [ReducedPivotGroupBy] holding, per group,
 *   the first row where the [rowExpression] produced the minumum result.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { minByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the first [row][DataRow] that has the smallest value in the given [column].
 *
 * {@include [MinDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MinDocs.ReducedPivotGroupBySnippet]}
 *
 * Don't confuse [minBy] with [minOf][PivotGroupBy.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [min][PivotGroupBy.min]` { columns }` — the minimum value itself, instead of the row.
 * - [maxBy][PivotGroupBy.maxBy] — the mirror operation.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MinDocsLink]} — an overview of all `min` modes.
 *
 * For more information: {@include [DocumentationUrls.MinBy]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the youngest person
 * df.pivot { city }.groupBy { name.lastName }.minBy("age").with { name.firstName }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MinDocs.SkipNanParam]
 * @return A [ReducedPivotGroupBy] holding, per group, the first row with the smallest value
 *   in the given column.
 */
public fun <T> PivotGroupBy<T>.minBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    minBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = minBy(column.toColumnAccessor(), skipNaN)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.min(): T = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(): T? = minOrNull(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minBy(noinline selector: (T) -> R): T & Any =
    minBy(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minByOrNull(noinline selector: (T) -> R): T? =
    minByOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOf(crossinline selector: (T) -> R): R & Any =
    minOf(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOfOrNull(crossinline selector: (T) -> R): R? =
    minOfOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMinOfOrNull(): T? =
    rowMinOfOrNull<T>(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T & Any>?> DataRow<*>.rowMinOf(): T & Any = rowMinOf(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.min(): DataRow<T> = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    minFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minFor(vararg columns: String): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(vararg columns: ColumnReference<C>): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(vararg columns: KProperty<C>): DataRow<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(columns: ColumnsSelector<T, C>): C & Any =
    min(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.min(vararg columns: String): Comparable<Any> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(vararg columns: ColumnReference<C>): C & Any =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(vararg columns: KProperty<C>): C & Any =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(columns: ColumnsSelector<T, C>): C? =
    minOrNull(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minOrNull(vararg columns: String): Comparable<Any>? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(vararg columns: ColumnReference<C>): C? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(vararg columns: KProperty<C>): C? =
    minOrNull(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOf(
    crossinline expression: RowExpression<T, C>,
): C & Any = minOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOfOrNull(
    crossinline expression: RowExpression<T, C>,
): C? = minOfOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minBy(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minBy(column: String): DataRow<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(column: ColumnReference<C>): DataRow<T> =
    minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(column: KProperty<C>): DataRow<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = minByOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.minByOrNull(column: String): DataRow<T>? = minByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    column: ColumnReference<C>,
): DataRow<T>? = minByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(column: KProperty<C>): DataRow<T>? =
    minByOrNull(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.min(): DataFrame<T> = min(skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    minFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.minFor(vararg columns: String): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(vararg columns: KProperty<C>): DataFrame<T> =
    minFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = min(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.min(vararg columns: String, name: String? = null): DataFrame<T> =
    min(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: ColumnReference<C>,
    name: String? = null,
): DataFrame<T> = min(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    vararg columns: KProperty<C>,
    name: String? = null,
): DataFrame<T> = min(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.minOf(
    name: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = minOf(name, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.minBy(
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: ColumnReference<C>,
): ReducedGroupBy<T, G> = minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<T, G>.minBy(column: String): ReducedGroupBy<T, G> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.minBy(
    column: KProperty<C>,
): ReducedGroupBy<T, G> = minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.min(separate: Boolean = false): DataRow<T> = min(separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = minFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.minFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataRow<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(columns: ColumnsSelector<T, R>): DataRow<T> =
    min(skipNaN = skipNaNDefault, columns = columns)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(vararg columns: ColumnReference<R>): DataRow<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(vararg columns: KProperty<R>): DataRow<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = minOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(column: ColumnReference<C>): ReducedPivot<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.minBy(column: String): ReducedPivot<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.minBy(column: KProperty<C>): ReducedPivot<T> =
    minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false): DataFrame<T> = min(separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = minFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.minFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.minFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataFrame<T> = minFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(columns: ColumnsSelector<T, R>): DataFrame<T> =
    min(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.min(vararg columns: String): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(vararg columns: ColumnReference<R>): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(vararg columns: KProperty<R>): DataFrame<T> =
    min(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = minOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = minBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: ColumnReference<C>,
): ReducedPivotGroupBy<T> = minBy(column, skipNaN = skipNaNDefault)

@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.minBy(column: String): ReducedPivotGroupBy<T> = minBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MIN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.minBy(
    column: KProperty<C>,
): ReducedPivotGroupBy<T> = minBy(column, skipNaN = skipNaNDefault)

// endregion
