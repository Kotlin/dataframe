package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.EmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWith
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KProperty

// region join

public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, type, true, selector)

public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

public fun <A, B> DataFrame<A>.outerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Outer, selector = selector)

public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    public val right: DataFrame<B>

    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)

    public infix fun <C> String.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnOf(), other)

    public infix fun <C> ColumnReference<C>.match(other: String): ColumnMatch<C> = ColumnMatch(this, other.toColumnOf())

    public infix fun String.match(other: String): ColumnMatch<Any?> = ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    public infix fun <C> KProperty<C>.match(other: KProperty<C>): ColumnMatch<C> = ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    public infix fun <C> ColumnReference<C>.match(other: KProperty<C>): ColumnMatch<C> = ColumnMatch(this, other.toColumnAccessor())

    public infix fun <C> KProperty<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnAccessor(), other)
}

public class ColumnMatch<C>(public val left: ColumnReference<C>, public val right: ColumnReference<C>) : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> {
        throw UnsupportedOperationException()
    }
}

public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(ColumnsContainer<A>) -> ColumnSet<*>

public enum class JoinType {
    Left, // all data from left data frame, nulls for mismatches in right data frame
    Right, // all data from right data frame, nulls for mismatches in left data frame
    Inner, // only matched data from right and left data frame
    Outer, // all data from left and from right data frame, nulls for any mismatches
    Exclude // mismatched rows from left data frame
}

public val JoinType.allowLeftNulls: Boolean get() = this == JoinType.Right || this == JoinType.Outer
public val JoinType.allowRightNulls: Boolean get() = this == JoinType.Left || this == JoinType.Outer || this == JoinType.Exclude

// endregion

// region concat

@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> = concatImpl(map { it?.toDataFrame() ?: emptyDataFrame(1) }).cast()

public fun <T> Iterable<DataFrame<T>?>.concat(): DataFrame<T> = concatImpl(filterNotNull()).cast()

public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat().cast()

public fun <T> DataFrame<T>.concat(vararg other: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + other.toList()).cast<T>()

// endregion

// region append

public fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T> {
    val ncol = ncol
    assert(values.size % ncol == 0) { "Invalid number of arguments. Multiple of ${ncol()} is expected, but actual was: ${values.size}" }
    val newRows = values.size / ncol
    return columns().mapIndexed { colIndex, col ->
        val newValues = (0 until newRows).map { values[colIndex + it * ncol] }
        col.updateWith(col.values + newValues)
    }.toDataFrame().cast()
}

public fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T> {
    require(numberOfRows >= 0)
    if (numberOfRows == 0) return this
    if (ncol() == 0) return EmptyDataFrame(nrow + numberOfRows)
    return columns().map { col ->
        col.updateWith(col.values + arrayOfNulls(numberOfRows))
    }.toDataFrame().cast()
}

// endregion
