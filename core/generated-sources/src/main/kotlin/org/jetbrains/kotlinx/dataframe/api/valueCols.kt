package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
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
 * ## Value Columns [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ValueColsColumnsSelectionDsl {

    /**
     * ## Value Columns Grammar
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
     *  [<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**`valueCols`**</code>][ColumnsSelectionDsl.valueCols] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`valueCols`**</code>][ColumnsSelectionDsl.valueCols] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`valueCols`**</code>][ColumnsSelectionDsl.valueCols] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][valueCols] can be called using any of the supported [<code>APIs</code>][AccessApis] (+ [<code>ColumnPath</code>][ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [<code>predicate</code>][Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][ColumnSet] of [<code>ValueColumns</code>][ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    private interface CommonValueColsDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") }.`[<code>valueCols</code>][ColumnSet.valueCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("ValueCols0")
    public fun ColumnSet<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        valueColumnsInternal(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("ValueCols1")
    public fun ColumnsSelectionDsl<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        asSingleColumn().valueColumnsInternal(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>valueCols</code>][SingleColumn.valueCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>valueCols</code>][SingleColumn.valueCols]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("ValueCols2")
    public fun SingleColumn<DataRow<*>>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        this.ensureIsColumnGroup().valueColumnsInternal(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][String.valueCols]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][String.valueCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>valueCols</code>][KProperty.valueCols]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>valueCols</code>][KProperty.valueCols]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>valueCols</code>][KProperty.valueCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [<code>APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] (+ [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * For more information: [See `valueCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-columns-frame-columns-column-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>valueCols</code>][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>valueCols</code>][ColumnPath.valueCols]`() }`
     *
     * @param [filter] An optional [<code>predicate</code>][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [<code>ValueColumns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        columnGroup(this).valueCols(filter)
}

/**
 * Returns a TransformableColumnSet containing the value columns that satisfy the given filter.
 *
 * @param filter The filter function to apply on each value column. Must accept a ValueColumn object and return a Boolean.
 * @return A [<code>TransformableColumnSet</code>][TransformableColumnSet] containing the value columns that satisfy the filter.
 */
internal inline fun ColumnsResolver<*>.valueColumnsInternal(
    crossinline filter: (ValueColumn<*>) -> Boolean,
): TransformableColumnSet<*> = colsInternal { it.isValueColumn() && filter(it) }

// endregion
