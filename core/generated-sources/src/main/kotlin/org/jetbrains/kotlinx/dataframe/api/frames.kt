package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by keeping its groups as-is.
 *
 * Returns a new [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as top-level columns
 * (or as [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * for multiple or nested pivot keys).
 * Each [<code>pivot</code>][pivot] key column is a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]
 * with a single value: the [<code>DataFrame</code>][DataFrame] of the rows belonging to the corresponding group.
 *
 * This is a shortcut for [<code>aggregate</code>][Pivot.aggregate]` { this }`,
 * where the whole group is used as the aggregation result.
 *
 * Check out [<code>`pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html) [See "`Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation)
 *
 * See also:
 * - [<code>toDataFrame</code>][GroupBy.toDataFrame] — the [<code>GroupBy</code>][GroupBy] counterpart that returns
 *   the grouping key columns together with the groups in a [<code>FrameColumn</code>][FrameColumn].
 * - [<code>Pivot aggregation</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot].
 *
 * ### Examples
 *
 * ```kotlin
 * // Keep the group of rows of each "isHappy" value as is, in a frame column
 * df.pivot { isHappy }.frames()
 * ```
 * ```kotlin
 * // Keep the group of rows of each "isHappy" value as is, in a frame column
 * df.pivot("isHappy").frames()
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @return A new [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys as columns
 *   and the corresponding groups of rows as their values.
 */
public fun <T> Pivot<T>.frames(): DataRow<*> = aggregate { this }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by keeping each combined [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] group as a [<code>DataFrame</code>][DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] containing the following matrix:
 * - one row per unique combination of values in the grouping columns;
 * - one column group per [<code>pivot</code>][pivot] key, where each inner column corresponds to a value of that key
 *   (with several or nested pivot keys, e.g. created via [<code>and</code>][PivotDsl.and] or [<code>then</code>][PivotDsl.then],
 *   the groups reflect that key structure instead; see the `inward` parameter of [<code>pivot</code>][pivot]);
 * - each cell is a [<code>DataFrame</code>][DataFrame] with the rows of the corresponding pivot–group pair;
 *   a combination with no rows is an empty [<code>DataFrame</code>][DataFrame].
 *
 * This is equivalent to calling [<code>aggregate</code>][PivotGroupBy.aggregate] { this }.
 *
 * See also:
 * - [<code>pivot</code>][pivot], [<code>DataFrame.groupBy</code>][DataFrame.groupBy], [<code>Pivot.groupBy</code>][Pivot.groupBy] and [<code>GroupBy.pivot</code>][GroupBy.pivot].
 * - common [<code>aggregate</code>][PivotGroupBy.aggregate].
 * - [<code>frames</code>][Pivot.frames] — the same aggregation without grouping.
 *
 * For more information: [See "`Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation) [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * ### Example
 * ```kotlin
 * df.pivot { isHappy }.groupBy { name.firstName }.frames()
 * ```
 *
 * @return A [<code>DataFrame</code>][DataFrame] with one row per combination of grouping values, with the pivot groups as
 * [<code>DataFrame</code>][DataFrame] values inside [<code>FrameColumn</code>][FrameColumn]s.
 */
@Refine
@Interpretable("PivotGroupByFrames")
public fun <T> PivotGroupBy<T>.frames(): DataFrame<*> = aggregate { this }

// endregion
