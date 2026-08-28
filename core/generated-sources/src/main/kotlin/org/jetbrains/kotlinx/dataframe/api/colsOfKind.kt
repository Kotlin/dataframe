package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols Of Kind [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColsOfKindColumnsSelectionDsl {

    /**
     * ## Cols Of Kind Grammar
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
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `kind: `[<code>`ColumnKind`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind]
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
     *  [<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**`colsOfKind`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`colsOfKind`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colsOfKind`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][colsOfKind] can be called using any of the supported [<code>APIs</code>][AccessApis] (+ [<code>ColumnPath</code>][ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOfKind</code>][ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOfKind</code>][ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][String.colsOfKind]`(`[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [<code>predicate</code>][ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    private interface CommonColsOfKindDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") }.`[<code>colsOfKind</code>][ColumnSet.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOfKind</code>][ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnSet<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> =
        columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOfKind</code>][ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnsSelectionDsl<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> =
        asSingleColumn().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOfKind</code>][SingleColumn.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> =
        this.ensureIsColumnGroup().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsOfKind</code>][SingleColumn.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>colsOfKind</code>][KProperty.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
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
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this] that are of the given kind(s).
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `colsOfKind` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of-kind)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Value</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOfKind</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[<code>Group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>colsOfKind</code>][kotlin.String.colsOfKind]`(`[<code>Frame</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>colsOfKind</code>][ColumnPath.colsOfKind]`(`[<code>Value</code>][ColumnKind.Value]`, `[<code>Frame</code>][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [<code>kind</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [<code>kinds</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    // endregion
}

/**
 * Returns a TransformableColumnSet containing the columns of given kind(s) that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column. Must accept a ColumnWithPath object and return a Boolean.
 * @return A [<code>TransformableColumnSet</code>][TransformableColumnSet] containing the columns of given kinds that satisfy the filter.
 */
internal inline fun ColumnsResolver<*>.columnsOfKindInternal(
    kinds: Set<ColumnKind>,
    crossinline filter: ColumnFilter<*>,
): TransformableColumnSet<*> = colsInternal { it.kind() in kinds && filter(it) }

// endregion
