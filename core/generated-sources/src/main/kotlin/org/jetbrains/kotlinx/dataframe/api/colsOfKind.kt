package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
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
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface ColsOfKindColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    /**
     * ## Cols Of Kind Usage
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
     * `| `[KProperty][KProperty]`<*>` | `[ColumnPath][ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `kind: `[ColumnKind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the plain DSL:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**colsOfKind**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[kind][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnKindDef]`, ..`**`)`**` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]` `[ `.[**atAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`()` ` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsOfKind**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[kind][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnKindDef]`, ..`**`)`**` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]` `[ `.[**atAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`()` ` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsOfKind**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[kind][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnKindDef]`, ..`**`)`**` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]` `[ `.[**atAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`()` ` ]`
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

        /** [**colsOfKind**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** .[**colsOfKind**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** .[**colsOfKind**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet].
     *
     * If the current [ColumnsResolver] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colsOfKind] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * Check out [Usage] for how to use [colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][DataFrame.select]` { `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Group][ColumnKind.Group]`).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][DataFrame.select]` { "myColGroup".`[colsOfKind][String.colsOfKind]`(`[Frame][ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColsOfKindDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind\] The [kind][ColumnKind] of columns to include.
     * @param [others\] Other optional [kinds][ColumnKind] of columns to include.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols\]
     * @see [ColumnsSelectionDsl.frameCols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @see [ColumnsSelectionDsl.cols\]
     */
    private interface CommonColsOfKindDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colsOfKind][ColumnSet.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnSet<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnsSelectionDsl<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        asSingleColumn().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter
        )

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOfKind][KProperty.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun KProperty<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * ## Cols Of Kind
     * Creates a subset of columns that are of the given kind(s) from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.Usage] for how to use [colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColsOfKindColumnsSelectionDsl.colsOfKind].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]`, `[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOfKind][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOfKind]`(`[Group][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group]`).`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colsOfKind][kotlin.String.colsOfKind]`(`[Frame][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOfKind][ColumnPath.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind] The [kind][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @param [others] Other optional [kinds][org.jetbrains.kotlinx.dataframe.columns.ColumnKind] of columns to include.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnGroup(this).colsOfKind(kind, *others, filter = filter)

    // endregion
}

/**
 * Returns a TransformableColumnSet containing the columns of given kind(s) that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column. Must accept a ColumnWithPath object and return a Boolean.
 * @return A [TransformableColumnSet] containing the columns of given kinds that satisfy the filter.
 */
internal fun ColumnsResolver<*>.columnsOfKindInternal(
    kinds: Set<ColumnKind>,
    filter: ColumnFilter<*>,
): TransformableColumnSet<*> = colsInternal {
    it.kind() in kinds && filter(it)
}

// endregion
