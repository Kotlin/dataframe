package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ReadonlyTreeNode
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ReferenceData
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.getAncestor

internal data class ColumnToInsert(
    val insertionPath: ColumnPath,
    val column: AnyBaseCol,
    val referenceNode: ReadonlyTreeNode<ReferenceData>? = null
)

@PublishedApi
internal fun <T> DataFrame<T>.insertImpl(path: ColumnPath, column: AnyCol): DataFrame<T> =
    insertImpl(this, listOf(ColumnToInsert(path, column)))

@JvmName("insertImplT")
internal fun <T> DataFrame<T>.insertImpl(columns: List<ColumnToInsert>) =
    insertImpl(this, columns)

internal fun <T> insertImpl(df: DataFrame<T>?, columns: List<ColumnToInsert>) =
    insertImpl(df, columns, columns.firstOrNull()?.referenceNode?.getRoot(), 0)

internal fun dataFrameOf(columns: List<ColumnToInsert>) =
    insertImpl<Unit>(null, columns, columns.firstOrNull()?.referenceNode?.getRoot(), 0)

internal fun <T> insertImpl(
    df: DataFrame<T>?,
    columns: List<ColumnToInsert>,
    treeNode: ReadonlyTreeNode<ReferenceData>?,
    depth: Int
): DataFrame<T> {
    return if (columns.isEmpty()) {
        df ?: DataFrame.empty().cast()
    } else {
        insertImplDataFrame(df, columns, treeNode, depth)
    }
}

internal fun <T> insertImplDataFrame(
    df: DataFrame<T>?,
    columns: List<ColumnToInsert>,
    treeNode: ReadonlyTreeNode<ReferenceData>?,
    depth: Int
): DataFrame<T> {
    class DfAdapter<T>(val df: DataFrame<T>) : DataFrameLikeContainer<BaseColumn<*>> {
        override fun columns(): List<DataColumn<*>> {
            return this.df.columns()
        }
    }

    return if (columns.isEmpty()) df ?: DataFrame.empty().cast() else {
        insertImplGenericContainer(
            df?.let { DfAdapter(it) },
            columns.map { ColumnToInsert1(it.insertionPath, it.column, it.referenceNode) },
            treeNode,
            depth,
            factory = { DfAdapter(it.toDataFrame().cast()) },
            empty = DfAdapter(DataFrame.Empty.cast()),
            rename = { rename(it) },
            createColumnGroup = { name, columns ->
                DataColumn.createColumnGroup(name, columns.toDataFrame())
            }
        ).df
    }
}

internal interface DataFrameLikeContainer<T : Col> {
    fun columns(): List<T>
}

internal fun <T : DataFrameLikeContainer<Column>, Column : Col, ColumnGroup> insertImplGenericContainer(
    df: T?,
    columns: List<ColumnToInsert1<Column>>,
    treeNode: ReadonlyTreeNode<ReferenceData>?,
    depth: Int,
    factory: (List<Column>) -> T,
    empty: T,
    rename: Column.(String) -> Column,
    createColumnGroup: (String, List<Column>) -> Column,
): T where ColumnGroup : MyColumnGroup<Column> {
    if (columns.isEmpty()) return df ?: empty

    val res: List<Column> = insertImplGenericTree(
        columns,
        treeNode,
        depth,
        df?.columns(),
        rename = rename,
        createColumnGroup
    )
    return factory(res)
}

public interface Col {
    public fun name(): String
}

public interface MyColumnGroup<Column : Col> : Col {
    public fun columns(): List<Column>
}

internal data class ColumnToInsert1<Column : Col> (
    val insertionPath: ColumnPath,
    val column: Column,
    val referenceNode: ReadonlyTreeNode<ReferenceData>? = null
)

internal fun <Column : Col, ColumnGroup : MyColumnGroup<Column>> insertImplGenericTree(
    columns: List<ColumnToInsert1<Column>>,
    treeNode: ReadonlyTreeNode<ReferenceData>?,
    depth: Int,
    existingColumns: List<Column>?,
    rename: Column.(String) -> Column,
    createColumnGroup: (String, List<Column>) -> Column,
): List<Column> {
    val childDepth = depth + 1

    val columnsMap = columns.groupBy { it.insertionPath[depth] }.toMutableMap() // map: columnName -> columnsToAdd

    val newColumns = mutableListOf<Column>()

    // insert new columns under existing
    existingColumns?.forEach {
        val subTree = columnsMap[it.name()]
        if (subTree != null) {
            // assert that new columns go directly under current column so they have longer paths
            val invalidPath = subTree.firstOrNull { it.insertionPath.size == childDepth }
            check(invalidPath == null) { "Can not insert column `" + invalidPath!!.insertionPath.joinToString(".") + "` because column with this path already exists in DataFrame" }
            val group = it as? ColumnGroup
            check(group != null) { "Can not insert columns under a column '${it.name()}', because it is not a column group" }
            val column = if (subTree.isEmpty()) {
                group as Column
            } else {
                val res = insertImplGenericTree(
                    subTree,
                    treeNode?.get(it.name()),
                    childDepth,
                    group.columns(),
                    rename,
                    createColumnGroup
                )
                createColumnGroup(group.name(), res)
            }
            val newCol = column
            newColumns.add(newCol)
            columnsMap.remove(it.name())
        } else newColumns.add(it)
    }

    // collect new columns to insert
    val columnsToAdd = columns.mapNotNull {
        val name = it.insertionPath[depth]
        val subTree = columnsMap[name]
        if (subTree != null) {
            columnsMap.remove(name)

            // look for columns in subtree that were originally located at the current insertion path
            // find the minimal original index among them
            // new column will be inserted at that position
            val minIndex = subTree.minOf {
                if (it.referenceNode == null) Int.MAX_VALUE
                else {
                    var col = it.referenceNode
                    if (col.depth > depth) col = col.getAncestor(depth + 1)
                    if (col.parent === treeNode) {
                        if (col.data.wasRemoved) col.data.originalIndex else col.data.originalIndex + 1
                    } else Int.MAX_VALUE
                }
            }

            minIndex to (name to subTree)
        } else null
    }.sortedBy { it.first } // sort by insertion index

    val removedSiblings = treeNode?.children
    var k = 0 // index in 'removedSiblings' list
    var insertionIndexOffset = 0

    columnsToAdd.forEach { (insertionIndex, pair) ->
        val (name, columns) = pair

        // adjust insertion index by number of columns that were removed before current index
        if (removedSiblings != null) {
            while (k < removedSiblings.size && removedSiblings[k].data.originalIndex < insertionIndex) {
                if (removedSiblings[k].data.wasRemoved) insertionIndexOffset--
                k++
            }
        }

        val nodeToInsert =
            columns.firstOrNull { it.insertionPath.size == childDepth } // try to find existing node to insert
        val newCol = if (nodeToInsert != null) {
            val column = nodeToInsert.column
            if (columns.size > 1) {
                check(columns.count { it.insertionPath.size == childDepth } == 1) { "Can not insert more than one column into the path ${nodeToInsert.insertionPath}" }
                column as ColumnGroup
                val columns1 = columns.filter { it.insertionPath.size > childDepth }
                val newDf = if (columns1.isEmpty()) {
                    listOf(column)
                } else {
                    insertImplGenericTree(
                        columns1, treeNode?.get(name),
                        childDepth,
                        column.columns(),
                        rename,
                        createColumnGroup
                    )
                }
                createColumnGroup(name, newDf)
            } else column.rename(name)
        } else {
            val newDf =
                if (columns.isEmpty()) {
                    emptyList()
                } else {
                    insertImplGenericTree(columns, treeNode?.get(name), childDepth, emptyList(), rename, createColumnGroup)
                }
            createColumnGroup(name, newDf) // new node needs to be created
        }
        if (insertionIndex == Int.MAX_VALUE) {
            newColumns.add(newCol)
        } else {
            newColumns.add(insertionIndex + insertionIndexOffset, newCol)
            insertionIndexOffset++
        }
    }

    return newColumns
}
