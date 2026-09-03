package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
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
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MINUS
import org.jetbrains.kotlinx.dataframe.util.MINUS_REPLACE
import kotlin.reflect.KProperty

// region DataFrame

// region remove

/**
 * ## The Remove Operation
 *
 * Removes the specified [<code>columns</code>][columns] from the original [<code>DataFrame</code>][DataFrame] and returns a new [<code>DataFrame</code>][DataFrame] without them.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][Select.SelectSelectingOptions].
 *
 * For more information: [See `remove` on the documentation website.](https://kotlin.github.io/dataframe/remove.html)
 */
internal typealias Remove = Nothing

/**
 * ## The Remove Operation
 *
 * Removes the specified [<code>columns</code>][org.jetbrains.kotlinx.dataframe.columns] from the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] without them.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `remove` on the documentation website.](https://kotlin.github.io/dataframe/remove.html)
 * ### This Remove Overload
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
 * <code>`df`</code>`.`[<code>remove</code>][org.jetbrains.kotlinx.dataframe.api.remove]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>remove</code>][org.jetbrains.kotlinx.dataframe.api.remove]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>remove</code>][org.jetbrains.kotlinx.dataframe.api.remove]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to remove the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Remove0")
public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> =
    removeImpl(allowMissingColumns = true, columns = columns).df

/**
 * ## The Remove Operation
 *
 * Removes the specified [<code>columns</code>][org.jetbrains.kotlinx.dataframe.columns] from the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] without them.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `remove` on the documentation website.](https://kotlin.github.io/dataframe/remove.html)
 * ### This Remove Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>remove</code>][org.jetbrains.kotlinx.dataframe.api.remove]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Column Names</code>][String] used to remove the columns of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumnSet() }

/**
 * ## The Remove Operation
 *
 * Removes the specified [<code>columns</code>][org.jetbrains.kotlinx.dataframe.columns] from the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] without them.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `remove` on the documentation website.](https://kotlin.github.io/dataframe/remove.html)
 * ### This Remove Overload
 *
 * @param [columns] The [<code>Column Accessors</code>][ColumnReference] used to remove the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.remove(vararg columns: AnyColumnReference): DataFrame<T> = remove { columns.toColumnSet() }

/**
 * ## The Remove Operation
 *
 * Removes the specified [<code>columns</code>][org.jetbrains.kotlinx.dataframe.columns] from the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] without them.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `remove` on the documentation website.](https://kotlin.github.io/dataframe/remove.html)
 * ### This Remove Overload
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] used to remove the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumnSet() }

// endregion

// region minus

@Deprecated(MINUS, ReplaceWith(MINUS_REPLACE), DeprecationLevel.ERROR)
public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)

@Deprecated(MINUS, ReplaceWith(MINUS_REPLACE), DeprecationLevel.ERROR)
public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)

@Deprecated(MINUS, ReplaceWith(MINUS_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public infix operator fun <T> DataFrame<T>.minus(column: AnyColumnReference): DataFrame<T> = remove(column)

@Deprecated(MINUS, ReplaceWith(MINUS_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public infix operator fun <T> DataFrame<T>.minus(columns: KProperty<*>): DataFrame<T> = remove(columns)

// endregion

// endregion
