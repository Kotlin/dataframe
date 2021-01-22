package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.*
import org.jetbrains.dataframe.impl.TreeNode
import org.jetbrains.dataframe.impl.columns.ColumnDataWithParent
import org.jetbrains.dataframe.impl.columns.ColumnWithParent
import org.jetbrains.dataframe.impl.columns.ColumnWithPathImpl
import org.jetbrains.dataframe.impl.getAncestor
import org.jetbrains.dataframe.impl.getOrPut
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure

fun rowNumber(columnName: String = "id") = AddRowNumberStub(columnName)

data class AddRowNumberStub(val columnName: String)

// map

fun <T> DataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): DataFrame<Unit> {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}


// size

val DataFrame<*>.size: DataFrameSize get() = DataFrameSize(ncol(), nrow())

fun commonParent(classes: Iterable<KClass<*>>) = commonParents(classes).withMostSuperclasses()

fun commonParent(vararg classes: KClass<*>) = commonParent(classes.toList())

fun Iterable<KClass<*>>.withMostSuperclasses() = maxByOrNull { it.allSuperclasses.size }

fun commonParents(vararg classes: KClass<*>) = commonParents(classes.toList())

fun commonParents(classes: Iterable<KClass<*>>) =
        when {
            !classes.any() -> emptyList()
            else -> {
                classes.distinct().let {
                    when {
                        it.size == 1 -> listOf(it[0]) // if there is only one class - return it
                        else -> it.fold(null as (Set<KClass<*>>?)) { set, clazz ->
                            // collect a set of all common superclasses from original classes
                            val superclasses = clazz.allSuperclasses + clazz
                            set?.intersect(superclasses) ?: superclasses.toSet()
                        }!!.let {
                            it - it.flatMap { it.superclasses } // leave only 'leaf' classes, that are not super to some other class in a set
                        }.toList()
                    }
                }
            }
        }

internal fun baseType(types: Set<KType>): KType {
    val nullable = types.any { it.isMarkedNullable }
    return when (types.size) {
        0 -> getType<Unit>()
        1 -> types.single()
        else -> {
            val classes = types.map { it.jvmErasure }.distinct()
            when {
                classes.size == 1 -> {
                    val typeProjections = classes[0].typeParameters.mapIndexed { index, parameter ->
                        val arguments = types.map { it.arguments[index].type }.toSet()
                        if (arguments.contains(null)) KTypeProjection.STAR
                        else {
                            val type = baseType(arguments as Set<KType>)
                            KTypeProjection(parameter.variance, type)
                        }
                    }
                    classes[0].createType(typeProjections, nullable)
                }
                classes.any { it == List::class } && classes.all { it == List::class || !it.isSubclassOf(Collection::class) } -> {
                    val listTypes = types.map { if (it.classifier == List::class) it.arguments[0].type else it }.toMutableSet()
                    if (listTypes.contains(null)) List::class.createStarProjectedType(nullable)
                    else {
                        val type = baseType(listTypes as Set<KType>)
                        List::class.createType(listOf(KTypeProjection.invariant(type)), nullable)
                    }
                }
                else -> {
                    val commonClass = commonParent(classes) ?: Any::class
                    commonClass.createStarProjectedType(nullable)
                }
            }
        }
    }
}

internal fun indexColumn(columnName: String, size: Int): DataCol = column(columnName, (0 until size).toList())

fun <T> DataFrame<T>.addRowNumber(column: ColumnDef<Int>) = addRowNumber(column.name())
fun <T> DataFrame<T>.addRowNumber(columnName: String = "id"): DataFrame<T> = dataFrameOf(columns() + indexColumn(columnName,
    nrow()
)).typed<T>()
fun DataCol.addRowNumber(columnName: String = "id") = dataFrameOf(listOf(indexColumn(columnName, size), this))

// Column operations

inline fun <reified T : Comparable<T>> ColumnData<T?>.median() = values.asSequence().filterNotNull().asIterable().median()

// Update

inline fun <reified C> headPlusArray(head: C, cols: Array<out C>) = (listOf(head) + cols.toList()).toTypedArray()

inline fun <reified C> ColumnsSelectorReceiver<*>.colsOfType(noinline filter: (ColumnData<C>) -> Boolean = { true }) = colsOfType(getType<C>(), filter)

// column grouping

internal fun <C> TreeNode<ColumnPosition>.column() = ColumnWithPathImpl(data.column as ColumnData<C>, pathFromRoot())

internal fun TreeNode<ColumnPosition>.allRemovedColumns() = dfs { it.data.wasRemoved && it.data.column != null }

internal fun TreeNode<ColumnPosition>.allWithColumns() = dfs { it.data.column != null }

internal fun Iterable<ColumnWithPath<*>>.dfs(): List<ColumnWithPath<*>> {

    val result = mutableListOf<ColumnWithPath<*>>()
    fun dfs(cols: Iterable<ColumnWithPath<*>>) {
        cols.forEach {
            result.add(it)
            val path = it.path
            if (it.data.isGrouped())
                dfs(it.data.asGrouped().columns().map { it.addPath(path + it.name()) })
        }
    }
    dfs(this)
    return result
}

internal fun DataFrame<*>.collectTree() = columns().map { it.addPath() }.collectTree()

internal fun List<ColumnWithPath<*>>.collectTree() = collectTree(DataCol.empty()) { it }

internal fun <D> DataFrame<*>.collectTree(emptyData: D, createData: (DataCol) -> D) = columns().map { it.addPath() }.collectTree(emptyData, createData)

internal fun <D> List<ColumnWithPath<*>>.collectTree(emptyData: D, createData: (DataCol) -> D): TreeNode<D> {

    val root = TreeNode.createRoot(emptyData)

    fun collectColumns(col: DataCol, parentNode: TreeNode<D>) {
        val newNode = parentNode.getOrPut(col.name()) { createData(col) }
        if(col.isGrouped()){
            col.asGrouped().columns().forEach {
                collectColumns(it, newNode)
            }
        }
    }
    forEach {
        if(it.path.isEmpty()){
            it.data.asGrouped().df.columns().forEach {
                collectColumns(it, root)
            }
        }else {
            val node = root.getOrPut(it.path.dropLast(1), emptyData)
            collectColumns(it.data, node)
        }
    }
    return root
}

internal data class ColumnPosition(val originalIndex: Int, var wasRemoved: Boolean, var column: DataCol?)

// TODO: replace 'insertionPath' with TreeNode<ColumnToInsert> tree
internal data class ColumnToInsert(val insertionPath: ColumnPath, val originalNode: TreeNode<ColumnPosition>?, val column: DataCol)

fun Column.getParent(): GroupedColumnDef? = when (this) {
    is ColumnWithParent<*> -> parent
    is ColumnDataWithParent<*> -> parent
    else -> null
}

fun Column.getPath(): ColumnPath {
    val list = mutableListOf<String>()
    var c = this.asNullable()
    while (c != null) {
        list.add(c.name())
        c = c.getParent()
    }
    list.reverse()
    return list
}

internal fun <T> DataFrame<T>.collectTree(selector: ColumnsSelector<T, *>): TreeNode<DataCol?> {

    val colPaths = getColumnPaths(selector)

    val root = TreeNode.createRoot(null as DataCol?)

    colPaths.forEach {

        var column: DataCol? = null
        var node: TreeNode<DataCol?> = root
        it.forEach {
            when (column) {
                null -> column = this[it]
                else -> column = column!!.asFrame()[it]
            }
            node = node.getOrPut(it) { null }
        }
        node.data = column
    }

    return root
}

internal fun <T> DataFrame<T>.doInsert(columns: List<ColumnToInsert>) = insertColumns(this, columns)

internal fun <T> insertColumns(df: DataFrame<T>?, columns: List<ColumnToInsert>) =
        insertColumns(df, columns, columns.firstOrNull()?.originalNode?.getRoot(), 0)

internal fun insertColumns(columns: List<ColumnToInsert>) =
        insertColumns<Unit>(null, columns, columns.firstOrNull()?.originalNode?.getRoot(), 0)

internal fun <T> insertColumns(df: DataFrame<T>?, columns: List<ColumnToInsert>, treeNode: TreeNode<ColumnPosition>?, depth: Int): DataFrame<T> {

    if (columns.isEmpty()) return df ?: DataFrame.empty()

    val childDepth = depth + 1

    val columnsMap = columns.groupBy { it.insertionPath[depth] }.toMutableMap() // map: columnName -> columnsToAdd

    val newColumns = mutableListOf<DataCol>()

    // insert new columns under existing
    df?.columns()?.forEach {
        val subTree = columnsMap[it.name()]
        if (subTree != null) {
            // assert that new columns go directly under current column so they have longer paths
            val invalidPath = subTree.firstOrNull { it.insertionPath.size == childDepth }
            assert(invalidPath == null) { "Can't move column to path: " + invalidPath!!.insertionPath.joinToString(".") + ". Column with this path already exists" }
            val group = it as? GroupedColumn<*>
            assert(group != null) { "Can not insert columns under a column '${it.name()}', because it is not a column group" }
            val newDf = insertColumns(group!!.df, subTree, treeNode?.get(it.name()), childDepth)
            val newCol = group.withDf(newDf)
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
                if (it.originalNode == null) Int.MAX_VALUE
                else {
                    var col = it.originalNode!!
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

        val nodeToInsert = columns.firstOrNull { it.insertionPath.size == childDepth } // try to find existing node to insert
        val newCol = if (nodeToInsert != null) {
            val column = nodeToInsert.column
            if (columns.size > 1) {
                assert(columns.count { it.insertionPath.size == childDepth } == 1) { "Can not insert more than one column into the path ${nodeToInsert.insertionPath}" }
                val group = column as GroupedColumn<*>
                val newDf = insertColumns(group.df, columns.filter { it.insertionPath.size > childDepth }, treeNode?.get(name), childDepth)
                group.withDf(newDf)
            } else column.rename(name)
        } else {
            val newDf = insertColumns<Unit>(null, columns, treeNode?.get(name), childDepth)
            ColumnData.createGroup(name, newDf) // new node needs to be created
        }
        if (insertionIndex == Int.MAX_VALUE)
            newColumns.add(newCol)
        else {
            newColumns.add(insertionIndex + insertionIndexOffset, newCol)
            insertionIndexOffset++
        }
    }

    return newColumns.asDataFrame()
}

internal fun <T> DataFrame<T>.splitByIndices(startIndices: List<Int>): List<DataFrame<T>> {
    return (startIndices + listOf(nrow())).zipWithNext { start, endExclusive ->
        get(start until endExclusive)
    }
}

internal fun <T> List<T>.splitByIndices(startIndices: List<Int>): List<List<T>> {
    return (startIndices + listOf(size)).zipWithNext { start, endExclusive ->
        subList(start, endExclusive)
    }
}

internal fun KClass<*>.createType(typeArgument: KType?) = if (typeArgument != null) createType(listOf(KTypeProjection.invariant(typeArgument)))
else createStarProjectedType(false)

internal inline fun <reified T> createType(typeArgument: KType? = null) = T::class.createType(typeArgument)

fun <T> TableColumn<T>.union() = if (size > 0) values.union() else df.getRows(emptyList())

internal fun <T> T.asNullable() = this as T?

internal fun <T> List<T>.last(count: Int) = subList(size - count, size)

/**
 * Shorten column paths as much as possible to keep them unique
 */
internal fun <C> List<ColumnWithPath<C>>.shortenPaths(): List<ColumnWithPath<C>> {

    // try to use just column name as column path
    val map = groupBy { it.path.last(1) }.toMutableMap()

    fun add(path: ColumnPath, column: ColumnWithPath<C>) {
        val list: MutableList<ColumnWithPath<C>> = (map.getOrPut(path) { mutableListOf() } as? MutableList<ColumnWithPath<C>>)
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
                val newPath = if (path.size < keyLength) path.last(keyLength + 1) else path
                add(newPath, it)
            }
        }
        conflicts = map.filter { it.value.size > 1 }
    }

    val pathRemapping = map.map { it.value.single().path to it.key }.toMap()

    return map { it.data.addPath(pathRemapping[it.path]!!) }
}