@file:OptIn(ExperimentalTypeInference::class)
@file:Suppress("LocalVariableName")

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
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
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
import org.jetbrains.kotlinx.dataframe.util.SUM_NO_SKIPNAN
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region docs

/**
 *
 *
 * ## The Sum Operation
 *
 * Computes the [sum](https://en.wikipedia.org/wiki/Summation) of values.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * ### Sum Modes
 *
 * Depending on what exactly you want the sum of, there are several modes.
 * They are shown here for [<code>DataFrame</code>][DataFrame], but they exist for the other receivers too:
 *
 * - [<code>`sum`</code>][DataFrame.sum]`()` — the sum of each suitable column separately.
 * - [<code>`sum`</code>][DataFrame.sum]` { columns }` — a single sum of all values in all selected columns.
 * - [<code>`sumFor`</code>][DataFrame.sumFor]` { columns }` — the sum of each selected column separately.
 * - [<code>`sumOf`</code>][DataFrame.sumOf]` { expression }` — the sum of the values that the given expression
 *   returns for each row.
 *
 * Related operations:
 * - [<code>`mean`</code>][DataFrame.mean] — the sum divided by the number of values.
 * - [<code>`cumSum`</code>][DataFrame.cumSum] — the running sum: each value plus all the values before it.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * For more information about [<code>unifying numbers</code>][UnifyingNumbers]:
 * [See "Number Unification" on the documentation website.](https://kotlin.github.io/dataframe/numberunification.html)
 *
 * See all summary statistics:
 * [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal interface SumDocs : CommonStatisticsDocs {

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
     * <code>`df`</code>`.`[<code>sum</code>][org.jetbrains.kotlinx.dataframe.api.sum]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>sum</code>][org.jetbrains.kotlinx.dataframe.api.sum]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>sum</code>][org.jetbrains.kotlinx.dataframe.api.sum]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>sum</code>][org.jetbrains.kotlinx.dataframe.api.sum]`("length", "age")`
     *
     *
     *
     */
    typealias SumSelectingOptions = Nothing

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
     * <code>`df`</code>`.`[<code>sumFor</code>][org.jetbrains.kotlinx.dataframe.api.sumFor]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>sumFor</code>][org.jetbrains.kotlinx.dataframe.api.sumFor]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>sumFor</code>][org.jetbrains.kotlinx.dataframe.api.sumFor]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>sumFor</code>][org.jetbrains.kotlinx.dataframe.api.sumFor]`("length", "age")`
     *
     *
     *
     */
    typealias SumForSelectingOptions = Nothing
}

// endregion

// region DataColumn

/**
 * Returns the sum of the [<code>Short</code>][Short] values in this [<code>DataColumn</code>][DataColumn], as an [<code>Int</code>][Int].
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sumOf] — the sum of the values a selector returns for each element.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean] — the sum divided by the number of values.
 * - [<code>`cumSum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.cumSum] — the running sum of the values in this column.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "amount" column of `Short`s, as an `Int`
 * df.amount.sum()
 * ```
 *
 * @return The sum of the values in this column, as an [<code>Int</code>][Int].
 */
@JvmName("sumShort")
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

/**
 * Returns the sum of the [<code>Byte</code>][Byte] values in this [<code>DataColumn</code>][DataColumn], as an [<code>Int</code>][Int].
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sumOf] — the sum of the values a selector returns for each element.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean] — the sum divided by the number of values.
 * - [<code>`cumSum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.cumSum] — the running sum of the values in this column.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "amount" column of `Byte`s, as an `Int`
 * df.amount.sum()
 * ```
 *
 * @return The sum of the values in this column, as an [<code>Int</code>][Int].
 */
@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

/**
 * Returns the sum of the values in this [<code>DataColumn</code>][DataColumn].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sumOf] — the sum of the values a selector returns for each element.
 * - [<code>`mean`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean] — the sum divided by the number of values.
 * - [<code>`cumSum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.cumSum] — the running sum of the values in this column.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all ages in the "age" Int column
 * df.age.sum()
 * // The sum of all weights in the "weight" `Double?` column, ignoring `null` values
 * df.weight.sum()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of the values in this column.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
public fun <T : Number?> DataColumn<T>.sum(skipNaN: Boolean = skipNaNDefault): T & Any =
    Aggregators.sum(skipNaN).aggregateSingleColumn(this) as (T & Any)

/**
 * Returns the sum of the [<code>Short</code>][Short] values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as an [<code>Int</code>][Int].
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sum] — the sum of the values in this column itself.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.meanOf] — the sum of those values divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved values in the "amount" column of `Short`s, as an `Int`
 * df.amount.sumOf { (it / 2).toShort() }
 * ```
 *
 * @param [expression] A function that returns the value to sum for each element of this column.
 * @return The sum of the values [<code>expression</code>][expression] returns, as an [<code>Int</code>][Int].
 */
@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Short?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the [<code>Byte</code>][Byte] values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn], as an [<code>Int</code>][Int].
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sum] — the sum of the values in this column itself.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.meanOf] — the sum of those values divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved values in the "amount" column of `Byte`s, as an `Int`
 * df.amount.sumOf { (it / 2).toByte() }
 * ```
 *
 * @param [expression] A function that returns the value to sum for each element of this column.
 * @return The sum of the values [<code>expression</code>][expression] returns, as an [<code>Int</code>][Int].
 */
@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Byte?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the values that the given [<code>expression</code>][expression] returns
 * for each element of this [<code>DataColumn</code>][DataColumn].
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.sum] — the sum of the values in this column itself.
 * - [<code>`meanOf`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.meanOf] — the sum of those values divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The total length of all first names in the "name"/"firstName" column
 * df.name.firstName.sumOf { it.length }
 * ```
 *
 *
 * @param [expression] A function that returns the value to sum for each element of this column.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of the values [<code>expression</code>][expression] returns.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Number?> DataColumn<C>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (C) -> V,
): V & Any = Aggregators.sum(skipNaN).aggregateOf(this, expression) as (V & Any)

// endregion

// region DataRow

/**
 * Returns the sum of all the numbers in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [<code>Number</code>][Number] columns)
 * are taken into account; all other columns of the row are ignored.
 *
 * Since the values of different columns are summed together, the result is the sum of all those
 * values converted to their common type.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSumOf<Type>()`</code>][DataRow.rowSumOf] — the sum of the values of one specific number type in this row.
 * - [<code>`rowMean`</code>][DataRow.rowMean] — the sum divided by the number of values.
 * - [<code>`sum`</code>][DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowSum()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of all the numbers in this row.
 */
public fun DataRow<*>.rowSum(skipNaN: Boolean = skipNaNDefault): Number =
    Aggregators.sum(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the sum of the [<code>Short</code>][Short] values in this [<code>DataRow</code>][DataRow], as an [<code>Int</code>][Int].
 *
 * Only the values in the columns of type [<code>Short</code>][Short] (or `Short?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Short` values in the first row, as an `Int`
 * df[0].rowSumOf<Short>()
 * ```
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @return The sum of the [<code>Short</code>][Short] values in this row, as an [<code>Int</code>][Int].
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfShort")
public inline fun <reified T : Short> DataRow<*>.rowSumOf(_kClass: KClass<Short> = Short::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [<code>Byte</code>][Byte] values in this [<code>DataRow</code>][DataRow], as an [<code>Int</code>][Int].
 *
 * Only the values in the columns of type [<code>Byte</code>][Byte] (or `Byte?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Byte` values in the first row, as an `Int`
 * df[0].rowSumOf<Byte>()
 * ```
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @return The sum of the [<code>Byte</code>][Byte] values in this row, as an [<code>Int</code>][Int].
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfByte")
public inline fun <reified T : Byte> DataRow<*>.rowSumOf(_kClass: KClass<Byte> = Byte::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [<code>Int</code>][Int] values in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>Int</code>][Int] (or `Int?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Int` values ("age" and "weight") in the first row
 * df[0].rowSumOf<Int>()
 * ```
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @return The sum of the [<code>Int</code>][Int] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfInt")
public inline fun <reified T : Int> DataRow<*>.rowSumOf(_kClass: KClass<Int> = Int::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [<code>Long</code>][Long] values in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>Long</code>][Long] (or `Long?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Long` values in the first row
 * df[0].rowSumOf<Long>()
 * ```
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @return The sum of the [<code>Long</code>][Long] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfLong")
public inline fun <reified T : Long> DataRow<*>.rowSumOf(_kClass: KClass<Long> = Long::class): Long =
    rowSumOf(typeOf<T>(), false) as Long

/**
 * Returns the sum of the [<code>Float</code>][Float] values in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>Float</code>][Float] (or `Float?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Float` values in the first row, ignoring `NaN` values
 * df[0].rowSumOf<Float>(skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of the [<code>Float</code>][Float] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfFloat")
public inline fun <reified T : Float> DataRow<*>.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Float> = Float::class,
): Float = rowSumOf(typeOf<T>(), skipNaN) as Float

/**
 * Returns the sum of the [<code>Double</code>][Double] values in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of type [<code>Double</code>][Double] (or `Double?`) are taken into account;
 * all other columns of the row are ignored.
 *
 *
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowSumOf<Double>(skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to sum. Only columns of this type are taken into account.
 * @param [_kClass] Technical parameter to distinguish this overload from the others;
 *   you never need to supply it.
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of the [<code>Double</code>][Double] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfDouble")
public inline fun <reified T : Double> DataRow<*>.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Double> = Double::class,
): Double = rowSumOf(typeOf<T>(), skipNaN) as Double

// unfortunately, we cannot make a `reified T : Number?` due to clashes

/**
 * Returns the sum of the values of the given [<code>type</code>][type] in this [<code>DataRow</code>][DataRow].
 *
 * Only the values in the columns of the given [<code>type</code>][type] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 *
 * This overload takes the type as a [<code>KType</code>][KType] argument; prefer the `reified` overloads, like
 * [<code>`rowSumOf`</code>][DataRow.rowSumOf]`<`[<code>`Int`</code>][Int]`>()`, whenever the type is known at compile time.
 *
 *
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`rowSum`</code>][org.jetbrains.kotlinx.dataframe.DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum] — the sum of the values in specific columns of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information:
 * [See "Row statistics" on the documentation website.](https://kotlin.github.io/dataframe/rowstats.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Int` values ("age" and "weight") in the first row
 * df[0].rowSumOf(typeOf<Int>())
 * ```
 * @param [type] The type of the values to sum. Only columns of this type are taken into account.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of the values of the given [<code>type</code>][type] in this row.
 * @throws IllegalArgumentException if [<code>type</code>][type] is not a primitive number type or [<code>Number</code>][Number] itself.
 */
public fun DataRow<*>.rowSumOf(type: KType, skipNaN: Boolean = skipNaNDefault): Number {
    require(type.isPrimitiveOrMixedNumber()) {
        "Type $type is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.sum(skipNaN).aggregateOfRow(this) {
        colsOf(type.withNullability(true))
    }
}
// endregion

// region DataFrame

/**
 * Returns the sum of the values of each suitable column of this [<code>DataFrame</code>][DataFrame] separately.
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * See also:
 * - [<code>`sumFor`</code>][DataFrame.sumFor] — the same, but for an explicit selection of columns.
 * - [<code>`sum`</code>][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>`mean`</code>][DataFrame.mean] — the sum of each column divided by its number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of each number column ("age" and "weight")
 * df.sum()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the sum of each suitable column of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Sum0")
public fun <T> DataFrame<T>.sum(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Returns the sum of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the sum of each selected column divided by its number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of the "age" values and the sum of the "weight" values
 * df.sumFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.sumFor(skipNaN = true) { age into "totalAge" and (weight into "totalWeight") }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the sum of.
 * @return A single [<code>DataRow</code>][DataRow] with the sum of each selected column.
 */
@Refine
@Interpretable("Sum1")
public fun <T, C : Number?> DataFrame<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Returns the sum of the values of each selected column of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] separately.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.meanFor] — the sum of each selected column divided by its number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of the "age" values and the sum of the "weight" values
 * df.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A single [<code>DataRow</code>][DataRow] with the sum of each selected column.
 */
public fun <T> DataFrame<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(skipNaN) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sumFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sumFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single sum of all the [<code>Short</code>][Short] values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * as an [<code>Int</code>][Int].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column separately.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
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
 * // The sum of all values in the "amount" and "bonus" columns of `Short`s, as an `Int`
 * df.sum { amount and bonus }
 * ```
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return The sum of all the values in the selected columns, as an [<code>Int</code>][Int].
 */
@JvmName("sumShort")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Short?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

/**
 * Returns a single sum of all the [<code>Byte</code>][Byte] values in the selected columns of this [<code>DataFrame</code>][DataFrame],
 * as an [<code>Int</code>][Int].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column separately.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
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
 * // The sum of all values in the "amount" and "bonus" columns of `Byte`s, as an `Int`
 * df.sum { amount and bonus }
 * ```
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return The sum of all the values in the selected columns, as an [<code>Int</code>][Int].
 */
@JvmName("sumByte")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Byte?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

/**
 * Returns a single sum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column separately.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [<code>`mean`</code>][DataFrame.mean] — the sum divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
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
 * // The sum of all values in the "age" and "weight" columns together
 * df.sum { age and weight }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return The sum of all the values in the selected columns.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Number?> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = Aggregators.sum(skipNaN).aggregateAll(this, columns) as (C & Any)

@JvmName("sumShort")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Short?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Byte?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = sum(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single sum of all the values in the selected columns of this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumFor] — the sum of each selected column separately.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [<code>`mean`</code>][DataFrame.mean] — the sum divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "age" and "weight" columns together
 * df.sum("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return The sum of all the values in the selected columns.
 */
public fun <T> DataFrame<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Number =
    sum(skipNaN) { columns.toColumnsSetOf<Number?>() }

@JvmName("sumShort")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    vararg columns: KProperty<C>,
): C & Any = sum(skipNaN) { columns.toColumnSet() }

/**
 * Returns the sum of the [<code>Short</code>][Short] values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], as an [<code>Int</code>][Int].
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved "amount" values (of `Short`s), as an `Int`
 * df.sumOf { (amount / 2).toShort() }
 * ```
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [<code>expression</code>][expression] returns, as an [<code>Int</code>][Int].
 */
@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Short?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the [<code>Byte</code>][Byte] values that the given [<code>expression</code>][expression] returns
 * for each row of this [<code>DataFrame</code>][DataFrame], as an [<code>Int</code>][Int].
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved "amount" values (of `Byte`s), as an `Int`
 * df.sumOf { (amount / 2).toByte() }
 * ```
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [<code>expression</code>][expression] returns, as an [<code>Int</code>][Int].
 */
@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Byte?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the values that the given [<code>expression</code>][expression] returns
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
 * the result is `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [<code>`meanOf`</code>][DataFrame.meanOf] — the sum of those values divided by the number of values.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 * For more information: [See `sum` on the documentation website.](https://kotlin.github.io/dataframe/sum.html)
 *
 * ### Example
 * ```kotlin
 * // The sum of the weight-to-age ratios of all rows
 * df.sumOf { (weight ?: 0) / age }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [<code>expression</code>][expression] returns.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = Aggregators.sum(skipNaN).aggregateOf(this, expression) as (C & Any)

// endregion

// region GroupBy

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each suitable column.
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * See also:
 * - [<code>`sumFor`</code>][Grouped.sumFor] — the same, but for an explicit selection of columns.
 * - [<code>`sum`</code>][Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>`mean`</code>][Grouped.mean] — the sum of each column divided by its number of values, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of each number column ("age" and "weight")
 * df.groupBy { city }.sum()
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the sum of each suitable column per group.
 */
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each selected column.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the sum of each selected column divided by its number of values,
 *   per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.groupBy { city }.sumFor { age and weight }
 * ```
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the sum of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the sum of each selected column per group.
 */
@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number?> Grouped<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each selected column.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>`meanFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.meanFor] — the sum of each selected column divided by its number of values,
 *   per group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.groupBy { city }.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and the sum of each selected column per group.
 */
public fun <T> Grouped<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sumFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sumFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions].
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumFor] — the sum of each selected column separately, per group.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumOf] — the sum of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of all values in the "age" and "weight" columns,
 * // in a column called "total"
 * df.groupBy { city }.sum("total") { age and weight }
 * ```
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"sum"` otherwise.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single sum per group.
 */
@Refine
@Interpretable("GroupBySum2")
public fun <T, C : Number?> Grouped<T>.sum(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, name, columns)

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions].
 *
 * See also:
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumFor] — the sum of each selected column separately, per group.
 * - [<code>`sumOf`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.sumOf] — the sum of the values a row expression returns
 *   for each row of a group.
 * - [<code>`aggregate`</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of all values in the "age" and "weight" columns,
 * // in a column called "total"
 * df.groupBy { city }.sum("age", "weight", name = "total")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"sum"` otherwise.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single sum per group.
 */
public fun <T> Grouped<T>.sum(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sum(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sum(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by computing the sum of the values that the given [<code>expression</code>][expression]
 * returns for each row of a group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group, containing the group key columns and
 * a single column with the sum per group, named [<code>resultName</code>][resultName] (or `"sum"` if [<code>resultName</code>][resultName] is `null`).
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * See also:
 * - [<code>`sum`</code>][Grouped.sum] — a single sum of all values in the selected columns, per group.
 * - [<code>`meanOf`</code>][Grouped.meanOf] — the sum of those values divided by the number of values, per group.
 * - [<code>`aggregate`</code>][Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics),
 * [See "`GroupBy` Aggregation Statistics" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the weight-to-age ratios, in a column called "totalRatio"
 * df.groupBy { city }.sumOf("totalRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [resultName] The name of the resulting column. If `null` (the default), `"sum"` is used.
 *
 *
 * @param [skipNaN] If `true`, [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [<code>Double</code>][Double] and [<code>Float</code>][Float] values.
 *
 *
 * @param [expression] The [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the group keys and a single sum per group.
 */
@Refine
@Interpretable("GroupBySumOf")
public inline fun <T, reified R : Number?> Grouped<T>.sumOf(
    resultName: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, resultName, expression)

// endregion

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the sum
 * of each suitable column of the corresponding group.
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sumFor`</code>][Pivot.sumFor] — the same, but for an explicit selection of columns.
 * - [<code>`sum`</code>][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of each number column ("age" and "weight")
 * df.pivot { city }.sum()
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
 *   This only has an effect on [Double] and [Float] values.
 * @return A single [DataRow] with the sum of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the sum
 * of each selected column of the corresponding group.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [columns] The [ColumnsForAggregateSelector][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the sum of.
 * @return A single [DataRow] with the sum of each selected column per [pivot] group.
 */
public fun <T, R : Number?> Pivot<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { sumFor(separate, skipNaN, columns) }

/**
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as (nested) columns, containing the sum
 * of each selected column of the corresponding group.
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 * @return A single [DataRow] with the sum of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]`()` — the sum of each suitable column separately, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sumFor] — the sum of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [columns] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return A single [DataRow] with, per [pivot] group, the sum of all the values
 *   in the selected columns.
 */
public fun <T, C : Number?> Pivot<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { sum(skipNaN, columns) }

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions], or check out the
 * [<code>`Pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sum]`()` — the sum of each suitable column separately, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.sumFor] — the sum of each selected column separately, per group.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 * @return A single [DataRow] with, per [pivot] group, the sum of all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sum(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: KProperty<C>, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sum(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by computing the sum of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as (nested) columns, containing the sum
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the weight-to-age ratios
 * df.pivot { city }.sumOf { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [expression] The [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return A single [DataRow] with, per [pivot] group, the sum of the expression's results.
 */
public inline fun <T, reified R : Number?> Pivot<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { sumOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the sum of each suitable column
 * of the group corresponding to that [<code>pivot</code>][pivot] key (column) and [<code>groupBy</code>][groupBy] key (row).
 *
 *
 *
 * All columns of a primitive number type (and all "mixed" [<code>Number</code>][Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sumFor`</code>][PivotGroupBy.sumFor] — the same, but for an explicit selection of columns.
 * - [<code>`sum`</code>][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.sum()
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
 *   This only has an effect on [Double] and [Float] values.
 * @return A [DataFrame] with the sum of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the sum of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 * The columns are selected with the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] — an extension of the
 * Columns Selection DSL which lets you rename the result of a column with
 * [<code>`into`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] and supply a
 * [<code>`default`</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] value for columns without any values.
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [columns] The [ColumnsForAggregateSelector][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns
 *   to compute the sum of.
 * @return A [DataFrame] with the sum of each selected column per group.
 */
public fun <T, R : Number?> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, separate, columns)

/**
 *
 *
 * Aggregates this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each cell contains the sum of each selected column
 * of the group corresponding to that [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key (column) and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key (row).
 *
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumForSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]`()` — the same, but for all suitable columns at once.
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 * @return A [DataFrame] with the sum of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]`()` — the sum of each suitable column separately, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sumFor] — the sum of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [columns] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to compute the sum of.
 * @return A [DataFrame] with, per group, the sum of all the values in the selected columns.
 */
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, columns)

/**
 *
 *
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs.SumSelectingOptions], or check out the
 * [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sum]`()` — the sum of each suitable column separately, per group.
 * - [<code>`sumFor`</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.sumFor] — the sum of each selected column separately, per group.
 * - [<code>PivotGroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 *
 * @param [columns] The names of the columns to compute the sum of.
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 * @return A [DataFrame] with, per group, the sum of all the values in the selected columns.
 */
public fun <T> PivotGroupBy<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by computing the sum of the values that the given [<code>expression</code>][expression]
 * returns for each row, per group.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] where each cell contains the sum of the expression's results for the
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
 * All primitive number types are supported: [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], and [<code>Double</code>][Double].
 * "Mixed" [<code>Number</code>][Number] input is supported too, as long as it consists solely of those primitive numbers;
 * its values are then first converted to their common type using [<code>UnifiedNumberTypeOptions.PRIMITIVES_ONLY</code>][UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
 * see [<code>number unification</code>][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers].
 * Big numbers ([<code>`BigInteger`</code>][java.math.BigInteger], [<code>`BigDecimal`</code>][java.math.BigDecimal]) are not
 * supported; [<code>`convert`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] them to a primitive number type first.
 *
 *
 *
 * `null` values in the input are always ignored.
 *
 * If the input contains [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values, the result will be `NaN`,
 * unless [skipNaN] is set to `true`.
 *
 * The result is never `null` and has the same type as the input, except for [<code>Byte</code>][Byte] and [<code>Short</code>][Short],
 * which sum to [<code>Int</code>][Int], and "mixed" [<code>Number</code>][Number] input, which sums to the common type of its values.
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 *
 * Result cells for which there is nothing to sum
 * (for instance, because the input was empty or contained only `null` values)
 * simply become `0` in the result type (defaulting to [<code>Double</code>][Double] if there is no number type to speak of).
 *
 * For more information about the resulting types:
 * [See "`sum` Type Conversion" on the documentation website.](https://kotlin.github.io/dataframe/sum.html#type-conversion)
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [<code>`sum`</code>][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [<code>PivotGroupBy</code>][PivotGroupBy].
 * - [<code>The Sum Operation</code>][org.jetbrains.kotlinx.dataframe.api.SumDocs] — an overview of all `sum` modes.
 *
 *
 *
 * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics),
 * [See "Pivot` Aggregation statistics" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation-statistics)
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.sumOf { (weight ?: 0) / age }
 * ```
 *
 *
 *
 * @param [skipNaN] If `true`, [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values are ignored, just like `null` values.
 *   If `false` (the default), a [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] in the input is propagated to the result.
 *   This only has an effect on [Double] and [Float] values.
 *
 *
 * @param [expression] The [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] to compute the value to sum for each row.
 * @return A [DataFrame] with, per group, the sum of the expression's results.
 */
public inline fun <T, reified R : Number?> PivotGroupBy<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, expression)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Number?> DataColumn<T>.sum(): T = sum(skipNaN = skipNaNDefault)

@JvmName("sumOfNumber")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <C, reified V : Number?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): V & Any =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun DataRow<*>.rowSum(): Number = rowSum(skipNaN = skipNaNDefault)

@JvmName("rowSumOfFloat")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Float?> DataRow<*>.rowSumOf(_kClass: KClass<Float> = Float::class): Float =
    rowSumOf(typeOf<T>(), skipNaN = skipNaNDefault) as Float

@JvmName("rowSumOfDouble")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Double?> DataRow<*>.rowSumOf(_kClass: KClass<Double> = Double::class): Double =
    rowSumOf(typeOf<T>(), skipNaN = skipNaNDefault) as Double

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun DataRow<*>.rowSumOf(type: KType): Number = rowSumOf(type, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sum(): DataRow<T> = sum(skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    sumFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(vararg columns: ColumnReference<C>): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(vararg columns: KProperty<C>): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C>): C =
    sum(skipNaN = skipNaNDefault, columns = columns)

@JvmName("sumNumber")
@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): C & Any =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum(columns = columns, skipNaN = skipNaNDefault)

@JvmName("sumNumber")
@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(vararg columns: KProperty<C>): C & Any =
    sum(skipNaN = skipNaNDefault, columns = columns)

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): C & Any =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sum(): DataFrame<T> = sum(skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    sumFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sumFor(vararg columns: String): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(vararg columns: KProperty<C>): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(name: String? = null, columns: ColumnsSelector<T, C>): DataFrame<T> =
    sum(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sum(vararg columns: String, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(vararg columns: ColumnReference<C>, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(vararg columns: KProperty<C>, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Grouped<T>.sumOf(
    resultName: String? = null,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = sumOf(resultName, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sum(separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> Pivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = sumFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataRow<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sumFor(vararg columns: KProperty<C>, separate: Boolean = false): DataRow<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(columns: ColumnsSelector<T, C>): DataRow<T> =
    sum(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sum(vararg columns: String): DataRow<T> = sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: ColumnReference<C>): DataRow<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: KProperty<C>): DataRow<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sum(separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = sumFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(columns: ColumnsSelector<T, C>): DataFrame<T> =
    sum(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sum(vararg columns: String): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(vararg columns: ColumnReference<C>): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(vararg columns: KProperty<C>): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> PivotGroupBy<T>.sumOf(
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = sumOf(skipNaN = skipNaNDefault, expression = expression)

// endregion
