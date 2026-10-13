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
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region docs

/**
 *
 *
 * ## The Median Operation
 *
 * Computes the [median](https://en.wikipedia.org/wiki/Median) of values:
 * the value in the "middle" of the sorted values.
 * This is also called the 50th [<code>percentile</code>][DataFrame.percentile],
 * or the 2-[quantile](https://en.wikipedia.org/wiki/Quantile).
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
 * while others throw an exception at runtime.
 * Don't rely on this; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 * ### Median Modes
 *
 * Depending on what exactly you want the median of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>`median`</code>][DataFrame.median]`()` — the median of each suitable column separately.
 * - [<code>`median`</code>][DataFrame.median]` { columns }` — a single median of all values in all selected columns.
 * - [<code>`medianFor`</code>][DataFrame.medianFor]` { columns }` — the median of each selected column separately.
 * - [<code>`medianOf`</code>][DataFrame.medianOf]` { expression }` — the median of the values that the given expression
 *   returns for each row.
 * - [<code>`medianBy`</code>][DataFrame.medianBy]` { expression }` — the row at the median of the values that the given
 *   expression returns for each row.
 *
 * [<code>`median`</code>][DataFrame.median]` { columns }`, [<code>`medianOf`</code>][DataFrame.medianOf], and
 * [<code>`medianBy`</code>][DataFrame.medianBy] all have an `-OrNull` counterpart which returns `null` instead of
 * throwing an exception when there is nothing to compute the median of.
 *
 * Due to a limitation in Kotlin's overload resolution ([KT-76683](https://youtrack.jetbrains.com/issue/KT-76683)),
 * computing the median of non-number comparable values with [<code>`median`</code>][DataFrame.median]` { columns }` or
 * [<code>`medianOf`</code>][DataFrame.medianOf] requires either explicit type arguments, like
 * `df.median<_, String> { name.firstName }`, or passing the lambda inside the parentheses, like
 * `df.median({ name.firstName })`.
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
 * - [<code>`percentile`</code>][DataFrame.percentile] — any percentile of values; `median` is the same as `percentile(50.0)`.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * See all summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface MedianDocs : CommonMedianPercentileDocs {

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
     * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`("length", "age")`
     *
     *
     *
     */
    typealias MedianSelectingOptions = Nothing

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
     * <code>`df`</code>`.`[<code>medianFor</code>][org.jetbrains.kotlinx.dataframe.api.medianFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>medianFor</code>][org.jetbrains.kotlinx.dataframe.api.medianFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>medianFor</code>][org.jetbrains.kotlinx.dataframe.api.medianFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>medianFor</code>][org.jetbrains.kotlinx.dataframe.api.medianFor]`("length", "age")`
     *
     *
     *
     */
    typealias MedianForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the median of the comparable values in this [<code>DataColumn</code>][DataColumn].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [<code>median</code>][DataColumn.median] overload with a `skipNaN` parameter,
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>medianOrNull</code>][DataColumn.medianOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [<code>medianOf</code>][DataColumn.medianOf] — the median of the values an expression returns for each element.
 * - [<code>medianBy</code>][DataColumn.medianBy] — the element at the median of the values a selector returns.
 * - [<code>percentile</code>][DataColumn.percentile] — any other percentile of the values in this column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median first name in the "name"/"firstName" column (in alphabetical order)
 * df.name.firstName.median()
 * ```
 *
 * @return The median of the values in this column.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.median(): T & Any = medianOrNull().suggestIfNull("median")

/**
 * Returns the median of the comparable values in this [<code>DataColumn</code>][DataColumn],
 * or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [<code>medianOrNull</code>][DataColumn.medianOrNull] overload with a `skipNaN`
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>median</code>][DataColumn.median] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — the median of the values an expression returns
 *   for each element.
 * - [<code>medianByOrNull</code>][DataColumn.medianByOrNull] — the element at the median of the values a selector returns.
 * - [<code>percentileOrNull</code>][DataColumn.percentileOrNull] — any other percentile of the values in this column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median city in the "city" column (in alphabetical order),
 * // or `null` if the column contains no values other than `null`
 * df.city.medianOrNull()
 * ```
 *
 * @return The median of the values in this column, or `null` if there are no values to compute the median of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.medianOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateSingleColumn(this)

/**
 * Returns the median of the numbers in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>medianOrNull</code>][DataColumn.medianOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [<code>medianOf</code>][DataColumn.medianOf] — the median of the values an expression returns for each element.
 * - [<code>medianBy</code>][DataColumn.medianBy] — the element at the median of the values a selector returns.
 * - [<code>percentile</code>][DataColumn.percentile] — any other percentile of the values in this column.
 * - [<code>mean</code>][DataColumn.mean] — the average of the values in this column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median age in the "age" column
 * df.age.median()
 * // The median weight in the "weight" column, ignoring `NaN` values
 * df.weight.median(skipNaN = true)
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of the values in this column, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T> DataColumn<T>.median(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? = medianOrNull(skipNaN = skipNaN).suggestIfNull("median")

/**
 * Returns the median of the numbers in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the median of.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>median</code>][DataColumn.median] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — the median of the values an expression returns
 *   for each element.
 * - [<code>medianByOrNull</code>][DataColumn.medianByOrNull] — the element at the median of the values a selector returns.
 * - [<code>percentileOrNull</code>][DataColumn.percentileOrNull] — any other percentile of the values in this column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median weight in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.medianOrNull()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of the values in this column, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the median of.
 */
public fun <T> DataColumn<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.medianNumbers<T>(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the element of this [<code>DataColumn</code>][DataColumn] at the median of the values that the given [<code>selector</code>][selector]
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>medianOf</code>][DataColumn.medianOf], which returns the median [<code>selector</code>][selector] value itself
 * instead of the element it belongs to.
 *
 * See also:
 * - [<code>medianByOrNull</code>][DataColumn.medianByOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [<code>percentileBy</code>][DataColumn.percentileBy] — the element at any other percentile of the values a selector returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The first name of median length in the "name"/"firstName" column
 * df.name.firstName.medianBy { it.length }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the median of the values [<code>selector</code>][selector] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = medianByOrNull(skipNaN, selector).suggestIfNull("medianBy")

/**
 * Returns the element of this [<code>DataColumn</code>][DataColumn] at the median of the values that the given [<code>selector</code>][selector]
 * returns for each element, or `null` if there is nothing to compute the median of.
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianByOrNull</code>][medianByOrNull] with [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull], which returns the median
 * [<code>selector</code>][selector] value itself instead of the element it belongs to.
 *
 * See also:
 * - [<code>medianBy</code>][DataColumn.medianBy] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [<code>percentileByOrNull</code>][DataColumn.percentileByOrNull] — the element at any other percentile of the values
 *   a selector returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The first name of median length in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.medianByOrNull { it.length }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the median of the values [<code>selector</code>][selector] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.medianCommon<R>(skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the median of the comparable values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>medianOf</code>][DataColumn.medianOf] overload
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][DataColumn.medianBy], which returns the element the median
 * [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — any other percentile of the values an expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Examples
 * ```kotlin
 * // The median of all last names in the "name" column group, in upper case
 * df.name.medianOf<_, String> { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.medianOf({ it.lastName.uppercase() })
 * ```
 *
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOf(
    crossinline expression: (T) -> R,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

/**
 * Returns the median of the comparable values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] overload
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOfOrNull</code>][medianOfOrNull] with [<code>medianByOrNull</code>][DataColumn.medianByOrNull], which returns the element
 * the median [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOf</code>][DataColumn.medianOf] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — any other percentile of the values
 *   an expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Examples
 * ```kotlin
 * // The median of all last names in the "name" column group, in upper case,
 * // or `null` if the column group is empty
 * df.name.medianOfOrNull<_, String> { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.medianOfOrNull({ it.lastName.uppercase() })
 * ```
 *
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOfOrNull(
    crossinline expression: (T) -> R,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

/**
 * Returns the median of the numbers that the given [<code>expression</code>][expression] returns
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][DataColumn.medianBy], which returns the element the median
 * [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOfOrNull</code>][DataColumn.medianOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [<code>percentileOf</code>][DataColumn.percentileOf] — any other percentile of the values an expression returns.
 * - [<code>meanOf</code>][DataColumn.meanOf] — the average of the values an expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median length of all first names in the "name"/"firstName" column
 * df.name.firstName.medianOf { it.length }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

/**
 * Returns the median of the numbers that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the median of.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOfOrNull</code>][medianOfOrNull] with [<code>medianByOrNull</code>][DataColumn.medianByOrNull], which returns the element
 * the median [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOf</code>][DataColumn.medianOf] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [<code>percentileOfOrNull</code>][DataColumn.percentileOfOrNull] — any other percentile of the values
 *   an expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median length of all first names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.medianOfOrNull { it.length }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the median of.
 */
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
public fun DataRow<*>.rowMedianOrNull(): Nothing? = error(ROW_MEDIAN_OR_NULL)

@Deprecated(ROW_MEDIAN, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMedian(): Nothing = error(ROW_MEDIAN)

/**
 * Returns the median of the comparable values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow],
 * or `null` if there is nothing to compute the median of.
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>rowMedianOf</code>][DataRow.rowMedianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — the median of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The median of all `String` values ("name"/"firstName", "name"/"lastName", and "city") in the first row,
 * // or `null` if there are none
 * df[0].rowMedianOfOrNull<String>()
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @return The median of the values of type [<code>T</code>][T] in this row,
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMedianOfOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the median of the comparable values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>rowMedianOfOrNull</code>][DataRow.rowMedianOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the median of.
 * - [<code>median</code>][DataFrame.median] — the median of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The median of all `String` values ("name"/"firstName", "name"/"lastName", and "city") in the first row
 * df[0].rowMedianOf<String>()
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @return The median of the values of type [<code>T</code>][T] in this row.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMedianOf(): T =
    rowMedianOfOrNull<T>().suggestIfNull("rowMedianOf")

/**
 * Returns the median of the numbers of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the median of.
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>rowMedianOf</code>][DataRow.rowMedianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — the median of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The median of all `Int` values ("age" and "weight") in the first row, or `null` if there are none
 * df[0].rowMedianOfOrNull<Int>(skipNaN = false)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <reified T> DataRow<*>.rowMedianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.medianNumbers<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the median of the numbers of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * See also:
 * - [<code>rowMedianOfOrNull</code>][DataRow.rowMedianOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the median of.
 * - [<code>rowMean</code>][DataRow.rowMean] — the average of all the numbers in this row.
 * - [<code>median</code>][DataFrame.median] — the median of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The median of all `Int` values ("age" and "weight") in the first row
 * df[0].rowMedianOf<Int>(skipNaN = false)
 * // The median of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowMedianOf<Double>(skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <reified T> DataRow<*>.rowMedianOf(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number = rowMedianOfOrNull<T>(skipNaN).suggestIfNull("rowMedianOf")

// endregion

// region DataFrame

/**
 * Returns the median of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * See also:
 * - [<code>medianFor</code>][DataFrame.medianFor] — the same, but for an explicit selection of columns.
 * - [<code>median</code>][DataFrame.median]` { columns }` — a single median of all values in the selected columns.
 * - [<code>percentile</code>][DataFrame.percentile] — any other percentile of each column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the median value of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.median()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Median0")
public fun <T> DataFrame<T>.median(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(skipNaN, intraComparableColumns())

/**
 *
 *
 * Returns the median of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.median]` { columns }` — a single median of all values in the selected columns.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentileFor] — any other percentile of each selected column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the median "age" and the median "weight"
 * df.medianFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.medianFor(skipNaN = true) { age into "medianAge" and (weight into "medianWeight") }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the median of.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each selected column.
 */
@Refine
@Interpretable("Median1")
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Returns the median of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.median]` { columns }` — a single median of all values in the selected columns.
 * - [<code>`percentileFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.percentileFor] — any other percentile of each selected column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the median "age" and the median "weight"
 * df.medianFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Median1", stringArgument = "columns", targetArgument = "columns")
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

/**
 * Returns a single median of all the comparable values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [<code>median</code>][DataFrame.median] overload with a `skipNaN` parameter is used,
 * which returns an interpolated [<code>Double</code>][Double].
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>medianOf</code>][DataFrame.medianOf] — the median of the values a row expression returns for each row.
 * - [<code>percentile</code>][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Examples
 * ```kotlin
 * // The median of all first and last names in the "name" column group
 * df.median<_, String> { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.median({ name.firstName and name.lastName })
 * ```
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return The median of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(columns: ColumnsSelector<T, C>): C & Any =
    medianOrNull(columns).suggestIfNull("median")

/**
 * Returns a single median of all the comparable values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [<code>medianOrNull</code>][DataFrame.medianOrNull] overload with a `skipNaN`
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>median</code>][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — the median of the values a row expression
 *   returns for each row.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Examples
 * ```kotlin
 * // The median of all first and last names in the "name" column group,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull<_, String> { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOrNull({ name.firstName and name.lastName })
 * ```
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return The median of all the values in the selected columns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(columns: ColumnsSelector<T, C>): C? =
    Aggregators.medianComparables<C>().aggregateAll(this, columns)

/**
 * Returns a single median of all the numbers in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double].
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>medianOf</code>][DataFrame.medianOf] — the median of the values a row expression returns for each row.
 * - [<code>percentile</code>][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns
 * df.median { age and weight }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return The median of all the values in the selected columns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? = medianOrNull(skipNaN, columns).suggestIfNull("median")

/**
 * Returns a single median of all the numbers in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double],
 * or `null` if there is nothing to compute the median of.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>median</code>][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — the median of the values a row expression
 *   returns for each row.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull { age and weight }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return The median of all the values in the selected columns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.medianNumbers<C>(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single median of all the values in the columns of this [<code>DataFrame</code>][DataFrame] with the given names.
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>percentile</code>][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>median</code>][org.jetbrains.kotlinx.dataframe.api.median]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns, as a `Double`
 * df.median("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the median of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T> DataFrame<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Any =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

/**
 * Returns a single median of all the values in the columns of this [<code>DataFrame</code>][DataFrame] with the given names,
 * or `null` if there is nothing to compute the median of.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>median</code>][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianFor</code>][DataFrame.medianFor] — the median of each selected column separately.
 * - [<code>percentileOrNull</code>][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
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
 * <code>`df`</code>`.`[<code>medianOrNull</code>][org.jetbrains.kotlinx.dataframe.api.medianOrNull]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns, as a `Double`,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the median of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The median of all the values in the selected columns,
 *   or `null` if there are no values to compute the median of.
 */
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

/**
 * Returns the median of the comparable values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>medianOf</code>][DataFrame.medianOf] overload
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][DataFrame.medianBy], which returns the row the median
 * [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>median</code>][DataFrame.median] — a single median of all values in the selected columns.
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — any other percentile of the values a row expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Examples
 * ```kotlin
 * // The median of the full names of all rows
 * df.medianOf<_, String> { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOf({ name.firstName + " " + name.lastName })
 * ```
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The median of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOf(
    crossinline expression: RowExpression<T, R>,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

/**
 * Returns the median of the comparable values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] overload
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOfOrNull</code>][medianOfOrNull] with [<code>medianByOrNull</code>][DataFrame.medianByOrNull], which returns the row the
 * median [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOf</code>][DataFrame.medianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — a single median of all values in the selected columns.
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — any other percentile of the values
 *   a row expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Examples
 * ```kotlin
 * // The median of the full names of all rows, or `null` if this dataframe is empty
 * df.medianOfOrNull<_, String> { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOfOrNull({ name.firstName + " " + name.lastName })
 * ```
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The median of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOfOrNull(
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

/**
 * Returns the median of the numbers that the given [<code>expression</code>][expression] returns
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
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][DataFrame.medianBy], which returns the row the median
 * [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>median</code>][DataFrame.median] — a single median of all values in the selected columns.
 * - [<code>percentileOf</code>][DataFrame.percentileOf] — any other percentile of the values a row expression returns.
 * - [<code>meanOf</code>][DataFrame.meanOf] — the average of the values a row expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median weight-to-age ratio of all rows
 * df.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The median of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

/**
 * Returns the median of the numbers that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double], or `null` if there is nothing to compute the median of.
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
 * Returns `null` when there is nothing to compute the median of,
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 * Don't confuse [<code>medianOfOrNull</code>][medianOfOrNull] with [<code>medianByOrNull</code>][DataFrame.medianByOrNull], which returns the row the
 * median [<code>expression</code>][expression] value belongs to instead of that value.
 *
 * See also:
 * - [<code>medianOf</code>][DataFrame.medianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>medianOrNull</code>][DataFrame.medianOrNull] — a single median of all values in the selected columns.
 * - [<code>percentileOfOrNull</code>][DataFrame.percentileOfOrNull] — any other percentile of the values
 *   a row expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The median weight-to-age ratio of all rows, or `null` if this dataframe is empty
 * df.medianOfOrNull { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The median of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double],
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.medianNumbers<R>(skipNaN).aggregateOf(this, expression)

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the median of the values that the given [<code>expression</code>][expression]
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>medianOf</code>][DataFrame.medianOf], which returns the median [<code>expression</code>][expression] value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianByOrNull</code>][DataFrame.medianByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>percentileBy</code>][DataFrame.percentileBy] — the row at any other percentile of the values
 *   a row expression returns.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just the middle one.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age"
 * df.medianBy { age }
 * // The row with the median weight-to-age ratio
 * df.medianBy { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return The [<code>DataRow</code>][DataRow] at the median of the values [<code>expression</code>][expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = medianByOrNull(skipNaN, expression).suggestIfNull("medianBy")

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the median of the values in the column with the given name.
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Throws a [<code>NoSuchElementException</code>][NoSuchElementException] when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>median</code>][DataFrame.median], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianByOrNull</code>][DataFrame.medianByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [<code>percentileBy</code>][DataFrame.percentileBy] — the row at any other percentile of the values in a column.
 * - [<code>sortBy</code>][DataFrame.sortBy] — orders all rows instead of taking just the middle one.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age"
 * df.medianBy("age")
 * ```
 *
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The [<code>DataRow</code>][DataRow] at the median of the values in the given column.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
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

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the median of the values that the given [<code>expression</code>][expression]
 * returns for each row, or `null` if there is nothing to compute the median of.
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianByOrNull</code>][medianByOrNull] with [<code>medianOfOrNull</code>][DataFrame.medianOfOrNull], which returns the median
 * [<code>expression</code>][expression] value itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianBy</code>][DataFrame.medianBy] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull] — the row at any other percentile of the values
 *   a row expression returns.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age", or `null` if this dataframe is empty
 * df.medianByOrNull { age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return The [<code>DataRow</code>][DataRow] at the median of the values [<code>expression</code>][expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.medianCommon<C>(skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the row of this [<code>DataFrame</code>][DataFrame] at the median of the values in the column with the given name,
 * or `null` if there is nothing to compute the median of.
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 *
 * Returns `null` when there is nothing to compute the median of,
 * for instance, when the input is empty or contains only `null` values.
 *
 * Don't confuse [<code>medianByOrNull</code>][medianByOrNull] with [<code>medianOrNull</code>][DataFrame.medianOrNull], which returns the median
 * value itself instead of the row it belongs to.
 *
 * See also:
 * - [<code>medianBy</code>][DataFrame.medianBy] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [<code>percentileByOrNull</code>][DataFrame.percentileByOrNull] — the row at any other percentile of the values
 *   in a column.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age", or `null` if this dataframe is empty
 * df.medianByOrNull("age")
 * ```
 *
 * @param [column] The name of the column of this [<code>DataFrame</code>][DataFrame] to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The [<code>DataRow</code>][DataRow] at the median of the values in the given column,
 *   or `null` if there are no values to compute the median of.
 */
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

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the median for each suitable column.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * See also:
 * - [<code>medianFor</code>][Grouped.medianFor] — the same, but for an explicit selection of columns.
 * - [<code>median</code>][Grouped.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>percentile</code>][Grouped.percentile] — any other percentile of each column, per group.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median value of each comparable column
 * df.groupBy { city }.median()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the median of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMedian1")
public fun <T> Grouped<T>.median(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the median for each selected column.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.groupBy { city }.medianFor { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the median of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the median of each selected column per group.
 */
@Refine
@Interpretable("GroupByMedian0")
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the median for each selected column.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.groupBy { city }.medianFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the median of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMedian0", stringArgument = "columns", targetArgument = "columns")
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

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the median per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"median"` otherwise.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions].
 *
 * See also:
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.medianFor] — the median of each selected column separately, per group.
 * - [<code>`medianOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.medianOf] — the median of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns,
 * // in a column called "medianValue"
 * df.groupBy { city }.median("medianValue") { age and weight }
 * ```
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"median"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single median per group.
 */
@Refine
@Interpretable("GroupByMedian2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, name, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the median per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"median"` otherwise.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions].
 *
 * See also:
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.medianFor] — the median of each selected column separately, per group.
 * - [<code>`medianOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.medianOf] — the median of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns,
 * // in a column called "medianValue"
 * df.groupBy { city }.median("age", "weight", name = "medianValue")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"median"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single median per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMedian2", stringArgument = "columns", targetArgument = "columns")
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

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the median of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the median per group, named [<code>name</code>][name] (or `"median"` if [<code>name</code>][name] is `null`).
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the median of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `null`.
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][GroupBy.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * See also:
 * - [<code>median</code>][Grouped.median] — a single median of all values in the selected columns, per group.
 * - [<code>percentileOf</code>][Grouped.percentileOf] — any other percentile of the values a row expression returns,
 *   per group.
 * - [<code>aggregate</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median weight-to-age ratio, in a column called "medianRatio"
 * df.groupBy { city }.medianOf("medianRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), `"median"` is used.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single median per group.
 */
@Refine
@Interpretable("GroupByMedianOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.medianOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the row at the median of the values that the given
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>medianOf</code>][Grouped.medianOf], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileBy</code>][GroupBy.percentileBy] — the row at any other percentile, per group.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the median "age"
 * df.groupBy { city }.medianBy { age }.concat()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the row at the median of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * Reduces each group of this [<code>GroupBy</code>][GroupBy] to the row at the median of the values
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>median</code>][Grouped.median], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [<code>percentileBy</code>][GroupBy.percentileBy] — the row at any other percentile, per group.
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the median "age"
 * df.groupBy { city }.medianBy("age").concat()
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] with, for each group, the row at the median of the values in the given column.
 */
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

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the median
 * of each suitable column of the corresponding group.
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>medianFor</code>][Pivot.medianFor] — the same, but for an explicit selection of columns.
 * - [<code>median</code>][Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median value of each comparable column
 * df.pivot { city }.median()
 * ```
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each suitable column per [<code>pivot</code>][pivot] group.
 */
public fun <T> Pivot<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the median
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.pivot { city }.medianFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.medianFor(separate = true) { age and weight }
 * ```
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the median of.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each selected column per [<code>pivot</code>][pivot] group.
 */
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { medianFor(separate, skipNaN, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the median
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.pivot { city }.medianFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the median of each selected column per [<code>pivot</code>][pivot] group.
 */
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

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the median
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]`()` — the median of each suitable column separately, per group.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianFor] — the median of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.median { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the median of all the values in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { median(skipNaN, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the median
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.median]`()` — the median of each suitable column separately, per group.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianFor] — the median of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.median("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the median of all the values in the selected columns.
 */
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

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the median of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the median
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][Pivot.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>median</code>][Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the median weight-to-age ratio
 * df.pivot { city }.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the median of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { medianOf(skipNaN, expression) }

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the [<code>row</code>][DataRow]
 * at the median of the values that the given [<code>rowExpression</code>][rowExpression] returns for each row of that group.
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>medianOf</code>][Pivot.medianOf], which returns the median value the expression
 * returns itself, instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>percentileBy</code>][Pivot.percentileBy] — the row at any other percentile, per group.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the median weight-to-age ratio
 * df.pivot { city }.medianBy { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group,
 *   the row at the median of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the [<code>row</code>][DataRow]
 * at the median of the values in the given [<code>column</code>][column].
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.with].
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>median</code>][Pivot.median], which returns the median value itself,
 * instead of the row.
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>percentileBy</code>][Pivot.percentileBy] — the row at any other percentile, per group.
 * - [<code>Pivot reducing</code>][PivotDocs.Reducing] — all other ways to reduce a [<code>Pivot</code>][Pivot].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the median "age"
 * df.pivot { city }.medianBy("age").with { name }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivot</code>][ReducedPivot] holding, per group, the row at the median of the values in the given column.
 */
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

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the median of each suitable column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 * All columns whose values are mutually comparable are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the median, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>medianFor</code>][PivotGroupBy.medianFor] — the same, but for an explicit selection of columns.
 * - [<code>median</code>][PivotGroupBy.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median value of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.median()
 * ```
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the median of each suitable column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
public fun <T> PivotGroupBy<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the median of each selected column
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]` { columns }` — a single median of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median "age" and the median "weight"
 * df.pivot { city }.groupBy { name.lastName }.medianFor { age and weight }
 * ```
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the median of.
 * @return A [<code>DataFrame</code>][DataFrame] with the median of each selected column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, separate, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the median of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the median of each selected column
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]`()` — the same, but for all suitable columns at once.
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]` { columns }` — a single median of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median "age" and the median "weight"
 * df.pivot { city }.groupBy { name.lastName }.medianFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the median of each selected column per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group.
 */
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

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the median of all the values in the
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]`()` — the median of each suitable column separately, per group.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.medianFor] — the median of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.median { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the median of.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the median of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single median of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the median of all the values in the
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs.MedianSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`median`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.median]`()` — the median of each suitable column separately, per group.
 * - [<code>`medianFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.medianFor] — the median of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.median("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the median of.
 *   The values in these columns must be mutually comparable, else an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the median of all the values
 *   in the selected columns.
 */
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

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the median of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the median of the expression's results for the rows
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
 * For more information about the resulting types: [See "`median` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/median.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * median of (for instance, because the group was empty or contained only `null` values)
 * simply become `null`.
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Don't confuse [<code>medianOf</code>][medianOf] with [<code>medianBy</code>][PivotGroupBy.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>median</code>][PivotGroupBy.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A [<code>DataFrame</code>][DataFrame] with, per [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group, the median of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, expression)

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the [<code>row</code>][DataRow] at the median of the values that the given [<code>rowExpression</code>][rowExpression] returns
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>medianOf</code>][PivotGroupBy.medianOf], which returns the median value the
 * expression returns itself, instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>percentileBy</code>][PivotGroupBy.percentileBy] — the row at any other percentile, per group.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the median weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.medianBy { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [rowExpression] The [<code>RowExpression</code>][RowExpression] to compute the value to compare the rows by.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group,
 *   the row at the median of the values [<code>rowExpression</code>][rowExpression] returns.
 */
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

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking from each group
 * the [<code>row</code>][DataRow] at the median of the values in the given [<code>column</code>][column].
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
 * Which value lies at the median is determined using
 * [<code>quantile estimation method</code>][org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs.QuantileEstimationMethods] R3, so no interpolation takes place, not even for numbers.
 * If [skipNaN] is `false` and [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are encountered,
 * the first one with a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] value is selected.
 *
 *
 * Currently, only the default quantile estimation methods can be used.
 * In the future, it might become possible to choose a different one, see #1121.
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 *
 *
 * This operation does not produce a result right away.
 * Instead, it returns a [<code>ReducedPivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy] — an intermediate step which can be finished with
 * [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.values] or [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy.with].
 *
 * Don't confuse [<code>medianBy</code>][medianBy] with [<code>median</code>][PivotGroupBy.median], which returns the median value itself,
 * instead of the row.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>percentileBy</code>][PivotGroupBy.percentileBy] — the row at any other percentile, per group.
 * - [<code>PivotGroupBy reducing</code>][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Median Operation</code>][org.jetbrains.kotlinx.dataframe.api.MedianDocs] — an overview of all `median` modes.
 *
 * For more information: [See `median` on the documentation website.](https://kotlin.github.io/dataframe/median.html)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the median "age"
 * df.pivot { city }.groupBy { name.lastName }.medianBy("age").with { name.firstName }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] holding, per group, the row at the median of the values in the given column.
 */
public fun <T> PivotGroupBy<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = medianBy(column.toColumnAccessor(), skipNaN)

// endregion
