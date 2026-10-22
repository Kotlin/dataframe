package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.implodeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Merges values in the specified [\columns] into lists, grouped by the values in all other columns —
 * that is, rows with equal values in the other columns are collapsed into a single row.
 *
 * The other columns are used as grouping keys and keep their types.
 *
 * If [\dropNA] is `true`, `NA` values are removed from the merged lists.
 *
 * Imploded columns will change their types:
 * - `T` to `List<T>`;
 * - [DataRow] to [DataFrame] — an imploded [ColumnGroup] is converted into a [FrameColumn].
 *
 * Reverse operation: [explode].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Implode]}
 *
 * ### This `implode` overload
 */
@ExcludeFromSources
internal interface ImplodeDocs {
    /**
     * @param [\dropNA] If `true`, removes `NA` values (`null`, [Double.NaN] or [Float.NaN])
     *   from the merged lists. `false` by default.
     */
    typealias DropNASnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per distinct combination of values in the other columns,
     *   where the [\columns] contain merged lists of values.
     */
    typealias ReturnSnippet = Nothing
}

/**
 * Merges all values of every column of this [DataFrame] into a single [DataRow].
 *
 * Each value of the resulting row is a [List] of all values in the corresponding column.
 * A [ColumnGroup] is converted into a [DataFrame] containing its rows.
 *
 * If [dropNA] is `true`, `NA` values are removed from the merged lists.
 *
 * It is the same as `df.implode(dropNA) { all() }[0]`.
 *
 * Reverse operation: [explode].
 *
 * For more information: {@include [DocumentationUrls.Implode]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ImplodeSamples.implodeAll]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @include [ImplodeDocs.DropNASnippet]
 * @return A [DataRow] with all values of every column merged into lists.
 */
@Refine
@Interpretable("ImplodeDefault")
public fun <T> DataFrame<T>.implode(dropNA: Boolean = false): DataRow<T> = implode(dropNA) { all() }[0]

/**
 * {@include [ImplodeDocs]}
 * {@include [SelectingColumns.ColumnsSelectionDsl]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ImplodeSamples.implodeSelector_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ImplodeSamples.implodeDropNA_properties]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [C] The common type of the selected columns.
 * @include [ImplodeDocs.DropNASnippet]
 * @param [columns] The [ColumnsSelector] used to select columns to implode.
 * @include [ImplodeDocs.ReturnSnippet]
 */
@Refine
@Interpretable("Implode")
public fun <T, C> DataFrame<T>.implode(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNA, columns)

/**
 * {@include [ImplodeDocs]}
 * {@include [SelectingColumns.ColumnNamesApi]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ImplodeSamples.implodeSelector_strings]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [columns] The [column names][String] used to select columns to implode.
 * @include [ImplodeDocs.DropNASnippet]
 * @include [ImplodeDocs.ReturnSnippet]
 */
@Refine
@StringApiInterpretable(interpreter = "Implode", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.implode(vararg columns: String, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.implode(vararg columns: ColumnReference<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.implode(vararg columns: KProperty<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

// endregion
