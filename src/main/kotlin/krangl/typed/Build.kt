package krangl.typed

import krangl.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.javaField

class IterableDataFrameBuilder<T>(val source: Iterable<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: T.() -> R?) = add(newColumn(name, source.map { expression(it) }))

    inline infix fun <reified R> String.to(noinline expression: T.() -> R?) = add(this, expression)

    inline infix fun <reified R> String.`=`(noinline expression: T.() -> R?) = add(this, expression)

    inline infix operator fun <reified R> String.invoke(noinline expression: T.() -> R?) = add(this, expression)
}

fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.() -> Unit): UntypedDataFrame {
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
            TypedDataCol(values, nullable, it.name, property.returnType.classifier as KClass<*>)
        }.let { dataFrameOf(it) }


fun dataFrameOf(columns: Iterable<DataCol>): UntypedDataFrame = TypedDataFrameImpl(columns.toList())

fun SrcDataCol.typed() = when (this) {
    is IntCol -> createColumn(name, values.toList(), hasNulls)
    is LongCol -> createColumn(name, values.toList(), hasNulls)
    is StringCol -> createColumn(name, values.toList(), hasNulls)
    is BooleanCol -> createColumn(name, values.toList(), hasNulls)
    is DoubleCol -> createColumn(name, values.toList(), hasNulls)
    is AnyCol -> createColumn(name, values.toList(), hasNulls)
    else -> createColumn(name, values().toList(), hasNulls)
}

fun dataFrameOf(vararg header: NamedColumn) = InplaceDataFrameBuilder(header.map { it.name })