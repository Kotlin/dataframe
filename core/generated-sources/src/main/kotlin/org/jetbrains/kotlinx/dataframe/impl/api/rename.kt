package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.RenameClause
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.allChildrenNotNull
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.collectTree
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.map
import org.jetbrains.kotlinx.dataframe.kind

internal fun <T, C> RenameClause<T, C>.renameImpl(newNames: Array<out String>): DataFrame<T> {
    var i = 0
    return renameImpl { newNames[i++] }
}

internal fun <T, C> RenameClause<T, C>.renameImpl(transform: (ColumnWithPath<C>) -> String): DataFrame<T> {
    // get all selected columns and their paths
    val selectedColumnsWithPath = df.getColumnsWithPaths(columns)
        .associateBy { it.data }
    // gather a tree of all columns where the nodes will be renamed
    val tree = df.getColumnsWithPaths { colsAtAnyDepth() }.collectTree()

    // perform rename in nodes
    tree.allChildrenNotNull().forEach { node ->
        // Check if the current node/column is a selected column and, if so, get its ColumnWithPath
        val column = selectedColumnsWithPath[node.data] ?: return@forEach
        // Use the found selected ColumnWithPath to query for the new name
        val newColumnName = transform(column)
        node.name = newColumnName
    }

    // use the mapping function to convert the tree to a ColumnGroup/ValueColumn structure
    // The result will be a ColumnGroup, since the root node's data is null
    val renamedDfAsColumnGroup = tree.map { node, children ->
        val col = node.data
        when (col?.kind) {
            // if the column is a value column or a frame column, rename it using the node's (new) name
            ColumnKind.Value, ColumnKind.Frame ->
                col.rename(node.name)

            // if the column is a group column, create a new column group using the node's (new) name and children
            // if the column is null, node is the root, so we'll create a column group as well
            ColumnKind.Group, null ->
                children
                    .toDataFrame()
                    .asColumnGroup(node.name)
        }
    } as ColumnGroup<*>

    // convert the created ColumnGroup to a DataFrame
    val renamedDf = renamedDfAsColumnGroup.columns().toDataFrame()
    return renamedDf.cast()
}
