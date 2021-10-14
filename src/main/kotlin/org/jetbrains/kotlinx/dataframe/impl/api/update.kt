package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.UpdateClause
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type

@PublishedApi
internal fun <T, C> updateImpl(clause: UpdateClause<T, C>, expression: (DataRow<T>, DataColumn<C>) -> Any?): DataFrame<T> {
    val removeResult = clause.df.removeImpl(clause.selector)

    val nrow = clause.df.nrow()
    val toInsert = removeResult.removedColumns.map {
        val srcColumn = it.data.column as DataColumn<C>
        val collector = when {
            clause.toNull -> createDataCollector(nrow, srcColumn.type)
            clause.typeSuggestions != null -> createDataCollector(nrow, clause.typeSuggestions)
            clause.targetType != null -> createDataCollector(nrow, clause.targetType)
            else -> createDataCollector(nrow, srcColumn.type())
        }
        if (clause.filter == null) {
            clause.df.forEach { row ->
                collector.add(expression(row, srcColumn))
            }
        } else {
            clause.df.forEach { row ->
                val currentValue = srcColumn[row.index]
                val newValue = if (clause.filter.invoke(row, currentValue)) expression(row, srcColumn) else currentValue
                collector.add(newValue)
            }
        }

        val newColumn = collector.toColumn(srcColumn.name())

        ColumnToInsert(it.pathFromRoot(), newColumn, it)
    }
    return removeResult.df.insertImpl(toInsert)
}
