package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.api.colsInternal
import org.jetbrains.kotlinx.dataframe.api.singleInternal
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/**
 * ## Transformable ColumnSet
 * This type of [<code>ColumnsResolver</code>][ColumnsResolver] can be [<code>transformed</code>][transformResolve] before being resolved.
 *
 * This is especially useful for calls like
 * [<code>colsInternal { }</code>][ColumnsResolver.colsInternal].[<code>atAnyDepthImpl()</code>][atAnyDepthImpl],
 * where [<code>atAnyDepthImpl</code>][atAnyDepthImpl] modifies the [<code>ColumnSet</code>][ColumnsResolver]
 * that [<code>colsInternal { }</code>][ColumnsResolver.colsInternal] operates on to include ALL columns, including those inside
 * column groups, before it's evaluated.
 *
 * @see [ColumnsResolver]
 * @see [TransformableSingleColumn]
 * @see [SingleColumn]
 */
@PublishedApi
internal interface TransformableColumnSet<out C> : ColumnSet<C> {
    fun transformResolve(
        context: ColumnResolutionContext,
        transformer: ColumnsResolverTransformer,
    ): List<ColumnWithPath<C>>
}

/**
 * ## Transformable SingleColumn
 * This type of [<code>SingleColumn</code>][SingleColumn] can be [<code>transformed</code>][transformResolveSingle] before being resolved.
 *
 * This is especially useful for calls like
 * [<code>singleInternal { }</code>][ColumnsResolver.singleInternal].[<code>atAnyDepthImpl()</code>][atAnyDepthImpl],
 * where [<code>atAnyDepthImpl</code>][atAnyDepthImpl] modifies the [<code>SingleColumn</code>][SingleColumn]
 * that [<code>singleInternal { }</code>][ColumnsResolver.singleInternal] operates on to include ALL columns, including those inside
 * column groups, before it's evaluated.
 *
 * @see [SingleColumn]
 * @see [TransformableColumnSet]
 * @see [ColumnsResolver]
 */
internal interface TransformableSingleColumn<out C> : SingleColumn<C> {
    fun transformResolveSingle(
        context: ColumnResolutionContext,
        transformer: ColumnsResolverTransformer,
    ): ColumnWithPath<C>?
}

/**
 * ## Columns Resolver Transformer.
 * This contains implementations for both [<code>transform</code>][ColumnSet.transform] and
 * [<code>transformSingle</code>][SingleColumn.transformSingle] and can be passed around.
 */
@PublishedApi
internal interface ColumnsResolverTransformer {
    fun transform(columnsResolver: ColumnsResolver<*>): ColumnsResolver<*>

    fun transformSet(columnSet: ColumnSet<*>): ColumnsResolver<*>

    fun transformSingle(singleColumn: SingleColumn<*>): ColumnsResolver<*>
}
