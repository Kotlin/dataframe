package org.jetbrains.kotlinx.dataframe.api

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.splitDefault
import org.jetbrains.kotlinx.dataframe.impl.api.splitImpl
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.SPLIT_STR
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region split

/**
 * Splits the values of the selected [<code>columns</code>][columns] into several parts and stores these parts
 * in new columns, in new rows, or as [<code>List</code>][List]s in the original columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * This function does not split anything immediately — it returns a [<code>Split</code>][Split] instance,
 * which serves as an intermediate step. No work is done until one of the terminal functions is called.
 *
 * Optionally, define how each value is split into parts:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] — splits each value by [<code>Char</code>][Char] or [<code>String</code>][String] delimiters, by a [<code>Regex</code>][Regex],
 *   or with a custom splitter function.
 * - [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match] — splits each [<code>String</code>][String] value into the capturing groups of a [<code>Regex</code>][Regex].
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.Split.default] — sets the value for the parts that are missing in some rows.
 *
 * Columns of [<code>Iterable</code>][Iterable] (e.g., [<code>List</code>][List]), [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] (a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]), and [<code>Pair</code>][Pair] values can be split without [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by]:
 * into their elements, rows (so into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]), and components, respectively.
 *
 * Finally, choose where the parts are stored:
 * - [<code>into</code>][Split.into] — stores the parts in new columns, which replace the original column.
 * - [<code>inward</code>][Split.inward] — stores the parts in new columns nested inside
 *   a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], which replaces the original column and keeps its name.
 * - [<code>intoRows</code>][Split.intoRows] — places each part in a separate row,
 *   duplicating the values in the other columns.
 * - [<code>inplace</code>][Split.inplace] — stores the parts as a [<code>List</code>][List] in the original column.
 * - [<code>intoColumns</code>][Split.intoColumns] — turns a column of [<code>DataFrame</code>][DataFrame]s into a [<code>column group</code>][ColumnGroup],
 *   where each cell holds a [<code>List</code>][List] of the values of the corresponding [<code>DataFrame</code>][DataFrame] column.
 *
 * Each of these methods returns a new [<code>DataFrame</code>][DataFrame].
 *
 * Reverse operations: [<code>merge</code>][merge] for [<code>into</code>][SplitWithTransform.into] and [<code>inward</code>][SplitWithTransform.inward],
 * [<code>implode</code>][implode] for [<code>intoRows</code>][SplitWithTransform.intoRows].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 */
internal interface SplitDocs {

    /**
     * ## [<code>split</code>][split] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`split`**</code>][split]**`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`cast`**</code>][Split.cast]**`<`**`C`**`>() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`by`**</code>][Split.by]**`(`**`delimiters: `[<code>`Char`</code>][Char]` | `[<code>`String`</code>][String]**`, ..`**`[`**`, `**`trim, ignoreCase, limit]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`by`**</code>][Split.by]**`(`**`regex: `[<code>`Regex`</code>][Regex]`[`**`, `**`trim, limit]`**`) `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`by`**</code>][Split.by]**`  {  `**`splitter: `[<code>`DataRow`</code>][DataRow]`<T>.(C) -> `[<code>`Iterable`</code>][Iterable]`<R>`**` } `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`match`**</code>][Split.match]**`(`**`regex: `[<code>`String`</code>][String]` | `[<code>`Regex`</code>][Regex]**`) `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`default`**</code>][SplitWithTransform.default]**`(`**`value: R?`**`) `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][SplitWithTransform.into]**`(`**`names: `[<code>`String`</code>][String]**`, ..) `**`[ `**`{ `**`extraNamesGenerator: `[<code>`ColumnNamesGenerator`</code>][ColumnNamesGenerator]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`inward`**</code>][SplitWithTransform.inward]**`(`**`names: `[<code>`String`</code>][String]**`, ..) `**`[ `**`{ `**`extraNamesGenerator: `[<code>`ColumnNamesGenerator`</code>][ColumnNamesGenerator]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`intoColumns`**</code>][Split.intoColumns]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`intoRows`**</code>][SplitWithTransform.intoRows]**`(`**`[dropEmpty: `[<code>`Boolean`</code>][Boolean]`]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`inplace`**</code>][SplitWithTransform.inplace]**`()`**
     *
     * For [<code>Iterable</code>][Iterable], [<code>DataFrame</code>][DataFrame], and [<code>Pair</code>][Pair] values, the [<code>by</code>][Split.by] or [<code>match</code>][Split.match] step can be omitted.
     * [<code>Pair</code>][Pair] values can be split directly with [<code>into</code>][Split.into] or [<code>inward</code>][Split.inward], each of which takes
     * exactly two names. [<code>intoColumns</code>][Split.intoColumns] is available only for [<code>DataFrame</code>][DataFrame] values.
     */
    typealias Grammar = Nothing
}

/**
 * Splits the values of the selected [<code>columns</code>][columns] into several parts and stores these parts
 * in new columns, in new rows, or as [<code>List</code>][List]s in the original columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * This function does not split anything immediately — it returns a [<code>Split</code>][org.jetbrains.kotlinx.dataframe.api.Split] instance,
 * which serves as an intermediate step. No work is done until one of the terminal functions is called.
 *
 * Optionally, define how each value is split into parts:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] — splits each value by [<code>Char</code>][Char] or [<code>String</code>][String] delimiters, by a [<code>Regex</code>][Regex],
 *   or with a custom splitter function.
 * - [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match] — splits each [<code>String</code>][String] value into the capturing groups of a [<code>Regex</code>][Regex].
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.Split.default] — sets the value for the parts that are missing in some rows.
 *
 * Columns of [<code>Iterable</code>][Iterable] (e.g., [<code>List</code>][List]), [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] (a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]), and [<code>Pair</code>][Pair] values can be split without [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by]:
 * into their elements, rows (so into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]), and components, respectively.
 *
 * Finally, choose where the parts are stored:
 * - [<code>into</code>][Split.into] — stores the parts in new columns, which replace the original column.
 * - [<code>inward</code>][Split.inward] — stores the parts in new columns nested inside
 *   a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], which replaces the original column and keeps its name.
 * - [<code>intoRows</code>][Split.intoRows] — places each part in a separate row,
 *   duplicating the values in the other columns.
 * - [<code>inplace</code>][Split.inplace] — stores the parts as a [<code>List</code>][List] in the original column.
 * - [<code>intoColumns</code>][org.jetbrains.kotlinx.dataframe.api.Split.intoColumns] — turns a column of [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 *   where each cell holds a [<code>List</code>][List] of the values of the corresponding [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] column.
 *
 * Each of these methods returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Reverse operations: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge] for [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into] and [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>implode</code>][org.jetbrains.kotlinx.dataframe.api.implode] for [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 * ### This Split Overload
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
 *
 * ```kotlin
 * df.split { name.lastName }.by { it.asIterable() }.into("char1", "char2")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to split.
 * @return A [<code>Split</code>][Split] builder to configure and complete the splitting.
 */
@Interpretable("Split0")
public fun <T, C> DataFrame<T>.split(columns: ColumnsSelector<T, C?>): Split<T, C> = Split(this, columns)

/**
 * Splits the values of the selected [<code>columns</code>][columns] into several parts and stores these parts
 * in new columns, in new rows, or as [<code>List</code>][List]s in the original columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * This function does not split anything immediately — it returns a [<code>Split</code>][org.jetbrains.kotlinx.dataframe.api.Split] instance,
 * which serves as an intermediate step. No work is done until one of the terminal functions is called.
 *
 * Optionally, define how each value is split into parts:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] — splits each value by [<code>Char</code>][Char] or [<code>String</code>][String] delimiters, by a [<code>Regex</code>][Regex],
 *   or with a custom splitter function.
 * - [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match] — splits each [<code>String</code>][String] value into the capturing groups of a [<code>Regex</code>][Regex].
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.Split.default] — sets the value for the parts that are missing in some rows.
 *
 * Columns of [<code>Iterable</code>][Iterable] (e.g., [<code>List</code>][List]), [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] (a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]), and [<code>Pair</code>][Pair] values can be split without [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by]:
 * into their elements, rows (so into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]), and components, respectively.
 *
 * Finally, choose where the parts are stored:
 * - [<code>into</code>][Split.into] — stores the parts in new columns, which replace the original column.
 * - [<code>inward</code>][Split.inward] — stores the parts in new columns nested inside
 *   a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], which replaces the original column and keeps its name.
 * - [<code>intoRows</code>][Split.intoRows] — places each part in a separate row,
 *   duplicating the values in the other columns.
 * - [<code>inplace</code>][Split.inplace] — stores the parts as a [<code>List</code>][List] in the original column.
 * - [<code>intoColumns</code>][org.jetbrains.kotlinx.dataframe.api.Split.intoColumns] — turns a column of [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 *   where each cell holds a [<code>List</code>][List] of the values of the corresponding [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] column.
 *
 * Each of these methods returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Reverse operations: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge] for [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into] and [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>implode</code>][org.jetbrains.kotlinx.dataframe.api.implode] for [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 * ### This Split Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * As the type of the selected columns is unknown, the values are treated as [<code>Any</code>][Any].
 * Use [<code>cast</code>][Split.cast] to specify their type for the following steps.
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each full name by the space into two new columns
 * df.split("name").by(" ").into("firstName", "lastName")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to split.
 * @return A [<code>Split</code>][Split] builder to configure and complete the splitting.
 */
@StringApiInterpretable(interpreter = "Split0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.split(vararg columns: String): Split<T, Any> = split { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.split(vararg columns: ColumnReference<C?>): Split<T, C> = split { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.split(vararg columns: KProperty<C?>): Split<T, C> = split { columns.toColumnSet() }

// endregion

/**
 * An intermediate class used in the [<code>split</code>][split] operation.
 *
 * This class itself does not split anything — it only holds the [<code>DataFrame</code>][DataFrame] and the columns selected by [<code>split</code>][split].
 * The values are split only when one of the terminal methods is called.
 *
 * Use the following methods to define how each value is split into parts:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] — splits each value by [<code>Char</code>][Char] or [<code>String</code>][String] delimiters, by a [<code>Regex</code>][Regex],
 *   or with a custom splitter function.
 * - [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match] — splits each [<code>String</code>][String] value into the capturing groups of a [<code>Regex</code>][Regex].
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.Split.default] — sets the value for the parts that are missing in some rows.
 *
 * Columns of [<code>Iterable</code>][Iterable] (e.g., [<code>List</code>][List]), [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] (a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]), and [<code>Pair</code>][Pair] values can be split without [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by]:
 * into their elements, rows (so into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]), and components, respectively.
 *
 * Use the following methods to complete the splitting:
 * - [<code>into</code>][Split.into] — stores the parts in new columns, which replace the original column.
 * - [<code>inward</code>][Split.inward] — stores the parts in new columns nested inside
 *   a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], which replaces the original column and keeps its name.
 * - [<code>intoRows</code>][Split.intoRows] — places each part in a separate row,
 *   duplicating the values in the other columns.
 * - [<code>inplace</code>][Split.inplace] — stores the parts as a [<code>List</code>][List] in the original column.
 * - [<code>intoColumns</code>][Split.intoColumns] — turns a column of [<code>DataFrame</code>][DataFrame]s into a [<code>column group</code>][ColumnGroup],
 *   where each cell holds a [<code>List</code>][List] of the values of the corresponding [<code>DataFrame</code>][DataFrame] column.
 *
 * Each of these methods returns a new [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 */
public class Split<T, C>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val columns: ColumnsSelector<T, C?>,
) {
    /**
     * Changes the type of the values in the columns selected by [<code>split</code>][split] to [<code>P</code>][P] for the following steps,
     * without checking or converting the values themselves.
     *
     * This is useful when the type of the values is unknown at compile time,
     * for example, when the columns are selected by their [<code>String</code>][String] names,
     * so that [<code>by</code>][Split.by] or a terminal method can treat the values as [<code>P</code>][P].
     *
     * This is an intermediate step of the [<code>split</code>][split] operation; it returns this [<code>Split</code>][Split] with the updated value type.
     *
     * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
     *
     * See [<code>Grammar</code>][SplitDocs.Grammar].
     *
     * ### Examples
     *
     * ```kotlin
     * // `split(String)` doesn't know the column type: `cast` lets `by` treat the values as `String`
     * df.split("name").cast<String>().by { it.split(" ").reversed() }.into("lastName", "firstName")
     * ```
     *
     * @param [P] The new type of the values in the selected columns.
     * @return This [<code>Split</code>][Split] with the values treated as [<code>P</code>][P].
     */
    public fun <P> cast(): Split<T, P> = this as Split<T, P>

    override fun toString(): String = "Split(df=$df, columns=$columns)"
}

/**
 * An intermediate class used in the [<code>split</code>][split] operation that holds the complete splitting configuration:
 * the columns selected by [<code>split</code>][split], the function that splits each value into parts,
 * and the [<code>default</code>][SplitWithTransform.default] value for the missing parts.
 *
 * It's returned by [<code>by</code>][Split.by], [<code>match</code>][Split.match], and [<code>default</code>][Split.default].
 * This class itself does not split anything — the values are split only when one of the terminal methods is called.
 *
 * Use [<code>default</code>][SplitWithTransform.default] to set the value for the parts that are missing in some rows.
 *
 * Use the following methods to complete the splitting:
 * - [<code>into</code>][SplitWithTransform.into] — stores the parts in new columns, which replace the original column.
 * - [<code>inward</code>][SplitWithTransform.inward] — stores the parts in new columns nested inside
 *   a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], which replaces the original column and keeps its name.
 * - [<code>intoRows</code>][SplitWithTransform.intoRows] — places each part in a separate row,
 *   duplicating the values in the other columns.
 * - [<code>inplace</code>][SplitWithTransform.inplace] — stores the parts as a [<code>List</code>][List] in the original column.
 *
 * Each of these methods returns a new [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 */
public class SplitWithTransform<T, C, R>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
    internal val inward: Boolean,
    internal val tartypeOf: KType,
    internal val default: R? = null,
    internal val transform: DataRow<T>.(C) -> Iterable<R>,
) {
    override fun toString(): String =
        "SplitWithTransform(df=$df, columns=$columns, inward=$inward, tartypeOf=$tartypeOf, default=$default, transform=$transform)"
}

/**
 * A function that produces the names of the extra columns created by [<code>split</code>][split]
 * when the values are split into more parts than there are column names given to
 * [<code>into</code>][SplitWithTransform.into] or [<code>inward</code>][SplitWithTransform.inward].
 *
 * It's called on the original column (as a [<code>ColumnWithPath</code>][ColumnWithPath]) with the index of each extra column
 * as the argument, starting from `1`, and returns the name for this column.
 * For example, `{ "part" + it }` names the extra columns `part1`, `part2`, and so on.
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * @param [C] The type of the values in the original column.
 */
public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

// region default

/**
 * Sets the [<code>value</code>][value] for the parts that are missing in some rows
 * when the [<code>Iterable</code>][Iterable] values of the columns selected by [<code>split</code>][split] are split into their elements.
 *
 * The values are split into as many new columns as the longest value has elements;
 * shorter values, as well as `null` values, get [<code>value</code>][value] in the remaining positions instead of `null`.
 * The default value only affects [<code>into</code>][SplitWithTransform.into] and [<code>inward</code>][SplitWithTransform.inward];
 * it is ignored by [<code>intoRows</code>][SplitWithTransform.intoRows] and [<code>inplace</code>][SplitWithTransform.inplace].
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split the lists of phones into two columns; missing phones are replaced with "none"
 * df.split { phones }.default("none").into("primaryPhone", "secondaryPhone")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>Iterable</code>][Iterable] type of the values in the columns selected by [<code>split</code>][split].
 * @param [R] The type of the elements of the [<code>Iterable</code>][Iterable] values.
 * @param [value] The value for the missing parts.
 * @return A [<code>SplitWithTransform</code>][SplitWithTransform] that splits the values into their elements and uses [<code>value</code>][value] for missing parts.
 */
@Interpretable("SplitDefault")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.default(value: R?): SplitWithTransform<T, C, R> =
    by { it }.default(value)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").default(value)"""))
public fun <T> Split<T, String>.default(value: String?): SplitWithTransform<T, String, String> =
    by { it.splitDefault() }.default(value)

/**
 * Sets the [<code>value</code>][value] for the parts that are missing in some rows.
 *
 * The values are split into as many new columns as the value with the most parts has;
 * values with fewer parts, as well as `null` values, get [<code>value</code>][value] in the remaining columns instead of `null`.
 * The default value only affects [<code>into</code>][SplitWithTransform.into] and [<code>inward</code>][SplitWithTransform.inward];
 * it is ignored by [<code>intoRows</code>][SplitWithTransform.intoRows] and [<code>inplace</code>][SplitWithTransform.inplace].
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each address by commas; missing parts are replaced with "unknown"
 * df.split { address }.by(",").default("unknown").into("city", "street", "house")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [value] The value for the missing parts.
 * @return A new [<code>SplitWithTransform</code>][SplitWithTransform] with the same configuration and the given [<code>value</code>][value] for missing parts.
 */
@Interpretable("SplitWithTransformDefault")
public fun <T, C, R> SplitWithTransform<T, C, R>.default(value: R?): SplitWithTransform<T, C, R> =
    SplitWithTransform(df, columns, inward, tartypeOf, default = value, transform)

// endregion

// region by

/**
 * Defines how each value of the columns selected by [<code>split</code>][split] is split into parts, using the [<code>splitter</code>][splitter] function.
 *
 * The [<code>splitter</code>][splitter] is called for each non-`null` value with this value as the argument
 * and the [<code>DataRow</code>][DataRow] containing it as the receiver, and returns the parts as an [<code>Iterable</code>][Iterable].
 * `null` values are not passed to the [<code>splitter</code>][splitter]; they are split into no parts.
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each full name with a custom splitter function
 * df.split { name }.by { it.split(" ").reversed() }.into("lastName", "firstName")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [splitter] The function that splits a value into parts.
 * @return A [<code>SplitWithTransform</code>][SplitWithTransform] that splits the values using the [<code>splitter</code>][splitter].
 */
@Interpretable("ByIterable")
public inline fun <T, C, reified R> Split<T, C>.by(
    noinline splitter: DataRow<T>.(C) -> Iterable<R>,
): SplitWithTransform<T, C, R> = by(typeOf<R>(), splitter)

/**
 * Defines how each value of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] is split into parts:
 * the [<code>String</code>][String] representation of each value is split by the given [<code>Char</code>][Char] [delimiters].
 * The parts are trimmed if [trim] is `true` (default), and their number is limited by [limit] if it's positive.
 * `null` values are split into no parts.
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each address by commas; parts are trimmed by default.
 * // Rows with fewer parts get `null` in the missing columns
 * df.split { address }.by(',').into("city", "street", "house")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [delimiters] One or more [<code>Char</code>][Char] delimiters to split the values by.
 * @param [trim] Whether to remove leading and trailing whitespace from each part. `true` by default.
 * @param [ignoreCase] Whether to ignore the character case when matching the [delimiters].
 *   `false` by default.
 * @param [limit] The maximum number of parts; the last part contains the rest of the value.
 *   `0` (no limit) by default.
 * @return A [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform] that splits the values by the [delimiters].
 */
@Interpretable("ByCharDelimiters")
public fun <T, C> Split<T, C>.by(
    vararg delimiters: Char,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

/**
 * Defines how each value of the columns selected by [<code>split</code>][split] is split into parts:
 * the [<code>String</code>][String] representation of each non-`null` value is split around matches of [<code>regex</code>][regex].
 * The regex matches are not included in the resulting parts.
 * The parts are trimmed if [<code>trim</code>][trim] is `true` (default), and their number is limited by [<code>limit</code>][limit] if it's positive.
 * `null` values are split into no parts.
 *
 * To split a [<code>String</code>][String] into its regex capturing groups instead, use [<code>match</code>][Split.match].
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each address around commas and any surrounding whitespace
 * df.split { address }.by("""s*,s*""".toRegex()).into("city", "street", "house")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [regex] The regular expression to split the values around.
 * @param [trim] Whether to remove leading and trailing whitespace from each part. `true` by default.
 * @param [limit] The maximum number of parts; the last part contains the rest of the value.
 *   `0` (no limit) by default.
 * @return A [<code>SplitWithTransform</code>][SplitWithTransform] that splits the values around matches of [<code>regex</code>][regex].
 */
@Interpretable("ByRegex")
public fun <T, C> Split<T, C>.by(
    regex: Regex,
    trim: Boolean = true,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(regex, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

/**
 * Defines how each value of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] is split into parts:
 * the [<code>String</code>][String] representation of each value is split by the given [<code>String</code>][String] [delimiters].
 * The parts are trimmed if [trim] is `true` (default), and their number is limited by [limit] if it's positive.
 * `null` values are split into no parts.
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each full name by the space into two new columns
 * df.split { name }.by(" ").into("firstName", "lastName")
 * ```
 *
 * ```kotlin
 * // Split each address by commas into at most two parts, without trimming them
 * df.split { address }.by(", ", limit = 2, trim = false).into("city", "rest")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [delimiters] One or more [<code>String</code>][String] delimiters to split the values by.
 * @param [trim] Whether to remove leading and trailing whitespace from each part. `true` by default.
 * @param [ignoreCase] Whether to ignore the character case when matching the [delimiters].
 *   `false` by default.
 * @param [limit] The maximum number of parts; the last part contains the rest of the value.
 *   `0` (no limit) by default.
 * @return A [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform] that splits the values by the [delimiters].
 */
@Interpretable("ByStringDelimiters")
public fun <T, C> Split<T, C>.by(
    vararg delimiters: String,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

@PublishedApi
internal inline fun <T, C, R> Split<T, C>.by(
    type: KType,
    crossinline splitter: DataRow<T>.(C) -> Iterable<R>,
): SplitWithTransform<T, C, R> =
    SplitWithTransform(df, columns, false, type) {
        if (it == null) emptyList() else splitter(it).asList()
    }

// endregion

// region match

/**
 * Defines how each [<code>String</code>][String] value is split into its [<code>Regex</code>][Regex] capturing groups.
 * The whole string must match [regex], as matching uses [<code>Regex.matchEntire</code>][Regex.matchEntire].
 * The entire match (group 0) is omitted; groups that do not participate in the match become `null`.
 * Strings that do not match, and `null` values, are split into no parts.
 * To split around regex matches instead, use [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by].
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * merged.split { "name"<String>() }
 *     .match("""(.*) ((.*))""")
 *     .inward("firstName", "lastName")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [regex] The regular expression pattern to compile with [<code>String.toRegex</code>][String.toRegex].
 * @return A [<code>SplitWithTransform</code>][SplitWithTransform] that splits the values into the capturing groups of [<code>regex</code>][regex].
 */
@Interpretable("MatchStringRegex")
public fun <T, C : String?> Split<T, C>.match(
    @Language("RegExp") regex: String,
): SplitWithTransform<T, C, String?> = match(regex.toRegex())

/**
 * Defines how each [<code>String</code>][String] value is split into its [<code>Regex</code>][Regex] capturing groups.
 * The whole string must match [regex], as matching uses [<code>Regex.matchEntire</code>][Regex.matchEntire].
 * The entire match (group 0) is omitted; groups that do not participate in the match become `null`.
 * Strings that do not match, and `null` values, are split into no parts.
 * To split around regex matches instead, use [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by].
 *
 * This is an intermediate step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a [<code>SplitWithTransform</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform],
 * which can be completed with [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward],
 * [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], or [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * merged.split { "name"<String>() }
 *     .match("""(.*) ((.*))""")
 *     .inward("firstName", "lastName")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [regex] The regular expression whose capturing groups define the parts.
 * @return A [<code>SplitWithTransform</code>][SplitWithTransform] that splits the values into the capturing groups of [<code>regex</code>][regex].
 */
@Interpretable("MatchRegex")
public fun <T, C : String?> Split<T, C>.match(regex: Regex): SplitWithTransform<T, C, String?> =
    by {
        it?.let {
            regex.matchEntire(it)
                ?.groups
                ?.drop(1)
                ?.map { it?.value }
        } ?: emptyList<String>()
    }

// endregion

internal fun <T, C> Split<T, C>.toDataFrame(): DataFrame<T> =
    by {
        when (it) {
            is List<*> -> it
            is AnyFrame -> it.rows()
            else -> listOf(it)
        }
    }.into()

// region into

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>,
): DataFrame<T> = into(listOf(firstName.name()) + otherNames.map { it.name() })

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>,
): DataFrame<T> = into(listOf(firstName.columnName) + otherNames.map { it.columnName })

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * which replace each original column at its position and nesting level.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward], which nests the new columns inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * df.split { name.lastName }.by { it.asIterable() }.into("char1", "char2")
 * ```
 *
 * ```kotlin
 * // Only one name is given: the other two new columns are named "split1" and "split2"
 * df.split { address }.by(",").into("city")
 * ```
 *
 * ```kotlin
 * // Only one name is given: the other two new columns are named
 * // by `extraNamesGenerator`, which receives the index of each extra column, starting from 1
 * df.split { address }.by(",").into("city") { "addressPart" + it }
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by the new columns.
 */
@Refine
@Interpretable("SplitWithTransformInto0")
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * which replace each original column at its position and nesting level.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward], which nests the new columns inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // Only one name is given: the other two new columns are named
 * // by `extraNamesGenerator`, which receives the index of each extra column, starting from 1
 * df.split { address }.by(",").into("city") { "addressPart" + it }
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by the new columns.
 */
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> =
    splitImpl(this) { numberOfNewCols ->
        if (extraNamesGenerator != null && names.size < numberOfNewCols) {
            names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(this, it) }
        } else {
            names
        }
    }

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * which replace each original column at its position and nesting level.
 *
 * Each [<code>Iterable</code>][Iterable] value (e.g., a [<code>List</code>][List]) is split into its elements,
 * so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward], which nests the new columns inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // `List` values are split into their elements, so no `by` is needed
 * df.split { phones }.into("primaryPhone", "secondaryPhone")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>Iterable</code>][Iterable] type of the values in the columns selected by [<code>split</code>][split].
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by the new columns.
 */
@Refine
@Interpretable("SplitIterableInto")
public fun <T, C : Iterable<*>> Split<T, C>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.into(names.toList(), extraNamesGenerator)

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * which replace each original column at its position and nesting level.
 *
 * Each [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] value is split into its rows, so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * As the parts are [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, the resulting columns are [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get `null` in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>inward</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inward], which nests the new columns inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // `DataFrame` values are split into their rows, so each new column is a column group
 * df.split { orders }.into("firstOrder", "secondOrder")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The schema marker type of the [<code>DataFrame</code>][DataFrame] values in the columns selected by [<code>split</code>][split].
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by the new column groups.
 */
@JvmName("splitDataFrameInto")
public fun <T, C> Split<T, DataFrame<C>>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<DataFrame<C>>? = null,
): DataFrame<T> = by { it.rows() }.into(names.toList(), extraNamesGenerator)

/**
 * Splits the [<code>Pair</code>][Pair] values of the columns selected by [<code>split</code>][split] into two new columns,
 * which replace each original column at its position and nesting level:
 * [<code>firstCol</code>][firstCol] with the [<code>first</code>][Pair.first] components and [<code>secondCol</code>][secondCol] with the [<code>second</code>][Pair.second] ones.
 *
 * No [<code>by</code>][Split.by] step is needed. `null` values get `null` in both new columns.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>inward</code>][Split.inward], which nests the new columns inside a [<code>column group</code>][ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [<code>merge</code>][merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // `Pair` values are split into their first and second components
 * df.split { location }.into("latitude", "longitude")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [A] The type of the first components of the [<code>Pair</code>][Pair] values.
 * @param [B] The type of the second components of the [<code>Pair</code>][Pair] values.
 * @param [firstCol] The name of the new column with the first components.
 * @param [secondCol] The name of the new column with the second components.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by the new columns.
 */
@Refine
@Interpretable("SplitPair")
public fun <T, A, B> Split<T, Pair<A, B>>.into(firstCol: String, secondCol: String): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.into(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>,
): DataFrame<T> = by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").into(*names, extraNamesGenerator = extraNamesGenerator)"""))
@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = by { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

// endregion

// region inward

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * nested inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that replaces each original column and keeps its name.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // Split each full name by the space into two columns nested inside the "name" column group
 * df.split { name }.by(" ").inward("firstName", "lastName")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups with the new columns.
 */
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    names: Iterable<String>,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> =
    SplitWithTransform(
        df,
        columns,
        inward = true,
        tartypeOf,
        default,
        transform,
    ).into(names.toList(), extraNamesGenerator)

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * nested inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that replaces each original column and keeps its name.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * df.split { name.lastName }
 *     .by { it.asIterable() }.default(' ')
 *     .inward { "char$it" }
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups with the new columns.
 */
@Refine
@Interpretable("SplitWithTransformInward0")
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>,
): DataFrame<T> = inward(listOf(firstName.name()) + otherNames.map { it.name() })

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>,
): DataFrame<T> = inward(listOf(firstName.columnName) + otherNames.map { it.columnName })

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * nested inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that replaces each original column and keeps its name.
 *
 * Each [<code>Iterable</code>][Iterable] value (e.g., a [<code>List</code>][List]) is split into its elements,
 * so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get the [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] value (`null` if not set) in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // `List` values are split into their elements, nested inside the "phones" column group
 * df.split { phones }.inward("primary", "secondary")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>Iterable</code>][Iterable] type of the values in the columns selected by [<code>split</code>][split].
 * @param [R] The type of the elements of the [<code>Iterable</code>][Iterable] values.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups with the new columns.
 */
@Refine
@Interpretable("SplitIterableInward")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.inward(names.toList(), extraNamesGenerator)

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them in new columns,
 * nested inside a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that replaces each original column and keeps its name.
 *
 * Each [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] value is split into its rows, so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * As the parts are [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, the resulting columns are [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * How many new columns are created depends on the maximum number of parts among the values of the original column,
 * or the number of the given [names].
 * Values that are split into fewer parts than the number of new columns get `null` in the remaining columns.
 * `null` values cannot be split, so they become `null` in all new columns.
 *
 * The new columns are named as follows:
 * - the first new columns get the given [names], in order;
 * - if there are more new columns than [names], the names of the extra columns are produced by
 *   [extraNamesGenerator], which is called on the original column with the index of each extra column,
 *   starting from `1`;
 * - if [extraNamesGenerator] is `null`, the extra columns are named `split1`, `split2`, and so on.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [<code>merge</code>][org.jetbrains.kotlinx.dataframe.api.merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // `DataFrame` values are split into their rows, nested inside the "orders" column group
 * df.split { orders }.inward("first", "second")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>DataFrame</code>][DataFrame] type of the values in the columns selected by [<code>split</code>][split].
 * @param [R] The schema marker type of the [<code>DataFrame</code>][DataFrame] values.
 * @param [names] The names of the new columns, in the order of the parts.
 * @param [extraNamesGenerator] An optional [<code>ColumnNamesGenerator</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNamesGenerator] that produces names for the new columns
 *   not covered by [names]. It's called on the original column with the index of each extra column,
 *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups with the new column groups.
 */
@JvmName("splitDataFrameInward")
public fun <T, C : DataFrame<R>, R> Split<T, C>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it.rows() }.inward(names.toList(), extraNamesGenerator)

/**
 * Splits the [<code>Pair</code>][Pair] values of the columns selected by [<code>split</code>][split] into two new columns,
 * nested inside a [<code>column group</code>][ColumnGroup] that replaces each original column and keeps its name:
 * [<code>firstCol</code>][firstCol] with the [<code>first</code>][Pair.first] components and [<code>secondCol</code>][secondCol] with the [<code>second</code>][Pair.second] ones.
 *
 * No [<code>by</code>][Split.by] step is needed. `null` values get `null` in both new columns.
 *
 * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>into</code>][Split.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [<code>merge</code>][merge].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * // `Pair` values are split into their components, nested inside the "location" column group
 * df.split { location }.inward("latitude", "longitude")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [A] The type of the first components of the [<code>Pair</code>][Pair] values.
 * @param [B] The type of the second components of the [<code>Pair</code>][Pair] values.
 * @param [firstCol] The name of the new column with the first components.
 * @param [secondCol] The name of the new column with the second components.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups with the new columns.
 */
@Refine
@Interpretable("SplitPairInward")
public fun <T, A, B> Split<T, Pair<A, B>>.inward(firstCol: String, secondCol: String): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.inward(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>,
): DataFrame<T> = by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").inward(*names, extraNamesGenerator = extraNamesGenerator)"""))
@JvmName("inwardTC")
public fun <T> Split<T, String>.inward(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = by { it.splitDefault() }.inward(names.toList(), extraNamesGenerator)

// endregion

// region intoColumns

/**
 * Splits the [<code>DataFrame</code>][DataFrame] values of the columns selected by [<code>split</code>][split] into columns:
 * each selected column becomes a [<code>column group</code>][ColumnGroup] with the columns of its [<code>DataFrame</code>][DataFrame] values,
 * where each cell holds a [<code>List</code>][List] of the values of the corresponding [<code>DataFrame</code>][DataFrame] column.
 *
 * In other words, each [<code>DataFrame</code>][DataFrame] value is [<code>imploded</code>][implode] into a single row.
 * Empty [<code>DataFrame</code>][DataFrame] values result in empty [<code>List</code>][List]s.
 *
 * Don't confuse this with [<code>into</code>][Split.into], which stores each row of the [<code>DataFrame</code>][DataFrame] values in a separate new column.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * val df1 = dataFrameOf("a", "b", "c")(
 *     1, 2, 3,
 *     4, 5, 6,
 * )
 * val df2 = dataFrameOf("a", "b")(
 *     5, 6,
 *     7, 8,
 *     9, 10,
 * )
 * val df = dataFrameOf(
 *     "id" to columnOf("x", "y"),
 *     "group" to columnOf(df1, df2),
 * )
 *
 * df.split { "group"<AnyFrame>() }.intoColumns()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>DataFrame</code>][DataFrame] type of the values in the columns selected by [<code>split</code>][split].
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced by column groups.
 */
@Refine
@Interpretable("SplitAnyFrameIntoColumns")
public fun <T, C : DataFrame<*>> Split<T, C>.intoColumns(): DataFrame<T> =
    df.convert(columns).with {
        when {
            it == null -> null
            it.isEmpty() -> DataRow.empty
            else -> it.implode { all() }.single()
        }
    }

// endregion

// region intoRows

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and places each part in a separate row,
 * duplicating the values in the other columns.
 *
 * Each [<code>Iterable</code>][Iterable] value (e.g., a [<code>List</code>][List]) is split into its elements,
 * so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * The type of each split column changes from the type of its values to the type of their parts.
 * When several columns are split, their parts are aligned in the resulting rows, like in [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * Values that are split into no parts, like `null`s, are handled according to [dropEmpty].
 *
 * It's equivalent to [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace] followed by [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Reverse operation: [<code>implode</code>][org.jetbrains.kotlinx.dataframe.api.implode].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // Place each phone in a separate row.
 * // Rows with an empty list of phones are removed, as `dropEmpty` is `true` by default
 * df.split { phones }.intoRows()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>Iterable</code>][Iterable] type of the values in the columns selected by [<code>split</code>][split].
 * @param [R] The type of the elements of the [<code>Iterable</code>][Iterable] values.
 * @param [dropEmpty] If `true`, removes rows with empty [<code>List</code>][List]s or [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 *                  If `false`, such rows will be exploded into `null` values.
 * @return A new [<code>DataFrame</code>][DataFrame] with a separate row for each element.
 */
@JvmName("intoRowsTC")
@Refine
@Interpretable("SplitIntoRows")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it }
        .intoRows(dropEmpty)

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and places each part in a separate row,
 * duplicating the values in the other columns.
 *
 * Each [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] value is split into its rows, so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * As the parts are [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, the resulting columns are [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * The type of each split column changes from the type of its values to the type of their parts.
 * When several columns are split, their parts are aligned in the resulting rows, like in [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * Values that are split into no parts, like `null`s, are handled according to [dropEmpty].
 *
 * It's equivalent to [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace] followed by [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Reverse operation: [<code>implode</code>][org.jetbrains.kotlinx.dataframe.api.implode].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // Place each order in a separate row; "orders" becomes a column group.
 * // Keep the customers without orders, with `null` values in "orders"
 * df.split { orders }.intoRows(dropEmpty = false)
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>DataFrame</code>][DataFrame] type of the values in the columns selected by [<code>split</code>][split].
 * @param [dropEmpty] If `true`, removes rows with empty [<code>List</code>][List]s or [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 *                  If `false`, such rows will be exploded into `null` values.
 * @return A new [<code>DataFrame</code>][DataFrame] with a separate row for each row of the [<code>DataFrame</code>][DataFrame] values.
 */
@JvmName("intoRowsFrame")
@Refine
@Interpretable("SplitAnyFrameRows")
public fun <T, C : DataFrame<*>> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it.rows() }.intoRows(dropEmpty)

internal inline fun <T, C, R> Convert<T, C?>.splitInplace(
    type: KType,
    crossinline transform: DataRow<T>.(C) -> Iterable<R>,
) = withRowCellImpl(getListType(type), Infer.None) { if (it == null) emptyList() else transform(it).asList() }

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and places each part in a separate row,
 * duplicating the values in the other columns.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The type of each split column changes from the type of its values to the type of their parts.
 * When several columns are split, their parts are aligned in the resulting rows, like in [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * Values that are split into no parts, like `null`s, are handled according to [dropEmpty].
 *
 * It's equivalent to [<code>inplace</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.inplace] followed by [<code>explode</code>][org.jetbrains.kotlinx.dataframe.api.explode].
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Reverse operation: [<code>implode</code>][org.jetbrains.kotlinx.dataframe.api.implode].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * df.split { name.firstName }.by { it.asIterable() }.intoRows()
 *
 * df.split { name }.by { it.values() }.intoRows()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @param [dropEmpty] If `true`, removes rows with empty [<code>List</code>][List]s or [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s.
 *                  If `false`, such rows will be exploded into `null` values.
 * @return A new [<code>DataFrame</code>][DataFrame] with a separate row for each part.
 */
@Refine
@Interpretable("SplitWithTransformIntoRows")
public fun <T, C, R> SplitWithTransform<T, C, R>.intoRows(dropEmpty: Boolean = true): DataFrame<T> {
    val paths = df.getColumnPaths(columns).toColumnSet()
    return df.convert { paths as ColumnSet<C?> }.splitInplace(tartypeOf, transform).explode(dropEmpty) { paths }
}

// endregion

// region inplace

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them as a [<code>List</code>][List]
 * in the original columns, keeping their positions and names.
 *
 * Each [<code>Iterable</code>][Iterable] value (e.g., a [<code>List</code>][List]) is split into its elements,
 * so no [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by] step is needed.
 * The type of each split column becomes a [<code>List</code>][List] of the type of the parts; `null` values become empty [<code>List</code>][List]s.
 *
 * See also [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], which places each part in a separate row instead.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * df.split { tags }.inplace()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The [<code>Iterable</code>][Iterable] type of the values in the columns selected by [<code>split</code>][split].
 * @param [R] The type of the elements of the [<code>Iterable</code>][Iterable] values.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values of the selected columns converted to [<code>List</code>][List]s.
 */
@JvmName("inplaceTC")
@Refine
@Interpretable("SplitInplace")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = by { it }.inplace()

/**
 * Splits the values of the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] into parts and stores them as a [<code>List</code>][List]
 * in the original columns, keeping their positions and names.
 *
 * The values are split into parts as defined by the previous [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Split.by],
 * [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.Split.match], or [<code>default</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.default] step.
 * The type of each split column becomes a [<code>List</code>][List] of the type of the parts; `null` values become empty [<code>List</code>][List]s.
 *
 * See also [<code>intoRows</code>][org.jetbrains.kotlinx.dataframe.api.SplitWithTransform.intoRows], which places each part in a separate row instead.
 *
 * This is a terminal step of the [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split] operation; it returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SplitDocs.Grammar].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * df.split { name.firstName }.by { it.asIterable() }.inplace()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param [C] The type of the values in the columns selected by [<code>split</code>][org.jetbrains.kotlinx.dataframe.api.split].
 * @param [R] The type of the parts the values are split into.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values of the selected columns replaced by [<code>List</code>][List]s of their parts.
 */
@Refine
@Interpretable("SplitWithTransformInplace")
public fun <T, C, R> SplitWithTransform<T, C, R>.inplace(): DataFrame<T> =
    df.convert(columns).splitInplace(tartypeOf, transform)

// endregion

// region DataColumn

/**
 * Splits the [<code>Iterable</code>][Iterable] values of this [<code>DataColumn</code>][DataColumn] into their elements
 * and returns them as a new [<code>DataFrame</code>][DataFrame]: the `i`-th element of each value is stored in the `i`-th column.
 *
 * The number of columns is the maximum number of elements among the values,
 * or the number of the given [<code>names</code>][names] if it's larger.
 * Values with fewer elements, as well as `null` values, get `null` in the remaining columns.
 * The columns not covered by [<code>names</code>][names] are named `split1`, `split2`, and so on.
 *
 * It's a shortcut for converting this column to a [<code>DataFrame</code>][DataFrame] and calling [<code>split</code>][DataFrame.split]
 * and [<code>into</code>][Split.into] on it.
 *
 * For more information: [See `split` on the documentation website.](https://kotlin.github.io/dataframe/split.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // Split a column of lists into a new DataFrame with a column for each element position
 * df.phones.splitInto("primaryPhone", "secondaryPhone")
 * ```
 *
 * @param [names] The names of the new columns, in the order of the elements.
 * @return A new [<code>DataFrame</code>][DataFrame] that contains only the new columns.
 */
public fun DataColumn<Iterable<*>>.splitInto(vararg names: String): DataFrame<*> =
    toDataFrame().split { this@splitInto }.into(*names)

// endregion
