package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*

/**
 * Selecting rows that satisfy a "Row Condition" ({@include [DocumentationUrls.DataRow.RowConditions]})
 * can occur in the following two types of operations:
 * - Selecting entire rows ({@include [RowConditionLink]}), for instance in [filter], [drop], [first], and [count].
 * - Selecting parts of rows using a `where` operation after selecting columns ({@include [RowValueConditionLink]}),
 * such as with [update], [gather], and [format] ([RowValueFilter]).
 * {@comment TODO Is `where` present everywhere it should be?}
 */
internal interface SelectingRows {

    /** {@arg [OperationArg] <operation>} */
    interface SetDefaultOperationArg

    /** [Entire-Row Condition][EntireRowCondition] */
    interface RowConditionLink

    /** Filter or find rows to operate on using a [row filter][RowFilter].
     *
     * For example:
     *
     * `df.`{@includeArg [OperationArg]}` { `[index][index]`() % 2 == 0 }`
     *
     * `df.`{@includeArg [OperationArg]}` { `[diff][diff]` { age } == 0 }`
     */
    interface EntireRowCondition

    /** [Row-Value Condition][RowValueCondition] */
    interface RowValueConditionLink

    /** Filter or find rows to operate on after [selecting columns][SelectingColumns] using a
     * [row value filter][RowValueFilter].
     *
     * For example:
     *
     * `df.`{@includeArg [OperationArg]}` { length }.`[where][where]` { it > 10.0 }`
     *
     * `df.`{@includeArg [OperationArg]}` { `[cols][ColumnsSelectionDsl.cols]`(1..5) }.`[where][where]` { `[index][index]`() > 4 && city != "Paris" }`
     *
     * @include [SetDefaultOperationArg]
     */
    interface RowValueCondition
}
