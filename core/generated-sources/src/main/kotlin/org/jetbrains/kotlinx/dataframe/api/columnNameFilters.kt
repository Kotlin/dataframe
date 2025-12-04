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
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## (Cols) Name (Contains / StartsWith / EndsWith) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColumnNameFiltersColumnsSelectionDsl {

    /**
     * ## (Cols) Name (Contains / StartsWith / EndsWith) Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `text: `[`String`][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `ignoreCase: `[`Boolean`][Boolean]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `regex: `[`Regex`][Regex]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`nameContains`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[`regex`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  `| `__`name`__`(`[**`Starts`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[**`Ends`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`nameContains`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[`regex`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.name`__`(`[**`Starts`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[**`Ends`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsNameContains`**][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]**`(`**[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[`regex`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.colsName`__`(`[**`Starts`**][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`|`[**`Ends`**][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameEndsWith]`)`**`With`**__`(`__[`text`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[`ignoreCase`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
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

        /** [**`nameContains`**][ColumnsSelectionDsl.nameContains] */
        public interface PlainDslNameContains

        /** __`name`__`(`[**`Starts`**][ColumnsSelectionDsl.nameStartsWith]`|`[**`Ends`**][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public interface PlainDslNameStartsEndsWith

        /** __`.`__[**`nameContains`**][ColumnsSelectionDsl.nameContains] */
        public interface ColumnSetNameContains

        /** __`.name`__`(`[**`Starts`**][ColumnsSelectionDsl.nameStartsWith]`|`[**`Ends`**][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public interface ColumnSetNameStartsEndsWith

        /**__`.`__[**`colsNameContains`**][ColumnsSelectionDsl.colsNameContains] */
        public interface ColumnGroupNameContains

        /** __`.colsName`__`(`[**`Starts`**][ColumnsSelectionDsl.colsNameStartsWith]`|`[**`Ends`**][ColumnsSelectionDsl.colsNameEndsWith]`)`**`With`** */
        public interface ColumnGroupNameStartsWith
    }

    // region nameContains

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my", ignoreCase = true) }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[nameContains][ColumnsSelectionDsl.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun String.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [text] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun ColumnPath.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    private interface NameContainsRegexDocs

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): ColumnSet<*> =
        asSingleColumn().colsNameContains(regex)

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun SingleColumn<DataRow<*>>.colsNameContains(regex: Regex): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     * @see [nameEndsWith]
     * @see [nameStartsWith]
     *
     */
    public fun String.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] having
     * [regex] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  {  `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Interpretable("NameStartsWith1")
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameStartsWith(prefix, ignoreCase)

    /**
     * ## (Cols) Name Starts With
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun String.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * ## (Cols) Name Starts With
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameStartsWith][KProperty.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * starting with [prefix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameStartsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameStartsWith][ColumnPath.colsNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Interpretable("NameEndsWith1")
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameEndsWith(suffix, ignoreCase)

    /**
     * ## (Cols) Name Ends With
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun String.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * ## (Cols) Name Ends With
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameEndsWith][KProperty.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this]
     * ending with [suffix] in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function is named `colsNameEndsWith` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameEndsWith][ColumnPath.colsNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @param [ignoreCase] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    // endregion
}

// endregion
