package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.asNullable
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.collectTree
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.getOrPut
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.put
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.topDfs
import org.jetbrains.kotlinx.dataframe.impl.equalsByElement
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.rollingHash
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

internal fun <T> BaseColumn<T>.checkEquals(other: Any?): Boolean {
    if (this === other) return true

    if (this !is AnyCol) return false
    if (other !is AnyCol) return false

    if (name != other.name) return false
    if (type != other.type) return false
    return values.equalsByElement(other.values)
}

internal fun AnyCol.getHashCode(): Int {
    var result = values.rollingHash()
    result = 31 * result + name().hashCode()
    result = 31 * result + type.hashCode()
    return result
}

internal fun <C> TreeNode<ColumnPosition>.toColumnWithPath(df: ColumnsContainer<*>) =
    (data.column as DataColumn<C>).addPath(pathFromRoot(), df)

@JvmName("toColumnWithPathAnyCol")
internal fun <C> TreeNode<DataColumn<C>>.toColumnWithPath(df: ColumnsContainer<*>) = data.addPath(pathFromRoot(), df)

internal fun <T> BaseColumn<T>.addPath(path: ColumnPath, df: ColumnsContainer<*>? = null): ColumnWithPath<T> =
    when (this) {
        is ValueColumn<T> -> ValueColumnWithPathImpl(this, path, df)
        is FrameColumn<*> -> FrameColumnWithPathImpl(this, path, df) as ColumnWithPath<T>
        is ColumnGroup<*> -> ColumnGroupWithPathImpl(this, path, df) as ColumnWithPath<T>
        else -> throw IllegalArgumentException("Can't add path to ${this.javaClass}")
    }

internal fun <T> ColumnWithPath<T>.changePath(path: ColumnPath): ColumnWithPath<T> = data.addPath(path, host)

internal fun <T> BaseColumn<T>.addParentPath(path: ColumnPath, df: ColumnsContainer<*>) = addPath(path + name, df)

internal fun <T> BaseColumn<T>.addPath(df: ColumnsContainer<*>): ColumnWithPath<T> = addPath(pathOf(name), df)

internal fun ColumnPath.depth() = size - 1

internal fun AnyCol.asColumnGroup(): ColumnGroup<*> = this as ColumnGroup<*>

internal fun <T> AnyCol.asValues(): ValueColumn<T> = this as ValueColumn<T>

internal fun AnyCol.asFrameColumn(): FrameColumn<*> = this as FrameColumn<*>

internal fun <T> AnyCol.grouped() = this as ColumnGroup<T>
internal fun <T> ColumnGroup<*>.withDf(newDf: DataFrame<T>) = DataColumn.createColumnGroup(name, newDf)

@JvmName("asGroupedT")
internal fun <T> DataColumn<DataRow<T>>.asColumnGroup(): ColumnGroup<T> = (this as AnyCol).asColumnGroup().cast()

@JvmName("asFrameT")
internal fun <T> DataColumn<DataFrame<T>?>.asFrameColumn(): FrameColumn<T> = (this as AnyCol).asFrameColumn().castFrameColumn()

internal fun <T> DataColumn<T>.assertIsComparable(): DataColumn<T> {
    if (!type.isSubtypeOf(getType<Comparable<*>?>())) {
        throw RuntimeException("Column '$name' has type '$type' that is not Comparable")
    }
    return this
}

internal fun <A, B> SingleColumn<A>.transformSingle(converter: (ColumnWithPath<A>) -> List<ColumnWithPath<B>>): ColumnSet<B> =
    object : ColumnSet<B> {
        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<B>> =
            this@transformSingle.resolveSingle(context)?.let { converter(it) } ?: emptyList()
    }

internal fun <A, B> ColumnSet<A>.transform(converter: (List<ColumnWithPath<A>>) -> List<ColumnWithPath<B>>): ColumnSet<B> =
    object : ColumnSet<B> {
        override fun resolve(context: ColumnResolutionContext) = converter(this@transform.resolve(context))
    }

internal fun <T> ColumnSet<T>.single() = object : SingleColumn<T> {
    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return this@single.resolve(context).singleOrNull()
    }
}

internal fun <T> ColumnSet<T>.getAt(index: Int) = object : SingleColumn<T> {
    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return this@getAt.resolve(context).getOrNull(index)
    }
}

internal fun <T> ColumnSet<T>.getChildrenAt(index: Int): ColumnSet<Any?> = transform { it.mapNotNull { it.getChild(index) } }

internal fun <C> ColumnsContainer<*>.getColumn(name: String, policy: UnresolvedColumnsPolicy) =
    getColumnOrNull(name)?.cast()
        ?: when (policy) {
            UnresolvedColumnsPolicy.Fail ->
                error("Column not found: $name")
            UnresolvedColumnsPolicy.Skip -> null
            UnresolvedColumnsPolicy.Create -> DataColumn.empty().cast<C>()
        }

internal fun <C> ColumnsContainer<*>.getColumn(path: ColumnPath, policy: UnresolvedColumnsPolicy) =
    getColumnOrNull(path)?.cast()
        ?: when (policy) {
            UnresolvedColumnsPolicy.Fail ->
                error("Column not found: $path")
            UnresolvedColumnsPolicy.Skip -> null
            UnresolvedColumnsPolicy.Create -> DataColumn.empty().cast<C>()
        }

internal fun <T> List<ColumnWithPath<T>>.top(): List<ColumnWithPath<T>> {
    val root = TreeNode.createRoot<ColumnWithPath<T>?>(null)
    forEach { root.put(it.path, it) }
    return root.topDfs { it.data != null }.map { it.data!! }
}

internal fun List<ColumnWithPath<*>>.allColumnsExcept(columns: Iterable<ColumnWithPath<*>>): List<ColumnWithPath<*>> {
    if (isEmpty()) return emptyList()
    val df = this[0].host
    require(all { it.host === df })
    val fullTree = collectTree()
    columns.forEach {
        var node = fullTree.getOrPut(it.path).asNullable()
        node?.dfs()?.forEach { it.data = null }
        while (node != null) {
            node.data = null
            node = node.parent
        }
    }
    val dfs = fullTree.topDfs { it.data != null }
    return dfs.map { it.data!!.addPath(it.pathFromRoot(), df) }
}

@PublishedApi
internal fun KType.toColumnKind(): ColumnKind = when {
    isSubtypeOf(getType<ColumnsContainer<*>?>()) -> ColumnKind.Frame
    isSubtypeOf(getType<DataRow<*>?>()) -> ColumnKind.Group
    else -> ColumnKind.Value
}

internal fun <C> ColumnSet<C>.resolve(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail
) =
    resolve(ColumnResolutionContext(df, unresolvedColumnsPolicy))

internal fun <C> SingleColumn<C>.resolveSingle(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail
): ColumnWithPath<C>? =
    resolveSingle(ColumnResolutionContext(df, unresolvedColumnsPolicy))
