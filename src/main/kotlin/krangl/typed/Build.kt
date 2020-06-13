package krangl.typed

import krangl.ArrayUtils
import krangl.DataFrame
import krangl.InplaceDataFrameBuilder
import krangl.util.asDF
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.javaField

class IterableDataFrameBuilder<T>(val source: Iterable<T>){
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: T.() -> R?) = add(newColumn(name, source.map { expression(it)}))

    inline infix fun <reified R> String.to(noinline expression: T.() -> R?) = add(this, expression)

    inline infix fun <reified R> String.`=`(noinline expression: T.() -> R?) = add(this, expression)

    inline infix operator fun <reified R> String.invoke(noinline expression: T.() -> R?) = add(this, expression)
}

fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.()->Unit): UntypedDataFrame {
    val builder = IterableDataFrameBuilder<T>(this)
    builder.body()
    return dataFrameOf(builder.columns)
}

inline fun <reified T> Iterable<T>.toDataFrame(): UntypedDataFrame {

    val declaredMembers = T::class.declaredMembers

    val properties = declaredMembers
            .filter { it.parameters.toList().size == 1 }
            .filter { it is KProperty }

    val results = properties.map {
        (it as KProperty).javaField?.isAccessible = true
        it.name to this.map { el -> it.call(el) }
    }

    val columns = results.map { ArrayUtils.handleListErasure(it.first, it.second).typed() }

    return dataFrameOf(columns)
}

fun dataFrameOf(columns: Iterable<DataCol>): UntypedDataFrame = TypedDataFrameImpl(columns.toList())

fun dataFrameOfSrc(columns: Iterable<SrcDataCol>) = dataFrameOf(columns.map { it.typed() })