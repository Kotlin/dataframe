package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.allRemovedColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.withDf
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.nrow

internal data class RemoveResult<T>(val df: DataFrame<T>, val removedColumns: List<TreeNode<ColumnPosition>>)

internal fun <T> DataFrame<T>.removeImpl(allowMissingColumns: Boolean = false, columns: ColumnsSelector<T, *>): RemoveResult<T> {
    val colWithPaths = getColumnsWithPaths(if (allowMissingColumns) UnresolvedColumnsPolicy.Skip else UnresolvedColumnsPolicy.Fail, columns)
    val colPaths = colWithPaths.map { it.path }
    val originalOrder = colPaths.mapIndexed { index, path -> path to index }.toMap()

    val root = TreeNode.createRoot(ColumnPosition(-1, false, null))

    if (colPaths.isEmpty()) return RemoveResult(this, emptyList())

    fun dfs(cols: Iterable<AnyBaseColumn>, removePaths: List<ColumnWithPath<*>>, node: TreeNode<ColumnPosition>): AnyFrame? {
        if (removePaths.isEmpty()) return null

        val depth = node.depth
        val childrenToRemove = removePaths.groupBy { it.path[depth] }
        val newCols = mutableListOf<AnyBaseColumn>()

        cols.forEachIndexed { index, column ->
            val toRemove = childrenToRemove[column.name()]
            if (toRemove != null) {
                val node = node.addChild(column.name, ColumnPosition(index, true, null))
                if (toRemove.all { it.path.size > depth + 1 }) {
                    val groupCol = (column as ColumnGroup<*>)
                    val newDf = dfs(groupCol.columns(), toRemove, node)
                    if (newDf != null) {
                        val newCol = groupCol.withDf(newDf)
                        newCols.add(newCol)
                        node.data.wasRemoved = false
                    }
                } else {
                    val removedChild = toRemove.single { it.path.size == depth + 1 }
                    node.data.column = removedChild.data
                }
            } else newCols.add(column)
        }
        if (newCols.isEmpty()) return null
        return newCols.toDataFrame()
    }

    val newDf = dfs(columns(), colWithPaths, root) ?: emptyDataFrame(nrow)

    val removedColumns = root.allRemovedColumns().map { it.pathFromRoot() to it }.sortedBy { originalOrder[it.first] }.map { it.second }

    return RemoveResult(newDf.cast(), removedColumns)
}
