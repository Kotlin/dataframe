package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.asSingleColumn

/**
 * ## ColumnSet
 *
 * Entity that can be resolved into a list of [<code>columns</code>][DataColumn].
 * Just like [<code>SingleColumn</code>][SingleColumn], this is a [<code>ColumnsResolver</code>][ColumnsResolver].
 *
 * @see [SingleColumn]
 * @see [ColumnsResolver]
 */
public interface ColumnSet<out C> : ColumnsResolver<C>

internal fun <C> ColumnsResolver<C>.asColumnSet(): ColumnSet<C> =
    when (this) {
        is ColumnSet<C> -> this

        else -> object : ColumnSet<C> {
            override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
                this@asColumnSet.resolve(context)
        }
    }

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <C> SingleColumn<C>.asColumnSet(): ColumnSet<C> =
    when (this) {
        is ColumnSet<*> -> this as ColumnSet<C>
        else -> object : ColumnSet<C>, SingleColumn<C> by this {}
    }

internal fun <C> ColumnsSelectionDsl<C>.asColumnSet(): ColumnSet<DataRow<C>> = asSingleColumn().asColumnSet()
