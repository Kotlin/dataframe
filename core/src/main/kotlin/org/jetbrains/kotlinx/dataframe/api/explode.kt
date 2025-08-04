package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

private val defaultExplodeColumns: ColumnsSelector<*, *> = {
    colsAtAnyDepth().filter { it.isList() || it.isFrameColumn() }
}

// region explode DataFrame

/**
 * Splits list-like values in the specified [\columns] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row).
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [\columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, rows with empty lists or [DataFrame]s will be removed.
 * If `false`, such rows will be exploded into `null` values.
 *
 * Returns a new [DataFrame] with exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` -> `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * This operation is the reverse of [implode].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, see: {@include [DocumentationUrls.Explode]}
 *
 * ### This `explode` overload
 */
@ExcludeFromSources
internal interface ExplodeDocs

/**
 * {@include [ExplodeDocs]}
 * {@include [SelectingColumns.Dsl]}
 *
 * #### Examples
 *
 * ```kotlin
 * // Explodes all `List` and `DataFrame` columns at any depth
 * df.explode()
 *
 * // Explodes the "tags" column of type `List<String>`
 * df.explode { tags }
 *
 * // Explodes all columns of type `List<Double>`
 * df.explode { colsOf<List<Double>>() }
 * ```
 *
 * @param dropEmpty If `true`, removes rows with empty lists or DataFrames.
 *                  If `false`, such rows will be exploded into `null` values.
 * @param selector The [ColumnsSelector] used to select columns to explode.
 *                If not specified, all applicable columns will be exploded.
 * @return A new [DataFrame] with exploded columns.
 */
@Refine
@Interpretable("Explode0")
public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns,
): DataFrame<T> = explodeImpl(dropEmpty, selector)

/**
 * {@include [ExplodeDocs]}
 * {@include [SelectingColumns.ColumnNames]}
 *
 * #### Example
 *
 * ```kotlin
 * // Explodes the "tags" and "scores" columns, where
 * // "tags" is a `List<String>` and "scores" is a `List<Int>`
 * val exploded = df.explode("tags", "scores")
 * ```
 *
 * @param dropEmpty If `true`, removes rows with empty lists or DataFrames.
 *                  If `false`, such rows will be exploded into `null` values.
 * @param columns The [column names][String] used to select columns to explode.
 *                If not specified, all applicable columns will be exploded.
 * @return A new [DataFrame] with exploded columns.
 */
public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataRow

/**
 * Splits list-like values in the specified [\columns] of this [DataRow] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row)
 * and combine them into new [DataFrame].
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [\columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, the result will exclude rows with empty lists or DataFrames.
 * If `false`, such values will be exploded into `null`.
 *
 * Returns a new [DataFrame] expanded into multiple rows based on the exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` → `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information, see: {@include [DocumentationUrls.Explode]}
 *
 * ### This `explode` overload
 */
@ExcludeFromSources
internal interface ExplodeDataRowDocs

/**
 * {@include [ExplodeDataRowDocs]}
 * {@include [SelectingColumns.Dsl]}
 *
 * #### Example
 *
 * ```kotlin
 * // Explodes the `hobbies` and `scores` values of the row,
 * // of types `List<String>` and `List<Int>`, respectively
 * row.explode { hobbies and scores }
 * ```
 *
 * @param dropEmpty If `true`, removes rows with empty lists or DataFrames.
 *                  If `false`, such rows will be exploded into `null` values.
 * @param columns The [ColumnsSelector] used to select columns to explode.
 *                 If not specified, all applicable columns will be exploded.
 * @return A new [DataFrame] with exploded columns from this [DataRow].
 */
public fun <T> DataRow<T>.explode(
    dropEmpty: Boolean = true,
    columns: ColumnsSelector<T, *> = defaultExplodeColumns,
): DataFrame<T> = toDataFrame().explode(dropEmpty, columns)

/**
 * {@include [ExplodeDataRowDocs]}
 * {@include [SelectingColumns.ColumnNames]}
 *
 * #### Example
 *
 * ```kotlin
 * // Explodes the `hobbies` and `scores` values of the row,
 * // of types `List<String>` and `List<Int>`, respectively
 * row.explode("hobbies", "scores")
 * ```
 *
 * @param dropEmpty If `true`, removes rows with empty lists or DataFrames.
 *                  If `false`, such rows will be exploded into `null` values.
 * @param columns The [column names][String] used to select columns to explode.
 *                 If not specified, all applicable columns will be exploded.
 * @return A new [DataFrame] with exploded columns from this [DataRow].
 */
public fun <T> DataRow<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataRow<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataRow<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataColumn

/**
 * Splits list-like values in this [DataColumn] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row).
 *
 * Returns a new [DataColumn] with the exploded values.
 * The resulting column will have a new type (`List<T>` → `T`).
 *
 * For more information, see: {@include [DocumentationUrls.Explode]}
 *
 * @return A new [DataColumn] with exploded values.
 */
@JvmName("explodeList")
public fun <T> DataColumn<Collection<T>>.explode(): DataColumn<T> = explodeImpl() as DataColumn<T>

/**
 * Explodes a [DataColumn] of [DataFrame] values into a single [ColumnGroup].
 *
 * Each nested [DataFrame] is unwrapped, and its columns are placed side by side
 * within a column group named after the original column.
 * The number of resulting rows equals the total number of rows across all nested DataFrames.
 *
 * For more information, see: {@include [DocumentationUrls.Explode]}
 *
 * @return A [ColumnGroup] containing the concatenated contents of all nested DataFrames.
 */
@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().asColumnGroup(name())

// endregion
