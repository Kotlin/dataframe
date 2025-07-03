package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Column Groups [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColGroupsColumnsSelectionDsl {

    /**
     * ## Column Groups Grammar
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
     *  [**`colGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [**`colGroups`**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** __`.`__[**`colGroups`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** __`.`__[**`colGroups`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [predicate][Predicate] to filter the column groups by.
     * @return A [ColumnSet] of [ColumnGroups][ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    private interface CommonColGroupsDocs {

        /** Example argument */
        interface EXAMPLE
    }

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups0")
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups1")
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups2")
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[colGroups][KProperty.colGroups]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun KProperty<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colGroups][ColumnPath.colGroups]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)
}

/**
 * Returns a ColumnSet containing the column groups that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column group. Must accept a ColumnGroup object and return a Boolean.
 * @return A [ColumnSet] containing the column groups that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal inline fun ColumnsResolver<*>.columnGroupsInternal(
    crossinline filter: (ColumnGroup<*>) -> Boolean,
): ColumnSet<AnyRow> = colsInternal { it.isColumnGroup() && filter(it) }.cast()

// endregion
