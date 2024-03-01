package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.Grammar.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
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

/**
 * ## Cols At Any Depth {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsAtAnyDepthColumnsSelectionDsl {

    // region atAnyDepth

    /**
     * ## Cols At Any Depth Grammar
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
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     */
    public interface Grammar {

        /** [**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface PlainDslName

        /** \**`.`**[**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnSetName

        /** \**`.`**[**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols At Any Depth
     *
     * Returns all columns in [this\] at any depth (so also inside [Column Groups][ColumnGroup]) if they satisfy the
     * optional given predicate.
     *
     * This function can also be followed by another [ColumnSet] filter function like
     * [colsOf][ColumnsSelectionDsl.colsOf], [single][ColumnsSelectionDsl.single], or [valueCols][ColumnsSelectionDsl.valueCols].
     * ### Check out: [Grammar]
     * #### For example:
     * `// Depth-first search to a column containing the value "Alice"`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[first][ColumnsSelectionDsl.firstCol]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     * {@include [LineBreak]}
     * `// The columns at any depth excluding the top-level`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnSet.colsAtAnyDepth]`() }`
     * {@include [LineBreak]}
     * `// All value- and frame columns at any depth`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     * {@include [LineBreak]}
     * `// All value columns at any depth nested under a column group named "myColGroup"`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]`().`[valueCols][ColumnsSelectionDsl.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonAtAnyDepthDocs.Examples]}
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
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun ColumnSet<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     */
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        asSingleColumn().colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsAtAnyDepth][String.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun String.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsAtAnyDepth][KProperty.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun KProperty<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @set [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
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
