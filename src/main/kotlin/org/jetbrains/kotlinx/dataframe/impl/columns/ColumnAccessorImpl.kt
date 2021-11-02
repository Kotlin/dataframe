package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

internal class ColumnAccessorImpl<T>(val path: ColumnPath) : ColumnAccessor<T> {

    override fun name() = path.last()

    override fun path() = path

    constructor(vararg path: String) : this(path.toPath())

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        var df = context.df
        var col: AnyCol? = null
        for (colName in path) {
            col = df.getColumn<Any?>(colName, context.unresolvedColumnsPolicy) ?: return null
            if (col.isColumnGroup()) {
                df = col.asColumnGroup().df
            }
        }
        return col?.cast<T>()?.addPath(path, context.df)
    }

    override fun rename(newName: String) = ColumnAccessorImpl<T>(path.dropLast(1) + newName)

    override fun <C> get(column: ColumnReference<C>) = ColumnAccessorImpl<C>(path + column.path())

    override fun getValue(row: AnyRow) = row.get(path) as T
}
