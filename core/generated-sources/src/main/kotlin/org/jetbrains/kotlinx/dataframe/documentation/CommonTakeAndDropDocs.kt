package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.FirstOrLastArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.NounArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.OperationArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.TitleArg

/**
 * ##  (Cols)
 * This s the  [n] columns from [this] collecting
 * the result into a [ColumnSet].
 *
 * This function only looks at columns at the top-level.
 *
 * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `` is called `Cols` when called on
 * a [ColumnGroup].
 *
 * ### Check out: [Grammar]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[][ColumnSet.]`(5) }`
 *
 * `df.`[select][DataFrame.select]` { `[][ColumnsSelectionDsl.]`(1) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[Cols][SingleColumn.Cols]`(2) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[Cols][String.Cols]`(3) }`
 *
 * #### Examples for this overload:
 *
 *
 *
 * @param [n] The number of columns to .
 * @return A [ColumnSet] containing the  [n] columns.
 */
internal interface CommonTakeAndDropDocs {

    /** Title, like "Take Last" */
    interface TitleArg

    /** Operation, like "takeLast" */
    interface OperationArg

    /** Operation, like "take" */
    interface NounArg

    /** like "first" */
    interface FirstOrLastArg

    /** Example argument to use */
    interface ExampleArg
}
