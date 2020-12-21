package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.ColumnPath

internal class TreeNode<T>(val name: String, val depth: Int, var data: T, val parent: TreeNode<T>? = null) {

    companion object {
        fun <T> createRoot(data: T) = TreeNode<T>("", 0, data)
    }

    private val myChildren = mutableListOf<TreeNode<T>>()
    private val childrenMap = mutableMapOf<String, TreeNode<T>>()

    val children: List<TreeNode<T>>
        get() = myChildren

    fun getRoot(): TreeNode<T> = parent?.getRoot() ?: this

    fun contains(childName: String) = childrenMap.containsKey(childName)

    operator fun get(childName: String) = childrenMap[childName]

    fun pathFromRoot(): List<String> {
        val path = mutableListOf<String>()
        var node: TreeNode<T>? = this
        while (node != null && node.parent != null) {
            path.add(node.name)
            node = node.parent
        }
        path.reverse()
        return path
    }

    fun addChild(childName: String, childData: T): TreeNode<T> {
        val node = TreeNode(childName, depth + 1, childData, this)
        myChildren.add(node)
        childrenMap[childName] = node
        return node
    }

    fun getOrPut(childName: String, createData: () -> T): TreeNode<T> {
        childrenMap[childName]?.let { return it }
        return addChild(childName, createData())
    }

    fun dfs(enterCondition: (TreeNode<T>) -> Boolean = { true }, yieldCondition: (TreeNode<T>) -> Boolean = { true }): List<TreeNode<T>> {

        val result = mutableListOf<TreeNode<T>>()
        fun doDfs(node: TreeNode<T>) {
            if (yieldCondition(node))
                result.add(node)
            if (enterCondition(node))
                node.children.forEach {
                    doDfs(it)
                }
        }
        doDfs(this)
        return result
    }

    fun <R> changeData(func: (Int, TreeNode<T>) -> R): TreeNode<R> {

        fun dfs(oldNode: TreeNode<T>, newNode: TreeNode<R>) {
            oldNode.children.forEachIndexed { index, child ->
                val newData = func(index, child)
                val newChild = newNode.addChild(child.name, newData)
                dfs(child, newChild)
            }
        }
        val newRoot = createRoot(func(0, this))
        dfs(this, newRoot)
        return newRoot
    }
}

internal tailrec fun <T> TreeNode<T>.getAncestor(depth: Int): TreeNode<T> {

    if(depth > this.depth) throw UnsupportedOperationException()
    if(depth == this.depth) return this
    if(parent == null) throw UnsupportedOperationException()
    return parent.getAncestor(depth)
}

internal fun <T> TreeNode<T?>.getOrPut(path: ColumnPath) = getOrPut(path, null)

internal fun <T> TreeNode<T>.getOrPut(path: ColumnPath, emptyData: T): TreeNode<T> {
    var node = this
    path.forEach {
        node = node.getOrPut(it) { emptyData }
    }
    return node
}

internal fun <T> TreeNode<T>.topDfs(yieldCondition: (TreeNode<T>) -> Boolean) = dfs(enterCondition = { !yieldCondition(it) }, yieldCondition = yieldCondition)
internal fun <T> TreeNode<T>.topDfsExcluding(excludeRoot: TreeNode<*>): List<TreeNode<T>> {

    val result = mutableListOf<TreeNode<T>>()
    fun doDfs(node: TreeNode<T>, exclude: TreeNode<*>) {
        if (exclude.children.isNotEmpty()) {
            node.children.filter { !exclude.contains(it.name) }.forEach { result.add(it) }
            exclude.children.forEach {
                val srcNode = node[it.name]
                if (srcNode != null)
                    doDfs(srcNode, it)
            }
        }
    }
    doDfs(this, excludeRoot)
    return result
}

internal fun <T> TreeNode<T?>.dfsNotNull() = dfs { it.data != null }.map { it as TreeNode<T> }
internal fun <T> TreeNode<T?>.dfsTopNotNull() = dfs(enterCondition = { it.data == null }, yieldCondition = { it.data != null }).map { it as TreeNode<T> }