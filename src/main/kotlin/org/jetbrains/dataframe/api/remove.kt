package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.TreeNode
import kotlin.reflect.KProperty

infix operator fun <T> DataFrame<T>.minus(column: String) = remove(column)
infix operator fun <T> DataFrame<T>.minus(column: Column) = remove(column)
infix operator fun <T> DataFrame<T>.minus(cols: Iterable<Column>) = remove(cols)
infix operator fun <T> DataFrame<T>.minus(cols: ColumnsSelector<T, *>) = remove(cols)
fun <T> DataFrame<T>.remove(selector: ColumnsSelector<T, *>) = remove(getColumns(selector))
fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>) = remove(getColumns(columns))
fun <T> DataFrame<T>.remove(vararg columns: String) = remove(getColumns(columns))
fun <T> DataFrame<T>.remove(vararg cols: Column) = remove(cols.toList())
fun <T> DataFrame<T>.remove(cols: Iterable<Column>) = doRemove(cols).first
internal fun <T> DataFrame<T>.doRemove(cols: Iterable<Column>): Pair<DataFrame<T>, TreeNode<ColumnPosition>> {

    val colPaths = cols.map { it.getPath() }

    val root = TreeNode.createRoot(ColumnPosition(-1, false, null))

    fun dfs(cols: Iterable<DataCol>, paths: List<List<String>>, node: TreeNode<ColumnPosition>): DataFrame<*>? {
        val depth = node.depth
        val children = paths.groupBy { it[depth] }
        val newCols = mutableListOf<DataCol>()

        cols.forEachIndexed { index, column ->
            val childPaths = children[column.name]
            if (childPaths != null) {
                val node = node.addChild(column.name, ColumnPosition(index, true, null))
                if (childPaths.all { it.size > depth + 1 }) {
                    val groupCol = (column as GroupedColumn<*>)
                    val newDf = dfs(groupCol.df.columns, childPaths, node)
                    if (newDf != null) {
                        val newCol = groupCol.withDf(newDf)
                        newCols.add(newCol)
                        node.data.wasRemoved = false
                    }
                } else {
                    node.data.column = column
                }
            } else newCols.add(column)
        }
        if (newCols.isEmpty()) return null
        return newCols.asDataFrame<Unit>()
    }

    val newDf = dfs(columns, colPaths, root) ?: emptyDataFrame(nrow)
    return newDf.typed<T>() to root
}