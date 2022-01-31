package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.extractDataFrame

internal fun <T, C> DataFrame<T>.implodeImpl(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> {
    return groupBy { except(columns) }.updateGroups {
        replace(columns).with { column ->
            val value = when (column.kind()) {
                ColumnKind.Value -> (if (dropNA) column.dropNA() else column).toList()
                ColumnKind.Group -> column.asColumnGroup().extractDataFrame()
                ColumnKind.Frame -> column.asAnyFrameColumn().concat()
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
