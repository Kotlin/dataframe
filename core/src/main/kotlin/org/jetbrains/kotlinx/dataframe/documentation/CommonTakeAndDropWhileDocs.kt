@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

/**
 * ## {@get [TITLE]} (Cols) While
 * This function {@get [NOUN]}s the {@get [FIRST_OR_LAST]} columns from [this\] for as long as the
 * given [predicate\] holds, stopping as soon as a column does not adhere to it, and collects the
 * result into a [ColumnSet].
 *
 * This function operates solely on columns at the top-level.
 *
 * Any {@include [AccessApiLink]} can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@get [CommonTakeAndDropWhileDocs.OPERATION]}While` is called
 * `{@get [CommonTakeAndDropWhileDocs.OPERATION]}ColsWhile` when called on a [String] or [ColumnPath] resembling
 * a [ColumnGroup].
 *
 * For more information: {@get [URL]}
 *
 * ### Check out: [Grammar\]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][DataColumn.name]` }.`[\`{@get [OPERATION]}While\`][ColumnSet.{@get [OPERATION]}While]` { "my" `[`in`][String.contains]` it.`[`name`][DataColumn.name]` } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[\`{@get [OPERATION]}While\`][SingleColumn.{@get [OPERATION]}ColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[\`{@get [OPERATION]}ColsWhile\`][String.{@get [OPERATION]}ColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][ColumnKind.Value]` } }`
 *
 * #### Examples for this overload:
 *
 * {@get [CommonTakeAndDropWhileDocs.EXAMPLE]}
 *
 * See also:
 * {@get [SEE_ALSO]}
 *
 * @param [predicate\] The [ColumnFilter] to control which columns to {@get [NOUN]}.
 * @return {@get [RETURN]}
 */
internal interface CommonTakeAndDropWhileDocs {

    // Title, like "Take Last"
    typealias TITLE = Nothing

    // Operation, like "takeLast"
    typealias OPERATION = Nothing

    // Operation, like "take"
    typealias NOUN = Nothing

    // like "last"
    typealias FIRST_OR_LAST = Nothing

    // Example argument to use
    typealias EXAMPLE = Nothing

    // Link to the corresponding page on the documentation website
    typealias URL = Nothing

    // Related operations (the `See also` part)
    typealias SEE_ALSO = Nothing

    // Value returned by the operation (the `@return` part)
    typealias RETURN = Nothing
}
