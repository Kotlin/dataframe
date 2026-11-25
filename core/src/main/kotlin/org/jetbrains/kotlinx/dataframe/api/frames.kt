package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region Pivot

/**
 * Aggregates this [Pivot] by keeping its groups as-is.
 *
 * Returns a new [DataRow] with the [pivot] keys as top-level columns
 * (or as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * for multiple or nested pivot keys).
 * Each [pivot] key column is a [frame column][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]
 * with a single value: the [DataFrame] of the rows belonging to the corresponding group.
 *
 * This is a shortcut for [aggregate][Pivot.aggregate]` { this }`,
 * where the whole group is used as the aggregation result.
 *
 * Check out [`pivot` Grammar][PivotDocs.Grammar].
 *
 * For more information: {@include [DocumentationUrls.Pivot]} {@include [DocumentationUrls.PivotAggregation]}
 *
 * See also:
 * - [toDataFrame][GroupBy.toDataFrame] — the [GroupBy] counterpart that returns
 *   the grouping key columns together with the groups in a [FrameColumn].
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotFrames_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotFrames_strings]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @return A new [DataRow] with the [pivot] keys as columns
 *   and the corresponding groups of rows as their values.
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
