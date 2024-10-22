package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

internal class ColumnAccessorImpl<T>(val path: ColumnPath) : ColumnAccessor<T> {

    override fun name() = path.last()

    override fun path() = path

    constructor(vararg path: String) : this(path.toPath())

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
        // resolve the n-1 first columns of the path as column groups (throwing an exception if any of them is not a column group)
        path.dropLast().foldIndexed(context.df) { i, df, colName ->
            val col = df.getColumn<Any?>(colName, context.unresolvedColumnsPolicy) ?: return null
            if (!col.isColumnGroup()) {
                error(
                    "Cannot resolve column '${path.subList(0, i + 2).joinToString(".")}': " +
                        "Column '${path.subList(0, i + 1).joinToString(".")}' is not a column group.",
                )
            } else {
                col
            }
        }
            // resolve the last column of the path
            .getColumn<Any?>(path.last(), context.unresolvedColumnsPolicy)
            ?.cast<T>()
            ?.addPath(path)

    override fun rename(newName: String) = ColumnAccessorImpl<T>(path.dropLast(1) + newName)

    override fun <C> get(column: ColumnReference<C>) = ColumnAccessorImpl<C>(path + column.path())

    override fun getValue(row: AnyRow) = path.getValue(row) as T

    override fun getValueOrNull(row: AnyRow) = path.getValueOrNull(row) as T
}
