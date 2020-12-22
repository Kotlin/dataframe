package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.ColumnDataCollector
import org.jetbrains.dataframe.impl.TypedColumnDataCollector
import org.jetbrains.dataframe.impl.createDataCollector

class SplitColClause<T, C, out R>(val df: DataFrame<T>, val column: ColumnWithPath<C>, val inward: Boolean, val transform: (C) -> R)

fun <T, C> SplitColClause<T, C, String?>.by(vararg delimiters: Char, ignoreCase: Boolean = false, limit: Int = 0) = SplitColClause(df, column, inward) {
    transform(it)?.split(*delimiters, ignoreCase = ignoreCase, limit = limit)
}

fun <T, C> SplitColClause<T, C, String?>.by(vararg delimiters: String, trim: Boolean = true, ignoreCase: Boolean = false, limit: Int = 0) = SplitColClause(df, column, inward) {
    transform(it)?.split(*delimiters, ignoreCase = ignoreCase, limit = limit)?.let {
        if (trim) it.map { it.trim() }
        else it
    }
}

fun <T, C, R, K> SplitColClause<T, C, R?>.by(splitter: (R) -> List<K>) = SplitColClause(df, column, inward) {
    transform(it)?.let(splitter)
}

fun <T, C> SplitColClause<T, C, List<*>?>.intoParts() = into { "part$it" }

fun <T, C> SplitColClause<T, C, List<*>?>.inward() = SplitColClause(df, column, true, transform)

fun <T, C> SplitColClause<T, C, List<*>?>.into(vararg firstNames: String, nameGenerator: ((Int) -> String)? = null) = doSplitCols(this) {
    when {
        it < firstNames.size -> firstNames[it]
        nameGenerator != null -> nameGenerator(it - firstNames.size)
        else -> throw Exception()
    }
}

fun <T, C> doSplitCols(clause: SplitColClause<T, C, List<*>?>, columnNameGenerator: (Int) -> String): DataFrame<T> {

    val nameGenerator = clause.df.nameGenerator()
    val nrow = clause.df.nrow
    val columnCollectors = mutableListOf<ColumnDataCollector>()
    for (row in 0 until nrow) {
        val list = clause.transform(clause.column.data[row])
        val listSize = list?.size ?: 0
        for (j in 0 until listSize) {
            if (columnCollectors.size <= j) {
                val collector = createDataCollector(nrow)
                repeat(row) { collector.add(null) }
                columnCollectors.add(collector)
            }
            columnCollectors[j].add(list!![j])
        }
        for (j in listSize until columnCollectors.size)
            columnCollectors[j].add(null)
    }

    val removeResult = clause.df.doRemove { clause.column }
    val singleRemoved = removeResult.removedColumns.single()

    val toInsert = columnCollectors.mapIndexed { i, col ->

        val name = nameGenerator.addUnique(columnNameGenerator(i))
        val sourcePath = singleRemoved.pathFromRoot()
        val path = if(clause.inward) sourcePath + name else sourcePath.dropLast(1) + name
        val data = col.toColumn(name)
        ColumnToInsert(path, singleRemoved, data)
    }
    return removeResult.df.doInsert(toInsert)
}

fun <T, C> DataFrame<T>.split(selector: ColumnSelector<T, C>) = SplitColClause(this, getColumnWithPath(selector), false) { it }

fun <T> DataFrame<T>.splitRows(selector: ColumnSelector<T, List<*>?>) = split(selector).intoRows()

fun <T, C> SplitColClause<T, C, List<*>?>.intoRows(): DataFrame<T> {

    val path = column.path
    val transformedColumn = column.data.map(transform)
    val list = transformedColumn.toList()
    val outputRowsCount = list.sumBy {
        it?.size ?: 0
    }

    fun splitIntoRows(df: DataFrame<*>, path: ColumnPath?, list: List<List<*>?>): DataFrame<*> {

        val newColumns = df.columns.map { col ->
            if (col.isGrouped()) {
                val group = col.asGrouped()
                val newPath = path?.let { if(it.size > 0 && it[0] == col.name) it.drop(1) else null }
                val newDf = splitIntoRows(group.df, newPath, list)
                ColumnData.createGroup(col.name, newDf)
            } else {
                if (path != null && path.size == 1 && path[0] == col.name) {
                    val collector = createDataCollector(outputRowsCount)
                    for(row in 0 until col.size)
                        list[row]?.forEach { collector.add(it) }
                    collector.toColumn(col.name)
                } else {
                    val collector = TypedColumnDataCollector<Any?>(outputRowsCount, col.type)
                    for (row in 0 until col.size) {
                        val l = list[row]
                        if (l != null && l.size > 0) {
                            val value = col[row]
                            repeat(l.size) {
                                collector.add(value)
                            }
                        }
                    }
                    if (col.isTable()) ColumnData.createTable(col.name, collector.values as List<DataFrame<*>>, col.asTable().df)
                    else collector.toColumn(col.name)
                }
            }
        }
        return newColumns.asDataFrame<Unit>()
    }

    return splitIntoRows(df, path, list).typed()
}