package org.jetbrains.kotlinx.dataframe.impl.columns.tree

internal interface ReadonlyTreeNode<out T> {

    val name: String
    val depth: Int
    val data: T
    val parent: ReadonlyTreeNode<T>?
    val children: List<ReadonlyTreeNode<T>>

    fun getRoot(): ReadonlyTreeNode<T>?

    operator fun get(childName: String): ReadonlyTreeNode<T>?
}
