package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Flatten Operation
 *
 * Removes  column groups in the [<code>DataFrame</code>][DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * @param [columns]
 * The names of the columns or selector determining which column groups should be flattened.
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the  column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 */
private interface FlattenDocs {
    typealias FLATTEN_PARAM = Nothing
    typealias GROUPS = Nothing

    /**
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
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
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>flatten</code>][flatten]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>flatten</code>][flatten]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>flatten</code>][flatten]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>flatten</code>][flatten]`("length", "age")`
     *
     *
     *
     */
    typealias FlattenSelectingOptions = Nothing
}

/**
 * ## The Flatten Operation
 *
 * Removes all column groups in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 *
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the all column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 *
 *
 */
@Refine
@Interpretable("FlattenDefault")
public fun <T> DataFrame<T>.flatten(keepParentNameForColumns: Boolean = false, separator: String = "_"): DataFrame<T> =
    flatten(keepParentNameForColumns, separator) { all() }

/**
 * ## The Flatten Operation
 *
 * Removes specified column groups in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * @param [columns]
 * The names of the columns or selector determining which column groups should be flattened.
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the specified column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 *
 */
@Refine
@Interpretable("Flatten0")
public fun <T, C> DataFrame<T>.flatten(
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = flattenImpl(columns, keepParentNameForColumns, separator)

/**
 * ## The Flatten Operation
 *
 * Removes selected column groups in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * @param [columns]
 * The names of the columns or selector determining which column groups should be flattened.
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 *
 */
@Refine
@StringApiInterpretable(interpreter = "Flatten0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.flatten(
    vararg columns: String,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

/**
 * ## The Flatten Operation
 *
 * Removes selected column groups in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * @param [columns]
 * The names of the columns or selector determining which column groups should be flattened.
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 *
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.flatten(
    vararg columns: ColumnReference<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

/**
 * ## The Flatten Operation
 *
 * Removes selected column groups in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], replacing them with their leaf columns.
 *
 * __NOTE:__ Columns after flattening will keep their original names.
 * Potential column name clashes are resolved by adding the minimal possible name prefix from ancestor columns.
 *
 * The columns to flatten need to be selected.
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FlattenDocs.FlattenSelectingOptions] for all the selecting options
 *
 * @param [keepParentNameForColumns] If true, retains the parent column name as a prefix for the flattened columns.
 * The prefix is separated from the original column names using the provided separator.
 * Defaults to `false`.
 * @param [separator] The string used to separate parent column names and the original column names when `keepParentNameForColumns` is `true`.
 * Defaults to `"_"`.
 * @param [columns]
 * The names of the columns or selector determining which column groups should be flattened.
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected column groups flattened.
 *
 * @see <a href="https://kotlin.github.io/dataframe/flatten.html">See `flatten` on the documentation website.</a>
 *
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.flatten(
    vararg columns: KProperty<C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> = flatten(keepParentNameForColumns, separator) { columns.toColumnSet() }

// endregion
