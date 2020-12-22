package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import org.jetbrains.dataframe.api.columns.ValueColumn
import org.jetbrains.dataframe.checkEquals

internal class TableColumnWithParent<T>(parent: GroupedColumn<*>, source: TableColumn<T>) : ColumnDataWithParentImpl<DataFrame<T>>(parent, source), TableColumn<T> by source {

    override fun kind() = super<TableColumn>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataFrame<T>>> {
        return super<ColumnDataWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataFrame<T>>? {
        return super<ColumnDataWithParentImpl>.resolveSingle(context)
    }
}