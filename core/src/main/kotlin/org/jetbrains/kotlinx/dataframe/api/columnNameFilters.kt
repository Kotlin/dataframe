package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
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
     * {@set [DslGrammarTemplate.DefinitionsArg]
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
     * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  `|` {@include [PlainDslNameStartsEndsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColumnSetNameStartsEndsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColumnGroupNameStartsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     * }
     */
    public interface Grammar {

        /** [**nameContains**][ColumnsSelectionDsl.nameContains] */
        public interface PlainDslNameContains

        /** {@comment newline because of rendering issue.}
         * **name**`(`[**Starts**][ColumnsSelectionDsl.nameStartsWith]`|`[**Ends**][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public interface PlainDslNameStartsEndsWith

        /** .[**nameContains**][ColumnsSelectionDsl.nameContains] */
        public interface ColumnSetNameContains

        /** .**name**`(`[**Starts**][ColumnsSelectionDsl.nameStartsWith]`|`[**Ends**][ColumnsSelectionDsl.nameEndsWith]`)`**`With`** */
        public interface ColumnSetNameStartsEndsWith

        /** .[**colsNameContains**][ColumnsSelectionDsl.colsNameContains] */
        public interface ColumnGroupNameContains

        /** .**colsName**`(`[**Starts**][ColumnsSelectionDsl.colsNameStartsWith]`|`[**Ends**][ColumnsSelectionDsl.colsNameEndsWith]`)`**`With`** */
        public interface ColumnGroupNameStartsWith
    }

    // region nameContains

    /**
     * ## (Cols) Name Contains
     * Returns a [ColumnSet] containing all columns from [this\] having
     * {@get [CommonNameContainsDocs.ArgumentArg]} in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `nameContains` is named `colsNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { `{@get [ArgumentArg]}{@get [ArgumentArg]}` `[in][String.contains]` it.`[name][DataColumn.name]` }`.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.colsNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.colsNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][DataFrame.select]` { Type::someGroupCol.`[nameContains][SingleColumn.colsNameContains]`("my", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * {@get [ExampleArg]}
     *
     * @param {@get [ArgumentArg]} what the column name should contain to be included in the result.
     * {@get [ExtraParamsArg]}
     * @return A [ColumnSet] containing
     *   all columns containing {@get [CommonNameContainsDocs.ArgumentArg]} in their name.
     * @see [nameEndsWith\]
     * @see [nameStartsWith\]
     * {@set [ExtraParamsArg]}
     */
    private interface CommonNameContainsDocs {

        /* Example to give */
        interface ExampleArg

        /* [text\] or [regex\] */
        interface ArgumentArg

        /* Optional extra params. */
        interface ExtraParamsArg
    }

    /**
     * @include [CommonNameContainsDocs]
     * @set [CommonNameContainsDocs.ArgumentArg] [text\]
     * {@set [CommonNameContainsDocs.ExtraParamsArg]
     *  @param [ignoreCase\] `true` to ignore character case when comparing strings. By default `false`.
     * }
     */
    private interface NameContainsTextDocs

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my", ignoreCase = true) }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<C> =
        colsInternal { it.name.contains(text, ignoreCase) } as TransformableColumnSet<C>

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.colsNameContains]`("my") }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        asSingleColumn().colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`("my") }`
     */
    public fun SingleColumn<DataRow<*>>.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(text, ignoreCase) }

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`("my") }`
     */
    public fun String.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`("my") }`
     */
    public fun KProperty<*>.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [NameContainsTextDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`("my") }`
     */
    public fun ColumnPath.colsNameContains(
        text: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(text, ignoreCase)

    /**
     * @include [CommonNameContainsDocs]
     * @set [CommonNameContainsDocs.ArgumentArg] [regex\] */
    private interface NameContainsRegexDocs

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): TransformableColumnSet<C> =
        colsInternal { it.name.contains(regex) } as TransformableColumnSet<C>

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): TransformableColumnSet<*> =
        asSingleColumn().colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameContains][SingleColumn.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun SingleColumn<DataRow<*>>.colsNameContains(regex: Regex): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameContains][String.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun String.colsNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameContains][KProperty.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun KProperty<*>.colsNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @set [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameContains][ColumnPath.colsNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnPath.colsNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).colsNameContains(regex)

    // endregion

    /**
     * ## (Cols) Name {@get [CommonNameStartsEndsDocs.CapitalTitleArg]} With
     * Returns a [ColumnSet] containing all columns from [this\]
     * {@get [CommonNameStartsEndsDocs.NounArg]} with {@get [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], the function is named `{@get [CommonNameStartsEndsDocs.ColsNameOperationNameArg]}` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { it.`[name][DataColumn.name]`.`[{@get [OperationNameArg]}][String.{@get [OperationNameArg]}]`(`{@get [ArgumentArg]}{@get [ArgumentArg]}`) }`.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@get [NameOperationNameArg]}][ColumnsSelectionDsl.{@get [NameOperationNameArg]}]`("order") }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[{@get [ColsNameOperationNameArg]}][String.{@get [ColsNameOperationNameArg]}]`("b") }`
     *
     * `df.`[select][DataFrame.select]` { Type::someGroupCol.`[{@get [ColsNameOperationNameArg]}][SingleColumn.{@get [ColsNameOperationNameArg]}]`("a", ignoreCase = true) }`
     *
     * #### Examples for this overload:
     *
     * {@get [ExampleArg]}
     *
     * @param {@get [ArgumentArg]} Columns {@get [CommonNameStartsEndsDocs.NounArg]} with this {@get [CommonNameStartsEndsDocs.ArgumentArg]} in their name will be returned.
     * @param [ignoreCase\] `true` to ignore character case when comparing strings. By default `false`.
     *
     * @return A [ColumnSet] containing
     *   all columns {@get [CommonNameStartsEndsDocs.NounArg]} with {@get [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameStartsEndsDocs {

        /* "Starts" or "Ends" */
        interface CapitalTitleArg

        /* "starting" or "ending" */
        interface NounArg

        /* "startsWith" or "endsWith" */
        interface OperationNameArg

        /* "nameStartsWith" or "nameEndsWith" */
        interface NameOperationNameArg

        /* "colsNameStartsWith" or "colsNameEndsWith" */
        interface ColsNameOperationNameArg

        /* [prefix\] or [suffix\] */
        interface ArgumentArg

        interface ExampleArg
    }

    // region nameStartsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @set [CommonNameStartsEndsDocs.CapitalTitleArg] Starts
     * @set [CommonNameStartsEndsDocs.NounArg] starting
     * @set [CommonNameStartsEndsDocs.OperationNameArg] startsWith
     * @set [CommonNameStartsEndsDocs.NameOperationNameArg] nameStartsWith
     * @set [CommonNameStartsEndsDocs.ColsNameOperationNameArg] colsNameStartsWith
     * @set [CommonNameStartsEndsDocs.ArgumentArg] [prefix\]
     *
     * @see [nameEndsWith\]
     * @see [nameContains\]
     */
    private interface CommonNameStartsWithDocs

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun <C> ColumnSet<C>.startsWith(prefix: CharSequence): TransformableColumnSet<C> =
        nameStartsWith(prefix)

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun ColumnsSelectionDsl<*>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        nameStartsWith(prefix)

    @Deprecated("Use colsNameStartsWith instead", ReplaceWith("this.colsNameStartsWith(prefix)"))
    public fun SingleColumn<DataRow<*>>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        colsNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix, ignoreCase) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     */
    public fun ColumnsSelectionDsl<*>.nameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        asSingleColumn().colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameStartsWith][SingleColumn.colsNameStartsWith]`("order-") }`
     */
    public fun SingleColumn<DataRow<*>>.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.startsWith(prefix, ignoreCase) }

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameStartsWith][String.colsNameStartsWith]`("order-") }`
     */
    public fun String.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameStartsWith][KProperty.colsNameStartsWith]`("order-") }`
     */
    public fun KProperty<*>.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    /**
     * @include [CommonNameStartsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameStartsWith][ColumnPath.colsNameStartsWith]`("order-") }`
     */
    public fun ColumnPath.colsNameStartsWith(
        prefix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameStartsWith(prefix, ignoreCase)

    // endregion

    // region nameEndsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @set [CommonNameStartsEndsDocs.CapitalTitleArg] Ends
     * @set [CommonNameStartsEndsDocs.NounArg] ending
     * @set [CommonNameStartsEndsDocs.OperationNameArg] endsWith
     * @set [CommonNameStartsEndsDocs.NameOperationNameArg] nameEndsWith
     * @set [CommonNameStartsEndsDocs.ColsNameOperationNameArg] colsNameEndsWith
     * @set [CommonNameStartsEndsDocs.ArgumentArg] [suffix\]
     *
     * @see [nameStartsWith\]
     * @see [nameContains\]
     */
    private interface CommonNameEndsWithDocs

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.endsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    public fun ColumnsSelectionDsl<*>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        nameEndsWith(suffix)

    @Deprecated("Use colsNameEndsWith instead", ReplaceWith("this.colsNameEndsWith(suffix)"))
    public fun SingleColumn<DataRow<*>>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix, ignoreCase) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     */
    public fun ColumnsSelectionDsl<*>.nameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        asSingleColumn().colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[colsNameEndsWith][SingleColumn.colsNameEndsWith]`("-order") }`
     */
    public fun SingleColumn<DataRow<*>>.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix, ignoreCase) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[colsNameEndsWith][String.colsNameEndsWith]`("-order") }`
     */
    public fun String.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[colsNameEndsWith][KProperty.colsNameEndsWith]`("-order") }`
     */
    public fun KProperty<*>.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    /**
     * @include [CommonNameEndsWithDocs]
     * @set [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[colsNameEndsWith][ColumnPath.colsNameEndsWith]`("-order") }`
     */
    public fun ColumnPath.colsNameEndsWith(
        suffix: CharSequence,
        ignoreCase: Boolean = false,
    ): TransformableColumnSet<*> =
        columnGroup(this).colsNameEndsWith(suffix, ignoreCase)

    // endregion
}

// endregion
