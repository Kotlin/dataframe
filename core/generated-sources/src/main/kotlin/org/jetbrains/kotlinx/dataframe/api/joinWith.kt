package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinWithImpl

/**
 * A specialized [<code>DataRow</code>][DataRow] used in a [<code>JoinExpression</code>][JoinExpression].
 *
 * Represents a row from the left [<code>DataFrame</code>][DataFrame] (as the receiver)
 * and provides access to the row from the right [<code>DataFrame</code>][DataFrame] via [<code>right</code>][right].
 */
public interface JoinedDataRow<out A, out B> : DataRow<A> {
    public val right: DataRow<B>
}

/**
 * A special [<code>row</code>][DataRow] expression used to define
 * the row-matching condition in a [<code>joinWith</code>][joinWith] operation.
 *
 * Provides the [<code>row</code>][DataRow] of the left [<code>DataFrame</code>][DataFrame] both
 * as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its values directly.
 *
 * The [<code>row</code>][DataRow] of the right [<code>DataFrame</code>][DataFrame] is available
 * as [<code>right</code>][JoinedDataRow.right].
 *
 * The expression must return a [<code>Boolean</code>][Boolean] indicating whether
 * the rows from the left and right [<code>DataFrame</code>][DataFrame]s match.
 */
public typealias JoinExpression<A, B> = Selector<JoinedDataRow<A, B>, Boolean>

/**
 * Joins this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [<code>join type</code>][type]:
 *
 * **Merging joins:**
 * * [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * Each join type has a corresponding shortcut function:
 * [<code>innerJoinWith</code>][innerJoinWith], [<code>leftJoinWith</code>][leftJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>filterJoinWith</code>][filterJoinWith], and [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [type] [<code>JoinType</code>][JoinType] defining how rows are matched and combined.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("JoinWith")
public fun <A, B> DataFrame<A>.joinWith(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, type, addNewColumns = type.addNewColumns, joinExpression)

/**
 * Performs an [<code>inner join</code>][JoinType.Inner] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Inner</code>][JoinType.Inner].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>leftJoinWith</code>][leftJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>filterJoinWith</code>][filterJoinWith], [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("InnerJoinWith")
public fun <A, B> DataFrame<A>.innerJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Inner, joinExpression)

/**
 * Performs a [<code>left join</code>][JoinType.Left] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Left</code>][JoinType.Left].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>innerJoinWith</code>][innerJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>filterJoinWith</code>][filterJoinWith], [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("LeftJoinWith")
public fun <A, B> DataFrame<A>.leftJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Left, joinExpression)

/**
 * Performs a [<code>right join</code>][JoinType.Right] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Right</code>][JoinType.Right].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>innerJoinWith</code>][innerJoinWith], [<code>leftJoinWith</code>][leftJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>filterJoinWith</code>][filterJoinWith], [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("RightJoinWith")
public fun <A, B> DataFrame<A>.rightJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Right, joinExpression)

/**
 * Performs a [<code>full join</code>][JoinType.Full] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Full</code>][JoinType.Full].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>leftJoinWith</code>][leftJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>innerJoinWith</code>][innerJoinWith], [<code>filterJoinWith</code>][filterJoinWith], [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("FullJoinWith")
public fun <A, B> DataFrame<A>.fullJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Full, joinExpression)

/**
 * Performs a [<code>filter join</code>][JoinType.Filter] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Filter</code>][JoinType.Filter].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>leftJoinWith</code>][leftJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>innerJoinWith</code>][innerJoinWith], [<code>excludeJoinWith</code>][excludeJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("FilterJoinWith")
public fun <A, B> DataFrame<A>.filterJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWithImpl(right, JoinType.Filter, addNewColumns = false, joinExpression)

/**
 * Performs an [<code>exclude join</code>][JoinType.Exclude] of this [<code>DataFrame</code>][DataFrame] with the [<code>right</code>][right] [<code>DataFrame</code>][DataFrame]
 * using the provided [<code>joinExpression</code>][joinExpression]. Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>joinWith</code>][joinWith] with [<code>JoinType.Exclude</code>][JoinType.Exclude].
 *
 * A [<code>JoinExpression</code>][org.jetbrains.kotlinx.dataframe.api.JoinExpression] defines the matching condition between [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of the two [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 * It provides access to row values from both the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and expects a [<code>Boolean</code>][Boolean] result indicating whether the rows match.
 * All combinations of rows from the left- and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that satisfies
 * this condition are matched.
 *
 * This method is useful when rows should be matched based on custom logic
 * rather than simple values equality.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from both inputs according to the [<code>joinExpression</code>][joinExpression] matching rule.
 *
 * See also general [<code>joinWith</code>][joinWith] as well as other shortcuts with each of join types:
 * [<code>leftJoinWith</code>][leftJoinWith], [<code>rightJoinWith</code>][rightJoinWith], [<code>fullJoinWith</code>][fullJoinWith], [<code>filterJoinWith</code>][filterJoinWith], [<code>innerJoinWith</code>][innerJoinWith].
 *
 * See also [<code>join</code>][join], which performs a join by exact value equality in the selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, [See `joinWith` on the documentation website.](https://kotlin.github.io/dataframe/joinwith.html).
 *
 * ### Examples
 * ```kotlin
 * // Join rows where the `fullName` value in the left `DataFrame`
 * // contains the `firstName` value in the right `DataFrame`.
 * dfLeft.joinWith(dfRight) { left -> left.fullName.contains(right.firstName) }
 *
 * // Join rows where the `date` value in the right `DataFrame`
 * // falls within the interval defined by the `startDate` and `endDate`
 * // values in the left `DataFrame`.
 * dfLeft.joinWith(dfRight) { right.date in startDate..endDate }
 *
 * // String API; join rows where `score` value in the left `DataFrame` is higher than 3.4
 * // and the `passed` value in the right `DataFrame` is `true`.
 * dfLeft.joinWith(dfRight) { "score"<Int>() > 3.4 && right["passed"] as Boolean }
 * ```
 * @param [right] [<code>DataFrame</code>][DataFrame] to join with.
 * @param [joinExpression] [<code>JoinExpression</code>][JoinExpression] specifying the rows join condition.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("ExcludeJoinWith")
public fun <A, B> DataFrame<A>.excludeJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, JoinType.Exclude, addNewColumns = false, joinExpression)
