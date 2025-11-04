@file:Suppress("ClassName")

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
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
 * Selecting rows that satisfy a "Row Condition" ([See Row Conditions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-conditions))
 * can occur in the following two types of operations:
 * - Selecting entire rows ([Entire-Row Condition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.EntireRowCondition.WithExample]), for instance in [filter], [drop], [first], and [count]
 * (using [RowFilter]).
 * - Selecting parts of rows using a `where` operation after selecting columns ([Row-Value Condition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]),
 * such as with [update], [gather], and [format]
 * (using [RowValueFilter]).
 *
 * A Row Condition is similar to a [Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow] but expects a [Boolean] as result.
 */
internal interface SelectingRows {

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    typealias FIRST_OPERATION = Nothing

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    typealias SECOND_OPERATION = Nothing

    typealias SetDefaultOperationArg = Nothing

    /** [Entire-Row Condition][EntireRowCondition.WithExample] */
    typealias RowConditionLink = Nothing

    /** Filter or find rows to operate on using a [row filter][RowFilter]. */
    interface EntireRowCondition {

        /**
         * Filter or find rows to operate on using a [row filter][org.jetbrains.kotlinx.dataframe.RowFilter].
         *
         * For example:
         *
         * `df.`operation` { `[index][index]`() % 2 == 0 }`
         *
         * `df.`operation` { `[diff][diff]` { age } == 0 }`
         *
         */
        typealias WithExample = Nothing
    }

    /** [Row-Value Condition][RowValueCondition.WithExample] */
    typealias RowValueConditionLink = Nothing

    /** Filter or find rows to operate on after [selecting columns][SelectingColumns] using a
     * [row value filter][RowValueFilter].
     */
    interface RowValueCondition {

        /**
         * Filter or find rows to operate on after [selecting columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] using a
         * [row value filter][org.jetbrains.kotlinx.dataframe.RowValueFilter].
         *
         * For example:
         *
         * `df.`operation` { length }.`where` { it > 10.0 }`
         *
         * `df.`operation` { `[cols][ColumnsSelectionDsl.cols]`(1..5) }.`where` { `[index][index]`() > 4 && city != "Paris" }`
         *
         */
        typealias WithExample = Nothing
    }
}

/** [Row Condition][SelectingRows] */
internal typealias RowConditionLink = Nothing
