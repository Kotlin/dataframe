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
 * Recursively implementation for [TransformableColumnSet].
 * This converts a [TransformableColumnSet] into a [ColumnSet] by redirecting [ColumnSet.resolve]
 * to [TransformableColumnSet.transformResolve] with a correctly configured [RecursivelyTransformer].
 */
internal fun <C> TransformableColumnSet<C>.recursivelyImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnSet<C> = object : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        this@recursivelyImpl.transformResolve(
            context = context,
            transformer = RecursivelyTransformer(
                includeGroups = includeGroups,
                includeTopLevel = includeTopLevel,
            ),
        )
}

/**
 * Recursively implementation for [TransformableSingleColumn].
 * This converts a [TransformableSingleColumn] into a [SingleColumn] by redirecting [SingleColumn.resolveSingle]
 * to [TransformableSingleColumn.transformResolveSingle] with a correctly configured [RecursivelyTransformer].
 */
internal fun <C> TransformableSingleColumn<C>.recursivelyImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): SingleColumn<C> = object : SingleColumn<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
        this@recursivelyImpl.transformResolveSingle(
            context = context,
            transformer = RecursivelyTransformer(
                includeGroups = includeGroups,
                includeTopLevel = includeTopLevel,
            ),
        )
}

/**
 * ## Recursively transformer.
 * A [ColumnsResolverTransformer] implementation around the [ColumnsResolver.flattenRecursively] function.
 * Created only using [recursivelyImpl].
 */
private class RecursivelyTransformer(
    val includeGroups: Boolean = true,
    val includeTopLevel: Boolean = true,
) : ColumnsResolverTransformer {

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
 * Flattens a [ColumnsResolver] recursively.
 *
 * If [this] is a [SingleColumn] containing a single [ColumnGroup], the "top-level" is
 * considered to be the [ColumnGroup]'s children, otherwise, if this is a [ColumnsResolver],
 * the "top-level" is considered to be the columns in the [ColumnsResolver].
 *
 * @param includeGroups Whether to include [ColumnGroup]s in the result.
 * @param includeTopLevel Whether to include the "top-level" columns in the result.
 */
internal fun ColumnsResolver<*>.flattenRecursively(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnsResolver<*> = allColumnsInternal().transform { cols ->
    if (includeTopLevel) {
        cols.flattenRecursively()
    } else {
        cols
            .filter { it.isColumnGroup() }
            .flatMap { it.children().flattenRecursively() }
    }.filter { includeGroups || !it.isColumnGroup() }
}
