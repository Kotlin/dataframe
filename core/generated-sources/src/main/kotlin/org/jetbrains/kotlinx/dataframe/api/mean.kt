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
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_VERB
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
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

// region docs

/**
 *
 *
 * ## The Mean Operation
 *
 * Computes the [mean (average)](https://en.wikipedia.org/wiki/Arithmetic_mean) of values —
 * the [<code>sum</code>][DataFrame.sum] divided by the number of values.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * ### Mean Modes
 *
 * Depending on what exactly you want the mean of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>`mean`</code>][DataFrame.mean]`()` — the mean of each suitable column separately.
 * - [<code>`mean`</code>][DataFrame.mean]` { columns }` — a single mean of all values in all selected columns.
 * - [<code>`meanFor`</code>][DataFrame.meanFor]` { columns }` — the mean of each selected column separately.
 * - [<code>`meanOf`</code>][DataFrame.meanOf]` { expression }` — the mean of the values that the given expression
 *   returns for each row.
 *
 * Related operation:
 * - [<code>`sum`</code>][DataFrame.sum] — the sum of values (mean is the sum divided by the number of values).
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * For more information about [<code>unifying numbers</code>][UnifyingNumbers]:
 * [See "Number Unification" on the documentation website.](https://kotlin.github.io/dataframe/numberunification.html)
 *
 * See all summary statistics:
 * [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface MeanDocs : CommonStatisticsDocs {

    /**
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
     * <code>`df`</code>`.`[<code>mean</code>][org.jetbrains.kotlinx.dataframe.api.mean]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>mean</code>][org.jetbrains.kotlinx.dataframe.api.mean]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>mean</code>][org.jetbrains.kotlinx.dataframe.api.mean]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>mean</code>][org.jetbrains.kotlinx.dataframe.api.mean]`("length", "age")`
     *
     *
     *
     */
    typealias MeanSelectingOptions = Nothing

    /**
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
     * <code>`df`</code>`.`[<code>meanFor</code>][org.jetbrains.kotlinx.dataframe.api.meanFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>meanFor</code>][org.jetbrains.kotlinx.dataframe.api.meanFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>meanFor</code>][org.jetbrains.kotlinx.dataframe.api.meanFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>meanFor</code>][org.jetbrains.kotlinx.dataframe.api.meanFor]`("length", "age")`
     *
     *
     *
     */
    typealias MeanForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the mean of the values in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.meanOf] — the mean of the values an expression returns for each element.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sum] — the sum of the values in this column.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information:
 * [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // The mean of all ages in the "age" Int column
 * df.age.mean()
 * // The mean of all weights in the "weight" `Double?` column, ignoring `null` values
 * df.weight.mean()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The mean of the values in this column, as a [<code>Double</code>][Double].
 */
public fun DataColumn<Number?>.mean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the mean of the values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
 *
 *
 *
 *
 *
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean] — the mean of the values in this column itself.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sumOf] — the sum of those values.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information:
 * [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // The mean length of all first names in the "name"/"firstName" column
 * df.name.firstName.meanOf { it.length }
 * ```
 *
 * @param [expression] A function that returns the value to average for each element of this column.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The mean of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 */
public inline fun <T, reified R : Number?> DataColumn<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

/**
 * Returns the mean of all the numbers in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [<code>Number</code>][Number] columns)
 * are taken into account; all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 * Since the values of different columns are averaged together, the result is the mean of all those
 * values converted to their common type.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`rowMeanOf<Type>()`</code>][DataRow.rowMeanOf] — the mean of the values of one specific number type in this row.
 * - [<code>`rowSum`</code>][DataRow.rowSum] — the sum of all the numbers in this row.
 * - [<code>`mean`</code>][DataFrame.mean] — the mean of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The mean of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowMean()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The mean of all the numbers in this row, as a [<code>Double</code>][Double].
 */
public fun DataRow<*>.rowMean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the mean of the values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 * [<code>T</code>][T] must be a primitive number type or [<code>Number</code>][Number] itself.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`rowMean`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowMean] — the mean of all the numbers in this row, of any number type.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean] — the mean of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The mean of all `Int` values ("age" and "weight") in the first row
 * df[0].rowMeanOf<Int>()
 * // The mean of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowMeanOf<Double>(skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to average.
 *   Only columns of this type are taken into account.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The mean of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double].
 * @throws IllegalArgumentException if [<code>T</code>][T] is not a primitive number type or [<code>Number</code>][Number] itself.
 */
public inline fun <reified T : Number> DataRow<*>.rowMeanOf(skipNaN: Boolean = skipNaNDefault): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.mean(skipNaN).aggregateOfRow(this) { colsOf<T?>() }
}

// endregion

// region DataFrame

/**
 * Returns the mean of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * See also:
 * - [<code>`meanFor`</code>][DataFrame.meanFor]` { columns }` — the same, but for an explicit selection of columns.
 * - [<code>`mean`</code>][DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
 * - [<code>`sum`</code>][DataFrame.sum] — the sum of each column.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the mean of each number column ("age" and "weight")
 * df.mean()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Mean0")
public fun <T> DataFrame<T>.mean(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Returns the mean of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the mean of the "age" values and the mean of the "weight" values
 * df.meanFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.meanFor(skipNaN = true) { age into "meanAge" and (weight into "meanWeight") }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the mean of.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each selected column.
 */
@Refine
@Interpretable("Mean1")
public fun <T, C : Number?> DataFrame<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Returns the mean of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the mean of the "age" values and the mean of the "weight" values
 * df.meanFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Mean1", stringArgument = "columns", targetArgument = "columns")
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

/**
 * Returns a single mean of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the mean of each selected column separately.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanOf] — the mean of the values a row expression returns for each row.
 * - [<code>`sum`</code>][DataFrame.sum] — the sum of all values in the selected columns.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
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
 * ### Example
 * ```kotlin
 * // The mean of all values in the "age" and "weight" columns together
 * df.mean { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the mean of.
 * @return The mean of all the values in the selected columns, as a [<code>Double</code>][Double].
 */
public fun <T, C : Number?> DataFrame<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double = Aggregators.mean(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single mean of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame], as a [<code>Double</code>][Double].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the mean of each selected column separately.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanOf] — the mean of the values a row expression returns for each row.
 * - [<code>`sum`</code>][DataFrame.sum] — the sum of all values in the selected columns.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // The mean of all values in the "age" and "weight" columns together
 * df.mean("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The mean of all the values in the selected columns, as a [<code>Double</code>][Double].
 */
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

/**
 * Returns the mean of the values that the given [<code>expression</code>][expression] returns
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
 * the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
 * - [<code>`sumOf`</code>][DataFrame.sumOf] — the sum of those values.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 * For more information: [See `mean` on the documentation website.](https://kotlin.github.io/dataframe/mean.html)
 *
 * ### Example
 * ```kotlin
 * // The mean of the weight-to-age ratios of all rows
 * df.meanOf { (weight ?: 0) / age }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to average
 *   for each row.
 * @return The mean of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 */
public inline fun <T, reified D : Number?> DataFrame<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, D>,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region GroupBy

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the mean for each suitable column.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * See also:
 * - [<code>`meanFor`</code>][Grouped.meanFor] — the same, but for an explicit selection of columns.
 * - [<code>`mean`</code>][Grouped.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>`sum`</code>][Grouped.sum] — the sum of each column, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of each number column ("age" and "weight")
 * df.groupBy { city }.mean()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the mean of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMean1")
public fun <T> Grouped<T>.mean(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the mean for each selected column.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumFor] — the sum of each selected column, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.groupBy { city }.meanFor { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the mean of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the mean of each selected column per group.
 */
@Refine
@Interpretable("GroupByMean0")
public fun <T, C : Number?> Grouped<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the mean for each selected column.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumFor] — the sum of each selected column, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.groupBy { city }.meanFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the mean of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMean0", stringArgument = "columns", targetArgument = "columns")
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

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single mean of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the mean per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"mean"` otherwise.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanSelectingOptions].
 *
 * See also:
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the mean of each selected column separately, per group.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanOf] — the mean of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns,
 * // in a column called "average"
 * df.groupBy { city }.mean("average") { age and weight }
 * ```
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"mean"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the mean of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single mean per group.
 */
@Refine
@Interpretable("GroupByMean2")
public fun <T, C : Number?> Grouped<T>.mean(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, name, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single mean of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the mean per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"mean"` otherwise.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanSelectingOptions].
 *
 * See also:
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the mean of each selected column separately, per group.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanOf] — the mean of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns,
 * // in a column called "average"
 * df.groupBy { city }.mean("age", "weight", name = "average")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"mean"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single mean per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMean2", stringArgument = "columns", targetArgument = "columns")
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

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the mean of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the mean per group, named [<code>name</code>][name] (or `"mean"` if [<code>name</code>][name] is `null`).
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * See also:
 * - [<code>`mean`</code>][Grouped.mean] — a single mean of all values in the selected columns, per group.
 * - [<code>`sumOf`</code>][Grouped.sumOf] — the sum of those values, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the weight-to-age ratios, in a column called "meanRatio"
 * df.groupBy { city }.meanOf("meanRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), `"mean"` is used.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to average
 *   for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single mean per group.
 */
@Refine
@Interpretable("GroupByMeanOf")
public inline fun <T, reified R : Number?> Grouped<T>.meanOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateOf(this, name, expression)

// endregion

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the mean
 * of each suitable column of the corresponding group.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`meanFor`</code>][Pivot.meanFor] — the same, but for an explicit selection of columns.
 * - [<code>`mean`</code>][Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of each number column ("age" and "weight")
 * df.pivot { city }.mean()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each suitable column per [<code>pivot</code>][pivot] group.
 */
public fun <T> Pivot<T>.mean(skipNaN: Boolean = skipNaNDefault, separate: Boolean = false): DataRow<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the mean
 * of each selected column of the corresponding group.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.meanFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.meanFor(separate = true) { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the mean of.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each selected column per [<code>pivot</code>][pivot] group.
 */
public fun <T, C : Number?> Pivot<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { meanFor(skipNaN, separate, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the mean
 * of each selected column of the corresponding group.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.meanFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A single [<code>DataRow</code>][DataRow] with the mean of each selected column per [<code>pivot</code>][pivot] group.
 */
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

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single mean of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the mean of all
 * the values in the selected columns of the corresponding group.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.mean]`()` — the mean of each suitable column separately, per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.meanFor] — the mean of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.mean { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the mean of.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the mean of all the values
 *   in the selected columns.
 */
public fun <T, R : Number?> Pivot<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { mean(skipNaN, columns) }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the mean of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the mean
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to average
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the weight-to-age ratios
 * df.pivot { city }.meanOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to average
 *   for each row.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the mean of the expression's results.
 */
public inline fun <T, reified R : Number?> Pivot<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { meanOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the mean of each suitable column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * Columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are also excluded.
 * To include those in the mean, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`meanFor`</code>][PivotGroupBy.meanFor] — the same, but for an explicit selection of columns.
 * - [<code>`mean`</code>][PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.mean()
 * ```
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the mean of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the mean of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.meanFor { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the mean of.
 * @return A [<code>DataFrame</code>][DataFrame] with the mean of each selected column per group.
 */
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, separate, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the mean of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the mean of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]`()` — the same, but for all suitable columns at once.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.meanFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with the mean of each selected column per group.
 */
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

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single mean of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the mean of all the values in the selected columns
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]`()` — the mean of each suitable column separately, per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.meanFor] — the mean of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.mean { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the mean of.
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the mean of all the values in the selected columns.
 */
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single mean of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the mean of all the values in the selected columns
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs.MeanSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.mean]`()` — the mean of each suitable column separately, per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.meanFor] — the mean of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.mean("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the mean of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the mean of all the values in the selected columns.
 */
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

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the mean of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the mean of the expression's results for the
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
 * The result of the expression is considered the 'input' of this operation.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using
 * [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is always a [<code>Double</code>][Double] and never `null`.
 * Converting [<code>Long</code>][Long] values to [<code>Double</code>][Double] may lose precision for very large values.
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to average
 * (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`mean` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/mean.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`mean`</code>][PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Mean Operation</code>][org.jetbrains.kotlinx.dataframe.api.MeanDocs] — an overview of all `mean` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.meanOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to average
 *   for each row.
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the mean of the expression's results.
 */
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
public fun DataRow<*>.rowMean(): Double = rowMean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Number> DataRow<*>.rowMeanOf(): Double = rowMeanOf<T>(skipNaN = skipNaNDefault)

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
