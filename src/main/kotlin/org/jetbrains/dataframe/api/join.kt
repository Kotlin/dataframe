package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnSet
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.DataColumn
import kotlin.reflect.full.withNullability

interface JoinReceiver<out A, out B> : SelectReceiver<A> {

    val right: DataFrame<B>

    infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>) = ColumnMatch(this, other)
}

class ColumnMatch<C>(val left: ColumnReference<C>, val right: ColumnReference<C>) : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> {
        throw UnsupportedOperationException()
    }
}

class JoinReceiverImpl<A, B>(private val left: DataFrame<A>, override val right: DataFrame<B>) : DataFrame<A> by left, JoinReceiver<A, B>

typealias JoinColumnSelector<A, B> = JoinReceiver<A, B>.(JoinReceiver<A, B>) -> ColumnSet<*>

// TODO: support column hierarchy
internal fun <C> ColumnSet<C>.extractJoinColumns(): List<ColumnMatch<C>> = when (this) {
    is Columns -> columns.flatMap { it.extractJoinColumns() }
    is ColumnReference<C> -> listOf(ColumnMatch(this, this))
    is ColumnMatch -> listOf(this)
    else -> throw Exception()
}

internal fun <A, B> DataFrame<A>.getColumns(other: DataFrame<B>, selector: JoinColumnSelector<A, B>) = JoinReceiverImpl(this, other).let { selector(it, it).extractJoinColumns() }

enum class JoinType {
    LEFT, // all data from left data frame, nulls for mismatches in right data frame
    RIGHT, // all data from right data frame, nulls for mismatches in left data frame
    INNER, // only matched data from right and left data frame
    OUTER, // all data from left and from right data frame, nulls for any mismatches
    EXCLUDE // mismatched rows from left data frame
}

val JoinType.allowLeftNulls get() = this == JoinType.RIGHT || this == JoinType.OUTER
val JoinType.allowRightNulls get() = this == JoinType.LEFT || this == JoinType.OUTER || this == JoinType.EXCLUDE

internal fun <A, B> defaultJoinColumns(left: DataFrame<A>, right: DataFrame<B>): JoinColumnSelector<A, B> =
        { left.columnNames().intersect(right.columnNames()).map { it.toColumnDef() }.let { Columns(it) } }

internal fun <T> defaultJoinColumns(dataFrames: Iterable<DataFrame<T>>): JoinColumnSelector<T, T> =
        {
            dataFrames.map { it.columnNames() }.fold<List<String>, Set<String>?>(null) { set, names ->
                set?.intersect(names) ?: names.toSet()
            }.orEmpty().map { it.toColumnDef() }.let { Columns(it) }
        }

fun <A, B> DataFrame<A>.innerJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.INNER, selector = selector)
fun <A, B> DataFrame<A>.leftJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.LEFT, selector = selector)
fun <A, B> DataFrame<A>.rightJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.RIGHT, selector = selector)
fun <A, B> DataFrame<A>.outerJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.OUTER, selector = selector)
fun <A, B> DataFrame<A>.filterJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.INNER, addNewColumns = false, selector = selector)
fun <A, B> DataFrame<A>.excludeJoin(other: DataFrame<B>, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)) = join(other, JoinType.EXCLUDE, addNewColumns = false, selector = selector)

fun <T> Iterable<DataFrame<T>>.joinOrNull(joinType: JoinType = JoinType.INNER, selector: JoinColumnSelector<T, T> = defaultJoinColumns(this)) =
        fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

fun <A, B> DataFrame<A>.join(other: DataFrame<B>, joinType: JoinType = JoinType.INNER, addNewColumns: Boolean = true, selector: JoinColumnSelector<A, B> = defaultJoinColumns(this, other)): DataFrame<A> {

    val joinColumns = getColumns(other, selector)

    val leftColumns = joinColumns.map { this[it.left] }
    val rightColumns = joinColumns.map { other[it.right] }

    val rightJoinKeyToIndex = (0 until other.nrow())
            .map { index -> rightColumns.map { it[index] } to index }

    val groupedRight = when (joinType) {
        JoinType.EXCLUDE -> rightJoinKeyToIndex.map { it.first to emptyList<Int>() }.toMap()
        else -> rightJoinKeyToIndex.groupBy({ it.first }) { it.second }
    }

    var outputRowsCount = 0

    // for every row index from left data frame stores a list of matched row indices from right data frame
    val leftToRightMapping = (0 until nrow()).map { leftIndex ->
        val leftKey = leftColumns.map { it[leftIndex] }
        val rightIndices = groupedRight[leftKey]
        outputRowsCount += rightIndices?.let { it.size } ?: if (joinType.allowRightNulls) 1 else 0
        rightIndices
    }

    // for every row index in right data frame stores a flag indicating whether this row was matched by some row in left data frame
    val rightMatched = Array(other.nrow()) { false }

    // number of rows in right data frame that were not matched by any row in left data frame. Used for correct allocation of an output array
    var rightUnmatchedCount = other.nrow()

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

    val nameGenerator = nameGenerator()
    val rightJoinColumns = rightColumns.map { it.name() to it }.toMap()

    // list of columns from right data frame that are not part of join key. Ensure that new column names doesn't clash with original columns
    val newRightColumns = if (addNewColumns) other.columns().filter { !rightJoinColumns.contains(it.name()) }.map {
        it.rename(nameGenerator.addUnique(it.name()))
    } else emptyList()

    val leftColumnsCount = ncol()
    val newRightColumnsCount = newRightColumns.size
    val outputColumnsCount = leftColumnsCount + newRightColumnsCount

    val outputData = Array<Array<Any?>>(outputColumnsCount) { arrayOfNulls(outputRowsCount) }
    val hasNulls = Array(outputColumnsCount) { false }

    var row = 0

    leftToRightMapping.forEachIndexed { leftRow, rightRows ->
        if (rightRows == null) {
            if (joinType.allowRightNulls) {
                for (col in 0 until leftColumnsCount) {
                    outputData[col][row] = column(col)[leftRow].also { if (it == null) hasNulls[col] = true }
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
                    outputData[col][row] = column(col)[leftRow].also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount) {
                    outputData[leftColumnsCount + col][row] = newRightColumns[col][rightRow].also { if (it == null) hasNulls[leftColumnsCount + col] = true }
                }
                row++
            }
        }
    }

    if (joinType.allowLeftNulls) {

        val leftToRightJoinColumns = columns().map { leftColumn -> joinColumns.firstOrNull { it.left.name() == leftColumn.name() }?.let { other[it.right] } }

        for (rightRow in rightMatched.indices) {
            if (!rightMatched[rightRow]) {
                for (col in 0 until leftColumnsCount) {
                    outputData[col][row] = leftToRightJoinColumns[col]?.get(rightRow).also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount)
                    outputData[leftColumnsCount + col][row] = newRightColumns[col][rightRow].also { if (it == null) hasNulls[leftColumnsCount + col] = true }
                row++
            }
        }
    }

    val columns = outputData.mapIndexed { columnIndex, columnValues ->
        val srcColumn = if (columnIndex < leftColumnsCount) column(columnIndex) else newRightColumns[columnIndex - leftColumnsCount]
        val hasNulls = hasNulls[columnIndex]
        DataColumn.create(srcColumn.name(), columnValues.asList(), srcColumn.type.withNullability(hasNulls))
    }

    return columns.asDataFrame<A>()
}