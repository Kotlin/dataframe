@file:ExcludeFromSources
@file:Suppress("ClassName")

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet

/**
 * ## {@get [TITLE]} (Cols)
 * {@get [SUMMARY]}
 *
 * This function operates solely on columns at the top-level.
 *
 * Any {@include [AccessApiLink]} can be used as receiver for these functions.
 *
 * NOTE: To avoid ambiguity, `{@get [CommonTakeAndDropDocs.OPERATION]}` is called `{@get [CommonTakeAndDropDocs.OPERATION]}Cols` when called on
 * a [ColumnGroup].
 *
 * For more information: {@get [URL]}
 *
 * ### Check out: [Grammar\]
 *
 * #### Examples:
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[{@get [OPERATION]}][ColumnSet.{@get [OPERATION]}]`(5) }`
 *
 * `df.`[select][DataFrame.select]` { `[{@get [OPERATION]}][ColumnsSelectionDsl.{@get [OPERATION]}]`(1) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[{@get [OPERATION]}Cols][SingleColumn.{@get [OPERATION]}Cols]`(2) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[{@get [OPERATION]}Cols][String.{@get [OPERATION]}Cols]`(3) }`
 *
 * #### Examples for this overload:
 *
 * {@get [CommonTakeAndDropDocs.EXAMPLE]}
 *
 * See also:
 * {@get [SEE_ALSO]}
 *
 * @param [n\] The number of columns to {@get [NOUN]}.
 * @return {@get [RETURN]}
 */
internal interface CommonTakeAndDropDocs {

    // Title, like "Take Last"
    typealias TITLE = Nothing

    // Operation, like "takeLast"
    typealias OPERATION = Nothing

    // Operation, like "take"
    typealias NOUN = Nothing

    // Summary of the operation
    typealias SUMMARY = Nothing

    // Example argument to use
    typealias EXAMPLE = Nothing

    // Link to the corresponding page on the documentation website
    typealias URL = Nothing

    // Related operations (the `See also` part)
    typealias SEE_ALSO = Nothing

    // Value returned by the operation (the `@return` part)
    typealias RETURN = Nothing
}
