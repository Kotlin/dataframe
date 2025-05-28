package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols Of Kind [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsOfKindColumnsSelectionDsl {

    /**
     * ## Cols Of Kind Grammar
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
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `kind: `[`ColumnKind`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind]
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
     *  [**`colsOfKind`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[`kind`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsOfKind`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[`kind`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsOfKind`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[`kind`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** __`.`__[**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** __`.`__[**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Group][ColumnKind.Group]`) }`
     *
     *  `df.`[select][DataFrame.select]` { "myColGroup".`[colsOfKind][String.colsOfKind]`(`[Frame][ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [predicate][ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][ColumnKind] of columns to include.
     * @return A [ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    private interface CommonColsOfKindDocs {

        /** Example argument */
        interface EXAMPLE
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colsOfKind][ColumnSet.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnSet<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnsSelectionDsl<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        asSingleColumn().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOfKind][KProperty.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOfKind][ColumnPath.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    // endregion
}

/**
 * Returns a TransformableColumnSet containing the columns of given kind(s) that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column. Must accept a ColumnWithPath object and return a Boolean.
 * @return A [TransformableColumnSet] containing the columns of given kinds that satisfy the filter.
 */
internal inline fun ColumnsResolver<*>.columnsOfKindInternal(
    kinds: Set<ColumnKind>,
    crossinline filter: ColumnFilter<*>,
): TransformableColumnSet<*> = colsInternal { it.kind() in kinds && filter(it) }

// endregion
