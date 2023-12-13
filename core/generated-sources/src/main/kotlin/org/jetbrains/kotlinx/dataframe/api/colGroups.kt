package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.util.COLS_SELECT_DSL_GROUP
import org.jetbrains.kotlinx.dataframe.util.COLS_SELECT_DSL_GROUP_REPLACE
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/** See [Usage]. */
public interface ColGroupsColumnsSelectionDsl {

    /**
     * ## Column Groups Usage
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
     *  [**colGroups**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colGroups**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colGroups**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** .[**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** .[**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][ColumnGroup] from the current [ColumnSet].
     *
     * If the current [ColumnsResolver] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colGroups] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * Check out [Usage] for how to use [colGroups].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColGroupsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the column groups by.
     * @return A [ColumnSet] of [ColumnGroups][ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind\]
     * @see [ColumnsSelectionDsl.cols\]
     * @see [ColumnsSelectionDsl.frameCols\]
     * @see [ColumnsSelectionDsl.valueCols\]
     */
    private interface CommonColGroupsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.valueCols]
     */
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
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
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
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
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
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
    public fun KProperty<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Usage] for how to use [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`() }`
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
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    // region deprecated

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun ColumnSet<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun SingleColumn<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun ColumnsSelectionDsl<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.asSingleColumn().columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun String.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun KProperty<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun ColumnPath.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    // endregion
}

/**
 * Returns a TransformableColumnSet containing the column groups that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column group. Must accept a ColumnGroup object and return a Boolean.
 * @return A [TransformableColumnSet] containing the column groups that satisfy the filter.
 */
@Suppress("UNCHECKED_CAST")
internal fun ColumnsResolver<*>.columnGroupsInternal(filter: (ColumnGroup<*>) -> Boolean): TransformableColumnSet<AnyRow> =
    colsInternal { it.isColumnGroup() && filter(it.asColumnGroup()) } as TransformableColumnSet<AnyRow>

// endregion
