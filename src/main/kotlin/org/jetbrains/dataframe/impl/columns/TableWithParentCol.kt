package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.TableCol

internal class TableWithParentCol<T>(parent: GroupedCol<*>, source: TableCol<T>) : DataColWithParentImpl<DataFrame<T>>(parent, source), TableCol<T> by source {

    override fun kind() = super<TableCol>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataFrame<T>>> {
        return super<DataColWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataFrame<T>>? {
        return super<DataColWithParentImpl>.resolveSingle(context)
    }
}