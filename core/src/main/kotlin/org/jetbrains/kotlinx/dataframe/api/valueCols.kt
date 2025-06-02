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
 * ## Value Columns {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ValueColsColumnsSelectionDsl {

    /**
     * ## Value Columns Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     */
    public interface Grammar {

        /** [**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public interface PlainDslName

        /** __`.`__[**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public interface ColumnSetName

        /** __`.`__[**`valueCols`**][ColumnsSelectionDsl.valueCols] */
        public interface ColumnGroupName
    }

    /**
     * ## Value Columns
     * Creates a subset of columns from [this\] that are [ValueColumns][ValueColumn].
     *
     * You can optionally use a [filter\] to only include certain columns.
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
     * {@get [CommonValueColsDocs.EXAMPLE]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the value columns by.
     * @return A [ColumnSet] of [ValueColumns][ValueColumn].
     * @see [ColumnsSelectionDsl.colsOfKind\]
     * @see [ColumnsSelectionDsl.frameCols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @see [ColumnsSelectionDsl.cols\]
     */
    private interface CommonValueColsDocs {

        /** Example argument */
        interface EXAMPLE
    }

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[valueCols][ColumnSet.valueCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    @Interpretable("ValueCols0")
    public fun ColumnSet<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        valueColumnsInternal(filter)

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCols][ColumnsSelectionDsl.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Interpretable("ValueCols1")
    public fun ColumnsSelectionDsl<*>.valueCols(
        filter: Predicate<ValueColumn<*>> = { true },
    ): TransformableColumnSet<*> = asSingleColumn().valueColumnsInternal(filter)

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Interpretable("ValueCols2")
    public fun SingleColumn<DataRow<*>>.valueCols(
        filter: Predicate<ValueColumn<*>> = { true },
    ): TransformableColumnSet<*> = this.ensureIsColumnGroup().valueColumnsInternal(filter)

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     */
    public fun String.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[valueCols][KProperty.valueCols]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * @include [CommonValueColsDocs]
     * @set [CommonValueColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[valueCols][ColumnPath.valueCols]`() }`
     */
    public fun ColumnPath.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
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
