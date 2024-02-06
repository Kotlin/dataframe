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
 * ## {@get [TitleArg]} (Cols)
 * This {@get [NounArg]}s the {@get [FirstOrLastArg]} [n\] columns from [this\] collecting
 * the result into a [ColumnSet].
 *
 * This function operates solely on columns at the top-level.
 *
 * Any {@include [AccessApiLink]} can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@get [CommonTakeAndDropDocs.OperationArg]}` is called `{@get [CommonTakeAndDropDocs.OperationArg]}Cols` when called on
 * a [ColumnGroup].
 *
 * ### Check out: [Grammar\]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[{@get [OperationArg]}][ColumnSet.{@get [OperationArg]}]`(5) }`
 *
 * `df.`[select][DataFrame.select]` { `[{@get [OperationArg]}][ColumnsSelectionDsl.{@get [OperationArg]}]`(1) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[{@get [OperationArg]}Cols][SingleColumn.{@get [OperationArg]}Cols]`(2) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[{@get [OperationArg]}Cols][String.{@get [OperationArg]}Cols]`(3) }`
 *
 * #### Examples for this overload:
 *
 * {@get [CommonTakeAndDropDocs.ExampleArg]}
 *
 * @param [n\] The number of columns to {@get [NounArg]}.
 * @return A [ColumnSet] containing the {@get [FirstOrLastArg]} [n\] columns.
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
