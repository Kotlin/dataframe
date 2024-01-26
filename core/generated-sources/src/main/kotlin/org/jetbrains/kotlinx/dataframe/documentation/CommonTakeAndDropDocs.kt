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
 * ## {@getArg [TitleArg]} (Cols)
 * This {@getArg [NounArg]}s the {@getArg [FirstOrLastArg]} [n] columns from [this] collecting
 * the result into a [ColumnSet].
 *
 * This function only looks at columns at the top-level.
 *
 * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@getArg [CommonTakeAndDropDocs.OperationArg]}` is called `{@getArg [CommonTakeAndDropDocs.OperationArg]}Cols` when called on
 * a [ColumnGroup].
 *
 * ### Check out: [Grammar]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[{@getArg [OperationArg]}][ColumnSet.{@getArg [OperationArg]}]`(5) }`
 *
 * `df.`[select][DataFrame.select]` { `[{@getArg [OperationArg]}][ColumnsSelectionDsl.{@getArg [OperationArg]}]`(1) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[{@getArg [OperationArg]}Cols][SingleColumn.{@getArg [OperationArg]}Cols]`(2) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[{@getArg [OperationArg]}Cols][String.{@getArg [OperationArg]}Cols]`(3) }`
 *
 * #### Examples for this overload:
 *
 * {@getArg [CommonTakeAndDropDocs.ExampleArg]}
 *
 * @param [n] The number of columns to {@getArg [NounArg]}.
 * @return A [ColumnSet] containing the {@getArg [FirstOrLastArg]} [n] columns.
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
