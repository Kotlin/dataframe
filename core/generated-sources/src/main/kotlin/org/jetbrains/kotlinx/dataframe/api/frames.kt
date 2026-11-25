package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by keeping each pivot group as a [<code>DataFrame</code>][DataFrame].
 *
 * Returns a single [<code>DataRow</code>][DataRow] where:
 * - each column corresponds to a [<code>pivot</code>][pivot] group;
 * - with several pivot keys created via [<code>and</code>][PivotDsl.and], there is a column group per pivot key,
 *   with columns inside corresponding to the values of that key — unless [<code>pivot</code>][pivot] was called with
 *   `inward = false`, which keeps the pivot key values as top-level columns;
 * - with nested pivot keys created via [<code>then</code>][PivotDsl.then], the columns nest by key values:
 *   a column group per value of the first key, with the columns for the values of the remaining keys
 *   inside (`inward` does not change this nesting);
 * - each column holding a group is a [<code>FrameColumn</code>][FrameColumn] with the rows of the corresponding pivot group.
 *
 * This is equivalent to calling [<code>aggregate</code>][Pivot.aggregate] { this }.
 *
 * See also:
 * - [<code>pivot</code>][pivot].
 * - common [<code>aggregate</code>][Pivot.aggregate].
 * - [<code>frames</code>][PivotGroupBy.frames] — the same aggregation on a [<code>Pivot</code>][Pivot] combined with [<code>groupBy</code>][Pivot.groupBy].
 *
 * For more information: [See "`Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation)
 *
 * ### Example
 * ```kotlin
 * df.pivot { isHappy }.frames()
 * ```
 *
 * @return A single [<code>DataRow</code>][DataRow] with one [<code>FrameColumn</code>][FrameColumn] per pivot group, holding each group as a [<code>DataFrame</code>][DataFrame].
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
