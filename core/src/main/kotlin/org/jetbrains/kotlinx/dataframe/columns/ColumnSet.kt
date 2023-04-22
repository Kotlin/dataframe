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
}

public interface ColumnSetWithRecursively<out C> : ColumnSet<C> {

    public fun resolveRecursively(
        context: ColumnResolutionContext,
        includeGroups: Boolean = true,
        includeTopLevel: Boolean = true,
    ): List<ColumnWithPath<C>>
}

internal fun <C> ColumnSetWithRecursively<C>.recursivelyImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): ColumnSet<C> = object : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        this@recursivelyImpl.resolveRecursively(
            context = context,
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )
}


public fun interface ColumnSetTransformer {

    public fun transform(columnSet: ColumnSet<*>): ColumnSet<*>
}

public operator fun ColumnSetTransformer.invoke(columnSet: ColumnSet<*>): ColumnSet<*> = transform(columnSet)

public class ColumnResolutionContext internal constructor(
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
