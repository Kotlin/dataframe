package org.jetbrains.dataframe

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

fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.() -> Unit): TypedDataFrame<Unit> {
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


fun dataFrameOf(columns: Iterable<DataCol>): TypedDataFrame<Unit> = TypedDataFrameImpl(columns.map { it.unbox() })

fun dataFrameOf(vararg header: ColumnDef<*>) = DataFrameBuilder(header.map { it.name })

fun <T> emptyDataFrame() = dataFrameOf(emptyList<DataCol>()).typed<T>()

fun dataFrameOf(vararg header: String) = dataFrameOf(header.toList())

fun dataFrameOf(header: List<String>) = DataFrameBuilder(header)

internal fun DataCol.unbox(): DataCol = when (this) {
    is RenamedColumn<*> -> source.unbox().doRename(name)
    is ColumnDataWithParent<*> -> source.unbox()
    is GroupedColumnWithParent<*> -> source.unbox()
    else -> this
}

fun <T> Iterable<DataCol>.asDataFrame() = dataFrameOf(this).typed<T>()

class DataFrameBuilder(private val columnNames: List<String>) {

    operator fun invoke(vararg values: Any?): TypedDataFrame<Unit> {

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
    ColumnDataImpl(values, name, it)
}