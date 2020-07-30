package krangl.typed

import sun.reflect.generics.reflectiveObjects.NotImplementedException

interface TypedDataFrameWithColumnsForJoin<out A, out B> : TypedDataFrameWithColumnsForSelect<A> {

    operator fun <R> NamedColumn.invoke(selector: RowSelector<B, R>): ColumnSet

    val right: TypedDataFrame<B>

    infix fun NamedColumn.match(other: NamedColumn) = ColumnMatch(this, other)
}

class ColumnMatch(val left: NamedColumn, val right: NamedColumn) : ColumnSet

class TypedDataFrameWithColumnsForJoinImpl<A, B>(private val left: TypedDataFrame<A>, override val right: TypedDataFrame<B>) : TypedDataFrame<A> by left, TypedDataFrameWithColumnsForJoin<A, B> {

    override fun <R> NamedColumn.invoke(selector: RowSelector<B, R>): ColumnSet {
        throw NotImplementedException()
    }

    override val allColumns: ColumnGroup
        get() = throw NotImplementedException()

}

typealias JoinColumnSelector<A, B> = TypedDataFrameWithColumnsForJoin<A, B>.(TypedDataFrameWithColumnsForJoin<A, B>) -> ColumnSet

internal fun ColumnSet.extractJoinColumns(): List<ColumnMatch> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractJoinColumns() }
    is NamedColumn -> listOf(ColumnMatch(this, this))
    is ColumnMatch -> listOf(this)
    else -> throw Exception()
}

internal fun <A, B> TypedDataFrame<A>.getColumns(other: TypedDataFrame<B>, selector: JoinColumnSelector<A, B>) = TypedDataFrameWithColumnsForJoinImpl(this, other).let { selector(it, it).extractJoinColumns() }

enum class JoinType {
    LEFT, RIGHT, INNER, OUTER
}

val JoinType.allowLeftNulls get() = this == JoinType.RIGHT || this == JoinType.OUTER
val JoinType.allowRightNulls get() = this == JoinType.LEFT || this == JoinType.OUTER

fun <A, B> TypedDataFrame<A>.innerJoin(other: TypedDataFrame<B>, selector: JoinColumnSelector<A, B>) = join(other, JoinType.INNER, selector)
fun <A, B> TypedDataFrame<A>.leftJoin(other: TypedDataFrame<B>, selector: JoinColumnSelector<A, B>) = join(other, JoinType.LEFT, selector)
fun <A, B> TypedDataFrame<A>.rightJoin(other: TypedDataFrame<B>, selector: JoinColumnSelector<A, B>) = join(other, JoinType.RIGHT, selector)
fun <A, B> TypedDataFrame<A>.outerJoin(other: TypedDataFrame<B>, selector: JoinColumnSelector<A, B>) = join(other, JoinType.OUTER, selector)

fun <A, B> TypedDataFrame<A>.join(other: TypedDataFrame<B>, joinType: JoinType = JoinType.INNER, selector: JoinColumnSelector<A, B>): TypedDataFrame<A> {

    val joinColumns = getColumns(other, selector)

    val leftColumns = joinColumns.map { this[it.left] }
    val rightColumns = joinColumns.map { other[it.right] }

    val groupedRight = (0 until other.nrow)
            .map { index -> rightColumns.map { it[index] } to index }
            .groupBy({ it.first }) { it.second }

    var outputRowsCount = 0

    // for every row index from left data frame stores a list of matched row indices from right data frame
    val leftToRightMapping = (0 until nrow).map { leftIndex ->
        val leftKey = leftColumns.map { it[leftIndex] }
        val rightIndices = groupedRight[leftKey]
        outputRowsCount += rightIndices?.let { it.size } ?: if (joinType.allowRightNulls) 1 else 0
        rightIndices
    }

    // for every row index in right data frame stores a flag indicating whether this row was matched by some row in left data frame
    val rightMatched = Array(other.nrow) { false }

    // number of rows in right data frame that were not matched by any row in left data frame. Used for correct allocation of an output array
    var rightUnmatchedCount = other.nrow

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

    val usedColumnsNames = columnNames().toMutableSet()
    val rightJoinColumns = rightColumns.map { it.name to it }.toMap()

    // list of columns from right data frame that are not part of join key. Ensure that new column names doesn't clash with original columns
    val newRightColumns = other.columns.filter { !rightJoinColumns.contains(it.name) }.map {
        var name = it.name
        var k = 2
        while (usedColumnsNames.contains(name)) {
            name = "${it.name}_${k++}"
        }
        usedColumnsNames.add(name)
        it.rename(name)
    }

    val leftColumnsCount = ncol
    val newRightColumnsCount = newRightColumns.size
    val outputColumnsCount = leftColumnsCount + newRightColumnsCount

    val outputData = Array<Array<Any?>>(outputColumnsCount) { arrayOfNulls(outputRowsCount) }
    val hasNulls = Array(outputColumnsCount) { false }

    var row = 0

    leftToRightMapping.forEachIndexed { leftRow, rightRows ->
        if (rightRows == null) {
            if (joinType.allowRightNulls) {
                for (col in 0 until leftColumnsCount) {
                    outputData[col][row] = columns[col][leftRow].also { if (it == null) hasNulls[col] = true }
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
                    outputData[col][row] = columns[col][leftRow].also { if (it == null) hasNulls[col] = true }
                }
                for (col in 0 until newRightColumnsCount) {
                    outputData[leftColumnsCount + col][row] = newRightColumns[col][rightRow].also { if (it == null) hasNulls[leftColumnsCount + col] = true }
                }
                row++
            }
        }
    }

    if (joinType.allowLeftNulls) {

        val leftToRightJoinColumns = columns.map { leftColumn -> joinColumns.firstOrNull { it.left.name == leftColumn.name }?.let { other[it.right] } }

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
        val srcColumn = if (columnIndex < leftColumnsCount) columns[columnIndex] else newRightColumns[columnIndex - leftColumnsCount]
        TypedDataCol(columnValues.asList(), hasNulls[columnIndex], srcColumn.name, srcColumn.valueClass)
    }

    return columns.asDataFrame().typed<A>()
}