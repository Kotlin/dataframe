package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar
import org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.corrImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/**
 * Calculates the correlation between values in the specified [columns\].
 *
 * This function does not compute the correlation immediately.
 * Instead, it defines the primary set of columns
 * and returns a [Corr] instance that allows configuring how the correlation should be computed.
 *
 * The [Corr] object provides two methods to perform correlation calculations:
 * - [with][Corr.with] — computes correlations between the initially selected columns and a second set of columns.
 * - [withItself][Corr.withItself] — computes pairwise correlations within the initially selected columns.
 *
 * Each method returns a square or rectangular correlation matrix represented as a [DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the correlation coefficient between the corresponding pair of columns.
 *
 * To compute correlations between all suitable columns in the [DataFrame], use [DataFrame.corr()][DataFrame.corr].
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See also: [Selecting Columns][SelectingOptions].
 *
 * For more information, see: {@include [DocumentationUrls.Corr]}
 */
internal interface CorrDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetCorrOperationArg]}
     */
    interface SelectingOptions

    /**
     * ## Corr Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`corr`][convert]**`  { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * {@include [Indent]}
     * __`.`__[**`with`**][Corr.with]` { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * {@include [Indent]}
     *`| `__`.`__[**`withItself`**][Corr.withItself]`()`
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [corr][corr]} */
@ExcludeFromSources
private interface SetCorrOperationArg

/**
 * {@include [CorrDocs]}
 * ### This Corr Overload
 */
@ExcludeFromSources
private interface CommonCorrDocs

internal fun AnyCol.isSuitableForCorr() = isSubtypeOf<Number>() || type() == typeOf<Boolean>()

// region DataFrame

/**
 * An intermediate class used in the [corr] operation.
 *
 * This class does not perform any computation by itself — it serves as a transitional step
 * before specifying how the correlation should be calculated.
 * It must be followed by one of the computation methods to produce a correlation [DataFrame].
 *
 * The resulting [DataFrame] is a correlation matrix where rows correspond to one set of columns,
 * columns to the other set, and each cell contains the correlation coefficient
 * between the respective pair of columns.
 *
 * Use the following methods to perform the computation:
 * - [with] — selects a second set of columns and computes correlations between
 *   the initially selected columns and this second set.
 * - [withItself] — computes pairwise correlations within the initially selected columns.
 *
 * See [Grammar][CorrDocs.Grammar] for more details.
 */
public data class Corr<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>)

/**
 * Computes the correlation matrix between all suitable columns in this [DataFrame],
 * including nested columns at any depth.
 *
 * The result is a square correlation matrix represented as a [DataFrame],
 * where both rows and columns correspond to the original columns,
 * and each cell contains the correlation coefficient between the respective pair of columns.
 *
 * Only columns suitable for correlation (e.g., numeric types) are included in the result.
 *
 * For more information, see: {@include [DocumentationUrls.Corr]}
 *
 * @return A square correlation matrix as a [DataFrame], where both rows and columns correspond to the original columns.
 */
public fun <T> DataFrame<T>.corr(): DataFrame<T> =
    corr {
        colsAtAnyDepth().filter { it.isSuitableForCorr() }
    }.withItself()

/**
 * {@include [CommonCorrDocs]}
 * @include [SelectingColumns.Dsl] {@include [SetCorrOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute pairwise correlations between all columns of type `Number`
 * df.corr { colsOf<Number>() }.withItself()
 * ```
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns
 * of this [DataFrame] to compute a correlation.
 * @return A [Corr] intermediate object with the selected columns.
 */
public fun <T, C> DataFrame<T>.corr(columns: ColumnsSelector<T, C>): Corr<T, C> = Corr(this, columns)

/**
 * {@include [CommonCorrDocs]}
 * @include [SelectingColumns.ColumnNames] {@include [SetCorrOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute pairwise correlations between all columns of type `Number`
 * df.corr { colsOf<Number>() }.withItself()
 * ```
 * @param [columns\] The [Column Names][String] used to select the columns
 * of this [DataFrame] to compute a correlation.
 * @return A [Corr] intermediate object with the selected columns.
 */
public fun <T> DataFrame<T>.corr(vararg columns: String): Corr<T, Any?> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: KProperty<C>): Corr<T, C> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: ColumnReference<C>): Corr<T, C> = corr { columns.toColumnSet() }

/**
 * Calculates the correlation of specified [columns][otherColumns]
 * with values in the columns previously selected with [corr].
 *
 * Returns a correlation matrix represented as a [DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the correlation coefficient between the corresponding pair of columns.
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See also: [Selecting Columns][SelectingOptions].
 *
 * For more information, see: {@include [DocumentationUrls.Corr]}
 */
internal interface CorrWithDocs

/**
 * {@include [CorrWithDocs]}
 * ### This Corr With Overload
 */
@ExcludeFromSources
private interface CommonCorrWithDocs

/**
 * {@include [CommonCorrWithDocs]}
 * @include [SelectingColumns.Dsl] {@include [SetCorrOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute correlations between the "speed" column and all columns of type `Double` (excluding itself)
 * df.corr { speed }.with { colsOf<Double>() except speed }
 * ```
 *
 * @param otherColumns The [ColumnsSelector] used to select the second set of columns
 * from this [DataFrame] to compute correlations against the initially selected columns.
 * @return A [DataFrame] containing the resulting correlation matrix.
 */
public fun <T, C, R> Corr<T, C>.with(otherColumns: ColumnsSelector<T, R>): DataFrame<T> = corrImpl(otherColumns)

/**
 * {@include [CommonCorrWithDocs]}
 * @include [SelectingColumns.ColumnNames] {@include [SetCorrOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr("age").with("weight", "height")
 *
 * // Compute correlations between the "speed" column and all columns of type `Number`
 * df.corr { colsOf<Number>() }.with("speed")
 * ```
 *
 * @param otherColumns The [Column Names][String] used to select the second set of columns
 * from this [DataFrame] to compute correlations against the initially selected columns.
 * @return A [DataFrame] containing the resulting correlation matrix.
 */
public fun <T, C> Corr<T, C>.with(vararg otherColumns: String): DataFrame<T> = with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: KProperty<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: ColumnReference<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

/**
 * Calculates pairwise correlations between the columns
 * previously selected with [corr].
 *
 * Returns a square correlation matrix represented as a [DataFrame],
 * where both rows and columns correspond to the selected columns,
 * and each cell contains the correlation coefficient between the respective pair of columns.
 *
 * Check out [Grammar].
 *
 * For more information, see: {@include [DocumentationUrls.Corr]}
 *
 * @return A [DataFrame] containing the pairwise correlation matrix.
 */
public fun <T, C> Corr<T, C>.withItself(): DataFrame<T> = with(columns)

// endregion
