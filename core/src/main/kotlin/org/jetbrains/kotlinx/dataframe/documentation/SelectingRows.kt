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
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowConditionLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueConditionLink
import org.jetbrains.kotlinx.dataframe.index

/**
 * ## Selecting Rows
 * Selecting rows that satisfy a "Row Condition" ({@include [DocumentationUrls.DataRow.RowConditions]})
 * can occur in the following two types of operations:
 * - Selecting entire rows ({@include [RowConditionLink]}), for instance in [filter], [drop], [first], and [count]
 * (using [RowFilter]).
 * - Selecting parts of rows using a `where` operation after selecting columns ({@include [RowValueConditionLink]}),
 * such as with [update], [gather], and [format]
 * (using [RowValueFilter]).
 *
 * A Row Condition is similar to a {@include [RowExpressionsLink]} but expects a [Boolean] as result.
 */
internal interface SelectingRows {

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface FirstOperationArg

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface SecondOperationArg

    /** {@set [FirstOperationArg] operation}{@set [SecondOperationArg] where} */
    interface SetDefaultOperationArg

    /** [Entire-Row Condition][EntireRowCondition.WithExample] */
    interface RowConditionLink

    /** Filter or find rows to operate on using a [row filter][RowFilter]. */
    interface EntireRowCondition {

        /**
         * {@include [EntireRowCondition]}
         *
         * For example:
         *
         * `df.`{@get [FirstOperationArg]}` { `[index][index]`() % 2 == 0 }`
         *
         * `df.`{@get [FirstOperationArg]}` { `[diff][diff]` { age } == 0 }`
         * @include [SetDefaultOperationArg]
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
         * {@include [RowValueCondition]}
         *
         * For example:
         *
         * `df.`{@get [FirstOperationArg]}` { length }.`{@get [SecondOperationArg]}` { it > 10.0 }`
         *
         * `df.`{@get [FirstOperationArg]}` { `[cols][ColumnsSelectionDsl.cols]`(1..5) }.`{@get [SecondOperationArg]}` { `[index][index]`() > 4 && city != "Paris" }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }
}

/** [Row Condition][SelectingRows] */
internal interface RowConditionLink
