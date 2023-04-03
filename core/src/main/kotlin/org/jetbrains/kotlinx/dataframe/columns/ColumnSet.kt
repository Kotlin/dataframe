package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.recursively
import org.jetbrains.kotlinx.dataframe.impl.columns.transform

/**
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 * @param C common type of resolved columns
 */
public interface ColumnSet<out C> {

    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

/**
 * Same as [ColumnSet] but with a reference to the original
 * [ColumnSet] that was used to create this one as well as the [converter] function
 * given to it by the [ColumnSet.transform] function.
 *
 * Used for [ColumnSetWithQuery.recursively] function.
 */
public interface ColumnSetWithQuery<A, out C> : ColumnSet<C> {

    public val originalColumnSet: ColumnSet<A>

    public val converter: (List<ColumnWithPath<A>>) -> List<ColumnWithPath<C>>
}

public class ColumnResolutionContext internal constructor (
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
