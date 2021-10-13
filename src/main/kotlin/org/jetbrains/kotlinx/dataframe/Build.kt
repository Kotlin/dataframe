package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.toAnyFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnWithParent

public fun DataFrame.Companion.of(columns: Iterable<AnyColumn>): AnyFrame = dataFrameOf(columns)
public fun DataFrame.Companion.of(vararg header: String): DataFrameBuilder = dataFrameOf(header.toList())
public fun DataFrame.Companion.of(vararg columns: AnyColumn): AnyFrame = dataFrameOf(columns.asIterable())

// TODO: remove checks for ColumnWithParent types
internal fun AnyColumn.unbox(): AnyCol = when (this) {
    is ColumnWithPath<*> -> data.unbox()
    is ColumnWithParent<*> -> source.unbox()
    else -> this as AnyCol
}

public fun Map<String, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map { DataColumn.create(it.key, it.value.asList()) }.toAnyFrame()
}

@JvmName("toDataFrameColumnPathAny?")
public fun Map<ColumnPath, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map { it.key to DataColumn.create(it.key.last(), it.value.asList()) }.toDataFrame<Unit>()
}
