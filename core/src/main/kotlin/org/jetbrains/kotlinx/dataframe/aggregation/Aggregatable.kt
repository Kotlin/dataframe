package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotDocs
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

/**
 * A common supertype for the dataframe-like structures that support aggregation:
 * [DataFrame], [GroupBy], [Pivot] and [PivotGroupBy].
 *
 * Aggregation computes derived values from the rows of each group —
 * or from all rows of a [DataFrame] — and stores them in the result:
 * a [GroupBy] is aggregated into a [DataFrame] with one row per group,
 * and a [Pivot] is aggregated into a single [DataRow]
 * with the pivot keys as top-level columns.
 *
 * See [GroupBy aggregation][GroupByDocs.Aggregation], [Pivot aggregation][PivotDocs.Aggregation],
 * and [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation]
 * for the available aggregation operations.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]} {@include [DocumentationUrls.Pivot]}
 */
public interface Aggregatable<out T>
