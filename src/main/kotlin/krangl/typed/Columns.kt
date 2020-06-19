package krangl.typed

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

interface TypedValues<out T> {
    val values: List<T>
}

interface ColumnSet

interface NamedColumn : ColumnSet {
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

    operator fun get(indices: Iterable<Int>) = slice(indices)
}

typealias DataCol = TypedColData<*>

typealias SrcDataCol = krangl.DataCol

class NamedColumnImpl(override val name: String) : NamedColumn

fun String.toColumnName() = NamedColumnImpl(this)
fun KProperty<*>.toColumnName() = NamedColumnImpl(name)

internal fun KProperty<*>.getColumnName() = this.findAnnotation<ColumnName>()?.name ?: name

inline fun <reified T> KProperty<T>.toColumn() = TypedColDesc<T>(name, T::class)

class TypedColDesc<T>(override val name: String, override val valueClass: KClass<*>) : TypedCol<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
}

inline fun <reified T> TypedColDesc<T>.nullable() = TypedColDesc<T?>(name, valueClass)

data class TypedDataCol<T>(override val values: List<T>, override val nullable: Boolean, override val name: String, override val valueClass: KClass<*>) : TypedColData<T> {

    override fun toString() = values.joinToString()
}

inline fun <reified T> TypedCol<T>.withValues(values: List<T>, hasNulls: Boolean) = TypedDataCol(values, hasNulls, name, valueClass)

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

inline fun <T> DataCol.cast() = this as TypedColData<T>

fun <T> TypedColData<T>.reorder(permutation: List<Int>): TypedColData<T> {
    var nullable = false
    val newValues = (0 until length).map { values[permutation[it]].also { if (it == null) nullable = true } }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun <T> TypedColData<T>.slice(indices: Iterable<Int>): TypedColData<T> {
    var nullable = false
    val newValues = indices.map { values[it].also { if (it == null) nullable = true } }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun DataCol.getRows(mask: BooleanArray): DataCol {
    var nullable = false
    val newValues = values.filterIndexed { index, value -> mask[index].also { if (it && value == null) nullable = true } }
    return TypedDataCol(newValues, nullable, name, valueClass)
}

fun DataCol.rename(newName: String) = TypedDataCol(values, nullable, newName, valueClass)

class InplaceColumnBuilder(val name: String) {
    inline operator fun <reified T> invoke(vararg values: T) = column(name, values.toList())
}

fun column(name: String) = InplaceColumnBuilder(name)

inline fun <T, reified R> TypedDataFrame<T>.new(name: String, noinline expression: RowSelector<T, R>): TypedDataCol<R> {
    var nullable = false
    val values = (0 until nrow).map { get(it).let { expression(it, it) }.also { if (it == null) nullable = true } }
    return column(name, values, nullable)
}

class ColumnGroup(val columns: List<ColumnSet>) : ColumnSet {
    constructor(vararg columns: ColumnSet) : this(columns.toList())
}

class ReversedColumn(val column: NamedColumn) : ColumnSet

class ColumnDelegate<T>(private val valueClass: KClass<*>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = TypedColDesc<T>(property.name, valueClass)
}

inline fun <reified T> column() = ColumnDelegate<T>(T::class)

inline fun <reified T> column(name: String) = TypedColDesc<T>(name, T::class)

inline fun <reified T> column(name: String, values: List<T>) = TypedDataCol(values, values.any { it == null }, name, T::class)

inline fun <reified T> column(name: String, values: List<T>, hasNulls: Boolean) = TypedDataCol(values, hasNulls, name, T::class)