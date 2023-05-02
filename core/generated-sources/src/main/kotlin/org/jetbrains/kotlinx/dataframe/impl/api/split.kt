package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnDataCollector
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.nrow

internal fun valueToList(value: Any?, splitStrings: Boolean = true): List<Any?> = when (value) {
    null -> emptyList()
    is List<*> -> value
    is AnyFrame -> value.rows().toList()
    else -> if (splitStrings) value.toString().split(",").map { it.trim() } else listOf(value)
}

internal fun <T, C, R> splitImpl(
    clause: SplitWithTransform<T, C, R>,
    columnNamesGenerator: ColumnWithPath<C>.(Int) -> List<String>
): DataFrame<T> {
    val nrow = clause.df.nrow

    val removeResult = clause.df.removeImpl(columns = clause.columns)

    // As we insert multiple columns at once it's possible to encounter a name conflict within a batch of a new columns
    // that's why an old school mutable list is used here to check for name conflicts in intermediate steps of generation
    // of columns to insert
    val columnsToInsert = mutableListOf<ColumnToInsert>()

    removeResult.removedColumns.forEach { node ->

        val column = node.toColumnWithPath<C>()
        val columnCollectors = mutableListOf<ColumnDataCollector>()
        for (row in 0 until nrow) {
            val value = clause.transform(clause.df[row], column.data[row])
            val list = valueToList(value)
            for (j in list.indices) {
                if (columnCollectors.size <= j) {
                    val collector = createDataCollector(nrow)
                    repeat(row) { collector.add(clause.default) }
                    columnCollectors.add(collector)
                }
                columnCollectors[j].add(list[j])
            }
            for (j in list.size until columnCollectors.size)
                columnCollectors[j].add(clause.default)
        }

        val names = columnNamesGenerator(column, columnCollectors.size)
        val sourcePath = node.pathFromRoot()

        columnCollectors.forEachIndexed { i, col ->
            val preferredName = names.getOrNull(i)

            val pathToInsert = if (clause.inward) sourcePath else sourcePath.dropLast(1)
            val name = generateUnusedName(removeResult.df, preferredName, pathToInsert, columnsToInsert)

            val path = pathToInsert + name

            val data = col.toColumn(name)
            columnsToInsert.add(ColumnToInsert(path, data, node))
        }
    }

    return removeResult.df.insertImpl(columnsToInsert)
}

internal fun generateUnusedName(
    df: DataFrame<*>,
    preferredName: String?,
    insertPath: ColumnPath,
    columnsToBeInserted: List<ColumnToInsert>
): String {
    // check if column with this name already exists in the df in the same position in the hierarchy,
    // or we already have a column with this name in the list of columns to be inserted to the same position in the hierarchy
    fun isUsed(name: String) =
        df.getColumnOrNull(insertPath + name) != null || columnsToBeInserted.any { it.insertionPath == insertPath + name }

    fun generateNameVariationByTryingNumericSuffixes(original: String? = null, startSuffix: Int): String {
        var k = startSuffix
        var name = original ?: "split$k"
        while (isUsed(name)) {
            name = original ?: "split"
            name = "${name}${k++}"
        }

        return name
    }

    return if (preferredName == null) {
        generateNameVariationByTryingNumericSuffixes(startSuffix = 1)
    } else {
        generateNameVariationByTryingNumericSuffixes(preferredName, 1)
    }
}

internal fun String.splitDefault() = split(",").map { it.trim() }
