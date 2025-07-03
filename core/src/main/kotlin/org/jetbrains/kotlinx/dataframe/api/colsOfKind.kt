package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Cols Of Kind {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsOfKindColumnsSelectionDsl {

    /**
     * ## Cols Of Kind Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnKindDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     */
    public interface Grammar {

        /** [**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** __`.`__[**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** __`.`__[**`colsOfKind`**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols Of Kind
     * Creates a subset of columns from [this\] that are of the given kind(s).
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colsOfKind] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     *  `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Group][ColumnKind.Group]`) }`
     *
     *  `df.`[select][DataFrame.select]` { "myColGroup".`[colsOfKind][String.colsOfKind]`(`[Frame][ColumnKind.Frame]`) }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonColsOfKindDocs.EXAMPLE]}
     *
     * @param [filter\] An optional [predicate][ColumnFilter] to filter the columns of given kind(s) by.
     * @param [kind\] The [kind][ColumnKind] of columns to include.
     * @param [others\] Other optional [kinds][ColumnKind] of columns to include.
     * @return A [ColumnSet] of columns of the given kind(s).
     * @see [ColumnsSelectionDsl.valueCols\]
     * @see [ColumnsSelectionDsl.frameCols\]
     * @see [ColumnsSelectionDsl.colGroups\]
     * @see [ColumnsSelectionDsl.cols\]
     */
    private interface CommonColsOfKindDocs {

        /** Example argument */
        interface EXAMPLE
    }

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colsOfKind][ColumnSet.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> =
        columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOfKind][ColumnsSelectionDsl.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnsSelectionDsl<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> =
        asSingleColumn().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     */
    public fun SingleColumn<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> =
        this.ensureIsColumnGroup().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            filter = filter,
        )

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOfKind][SingleColumn.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     */
    public fun String.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOfKind][KProperty.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    /**
     * @include [CommonColsOfKindDocs]
     * @set [CommonColsOfKindDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOfKind][ColumnPath.colsOfKind]`(`[Value][ColumnKind.Value]`, `[Frame][ColumnKind.Frame]`) }`
     */
    public fun ColumnPath.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        filter: ColumnFilter<*> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOfKind(kind, *others, filter = filter)

    // endregion
}

/**
 * Returns a TransformableColumnSet containing the columns of given kind(s) that satisfy the given filter.
 *
 * @param filter The filter function to apply on each column. Must accept a ColumnWithPath object and return a Boolean.
 * @return A [TransformableColumnSet] containing the columns of given kinds that satisfy the filter.
 */
internal inline fun ColumnsResolver<*>.columnsOfKindInternal(
    kinds: Set<ColumnKind>,
    crossinline filter: ColumnFilter<*>,
): TransformableColumnSet<*> = colsInternal { it.kind() in kinds && filter(it) }

// endregion
