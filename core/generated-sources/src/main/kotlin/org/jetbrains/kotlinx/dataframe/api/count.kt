package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateValue

// region DataColumn

/**
 * Counts the elements in this [<code>DataColumn</code>][DataColumn] that satisfy a given [<code>predicate</code>][predicate] or returns the total count
 * if no predicate is provided.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
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
 * Returns the number of columns in this [<code>DataRow</code>][DataRow].
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html) [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return the number of columns in this row.
 * @see [columnsCount].
 */
public fun DataRow<*>.count(): Int = columnsCount()

/**
 * Counts the number of elements in the current row that satisfy the given [<code>predicate</code>][predicate].
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html) [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @param predicate A predicate function to test each element.
 * The predicate should return `true` for elements to be counted.
 * @return The number of elements that satisfy the predicate.
 */
public inline fun DataRow<*>.count(predicate: Predicate<Any?>): Int = values().count(predicate)

// endregion

// region DataFrame

/**
 * Returns the total number of rows of this [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * @return The number of rows in the [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.count(): Int = rowsCount()

/**
 * Counts the number of rows in this [<code>DataFrame</code>][DataFrame] that satisfy the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 * See also:
 * - [<code>filter</code>][DataFrame.filter] — filters rows using a [<code>RowFilter</code>][RowFilter] condition.
 * - [<code>countDistinct</code>][DataFrame.countDistinct] — counts distinct rows or values.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
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
 * @param T The schema marker type of the [<code>DataFrame</code>][DataFrame].
 * @param predicate A [<code>RowFilter</code>][RowFilter] that returns `true` for rows that should be counted.
 * @return The number of rows that satisfy the predicate.
 */
public inline fun <T> DataFrame<T>.count(predicate: RowFilter<T>): Int = rows().count { predicate(it, it) }

// endregion

// region GroupBy

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by counting the number of rows in each group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] where each row corresponds to a group.
 * The resulting frame contains:
 * - the original group key columns,
 * - a new column (named [<code>resultName</code>][resultName], default is `"count"`) that contains the number of rows in each group.
 *
 * This is equivalent to applying `.aggregate { count() }`, but more efficient.
 *
 * See also [<code>DataFrame.groupBy</code>][DataFrame.groupBy] and common [<code>aggregate</code>][Grouped.aggregate].
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Counts number of rows for each city, returning
 * // a new DataFrame with columns "city" and "count"
 * df.groupBy { city }.count()
 * ```
 *
 * @param resultName The name of the result column that will store the group sizes. Defaults to `"count"`.
 * @return A new [<code>DataFrame</code>][DataFrame] with group keys and corresponding group sizes.
 */
@Refine
@Interpretable("GroupByCount0")
public fun <T> Grouped<T>.count(resultName: String = "count"): DataFrame<T> =
    aggregateValue(resultName) { count() default 0 }

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] by counting the number of rows in each group
 * that satisfy the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] where each row corresponds to a group.
 * The resulting frame contains:
 * - the original group key columns,
 * - a new column (named [<code>resultName</code>][resultName], defaults to `"count"`)
 *   that stores the number of rows in each group matching the [<code>predicate</code>][predicate].
 *
 * This is equivalent to calling `.aggregate { count(predicate) }`, but more efficient.
 *
 * See also [<code>DataFrame.groupBy</code>][DataFrame.groupBy] and common [<code>aggregate</code>][Grouped.aggregate].
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Count rows for each city where the "income" value is greater than 30.0.
 * // Returns a new DataFrame with columns "city" and "pointsCount".
 * df.groupBy { city }.count("pointsCount") { income >= 30.0 }
 * ```
 *
 * @param resultName The name of the result column containing the group sizes. Defaults to `"count"`.
 * @return A new [<code>DataFrame</code>][DataFrame] with group keys and filtered row counts per group.
 */
@Refine
@Interpretable("GroupByCount0")
public inline fun <T> Grouped<T>.count(
    resultName: String = "count",
    crossinline predicate: RowFilter<T>,
): DataFrame<T> = aggregateValue(resultName) { count(predicate) default 0 }

// endregion

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by counting the number of rows in each group.
 *
 * Returns a single [<code>DataRow</code>][DataRow] where:
 * - each column corresponds to a [<code>pivot</code>][pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the number of rows in that group.
 *
 * The original [<code>Pivot</code>][Pivot] column structure is preserved.
 * If the [<code>Pivot</code>][Pivot] was created using multiple or nested keys
 * (e.g., via [<code>and</code>][PivotDsl.and] or [<code>then</code>][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the number of rows in that group.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 * - [<code>pivot</code>][pivot].
 * - common [<code>aggregate</code>][Pivot.aggregate].
 * - [<code>pivotCounts</code>][DataFrame.pivotCounts] shortcut.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Count the number of rows for each city.
 * // Returns a single DataRow with one column per city and the count of rows in each.
 * df.pivot { city }.count()
 * ```
 *
 * @return A single [<code>DataRow</code>][DataRow] with one column per group and the corresponding group size as its value.
 */
public fun <T> Pivot<T>.count(): DataRow<T> = delegate { count() }

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by counting the number of rows in each group
 * that satisfy the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 * Returns a single [<code>DataRow</code>][DataRow] where:
 * - each column corresponds to a [<code>pivot</code>][pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the number of rows in that group matching the [<code>predicate</code>][predicate].
 *
 * The original [<code>Pivot</code>][Pivot] column structure is preserved.
 * If the [<code>Pivot</code>][Pivot] was created using multiple or nested keys
 * (e.g., via [<code>and</code>][PivotDsl.and] or [<code>then</code>][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the number of rows (matching the [<code>predicate</code>][predicate]) in that group.
 *
 * This is equivalent to calling `.aggregate { count(predicate) }`, but more efficient.
 *
 * See also:
 * - [<code>pivot</code>][pivot].
 * - common [<code>aggregate</code>][Pivot.aggregate].
 * - [<code>pivotCounts</code>][DataFrame.pivotCounts] shortcut.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Count rows for each city where the "income" value is greater than 30.0.
 * // Returns a single DataRow with one column per city and the count of matching rows.
 * df.pivot { city }.count { income > 30.0 }
 * ```
 *
 * @return A single [<code>DataRow</code>][DataRow] with original [<code>Pivot</code>][Pivot] columns and filtered row counts per group.
 */
public inline fun <T> Pivot<T>.count(crossinline predicate: RowFilter<T>): DataRow<T> = delegate { count(predicate) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by counting the number of rows in each
 * combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] containing a following matrix:
 * - one row per [<code>groupBy</code>][groupBy] key (or keys set);
 * - one column group per [<code>pivot</code>][pivot] key, where each inner column corresponds to a value of that key;
 * - each cell contains the number of rows in the corresponding pivot–group pair.
 *
 * The original [<code>Pivot</code>][Pivot] column structure is preserved.
 * If the [<code>Pivot</code>][Pivot] was created using multiple or nested keys
 * (e.g., via [<code>and</code>][PivotDsl.and] or [<code>then</code>][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values of the corresponding key.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 *  - [<code>pivot</code>][pivot], [<code>DataFrame.groupBy</code>][DataFrame.groupBy], [<code>Pivot.groupBy</code>][Pivot.groupBy] and [<code>GroupBy.pivot</code>][GroupBy.pivot].
 *  - common [<code>aggregate</code>][PivotGroupBy.aggregate];
 *  - [<code>GroupBy.pivotCounts</code>][GroupBy.pivotCounts] shortcut.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Compute a matrix with "city" values horizontally and
 * // "age" values vertically, where each cell contains
 * // the number of rows with the corresponding age–city pair.
 * df.pivot { city }.groupBy { age }.count()
 * ```
 *
 * @return A [<code>DataFrame</code>][DataFrame] with [<code>groupBy</code>][groupBy] rows and pivoted counts as columns.
 */
public fun <T> PivotGroupBy<T>.count(): DataFrame<T> = aggregate { count() default 0 }

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by counting the number of rows in each
 * combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group, that satisfy the given [<code>predicate</code>][predicate].
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] containing a following matrix:
 * - one row per [<code>groupBy</code>][groupBy] key (or keys set);
 * - one column group per [<code>pivot</code>][pivot] key, where each inner column corresponds to a value of that key;
 * - each cell contains the number of rows in the corresponding pivot–group pair.
 *
 * The original [<code>Pivot</code>][Pivot] column structure is preserved.
 * If the [<code>Pivot</code>][Pivot] was created using multiple or nested keys
 * (e.g., via [<code>and</code>][PivotDsl.and] or [<code>then</code>][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values
 * (matching the [<code>predicate</code>][predicate]) of the corresponding key.
 *
 * This is equivalent to calling `.aggregate { count() }`, but more efficient.
 *
 * See also:
 *  - [<code>pivot</code>][pivot], [<code>DataFrame.groupBy</code>][DataFrame.groupBy], [<code>Pivot.groupBy</code>][Pivot.groupBy] and [<code>GroupBy.pivot</code>][GroupBy.pivot].
 *  - common [<code>aggregate</code>][PivotGroupBy.aggregate];
 *  - [<code>GroupBy.pivotCounts</code>][GroupBy.pivotCounts] shortcut.
 *
 * For more information: [See `count` on the documentation website.](https://kotlin.github.io/dataframe/count.html)
 *
 * ### Example
 * ```kotlin
 * // Compute a matrix with "city" values horizontally and
 * // "age" values vertically, where each cell contains
 * // the number of rows with the corresponding age–city pair.
 * df.pivot { city }.groupBy { age }.count()
 * ```
 *
 * @return A [<code>DataFrame</code>][DataFrame] with [<code>groupBy</code>][groupBy] rows and pivoted counts as columns matching the [<code>predicate</code>][predicate]..
 */
public inline fun <T> PivotGroupBy<T>.count(crossinline predicate: RowFilter<T>): DataFrame<T> =
    aggregate {
        count(predicate) default 0
    }

// endregion
