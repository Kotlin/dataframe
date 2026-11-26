package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.api.allColumnsInternal
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.flattenRecursively

/**
 * AtAnyDepth implementation for [<code>TransformableColumnSet</code>][TransformableColumnSet].
 * This converts a [<code>TransformableColumnSet</code>][TransformableColumnSet] into a [<code>ColumnSet</code>][ColumnSet] by redirecting [<code>ColumnSet.resolve</code>][ColumnSet.resolve]
 * to [<code>TransformableColumnSet.transformResolve</code>][TransformableColumnSet.transformResolve] with a correctly configured [<code>AtAnyDepthTransformer</code>][AtAnyDepthTransformer].
 */
internal fun <C> TransformableColumnSet<C>.atAnyDepthImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnSet<C> =
    object : ColumnSet<C> {

        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
            this@atAnyDepthImpl.transformResolve(
                context = context,
                transformer = AtAnyDepthTransformer(
                    includeGroups = includeGroups,
                    includeTopLevel = includeTopLevel,
                ),
            )
    }

/**
 * AtAnyDepth implementation for [<code>TransformableSingleColumn</code>][TransformableSingleColumn].
 * This converts a [<code>TransformableSingleColumn</code>][TransformableSingleColumn] into a [<code>SingleColumn</code>][SingleColumn] by redirecting [<code>SingleColumn.resolveSingle</code>][SingleColumn.resolveSingle]
 * to [<code>TransformableSingleColumn.transformResolveSingle</code>][TransformableSingleColumn.transformResolveSingle] with a correctly configured [<code>AtAnyDepthTransformer</code>][AtAnyDepthTransformer].
 */
internal fun <C> TransformableSingleColumn<C>.atAnyDepthImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): SingleColumn<C> =
    object : SingleColumn<C> {

        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
            this@atAnyDepthImpl.transformResolveSingle(
                context = context,
                transformer = AtAnyDepthTransformer(
                    includeGroups = includeGroups,
                    includeTopLevel = includeTopLevel,
                ),
            )
    }

/**
 * ## AtAnyDepth transformer.
 * A [<code>ColumnsResolverTransformer</code>][ColumnsResolverTransformer] implementation around the [<code>ColumnsResolver.flattenRecursively</code>][ColumnsResolver.flattenRecursively] function.
 * Created only using [<code>atAnyDepthImpl</code>][atAnyDepthImpl].
 */
private class AtAnyDepthTransformer(val includeGroups: Boolean = true, val includeTopLevel: Boolean = true) :
    ColumnsResolverTransformer {

    override fun transform(columnsResolver: ColumnsResolver<*>): ColumnsResolver<*> =
        columnsResolver.flattenRecursively(
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )

    override fun transformSet(columnSet: ColumnSet<*>): ColumnsResolver<*> =
        columnSet.flattenRecursively(
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )

    override fun transformSingle(singleColumn: SingleColumn<*>): ColumnsResolver<*> =
        singleColumn.flattenRecursively(
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )
}

/**
 * Flattens a [<code>ColumnsResolver</code>][ColumnsResolver] recursively.
 *
 * If [<code>this</code>][this] is a [<code>SingleColumn</code>][SingleColumn] containing a single [<code>ColumnGroup</code>][ColumnGroup], the "top-level" is
 * considered to be the [<code>ColumnGroup</code>][ColumnGroup]'s children, otherwise, if this is a [<code>ColumnsResolver</code>][ColumnsResolver],
 * the "top-level" is considered to be the columns in the [<code>ColumnsResolver</code>][ColumnsResolver].
 *
 * @param includeGroups Whether to include [<code>ColumnGroup</code>][ColumnGroup]s in the result.
 * @param includeTopLevel Whether to include the "top-level" columns in the result.
 */
internal fun ColumnsResolver<*>.flattenRecursively(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnsResolver<*> =
    allColumnsInternal().transform { cols ->
        if (includeTopLevel) {
            cols.flattenRecursively()
        } else {
            cols.filter { it.isColumnGroup() }
                .flatMap { it.cols().flattenRecursively() }
        }.filter { includeGroups || !it.isColumnGroup() }
    }
