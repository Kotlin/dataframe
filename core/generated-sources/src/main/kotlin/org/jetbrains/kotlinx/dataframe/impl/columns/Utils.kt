package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asNullable
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingDataColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.*
import org.jetbrains.kotlinx.dataframe.impl.equalsByElement
import org.jetbrains.kotlinx.dataframe.impl.rollingHash
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure
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

internal fun <C> TreeNode<ColumnPosition>.toColumnWithPath() =
    (data.column as DataColumn<C>).addPath(pathFromRoot())

internal fun <T> BaseColumn<T>.addPath(path: ColumnPath): ColumnWithPath<T> =
    when (this) {
        is ValueColumn<T> -> ValueColumnWithPathImpl(this, path)
        is FrameColumn<*> -> FrameColumnWithPathImpl(this, path) as ColumnWithPath<T>
        is ColumnGroup<*> -> ColumnGroupWithPathImpl(this, path) as ColumnWithPath<T>
        else -> throw IllegalArgumentException("Can't add path to ${this.javaClass}")
    }

internal fun <T> ColumnWithPath<T>.changePath(path: ColumnPath): ColumnWithPath<T> = data.addPath(path)

internal fun <T> BaseColumn<T>.addParentPath(path: ColumnPath) = addPath(path + name)

/* TODO internal */ public fun <T> BaseColumn<T>.addPath(): ColumnWithPath<T> = addPath(pathOf(name))

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
 * Applies a transformation on [this] [SingleColumn] by converting its
 * single [ColumnWithPath]<[A]> to [List]<[ColumnWithPath]<[B]>] using [converter].
 * Since [converter] allows you to return multiple columns, the result is turned into a [ColumnSet]<[B]>.
 */
internal fun <A, B> SingleColumn<A>.transformSingle(
    converter: (ColumnWithPath<A>) -> List<ColumnWithPath<B>>,
): ColumnSet<B> = object : ColumnSet<B> {
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
 * or it can be used as a [TransformableColumnSet]<[B]>, where a [ColumnSetTransformer] can be injected before
 * the [converter] is applied.
 */
internal fun <A, B> ColumnSet<A>.transform(
    converter: (List<ColumnWithPath<A>>) -> List<ColumnWithPath<B>>,
): TransformableColumnSet<B> = object : TransformableColumnSet<B> {
    override fun resolve(context: ColumnResolutionContext) =
        this@transform
            .resolve(context)
            .let(converter)

    override fun transformResolve(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): List<ColumnWithPath<B>> =
        transformer.transform(this@transform)
            .resolve(context)
            .let { converter(it as List<ColumnWithPath<A>>) }
}

/**
 * Applies a transformation on [this] by converting its [List]<[ColumnWithPath]<[A]>] to [List]<[ColumnWithPath]<[B]>]
 * using [converter], but also providing the [ColumnResolutionContext] to the converter.
 *
 * The result can either be used as a normal [ColumnSet]<[B]>,
 * which resolves [this] and then applies [converter] on the result,
 *
 * or it can be used as a [TransformableColumnSet]<[B]>, where a [ColumnSetTransformer] can be injected before
 * the [converter] is applied.
 */
internal fun <A, B> ColumnSet<A>.transformWithContext(
    converter: ColumnResolutionContext.(List<ColumnWithPath<A>>) -> List<ColumnWithPath<B>>,
): TransformableColumnSet<B> = object : TransformableColumnSet<B> {
    override fun resolve(context: ColumnResolutionContext) =
        this@transformWithContext
            .resolve(context)
            .let { converter(context, it) }

    override fun transformResolve(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): List<ColumnWithPath<B>> =
        transformer.transform(this@transformWithContext)
            .resolve(context)
            .let { converter(context, it as List<ColumnWithPath<A>>) }
}

/**
 * Converts [this] [ColumnSet] to a [SingleColumn].
 * [resolveSingle] will return the single column of [this] if there is only one, else it will return `null`.
 * If the result used as a [ColumnSet], `null` will be converted to an empty list.
 */
internal fun <T> ColumnSet<T>.singleImpl(): SingleColumn<T> = object : SingleColumn<T> {
    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
        this@singleImpl.resolve(context).singleOrNull()
}

/**
 * Same as [singleImpl], however, it passes any [ColumnSetTransformer] back to [this] if it is supplied.
 */
internal fun <T> TransformableColumnSet<T>.singleWithTransformerImpl(): TransformableSingleColumn<T> =
    object : TransformableSingleColumn<T> {
        override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
            this@singleWithTransformerImpl.resolve(context).singleOrNull()

        override fun transformResolveSingle(
            context: ColumnResolutionContext,
            transformer: ColumnSetTransformer,
        ): ColumnWithPath<T>? =
            this@singleWithTransformerImpl.transformResolve(
                context = context,
                transformer = transformer,
            ).singleOrNull()
    }

internal fun <T> ColumnSet<T>.getAt(index: Int): SingleColumn<T> = object : SingleColumn<T> {
    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? =
        this@getAt
            .resolve(context)
            .getOrNull(index)
}

internal fun <T> ColumnSet<T>.getChildrenAt(index: Int): ColumnSet<Any?> =
    transform { it.mapNotNull { it.getChild(index) } }

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

/**
 * Returns a sub-list of columns that are roots of the trees of columns.
 *
 * In practice, this means that if a column in [this] is a child of another column in [this],
 * it will not be included in the result.
 */
internal fun <T> List<ColumnWithPath<T>>.roots(): List<ColumnWithPath<T>> {
    val emptyRoot = TreeNode.createRoot<ColumnWithPath<T>?>(data = null)
    this.forEach { emptyRoot.put(it.path, it) }
    return emptyRoot.topmostChildren { it.data != null }.map { it.data!! }
}

internal fun List<ColumnWithPath<*>>.allColumnsExcept(columns: Iterable<ColumnWithPath<*>>): List<ColumnWithPath<*>> {
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

internal fun KType.toColumnKind(): ColumnKind = jvmErasure.let {
    when (it) {
        DataFrame::class -> ColumnKind.Frame
        DataRow::class -> ColumnKind.Group
        else -> ColumnKind.Value
    }
}

internal fun <C> ColumnSet<C>.resolve(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
) =
    resolve(ColumnResolutionContext(df, unresolvedColumnsPolicy))

internal fun <C> SingleColumn<C>.resolveSingle(
    df: DataFrame<*>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
): ColumnWithPath<C>? =
    resolveSingle(ColumnResolutionContext(df, unresolvedColumnsPolicy))

internal fun AnyBaseCol.unbox(): AnyCol = when (this) {
    is ColumnWithPath<*> -> data.unbox()
    is ColumnWithParent<*> -> source.unbox()
    is ForceResolvedColumn<*> -> source.unbox()
    else -> this as AnyCol
}

internal fun AnyCol.isMissingColumn() = this is MissingDataColumn

internal fun <T> ColumnGroup<T>.extractDataFrame(): DataFrame<T> = DataFrameImpl(columns(), nrow)

internal fun <T> BaseColumn<T>.addParent(parent: ColumnGroup<*>) = (this as DataColumnInternal<T>).addParent(parent)
