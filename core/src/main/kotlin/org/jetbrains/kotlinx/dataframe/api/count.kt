package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.RowFilterDescription
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateValue

// region DataColumn

/**
 * Counts the elements in this [DataColumn] that satisfy a given [predicate] or returns the total count
 * if no predicate is provided.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * @param predicate An optional predicate used to filter the elements.
 * The predicate should return `true` for elements to be counted.
 * If `null` (by default), all elements are counted.
 * @return The count of elements in the column
 * that either match the predicate or the total count of elements if no predicate is provided.
 */
public fun <T> DataColumn<T>.count(predicate: Predicate<T>? = null): Int =
    if (predicate == null) {
        size()
    } else {
        values().count(predicate)
    }

// endregion

// region DataRow

/**
 * Returns the number of columns in this [DataRow].
 *
 * @return the number of columns in this row.
 * @see [columnsCount].
 */
public fun AnyRow.count(): Int = columnsCount()

/**
 * Counts the number of elements in the current row that satisfy the given [predicate].
 *
 * @param predicate A predicate function to test each element.
 * The predicate should return `true` for elements to be counted.
 * @return The number of elements that satisfy the predicate.
 */
public inline fun AnyRow.count(predicate: Predicate<Any?>): Int = values().count(predicate)

// endregion

// region DataFrame

/**
 * Returns the total number of rows of this [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * @return The number of rows in the [DataFrame].
 */
public fun <T> DataFrame<T>.count(): Int = rowsCount()

/**
 * Counts the number of rows in this [DataFrame] that satisfy the given [predicate].
 *
 * {@include [RowFilterDescription]}
 *
 * See also:
 * - [filter][DataFrame.filter] — filters rows using a [RowFilter] condition.
 * - [countDistinct][DataFrame.countDistinct] — counts distinct rows or values.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Count rows where the value in the "age" column is greater than 18
 * // and the "name/firstName" column starts with 'A'
 * df.count { age > 18 && name.firstName.startsWith("A") }
 * // Count rows
 * df.count { prev()?.length >= 50.0 ?: false }
 * ```
 *
 * @param T The schema marker type of the [DataFrame].
 * @param predicate A [RowFilter] that returns `true` for rows that should be counted.
 * @return The number of rows that satisfy the predicate.
 */
public inline fun <T> DataFrame<T>.count(predicate: RowFilter<T>): Int = rows().count { predicate(it, it) }

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by counting the number of rows in each group.
 *
 * Returns a new [DataFrame] where each row corresponds to a group.
 * The resulting frame contains:
 * - the original group key columns,
 * - a new column (named [resultName], default is `"count"`) that contains the number of rows in each group.
 *
 * This is equivalent to applying `.aggregate { count() }`, but more efficient.
 *
 * See also [DataFrame.groupBy] and common [aggregate][Grouped.aggregate].
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Counts number of rows for each city, returning
 * // a new DataFrame with columns "city" and "count"
 * df.groupBy { city }.count()
 * ```
 *
 * @param resultName The name of the result column that will store the group sizes. Defaults to `"count"`.
 * @return A new [DataFrame] with group keys and corresponding group sizes.
 */
@[Refine Interpretable("GroupByCount0")]
public fun <T> Grouped<T>.count(resultName: String = "count"): DataFrame<T> =
    aggregateValue(resultName) { count() default 0 }

/**
 * Aggregates this [GroupBy] by counting the number of rows in each group
 * that satisfy the given [predicate].
 *
 * {@include [RowFilterDescription]}
 *
 * Returns a new [DataFrame] where each row corresponds to a group.
 * The resulting frame contains:
 * - the original group key columns,
 * - a new column (named [resultName], defaults to `"count"`)
 *   that stores the number of rows in each group matching the [predicate].
 *
 * This is equivalent to calling `.aggregate { count(predicate) }`, but more efficient.
 *
 * See also [DataFrame.groupBy] and common [aggregate][Grouped.aggregate].
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Count rows for each city where the "income" value is greater than 30.0.
 * // Returns a new DataFrame with columns "city" and "pointsCount".
 * df.groupBy { city }.count("pointsCount") { income >= 30.0 }
 * ```
 *
 * @param resultName The name of the result column containing the group sizes. Defaults to `"count"`.
 * @return A new [DataFrame] with group keys and filtered row counts per group.
 */
@[Refine Interpretable("GroupByCount0")]
public inline fun <T> Grouped<T>.count(
    resultName: String = "count",
    crossinline predicate: RowFilter<T>,
): DataFrame<T> = aggregateValue(resultName) { count(predicate) default 0 }

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by counting the number of rows in each group.
 *
 * Returns a single [DataRow] where:
 * - each column corresponds to a [pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the number of rows in that group.
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the number of rows in that group.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 * - [pivot].
 * - common [aggregate][Pivot.aggregate].
 * - [pivotCounts][DataFrame.pivotCounts] shortcut.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Count the number of rows for each city.
 * // Returns a single DataRow with one column per city and the count of rows in each.
 * df.pivot { city }.count()
 * ```
 *
 * @return A single [DataRow] with one column per group and the corresponding group size as its value.
 */
public fun <T> Pivot<T>.count(): DataRow<T> = delegate { count() }

/**
 * Aggregates this [Pivot] by counting the number of rows in each group
 * that satisfy the given [predicate].
 *
 * {@include [RowFilterDescription]}
 *
 * Returns a single [DataRow] where:
 * - each column corresponds to a [pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the number of rows in that group matching the [predicate].
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the number of rows (matching the [predicate]) in that group.
 *
 * This is equivalent to calling `.aggregate { count(predicate) }`, but more efficient.
 *
 * See also:
 * - [pivot].
 * - common [aggregate][Pivot.aggregate].
 * - [pivotCounts][DataFrame.pivotCounts] shortcut.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Count rows for each city where the "income" value is greater than 30.0.
 * // Returns a single DataRow with one column per city and the count of matching rows.
 * df.pivot { city }.count { income > 30.0 }
 * ```
 *
 * @return A single [DataRow] with original [Pivot] columns and filtered row counts per group.
 */
public inline fun <T> Pivot<T>.count(crossinline predicate: RowFilter<T>): DataRow<T> = delegate { count(predicate) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by counting the number of rows in each
 * combined [pivot] + [groupBy] group.
 *
 * Returns a new [DataFrame] containing a following matrix:
 * - one row per [groupBy] key (or keys set);
 * - one column group per [pivot] key, where each inner column corresponds to a value of that key;
 * - each cell contains the number of rows in the corresponding pivot–group pair.
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values of the corresponding key.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 *  - [pivot], [DataFrame.groupBy], [Pivot.groupBy] and [GroupBy.pivot].
 *  - common [aggregate][PivotGroupBy.aggregate];
 *  - [GroupBy.pivotCounts] shortcut.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Compute a matrix with "city" values horizontally and
 * // "age" values vertically, where each cell contains
 * // the number of rows with the corresponding age–city pair.
 * df.pivot { city }.groupBy { age }.count()
 * ```
 *
 * @return A [DataFrame] with [groupBy] rows and pivoted counts as columns.
 */
public fun <T> PivotGroupBy<T>.count(): DataFrame<T> = aggregate { count() default 0 }

/**
 * Aggregates this [PivotGroupBy] by counting the number of rows in each
 * combined [pivot] + [groupBy] group, that satisfy the given [predicate].
 *
 * Returns a new [DataFrame] containing a following matrix:
 * - one row per [groupBy] key (or keys set);
 * - one column group per [pivot] key, where each inner column corresponds to a value of that key;
 * - each cell contains the number of rows in the corresponding pivot–group pair.
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values
 * (matching the [predicate]) of the corresponding key.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 *  - [pivot], [DataFrame.groupBy], [Pivot.groupBy] and [GroupBy.pivot].
 *  - common [aggregate][PivotGroupBy.aggregate];
 *  - [GroupBy.pivotCounts] shortcut.
 *
 * For more information: {@include [DocumentationUrls.Count]}
 *
 * ### Example
 * ```kotlin
 * // Compute a matrix with "city" values horizontally and
 * // "age" values vertically, where each cell contains
 * // the number of rows with the corresponding age–city pair.
 * df.pivot { city }.groupBy { age }.count()
 * ```
 *
 * @return A [DataFrame] with [groupBy] rows and pivoted counts as columns matching the [predicate]..
 */
public inline fun <T> PivotGroupBy<T>.count(crossinline predicate: RowFilter<T>): DataFrame<T> =
    aggregate {
        count(predicate) default 0
    }

// endregion
