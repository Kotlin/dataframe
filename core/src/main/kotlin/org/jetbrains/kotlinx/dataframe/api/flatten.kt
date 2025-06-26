package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Flatten Operation
 *
 * Removes {@get [FlattenDocs.GROUPS]} column groups in the [DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [Selecting Columns][FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns\] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator\] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * {@get [FLATTEN_PARAM] @param [columns\]
 * The names of the columns or selector determining which column groups should be flattened.}
 *
 * @return A new [DataFrame] with the {@get [FlattenDocs.GROUPS]} column groups flattened.
 *
 * @see {@include [DocumentationUrls.Flatten]}
 */
@ExcludeFromSources
@Suppress("ClassName")
private interface FlattenDocs {
    interface FLATTEN_PARAM

    interface GROUPS

    /**
     * @include [SelectingColumns] {@set [SelectingColumns.OPERATION] [flatten][flatten]}
     */
    interface FlattenSelectingOptions
}

/**
 * {@include [FlattenDocs]}
 * {@set [FlattenDocs.GROUPS] all}
 * {@set [FlattenDocs.FLATTEN_PARAM]}
 */
@Refine
@Interpretable("FlattenDefault")
public fun <T> DataFrame<T>.flatten(keepParentNameForColumns: Boolean = false, separator: String = "_"): DataFrame<T> =
    flatten(keepParentNameForColumns, separator) { all() }

/**
 * {@include [FlattenDocs]}
 * {@set [FlattenDocs.GROUPS] specified}
 */
@Refine
@Interpretable("Flatten0")
public fun <T, C> DataFrame<T>.flatten(
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = flattenImpl(columns, keepParentNameForColumns, separator)

/**
 * {@include [FlattenDocs]}
 * {@set [FlattenDocs.GROUPS] selected}
 */
public fun <T> DataFrame<T>.flatten(
    vararg columns: String,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

/**
 * {@include [FlattenDocs]}
 * {@set [FlattenDocs.GROUPS] selected}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.flatten(
    vararg columns: ColumnReference<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

/**
 * {@include [FlattenDocs]}
 * {@set [FlattenDocs.GROUPS] selected}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.flatten(
    vararg columns: KProperty<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

// endregion
