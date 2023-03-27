package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Reorder
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.reorder
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import kotlin.reflect.typeOf

internal fun <T, C, V : Comparable<V>> Reorder<T, C>.reorderImpl(
    desc: Boolean,
    expression: ColumnExpression<C, V>
): DataFrame<T> {
    data class ColumnInfo(
        val treeNode: TreeNode<ColumnPosition>,
        val column: DataColumn<C>,
        val value: V,
        val index: Int
    )

    val columnsWithPaths = df.getColumnsWithPaths(columns)
    if (columnsWithPaths.size == 1 && columnsWithPaths[0].isColumnGroup()) {
        val path = columnsWithPaths[0].path
        return df.reorder { path.all().cast<C>() }.reorderImpl(desc, expression)
    }

    var df = df

    columnsWithPaths
        .groupBy({ it.path.parent()!! }) { it.name() }
        .forEach { (parentPath, names) ->
            val group = if (parentPath.isEmpty()) df else df.getColumnGroup(parentPath)

            val removed = group.removeImpl(false) { names.toColumnSet() }

            val mapped = removed.removedColumns
                .sortedBy { group.getColumnIndex(it.name) }
                .mapIndexed { i, treeNode ->
                    val column = treeNode.data.column!!.cast<C>()
                    ColumnInfo(treeNode, column, expression(column, column), i)
                }

            val sorted = if (desc) mapped.sortedByDescending { it.value } else mapped.sortedBy { it.value }

            val toInsert = sorted.mapIndexed { i, c ->
                val src = mapped[i]
                val path = src.treeNode.pathFromRoot().rename(c.column.name())
                var column = c.column
                if (inFrameColumns && column.isFrameColumn()) {
                    column = column.asAnyFrameColumn().map(typeOf<AnyFrame>()) { it.cast<T>().reorder(columns).reorderImpl(desc, expression) }.cast()
                }
                ColumnToInsert(path, column, src.treeNode)
            }
            val newGroup = removed.df.insertImpl(toInsert)
            df = if (parentPath.isEmpty()) newGroup.cast()
            else df.replace(parentPath).with { newGroup.asColumnGroup(it.name()) }
        }
    return df
}
