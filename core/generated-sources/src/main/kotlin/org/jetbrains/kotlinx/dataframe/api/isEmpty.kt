package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

// region DataFrame

/**
 * Returns `true` if this [<code>DataFrame</code>][DataFrame] has no rows or no columns.
 *
 * Removing all columns with [<code>remove</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove] keeps the number of rows,
 * so a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] can have rows and no columns.
 * Such a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] holds no values, so it counts as empty as well as one without rows.
 *
 * A [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] too, and both cases apply to it:
 * a column group is empty when it has no nested columns,
 * or when the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that holds it has no rows.
 *
 * Nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s of a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are not taken into account:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] whose frame column holds only empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s is not empty itself.
 *
 * For more information: [See `isEmpty` on the documentation website.](https://kotlin.github.io/dataframe/isempty.html)
 *
 * See also:
 * - [<code>isNotEmpty</code>][DataFrame.isNotEmpty] — the opposite check.
 * - [<code>rowsCount</code>][DataFrame.rowsCount] and [<code>columnsCount</code>][DataFrame.columnsCount] — the two numbers behind it.
 * - [<code>DataFrame.Empty</code>][DataFrame.Empty], [<code>DataFrame.empty</code>][DataFrame.empty] and [<code>emptyDataFrame</code>][emptyDataFrame] — create an empty [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`isEmpty` page on the documentation website](https://kotlin.github.io/dataframe/isempty.html):
 *
 * | name    | age |
 * | :------ | :-- |
 * | Alice   | 15  |
 * | Charlie | 40  |
 *
 * ```kotlin
 * df.isEmpty() // false
 * df.filter { age > 100 }.isEmpty() // true: no row is left
 * df.remove { all() }.isEmpty() // true: no column is left, while both rows are still there
 * ```
 *
 * @return `true` if this [<code>DataFrame</code>][DataFrame] has no rows or no columns, `false` otherwise.
 */
public fun DataFrame<*>.isEmpty(): Boolean = ncol == 0 || nrow == 0

/**
 * Returns `true` if this [<code>DataFrame</code>][DataFrame] has at least one row and at least one column,
 * that is, if it is not empty.
 *
 * Removing all columns with [<code>remove</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove] keeps the number of rows,
 * so a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] can have rows and no columns.
 * Such a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] holds no values, so it counts as empty as well as one without rows.
 *
 * A [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] too, and both cases apply to it:
 * a column group is empty when it has no nested columns,
 * or when the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that holds it has no rows.
 *
 * Nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s of a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are not taken into account:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] whose frame column holds only empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s is not empty itself.
 *
 * For more information: [See `isNotEmpty` on the documentation website.](https://kotlin.github.io/dataframe/isempty.html#isnotempty)
 *
 * See also:
 * - [<code>isEmpty</code>][DataFrame.isEmpty] — the opposite check.
 * - [<code>rowsCount</code>][DataFrame.rowsCount] and [<code>columnsCount</code>][DataFrame.columnsCount] — the two numbers behind it.
 * - [<code>DataFrame.Empty</code>][DataFrame.Empty], [<code>DataFrame.empty</code>][DataFrame.empty] and [<code>emptyDataFrame</code>][emptyDataFrame] — create an empty [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`isEmpty` page on the documentation website](https://kotlin.github.io/dataframe/isempty.html):
 *
 * | name    | age |
 * | :------ | :-- |
 * | Alice   | 15  |
 * | Charlie | 40  |
 *
 * ```kotlin
 * df.isNotEmpty() // true
 * df.filter { age > 100 }.isNotEmpty() // false: no row is left
 * ```
 *
 * @return `true` if this [<code>DataFrame</code>][DataFrame] has at least one row and at least one column, `false` otherwise.
 */
public fun DataFrame<*>.isNotEmpty(): Boolean = !isEmpty()

// endregion
