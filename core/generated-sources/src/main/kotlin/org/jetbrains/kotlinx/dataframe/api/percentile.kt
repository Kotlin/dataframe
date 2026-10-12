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
import org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_VERB
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Issues
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
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region docs

/**
 *
 *
 * ## The Percentile Operation
 *
 * Computes the given [percentile](https://en.wikipedia.org/wiki/Percentile) of values:
 * the value below which the given percentage of the sorted values falls.
 * This is also called the "centile", or the 100-[quantile](https://en.wikipedia.org/wiki/Quantile).
 *
 * The 25th percentile is also known as the first [quartile](https://en.wikipedia.org/wiki/Quartile) (Q1),
 * the 50th percentile as the [<code>median</code>][DataFrame.median] or second quartile (Q2),
 * and the 75th percentile as the third quartile (Q3).
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * ### Percentile Modes
 *
 * Depending on what exactly you want the percentile of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>`percentile`</code>][DataFrame.percentile]`(percentile)` — the percentile of each suitable column separately.
 * - [<code>`percentile`</code>][DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in all selected columns.
 * - [<code>`percentileFor`</code>][DataFrame.percentileFor]`(percentile) { columns }` — the percentile of each selected column
 *   separately.
 * - [<code>`percentileOf`</code>][DataFrame.percentileOf]`(percentile) { expression }` — the percentile of the values that
 *   the given expression returns for each row.
 * - [<code>`percentileBy`</code>][DataFrame.percentileBy]`(percentile) { expression }` — the row at the percentile of the values
 *   that the given expression returns for each row.
 *
 * [<code>`percentile`</code>][DataFrame.percentile]`(percentile) { columns }`, [<code>`percentileOf`</code>][DataFrame.percentileOf], and
 * [<code>`percentileBy`</code>][DataFrame.percentileBy] all have an `-OrNull` counterpart which returns `null` instead of
 * throwing an exception when there is nothing to compute the percentile of.
 *
 * Due to a limitation in Kotlin's overload resolution ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)),
 * computing the percentile of non-number comparable values with
 * [<code>`percentile`</code>][DataFrame.percentile]`(percentile) { columns }` or [<code>`percentileOf`</code>][DataFrame.percentileOf]
 * requires either explicit type arguments, like `df.percentile<_, String>(25.0) { name.firstName }`,
 * or passing the lambda inside the parentheses, like `df.percentile(25.0, { name.firstName })`.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
 *
 * Related operation:
 * - [<code>`median`</code>][DataFrame.median] — the 50th percentile; the same as `percentile(50.0)`.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * See all summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface PercentileDocs : CommonMedianPercentileDocs {

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
     * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`("length", "age")`
     *
     *
     *
     */
    typealias PercentileSelectingOptions = Nothing

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
     * <code>`df`</code>`.`[<code>percentileFor</code>][org.jetbrains.kotlinx.dataframe.api.percentileFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>percentileFor</code>][org.jetbrains.kotlinx.dataframe.api.percentileFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>percentileFor</code>][org.jetbrains.kotlinx.dataframe.api.percentileFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>percentileFor</code>][org.jetbrains.kotlinx.dataframe.api.percentileFor]`("length", "age")`
     *
     *
     *
     */
    typealias PercentileForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values in this [<code>DataColumn</code>][DataColumn].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [<code>percentile</code>][DataColumn.percentile] overload with a `skipNaN` parameter,
 * which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>percentileOrNull</code>][DataColumn.percentileOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — the percentile of the values an expression returns for each element.
 * - [<code>percentileBy</code>][DataColumn.percentileBy] — the element at the percentile of the values a selector returns.
 * - [<code>median</code>][DataColumn.median] — the median (50th percentile) of the values in this column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The first quartile of the first names in the "name"/"firstName" column (in alphabetical order)
 * df.name.firstName.percentile(25.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @return The given percentile of the values in this column.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentile(percentile: Double): T & Any =
    percentileOrNull(percentile).suggestIfNull("percentile")

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values in this [<code>DataColumn</code>][DataColumn],
 * or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [<code>percentileOrNull</code>][DataColumn.percentileOrNull] overload with a `skipNaN`
 * parameter, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>percentile</code>][DataColumn.percentile] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — the percentile of the values an expression returns
 *   for each element.
 * - [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull] — the element at the percentile of the values
 *   a selector returns.
 * - [<code>medianOrNull</code>][DataColumn.medianOrNull] — the median (50th percentile) of the values in this column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the cities in the "city" column (in alphabetical order),
 * // or `null` if the column contains no values other than `null`
 * df.city.percentileOrNull(75.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @return The given percentile of the values in this column,
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentileOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateSingleColumn(this)

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>percentileOrNull</code>][DataColumn.percentileOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — the percentile of the values an expression returns for each element.
 * - [<code>percentileBy</code>][DataColumn.percentileBy] — the element at the percentile of the values a selector returns.
 * - [<code>median</code>][DataColumn.median] — the median (50th percentile) of the values in this column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The first quartile of the ages in the "age" column
 * df.age.percentile(25.0)
 * // The 90th percentile of the weights in the "weight" column, ignoring `NaN` values
 * df.weight.percentile(90.0, skipNaN = true)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of the values in this column, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T> DataColumn<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? =
    percentileOrNull(percentile = percentile, skipNaN = skipNaN).suggestIfNull("percentile")

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>percentile</code>][DataColumn.percentile] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — the percentile of the values an expression returns
 *   for each element.
 * - [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull] — the element at the percentile of the values
 *   a selector returns.
 * - [<code>medianOrNull</code>][DataColumn.medianOrNull] — the median (50th percentile) of the values in this column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weights in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.percentileOrNull(75.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of the values in this column, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T> DataColumn<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateSingleColumn(this)

/**
 * Returns the element of this [<code>DataColumn</code>][DataColumn] at the given [<code>percentile</code>][percentile] of the values that the given [<code>selector</code>][selector]
 * returns for each element.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentileOf</code>][DataColumn.percentileOf], which returns the percentile
 * of the [<code>selector</code>][selector] values itself instead of the element it belongs to.
 *
 * See also:
 * - [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianBy</code>][DataColumn.medianBy] — the element at the median (50th percentile) of the values
 *   a selector returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The first name at the first quartile of the lengths of all names in the "name"/"firstName" column
 * df.name.firstName.percentileBy(25.0) { it.length }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the given percentile of the values [<code>selector</code>][selector] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = percentileByOrNull(percentile, skipNaN, selector).suggestIfNull("percentileBy")

/**
 * Returns the element of this [<code>DataColumn</code>][DataColumn] at the given [<code>percentile</code>][percentile] of the values that the given [<code>selector</code>][selector]
 * returns for each element, or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileByOrNull</code>][percentileByOrNull] with [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull], which returns
 * the percentile of the [<code>selector</code>][selector] values itself instead of the element it belongs to.
 *
 * See also:
 * - [<code>percentileBy</code>][DataColumn.percentileBy] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianByOrNull</code>][DataColumn.medianByOrNull] — the element at the median (50th percentile) of the values
 *   a selector returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The first name at the first quartile of the lengths of all names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.percentileByOrNull(25.0) { it.length }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the given percentile of the values [<code>selector</code>][selector] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>percentileOf</code>][DataColumn.percentileOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
 *
 *
 *
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][DataColumn.percentileBy], which returns the element
 * the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianOf</code>][DataColumn.medianOf] — the median (50th percentile) of the values an expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all last names in the "name" column group, in upper case
 * df.name.percentileOf<_, String>(25.0) { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.percentileOf(25.0, { it.lastName.uppercase() })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOf(
    percentile: Double,
    crossinline expression: (T) -> R,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
 *
 *
 *
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOfOrNull</code>][percentileOfOrNull] with [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull], which returns
 * the element the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — the median (50th percentile) of the values
 *   an expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all last names in the "name" column group, in upper case,
 * // or `null` if the column group is empty
 * df.name.percentileOfOrNull<_, String>(25.0) { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.percentileOfOrNull(25.0, { it.lastName.uppercase() })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: (T) -> R,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
 *
 *
 *
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][DataColumn.percentileBy], which returns the element
 * the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianOf</code>][DataColumn.medianOf] — the median (50th percentile) of the values an expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The 90th percentile of the lengths of all first names in the "name"/"firstName" column
 * df.name.firstName.percentileOf(90.0) { it.length }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOfOrNull</code>][percentileOfOrNull] with [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull], which returns
 * the element the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — the median (50th percentile) of the values
 *   an expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The 90th percentile of the lengths of all first names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.percentileOfOrNull(90.0) { it.length }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the percentile of.
 */
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

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow],
 * or `null` if there is nothing to compute the percentile of.
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 * This overload is meant for non-number self-comparable types, like [<code>String</code>][String] or dates.
 *
 *
 *
 * __Careful:__ Calling this function with a primitive number type, like `<Int>`, without passing `skipNaN`
 * also resolves to this overload, instead of the one returning a [<code>Double</code>][Double].
 * The interpolated [<code>Double</code>][Double] result is then returned as [T], which may truncate it or throw a
 * [<code>ClassCastException</code>][ClassCastException]. Pass `skipNaN` explicitly (like `skipNaN = false`) to call the overload for numbers.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>rowPercentileOf</code>][DataRow.rowPercentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — the percentile of the values in specific columns
 *   of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The first quartile of all `String` values ("name"/"firstName", "name"/"lastName", and "city")
 * // in the first row, or `null` if there are none
 * df[0].rowPercentileOfOrNull<String>(25.0)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @return The given percentile of the values of type [<code>T</code>][T] in this row,
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOfOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 * This overload is meant for non-number self-comparable types, like [<code>String</code>][String] or dates.
 *
 *
 *
 * __Careful:__ Calling this function with a primitive number type, like `<Int>`, without passing `skipNaN`
 * also resolves to this overload, instead of the one returning a [<code>Double</code>][Double].
 * The interpolated [<code>Double</code>][Double] result is then returned as [T], which may truncate it or throw a
 * [<code>ClassCastException</code>][ClassCastException]. Pass `skipNaN` explicitly (like `skipNaN = false`) to call the overload for numbers.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>rowPercentileOfOrNull</code>][DataRow.rowPercentileOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the percentile of.
 * - [<code>percentile</code>][DataFrame.percentile] — the percentile of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The first quartile of all `String` values ("name"/"firstName", "name"/"lastName", and "city")
 * // in the first row
 * df[0].rowPercentileOf<String>(25.0)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @return The given percentile of the values of type [<code>T</code>][T] in this row.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOf(percentile: Double): T =
    rowPercentileOfOrNull<T>(percentile).suggestIfNull("rowPercentileOf")

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 * __Careful:__ To call this overload, [skipNaN] must be passed explicitly (like `skipNaN = false`).
 * Without it, a call like `<Int>` resolves to the overload for comparable values instead,
 * which returns the interpolated result as [T], possibly truncating it or throwing a [<code>ClassCastException</code>][ClassCastException].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>rowPercentileOf</code>][DataRow.rowPercentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — the percentile of the values in specific columns
 *   of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all `Int` values ("age" and "weight") in the first row, or `null` if there are none
 * df[0].rowPercentileOfOrNull<Int>(75.0, skipNaN = false)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <reified T> DataRow<*>.rowPercentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 * __Careful:__ To call this overload, [skipNaN] must be passed explicitly (like `skipNaN = false`).
 * Without it, a call like `<Int>` resolves to the overload for comparable values instead,
 * which returns the interpolated result as [T], possibly truncating it or throwing a [<code>ClassCastException</code>][ClassCastException].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * See also:
 * - [<code>rowPercentileOfOrNull</code>][DataRow.rowPercentileOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the percentile of.
 * - [<code>rowMedianOf</code>][DataRow.rowMedianOf] — the median (50th percentile) of the numbers of one type in this row.
 * - [<code>percentile</code>][DataFrame.percentile] — the percentile of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all `Int` values ("age" and "weight") in the first row
 * df[0].rowPercentileOf<Int>(75.0, skipNaN = false)
 * // The 90th percentile of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowPercentileOf<Double>(90.0, skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <reified T> DataRow<*>.rowPercentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number =
    rowPercentileOfOrNull<T>(percentile, skipNaN).suggestIfNull("rowPercentileOf")

// endregion

// region DataFrame

/**
 * Returns the given [<code>percentile</code>][percentile] of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * See also:
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the same, but for an explicit selection of columns.
 * - [<code>percentile</code>][DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns.
 * - [<code>median</code>][DataFrame.median] — the median (50th percentile) of each column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the first quartile of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.percentile(25.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Percentile0")
public fun <T> DataFrame<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

/**
 *
 *
 * Returns the given percentile of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.medianFor] — the median (50th percentile) of each selected column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the first quartile of the "age" values and of the "weight" values
 * df.percentileFor(25.0) { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.percentileFor(25.0, skipNaN = true) { age into "q1Age" and (weight into "q1Weight") }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the percentile of.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each selected column.
 */
@Refine
@Interpretable("Percentile1")
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Returns the given percentile of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.medianFor] — the median (50th percentile) of each selected column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the first quartile of the "age" values and of the "weight" values
 * df.percentileFor(25.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each selected column.
 */
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

/**
 * Returns a single [<code>percentile</code>][percentile] of all the comparable values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [<code>percentile</code>][DataFrame.percentile] overload with a `skipNaN` parameter
 * is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — the percentile of the values a row expression returns for each row.
 * - [<code>median</code>][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all first and last names in the "name" column group
 * df.percentile<_, String>(25.0) { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentile(25.0, { name.firstName and name.lastName })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return The given percentile of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C & Any = percentileOrNull(percentile, columns).suggestIfNull("percentile")

/**
 * Returns a single [<code>percentile</code>][percentile] of all the comparable values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [<code>percentileOrNull</code>][DataFrame.percentileOrNull] overload with a `skipNaN`
 * parameter is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentile</code>][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — the percentile of the values a row expression
 *   returns for each row.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all first and last names in the "name" column group,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull<_, String>(25.0) { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOrNull(25.0, { name.firstName and name.lastName })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return The given percentile of all the values in the selected columns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.percentileComparables<C>(percentile).aggregateAll(this, columns)

/**
 * Returns a single [<code>percentile</code>][percentile] of all the numbers in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — the percentile of the values a row expression returns for each row.
 * - [<code>median</code>][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns
 * df.percentile(75.0) { age and weight }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return The given percentile of all the values in the selected columns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? =
    percentileOrNull(percentile, skipNaN, columns).suggestIfNull("percentile")

/**
 * Returns a single [<code>percentile</code>][percentile] of all the numbers in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentile</code>][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — the percentile of the values a row expression
 *   returns for each row.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull(75.0) { age and weight }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return The given percentile of all the values in the selected columns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.percentileNumbers<C>(percentile, skipNaN).aggregateAll(this, columns)

/**
 * Returns a single [<code>percentile</code>][percentile] of all the values in the columns of this [<code>DataFrame</code>][DataFrame] with the given names.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>median</code>][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentile</code>][org.jetbrains.kotlinx.dataframe.api.percentile]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns, as a `Double`
 * df.percentile(75.0, "age", "weight")
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any = percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

/**
 * Returns a single [<code>percentile</code>][percentile] of all the values in the columns of this [<code>DataFrame</code>][DataFrame] with the given names,
 * or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>percentile</code>][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileFor</code>][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
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
 * <code>`df`</code>`.`[<code>percentileOrNull</code>][org.jetbrains.kotlinx.dataframe.api.percentileOrNull]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns, as a `Double`,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull(75.0, "age", "weight")
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The given percentile of all the values in the selected columns,
 *   or `null` if there are no values to compute the percentile of.
 */
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

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>percentileOf</code>][DataFrame.percentileOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][DataFrame.percentileBy], which returns the row the
 * percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>percentile</code>][DataFrame.percentile] — a single percentile of all values in the selected columns.
 * - [<code>medianOf</code>][DataFrame.medianOf] — the median (50th percentile) of the values a row expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of the full names of all rows
 * df.percentileOf<_, String>(25.0) { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOf(25.0, { name.firstName + " " + name.lastName })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The given percentile of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOf(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [<code>percentile</code>][percentile] of the comparable values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [<code>Double</code>][Double].
 *
 *
 *
 * __Note:__ Due to a limitation in Kotlin's overload resolution
 * ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)), calling this overload with a lambda
 * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
 * or passing the lambda inside the parentheses (see the examples below).
 * For a lambda returning primitive numbers, the overload returning a [<code>Double</code>][Double] is picked automatically.
 *
 *
 *
 * From Kotlin 2.5, this limitation can be lifted by enabling eager lambda analysis
 * ([KT-51107](https://youtrack.jetbrains.com/issue/KT-51107/ELA-Overload-resolution-via-eager-lambda-return-type-analysis)) with the compiler option `-XXLanguage:+EagerLambdaAnalysis`,
 * like `kotlin { compilerOptions { freeCompilerArgs.add("-XXLanguage:+EagerLambdaAnalysis") } }` in Gradle.
 * Then, the right overload is picked for lambdas returning non-number comparable values too,
 * without explicit type arguments.
 * Note that this is an internal compiler option without any stability guarantees,
 * so it is not recommended for production use yet.
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is selected from the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3,
 * so it has the same type as the values (minus nullability, if the values were nullable).
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOfOrNull</code>][percentileOfOrNull] with [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull], which returns
 * the row the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — a single percentile of all values in the selected columns.
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — the median (50th percentile) of the values
 *   a row expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of the full names of all rows, or `null` if this dataframe is empty
 * df.percentileOfOrNull<_, String>(25.0) { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOfOrNull(25.0, { name.firstName + " " + name.lastName })
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The given percentile of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double].
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][DataFrame.percentileBy], which returns the row the
 * percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>percentile</code>][DataFrame.percentile] — a single percentile of all values in the selected columns.
 * - [<code>medianOf</code>][DataFrame.medianOf] — the median (50th percentile) of the values a row expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weight-to-age ratios of all rows
 * df.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The given percentile of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [<code>percentile</code>][percentile] of the numbers that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the percentile of.
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 *
 *
 * The result is a [<code>Double</code>][Double], interpolated between the values using [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 * Don't confuse [<code>percentileOfOrNull</code>][percentileOfOrNull] with [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull], which returns
 * the row the percentile of the [<code>expression</code>][expression] values belongs to instead of that value.
 *
 * See also:
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — a single percentile of all values in the selected columns.
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — the median (50th percentile) of the values
 *   a row expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weight-to-age ratios of all rows, or `null` if this dataframe is empty
 * df.percentileOfOrNull(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The given percentile of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN).aggregateOf(this, expression)

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the given [<code>percentile</code>][percentile] of the values that the given [<code>expression</code>][expression]
 * returns for each row.
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentileOf</code>][DataFrame.percentileOf], which returns the percentile
 * of the [<code>expression</code>][expression] values itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>medianBy</code>][DataFrame.medianBy] — the row at the median (50th percentile) of the values
 *   a row expression returns.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just one.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age"
 * df.percentileBy(25.0) { age }
 * // The row at the 90th percentile of the weight-to-age ratios
 * df.percentileBy(90.0) { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return The [<code>DataRow</code>][DataRow] at the given percentile of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = percentileByOrNull(percentile, skipNaN, expression).suggestIfNull("percentileBy")

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the given [<code>percentile</code>][percentile] of the values in the column with the given name.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentile</code>][DataFrame.percentile], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [<code>medianBy</code>][DataFrame.medianBy] — the row at the median (50th percentile) of the values in a column.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just one.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age"
 * df.percentileBy(25.0, "age")
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The [<code>DataRow</code>][DataRow] at the given percentile of the values in the given column.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
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

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the given [<code>percentile</code>][percentile] of the values that the given [<code>expression</code>][expression]
 * returns for each row, or `null` if there is nothing to compute the percentile of.
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileByOrNull</code>][percentileByOrNull] with [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull], which returns
 * the percentile of the [<code>expression</code>][expression] values itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileBy</code>][DataFrame.percentileBy] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>medianByOrNull</code>][DataFrame.medianByOrNull] — the row at the median (50th percentile) of the values
 *   a row expression returns.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age", or `null` if this dataframe is empty
 * df.percentileByOrNull(25.0) { age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return The [<code>DataRow</code>][DataRow] at the given percentile of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the given [<code>percentile</code>][percentile] of the values in the column with the given name,
 * or `null` if there is nothing to compute the percentile of.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the percentile of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>percentileByOrNull</code>][percentileByOrNull] with [<code>percentileOrNull</code>][DataFrame.percentileOrNull], which returns
 * the percentile value itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileBy</code>][DataFrame.percentileBy] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [<code>medianByOrNull</code>][DataFrame.medianByOrNull] — the row at the median (50th percentile) of the values
 *   in a column.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age", or `null` if this dataframe is empty
 * df.percentileByOrNull(25.0, "age")
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The [<code>DataRow</code>][DataRow] at the given percentile of the values in the given column,
 *   or `null` if there are no values to compute the percentile of.
 */
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

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the given [<code>percentile</code>][percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the percentile for each suitable column.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * See also:
 * - [<code>percentileFor</code>][Grouped.percentileFor] — the same, but for an explicit selection of columns.
 * - [<code>percentile</code>][Grouped.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>median</code>][Grouped.median] — the median (50th percentile) of each column, per group.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of each comparable column
 * df.groupBy { city }.percentile(25.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the given percentile of each suitable column per group.
 */
@Refine
@Interpretable("GroupByPercentile1")
public fun <T> Grouped<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the percentile for each selected column.
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.groupBy { city }.percentileFor(25.0) { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the percentile of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the given percentile of each selected column per group.
 */
@Refine
@Interpretable("GroupByPercentile0")
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the percentile for each selected column.
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.groupBy { city }.percentileFor(25.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the given percentile of each selected column per group.
 */
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

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the percentile per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"percentile"` otherwise.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions].
 *
 * See also:
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentileFor] — the percentile of each selected column separately, per group.
 * - [<code>`percentileOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentileOf] — the percentile of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of all values in the "age" and "weight" columns,
 * // in a column called "q1"
 * df.groupBy { city }.percentile(25.0, "q1") { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"percentile"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single percentile per group.
 */
@Refine
@Interpretable("GroupByPercentile2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, name, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the percentile per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"percentile"` otherwise.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions].
 *
 * See also:
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentileFor] — the percentile of each selected column separately, per group.
 * - [<code>`percentileOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.percentileOf] — the percentile of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of all values in the "age" and "weight" columns,
 * // in a column called "q1"
 * df.groupBy { city }.percentile(25.0, "age", "weight", name = "q1")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"percentile"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single percentile per group.
 */
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

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the given [<code>percentile</code>][percentile] of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the percentile per group, named [<code>name</code>][name] (or `"percentile"` if [<code>name</code>][name] is `null`).
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the percentile of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][GroupBy.percentileBy], which returns the row of each group
 * at the percentile of the values the expression returns, instead of that value.
 *
 * See also:
 * - [<code>percentile</code>][Grouped.percentile] — a single percentile of all values in the selected columns, per group.
 * - [<code>medianOf</code>][Grouped.medianOf] — the median (50th percentile) of the values a row expression returns,
 *   per group.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of the weight-to-age ratios, in a column called "q3Ratio"
 * df.groupBy { city }.percentileOf(75.0, "q3Ratio") { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [name] The name of the resulting column.
 *   If `null` (the default), `"percentile"` is used.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single percentile per group.
 */
@Refine
@Interpretable("GroupByPercentileOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.percentileOf(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the row at the given [<code>percentile</code>][percentile] of the values that the given
 * [<code>rowExpression</code>][rowExpression] returns for each row of that group.
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentileOf</code>][Grouped.percentileOf], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianBy</code>][GroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person at the first quartile of "age"
 * df.groupBy { city }.percentileBy(25.0) { age }.concat()
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the row at the given percentile
 *   of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the row at the given [<code>percentile</code>][percentile] of the values
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentile</code>][Grouped.percentile], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianBy</code>][GroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person at the first quartile of "age"
 * df.groupBy { city }.percentileBy(25.0, "age").concat()
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the row at the given percentile
 *   of the values in the given column.
 */
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

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the given [<code>percentile</code>][percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the percentile
 * of each suitable column of the corresponding group.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>percentileFor</code>][Pivot.percentileFor] — the same, but for an explicit selection of columns.
 * - [<code>percentile</code>][Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of each comparable column
 * df.pivot { city }.percentile(25.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each suitable column per [<code>pivot</code>][pivot] group.
 */
public fun <T> Pivot<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the percentile
 * of each selected column of the corresponding group.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.percentileFor(25.0) { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.percentileFor(25.0, separate = true) { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the percentile of.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each selected column per [<code>pivot</code>][pivot] group.
 */
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { percentileFor(percentile, separate, skipNaN, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the percentile
 * of each selected column of the corresponding group.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.percentileFor(25.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the given percentile of each selected column per [<code>pivot</code>][pivot] group.
 */
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

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the percentile
 * of all the values in the selected columns of the corresponding group.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile)` — the percentile of each suitable column separately,
 *   per group.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileFor] — the percentile of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.percentile(75.0) { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { percentile(percentile, skipNaN, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the percentile
 * of all the values in the selected columns of the corresponding group.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentile]`(percentile)` — the percentile of each suitable column separately,
 *   per group.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileFor] — the percentile of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.percentile(75.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the given percentile of all the values
 *   in the selected columns.
 */
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

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the given [<code>percentile</code>][percentile] of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the percentile
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][Pivot.percentileBy], which returns the row of each group
 * at the percentile of the values the expression returns, instead of that value.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>percentile</code>][Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of the weight-to-age ratios
 * df.pivot { city }.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the given percentile of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { percentileOf(percentile, skipNaN, expression) }

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the [<code>row</code>][DataRow]
 * at the given [<code>percentile</code>][percentile] of the values that the given [<code>rowExpression</code>][rowExpression] returns for each row of that group.
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentileOf</code>][Pivot.percentileOf], which returns the percentile of the values
 * the expression returns itself, instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>medianBy</code>][Pivot.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person at the third quartile of the weight-to-age ratios
 * df.pivot { city }.percentileBy(75.0) { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group,
 *   the row at the given percentile of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the [<code>row</code>][DataRow]
 * at the given [<code>percentile</code>][percentile] of the values in the given [<code>column</code>][column].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentile</code>][Pivot.percentile], which returns the percentile value itself,
 * instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>medianBy</code>][Pivot.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person at the first quartile of "age"
 * df.pivot { city }.percentileBy(25.0, "age").with { name }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group, the row at the given percentile of the values in the given column.
 */
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

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the given [<code>percentile</code>][percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the percentile of each suitable column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the percentile, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>percentileFor</code>][PivotGroupBy.percentileFor] — the same, but for an explicit selection of columns.
 * - [<code>percentile</code>][PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the first quartile of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.percentile(25.0)
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the given percentile of each suitable column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the percentile of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.percentileFor(25.0) { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the percentile of.
 * @return A [<code>DataFrame</code>][DataFrame] with the given percentile of each selected column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, separate, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the given percentile of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the percentile of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile)` — the same, but for all suitable columns at once.
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.percentileFor(25.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the given percentile of each selected column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
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

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the percentile of all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile)` — the percentile of each suitable column
 *   separately, per group.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentileFor] — the percentile of each selected column separately,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.percentile(75.0) { age and weight }
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the percentile of.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single percentile of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the percentile of all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs.PercentileSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`percentile`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentile]`(percentile)` — the percentile of each suitable column
 *   separately, per group.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.percentileFor] — the percentile of each selected column separately,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.percentile(75.0, "age", "weight")
 * ```
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [columns] The names of the columns to compute the percentile of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the given percentile of all the values
 *   in the selected columns.
 */
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

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the given [<code>percentile</code>][percentile] of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the percentile of the expression's results for the rows
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 *
 *
 * For primitive numbers, the result is a [<code>Double</code>][Double], interpolated between the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R8.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 * For all other self-comparable values, the result is selected from the values using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so it has the same type as those values.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * For more information about the resulting types: [See "`percentile` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/percentile.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * percentile of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Don't confuse [<code>percentileOf</code>][percentileOf] with [<code>percentileBy</code>][PivotGroupBy.percentileBy], which returns the row of each
 * group at the percentile of the values the expression returns, instead of that value.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>percentile</code>][PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the third quartile of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the given percentile of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, expression)

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the [<code>row</code>][DataRow] at the given [<code>percentile</code>][percentile] of the values that the given [<code>rowExpression</code>][rowExpression] returns
 * for each row of that group.
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
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentileOf</code>][PivotGroupBy.percentileOf], which returns the percentile
 * of the values the expression returns itself, instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>medianBy</code>][PivotGroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person at the third quartile of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.percentileBy(75.0) { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group,
 *   the row at the given percentile of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the [<code>row</code>][DataRow] at the given [<code>percentile</code>][percentile] of the values in the given [<code>column</code>][column].
 *
 *
 *
 *
 *
 * Only self-comparable values are supported: values of a type `T : Comparable<T>`
 * that are mutually comparable (like strings, primitive numbers, or dates).
 * This includes all primitive number types, but no mix of different number types.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * Which value lies at the percentile is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>percentileBy</code>][percentileBy] with [<code>percentile</code>][PivotGroupBy.percentile], which returns the percentile value
 * itself, instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>medianBy</code>][PivotGroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Percentile Operation</code>][org.jetbrains.kotlinx.dataframe.api.PercentileDocs] — an overview of all `percentile` modes.
 *
 * For more information: [See `percentile` on the documentation website.](https://kotlin.github.io/dataframe/percentile.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person at the first quartile of "age"
 * df.pivot { city }.groupBy { name.lastName }.percentileBy(25.0, "age").with { name.firstName }
 * ```
 *
 * @param [percentile] The percentile to compute, in the range `[0.0, 100.0]`,
 *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
 *   A percentile outside this range causes an [<code>IllegalStateException</code>][IllegalStateException].
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group, the row at the given percentile
 *   of the values in the given column.
 */
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
