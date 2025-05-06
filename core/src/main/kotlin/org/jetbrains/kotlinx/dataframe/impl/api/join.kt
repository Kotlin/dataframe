package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnMatch
import org.jetbrains.kotlinx.dataframe.api.JoinColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.JoinDsl
import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.api.allowLeftNulls
import org.jetbrains.kotlinx.dataframe.api.allowRightNulls
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability

internal fun <T> defaultJoinColumns(dataFrames: Iterable<DataFrame<T>>): JoinColumnsSelector<T, T> =
    {
        dataFrames.map { it.columnNames() }
            .fold<List<String>, Set<String>?>(null) { set, names ->
                set?.intersect(names.toSet()) ?: names.toSet()
            }.orEmpty()
            .map { it.toColumnAccessor() }
            .let { ColumnListImpl(it) }
    }

internal fun <C> ColumnsResolver<C>.extractJoinColumns(): List<ColumnMatch<C>> =
    when (this) {
        is ColumnsList -> columns.flatMap { it.extractJoinColumns() }
        is ColumnReference<C> -> listOf(ColumnMatch(this, path().toColumnAccessor() as ColumnReference<C>))
        is ColumnMatch -> listOf(this)
        else -> throw Exception()
    }

internal fun <A, B> DataFrame<A>.joinImpl(
    other: DataFrame<B>,
    joinType: JoinType = JoinType.Inner,
    addNewColumns: Boolean = true,
    selector: JoinColumnsSelector<A, B>?,
): DataFrame<A> {
    val joinColumns = JoinDsl.getColumns(this, other, selector ?: JoinDsl.defaultJoinColumns<A, B>(this, other))

    val leftJoinColumns = getColumnsWithPaths { joinColumns.map { it.left }.toColumnSet() }
    val rightJoinColumns = other.getColumnsWithPaths { joinColumns.map { it.right }.toColumnSet() }

    require(leftJoinColumns.size == rightJoinColumns.size)

    // replace all ColumnGroups in join with nested columns, matching by column path
    val allLeftJoinColumns = mutableListOf<ColumnWithPath<*>>()
    val allRightJoinColumns = mutableListOf<ColumnWithPath<*>>()

    leftJoinColumns.indices.forEach { i ->
        val leftCol = leftJoinColumns[i]
        val rightCol = rightJoinColumns[i]
        if (leftCol.isColumnGroup() && rightCol.isColumnGroup()) {
            val leftColumns = getColumnsWithPaths {
                leftCol.colsAtAnyDepth().filter { !it.isColumnGroup() }
            }
            val rightColumns = other.getColumnsWithPaths {
                rightCol.colsAtAnyDepth().filter { !it.isColumnGroup() }
            }

            val leftPrefixLength = leftCol.path.size
            val rightPrefixLength = rightCol.path.size
            val leftMap = leftColumns.associateBy { it.path.drop(leftPrefixLength) }.toMutableMap()

            rightColumns.forEach { right ->
                val relativePath = right.path.drop(rightPrefixLength)
                val left = leftMap[relativePath]
                if (left == null) {
                    require(selector == null) {
                        "Unable to perform join by column groups `${leftCol.name}` to `${rightCol.name}, because `$relativePath` was not found under `${leftCol.name}` in left DataFrame"
                    }
                } else {
                    allLeftJoinColumns.add(left)
                    allRightJoinColumns.add(right)
                    leftMap.remove(relativePath)
                }
            }
            require(leftMap.isEmpty() || selector == null) {
                "Unable to perform join by column groups `${leftCol.name}` to `${rightCol.name}, because `${leftMap.values.first()}` was not found under `${rightCol.name}` in right DataFrame"
            }
        } else {
            allLeftJoinColumns.add(leftCol)
            allRightJoinColumns.add(rightCol)
        }
    }

    // compute left to right column path mappings
    val pathMapping = allLeftJoinColumns
        .mapIndexed { colNumber, leftCol ->
            leftCol.path to allRightJoinColumns[colNumber].path
        }.toMap()

    // compute pairs of join key to row index from right data frame
    val rightJoinKeyToIndex = other
        .indices()
        .map { index -> allRightJoinColumns.map { it.data[index] } to index }

    // group row indices by key from right data frame
    val groupedRight = when (joinType) {
        JoinType.Exclude -> rightJoinKeyToIndex.associate { it.first to emptyList() }
        else -> rightJoinKeyToIndex.groupBy({ it.first }) { it.second }
    }

    var outputRowsCount = 0

    // for every row index from left data frame compute a list of matched indices from right data frame
    val leftToRightMapping = indices.map { leftIndex ->
        val leftKey = allLeftJoinColumns.map { it.data[leftIndex] }
        val rightIndices = groupedRight[leftKey]
        outputRowsCount += rightIndices?.size ?: if (joinType.allowRightNulls) 1 else 0
        rightIndices
    }

    // for every row index in right data frame store a flag indicating whether this row was matched by some row in left data frame
    val rightMatched = BooleanArray(other.nrow) { false }

    // number of rows in right data frame that were not matched by any row in left data frame. Used for correct allocation of an output array
    var rightUnmatchedCount = other.nrow

    // compute matched indices from right data frame and number of rows in output data frame
    if (joinType.allowLeftNulls) {
        leftToRightMapping.forEach { rightIndices ->
            rightIndices?.forEach { i ->
                if (!rightMatched[i]) {
                    rightUnmatchedCount--
                    rightMatched[i] = true
                }
            }
        }
        outputRowsCount += rightUnmatchedCount
    }

    val leftColumns = getColumnsWithPaths { colsAtAnyDepth().filter { !it.isColumnGroup() } }

    val rightJoinColumnPaths = allRightJoinColumns.associate { it.path to it.data }

    val newRightColumns =
        if (addNewColumns) {
            other.getColumnsWithPaths {
                colsAtAnyDepth().filter {
                    !it.isColumnGroup() && !rightJoinColumnPaths.contains(it.path)
                }
            }
        } else {
            emptyList()
        }

    // for every column index from the left dataframe store matching column from the right dataframe
    val leftToRightColumns = leftColumns.map { rightJoinColumnPaths[pathMapping[it.path()]] }

    val leftColumnsCount = leftColumns.size
    val newRightColumnsCount = newRightColumns.size
    val outputColumnsCount = leftColumnsCount + newRightColumnsCount

    val outputData = List<Array<Any?>>(outputColumnsCount) { arrayOfNulls(outputRowsCount) }
    val hasNulls = BooleanArray(outputColumnsCount) { false }

    var row = 0

    leftToRightMapping.forEachIndexed { leftRow, rightRows ->
        if (rightRows == null) {
            if (joinType.allowRightNulls) {
                for (col in 0 until leftColumnsCount) {
                    val leftColumn = leftColumns[col].data
                    outputData[col][row] = leftColumn[leftRow].also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount) {
                    outputData[leftColumnsCount + col][row] = null
                    hasNulls[leftColumnsCount + col] = true
                }
                row++
            }
        } else {
            for (rightRow in rightRows) {
                for (col in 0 until leftColumnsCount) {
                    val leftColumn = leftColumns[col].data
                    outputData[col][row] = leftColumn[leftRow].also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount) {
                    val rightColumn = newRightColumns[col].data
                    outputData[leftColumnsCount + col][row] =
                        rightColumn[rightRow].also { if (it == null) hasNulls[leftColumnsCount + col] = true }
                }
                row++
            }
        }
    }

    if (joinType.allowLeftNulls) {
        for (rightRow in rightMatched.indices) {
            if (!rightMatched[rightRow]) {
                for (col in 0 until leftColumnsCount) {
                    val rightColumn = leftToRightColumns[col]
                    outputData[col][row] = rightColumn?.get(rightRow).also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount) {
                    val rightColumn = newRightColumns[col].data
                    outputData[leftColumnsCount + col][row] =
                        rightColumn[rightRow].also { if (it == null) hasNulls[leftColumnsCount + col] = true }
                }
                row++
            }
        }
    }

    val columns = outputData.mapIndexed { columnIndex, columnValues ->
        val srcColumn = if (columnIndex < leftColumnsCount) {
            leftColumns[columnIndex]
        } else {
            newRightColumns[columnIndex - leftColumnsCount]
        }
        val hasNulls = hasNulls[columnIndex]
        val newColumn = when (srcColumn.kind) {
            ColumnKind.Value -> DataColumn.createValueColumn(
                name = srcColumn.name,
                values = columnValues.asList(),
                type = srcColumn.type.withNullability(hasNulls),
            )

            ColumnKind.Frame -> DataColumn.createFrameColumn(srcColumn.name, columnValues.asList() as List<AnyFrame>)

            ColumnKind.Group -> error("Unexpected ColumnGroup at path ${srcColumn.path}")
        }
        srcColumn.path to newColumn
    }

    return columns.toDataFrameFromPairs()
}
