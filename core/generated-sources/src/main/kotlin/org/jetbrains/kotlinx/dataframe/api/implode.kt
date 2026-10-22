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
 * Merges all values of every column of this [<code>DataFrame</code>][DataFrame] into a single [<code>DataRow</code>][DataRow].
 *
 * Each value of the resulting row is a [<code>List</code>][List] of all values in the corresponding column.
 * A [<code>ColumnGroup</code>][ColumnGroup] is converted into a [<code>DataFrame</code>][DataFrame] containing its rows.
 *
 * If [<code>dropNA</code>][dropNA] is `true`, `NA` values are removed from the merged lists.
 *
 * It is the same as `df.implode(dropNA) { all() }[0]`.
 *
 * Reverse operation: [<code>explode</code>][explode].
 *
 * For more information: [See `implode` on the documentation website.](https://kotlin.github.io/dataframe/implode.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // Merges all values of every column into a single row of lists
 * val row = df.implode()
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [dropNA] If `true`, removes `NA` values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   from the merged lists. `false` by default.
 * @return A [<code>DataRow</code>][DataRow] with all values of every column merged into lists.
 */
@Refine
@Interpretable("ImplodeDefault")
public fun <T> DataFrame<T>.implode(dropNA: Boolean = false): DataRow<T> = implode(dropNA) { all() }[0]

/**
 * Merges values in the specified [<code>columns</code>][columns] into lists, grouped by the values in all other columns —
 * that is, rows with equal values in the other columns are collapsed into a single row.
 *
 * The other columns are used as grouping keys and keep their types.
 *
 * If [<code>dropNA</code>][dropNA] is `true`, `NA` values are removed from the merged lists.
 *
 * Imploded columns will change their types:
 * - `T` to `List<T>`;
 * - [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] to [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] — an imploded [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is converted into a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * Reverse operation: [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `implode` on the documentation website.](https://kotlin.github.io/dataframe/implode.html)
 *
 * ### This `implode` overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // Merges the "city" and "year" values of each person into lists,
 * // one row per distinct "name"
 * df.implode { city and year }
 * ```
 *
 * ```kotlin
 * // Same as above, but `null` cities are removed from the lists
 * df.implode(dropNA = true) { city and year }
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [C] The common type of the selected columns.
 * @param [dropNA] If `true`, removes `NA` values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   from the merged lists. `false` by default.
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select columns to implode.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per distinct combination of values in the other columns,
 *   where the [<code>columns</code>][columns] contain merged lists of values.
 */
@Refine
@Interpretable("Implode")
public fun <T, C> DataFrame<T>.implode(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNA, columns)

/**
 * Merges values in the specified [<code>columns</code>][columns] into lists, grouped by the values in all other columns —
 * that is, rows with equal values in the other columns are collapsed into a single row.
 *
 * The other columns are used as grouping keys and keep their types.
 *
 * If [<code>dropNA</code>][dropNA] is `true`, `NA` values are removed from the merged lists.
 *
 * Imploded columns will change their types:
 * - `T` to `List<T>`;
 * - [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] to [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] — an imploded [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is converted into a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * Reverse operation: [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `implode` on the documentation website.](https://kotlin.github.io/dataframe/implode.html)
 *
 * ### This `implode` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples
 *
 * ```kotlin
 * // Merges the "city" and "year" values of each person into lists,
 * // one row per distinct "name"
 * df.implode("city", "year")
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [columns] The [<code>column names</code>][String] used to select columns to implode.
 * @param [dropNA] If `true`, removes `NA` values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   from the merged lists. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per distinct combination of values in the other columns,
 *   where the [<code>columns</code>][columns] contain merged lists of values.
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
