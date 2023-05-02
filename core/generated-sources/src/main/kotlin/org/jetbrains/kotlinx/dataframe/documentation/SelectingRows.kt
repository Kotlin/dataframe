package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowConditionLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueConditionLink
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

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface FirstOperationArg

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface SecondOperationArg

    interface SetDefaultOperationArg

    /** [Entire-Row Condition][EntireRowCondition.WithExample] */
    interface RowConditionLink

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
        interface WithExample
    }

    /** [Row-Value Condition][RowValueCondition.WithExample] */
    interface RowValueConditionLink

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
        interface WithExample
    }
}

/** [Row Condition][SelectingRows] */
internal interface RowConditionLink
