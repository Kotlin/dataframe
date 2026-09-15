package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

@ExcludeFromSources
internal interface IsEmptyDocs {

    /**
     * Removing all columns with [remove][DataFrame.remove] keeps the number of rows,
     * so a [DataFrame] can have rows and no columns.
     * Such a [DataFrame] holds no values, so it counts as empty as well as one without rows.
     *
     * A [ColumnGroup] is a [DataFrame] too, and both cases apply to it:
     * a column group is empty when it has no nested columns,
     * or when the [DataFrame] that holds it has no rows.
     *
     * Nested [DataFrame]s of a [FrameColumn] are not taken into account:
     * a [DataFrame] whose frame column holds only empty [DataFrame]s is not empty itself.
     */
    @ExcludeFromSources
    typealias EmptinessSnippet = Nothing

    /**
     * {@comment The input of every `isEmpty` / `isNotEmpty` example. KDoc-snippet.
     *    Every result that follows it is an expected value in `IsEmptyTests`.}
     *
     * The examples below use this [DataFrame], the same data as the
     * [`isEmpty` page on the documentation website]({@include [DocumentationUrls.Url]}/isempty.html):
     *
     * | name    | age |
     * | :------ | :-- |
     * | Alice   | 15  |
     * | Charlie | 40  |
     */
    @ExcludeFromSources
    typealias ExampleDataSnippet = Nothing
}

// region DataFrame

/**
 * Returns `true` if this [DataFrame] has no rows or no columns.
 *
 * @include [IsEmptyDocs.EmptinessSnippet]
 *
 * For more information: {@include [DocumentationUrls.IsEmpty]}
 *
 * See also:
 * - [isNotEmpty][DataFrame.isNotEmpty] — the opposite check.
 * - [rowsCount][DataFrame.rowsCount] and [columnsCount][DataFrame.columnsCount] — the two numbers behind it.
 * - [DataFrame.Empty], [DataFrame.empty] and [emptyDataFrame] — create an empty [DataFrame].
 *
 * ### Example
 *
 * @include [IsEmptyDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * df.isEmpty() // false
 * df.filter { age > 100 }.isEmpty() // true: no row is left
 * df.remove { all() }.isEmpty() // true: no column is left, while both rows are still there
 * ```
 *
 * @return `true` if this [DataFrame] has no rows or no columns, `false` otherwise.
 */
public fun DataFrame<*>.isEmpty(): Boolean = ncol == 0 || nrow == 0

/**
 * Returns `true` if this [DataFrame] has at least one row and at least one column,
 * that is, if it is not empty.
 *
 * @include [IsEmptyDocs.EmptinessSnippet]
 *
 * For more information: {@include [DocumentationUrls.IsNotEmpty]}
 *
 * See also:
 * - [isEmpty][DataFrame.isEmpty] — the opposite check.
 * - [rowsCount][DataFrame.rowsCount] and [columnsCount][DataFrame.columnsCount] — the two numbers behind it.
 * - [DataFrame.Empty], [DataFrame.empty] and [emptyDataFrame] — create an empty [DataFrame].
 *
 * ### Example
 *
 * @include [IsEmptyDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * df.isNotEmpty() // true
 * df.filter { age > 100 }.isNotEmpty() // false: no row is left
 * ```
 *
 * @return `true` if this [DataFrame] has at least one row and at least one column, `false` otherwise.
 */
public fun DataFrame<*>.isNotEmpty(): Boolean = !isEmpty()

// endregion
