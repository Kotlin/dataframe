package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.extractDataFrame

internal fun <T, C> DataFrame<T>.implodeImpl(dropNulls: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> {
    return groupBy { except(columns) }.updateGroups {
        replace(columns).with { column ->
            val filterNulls = dropNulls && column.hasNulls()
            val value = when (column.kind()) {
                ColumnKind.Value -> column.toList().let { if (filterNulls) (it as List<*>).filterNotNull() else it }.asList()
                ColumnKind.Group -> column.asColumnGroup().extractDataFrame()
                ColumnKind.Frame -> column.asFrameColumn().concat()
            }
            var first = true
            column.map {
                if (first) {
                    first = false
                    value
                } else null
            }
        }[0..0]
    }.concat()
}
