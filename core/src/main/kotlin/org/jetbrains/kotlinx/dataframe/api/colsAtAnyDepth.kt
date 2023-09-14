package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
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
     * TODO
     */
    public interface Usage {

        /** .[**colsAtAnyDepth**][ColumnsSelectionDsl.colsAtAnyDepth]`()` */
        public interface Name
    }

    /**
     * ## At Any Depth
     *
     * Modifies the previous call to run not only on the current column set,
     * but also on all columns inside [column groups][ColumnGroup].
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() }`
     *
     * returns all columns of type [String] in the top-level, as expected. However, what if you want ALL
     * columns of type [String] even if they are inside a nested [column group][ColumnGroup]? Then you can use [atAnyDepth]:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     *
     * This will return the columns of type [String] in all levels.
     *
     * More examples:
     *
     * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.firstCol]` { col -> col.`[any][DataColumn.any]` { it == "Alice" } }.`[atAnyDepth][TransformableSingleColumn.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "name" in it.`[name][ColumnReference.name]` }.`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][ColumnsSelectionDsl.valueCols]`().`[atAnyDepth][TransformableColumnSet.atAnyDepth]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonAtAnyDepthDocs.Examples]}
     *
     * @param [includeTopLevel\] Whether to include the top-level columns in the result. `true` by default.
     * @see [DataFrame.flatten\]
     */
    private interface CommonAtAnyDepthDocs {

        /** Example argument */
        interface Examples
    }

    public fun ColumnSet<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        colsAtAnyDepthInternal(predicate)

    public fun ColumnsSelectionDsl<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        asSingleColumn().colsAtAnyDepthInternal(predicate)

    public fun SingleColumn<DataRow<*>>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        ensureIsColumnGroup().colsAtAnyDepthInternal(predicate)

    public fun String.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

    public fun KProperty<*>.colsAtAnyDepth(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).colsAtAnyDepth(predicate)

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
@Deprecated("Replaced with atAnyDepth()")
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
