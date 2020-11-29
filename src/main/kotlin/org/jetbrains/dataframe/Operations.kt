package org.jetbrains.dataframe

import com.beust.klaxon.internal.firstNotNullResult
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

inline fun <reified R, T, G> GroupedDataFrame<T, G>.add(name: String, noinline expression: RowSelector<G, R>) =
        modify { add(name, expression) }

fun <T, G> TypedDataFrame<T>.asGrouped(selector: ColumnSelector<T, TypedDataFrame<G>>): GroupedDataFrame<T, G> {
    val column = getColumn(selector).asTable<G>()
    return GroupedDataFrameImpl(this, column)
}

inline fun <reified R, T> TypedDataFrame<T>.add(column: ColumnDefinition<R>, noinline expression: RowSelector<T, R>) =
        (this + new(column.name, expression))

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

fun Iterable<TypedDataFrame<*>>.union() = merge(asList())


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

    return dataFrames
            .fold(emptyList<String>()) { acc, df -> acc + (df.columnNames() - acc) } // collect column names preserving order
            .map { name ->
                val list = mutableListOf<Any?>()
                var nullable = false
                val types = mutableSetOf<KType>()

                // TODO: check not only the first column
                val firstColumn = dataFrames.firstNotNullResult { it.tryGetColumn(name) }!!
                if (firstColumn.isGrouped()) {
                    val groupedDataFrames = dataFrames.map {
                        val column = it.tryGetColumn(name)
                        if (column != null)
                            column.asFrame()
                        else
                            emptyDataFrame(it.nrow)
                    }
                    val merged = merge(groupedDataFrames)
                    ColumnData.createGroup(name, merged)
                } else {

                    val defaultValue = firstColumn.defaultValue()

                    dataFrames.forEach {
                        if(it.nrow == 0) return@forEach
                        val column = it.tryGetColumn(name)
                        if (column != null) {
                            nullable = nullable || column.hasNulls
                            if(!column.hasNulls || column.values.any { it != null })
                                types.add(column.type)
                            list.addAll(column.values)
                        } else {
                            if (it.nrow > 0 && defaultValue == null) nullable = true
                            for (row in (0 until it.nrow)) {
                                list.add(defaultValue)
                            }
                        }
                    }

                    val baseType = when {
                        types.size == 1 -> types.single().withNullability(nullable)
                        types.map { it.jvmErasure }.distinct().count() == 1 -> types.first().withNullability(nullable)
                        // TODO: implement correct parent type computation with valid type projections
                        else -> (commonParent(types.map { it.jvmErasure })
                                ?: Any::class).createStarProjectedType(nullable)
                    }
                    ColumnData.create(name, list, baseType, defaultValue)
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
inline fun <reified T : Comparable<T>> ColumnData<T?>.median() = values.asSequence().filterNotNull().asIterable().median()

// Update

class UpdateClause<T, C>(val df: TypedDataFrame<T>, val filter: UpdateExpression<T, C, Boolean>?, val cols: List<ColumnDef<C>>)

fun <T, C> UpdateClause<T, C>.where(predicate: UpdateExpression<T, C, Boolean>) = UpdateClause(df, predicate, cols)

typealias UpdateExpression<T, C, R> = TypedDataFrameRow<T>.(C) -> R

typealias UpdateByColumnExpression<T, C, R> = (TypedDataFrameRow<T>, ColumnData<C>) -> R

fun <T> TypedDataFrame<T>.addOrReplace(newColumns: List<DataCol>): TypedDataFrame<T> {
    val map = mutableMapOf<String, DataCol>()
    newColumns.map { it.name to it }.toMap(map)
    val oldCols = columns.map { map.remove(it.name) ?: it }
    val newCols = newColumns.filter { map.containsKey(it.name) }
    return dataFrameOf(oldCols + newCols).typed()
}

fun <T, C, R> doUpdate(clause: UpdateClause<T, C>, type: KType, expression: (TypedDataFrameRow<T>,ColumnData<C>) -> R): TypedDataFrame<T> {

    val srcColumns = clause.cols.map { clause.df[it] }
    val (newDf, removed) = clause.df.doRemove(srcColumns)

    val toInsert = removed.dfs().mapNotNull {
        val srcColumn = it.data.column as? ColumnData<C>
        if (srcColumn != null) {

            var nullable = false
            val values = (0 until clause.df.nrow).map {
                clause.df[it].let { row ->
                    val currentValue = srcColumn[row.index]
                    if (clause.filter?.invoke(row, currentValue) == false)
                        currentValue as R
                    else expression(row, srcColumn)
                }.also { if (it == null) nullable = true }
            }
            val typeWithNullability = type.withNullability(nullable)

            val newColumn: ColumnData<*> = if (type.classifier == TypedDataFrame::class) {
                val firstFrame = values.firstOrNull { it != null } as? TypedDataFrame<T>
                if (firstFrame != null) {
                    // TODO :compute most general scheme for all data frames
                    ColumnData.createTable(srcColumn.name, values as List<TypedDataFrame<T>>, firstFrame)
                } else column(srcColumn.name, values, typeWithNullability)
            } else column(srcColumn.name, values, typeWithNullability)

            ColumnToInsert(it.pathFromRoot(), it, newColumn)
        } else null
    }
    return newDf.doInsert(toInsert)
}

// TODO: rename
inline infix fun <T, C, reified R> UpdateClause<T, C>.with2(noinline expression: UpdateByColumnExpression<T, C, R>) = doUpdate(this, getType<R>(), expression)

inline infix fun <T, C, reified R> UpdateClause<T, C>.with(noinline expression: UpdateExpression<T, C, R>) = doUpdate(this, getType<R>()) { row,column ->
    val currentValue = column[row.index]
    if (filter?.invoke(row, currentValue) == false)
        currentValue as R
    else expression(row, currentValue)
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

class MoveColsClause<T, C> internal constructor(internal val df: TypedDataFrame<T>, internal val removed: TreeNode<ColumnPosition>) {

    internal fun removedColumns() = removed.allRemovedColumns()

    internal val TreeNode<ColumnPosition>.column get() = column<C>()
}

internal fun <C> TreeNode<ColumnPosition>.column() = ColumnWithPath(data.column as ColumnData<C>, pathFromRoot())

internal fun TreeNode<ColumnPosition>.allRemovedColumns() = dfs { it.data.wasRemoved && it.data.column != null }

internal fun TreeNode<ColumnPosition>.allWithColumns() = dfs { it.data.column != null }

fun <T> TypedDataFrame<T>.flatten() = flatten { all() }

fun <T, C> TypedDataFrame<T>.flatten(selector: ColumnsSelector<T, C>): TypedDataFrame<T> {

    val columns = getColumnsWithData(selector)
    val groupedColumns = columns.mapNotNull { if (it.isGrouped()) it.asGrouped() else null }
    val prefixes = groupedColumns.map { it.getPath() }.toSet()
    val result = move { ColumnGroup(groupedColumns.map { it.colsDfs { !it.isGrouped() } }) }
            .into {
                var first = it.path.size - 1
                while (first > 0 && !prefixes.contains(it.path.subList(0, first)))
                    first--
                if (first == 0)
                    throw Exception()
                val collapsedPath = it.path.drop(first - 1).joinToString(".")
                it.path.subList(0, first - 1) + collapsedPath
            }
    return result
}

fun <T, C> TypedDataFrame<T>.move(selector: ColumnsSelector<T, C>): MoveColsClause<T, C> {

    val (df, removed) = doRemove(getColumns(selector))
    return MoveColsClause(df, removed)
}

fun <T, C> TypedDataFrame<T>.ungroup(selector: ColumnsSelector<T, C>): TypedDataFrame<T> {

    val columns = getColumnsWithData(selector)
    val groupedColumns = columns.mapNotNull { if (it.isGrouped()) it.asGrouped() else null }
    val result = move { ColumnGroup(groupedColumns.map { it.all() }) }.into { it.path.subList(0, it.path.size - 2) + it.path.last() }
    return result
}

internal fun Iterable<DataCol>.dfs(): List<DataCol> {

    val result = mutableListOf<DataCol>()
    fun dfs(cols: Iterable<DataCol>) {
        cols.forEach {
            result.add(it)
            if (it is GroupedColumn<*>)
                dfs(it.columns())
        }
    }
    dfs(this)
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

internal fun <T> TreeNode<T?>.getOrPut(path: List<String>): TreeNode<T?> {
    var node = this
    path.forEach {
        node = node.getOrPut(it) { null }
    }
    return node
}

internal fun TypedDataFrame<*>.collectTree() = collectTree(DataCol.empty()) { it }

internal fun <D> TypedDataFrame<*>.collectTree(emptyData: D, createData: (DataCol) -> D): TreeNode<D> {

    val root = TreeNode.createRoot(emptyData)

    fun collectColumns(cols: Iterable<DataCol>, node: TreeNode<D>) {
        cols.forEach {
            val newNode = node.addChild(it.name, createData(it))
            if (it is GroupedColumn<*>) {
                collectColumns(it.columns(), newNode)
            }
        }
    }

    collectColumns(columns, root)
    return root
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

internal data class ColumnPosition(val index: Int, var wasRemoved: Boolean, var column: DataCol?)

// TODO: replace 'insertionPath' with TreeNode<ColumnToInsert> tree
internal data class ColumnToInsert(val insertionPath: List<String>, val originalNode: TreeNode<ColumnPosition>?, val column: DataCol)

internal fun Column.getPath(): List<String> {
    val list = mutableListOf<String>()
    var c = this
    while (c is ColumnWithParent<*>) {
        list.add(c.name)
        c = c.parent
    }
    list.add(c.name)
    list.reverse()
    return list
}

internal fun <T> TreeNode<T?>.dfsNotNull() = dfs { it.data != null }.map { it as TreeNode<T> }

internal fun <T> TreeNode<T?>.dfsTopNotNull() = dfs(enterCondition = { it.data == null }, yieldCondition = { it.data != null }).map { it as TreeNode<T> }

internal fun <T> TypedDataFrame<T>.collectTree(cols: Iterable<Column>): TreeNode<DataCol?> {

    val colPaths = cols.map { it.getPath() }

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

internal fun <T> TypedDataFrame<T>.doRemove(cols: Iterable<Column>): Pair<TypedDataFrame<T>, TreeNode<ColumnPosition>> {

    val colPaths = cols.map { it.getPath() }

    val root = TreeNode.createRoot(ColumnPosition(-1, false, null))

    fun dfs(cols: Iterable<DataCol>, paths: List<List<String>>, node: TreeNode<ColumnPosition>): TypedDataFrame<*>? {
        val depth = node.depth
        val children = paths.groupBy { it[depth] }
        val newCols = mutableListOf<DataCol>()

        cols.forEachIndexed { index, column ->
            val childPaths = children[column.name]
            if (childPaths != null) {
                val node = node.addChild(column.name, ColumnPosition(index, true, null))
                if (childPaths.all { it.size > depth + 1 }) {
                    val groupCol = (column as GroupedColumn<*>)
                    val newDf = dfs(groupCol.df.columns, childPaths, node)
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
        return newCols.asDataFrame<Unit>()
    }

    val newDf = dfs(columns, colPaths, root) ?: emptyDataFrame(nrow)
    return newDf.typed<T>() to root
}

internal fun <T> TypedDataFrame<T>.doInsert(columns: List<ColumnToInsert>) = insertColumns(this, columns)

internal fun <T> insertColumns(df: TypedDataFrame<T>?, columns: List<ColumnToInsert>) =
        insertColumns(df, columns, columns.firstOrNull()?.originalNode?.getRoot(), 0)

internal fun insertColumns(columns: List<ColumnToInsert>) =
        insertColumns<Unit>(null, columns, columns.firstOrNull()?.originalNode?.getRoot(), 0)

internal fun <T> insertColumns(df: TypedDataFrame<T>?, columns: List<ColumnToInsert>, treeNode: TreeNode<ColumnPosition>?, depth: Int): TypedDataFrame<T> {

    val childDepth = depth + 1

    val columnsMap = columns.groupBy { it.insertionPath[depth] }.toMutableMap() // map: columnName -> columnsToAdd

    val newColumns = mutableListOf<DataCol>()

    // insert new columns under existing
    df?.columns?.forEach {
        val subTree = columnsMap[it.name]
        if (subTree != null) {
            // assert that new columns go directly under current column so they have longer paths
            val invalidPath = subTree.firstOrNull { it.insertionPath.size == childDepth }
            assert(invalidPath == null) { "Can't move column to path: " + invalidPath!!.insertionPath.joinToString(".") + ". Column with this path already exists" }
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
        val name = it.insertionPath[depth]
        val subTree = columnsMap[name]
        if (subTree != null) {
            columnsMap.remove(name)

            // look for columns in subtree that were originally located at the current insertion path
            // find the minimal original index among them
            // new column will be inserted at that position
            val minIndex = subTree.minOf {
                if(it.originalNode == null) Int.MAX_VALUE
                else {
                    var col = it.originalNode!!
                    if(col.depth > depth) col = col.getAncestor(depth + 1)
                    if (col.parent === treeNode) {
                        if(col.data.wasRemoved) col.data.index else col.data.index + 1
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
            while (k < removedSiblings.size && removedSiblings[k].data.index < insertionIndex) {
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

interface DataFrameForMove<T> : DataFrameBase<T> {

    fun path(vararg columns: String): List<String> = listOf(*columns)

    fun SingleColumn<*>.addPath(vararg columns: String): List<String> = (this as Column).getPath() + listOf(*columns)

    operator fun SingleColumn<*>.plus(column: String) = addPath(column)
}

class MoveReceiver<T>(df: TypedDataFrame<T>) : DataFrameForMove<T>, DataFrameBase<T> by df

fun <T, C> MoveColsClause<T, C>.intoGroup(groupPath: DataFrameForMove<T>.(DataFrameForMove<T>) -> List<String>): TypedDataFrame<T> {
    val receiver = MoveReceiver(df)
    val path = groupPath(receiver, receiver)
    val columnsToInsert = removedColumns().map { ColumnToInsert(path + it.name, it, it.data.column!!) }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.intoGroups(groupName: DataFrameForMove<T>.(ColumnWithPath<C>) -> String): TypedDataFrame<T> {
    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map {
        val col = it.column
        ColumnToInsert(listOf(groupName(receiver, col), it.name), it, col)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.toTop(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> String = { it.name }) = into { listOf(groupNameExpression(it)) }

fun <T, C> MoveColsClause<T, C>.into(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> List<String>): TypedDataFrame<T> {

    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map {
        val col = it.column
        ColumnToInsert(groupNameExpression(receiver, col), it, col)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.into(name: String) = intoGroup { listOf(name) }

fun <T, C> MoveColsClause<T, C>.into(path: List<String>) = intoGroup { path }

fun <T, C> MoveColsClause<T, C>.to(columnIndex: Int): TypedDataFrame<T> {
    val newColumnList = df.columns.subList(0, columnIndex) + removedColumns().map { it.data.column as ColumnData<C> } + df.columns.subList(columnIndex, df.ncol)
    return newColumnList.asDataFrame()
}

fun <T, C> MoveColsClause<T, C>.toLeft() = to(0)

fun <T, C> MoveColsClause<T, C>.toRight() = to(df.ncol)

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

fun <T> TableColumn<T>.union() = if(size > 0) values.union() else df.getRows(emptyList())

internal fun <T> T.asNullable() = this as T?

internal fun <T> List<T>.last(count: Int) = subList(size - count, size)

/**
 * Shorten column paths as much as possible to keep them unique
 */
internal fun <C> List<ColumnWithPath<C>>.shortenPaths(): List<ColumnWithPath<C>> {

    // try to use just column name as column path
    val map = groupBy { it.path.last(1) }.toMutableMap()

    fun add(path: ColumnPath, column: ColumnWithPath<C>){
        val list: MutableList<ColumnWithPath<C>> = (map.getOrPut(path) { mutableListOf() } as? MutableList<ColumnWithPath<C>>) ?: let {
            val values = map.remove(path)!!
            map.put(path, values.toMutableList()) as MutableList<ColumnWithPath<C>>
        }
        list.add(column)
    }

    // resolve name collisions by using more parts of column path
    var conflicts = map.filter { it.value.size > 1 }
    while(conflicts.size > 0) {
        conflicts.forEach {
            val key = it.key
            val keyLength = key.size
            map.remove(key)
            it.value.forEach {
                val path = it.path
                val newPath = if(path.size < keyLength) path.last(keyLength + 1) else path
                add(newPath, it)
            }
        }
        conflicts = map.filter { it.value.size > 1 }
    }

    val pathRemapping = map.map { it.value.single().path to it.key }.toMap()

    return map { ColumnWithPath<C>(it.source, pathRemapping[it.path]!!) }
}