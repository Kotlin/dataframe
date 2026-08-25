package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Frame Columns [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface FrameColsColumnsSelectionDsl {

    /**
     * ## Frame Cols Grammar
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
     *  [<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**`frameCols`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`frameCols`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`frameCols`**</code>][ColumnsSelectionDsl.colGroups] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][frameCols] can be called using any of the supported [<code>APIs</code>][AccessApis] (+ [<code>ColumnPath</code>][ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCols</code>][SingleColumn.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [<code>predicate</code>][Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][ColumnSet] of [<code>FrameColumns</code>][FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    private interface CommonFrameColsDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") }.`[<code>frameCols</code>][ColumnSet.frameCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCols</code>][ColumnsSelectionDsl.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("FrameCols0")
    public fun ColumnSet<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): ColumnSet<DataFrame<*>> =
        frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCols</code>][ColumnsSelectionDsl.frameCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCols</code>][ColumnsSelectionDsl.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("FrameCols1")
    public fun ColumnsSelectionDsl<*>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): ColumnSet<DataFrame<*>> = asSingleColumn().frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>frameCols</code>][SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>frameCols</code>][SingleColumn.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("FrameCols2")
    public fun SingleColumn<DataRow<*>>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): ColumnSet<DataFrame<*>> = this.ensureIsColumnGroup().frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][String.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][String.frameCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.frameCols(filter: Predicate<FrameColumn<*>> = { true }): ColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroup</code>][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>frameCols</code>][SingleColumn.frameCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColGroup.`[<code>frameCols</code>][SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColGroup.`[<code>frameCols</code>][KProperty.frameCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): ColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this] that are [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `frameCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>frameCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>frameCols</code>][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>frameCols</code>][ColumnPath.frameCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>FrameColumns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.frameCols(filter: Predicate<FrameColumn<*>> = { true }): ColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)
}

/**
 * Returns a TransformableColumnSet containing the frame columns that satisfy the given filter.
 *
 * @param filter The filter function to apply on each frame column. Must accept a FrameColumn object and return a Boolean.
 * @return A [<code>TransformableColumnSet</code>][TransformableColumnSet] containing the frame columns that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal inline fun ColumnsResolver<*>.frameColumnsInternal(
    crossinline filter: (FrameColumn<*>) -> Boolean,
): TransformableColumnSet<AnyFrame> =
    colsInternal { it.isFrameColumn() && filter(it.asFrameColumn()) } as TransformableColumnSet<AnyFrame>

// endregion
