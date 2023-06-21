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
 * ## {@includeArg [TitleArg]} (Children)
 * This function {@includeArg [NounArg]}s the {@includeArg [FirstOrLastArg]} [n\] columns of a [ColumnGroup] or [ColumnSet].
 *
 * If called on a [SingleColumn] containing a [ColumnGroup],
 * [{@includeArg [OperationArg]}Children][SingleColumn.{@includeArg [OperationArg]}Children] will {@includeArg [NounArg]} the {@includeArg [FirstOrLastArg]} [n\] children of that column group.
 *
 * Else, if called on a [ColumnSet], [{@includeArg [OperationArg]}][ColumnSet.{@includeArg [OperationArg]}] will {@includeArg [NounArg]} the {@includeArg [FirstOrLastArg]} [n\] columns of that column set.
 *
 * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@includeArg [CommonTakeAndDropDocs.OperationArg]}` is called `{@includeArg [CommonTakeAndDropDocs.OperationArg]}Children` when called on
 * a [ColumnGroup].
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[{@includeArg [OperationArg]}][ColumnSet.{@includeArg [OperationArg]}]`(5) }`
 *
 * `df.`[select][DataFrame.select]` { `[{@includeArg [OperationArg]}][ColumnsSelectionDsl.{@includeArg [OperationArg]}]`(1) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[{@includeArg [OperationArg]}Children][SingleColumn.{@includeArg [OperationArg]}Children]`(2) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[{@includeArg [OperationArg]}Children][String.{@includeArg [OperationArg]}Children]`(3) }`
 *
 * #### Examples for this overload:
 *
 * {@includeArg [CommonTakeAndDropDocs.ExampleArg]}
 *
 * @param [n\] The number of columns to {@includeArg [NounArg]}.
 * @return A [ColumnSet] containing the {@includeArg [FirstOrLastArg]} [n\] columns.
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
