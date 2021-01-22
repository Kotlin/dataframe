package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.MapColumn
import org.jetbrains.dataframe.api.columns.TableColumn

internal class TableWithParentColumn<T>(parent: MapColumn<*>, source: TableColumn<T>) : DataColumnWithParentImpl<DataFrame<T>>(parent, source), TableColumn<T> by source {

    override fun kind() = super<TableColumn>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataFrame<T>>> {
        return super<DataColumnWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataFrame<T>>? {
        return super<DataColumnWithParentImpl>.resolveSingle(context)
    }
}