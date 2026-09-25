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
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region docs

/**
 *
 *
 * ## The Std Operation
 *
 * Computes the [standard deviation](https://en.wikipedia.org/wiki/Standard_deviation) of values —
 * a measure of how spread out the values are around their [<code>mean</code>][DataFrame.mean].
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * ### Std Modes
 *
 * Depending on what exactly you want the standard deviation of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>`std`</code>][DataFrame.std]`()` — the standard deviation of each suitable column separately.
 * - [<code>`std`</code>][DataFrame.std]` { columns }` — a single standard deviation of all values in all selected columns.
 * - [<code>`stdFor`</code>][DataFrame.stdFor]` { columns }` — the standard deviation of each selected column separately.
 * - [<code>`stdOf`</code>][DataFrame.stdOf]` { expression }` — the standard deviation of the values that the given
 *   expression returns for each row.
 *
 * ### Delta Degrees of Freedom (ddof)
 *
 * All `std` operations take a [<code>`ddof`</code>][DdofParam] ("Delta Degrees of Freedom") argument.
 * The divisor used in the calculation is `N - ddof`, where `N` is the number of values.
 * The default is `1`, meaning DataFrame applies
 * [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 * "unbiased sample standard deviation" by default (as is standard in languages like R).
 * This is different from the "population standard deviation" (`ddof = 0`), which is the default
 * in libraries like Numpy.
 *
 * Related operation:
 * - [<code>`mean`</code>][DataFrame.mean] — the average of values (the standard deviation measures the spread around it).
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * For more information about [<code>unifying numbers</code>][UnifyingNumbers]:
 * [See "Number Unification" on the documentation website.](https://kotlin.github.io/dataframe/numberunification.html)
 *
 * See all summary statistics:
 * [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface StdDocs : CommonStatisticsDocs {

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
     * <code>`df`</code>`.`[<code>std</code>][org.jetbrains.kotlinx.dataframe.api.std]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>std</code>][org.jetbrains.kotlinx.dataframe.api.std]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>std</code>][org.jetbrains.kotlinx.dataframe.api.std]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>std</code>][org.jetbrains.kotlinx.dataframe.api.std]`("length", "age")`
     *
     *
     *
     */
    typealias StdSelectingOptions = Nothing

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
     * <code>`df`</code>`.`[<code>stdFor</code>][org.jetbrains.kotlinx.dataframe.api.stdFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>stdFor</code>][org.jetbrains.kotlinx.dataframe.api.stdFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>stdFor</code>][org.jetbrains.kotlinx.dataframe.api.stdFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>stdFor</code>][org.jetbrains.kotlinx.dataframe.api.stdFor]`("length", "age")`
     *
     *
     *
     */
    typealias StdForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the standard deviation of the values in this [<code>DataColumn</code>][DataColumn], as a [<code>Double</code>][Double].
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`stdOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.stdOf] — the standard deviation of the values an expression returns for each element.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean] — the mean of the values in this column.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information:
 * [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of all ages in the "age" Int column
 * df.age.std()
 * // The population standard deviation of all weights in the "weight" `Double?` column
 * df.weight.std(ddof = 0)
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return The standard deviation of the values in this column, as a [<code>Double</code>][Double].
 */
public fun DataColumn<Number?>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateSingleColumn(this)

/**
 * Returns the standard deviation of the values that the given [<code>expression</code>][expression] returns
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.std] — the standard deviation of the values in this column itself.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.meanOf] — the mean of those values.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information:
 * [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of the lengths of all first names in the "name"/"firstName" column
 * df.name.firstName.stdOf { it.length }
 * ```
 *
 * @param [expression] A function that returns the value to include for each element of this column.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return The standard deviation of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 */
public inline fun <T, reified R : Number?> DataColumn<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    noinline expression: (T) -> R,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region DataRow

/**
 * Returns the standard deviation of all the numbers in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [<code>Number</code>][Number] columns)
 * are taken into account; all other columns of the row are ignored.
 *
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
 *
 * Since the values of different columns are combined together, the result is the standard deviation
 * of all those values converted to their common type.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`rowStdOf<Type>()`</code>][DataRow.rowStdOf] — the standard deviation of the values of one specific number type in this row.
 * - [<code>`rowMean`</code>][DataRow.rowMean] — the mean of all the numbers in this row.
 * - [<code>`std`</code>][DataFrame.std] — the standard deviation of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowStd()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return The standard deviation of all the numbers in this row, as a [<code>Double</code>][Double].
 */
public fun DataRow<*>.rowStd(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the standard deviation of the values of type [<code>T</code>][T] in this [<code>DataRow</code>][DataRow], as a [<code>Double</code>][Double].
 *
 * Only the values in the columns of type [<code>T</code>][T] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 *
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`rowStd`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowStd] — the standard deviation of all the numbers in this row, of any number type.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std] — the standard deviation of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of all `Int` values ("age" and "weight") in the first row
 * df[0].rowStdOf<Int>()
 * // The population standard deviation of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowStdOf<Double>(skipNaN = true, ddof = 0)
 * ```
 *
 * @param [T] The type of the values to include.
 *   Only columns of this type are taken into account.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return The standard deviation of the values of type [<code>T</code>][T] in this row, as a [<code>Double</code>][Double].
 * @throws IllegalArgumentException if [<code>T</code>][T] is not a primitive number type or [<code>Number</code>][Number] itself.
 */
public inline fun <reified T : Number?> DataRow<*>.rowStdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Std only supports primitive number types."
    }
    return Aggregators.std(skipNaN, ddof).aggregateOfRow(this) { colsOf<T>() }
}

// endregion

// region DataFrame

/**
 * Returns the standard deviation of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * See also:
 * - [<code>`stdFor`</code>][DataFrame.stdFor] — the same, but for an explicit selection of columns.
 * - [<code>`std`</code>][DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
 * - [<code>`mean`</code>][DataFrame.mean] — the mean of each column.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the standard deviation of each number column ("age" and "weight")
 * df.std()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Std0")
public fun <T> DataFrame<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataRow<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Returns the standard deviation of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the mean of each selected column.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the standard deviation of the "age" values and of the "weight" values
 * df.stdFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.stdFor(skipNaN = true) { age into "stdAge" and (weight into "stdWeight") }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each selected column.
 */
@Refine
@Interpretable("Std1")
public fun <T, C : Number?> DataFrame<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

/**
 *
 *
 * Returns the standard deviation of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the mean of each selected column.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the standard deviation of the "age" values and of the "weight" values
 * df.stdFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Std1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Returns a single standard deviation of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * as a [<code>Double</code>][Double].
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.stdFor] — the standard deviation of each selected column separately.
 * - [<code>`stdOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.stdOf] — the standard deviation of the values a row expression returns for each row.
 * - [<code>`mean`</code>][DataFrame.mean] — the mean of all values in the selected columns.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
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
 * // The standard deviation of all values in the "age" and "weight" columns together
 * df.std { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return The standard deviation of all the values in the selected columns, as a [<code>Double</code>][Double].
 */
public fun <T> DataFrame<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, Number?>,
): Double = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: ColumnReference<C>): Double = std { columns.toColumnSet() }

/**
 * Returns a single standard deviation of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * as a [<code>Double</code>][Double].
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.stdFor] — the standard deviation of each selected column separately.
 * - [<code>`stdOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.stdOf] — the standard deviation of the values a row expression returns for each row.
 * - [<code>`mean`</code>][DataFrame.mean] — the mean of all values in the selected columns.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of all values in the "age" and "weight" columns together
 * df.std("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @return The standard deviation of all the values in the selected columns, as a [<code>Double</code>][Double].
 */
public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: KProperty<C>): Double = std { columns.toColumnSet() }

/**
 * Returns the standard deviation of the values that the given [<code>expression</code>][expression] returns
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * When there is nothing to compute the standard deviation of, for instance, when the input is empty
 * or contains only `null` values, the result is [<code>Double.NaN</code>][Double.NaN]
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
 * - [<code>`meanOf`</code>][DataFrame.meanOf] — the mean of those values.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 * For more information: [See `std` on the documentation website.](https://kotlin.github.io/dataframe/std.html)
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of the weight-to-age ratios of all rows
 * df.stdOf { (weight ?: 0) / age }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return The standard deviation of the values [<code>expression</code>][expression] returns, as a [<code>Double</code>][Double].
 */
public inline fun <T, reified R : Number?> DataFrame<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region GroupBy

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the standard deviation for each suitable column.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * See also:
 * - [<code>`stdFor`</code>][Grouped.stdFor] — the same, but for an explicit selection of columns.
 * - [<code>`std`</code>][Grouped.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [<code>`mean`</code>][Grouped.mean] — the mean of each column, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of each number column ("age" and "weight")
 * df.groupBy { city }.std()
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the standard deviation of each suitable column per group.
 */
@Refine
@Interpretable("GroupByStd1")
public fun <T> Grouped<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataFrame<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the standard deviation for each selected column.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.std]` { columns }` — a single standard deviation of all values in the selected columns,
 *   per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the mean of each selected column, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.groupBy { city }.stdFor { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the standard deviation of each selected column per group.
 */
@Refine
@Interpretable("GroupByStd0")
public fun <T, C : Number?> Grouped<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the standard deviation for each selected column.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.std]` { columns }` — a single standard deviation of all values in the selected columns,
 *   per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the mean of each selected column, per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.groupBy { city }.stdFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the standard deviation of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByStd0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the standard deviation per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"std"` otherwise.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions].
 *
 * See also:
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>`stdOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.stdOf] — the standard deviation of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns,
 * // in a column called "deviation"
 * df.groupBy { city }.std("deviation") { age and weight }
 * ```
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"std"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@Interpretable("GroupByStd2")
public fun <T, C : Number?> Grouped<T>.std(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, name, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns and
 * a single column with the standard deviation per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"std"` otherwise.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions].
 *
 * See also:
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>`stdOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.stdOf] — the standard deviation of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns,
 * // in a column called "deviation"
 * df.groupBy { city }.std("age", "weight", name = "deviation")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"std"` otherwise.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByStd2", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.std(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the standard deviation of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the standard deviation per group, named [<code>name</code>][name] (or `"std"` if [<code>name</code>][name] is `null`).
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * See also:
 * - [<code>`std`</code>][Grouped.std] — a single standard deviation of all values in the selected columns, per group.
 * - [<code>`meanOf`</code>][Grouped.meanOf] — the mean of those values, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics), and
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the weight-to-age ratios, in a column called "stdRatio"
 * df.groupBy { city }.stdOf("stdRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), `"std"` is used.
 *   This name needs to be unique, else a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@Interpretable("GroupByStdOf")
public inline fun <T, reified R : Number?> Grouped<T>.stdOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, name, expression)

// endregion

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the standard
 * deviation of each suitable column of the corresponding group.
 *
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 *
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`stdFor`</code>][Pivot.stdFor] — the same, but for an explicit selection of columns.
 * - [<code>`std`</code>][Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of each number column ("age" and "weight")
 * df.pivot { city }.std()
 * ```
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each suitable column per [<code>pivot</code>][pivot] group.
 */
public fun <T> Pivot<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the standard
 * deviation of each selected column of the corresponding group.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.stdFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.stdFor(separate = true) { age and weight }
 * ```
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each selected column per [<code>pivot</code>][pivot] group.
 */
public fun <T, R : Number?> Pivot<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { stdFor(separate, skipNaN, ddof, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the standard
 * deviation of each selected column of the corresponding group.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns,
 *   per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.stdFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A single [<code>DataRow</code>][DataRow] with the standard deviation of each selected column per [<code>pivot</code>][pivot] group.
 */
public fun <T> Pivot<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the standard
 * deviation of all the values in the selected columns of the corresponding group.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]`()` — the standard deviation of each suitable column separately, per group.
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.std { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the standard deviation of all the values
 *   in the selected columns.
 */
public fun <T, C : Number?> Pivot<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { std(skipNaN, ddof, columns) }

public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the standard
 * deviation of all the values in the selected columns of the corresponding group.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.std]`()` — the standard deviation of each suitable column separately, per group.
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.std("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the standard deviation of all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the standard deviation of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the standard
 * deviation of the expression's results for the rows of the corresponding group.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to compute the standard deviation of
 * (for instance, because the input was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the weight-to-age ratios
 * df.pivot { city }.stdOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A single [<code>DataRow</code>][DataRow] with, per [<code>pivot</code>][pivot] group, the standard deviation of the expression's results.
 */
public inline fun <reified T : Number?> Pivot<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, T>,
): DataRow<T> = delegate { stdOf(skipNaN, ddof, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the standard deviation of each suitable column
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
 * This includes columns inside [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * To include those in the standard deviation, [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] the DataFrame first.
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`stdFor`</code>][PivotGroupBy.stdFor] — the same, but for an explicit selection of columns.
 * - [<code>`std`</code>][PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.std()
 * ```
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A [<code>DataFrame</code>][DataFrame] with the standard deviation of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the standard deviation of each selected column
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
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
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.stdFor { age and weight }
 * ```
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A [<code>DataFrame</code>][DataFrame] with the standard deviation of each selected column per group.
 */
public fun <T, R : Number?> PivotGroupBy<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, separate, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the standard deviation of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the standard deviation of each selected column
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]`()` — the same, but for all suitable columns at once.
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.stdFor("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A [<code>DataFrame</code>][DataFrame] with the standard deviation of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the standard deviation of all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]`()` — the standard deviation of each suitable column separately, per group.
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.std { age and weight }
 * ```
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns
 *   to compute the standard deviation of.
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the standard deviation of all the values in the selected columns.
 */
public fun <T, C : Number?> PivotGroupBy<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing a single standard deviation of all the values
 * in the selected columns, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the standard deviation of all the values in the
 * selected columns of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs.StdSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.std]`()` — the standard deviation of each suitable column separately, per group.
 * - [<code>`stdFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.stdFor] — the standard deviation of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.std("age", "weight")
 * ```
 * @param [columns] The names of the columns to compute the standard deviation of.
 *   These must be primitive number columns, else an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the standard deviation of all the values in the selected columns.
 */
public fun <T> PivotGroupBy<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the standard deviation of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the standard deviation of the expression's results for the
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
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 * Result cells for which there exists a group, but there is nothing to compute the
 * standard deviation of (for instance, because the group was empty or contained only `null` values)
 * simply become [<code>Double.NaN</code>][Double.NaN].
 *
 * For more information about the resulting types:
 * [See "`std` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/std.html#type-conversion)
 *
 *
 *
 * For empty pivot intersections, `null` or the [<code>set default</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.default] are used.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`std`</code>][PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Std Operation</code>][org.jetbrains.kotlinx.dataframe.api.StdDocs] — an overview of all `std` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics), and
 * [See "`Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.stdOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @param [ddof] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
 *   where `N` is the number of values. The default is `1`, which applies
 *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
 *   (as in Numpy).
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to include
 *   for each row.
 * @return A [<code>DataFrame</code>][DataFrame] with, per group, the standard deviation of the expression's results.
 */
public inline fun <T, reified R : Number?> PivotGroupBy<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion
