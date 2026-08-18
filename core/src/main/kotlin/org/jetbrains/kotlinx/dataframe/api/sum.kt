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
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions
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
 * {@comment
 *    The Sum Operation KDoc-topic; it also holds all common `sum` KDoc-snippets.
 *    Link to it with `{@include [SumDocsLink]}`.
 * }
 *
 * ## The Sum Operation
 *
 * Computes the [sum](https://en.wikipedia.org/wiki/Summation) of values.
 *
 * @include [SupportedTypesSnippet]
 *
 * ### Sum Modes
 *
 * Depending on what exactly you want the sum of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [`sum`][DataFrame.sum]`()` — the sum of each suitable column separately.
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in all selected columns.
 * - [`sumFor`][DataFrame.sumFor]` { columns }` — the sum of each selected column separately.
 * - [`sumOf`][DataFrame.sumOf]` { expression }` — the sum of the values that the given expression
 *   returns for each row.
 *
 * Related operations:
 * - [`mean`][DataFrame.mean] — the sum divided by the number of values.
 * - [`cumSum`][DataFrame.cumSum] — the running sum: each value plus all the values before it.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * For more information about [unifying numbers][UnifyingNumbers]:
 * {@include [DocumentationUrls.NumberUnification]}
 *
 * See all summary statistics: {@include [DocumentationUrls.Statistics]}
 */
internal interface SumDocs : CommonStatisticsDocs {

    /**
     * {@comment Note about which values are supported, how `null` and `NaN` values are treated,
     *    and what type the result has. KDoc-snippet.}
     *
     * All primitive number types are supported: [Byte], [Short], [Int], [Long], [Float], and [Double].
     * "Mixed" [Number] input is supported too, as long as it consists solely of those primitive numbers;
     * its values are then first converted to their common type using [UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
     * see [number unification][UnifyingNumbers].
     * Big numbers ([`BigInteger`][java.math.BigInteger], [`BigDecimal`][java.math.BigDecimal]) are not
     * supported; [`convert`][DataFrame.convert] them to a primitive number type first.
     *
     * {@include [CommonStatisticsDocs.NullAndNaNHandlingSnippet]}
     *
     * The result is never `null` and has the same type as the input, except for [Byte] and [Short],
     * which sum to [Int], and "mixed" [Number] input, which sums to the common type of its values.
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Sum.TypeConversion]}
     */
    @ExcludeFromSources
    typealias SupportedTypesSnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with a single result. KDoc-snippet.}
     *
     * When there is nothing to sum, for instance, when the input is empty or contains only `null` values,
     * the result is `0` in the result type (defaulting to [Double] if there is no number type to speak of).
     */
    @ExcludeFromSources
    typealias ZeroOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with multiple results. KDoc-snippet.}
     *
     * Result cells for which there is nothing to sum
     * (for instance, because the input was empty or contained only `null` values)
     * simply become `0` in the result type (defaulting to [Double] if there is no number type to speak of).
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Sum.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ZeroCellOnEmptySnippet = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetSumOperationArg]}
     */
    typealias SumSelectingOptions = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetSumForOperationArg]}
     */
    typealias SumForSelectingOptions = Nothing

    /**
     * {@include [SumDocs.ZeroOnEmptySnippet]}
     *
     * See also:
     * - [`sumOf`][DataColumn.sumOf] — the sum of the values a selector returns for each element.
     * - [`mean`][DataColumn.mean] — the sum divided by the number of values.
     * - [`cumSum`][DataColumn.cumSum] — the running sum of the values in this column.
     * - {@include [SumDocsLink]} — an overview of all `sum` modes.
     *
     * For more information: {@include [DocumentationUrls.Sum]}
     *
     * ### Example
     */
    @ExcludeFromSources
    typealias DataColumnSumSnippet = Nothing

    /**
     * {@comment The parts all [DataColumn.sumOf] overloads have in common. KDoc-snippet.}
     *
     * {@include [SumDocs.SupportedTypesSnippet]}
     *
     * {@include [SumDocs.ZeroOnEmptySnippet]}
     *
     * See also:
     * - [`sum`][DataColumn.sum] — the sum of the values in this column itself.
     * - [`meanOf`][DataColumn.meanOf] — the sum of those values divided by the number of values.
     * - {@include [SumDocsLink]} — an overview of all `sum` modes.
     *
     * For more information: {@include [DocumentationUrls.Sum]}
     *
     * ### Example
     * $[EXAMPLE]
     *
     * @param [expression\] A function that returns the value to sum for each element of this column.
     */
    @ExcludeFromSources
    interface DataColumnSumOfSnippet {

        // The example to render for this sumOf overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [DataRow.rowSumOf] overloads have in common. KDoc-snippet.}
     *
     * {@include [SumDocs.SupportedTypesSnippet]}
     *
     * {@include [SumDocs.ZeroOnEmptySnippet]}
     *
     * See also:
     * - [`rowSum`][DataRow.rowSum] — the sum of all the numbers in this row, of any number type.
     * - [`sum`][DataFrame.sum] — the sum of the values in specific columns of a [DataFrame].
     * - {@include [SumDocsLink]} — an overview of all `sum` modes.
     *
     * For more information: {@include [DocumentationUrls.RowStatistics]}
     */
    @ExcludeFromSources
    typealias RowSumOfSnippet = Nothing

    /**
     * {@comment The shared `T` parameter documentation of the reified [DataRow.rowSumOf] overloads.
     *    KDoc-snippet.}
     *
     * @param [T\] The type of the values to sum. Only columns of this type are taken into account.
     */
    @ExcludeFromSources
    typealias RowSumOfTypeParam = Nothing

    /**
     * {@comment The shared `_kClass` parameter documentation of the reified [DataRow.rowSumOf] overloads.
     *    KDoc-snippet.}
     *
     * @param [_kClass\] Technical parameter to distinguish this overload from the others;
     *   you never need to supply it.
     */
    @ExcludeFromSources
    typealias RowSumOfKClassParam = Nothing
}

/** [The Sum Operation][SumDocs] */
@ExcludeFromSources
private typealias SumDocsLink = Nothing

/** {@set [SelectingColumns.OPERATION] [sum][sum]} */
@ExcludeFromSources
private typealias SetSumOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [sumFor][sumFor]} */
@ExcludeFromSources
private typealias SetSumForOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the sum of the [Short] values in this [DataColumn], as an [Int].
 *
 * @include [SumDocs.DataColumnSumSnippet]
 * ```kotlin
 * // The sum of all values in the "amount" column of `Short`s, as an `Int`
 * df.amount.sum()
 * ```
 *
 * @return The sum of the values in this column, as an [Int].
 */
@JvmName("sumShort")
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

/**
 * Returns the sum of the [Byte] values in this [DataColumn], as an [Int].
 *
 * @include [SumDocs.DataColumnSumSnippet]
 * ```kotlin
 * // The sum of all values in the "amount" column of `Byte`s, as an `Int`
 * df.amount.sum()
 * ```
 *
 * @return The sum of the values in this column, as an [Int].
 */
@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

/**
 * Returns the sum of the values in this [DataColumn].
 *
 * @include [SumDocs.SupportedTypesSnippet]
 * @include [SumDocs.DataColumnSumSnippet]
 * ```kotlin
 * // The sum of all ages in the "age" Int column
 * df.age.sum()
 * // The sum of all weights in the "weight" `Double?` column, ignoring `null` values
 * df.weight.sum()
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @return The sum of the values in this column.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
public fun <T : Number?> DataColumn<T>.sum(skipNaN: Boolean = skipNaNDefault): T & Any =
    Aggregators.sum(skipNaN).aggregateSingleColumn(this) as (T & Any)

/**
 * Returns the sum of the [Short] values that the given [expression] returns
 * for each element of this [DataColumn], as an [Int].
 *
 * @include [SumDocs.DataColumnSumOfSnippet]
 * @set [SumDocs.DataColumnSumOfSnippet.EXAMPLE]
 * ```kotlin
 * // The sum of all halved values in the "amount" column of `Short`s, as an `Int`
 * df.amount.sumOf { (it / 2).toShort() }
 * ```
 * @return The sum of the values [expression] returns, as an [Int].
 */
@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Short?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the [Byte] values that the given [expression] returns
 * for each element of this [DataColumn], as an [Int].
 *
 * @include [SumDocs.DataColumnSumOfSnippet]
 * @set [SumDocs.DataColumnSumOfSnippet.EXAMPLE]
 * ```kotlin
 * // The sum of all halved values in the "amount" column of `Byte`s, as an `Int`
 * df.amount.sumOf { (it / 2).toByte() }
 * ```
 * @return The sum of the values [expression] returns, as an [Int].
 */
@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Byte?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the values that the given [expression] returns
 * for each element of this [DataColumn].
 *
 * @include [SumDocs.DataColumnSumOfSnippet]
 * @set [SumDocs.DataColumnSumOfSnippet.EXAMPLE]
 * ```kotlin
 * // The total length of all first names in the "name"/"firstName" column
 * df.name.firstName.sumOf { it.length }
 * ```
 * @include [SumDocs.SkipNanParam]
 * @return The sum of the values [expression] returns.
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
 * Returns the sum of all the numbers in this [DataRow].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [Number] columns)
 * are taken into account; all other columns of the row are ignored.
 *
 * Since the values of different columns are summed together, the result is the sum of all those
 * values converted to their common type.
 *
 * @include [SumDocs.SupportedTypesSnippet]
 * @include [SumDocs.ZeroOnEmptySnippet]
 *
 * See also:
 * - [`rowSumOf<Type>()`][DataRow.rowSumOf] — the sum of the values of one specific number type in this row.
 * - [`rowMean`][DataRow.rowMean] — the sum divided by the number of values.
 * - [`sum`][DataFrame.sum] — the sum of the values in specific columns of a [DataFrame].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowSum()
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @return The sum of all the numbers in this row.
 */
public fun DataRow<*>.rowSum(skipNaN: Boolean = skipNaNDefault): Number =
    Aggregators.sum(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the sum of the [Short] values in this [DataRow], as an [Int].
 *
 * Only the values in the columns of type [Short] (or `Short?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Short` values in the first row, as an `Int`
 * df[0].rowSumOf<Short>()
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Short] values in this row, as an [Int].
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfShort")
public inline fun <reified T : Short> DataRow<*>.rowSumOf(_kClass: KClass<Short> = Short::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [Byte] values in this [DataRow], as an [Int].
 *
 * Only the values in the columns of type [Byte] (or `Byte?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Byte` values in the first row, as an `Int`
 * df[0].rowSumOf<Byte>()
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Byte] values in this row, as an [Int].
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfByte")
public inline fun <reified T : Byte> DataRow<*>.rowSumOf(_kClass: KClass<Byte> = Byte::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [Int] values in this [DataRow].
 *
 * Only the values in the columns of type [Int] (or `Int?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Int` values ("age" and "weight") in the first row
 * df[0].rowSumOf<Int>()
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Int] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfInt")
public inline fun <reified T : Int> DataRow<*>.rowSumOf(_kClass: KClass<Int> = Int::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

/**
 * Returns the sum of the [Long] values in this [DataRow].
 *
 * Only the values in the columns of type [Long] (or `Long?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Long` values in the first row
 * df[0].rowSumOf<Long>()
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Long] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfLong")
public inline fun <reified T : Long> DataRow<*>.rowSumOf(_kClass: KClass<Long> = Long::class): Long =
    rowSumOf(typeOf<T>(), false) as Long

/**
 * Returns the sum of the [Float] values in this [DataRow].
 *
 * Only the values in the columns of type [Float] (or `Float?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Float` values in the first row, ignoring `NaN` values
 * df[0].rowSumOf<Float>(skipNaN = true)
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.SkipNanParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Float] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfFloat")
public inline fun <reified T : Float> DataRow<*>.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Float> = Float::class,
): Float = rowSumOf(typeOf<T>(), skipNaN) as Float

/**
 * Returns the sum of the [Double] values in this [DataRow].
 *
 * Only the values in the columns of type [Double] (or `Double?`) are taken into account;
 * all other columns of the row are ignored.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowSumOf<Double>(skipNaN = true)
 * ```
 *
 * @include [SumDocs.RowSumOfTypeParam]
 * @include [SumDocs.SkipNanParam]
 * @include [SumDocs.RowSumOfKClassParam]
 * @return The sum of the [Double] values in this row.
 */
@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfDouble")
public inline fun <reified T : Double> DataRow<*>.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Double> = Double::class,
): Double = rowSumOf(typeOf<T>(), skipNaN) as Double

// unfortunately, we cannot make a `reified T : Number?` due to clashes

/**
 * Returns the sum of the values of the given [type] in this [DataRow].
 *
 * Only the values in the columns of the given [type] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 *
 * This overload takes the type as a [KType] argument; prefer the `reified` overloads, like
 * [`rowSumOf`][DataRow.rowSumOf]`<`[`Int`][Int]`>()`, whenever the type is known at compile time.
 *
 * {@include [SumDocs.RowSumOfSnippet]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all `Int` values ("age" and "weight") in the first row
 * df[0].rowSumOf(typeOf<Int>())
 * ```
 *
 * @param [type] The type of the values to sum. Only columns of this type are taken into account.
 * @include [SumDocs.SkipNanParam]
 * @return The sum of the values of the given [type] in this row.
 * @throws IllegalArgumentException if [type] is not a primitive number type or [Number] itself.
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
 * Returns the sum of the values of each suitable column of this [DataFrame] separately.
 *
 * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * See also:
 * - [`sumFor`][DataFrame.sumFor] — the same, but for an explicit selection of columns.
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [`mean`][DataFrame.mean] — the sum of each column divided by its number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of each number column ("age" and "weight")
 * df.sum()
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @return A single [DataRow] with the sum of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Sum0")
public fun <T> DataFrame<T>.sum(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 * Returns the sum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SumDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [`sum`][DataFrame.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [`meanFor`][DataFrame.meanFor] — the sum of each selected column divided by its number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of the "age" values and the sum of the "weight" values
 * df.sumFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.sumFor(skipNaN = true) { age into "totalAge" and (weight into "totalWeight") }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns of this [DataFrame]
 *   to compute the sum of.
 * @return A single [DataRow] with the sum of each selected column.
 */
@Refine
@Interpretable("Sum1")
public fun <T, C : Number?> DataFrame<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

/**
 * Returns the sum of the values of each selected column of this [DataFrame] separately.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [`sum`][DataFrame.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [`meanFor`][DataFrame.meanFor] — the sum of each selected column divided by its number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the sum of the "age" values and the sum of the "weight" values
 * df.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the sum of.
 * @include [SumDocs.SkipNanParam]
 * @return A single [DataRow] with the sum of each selected column.
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
 * Returns a single sum of all the [Short] values in the selected columns of this [DataFrame],
 * as an [Int].
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [`sumFor`][DataFrame.sumFor] — the sum of each selected column separately.
 * - [`sumOf`][DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "amount" and "bonus" columns of `Short`s, as an `Int`
 * df.sum { amount and bonus }
 * ```
 *
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the sum of.
 * @return The sum of all the values in the selected columns, as an [Int].
 */
@JvmName("sumShort")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Short?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

/**
 * Returns a single sum of all the [Byte] values in the selected columns of this [DataFrame],
 * as an [Int].
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [`sumFor`][DataFrame.sumFor] — the sum of each selected column separately.
 * - [`sumOf`][DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "amount" and "bonus" columns of `Byte`s, as an `Int`
 * df.sum { amount and bonus }
 * ```
 *
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the sum of.
 * @return The sum of all the values in the selected columns, as an [Int].
 */
@JvmName("sumByte")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Byte?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

/**
 * Returns a single sum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [`sumFor`][DataFrame.sumFor] — the sum of each selected column separately.
 * - [`sumOf`][DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [`mean`][DataFrame.mean] — the sum divided by the number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "age" and "weight" columns together
 * df.sum { age and weight }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns of this [DataFrame]
 *   to compute the sum of.
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
 * Returns a single sum of all the values in the selected columns of this [DataFrame].
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See also:
 * - [`sumFor`][DataFrame.sumFor] — the sum of each selected column separately.
 * - [`sumOf`][DataFrame.sumOf] — the sum of the values a row expression returns for each row.
 * - [`mean`][DataFrame.mean] — the sum divided by the number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Example
 * ```kotlin
 * // The sum of all values in the "age" and "weight" columns together
 * df.sum("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to compute the sum of.
 * @include [SumDocs.SkipNanParam]
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
 * Returns the sum of the [Short] values that the given [expression] returns
 * for each row of this [DataFrame], as an [Int].
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * See also:
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved "amount" values (of `Short`s), as an `Int`
 * df.sumOf { (amount / 2).toShort() }
 * ```
 *
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [expression] returns, as an [Int].
 */
@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Short?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the [Byte] values that the given [expression] returns
 * for each row of this [DataFrame], as an [Int].
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * See also:
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // The sum of all halved "amount" values (of `Byte`s), as an `Int`
 * df.sumOf { (amount / 2).toByte() }
 * ```
 *
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [expression] returns, as an [Int].
 */
@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Byte?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

/**
 * Returns the sum of the values that the given [expression] returns
 * for each row of this [DataFrame].
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroOnEmptySnippet]}
 *
 * See also:
 * - [`sum`][DataFrame.sum]` { columns }` — a single sum of all values in the selected columns.
 * - [`meanOf`][DataFrame.meanOf] — the sum of those values divided by the number of values.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.Sum]}
 *
 * ### Example
 * ```kotlin
 * // The sum of the weight-to-age ratios of all rows
 * df.sumOf { (weight ?: 0) / age }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
 * @return The sum of the values [expression] returns.
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
 * Aggregates this [GroupBy] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each suitable column.
 * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * See also:
 * - [`sumFor`][Grouped.sumFor] — the same, but for an explicit selection of columns.
 * - [`sum`][Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [`mean`][Grouped.mean] — the sum of each column divided by its number of values, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of each number column ("age" and "weight")
 * df.groupBy { city }.sum()
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the sum of each suitable column per group.
 */
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 * Aggregates this [GroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each selected column.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SumDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [`sum`][Grouped.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [`meanFor`][Grouped.meanFor] — the sum of each selected column divided by its number of values,
 *   per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.groupBy { city }.sumFor { age and weight }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns to compute the sum of.
 * @return A new [DataFrame] with the group keys and the sum of each selected column per group.
 */
@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number?> Grouped<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

/**
 * Aggregates this [GroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the sum for each selected column.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions].
 *
 * See also:
 * - [`sum`][Grouped.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][Grouped.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [`meanFor`][Grouped.meanFor] — the sum of each selected column divided by its number of values,
 *   per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.groupBy { city }.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @include [SumDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the sum of each selected column per group.
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
 * Aggregates this [GroupBy] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the sum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"sum"` otherwise.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions].
 *
 * See also:
 * - [`sumFor`][Grouped.sumFor] — the sum of each selected column separately, per group.
 * - [`sumOf`][Grouped.sumOf] — the sum of the values a row expression returns
 *   for each row of a group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
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
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the sum of.
 * @return A new [DataFrame] with the group keys and a single sum per group.
 */
@Refine
@Interpretable("GroupBySum2")
public fun <T, C : Number?> Grouped<T>.sum(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, name, columns)

/**
 * Aggregates this [GroupBy] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the sum per group.
 * That column is named [name], or, if [name] is `null`, after the selected column
 * if exactly one column is selected, and `"sum"` otherwise.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions].
 *
 * See also:
 * - [`sumFor`][Grouped.sumFor] — the sum of each selected column separately, per group.
 * - [`sumOf`][Grouped.sumOf] — the sum of the values a row expression returns
 *   for each row of a group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of all values in the "age" and "weight" columns,
 * // in a column called "total"
 * df.groupBy { city }.sum("age", "weight", name = "total")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @param [name] The name of the resulting column.
 *   If `null` (the default), the name of the selected column is used if exactly one column
 *   is selected, and `"sum"` otherwise.
 * @include [SumDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and a single sum per group.
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
 * Aggregates this [GroupBy] by computing the sum of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the sum per group, named [resultName] (or `"sum"` if [resultName] is `null`).
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * See also:
 * - [`sum`][Grouped.sum] — a single sum of all values in the selected columns, per group.
 * - [`meanOf`][Grouped.meanOf] — the sum of those values divided by the number of values, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.GroupByStatistics]},
 * {@include [DocumentationUrls.GroupByAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the weight-to-age ratios, in a column called "totalRatio"
 * df.groupBy { city }.sumOf("totalRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [resultName] The name of the resulting column. If `null` (the default), `"sum"` is used.
 * @include [SumDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
 * @return A new [DataFrame] with the group keys and a single sum per group.
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
 * Aggregates this [Pivot] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum
 * of each suitable column of the corresponding group.
 * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sumFor`][Pivot.sumFor] — the same, but for an explicit selection of columns.
 * - [`sum`][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of each number column ("age" and "weight")
 * df.pivot { city }.sum()
 * ```
 *
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
 * @return A single [DataRow] with the sum of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

/**
 * Aggregates this [Pivot] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum
 * of each selected column of the corresponding group.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SumDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions], or check out the
 * [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sum`][Pivot.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.pivot { city }.sumFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.sumFor(separate = true) { age and weight }
 * ```
 *
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns to compute the sum of.
 * @return A single [DataRow] with the sum of each selected column per [pivot] group.
 */
public fun <T, R : Number?> Pivot<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { sumFor(separate, skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum
 * of each selected column of the corresponding group.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions], or check out the
 * [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sum`][Pivot.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the "age" values and the sum of the "weight" values
 * df.pivot { city }.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
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
 * Aggregates this [Pivot] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum of all
 * the values in the selected columns of the corresponding group.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions], or check out the
 * [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sum`][Pivot.sum]`()` — the sum of each suitable column separately, per group.
 * - [`sumFor`][Pivot.sumFor] — the sum of each selected column separately, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of all values in the "age" and "weight" columns
 * df.pivot { city }.sum { age and weight }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the sum of.
 * @return A single [DataRow] with, per [pivot] group, the sum of all the values
 *   in the selected columns.
 */
public fun <T, C : Number?> Pivot<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { sum(skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum of all
 * the values in the selected columns of the corresponding group.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions], or check out the
 * [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sum`][Pivot.sum]`()` — the sum of each suitable column separately, per group.
 * - [`sumFor`][Pivot.sumFor] — the sum of each selected column separately, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of all values in the "age" and "weight" columns
 * df.pivot { city }.sum("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @include [SumDocs.SkipNanParam]
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
 * Aggregates this [Pivot] by computing the sum of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the sum
 * of the expression's results for the rows of the corresponding group.
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`sum`][Pivot.sum]` { columns }` — a single sum of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the sum of the weight-to-age ratios
 * df.pivot { city }.sumOf { (weight ?: 0) / age }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
 * @return A single [DataRow] with, per [pivot] group, the sum of the expression's results.
 */
public inline fun <T, reified R : Number?> Pivot<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { sumOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the sum of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
 * the other columns are simply left out of the result.
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sumFor`][PivotGroupBy.sumFor] — the same, but for an explicit selection of columns.
 * - [`sum`][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.sum()
 * ```
 *
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
 * @return A [DataFrame] with the sum of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

/**
 * Aggregates this [PivotGroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SumDocs.AggregateColumnsSelectorSnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sum`][PivotGroupBy.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of the "age" values and the sum of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.sumFor { age and weight }
 * ```
 *
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns to compute the sum of.
 * @return A [DataFrame] with the sum of each selected column per group.
 */
public fun <T, R : Number?> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, separate, columns)

/**
 * Aggregates this [PivotGroupBy] by computing the sum of the values of
 * each selected column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of each selected column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumForSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sum`][PivotGroupBy.sum]`()` — the same, but for all suitable columns at once.
 * - [`sum`][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of the "age" values and the sum of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.sumFor("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @include [SumDocs.SeparateParam]
 * @include [SumDocs.SkipNanParam]
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
 * Aggregates this [PivotGroupBy] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of all the values in the selected columns
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sum`][PivotGroupBy.sum]`()` — the sum of each suitable column separately, per group.
 * - [`sumFor`][PivotGroupBy.sumFor] — the sum of each selected column separately, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.sum { age and weight }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [columns] The [ColumnsSelector] used to select the columns to compute the sum of.
 * @return A [DataFrame] with, per group, the sum of all the values in the selected columns.
 */
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, columns)

/**
 * Aggregates this [PivotGroupBy] by computing a single sum of all the values
 * in the selected columns, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of all the values in the selected columns
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * See [Selecting Columns][SumDocs.SumSelectingOptions], or check out the
 * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sum`][PivotGroupBy.sum]`()` — the sum of each suitable column separately, per group.
 * - [`sumFor`][PivotGroupBy.sumFor] — the sum of each selected column separately, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.sum("age", "weight")
 * ```
 *
 * @param [columns] The names of the columns to compute the sum of.
 * @include [SumDocs.SkipNanParam]
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
 * Aggregates this [PivotGroupBy] by computing the sum of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the sum of the expression's results for the
 * rows of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * {@include [SumDocs.RowExpressionSnippet]}
 *
 * {@include [SumDocs.SupportedTypesSnippet]}
 *
 * {@include [SumDocs.ZeroCellOnEmptySnippet]}
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`sum`][PivotGroupBy.sum]` { columns }` — a single sum of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [SumDocsLink]} — an overview of all `sum` modes.
 *
 * For more information: {@include [DocumentationUrls.PivotStatistics]},
 * {@include [DocumentationUrls.PivotAggregationStatistics]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the sum of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.sumOf { (weight ?: 0) / age }
 * ```
 *
 * @include [SumDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to sum for each row.
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
