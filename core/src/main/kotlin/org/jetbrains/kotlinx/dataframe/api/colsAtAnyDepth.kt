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
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.flattenRecursively
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_DFS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_DFS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_AT_ANY_DEPTH
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_DFS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_DFS_OF
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_DFS_OF_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_DFS_OF_TYPED_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_DFS_REPLACE
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

public interface ColsAtAnyDepthColumnsSelectionDsl {

    // region atAnyDepth

    /**
     * ## Cols At Any Depth Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ConditionDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
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
     * {@include [LineBreak]}
     * For usage, check out [Usage].
     * {@include [LineBreak]}
     * For example:
     * {@include [LineBreak]}
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
     */
    private interface CommonAtAnyDepthDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun ColumnSet<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() }.`[first][ColumnsSelectionDsl.first]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { !it.`[isColumnGroup][DataColumn.isColumnGroup]` } }`
     */
    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        asSingleColumn().colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colsAtAnyDepth][SingleColumn.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsAtAnyDepth][String.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun String.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsAtAnyDepth][KProperty.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun KProperty<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "Alice" `[in][Iterable.contains]` it.`[values][DataColumn.values]`() } }`
     */
    public fun ColumnPath.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    // endregion

    // region deprecated

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[allCols][ColumnsSelectionDsl.allCols]`().`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[groups][ColumnsSelectionDsl.groups]`().`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     */
    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun <C> TransformableColumnSet<C>.atAnyDepth(): ColumnSet<C> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    /**
     * @include [CommonAtAnyDepthDocs]
     * @setArg [CommonAtAnyDepthDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.firstCol]` { col -> col.`[any][DataColumn.any]` { it == "Alice" } }.`[atAnyDepth][TransformableSingleColumn.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[single][ColumnsSelectionDsl.singleCol]` { it.name == "myCol" }.`[atAnyDepth][TransformableSingleColumn.atAnyDepth]`() }`
     */
    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun TransformableSingleColumn<*>.atAnyDepth(): SingleColumn<*> =
        atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun <C> TransformableColumnSet<C>.recursively(): ColumnSet<C> = atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun <C> TransformableColumnSet<C>.rec(): ColumnSet<C> = atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun TransformableSingleColumn<*>.recursively(): SingleColumn<*> = atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    @Deprecated(COL_SELECT_DSL_AT_ANY_DEPTH, ReplaceWith(COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE), DeprecationLevel.WARNING)
    public fun TransformableSingleColumn<*>.rec(): SingleColumn<*> = atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

    // endregion

    // region deprecated dfs

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> = dfsInternal(predicate)

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().dfsInternal(predicate)

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        asSingleColumn().ensureIsColumnGroup().asColumnSet().dfsInternal(predicate)

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun String.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().dfsInternal(predicate)

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun <C> KProperty<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().dfsInternal(predicate)

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS, ReplaceWith(COL_SELECT_DSL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().dfsInternal(predicate)

    // endregion

    // region deprecated allDfs

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnSet<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        asSingleColumn().ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun String.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun KProperty<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_ALL_DFS, ReplaceWith(COL_SELECT_DSL_ALL_DFS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        columnGroup(this).ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    // endregion

    // region deprecated dfsOf

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_REPLACE), DeprecationLevel.ERROR)
    public fun <C> String.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this)
            .ensureIsColumnGroup()
            .asColumnSet()
            .dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

    @Suppress("DEPRECATION")
    @Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_REPLACE), DeprecationLevel.ERROR)
    public fun <C> KProperty<DataRow<*>>.dfsOf(
        type: KType,
        predicate: (ColumnWithPath<C>) -> Boolean = { true },
    ): ColumnSet<*> =
        columnGroup(this)
            .ensureIsColumnGroup()
            .asColumnSet()
            .dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

    // endregion
}

/**
 * Returns all columns inside this [ColumnsResolver] at any depth if they satisfy the
 * given predicate.
 */
internal fun ColumnsResolver<*>.colsAtAnyDepthInternal(predicate: ColumnFilter<*>): ColumnSet<*> =
    colsInternal(predicate)
        .atAnyDepthImpl(includeTopLevel = true, includeGroups = true)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Replaced with colsAtAnyDepth()")
@PublishedApi
internal fun ColumnSet<*>.dfsInternal(
    predicate: (ColumnWithPath<*>) -> Boolean,
): TransformableColumnSet<*> =
    transform {
        it.filter { it.isColumnGroup() }
            .flatMap { it.children().flattenRecursively().filter(predicate) }
    }

@Suppress("DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_REPLACE), DeprecationLevel.ERROR)
public fun <C> ColumnSet<*>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> =
    dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Suppress("DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_REPLACE), DeprecationLevel.ERROR)
public fun <C> SingleColumn<DataRow<*>>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> =
    ensureIsColumnGroup()
        .asColumnSet()
        .dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Suppress("DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_REPLACE), DeprecationLevel.ERROR)
public fun <C> ColumnsSelectionDsl<*>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> =
    asSingleColumn()
        .ensureIsColumnGroup()
        .asColumnSet()
        .dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Suppress("UNCHECKED_CAST", "DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_TYPED_REPLACE), DeprecationLevel.ERROR)
public inline fun <reified C> ColumnSet<*>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> =
    dfsInternal { it.isSubtypeOf(typeOf<C>()) && filter(it.cast()) } as ColumnSet<C>

@Suppress("UNCHECKED_CAST", "DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_TYPED_REPLACE), DeprecationLevel.ERROR)
public inline fun <reified C> SingleColumn<DataRow<*>>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> =
    ensureIsColumnGroup()
        .asColumnSet()
        .dfsInternal { it.isSubtypeOf(typeOf<C>()) && filter(it.cast()) } as ColumnSet<C>

@Suppress("UNCHECKED_CAST", "DEPRECATION")
@Deprecated(COL_SELECT_DSL_DFS_OF, ReplaceWith(COL_SELECT_DSL_DFS_OF_TYPED_REPLACE), DeprecationLevel.ERROR)
public inline fun <reified C> ColumnsSelectionDsl<*>.dfsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> =
    asSingleColumn()
        .ensureIsColumnGroup()
        .asColumnSet()
        .dfsInternal { it.isSubtypeOf(typeOf<C>()) && filter(it.cast()) } as ColumnSet<C>

// endregion
