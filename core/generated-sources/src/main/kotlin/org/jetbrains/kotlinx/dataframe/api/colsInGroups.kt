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
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.COLS_IN_GROUPS
import org.jetbrains.kotlinx.dataframe.util.COLS_IN_GROUPS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols in Groups [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsInGroupsColumnsSelectionDsl {

    /**
     * ## Cols in Groups Grammar
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
     *  [**`colsInGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsInGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsInGroups`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
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

        /** [**`colsInGroups`**][ColumnsSelectionDsl.colsInGroups] */
        public interface PlainDslName

        /** __`.`__[**`colsInGroups`**][ColumnsSelectionDsl.colsInGroups] */
        public interface ColumnSetName

        /** __`.`__[**`colsInGroups`**][ColumnsSelectionDsl.colsInGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame], instead of having to write:
     *
     * `df.`[select][DataFrame.select]` { colGroupA.`[cols][ColumnsSelectionDsl.cols]`() `[and][ColumnsSelectionDsl.and]` colGroupB.`[cols][ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnsSelectionDsl.colsInGroups]`().`[nameContains][ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][ColumnGroup] in a [ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`().`[nameContains][ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][ColumnSet.colsInGroups]`() }`
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
     * @return A [TransformableColumnSet] containing the cols.
     */
    private interface ColsInGroupsDocs {

        /** Example argument to use */
        interface EXAMPLE
    }

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[colsInGroups][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnSet<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        transform { it.flatMap { it.cols().filter { predicate(it) } } }

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[colsInGroups][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    public fun ColumnSet<*>.colsInGroups(): TransformableColumnSet<*> = transform { it.flatMap { it.cols() } }

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnsSelectionDsl<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        asSingleColumn().colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    public fun ColumnsSelectionDsl<*>.colsInGroups(): TransformableColumnSet<*> = asSingleColumn().colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    public fun SingleColumn<DataRow<*>>.colsInGroups(): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsInGroups][String.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsInGroups][String.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    public fun String.colsInGroups(): TransformableColumnSet<*> = columnGroup(this).colsInGroups()

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsInGroups][ColumnPath.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    @Deprecated(
        message = COLS_IN_GROUPS,
        replaceWith = ReplaceWith(COLS_IN_GROUPS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnPath.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * ## Cols in Groups
     *
     * [colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.colsInGroups] is a function that returns all columns at the top-levels of
     * all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in [this]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this], or with [colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this] at any depth.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * To get only the columns inside all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], instead of having to write:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroupA.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colGroupB.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() ...  }`
     *
     * you can use:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`() }`
     *
     * and
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsInGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("user") }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Similarly, you can take the columns inside all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.colsInGroups]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsInGroups][ColumnPath.colsInGroups]`() }`
     *
     * @see [ColumnsSelectionDsl.cols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the cols.
     */
    public fun ColumnPath.colsInGroups(): TransformableColumnSet<*> = columnGroup(this).colsInGroups()
}

// endregion
