package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.DataFrameImpl
import org.jetbrains.dataframe.impl.TreeNode
import org.jetbrains.dataframe.impl.columns.ColumnDataWithParentImpl
import org.jetbrains.dataframe.impl.columns.GroupedColumnWithParent
import org.jetbrains.dataframe.impl.getOrPut
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

class IterableDataFrameBuilder<T>(val source: Iterable<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: T.() -> R?) = add(column(name, source.map { expression(it) }))

    inline infix fun <reified R> String.to(noinline expression: T.() -> R?) = add(this, expression)

    inline infix operator fun <reified R> String.invoke(noinline expression: T.() -> R?) = add(this, expression)
}

fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.() -> Unit): DataFrame<Unit> {
    val builder = IterableDataFrameBuilder<T>(this)
    builder.body()
    return dataFrameOf(builder.columns)
}

inline fun <reified T> Iterable<T>.toDataFrame() = T::class.declaredMembers
        .filter { it.parameters.toList().size == 1 }
        .filter { it is KProperty }
        .map {
            val property = (it as KProperty)
            property.javaField?.isAccessible = true
            var nullable = false
            val values = this.map { el -> it.call(el).also { if (it == null) nullable = true } }
            ColumnData.create(it.name, values, property.returnType.withNullability(nullable))
        }.let { dataFrameOf(it) }


fun dataFrameOf(columns: Iterable<DataCol>): DataFrame<Unit> = DataFrameImpl(columns.map { it.unbox() })

fun dataFrameOf(vararg header: ColumnDef<*>) = DataFrameBuilder(header.map { it.name() })

fun emptyDataFrame(nrow: Int) = DataFrame.empty<Any?>(nrow)

fun dataFrameOf(vararg header: String) = dataFrameOf(header.toList())

fun dataFrameOf(header: List<String>) = DataFrameBuilder(header)

// TODO: remove checks for ColumnWithParent types
internal fun DataCol.unbox(): DataCol = when (this) {
    is ColumnWithPath<*> -> data.unbox()
    is ColumnDataWithParentImpl<*> -> source.unbox()
    is GroupedColumnWithParent<*> -> source.unbox()
    else -> this
}

fun <T> Iterable<DataCol>.asDataFrame() = dataFrameOf(this).typed<T>()

fun <T> List<Pair<List<String>, DataCol>>.toDataFrame(): DataFrame<T>? {
    if(size == 0) return null
    val tree = TreeNode.createRoot(null as DataCol?)
    forEach {
        val (path, col) = it
        val node = tree.getOrPut(path)
        if(node.data != null)
            throw UnsupportedOperationException("Duplicate column paths: $path")
        node.data = col
    }
    fun dfs(node: TreeNode<DataCol?>){
        if(node.children.isNotEmpty()){
            if(node.data != null)
                throw UnsupportedOperationException("Can not add data to grouped column: ${node.pathFromRoot()}")
            node.children.forEach { dfs(it) }
            node.data = DataCol.createGroup(node.name, node.children.map { it.data!! }.asDataFrame<Unit>())
        }else assert(node.data != null)
    }
    dfs(tree)
    return tree.data!!.asFrame().typed<T>()
}

class DataFrameBuilder(private val columnNames: List<String>) {

    operator fun invoke(vararg values: Any?): DataFrame<Unit> {

        require(columnNames.size > 0 && values.size.rem(columnNames.size) == 0) {
            "data dimension ${columnNames.size} is not compatible with length of data vector ${values.size}"
        }

        val columnValues = values
                .mapIndexed { i, value -> i.rem(columnNames.size) to value }
                .groupBy { it.first }.values.map {
                    it.map { it.second }
                }

        val columns = columnNames.zip(columnValues).map { (columnName, values) ->
            guessColumnType(columnName, values)
        }

        return dataFrameOf(columns)
    }

    operator fun invoke(args: Iterable<Any?>) = invoke(*args.toList().toTypedArray())
    operator fun invoke(args: Sequence<Any?>) = invoke(*args.toList().toTypedArray())
}

internal fun Iterable<KClass<*>>.commonParent() = commonParents(this).withMostSuperclasses() ?: Any::class

internal fun Iterable<KClass<*>>.commonType(nullable: Boolean) = commonParent().createStarProjectedType(nullable)

internal fun guessValueType(values: List<Any?>): KType {
    var nullable = false
    val types = values.map {
        if (it == null) nullable = true
        it?.javaClass
    }.distinct().mapNotNull { it?.kotlin }
    return types.commonType(nullable)
}

internal fun guessColumnType(name: String, values: List<Any?>) = guessValueType(values).let {
    ColumnData.create(name, values, it)
}