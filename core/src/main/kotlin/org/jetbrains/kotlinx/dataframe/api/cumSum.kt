package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.math.cumSumImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * ## The CumSum Operation
 *
 * Computes the cumulative sums of the values in each column from the {@get [CumSumDocs.DATA_TYPE]}
 * from the first cell to the last cell.
 *
 * __NOTE:__ If the column contains nullable values and [skipNA\] is set to `true`,
 * null and NaN values are skipped when computing the cumulative sum.
 * When false, all values after the first NA will be NaN (for Double and Float columns)
 * or null (for integer columns).
 *
 * {@get [CumSumDocs.CUMSUM_PARAM] @param [columns\]
 * The names of the columns to apply cumSum operation.}
 *
 * @param [skipNA\] Whether to skip null and NaN values (default: `true`).
 *
 * @return A new {@get [CumSumDocs.DATA_TYPE]} of the same type with the cumulative sums.
 *
 * {@get [CumSumDocs.CUMSUM_PARAM] @see [Selecting Columns][SelectSelectingOptions].}
 * @see {@include [DocumentationUrls.CumSum]}
 */
@ExcludeFromSources
@Suppress("ClassName")
private interface CumSumDocs {
    interface CUMSUM_PARAM

    interface DATA_TYPE
}

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumShort")
public fun DataColumn<Short>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumNullableShort")
public fun DataColumn<Short?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int?> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumByte")
public fun DataColumn<Byte>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumNullableByte")
public fun DataColumn<Byte?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Int?> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumDouble")
public fun DataColumn<Double?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Double> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
@JvmName("cumSumFloat")
public fun DataColumn<Float?>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<Float> =
    cumSumImpl(type(), skipNA).cast()

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataColumn]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
public fun <T : Number?> DataColumn<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<T> =
    cumSumImpl(type(), skipNA).cast()

// endregion

// region DataFrame

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataFrame]}
 */
public fun <T, C : Number?> DataFrame<T>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = convert(columns).asColumn { it.cumSum(skipNA) }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataFrame]}
 */
public fun <T> DataFrame<T>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) { columns.toColumnSet().cast() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataFrame]}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.cumSum(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): DataFrame<T> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataFrame]}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.cumSum(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): DataFrame<T> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [DataFrame]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
public fun <T> DataFrame<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) {
        // TODO keep at any depth?
        colsAtAnyDepth().filter { it.isNumber() }.cast()
    }

// endregion

// region GroupBy

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [GroupBy]}
 */
public fun <T, G, C : Number?> GroupBy<T, G>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<G, C>,
): GroupBy<T, G> = updateGroups { cumSum(skipNA, columns) }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [GroupBy]}
 */
public fun <T, G> GroupBy<T, G>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) { columns.toColumnSet().cast() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [GroupBy]}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [GroupBy]}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

/**
 * {@include [CumSumDocs]}
 * {@set [CumSumDocs.DATA_TYPE] [GroupBy]}
 * {@set [CumSumDocs.CUMSUM_PARAM]}
 */
public fun <T, G> GroupBy<T, G>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) {
        // TODO keep at any depth?
        colsAtAnyDepth().filter { it.isNumber() }.cast()
    }

// endregion
