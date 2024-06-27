@file:ExcludeFromSources

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
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.FirstOrLastArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.NounArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.OperationArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.TitleArg

/**
 * ## {@get [TitleArg]} (Cols) While
 * This function {@get [NounArg]}s the {@get [FirstOrLastArg]} columns from [this\] adhering to the
 * given [predicate\] collecting the result into a [ColumnSet].
 *
 * This function operates solely on columns at the top-level.
 *
 * Any {@include [AccessApiLink]} can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@get [CommonTakeAndDropWhileDocs.OperationArg]}While` is called
 * `{@get [CommonTakeAndDropWhileDocs.OperationArg]}ColsWhile` when called on a [String] or [ColumnPath] resembling
 * a [ColumnGroup].
 *
 * ### Check out: [Usage\]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][DataColumn.name]` }.`[\`{@get [OperationArg]}While\`][ColumnSet.{@get [OperationArg]}While]` { "my" `[`in`][String.contains]` it.`[`name`][DataColumn.name]` } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[\`{@get [OperationArg]}While\`][SingleColumn.{@get [OperationArg]}ColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[\`{@get [OperationArg]}ColsWhile\`][String.{@get [OperationArg]}ColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][ColumnKind.Value]` } }`
 *
 * #### Examples for this overload:
 *
 * {@get [CommonTakeAndDropWhileDocs.ExampleArg]}
 *
 * @param [predicate\] The [ColumnFilter] to control which columns to {@get [NounArg]}.
 * @return A [ColumnSet] containing the {@get [FirstOrLastArg]} columns adhering to the [predicate\].
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
