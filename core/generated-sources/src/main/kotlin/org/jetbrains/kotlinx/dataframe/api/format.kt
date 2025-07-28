package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.green
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linearBg
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.MergedAttributes
import org.jetbrains.kotlinx.dataframe.impl.api.SingleAttribute
import org.jetbrains.kotlinx.dataframe.impl.api.encode
import org.jetbrains.kotlinx.dataframe.impl.api.formatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.linearGradient
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region docs

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][FormatSelectingColumns].
 *
 * The [FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][FormatClause.with], [perRowCol][FormatClause.perRowCol], or [linearBg][FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame] by calling [format][FormattedFrame.format] on it again.
 *
 * Check out the [Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
internal interface FormatDocs {

    /**
     *
     * ## Selecting Columns
     * Selecting columns for various operations (including but not limited to
     * [DataFrame.select][org.jetbrains.kotlinx.dataframe.DataFrame.select], [DataFrame.update][org.jetbrains.kotlinx.dataframe.DataFrame.update], [DataFrame.gather][org.jetbrains.kotlinx.dataframe.DataFrame.gather], and [DataFrame.fillNulls][org.jetbrains.kotlinx.dataframe.DataFrame.fillNulls])
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample]
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
     * #### For example:
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]`("length", "age")`
     *
     * ### 3. [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample]
     * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
     * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
     *
     * #### For example:
     *
     * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[format][org.jetbrains.kotlinx.dataframe.api.format]`(Person::length, Person::age)`
     *
     */
    interface FormatSelectingColumns

    /**
     * ## Format Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * TODO
     */
    interface Grammar
}

// endregion

// region DataFrame format

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
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
 * ### Examples:
 * TODO example
 *
 * @param [columns] The [columns-selector][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> DataFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> = FormatClause(this, columns)

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Examples:
 * TODO example
 *
 * @param [columns] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> DataFrame<T>.format(vararg columns: String): FormatClause<T, Any?> = format { columns.toColumnSet() }

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [columns-selector][ColumnsSelector] or by [column names][String].
 *
 * ### Examples:
 * TODO example
 */
public fun <T> DataFrame<T>.format(): FormatClause<T, Any?> = FormatClause(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.format(vararg columns: ColumnReference<C>): FormatClause<T, C> =
    format { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.format(vararg columns: KProperty<C>): FormatClause<T, C> =
    format { columns.toColumnSet() }

// endregion

// region FormattedFrame format

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
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
 * ### Examples:
 * TODO example
 *
 * @param [columns] The [columns-selector][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> FormattedFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> =
    FormatClause(df, columns, formatter)

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Examples:
 * TODO example
 *
 * @param [columns] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> FormattedFrame<T>.format(vararg columns: String): FormatClause<T, Any?> =
    format { columns.toColumnSet() }

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [FormatClause][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [perRowCol][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [linearBg][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [FormattedFrame][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [format][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Check out the [Grammar][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [columns-selector][ColumnsSelector] or by [column names][String].
 *
 * ### Examples:
 * TODO example
 */
public fun <T> FormattedFrame<T>.format(): FormatClause<T, Any?> = FormatClause(df, null, formatter)

// endregion

// region intermediate operations

/**
 * Filters the rows to format using a [RowValueFilter].
 *
 * See [Row Condition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows].
 *
 * You need to specify [filter]: A lambda function expecting a `true` result for each
 * cell that should be included in the formatting selection.
 * Both the cell value (`it: `[C][C]) and its row (`this: `[DataRow][DataRow]`<`[T][T]`>`) are available.
 *
 * ### Examples using [where]:
 * TODO
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.where(filter: RowValueFilter<T, C>): FormatClause<T, C> =
    FormatClause(filter = this.filter and filter, df = df, columns = columns, oldFormatter = oldFormatter)

public fun <T, C> FormatClause<T, C>.at(rowIndices: Collection<Int>): FormatClause<T, C> = where { index in rowIndices }

public fun <T, C> FormatClause<T, C>.at(vararg rowIndices: Int): FormatClause<T, C> = at(rowIndices.toSet())

public fun <T, C> FormatClause<T, C>.at(rowRange: IntRange): FormatClause<T, C> = where { index in rowRange }

/**
 * Filters the format-selection to only include cells where the value is not null.
 *
 * This is shorthand for `.`[where][FormatClause.where]` { it != null }`.
 *
 * For example:
 *
 * `df.`[format][format]` { `[colsOf][colsOf]`<`[Int][Int]`?>() }.`[notNull][notNull]`().`[perRowCol][FormatClause.perRowCol]` { row, col ->`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;[linearBg][FormattingDsl.linearBg]`(row[col], col.`[min][DataColumn.min]`() to `[red][FormattingDsl.red]`, col.`[max][DataColumn.max]`() to `[green][FormattingDsl.green]`)`
 *
 * `}`
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C?>.notNull(): FormatClause<T, C> = where { it != null } as FormatClause<T, C>

// endregion

// region terminal operations

/**
 * Creates a new [FormattedFrame] that uses the specified [RowColFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [formatter]: A lambda function expecting a [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of
 * [DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[T][T]`>` and [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[C][C]`>`.
 *
 * This is similar to a [RowColumnExpression][org.jetbrains.kotlinx.dataframe.RowColumnExpression], except that you also have access
 * to the [FormattingDsl][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[white][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [textColor][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[black][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [bold][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [linear][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [attr][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * ### Examples using [perRowCol]:
 * TODO
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.perRowCol(formatter: RowColFormatter<T, C>): FormattedFrame<T> =
    formatImpl(formatter)

/**
 * Creates a new [FormattedFrame] that uses the specified [CellFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [formatter]: A lambda function expecting a [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of a cell: [C] of the dataframe.
 *
 * You have access to the [FormattingDsl][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[white][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [textColor][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[black][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [bold][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [linear][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [attr][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * ### Examples using [with]:
 * TODO
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C>.with(formatter: CellFormatter<C>): FormattedFrame<T> =
    formatImpl { row, col -> formatter(row[col.name] as C) }

/**
 * Creates a new [FormattedFrame] that uses the specified [CellFormatter] to format selected non-null cells of the dataframe.
 *
 * This function is shorthand for `.`[notNull()][FormatClause.notNull]`.`[with { }][FormatClause.with].
 *
 * You need to specify [formatter]: A lambda function expecting a [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of a cell: [C] of the dataframe.
 *
 * You have access to the [FormattingDsl][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[white][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [textColor][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[black][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [bold][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [linear][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [attr][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * ### Examples using [notNull]:
 * TODO
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C?>.notNull(formatter: CellFormatter<C>): FormattedFrame<T> =
    notNull().with(formatter)

/**
 * Creates a new [FormattedFrame] by just changing the background colors of the selected cells.
 *
 * The background color of each selected cell is calculated by interpolating between [from] and [to],
 * given the numeric value of that cell.
 * The interpolation is linear.
 *
 * If the numeric cell value falls outside the range [from]..[to], the colors at the bounds will be used.
 *
 * This function is shorthand for:
 *
 * `.`[with][FormatClause.with]`  {  `[background][FormattingDsl.background]`(`[linear][FormattingDsl.linear]`(it, `[from][from]`, `[to][to]`)) }`
 *
 * See also [with][FormatClause.with], [background][FormattingDsl.background], and [linear][FormattingDsl.linear].
 *
 * ### Examples using [linearBg]:
 * TODO
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 *
 * @param [from] The lower bound of the interpolation range and the color that will be returned when the cell value touches this bound.
 * @param [to] The upper bound of the interpolation range and the color that will be returned when the cell value touches this bound.
 */
public fun <T, C : Number?> FormatClause<T, C>.linearBg(
    from: Pair<Number, RgbColor>,
    to: Pair<Number, RgbColor>,
): FormattedFrame<T> =
    with {
        if (it != null) {
            background(linear(it, from, to))
        } else {
            null
        }
    }

// endregion

// region Formatting DSL

/**
 * Represents a color in the RGB color space.
 * To be used in the [DataFrame.format]; [FormattingDsl].
 *
 * Any color can be represented in terms of [r] (red), [g] (green), and [b] (blue) values from `0..255`.
 *
 * Inside [FormattingDsl], there are shortcuts for common colors, like [white][FormattingDsl.white],
 * [green][FormattingDsl.green], and [gray][FormattingDsl.gray].
 */
public data class RgbColor(val r: Short, val g: Short, val b: Short) {

    /** Encodes the color as a [String] such that it can be used as the value of an attribute in CSS. */
    override fun toString(): String = encode()
}

/**
 * This represents a collection of CSS cell attributes that can be applied to a cell in an HTML-rendered dataframe.
 *
 * [Cell attributes][CellAttributes] are created inside the [FormattingDsl] by calling
 * [FormatClause.with] or [FormatClause.perRowCol].
 *
 * Multiple attributes can be combined using the [and] operator.
 *
 * For instance:
 *
 * `df.`[format()][DataFrame.format]`.`[`with {`][FormatClause.with]` `[background][FormattingDsl.background]`(`[white][FormattingDsl.white]`) `[and][CellAttributes.and]` `[textColor][FormattingDsl.textColor]`(`[black][FormattingDsl.black]`) `[`}`][FormatClause.with]
 *
 * @see [CellAttributes.and]
 */
public interface CellAttributes {

    /** Retrieves all CSS cell attributes as a list of name-value pairs. */
    public fun attributes(): List<Pair<String, String>>
}

/**
 * Combines two [CellAttributes] instances into a new one that combines their attributes.
 *
 * For instance:
 *
 * `df.`[format()][DataFrame.format]`.`[`with {`][FormatClause.with]` `[background][FormattingDsl.background]`(`[white][FormattingDsl.white]`) `[and][CellAttributes.and]` `[textColor][FormattingDsl.textColor]`(`[black][FormattingDsl.black]`) `[`}`][FormatClause.with]
 */
public infix fun CellAttributes?.and(other: CellAttributes?): CellAttributes? =
    when {
        other == null -> this
        this == null -> other
        else -> MergedAttributes(listOf(this, other))
    }

/**
 * The formatting DSL allows you to create and combine [CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][FormattingDsl.background]`(`[white][FormattingDsl.white]`) `[and][CellAttributes.and]` `
 * [textColor][FormattingDsl.textColor]`(`[black][FormattingDsl.black]`) `[and][CellAttributes.and]` `
 * [bold][FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][FormattingDsl.rgb] or interpolate
 * colors using [linear][FormattingDsl.linear].
 *
 * Use [attr] if you want to specify a custom CSS attribute.
 */
public object FormattingDsl {

    /** Creates a new [RgbColor] instance with [r] (red), [g] (green), and [b] (blue) values from `0..255`. */
    public fun rgb(r: Short, g: Short, b: Short): RgbColor = RgbColor(r, g, b)

    public val black: RgbColor = rgb(0, 0, 0)

    public val white: RgbColor = rgb(255, 255, 255)

    public val green: RgbColor = rgb(0, 255, 0)

    public val red: RgbColor = rgb(255, 0, 0)

    public val blue: RgbColor = rgb(0, 0, 255)

    public val gray: RgbColor = rgb(128, 128, 128)

    public val darkGray: RgbColor = rgb(169, 169, 169)

    public val lightGray: RgbColor = rgb(211, 211, 211)

    /**
     * A custom [cell attribute][CellAttributes]
     * that allows you to specify any custom CSS attribute by [name] and [value].
     *
     * For example:
     * ```kt
     * attr("text-align", "center")
     * attr("border", "3px solid green")
     * ```
     */
    public fun attr(name: String, value: String): CellAttributes = SingleAttribute(name, value)

    /**
     * A [cell attribute][CellAttributes] that sets the background color of a cell.
     * @param color Either one of the predefined colors, like [black], or [green], or a custom color using [rgb()][rgb].
     */
    public fun background(color: RgbColor): CellAttributes = attr("background-color", color.toString())

    /**
     * A [cell attribute][CellAttributes] that sets the background color of a cell.
     * A shortcut for [background][background]`(`[rgb(...)][rgb]`)`.
     * @see [rgb]
     */
    public fun background(r: Short, g: Short, b: Short): CellAttributes = background(RgbColor(r, g, b))

    /**
     * A [cell attribute][CellAttributes] that sets the text color of a cell.
     * @param color Either one of the predefined colors, like [black], or [green], or a custom color using [rgb()][rgb].
     */
    public fun textColor(color: RgbColor): CellAttributes = attr("color", color.toString())

    /**
     * A [cell attribute][CellAttributes] that sets the text color of a cell.
     * A shortcut for [textColor][textColor]`(`[rgb(...)][rgb]`)`.
     * @see [rgb]
     */
    public fun textColor(r: Short, g: Short, b: Short): CellAttributes = textColor(RgbColor(r, g, b))

    /** A [cell attribute][CellAttributes] that makes the text inside the cell *italic*. */
    public val italic: CellAttributes = attr("font-style", "italic")

    /** A [cell attribute][CellAttributes] that makes the text inside the cell **bold**. */
    public val bold: CellAttributes = attr("font-weight", "bold")

    /** A [cell attribute][CellAttributes] that u͟n͟d͟e͟r͟l͟i͟n͟e͟s͟ the text inside the cell. */
    public val underline: CellAttributes = attr("text-decoration", "underline")

    /**
     * Shorthand for [background][background]`(`[linear][linear]`(...))`
     *
     * Creates a [cell attribute][CellAttributes] that applies a background color calculated
     * by interpolating between [from] and [to], given [value].
     *
     * See [linear] for more information.
     *
     * @see linear
     * @see background
     */
    public fun linearBg(value: Number, from: Pair<Number, RgbColor>, to: Pair<Number, RgbColor>): CellAttributes =
        background(
            linear(value, from, to),
        )

    /**
     * Calculates an [RgbColor] by interpolating between [from] and [to], given [value].
     * The interpolation is linear.
     * If [value] falls outside the range [from]..[to], the colors at the bounds will be used.
     *
     * Very useful if you want the text-, or background color to correspond to the value of a cell, for instance.
     *
     * For example:
     * ```kt
     * df.format { temperature }.with { value ->
     *     background(linear(value, -20 to blue, 40 to red)) and textColor(black)
     * }
     * ```
     *
     * @param [value] The value to interpolate the color for.
     * @param [from] The lower bound of the interpolation range and the color that will be returned when [value] touches this bound.
     * @param [to] The upper bound of the interpolation range and the color that will be returned when [value] touches this bound.
     * @return An [RgbColor] that corresponds to the interpolation.
     * @see linearBg
     */
    public fun linear(value: Number, from: Pair<Number, RgbColor>, to: Pair<Number, RgbColor>): RgbColor {
        val a = from.first.toDouble()
        val b = to.first.toDouble()
        return if (a < b) {
            linearGradient(
                x = value.toDouble(),
                minValue = a,
                minColor = from.second,
                maxValue = b,
                maxColor = to.second,
            )
        } else {
            linearGradient(
                x = value.toDouble(),
                minValue = b,
                minColor = to.second,
                maxValue = a,
                maxColor = from.second,
            )
        }
    }
}

// endregion

// region types and classes

/**
 * A lambda function expecting a [CellAttributes] or `null` given an instance of
 * [DataRow][DataRow]`<`[T][T]`>` and [DataColumn][DataColumn]`<`[C][C]`>`.
 *
 * This is similar to a [RowColumnExpression], except that you also have access
 * to the [FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[white][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [textColor][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[black][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [bold][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [linear][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [attr][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 */
public typealias RowColFormatter<T, C> = FormattingDsl.(row: DataRow<T>, col: DataColumn<C>) -> CellAttributes?

/**
 * A lambda function expecting a [CellAttributes] or `null` given an instance of a cell: [C] of the dataframe.
 *
 * You have access to the [FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [CellAttributes][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [background][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[white][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [textColor][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[black][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[and][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [bold][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [rgb][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [linear][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [attr][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 */
public typealias CellFormatter<C> = FormattingDsl.(cell: C) -> CellAttributes?

/**
 * A wrapper around a [DataFrame][df] with CSS attributes that can be
 * converted to a formatted HTML table in the form of [DataFrameHtmlData].
 *
 * Call [toHtml] or [toStandaloneHtml] to get the HTML representation of the [DataFrame].
 *
 * You can apply further formatting to this [FormattedFrame] by calling [format()][FormattedFrame.format] once again.
 */
public class FormattedFrame<T>(internal val df: DataFrame<T>, internal val formatter: RowColFormatter<T, *>? = null) {

    /**
     * todo copy from toHtml
     * @return DataFrameHtmlData without additional definitions. Can be rendered in Jupyter kernel environments
     */
    public fun toHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toHtml(getDisplayConfiguration(configuration))

    /**
     * todo copy from toStandaloneHtml
     * @return DataFrameHtmlData with table script and css definitions. Can be saved as an *.html file and displayed in the browser
     */
    public fun toStandaloneHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toStandaloneHtml(getDisplayConfiguration(configuration))

    /**
     *
     */
    @Suppress("UNCHECKED_CAST")
    public fun getDisplayConfiguration(configuration: DisplayConfiguration): DisplayConfiguration =
        configuration.copy(cellFormatter = formatter as RowColFormatter<*, *>?)
}

/**
 * An intermediate class used in the [format] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * how to format the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [FormattedFrame]; a [DataFrame] with HTML formatting data.
 *
 * Use the following function to filter the rows to format:
 * - [where][FormatClause.where] – filters the rows to format using a [RowValueFilter].
 * - [at][FormatClause.at] – Only format in rows with certain indices.
 * - [notNull][FormatClause.notNull] – Only format cells that have non-null values.
 *
 * Use the following functions to finalize this formatting round:
 * - [with][FormatClause.with] – Specifies how to format the cells using a [CellFormatter].
 * - [perRowCol][FormatClause.perRowCol] – Specifies how to format each cell individually using a [RowColFormatter].
 * - [linearBg][FormatClause.linearBg] –
 *   Interpolates between two colors to set the background color of each numeric cell based on its value.
 *   Shorthand for `.`[with][FormatClause.with]`  {  `[background][FormattingDsl.background]`(`[linear][FormattingDsl.linear]`(it, from, to)) }`
 * - [notNull][FormatClause.notNull] – Specifies how to format non-null cells using a [CellFormatter].
 *   Shorthand for `.`[notNull()][FormatClause.notNull]`.`[with { }][FormatClause.with].
 *
 * See [Grammar][FormatDocs.Grammar] for more details.
 */
public class FormatClause<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>? = null,
    internal val oldFormatter: RowColFormatter<T, C>? = null,
    internal val filter: RowValueFilter<T, C> = { true },
) {
    override fun toString(): String =
        "FormatClause(df=$df, columns=$columns, oldFormatter=$oldFormatter, filter=$filter)"
}

// endregion
