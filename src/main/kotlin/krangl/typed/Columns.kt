package krangl.typed

import krangl.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType

interface TypedValues<out T>

interface ColumnSet

interface NamedColumn: ColumnSet {
    val name: String
}

interface TypedCol<T: Any> : NamedColumn {
    val valueClass: KClass<T>
}

typealias Column = TypedCol<*>

interface TypedColData<T: Any> : TypedCol<T>, TypedValues<T> {
    val hasNulls: Boolean
    val length: Int
    val values: Array<T>
    val type get() = valueClass.createType(nullable = hasNulls)
}

typealias DataCol = TypedColData<*>

typealias SrcDataCol = krangl.DataCol

class NamedColumnImpl(override val name: String): NamedColumn

fun String.asColumnName() = NamedColumnImpl(this)

class TypedColDesc<T: Any>(override val name: String, override val valueClass: KClass<T>) : TypedCol<T>{
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
}

class TypedDataCol<T: Any>(val col: SrcDataCol, override val valueClass: KClass<T>) : TypedColData<T> {
    override val name: String
        get() = col.name

    override val hasNulls = col.hasNulls

    override val length get() = col.length

    override val values: Array<T>
        get() = col.values() as Array<T>
}

fun DataCol.toSrc() = when (this) {
    is TypedDataCol<*> -> col
    else -> throw Exception()
}

fun SrcDataCol.typed() = when (this) {
    is IntCol -> TypedDataCol(this, Int::class)
    is LongCol -> TypedDataCol(this, Long::class)
    is StringCol -> TypedDataCol(this, String::class)
    is BooleanCol -> TypedDataCol(this, Boolean::class)
    is AnyCol -> TypedDataCol(this, Any::class)
    else -> TypedDataCol(this, Any::class)
}

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

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

val SrcDataCol.valueClass: KClass<*>
    get() = when (this) {
        is LongCol -> Long::class
        is IntCol -> Int::class
        is StringCol -> String::class
        is DoubleCol -> Double::class
        is BooleanCol -> Boolean::class
        is AnyCol -> (values.firstOrNull { it != null }?.javaClass?.kotlin ?: Any::class)
        else -> throw Exception()
    }

val DataCol.valueClass get() = toSrc().valueClass

fun <T: Any> TypedDataCol<*>.cast() = this as TypedDataCol<T>

fun SrcDataCol.rename(newName: String) =
        when (this) {
            is LongCol -> LongCol(newName, values)
            is IntCol -> IntCol(newName, values)
            is StringCol -> StringCol(newName, values)
            is DoubleCol -> DoubleCol(newName, values)
            is BooleanCol -> BooleanCol(newName, values)
            is AnyCol -> AnyCol(newName, values)
            else -> throw Exception()
        }

fun DataCol.reorder(permutation: IntArray) =
        when (val it = toSrc()) {
            is DoubleCol -> DoubleCol(name, Array(length, { index -> it.values[permutation[index]] }))
            is IntCol -> IntCol(name, Array(length, { index -> it.values[permutation[index]] }))
            is LongCol -> LongCol(name, Array(length, { index -> it.values[permutation[index]] }))
            is BooleanCol -> BooleanCol(name, Array(length, { index -> it.values[permutation[index]] }))
            is StringCol -> StringCol(name, Array(length, { index -> it.values[permutation[index]] }))
            is AnyCol -> AnyCol(name, Array(length, { index -> it.values[permutation[index]] }))
            else -> throw UnsupportedOperationException()
        }.typed()

fun DataCol.getRows(indices: IntArray) = when (val col = toSrc()) {
    is DoubleCol -> DoubleCol(name, indices.map { col.values[it] }.toTypedArray())
    is IntCol -> IntCol(name, indices.map { col.values[it] }.toTypedArray())
    is LongCol -> LongCol(name, indices.map { col.values[it] }.toTypedArray())
    is StringCol -> StringCol(name, indices.map { col.values[it] }.toTypedArray())
    is BooleanCol -> BooleanCol(name, indices.map { col.values[it] }.toTypedArray())
    is AnyCol -> AnyCol(name, indices.map { col.values[it] }.toTypedArray())
    else -> throw UnsupportedOperationException()
}.typed()

fun DataCol.getRows(mask: BooleanArray) = when (val col = toSrc()) {
    is DoubleCol -> DoubleCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    is IntCol -> IntCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    is LongCol -> LongCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    is StringCol -> StringCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    is BooleanCol -> BooleanCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    is AnyCol -> AnyCol(name, col.values.filterIndexed { index, _ -> mask[index] }.toTypedArray())
    else -> throw UnsupportedOperationException()
}.typed()

fun DataCol.rename(newName: String) = toSrc().rename(newName).typed()

fun <T> AnyCol.toList() = values.map { it as T }
fun LongCol.toList() = values.toList()
fun DoubleCol.toList() = values.toList()
fun BooleanCol.toList() = values.toList()
fun StringCol.toList() = values.toList()

class InplaceColumnBuilder(val name: String) {
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
        }.typed()

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
        }.typed()

inline fun <reified T, D> TypedDataFrame<D>.createColumnValues(crossinline expression: TypedDataFrameRow<D>.() -> T) =
        rowWise { getRow ->
            Array(nrow) { index ->
                expression(getRow(index)!!)
            }
        }

class ColumnGroup(val columns: List<ColumnSet>) : ColumnSet

class ReversedColumn(val column: NamedColumn) : ColumnSet

class ColumnDelegate<T: Any>(private val valueClass: KClass<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = TypedColDesc<T>(property.name, valueClass)
}

inline fun <reified T: Any> column() = ColumnDelegate(T::class)

inline fun <reified T: Any> column(name: String) = TypedColDesc(name, T::class)