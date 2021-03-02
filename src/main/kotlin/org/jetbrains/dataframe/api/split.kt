package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.ColumnDataCollector
import org.jetbrains.dataframe.impl.createDataCollector
import kotlin.reflect.KProperty

fun <T, C> DataFrame<T>.split(selector: ColumnsSelector<T, C>): SplitClause<T, C> =
    SplitClause(this, selector, false) { it }

fun <T> DataFrame<T>.split(column: String) = split { column.toColumnDef() }
fun <T, C> DataFrame<T>.split(column: ColumnReference<C>) = split { column }
fun <T, C> DataFrame<T>.split(column: KProperty<C>) = split { column.toColumnDef() }

class SplitClause<T, C>(
    val df: DataFrame<T>,
    val columns: ColumnsSelector<T, C>,
    val inward: Boolean,
    val transform: (C) -> Any?
)

fun <T, C> SplitClause<T, C>.by(
    vararg delimiters: String,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0
) = by {
    it?.toString()?.split(*delimiters, ignoreCase = ignoreCase, limit = limit)?.let {
        if (trim) it.map { it.trim() }
        else it
    } ?: emptyList()
}

fun <T, C, K> SplitClause<T, C>.by(splitter: (C) -> List<K>) = SplitClause(df, columns, inward) {
    splitter(it)
}

fun <T, C> SplitClause<T, C>.inward() = SplitClause(df, columns, true, transform)

fun <T, C> SplitClause<T, C>.into(firstName: ColumnReference<*>, vararg otherNames: ColumnReference<*>) =
    into(listOf(firstName.name()) + otherNames.map { it.name() })

fun <T, C> SplitClause<T, C>.intoMany(namesProvider: (ColumnWithPath<C>, numberOfNewColumns: Int) -> List<String>) =
    doSplitCols(this, namesProvider)

fun <T, C> SplitClause<T, C>.into(vararg names: String, extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null) = into(names.toList(), extraNamesGenerator)

fun <T, C> SplitClause<T, C>.into(names: List<String>, extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null) = intoMany { col, numberOfNewCols ->
    if(extraNamesGenerator != null && names.size < numberOfNewCols)
        names + (1 .. (numberOfNewCols-names.size)).map { extraNamesGenerator(col, it) }
    else names
}

internal fun valueToList(value: Any?) = when (value) {
    null -> emptyList()
    is List<*> -> value
    else -> value.toString().split(",").map {it.trim()}
}

fun <T, C> doSplitCols(
    clause: SplitClause<T, C>,
    columnNamesGenerator: ColumnWithPath<C>.(Int) -> List<String>
): DataFrame<T> {

    val nameGenerator = clause.df.nameGenerator()
    val nrow = clause.df.nrow()

    val removeResult = clause.df.doRemove(clause.columns)

    val toInsert = removeResult.removedColumns.flatMap { node ->

        val column = node.toColumnWithPath<C>(clause.df)
        val columnCollectors = mutableListOf<ColumnDataCollector>()
        for (row in 0 until nrow) {
            val value = clause.transform(column.data[row])
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
        if(names.size < columnCollectors.size)
            names = names + (1..(columnCollectors.size - names.size)).map {"splitted$it" }

        columnCollectors.mapIndexed { i, col ->

            val name = nameGenerator.addUnique(names[i])
            val sourcePath = node.pathFromRoot()
            val path = if (clause.inward) sourcePath + name else sourcePath.dropLast(1) + name
            val data = col.toColumn(name)
            ColumnToInsert(path, node, data)
        }
    }

    return removeResult.df.doInsert(toInsert)
}

fun <T> DataFrame<T>.splitRows(selector: ColumnsSelector<T, List<*>?>) =
    doSplitRows(this, getColumnsWithPaths(selector))

fun <T, C> SplitClause<T, C>.intoRows() = doSplitRows(
    df,
    df.getColumnsWithPaths(columns).map { it.data.map { valueToList(transform(it)) }.addPath(it.path, df) })

fun <T> doSplitRows(df: DataFrame<T>, columns: List<ColumnWithPath<List<*>?>>): DataFrame<T> {

    val rowExpandSizes = (0 until df.nrow()).map { row ->
        columns.maxOf { it.data[row]?.size ?: 0 }
    }

    val alignedColumnsData = columns.map {
        val list = it.data.toList()
        val alignedLists: List<List<*>> = list.mapIndexed { rowIndex, rowList ->

            val expectedListSize = rowExpandSizes[rowIndex]
            val actualSize = rowList?.size ?: 0

            if (actualSize != expectedListSize) {
                rowList?.let { it + arrayOfNulls(expectedListSize - actualSize) }
                    ?: arrayOfNulls<Any?>(expectedListSize).asList()
            } else rowList ?: emptyList()
        }
        it.path to alignedLists
    }.toMap()

    val outputRowsCount = rowExpandSizes.sum()

    fun splitIntoRows(df: AnyFrame, data: Map<ColumnPath, List<List<*>>>): AnyFrame {

        val newColumns = df.columns().map { col ->
            if (col.isGroup()) {
                val group = col.asGroup()
                val newData = data.mapNotNull {
                    if (it.key.isNotEmpty() && it.key[0] == col.name()) it.key.drop(1) to it.value else null
                }.toMap()
                val newDf = splitIntoRows(group.df, newData)
                DataColumn.create(col.name(), newDf)
            } else {
                val targetData = data[listOf(col.name())]
                if (targetData != null) {
                    assert(!col.isTable())
                    val collector = createDataCollector(outputRowsCount)
                    for (row in 0 until col.size)
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
                    if (col.isTable()) DataColumn.create(
                        col.name(),
                        collector.values as List<AnyFrame>,
                        col.asTable().df
                    )
                    else collector.toColumn(col.name())
                }
            }
        }
        return newColumns.asDataFrame<Unit>()
    }

    return splitIntoRows(df, alignedColumnsData).typed()
}