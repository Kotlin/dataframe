package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mapNotNullGroups
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columns.asGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asTable
import org.jetbrains.kotlinx.dataframe.toMany

internal fun <T, C> DataFrame<T>.mergeRowsImpl(dropNulls: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> {
    return groupBy { except(columns) }.mapNotNullGroups {
        replace(columns).with {
            val column = it
            val filterNulls = dropNulls && column.hasNulls()
            val value = when (column.kind()) {
                org.jetbrains.kotlinx.dataframe.ColumnKind.Value -> column.toList().let { if (filterNulls) (it as List<Any?>).filterNotNull() else it }.toMany()
                org.jetbrains.kotlinx.dataframe.ColumnKind.Group -> column.asGroup().df
                org.jetbrains.kotlinx.dataframe.ColumnKind.Frame -> column.asTable().values.concat()
            }
            var first = true
            column.map {
                if (first) {
                    first = false
                    value
                } else null
            }
        }[0..0]
    }.union()
}
