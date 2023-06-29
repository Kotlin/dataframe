package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.RenameClause
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.allChildrenNotNull
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.collectTree
import org.jetbrains.kotlinx.dataframe.kind

internal fun <T, C> RenameClause<T, C>.renameImpl(newNames: Array<out String>): DataFrame<T> {
    var i = 0
    return renameImpl { newNames[i++] }
}

internal fun <T, C> RenameClause<T, C>.renameImpl(transform: (ColumnWithPath<C>) -> String): DataFrame<T> {
    val selectedColumns = df.getColumnsWithPaths(columns)
    val tree = df.getColumnsWithPaths { all().rec() }.collectTree()

    // perform rename in nodes
    tree.allChildrenNotNull().forEach { node ->
        val column = selectedColumns.find { it.data == node.data } ?: return@forEach
        val newName = transform(column)
        node.name = newName
    }

    // build up a new DataFrame using the modified names
    var newDf = DataFrame.empty(df.rowsCount()).cast<T>()
    tree.allChildrenNotNull().forEach { node ->
        val path = node.pathFromRoot().dropLast(1)
        val col = node.data.rename(node.name)

        when (col.kind) {
            ColumnKind.Value, ColumnKind.Frame ->
                newDf = newDf.insert(col).under(path)

            ColumnKind.Group -> Unit
        }
    }

    return newDf
}
