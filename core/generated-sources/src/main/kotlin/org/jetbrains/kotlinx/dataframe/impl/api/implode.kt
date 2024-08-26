package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
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
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.util.ANY_FRAME
import org.jetbrains.kotlinx.dataframe.util.LIST_ANY_FRAME

internal fun <T, C> DataFrame<T>.implodeImpl(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    groupBy { allExcept(columns) }.updateGroups {
        replace(columns).with { column ->
            val (value, type) = when (column.kind()) {
                ColumnKind.Value -> (if (dropNA) column.dropNA() else column).toList() to getListType(column.type())
                ColumnKind.Group -> column.asColumnGroup().extractDataFrame() to ANY_FRAME
                ColumnKind.Frame -> column.asAnyFrameColumn().concat() to LIST_ANY_FRAME
            }
            var first = true
            column.map(type) {
                if (first) {
                    first = false
                    value
                } else {
                    // these rows will not be taken into account,
                    // but we cannot leave them empty, as `map` creates a full column
                    when (column.kind()) {
                        ColumnKind.Value -> emptyList<Any?>()
                        ColumnKind.Group -> DataFrame.empty()
                        ColumnKind.Frame -> emptyList<AnyFrame>()
                    }
                }
            }
        }[0..0] // takes only the first row
    }.concat()
