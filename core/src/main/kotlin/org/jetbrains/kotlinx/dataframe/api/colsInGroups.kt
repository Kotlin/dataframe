package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColsInGroupsColumnsSelectionDsl.Grammar.PlainDslName
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
     * {@set [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
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
     * [colsInGroups][colsInGroups] is a function that returns all (optionally filtered) columns at the top-levels of
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
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnsSelectionDsl.colsInGroups]`  { "user"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     * {@include [LineBreak]}
     * Similarly, you can take the columns inside all [column groups][ColumnGroup] in a [ColumnSet]:
     * {@include [LineBreak]}
     * `df.`[select][DataFrame.select]`  {  `[colGroups][ColumnsSelectionDsl.colGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` }.`[colsInGroups][ColumnSet.colsInGroups]`() }`
     * {@include [LineBreak]}
     *
     * #### Examples of this overload:
     *
     * {@get [ColsInGroupsDocs.ExampleArg]}
     *
     * @see [ColumnsSelectionDsl.cols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @param [predicate\] An optional predicate to filter the cols by.
     * @return A [TransformableColumnSet] containing the (filtered) cols.
     */
    private interface ColsInGroupsDocs {

        /** Example argument to use */
        interface ExampleArg
    }

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    public fun ColumnSet<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        transform { it.flatMap { it.cols().filter { predicate(it) } } }

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`  { "my"  `[in][String.contains]` it.`[name][DataColumn.name]` } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsInGroups][ColumnSet.colsInGroups]`() }`
     */
    public fun ColumnsSelectionDsl<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        asSingleColumn().colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsInGroups][SingleColumn.colsInGroups]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun SingleColumn<DataRow<*>>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal().colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsInGroups][String.colsInGroups]`() }`
     */
    public fun String.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsInGroups][KProperty.colsInGroups]`() }`
     */
    public fun KProperty<*>.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)

    /**
     * @include [ColsInGroupsDocs]
     * @set [ColsInGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsInGroups][ColumnPath.colsInGroups]`() }`
     */
    public fun ColumnPath.colsInGroups(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).colsInGroups(predicate)
}

// endregion
