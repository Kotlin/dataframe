package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn
import org.jetbrains.kotlinx.dataframe.indices

// region DataFrame

/**
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the same rows in reversed order,
 * so the last row becomes the first one.
 *
 * Only the order of the rows changes:
 * the columns, their names and types, and thus the schema of the dataframe stay the same.
 *
 * For more information: [See `reverse` on the documentation website.](https://kotlin.github.io/dataframe/reverse.html)
 *
 * See also [<code>shuffle</code>][DataFrame.shuffle], which reorders rows randomly,
 * and [<code>sortBy</code>][DataFrame.sortBy], which orders rows by the values of the selected columns.
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @return A new [<code>DataFrame</code>][DataFrame] with the rows in reversed order.
 */
public fun <T> DataFrame<T>.reverse(): DataFrame<T> = get(indices.reversed())

// endregion

// region DataColumn

/**
 *
 * Returns a new [<code>DataColumn</code>][DataColumn] with the same values in reversed order,
 * so the last value becomes the first one.
 *
 * The column keeps its name, type, and [<code>kind</code>][ColumnKind]:
 * a value column stays a value column, a column group stays a column group,
 * and a frame column stays a frame column.
 * The result is typed as [<code>DataColumn</code>][DataColumn] though, so for a column group use
 * [<code>asColumnGroup</code>][DataColumn.asColumnGroup] to get [<code>ColumnGroup</code>][ColumnGroup] back.
 *
 * For more information: [See `reverse` on the documentation website.](https://kotlin.github.io/dataframe/reverse.html)
 *
 * See also [<code>shuffle</code>][DataColumn.shuffle],
 * which reorders values randomly.
 *
 * @param [T] The type of the values in this [<code>DataColumn</code>][DataColumn].
 * @return A new [<code>DataColumn</code>][DataColumn] with the values in reversed order.
 */
public fun <T> DataColumn<T>.reverse(): DataColumn<T> = get(indices.reversed())

/**
 *
 * Returns a new [<code>ColumnGroup</code>][ColumnGroup] with the same rows in reversed order,
 * so the last row becomes the first one.
 *
 * The group keeps its name and its nested column structure.
 * Reversing is applied to the group as a whole, so the values in all nested columns
 * stay aligned row-wise.
 *
 * For more information: [See `reverse` on the documentation website.](https://kotlin.github.io/dataframe/reverse.html)
 *
 * See also [<code>shuffle</code>][DataFrame.shuffle],
 * which reorders rows randomly.
 *
 * @param [T] The schema marker type of this [<code>ColumnGroup</code>][ColumnGroup].
 * @return A new [<code>ColumnGroup</code>][ColumnGroup] with the rows in reversed order.
 */
public fun <T> ColumnGroup<T>.reverse(): ColumnGroup<T> = get(indices.reversed())

/**
 *
 * Returns a new [<code>FrameColumn</code>][FrameColumn] with the same dataframes in reversed order,
 * so the last dataframe becomes the first one.
 *
 * The column keeps its name and type.
 * Only the order of the dataframes in it changes;
 * the rows inside each of them keep their original order.
 *
 * For more information: [See `reverse` on the documentation website.](https://kotlin.github.io/dataframe/reverse.html)
 *
 * See also [<code>reverse</code>][DataFrame.reverse],
 * which reverses the rows inside a single dataframe.
 *
 * @param [T] The schema marker type of the dataframes in this [<code>FrameColumn</code>][FrameColumn].
 * @return A new [<code>FrameColumn</code>][FrameColumn] with the dataframes in reversed order.
 */
public fun <T> FrameColumn<T>.reverse(): FrameColumn<T> = get(indices.reversed())

/**
 *
 * Returns a new [<code>ValueColumn</code>][ValueColumn] with the same values in reversed order,
 * so the last value becomes the first one.
 *
 * The column keeps its name and type.
 *
 * For more information: [See `reverse` on the documentation website.](https://kotlin.github.io/dataframe/reverse.html)
 *
 * See also [<code>shuffle</code>][DataColumn.shuffle],
 * which reorders values randomly.
 *
 * @param [T] The type of the values in this [<code>ValueColumn</code>][ValueColumn].
 * @return A new [<code>ValueColumn</code>][ValueColumn] with the values in reversed order.
 */
public fun <T> ValueColumn<T>.reverse(): ValueColumn<T> = get(indices.reversed()).asValueColumn()

// endregion
