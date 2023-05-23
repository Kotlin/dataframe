package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.columns.*

internal class ConfiguredAggregateColumn<C> private constructor(
    val columns: ColumnsResolver<C>,
    private val default: C? = null,
    private val newPath: ColumnPath? = null,
) : ColumnSet<C> {

    private fun ColumnWithPath<C>.toDescriptor(keepName: Boolean): AggregateColumnDescriptor<C> =
        when (val col = this) {
            is AggregateColumnDescriptor<C> -> {
                val path = if (keepName) newPath?.plus(col.newPath ?: col.column.shortPath()) ?: col.newPath
                else newPath ?: col.newPath
                AggregateColumnDescriptor(col.column, default ?: col.default, path)
            }

            else -> AggregateColumnDescriptor(col, default, if (keepName) newPath?.plus(col.name) else newPath)
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

    companion object {

        fun <C> withDefault(src: ColumnsResolver<C>, default: C?): ColumnSet<C> = when (src) {
            is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, default, src.newPath)
            else -> ConfiguredAggregateColumn(src, default, null)
        }

        fun <C> withPath(src: ColumnsResolver<C>, newPath: ColumnPath): ColumnSet<C> = when (src) {
            is ConfiguredAggregateColumn<C> -> ConfiguredAggregateColumn(src.columns, src.default, newPath)
            else -> ConfiguredAggregateColumn(src, null, newPath)
        }
    }
}
