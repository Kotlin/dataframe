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
import org.jetbrains.kotlinx.dataframe.jupyter.CellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent.Companion.media
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.FORMATTING_DSL
import org.jetbrains.kotlinx.dataframe.util.FORMATTING_DSL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.RGB_COLOR
import org.jetbrains.kotlinx.dataframe.util.RGB_COLOR_REPLACE
import kotlin.reflect.KProperty

// region docs

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][FormatClause.with], [<code>perRowCol</code>][FormatClause.perRowCol], or [<code>linearBg</code>][FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][FormattedFrame] by calling [<code>format</code>][FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][DataFrame] to a [<code>FormattedFrame</code>][FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
internal interface FormatDocs {

    /**
     *
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
     * <code>`df`</code>`.`[<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format]`("length", "age")`
     *
     *
     *
     */
    typealias FormatSelectingColumns = Nothing

    /**
     * ## Format Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * ### Definitions:
     * `cellFormatter: `[<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.FormattingDslGrammarDef]`.(cell: C) -> `[<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes]`?`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `rowColFormatter: `[<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.FormattingDslGrammarDef]`.(row: `[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<T>, col: `[<code>ColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath]`<C>) -> `[<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes]`?`
     *
     * ### Notation:
     *
     * [<code>**format**</code>][org.jetbrains.kotlinx.dataframe.DataFrame.format]**`  {  `**[<code>`columns`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where]**`  {  `**[<code>`filter`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition]`: `[<code>`RowValueFilter`</code>][org.jetbrains.kotlinx.dataframe.RowValueFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`at`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.at]**`(`**`rowIndices: `[<code>Collection</code>][Collection]`<`[<code>Int</code>][Int]`> | `[<code>IntRange</code>][IntRange]` | `**`vararg`**` `[<code>Int</code>][Int]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with]**`  {  `**[<code>cellFormatter</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellFormatterDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.notNull]**`  {  `**[<code>cellFormatter</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellFormatterDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol]**`  {  `**[<code>rowColFormatter</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.RowColFormatterDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`linearBg`**</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg]**`(`**`from: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`,`**` to: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`)`**
     *
     * `[ `__`.`__[<code>**format**</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format]` ↺ ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * ## Formatting DSL Grammar
     *
     * ### Definitions:
     * `cellAttributes: `[<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `color: `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]
     *
     * ### Notation:
     * _- Returning [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes]_:
     *
     * [<code>cellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellAttributesDef]` `[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `[<code>cellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellAttributesDef]
     *
     * `| `[<code>**`italic`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.italic]`  |  `[<code>**`bold`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]`  |  `[<code>**`underline`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.underline]
     *
     * `| `[<code>**`background`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]**`(`**[<code>color</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.RgbColorDef]**`)`**
     *
     * `| `[<code>**`background`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
     *
     * `| `[<code>**`linearBg`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linearBg]**`(`**`value: `[<code>Number</code>][Number]**`,`**` from: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`,`**` to: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`)`**
     *
     * `| `[<code>**`textColor`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]**`(`**[<code>color</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.RgbColorDef]**`)`**
     *
     * `| `[<code>**`textColor`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
     *
     * `| `[<code>**`attr`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr]**`(`**`name: `[<code>String</code>][String]**`,`**` value: `[<code>String</code>][String]**`)`**
     *
     * _- Returning [<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]:_
     *
     * [<code>**`black`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`  |  `[<code>**`white`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`  |  `[<code>**`green`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.green]`  |  `[<code>**`red`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.red]`  |  `[<code>**`blue`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.blue]`  |  `[<code>**`gray`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.gray]`  |  `[<code>**`darkGray`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.darkGray]`  |  `[<code>**`lightGray`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.lightGray]
     *
     * `| `[<code>**`rgb`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
     *
     * `| `[<code>**`linear`**</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear]**`(`**`value: `[<code>Number</code>][Number]**`,`**` from: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`,`**` to: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]`>`**`)`**
     */
    interface Grammar {

        /**
         * ## Formatting DSL Grammar
         *
         * ### Definitions:
         * `cellAttributes: `[<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;
         *
         * `color: `[<code>RgbColor</code>][org.jetbrains.kotlinx.dataframe.api.RgbColor]
         *
         * ### Notation:
         * _- Returning [<code>CellAttributes</code>][CellAttributes]_:
         *
         * [<code>cellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellAttributesDef]` `[<code>**`and`**</code>][CellAttributes.and]` `[<code>cellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.CellAttributesDef]
         *
         * `| `[<code>**`italic`**</code>][FormattingDsl.italic]`  |  `[<code>**`bold`**</code>][FormattingDsl.bold]`  |  `[<code>**`underline`**</code>][FormattingDsl.underline]
         *
         * `| `[<code>**`background`**</code>][FormattingDsl.background]**`(`**[<code>color</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.RgbColorDef]**`)`**
         *
         * `| `[<code>**`background`**</code>][FormattingDsl.background]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
         *
         * `| `[<code>**`linearBg`**</code>][FormattingDsl.linearBg]**`(`**`value: `[<code>Number</code>][Number]**`,`**` from: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][RgbColor]`>`**`,`**` to: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][RgbColor]`>`**`)`**
         *
         * `| `[<code>**`textColor`**</code>][FormattingDsl.textColor]**`(`**[<code>color</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.RgbColorDef]**`)`**
         *
         * `| `[<code>**`textColor`**</code>][FormattingDsl.textColor]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
         *
         * `| `[<code>**`attr`**</code>][attr]**`(`**`name: `[<code>String</code>][String]**`,`**` value: `[<code>String</code>][String]**`)`**
         *
         * _- Returning [<code>RgbColor</code>][RgbColor]:_
         *
         * [<code>**`black`**</code>][FormattingDsl.black]`  |  `[<code>**`white`**</code>][FormattingDsl.white]`  |  `[<code>**`green`**</code>][FormattingDsl.green]`  |  `[<code>**`red`**</code>][FormattingDsl.red]`  |  `[<code>**`blue`**</code>][FormattingDsl.blue]`  |  `[<code>**`gray`**</code>][FormattingDsl.gray]`  |  `[<code>**`darkGray`**</code>][FormattingDsl.darkGray]`  |  `[<code>**`lightGray`**</code>][FormattingDsl.lightGray]
         *
         * `| `[<code>**`rgb`**</code>][FormattingDsl.rgb]**`(`**`r: `[<code>Short</code>][Short]**`,`**` g: `[<code>Short</code>][Short]**`,`**` b: `[<code>Short</code>][Short]**`)`**
         *
         * `| `[<code>**`linear`**</code>][FormattingDsl.linear]**`(`**`value: `[<code>Number</code>][Number]**`,`**` from: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][RgbColor]`>`**`,`**` to: `[<code>Pair</code>][Pair]`<`[<code>Number</code>][Number]`, `[<code>RgbColor</code>][RgbColor]`>`**`)`**
         */
        typealias FormattingDslGrammarDef = Nothing

        /**
         * `cellFormatter: `[<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.FormattingDslGrammarDef]`.(cell: C) -> `[<code>CellAttributes</code>][CellAttributes]`?`
         */
        typealias CellFormatterDef = Nothing

        /**
         * `rowColFormatter: `[<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.FormattingDslGrammarDef]`.(row: `[<code>DataRow</code>][DataRow]`<T>, col: `[<code>ColumnWithPath</code>][ColumnWithPath]`<C>) -> `[<code>CellAttributes</code>][CellAttributes]`?`
         */
        typealias RowColFormatterDef = Nothing

        /**
         * `cellAttributes: `[<code>CellAttributes</code>][CellAttributes]
         */
        typealias CellAttributesDef = Nothing

        /**
         * `color: `[<code>RgbColor</code>][RgbColor]
         */
        typealias RgbColorDef = Nothing
    }
}

// endregion

// region DataFrame format

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
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
 * @param [columns] The [<code>columns-selector</code>][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> DataFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> = FormatClause(this, columns)

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> DataFrame<T>.format(vararg columns: String): FormatClause<T, Any?> = format { columns.toColumnSet() }

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [<code>columns-selector</code>][ColumnsSelector] or by [<code>column names</code>][String].
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
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
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
 * @param [columns] The [<code>columns-selector</code>][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> FormattedFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> =
    FormatClause(df, columns, formatter, oldHeaderFormatter = headerFormatter)

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> FormattedFrame<T>.format(vararg columns: String): FormatClause<T, Any?> =
    format { columns.toColumnSet() }

/**
 * Formats the specified [columns] or cells within this dataframe such that
 * they have specific CSS attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] which serves as an intermediate step.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.FormatSelectingColumns].
 *
 * The [<code>FormatClause</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [<code>where</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.where],
 * and then finally specify how to format the cells using
 * [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.with], [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.perRowCol], or [<code>linearBg</code>][org.jetbrains.kotlinx.dataframe.api.FormatClause.linearBg].
 *
 * You can continue formatting the [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] by calling [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame.format] on it again.
 *
 * Specifying a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] makes all of its inner columns be formatted in the same way unless
 * overridden.
 *
 * Formatting is done additively, meaning you can add more formatting to a cell that's already formatted or
 * override certain attributes inherited from its outer group.
 *
 * Specifying a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] at the moment does nothing
 * ([Issue #1375](https://github.com/Kotlin/dataframe/issues/1375)),
 * convert each nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a [<code>FormattedFrame</code>][org.jetbrains.kotlinx.dataframe.api.FormattedFrame] instead:
 * ```kt
 * df.convert { myFrameCol }.with {
 *     it.format { someCol }.with { background(green) }
 * }.toStandaloneHtml()
 * ```
 *
 * Check out the [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 * ### This Format Overload
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [<code>columns-selector</code>][ColumnsSelector] or by [<code>column names</code>][String].
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
 * Filters the rows to format using a [<code>RowValueFilter</code>][RowValueFilter].
 *
 * See [<code>Row Condition</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows].
 *
 * You need to specify [<code>filter</code>][filter]: A lambda function expecting a `true` result for each
 * cell that should be included in the formatting selection.
 * Both the cell value (`it: `[<code>C</code>][C]) and its row (`this: `[<code>DataRow</code>][DataRow]`<`[<code>T</code>][T]`>`) are available.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>where</code>][where]:
 * ```kt
 * df.format { temperature }
 *   .where { it !in -10..40 }
 *   .with { background(red) }
 * ```
 *
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.where(filter: RowValueFilter<T, C>): FormatClause<T, C> =
    FormatClause(
        filter = this.filter and filter,
        df = df,
        columns = columns,
        oldFormatter = oldFormatter,
        oldHeaderFormatter = oldHeaderFormatter,
    )

/**
 * Only format the selected columns at given row indices.
 *
 * Accepts either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg `[<code>Int</code>][Int] indices.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]
 * ```kt
 * df.format()
 *   .at(df.indices().step(2).toList())
 *   .with { background(lightGray) }
 * ```
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(rowIndices: Collection<Int>): FormatClause<T, C> = where { index in rowIndices }

/**
 * Only format the selected columns at given row indices.
 *
 * Accepts either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg `[<code>Int</code>][Int] indices.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]
 * ```kt
 * df.format { colsOf<String?>() }
 *   .at(0, 3, 4)
 *   .with { background(lightGray) }
 * ```
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(vararg rowIndices: Int): FormatClause<T, C> = at(rowIndices.toSet())

/**
 * Only format the selected columns at given row indices.
 *
 * Accepts either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg `[<code>Int</code>][Int] indices.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]
 * ```kt
 * df.format { cols(2..7) }
 *   .at(2..7)
 *   .with { background(lightGray) }
 * ```
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.at(rowRange: IntRange): FormatClause<T, C> = where { index in rowRange }

/**
 * Filters the format-selection to only include cells where the value is not null.
 *
 * This is shorthand for `.`[<code>where</code>][FormatClause.where]` { it != null }`.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>notNull</code>][notNull]:
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
 * Creates a new [<code>FormattedFrame</code>][FormattedFrame] that uses the specified [<code>RowColFormatter</code>][RowColFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [<code>formatter</code>][formatter]: A lambda function expecting a [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of
 * [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>T</code>][T]`>` and [<code>ColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath]`<`[<code>C</code>][C]`>`.
 *
 * This is similar to a [<code>RowColumnExpression</code>][org.jetbrains.kotlinx.dataframe.RowColumnExpression], except that you also have access
 * to the [<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[<code>white</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>textColor</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[<code>black</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>bold</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [<code>attr</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>perRowCol</code>][perRowCol]:
 * ```kt
 * df.format { colsOf<Int>() }.perRowCol { row, col ->
 *     linearBg(col[row], col.min() to red, col.max() to green)
 * }
 * ```
 *
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C>.perRowCol(formatter: RowColFormatter<T, C>): FormattedFrame<T> =
    formatImpl(formatter)

/**
 * Creates a new [<code>FormattedFrame</code>][FormattedFrame] that uses the specified [<code>CellFormatter</code>][CellFormatter] to format the selected cells of the dataframe.
 *
 * You need to specify [<code>formatter</code>][formatter]: A lambda function expecting a [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of a cell: [<code>C</code>][C] of the dataframe.
 *
 * You have access to the [<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[<code>white</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>textColor</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[<code>black</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>bold</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [<code>attr</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>with</code>][with]:
 * ```kt
 * df.format()
 *   .at(df.indices().step(2).toList())
 *   .with { background(lightGray) and bold and textColor(black) }
 * ```
 *
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C>.with(formatter: CellFormatter<C>): FormattedFrame<T> =
    formatImpl { row, col -> formatter(col[row] as C) }

/**
 * Creates a new [<code>FormattedFrame</code>][FormattedFrame] that uses the specified [<code>CellFormatter</code>][CellFormatter] to format selected non-null cells of the dataframe.
 *
 * This function is shorthand for `.`[<code>notNull()</code>][FormatClause.notNull]`.`[<code>with { }</code>][FormatClause.with].
 *
 * You need to specify [<code>formatter</code>][formatter]: A lambda function expecting a [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] or `null` given an instance of a cell: [<code>C</code>][C] of the dataframe.
 *
 * You have access to the [<code>FormattingDsl</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[<code>white</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>textColor</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[<code>black</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>bold</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [<code>attr</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * ### Examples using [<code>notNull</code>][notNull]:
 * ```kt
 * df.format().notNull { bold and textColor(black) }
 * ```
 *
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
 */
public fun <T, C> FormatClause<T, C?>.notNull(formatter: CellFormatter<C>): FormattedFrame<T> =
    notNull().with(formatter)

/**
 * Creates a new [<code>FormattedFrame</code>][FormattedFrame] by just changing the background colors of the selected cells.
 *
 * The background color of each selected cell is calculated by interpolating between [<code>from</code>][from] and [<code>to</code>][to],
 * given the numeric value of that cell.
 * The interpolation is linear.
 *
 * If the numeric cell value falls outside the range [<code>from</code>][from]..[<code>to</code>][to], the colors at the bounds will be used.
 *
 * This function is shorthand for:
 *
 * `.`[<code>with</code>][FormatClause.with]`  {  `[<code>background</code>][FormattingDsl.background]`(`[<code>linear</code>][FormattingDsl.linear]`(it, `[<code>from</code>][from]`, `[<code>to</code>][to]`)) }`
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * See also [<code>with</code>][FormatClause.with], [<code>background</code>][FormattingDsl.background], and [<code>linear</code>][FormattingDsl.linear].
 *
 * ### Examples using [<code>linearBg</code>][linearBg]:
 * ```kt
 * df.format { temperature }.linearBg(-20 to FormattingDsl.blue, 50 to FormattingDsl.red)
 *   .format { age }.notNull().perRowCol { row, col ->
 *     textColor(
 *       linear(col[row], col.min() to green, col.max() to red)
 *     )
 *   }.toStandaloneHtml().openInBrowser()
 * ```
 *
 * Check out the full [<code>Grammar</code>][FormatDocs.Grammar].
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
 * To be used in the [<code>DataFrame.format</code>][DataFrame.format]; [<code>FormattingDsl</code>][FormattingDsl].
 *
 * Any color can be represented in terms of [<code>r</code>][r] (red), [<code>g</code>][g] (green), and [<code>b</code>][b] (blue) values from `0..255`.
 *
 * Inside [<code>FormattingDsl</code>][FormattingDsl], there are shortcuts for common colors, like [<code>white</code>][FormattingDsl.white],
 * [<code>green</code>][FormattingDsl.green], and [<code>gray</code>][FormattingDsl.gray].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
public data class RgbColor(val r: Short, val g: Short, val b: Short) {

    /** Encodes the color as a [<code>String</code>][String] such that it can be used as the value of an attribute in CSS. */
    override fun toString(): String = encode()
}

/**
 * This represents a collection of CSS cell attributes that can be applied to a cell in an HTML-rendered dataframe.
 *
 * [<code>Cell attributes</code>][CellAttributes] are created inside the [<code>FormattingDsl</code>][FormattingDsl] by calling
 * [<code>FormatClause.with</code>][FormatClause.with] or [<code>FormatClause.perRowCol</code>][FormatClause.perRowCol].
 *
 * Multiple attributes can be combined using the [<code>and</code>][and] operator.
 *
 * For instance:
 *
 * `df.`[<code>format()</code>][DataFrame.format]`.`[<code>`with {`</code>][FormatClause.with]` `[<code>background</code>][FormattingDsl.background]`(`[<code>white</code>][FormattingDsl.white]`) `[<code>and</code>][CellAttributes.and]` `[<code>textColor</code>][FormattingDsl.textColor]`(`[<code>black</code>][FormattingDsl.black]`) `[<code>`}`</code>][FormatClause.with]
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * @see [CellAttributes.and]
 */
public interface CellAttributes {

    /**
     * Retrieves all CSS cell attributes as a list of name-value pairs.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
    public fun attributes(): List<Pair<String, String>>
}

/**
 * Combines two [<code>CellAttributes</code>][CellAttributes] instances into a new one that combines their attributes.
 *
 * For instance:
 *
 * `df.`[<code>format()</code>][DataFrame.format]`.`[<code>`with {`</code>][FormatClause.with]` `[<code>background</code>][FormattingDsl.background]`(`[<code>white</code>][FormattingDsl.white]`) `[<code>and</code>][CellAttributes.and]` `[<code>textColor</code>][FormattingDsl.textColor]`(`[<code>black</code>][FormattingDsl.black]`) `[<code>`}`</code>][FormatClause.with]
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
public infix fun CellAttributes?.and(other: CellAttributes?): CellAttributes? =
    when {
        other == null -> this
        this == null -> other
        else -> MergedAttributes(listOf(this, other))
    }

/**
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][FormattingDsl.background]`(`[<code>white</code>][FormattingDsl.white]`) `[<code>and</code>][CellAttributes.and]` `
 * [<code>textColor</code>][FormattingDsl.textColor]`(`[<code>black</code>][FormattingDsl.black]`) `[<code>and</code>][CellAttributes.and]` `
 * [<code>bold</code>][FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][FormattingDsl.linear].
 *
 * Use [<code>attr</code>][attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
public object FormattingDsl {

    /**
     * Creates a new [<code>RgbColor</code>][RgbColor] instance with [<code>r</code>][r] (red), [<code>g</code>][g] (green), and [<code>b</code>][b] (blue) values from `0..255`.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
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
     * A custom [<code>cell attribute</code>][CellAttributes]
     * that allows you to specify any custom CSS attribute by [<code>name</code>][name] and [<code>value</code>][value].
     *
     * For example:
     * ```kt
     * attr("text-align", "center")
     * attr("border", "3px solid green")
     * ```
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
    public fun attr(name: String, value: String): CellAttributes = SingleAttribute(name, value)

    /**
     * A [<code>cell attribute</code>][CellAttributes] that sets the background color of a cell.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @param color Either one of the predefined colors, like [<code>black</code>][black], or [<code>green</code>][green], or a custom color using [<code>rgb()</code>][rgb].
     */
    public fun background(color: RgbColor): CellAttributes = attr("background-color", color.toString())

    /**
     * A [<code>cell attribute</code>][CellAttributes] that sets the background color of a cell.
     * A shortcut for [<code>background</code>][background]`(`[<code>rgb(...)</code>][rgb]`)`.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @see [rgb]
     */
    public fun background(r: Short, g: Short, b: Short): CellAttributes = background(RgbColor(r, g, b))

    /**
     * A [<code>cell attribute</code>][CellAttributes] that sets the text color of a cell.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @param color Either one of the predefined colors, like [<code>black</code>][black], or [<code>green</code>][green], or a custom color using [<code>rgb()</code>][rgb].
     */
    public fun textColor(color: RgbColor): CellAttributes = attr("color", color.toString())

    /**
     * A [<code>cell attribute</code>][CellAttributes] that sets the text color of a cell.
     * A shortcut for [<code>textColor</code>][textColor]`(`[<code>rgb(...)</code>][rgb]`)`.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @see [rgb]
     */
    public fun textColor(r: Short, g: Short, b: Short): CellAttributes = textColor(RgbColor(r, g, b))

    /**
     * A [<code>cell attribute</code>][CellAttributes] that makes the text inside the cell *italic*.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
    public val italic: CellAttributes = attr("font-style", "italic")

    /**
     * A [<code>cell attribute</code>][CellAttributes] that makes the text inside the cell **bold**.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
    public val bold: CellAttributes = attr("font-weight", "bold")

    /**
     * A [<code>cell attribute</code>][CellAttributes] that u͟n͟d͟e͟r͟l͟i͟n͟e͟s͟ the text inside the cell.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     */
    public val underline: CellAttributes = attr("text-decoration", "underline")

    /**
     * Shorthand for [<code>background</code>][background]`(`[<code>linear</code>][linear]`(...))`
     *
     * Creates a [<code>cell attribute</code>][CellAttributes] that applies a background color calculated
     * by interpolating between [<code>from</code>][from] and [<code>to</code>][to], given [<code>value</code>][value].
     *
     * See [<code>linear</code>][linear] for more information.
     *
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @see linear
     * @see background
     */
    public fun linearBg(value: Number, from: Pair<Number, RgbColor>, to: Pair<Number, RgbColor>): CellAttributes =
        background(
            linear(value, from, to),
        )

    /**
     * Calculates an [<code>RgbColor</code>][RgbColor] by interpolating between [<code>from</code>][from] and [<code>to</code>][to], given [<code>value</code>][value].
     * The interpolation is linear.
     * If [<code>value</code>][value] falls outside the range [<code>from</code>][from]..[<code>to</code>][to], the colors at the bounds will be used.
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
     * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
     *
     * @param [value] The value to interpolate the color for.
     * @param [from] The lower bound of the interpolation range and the color that will be returned when [<code>value</code>][value] touches this bound.
     * @param [to] The upper bound of the interpolation range and the color that will be returned when [<code>value</code>][value] touches this bound.
     * @return An [<code>RgbColor</code>][RgbColor] that corresponds to the interpolation.
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
 * A lambda function expecting a [<code>CellAttributes</code>][CellAttributes] or `null` given an instance of
 * [<code>DataRow</code>][DataRow]`<`[<code>T</code>][T]`>` and [<code>ColumnWithPath</code>][ColumnWithPath]`<`[<code>C</code>][C]`>`.
 *
 * This is similar to a [<code>RowColumnExpression</code>][RowColumnExpression], except that you also have access
 * to the [<code>FormattingDsl</code>][FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[<code>white</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>textColor</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[<code>black</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>bold</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [<code>attr</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
public typealias RowColFormatter<T, C> = FormattingDsl.(row: DataRow<T>, col: ColumnWithPath<C>) -> CellAttributes?

/**
 * A lambda function expecting a [<code>CellAttributes</code>][CellAttributes] or `null` given an instance of a cell: [<code>C</code>][C] of the dataframe.
 *
 * You have access to the [<code>FormattingDsl</code>][FormattingDsl] in the context.
 *
 * The formatting DSL allows you to create and combine [<code>CellAttributes</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes] to apply to one
 * or multiple cells of a dataframe such that they have specific CSS attributes applied to them
 * when rendered to HTML.
 *
 * For instance, to specify black, bold text on a white background, you could write:
 *
 * [<code>background</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.background]`(`[<code>white</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.white]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>textColor</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.textColor]`(`[<code>black</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.black]`) `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.CellAttributes.and]` `
 * [<code>bold</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.bold]
 *
 * It's also possible to define your own colors using [<code>rgb</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb] or interpolate
 * colors using [<code>linear</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.linear].
 *
 * Use [<code>attr</code>][org.jetbrains.kotlinx.dataframe.api.FormattingDsl.attr] if you want to specify a custom CSS attribute.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 */
public typealias CellFormatter<C> = FormattingDsl.(cell: C) -> CellAttributes?

/**
 * A wrapper around a [<code>DataFrame</code>][df] with CSS attributes that can be
 * converted to a formatted HTML table in the form of [<code>DataFrameHtmlData</code>][DataFrameHtmlData].
 *
 * Call [<code>toHtml</code>][toHtml] or [<code>toStandaloneHtml</code>][toStandaloneHtml] to get the HTML representation of the [<code>DataFrame</code>][DataFrame].
 *
 * In Jupyter kernel (Kotlin Notebook) environments, you can often output this class directly.
 * Use [<code>toHtml</code>][toHtml] or [<code>toStandaloneHtml</code>][toStandaloneHtml] when this produces unexpected results.
 *
 * You can apply further formatting to this [<code>FormattedFrame</code>][FormattedFrame] by calling [<code>format()</code>][FormattedFrame.format] once again.
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html) [See `toHtml` on the documentation website.](https://kotlin.github.io/dataframe/tohtml.html)
 */
public class FormattedFrame<T>(
    internal val df: DataFrame<T>,
    internal val formatter: RowColFormatter<T, *>? = null,
    internal val headerFormatter: HeaderColFormatter<*>? = null,
) {

    /**
     * Returns a [<code>DataFrameHtmlData</code>][DataFrameHtmlData] without additional definitions.
     * Can be rendered in Jupyter kernel (Kotlin Notebook) environments or other environments that already have
     * CSS- and script definitions for DataFrame.
     *
     * Use [<code>toStandaloneHtml</code>][toStandaloneHtml] if you need the [<code>DataFrameHtmlData</code>][DataFrameHtmlData] to include CSS- and script definitions.
     *
     * By default, cell content is formatted as text
     * Use [<code>RenderedContent.media</code>][media] or [<code>IMG</code>][IMG], [<code>IFRAME</code>][IFRAME] if you need custom HTML inside a cell.
     *
     * For more information: [See `toHtml` on the documentation website.](https://kotlin.github.io/dataframe/tohtml.html)
     *
     * @param [configuration] The [<code>DisplayConfiguration</code>][DisplayConfiguration] to use as a base for this [<code>FormattedFrame</code>][FormattedFrame].
     *   Default: [<code>DisplayConfiguration.DEFAULT</code>][DisplayConfiguration.DEFAULT].
     * @param [cellRenderer] Mostly for internal usage, use [<code>DefaultCellRenderer</code>][DefaultCellRenderer] if unsure.
     * @param [getFooter] Allows you to specify how to render the footer text beneath the dataframe.
     *   Default: `"DataFrame [rows x cols]"`
     * @see toStandaloneHtml
     */
    public fun toHtml(
        configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
        cellRenderer: CellRenderer = DefaultCellRenderer,
        getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
    ): DataFrameHtmlData = df.toHtml(getDisplayConfiguration(configuration), cellRenderer, getFooter)

    /**
     * Returns a [<code>DataFrameHtmlData</code>][DataFrameHtmlData] with CSS- and script definitions for DataFrame.
     *
     * Use [<code>toHtml</code>][toHtml] if you don't need the [<code>DataFrameHtmlData</code>][DataFrameHtmlData] to include CSS- and script definitions.
     *
     * The [<code>DataFrameHtmlData</code>][DataFrameHtmlData] can be saved as an *.html file and displayed in the browser.
     * If you save it as a file and find it in the project tree,
     * the ["Open in browser"](https://www.jetbrains.com/help/idea/editing-html-files.html#ws_html_preview_output_procedure)
     * feature of IntelliJ IDEA will automatically reload the file content when it's updated.
     *
     * By default, cell content is formatted as text
     * Use [<code>RenderedContent.media</code>][media] or [<code>IMG</code>][IMG], [<code>IFRAME</code>][IFRAME] if you need custom HTML inside a cell.
     *
     * __NOTE:__ In Kotlin Notebook, output [<code>FormattedFrame</code>][FormattedFrame] directly, or use [<code>toHtml</code>][toHtml],
     * as that environment already has CSS- and script definitions for DataFrame.
     * Using [<code>toStandaloneHtml</code>][toStandaloneHtml] might produce unexpected results.
     *
     * For more information: [See `toHtml` on the documentation website.](https://kotlin.github.io/dataframe/tohtml.html)
     *
     * @param [configuration] The [<code>DisplayConfiguration</code>][DisplayConfiguration] to use as a base for this [<code>FormattedFrame</code>][FormattedFrame].
     *   Default: [<code>DisplayConfiguration.DEFAULT</code>][DisplayConfiguration.DEFAULT].
     * @param [cellRenderer] Mostly for internal usage, use [<code>DefaultCellRenderer</code>][DefaultCellRenderer] if unsure.
     * @param [getFooter] Allows you to specify how to render the footer text beneath the dataframe.
     *   Default: `"DataFrame [rows x cols]"`
     * @see toHtml
     */
    public fun toStandaloneHtml(
        configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
        cellRenderer: CellRenderer = DefaultCellRenderer,
        getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
    ): DataFrameHtmlData = df.toStandaloneHtml(getDisplayConfiguration(configuration), cellRenderer, getFooter)

    /**
     * Applies this formatter to the given [<code>configuration</code>][configuration] and returns a new instance.
     *
     * For more information: [See `toHtml` on the documentation website.](https://kotlin.github.io/dataframe/tohtml.html)
     */
    @Suppress("UNCHECKED_CAST")
    public fun getDisplayConfiguration(configuration: DisplayConfiguration): DisplayConfiguration =
        configuration.copy(
            cellFormatter = formatter as RowColFormatter<*, *>?,
            headerFormatter = headerFormatter,
        )
}

/**
 * An intermediate class used in the [<code>format</code>][format] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * how to format the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [<code>FormattedFrame</code>][FormattedFrame]; a [<code>DataFrame</code>][DataFrame] with HTML formatting data.
 *
 * Use the following function to filter the rows to format:
 * - [<code>where</code>][FormatClause.where] – filters the rows to format using a [<code>RowValueFilter</code>][RowValueFilter].
 * - [<code>at</code>][FormatClause.at] – Only format in rows with certain indices.
 * - [<code>notNull</code>][FormatClause.notNull] – Only format cells that have non-null values.
 *
 * Use the following functions to finalize this formatting round:
 * - [<code>with</code>][FormatClause.with] – Specifies how to format the cells using a [<code>CellFormatter</code>][CellFormatter].
 * - [<code>perRowCol</code>][FormatClause.perRowCol] – Specifies how to format each cell individually using a [<code>RowColFormatter</code>][RowColFormatter].
 * - [<code>linearBg</code>][FormatClause.linearBg] –
 *   Interpolates between two colors to set the background color of each numeric cell based on its value.
 *   Shorthand for `.`[<code>with</code>][FormatClause.with]`  {  `[<code>background</code>][FormattingDsl.background]`(`[<code>linear</code>][FormattingDsl.linear]`(it, from, to)) }`
 * - [<code>notNull</code>][FormatClause.notNull] – Specifies how to format non-null cells using a [<code>CellFormatter</code>][CellFormatter].
 *   Shorthand for `.`[<code>notNull()</code>][FormatClause.notNull]`.`[<code>with { }</code>][FormatClause.with].
 *
 * For more information: [See `format` on the documentation website.](https://kotlin.github.io/dataframe/format.html)
 *
 * See [<code>Grammar</code>][FormatDocs.Grammar] for more details.
 */
public class FormatClause<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C> = { all().cast() },
    internal val oldFormatter: RowColFormatter<T, C>? = null,
    internal val filter: RowValueFilter<T, C> = { true },
    internal val oldHeaderFormatter: HeaderColFormatter<*>? = null,
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
