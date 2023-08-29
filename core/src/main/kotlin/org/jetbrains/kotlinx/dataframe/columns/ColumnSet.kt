package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.asSingleColumn

/**
 * ## ColumnSet
 *
 * Entity that can be resolved into a list of [columns][DataColumn].
 * Just like [SingleColumn], this is a [ColumnsResolver].
 *
 * @see [SingleColumn]
 * @see [ColumnsResolver]
 */
public interface ColumnSet<out C> : ColumnsResolver<C>

internal fun <C> ColumnsResolver<C>.asColumnSet(): ColumnSet<C> =
    object : ColumnSet<C>, ColumnsResolver<C> by this {}

@PublishedApi
internal fun <C> SingleColumn<C>.asColumnSet(): ColumnSet<C> =
    object : ColumnSet<C>, SingleColumn<C> by this {}

internal fun <C> ColumnsSelectionDsl<C>.asColumnSet(): ColumnSet<DataRow<C>> =
    asSingleColumn().asColumnSet()
