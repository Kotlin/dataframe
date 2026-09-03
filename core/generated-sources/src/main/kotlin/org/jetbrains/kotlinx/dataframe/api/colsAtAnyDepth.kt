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
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.util.COLS_AT_ANY_DEPTH
import org.jetbrains.kotlinx.dataframe.util.COLS_AT_ANY_DEPTH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols At Any Depth [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColsAtAnyDepthColumnsSelectionDsl {

    // region atAnyDepth

    /**
     * ## Cols At Any Depth Grammar
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
     *  [<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [<code>**`colsAtAnyDepth`**</code>][ColumnsSelectionDsl.colsAtAnyDepth] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`colsAtAnyDepth`**</code>][ColumnsSelectionDsl.colsAtAnyDepth] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colsAtAnyDepth`**</code>][ColumnsSelectionDsl.colsAtAnyDepth] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][ColumnSet] filter function like
     * [<code>colsOf</code>][ColumnsSelectionDsl.colsOf], [<code>single</code>][ColumnsSelectionDsl.single], or [<code>valueCols</code>][ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() }.`[<code>first</code>][ColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]`().`[<code>filter</code>][FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]`().`[<code>filter</code>][FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]`().`[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]`().`[<code>filter</code>][FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]` { condition }.`[<code>first</code>][ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    private interface CommonAtAnyDepthDocs {

        /** Example argument */
        typealias Examples = Nothing
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
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
    public fun ColumnSet<*>.colsAtAnyDepth(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth0")
    public fun ColumnSet<*>.colsAtAnyDepth(): ColumnSet<*> = colsAtAnyDepthInternal { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() }.`[<code>first</code>][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[<code>isColumnGroup</code>][DataColumn.isColumnGroup]` } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
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
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(
        predicate: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = asSingleColumn().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() }.`[<code>first</code>][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().filter { !it.`[<code>isColumnGroup</code>][DataColumn.isColumnGroup]` } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Interpretable("ColsAtAnyDepth1")
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(): ColumnSet<*> = asSingleColumn().colsAtAnyDepthInternal { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][SingleColumn.colsAtAnyDepth]`  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
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
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(
        predicate: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][SingleColumn.colsAtAnyDepth]`().filter  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
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
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsAtAnyDepth</code>][String.colsAtAnyDepth]`  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated(
        message = COLS_AT_ANY_DEPTH,
        replaceWith = ReplaceWith(COLS_AT_ANY_DEPTH_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.colsAtAnyDepth(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsAtAnyDepth</code>][String.colsAtAnyDepth]`().filter  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun String.colsAtAnyDepth(): ColumnSet<*> = columnGroup(this).colsAtAnyDepth { true }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>colsAtAnyDepth</code>][KProperty.colsAtAnyDepth]`  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsAtAnyDepth(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    @Deprecated("", replaceWith = ReplaceWith("colsAtAnyDepth().filter(predicate)"))
    public fun ColumnPath.colsAtAnyDepth(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this] at any depth (so also inside [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])
     *
     * This function can also be followed by another [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] filter function like
     * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single], or [<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols].
     *
     * For more information: [See `colsAtAnyDepth` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-at-any-depth)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][org.jetbrains.kotlinx.dataframe.DataColumn.values]`() }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsAtAnyDepth]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value- and frame columns at any depth`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]` } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsAtAnyDepth]`().`[<code>valueCols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`().filter  { "Alice"  `[<code>in</code>][Iterable.contains]` it.`[<code>values</code>][DataColumn.values]`() } }`
     *
     * #### Converting from deprecated syntax:
     *
     * `dfs  { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * `allDfs(includeGroups = false) -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { includeGroups || !it.`[<code>isColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.isColumnGroup]`() }`
     *
     * `dfsOf<Type> { condition } -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<Type> { condition }`
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { condition }.recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`().`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { condition }`
     *
     * [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { condition }.rec() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]` { condition }.`[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`()`
     *
     * [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().recursively() -> `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * @see [DataFrame.flatten]
     * @see [ColumnsSelectionDsl.simplify]
     */
    public fun ColumnPath.colsAtAnyDepth(): ColumnSet<*> = columnGroup(this).colsAtAnyDepth { true }

    // endregion
}

/**
 * Returns all columns inside this [<code>ColumnsResolver</code>][ColumnsResolver] at any depth if they satisfy the
 * given predicate.
 */
internal fun ColumnsResolver<*>.colsAtAnyDepthInternal(predicate: ColumnFilter<*>): ColumnSet<*> =
    colsInternal(predicate)
        .atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

// endregion
