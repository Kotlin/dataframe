package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyMany
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.SplitClauseWithTransform
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnDataCollector
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator

internal fun valueToList(value: Any?, splitStrings: Boolean = true): List<Any?> = when (value) {
    null -> emptyList()
    is AnyMany -> value
    is AnyFrame -> value.rows().toList()
    else -> if (splitStrings) value.toString().split(",").map { it.trim() } else listOf(value)
}

internal fun <T, C, R> splitImpl(
    clause: SplitClauseWithTransform<T, C, R>,
    columnNamesGenerator: ColumnWithPath<C>.(Int) -> List<String>
): DataFrame<T> {
    val nameGenerator = clause.df.nameGenerator()
    val nrow = clause.df.nrow()

    val removeResult = clause.df.removeImpl(clause.columns)

    val toInsert = removeResult.removedColumns.flatMap { node ->

        val column = node.toColumnWithPath<C>(clause.df)
        val columnCollectors = mutableListOf<ColumnDataCollector>()
        for (row in 0 until nrow) {
            val value = clause.transform(clause.df[row], column.data[row])
            val list = valueToList(value)
            for (j in list.indices) {
                if (columnCollectors.size <= j) {
                    val collector = createDataCollector(nrow)
                    repeat(row) { collector.add(null) }
                    columnCollectors.add(collector)
                }
                columnCollectors[j].add(list[j])
            }
            for (j in list.size until columnCollectors.size)
                columnCollectors[j].add(null)
        }

        var names = columnNamesGenerator(column, columnCollectors.size)
        if (names.size < columnCollectors.size) {
            names = names + (1..(columnCollectors.size - names.size)).map { "splitted$it" }
        }

        columnCollectors.mapIndexed { i, col ->

            val name = nameGenerator.addUnique(names[i])
            val sourcePath = node.pathFromRoot()
            val path = if (clause.inward) sourcePath + name else sourcePath.dropLast(1) + name
            val data = col.toColumn(name)
            ColumnToInsert(path, data, node)
        }
    }

    return removeResult.df.insertImpl(toInsert)
}

internal fun String.splitDefault() = split(",").map { it.trim() }
