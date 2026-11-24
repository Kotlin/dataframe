package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region Pivot

/**
 * Aggregates this [Pivot] by keeping each pivot group as a [DataFrame].
 *
 * Returns a single [DataRow] where:
 * - each column corresponds to a [pivot] group;
 * - with several pivot keys created via [and][PivotDsl.and], there is a column group per pivot key,
 *   with columns inside corresponding to the values of that key — unless [pivot] was called with
 *   `inward = false`, which keeps the pivot key values as top-level columns;
 * - with nested pivot keys created via [then][PivotDsl.then], the columns nest by key values:
 *   a column group per value of the first key, with the columns for the values of the remaining keys
 *   inside (`inward` does not change this nesting);
 * - each column holding a group is a [FrameColumn] with the rows of the corresponding pivot group.
 *
 * This is equivalent to calling [aggregate][Pivot.aggregate] { this }.
 *
 * See also:
 * - [pivot].
 * - common [aggregate][Pivot.aggregate].
 * - [frames][PivotGroupBy.frames] — the same aggregation on a [Pivot] combined with [groupBy][Pivot.groupBy].
 *
 * For more information: {@include [DocumentationUrls.PivotAggregation]}
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotFrames_properties]
 *
 * @return A single [DataRow] with one [FrameColumn] per pivot group, holding each group as a [DataFrame].
 */
public fun <T> Pivot<T>.frames(): DataRow<*> = aggregate { this }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by keeping each combined [pivot] and [groupBy] group as a [DataFrame].
 *
 * Returns a new [DataFrame] containing the following matrix:
 * - one row per unique combination of values in the grouping columns;
 * - one column group per [pivot] key, where each inner column corresponds to a value of that key
 *   (with several or nested pivot keys, e.g. created via [and][PivotDsl.and] or [then][PivotDsl.then],
 *   the groups reflect that key structure instead; see the `inward` parameter of [pivot]);
 * - each cell is a [DataFrame] with the rows of the corresponding pivot–group pair;
 *   a combination with no rows is an empty [DataFrame].
 *
 * This is equivalent to calling [aggregate][PivotGroupBy.aggregate] { this }.
 *
 * See also:
 * - [pivot], [DataFrame.groupBy], [Pivot.groupBy] and [GroupBy.pivot].
 * - common [aggregate][PivotGroupBy.aggregate].
 * - [frames][Pivot.frames] — the same aggregation without grouping.
 *
 * For more information: {@include [DocumentationUrls.PivotAggregation]} {@include [DocumentationUrls.PivotGroupBy]}
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotGroupByFrames_properties]
 *
 * @return A [DataFrame] with one row per combination of grouping values, with the pivot groups as
 * [DataFrame] values inside [FrameColumn]s.
 */
@Refine
@Interpretable("PivotGroupByFrames")
public fun <T> PivotGroupBy<T>.frames(): DataFrame<*> = aggregate { this }

// endregion
