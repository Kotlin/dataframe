package org.jetbrains.kotlinx.dataframe.impl.columns.tree

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath

internal tailrec fun <T> ReadonlyTreeNode<T>.getAncestor(depth: Int): ReadonlyTreeNode<T> {
    if (depth > this.depth) throw UnsupportedOperationException()
    if (depth == this.depth) return this
    val p = parent ?: throw UnsupportedOperationException()
    return p.getAncestor(depth)
}

internal fun <T> TreeNode<T?>.getOrPut(path: ColumnPath) = getOrPutEmpty(path, null)

internal fun <T> TreeNode<T>.getOrPutEmpty(path: ColumnPath, emptyData: T): TreeNode<T> = getOrPut(path) { emptyData }

internal fun <T> TreeNode<T?>.put(path: ColumnPath, data: T): TreeNode<T?> = getOrPut(path).also { it.data = data }

internal fun <T> TreeNode<T>.getOrPut(path: ColumnPath, createData: (ColumnPath) -> T): TreeNode<T> {
    var node = this
    path.indices.forEach {
        node = node.getOrPut(path[it]) { createData(path.take(it + 1)) }
    }
    return node
}

/**
 * Traverses all children in the tree in depth-first order and returns the top-most nodes that satisfy
 * [yieldCondition]. This means that if a node satisfies [yieldCondition], its children are not traversed, regardless of
 * whether they satisfy [yieldCondition] or not.
 */
internal fun <T> TreeNode<T>.topmostChildren(yieldCondition: (TreeNode<T>) -> Boolean): List<TreeNode<T>> = allChildren(
    enterCondition = { !yieldCondition(it) },
    yieldCondition = yieldCondition,
)

internal fun <T> TreeNode<T>.topmostChildrenExcluding(excludeRoot: TreeNode<*>): List<TreeNode<T>> {
    val result = mutableListOf<TreeNode<T>>()
    fun doDfs(node: TreeNode<T>, exclude: TreeNode<*>) {
        if (exclude.children.isNotEmpty()) {
            node.children.filter { !exclude.contains(it.name) }.forEach { result.add(it) }
            exclude.children.forEach {
                val srcNode = node[it.name]
                if (srcNode != null) {
                    doDfs(srcNode, it)
                }
            }
        }
    }
    doDfs(this, excludeRoot)
    return result
}

/**
 * Mapping function for [ReadonlyTreeNodes][ReadonlyTreeNode] (like [TreeNode])
 * which can convert the tree-structure (depth-first) to any other tree-type structure (e.g. [DataFrame]).
 */
@Suppress("UNCHECKED_CAST")
internal fun <T : ReadonlyTreeNode<*>, R> T.map(operation: (node: T, children: List<R>) -> R): R {
    val children = children.map { (it as T).map(operation) }
    return operation(this, children)
}

internal fun <T> TreeNode<T?>.allChildrenNotNull(): List<TreeNode<T>> =
    allChildren { it.data != null } as List<TreeNode<T>>

internal fun <T> TreeNode<T?>.topmostChildrenNotNull() = topmostChildren { it.data != null } as List<TreeNode<T>>

internal fun TreeNode<ColumnPosition>.allRemovedColumns() = allChildren { it.data.wasRemoved && it.data.column != null }

internal fun TreeNode<ColumnPosition>.allWithColumns() = allChildren { it.data.column != null }

internal fun Iterable<ColumnWithPath<*>>.flattenRecursively(): List<ColumnWithPath<*>> {
    val result = mutableListOf<ColumnWithPath<*>>()

    fun flattenRecursively(cols: Iterable<ColumnWithPath<*>>) {
        cols.forEach {
            result.add(it)
            val path = it.path
            if (it.data.isColumnGroup()) {
                flattenRecursively(
                    it.data.asColumnGroup()
                        .columns()
                        .map { it.addPath(path + it.name()) },
                )
            }
        }
    }
    flattenRecursively(this)
    return result
}

internal fun List<ColumnWithPath<*>>.collectTree() = collectTree(null) { it }
internal fun <D> List<ColumnWithPath<*>>.collectTree(emptyData: D, createData: (AnyCol) -> D): TreeNode<D> {
    val root = TreeNode.createRoot(emptyData)

    fun collectColumns(col: AnyCol, parentNode: TreeNode<D>) {
        val newNode = parentNode.getOrPut(col.name()) { createData(col) }
        if (col.isColumnGroup()) {
            col.asColumnGroup().columns().forEach {
                collectColumns(it, newNode)
            }
        }
    }
    forEach {
        if (it.path.isEmpty()) {
            it.data.asColumnGroup().columns().forEach {
                collectColumns(it, root)
            }
        } else {
            val node = root.getOrPutEmpty(it.path.dropLast(1), emptyData)
            collectColumns(it.data, node)
        }
    }
    return root
}

/**
 * Shorten column paths as much as possible to keep them unique
 */
internal fun <C> List<ColumnWithPath<C>>.shortenPaths(): List<ColumnWithPath<C>> {
    // try to use just column name as column path
    val map = groupBy { it.path.takeLast(1) }.toMutableMap()

    fun add(path: ColumnPath, column: ColumnWithPath<C>) {
        val list: MutableList<ColumnWithPath<C>> =
            (map.getOrPut(path) { mutableListOf() } as? MutableList<ColumnWithPath<C>>)
                ?: let {
                    val values = map.remove(path)!!
                    map.put(path, values.toMutableList()) as MutableList<ColumnWithPath<C>>
                }
        list.add(column)
    }

    // resolve name collisions by using more parts of column path
    var conflicts = map.filter { it.value.size > 1 }
    while (conflicts.size > 0) {
        conflicts.forEach {
            val key = it.key
            val keyLength = key.size
            map.remove(key)
            it.value.forEach {
                val path = it.path
                val newPath = if (path.size < keyLength) path.takeLast(keyLength + 1) else path
                add(newPath, it)
            }
        }
        conflicts = map.filter { it.value.size > 1 }
    }

    val pathRemapping = map.map { it.value.single().path to it.key }.toMap()

    return map { it.changePath(pathRemapping[it.path]!!) }
}
