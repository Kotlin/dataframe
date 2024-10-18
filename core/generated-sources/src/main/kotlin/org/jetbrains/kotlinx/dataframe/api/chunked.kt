package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type

/**
 * Creates a [FrameColumn] from [this] by splitting the dataframe into
 * smaller ones, based on the given [startIndices].
 */
public fun <T> DataFrame<T>.chunked(startIndices: Iterable<Int>, name: String = "groups"): FrameColumn<T> =
    DataColumn.createFrameColumn(
        name = name,
        groups = this.splitByIndices(startIndices.asSequence()).toList(),
        schema = lazy { this.schema() },
    )

/**
 * Creates a [FrameColumn] from [this] by splitting the dataframe into
 * smaller ones, with their number of rows at most [size].
 */
public fun <T> DataFrame<T>.chunked(size: Int, name: String = "groups"): FrameColumn<T> {
    val startIndices = (0 until nrow step size)
    return this.chunked(startIndices, name)
}

public fun <T> DataColumn<T>.chunked(size: Int): ValueColumn<List<T>> {
    val values = toList().chunked(size)
    return DataColumn.createValueColumn(name(), values, getListType(type))
}

public fun <T> ColumnGroup<T>.chunked(size: Int): FrameColumn<T> = chunked(size, name())

public fun <T> DataColumn<DataRow<T>>.chunked(size: Int): FrameColumn<T> = asColumnGroup().chunked(size)
