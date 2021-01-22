package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.ColumnDataCollector
import org.jetbrains.dataframe.impl.createDataCollector
import kotlin.reflect.KProperty

fun <T, C> DataFrame<T>.split(selector: ColumnSelector<T, C>) = SplitColClause(this, getColumnWithPath(selector), false) { it }
fun <T> DataFrame<T>.split(column: String) = split { column.toColumnDef() }
fun <T, C> DataFrame<T>.split(column: ColumnReference<C>) = split { column }
fun <T, C> DataFrame<T>.split(column: KProperty<C>) = split { column.toColumnDef() }

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

fun <T, C> SplitColClause<T, C, List<*>?>.into(firstName: ColumnReference<*>, vararg otherNames: ColumnReference<*>, nameGenerator: ((Int) -> String)? = null) = into(listOf(firstName.name()) + otherNames.map{ it.name() }, nameGenerator)

fun <T, C> SplitColClause<T, C, List<*>?>.into(firstNames: List<String>, nameGenerator: ((Int) -> String)? = null) = doSplitCols(this) {
    when {
        it < firstNames.size -> firstNames[it]
        nameGenerator != null -> nameGenerator(it - firstNames.size)
        else -> throw Exception()
    }
}


fun <T, C> SplitColClause<T, C, List<*>?>.into(vararg firstNames: String, nameGenerator: ((Int) -> String)? = null) = into(firstNames.asList(), nameGenerator)

fun <T, C> doSplitCols(clause: SplitColClause<T, C, List<*>?>, columnNameGenerator: (Int) -> String): DataFrame<T> {

    val nameGenerator = clause.df.nameGenerator()
    val nrow = clause.df.nrow()
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

fun <T> DataFrame<T>.splitRows(selector: ColumnsSelector<T, List<*>?>) = doSplitRows(this, getColumnsWithPaths(selector))

fun <T, C> SplitColClause<T, C, List<*>?>.intoRows() = doSplitRows(df, listOf(column.data.map(transform).addPath(column.path)))

fun <T> doSplitRows(df: DataFrame<T>, columns: List<ColumnWithPath<List<*>?>>): DataFrame<T> {

    val rowExpandSizes = (0 until df.nrow()).map { row ->
        columns.maxOf { it.data[row]?.size ?: 0 }
    }

    val alignedColumnsData = columns.map {
        val list = it.data.toList()
        val alignedLists: List<List<*>> = list.mapIndexed { rowIndex, rowList ->

            val expectedListSize = rowExpandSizes[rowIndex]
            val actualSize = rowList?.size ?: 0

            if(actualSize != expectedListSize) {
                rowList?.let { it + arrayOfNulls(expectedListSize - actualSize) } ?: arrayOfNulls<Any?>(expectedListSize).asList()
            } else rowList ?: emptyList()
        }
        it.path to alignedLists
    }.toMap()

    val outputRowsCount = rowExpandSizes.sum()

    fun splitIntoRows(df: DataFrame<*>, data: Map<ColumnPath, List<List<*>>>): DataFrame<*> {

        val newColumns = df.columns().map { col ->
            if (col.isGroup()) {
                val group = col.asGroup()
                val newData = data.mapNotNull {
                    if(it.key.isNotEmpty() && it.key[0] == col.name()) it.key.drop(1) to it.value else null
                }.toMap()
                val newDf = splitIntoRows(group.df, newData)
                DataColumn.createGroup(col.name(), newDf)
            } else {
                val targetData = data[listOf(col.name())]
                if (targetData != null) {
                    assert(!col.isTable())
                    val collector = createDataCollector(outputRowsCount)
                    for(row in 0 until col.size)
                        targetData[row].forEach { collector.add(it) }
                    collector.toColumn(col.name())
                } else {
                    val collector = createDataCollector<Any?>(outputRowsCount, col.type)
                    for (row in 0 until col.size) {
                        val expandSize = rowExpandSizes[row]
                        if (expandSize > 0) {
                            val value = col[row]
                            repeat(expandSize) {
                                collector.add(value)
                            }
                        }
                    }
                    if (col.isTable()) DataColumn.createTable(col.name(), collector.values as List<DataFrame<*>>, col.asTable().df)
                    else collector.toColumn(col.name())
                }
            }
        }
        return newColumns.asDataFrame<Unit>()
    }

    return splitIntoRows(df, alignedColumnsData).typed()
}