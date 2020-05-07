package krangl.typed

import krangl.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

fun DataCol.toDataFrame() = dataFrameOf(this)

fun List<Long?>.toColumn(name: String) = LongCol(name, this)
fun List<Int?>.toColumn(name: String) = IntCol(name, this)
fun List<String?>.toColumn(name: String) = StringCol(name, this)
fun List<Double?>.toColumn(name: String) = DoubleCol(name, this)
fun List<Boolean?>.toColumn(name: String) = BooleanCol(name, this)
fun List<Any?>.toAnyColumn(name: String) = AnyCol(name, this)

fun Array<Long?>.toColumn(name: String) = LongCol(name, this)
fun Array<Int?>.toColumn(name: String) = IntCol(name, this)
fun Array<String?>.toColumn(name: String) = StringCol(name, this)
fun Array<Double?>.toColumn(name: String) = DoubleCol(name, this)
fun Array<Boolean?>.toColumn(name: String) = BooleanCol(name, this)
fun Array<Any?>.toAnyColumn(name: String) = AnyCol(name, this)

val DataCol.valueType: KType
    get() = valueClass.createType(nullable = hasNulls)

val DataCol.valueClass: KClass<*>
    get() = when (this) {
        is LongCol -> Long::class
        is IntCol -> Int::class
        is StringCol -> String::class
        is DoubleCol -> Double::class
        is BooleanCol -> Boolean::class
        is AnyCol -> (values.firstOrNull { it != null }?.javaClass?.kotlin ?: Any::class)
        else -> throw Exception()
    }

fun DataCol.rename(newName: String) =
        when (this) {
            is LongCol -> LongCol(newName, values)
            is IntCol -> IntCol(newName, values)
            is StringCol -> StringCol(newName, values)
            is DoubleCol -> DoubleCol(newName, values)
            is BooleanCol -> BooleanCol(newName, values)
            is AnyCol -> AnyCol(newName, values)
            else -> throw Exception()
        }

fun <T> AnyCol.toList() = values.map { it as T }
fun LongCol.toList() = values.toList()
fun DoubleCol.toList() = values.toList()
fun BooleanCol.toList() = values.toList()
fun StringCol.toList() = values.toList()

class InplaceColumnBuilder(val name: String){
    inline operator fun <reified T> invoke(vararg values: T?) = newColumn(name, values as Array<T?>)
}

fun column(name: String) = InplaceColumnBuilder(name)

inline fun <reified R, T> TypedDataFrame<T>.new(name: String, noinline expression: TypedDataFrameRow<T>.() -> R?) =
        when (R::class) {
            Long::class -> createColumnValues(expression as TypedDataFrameRow<T>.() -> Long?).toColumn(name)
            Int::class -> createColumnValues(expression as TypedDataFrameRow<T>.() -> Int?).toColumn(name)
            String::class -> createColumnValues(expression as TypedDataFrameRow<T>.() -> String?).toColumn(name)
            Double::class -> createColumnValues(expression as TypedDataFrameRow<T>.() -> Double?).toColumn(name)
            Boolean::class -> createColumnValues(expression as TypedDataFrameRow<T>.() -> Boolean?).toColumn(name)
            else -> AnyCol(name, createColumnValues { expression(this) as Any? })
        }

inline fun <reified T> newColumn(name: String, values: Array<T?>) =
        when (T::class) {
            Long::class -> (values as Array<Long?>).toColumn(name)
            Int::class -> (values as Array<Int?>).toColumn(name)
            String::class -> (values as Array<String?>).toColumn(name)
            Double::class -> (values as Array<Double?>).toColumn(name)
            Boolean::class -> (values as Array<Boolean?>).toColumn(name)
            else -> AnyCol(name, (values as Array<Any?>))
        }

inline fun <reified T> newColumn(name: String, values: List<T?>) =
        when (T::class) {
            Long::class -> (values as List<Long?>).toColumn(name)
            Int::class -> (values as List<Int?>).toColumn(name)
            String::class -> (values as List<String?>).toColumn(name)
            Double::class -> (values as List<Double?>).toColumn(name)
            Boolean::class -> (values as List<Boolean?>).toColumn(name)
            else -> values.toAnyColumn(name)
        }

inline fun <reified T, D> TypedDataFrame<D>.createColumnValues(crossinline expression: TypedDataFrameRow<D>.() -> T) =
        rowWise { getRow ->
            Array(nrow) { index ->
                expression(getRow(index)!!)
            }
        }

class ColumnGroup(val columns: List<DataCol>): DataCol(""){
    override fun values(): Array<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val length: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}