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
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Frame Columns {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FrameColsColumnsSelectionDsl {

    /**
     * ## Frame Cols Grammar
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

        /** [**`frameCols`**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** __`.`__[**`frameCols`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** __`.`__[**`frameCols`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Frame Columns
     * Creates a subset of columns from [this\] that are [FrameColumns][FrameColumn].
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [frameCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonFrameColsDocs.EXAMPLE]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the frame columns by.
     * @return A [ColumnSet] of [FrameColumns][FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind\]
     * @see [ColumnsSelectionDsl.valueCols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @see [ColumnsSelectionDsl.cols\]
     */
    private interface CommonFrameColsDocs {

        /** Example argument */
        interface EXAMPLE
    }

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[frameCols][ColumnSet.frameCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCols][ColumnsSelectionDsl.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    @Interpretable("FrameCols0")
    public fun ColumnSet<*>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): TransformableColumnSet<DataFrame<*>> = frameColumnsInternal(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCols][ColumnsSelectionDsl.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCols][ColumnsSelectionDsl.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    @Interpretable("FrameCols1")
    public fun ColumnsSelectionDsl<*>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): TransformableColumnSet<DataFrame<*>> = asSingleColumn().frameColumnsInternal(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    @Interpretable("FrameCols2")
    public fun SingleColumn<DataRow<*>>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): TransformableColumnSet<DataFrame<*>> = this.ensureIsColumnGroup().frameColumnsInternal(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     */
    public fun String.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[frameCols][KProperty.frameCols]`() }`
     */
    @AccessApiOverload
    public fun KProperty<*>.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): TransformableColumnSet<DataFrame<*>> = columnGroup(this).frameCols(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @set [CommonFrameColsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[frameCols][ColumnPath.frameCols]`() }`
     */
    public fun ColumnPath.frameCols(
        filter: Predicate<FrameColumn<*>> = { true },
    ): TransformableColumnSet<DataFrame<*>> = columnGroup(this).frameCols(filter)
}

/**
 * Returns a TransformableColumnSet containing the frame columns that satisfy the given filter.
 *
 * @param filter The filter function to apply on each frame column. Must accept a FrameColumn object and return a Boolean.
 * @return A [TransformableColumnSet] containing the frame columns that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal fun ColumnsResolver<*>.frameColumnsInternal(
    filter: (FrameColumn<*>) -> Boolean,
): TransformableColumnSet<AnyFrame> =
    colsInternal { it.isFrameColumn() && filter(it.asFrameColumn()) } as TransformableColumnSet<AnyFrame>

// endregion
