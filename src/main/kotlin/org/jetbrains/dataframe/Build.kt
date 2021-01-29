package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.DataFrameImpl
import org.jetbrains.dataframe.impl.TreeNode
import org.jetbrains.dataframe.impl.columns.DataColumnWithParentImpl
import org.jetbrains.dataframe.impl.columns.MapColumnWithParent
import org.jetbrains.dataframe.impl.getOrPut
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

class IterableDataFrameBuilder<T>(val source: Iterable<T>) {
    internal val columns = mutableListOf<AnyCol>()

    fun add(column: AnyCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: T.() -> R?) = add(column(name, source.map { expression(it) }))

    inline infix fun <reified R> String.to(noinline expression: T.() -> R?) = add(this, expression)

    inline infix operator fun <reified R> String.invoke(noinline expression: T.() -> R?) = add(this, expression)
}

fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.() -> Unit): AnyFrame {
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
            DataColumn.create(it.name, values, property.returnType.withNullability(nullable))
        }.let { dataFrameOf(it) }


fun dataFrameOf(columns: Iterable<AnyCol>): AnyFrame = DataFrameImpl<Unit>(columns.map { it.unbox() })

fun dataFrameOf(vararg header: ColumnReference<*>) = DataFrameBuilder(header.map { it.name() })

fun dataFrameOf(vararg columns: AnyCol): AnyFrame = dataFrameOf(columns.asIterable())

fun emptyDataFrame(nrow: Int) = DataFrame.empty<Any?>(nrow)

fun dataFrameOf(vararg header: String) = dataFrameOf(header.toList())

fun dataFrameOf(header: List<String>) = DataFrameBuilder(header)

// TODO: remove checks for ColumnWithParent types
internal fun AnyCol.unbox(): AnyCol = when (this) {
    is ColumnWithPath<*> -> data.unbox()
    is DataColumnWithParentImpl<*> -> source.unbox()
    is MapColumnWithParent<*> -> source.unbox()
    else -> this
}

fun <T> Iterable<AnyCol>.asDataFrame() = dataFrameOf(this).typed<T>()

fun <T> List<Pair<List<String>, AnyCol>>.toDataFrame(): DataFrame<T>? {
    if(size == 0) return null
    val tree = TreeNode.createRoot(null as AnyCol?)
    forEach {
        val (path, col) = it
        val node = tree.getOrPut(path)
        if(node.data != null)
            throw UnsupportedOperationException("Duplicate column paths: $path")
        node.data = col
    }
    fun dfs(node: TreeNode<AnyCol?>){
        if(node.children.isNotEmpty()){
            if(node.data != null)
                throw UnsupportedOperationException("Can not add data to grouped column: ${node.pathFromRoot()}")
            node.children.forEach { dfs(it) }
            node.data = DataColumn.createGroup(node.name, node.children.map { it.data!! }.asDataFrame<Unit>())
        }else assert(node.data != null)
    }
    dfs(tree)
    return tree.data!!.asFrame().typed<T>()
}

class DataFrameBuilder(private val columnNames: List<String>) {

    operator fun invoke(vararg values: Any?): AnyFrame {

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
    DataColumn.create(name, values, it)
}