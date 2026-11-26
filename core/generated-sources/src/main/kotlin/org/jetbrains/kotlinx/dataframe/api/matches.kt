package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] into a [<code>Boolean</code>][Boolean] presence matrix
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics)),
 * returning a new [<code>DataFrame</code>][DataFrame] where:
 * * **Columns** represent all unique values of the pivoted columns of this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] corresponding to value combinations
 *   when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then], similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key values;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the original [<code>DataFrame</code>][DataFrame].
 *
 * This is a shortcut for the [<code>PivotGroupBy.matches</code>][PivotGroupBy.matches] overload that takes the marker values,
 * called with `yes = true` and `no = false`.
 * It is also the final step of the [<code>pivotMatches</code>][DataFrame.pivotMatches]
 * and [<code>pivotMatches</code>][GroupBy.pivotMatches] shortcuts.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches) [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * See also [<code>count</code>][PivotGroupBy.count], which performs a similar operation
 * but counts the number of rows in each group instead of checking for their presence.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each combination of a "city" and an "isHappy" value:
 * // `true` if there is at least one person with this combination, `false` otherwise
 * df.pivot { isHappy }.groupBy { city }.matches()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] representing a [<code>Boolean</code>][Boolean] presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
@Refine
@Interpretable("PivotGroupByMatches0")
public fun <T> PivotGroupBy<T>.matches(): DataFrame<*> = matches(yes = true, no = false)

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] into a **marker matrix**, returning a new [<code>DataFrame</code>][DataFrame] where:
 * * **Columns** represent all unique values of the pivoted columns of this [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] corresponding to value combinations
 *   when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then], similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] key values;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain [<code>yes</code>][yes] if a row with the corresponding combination of values
 *   (horizontal and vertical) exists in the original [<code>DataFrame</code>][DataFrame], and [<code>no</code>][no] otherwise.
 *
 * The no-argument [<code>matches</code>][PivotGroupBy.matches] overload is a shortcut for this function
 * with `yes = true` and `no = false`.
 *
 * Check out the [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches) [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * See also [<code>count</code>][PivotGroupBy.count], which performs a similar operation
 * but counts the number of rows in each group instead of marking them.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each combination of a "city" and an "isHappy" value:
 * // "yes" if there is at least one person with this combination, "no" otherwise
 * df.pivot { isHappy }.groupBy { city }.matches(yes = "yes", no = "no")
 * ```
 *
 * @param [yes] The marker for the cells where a row with the corresponding combination of values exists.
 * @param [no] The marker for the cells where no such row exists.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a marker matrix — with grouping key columns as rows,
 * pivot key values as columns, and the [<code>yes</code>][yes]/[<code>no</code>][no] markers in the cells.
 */
@Refine
@Interpretable("PivotGroupByMatches1")
public fun <T, R> PivotGroupBy<T>.matches(yes: R, no: R): DataFrame<*> = aggregate { yes default no }

// endregion
