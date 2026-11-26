package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotDocs
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs

/**
 * A common supertype for the dataframe-like structures that support aggregation:
 * [<code>DataFrame</code>][DataFrame], [<code>GroupBy</code>][GroupBy], [<code>Pivot</code>][Pivot] and [<code>PivotGroupBy</code>][PivotGroupBy].
 *
 * Aggregation computes derived values from the rows of each group —
 * or from all rows of a [<code>DataFrame</code>][DataFrame] — and stores them in the result:
 * a [<code>GroupBy</code>][GroupBy] is aggregated into a [<code>DataFrame</code>][DataFrame] with one row per group,
 * and a [<code>Pivot</code>][Pivot] is aggregated into a single [<code>DataRow</code>][DataRow]
 * with the pivot keys as top-level columns.
 *
 * See [<code>GroupBy aggregation</code>][GroupByDocs.Aggregation], [<code>Pivot aggregation</code>][PivotDocs.Aggregation],
 * and [<code>PivotGroupBy aggregation</code>][PivotGroupByDocs.Aggregation]
 * for the available aggregation operations.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 */
public interface Aggregatable<out T>
