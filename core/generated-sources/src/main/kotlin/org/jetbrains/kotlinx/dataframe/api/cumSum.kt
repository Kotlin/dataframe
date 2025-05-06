package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.math.cumSumImpl
import kotlin.reflect.KProperty

// region DataColumn

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumShort")
public fun DataColumn<Short>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumNullableShort")
public fun DataColumn<Short?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int?> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumByte")
public fun DataColumn<Byte>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumNullableByte")
public fun DataColumn<Byte?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int?> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumDouble")
public fun DataColumn<Double?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Double> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
@JvmName("cumSumFloat")
public fun DataColumn<Float?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Float> =
    cumSumImpl(type(), skipNA).cast()

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataColumn]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataColumn] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
public fun <T : Number?> DataColumn<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<T> =
    cumSumImpl(type(), skipNA).cast()

// endregion

// region DataFrame

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataFrame]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataFrame] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
public fun <T, C : Number?> DataFrame<T>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = convert(columns).asColumn { it.cumSum(skipNA) }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataFrame]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataFrame] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
public fun <T> DataFrame<T>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) { columns.toColumnSet().cast() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataFrame]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataFrame] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T> DataFrame<T>.cumSum(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): DataFrame<T> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataFrame]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataFrame] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T> DataFrame<T>.cumSum(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): DataFrame<T> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [DataFrame]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [DataFrame] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
public fun <T> DataFrame<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) {
        // TODO keep at any depth?
        colsAtAnyDepth { it.isNumber() }.cast()
    }

// endregion

// region GroupBy

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [GroupBy]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [GroupBy] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
public fun <T, G, C : Number?> GroupBy<T, G>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<G, C>,
): GroupBy<T, G> = updateGroups { cumSum(skipNA, columns) }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [GroupBy]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [GroupBy] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
public fun <T, G> GroupBy<T, G>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) { columns.toColumnSet().cast() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [GroupBy]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [GroupBy] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [GroupBy]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * @param [columns]
 * The names of the columns to apply cumSum operation.
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [GroupBy] of the same type with the cumulative sums.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 */
@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the [GroupBy]
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 *
 *
 * @param [skipNA] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new [GroupBy] of the same type with the cumulative sums.
 *
 *
 * @see <a href="https://kotlin.github.io/dataframe/cumsum.html">See `cumSum` on the documentation website.</a>
 *
 *
 */
public fun <T, G> GroupBy<T, G>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) {
        // TODO keep at any depth?
        colsAtAnyDepth { it.isNumber() }.cast()
    }

// endregion
