package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.*

/**
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 * @param C common type of resolved columns
 */
public interface ColumnSet<out C> {

    /**
     * Resolves this [ColumnSet] as a [List]<[ColumnWithPath]<[C]>>.
     * In many cases this function [transforms][ColumnSet.transform] a parent [ColumnSet] to reach
     * the current [ColumnSet] result.
     */
    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>

    /**
     * Resolves this [ColumnSet] as a [List]<[ColumnWithPath]<[C]>> after applying [transformer] to the parent
     * [ColumnSet]. This essentially injects a call right before the current in the [ColumnSet.resolve] chain.
     */
    public fun resolveAfterTransform(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): List<ColumnWithPath<C>>
}

public interface ColumnSetTransformer {

//    @Deprecated("see if this can be removed")
//    public fun transformRemainingSingle(singleColumn: SingleColumn<*>): SingleColumn<*>

//    public fun transformSingle(singleColumn: SingleColumn<*>): ColumnSet<*>

    public fun transform(columnSet: ColumnSet<*>): ColumnSet<*>
}

public class ColumnResolutionContext internal constructor(
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
