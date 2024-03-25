package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.Grammar.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
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
import org.jetbrains.kotlinx.dataframe.util.COLS_SELECT_DSL_GROUP
import org.jetbrains.kotlinx.dataframe.util.COLS_SELECT_DSL_GROUP_REPLACE
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Column Groups {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColGroupsColumnsSelectionDsl {

    /**
     * ## Column Groups Grammar
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
     *  {@include [PlainDslName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     */
    public interface Grammar {

        /** [**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface PlainDslName

        /** .[**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnSetName

        /** .[**colGroups**][ColumnsSelectionDsl.colGroups] */
        public interface ColumnGroupName
    }

    /**
     * ## Column Groups
     * Creates a subset of columns from [this\] that are [ColumnGroups][ColumnGroup].
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colGroups] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
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
     * {@get [CommonColGroupsDocs.ExampleArg]}
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
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[colGroups][KProperty.colGroups]`() }`
     */
    public fun KProperty<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @set [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colGroups][ColumnPath.colGroups]`() }`
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    // region deprecated

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnSet<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.ensureIsColumnGroup().columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.asSingleColumn().columnGroupsInternal(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun String.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun KProperty<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    @Deprecated(COLS_SELECT_DSL_GROUP, ReplaceWith(COLS_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

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
