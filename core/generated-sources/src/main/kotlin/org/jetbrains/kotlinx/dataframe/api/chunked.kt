package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.api.chunkedImpl
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type

/**
 * Splits this [<code>DataFrame</code>][DataFrame] into consecutive chunks of up to [<code>size</code>][size] rows
 * and returns them as a [<code>FrameColumn</code>][FrameColumn].
 *
 * Each element of the resulting [<code>FrameColumn</code>][FrameColumn] is a sub-[<code>DataFrame</code>][DataFrame] containing
 * at most [<code>size</code>][size] rows. Chunks are formed in order, without overlap.
 *
 * For more information: [See `chunked` on the documentation website.](https://kotlin.github.io/dataframe/chunked.html)
 *
 * @param [size] Maximum number of rows in each chunk. Must be positive.
 * @param [name] Name of the resulting [<code>FrameColumn</code>][FrameColumn]. Defaults to `"groups"`.
 * @return A [<code>FrameColumn</code>][FrameColumn] where each value is a sub-[<code>DataFrame</code>][DataFrame] chunk.
 */
public fun <T> DataFrame<T>.chunked(size: Int, name: String = "groups"): FrameColumn<T> =
    chunked(startIndices = 0 until nrow step size, name = name)

/**
 * Splits this [<code>DataFrame</code>][DataFrame] into chunks starting at the given [<code>startIndices</code>][startIndices].
 *
 * The chunk starting at index `i` ends right before the next start index
 * or the end of the [<code>DataFrame</code>][DataFrame].
 * Use this overload when you need custom chunk boundaries.
 *
 * For more information: [See `chunked` on the documentation website.](https://kotlin.github.io/dataframe/chunked.html)
 *
 * @param [startIndices] Zero-based row indices where each new chunk starts.
 * @param [name] Name of the resulting [<code>FrameColumn</code>][FrameColumn]. Defaults to `"groups"`.
 * @return A [<code>FrameColumn</code>][FrameColumn] where each value is a sub-[<code>DataFrame</code>][DataFrame] chunk.
 */
public fun <T> DataFrame<T>.chunked(startIndices: Iterable<Int>, name: String = "groups"): FrameColumn<T> =
    chunkedImpl(startIndices, name)

/**
 * Groups consecutive values of this [<code>DataColumn</code>][DataColumn] into lists of at most [<code>size</code>][size] elements.
 *
 * This works like [<code>kotlin.collections.chunked</code>][kotlin.collections.chunked], but returns a [<code>ValueColumn</code>][ValueColumn] instead of a [<code>List</code>][List].
 *
 * For more information: [See `chunked` on the documentation website.](https://kotlin.github.io/dataframe/chunked.html)
 *
 * @param [size] Maximum number of elements in each chunk. Must be positive.
 * @return A [<code>ValueColumn</code>][ValueColumn] whose elements are lists representing chunks of the original values.
 */
public fun <T> DataColumn<T>.chunked(size: Int): ValueColumn<List<T>> {
    val values = toList().chunked(size)
    return DataColumn.createValueColumn(name(), values, getListType(type))
}

/**
 * Splits this [<code>ColumnGroup</code>][ColumnGroup] into a [<code>FrameColumn</code>][FrameColumn] of sub-dataframes
 * with up to [<code>size</code>][size] rows in each chunk.
 *
 * The resulting [<code>FrameColumn</code>][FrameColumn] inherits the name of this group.
 *
 * For more information: [See `chunked` on the documentation website.](https://kotlin.github.io/dataframe/chunked.html)
 *
 * @param [size] Maximum number of rows in each sub-dataframe. Must be positive.
 * @return A [<code>FrameColumn</code>][FrameColumn] where each value is a sub-[<code>DataFrame</code>][DataFrame] chunk.
 */
public fun <T> ColumnGroup<T>.chunked(size: Int): FrameColumn<T> = chunked(size, name())

/**
 * Splits a [<code>DataColumn</code>][DataColumn] of [<code>DataRow</code>][DataRow] into a [<code>FrameColumn</code>][FrameColumn] of sub-dataframes
 * with up to [<code>size</code>][size] rows in each chunk.
 *
 * This is a convenience overload that treats a [<code>DataColumn</code>][DataColumn] of rows
 * as if it were a [<code>ColumnGroup</code>][ColumnGroup] (see [<code>ColumnGroup.chunked</code>][ColumnGroup.chunked]).
 *
 * For more information: [See `chunked` on the documentation website.](https://kotlin.github.io/dataframe/chunked.html)
 *
 * @param [size] Maximum number of rows in each sub-dataframe. Must be positive.
 * @return A [<code>FrameColumn</code>][FrameColumn] where each value is a sub-[<code>DataFrame</code>][DataFrame] chunk.
 */
public fun <T> DataColumn<DataRow<T>>.chunked(size: Int): FrameColumn<T> = asColumnGroup().chunked(size)
