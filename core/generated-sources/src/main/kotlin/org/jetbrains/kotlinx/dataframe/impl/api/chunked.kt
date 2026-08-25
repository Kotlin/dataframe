package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices

/**
 * Creates a [<code>FrameColumn</code>][FrameColumn] from [<code>this</code>][this] by splitting the dataframe into
 * smaller ones, based on the given [<code>startIndices</code>][startIndices].
 */
internal fun <T> DataFrame<T>.chunkedImpl(startIndices: Iterable<Int>, name: String = "groups"): FrameColumn<T> =
    DataColumn.createFrameColumn(
        name = name,
        groups = this.splitByIndices(startIndices.asSequence()).toList(),
        schema = lazy { this.schema() },
    )
