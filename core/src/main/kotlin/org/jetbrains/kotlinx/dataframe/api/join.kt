package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.api.extractJoinColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

@Refine
@Interpretable("Join0")
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, type, addNewColumns = type.addNewColumns, selector)

public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    vararg columns: String,
    type: JoinType = JoinType.Inner,
): DataFrame<A> = join(other, type) { columns.toColumnSet() }

@Refine
@Interpretable("InnerJoin")
public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

public fun <A, B> DataFrame<A>.innerJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    innerJoin(other) { columns.toColumnSet() }

@Refine
@Interpretable("LeftJoin")
public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

public fun <A, B> DataFrame<A>.leftJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    leftJoin(other) { columns.toColumnSet() }

@Refine
@Interpretable("RightJoin")
public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

public fun <A, B> DataFrame<A>.rightJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    rightJoin(other) { columns.toColumnSet() }

@Refine
@Interpretable("FullJoin")
public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Full, selector = selector)

public fun <A, B> DataFrame<A>.fullJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    fullJoin(other) { columns.toColumnSet() }

@Refine
@Interpretable("FilterJoin")
public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.filterJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    filterJoin(other) { columns.toColumnSet() }

@Refine
@Interpretable("ExcludeJoin")
public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

public fun <A, B> DataFrame<A>.excludeJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    excludeJoin(other) { columns.toColumnSet() }

public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null,
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    public val right: DataFrame<B>

    @Interpretable("Match0")
    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)

    public infix fun <C> String.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnOf(), other)

    public infix fun <C> ColumnReference<C>.match(other: String): ColumnMatch<C> = ColumnMatch(this, other.toColumnOf())

    public infix fun String.match(other: String): ColumnMatch<Any?> =
        ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.match(other: KProperty<C>): ColumnMatch<C> =
        ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.match(other: KProperty<C>): ColumnMatch<C> =
        ColumnMatch(this, other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.match(other: ColumnReference<C>): ColumnMatch<C> =
        ColumnMatch(toColumnAccessor(), other)

    public companion object {
        public fun <A, B> defaultJoinColumns(left: DataFrame<A>, right: DataFrame<B>): JoinColumnsSelector<A, B> =
            {
                left.columnNames().intersect(right.columnNames().toSet())
                    .map { it.toColumnAccessor() }
                    .let { ColumnListImpl(it) }
            }

        public fun <A, B> getColumns(
            left: DataFrame<A>,
            other: DataFrame<B>,
            selector: JoinColumnsSelector<A, B>,
        ): List<ColumnMatch<Any?>> {
            val receiver = object : DataFrameReceiver<A>(left, UnresolvedColumnsPolicy.Fail), JoinDsl<A, B> {
                override val right: DataFrame<B> = DataFrameReceiver(other, UnresolvedColumnsPolicy.Fail)
            }
            val columns = selector(receiver, left)
            return columns.extractJoinColumns()
        }
    }
}

public interface ColumnMatch<C> : ColumnSet<C> {
    public val left: ColumnReference<C>
    public val right: ColumnReference<C>
}

internal class ColumnMatchImpl<C>(override val left: ColumnReference<C>, override val right: ColumnReference<C>) :
    ColumnMatch<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        throw UnsupportedOperationException()
}

public fun <C> ColumnMatch(left: ColumnReference<C>, right: ColumnReference<C>): ColumnMatch<C> =
    ColumnMatchImpl(left, right)

public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(ColumnsContainer<A>) -> ColumnsResolver<*>

public enum class JoinType {
    Left, // all data from left dataframe, nulls for mismatches in right dataframe
    Right, // all data from right dataframe, nulls for mismatches in left dataframe
    Inner, // only matched data from right and left dataframe
    Filter, // only matched data from left dataframe
    Full, // all data from left and from right dataframe, nulls for any mismatches
    Exclude, // mismatched rows from left dataframe
}

internal val JoinType.addNewColumns: Boolean
    get() = when (this) {
        JoinType.Filter, JoinType.Exclude -> false
        JoinType.Left, JoinType.Right, JoinType.Inner, JoinType.Full -> true
    }

public val JoinType.allowLeftNulls: Boolean
    get() = this == JoinType.Right || this == JoinType.Full

public val JoinType.allowRightNulls: Boolean
    get() = this == JoinType.Left ||
        this == JoinType.Full ||
        this == JoinType.Exclude
