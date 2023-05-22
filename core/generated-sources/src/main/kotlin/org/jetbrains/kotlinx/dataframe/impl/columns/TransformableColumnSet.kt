package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/**
 * ## Transformable ColumnSet
 * This type of [ColumnSet] can be [transformed][transformResolve] before being resolved.
 *
 * This is especially useful for calls like
 * [cols { }][ColumnsSelectionDsl.cols].[recursively()][ColumnsSelectionDsl.recursively],
 * where [recursively][ColumnsSelectionDsl.recursively] modifies the [ColumnSet][ColumnSet]
 * that [cols { }][ColumnsSelectionDsl.cols] operates on before it's evaluated.
 *
 * @see [ColumnSet]
 * @see [TransformableSingleColumn]
 * @see [SingleColumn]
 */
public interface TransformableColumnSet<out C> : ColumnSet<C> {
    public fun transformResolve(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): List<ColumnWithPath<C>>
}

/**
 * ## Transformable SingleColumn
 * This type of [SingleColumn] can be [transformed][transformResolveSingle] before being resolved.
 *
 * This is especially useful for calls like
 * [first { }][ColumnsSelectionDsl.first].[recursively()][ColumnsSelectionDsl.recursively],
 * where [recursively][ColumnsSelectionDsl.recursively] modifies the [ColumnSet][ColumnSet]
 * that [first { }][ColumnsSelectionDsl.first] operates on before it's evaluated.
 *
 * @see [SingleColumn]
 * @see [TransformableColumnSet]
 * @see [ColumnSet]
 */
public interface TransformableSingleColumn<out C> : SingleColumn<C> {
    public fun transformResolveSingle(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): ColumnWithPath<C>?
}

/**
 * ## Column set transformer.
 * This contains implementations for both [transform][ColumnSet.transform] and
 * [transformSingle][SingleColumn.transformSingle] and can be passed around.
 */
public interface ColumnSetTransformer {
    public fun transform(columnSet: ColumnSet<*>): ColumnSet<*>

    public fun transformSingle(singleColumn: SingleColumn<*>): ColumnSet<*>
}
