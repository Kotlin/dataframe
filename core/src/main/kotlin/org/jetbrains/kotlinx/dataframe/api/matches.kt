package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

// region PivotGroupBy

/**
 * * **Columns** represent all unique values of the pivoted columns of this [PivotGroupBy]
 *   (they become [column groups][ColumnGroup] corresponding to value combinations
 *   when using [then][PivotDsl.then], similar to [pivot]);
 * * **Rows** correspond to all unique combinations of the [groupBy] key values;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [keys][GroupBy.keys] in [GroupBy]).
 */
@ExcludeFromSources
internal typealias PivotGroupByMatchesResultingMatrixDescription = Nothing

/**
 * Aggregates this [PivotGroupBy] into a [Boolean] presence matrix
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics)),
 * returning a new [DataFrame] where:
 * @include [PivotGroupByMatchesResultingMatrixDescription]
 * * **Cells** contain a [Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the original [DataFrame].
 *
 * This is a shortcut for the [PivotGroupBy.matches] overload that takes the marker values,
 * called with `yes = true` and `no = false`.
 * It is also the final step of the [pivotMatches][DataFrame.pivotMatches]
 * and [pivotMatches][GroupBy.pivotMatches] shortcuts.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * For more information: {@include [DocumentationUrls.PivotMatches]} {@include [DocumentationUrls.PivotGroupBy]}
 *
 * See also [count][PivotGroupBy.count], which performs a similar operation
 * but counts the number of rows in each group instead of checking for their presence.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.matchesOnPivotGroupBy]
 *
 * @return A new [DataFrame] representing a [Boolean] presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
@Refine
@Interpretable("PivotGroupByMatches0")
public fun <T> PivotGroupBy<T>.matches(): DataFrame<*> = matches(yes = true, no = false)

/**
 * Aggregates this [PivotGroupBy] into a **marker matrix**, returning a new [DataFrame] where:
 * @include [PivotGroupByMatchesResultingMatrixDescription]
 * * **Cells** contain [yes] if a row with the corresponding combination of values
 *   (horizontal and vertical) exists in the original [DataFrame], and [no] otherwise.
 *
 * The no-argument [matches][PivotGroupBy.matches] overload is a shortcut for this function
 * with `yes = true` and `no = false`.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * For more information: {@include [DocumentationUrls.PivotMatches]} {@include [DocumentationUrls.PivotGroupBy]}
 *
 * See also [count][PivotGroupBy.count], which performs a similar operation
 * but counts the number of rows in each group instead of marking them.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.matchesYesNoOnPivotGroupBy]
 *
 * @param [yes] The marker for the cells where a row with the corresponding combination of values exists.
 * @param [no] The marker for the cells where no such row exists.
 * @return A new [DataFrame] representing a marker matrix — with grouping key columns as rows,
 * pivot key values as columns, and the [yes]/[no] markers in the cells.
 */
@Refine
@Interpretable("PivotGroupByMatches1")
public fun <T, R> PivotGroupBy<T>.matches(yes: R, no: R): DataFrame<*> = aggregate { yes default no }

// endregion
