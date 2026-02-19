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
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Value Columns [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ValueColsColumnsSelectionDsl {

    /**
     * ## Value Columns Grammar
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
     *  [**`valueCols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`valueCols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`valueCols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [ValueColumns][ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [filter] An optional [predicate][Predicate] to filter the value columns by.
     * @return A [ColumnSet] of [ValueColumns][ValueColumn].
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
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[valueCols][ColumnSet.valueCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
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
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
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
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
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
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.valueCols(filter: Predicate<ValueColumn<*>> = { true }): ColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[valueCols][KProperty.valueCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
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
     * Creates a subset of columns from [this] that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[valueCols][ColumnPath.valueCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
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
 * @return A [TransformableColumnSet] containing the value columns that satisfy the filter.
 */
internal inline fun ColumnsResolver<*>.valueColumnsInternal(
    crossinline filter: (ValueColumn<*>) -> Boolean,
): TransformableColumnSet<*> = colsInternal { it.isValueColumn() && filter(it) }

// endregion
