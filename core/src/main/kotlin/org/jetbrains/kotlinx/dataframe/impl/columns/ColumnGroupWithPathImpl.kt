package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

internal class ColumnGroupWithPathImpl<T> internal constructor(
    val column: ColumnGroup<T>,
    override val path: ColumnPath
) : ColumnWithPath<DataRow<T>>, ColumnGroupImpl<T>(column.name, column) {

    override fun rename(newName: String) = if (newName == name()) this else ColumnGroupWithPathImpl(
        column.rename(
            newName
        ),
        path.dropLast(1) + newName
    )

    override val data: DataColumn<DataRow<T>>
        get() = column as DataColumn<DataRow<T>>

    override fun path() = path
}
