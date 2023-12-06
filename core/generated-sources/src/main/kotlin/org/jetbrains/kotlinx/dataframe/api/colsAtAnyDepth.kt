package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.util.ALL_DFS_MESSAGE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_AT_ANY_DEPTH
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DFS_MESSAGE
import org.jetbrains.kotlinx.dataframe.util.DFS_OF_MESSAGE
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region ColumnsSelectionDsl

public interface ColsAtAnyDepthColumnsSelectionDsl {

    // region atAnyDepth

    /**
     * ## Cols At Any Depth Usage
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
     * `| `[KProperty][kotlin.reflect.KProperty]`<*>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
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
     *  [**colsAtAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsAtAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsAtAnyDepth**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`** `]`
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

        /** [**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface PlainDslName

        /** .[**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnSetName

        /** .[**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][ColumnsSelectionDsl.colsOf], [single][ColumnsSelectionDsl.single], and [valueCols][ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][ColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]`().`[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonAtAnyDepthDocs.Examples]}
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][dfs]` { condition } -> `[colsAtAnyDepth][colsAtAnyDepth]` { condition }`
     *
     * [allDfs][allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][colsAtAnyDepth]`().`[colsOf][ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][ColumnsSelectionDsl.cols]` { condition }.`[recursively][recursively]`() -> `[colsAtAnyDepth][colsAtAnyDepth]` { condition }`
     *
     * [first][ColumnsSelectionDsl.first]` { condition }.`[rec][rec]`() -> `[colsAtAnyDepth][colsAtAnyDepth]` { condition }.`[first][ColumnsSelectionDsl.first]`()`
     *
     * [all][ColumnsSelectionDsl.all]`().`[recursively][recursively]`() -> `[colsAtAnyDepth][colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten\]
     * @see [ColumnsSelectionDsl.simplify\]
     */
    private interface CommonAtAnyDepthDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun ColumnSet<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        asSingleColumn().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsAtAnyDepth][String.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun String.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsAtAnyDepth][KProperty.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun KProperty<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns the columns of this [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] at any depth if they satisfy the optional given predicate.
     *
     * This function is especially powerful if followed by another filter function such as
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], and [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For usage, check out [Usage][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Usage].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * For example:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * [dfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfs]` { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [allDfs][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.allDfs]`(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * [dfsOf][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.dfsOf]`<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition } }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.`[rec][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.rec]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.recursively]`() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun ColumnPath.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    // endregion

    // region deprecated recursively

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun <C> TransformableColumnSet<C>.recursively(): ColumnSet<C> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun <C> TransformableColumnSet<C>.rec(): ColumnSet<C> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun TransformableSingleColumn<*>.recursively(): SingleColumn<*> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun TransformableSingleColumn<*>.rec(): SingleColumn<*> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    // endregion

    // region deprecated dfs

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun String.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <C> KProperty<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    @Deprecated(
        message = DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth(predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnPath.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        error(DFS_MESSAGE)

    // endregion

    // region deprecated allDfs

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnSet<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun String.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun KProperty<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    @Deprecated(
        message = ALL_DFS_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth { includeGroups || !it.isColumnGroup() }"),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnPath.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        error(ALL_DFS_MESSAGE)

    // endregion

    // region deprecated dfsOf

    @Deprecated(
        message = DFS_OF_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf(type, predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <C> String.dfsOf(
        type: KType,
        predicate: (ColumnWithPath<C>) -> Boolean = { true },
    ): ColumnSet<*> = error(DFS_OF_MESSAGE)

    @Deprecated(
        message = DFS_OF_MESSAGE,
        replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf(type, predicate)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <C> KProperty<DataRow<*>>.dfsOf(
        type: KType,
        predicate: (ColumnWithPath<C>) -> Boolean = { true },
    ): ColumnSet<*> = error(DFS_OF_MESSAGE)

    // endregion
}

/**
 * Returns all columns inside this [ColumnsResolver] at any depth if they satisfy the
 * given predicate.
 */
internal fun ColumnsResolver<*>.colsAtAnyDepthInternal(predicate: ColumnFilter<*>): ColumnSet<*> =
    colsInternal(predicate)
        .atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

@Deprecated(
    message = DFS_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth(predicated)"),
    level = DeprecationLevel.ERROR,
)
@PublishedApi
internal fun ColumnSet<*>.dfsInternal(
    predicate: (ColumnWithPath<*>) -> Boolean,
): TransformableColumnSet<*> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf(type, predicated)"),
    level = DeprecationLevel.ERROR,
)
public fun <C> ColumnSet<*>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf(type, predicated)"),
    level = DeprecationLevel.ERROR,
)
public fun <C> SingleColumn<DataRow<*>>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf(type, predicated)"),
    level = DeprecationLevel.ERROR,
)
public fun <C> ColumnsSelectionDsl<*>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf<C>(filter)"),
    level = DeprecationLevel.ERROR,
)
public inline fun <reified C> ColumnSet<*>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf<C>(filter)"),
    level = DeprecationLevel.ERROR,
)
public inline fun <reified C> SingleColumn<DataRow<*>>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = error(DFS_OF_MESSAGE)

@Deprecated(
    message = DFS_OF_MESSAGE,
    replaceWith = ReplaceWith("this.colsAtAnyDepth().colsOf<C>(filter)"),
    level = DeprecationLevel.ERROR,
)
public inline fun <reified C> ColumnsSelectionDsl<*>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = error(DFS_OF_MESSAGE)

// endregion
