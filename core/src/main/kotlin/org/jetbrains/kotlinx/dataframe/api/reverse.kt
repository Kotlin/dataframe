package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn
import org.jetbrains.kotlinx.dataframe.indices

/**
 * {@comment Shared KDoc template for all `reverse` overloads. KDoc-snippet.}
 * Returns a new {@get [RECEIVER]} with the same {@get [UNIT]}s in reversed order,
 * so the last {@get [UNIT]} becomes the first one.
 *
 * {@get [DETAILS]}
 *
 * For more information: {@include [DocumentationUrls.Reverse]}
 *
 * {@get [SEE_ALSO]}
 *
 * @param [T\] {@get [TYPE_PARAM]}
 * @return A new {@get [RECEIVER]} with the {@get [UNIT]}s in reversed order.
 */
@ExcludeFromSources
internal interface CommonReverseDocs {

    // Receiver type link, like "[DataFrame]"
    typealias RECEIVER = Nothing

    // The reordered unit, like "row" or "value"
    typealias UNIT = Nothing

    // Overload-specific paragraph about what exactly is (not) affected
    typealias DETAILS = Nothing

    // Overload-specific "See also" paragraph
    typealias SEE_ALSO = Nothing

    // Description of the [T] type parameter
    typealias TYPE_PARAM = Nothing
}

// region DataFrame

/**
 * @include [CommonReverseDocs]
 * {@set [CommonReverseDocs.RECEIVER] [DataFrame]}
 * {@set [CommonReverseDocs.UNIT] row}
 * {@set [CommonReverseDocs.DETAILS] Only the order of the rows changes:
 * the columns, their names and types, and thus the schema of the dataframe stay the same.}
 * {@set [CommonReverseDocs.SEE_ALSO] See also [shuffle][DataFrame.shuffle], which reorders rows randomly,
 * and [sortBy][DataFrame.sortBy], which orders rows by the values of the selected columns.}
 * {@set [CommonReverseDocs.TYPE_PARAM] The schema marker type of this [DataFrame].}
 */
public fun <T> DataFrame<T>.reverse(): DataFrame<T> = get(indices.reversed())

// endregion

// region DataColumn

/**
 * @include [CommonReverseDocs]
 * {@set [CommonReverseDocs.RECEIVER] [DataColumn]}
 * {@set [CommonReverseDocs.UNIT] value}
 * {@set [CommonReverseDocs.DETAILS] The column keeps its name, type and [kind][ColumnKind]:
 * a value column stays a value column, a column group stays a column group,
 * and a frame column stays a frame column.
 * The result is typed as [DataColumn] though, so for a column group use
 * [asColumnGroup][DataColumn.asColumnGroup] to get [ColumnGroup] back.}
 * {@set [CommonReverseDocs.SEE_ALSO] See also [shuffle][DataColumn.shuffle],
 * which reorders values randomly.}
 * {@set [CommonReverseDocs.TYPE_PARAM] The type of the values in this [DataColumn].}
 */
public fun <T> DataColumn<T>.reverse(): DataColumn<T> = get(indices.reversed())

/**
 * @include [CommonReverseDocs]
 * {@set [CommonReverseDocs.RECEIVER] [ColumnGroup]}
 * {@set [CommonReverseDocs.UNIT] row}
 * {@set [CommonReverseDocs.DETAILS] The group keeps its name and its nested column structure.
 * Reversing is applied to the group as a whole, so the values in all nested columns
 * stay aligned row-wise.}
 * {@set [CommonReverseDocs.SEE_ALSO] See also [shuffle][DataFrame.shuffle],
 * which reorders rows randomly.}
 * {@set [CommonReverseDocs.TYPE_PARAM] The schema marker type of this [ColumnGroup].}
 */
public fun <T> ColumnGroup<T>.reverse(): ColumnGroup<T> = get(indices.reversed())

/**
 * @include [CommonReverseDocs]
 * {@set [CommonReverseDocs.RECEIVER] [FrameColumn]}
 * {@set [CommonReverseDocs.UNIT] dataframe}
 * {@set [CommonReverseDocs.DETAILS] The column keeps its name and type.
 * Only the order of the dataframes in it changes;
 * the rows inside each of them keep their original order.}
 * {@set [CommonReverseDocs.SEE_ALSO] See also [reverse][DataFrame.reverse],
 * which reverses the rows inside a single dataframe.}
 * {@set [CommonReverseDocs.TYPE_PARAM] The schema marker type of the dataframes in this [FrameColumn].}
 */
public fun <T> FrameColumn<T>.reverse(): FrameColumn<T> = get(indices.reversed())

/**
 * @include [CommonReverseDocs]
 * {@set [CommonReverseDocs.RECEIVER] [ValueColumn]}
 * {@set [CommonReverseDocs.UNIT] value}
 * {@set [CommonReverseDocs.DETAILS] The column keeps its name and type.}
 * {@set [CommonReverseDocs.SEE_ALSO] See also [shuffle][DataColumn.shuffle],
 * which reorders values randomly.}
 * {@set [CommonReverseDocs.TYPE_PARAM] The type of the values in this [ValueColumn].}
 */
public fun <T> ValueColumn<T>.reverse(): ValueColumn<T> = get(indices.reversed()).asValueColumn()

// endregion
