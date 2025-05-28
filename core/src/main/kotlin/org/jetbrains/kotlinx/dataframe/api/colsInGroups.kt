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
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols in Groups {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsInGroupsColumnsSelectionDsl {

    /**
     * ## Cols in Groups Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`()`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}`()`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`()`
     * }
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
     * all [column groups][ColumnGroup] in [this\]. This is useful if you want to select all columns that are
     * "one level deeper".
     *
     * NOTE: This function should not be confused with [cols][ColumnsSelectionDsl.cols], which operates on all
     * columns directly in [this\], or with [colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth], which operates on all
     * columns in [this\] at any depth.
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
     * {@include [LineBreak]}
     * Similarly, you can take the columns inside all [column groups][ColumnGroup] in a [ColumnSet]:
     * {@include [LineBreak]}
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`().`[nameContains][ColumnsSelectionDsl.nameContains]`("my").`[colsInGroups][ColumnSet.colsInGroups]`() }`
     * {@include [LineBreak]}
     *
     * #### Examples of this overload:
     *
     * {@get [ColsInGroupsDocs.EXAMPLE]}
     *
     * @see [ColumnsSelectionDsl.cols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @return A [TransformableColumnSet] containing the cols.
     */
    private interface ColsInGroupsDocs {

        /** Example argument to use */
        interface EXAMPLE
    }

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    @Deprecated("", ReplaceWith("this.colsInGroups().filter(predicate)"))
    public fun ColumnSet<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        transform { it.flatMap { it.cols().filter { predicate(it) } } }

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    public fun ColumnSet<*>.colsInGroups(): TransformableColumnSet<*> = transform { it.flatMap { it.cols() } }

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    @Deprecated("", ReplaceWith("colsInGroups().filter(predicate)"))
    public fun ColumnsSelectionDsl<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        asSingleColumn().colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    public fun ColumnsSelectionDsl<*>.colsInGroups(): TransformableColumnSet<*> = asSingleColumn().colsInGroups()

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Deprecated("", ReplaceWith("this.colsInGroups().filter(predicate)"))
    public fun SingleColumn<DataRow<*>>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun SingleColumn<DataRow<*>>.colsInGroups(): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups()

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsInGroups][String.colsInGroups]`() }`
     */
    @Deprecated("", ReplaceWith("colsInGroups().filter(predicate)"))
    public fun String.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsInGroups][String.colsInGroups]`() }`
     */
    public fun String.colsInGroups(): TransformableColumnSet<*> = columnGroup(this).colsInGroups()

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     */
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
    @AccessApiOverload
    public fun KProperty<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsInGroups][ColumnPath.colsInGroups]`() }`
     */
    @Deprecated("", ReplaceWith("colsInGroups().filter(predicate)"))
    public fun ColumnPath.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsInGroups][ColumnPath.colsInGroups]`() }`
     */
    public fun ColumnPath.colsInGroups(): TransformableColumnSet<*> = columnGroup(this).colsInGroups()
}

// endregion
