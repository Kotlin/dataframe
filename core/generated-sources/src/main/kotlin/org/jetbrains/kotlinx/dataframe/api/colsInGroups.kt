package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.COLS_IN_GROUPS
import org.jetbrains.kotlinx.dataframe.util.COLS_IN_GROUPS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols in Groups [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColsInGroupsColumnsSelectionDsl {

    /**
     * ## Cols in Groups Grammar
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
     *  [<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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

        /** [<code>**`colsInGroups`**</code>][ColumnsSelectionDsl.colsInGroups] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`colsInGroups`**</code>][ColumnsSelectionDsl.colsInGroups] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colsInGroups`**</code>][ColumnsSelectionDsl.colsInGroups] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { colGroupA.`[<code>cols</code>][ColumnsSelectionDsl.cols]`() `[<code>and</code>][ColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsInGroups</code>][ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsInGroups</code>][ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][ColumnGroup] in a [<code>ColumnSet</code>][ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colGroups</code>][ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     *
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the cols.
     */
    private interface ColsInGroupsDocs {

        /** Example argument to use */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>colsInGroups</code>][ColumnSet.colsInGroups]`  { "my"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>DataRow</code>][DataRow]`<MyGroupType>>().`[<code>colsInGroups</code>][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnSet<*>.colsInGroups(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        transform { it.flatMap { it.cols().filter { predicate(it) } } }

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>DataRow</code>][DataRow]`<MyGroupType>>().`[<code>colsInGroups</code>][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    public fun ColumnSet<*>.colsInGroups(): ColumnSet<*> = transform { it.flatMap { it.cols() } }

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsInGroups</code>][ColumnSet.colsInGroups]`  { "my"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsInGroups</code>][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnsSelectionDsl<*>.colsInGroups(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        asSingleColumn().colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsInGroups</code>][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    public fun ColumnsSelectionDsl<*>.colsInGroups(): ColumnSet<*> = asSingleColumn().colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsInGroups</code>][SingleColumn.colsInGroups]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsInGroups</code>][SingleColumn.colsInGroups]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.colsInGroups(
        predicate: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = ensureIsColumnGroup().allColumnsInternal().colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsInGroups</code>][SingleColumn.colsInGroups]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    public fun SingleColumn<DataRow<*>>.colsInGroups(): ColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsInGroups</code>][String.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.colsInGroups(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsInGroups</code>][String.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    public fun String.colsInGroups(): ColumnSet<*> = columnGroup(this).colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>colsInGroups</code>][KProperty.colsInGroups]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>colsInGroups</code>][KProperty.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsInGroups(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>colsInGroups</code>][ColumnPath.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnPath.colsInGroups(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * For more information: [See `colsInGroups` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-in-groups)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[<code>colsInGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>colsInGroups</code>][ColumnPath.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the cols.
     */
    public fun ColumnPath.colsInGroups(): ColumnSet<*> = columnGroup(this).colsInGroups()
}

// endregion
