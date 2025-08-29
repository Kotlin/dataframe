package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.ExportAsHtml
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.RowConditionLink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
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
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent.Companion.media
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.FORMATTING_DSL
import org.jetbrains.kotlinx.dataframe.util.FORMATTING_DSL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.RGB_COLOR
import org.jetbrains.kotlinx.dataframe.util.RGB_COLOR_REPLACE
import kotlin.reflect.KProperty

// region docs

/**
 * Formats the specified [columns\] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause] which serves as an intermediate step.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
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
 * Specifying a [column group][ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [frame column][FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [DataFrame] to a [FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [Grammar].
 *
 * For more information: {@include [DocumentationUrls.Format]}
 */
internal interface FormatDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetFormatOperationArg]}
     */
    interface FormatSelectingColumns

    /**
     * ## Format Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     *
     * @include [ForHtml]
     */
    interface Grammar {

        /**
         * ### Definitions:
         * {@include [CellFormatterDef]}
         * {@include [LineBreak]}
         * {@include [RowColFormatterDef]}
         *
         * ### Notation:
         *
         * [**format**][DataFrame.format]**`  {  `**[`columns`][SelectingColumns]**` }`**
         *
         * {@include [Indent]}
         * `\[ `__`.`__[**`where`**][FormatClause.where]**`  {  `**[`filter`][SelectingRows.RowValueCondition]`: `[`RowValueFilter`][RowValueFilter]**`  }  `**`]`
         *
         * {@include [Indent]}
         * `\[ `__`.`__[**`at`**][FormatClause.at]**`(`**`rowIndices: `[Collection][Collection]`<`[Int][Int]`> | `[IntRange][IntRange]` | `**`vararg`**` `[Int][Int]**`)`**` ]`
         *
         * {@include [Indent]}
         * `\[ `__`.`__[**`notNull`**][FormatClause.notNull]**`()`**` ]`
         *
         * {@include [Indent]}
         * __`.`__[**`with`**][FormatClause.with]**`  {  `**{@include [CellFormatterRef]}**` }`**
         *
         * {@include [Indent]}
         * `| `__`.`__[**`notNull`**][FormatClause.notNull]**`  {  `**{@include [CellFormatterRef]}**` }`**
         *
         * {@include [Indent]}
         * `| `__`.`__[**`perRowCol`**][FormatClause.perRowCol]**`  {  `**{@include [RowColFormatterRef]}**` }`**
         *
         * {@include [Indent]}
         * `| `__`.`__[**`linearBg`**][FormatClause.linearBg]**`(`**`from: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`,`**` to: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`)`**
         *
         * `\[ `__`.`__[**format**][FormattedFrame.format]` ↺ \]`
         * {@include [LineBreak]}
         * {@include [FormattingDslGrammarDef]}
         */
        @ExportAsHtml
        @ExcludeFromSources
        interface ForHtml

        /**
         * ## Formatting DSL Grammar
         *
         * ### Definitions:
         * {@include [CellAttributesDef]}
         * {@include [LineBreak]}
         * {@include [RgbColorDef]}
         *
         * ### Notation:
         * _- Returning [CellAttributes][CellAttributes]_:
         *
         * {@include [CellAttributesRef]}` `[**`and`**][CellAttributes.and]` `{@include [CellAttributesRef]}
         *
         * `| `[**`italic`**][FormattingDsl.italic]`  |  `[**`bold`**][FormattingDsl.bold]`  |  `[**`underline`**][FormattingDsl.underline]
         *
         * `| `[**`background`**][FormattingDsl.background]**`(`**{@include [RgbColorRef]}**`)`**
         *
         * `| `[**`background`**][FormattingDsl.background]**`(`**`r: `[Short][Short]**`,`**` g: `[Short][Short]**`,`**` b: `[Short][Short]**`)`**
         *
         * `| `[**`linearBg`**][FormattingDsl.linearBg]**`(`**`value: `[Number][Number]**`,`**` from: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`,`**` to: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`)`**
         *
         * `| `[**`textColor`**][FormattingDsl.textColor]**`(`**{@include [RgbColorRef]}**`)`**
         *
         * `| `[**`textColor`**][FormattingDsl.textColor]**`(`**`r: `[Short][Short]**`,`**` g: `[Short][Short]**`,`**` b: `[Short][Short]**`)`**
         *
         * `| `[**`attr`**][attr]**`(`**`name: `[String][String]**`,`**` value: `[String][String]**`)`**
         *
         * _- Returning [RgbColor][RgbColor]:_
         *
         * [**`black`**][FormattingDsl.black]`  |  `[**`white`**][FormattingDsl.white]`  |  `[**`green`**][FormattingDsl.green]`  |  `[**`red`**][FormattingDsl.red]`  |  `[**`blue`**][FormattingDsl.blue]`  |  `[**`gray`**][FormattingDsl.gray]`  |  `[**`darkGray`**][FormattingDsl.darkGray]`  |  `[**`lightGray`**][FormattingDsl.lightGray]
         *
         * `| `[**`rgb`**][FormattingDsl.rgb]**`(`**`r: `[Short][Short]**`,`**` g: `[Short][Short]**`,`**` b: `[Short][Short]**`)`**
         *
         * `| `[**`linear`**][FormattingDsl.linear]**`(`**`value: `[Number][Number]**`,`**` from: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`,`**` to: `[Pair][Pair]`<`[Number][Number]`, `[RgbColor][RgbColor]`>`**`)`**
         */
        interface FormattingDslGrammarDef

        /**
         * `cellFormatter: `{@include [FormattingDslGrammarRef]}`.(cell: C) -> `[CellAttributes][CellAttributes]`?`
         */
        interface CellFormatterDef

        /**
         * `rowColFormatter: `{@include [FormattingDslGrammarRef]}`.(row: `[DataRow][DataRow]`<T>, col: `[ColumnWithPath][ColumnWithPath]`<C>) -> `[CellAttributes][CellAttributes]`?`
         */
        interface RowColFormatterDef

        /**
         * `cellAttributes: `[CellAttributes][CellAttributes]
         */
        interface CellAttributesDef

        /**
         * `color: `[RgbColor][RgbColor]
         */
        interface RgbColorDef

        /** [cellFormatter][CellFormatterDef] */
        @ExcludeFromSources
        interface CellFormatterRef

        /** [rowColFormatter][RowColFormatterDef] */
        @ExcludeFromSources
        interface RowColFormatterRef

        /** [FormattingDsl][FormattingDslGrammarDef] */
        @ExcludeFromSources
        interface FormattingDslGrammarRef

        /** [cellAttributes][CellAttributesDef] */
        @ExcludeFromSources
        interface CellAttributesRef

        /** [color][RgbColorDef] */
        @ExcludeFromSources
        interface RgbColorRef
    }
}

/** {@set [SelectingColumns.OPERATION] [format][format]} */
@ExcludeFromSources
private interface SetFormatOperationArg

/**
 * @include [FormatDocs]
 * ### This Format Overload
 */
@ExcludeFromSources
private interface CommonFormatDocs

// endregion

// region DataFrame format

/**
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.Dsl] {@include [SetFormatOperationArg]}
 * ### Examples:
 * ```kt
 * df.format { temperature }.linearBg(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
 *
 * @param [columns\] The [columns-selector][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> DataFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> = FormatClause(this, columns)

/**
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetFormatOperationArg]}
 * ### Examples:
 * ```kt
 * df.format("temperature").with { linearBg(it as Number, -20 to blue, 50 to red) }
 *   .format("age").notNull().perRowCol { row, col ->
 *     col as DataColumn<Int>
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
 *
 * @param [columns\] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> DataFrame<T>.format(vararg columns: String): FormatClause<T, Any?> = format { columns.toColumnSet() }

/**
 * @include [CommonFormatDocs]
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [columns-selector][ColumnsSelector] or by [column names][String].
 *
 * ### Examples:
 * ```kt
 * df.format().with { background(white) and textColor(black) and bold }
 *   .format { temperature }.linearBg(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
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
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.Dsl] {@include [SetFormatOperationArg]}
 * ### Examples:
 * ```kt
 * df.format().with { background(white) and textColor(black) and bold }
 *   .format { temperature }.linearBg(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
 *
 * @param [columns\] The [columns-selector][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> FormattedFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> =
    FormatClause(df, columns, formatter)

/**
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetFormatOperationArg]}
 * ### Examples:
 * ```kt
 * df.format("temperature").with { linearBg(it as Number, -20 to blue, 50 to red) }
 *   .format("age").notNull().perRowCol { row, col ->
 *     col as DataColumn<Int>
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
 *
 * @param [columns\] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> FormattedFrame<T>.format(vararg columns: String): FormatClause<T, Any?> =
    format { columns.toColumnSet() }

/**
 * @include [CommonFormatDocs]
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [columns-selector][ColumnsSelector] or by [column names][String].
 *
 * ### Examples:
 * ```kt
 * df.format { temperature }.with { textColor(linear(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)) }
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }
 *   .format().with { background(white) and bold }
 *   .toStandaloneHtml().openInBrowser()
 * ```
 */
public fun <T> FormattedFrame<T>.format(): FormatClause<T, Any?> = FormatClause(df = df, oldFormatter = formatter)

// endregion

// region intermediate operations

/**
 * Filters the rows to format using a [RowValueFilter].
 *
 * See {@include [RowConditionLink]}.
 *
 * You need to specify [filter]: A lambda function expecting a `true` result for each
 * cell that should be included in the formatting selection.
 * Both the cell value (`it: `[C][C]) and its row (`this: `[DataRow][DataRow]`<`[T][T]`>`) are available.
 *
 * ### Examples using [where]:
 * ```kt
 * df.format { temperature }
 *   .where { it !in -10..40 }
 *   .with { background(red) }
 * ```
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.where(filter: RowValueFilter<T, C>): FormatClause<T, C> =
    FormatClause(filter = this.filter and filter, df = df, columns = columns, oldFormatter = oldFormatter)

/**
 * Only format the selected columns at given row indices.
 *
 * Accepts either a [Collection]<[Int]>, an [IntRange], or just `vararg `[Int] indices.
 *
 * ### Examples using [at]
 */
@ExcludeFromSources
private interface CommonFormatAtDocs

/**
 * @include [CommonFormatAtDocs]
 * ```kt
 * df.format()
 *   .at(df.indices().step(2).toList())
 *   .with { background(lightGray) }
 * ```
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(rowIndices: Collection<Int>): FormatClause<T, C> = where { index in rowIndices }

/**
 * @include [CommonFormatAtDocs]
 * ```kt
 * df.format { colsOf<String?>() }
 *   .at(0, 3, 4)
 *   .with { background(lightGray) }
 * ```
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(vararg rowIndices: Int): FormatClause<T, C> = at(rowIndices.toSet())

/**
 * @include [CommonFormatAtDocs]
 * ```kt
 * df.format { cols(2..7) }
 *   .at(2..7)
 *   .with { background(lightGray) }
 * ```
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(rowRange: IntRange): FormatClause<T, C> = where { index in rowRange }

/**
 * Filters the format-selection to only include cells where the value is not null.
 *
 * This is shorthand for `.`[where][FormatClause.where]` { it != null }`.
 *
 * ### Examples using [notNull]:
 * ```kt
 * df.format { colsOf<Int?>() }.notNull().perRowCol { row, col ->
 *     linearBg(col[row], col.min() to red, col.max() to green)
 * }
 * ```
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C?>.notNull(): FormatClause<T, C> = where { it != null } as FormatClause<T, C>

// endregion

// region terminal operations

/**
 * Creates a new [FormattedFrame] that uses the specified [RowColFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [formatter]: {@include [RowColFormatter]}
 *
 * ### Examples using [perRowCol]:
 * ```kt
 * df.format { colsOf<Int>() }.perRowCol { row, col ->
 *     linearBg(col[row], col.min() to red, col.max() to green)
 * }
 * ```
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.perRowCol(formatter: RowColFormatter<T, C>): FormattedFrame<T> =
    formatImpl(formatter)

/**
 * Creates a new [FormattedFrame] that uses the specified [CellFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [formatter]: {@include [CellFormatter]}
 *
 * ### Examples using [with]:
 * ```kt
 * df.format()
 *   .at(df.indices().step(2).toList())
 *   .with { background(lightGray) and bold and textColor(black) }
 * ```
 *
 * Check out the full [Grammar][FormatDocs.Grammar].
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C>.with(formatter: CellFormatter<C>): FormattedFrame<T> =
    formatImpl { row, col -> formatter(col[row] as C) }

/**
 * Creates a new [FormattedFrame] that uses the specified [CellFormatter] to format selected non-null cells of the dataframe.
 *
 * This function is shorthand for `.`[notNull()][FormatClause.notNull]`.`[with { }][FormatClause.with].
 *
 * You need to specify [formatter]: {@include [CellFormatter]}
 *
 * ### Examples using [notNull]:
 * ```kt
 * df.format().notNull { bold and textColor(black) }
 * ```
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
 * ```kt
 * df.format { temperature }.linearBg(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
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
     *     background(linear(value, -20 to blue, 40 to red)) and
     *       textColor(black)
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
 * [DataRow][DataRow]`<`[T][T]`>` and [ColumnWithPath][ColumnWithPath]`<`[C][C]`>`.
 *
 * This is similar to a [RowColumnExpression], except that you also have access
 * to the [FormattingDsl] in the context.
 *
 * @include [FormattingDsl]
 */
public typealias RowColFormatter<T, C> = FormattingDsl.(row: DataRow<T>, col: ColumnWithPath<C>) -> CellAttributes?

/**
 * A lambda function expecting a [CellAttributes] or `null` given an instance of a cell: [C] of the dataframe.
 *
 * You have access to the [FormattingDsl] in the context.
 *
 * @include [FormattingDsl]
 */
public typealias CellFormatter<C> = FormattingDsl.(cell: C) -> CellAttributes?

/**
 * A wrapper around a [DataFrame][df] with CSS attributes that can be
 * converted to a formatted HTML table in the form of [DataFrameHtmlData].
 *
 * Call [toHtml] or [toStandaloneHtml] to get the HTML representation of the [DataFrame].
 *
 * In Jupyter kernel (Kotlin Notebook) environments, you can often output this class directly.
 * Use [toHtml] or [toStandaloneHtml] when this produces unexpected results.
 *
 * You can apply further formatting to this [FormattedFrame] by calling [format()][FormattedFrame.format] once again.
 */
public class FormattedFrame<T>(internal val df: DataFrame<T>, internal val formatter: RowColFormatter<T, *>? = null) {

    /**
     * Returns a [DataFrameHtmlData] without additional definitions.
     * Can be rendered in Jupyter kernel (Kotlin Notebook) environments or other environments that already have
     * CSS- and script definitions for DataFrame.
     *
     * Use [toStandaloneHtml] if you need the [DataFrameHtmlData] to include CSS- and script definitions.
     *
     * By default, cell content is formatted as text
     * Use [RenderedContent.media][media] or [IMG], [IFRAME] if you need custom HTML inside a cell.
     *
     * @param [configuration] The [DisplayConfiguration] to use as a base for this [FormattedFrame].
     *   Default: [DisplayConfiguration.DEFAULT].
     * @see toStandaloneHtml
     */
    public fun toHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toHtml(getDisplayConfiguration(configuration))

    /**
     * Returns a [DataFrameHtmlData] with CSS- and script definitions for DataFrame.
     *
     * Use [toHtml] if you don't need the [DataFrameHtmlData] to include CSS- and script definitions.
     *
     * The [DataFrameHtmlData] can be saved as an *.html file and displayed in the browser.
     * If you save it as a file and find it in the project tree,
     * the ["Open in browser"](https://www.jetbrains.com/help/idea/editing-html-files.html#ws_html_preview_output_procedure)
     * feature of IntelliJ IDEA will automatically reload the file content when it's updated.
     *
     * By default, cell content is formatted as text
     * Use [RenderedContent.media][media] or [IMG], [IFRAME] if you need custom HTML inside a cell.
     *
     * __NOTE:__ In Kotlin Notebook, output [FormattedFrame] directly, or use [toHtml],
     * as that environment already has CSS- and script definitions for DataFrame.
     * Using [toStandaloneHtml] might produce unexpected results.
     *
     * @param [configuration] The [DisplayConfiguration] to use as a base for this [FormattedFrame].
     *   Default: [DisplayConfiguration.DEFAULT].
     * @see toHtml
     */
    public fun toStandaloneHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toStandaloneHtml(getDisplayConfiguration(configuration))

    /** Applies this formatter to the given [configuration] and returns a new instance. */
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
    internal val columns: ColumnsSelector<T, C> = { all().cast() },
    internal val oldFormatter: RowColFormatter<T, C>? = null,
    internal val filter: RowValueFilter<T, C> = { true },
) {
    override fun toString(): String =
        "FormatClause(df=$df, columns=$columns, oldFormatter=$oldFormatter, filter=$filter)"
}

// endregion

// region Deprecated

@Deprecated(
    message = FORMATTING_DSL,
    replaceWith = ReplaceWith(FORMATTING_DSL_REPLACE),
    level = DeprecationLevel.ERROR,
)
public typealias FormattingDSL = FormattingDsl

@Deprecated(
    message = RGB_COLOR,
    replaceWith = ReplaceWith(RGB_COLOR_REPLACE),
    level = DeprecationLevel.ERROR,
)
public typealias RGBColor = RgbColor

// endregion
