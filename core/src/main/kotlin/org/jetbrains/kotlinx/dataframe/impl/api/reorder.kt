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
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import kotlin.reflect.typeOf

/**
 * @param reorderNestedColumnsOfSingleGroup when `true` and [Reorder.columns] selects exactly one column group,
 * the nested columns of that group are reordered instead of the group itself.
 * `reorderColumnsBy(atAnyDepth = false)` passes `false`: it must reorder only the top-level columns,
 * even when the only top-level column is a column group.
 */
internal fun <T, C, V : Comparable<V>> Reorder<T, C>.reorderImpl(
    desc: Boolean,
    expression: ColumnExpression<C, V>,
    reorderNestedColumnsOfSingleGroup: Boolean = true,
): DataFrame<T> {
    data class ColumnInfo(
        val treeNode: TreeNode<ColumnPosition>,
        val column: DataColumn<C>,
        val value: V,
        val index: Int,
    )

    val columnsWithPaths = df.getColumnsWithPaths(columns)
    if (reorderNestedColumnsOfSingleGroup && columnsWithPaths.size == 1 && columnsWithPaths[0].isColumnGroup()) {
        val path = columnsWithPaths[0].path
        // `false`: if the only nested column is a column group too, its own nested columns keep their order
        return df.reorder { path.allCols().cast<C>() }
            .reorderImpl(desc, expression, reorderNestedColumnsOfSingleGroup = false)
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
                    column = column.asAnyFrameColumn()
                        // pass `inFrameColumns` on, so that frame columns nested in this frame are reordered too
                        .map(typeOf<AnyFrame>()) {
                            Reorder(it.cast<T>(), columns, inFrameColumns).reorderImpl(desc, expression)
                        }
                        .cast()
                }
                ColumnToInsert(path, column, src.treeNode)
            }
            val newGroup = removed.df.insertImpl(toInsert)
            df = if (parentPath.isEmpty()) {
                newGroup.cast()
            } else {
                df.replace { parentPath }.with { newGroup.asColumnGroup(it.name()) }
            }
        }
    return df
}
