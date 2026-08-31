package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Column Groups [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColGroupsColumnsSelectionDsl {

    /**
     * ## Column Groups Grammar
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
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
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
     *  [<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**`colGroups`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`colGroups`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colGroups`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][colGroups] can be called using any of the supported [<code>APIs</code>][AccessApis] (+ [<code>ColumnPath</code>][ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [<code>predicate</code>][Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][ColumnSet] of [<code>ColumnGroups</code>][ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    private interface CommonColGroupsDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") }.`[<code>colGroups</code>][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups0")
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups1")
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>colGroups</code>][SingleColumn.colGroups]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>colGroups</code>][SingleColumn.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    @Interpretable("ColGroups2")
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][String.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][String.colGroups]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroup</code>][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>colGroups</code>][SingleColumn.colGroups]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColGroup.`[<code>colGroups</code>][KProperty.colGroups]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun KProperty<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns from [this] that are [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colGroups</code>][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>colGroups</code>][ColumnPath.colGroups]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): ColumnSet<DataRow<*>> =
        columnGroup(this).colGroups(filter)
}

/**
 * Returns a ColumnSet containing the column groups that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column group. Must accept a ColumnGroup object and return a Boolean.
 * @return A [<code>ColumnSet</code>][ColumnSet] containing the column groups that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal inline fun ColumnsResolver<*>.columnGroupsInternal(
    crossinline filter: (ColumnGroup<*>) -> Boolean,
): ColumnSet<DataRow<*>> = colsInternal { it.isColumnGroup() && filter(it) }.cast()

// endregion
