package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asNullable
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingDataColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.ColumnPosition
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.TreeNode
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.collectTree
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.getOrPut
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.put
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.topmostChildren
import org.jetbrains.kotlinx.dataframe.impl.equalsByElement
import org.jetbrains.kotlinx.dataframe.impl.rollingHash
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

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

internal fun <C> TreeNode<ColumnPosition>.toColumnWithPath() = (data.column as DataColumn<C>).addPath(pathFromRoot())

internal fun <T> BaseColumn<T>.addPath(path: ColumnPath): ColumnWithPath<T> =
    when (this) {
        is ValueColumn<T> -> ValueColumnWithPathImpl(this, path)
        is FrameColumn<*> -> FrameColumnWithPathImpl(this, path) as ColumnWithPath<T>
        is ColumnGroup<*> -> ColumnGroupWithPathImpl(this, path) as ColumnWithPath<T>
        else -> throw IllegalArgumentException("Can't add path to ${this.javaClass}")
    }

internal fun <T> ColumnWithPath<T>.changePath(path: ColumnPath): ColumnWithPath<T> = data.addPath(path)

internal fun <T> BaseColumn<T>.addParentPath(path: ColumnPath) = addPath(path + name)

internal fun <T> BaseColumn<T>.addPath(): ColumnWithPath<T> = addPath(pathOf(name))

internal fun ColumnPath.depth() = size - 1

internal fun <T> AnyCol.asValues(): ValueColumn<T> = this as ValueColumn<T>

internal fun <T> DataColumn<T>.asValueColumn(): ValueColumn<T> = this as ValueColumn<T>

@PublishedApi
internal fun AnyCol.asAnyFrameColumn(): FrameColumn<*> = this as FrameColumn<*>

internal fun <T> AnyCol.grouped() = this as ColumnGroup<T>

internal fun <T> ColumnGroup<*>.withDf(newDf: DataFrame<T>) = DataColumn.createColumnGroup(name, newDf)

internal fun <T> DataColumn<T>.assertIsComparable(): DataColumn<T> {
    if (!type.isSubtypeOf(typeOf<Comparable<*>?>())) {
        throw RuntimeException("Column '$name' has type '$type' that is not Comparable")
    }
    return this
}

/**
 * Helper function to perform runtime checks on a [SingleColumn].
 *
 * One use case is to check that a column is actually a [ColumnGroup], which is handled by
 * [org.jetbrains.kotlinx.dataframe.api.ensureIsColumnGroup].
 */
internal fun <A> SingleColumn<A>.onResolve(check: (ColumnWithPath<A>?) -> Unit): SingleColumn<A> =
    object : SingleColumn<A> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<A>? =
            this@onResolve.resolveSingle(context).also(check)
    }

/**
 * Helper function to perform runtime checks on a [ColumnAccessor].
 *
 * One use case is to check that a column is actually a [ValueColumn], which is handled by
 * [org.jetbrains.kotlinx.dataframe.api.ensureIsValueColumn].
 */
internal fun <A> ColumnAccessor<A>.onResolve(check: (ColumnWithPath<*>?) -> Unit): ColumnAccessor<A> =
    object : ColumnAccessor<A> {
        override fun <C> get(column: ColumnReference<C>): ColumnAccessor<C> =
            this@onResolve.get(column).onResolve(check)

        override fun rename(newName: String): ColumnAccessor<A> = this@onResolve.rename(newName).onResolve(check)

        override fun name(): String = this@onResolve.name()

        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<A>? =
            this@onResolve.resolveSingle(context).also(check)
    }

internal fun <A> TransformableSingleColumn<A>.onResolve(
    check: (ColumnWithPath<A>?) -> Unit,
): TransformableSingleColumn<A> =
    object : TransformableSingleColumn<A> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<A>? =
            this@onResolve.resolveSingle(context).also(check)

        override fun transformResolveSingle(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): ColumnWithPath<A>? = this@onResolve.transformResolveSingle(context, transformer).also(check)
    }

internal fun <A> ColumnSet<A>.onResolve(check: (List<ColumnWithPath<A>>) -> Unit): ColumnSet<A> =
    object : ColumnSet<A> {
        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<A>> =
            this@onResolve.resolve(context).also(check)
    }

internal fun <A> TransformableColumnSet<A>.onResolve(
    check: (List<ColumnWithPath<A>>) -> Unit,
): TransformableColumnSet<A> =
    object : TransformableColumnSet<A> {
        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<A>> =
            this@onResolve.resolve(context).also(check)

        override fun transformResolve(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): List<ColumnWithPath<A>> = this@onResolve.transformResolve(context, transformer).also(check)
    }

/**
 * Applies a transformation on [this] [SingleColumn] by converting its
 * single [ColumnWithPath]<[A]> to [List]<[ColumnWithPath]<[B]>] using [converter].
 * Since [converter] allows you to return multiple columns, the result is turned into a [ColumnSet]<[B]>.
 */
internal fun <A, B> SingleColumn<A>.transformSingle(
    converter: (ColumnWithPath<A>) -> List<ColumnWithPath<B>>,
): ColumnSet<B> =
    object : ColumnSet<B> {
        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<B>> =
            this@transformSingle
                .resolveSingle(context)
                ?.let(converter)
                ?: emptyList()
    }

/**
 * Applies a transformation on [this] by converting its [List]<[ColumnWithPath]<[A]>] to [List]<[ColumnWithPath]<[B]>]
 * using [converter].
 *
 * The result can either be used as a normal [ColumnSet]<[B]>,
 * which resolves [this] and then applies [converter] on the result,
 *
 * or it can be used as a [TransformableColumnSet]<[B]>, where a [ColumnsResolverTransformer] can be injected before
 * the [converter] is applied.
 */
@PublishedApi
internal inline fun <A, B> ColumnsResolver<A>.transform(
    crossinline converter: (List<ColumnWithPath<A>>) -> List<ColumnWithPath<B>>,
): TransformableColumnSet<B> =
    object : TransformableColumnSet<B> {
        override fun resolve(context: ColumnResolutionContext) =
            this@transform
                .resolve(context)
                .let(converter)

        @Suppress("UNCHECKED_CAST")
        override fun transformResolve(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): List<ColumnWithPath<B>> =
            transformer.transform(this@transform)
                .resolve(context)
                .let { converter(it as List<ColumnWithPath<A>>) }
    }

/**
 * Applies a transformation on [this] [SingleColumn] by converting its
 * single [ColumnWithPath]<[A]> to [List]<[ColumnWithPath]<[B]>] using [converter] but
 * also providing the [ColumnResolutionContext] to the converter.
 *
 *  Since [converter] allows you to return multiple columns, the result is turned into a [ColumnSet]<[B]>.
 *
 * The result can either be used as a normal [ColumnSet]<[B]>,
 * which resolves [this] and then applies [converter] on the result,
 */
internal fun <A, B> SingleColumn<A>.transformSingleWithContext(
    converter: ColumnResolutionContext.(ColumnWithPath<A>) -> List<ColumnWithPath<B>>,
): ColumnSet<B> =
    object : ColumnSet<B> {

        override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<B>> =
            this@transformSingleWithContext
                .resolveSingle(context)
                ?.let { converter(context, it) }
                ?: emptyList()
    }

/**
 * Applies a transformation on [this] by converting its [List]<[ColumnWithPath]<[A]>] to [List]<[ColumnWithPath]<[B]>]
 * using [converter], but also providing the [ColumnResolutionContext] to the converter.
 *
 * The result can either be used as a normal [ColumnSet]<[B]>,
 * which resolves [this] and then applies [converter] on the result,
 *
 * or it can be used as a [TransformableColumnSet]<[B]>, where a [ColumnsResolverTransformer] can be injected before
 * the [converter] is applied.
 */
internal fun <A, B> ColumnsResolver<A>.transformWithContext(
    converter: ColumnResolutionContext.(List<ColumnWithPath<A>>) -> List<ColumnWithPath<B>>,
): TransformableColumnSet<B> =
    object : TransformableColumnSet<B> {
        override fun resolve(context: ColumnResolutionContext) =
            this@transformWithContext
                .resolve(context)
                .let { converter(context, it) }

        override fun transformResolve(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): List<ColumnWithPath<B>> =
            transformer.transform(this@transformWithContext)
                .resolve(context)
                .let { converter(context, it as List<ColumnWithPath<A>>) }
    }

/** Makes sure the right error messages appear when running [List.single] */
private fun <T> List<ColumnWithPath<T>>.singleImpl(): ColumnWithPath<T> =
    try {
        single()
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("ColumnSet is empty")
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("ColumnSet contains more than one column")
    }

/**
 * Converts [this] [ColumnsResolver] to a [SingleColumn].
 * [resolveSingle] will return the single column of [this] if there is only one, else it will return throw an exception:
 * In case of an empty [ColumnsResolver], a [NoSuchElementException] will be thrown.
 * In case of more than one column, a [IllegalArgumentException] will be thrown.
 * If the result is used as a [ColumnSet], `null` will be converted to an empty list.
 */
internal fun <T> ColumnsResolver<T>.singleImpl(): SingleColumn<T> =
    object : SingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T> =
            this@singleImpl.resolve(context).singleImpl()
    }

/**
 * Same as [singleImpl], however, it passes any [ColumnsResolverTransformer] back to [this] if it is supplied.
 */
internal fun <T> TransformableColumnSet<T>.singleWithTransformerImpl(): TransformableSingleColumn<T> =
    object : TransformableSingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T> =
            this@singleWithTransformerImpl.resolve(context).singleImpl()

        override fun transformResolveSingle(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): ColumnWithPath<T> =
            this@singleWithTransformerImpl.transformResolve(
                context = context,
                transformer = transformer,
            ).singleImpl()
    }

/**
 * Converts [this] [ColumnsResolver] to a [SingleColumn].
 * [resolveSingle] will return the single column of [this] if there is only one, else it will return `null`.
 * If the result used as a [ColumnSet], `null` will be converted to an empty list.
 */
internal fun <T> ColumnsResolver<T>.singleOrNullImpl(): SingleColumn<T> =
    object : SingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
            this@singleOrNullImpl.resolve(context).singleOrNull()
    }

/**
 * Same as [singleOrNullImpl], however, it passes any [ColumnsResolverTransformer] back to [this] if it is supplied.
 */
internal fun <T> TransformableColumnSet<T>.singleOrNullWithTransformerImpl(): TransformableSingleColumn<T> =
    object : TransformableSingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
            this@singleOrNullWithTransformerImpl.resolve(context).singleOrNull()

        override fun transformResolveSingle(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): ColumnWithPath<T>? =
            this@singleOrNullWithTransformerImpl.transformResolve(
                context = context,
                transformer = transformer,
            ).singleOrNull()
    }

internal fun <T> ColumnsResolver<T>.getAt(index: Int): SingleColumn<T> =
    object : SingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T> =
            this@getAt.resolve(context).let {
                try {
                    it[index]
                } catch (e: IndexOutOfBoundsException) {
                    throw IndexOutOfBoundsException("ColumnSet has no column at index $index")
                }
            }
    }

internal fun <T> ColumnSet<T>.getAtOrNull(index: Int): SingleColumn<T> =
    object : SingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
            this@getAtOrNull
                .resolve(context)
                .getOrNull(index)
    }

internal fun ColumnSet<*>.getChildrenAt(index: Int): ColumnSet<*> = transform { it.mapNotNull { it.getCol(index) } }

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
                error("Column not found: '${path.joinToString()}'")

            UnresolvedColumnsPolicy.Skip -> null

            UnresolvedColumnsPolicy.Create -> DataColumn.empty().cast<C>()
        }

/**
 * Simplifies structure by removing columns that are already present in
 * column groups in [this].
 *
 * A.k.a. it gets a sub-list of columns that are roots of the trees of columns.
 *
 * Note: this doesn't merge similarly called groups into one, nor does it insert columns into groups if they weren't
 * so already.
 */
internal fun <T> List<ColumnWithPath<T>>.simplify(): List<ColumnWithPath<T>> {
    val root = TreeNode.createRoot<ColumnWithPath<T>?>(data = null)
    this.forEach { root.put(it.path, it) }
    return root.topmostChildren { it.data != null }.map { it.data!! }
}

/**
 * Returns a new list of column paths, except the ones inside [columns].
 * NOTE: The structure is not kept the same; if a column is removed, its parent will be removed as well, and
 * all its siblings will be lifted out of the group. This also happens if a column is "removed" that does
 * not exist in [this].
 */
internal fun List<ColumnWithPath<*>>.allColumnsExceptAndUnpack(
    columns: Iterable<ColumnWithPath<*>>,
): List<ColumnWithPath<*>> {
    if (isEmpty()) return emptyList()
    val fullTree = collectTree()
    columns.forEach {
        var node = fullTree.getOrPut(it.path).asNullable()
        node?.allChildren()?.forEach { it.data = null }
        while (node != null) {
            node.data = null
            node = node.parent
        }
    }
    val subtrees = fullTree.topmostChildren { it.data != null }
    return subtrees.map { it.data!!.addPath(it.pathFromRoot()) }
}

/**
 * Returns a new list of column paths, except the ones inside [columns].
 * NOTE: ColumnGroups are adapted to keep their structure. If a column inside a column group is excepted, it will
 * be removed from the group.
 * Empty groups will be removed if [removeEmptyGroups]` == true`
 */
internal fun List<ColumnWithPath<*>>.allColumnsExceptKeepingStructure(
    columns: Set<ColumnWithPath<*>>,
    removeEmptyGroups: Boolean = true,
): List<ColumnWithPath<*>> {
    if (isEmpty()) return emptyList()
    return flatMap {
        val fullTree = listOf(it).collectTree()
        for (columnToExcept in columns.sortedByDescending { it.path.size }) {
            // grab the node representing the column from the tree
            val nodeToExcept = fullTree.getOrPut(columnToExcept.path).asNullable()
            if (nodeToExcept != null) {
                // remove the children from the node (if it's a column group) and remove its data (the column itself)
                nodeToExcept.allChildren().forEach { it.data = null }
                nodeToExcept.data = null

                // we need to update the data of the parent node(s) to reflect the removal of the column
                if (nodeToExcept.parent != null) {
                    // we grab the data of the parent node, which should be a column group
                    // treat it as a DF to remove the column to except from it and
                    // convert it back to a column group
                    val current = nodeToExcept.parent.data as ColumnGroup<*>? ?: continue
                    val adjustedCurrent = current
                        .remove(nodeToExcept.name)
                        .asColumnGroup(current.name)
                        .addPath(current.path())

                    // remove the group if it's empty and removeEmptyGroups is true
                    // else, simply update the parent's data with the adjusted column group
                    nodeToExcept.parent.data =
                        if (removeEmptyGroups && adjustedCurrent.cols().isEmpty()) {
                            null
                        } else {
                            adjustedCurrent
                        }

                    // now we update the parent's parents recursively with new column group instances
                    var parent = nodeToExcept.parent.parent

                    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                    var currentNode = nodeToExcept.parent!!
                    while (parent != null) {
                        val parentData = parent.data as ColumnGroup<*>? ?: break
                        val currentData = currentNode.data
                        val modifiedParentData =
                            if (currentData == null) {
                                parentData.remove(currentNode.name)
                            } else {
                                parentData.replace(currentNode.name).with { currentData }
                            }
                        parent.data = modifiedParentData
                            .asColumnGroup(parentData.name)
                            .addPath(parentData.path())
                        currentNode = parent
                        parent = parent.parent
                    }
                }
            }
        }
        val subtrees = fullTree.topmostChildren { it.data != null }
        subtrees.map { it.data!!.addPath(it.pathFromRoot()) }
    }
}

/**
 * Retrieves the correct [ColumnKind] based on the [type][KType] of the values in the column.
 *
 * NOTE: nullable DataFrames cannot become a [FrameColumns][FrameColumn],
 * so they become [ValueColumns][ValueColumn] instead.
 */
internal fun KType.toColumnKind(): ColumnKind =
    when {
        this.isSubtypeOf(typeOf<AnyFrame>()) -> ColumnKind.Frame
        this.isSubtypeOf(typeOf<AnyRow?>()) -> ColumnKind.Group
        else -> ColumnKind.Value
    }

internal fun <C> ColumnsResolver<C>.resolve(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
) = resolve(ColumnResolutionContext(df, unresolvedColumnsPolicy))

internal fun <C> SingleColumn<C>.resolveSingle(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
): ColumnWithPath<C>? = resolveSingle(ColumnResolutionContext(df, unresolvedColumnsPolicy))

internal fun AnyBaseCol.unbox(): AnyCol =
    when (this) {
        is ColumnWithPath<*> -> data.unbox()
        is ColumnWithParent<*> -> source.unbox()
        is ForceResolvedColumn<*> -> source.unbox()
        else -> this as AnyCol
    }

internal fun AnyCol.isMissingColumn() = this is MissingDataColumn

internal fun <T> ColumnGroup<T>.extractDataFrame(): DataFrame<T> = DataFrameImpl(columns(), nrow)

internal fun <T> BaseColumn<T>.addParent(parent: ColumnGroup<*>) = (this as DataColumnInternal<T>).addParent(parent)
