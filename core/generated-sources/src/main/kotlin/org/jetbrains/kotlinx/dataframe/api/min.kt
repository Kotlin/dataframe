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
 *
 *
 * ## The Min Operation
 *
 * Computes the [minimum](https://en.wikipedia.org/wiki/Maximum_and_minimum) of values.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * ### Min Modes
 *
 * Depending on what exactly you want the minimum of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>min</code>][DataFrame.min]`()` — the minimum of each suitable column separately.
 * - [<code>min</code>][DataFrame.min]` { columns }` — a single minimum of all values in all selected columns.
 * - [<code>minFor</code>][DataFrame.minFor]` { columns }` — the minimum of each selected column separately.
 * - [<code>minOf</code>][DataFrame.minOf]` { expression }` — the minimum of the values that the given expression
 *   returns for each row.
 * - [<code>minBy</code>][DataFrame.minBy]` { expression }` — the first row for which the given expression returns
 *   the minimum value.
 *
 * [<code>min</code>][DataFrame.min], [<code>minOf</code>][DataFrame.minOf], and [<code>minBy</code>][DataFrame.minBy] all have an `-OrNull`
 * counterpart which returns `null` instead of throwing an exception when there's nothing to compare.
 *
 * Mirror operation: [<code>max</code>][DataFrame.max].
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * See all summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface MinDocs : CommonMinMaxDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`("length", "age")`
     *
     *
     *
     */
    typealias MinSelectingOptions = Nothing

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>minFor</code>][org.jetbrains.kotlinx.dataframe.api.minFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>minFor</code>][org.jetbrains.kotlinx.dataframe.api.minFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>minFor</code>][org.jetbrains.kotlinx.dataframe.api.minFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>minFor</code>][org.jetbrains.kotlinx.dataframe.api.minFor]`("length", "age")`
     *
     *
     *
     */
    typealias MinForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the minimum of the values in this [<code>DataColumn</code>][DataColumn].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * See also:
 * - [<code>minOrNull</code>][DataColumn.minOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [<code>minOf</code>][DataColumn.minOf] — the minimum of the values a selector returns for each element.
 * - [<code>minBy</code>][DataColumn.minBy] — the element for which a selector returns the minimum value.
 * - [<code>max</code>][DataColumn.max] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest age in the "age" column
 * df.age.min()
 * // The smallest weight in the "weight" column, ignoring `null` values
 * df.weight.min()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The smallest value in this column.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.min(skipNaN: Boolean = skipNaNDefault): T =
    minOrNull(skipNaN).suggestIfNull("min")

/**
 * Returns the minimum of the values in this [<code>DataColumn</code>][DataColumn], or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * See also:
 * - [<code>min</code>][DataColumn.min] — throws instead of returning `null` for a column with nothing to compare.
 * - [<code>minOfOrNull</code>][DataColumn.minOfOrNull] — the minimum of the values a selector returns
 *   for each element.
 * - [<code>minByOrNull</code>][DataColumn.minByOrNull] — the element for which a selector returns
 *   the minimum value.
 * - [<code>maxOrNull</code>][DataColumn.maxOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest weight in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.minOrNull()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The smallest value in this column, or `null` if there are no values to compare.
 */
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the first element of this [<code>DataColumn</code>][DataColumn] for which the given [<code>selector</code>][selector]
 * returns the minimum value.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][DataColumn.minOf], which returns the minimum [<code>selector</code>][selector] value itself
 * instead of the element it belongs to.
 *
 * See also:
 * - [<code>minByOrNull</code>][DataColumn.minByOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [<code>maxBy</code>][DataColumn.maxBy] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The shortest first name in the "name"/"firstName" column
 * df.name.firstName.minBy { it.length }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [<code>selector</code>][selector] returns the minimum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = minByOrNull(skipNaN, selector).suggestIfNull("minBy")

/**
 * Returns the first element of this [<code>DataColumn</code>][DataColumn] for which the given [<code>selector</code>][selector]
 * returns the minimum value, or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minByOrNull</code>][minByOrNull] with [<code>minOfOrNull</code>][DataColumn.minOfOrNull], which returns the minimum
 * [<code>selector</code>][selector] value itself instead of the element it belongs to.
 *
 * See also:
 * - [<code>minBy</code>][DataColumn.minBy] — throws instead of returning `null` for a column with nothing to compare.
 * - [<code>maxByOrNull</code>][DataColumn.maxByOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The shortest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.minByOrNull { it.length }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The first element for which [<code>selector</code>][selector] returns the minimum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.min<R>(skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the minimum of the values that the given [<code>selector</code>][selector] returns
 * for each element of this [<code>DataColumn</code>][DataColumn].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minOf</code>][minOf] with [<code>minBy</code>][DataColumn.minBy], which returns the element the minimum
 * [<code>selector</code>][selector] value belongs to instead of that value.
 *
 * See also:
 * - [<code>minOfOrNull</code>][DataColumn.minOfOrNull] — returns `null` instead of throwing for a column with nothing to compare.
 * - [<code>maxOf</code>][DataColumn.maxOf] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The length of the shortest first name in the "name"/"firstName" column
 * df.name.firstName.minOf { it.length }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The minimum of the values [<code>selector</code>][selector] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): R & Any = minOfOrNull(skipNaN, selector).suggestIfNull("minOf")

/**
 * Returns the minimum of the values that the given [<code>selector</code>][selector] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minOfOrNull</code>][minOfOrNull] with [<code>minByOrNull</code>][DataColumn.minByOrNull], which returns the element
 * the minimum [<code>selector</code>][selector] value belongs to instead of that value.
 *
 * See also:
 * - [<code>minOf</code>][DataColumn.minOf] — throws instead of returning `null` for a column with nothing to compare.
 * - [<code>maxOfOrNull</code>][DataColumn.maxOfOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The length of the shortest first name in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.minOfOrNull { it.length }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The minimum of the values [<code>selector</code>][selector] returns,
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
 * Returns the minimum of the values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow],
 * or `null` if there is nothing to compare.
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * See also:
 * - [<code>rowMinOf</code>][DataRow.rowMinOf] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>rowMaxOfOrNull</code>][DataRow.rowMaxOfOrNull] — the mirror operation.
 * - [<code>minOrNull</code>][DataFrame.minOrNull] — the minimum of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest of all `Int` values in the first row, or `null` if there are none
 * df[0].rowMinOfOrNull<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The smallest value of type [<code>T</code>][T] in this row, or `null` if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMinOfOrNull(skipNaN: Boolean = skipNaNDefault): T? =
    Aggregators.min<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the minimum of the values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * See also:
 * - [<code>rowMinOfOrNull</code>][DataRow.rowMinOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compare.
 * - [<code>rowMaxOf</code>][DataRow.rowMaxOf] — the mirror operation.
 * - [<code>min</code>][DataFrame.min] — the minimum of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest of all `Int` values in the first row
 * df[0].rowMinOf<Int>()
 * ```
 *
 * @param [T] The type of the values to compare. Only columns of this type are taken into account.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The smallest value of type [<code>T</code>][T] in this row.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMinOf(skipNaN: Boolean = skipNaNDefault): T =
    rowMinOfOrNull<T>(skipNaN).suggestIfNull("rowMinOf")

// endregion

// region DataFrame

/**
 * Returns the minimum of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * See also:
 * - [<code>minFor</code>][DataFrame.minFor] — the same, but for an explicit selection of columns.
 * - [<code>min</code>][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [<code>max</code>][DataFrame.max] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest value of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.min()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the minimum of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Min0")
public fun <T> DataFrame<T>.min(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    minFor(skipNaN, intraComparableColumns())

/**
 * Returns the minimum of the values of each selected column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [<code>min</code>][DataFrame.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [<code>maxFor</code>][DataFrame.maxFor] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest "age" and the smallest "weight"
 * df.minFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.minFor(skipNaN = true) { age into "minAge" and (weight into "minWeight") }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame]
 *   to compute the minimum of.
 * @return A single [<code>DataRow</code>][DataRow] with the minimum of each selected column.
 */
@Refine
@Interpretable("Min1")
public fun <T, C : Comparable<*>?> DataFrame<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Returns the minimum of the values of each selected column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [<code>min</code>][DataFrame.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][DataFrame.min]` { columns }` — a single minimum of all values in the selected columns.
 * - [<code>maxFor</code>][DataFrame.maxFor] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the smallest "age" and the smallest "weight"
 * df.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [<code>DataFrame</code>][DataFrame] to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the minimum of each selected column.
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
 * Returns a single minimum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>minOrNull</code>][DataFrame.minOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [<code>minFor</code>][DataFrame.minFor] — the minimum of each selected column separately.
 * - [<code>minOf</code>][DataFrame.minOf] — the minimum of the values a row expression returns for each row.
 * - [<code>max</code>][DataFrame.max] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns
 * df.min { age and weight }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame]
 *   to compute the minimum of.
 * @return The smallest value among all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = minOrNull(skipNaN, columns).suggestIfNull("min")

/**
 * Returns a single minimum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>minOrNull</code>][DataFrame.minOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [<code>minFor</code>][DataFrame.minFor] — the minimum of each selected column separately.
 * - [<code>minOf</code>][DataFrame.minOf] — the minimum of the values a row expression returns for each row.
 * - [<code>max</code>][DataFrame.max] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>min</code>][org.jetbrains.kotlinx.dataframe.api.min]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns
 * df.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [<code>DataFrame</code>][DataFrame] to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
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
 * Returns a single minimum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>min</code>][DataFrame.min] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>minFor</code>][DataFrame.minFor] — the minimum of each selected column separately.
 * - [<code>minOfOrNull</code>][DataFrame.minOfOrNull] — the minimum of the values a row expression
 *   returns for each row.
 * - [<code>maxOrNull</code>][DataFrame.maxOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>minOrNull</code>][org.jetbrains.kotlinx.dataframe.api.minOrNull]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>minOrNull</code>][org.jetbrains.kotlinx.dataframe.api.minOrNull]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>minOrNull</code>][org.jetbrains.kotlinx.dataframe.api.minOrNull]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.minOrNull { age and weight }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame]
 *   to compute the minimum of.
 * @return The smallest value among all the values in the selected columns,
 *   or `null` if there are no values to compare.
 */
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.minOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single minimum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>min</code>][DataFrame.min] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>minFor</code>][DataFrame.minFor] — the minimum of each selected column separately.
 * - [<code>minOfOrNull</code>][DataFrame.minOfOrNull] — the minimum of the values a row expression
 *   returns for each row.
 * - [<code>maxOrNull</code>][DataFrame.maxOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>minOrNull</code>][org.jetbrains.kotlinx.dataframe.api.minOrNull]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The smallest of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compare
 * df.minOrNull("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [<code>DataFrame</code>][DataFrame] to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
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
 * Returns the minimum of the values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minOf</code>][minOf] with [<code>minBy</code>][DataFrame.minBy], which returns the row the minimum
 * [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>minOfOrNull</code>][DataFrame.minOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [<code>min</code>][DataFrame.min] — a single minimum of all values in the selected columns.
 * - [<code>maxOf</code>][DataFrame.maxOf] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest weight-to-age ratio of all rows
 * df.minOf { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return The minimum of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = minOfOrNull(skipNaN, expression).suggestIfNull("minOf")

/**
 * Returns the minimum of the values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], or `null` if there is nothing to compare.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minOfOrNull</code>][minOfOrNull] with [<code>minByOrNull</code>][DataFrame.minByOrNull], which returns the row the
 * minimum [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>minOf</code>][DataFrame.minOf] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>minOrNull</code>][DataFrame.minOrNull] — a single minimum of all values in the selected columns.
 * - [<code>maxOfOrNull</code>][DataFrame.maxOfOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "min / max" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html)
 *
 * ### Example
 * ```kotlin
 * // The smallest weight-to-age ratio of all rows,
 * // or `null` if this dataframe is empty
 * df.minOfOrNull { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return The minimum of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C? = Aggregators.min<C>(skipNaN).aggregateOf(this, expression)

/**
 * Returns the first row of this [<code>DataFrame</code>][DataFrame] for which the given [<code>expression</code>][expression]
 * returns the minimum value.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][DataFrame.minOf], which returns the minimum [<code>expression</code>][expression] value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>minByOrNull</code>][DataFrame.minByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [<code>maxBy</code>][DataFrame.maxBy] — the mirror operation.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just the smallest one.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age"
 * df.minBy { age }
 * // The row with the smallest weight-to-age ratio
 * df.minBy { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return The first [<code>DataRow</code>][DataRow] for which [<code>expression</code>][expression] returns the minimum value.
 * @throws NoSuchElementException if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = minByOrNull(skipNaN, expression).suggestIfNull("minBy")

/**
 * Returns the first row of this [<code>DataFrame</code>][DataFrame] that has the smallest value
 * in the column with the given name.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][DataFrame.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [<code>minByOrNull</code>][DataFrame.minByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compare.
 * - [<code>min</code>][DataFrame.min] — returns the smallest value itself instead of the row it belongs to.
 * - [<code>maxBy</code>][DataFrame.maxBy] — the mirror operation.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just the smallest one.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age"
 * df.minBy("age")
 * ```
 *
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The first [<code>DataRow</code>][DataRow] with the smallest value in the given column.
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
 * Returns the first row of this [<code>DataFrame</code>][DataFrame] for which the given [<code>expression</code>][expression] returns
 * the minimum value, or `null` if there is nothing to compare.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minByOrNull</code>][minByOrNull] with [<code>minOfOrNull</code>][DataFrame.minOfOrNull], which returns the minimum
 * [<code>expression</code>][expression] value itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>minBy</code>][DataFrame.minBy] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>maxByOrNull</code>][DataFrame.maxByOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age", or `null` if this dataframe is empty
 * df.minByOrNull { age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return The first [<code>DataRow</code>][DataRow] for which [<code>expression</code>][expression] returns the minimum value,
 *   or `null` if there are no values to compare.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.minByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.min<C>(skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the first row of this [<code>DataFrame</code>][DataFrame] that has the smallest value in the column with
 * the given name, or `null` if there is nothing to compare.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Returns `null` when there is nothing left to compare,
 * for instance when the input is empty or contains only `null`
 * (or, if [skipNaN] is `true`, only `null` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN]) values.
 *
 * Don't confuse [<code>minByOrNull</code>][minByOrNull] with [<code>minOfOrNull</code>][DataFrame.minOfOrNull], which returns the minimum
 * value a row expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [<code>minBy</code>][DataFrame.minBy] — throws instead of returning `null` when there's nothing to compare.
 * - [<code>minOrNull</code>][DataFrame.minOrNull] — returns the smallest value itself instead of
 *   the row it belongs to.
 * - [<code>maxByOrNull</code>][DataFrame.maxByOrNull] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the smallest "age", or `null` if this dataframe is empty
 * df.minByOrNull("age")
 * ```
 *
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The first [<code>DataRow</code>][DataRow] with the smallest value in the given column,
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
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each suitable column.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * See also:
 * - [<code>minFor</code>][Grouped.minFor] — the same, but for an explicit selection of columns.
 * - [<code>min</code>][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>max</code>][Grouped.max] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest value of each comparable column
 * df.groupBy { city }.min()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the minimum of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMin1")
public fun <T> Grouped<T>.min(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(skipNaN, intraComparableColumns())

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each selected column.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [<code>min</code>][Grouped.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][Grouped.maxFor] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.groupBy { city }.minFor { age and weight }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] used to select the columns
 *   to compute the minimum of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the minimum of each selected column per group.
 */
@Refine
@Interpretable("GroupByMin0")
public fun <T, C : Comparable<*>?> Grouped<T>.minFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.min.invoke(skipNaN).aggregateFor(this, columns)

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the minimum for each selected column.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions].
 *
 * See also:
 * - [<code>min</code>][Grouped.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][Grouped.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][Grouped.maxFor] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.groupBy { city }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the minimum of each selected column per group.
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
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group.
 * That column is named [<code>name</code>][name], or, if [<code>name</code>][name] is `null`, after the selected column
 * if exactly one column is selected, and `"min"` otherwise.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions].
 *
 * See also:
 * - [<code>minFor</code>][Grouped.minFor] — the minimum of each selected column separately, per group.
 * - [<code>minOf</code>][Grouped.minOf] — the minimum of the values a row expression returns
 *   for each row of a group.
 * - [<code>max</code>][Grouped.max] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
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
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single minimum per group.
 */
@Refine
@Interpretable("GroupByMin2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.min(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateAll(this, name, columns)

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group.
 * That column is named [<code>name</code>][name], or, if [<code>name</code>][name] is `null`, after the selected column
 * if exactly one column is selected, and `"min"` otherwise.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions].
 *
 * See also:
 * - [<code>minFor</code>][Grouped.minFor] — the minimum of each selected column separately, per group.
 * - [<code>minOf</code>][Grouped.minOf] — the minimum of the values a row expression returns
 *   for each row of a group.
 * - [<code>max</code>][Grouped.max] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
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
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single minimum per group.
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
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the minimum of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the minimum per group, named [<code>name</code>][name] (or `"min"` if [<code>name</code>][name] is `null`).
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * Don't confuse [<code>minOf</code>][minOf] with [<code>minBy</code>][GroupBy.minBy], which returns the row of each group for which
 * the expression returns the minimum value, instead of that value.
 *
 * See also:
 * - [<code>min</code>][Grouped.min] — a single minimum of all values in the selected columns, per group.
 * - [<code>maxOf</code>][Grouped.maxOf] — the mirror operation.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest weight-to-age ratio, in a column called "minRatio"
 * df.groupBy { city }.minOf("minRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column. If `null` (the default), `"min"` is used.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single minimum per group.
 */
@Refine
@Interpretable("GroupByMinOf")
public inline fun <T, reified C : Comparable<C & Any>?> Grouped<T>.minOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = Aggregators.min<C>(skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the first row for which the given [<code>rowExpression</code>][rowExpression]
 * returns the minimum value.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy] — an intermediate step which can be finished with
 * [<code>concat</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.concat] (to get a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected rows),
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.values], or [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.into].
 *
 * See [<code>GroupBy reducing</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing] for more details.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][Grouped.minOf], which returns the minimum value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [<code>maxBy</code>][GroupBy.maxBy] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the smallest "age"
 * df.groupBy { city }.minBy { age }.concat()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare for each row.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the first row
 *   for which [<code>rowExpression</code>][rowExpression] returns the minimum value.
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
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the first row that has the smallest value
 * in the column with the given name.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy] — an intermediate step which can be finished with
 * [<code>concat</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.concat] (to get a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected rows),
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.values], or [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.into].
 *
 * See [<code>GroupBy reducing</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing] for more details.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][Grouped.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row it belongs to.
 *
 * See also:
 * - [<code>min</code>][Grouped.min] — the minimum value itself instead of the row it belongs to.
 * - [<code>maxBy</code>][GroupBy.maxBy] — the mirror operation.
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the smallest "age"
 * df.groupBy { city }.minBy("age").concat()
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the first row
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
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the minimum
 * of each suitable column of the corresponding group.
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>minFor</code>][Pivot.minFor] — the same, but for an explicit selection of columns.
 * - [<code>min</code>][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>max</code>][Pivot.max] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest value of each comparable column
 * df.pivot { city }.min()
 * ```
 *
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @return A single [DataRow] with the minimum of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    delegate { min(separate, skipNaN) }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the minimum
 * of each selected column of the corresponding group.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions], or check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][Pivot.maxFor] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.pivot { city }.minFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.minFor(separate = true) { age and weight }
 * ```
 *
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the minimum
 * of each selected column of the corresponding group.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions], or check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][Pivot.maxFor] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest "age" and the smallest "weight"
 * df.pivot { city }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>Pivot</code>][Pivot] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the smallest
 * value among all the values in the selected columns of the corresponding group.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions], or check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]`()` — the minimum of each suitable column separately, per group.
 * - [<code>minFor</code>][Pivot.minFor] — the minimum of each selected column separately, per group.
 * - [<code>max</code>][Pivot.max] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.min { age and weight }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A single [DataRow] with, per [pivot] group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> Pivot<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { min(skipNaN, columns) }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the smallest
 * value among all the values in the selected columns of the corresponding group.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions], or check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]`()` — the minimum of each suitable column separately, per group.
 * - [<code>minFor</code>][Pivot.minFor] — the minimum of each selected column separately, per group.
 * - [<code>max</code>][Pivot.max] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the minimum of the values that the given [<code>rowExpression</code>][rowExpression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the minimum
 * of the expression's results for the rows of the corresponding group.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * Don't confuse [<code>minOf</code>][minOf] with [<code>minBy</code>][Pivot.minBy], which returns the first row of each group for
 * which the expression returns the minimum value, instead of that value.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxOf</code>][Pivot.maxOf] — the mirror operation.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the smallest weight-to-age ratio
 * df.pivot { city }.minOf { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A single [DataRow] with, per [pivot] group, the minimum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataRow<T> = delegate { minOf(skipNaN, rowExpression) }

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the first [<code>row</code>][DataRow]
 * for which the given [<code>rowExpression</code>][rowExpression] returns the minimum value.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][Pivot.minOf], which returns the minimum value the expression
 * returns itself, instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>maxBy</code>][Pivot.maxBy] — the mirror operation.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the smallest weight-to-age ratio
 * df.pivot { city }.minBy { (weight ?: 0) / age }.with { name }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to evaluate for each row.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group,
 *   the first row where the [<code>rowExpression</code>][rowExpression] produced the minimum result.
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
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the first [<code>row</code>][DataRow]
 * that has the smallest value in the given [<code>column</code>][column].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][Pivot.minOf], which returns the minimum value a row expression
 * returns itself, instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][Pivot.min]` { columns }` — the minimum value itself, instead of the row.
 * - [<code>maxBy</code>][Pivot.maxBy] — the mirror operation.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the youngest person
 * df.pivot { city }.minBy("age").with { name }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group, the first row with the smallest value
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
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the minimum of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the minimum of each suitable column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>minFor</code>][PivotGroupBy.minFor] — the same, but for an explicit selection of columns.
 * - [<code>min</code>][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>max</code>][PivotGroupBy.max] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest value of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.min()
 * ```
 *
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @return A [DataFrame] with the minimum of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.min(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    minFor(separate, skipNaN, intraComparableColumns())

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the minimum of each selected column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][PivotGroupBy.maxFor] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest "age" and the smallest "weight"
 * df.pivot { city }.groupBy { name.lastName }.minFor { age and weight }
 * ```
 *
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the minimum of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the minimum of each selected column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]`()` — the same, but for all suitable columns at once.
 * - [<code>min</code>][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxFor</code>][PivotGroupBy.maxFor] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest "age" and the smallest "weight"
 * df.pivot { city }.groupBy { name.lastName }.minFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the smallest value among all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][pivot] key (column)
 * and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]`()` — the minimum of each suitable column separately, per group.
 * - [<code>minFor</code>][PivotGroupBy.minFor] — the minimum of each selected column separately, per group.
 * - [<code>max</code>][PivotGroupBy.max] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.min { age and weight }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the minimum of.
 * @return A [DataFrame] with, per group, the smallest value among all the values
 *   in the selected columns.
 */
public fun <T, R : Comparable<R & Any>?> PivotGroupBy<T>.min(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.min<R>(skipNaN).aggregateAll(this, columns)

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing a single minimum of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the smallest value among all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][pivot] key (column)
 * and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MinDocs.MinSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]`()` — the minimum of each suitable column separately, per group.
 * - [<code>minFor</code>][PivotGroupBy.minFor] — the minimum of each selected column separately, per group.
 * - [<code>max</code>][PivotGroupBy.max] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.min("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the minimum of.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
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
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the minimum of the values that the given
 * [<code>rowExpression</code>][rowExpression] returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the minimum of the expression's results for the
 * rows of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * Result cells for which there is nothing left to compare
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * For more information about the resulting types:
 * [See "min / max Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/minmax.html#type-conversion)
 *
 * Don't confuse [<code>minOf</code>][minOf] with [<code>minBy</code>][PivotGroupBy.minBy], which returns the first row of each group
 * for which the expression returns the minimum value, instead of that value.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]` { columns }` — a single minimum of all values in the selected columns,
 *   per group.
 * - [<code>maxOf</code>][PivotGroupBy.maxOf] — the mirror operation.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the smallest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.minOf { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [Double] and [Float] values.
 * @param [rowExpression] The [RowExpression] to evaluate for each row.
 * @return A [DataFrame] with, per group, the minimum of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.minOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): DataFrame<T> = aggregate { minOfOrNull(skipNaN, rowExpression) }

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the first [<code>row</code>][DataRow] for which the given [<code>rowExpression</code>][rowExpression] returns the minimum value.
 *
 *
 *
 * The given [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] is evaluated for each row of the dataframe.
 * The row is both the receiver and the argument (`it`) of the expression,
 * so the values in it can be accessed directly.
 *
 * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][PivotGroupBy.minOf], which returns the minimum value the
 * expression returns itself, instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>maxBy</code>][PivotGroupBy.maxBy] — the mirror operation.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the smallest weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.minBy { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to evaluate for each row.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group,
 *   the first row where the [<code>rowExpression</code>][rowExpression] produced the minimum result.
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
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the first [<code>row</code>][DataRow] that has the smallest value in the given [<code>column</code>][column].
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * Groups that have no values to compare cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>minBy</code>][minBy] with [<code>minOf</code>][PivotGroupBy.minOf], which returns the minimum value a row
 * expression returns itself, instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>min</code>][PivotGroupBy.min]` { columns }` — the minimum value itself, instead of the row.
 * - [<code>maxBy</code>][PivotGroupBy.maxBy] — the mirror operation.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Min Operation</code>][org.jetbrains.kotlinx.dataframe.api.MinDocs] — an overview of all `min` modes.
 *
 * For more information: [See `minBy` on the documentation website.](https://kotlin.github.io/dataframe/minby.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the youngest person
 * df.pivot { city }.groupBy { name.lastName }.minBy("age").with { name.firstName }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   Only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group, the first row with the smallest value
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
