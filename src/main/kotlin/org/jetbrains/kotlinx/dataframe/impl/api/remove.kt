package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumnPaths
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.typed
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.allRemovedColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.withDf

internal data class RemoveResult<T>(val df: DataFrame<T>, val removedColumns: List<TreeNode<ColumnPosition>>)

internal fun <T> DataFrame<T>.removeImpl(selector: ColumnsSelector<T, *>): RemoveResult<T> {
    val colPaths = getColumnPaths(selector)
    val originalOrder = colPaths.mapIndexed { index, path -> path to index }.toMap()

    val root = TreeNode.createRoot(ColumnPosition(-1, false, null))

    if (colPaths.isEmpty()) return RemoveResult(this, emptyList())

    fun dfs(cols: Iterable<AnyColumn>, paths: List<ColumnPath>, node: TreeNode<ColumnPosition>): AnyFrame? {
        if (paths.isEmpty()) return null

        val depth = node.depth
        val children = paths.groupBy { it[depth] }
        val newCols = mutableListOf<AnyColumn>()

        cols.forEachIndexed { index, column ->
            val childPaths = children[column.name()]
            if (childPaths != null) {
                val node = node.addChild(column.name, ColumnPosition(index, true, null))
                if (childPaths.all { it.size > depth + 1 }) {
                    val groupCol = (column as ColumnGroup<*>)
                    val newDf = dfs(groupCol.df.columns(), childPaths, node)
                    if (newDf != null) {
                        val newCol = groupCol.withDf(newDf)
                        newCols.add(newCol)
                        node.data.wasRemoved = false
                    }
                } else {
                    node.data.column = column as AnyCol
                }
            } else newCols.add(column)
        }
        if (newCols.isEmpty()) return null
        return newCols.toDataFrame<Unit>()
    }

    val newDf = dfs(columns(), colPaths, root) ?: emptyDataFrame(nrow())

    val removedColumns = root.allRemovedColumns().map { it.pathFromRoot() to it }.sortedBy { originalOrder[it.first] }.map { it.second }

    return RemoveResult(newDf.typed(), removedColumns)
}
