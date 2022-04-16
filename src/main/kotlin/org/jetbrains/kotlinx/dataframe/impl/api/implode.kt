package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapTo
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.extractDataFrame
import org.jetbrains.kotlinx.dataframe.impl.getListType
import kotlin.reflect.typeOf

internal fun <T, C> DataFrame<T>.implodeImpl(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> {
    return groupBy { except(columns) }.updateGroups {
        replace(columns).with { column ->
            val (value, type) = when (column.kind()) {
                ColumnKind.Value -> (if (dropNA) column.dropNA() else column).toList() to getListType(column.type())
                ColumnKind.Group -> column.asColumnGroup().extractDataFrame() to typeOf<AnyFrame>()
                ColumnKind.Frame -> column.asAnyFrameColumn().concat() to typeOf<List<AnyFrame>>()
            }
            var first = true
            column.mapTo(type) {
                if (first) {
                    first = false
                    value
                } else null
            }
        }[0..0]
    }.concat()
}
