package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnResolutionContext

/**
 * A set of columns. Used in column selectors API
 * @param C common type of columns
 */
public interface Columns<out C> {

    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}
