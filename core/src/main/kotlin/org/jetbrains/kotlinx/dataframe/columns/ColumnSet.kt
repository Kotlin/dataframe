package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.asSingleColumn

/**
 * ## ColumnSet
 *
 * Entity that can be resolved into a list of [columns][DataColumn].
 * Unlike an actual "set", repeated columns are allowed.
 * Just like [SingleColumn], this is a [ColumnsResolver].
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
