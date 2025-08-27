package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.chunkedImpl
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type

/**
 * Splits this [DataFrame] into consecutive chunks of up to [size] rows
 * and returns them as a [FrameColumn].
 *
 * Each element of the resulting [FrameColumn] is a sub-[DataFrame] containing
 * at most [size] rows. Chunks are formed in order, without overlap.
 *
 * @param [size] Maximum number of rows in each chunk. Must be positive.
 * @param [name] Name of the resulting [FrameColumn]. Defaults to `"groups"`.
 * @return A [FrameColumn] where each value is a sub-[DataFrame] chunk.
 */
public fun <T> DataFrame<T>.chunked(size: Int, name: String = "groups"): FrameColumn<T> =
    chunked(startIndices = 0 until nrow step size, name = name)

/**
 * Splits this [DataFrame] into chunks starting at the given [startIndices].
 *
 * The chunk starting at index `i` ends right before the next start index
 * or the end of the [DataFrame].
 * Use this overload when you need custom chunk boundaries.
 *
 * @param [startIndices] Zero-based row indices where each new chunk starts.
 * @param [name] Name of the resulting [FrameColumn]. Defaults to `"groups"`.
 * @return A [FrameColumn] where each value is a sub-[DataFrame] chunk.
 */
public fun <T> DataFrame<T>.chunked(startIndices: Iterable<Int>, name: String = "groups"): FrameColumn<T> =
    chunkedImpl(startIndices, name)

/**
 * Groups consecutive values of this [DataColumn] into lists of at most [size] elements.
 *
 * This works like [kotlin.collections.chunked], but returns a [ValueColumn] instead of a [List].
 *
 * @param [size] Maximum number of elements in each chunk. Must be positive.
 * @return A [ValueColumn] whose elements are lists representing chunks of the original values.
 */
public fun <T> DataColumn<T>.chunked(size: Int): ValueColumn<List<T>> {
    val values = toList().chunked(size)
    return DataColumn.createValueColumn(name(), values, getListType(type))
}

/**
 * Splits this [ColumnGroup] into a [FrameColumn] of sub-dataframes
 * with up to [size] rows in each chunk.
 *
 * The resulting [FrameColumn] inherits the name of this group.
 *
 * @param [size] Maximum number of rows in each sub-dataframe. Must be positive.
 * @return A [FrameColumn] where each value is a sub-[DataFrame] chunk.
 */
public fun <T> ColumnGroup<T>.chunked(size: Int): FrameColumn<T> = chunked(size, name())

/**
 * Splits a [DataColumn] of [DataRow] into a [FrameColumn] of sub-dataframes
 * with up to [size] rows in each chunk.
 *
 * This is a convenience overload that treats a [DataColumn] of rows
 * as if it were a [ColumnGroup] (see [ColumnGroup.chunked]).
 *
 * @param [size] Maximum number of rows in each sub-dataframe. Must be positive.
 * @return A [FrameColumn] where each value is a sub-[DataFrame] chunk.
 */
public fun <T> DataColumn<DataRow<T>>.chunked(size: Int): FrameColumn<T> = asColumnGroup().chunked(size)
