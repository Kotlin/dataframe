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
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

private val defaultExplodeColumns: ColumnsSelector<*, *> = {
    colsAtAnyDepth().filter { it.isList() || it.isFrameColumn() }
}

// region explode DataFrame

/**
 * Splits list-like values in the specified [columns] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row).
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, rows with empty lists or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s will be removed.
 * If `false`, such rows will be exploded into `null` values.
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` -> `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * This operation is the reverse of [implode][org.jetbrains.kotlinx.dataframe.api.implode].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
 *
 * ### This `explode` overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
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
 * @param columns The [ColumnsSelector] used to select columns to explode.
 *                If not specified, all applicable columns will be exploded.
 * @return A new [DataFrame] with exploded columns.
 */
@Refine
@Interpretable("Explode0")
public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    columns: ColumnsSelector<T, *> = defaultExplodeColumns,
): DataFrame<T> = explodeImpl(dropEmpty, columns)

/**
 * Splits list-like values in the specified [columns] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row).
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, rows with empty lists or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s will be removed.
 * If `false`, such rows will be exploded into `null` values.
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` -> `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * This operation is the reverse of [implode][org.jetbrains.kotlinx.dataframe.api.implode].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
 *
 * ### This `explode` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
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
 * Splits list-like values in the specified [columns] of this [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row)
 * and combine them into new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, the result will exclude rows with empty lists or DataFrames.
 * If `false`, such values will be exploded into `null`.
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] expanded into multiple rows based on the exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` → `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
 *
 * ### This `explode` overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
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
 * Splits list-like values in the specified [columns] of this [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] and spreads them vertically —
 * that is, it adds a separate row for each element (one value per row)
 * and combine them into new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * Values in all other columns are duplicated to preserve row context.
 *
 * If no [columns] are specified, all columns (at any depth) containing
 * [List] or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] values will be exploded.
 *
 * If [dropEmpty] is `true`, the result will exclude rows with empty lists or DataFrames.
 * If `false`, such values will be exploded into `null`.
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] expanded into multiple rows based on the exploded columns.
 *
 * Each exploded column will have a new type (`List<T>` → `T`).
 * When several columns are exploded in one operation, lists in different columns will be aligned.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
 *
 * ### This `explode` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
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
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
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
 * For more information, see: [See `explode` on the documentation website.](https://kotlin.github.io/dataframe/explode.html)
 *
 * @return A [ColumnGroup] containing the concatenated contents of all nested DataFrames.
 */
@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().asColumnGroup(name())

// endregion
