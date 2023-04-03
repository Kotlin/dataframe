package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import kotlin.reflect.full.withNullability

internal fun <A, B> defaultJoinColumns(left: DataFrame<A>, right: DataFrame<B>): JoinColumnsSelector<A, B> =
    { left.columnNames().intersect(right.columnNames()).map { it.toColumnAccessor() }.let { ColumnsList(it) } }

internal fun <T> defaultJoinColumns(dataFrames: Iterable<DataFrame<T>>): JoinColumnsSelector<T, T> =
    {
        dataFrames.map { it.columnNames() }.fold<List<String>, Set<String>?>(null) { set, names ->
            set?.intersect(names) ?: names.toSet()
        }.orEmpty().map { it.toColumnAccessor() }.let { ColumnsList(it) }
    }

internal fun <C> ColumnSet<C>.extractJoinColumns(): List<ColumnMatch<C>> = when (this) {
    is ColumnsList -> columns.flatMap { it.extractJoinColumns() }
    is ColumnReference<C> -> listOf(ColumnMatch(this, path().toColumnAccessor() as ColumnReference<C>))
    is ColumnMatch -> listOf(this)
    else -> throw Exception()
}

internal fun <A, B> DataFrame<A>.getColumns(other: DataFrame<B>, selector: JoinColumnsSelector<A, B>): List<ColumnMatch<Any?>> {
    val receiver = object : DataFrameReceiver<A>(this, UnresolvedColumnsPolicy.Fail), JoinDsl<A, B> {
        override val right: DataFrame<B> = DataFrameReceiver(other, UnresolvedColumnsPolicy.Fail)
    }
    val columns = selector(receiver, this)
    return columns.extractJoinColumns()
}

internal fun <A, B> DataFrame<A>.joinImpl(
    other: DataFrame<B>,
    joinType: JoinType = JoinType.Inner,
    addNewColumns: Boolean = true,
    selector: JoinColumnsSelector<A, B>?
): DataFrame<A> {
    val joinColumns = getColumns(other, selector ?: defaultJoinColumns(this, other))

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
            val leftColumns = getColumnsWithPaths { leftCol.all().recursively(false) }
            val rightColumns = other.getColumnsWithPaths { rightCol.all().recursively(false) }

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
    val pathMapping = allLeftJoinColumns.mapIndexed { colNumber, leftCol ->
        leftCol.path to allRightJoinColumns[colNumber].path
    }.toMap()

    // compute pairs of join key to row index from right data frame
    val rightJoinKeyToIndex = other.indices()
        .map { index -> allRightJoinColumns.map { it.data[index] } to index }

    // group row indices by key from right data frame
    val groupedRight = when (joinType) {
        JoinType.Exclude -> rightJoinKeyToIndex.map { it.first to emptyList<Int>() }.toMap()
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
    val rightMatched = Array(other.nrow) { false }

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

    val leftColumns = getColumnsWithPaths { all().recursively(false) }

    val rightJoinColumnPaths = allRightJoinColumns.map { it.path to it.data }.toMap()

    val newRightColumns =
        if (addNewColumns) other.getColumnsWithPaths { cols { !it.isColumnGroup() && !rightJoinColumnPaths.contains(it.path) }.recursively() } else emptyList()

    // for every column index from the left dataframe store matching column from the right dataframe
    val leftToRightColumns = leftColumns.map { rightJoinColumnPaths[pathMapping[it.path()]] }

    val leftColumnsCount = leftColumns.size
    val newRightColumnsCount = newRightColumns.size
    val outputColumnsCount = leftColumnsCount + newRightColumnsCount

    val outputData = Array<Array<Any?>>(outputColumnsCount) { arrayOfNulls(outputRowsCount) }
    val hasNulls = Array(outputColumnsCount) { false }

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
        val srcColumn =
            if (columnIndex < leftColumnsCount) leftColumns[columnIndex] else newRightColumns[columnIndex - leftColumnsCount]
        val hasNulls = hasNulls[columnIndex]
        val newColumn = when (srcColumn.kind) {
            ColumnKind.Value -> DataColumn.createValueColumn(srcColumn.name, columnValues.asList(), srcColumn.type.withNullability(hasNulls))
            ColumnKind.Frame -> DataColumn.createFrameColumn(srcColumn.name, columnValues.asList() as List<AnyFrame>)
            ColumnKind.Group -> error("Unexpected ColumnGroup at path ${srcColumn.path}")
        }
        srcColumn.path to newColumn
    }

    return columns.toDataFrameFromPairs()
}
