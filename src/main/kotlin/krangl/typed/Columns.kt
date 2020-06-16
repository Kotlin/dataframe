package krangl.typed

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType

interface TypedValues<out T>{
    val values: List<T>
}

interface ColumnSet

interface NamedColumn: ColumnSet {
    val name: String
}

interface TypedCol<out T> : NamedColumn {
    val valueClass: KClass<*>
    operator fun invoke(row: TypedDataFrameRow<*>) = row[this]
}

typealias Column = TypedCol<*>

interface TypedColData<out T> : TypedCol<T>, TypedValues<T> {
    val nullable: Boolean
    val length get() = values.size
    operator fun get(index: Int) = values[index]
    val type get() = valueClass.createType(nullable = nullable)
}

typealias DataCol = TypedColData<*>

typealias SrcDataCol = krangl.DataCol

class NamedColumnImpl(override val name: String): NamedColumn

fun String.asColumnName() = NamedColumnImpl(this)

class TypedColDesc<T>(override val name: String, override val valueClass: KClass<*>) : TypedCol<T>{
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this

}

inline fun <reified T> TypedColDesc<T>.nullable() = TypedColDesc<T?>(name, valueClass)

class TypedDataCol<T>(override val values: List<T>, override val nullable: Boolean, override val name: String, override val valueClass: KClass<*>) : TypedColData<T> {

    override fun toString() = values.joinToString()
}

inline fun <reified T> createColumn(name: String, values: List<T>, hasNulls: Boolean)
    = TypedDataCol(values, hasNulls, name, T::class)

inline fun <reified T> createColumn(name: String, values: List<T>)
        = TypedDataCol(values, values.any {it == null}, name, T::class)

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

inline fun <T> DataCol.cast() = this as TypedColData<T>

fun DataCol.reorder(permutation: IntArray): DataCol {
    var nullable = false
    val newValues = (0 until length).map { values[permutation[it]].also { if(it == null) nullable = true } }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun DataCol.getRows(indices: IntArray): DataCol {
    var nullable = false
    val newValues = indices.map { values[it].also { if(it == null) nullable = true } }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun DataCol.getRows(mask: BooleanArray): DataCol {
    var nullable = false
    val newValues = values.filterIndexed {index, value -> mask[index].also { if(it && value == null) nullable = true }  }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun DataCol.rename(newName: String) = TypedDataCol(values, nullable, newName, valueClass)

class InplaceColumnBuilder(val name: String) {
    inline operator fun <reified T> invoke(vararg values: T) = newColumn(name, values.toList())
}

fun column(name: String) = InplaceColumnBuilder(name)

inline fun <reified R, T> TypedDataFrame<T>.new(name: String, noinline expression: TypedDataFrameRow<T>.() -> R): TypedDataCol<R> {
    var nullable = false
    rowWise { getRow -> (0 until nrow).map { expression(getRow(it)!!).also {if(it == null) nullable = true} } }
    return createColumn(name, createColumnValues(expression), nullable)
}

inline fun <reified T> newColumn(name: String, values: List<T>) = createColumn(name, values)

fun <T, D> TypedDataFrame<D>.createColumnValues(expression: TypedDataFrameRow<D>.() -> T) =
        rowWise { getRow -> (0 until nrow).map {expression(getRow(it)!!)} }

class ColumnGroup(val columns: List<ColumnSet>) : ColumnSet

class ReversedColumn(val column: NamedColumn) : ColumnSet

class ColumnDelegate<T>(private val valueClass: KClass<*>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = TypedColDesc<T>(property.name, valueClass)
}

inline fun <reified T> column() = ColumnDelegate<T>(T::class)

inline fun <reified T> column(name: String) = TypedColDesc<T>(name, T::class)