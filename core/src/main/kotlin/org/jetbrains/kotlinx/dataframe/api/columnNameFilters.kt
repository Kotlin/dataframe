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
 * ## (Cols) Name (Contains / StartsWith / EndsWith) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColumnNameFiltersColumnsSelectionDsl {

    /**
     * ## (Cols) Name (Contains / StartsWith / EndsWith) Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.TextDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.IgnoreCaseDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.RegexDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  `| `{@include [PlainDslNameStartsEndsWith]}__`(`__{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`| `{@include [ColumnSetNameStartsEndsWith]}__`(`__{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`| `{@include [ColumnGroupNameStartsWith]}__`(`__{@include [DslGrammarTemplate.TextRef]}`[`**`, `**{@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
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
     * Returns a [ColumnSet] containing all columns from [this\] having
     * {@get [CommonNameContainsDocs.ARGUMENT]} in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]`  {  `{@get [ARGUMENT]}{@get [ARGUMENT]}` `[in][String.contains]` it.`[name][DataColumn.name]` }`.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[nameContains][SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][DataFrame.select]` { Type::someGroupCol.`[nameContains][SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * {@get [EXAMPLE]}
     *
     * @param {@get [ARGUMENT]} what the column name should contain to be included in the result.
     * {@get [EXTRA_PARAMS]}
     * @return A [ColumnSet] containing
     *   all columns containing {@get [CommonNameContainsDocs.ARGUMENT]} in their name.
     * @see [nameEndsWith\]
     * @see [nameStartsWith\]
     * {@set [EXTRA_PARAMS]}
     */
    @Suppress("ClassName")
    @ExcludeFromSources
    private interface CommonNameContainsDocs {

        // Example to give
        interface EXAMPLE

        // [text\] or [regex\]
        interface ARGUMENT

        // Optional extra params.
        interface EXTRA_PARAMS
    }

    /**
     * @include [CommonNameContainsDocs]
     * @set [CommonNameContainsDocs.ARGUMENT] [text\]
     * {@set [CommonNameContainsDocs.EXTRA_PARAMS]
     *  @param [ignoreCase\] `true` to ignore character case when comparing strings. By default `false`.
     * }
     */
    @ExcludeFromSources
    private interface NameContainsTextDocs

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my", ignoreCase = true) }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameContains0")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.contains(text, ignoreCase) }.cast()

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[nameContains][ColumnsSelectionDsl.colsNameContains]`("my") }`
     */
    @Interpretable("NameContains1")
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`("my") }`
     */
    @Interpretable("NameContains2")
    public fun SingleColumn<DataRow<*>>.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.contains(text, ignoreCase) }

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`("my") }`
     */
    public fun String.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`("my") }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`("my") }`
     */
    public fun ColumnPath.colsNameContains(text: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [CommonNameContainsDocs]
     * @set [CommonNameContainsDocs.ARGUMENT] [regex\] */
    private interface NameContainsRegexDocs

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): ColumnSet<C> =
        colsInternal { it.name.contains(regex) }.cast()

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): ColumnSet<*> =
        asSingleColumn().colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun SingleColumn<DataRow<*>>.colsNameContains(regex: Regex): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun String.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnPath.colsNameContains(regex: Regex): ColumnSet<*> = columnGroup(this).colsNameContains(regex)

    // endregion

    /**
     * ## (Cols) Name {@get [CommonNameStartsEndsDocs.CAPITAL_TITLE]} With
     * Returns a [ColumnSet] containing all columns from [this\]
     * {@get [CommonNameStartsEndsDocs.NOUN]} with {@get [CommonNameStartsEndsDocs.ARGUMENT]} in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], the function is named `{@get [CommonNameStartsEndsDocs.COLS_NAME_OPERATION_NAME]}` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { it.`[name][DataColumn.name]`.`[{@get [OPERATION_NAME]}][String.{@get [OPERATION_NAME]}]`(`{@get [ARGUMENT]}{@get [ARGUMENT]}`) }`.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[{@get [NAME_OPERATION_NAME]}][ColumnsSelectionDsl.{@get [NAME_OPERATION_NAME]}]`("order") }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[{@get [COLS_NAME_OPERATION_NAME]}][String.{@get [COLS_NAME_OPERATION_NAME]}]`("b") }`
     *
     * `df.`[select][DataFrame.select]` { Type::someGroupCol.`[{@get [COLS_NAME_OPERATION_NAME]}][SingleColumn.{@get [COLS_NAME_OPERATION_NAME]}]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * {@get [EXAMPLE]}
     *
     * @param {@get [ARGUMENT]} Columns {@get [CommonNameStartsEndsDocs.NOUN]} with this {@get [CommonNameStartsEndsDocs.ARGUMENT]} in their name will be returned.
     * @param [ignoreCase\] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet] containing
     *   all columns {@get [CommonNameStartsEndsDocs.NOUN]} with {@get [CommonNameStartsEndsDocs.ARGUMENT]} in their name.
     */
    @Suppress("ClassName")
    @ExcludeFromSources
    private interface CommonNameStartsEndsDocs {

        // "Starts" or "Ends"
        interface CAPITAL_TITLE

        // "starting" or "ending"
        interface NOUN

        // "startsWith" or "endsWith"
        interface OPERATION_NAME

        // "nameStartsWith" or "nameEndsWith"
        interface NAME_OPERATION_NAME

        // "colsNameStartsWith" or "colsNameEndsWith"
        interface COLS_NAME_OPERATION_NAME

        // [prefix\] or [suffix\]
        interface ARGUMENT

        interface EXAMPLE
    }

    // region nameStartsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @set [CommonNameStartsEndsDocs.CAPITAL_TITLE] Starts
     * @set [CommonNameStartsEndsDocs.NOUN] starting
     * @set [CommonNameStartsEndsDocs.OPERATION_NAME] startsWith
     * @set [CommonNameStartsEndsDocs.NAME_OPERATION_NAME] nameStartsWith
     * @set [CommonNameStartsEndsDocs.COLS_NAME_OPERATION_NAME] colsNameStartsWith
     * @set [CommonNameStartsEndsDocs.ARGUMENT] [prefix\]
     *
     * @see [nameEndsWith\]
     * @see [nameContains\]
     */
    @ExcludeFromSources
    private interface CommonNameStartsWithDocs

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameStartsWith0")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.startsWith(prefix, ignoreCase) }.cast()

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     */
    @Interpretable("NameStartsWith1")
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("order-") }`
     */
    @Interpretable("NameStartsWith2")
    public fun SingleColumn<DataRow<*>>.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.startsWith(prefix, ignoreCase) }

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("order-") }`
     */
    public fun String.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameStartsWith][KProperty.colsNameStartsWith]`("order-") }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameStartsWith][ColumnPath.colsNameStartsWith]`("order-") }`
     */
    public fun ColumnPath.colsNameStartsWith(prefix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    // endregion

    // region nameEndsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @set [CommonNameStartsEndsDocs.CAPITAL_TITLE] Ends
     * @set [CommonNameStartsEndsDocs.NOUN] ending
     * @set [CommonNameStartsEndsDocs.OPERATION_NAME] endsWith
     * @set [CommonNameStartsEndsDocs.NAME_OPERATION_NAME] nameEndsWith
     * @set [CommonNameStartsEndsDocs.COLS_NAME_OPERATION_NAME] colsNameEndsWith
     * @set [CommonNameStartsEndsDocs.ARGUMENT] [suffix\]
     *
     * @see [nameStartsWith\]
     * @see [nameContains\]
     */
    @ExcludeFromSources
    private interface CommonNameEndsWithDocs

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("NameEndsWith0")
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<C> =
        colsInternal { it.name.endsWith(suffix, ignoreCase) }.cast()

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     */
    @Interpretable("NameEndsWith1")
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        asSingleColumn().colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("-order") }`
     */
    @Interpretable("NameEndsWith2")
    public fun SingleColumn<DataRow<*>>.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): ColumnSet<*> = this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix, ignoreCase) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("-order") }`
     */
    public fun String.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameEndsWith][KProperty.colsNameEndsWith]`("-order") }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameEndsWith][ColumnPath.colsNameEndsWith]`("-order") }`
     */
    public fun ColumnPath.colsNameEndsWith(suffix: CharSequence, ignoreCase: Boolean = false): ColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    // endregion
}

// endregion
