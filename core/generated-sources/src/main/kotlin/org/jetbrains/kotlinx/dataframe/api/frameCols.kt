package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface FrameColsColumnsSelectionDsl {

    /**
     * ## Frame Cols Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<* | `[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**frameCols**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**frameCols**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**frameCols**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
     *
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**frameCols**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** .[**frameCols**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** .[**frameCols**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][FrameColumn] from the current [ColumnSet].
     *
     * If the current [ColumnsResolver] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [frameCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * ### Check out: [Usage]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonFrameColsDocs.ExampleArg]}
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
        interface ExampleArg
    }

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[frameCols][ColumnSet.frameCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][ColumnsSelectionDsl.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnSet<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][ColumnsSelectionDsl.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][ColumnsSelectionDsl.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnsSelectionDsl<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        asSingleColumn().frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        this.ensureIsColumnGroup().frameColumnsInternal(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[frameCols][KProperty.frameCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun KProperty<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [frameCols][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.frameCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.FrameColsColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[frameCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.frameCols]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[frameCols][kotlin.String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[frameCols][ColumnPath.frameCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the frame columns by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [FrameColumns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        columnGroup(this).frameCols(filter)
}

/**
 * Returns a TransformableColumnSet containing the frame columns that satisfy the given filter.
 *
 * @param filter The filter function to apply on each frame column. Must accept a FrameColumn object and return a Boolean.
 * @return A [TransformableColumnSet] containing the frame columns that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal fun ColumnsResolver<*>.frameColumnsInternal(filter: (FrameColumn<*>) -> Boolean): TransformableColumnSet<AnyFrame> =
    colsInternal { it.isFrameColumn() && filter(it.asFrameColumn()) } as TransformableColumnSet<AnyFrame>

// endregion
