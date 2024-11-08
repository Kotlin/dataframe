package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices

/**
 * Creates a [FrameColumn] from [this] by splitting the dataframe into
 * smaller ones, based on the given [startIndices].
 */
internal fun <T> DataFrame<T>.chunkedImpl(startIndices: Iterable<Int>, name: String = "groups"): FrameColumn<T> =
    DataColumn.createFrameColumn(
        name = name,
        groups = this.splitByIndices(startIndices.asSequence()).toList(),
        schema = lazy { this.schema() },
    )
