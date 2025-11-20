package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.api.extractJoinColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * If no join columns are specified, all columns with matching names in both [DataFrame]s are used.
 *
 * If both [DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame].
 */
@ExcludeFromSources
private interface JoinBehavior

/**
 * Joins this [DataFrame] with the [other][\other] [DataFrame] using the selected key columns.
 *
 * Creates a new [DataFrame] by combining [rows][org.jetbrains.kotlinx.dataframe.DataRow]
 * from two input dataframes according to one or more matching key columns.
 *
 * {@include [JoinTypeDescription]}
 *
 * @include [JoinBehavior]
 *
 * Each join type has a corresponding shortcut function:
 * [innerJoin], [leftJoin], [rightJoin], [fullJoin], [filterJoin], and [excludeJoin].
 *
 * See also [joinWith], which performs a join by matching row values condition.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `join` overload
 */
@ExcludeFromSources
private interface JoinDocs

// `join` method used in the example
@Suppress("ktlint:standard:class-naming")
@ExcludeFromSources
private interface JOIN_METHOD

/**
 * [JoinDsl] defines the columns used for joining [DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][JoinDsl.right] to access columns from the right [DataFrame],
 * and [match][JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.{@get [JoinMethod] join}(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.{@get [JoinMethod] join}(dfRight) { left -> left.firstName match right.name }
 * ```
 */
@ExcludeFromSources
internal interface JoinDslDescription

/**
 * Select join columns (including those that have different names in different [DataFrame]s)
 * using [JoinDsl].
 *
 * @include [JoinDslDescription]
 */
@ExcludeFromSources
private interface SelectingColumnsJoinDsl

/**
 * @include [JoinDocs]
 * @include [SelectingColumnsJoinDsl]
 * @param other [DataFrame] to join with.
 * @param type [JoinType] defining how the resulting rows are constructed.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("Join0")
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, type, addNewColumns = type.addNewColumns, selector)

/**
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.{@get [JoinMethod] join}(dfRight, "name", "city")
 * ```
 */
private interface JoinStringApiExample

/**
 * @include [JoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample]
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @param type [JoinType] defining how the resulting rows are constructed.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    vararg columns: String,
    type: JoinType = JoinType.Inner,
): DataFrame<A> = join(other, type) { columns.toColumnSet() }

/**
 * Performs a [inner join][JoinType.Inner] of this [DataFrame] with the [other][\other] [DataFrame]
 * using the selected key columns.
 * @include [InnerJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Inner].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [leftJoin], [rightJoin], [fullJoin], [filterJoin], [excludeJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `innerJoin` overload
 */
@ExcludeFromSources
private interface InnerJoinDocs

/**
 * @include [InnerJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] innerJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("InnerJoin")
public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

/**
 * @include [InnerJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] innerJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.innerJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    innerJoin(other) { columns.toColumnSet() }

/**
 * Performs a [left join][JoinType.Left] of this [DataFrame] with the [other][\other] [DataFrame]
 * using the selected key columns.
 * @include [LeftJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Left].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [innerJoin], [rightJoin], [fullJoin], [filterJoin], [excludeJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `leftJoin` overload
 */
@ExcludeFromSources
private interface LeftJoinDocs

/**
 * @include [LeftJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] leftJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("LeftJoin")
public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

/**
 * @include [LeftJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] leftJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.leftJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    leftJoin(other) { columns.toColumnSet() }

/**
 * Performs a [right join][JoinType.Right] of this [DataFrame] with [other][\other] [DataFrame]
 * using selected key columns.
 * @include [RightJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Right].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [innerJoin], [leftJoin], [fullJoin], [filterJoin], [excludeJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `rightJoin` overload
 */
@ExcludeFromSources
private interface RightJoinDocs

/**
 * @include [RightJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] rightJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("RightJoin")
public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

/**
 * @include [RightJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] rightJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.rightJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    rightJoin(other) { columns.toColumnSet() }

/**
 * Performs a [full join][JoinType.Full] of this [DataFrame] with [other][\other] [DataFrame]
 * using selected key columns.
 * @include [FullJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Full].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [innerJoin], [leftJoin], [rightJoin], [filterJoin], [excludeJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `fullJoin` overload
 */
@ExcludeFromSources
private interface FullJoinDocs

/**
 * @include [FullJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] fullJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FullJoin")
public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Full, selector = selector)

/**
 * @include [FullJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] fullJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.fullJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    fullJoin(other) { columns.toColumnSet() }

/**
 * Performs a [filter join][JoinType.Filter] of this [DataFrame] with [other][\other] [DataFrame]
 * using selected key columns.
 * @include [FilterJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Filter].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [innerJoin], [leftJoin], [rightJoin], [fullJoin], [excludeJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `filterJoin` overload
 */
@ExcludeFromSources
private interface FilterJoinDocs

/**
 * @include [FilterJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] filterJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FilterJoin")
public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

/**
 * @include [FilterJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] filterJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.filterJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    filterJoin(other) { columns.toColumnSet() }

/**
 * Performs a [exclude join][JoinType.Exclude] of this [DataFrame] with [other][\other] [DataFrame]
 * using selected key columns.
 * @include [ExcludeJoinTypeDocs]
 *
 * This is a shortcut for [join] with [JoinType.Exclude].
 *
 * @include [JoinBehavior]
 *
 * See also general [join], as well as other shortcuts with each of join types:
 * [innerJoin], [leftJoin], [rightJoin], [filterJoin], [fullJoin].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information, {@include [DocumentationUrls.Join]}.
 *
 * ### This `excludeJoin` overload
 */
@ExcludeFromSources
private interface ExcludeJoinDocs

/**
 * @include [ExcludeJoinDocs]
 * @include [SelectingColumnsJoinDsl] {@set [JOIN_METHOD] excludeJoin}
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("ExcludeJoin")
public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

/**
 * @include [ExcludeJoinDocs]
 * @include [SelectingColumns.ColumnNames]
 * @include [JoinStringApiExample] {@set [JOIN_METHOD] excludeJoin}
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.excludeJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    excludeJoin(other) { columns.toColumnSet() }

/**
 * Joins all [DataFrame]s in this iterable into a single [DataFrame].
 *
 * Sequentially applies the [join] operation to each [DataFrame] in order.
 * Returns `null` if the iterable is empty.
 *
 * @param [joinType] [JoinType] defining how rows are matched and combined.
 * @param [selector] optional [JoinColumnsSelector] specifying key columns.
 * @return resulting [DataFrame], or `null` if the iterable is empty.
 */
public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null,
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

/**
 * A specialized [ColumnsSelectionDsl] that allows specifying [join] matching columns
 * with different names in left and right [DataFrame]s.
 *
 * @include [JoinDslDescription]
 */
public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    /**
     * Provides access to columns of the right [DataFrame]
     * for further matching with left columns [match].
     */
    public val right: DataFrame<B>

    /**
     * Matches columns from the left and right [DataFrame]s for [joining][join].
     *
     * The receiver column must belong to the left [DataFrame],
     * and the argument ([\other]) column must belong to the right [DataFrame].
     *
     * @receiver column from the left [DataFrame].
     * @param [other] column from the right [DataFrame].
     * @return [ColumnMatch] representing the column pair used for joining.
     */
    @ExcludeFromSources
    private interface MatchDocs

    /** @include [MatchDocs] */
    @Interpretable("Match0")
    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)

    /** @include [MatchDocs] */
    public infix fun <C> String.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnOf(), other)

    /** @include [MatchDocs] */
    public infix fun <C> ColumnReference<C>.match(other: String): ColumnMatch<C> = ColumnMatch(this, other.toColumnOf())

    /** @include [MatchDocs] */
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
        /**
         * **For internal use only.**
         * Not intended for public API consumption.
         *
         * Used in Compiler Plugin.
         */
        public fun <A, B> defaultJoinColumns(left: DataFrame<A>, right: DataFrame<B>): JoinColumnsSelector<A, B> =
            {
                left.columnNames().intersect(right.columnNames().toSet())
                    .map { it.toColumnAccessor() }
                    .let { ColumnListImpl(it) }
            }

        /**
         * **For internal use only.**
         * Not intended for public API consumption.
         *
         * Used in Compiler Plugin.
         */
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

/**
 * A special [ColumnSet] that specifies a [column match][JoinDsl.match] for the [join] operation.
 */
public interface ColumnMatch<C> : ColumnSet<C> {
    public val left: ColumnReference<C>
    public val right: ColumnReference<C>
}

internal class ColumnMatchImpl<C>(override val left: ColumnReference<C>, override val right: ColumnReference<C>) :
    ColumnMatch<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        throw UnsupportedOperationException()
}

/**
 * Creates a [ColumnMatch].
 *
 * Not intended for public API consumption. Please use [match][JoinDsl.match] instead.
 */
public fun <C> ColumnMatch(left: ColumnReference<C>, right: ColumnReference<C>): ColumnMatch<C> =
    ColumnMatchImpl(left, right)

/**
 * A specialized [ColumnsSelector] used for matching columns in a [join] operation.
 *
 * Provides [JoinDsl] both as the receiver and the lambda parameter, and expects
 * a [ColumnsResolver] as the return value.
 *
 * Enables defining matching columns from left and right [DataFrame]s
 * using [right][JoinDsl.right] and [match][JoinDsl.match].
 */
public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(ColumnsContainer<A>) -> ColumnsResolver<*>

/**
 * Includes only matching rows from both [DataFrame]s;
 * rows are merged.
 */
@ExcludeFromSources
internal interface InnerJoinTypeDocs

/**
 * Includes all rows from the left [DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 */
@ExcludeFromSources
internal interface LeftJoinTypeDocs

/**
 * Includes all rows from the right [DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 */
@ExcludeFromSources
internal interface RightJoinTypeDocs

/**
 * Includes only rows from the left [DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 */
@ExcludeFromSources
internal interface FilterJoinTypeDocs

/**
 * Includes all rows from both [DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 */
@ExcludeFromSources
internal interface FullJoinTypeDocs

/**
 * Includes only rows from the left [DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 */
@ExcludeFromSources
internal interface ExcludeJoinTypeDocs

/**
 * Represents the type of [join] operation.
 *
 * {@include [JoinTypeDescription]}
 */
public enum class JoinType {

    /**
     * Includes all rows from the left [DataFrame]; matching rows are merged,
     * unmatched right-side values are filled with `null`.
     */
    Left,

    /**
     * Includes all rows from the right [DataFrame]; matching rows are merged,
     * unmatched left-side values are filled with `null`.
     */
    Right,

    /**
     * Includes only matching rows from both [DataFrame]s;
     * rows are merged.
     */
    Inner,

    /**
     * Includes only rows from the left [DataFrame] that have a match in the right one;
     * right-side columns are not merged.
     */
    Filter,

    /**
     * Includes all rows from both [DataFrame]s; matching rows are merged,
     * all mismatches are filled with `null`.
     */
    Full,

    /**
     * Includes only rows from the left [DataFrame] that do *not* have a match in the right one;
     * right-side columns are not merged.
     */
    Exclude,
}

/**
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [join type][\type]:
 *
 * **Merging joins:**
 * * [JoinType.Inner] (default) — {@include [InnerJoinTypeDocs]}
 * * [JoinType.Left] — {@include [LeftJoinTypeDocs]}
 * * [JoinType.Right] — {@include [RightJoinTypeDocs]}
 * * [JoinType.Full] — {@include [FullJoinTypeDocs]}
 *
 * **Non-merging joins:**
 * * [JoinType.Filter] — {@include [FilterJoinTypeDocs]}
 * * [JoinType.Exclude] — {@include [ExcludeJoinTypeDocs]}
 */
@ExcludeFromSources
internal interface JoinTypeDescription

internal val JoinType.addNewColumns: Boolean
    get() = when (this) {
        JoinType.Filter, JoinType.Exclude -> false
        JoinType.Left, JoinType.Right, JoinType.Inner, JoinType.Full -> true
    }

internal val JoinType.allowLeftNulls: Boolean
    get() = this == JoinType.Right || this == JoinType.Full

internal val JoinType.allowRightNulls: Boolean
    get() = this == JoinType.Left ||
        this == JoinType.Full ||
        this == JoinType.Exclude
