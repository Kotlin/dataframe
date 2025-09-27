package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.MoveClause
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.getOrPut

// TODO: support 'before' mode
internal fun <T, C> MoveClause<T, C>.afterOrBefore(column: ColumnSelector<T, *>, isAfter: Boolean): DataFrame<T> {
    val removeResult = if (isAfter) df.removeImpl(columns = columns) else df.removeImpl(columns = column)  //what remains after the removal

    val targetPath = if (isAfter) df.getColumnWithPath(column).path else df.getColumnsWithPaths(columns).map { it.path }
    val sourcePaths = removeResult.removedColumns.map { it.toColumnWithPath<C>().path }

    // Check if any source path is a prefix of the target path
    sourcePaths.forEach { sourcePath ->
        val sourceSegments = sourcePath.toList()
        val targetSegments = targetPath.toList()

        if (sourceSegments.size <= targetSegments.size &&
            sourceSegments.indices.all { targetSegments[it] == sourceSegments[it] }
        ) {
            throw IllegalArgumentException(
                "Cannot move column '${sourcePath.joinToString()}' after its own child column '${targetPath.joinToString()}'",
            )
        }
    }

    val removeRoot = removeResult.removedColumns.first().getRoot() //first column to insert, a TreeNode (string, depth(int)..)
    //finding the first common node between target and inserting
    val effectivePath = if (isAfter) targetPath else targetPath.first()
    effectivePath as ColumnPath
    val refNode = removeRoot.getOrPut(effectivePath) {  //the TreeNode<ColumnPosition>, first node that target and inserting c. have in common,
        val path = it.asList()                       //df if they both at top

        //Get parent of a target path
        val effectivePath = path.dropLast(1)

        // Get column name (last segment)
        val columnName = path.last()

        // Get the parent
        val parent = if (effectivePath.isEmpty()) {
            df
        } else {
            df.getColumnGroup(ColumnPath(effectivePath))
        }

        // Get the column index and the column itself
        val index = parent.getColumnIndex(columnName)
        val col = parent.getColumn(index)

        ColumnPosition(index, false, col)
    }
    //final step,
    val parentPath = effectivePath.dropLast(1)
    val toInsert = removeResult.removedColumns.map {
        val sourceCol = it.toColumnWithPath<C>()
        val sourcePath = sourceCol.path //path of each column to insert
        val path = if (sourcePath.size > 1) {
            // If source is nested, preserve its structure under the target parent
            parentPath + sourcePath.last()
        } else {
            parentPath + sourceCol.name()
        }
        ColumnToInsert(path, sourceCol.data, refNode)
    }
    return removeResult.df.insertImpl(toInsert)//automatically insert after!
    //idea: insertImpl(List<ColumnToInsert>) automatically insert after columns that share same path untill the common parent
    //-> (idea1) rather than removing and than reinserting source columns, i remove and reinsert target!
    //OR (idea2) i create a new version of insertImpl
}

internal fun <T, C> MoveClause<T, C>.moveImpl(
    under: Boolean = false,
    newPathExpression: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> AnyColumnReference,
): DataFrame<T> {
    val receiver = object : DataFrameReceiver<T>(df, UnresolvedColumnsPolicy.Fail), ColumnsSelectionDsl<T> {}
    val removeResult = df.removeImpl(columns = columns)
    val columnsToInsert = removeResult.removedColumns.map {
        val col = it.toColumnWithPath<C>()
        var path = newPathExpression(receiver, col).path()
        if (under) path += col.name()
        ColumnToInsert(path, col.data, it)
    }
    return removeResult.df.insertImpl(columnsToInsert)
}

internal fun <T, C> MoveClause<T, C>.moveTo(columnIndex: Int): DataFrame<T> {
    val removed = df.removeImpl(columns = columns)
    val remainingColumns = removed.df.columns()
    val targetIndex = if (columnIndex > remainingColumns.size) remainingColumns.size else columnIndex
    val newColumnList =
        remainingColumns.subList(0, targetIndex) +
            removed.removedColumns.map { it.data.column as DataColumn<C> } +
            if (targetIndex < remainingColumns.size) {
                remainingColumns.subList(targetIndex, remainingColumns.size)
            } else {
                emptyList()
            }
    return newColumnList.toDataFrame().cast()
}
