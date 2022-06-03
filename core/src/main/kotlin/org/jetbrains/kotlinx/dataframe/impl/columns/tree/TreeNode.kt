package org.jetbrains.kotlinx.dataframe.impl.columns.tree

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath

internal class TreeNode<T>(
    override val name: String,
    override val depth: Int,
    override var data: T,
    override val parent: TreeNode<T>? = null
) : ReadonlyTreeNode<T> {

    companion object {
        fun <T> createRoot(data: T) = TreeNode<T>("", 0, data)
    }

    private val myChildren = mutableListOf<TreeNode<T>>()
    private val childrenMap = mutableMapOf<String, TreeNode<T>>()

    override val children: List<TreeNode<T>>
        get() = myChildren

    override fun getRoot(): TreeNode<T> = parent?.getRoot() ?: this

    fun contains(childName: String) = childrenMap.containsKey(childName)

    override operator fun get(childName: String) = childrenMap[childName]

    fun pathFromRoot(): ColumnPath {
        val path = mutableListOf<String>()
        var node: TreeNode<T>? = this
        while (node != null && node.parent != null) {
            path.add(node.name)
            node = node.parent
        }
        path.reverse()
        return ColumnPath(path)
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
            if (yieldCondition(node)) {
                result.add(node)
            }
            if (enterCondition(node)) {
                node.children.forEach {
                    doDfs(it)
                }
            }
        }
        doDfs(this)
        return result
    }
}
