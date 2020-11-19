package org.jetbrains.dataframe

import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

typealias RowSelector<T, R> = TypedDataFrameRow<T>.(TypedDataFrameRow<T>) -> R

typealias DataFrameExpression<T, R> = TypedDataFrame<T>.(TypedDataFrame<T>) -> R

typealias RowFilter<T> = RowSelector<T, Boolean>

class TypedColumnsFromDataRowBuilder<T>(val dataFrame: TypedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: RowSelector<T, R>) = add(dataFrame.new(name, expression))

    inline infix fun <reified R> String.to(noinline expression: RowSelector<T, R>) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: RowSelector<T, R>) = add(this, expression)
}

// add Column

operator fun TypedDataFrame<*>.plus(col: DataCol) = dataFrameOf(columns + col)
operator fun TypedDataFrame<*>.plus(col: Iterable<DataCol>) = dataFrameOf(columns + col)

inline fun <reified R, T> TypedDataFrame<T>.add(name: String, noinline expression: RowSelector<T, R>) =
        (this + new(name, expression))

inline fun <reified R, T> TypedDataFrame<T>.add(column: ColumnDefinition<R>, noinline expression: RowSelector<T, R>) =
        (this + new(column.name, expression))

inline fun <reified R, T> GroupedDataFrame<T>.add(name: String, noinline expression: RowSelector<T, R>) =
        modify { add(name, expression) }

fun <T> TypedDataFrame<T>.add(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) =
        with(TypedColumnsFromDataRowBuilder(this)) {
            body(this)
            dataFrameOf(this@add.columns + columns).typed<T>()
        }

fun rowNumber(columnName: String = "id") = AddRowNumberStub(columnName)

data class AddRowNumberStub(val columnName: String)

operator fun <T> TypedDataFrame<T>.plus(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) = add(body)

// map

fun <T> TypedDataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): TypedDataFrame<Unit> {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}


// group by

inline fun <T, reified R> TypedDataFrame<T>.groupBy(name: String = "key", noinline expression: RowSelector<T, R?>) =
        add(name, expression).groupBy(name)

// size

val TypedDataFrame<*>.size: DataFrameSize get() = DataFrameSize(ncol, nrow)

// toList

inline fun <reified C> TypedDataFrame<*>.toList() = DataFrameToListTypedStub(this, C::class)

fun TypedDataFrame<*>.toList(className: String) = DataFrameToListNamedStub(this, className)

inline fun <T, reified R : Number> TypedDataFrameRow<T>.diff(selector: RowSelector<T, R>) = when (R::class) {
    Double::class -> prev?.let { (selector(this) as Double) - (selector(it) as Double) } ?: .0
    Int::class -> prev?.let { (selector(this) as Int) - (selector(it) as Int) } ?: 0
    Long::class -> prev?.let { (selector(this) as Long) - (selector(it) as Long) } ?: 0
    else -> throw NotImplementedError()
}

fun <T> TypedDataFrameRow<T>.movingAverage(k: Int, selector: RowSelector<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumByDouble {
        count++
        selector(it).toDouble()
    } / count
}

// merge

fun Iterable<TypedDataFrame<*>>.union() = merge(toList())


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

fun merge(dataFrames: List<TypedDataFrame<*>>): TypedDataFrame<Unit> {
    if (dataFrames.size == 1) return dataFrames[0].typed()

    // TODO: get rid of fake column. Create data frame without columns, but with rows count
    val fakeColumn = "__FAKE_COLUMN"
    return dataFrames
            .fold(emptyList<String>()) { acc, df -> acc + (df.columnNames() - acc) } // collect column names preserving order
            .map { name ->
                val list = mutableListOf<Any?>()
                var nullable = false
                val types = mutableSetOf<KType>()

                val firstColumn = dataFrames.mapNotNull { it.tryGetColumn(name) }.first()
                if (firstColumn.isGrouped()) {
                    val groupedDataFrames = dataFrames.map {
                        val column = it.tryGetColumn(name)
                        if (column != null)
                            column.asGroup()
                        else {
                            val fakeColumnData = column(fakeColumn, arrayOfNulls<Any?>(it.nrow).asList(), true)
                            dataFrameOf(listOf(fakeColumnData))
                        }
                    }
                    val merged = merge(groupedDataFrames).let { if(it.tryGetColumn(fakeColumn) != null) it.remove(fakeColumn) else it }
                    ColumnData.createGroup(name, merged)
                } else {

                    dataFrames.forEach {
                        val column = it.tryGetColumn(name)
                        if (column != null) {
                            nullable = nullable || column.hasNulls
                            types.add(column.type)
                            list.addAll(column.values)
                        } else {
                            if (it.nrow > 0) nullable = true
                            for (row in (0 until it.nrow)) {
                                list.add(null)
                            }
                        }
                    }

                    val baseType = when {
                        types.size == 1 -> types.single()
                        types.map { it.jvmErasure }.distinct().count() == 1 -> types.first().withNullability(nullable)
                        // TODO: implement correct parent type computation with valid type projections
                        else -> (commonParent(types.map { it.jvmErasure })
                                ?: Any::class).createStarProjectedType(nullable)
                    }
                    ColumnDataImpl(list, name, baseType)
                }
            }.let { dataFrameOf(it) }
}

operator fun <T> TypedDataFrame<T>.plus(other: TypedDataFrame<T>) = merge(listOf(this, other)).typed<T>()

fun <T> TypedDataFrame<T>.union(vararg other: TypedDataFrame<T>) = merge(listOf(this) + other.toList()).typed<T>()

fun <T> TypedDataFrame<T>.rename(vararg mappings: Pair<String, String>): TypedDataFrame<T> {
    val map = mappings.toMap()
    return columns.map {
        val newName = map[it.name] ?: it.name
        it.doRename(newName)
    }.asDataFrame()
}

internal fun indexColumn(columnName: String, size: Int): DataCol = column(columnName, (0 until size).toList())

fun <T> TypedDataFrame<T>.addRowNumber(column: ColumnDef<Int>) = addRowNumber(column.name)
fun <T> TypedDataFrame<T>.addRowNumber(columnName: String = "id"): TypedDataFrame<T> = dataFrameOf(columns + indexColumn(columnName, nrow)).typed<T>()
fun DataCol.addRowNumber(columnName: String = "id") = dataFrameOf(listOf(indexColumn(columnName, size), this))

// Column operations

fun <T : Comparable<T>> ColumnData<T?>.min() = values.asSequence().filterNotNull().minOrNull()
fun <T : Comparable<T>> ColumnData<T?>.max() = values.asSequence().filterNotNull().maxOrNull()

// Update

class UpdateClause<T, C>(val df: TypedDataFrame<T>, val filter: UpdateExpression<T, C, Boolean>?, val cols: List<ColumnDef<C>>)

fun <T, C> UpdateClause<T, C>.where(predicate: UpdateExpression<T, C, Boolean>) = UpdateClause(df, predicate, cols)

typealias UpdateExpression<T, C, R> = TypedDataFrameRow<T>.(C) -> R

fun <T> TypedDataFrame<T>.addOrReplace(newColumns: List<DataCol>): TypedDataFrame<T> {
    val map = mutableMapOf<String, DataCol>()
    newColumns.map { it.name to it }.toMap(map)
    val oldCols = columns.map { map.remove(it.name) ?: it }
    val newCols = newColumns.filter { map.containsKey(it.name) }
    return dataFrameOf(oldCols + newCols).typed()
}

inline infix fun <T, C, reified R> UpdateClause<T, C>.with(noinline expression: UpdateExpression<T, C, R>): TypedDataFrame<T> {
    val newCols = cols.map { col ->
        var nullable = false
        val colData = df[col]
        val values = (0 until df.nrow).map {
            df[it].let { row ->
                val currentValue = colData[row.index]
                if (filter?.invoke(row, currentValue) == false)
                    currentValue as R
                else expression(row, currentValue)
            }.also { if (it == null) nullable = true }
        }
        var oldCol: ColumnData<*> = colData
        var newColumn: ColumnData<*> = column(col.name, values, nullable)

        while (oldCol is ColumnWithParent<*>) {
            val parent = oldCol.parent
            val newDf = parent.df.addOrReplace(listOf(newColumn))
            newColumn = ColumnData.createGroup(parent.name, newDf)
            oldCol = parent
        }
        newColumn
    }
    return df.addOrReplace(newCols)
}

inline fun <reified C> headPlusArray(head: C, cols: Array<out C>) = (listOf(head) + cols.toList()).toTypedArray()

inline fun <T, C, reified R> TypedDataFrame<T>.update(firstCol: ColumnDef<C>, vararg cols: ColumnDef<C>, noinline expression: UpdateExpression<T, C, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

inline fun <T, C, reified R> TypedDataFrame<T>.update(firstCol: KProperty<C>, vararg cols: KProperty<C>, noinline expression: UpdateExpression<T, C, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

inline fun <T, reified R> TypedDataFrame<T>.update(firstCol: String, vararg cols: String, noinline expression: UpdateExpression<T, Any?, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

fun <T, C> UpdateClause<T, C>.withNull() = with { null as Any? }
inline infix fun <T, C, reified R> UpdateClause<T, C>.with(value: R) = with { value }

fun <T, C> TypedDataFrame<T>.update(cols: Iterable<ColumnDef<C>>) = UpdateClause(this, null, cols.toList())
fun <T> TypedDataFrame<T>.update(vararg cols: String) = update(getColumns(cols))
fun <T, C> TypedDataFrame<T>.update(vararg cols: KProperty<C>) = update(getColumns(cols))
fun <T, C> TypedDataFrame<T>.update(vararg cols: ColumnDef<C>) = update(cols.asIterable())
fun <T, C> TypedDataFrame<T>.update(cols: ColumnsSelector<T, C>) = update(getColumns(cols))

fun <T, C> TypedDataFrame<T>.fillNulls(cols: ColumnsSelector<T, C>) = fillNulls(getColumns(cols))
fun <T, C> TypedDataFrame<T>.fillNulls(cols: Iterable<ColumnDef<C>>) = update(cols).where { it == null }
fun <T> TypedDataFrame<T>.fillNulls(vararg cols: String) = fillNulls(getColumns(cols))
fun <T, C> TypedDataFrame<T>.fillNulls(vararg cols: KProperty<C>) = fillNulls(getColumns(cols))
fun <T, C> TypedDataFrame<T>.fillNulls(vararg cols: ColumnDef<C>) = fillNulls(cols.asIterable())
// Move

fun <T> TypedDataFrame<T>.moveTo(newColumnIndex: Int, cols: ColumnsSelector<T, *>) = moveTo(newColumnIndex, getColumns(cols) as Iterable<Column>)
fun <T> TypedDataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: String) = moveTo(newColumnIndex, getColumns(cols))
fun <T> TypedDataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: KProperty<*>) = moveTo(newColumnIndex, getColumns(cols))
fun <T> TypedDataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: Column) = moveTo(newColumnIndex, cols.asIterable())
fun <T> TypedDataFrame<T>.moveTo(newColumnIndex: Int, cols: Iterable<Column>): TypedDataFrame<T> {
    val columnsToMove = cols.map { this[it] }
    val otherColumns = columns - columnsToMove
    val newColumnList = otherColumns.subList(0, newColumnIndex) + columnsToMove + otherColumns.subList(newColumnIndex, otherColumns.size)
    return dataFrameOf(newColumnList).typed()
}

fun <T> TypedDataFrame<T>.moveToLeft(cols: ColumnsSelector<T, *>) = moveToLeft(getColumns(cols))
fun <T> TypedDataFrame<T>.moveToLeft(cols: Iterable<Column>) = moveTo(0, cols)
fun <T> TypedDataFrame<T>.moveToLeft(vararg cols: String) = moveToLeft(getColumns(cols))
fun <T> TypedDataFrame<T>.moveToLeft(vararg cols: Column) = moveToLeft(cols.asIterable())
fun <T> TypedDataFrame<T>.moveToLeft(vararg cols: KProperty<*>) = moveToLeft(getColumns(cols))

fun <T> TypedDataFrame<T>.moveToRight(cols: Iterable<Column>) = moveTo(ncol - cols.count(), cols)
fun <T> TypedDataFrame<T>.moveToRight(cols: ColumnsSelector<T, *>) = moveToRight(getColumns(cols))
fun <T> TypedDataFrame<T>.moveToRight(vararg cols: String) = moveToRight(getColumns(cols))
fun <T> TypedDataFrame<T>.moveToRight(vararg cols: Column) = moveToRight(cols.asIterable())
fun <T> TypedDataFrame<T>.moveToRight(vararg cols: KProperty<*>) = moveToRight(getColumns(cols))

fun <C> List<ColumnSet<C>>.toColumnSet() = ColumnGroup(this)

fun <C> TypedDataFrameWithColumns<*>.colsOfType(type: KType, filter: (ColumnData<C>) -> Boolean = { true }) = cols.filter { it.type.isSubtypeOf(type) && (type.isMarkedNullable || !it.hasNulls) && filter(it.typed()) }.map { it.typed<C>() }.toColumnSet()

inline fun <reified C> TypedDataFrameWithColumns<*>.colsOfType(noinline filter: (ColumnData<C>) -> Boolean = { true }) = colsOfType(getType<C>(), filter)

fun <T> TypedDataFrame<T>.summary() =
        columns.toDataFrame {
            "column" { name }
            "type" { type.fullName }
            "distinct values" { ndistinct }
            "nulls %" { values.count { it == null }.toDouble() * 100 / size.let { if (it == 0) 1 else it } }
            "most frequent value" { values.groupBy { it }.maxByOrNull { it.value.size }?.key }
        }

data class CastClause<T>(val df: TypedDataFrame<T>, val columns: Set<Column>) {
    inline fun <reified C> to() = df.columns.map { if (columns.contains(it)) it.cast<C>() else it }.asDataFrame<T>()
}

fun <T> TypedDataFrame<T>.cast(selector: ColumnsSelector<T, *>) = CastClause(this, getColumns(selector).toSet())

// column grouping

class GroupColsClause<T, C> internal constructor(internal val df: TypedDataFrame<T>, internal val removed: TreeNode<RemovedColumn>) {
    internal fun removedColumns() = removed.dfs().filter { it.data.wasRemoved && it.data.column != null }

    internal val TreeNode<RemovedColumn>.column get() = ColumnWithPath<C>(data.column as ColumnData<C>, pathFromRoot())
}

fun <T, C> TypedDataFrame<T>.move(selector: ColumnsSelector<T, C>): GroupColsClause<T, C> {
    val (df, removed) = doRemove(getColumns(selector))
    return GroupColsClause(df, removed)
}

fun <T, C> TypedDataFrame<T>.ungroupCol(selector: ColumnSelector<T, TypedDataFrameRow<C>>) = ungroupCols(selector)

fun <T, C> TypedDataFrame<T>.ungroupCols(selector: ColumnsSelector<T, TypedDataFrameRow<C>>): TypedDataFrame<T> {

    val groupedColumns = getGroupColumns(selector)
    val columnIndices = groupedColumns.map { getColumnIndex(it) to it }.toMap()
    val resultColumns = mutableListOf<DataCol>()
    val otherColumns = this - groupedColumns
    val nameGenerator = otherColumns.nameGenerator()
    for (colIndex in 0 until ncol) {
        val groupedColumn = columnIndices[colIndex]
        if (groupedColumn != null) {
            groupedColumn.df.columns.forEach {
                resultColumns.add(it.ensureUniqueName(nameGenerator))
            }
        } else resultColumns.add(columns[colIndex])
    }
    return resultColumns.asDataFrame()
}

internal fun Iterable<DataCol>.dfs(): List<ColumnWithPath<*>> {

    val result = mutableListOf<ColumnWithPath<*>>()
    fun dfs(cols: Iterable<DataCol>, path: List<String>) {
        cols.forEach {
            val p = path + it.name
            result.add(ColumnWithPath(it, p))
            if (it is GroupedColumn<*>)
                dfs(it.df.columns, p)
        }
    }
    dfs(this, emptyList())
    return result
}

internal class TreeNode<T>(val name: String, val depth: Int, var data: T, val parent: TreeNode<T>? = null) {

    companion object {
        fun <T> createRoot(data: T) = TreeNode<T>("", 0, data)
    }

    private val myChildren = mutableListOf<TreeNode<T>>()
    private val childrenMap = mutableMapOf<String, TreeNode<T>>()

    val children: List<TreeNode<T>>
        get() = myChildren

    fun getRoot(): TreeNode<T> = if (parent == null) this else parent.getRoot()

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

    fun create(childName: String, childData: T): TreeNode<T> {
        val node = TreeNode(childName, depth + 1, childData, this)
        myChildren.add(node)
        childrenMap[childName] = node
        return node
    }

    fun dfs(): List<TreeNode<T>> {

        val result = mutableListOf<TreeNode<T>>()
        fun doDfs(node: TreeNode<T>) {
            result.add(node)
            node.children.forEach {
                doDfs(it)
            }
        }
        doDfs(this)
        return result
    }
}

internal data class RemovedColumn(val index: Int, var wasRemoved: Boolean, var column: DataCol?)

internal data class ColumnToInsert(val path: List<String>, val removedColumn: TreeNode<RemovedColumn>)

internal fun <T> TypedDataFrame<T>.doRemove(cols: Iterable<Column>): Pair<TypedDataFrame<T>, TreeNode<RemovedColumn>> {

    val colPaths = cols.map {
        if (it is ColumnWithPath<*>)
            it.path
        else {
            var c = it
            val list = mutableListOf<String>()
            while (c is ColumnWithParent<*>) {
                list.add(c.name)
                c = c.parent
            }
            list.add(c.name)
            list.reverse()
            list
        }
    }

    val removeRoot = TreeNode.createRoot(RemovedColumn(-1, false, null))

    fun <C> remove(df: TypedDataFrame<C>, paths: List<List<String>>, removeNode: TreeNode<RemovedColumn>): TypedDataFrame<C>? {
        val depth = removeNode.depth
        val children = paths.groupBy { it[depth] }
        val newCols = mutableListOf<DataCol>()
        df.columns.forEachIndexed { index, column ->
            val childPaths = children[column.name]
            if (childPaths != null) {
                val node = removeNode.create(column.name, RemovedColumn(index, true, null))
                if (childPaths.all { it.size > depth + 1 }) {
                    val groupCol = (column as GroupedColumn<C>)
                    val newDf = remove(groupCol.df, childPaths, node)
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
        return newCols.asDataFrame()
    }

    val newDf = remove(this, colPaths, removeRoot) ?: emptyDataFrame()
    return newDf to removeRoot
}

internal fun <T> insertColumns(df: TypedDataFrame<T>?, columns: List<ColumnToInsert>) =
        insertColumns(df, columns, columns.firstOrNull()?.removedColumn?.getRoot(), 0)

internal fun <T> insertColumns(df: TypedDataFrame<T>?, columns: List<ColumnToInsert>, treeNode: TreeNode<RemovedColumn>?, depth: Int): TypedDataFrame<T> {

    val childDepth = depth + 1

    val columnsMap = columns.groupBy { it.path[depth] }.toMutableMap() // map: columnName -> columnsToAdd

    val newColumns = mutableListOf<DataCol>()

    // insert new columns under existing
    df?.columns?.forEach {
        val subTree = columnsMap[it.name]
        if (subTree != null) {
            // assert that new columns go directly under current column so they have longer paths
            val invalidPath = subTree.firstOrNull { it.path.size == childDepth }
            assert(invalidPath == null) { "Can't move column to path: " + invalidPath!!.path.joinToString(".") + ". Column with this path already exists" }
            val group = it as? GroupedColumn<*>
            assert(group != null) { "Can not insert columns under a column '${it.name}', because it is not a column group" }
            val newDf = insertColumns(group!!.df, subTree, treeNode?.get(it.name), childDepth)
            val newCol = group.withDf(newDf)
            newColumns.add(newCol)
            columnsMap.remove(it.name)
        } else newColumns.add(it)
    }

    // collect new columns to insert
    val columnsToAdd = columns.mapNotNull {
        val name = it.path[depth]
        val subTree = columnsMap[name]
        if (subTree != null) {
            columnsMap.remove(name)

            // look for columns in subtree that were originally located at the current insertion path
            // find the minimal original index among them
            // new column will be inserted at that position
            val minIndex = subTree.minOf {
                var col = it.removedColumn
                while (col.depth > depth + 1) col = col.parent!!
                if (col.parent === treeNode) {
                    col.data.index
                } else Int.MAX_VALUE
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
            while (k < removedSiblings.size && removedSiblings[k].data.index < insertionIndex) {
                if (removedSiblings[k].data.wasRemoved) insertionIndexOffset--
                k++
            }
        }

        val nodeToInsert = columns.firstOrNull { it.path.size == childDepth } // try to find existing node to insert
        val newCol = if (nodeToInsert != null) {
            val column = nodeToInsert.removedColumn.data.column!!
            if (columns.size > 1) {
                assert(columns.count { it.path.size == childDepth } == 1) { "Can not insert more than one column into the path ${nodeToInsert.path}" }
                val group = column as GroupedColumn<*>
                val newDf = insertColumns(group.df, columns.filter { it.path.size > childDepth }, treeNode?.get(name), childDepth)
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

internal fun Column.getPath(): List<String> =
        when (this) {
            is ColumnWithParent<*> -> parent.getPath() + name
            else -> listOf(name)
        }


interface DataFrameForMove<T> : DataFrameBase<T> {

    fun path(vararg columns: String): List<String> = listOf(*columns)

    fun SingleColumn<*>.addPath(vararg columns: String): List<String> = (this as Column).getPath() + listOf(*columns)

    operator fun SingleColumn<*>.plus(column: String) = addPath(column)
}

class MoveReceiver<T>(df: TypedDataFrame<T>) : DataFrameForMove<T>, DataFrameBase<T> by df

fun <T, C> GroupColsClause<T, C>.intoGroup(groupPath: DataFrameForMove<T>.(DataFrameForMove<T>) -> List<String>): TypedDataFrame<T> {
    val receiver = MoveReceiver(df)
    val path = groupPath(receiver, receiver)
    val columnsToInsert = removedColumns().map { ColumnToInsert(path + it.name, it) }
    return insertColumns(df, columnsToInsert)
}

fun <T, C> GroupColsClause<T, C>.intoGroups(groupPath: DataFrameForMove<T>.(ColumnWithPath<C>) -> String): TypedDataFrame<T> {
    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map { ColumnToInsert(listOf(groupPath(receiver, it.column), it.name), it) }
    return insertColumns(df, columnsToInsert)
}

fun <T, C> GroupColsClause<T, C>.into(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> List<String>): TypedDataFrame<T> {

    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map { ColumnToInsert(groupNameExpression(receiver, it.column), it) }
    return insertColumns(df, columnsToInsert)
}

fun <T, C> GroupColsClause<T, C>.into(name: String) = intoGroup { listOf(name) }

fun <T, C> GroupColsClause<T, C>.to(columnIndex: Int): TypedDataFrame<T> {
    val newColumnList = df.columns.subList(0, columnIndex) + removedColumns().map { it.data.column as ColumnData<C> } + df.columns.subList(columnIndex, df.ncol)
    return newColumnList.asDataFrame()
}

fun <T, C> GroupColsClause<T, C>.toLeft() = to(0)

fun <T, C> GroupColsClause<T, C>.toRight() = to(df.ncol)

internal fun <T> TypedDataFrame<T>.splitByIndices(startIndices: List<Int>): List<TypedDataFrame<T>> {
    return (startIndices + listOf(nrow)).zipWithNext { start, endExclusive ->
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