package krangl.typed

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

interface TypedValues<out T> {
    val values: List<T>
    val ndistinct: Int
    val nullable: Boolean
    val size get() = values.size
    operator fun get(index: Int) = values[index]
}

interface ColumnSet

interface NamedColumn : ColumnSet {
    val name: String
}

interface TypedCol<out T> : NamedColumn {
    val valueClass: KClass<*>
    operator fun invoke(row: TypedDataFrameRow<*>) = row[this]
}

interface TypedColumnPair<out A>: TypedCol<A> {
    val firstColumn: TypedCol<A>
    val secondColumn: DataCol?
    var groupingColumns: ColumnSet?
}

class TypedColumnPairImpl<A>(override val firstColumn: TypedCol<A>, override val secondColumn: DataCol?, override var groupingColumns: ColumnSet? = null): TypedCol<A> by firstColumn, TypedColumnPair<A>

typealias Column = TypedCol<*>

interface TypedColData<out T> : TypedCol<T>, TypedValues<T> {
    val type get() = valueClass.createType(nullable = nullable)

    operator fun get(indices: Iterable<Int>) = slice(indices)

    fun distinct(): TypedColData<T>

    fun toSet(): Set<T>
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

class TypedDataCol<T>(override val values: List<T>, override val nullable: Boolean, override val name: String, override val valueClass: KClass<*>) : TypedColData<T> {

    private var valuesSet: Set<T>? = null

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = values.joinToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypedDataCol<*>

        if (values != other.values) return false
        if (nullable != other.nullable) return false
        if (name != other.name) return false
        if (valueClass != other.valueClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + nullable.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + valueClass.hashCode()
        return result
    }

    override val ndistinct = toSet().size

    override fun distinct()= TypedDataCol(toSet().toList(), nullable, name, valueClass)
}

inline fun <reified T> TypedCol<T>.withValues(values: List<T>, hasNulls: Boolean) = TypedDataCol(values, hasNulls, name, valueClass)

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

inline fun <T> DataCol.cast() = this as TypedColData<T>

fun <T> TypedColData<T>.reorder(permutation: List<Int>): TypedColData<T> {
    var nullable = false
    val newValues = (0 until size).map { values[permutation[it]].also { if (it == null) nullable = true } }
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

fun DataCol.rename(newName: String) = if(newName == name) this else TypedDataCol(values, nullable, newName, valueClass)

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

fun column(name: String, values: List<Any?>, hasNulls: Boolean, clazz: KClass<*>) = TypedDataCol(values, hasNulls, name, clazz)

fun <T> TypedValues<T>.contains(value: T) = (this as TypedDataCol<T>).contains(value)

class ColumnNameGenerator(private val columnNames: List<String>){

    private val usedNames = columnNames.toMutableSet()

    fun createUniqueName(preferredName: String): String{
        var name = preferredName
        var k = 2
        while (usedNames.contains(name)) {
            name = "${preferredName}_${k++}"
        }
        usedNames.add(name)
        return name
    }
}

inline fun TypedDataFrame<*>.nameGenerator() = ColumnNameGenerator(columnNames())