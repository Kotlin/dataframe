package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.MoveClause
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.getOrPut
import org.jetbrains.kotlinx.dataframe.impl.last
import org.jetbrains.kotlinx.dataframe.path
import kotlin.collections.first

internal fun <T, C> MoveClause<T, C>.afterOrBefore(column: ColumnSelector<T, *>, isAfter: Boolean): DataFrame<T> {
    val removeResult = df.removeImpl(columns = columns)

    val targetPath = df.getColumnWithPath(column).path
    val sourcePaths = removeResult.removedColumns.map { it.toColumnWithPath<C>().path }

    // Check if any source path is a prefix of the target path
    sourcePaths.forEach { sourcePath ->
        val sourceSegments = sourcePath.toList()
        val targetSegments = targetPath.toList()

        if (sourceSegments.size <= targetSegments.size &&
            sourceSegments.indices.all { targetSegments[it] == sourceSegments[it] }
        ) {
            val afterOrBefore = if (isAfter) "after" else "before"
            throw IllegalArgumentException(
                "Cannot move column '${sourcePath.joinToString()}' $afterOrBefore its own child column '${targetPath.joinToString()}'",
            )
        }
    }

    val removeRoot = removeResult.removedColumns.first().getRoot()

    val refNode = removeRoot.getOrPut(targetPath) {
        val path = it.asList()

        // Get parent of a target path
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

    val parentPath = targetPath.dropLast(1)
    val toInsert = removeResult.removedColumns.map {
        val sourceCol = it.toColumnWithPath<C>()
        val sourcePath = sourceCol.path
        val path = if (sourcePath.size > 1) {
            // If source is nested, preserve its structure under the target parent
            parentPath + sourcePath.last()
        } else {
            parentPath + sourceCol.name()
        }
        ColumnToInsert(path, sourceCol.data, refNode)
    }
    if (isAfter) {
        return removeResult.df.insertImpl(toInsert)
    }

    //  Move the target column after the removed/inserted columns
    val logicOfAfter = removeResult.df.insertImpl(toInsert)
    val lastOfInsertedCols = toInsert.last().insertionPath
    val siblingsOfTargetAndTarget = removeResult.df[parentPath].asColumnGroup().columns().map { parentPath + it.path }
    val target = siblingsOfTargetAndTarget.filter { it.last() == targetPath.last() }
    return logicOfAfter.move { target.toColumnSet() }.after { lastOfInsertedCols }
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

internal fun <T, C> MoveClause<T, C>.moveToImpl(columnIndex: Int, insideGroup: Boolean): DataFrame<T> {
    if (!insideGroup) {
        return moveTo(columnIndex)
    }

    val columnsToMove = df.getColumns(columns)

    // check if columns to move have the same parent
    val columnsToMoveParents = columnsToMove.map { it.path.dropLast() }
    val parentOfFirst = columnsToMoveParents.first()
    if (columnsToMoveParents.any { it != parentOfFirst }) {
        throw IllegalArgumentException(
            "Cannot move columns to an index remaining inside group if they have different parent",
        )
    }

    // if columns will be moved to top level or columns to move are at top level
    if (parentOfFirst.isEmpty()) {
        return moveTo(columnIndex)
    }

    // replace the level where columns to move are with a new one where columns are moved
    val columnsToMoveNames = columnsToMove.map { it.name() }
    return df.replace { parentOfFirst.asColumnGroup() }.with {
        it.asDataFrame()
            .move { columnsToMoveNames.toColumnSet() }.to(columnIndex)
            .asColumnGroup(it.name())
    }
}
