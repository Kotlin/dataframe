package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.api.allInternal
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.flattenRecursively

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

private class RecursivelyTransformer(
    val includeGroups: Boolean = true,
    val includeTopLevel: Boolean = true,
) : ColumnSetTransformer {

    override fun transform(columnSet: ColumnSet<*>): ColumnSet<*> =
        columnSet.flattenRecursively(
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )

    override fun transformSingle(singleColumn: SingleColumn<*>): ColumnSet<*> =
        singleColumn.flattenRecursively(
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )
}

internal fun ColumnSet<*>.flattenRecursively(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnSet<*> = allInternal().transform { cols ->
    if (includeTopLevel) {
        cols.flattenRecursively()
    } else {
        cols
            .filter { it.isColumnGroup() }
            .flatMap { it.children().flattenRecursively() }
    }.filter { includeGroups || !it.isColumnGroup() }
}

internal fun SingleColumn<*>.flattenRecursively(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnSet<*> = (this as ColumnSet<*>).flattenRecursively(
    includeGroups = includeGroups,
    includeTopLevel = includeTopLevel,
)
