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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
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
 * and merged into a single value: by default, a [List] of the collected values.
 *
 * The selected columns are removed from the [DataFrame] and replaced by a single column
 * with the merged values; all other columns remain unchanged.
 *
 * This function does not perform the merge immediately — it returns a [Merge] object,
 * which serves as an intermediate step.
 * Before the result is produced, you may:
 * - exclude `null` and empty row values ([notNull][Merge.notNull]);
 * - specify how the collected values are merged ([by][Merge.by], [asStrings][Merge.asStrings]).
 *
 * Finally, produce the result using one of the following methods:
 * - [into][Merge.into] — stores the merged values in a new column and returns the updated [DataFrame];
 * - [intoList][Merge.intoList] — returns the merged values as a [List], one per row; the [DataFrame] itself is not changed: the selected columns stay in place.
 *
 * This operation is the reverse of [split].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 */
internal interface MergeDocs {

    /**
     * ## [merge][merge] Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * [**`merge`**][merge]**`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`notNull`**][Merge.notNull]**`() `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`by`**][Merge.by]**`(...) `**`| `__`.`__[**`by`**][Merge.by]**`  {  `**`transform: ... `**`} `**
     * `| `__`.`__[**`asStrings`**][Merge.asStrings]**`() `**`]`
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][Merge.into]**`(`**`columnName: `[`String`][String]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`into`**][Merge.into]**`(`**`path: `[`ColumnPath`][ColumnPath]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`intoList`**][Merge.intoList]**`() `**
     */
    typealias Grammar = Nothing
}

/** {@set [SelectingColumns.OPERATION] [merge][merge]} */
@ExcludeFromSources
private typealias SetMergeOperationArg = Nothing

/**
 * {@include [MergeDocs]}
 * ### This Merge Overload
 */
@ExcludeFromSources
private typealias CommonMergeDocs = Nothing

/**
 * @include [CommonMergeDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetMergeOperationArg]}
 * ### Examples
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.merge_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeDefault]
 * @param [selector] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to merge.
 */
@Interpretable("Merge0")
public fun <T, C> DataFrame<T>.merge(selector: ColumnsSelector<T, C>): Merge<T, C, List<C>> =
    Merge(this, selector, false, { it }, typeOf<Any?>(), Infer.Type)

/**
 * @include [CommonMergeDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetMergeOperationArg]}
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeColumnNames]
 * @param [columns] The [column names][String] used to select the columns of this [DataFrame] to merge.
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
 * An intermediate class used in the [merge] operation.
 *
 * This class itself does not merge anything — it stores the columns selected by [merge]
 * and serves as a transitional step before the merged values are computed.
 * It must be followed by one of the final methods to produce the result:
 * - [into] – stores the merged values in a new column and returns the updated [DataFrame];
 * - [intoList] – returns the merged values as a [List], one per row; the [DataFrame] itself is not changed: the selected columns stay in place.
 *
 * You can also configure the merge using:
 * - [notNull] – to exclude `null` and empty row values;
 * - [by] – to specify how the collected values are merged;
 * - [asStrings] – to merge the values into a [String].
 *
 * This operation is the reverse of [split].
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar][MergeDocs.Grammar] for more details.
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
 * An intermediate class used in the [merge] operation, produced by [by][Merge.by] or [asStrings][Merge.asStrings].
 *
 * It stores the transform applied to the collected values and must be followed
 * by one of the final methods to produce the result:
 * - [into] – stores the merged values in a new column and returns the updated [DataFrame];
 * - [intoList] – returns the merged values as a [List], one per row; the [DataFrame] itself is not changed: the selected columns stay in place.
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar][MergeDocs.Grammar] for more details.
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
 * A row value collected from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * is excluded as well when all of its values are `null`.
 *
 * It's an intermediate step; returns a new [Merge] that skips `null` values.
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeNotNull]
 * @return A new [Merge] instance with `null` values excluded from the merged values.
 */
@ExcludeFromSources
private typealias MergeNotNullSnippet = Nothing

/**
 * @include [MergeNotNullSnippet]
 */
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, R>.notNull(): Merge<T, C & Any, R> = copy(notNull = true) as Merge<T, C & Any, R>

/**
 * @include [MergeNotNullSnippet]
 */
@JvmName("notNullList")
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, List<R>>.notNull(): Merge<T, C & Any, List<R & Any>> =
    copy(notNull = true) as Merge<T, C & Any, List<R & Any>>

/**
 * Merges the selected columns into a single column named [columnName]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If a column named [columnName] already exists, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 */
@ExcludeFromSources
private typealias MergeIntoNameSnippet = Nothing

/**
 * @include [MergeIntoNameSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.merge_properties]
 * @param [columnName] The name of the column to store the merged values in.
 * @return A new [DataFrame] with the merged column.
 */
@Refine
@Interpretable("MergeInto0")
public fun <T, C, R> MergeWithTransform<T, C, R>.into(columnName: String): DataFrame<T> = into(pathOf(columnName))

/**
 * @include [MergeIntoNameSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeDefault]
 * @param [columnName] The name of the column to store the merged values in.
 * @return A new [DataFrame] with the merged column.
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
 * Merges the selected columns and returns the merged values as a [List] —
 * one merged value per row, in the row order of the [DataFrame].
 * The [DataFrame] itself is not changed: the selected columns stay in place.
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 */
@ExcludeFromSources
private typealias MergeIntoListSnippet = Nothing

/**
 * @include [MergeIntoListSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeIntoList]
 * @return The merged values, one per row.
 */
public fun <T, C, R> Merge<T, C, R>.intoList(): List<R> =
    df.select(selector).rows().map { row ->
        val values = (row.values() as List<C>).filterNotNullAndEmptyRows(notNull)
        transform(row, values)
    }

/**
 * @include [MergeIntoListSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeIntoList]
 * @return The merged values, one per row.
 */
public fun <T, C, R> MergeWithTransform<T, C, R>.intoList(): List<R> =
    df.select(selector).rows().map { row ->
        val values = (row.values() as List<C>).filterNotNullAndEmptyRows(notNull)
        transform(row, values)
    }

/**
 * Merges the selected columns into a single column at the given [path][ColumnPath]:
 * the selected columns are removed, and the merged values are stored in a new column.
 * If the parent [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] of the path do not exist, they are created.
 * If a column already exists at that path, it is removed — including all its nested columns —
 * and replaced by the new column. All other columns remain unchanged.
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 */
@ExcludeFromSources
private typealias MergeIntoPathSnippet = Nothing

/**
 * The `notNull` filter shared by [into][MergeWithTransform.into] and [intoList][MergeWithTransform.intoList]:
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
 * @include [MergeIntoPathSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeIntoPath]
 * @param [path] The [path][ColumnPath] of the column to store the merged values in.
 * @return A new [DataFrame] with the merged column.
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
 * @include [MergeIntoPathSnippet]
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeIntoPath]
 * @param [path] The [path][ColumnPath] of the column to store the merged values in.
 * @return A new [DataFrame] with the merged column.
 */
public fun <T, C, R> Merge<T, C, R>.into(path: ColumnPath): DataFrame<T> =
    MergeWithTransform(df, selector, notNull, transform, resultType, infer).into(path)

/**
 * Merges the values into a single [String]: they are joined with `", "`,
 * which is the same as [by][Merge.by] with the default separator.
 *
 * This is an intermediate step; returns a new [MergeWithTransform] that joins the values into a [String].
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeAsStrings]
 * @return A new [MergeWithTransform] instance with the values joined into a [String].
 */
@Interpretable("MergeId")
public fun <T, C, R> Merge<T, C, R>.asStrings(): MergeWithTransform<T, C, String> = by(", ")

/**
 * Specifies that the collected values should be merged into a single [String]:
 * they are joined with [separator], optionally surrounded by [prefix] and [postfix],
 * and, if there are more than [limit] of them, only the first [limit] values are included,
 * followed by [truncated]. A `null` value is rendered as `null`.
 *
 * This is an intermediate step; returns a new [MergeWithTransform] that joins the values into a [String].
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.merge_properties]
 *
 * @param [separator] The string used to separate the values. `", "` by default.
 * @param [prefix] The string prepended to the merged [String]. Empty by default.
 * @param [postfix] The string appended to the merged [String]. Empty by default.
 * @param [limit] The maximum number of values to include in the merged [String];
 *   all values are included by default (`-1`).
 * @param [truncated] The string appended to the merged [String] if the values were truncated. `"..."` by default.
 * @return A new [MergeWithTransform] instance with the values joined into a [String].
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
 * Specifies a custom [transform] for the values collected by [merge].
 *
 * The [transform] receives the collected values as a [List] — in the order the columns were selected —
 * and the source row as the receiver. When the selected columns have different value types,
 * the values have their common supertype, so the [transform] may need to cast them.
 *
 * This is an intermediate step; returns a new [MergeWithTransform] with the specified [transform].
 *
 * For more information: {@include [DocumentationUrls.Merge]}
 *
 * See [Grammar].
 *
 * ### Examples
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeSameWith]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples.mergeDifferentWith]
 *
 * @param [transform] The transformation applied to the collected values to produce the merged value.
 * @param [infer] The [type inference][Infer] of the merged column.
 * @return A new [MergeWithTransform] instance with the specified [transform].
 */
@Interpretable("MergeBy1")
public inline fun <T, C, R, reified V> Merge<T, C, R>.by(
    infer: Infer = Infer.Nulls,
    crossinline transform: DataRow<T>.(R) -> V,
): MergeWithTransform<T, C, V> =
    MergeWithTransform(df, selector, notNull, {
        transform(this@by.transform(this, it))
    }, typeOf<V>(), infer)
