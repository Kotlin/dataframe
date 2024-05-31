package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.shortPath

internal class ConfiguredAggregateColumn<C> private constructor(
    val columns: ColumnsResolver<C>,
    private val default: C? = null,
    private val newPath: ColumnPath? = null,
) : SingleColumn<C>, ColumnSet<C> {

    private fun ColumnWithPath<C>.toDescriptor(keepName: Boolean): AggregateColumnDescriptor<C> =
        when (val col = this) {
            is AggregateColumnDescriptor<C> -> {
                // Fix for K2 smart-casting changes
                val currentDefault = this@ConfiguredAggregateColumn.default
                val currentNewPath = this@ConfiguredAggregateColumn.newPath

                val newPath = when {
                    currentNewPath == null -> col.newPath
                    keepName -> currentNewPath + (col.newPath ?: col.column.shortPath())
                    else -> currentNewPath
                }
                AggregateColumnDescriptor(
                    column = col.column,
                    default = currentDefault ?: col.default,
                    newPath = newPath,
                )
            }

            else ->
                AggregateColumnDescriptor(
                    column = col,
                    default = default,
                    newPath = if (keepName) newPath?.plus(col.name) else newPath,
                )
        }

    private fun resolve(context: ColumnResolutionContext, columns: ColumnsResolver<C>): List<ColumnWithPath<C>> {
        val resolved = columns.resolve(context)
        return if (resolved.size == 1) {
            listOf(resolved[0].toDescriptor(false))
        } else {
            resolved.map {
                it.toDescriptor(true)
            }
        }
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        resolve(context, columns)

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
        resolve(context, columns).singleOrNull()

    companion object {

        fun <C> withPath(src: SingleColumn<C>, newPath: ColumnPath): SingleColumn<C> =
            when (src) {
                is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, src.default, newPath)
                else -> ConfiguredAggregateColumn(src, null, newPath)
            }

        fun <C> withDefault(src: SingleColumn<C>, default: C?): SingleColumn<C> =
            when (src) {
                is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, default, src.newPath)
                else -> ConfiguredAggregateColumn(src, default, null)
            }

        fun <C> withPath(src: ColumnSet<C>, newPath: ColumnPath): ColumnSet<C> =
            when (src) {
                is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, src.default, newPath)
                else -> ConfiguredAggregateColumn(src, null, newPath)
            }

        fun <C> withDefault(src: ColumnSet<C>, default: C?): ColumnSet<C> =
            when (src) {
                is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, default, src.newPath)
                else -> ConfiguredAggregateColumn(src, default, null)
            }
    }
}
