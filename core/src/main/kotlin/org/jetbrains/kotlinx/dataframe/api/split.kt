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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
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
 * Splits the values of the selected [\columns] into several parts and stores these parts
 * in new columns, in new rows, or as [List]s in the original columns.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * This function does not split anything immediately — it returns a [Split] builder,
 * which serves as an intermediate step. No work is done until one of the terminal methods is called.
 *
 * Optionally, define how each value is split into parts:
 * @include [SplitDocs.SplittingMethodsSnippet]
 *
 * Finally, choose where the parts are stored:
 * @include [SplitDocs.TerminalMethodsSnippet]
 * - [intoColumns][Split.intoColumns] — turns a column of [DataFrame]s into a [column group][ColumnGroup],
 *   where each cell holds a [List] of the values of the corresponding [DataFrame] column.
 *
 * Each of these methods returns a new [DataFrame].
 *
 * Reverse operations: [merge] for [into][SplitWithTransform.into] and [inward][SplitWithTransform.inward],
 * [implode] for [intoRows][SplitWithTransform.intoRows].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 */
internal interface SplitDocs {

    /**
     * ## [split][split] Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * [**`split`**][split]**`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`cast`**][Split.cast]**`<`**`C`**`>() `**`\]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`by`**][Split.by]**`(`**`delimiters: `[`Char`][Char]` | `[`String`][String]**`, ..`**`\[`**`, `**`trim, ignoreCase, limit\]`**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`by`**][Split.by]**`(`**`regex: `[`Regex`][Regex]`\[`**`, `**`trim, limit\]`**`) `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`by`**][Split.by]**`  {  `**`splitter: `[`DataRow`][DataRow]`<T>.(C) -> `[`Iterable`][Iterable]`<R>`**` } `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`match`**][Split.match]**`(`**`regex: `[`String`][String]` | `[`Regex`][Regex]**`) `**`\]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`default`**][SplitWithTransform.default]**`(`**`value: R?`**`) `**`\]`
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][SplitWithTransform.into]**`(`**`names: `[`String`][String]**`, ..) `**`\[ `**`{ `**`extraNamesGenerator: `[`ColumnNamesGenerator`][ColumnNamesGenerator]**` } `**`\]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`inward`**][SplitWithTransform.inward]**`(`**`names: `[`String`][String]**`, ..) `**`\[ `**`{ `**`extraNamesGenerator: `[`ColumnNamesGenerator`][ColumnNamesGenerator]**` } `**`\]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`intoColumns`**][Split.intoColumns]**`()`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`intoRows`**][SplitWithTransform.intoRows]**`(`**`\[dropEmpty: `[`Boolean`][Boolean]`\]`**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`inplace`**][SplitWithTransform.inplace]**`()`**
     */
    typealias Grammar = Nothing

    /**
     * - [by][Split.by] — splits each value by [Char] or [String] delimiters, by a [Regex],
     *   or with a custom splitter function.
     * - [match][Split.match] — splits each [String] value into the capturing groups of a [Regex].
     * - [default][SplitWithTransform.default] — sets the value for the parts that are missing in some rows.
     *
     * Columns of [Iterable] (e.g., [List]), [DataFrame] (a [FrameColumn]), and [Pair] values can be split without [by][Split.by]:
     * into their elements, rows, and components, respectively.
     */
    @ExcludeFromSources
    typealias SplittingMethodsSnippet = Nothing

    /**
     * - [into][SplitWithTransform.into] — stores the parts in new columns, which replace the original column.
     * - [inward][SplitWithTransform.inward] — stores the parts in new columns nested inside
     *   a [column group][ColumnGroup], which replaces the original column and keeps its name.
     * - [intoRows][SplitWithTransform.intoRows] — places each part in a separate row,
     *   duplicating the values in the other columns.
     * - [inplace][SplitWithTransform.inplace] — stores the parts as a [List] in the original column.
     */
    @ExcludeFromSources
    typealias TerminalMethodsSnippet = Nothing

    /*
     * The key for a @set that describes how the values are split into parts in the terminal methods
     * of the `split` operation. Set it with one of the `Set...Parts` snippets below.
     */
    @ExcludeFromSources
    typealias PARTS = Nothing

    /**
     * {@set [PARTS] The values are split into parts as defined by the previous [by][Split.by],
     * [match][Split.match], or [default][SplitWithTransform.default] step.}
     */
    @ExcludeFromSources
    typealias SetTransformParts = Nothing

    /**
     * {@set [PARTS] Each [Iterable] value (e.g., a [List]) is split into its elements,
     * so no [by][Split.by] step is needed.}
     */
    @ExcludeFromSources
    typealias SetIterableParts = Nothing

    /**
     * {@set [PARTS] Each [DataFrame] value is split into its rows, so no [by][Split.by] step is needed.
     * As the parts are [DataRow]s, the resulting columns are [column groups][ColumnGroup].}
     */
    @ExcludeFromSources
    typealias SetDataFrameParts = Nothing

    /**
     * This is an intermediate step of the [split] operation; it returns a [SplitWithTransform],
     * which can be completed with [into][SplitWithTransform.into], [inward][SplitWithTransform.inward],
     * [intoRows][SplitWithTransform.intoRows], or [inplace][SplitWithTransform.inplace].
     *
     * For more information: {@include [DocumentationUrls.Split]}
     *
     * See [Grammar][SplitDocs.Grammar].
     */
    @ExcludeFromSources
    typealias SplittingStepSnippet = Nothing

    /**
     * This is a terminal step of the [split] operation; it returns a new [DataFrame].
     */
    @ExcludeFromSources
    typealias TerminalStepSnippet = Nothing

    /**
     * The number of new columns is the maximum number of parts among the values of the original column,
     * or the number of the given [names\] if it's larger.
     * Values that are split into fewer parts get the [default][SplitWithTransform.default] value
     * (`null` if not set) in the remaining columns; `null` values get it in all new columns.
     */
    @ExcludeFromSources
    typealias ColumnCountSnippet = Nothing

    /**
     * If a name is already taken at the place where a new column is inserted, a numeric suffix is added to it.
     */
    @ExcludeFromSources
    typealias NameConflictSnippet = Nothing

    /**
     * The new columns are named as follows:
     * - the first new columns get the given [names\], in order;
     * - if there are more new columns than [names\], the names of the extra columns are produced by
     *   [extraNamesGenerator\], which is called on the original column with the index of each extra column,
     *   starting from `1`;
     * - if [extraNamesGenerator\] is `null`, the extra columns are named `split1`, `split2`, and so on.
     *
     * @include [NameConflictSnippet]
     */
    @ExcludeFromSources
    typealias ColumnNamesSnippet = Nothing

    /**
     * @param [names\] The names of the new columns, in the order of the parts.
     * @param [extraNamesGenerator\] An optional [ColumnNamesGenerator] that produces names for the new columns
     *   not covered by [names\]. It's called on the original column with the index of each extra column,
     *   starting from `1`. If `null`, such columns are named `split1`, `split2`, and so on.
     */
    @ExcludeFromSources
    typealias NamesParamsSnippet = Nothing

    /** @param [T\] The schema marker type of the [DataFrame]. */
    @ExcludeFromSources
    typealias TypeParamTSnippet = Nothing

    /** @param [C\] The type of the values in the columns selected by [split]. */
    @ExcludeFromSources
    typealias TypeParamCSnippet = Nothing

    /** @param [R\] The type of the parts the values are split into. */
    @ExcludeFromSources
    typealias TypeParamRSnippet = Nothing
}

/** {@set [SelectingColumns.OPERATION] [split][split]} */
@ExcludeFromSources
private typealias SetSplitOperationArg = Nothing

/**
 * {@include [SplitDocs]}
 * ### This Split Overload
 */
@ExcludeFromSources
private typealias CommonSplitDocs = Nothing

/**
 * @include [CommonSplitDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetSplitOperationArg]}
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.split_properties]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to split.
 * @return A [Split] builder to configure and complete the splitting.
 */
@Interpretable("Split0")
public fun <T, C> DataFrame<T>.split(columns: ColumnsSelector<T, C?>): Split<T, C> = Split(this, columns)

/**
 * @include [CommonSplitDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetSplitOperationArg]}
 *
 * As the type of the selected columns is unknown, the values are treated as [Any].
 * Use [cast][Split.cast] to specify their type for the following steps.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitColumnNames]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [columns] The [Column Names][String] used to select the columns of this [DataFrame] to split.
 * @return A [Split] builder to configure and complete the splitting.
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
 * An intermediate class used in the [split] operation.
 *
 * This class itself does not split anything — it only holds the [DataFrame] and the columns selected by [split].
 * The values are split only when one of the terminal methods is called.
 *
 * Use the following methods to define how each value is split into parts:
 * @include [SplitDocs.SplittingMethodsSnippet]
 *
 * Use the following methods to complete the splitting:
 * @include [SplitDocs.TerminalMethodsSnippet]
 * - [intoColumns][Split.intoColumns] — turns a column of [DataFrame]s into a [column group][ColumnGroup],
 *   where each cell holds a [List] of the values of the corresponding [DataFrame] column.
 *
 * Each of these methods returns a new [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 */
public class Split<T, C>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val columns: ColumnsSelector<T, C?>,
) {
    /**
     * Changes the type of the values in the columns selected by [split] to [P] for the following steps,
     * without checking or converting the values themselves.
     *
     * This is useful when the type of the values is unknown at compile time,
     * for example, when the columns are selected by their [String] names,
     * so that [by][Split.by] or a terminal method can treat the values as [P].
     *
     * This is an intermediate step of the [split] operation; it returns this [Split] with the updated value type.
     *
     * For more information: {@include [DocumentationUrls.Split]}
     *
     * See [Grammar][SplitDocs.Grammar].
     *
     * ### Examples
     *
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitCast]
     *
     * @param [P] The new type of the values in the selected columns.
     * @return This [Split] with the values treated as [P].
     */
    public fun <P> cast(): Split<T, P> = this as Split<T, P>

    override fun toString(): String = "Split(df=$df, columns=$columns)"
}

/**
 * An intermediate class used in the [split] operation that holds the complete splitting configuration:
 * the columns selected by [split], the function that splits each value into parts,
 * and the [default][SplitWithTransform.default] value for the missing parts.
 *
 * It's returned by [by][Split.by], [match][Split.match], and [default][Split.default].
 * This class itself does not split anything — the values are split only when one of the terminal methods is called.
 *
 * Use [default][SplitWithTransform.default] to set the value for the parts that are missing in some rows.
 *
 * Use the following methods to complete the splitting:
 * @include [SplitDocs.TerminalMethodsSnippet]
 *
 * Each of these methods returns a new [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
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
 * A function that produces the names of the extra columns created by [split]
 * when the values are split into more parts than there are column names given to
 * [into][SplitWithTransform.into] or [inward][SplitWithTransform.inward].
 *
 * It's called on the original column (as a [ColumnWithPath]) with the index of each extra column
 * as the argument, starting from `1`, and returns the name for this column.
 * For example, `{ "part" + it }` names the extra columns `part1`, `part2`, and so on.
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * @param [C] The type of the values in the original column.
 */
public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

// region default

/**
 * Sets the [value] for the parts that are missing in some rows
 * when the [Iterable] values of the columns selected by [split] are split into their elements.
 *
 * The values are split into as many new columns (or list elements) as the longest value has elements;
 * shorter values, as well as `null` values, get [value] in the remaining positions instead of `null`.
 *
 * @include [SplitDocs.SplittingStepSnippet]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitDefault]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [Iterable] type of the values in the columns selected by [split].
 * @param [R] The type of the elements of the [Iterable] values.
 * @param [value] The value for the missing parts.
 * @return A [SplitWithTransform] that splits the values into their elements and uses [value] for missing parts.
 */
@Interpretable("SplitDefault")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.default(value: R?): SplitWithTransform<T, C, R> =
    by { it }.default(value)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").default(value)"""))
public fun <T> Split<T, String>.default(value: String?): SplitWithTransform<T, String, String> =
    by { it.splitDefault() }.default(value)

/**
 * Sets the [value] for the parts that are missing in some rows.
 *
 * The values are split into as many new columns as the value with the most parts has;
 * values with fewer parts, as well as `null` values, get [value] in the remaining columns instead of `null`.
 *
 * This is an intermediate step of the [split] operation; it returns a new [SplitWithTransform],
 * which can be completed with [into][SplitWithTransform.into], [inward][SplitWithTransform.inward],
 * [intoRows][SplitWithTransform.intoRows], or [inplace][SplitWithTransform.inplace].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitWithTransformDefault]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @param [value] The value for the missing parts.
 * @return A new [SplitWithTransform] with the same configuration and the given [value] for missing parts.
 */
@Interpretable("SplitWithTransformDefault")
public fun <T, C, R> SplitWithTransform<T, C, R>.default(value: R?): SplitWithTransform<T, C, R> =
    SplitWithTransform(df, columns, inward, tartypeOf, default = value, transform)

// endregion

// region by

/**
 * Defines how each value of the columns selected by [split] is split into parts, using the [splitter] function.
 *
 * The [splitter] is called for each non-`null` value with this value as the argument
 * and the [DataRow] containing it as the receiver, and returns the parts as an [Iterable].
 * `null` values are not passed to the [splitter]; they are split into no parts.
 *
 * @include [SplitDocs.SplittingStepSnippet]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitByTransform]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @param [splitter] The function that splits a value into parts.
 * @return A [SplitWithTransform] that splits the values using the [splitter].
 */
@Interpretable("ByIterable")
public inline fun <T, C, reified R> Split<T, C>.by(
    noinline splitter: DataRow<T>.(C) -> Iterable<R>,
): SplitWithTransform<T, C, R> = by(typeOf<R>(), splitter)

/**
 * @include [SplitByDelimitersDocs] {@set [SplitByDelimitersDocs.TYPE] [Char]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitByCharDelimiters]
 *
 * @include [SplitByDelimitersDocs.ParamsSnippet]
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
 * Example:
 * ```
 * dataFrameOf("str" to listOf("1    2 3     4"))
 *   .split("str").by("\\s+".toRegex())
 *   // when the list of explicitly specified columnNames is not long enough (or none at all),
 *   // names for additional columns are generates
 *   .into()
 * ```
 * Result:
 * ```
 *    split1 split2 split3 split4
 *         1      2      3      4
 * ```
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
 * @include [SplitByDelimitersDocs] {@set [SplitByDelimitersDocs.TYPE] [String]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitByDelimiter]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitByStringDelimiters]
 *
 * @include [SplitByDelimitersDocs.ParamsSnippet]
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

/**
 * Defines how each value of the columns selected by [split] is split into parts:
 * the [String] representation of each value is split by the given {@get [SplitByDelimitersDocs.TYPE]} [delimiters\].
 * The parts are trimmed if [trim\] is `true` (default), and their number is limited by [limit\] if it's positive.
 * `null` values are split into no parts.
 *
 * @include [SplitDocs.SplittingStepSnippet]
 */
@ExcludeFromSources
private interface SplitByDelimitersDocs {

    /*
     * The key for a @set that defines the type of the delimiters, [Char] or [String].
     */
    @ExcludeFromSources
    typealias TYPE = Nothing

    /**
     * @include [SplitDocs.TypeParamTSnippet]
     * @include [SplitDocs.TypeParamCSnippet]
     * @param [delimiters\] One or more {@get [TYPE]} delimiters to split the values by.
     * @param [trim\] Whether to remove leading and trailing whitespace from each part. `true` by default.
     * @param [ignoreCase\] Whether to ignore the character case when matching the [delimiters\].
     *   `false` by default.
     * @param [limit\] The maximum number of parts; the last part contains the rest of the value.
     *   `0` (no limit) by default.
     * @return A [SplitWithTransform] that splits the values by the [delimiters\].
     */
    typealias ParamsSnippet = Nothing
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
 * Creates new String columns according to MatchResult [capturing groups](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-match-result/group-values.html),
 * excluding the first group which is the entire matched String.
 * Created columns will be nullable if [regex] doesn't match some rows or there are nulls in the original column
 * Check [Split.by] overload with regex parameter if you're looking to split String value by [Regex] delimiter
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitRegex1]
 *
 * For more information: {@include [DocumentationUrls.Split]}
 */
@Interpretable("MatchStringRegex")
public fun <T, C : String?> Split<T, C>.match(
    @Language("RegExp") regex: String,
): SplitWithTransform<T, C, String?> = match(regex.toRegex())

/**
 * @include [match]
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

/**
 * Splits the values of the columns selected by [split] into parts and stores them in new columns,
 * which replace each original column at its position and nesting level.
 *
 * {@get [SplitDocs.PARTS]}
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * @include [SplitDocs.ColumnCountSnippet]
 *
 * @include [SplitDocs.ColumnNamesSnippet]
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * See also [inward][SplitWithTransform.inward], which nests the new columns inside a [column group][ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [merge].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 */
@ExcludeFromSources
private typealias CommonSplitIntoDocs = Nothing

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
 * @include [CommonSplitIntoDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.split_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIntoDefaultNames]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIntoExtraNames]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by the new columns.
 */
@Refine
@Interpretable("SplitWithTransformInto0")
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

/**
 * @include [CommonSplitIntoDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIntoExtraNames]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by the new columns.
 */
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> =
    splitImpl(this) { numberOfNewCols ->
        if (extraNamesGenerator != null && names.size < numberOfNewCols) {
            names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(this, it) }
        } else {
            names
        }
    }

/**
 * @include [CommonSplitIntoDocs] {@include [SplitDocs.SetIterableParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIterableInto]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [Iterable] type of the values in the columns selected by [split].
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by the new columns.
 */
@Refine
@Interpretable("SplitIterableInto")
public fun <T, C : Iterable<*>> Split<T, C>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.into(names.toList(), extraNamesGenerator)

/**
 * @include [CommonSplitIntoDocs] {@include [SplitDocs.SetDataFrameParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitFrameInto]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The schema marker type of the [DataFrame] values in the columns selected by [split].
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by the new column groups.
 */
@JvmName("splitDataFrameInto")
public fun <T, C> Split<T, DataFrame<C>>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<DataFrame<C>>? = null,
): DataFrame<T> = by { it.rows() }.into(names.toList(), extraNamesGenerator)

/**
 * Splits the [Pair] values of the columns selected by [split] into two new columns,
 * which replace each original column at its position and nesting level:
 * [firstCol] with the [first][Pair.first] components and [secondCol] with the [second][Pair.second] ones.
 *
 * No [by][Split.by] step is needed. `null` values get `null` in both new columns.
 *
 * @include [SplitDocs.NameConflictSnippet]
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * See also [inward][Split.inward], which nests the new columns inside a [column group][ColumnGroup]
 * named after the original column.
 *
 * Reverse operation: [merge].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitPairInto]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [A] The type of the first components of the [Pair] values.
 * @param [B] The type of the second components of the [Pair] values.
 * @param [firstCol] The name of the new column with the first components.
 * @param [secondCol] The name of the new column with the second components.
 * @return A new [DataFrame] with the selected columns replaced by the new columns.
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
 * Splits the values of the columns selected by [split] into parts and stores them in new columns,
 * nested inside a [column group][ColumnGroup] that replaces each original column and keeps its name.
 *
 * {@get [SplitDocs.PARTS]}
 * The `i`-th part of each value is stored in the `i`-th new column.
 *
 * @include [SplitDocs.ColumnCountSnippet]
 *
 * @include [SplitDocs.ColumnNamesSnippet]
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * See also [into][SplitWithTransform.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [merge].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 */
@ExcludeFromSources
private typealias CommonSplitInwardDocs = Nothing

/**
 * @include [CommonSplitInwardDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitInward]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by column groups with the new columns.
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
 * @include [CommonSplitInwardDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.split1_properties]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by column groups with the new columns.
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
 * @include [CommonSplitInwardDocs] {@include [SplitDocs.SetIterableParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIterableInward]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [Iterable] type of the values in the columns selected by [split].
 * @param [R] The type of the elements of the [Iterable] values.
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by column groups with the new columns.
 */
@Refine
@Interpretable("SplitIterableInward")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.inward(names.toList(), extraNamesGenerator)

/**
 * @include [CommonSplitInwardDocs] {@include [SplitDocs.SetDataFrameParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitFrameInward]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [DataFrame] type of the values in the columns selected by [split].
 * @param [R] The schema marker type of the [DataFrame] values.
 * @include [SplitDocs.NamesParamsSnippet]
 * @return A new [DataFrame] with the selected columns replaced by column groups with the new column groups.
 */
@JvmName("splitDataFrameInward")
public fun <T, C : DataFrame<R>, R> Split<T, C>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it.rows() }.inward(names.toList(), extraNamesGenerator)

/**
 * Splits the [Pair] values of the columns selected by [split] into two new columns,
 * nested inside a [column group][ColumnGroup] that replaces each original column and keeps its name:
 * [firstCol] with the [first][Pair.first] components and [secondCol] with the [second][Pair.second] ones.
 *
 * No [by][Split.by] step is needed. `null` values get `null` in both new columns.
 *
 * @include [SplitDocs.NameConflictSnippet]
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * See also [into][Split.into], which places the new columns at the same level as the original column.
 *
 * Reverse operation: [merge].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitPairInward]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [A] The type of the first components of the [Pair] values.
 * @param [B] The type of the second components of the [Pair] values.
 * @param [firstCol] The name of the new column with the first components.
 * @param [secondCol] The name of the new column with the second components.
 * @return A new [DataFrame] with the selected columns replaced by column groups with the new columns.
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
 * Splits the [DataFrame] values of the columns selected by [split] into columns:
 * each selected column becomes a [column group][ColumnGroup] with the columns of its [DataFrame] values,
 * where each cell holds a [List] of the values of the corresponding [DataFrame] column.
 *
 * In other words, each [DataFrame] value is [imploded][implode] into a single row.
 * Empty [DataFrame] values result in empty [List]s.
 *
 * Don't confuse this with [into][Split.into], which stores each row of the [DataFrame] values in a separate new column.
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitFrameColumn]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [DataFrame] type of the values in the columns selected by [split].
 * @return A new [DataFrame] with the selected columns replaced by column groups.
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
 * Splits the values of the columns selected by [split] into parts and places each part in a separate row,
 * duplicating the values in the other columns.
 *
 * {@get [SplitDocs.PARTS]}
 * The type of each split column changes from the type of its values to the type of their parts.
 * When several columns are split, their parts are aligned in the resulting rows, like in [explode].
 *
 * Values that are split into no parts, like `null`s, are handled according to [dropEmpty\].
 *
 * It's equivalent to [inplace][SplitWithTransform.inplace] followed by [explode].
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * Reverse operation: [implode].
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 */
@ExcludeFromSources
private typealias CommonSplitIntoRowsDocs = Nothing

/**
 * @include [CommonSplitIntoRowsDocs] {@include [SplitDocs.SetIterableParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIterableIntoRows]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [Iterable] type of the values in the columns selected by [split].
 * @param [R] The type of the elements of the [Iterable] values.
 * @include [ExplodeDocs.DropEmptySnippet]
 * @return A new [DataFrame] with a separate row for each element.
 */
@JvmName("intoRowsTC")
@Refine
@Interpretable("SplitIntoRows")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it }
        .intoRows(dropEmpty)

/**
 * @include [CommonSplitIntoRowsDocs] {@include [SplitDocs.SetDataFrameParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitFrameIntoRows]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [DataFrame] type of the values in the columns selected by [split].
 * @include [ExplodeDocs.DropEmptySnippet]
 * @return A new [DataFrame] with a separate row for each row of the [DataFrame] values.
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
 * @include [CommonSplitIntoRowsDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitIntoRows_properties]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @include [ExplodeDocs.DropEmptySnippet]
 * @return A new [DataFrame] with a separate row for each part.
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
 * Splits the values of the columns selected by [split] into parts and stores them as a [List]
 * in the original columns, keeping their positions and names.
 *
 * {@get [SplitDocs.PARTS]}
 * The type of each split column becomes a [List] of the type of the parts; `null` values become empty [List]s.
 *
 * See also [intoRows][SplitWithTransform.intoRows], which also places each part in a separate row.
 *
 * @include [SplitDocs.TerminalStepSnippet]
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * See [Grammar][SplitDocs.Grammar].
 */
@ExcludeFromSources
private typealias CommonSplitInplaceDocs = Nothing

/**
 * @include [CommonSplitInplaceDocs] {@include [SplitDocs.SetIterableParts]}
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @param [C] The [Iterable] type of the values in the columns selected by [split].
 * @param [R] The type of the elements of the [Iterable] values.
 * @return A new [DataFrame] with the values of the selected columns converted to [List]s.
 */
@JvmName("inplaceTC")
@Refine
@Interpretable("SplitInplace")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = by { it }.inplace()

/**
 * @include [CommonSplitInplaceDocs] {@include [SplitDocs.SetTransformParts]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitInplace_properties]
 *
 * @include [SplitDocs.TypeParamTSnippet]
 * @include [SplitDocs.TypeParamCSnippet]
 * @include [SplitDocs.TypeParamRSnippet]
 * @return A new [DataFrame] with the values of the selected columns replaced by [List]s of their parts.
 */
@Refine
@Interpretable("SplitWithTransformInplace")
public fun <T, C, R> SplitWithTransform<T, C, R>.inplace(): DataFrame<T> =
    df.convert(columns).splitInplace(tartypeOf, transform)

// endregion

// region DataColumn

/**
 * Splits the [Iterable] values of this [DataColumn] into their elements
 * and returns them as a new [DataFrame]: the `i`-th element of each value is stored in the `i`-th column.
 *
 * The number of columns is the maximum number of elements among the values,
 * or the number of the given [names] if it's larger.
 * Values with fewer elements, as well as `null` values, get `null` in the remaining columns.
 * The columns not covered by [names] are named `split1`, `split2`, and so on.
 *
 * It's a shortcut for converting this column to a [DataFrame] and calling [split][DataFrame.split]
 * and [into][Split.into] on it.
 *
 * For more information: {@include [DocumentationUrls.Split]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.SplitSamples.splitColumnInto]
 *
 * @param [names] The names of the new columns, in the order of the elements.
 * @return A new [DataFrame] that contains only the new columns.
 */
public fun DataColumn<Iterable<*>>.splitInto(vararg names: String): DataFrame<*> =
    toDataFrame().split { this@splitInto }.into(*names)

// endregion
