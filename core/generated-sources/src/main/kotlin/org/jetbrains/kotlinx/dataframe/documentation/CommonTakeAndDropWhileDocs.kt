package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.FirstOrLastArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.NounArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.OperationArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.TitleArg

/**
 * ## {@includeArg [TitleArg]} (Children) While
 * This function {@includeArg [NounArg]}s the {@includeArg [FirstOrLastArg]} columns of a [ColumnGroup] or
 * [ColumnSet] adhering to the given [predicate\].
 *
 * If called on a [SingleColumn] containing a [ColumnGroup],
 * [{@includeArg [OperationArg]}While][SingleColumn.{@includeArg [OperationArg]}While] will {@includeArg [NounArg]} the
 * {@includeArg [FirstOrLastArg]} children of that column group adhering to the given [predicate\].
 *
 * Else, if called on a [ColumnSet], [{@includeArg [OperationArg]}While][ColumnSet.{@includeArg [OperationArg]}While] will
 * {@includeArg [NounArg]} the {@includeArg [FirstOrLastArg]} columns of that column set adhering to the given [predicate\].
 *
 * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@includeArg [CommonTakeAndDropWhileDocs.OperationArg]}While` is called
 * `{@includeArg [CommonTakeAndDropWhileDocs.OperationArg]}ChildrenWhile` when called on a [String] or [ColumnPath] resembling
 * a [ColumnGroup].
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[{@includeArg [OperationArg]}While][ColumnSet.{@includeArg [OperationArg]}While]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[{@includeArg [OperationArg]}While][SingleColumn.{@includeArg [OperationArg]}While]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[{@includeArg [OperationArg]}ChildrenWhile][String.{@includeArg [OperationArg]}ChildrenWhile]` { it.`[kind][ColumnWithPath.kind]`() == `[ColumnKind.Value][ColumnKind.Value]` } }`
 *
 * #### Examples for this overload:
 *
 * {@includeArg [CommonTakeAndDropWhileDocs.ExampleArg]}
 *
 * @param [predicate\] The [ColumnFilter] to control which columns to {@includeArg [NounArg]}.
 * @return A [ColumnSet] containing the {@includeArg [FirstOrLastArg]} columns adhering to the [predicate\].
 */
internal interface CommonTakeAndDropWhileDocs {

    /** Title, like "Take Last" */
    interface TitleArg

    /** Operation, like "takeLast" */
    interface OperationArg

    /** Operation, like "take" */
    interface NounArg

    /** like "last" */
    interface FirstOrLastArg

    /** Example argument to use */
    interface ExampleArg
}
