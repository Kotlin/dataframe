package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Merges the values of several columns into a single value, per row, and stores the merged values in one column.
 *
 * The values of the selected columns are collected per row — in the order the columns were selected —
 * and merged into a single value: by default, a [<code>List</code>][List] of the collected values.
 *
 * The selected columns are removed from the [<code>DataFrame</code>][DataFrame] and replaced by a single column
 * with the merged values; all other columns remain unchanged.
 *
 * This function does not perform the merge immediately — it returns a [<code>Merge</code>][Merge] object,
 * which serves as an intermediate step.
 * Before the result is produced, you may:
 * - exclude `null` and empty row values ([<code>notNull</code>][Merge.notNull]);
 * - specify how the collected values are merged ([<code>by</code>][Merge.by], [<code>asStrings</code>][Merge.asStrings]).
 *
 * Finally, produce the result using one of the following methods:
 * - [<code>into</code>][Merge.into] — stores the merged values in a new column and returns the updated [<code>DataFrame</code>][DataFrame];
 * - [<code>intoList</code>][Merge.intoList] — returns the merged values as a [<code>List</code>][List] without changing the [<code>DataFrame</code>][DataFrame].
 *
 * This operation is the reverse of [<code>split</code>][split].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][Grammar].
 */
internal interface MergeDocs {

    /**
     * ## [<code>merge</code>][merge] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`merge`**</code>][merge]**`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][Merge.notNull]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`by`**</code>][Merge.by]**`(...) `**`| `__`.`__[<code>**`by`**</code>][Merge.by]**`  {  `**`transform: ... `**`} `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`asStrings`**</code>][Merge.asStrings]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][Merge.into]**`(`**`columnName: `[<code>`String`</code>][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`into`**</code>][Merge.into]**`(`**`path: `[<code>`ColumnPath`</code>][ColumnPath]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`intoList`**</code>][Merge.intoList]**`() `**
     */
    typealias Grammar = Nothing
}

/**
 * Merges the values of several columns into a single value, per row, and stores the merged values in one column.
 *
 * The values of the selected columns are collected per row — in the order the columns were selected —
 * and merged into a single value: by default, a [<code>List</code>][List] of the collected values.
 *
 * The selected columns are removed from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and replaced by a single column
 * with the merged values; all other columns remain unchanged.
 *
 * This function does not perform the merge immediately — it returns a [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] object,
 * which serves as an intermediate step.
 * Before the result is produced, you may:
 * - exclude `null` and empty row values ([<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Merge.notNull]);
 * - specify how the collected values are merged ([<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Merge.by], [<code>asStrings</code>][org.jetbrains.kotlinx.dataframe.api.Merge.asStrings]).
 *
 * Finally, produce the result using one of the following methods:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.Merge.into] — stores the merged values in a new column and returns the updated [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame];
 * - [<code>intoList</code>][org.jetbrains.kotlinx.dataframe.api.Merge.intoList] — returns the merged values as a [<code>List</code>][List] without changing the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### This Merge Overload
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
 * ### Examples
 * ```kotlin
 * // Merge two columns into one column "fullName"
 * peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
 * ```
 * ```kotlin
 * // By default, when no `by` or `asStrings` is specified, values will be merged into the List
 * peopleDf.merge { colsOf<Number>() }.into("data")
 * ```
 * @param [selector] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to merge.
 */
@Interpretable("Merge0")
public fun <T, C> DataFrame<T>.merge(selector: ColumnsSelector<T, C>): Merge<T, C, List<C>> =
    Merge(this, selector, false, { it }, typeOf<Any?>(), Infer.Type)

/**
 * Merges the values of several columns into a single value, per row, and stores the merged values in one column.
 *
 * The values of the selected columns are collected per row — in the order the columns were selected —
 * and merged into a single value: by default, a [<code>List</code>][List] of the collected values.
 *
 * The selected columns are removed from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and replaced by a single column
 * with the merged values; all other columns remain unchanged.
 *
 * This function does not perform the merge immediately — it returns a [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] object,
 * which serves as an intermediate step.
 * Before the result is produced, you may:
 * - exclude `null` and empty row values ([<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Merge.notNull]);
 * - specify how the collected values are merged ([<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Merge.by], [<code>asStrings</code>][org.jetbrains.kotlinx.dataframe.api.Merge.asStrings]).
 *
 * Finally, produce the result using one of the following methods:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.Merge.into] — stores the merged values in a new column and returns the updated [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame];
 * - [<code>intoList</code>][org.jetbrains.kotlinx.dataframe.api.Merge.intoList] — returns the merged values as a [<code>List</code>][List] without changing the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### This Merge Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Merge top-level columns by their names into one column "data" of lists
 * peopleDf.merge("age", "weight").into("data")
 * ```
 * @param [columns] The [<code>column names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to merge.
 */
@StringApiInterpretable(interpreter = "Merge0", stringArgument = "columns", targetArgument = "selector")
public fun <T> DataFrame<T>.merge(vararg columns: String): Merge<T, Any?, List<Any?>> = merge { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C> DataFrame<T>.merge(vararg columns: ColumnReference<C>): Merge<T, C, List<C>> =
    merge { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C> DataFrame<T>.merge(vararg columns: KProperty<C>): Merge<T, C, List<C>> =
    merge { columns.toColumnSet() }

/**
 * An intermediate class used in the [<code>merge</code>][merge] operation.
 *
 * This class itself does not merge anything — it stores the columns selected by [<code>merge</code>][merge]
 * and serves as a transitional step before the merged values are computed.
 * It must be followed by one of the final methods to produce the result:
 * - [<code>into</code>][into] – stores the merged values in a new column and returns the updated [<code>DataFrame</code>][DataFrame];
 * - [<code>intoList</code>][intoList] – returns the merged values as a [<code>List</code>][List] without changing the [<code>DataFrame</code>][DataFrame].
 *
 * You can also configure the merge using:
 * - [<code>notNull</code>][notNull] – to exclude `null` and empty row values;
 * - [<code>by</code>][by] – to specify how the collected values are merged;
 * - [<code>asStrings</code>][asStrings] – to merge the values into a [<code>String</code>][String].
 *
 * This operation is the reverse of [<code>split</code>][split].
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][MergeDocs.Grammar] for more details.
 */
public data class Merge<T, C, R>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val selector: ColumnsSelector<T, C>,
    @PublishedApi
    internal val notNull: Boolean,
    @PublishedApi
    internal val transform: DataRow<T>.(List<C>) -> R,
    @PublishedApi
    internal val resultType: KType,
    @PublishedApi
    internal val infer: Infer,
)

/**
 * An intermediate class used in the [<code>merge</code>][merge] operation, produced by [<code>by</code>][Merge.by] or [<code>asStrings</code>][Merge.asStrings].
 *
 * It stores the transform applied to the collected values and must be followed
 * by one of the final methods to produce the result:
 * - [<code>into</code>][into] – stores the merged values in a new column and returns the updated [<code>DataFrame</code>][DataFrame];
 * - [<code>intoList</code>][intoList] – returns the merged values as a [<code>List</code>][List] without changing the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][MergeDocs.Grammar] for more details.
 */
public class MergeWithTransform<T, C, R>(
    internal val df: DataFrame<T>,
    internal val selector: ColumnsSelector<T, C>,
    internal val notNull: Boolean,
    internal val transform: DataRow<T>.(List<C>) -> R,
    internal val resultType: KType,
    internal val infer: Infer,
)

/**
 * Excludes `null` values from the merged values, so that only values that are not `null` are merged.
 *
 * A row value collected from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * is excluded as well when all of its values are `null`.
 *
 * It's an intermediate step; returns a new [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] that skips `null` values.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 *
 * ### Example
 * ```kotlin
 * // Merge two columns into one column "info" excluding null values
 * peopleDf.merge { name.firstName and weight }.notNull().by(" ").into("info")
 * ```
 * @return A new [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] instance with `null` values excluded from the merged values.
 */
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, R>.notNull(): Merge<T, C & Any, R> = copy(notNull = true) as Merge<T, C & Any, R>

/**
 * Excludes `null` values from the merged values, so that only values that are not `null` are merged.
 *
 * A row value collected from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * is excluded as well when all of its values are `null`.
 *
 * It's an intermediate step; returns a new [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] that skips `null` values.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 *
 * ### Example
 * ```kotlin
 * // Merge two columns into one column "info" excluding null values
 * peopleDf.merge { name.firstName and weight }.notNull().by(" ").into("info")
 * ```
 * @return A new [<code>Merge</code>][org.jetbrains.kotlinx.dataframe.api.Merge] instance with `null` values excluded from the merged values.
 */
@JvmName("notNullList")
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, List<R>>.notNull(): Merge<T, C & Any, List<R & Any>> =
    copy(notNull = true) as Merge<T, C & Any, List<R & Any>>

/**
 * Merges the selected columns into a single column named [<code>columnName</code>][columnName]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If a column named [<code>columnName</code>][columnName] already exists, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // Merge two columns into one column "fullName"
 * peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
 * ```
 * @param [columnName] The name of the column to store the merged values in.
 * @return A new [<code>DataFrame</code>][DataFrame] with the merged column.
 */
@Refine
@Interpretable("MergeInto0")
public fun <T, C, R> MergeWithTransform<T, C, R>.into(columnName: String): DataFrame<T> = into(pathOf(columnName))

/**
 * Merges the selected columns into a single column named [<code>columnName</code>][columnName]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If a column named [<code>columnName</code>][columnName] already exists, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // By default, when no `by` or `asStrings` is specified, values will be merged into the List
 * peopleDf.merge { colsOf<Number>() }.into("data")
 * ```
 * @param [columnName] The name of the column to store the merged values in.
 * @return A new [<code>DataFrame</code>][DataFrame] with the merged column.
 */
@Refine
@Interpretable("MergeInto0")
public fun <T, C, R> Merge<T, C, R>.into(columnName: String): DataFrame<T> = into(pathOf(columnName))

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, C, reified R> Merge<T, C, R>.into(column: ColumnAccessor<*>): DataFrame<T> = into(column.path())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, C, reified R> MergeWithTransform<T, C, R>.into(column: ColumnAccessor<*>): DataFrame<T> =
    into(column.path())

/**
 * Merges the selected columns and returns the merged values as a [<code>List</code>][List] —
 * one merged value per row, in the row order of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * The [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] itself is not changed: the selected columns stay in place.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // Merge data from two columns into List<String>
 * peopleDf.merge { name.firstName and name.lastName }.by(",").intoList()
 * ```
 * @return The merged values, one per row.
 */
public fun <T, C, R> Merge<T, C, R>.intoList(): List<R> =
    df.select(selector).rows().map { row ->
        val values = (row.values() as List<C>).filterNotNullAndEmptyRows(notNull)
        transform(row, values)
    }

/**
 * Merges the selected columns and returns the merged values as a [<code>List</code>][List] —
 * one merged value per row, in the row order of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * The [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] itself is not changed: the selected columns stay in place.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // Merge data from two columns into List<String>
 * peopleDf.merge { name.firstName and name.lastName }.by(",").intoList()
 * ```
 * @return The merged values, one per row.
 */
public fun <T, C, R> MergeWithTransform<T, C, R>.intoList(): List<R> =
    df.select(selector).rows().map { row ->
        val values = (row.values() as List<C>).filterNotNullAndEmptyRows(notNull)
        transform(row, values)
    }

/**
 * The `notNull` filter shared by [<code>into</code>][MergeWithTransform.into] and [<code>intoList</code>][MergeWithTransform.intoList]:
 * drops `null` values, and a row value when its row is entirely `null`.
 *
 * `DataRow.isEmpty` is deprecated as a shortcut, but its prescribed replacement
 * `values().all { it == null }` is not equivalent for row values collected from column groups:
 * for such rows `values()` resolves a different row than `owner`-based `isEmpty` does.
 */
@Suppress("DEPRECATION")
private fun <C> List<C>.filterNotNullAndEmptyRows(notNull: Boolean): List<C> =
    if (notNull) {
        filter { it != null && (it !is AnyRow || !it.isEmpty()) }
    } else {
        this
    }

/**
 * Merges the selected columns into a single column at the given [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If the parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] of the path do not exist, they are created.
 * If a column already exists at that path, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // Merge the two name columns into a new nested column; the "person" column group is created
 * peopleDf.merge { name.firstName and name.lastName }.by(" ").into(pathOf("person", "fullName"))
 * ```
 * @param [path] The [<code>path</code>][ColumnPath] of the column to store the merged values in.
 * @return A new [<code>DataFrame</code>][DataFrame] with the merged column.
 */
@Suppress("DEPRECATION_ERROR")
public fun <T, C, R> MergeWithTransform<T, C, R>.into(path: ColumnPath): DataFrame<T> {
    // If target path exists, merge into temp path
    val mergePath = if (df.getColumnOrNull(path) != null) {
        pathOf(df.nameGenerator().addUnique("temp"))
    } else {
        path
    }

    // move columns into group
    val grouped = df.move(selector).under { mergePath }

    var res = grouped.convert { columnGroup(mergePath) }.withRowCellImpl(resultType, infer) {
        val srcRow = df[index()]
        val values = (it.values() as List<C>).filterNotNullAndEmptyRows(notNull)
        transform(srcRow, values)
    }
    if (mergePath != path) {
        // target path existed before merge, but
        // it may have already been removed
        res = res
            .removeImpl(allowMissingColumns = true) { path }
            .df
            .move { mergePath }.into { path }
    }
    return res
}

/**
 * Merges the selected columns into a single column at the given [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If the parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] of the path do not exist, they are created.
 * If a column already exists at that path, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.MergeDocs.Grammar].
 * ### Example
 * ```kotlin
 * // Merge the two name columns into a new nested column; the "person" column group is created
 * peopleDf.merge { name.firstName and name.lastName }.by(" ").into(pathOf("person", "fullName"))
 * ```
 * @param [path] The [<code>path</code>][ColumnPath] of the column to store the merged values in.
 * @return A new [<code>DataFrame</code>][DataFrame] with the merged column.
 */
public fun <T, C, R> Merge<T, C, R>.into(path: ColumnPath): DataFrame<T> =
    MergeWithTransform(df, selector, notNull, transform, resultType, infer).into(path)

/**
 * Merges the values into a single [<code>String</code>][String]: they are joined with `", "`,
 * which is the same as [<code>by</code>][Merge.by] with the default separator.
 *
 * This is an intermediate step; returns a new [<code>MergeWithTransform</code>][MergeWithTransform] that joins the values into a [<code>String</code>][String].
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Merge values into a single String
 * peopleDf.merge { name.firstName and name.lastName }.asStrings().into("fullName")
 * ```
 * @return A new [<code>MergeWithTransform</code>][MergeWithTransform] instance with the values joined into a [<code>String</code>][String].
 */
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, R>.asStrings(): MergeWithTransform<T, C, String> = by(", ")

/**
 * Specifies that the collected values should be merged into a single [<code>String</code>][String]:
 * they are joined with [<code>separator</code>][separator], optionally surrounded by [<code>prefix</code>][prefix] and [<code>postfix</code>][postfix],
 * and, if there are more than [<code>limit</code>][limit] of them, only the first [<code>limit</code>][limit] values are included,
 * followed by [<code>truncated</code>][truncated]. A `null` value is rendered as `null`.
 *
 * This is an intermediate step; returns a new [<code>MergeWithTransform</code>][MergeWithTransform] that joins the values into a [<code>String</code>][String].
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Merge two columns into one column "fullName"
 * peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
 * ```
 *
 * @param [separator] The string used to separate the values. `", "` by default.
 * @param [prefix] The string prepended to the merged [<code>String</code>][String]. Empty by default.
 * @param [postfix] The string appended to the merged [<code>String</code>][String]. Empty by default.
 * @param [limit] The maximum number of values to include in the merged [<code>String</code>][String];
 *   all values are included by default (`-1`).
 * @param [truncated] The string appended to the merged [<code>String</code>][String] if the values were truncated. `"..."` by default.
 * @return A new [<code>MergeWithTransform</code>][MergeWithTransform] instance with the values joined into a [<code>String</code>][String].
 */
@Interpretable("MergeBy0")
public fun <T, C, R> Merge<T, C, R>.by(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
): MergeWithTransform<T, C, String> =
    MergeWithTransform(
        df = df,
        selector = selector,
        notNull = notNull,
        transform = {
            it.joinToString(
                separator = separator,
                prefix = prefix,
                postfix = postfix,
                limit = limit,
                truncated = truncated,
            )
        },
        resultType = typeOf<String>(),
        infer = Infer.Nulls,
    )

/**
 * Specifies a custom [<code>transform</code>][transform] for the values collected by [<code>merge</code>][merge].
 *
 * The [<code>transform</code>][transform] receives the collected values as a [<code>List</code>][List] — in the order the columns were selected —
 * and the source row as the receiver. When the selected columns have different value types,
 * the values have their common supertype, so the [<code>transform</code>][transform] may need to cast them.
 *
 * This is an intermediate step; returns a new [<code>MergeWithTransform</code>][MergeWithTransform] with the specified [<code>transform</code>][transform].
 *
 * For more information: [See `merge` on the documentation website.](https://kotlin.github.io/dataframe/merge.html)
 *
 * See [<code>Grammar</code>][Grammar].
 *
 * ### Examples
 * ```kotlin
 * // "transform" accepts a List of collected values for every row typed by their common type
 * peopleDf.merge { name.firstName and name.lastName }
 *     .by { it.joinToString(" ") }
 *     .into("fullName")
 * ```
 * ```kotlin
 * // When heterogeneous columns are merged, they may need to be cast to valid types in "transform"
 * peopleDf.merge { name.firstName and age and isHappy }
 *     .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
 *     .into("status")
 * ```
 *
 * @param [transform] The transformation applied to the collected values to produce the merged value.
 * @param [infer] The [<code>type inference</code>][Infer] of the merged column.
 * @return A new [<code>MergeWithTransform</code>][MergeWithTransform] instance with the specified [<code>transform</code>][transform].
 */
@Interpretable("MergeBy1")
public inline fun <T, C, R, reified V> Merge<T, C, R>.by(
    infer: Infer = Infer.Nulls,
    crossinline transform: DataRow<T>.(R) -> V,
): MergeWithTransform<T, C, V> =
    MergeWithTransform(df, selector, notNull, {
        transform(this@by.transform(this, it))
    }, typeOf<V>(), infer)
