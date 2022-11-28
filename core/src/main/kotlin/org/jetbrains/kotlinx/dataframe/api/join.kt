package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.plugin.Join0
import org.jetbrains.kotlinx.dataframe.plugin.Match0
import kotlin.reflect.KProperty

@Refine("joinDefault")
@Interpretable(Join0::class)
public fun <A, B> DataFrame<A>.joinDefault(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>
): DataFrame<A> = join(other, selector = selector)

public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, type, true, selector)

public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    vararg columns: String,
    type: JoinType = JoinType.Inner
): DataFrame<A> = join(other, type) { columns.toColumns() }

public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = innerJoin(other) { columns.toColumns() }

public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = leftJoin(other) { columns.toColumns() }

public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = rightJoin(other) { columns.toColumns() }

public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = join(other, JoinType.Full, selector = selector)

public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = fullJoin(other) { columns.toColumns() }

public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = filterJoin(other) { columns.toColumns() }

public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    vararg columns: String
): DataFrame<A> = excludeJoin(other) { columns.toColumns() }

public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    public val right: DataFrame<B>
    @Interpretable(Match0::class)

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
    Full, // all data from left and from right data frame, nulls for any mismatches
    Exclude // mismatched rows from left data frame
}

public val JoinType.allowLeftNulls: Boolean get() = this == JoinType.Right || this == JoinType.Full
public val JoinType.allowRightNulls: Boolean get() = this == JoinType.Left || this == JoinType.Full || this == JoinType.Exclude
