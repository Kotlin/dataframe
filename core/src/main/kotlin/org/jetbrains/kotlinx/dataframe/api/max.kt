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
import org.jetbrains.kotlinx.dataframe.util.MAX_NO_SKIPNAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MAX
import org.jetbrains.kotlinx.dataframe.util.ROW_MAX_OR_NULL
import kotlin.reflect.KProperty

// region docs

/**
 * {@comment
 *    The Max Operation KDoc-topic; it also holds all common `max` KDoc-snippets.
 *    Link to it with `{@include [MaxDocsLink]}`.
 * }
 *
 * ## The Max Operation
 *
 * Computes the [maximum](https://en.wikipedia.org/wiki/Maximum_and_minimum) of values.
 *
 * @include [InputValuesSnippet]
 *
 * ### Max Modes
 *
 * Depending on what exactly you want the maximum of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [max][DataFrame.max]`()` — the maximum of each suitable column separately.
 * - [max][DataFrame.max]` { columns }` — a single maximum of all values in all selected columns.
 * - [maxFor][DataFrame.maxFor]` { columns }` — the maximum of each selected column separately.
 * - [maxOf][DataFrame.maxOf]` { expression }` — the maximum of the values that the given expression
 *   returns for each row.
 * - [maxBy][DataFrame.maxBy]` { expression }` — the first row for which the given expression returns
 *   the maximum value.
 *
 * [max][DataFrame.max], [maxOf][DataFrame.maxOf], and [maxBy][DataFrame.maxBy] all have an `-OrNull`
 * counterpart which returns `null` instead of throwing an exception when there's nothing to compare.
 *
 * Mirror operation: [min][DataFrame.min].
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * See all summary statistics: {@include [DocumentationUrls.Statistics]}
 */
internal interface MaxDocs : CommonMinMaxDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMaxOperationArg]}
     */
    typealias MaxSelectingOptions = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMaxForOperationArg]}
     */
    typealias MaxForSelectingOptions = Nothing
}

/** [The Max Operation][MaxDocs] */
@ExcludeFromSources
private typealias MaxDocsLink = Nothing

/** {@set [SelectingColumns.OPERATION] [max][max]} */
@ExcludeFromSources
private typealias SetMaxOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [maxFor][maxFor]} */
@ExcludeFromSources
private typealias SetMaxForOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [maxOrNull][maxOrNull]} */
@ExcludeFromSources
private typealias SetMaxOrNullOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the maximum of the values in this [DataColumn].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * See also:
 * - [maxOrNull][DataColumn.maxOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [maxOf][DataColumn.maxOf] — the maximum of the values a selector returns for each element.
 * - [maxBy][DataColumn.maxBy] — the element for which a selector returns the maximum value.
 * - [min][DataColumn.min] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The largest age in the "age" column
 * df.age.max()
 * // The largest weight in the "weight" column, ignoring `null` values
 * df.weight.max()
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value in this column.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.max(skipNaN: Boolean = skipNaNDefault): T =
    maxOrNull(skipNaN).suggestIfNull("max")

/**
 * Returns the maximum of the values in this [DataColumn], or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * See also:
 * - [max][DataColumn.max] — throws instead of returning `null` for a column with nothing to compare.
 * - [maxOfOrNull][DataColumn.maxOfOrNull] — the maximum of the values a selector returns
 *   for each element.
 * - [maxByOrNull][DataColumn.maxByOrNull] — the element for which a selector returns
 *   the maximum value.
 * - [minOrNull][DataColumn.minOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The largest weight in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.maxOrNull()
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value in this column, or `null` if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.max<T>(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the first element of this [DataColumn] for which the given [selector]
 * returns the maximum value.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][DataColumn.maxOf], which returns the maximum [selector] value itself
 * instead of the element it belongs to.
 *
 * See also:
 * - [maxByOrNull][DataColumn.maxByOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [minBy][DataColumn.minBy] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The longest first name in the "name"/"firstName" column
 * df.name.firstName.maxBy { it.length }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [selector] returns the maximum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = maxByOrNull(skipNaN, selector).suggestIfNull("maxBy")

/**
 * Returns the first element of this [DataColumn] for which the given [selector]
 * returns the maximum value, or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [maxByOrNull] with [maxOfOrNull][DataColumn.maxOfOrNull], which returns the maximum
 * [selector] value itself instead of the element it belongs to.
 *
 * See also:
 * - [maxBy][DataColumn.maxBy] — throws instead of returning `null` for a column with nothing to compare.
 * - [minByOrNull][DataColumn.minByOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The longest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.maxByOrNull { it.length }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [selector] returns the maximum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.max<R>(skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the maximum of the values that the given [selector] returns
 * for each element of this [DataColumn].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [maxOf] with [maxBy][DataColumn.maxBy], which returns the element the maximum
 * [selector] value belongs to instead of that value.
 *
 * See also:
 * - [maxOfOrNull][DataColumn.maxOfOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [minOf][DataColumn.minOf] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The length of the longest first name in the "name"/"firstName" column
 * df.name.firstName.maxOf { it.length }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The maximum of the values [selector] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R & Any = maxOfOrNull(skipNaN, selector).suggestIfNull("maxOf")

/**
 * Returns the maximum of the values that the given [selector] returns
 * for each element of this [DataColumn], or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [maxOfOrNull] with [maxByOrNull][DataColumn.maxByOrNull], which returns the element
 * the maximum [selector] value belongs to instead of that value.
 *
 * See also:
 * - [maxOf][DataColumn.maxOf] — throws instead of returning `null` for a column with nothing to compare.
 * - [minOfOrNull][DataColumn.minOfOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The length of the longest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.maxOfOrNull { it.length }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The maximum of the values [selector] returns,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R? = Aggregators.max<R>(skipNaN).aggregateOf(this, selector)

// endregion

// region DataRow

@Deprecated(ROW_MAX_OR_NULL, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMaxOrNull(): Nothing? = error(ROW_MAX_OR_NULL)

@Deprecated(ROW_MAX, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMax(): Nothing = error(ROW_MAX)

/**
 * Returns the maximum of the values of type [T] in this [DataRow],
 * or `null` if there is nothing to compare.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * See also:
 * - [rowMaxOf][DataRow.rowMaxOf] — throws instead of returning `null` when there's nothing to compare.
 * - [rowMinOfOrNull][DataRow.rowMinOfOrNull] — the mirror operation.
 * - [maxOrNull][DataFrame.maxOrNull] — the maximum of the values in specific columns of a [DataFrame].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all `Int` values in the first row, or `null` if there are none
 * df[0].rowMaxOfOrNull<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value of type [T] in this row, or `null` if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMaxOfOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.max<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the maximum of the values of type [T] in this [DataRow].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * See also:
 * - [rowMaxOfOrNull][DataRow.rowMaxOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compare.
 * - [rowMinOf][DataRow.rowMinOf] — the mirror operation.
 * - [max][DataFrame.max] — the maximum of the values in specific columns of a [DataFrame].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all `Int` values in the first row
 * df[0].rowMaxOf<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value of type [T] in this row.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMaxOf(skipNaN: Boolean = skipNaNDefault): T =
    rowMaxOfOrNull<T>(skipNaN).suggestIfNull("rowMaxOf")

// endregion

// region DataFrame

/**
 * Returns the maximum of the values of each suitable column of this [DataFrame] separately.
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * See also:
 * - [maxFor][DataFrame.maxFor] — the same, but for an explicit selection of columns.
 * - [max][DataFrame.max]` { columns }` — a single maximum of all values in the selected columns.
 * - [min][DataFrame.min] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the largest value of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.max()
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @return A single [DataRow] with the maximum of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Max0")
public fun <T> DataFrame<T>.max(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxFor(skipNaN, intraComparableColumns())

/**
 * Returns the maximum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MaxDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions].
 *
 * See also:
 * - [max][DataFrame.max]`()` — the same, but for all suitable columns at once.
 * - [max][DataFrame.max]` { columns }` — a single maximum of all values in the selected columns.
 * - [minFor][DataFrame.minFor] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the largest "age" and the largest "weight"
 * df.maxFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.maxFor(skipNaN = true) { age into "maxAge" and (weight into "maxWeight") }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns of this [DataFrame]
 *   to compute the maximum of.
 * @return A single [DataRow] with the maximum of each selected column.
 */
@Refine
@Interpretable("Max1")
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.max.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Returns the maximum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions].
 *
 * See also:
 * - [max][DataFrame.max]`()` — the same, but for all suitable columns at once.
 * - [max][DataFrame.max]` { columns }` — a single maximum of all values in the selected columns.
 * - [minFor][DataFrame.minFor] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the largest "age" and the largest "weight"
 * df.maxFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return A single [DataRow] with the maximum of each selected column.
 */
public fun <T> DataFrame<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single maximum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [maxOrNull][DataFrame.maxOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [maxFor][DataFrame.maxFor] — the maximum of each selected column separately.
 * - [maxOf][DataFrame.maxOf] — the maximum of the values a row expression returns for each row.
 * - [min][DataFrame.min] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMaxOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all values in the "age" and "weight" columns
 * df.max { age and weight }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the maximum of.
 * @return The largest value among all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = maxOrNull(skipNaN, columns).suggestIfNull("max")

/**
 * Returns a single maximum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [maxOrNull][DataFrame.maxOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [maxFor][DataFrame.maxFor] — the maximum of each selected column separately.
 * - [maxOf][DataFrame.maxOf] — the maximum of the values a row expression returns for each row.
 * - [min][DataFrame.min] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMaxOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all values in the "age" and "weight" columns
 * df.max("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value among all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T> DataFrame<T>.max(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any> =
    maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = maxOrNull(*columns, skipNaN = skipNaN).suggestIfNull("max")

/**
 * Returns a single maximum of all the values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [max][DataFrame.max] — throws instead of returning `null` when there's nothing to compare.
 * - [maxFor][DataFrame.maxFor] — the maximum of each selected column separately.
 * - [maxOfOrNull][DataFrame.maxOfOrNull] — the maximum of the values a row expression
 *   returns for each row.
 * - [minOrNull][DataFrame.minOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMaxOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.maxOrNull { age and weight }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the maximum of.
 * @return The largest value among all the values in the selected columns,
 *   or `null` if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single maximum of all the values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [max][DataFrame.max] — throws instead of returning `null` when there's nothing to compare.
 * - [maxFor][DataFrame.maxFor] — the maximum of each selected column separately.
 * - [maxOfOrNull][DataFrame.maxOfOrNull] — the maximum of the values a row expression
 *   returns for each row.
 * - [minOrNull][DataFrame.minOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMaxOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The largest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.maxOrNull("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return The largest value among all the values in the selected columns,
 *   or `null` if there are no values to compare.
 */
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Comparable<Any>? =
    maxOrNull(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): C? = maxOrNull(skipNaN) { columns.toColumnSet() }

/**
 * Returns the maximum of the values that the given [expression] returns
 * for each row of this [DataFrame].
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [maxOf] with [maxBy][DataFrame.maxBy], which returns the row the maximum
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [maxOfOrNull][DataFrame.maxOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [max][DataFrame.max] — a single maximum of all values in the selected columns.
 * - [minOf][DataFrame.minOf] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The largest weight-to-age ratio of all rows
 * df.maxOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The maximum of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = maxOfOrNull(skipNaN, expression).suggestIfNull("maxOf")

/**
 * Returns the maximum of the values that the given [expression] returns
 * for each row of this [DataFrame], or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [maxOfOrNull] with [maxByOrNull][DataFrame.maxByOrNull], which returns the row the
 * maximum [expression] value belongs to instead of that value.
 *
 * See also:
 * - [maxOf][DataFrame.maxOf] — throws instead of returning `null` when there's nothing to compare.
 * - [maxOrNull][DataFrame.maxOrNull] — a single maximum of all values in the selected columns.
 * - [minOfOrNull][DataFrame.minOfOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MinMax]}
 *
 * ### Example
 * ```kotlin
 * // The largest weight-to-age ratio of all rows,
 * // or `null` if this dataframe is empty
 * df.maxOfOrNull { (weight ?: 0) / age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The maximum of the values [expression] returns,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.max<C>(skipNaN).aggregateOf(this, expression)

/**
 * Returns the first row of this [DataFrame] for which the given [expression]
 * returns the maximum value.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][DataFrame.maxOf], which returns the maximum [expression] value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [maxByOrNull][DataFrame.maxByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [minBy][DataFrame.minBy] — the mirror operation.
 * - [sortByDesc][DataFrame.sortByDesc] — orders all rows instead of taking just the largest one.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the largest "age"
 * df.maxBy { age }
 * // The row with the largest weight-to-age ratio
 * df.maxBy { (weight ?: 0) / age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The first [DataRow] for which [expression] returns the maximum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = maxByOrNull(skipNaN, expression).suggestIfNull("maxBy")

/**
 * Returns the first row of this [DataFrame] that has the largest value
 * in the column with the given name.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.ThrowsOnEmptySnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][DataFrame.maxOf], which returns the maximum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [maxByOrNull][DataFrame.maxByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [max][DataFrame.max] — returns the largest value itself instead of the row it belongs to.
 * - [minBy][DataFrame.minBy] — the mirror operation.
 * - [sortByDesc][DataFrame.sortByDesc] — orders all rows instead of taking just the largest one.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the largest "age"
 * df.maxBy("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MaxDocs.SkipNaNParam]
 * @return The first [DataRow] with the largest value in the given column.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T> DataFrame<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxByOrNull(column, skipNaN).suggestIfNull("maxBy")

/**
 * Returns the first row of this [DataFrame] for which the given [expression] returns
 * the maximum value, or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [maxByOrNull] with [maxOfOrNull][DataFrame.maxOfOrNull], which returns the maximum
 * [expression] value itself instead of the row it belongs to.
 *
 * See also:
 * - [maxBy][DataFrame.maxBy] — throws instead of returning `null` when there's nothing to compare.
 * - [minByOrNull][DataFrame.minByOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the largest "age", or `null` if this dataframe is empty
 * df.maxByOrNull { age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return The first [DataRow] for which [expression] returns the maximum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the first row of this [DataFrame] that has the largest value in the column with
 * the given name, or `null` if there is nothing to compare.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullOnEmptySnippet]}
 *
 * Don't confuse [maxByOrNull] with [maxOfOrNull][DataFrame.maxOfOrNull], which returns the maximum
 * value a row expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [maxBy][DataFrame.maxBy] — throws instead of returning `null` when there's nothing to compare.
 * - [maxOrNull][DataFrame.maxOrNull] — returns the largest value itself instead of
 *   the row it belongs to.
 * - [minByOrNull][DataFrame.minByOrNull] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // The row with the largest "age", or `null` if this dataframe is empty
 * df.maxByOrNull("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MaxDocs.SkipNaNParam]
 * @return The first [DataRow] with the largest value in the given column,
 *   or `null` if there are no values to compare.
 */
public fun <T> DataFrame<T>.maxByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    maxByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.max<C>(skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = maxByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the maximum of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the maximum for each suitable column.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * See also:
 * - [maxFor][Grouped.maxFor] — the same, but for an explicit selection of columns.
 * - [max][Grouped.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [min][Grouped.min] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest value of each comparable column
 * df.groupBy { city }.max()
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @return A new [DataFrame] with the group keys and the maximum of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMax1")
public fun <T> Grouped<T>.max(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(skipNaN, intraComparableColumns())

/**
 * Aggregates this [GroupBy] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the maximum for each selected column.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MaxDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions].
 *
 * See also:
 * - [max][Grouped.max]`()` — the same, but for all suitable columns at once.
 * - [max][Grouped.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][Grouped.minFor] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest "age" and the largest "weight"
 * df.groupBy { city }.maxFor { age and weight }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the maximum of.
 * @return A new [DataFrame] with the group keys and the maximum of each selected column per group.
 */
@Refine
@Interpretable("GroupByMax0")
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.max.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Aggregates this [GroupBy] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the maximum for each selected column.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions].
 *
 * See also:
 * - [max][Grouped.max]`()` — the same, but for all suitable columns at once.
 * - [max][Grouped.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][Grouped.minFor] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest "age" and the largest "weight"
 * df.groupBy { city }.maxFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return A new [DataFrame] with the group keys and the maximum of each selected column per group.
 */
public fun <T> Grouped<T>.maxFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the maximum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"max"` otherwise.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions].
 *
 * See also:
 * - [maxFor][Grouped.maxFor] — the maximum of each selected column separately, per group.
 * - [maxOf][Grouped.maxOf] — the maximum of the values a row expression returns
 *   for each row of a group.
 * - [min][Grouped.min] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest of all values in the "age" and "weight" columns,
 * // in a column called "maxValue"
 * df.groupBy { city }.max("maxValue") { age and weight }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"max"` otherwise.
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the maximum of.
 * @return A new [DataFrame] with the group keys and a single maximum per group.
 */
@Refine
@Interpretable("GroupByMax2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateAll(this, name, columns)

/**
 * Aggregates this [GroupBy] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the maximum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"max"` otherwise.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions].
 *
 * See also:
 * - [maxFor][Grouped.maxFor] — the maximum of each selected column separately, per group.
 * - [maxOf][Grouped.maxOf] — the maximum of the values a row expression returns
 *   for each row of a group.
 * - [min][Grouped.min] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest of all values in the "age" and "weight" columns,
 * // in a column called "maxValue"
 * df.groupBy { city }.max("age", "weight", name = "maxValue")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"max"` otherwise.
 * @include [MaxDocs.SkipNaNParam]
 * @return A new [DataFrame] with the group keys and a single maximum per group.
 */
public fun <T> Grouped<T>.max(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the maximum of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the maximum per group, named [name] (or `"max"` if [name] is `null`).
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [maxOf] with [maxBy][GroupBy.maxBy], which returns the row of each group for which
 * the expression returns the maximum value, instead of that value.
 *
 * See also:
 * - [max][Grouped.max] — a single maximum of all values in the selected columns, per group.
 * - [minOf][Grouped.minOf] — the mirror operation.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest weight-to-age ratio, in a column called "maxRatio"
 * df.groupBy { city }.maxOf("maxRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column. If `null` (the default), `"max"` is used.
 * @include [MaxDocs.SkipNaNParam]
 * @param [expression] The [RowExpression] to compute the value to compare for each row.
 * @return A new [DataFrame] with the group keys and a single maximum per group.
 */
@Refine
@Interpretable("GroupByMaxOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.maxOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.max<C>(skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [GroupBy] to the first row for which the given [rowExpression]
 * returns the maximum value.
 *
 * {@include [MaxDocs.ReducedGroupBySnippet]}
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [maxBy] with [maxOf][Grouped.maxOf], which returns the maximum value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [minBy][GroupBy.minBy] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the largest "age"
 * df.groupBy { city }.maxBy { age }.concat()
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare for each row.
 * @return A [ReducedGroupBy] with, for each group, the first row
 *   for which [rowExpression] returns the maximum value.
 */
@Interpretable("GroupByReduceExpression")
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { maxByOrNull(column, skipNaN) }

/**
 * Reduces each group of this [GroupBy] to the first row that has the largest value
 * in the column with the given name.
 *
 * {@include [MaxDocs.ReducedGroupBySnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [maxBy] with [maxOf][Grouped.maxOf], which returns the maximum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [max][Grouped.max] — the maximum value itself instead of the row it belongs to.
 * - [minBy][GroupBy.minBy] — the mirror operation.
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the largest "age"
 * df.groupBy { city }.maxBy("age").concat()
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MaxDocs.SkipNaNParam]
 * @return A [ReducedGroupBy] with, for each group, the first row
 *   that has the largest value in the given column.
 */
public fun <T, G> GroupBy<T, G>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the maximum of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the maximum
 * of each suitable column of the corresponding group.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [maxFor][Pivot.maxFor] — the same, but for an explicit selection of columns.
 * - [max][Pivot.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [min][Pivot.min] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest value of each comparable column
 * df.pivot { city }.max()
 * ```
 *
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @return A single [DataRow] with the maximum of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    delegate { max(separate, skipNaN) }

/**
 * Aggregates this [Pivot] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the maximum
 * of each selected column of the corresponding group.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MaxDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]`()` — the same, but for all suitable columns at once.
 * - [max][Pivot.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][Pivot.minFor] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest "age" and the largest "weight"
 * df.pivot { city }.maxFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.maxFor(separate = true) { age and weight }
 * ```
 *
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the maximum of.
 * @return A single [DataRow] with the maximum of each selected column per [pivot] group.
 */
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { maxFor(separate, skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the maximum
 * of each selected column of the corresponding group.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]`()` — the same, but for all suitable columns at once.
 * - [max][Pivot.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][Pivot.minFor] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest "age" and the largest "weight"
 * df.pivot { city }.maxFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @return A single [DataRow] with the maximum of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the largest
 * value among all the values in the selected columns of the corresponding group.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]`()` — the maximum of each suitable column separately, per group.
 * - [maxFor][Pivot.maxFor] — the maximum of each selected column separately, per group.
 * - [min][Pivot.min] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest of all values in the "age" and "weight" columns
 * df.pivot { city }.max { age and weight }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the maximum of.
 * @return A single [DataRow] with, per [pivot] group, the largest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { max(skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the largest
 * value among all the values in the selected columns of the corresponding group.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions], or check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]`()` — the maximum of each suitable column separately, per group.
 * - [maxFor][Pivot.maxFor] — the maximum of each selected column separately, per group.
 * - [min][Pivot.min] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest of all values in the "age" and "weight" columns
 * df.pivot { city }.max("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return A single [DataRow] with, per [pivot] group, the largest value among all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.max(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    max(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = max(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing the maximum of the values that the given [rowExpression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the maximum
 * of the expression's results for the rows of the corresponding group.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [maxOf] with [maxBy][Pivot.maxBy], which returns the first row of each group for
 * which the expression returns the maximum value, instead of that value.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minOf][Pivot.minOf] — the mirror operation.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the largest weight-to-age ratio
 * df.pivot { city }.maxOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A single [DataRow] with, per [pivot] group, the maximum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { maxOf(skipNaN, rowExpression) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the first [row][DataRow]
 * for which the given [rowExpression] returns the maximum value.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MaxDocs.ReducedPivotSnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][Pivot.maxOf], which returns the maximum value the expression
 * returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [minBy][Pivot.minBy] — the mirror operation.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the largest weight-to-age ratio
 * df.pivot { city }.maxBy { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [ReducedPivot] holding, per group,
 *   the first row where the [rowExpression] produced the maximum result.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { maxByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the first [row][DataRow]
 * that has the largest value in the given [column].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MaxDocs.ReducedPivotSnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][Pivot.maxOf], which returns the maximum value a row expression
 * returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [max][Pivot.max]` { columns }` — the maximum value itself, instead of the row.
 * - [minBy][Pivot.minBy] — the mirror operation.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the oldest person
 * df.pivot { city }.maxBy("age").with { name }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MaxDocs.SkipNaNParam]
 * @return A [ReducedPivot] holding, per group, the first row with the largest value
 *   in the given column.
 */
public fun <T> Pivot<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the maximum of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the maximum of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [maxFor][PivotGroupBy.maxFor] — the same, but for an explicit selection of columns.
 * - [max][PivotGroupBy.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [min][PivotGroupBy.min] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest value of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.max()
 * ```
 *
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @return A [DataFrame] with the maximum of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.max(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    maxFor(separate, skipNaN, intraComparableColumns())

/**
 * Aggregates this [PivotGroupBy] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the maximum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [MaxDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]`()` — the same, but for all suitable columns at once.
 * - [max][PivotGroupBy.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][PivotGroupBy.minFor] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest "age" and the largest "weight"
 * df.pivot { city }.groupBy { name.lastName }.maxFor { age and weight }
 * ```
 *
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns
 *   to compute the maximum of.
 * @return A [DataFrame] with the maximum of each selected column per group.
 */
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.max.invoke(skipNaN).aggregateFor(this, separate, columns)

/**
 * Aggregates this [PivotGroupBy] by computing the maximum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the maximum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]`()` — the same, but for all suitable columns at once.
 * - [max][PivotGroupBy.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minFor][PivotGroupBy.minFor] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest "age" and the largest "weight"
 * df.pivot { city }.groupBy { name.lastName }.maxFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @include [MaxDocs.SeparateParam]
 * @include [MaxDocs.SkipNaNParam]
 * @return A [DataFrame] with the maximum of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.maxFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = maxFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the largest value among all the values in the
 * selected columns of the group corresponding to that [pivot] key (column)
 * and [groupBy] key (row).
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]`()` — the maximum of each suitable column separately, per group.
 * - [maxFor][PivotGroupBy.maxFor] — the maximum of each selected column separately, per group.
 * - [min][PivotGroupBy.min] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.max { age and weight }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the maximum of.
 * @return A [DataFrame] with, per group, the largest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.max<R>(skipNaN).aggregateAll(this, columns)

/**
 * Aggregates this [PivotGroupBy] by computing a single maximum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the largest value among all the values in the
 * selected columns of the group corresponding to that [pivot] key (column)
 * and [groupBy] key (row).
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][MaxDocs.MaxSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]`()` — the maximum of each suitable column separately, per group.
 * - [maxFor][PivotGroupBy.maxFor] — the maximum of each selected column separately, per group.
 * - [min][PivotGroupBy.min] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.max("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the maximum of.
 * @include [MaxDocs.SkipNaNParam]
 * @return A [DataFrame] with, per group, the largest value among all the values
 *   in the selected columns.
 */
public fun <T> PivotGroupBy<T>.max(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    max(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = max(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the maximum of the values that the given
 * [rowExpression] returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the maximum of the expression's results for the
 * rows of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * {@include [MaxDocs.NullCellOnEmptySnippet]}
 *
 * Don't confuse [maxOf] with [maxBy][PivotGroupBy.maxBy], which returns the first row of each group
 * for which the expression returns the maximum value, instead of that value.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]` { columns }` — a single maximum of all values in the selected columns,
 *   per group.
 * - [minOf][PivotGroupBy.minOf] — the mirror operation.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the largest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.maxOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [DataFrame] with, per group, the maximum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { maxOf(skipNaN, rowExpression) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the first [row][DataRow] for which the given [rowExpression] returns the maximum value.
 *
 * {@include [MaxDocs.RowExpressionSnippet]}
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MaxDocs.ReducedPivotGroupBySnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][PivotGroupBy.maxOf], which returns the maximum value the
 * expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [minBy][PivotGroupBy.minBy] — the mirror operation.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the largest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.maxBy { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @include [MaxDocs.SkipNaNParam]
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [ReducedPivotGroupBy] holding, per group,
 *   the first row where the [rowExpression] produced the maximum result.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { maxByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the first [row][DataRow] that has the largest value in the given [column].
 *
 * {@include [MaxDocs.InputValuesSnippet]}
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * {@include [MaxDocs.ReducedPivotGroupBySnippet]}
 *
 * Don't confuse [maxBy] with [maxOf][PivotGroupBy.maxOf], which returns the maximum value a row
 * expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [max][PivotGroupBy.max]` { columns }` — the maximum value itself, instead of the row.
 * - [minBy][PivotGroupBy.minBy] — the mirror operation.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MaxDocsLink]} — an overview of all `max` modes.
 *
 * For more information: {@include [DocumentationUrls.MaxBy]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the oldest person
 * df.pivot { city }.groupBy { name.lastName }.maxBy("age").with { name.firstName }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MaxDocs.SkipNaNParam]
 * @return A [ReducedPivotGroupBy] holding, per group, the first row with the largest value
 *   in the given column.
 */
public fun <T> PivotGroupBy<T>.maxBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    maxBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = maxBy(column.toColumnAccessor(), skipNaN)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.max(): T = max(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(): T? = maxOrNull(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxBy(noinline selector: (T) -> R): T & Any =
    maxBy(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxByOrNull(noinline selector: (T) -> R): T? =
    maxByOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOf(crossinline selector: (T) -> R): R & Any =
    maxOf(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.maxOfOrNull(crossinline selector: (T) -> R): R? =
    maxOfOrNull(skipNaN = skipNaNDefault, selector = selector)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMaxOfOrNull(): T? =
    rowMaxOfOrNull<T>(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Comparable<T & Any>?> DataRow<*>.rowMaxOf(): T & Any = rowMaxOf(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.max(): DataRow<T> = max(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    maxFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxFor(vararg columns: String): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(vararg columns: ColumnReference<C>): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> DataFrame<T>.maxFor(vararg columns: KProperty<C>): DataRow<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(columns: ColumnsSelector<T, C>): C & Any =
    max(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.max(vararg columns: String): Comparable<Any> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(vararg columns: ColumnReference<C>): C & Any =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.max(vararg columns: KProperty<C>): C & Any =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(columns: ColumnsSelector<T, C>): C? =
    maxOrNull(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxOrNull(vararg columns: String): Comparable<Any>? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(vararg columns: ColumnReference<C>): C? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.maxOrNull(vararg columns: KProperty<C>): C? =
    maxOrNull(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOf(
    crossinline expression: RowExpression<T, C>,
): C & Any = maxOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxOfOrNull(
    crossinline expression: RowExpression<T, C>,
): C? = maxOfOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = maxBy(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxBy(column: String): DataRow<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(column: ColumnReference<C>): DataRow<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxBy(column: KProperty<C>): DataRow<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = maxByOrNull(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.maxByOrNull(column: String): DataRow<T>? = maxByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(
    column: ColumnReference<C>,
): DataRow<T>? = maxByOrNull(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.maxByOrNull(column: KProperty<C>): DataRow<T>? =
    maxByOrNull(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.max(): DataFrame<T> = max(skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    maxFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.maxFor(vararg columns: String): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<*>?> Grouped<T>.maxFor(vararg columns: KProperty<C>): DataFrame<T> =
    maxFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    name: String? = null,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = max(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.max(vararg columns: String, name: String? = null): DataFrame<T> =
    max(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: ColumnReference<C>,
    name: String? = null,
): DataFrame<T> = max(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Comparable<C & Any>?> Grouped<T>.max(
    vararg columns: KProperty<C>,
    name: String? = null,
): DataFrame<T> = max(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.maxOf(
    name: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = maxOf(name, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.maxBy(
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: ColumnReference<C>,
): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<T, G>.maxBy(column: String): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.maxBy(
    column: KProperty<C>,
): ReducedGroupBy<T, G> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.max(separate: Boolean = false): DataRow<T> = max(separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = maxFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.maxFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataRow<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> Pivot<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataRow<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(columns: ColumnsSelector<T, R>): DataRow<T> =
    max(skipNaN = skipNaNDefault, columns = columns)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(vararg columns: ColumnReference<R>): DataRow<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> Pivot<T>.max(vararg columns: KProperty<R>): DataRow<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = maxOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.maxBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(column: ColumnReference<C>): ReducedPivot<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.maxBy(column: String): ReducedPivot<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.maxBy(column: KProperty<C>): ReducedPivot<T> =
    maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.max(separate: Boolean = false): DataFrame<T> = max(separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = maxFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.maxFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    vararg columns: ColumnReference<R>,
    separate: Boolean = false,
): DataFrame<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<*>?> PivotGroupBy<T>.maxFor(
    vararg columns: KProperty<R>,
    separate: Boolean = false,
): DataFrame<T> = maxFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(columns: ColumnsSelector<T, R>): DataFrame<T> =
    max(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.max(vararg columns: String): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(vararg columns: ColumnReference<R>): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.max(vararg columns: KProperty<R>): DataFrame<T> =
    max(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxOf(
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = maxOf(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.maxBy(
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = maxBy(skipNaN = skipNaNDefault, rowExpression = rowExpression)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: ColumnReference<C>,
): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.maxBy(column: String): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MAX_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.maxBy(
    column: KProperty<C>,
): ReducedPivotGroupBy<T> = maxBy(column, skipNaN = skipNaNDefault)

// endregion
