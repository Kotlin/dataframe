package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.`Selecting Columns`
import org.jetbrains.kotlinx.dataframe.impl.api.joinWithImpl

/**
 * A [JoinExpression] defines the matching condition between [rows][DataRow] of the two [DataFrame]s.
 * It provides access to row values from both the left and right [DataFrame]s
 * and expects a [Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [DataFrame] by combining [rows][DataRow]
 * from both inputs according to the [\joinExpression] matching rule.
 */
@ExcludeFromSources
private typealias JoinWithCommonDescription = Nothing

// `joinWith` method used in the example
@Suppress("ClassName")
@ExcludeFromSources
private typealias JOIN_WITH_METHOD = Nothing

/**
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.{@get [JoinWithMethod] joinWith}(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.{@get [JoinWithMethod] joinWith}(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.{@get [JoinWithMethod] joinWith}(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 */
@ExcludeFromSources
private typealias JoinWithExample = Nothing

/**
 * A specialized [DataRow] used in a [JoinExpression].
 *
 * Represents a row from the left [DataFrame] (as the receiver)
 * and provides access to the row from the right [DataFrame] via [right].
 */
public interface JoinedDataRow<out A, out B> : DataRow<A> {
    public val right: DataRow<B>
}

/**
 * A special [row][DataRow] expression used to define
 * the row-matching condition in a [joinWith] operation.
 *
 * Provides the [row][DataRow] of the left [DataFrame] both
 * as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its values directly.
 *
 * The [row][DataRow] of the right [DataFrame] is available
 * as [right][JoinedDataRow.right].
 *
 * The expression must return a [Boolean] indicating whether
 * the rows from the left and right [DataFrame]s match.
 */
public typealias JoinExpression<A, B> = Selector<JoinedDataRow<A, B>, Boolean>

/**
 * Joins this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression].
 *
 * @include [JoinWithCommonDescription]
 *
 * {@include [JoinTypeDescription]}
 *
 * Each join type has a corresponding shortcut function:
 * [innerJoinWith], [leftJoinWith], [rightJoinWith], [fullJoinWith], [filterJoinWith], and [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample]
 * @param [right] [DataFrame] to join with.
 * @param [type] [JoinType] defining how rows are matched and combined.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("JoinWith")
public fun <A, B> DataFrame<A>.joinWith(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, type, addNewColumns = type.addNewColumns, joinExpression)

/**
 * Performs an [inner join][JoinType.Inner] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [InnerJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Inner].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [leftJoinWith], [rightJoinWith], [fullJoinWith], [filterJoinWith], [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] innerJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("InnerJoinWith")
public fun <A, B> DataFrame<A>.innerJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Inner, joinExpression)

/**
 * Performs a [left join][JoinType.Left] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [LeftJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Left].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [innerJoinWith], [rightJoinWith], [fullJoinWith], [filterJoinWith], [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] leftJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("LeftJoinWith")
public fun <A, B> DataFrame<A>.leftJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Left, joinExpression)

/**
 * Performs a [right join][JoinType.Right] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [RightJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Right].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [innerJoinWith], [leftJoinWith], [fullJoinWith], [filterJoinWith], [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] rightJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("RightJoinWith")
public fun <A, B> DataFrame<A>.rightJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Right, joinExpression)

/**
 * Performs a [full join][JoinType.Full] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [FullJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Full].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [leftJoinWith], [rightJoinWith], [innerJoinWith], [filterJoinWith], [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] fullJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FullJoinWith")
public fun <A, B> DataFrame<A>.fullJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Full, joinExpression)

/**
 * Performs a [filter join][JoinType.Filter] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [FilterJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Filter].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [leftJoinWith], [rightJoinWith], [fullJoinWith], [innerJoinWith], [excludeJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] filterJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FilterJoinWith")
public fun <A, B> DataFrame<A>.filterJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWithImpl(right, JoinType.Filter, addNewColumns = false, joinExpression)

/**
 * Performs an [exclude join][JoinType.Exclude] of this [DataFrame] with the [right][\right] [DataFrame]
 * using the provided [\joinExpression]. {@include [ExcludeJoinTypeDocs]}
 *
 * This is a shortcut for [joinWith] with [JoinType.Exclude].
 *
 * @include [JoinWithCommonDescription]
 *
 * See also general [joinWith] as well as other shortcuts with each of join types:
 * [leftJoinWith], [rightJoinWith], [fullJoinWith], [filterJoinWith], [innerJoinWith].
 *
 * See also [join], which performs a join by exact value equality in the selected columns.
 *
 * @include [`Selecting Columns`.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, {@include [DocumentationUrls.JoinWith]}.
 *
 * @include [JoinWithExample] {@set [JOIN_WITH_METHOD] excludeJoinWith}
 * @param [right] [DataFrame] to join with.
 * @param [joinExpression] [JoinExpression] specifying the rows join condition.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("ExcludeJoinWith")
public fun <A, B> DataFrame<A>.excludeJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, JoinType.Exclude, addNewColumns = false, joinExpression)
