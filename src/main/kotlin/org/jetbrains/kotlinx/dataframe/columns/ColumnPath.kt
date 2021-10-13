package org.jetbrains.kotlinx.dataframe.columns

public data class ColumnPath(val path: List<String>) : List<String> by path, ColumnReference<Any?> {

    public constructor(name: String) : this(listOf(name))

    public fun drop(size: Int): ColumnPath = ColumnPath(path.drop(size))

    public fun dropLast(size: Int): ColumnPath = ColumnPath(path.dropLast(size))

    public operator fun plus(name: String): ColumnPath = ColumnPath(path + name)

    public operator fun plus(otherPath: ColumnPath): ColumnPath = ColumnPath(path + otherPath)

    public operator fun plus(otherPath: Iterable<String>): ColumnPath = ColumnPath(path + otherPath)

    public fun take(first: Int): ColumnPath = ColumnPath(path.take(first))

    public fun replaceLast(name: String): ColumnPath = ColumnPath(if (size < 2) listOf(name) else dropLast(1) + name)

    public fun takeLast(first: Int): ColumnPath = ColumnPath(path.takeLast(first))

    override fun path(): ColumnPath = this

    override fun name(): String = path.last()

    override fun rename(newName: String): ColumnPath = ColumnPath(path.dropLast(1) + newName)
}
