package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.util.COLS_AT_ANY_DEPTH
import org.jetbrains.kotlinx.dataframe.util.COLS_AT_ANY_DEPTH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols At Any Depth [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsAtAnyDepthColumnsSelectionDsl {

    // region atAnyDepth

    /**
     * ## Cols At Any Depth Grammar
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
     *  [**`colsAtAnyDepth`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsAtAnyDepth`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsAtAnyDepth`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [**`colsAtAnyDepth`**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface PlainDslName

        /** __`.`__[**`colsAtAnyDepth`**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnSetName

        /** __`.`__[**`colsAtAnyDepth`**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet] filter function like
     * [colsOf][ColumnsSelectionDsl.colsOf], [single][ColumnsSelectionDsl.single], or [valueCols][ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]`().`[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][colsAtAnyDepth]`().`[filter][FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][colsAtAnyDepth]`().`[filter][FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][colsAtAnyDepth]`().`[colsOf][ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][colsAtAnyDepth]`().`[filter][FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][colsAtAnyDepth]` { condition }.`[first][ColumnsSelectionDsl.first]`()`
     *
     * [all][ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    private interface CommonAtAnyDepthDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth0")
    @Deprecated(
        message = COLS_AT_ANY_DEPTH,
        replaceWith = ReplaceWith(COLS_AT_ANY_DEPTH_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnSet<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth0")
    public fun ColumnSet<*>.colsAtAnyDepth(): ColumnSet<*> = colsAtAnyDepthInternal { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth1")
    @Deprecated(
        message = COLS_AT_ANY_DEPTH,
        replaceWith = ReplaceWith(COLS_AT_ANY_DEPTH_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        asSingleColumn().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().filter { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth1")
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(): ColumnSet<*> = asSingleColumn().colsAtAnyDepthInternal { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]`  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth2")
    @Deprecated(
        message = COLS_AT_ANY_DEPTH,
        replaceWith = ReplaceWith(COLS_AT_ANY_DEPTH_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]`().filter  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth2")
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsAtAnyDepth][String.colsAtAnyDepth]`  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated(
        message = COLS_AT_ANY_DEPTH,
        replaceWith = ReplaceWith(COLS_AT_ANY_DEPTH_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsAtAnyDepth][String.colsAtAnyDepth]`().filter  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun String.colsAtAnyDepth(): ColumnSet<*> = columnGroup(this).colsAtAnyDepth { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsAtAnyDepth][KProperty.colsAtAnyDepth]`  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated("", replaceWith = ReplaceWith("colsAtAnyDepth().filter(predicate)"))
    public fun ColumnPath.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[in][Iterable.contains]` it.`[values][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[first][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[valueCols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[isColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[filter][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun ColumnPath.colsAtAnyDepth(): ColumnSet<*> = columnGroup(this).colsAtAnyDepth { true }

    // endregion
}

/**
 * Returns all columns inside this [ColumnsResolver] at any depth if they satisfy the
 * given predicate.
 */
internal fun ColumnsResolver<*>.colsAtAnyDepthInternal(predicate: ColumnFilter<*>): ColumnSet<*> =
    colsInternal(predicate)
        .atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

// endregion
