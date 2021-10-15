package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.EmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultJoinColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWith
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.toDataFrame
import org.jetbrains.kotlinx.dataframe.typed

// region join

public interface JoinDsl<out A, out B> : ColumnSelectionDsl<A> {

    public val right: DataFrame<B>

    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)
}

public class ColumnMatch<C>(public val left: ColumnReference<C>, public val right: ColumnReference<C>) : Columns<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> {
        throw UnsupportedOperationException()
    }
}

public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(JoinDsl<A, B>) -> Columns<*>

public enum class JoinType {
    LEFT, // all data from left data frame, nulls for mismatches in right data frame
    RIGHT, // all data from right data frame, nulls for mismatches in left data frame
    INNER, // only matched data from right and left data frame
    OUTER, // all data from left and from right data frame, nulls for any mismatches
    EXCLUDE // mismatched rows from left data frame
}

public val JoinType.allowLeftNulls: Boolean get() = this == JoinType.RIGHT || this == JoinType.OUTER
public val JoinType.allowRightNulls: Boolean get() = this == JoinType.LEFT || this == JoinType.OUTER || this == JoinType.EXCLUDE

public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.INNER, selector = selector)

public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.LEFT, selector = selector)

public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.RIGHT, selector = selector)

public fun <A, B> DataFrame<A>.outerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.OUTER, selector = selector)

public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.INNER, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = join(other, JoinType.EXCLUDE, addNewColumns = false, selector = selector)

public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.INNER,
    selector: JoinColumnsSelector<T, T> = defaultJoinColumns(this)
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    joinType: JoinType = JoinType.INNER,
    addNewColumns: Boolean = true,
    selector: JoinColumnsSelector<A, B> = defaultJoinColumns(this, other)
): DataFrame<A> = joinImpl(other, joinType, addNewColumns, selector)

// endregion

// region concat

@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> = concatImpl(map { it?.toDataFrame() ?: emptyDataFrame(1) }).typed()

public fun <T> Iterable<DataFrame<T>?>.concat(): DataFrame<T> = concatImpl(filterNotNull()).typed()

public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat().typed()

public fun <T> DataFrame<T>.concat(vararg other: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + other.toList()).typed<T>()

// endregion

// region append

public fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T> {
    val ncol = ncol
    assert(values.size % ncol == 0) { "Invalid number of arguments. Multiple of ${ncol()} is expected, but actual was: ${values.size}" }
    val newRows = values.size / ncol
    return columns().mapIndexed { colIndex, col ->
        val newValues = (0 until newRows).map { values[colIndex + it * ncol] }
        col.updateWith(col.values + newValues)
    }.toDataFrame()
}

public fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T> {
    require(numberOfRows >= 0)
    if (numberOfRows == 0) return this
    if (ncol() == 0) return EmptyDataFrame(nrow + numberOfRows)
    return columns().map { col ->
        col.updateWith(col.values + arrayOfNulls(numberOfRows))
    }.toDataFrame()
}

// endregion
