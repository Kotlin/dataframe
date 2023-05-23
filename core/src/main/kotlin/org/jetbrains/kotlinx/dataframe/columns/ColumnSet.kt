package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn

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

internal fun <C> SingleColumn<C>.asColumnSet(): ColumnSet<C> =
    object : ColumnSet<C>, SingleColumn<C> by this {}
