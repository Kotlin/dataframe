package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.diff
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.index

/**
 * ## Selecting Rows
 *
 * Selecting rows that satisfy a "Row Condition" ([See Row Conditions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-conditions))
 * can occur in the following two types of operations:
 * - Selecting entire rows ([<code>Entire-Row Condition</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.EntireRowCondition.WithExample]), for instance in [<code>filter</code>][filter], [<code>drop</code>][drop], [<code>first</code>][first], and [<code>count</code>][count]
 * (using [<code>RowFilter</code>][RowFilter]).
 * - Selecting parts of rows using a `where` operation after selecting columns ([<code>Row-Value Condition</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]),
 * such as with [<code>update</code>][update], [<code>gather</code>][gather], and [<code>format</code>][format]
 * (using [<code>RowValueFilter</code>][RowValueFilter]).
 *
 * A Row Condition is similar to a [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow] but expects a [<code>Boolean</code>][Boolean] as result.
 */
internal interface SelectingRows {

    /** Filter or find rows to operate on using a [<code>row filter</code>][RowFilter]. */
    interface EntireRowCondition {

        /**
         * Filter or find rows to operate on using a [<code>row filter</code>][org.jetbrains.kotlinx.dataframe.RowFilter].
         *
         * For example:
         *
         * `df.`operation` { `[<code>index</code>][index]`() % 2 == 0 }`
         *
         * `df.`operation` { `[<code>diff</code>][diff]` { age } == 0 }`
         *
         */
        typealias WithExample = Nothing
    }

    /** Filter or find rows to operate on after [<code>selecting columns</code>][SelectingColumns] using a
     * [<code>row value filter</code>][RowValueFilter].
     */
    interface RowValueCondition {

        /**
         * Filter or find rows to operate on after [<code>selecting columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] using a
         * [<code>row value filter</code>][org.jetbrains.kotlinx.dataframe.RowValueFilter].
         *
         * For example:
         *
         * `df.`operation` { length }.`where` { it > 10.0 }`
         *
         * `df.`operation` { `[<code>cols</code>][ColumnsSelectionDsl.cols]`(1..5) }.`where` { `[<code>index</code>][index]`() > 4 && city != "Paris" }`
         *
         */
        typealias WithExample = Nothing
    }
}
