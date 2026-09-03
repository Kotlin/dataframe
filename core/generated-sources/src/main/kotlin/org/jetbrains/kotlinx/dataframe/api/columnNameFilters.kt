package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## (Cols) Name (Contains / StartsWith / EndsWith) [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColumnNameFiltersColumnsSelectionDsl {

    /**
     * ## (Cols) Name (Contains / StartsWith / EndsWith) Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `text: `[<code>`String`</code>][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `ignoreCase: `[<code>`Boolean`</code>][Boolean]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `regex: `[<code>`Regex`</code>][Regex]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`nameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  `| `__`name`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`nameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.name`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsNameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.colsName`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [<code>**`nameContains`**</code>][ColumnsSelectionDsl.nameContains] */
        public typealias PlainDslNameContains = Nothing

        /** __`name`__`(`[<code>**`Starts`**</code>][ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public typealias PlainDslNameStartsEndsWith = Nothing

        /** __`.`__[<code>**`nameContains`**</code>][ColumnsSelectionDsl.nameContains] */
        public typealias ColumnSetNameContains = Nothing

        /** __`.name`__`(`[<code>**`Starts`**</code>][ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public typealias ColumnSetNameStartsEndsWith = Nothing

        /** __`.`__[<code>**`colsNameContains`**</code>][ColumnsSelectionDsl.colsNameContains] */
        public typealias ColumnGroupNameContains = Nothing

        /** __`.colsName`__`(`[<code>**`Starts`**</code>][ColumnsSelectionDsl.colsNameStartsWith]`|`[<code>**`Ends`**</code>][ColumnsSelectionDsl.colsNameEndsWith]`)`**`With`** */
        public typealias ColumnGroupNameStartsWith = Nothing
    }

    // region nameContains

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>nameContains</code>][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>nameContains</code>][ColumnSet.nameContains]`("my", ignoreCase = true) }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameContains0")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.contains(text, ignoreCase) }.cast()

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>nameContains</code>][ColumnsSelectionDsl.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Interpretable("NameContains1")
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { someGroupCol.`[<code>colsNameContains</code>][SingleColumn.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Interpretable("NameContains2")
    public fun SingleColumn<DataRow<*>>.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.contains(text, ignoreCase) }

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "someGroupCol".`[<code>colsNameContains</code>][String.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun String.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::someGroupCol.`[<code>colsNameContains</code>][KProperty.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["someGroupCol"].`[<code>colsNameContains</code>][ColumnPath.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun ColumnPath.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    private typealias NameContainsRegexDocs = Nothing

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>nameContains</code>][ColumnSet.nameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>nameContains</code>][ColumnSet.nameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): ColumnSet<C> =
        colsInternal { it.name.contains(regex) }.cast()

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>nameContains</code>][ColumnsSelectionDsl.nameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): ColumnSet<*> =
        asSingleColumn().colsNameContains(regex)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { someGroupCol.`[<code>colsNameContains</code>][SingleColumn.colsNameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun SingleColumn<DataRow<*>>.colsNameContains(regex: Regex): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "someGroupCol".`[<code>colsNameContains</code>][String.colsNameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun String.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::someGroupCol.`[<code>colsNameContains</code>][KProperty.colsNameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    /**
     * ## (Cols) Name Contains
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>nameContains</code>][kotlin.String.colsNameContains]`(`[<code>Regex</code>][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["someGroupCol"].`[<code>colsNameContains</code>][ColumnPath.colsNameContains]`(`[<code>Regex</code>][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun ColumnPath.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    // endregion

    // region nameStartsWith

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>nameStartsWith</code>][ColumnSet.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameStartsWith0")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.startsWith(prefix, ignoreCase) }.cast()

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Interpretable("NameStartsWith1")
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameStartsWith(prefix, ignoreCase)

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Interpretable("NameStartsWith2")
    public fun SingleColumn<DataRow<*>>.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.startsWith(prefix, ignoreCase) }

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun String.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::someGroupCol.`[<code>colsNameStartsWith</code>][KProperty.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * ## (Cols) Name Starts With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>startsWith</code>][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameStartsWith</code>][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameStartsWith</code>][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["someGroupCol"].`[<code>colsNameStartsWith</code>][ColumnPath.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    // endregion

    // region nameEndsWith

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>nameEndsWith</code>][ColumnSet.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameEndsWith0")
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.endsWith(suffix, ignoreCase) }.cast()

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Interpretable("NameEndsWith")
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameEndsWith(suffix, ignoreCase)

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Interpretable("NameEndsWith2")
    public fun SingleColumn<DataRow<*>>.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix, ignoreCase) }

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun String.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::someGroupCol.`[<code>colsNameEndsWith</code>][KProperty.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * ## (Cols) Name Ends With
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[<code>endsWith</code>][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * For more information: [See `Column Name Filters` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-name-filters)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>nameEndsWith</code>][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[<code>colsNameEndsWith</code>][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[<code>colsNameEndsWith</code>][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["someGroupCol"].`[<code>colsNameEndsWith</code>][ColumnPath.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    // endregion
}

// endregion
