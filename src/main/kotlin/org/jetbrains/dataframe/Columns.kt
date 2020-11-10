package org.jetbrains.dataframe

import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

interface ColumnSet<out C>

interface ColumnDef<out C> : ColumnSet<C> {
    val name: String
    operator fun invoke(row: TypedDataFrameRow<*>) = row[this]
}

interface ConvertedColumn<out C> : ColumnData<C> {
    val source: DataCol
    val data: ColumnData<C>
}

interface RenamedColumnDef<out C> : ColumnDef<C> {
    val source: ColumnDef<C>
}

interface RenamedColumn<out C> : RenamedColumnDef<C>, ColumnData<C> {
    override val source: ColumnData<C>
}

class RenamedColumnDefImpl<C>(override val source: ColumnDef<C>, override val name: String) : RenamedColumnDef<C>, ColumnDef<C> by source

class RenamedColumnImpl<C>(override val source: ColumnData<C>, override val name: String) : RenamedColumn<C>, ColumnData<C> by source {
    override fun distinct() = source.distinct().rename(name)

    override fun get(indices: Iterable<Int>) = source.get(indices).rename(name)
}

class ConvertedColumnImpl<C>(override val source: DataCol, override val data: ColumnData<C>) : ConvertedColumn<C>, ColumnData<C> by data

typealias Column = ColumnDef<*>

interface ColumnData<out T> : ColumnDef<T> {

    val values: Iterable<T>
    val ndistinct: Int
    val type: KType
    val hasNulls: Boolean get() = type.isMarkedNullable
    val size: Int
    operator fun get(index: Int): T

    operator fun get(columnName: String): ColumnData<*>

    operator fun get(indices: Iterable<Int>) = slice(indices)

    fun distinct(): ColumnData<T>

    fun toSet(): Set<T>
}

typealias DataCol = ColumnData<*>

class NamedColumnImpl<C>(override val name: String) : ColumnDef<C>

fun String.toColumn(): ColumnDef<Any?> = NamedColumnImpl(this)
fun <C> KProperty<C>.toColumnName(): ColumnDef<C> = NamedColumnImpl(name)

internal fun KProperty<*>.getColumnName() = this.findAnnotation<ColumnName>()?.name ?: name

fun <T> KProperty<T>.toColumn() = ColumnDefinition<T>(name)

class ColumnDefinition<T>(override val name: String) : ColumnDef<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
}

inline fun <reified T> ColumnDefinition<T>.nullable() = ColumnDefinition<T?>(name)

interface GroupedColumn<T> : ColumnData<TypedDataFrameRow<T>>, DataFrameBase<T> {
    val df : TypedDataFrame<T>
    val dfType: KType
}

class GroupedColumnImpl<T>(override val df: TypedDataFrame<T>, override val name: String, override val dfType: KType) : GroupedColumn<T> {

    override val values: Iterable<TypedDataFrameRow<T>>
        get() = df.rows

    override val ndistinct: Int
        get() = distinct.nrow

    override val type by lazy { (TypedDataFrameRow::class).createType(listOf(KTypeProjection.invariant(dfType))) }

    override fun distinct() = GroupedColumnImpl(distinct, name, dfType)

    private val distinct by lazy { df.distinct() }

    private val set by lazy { distinct.rows.toSet() }

    override fun toSet() = set

    override val size: Int
        get() = df.nrow

    override fun get(index: Int) = df[index]

    override fun get(columnName: String) = df[columnName]
}

class ColumnDataImpl<T>(override val values: List<T>, override val name: String, override val type: KType, set: Set<T>? = null) : ColumnData<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = values.joinToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColumnDataImpl<*>

        if (name != other.name) return false
        if (type != other.type) return false
        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override val ndistinct = toSet().size

    override fun distinct() = ColumnDataImpl(toSet().toList(), name, type, valuesSet)

    override fun get(index: Int) = values[index]

    override fun get(columnName: String) = throw Exception()

    override val size: Int
        get() = values.size
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> getType() = typeOf<T>()

val KType.fullName: String get() = toString()

fun KClass<*>.createStarProjectedType(nullable: Boolean) = this.starProjectedType.let { if (nullable) it.withNullability(true) else it }

inline fun <reified T> ColumnDef<T>.withValues(values: List<T>, hasNulls: Boolean) = column(name, values, hasNulls)

fun <T> ColumnData<T>.withValues(values: List<T>, hasNulls: Boolean) = column<T>(name, values, type.withNullability(hasNulls))

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

internal fun <T> DataCol.typed() = this as ColumnData<T>

internal fun <T> DataCol.grouped() = this as GroupedColumn<T>

inline fun <reified T> DataCol.cast() = column(name, values as List<T>, hasNulls)

fun <T> ColumnData<T>.reorder(permutation: List<Int>): ColumnData<T> {
    var nullable = false
    val newValues = (0 until size).map { get(permutation[it]).also { if (it == null) nullable = true } }
    return withValues(newValues, nullable)
}

fun <T> ColumnData<T>.slice(indices: Iterable<Int>): ColumnData<T> {
    var nullable = false
    val newValues = indices.map { get(it).also { if (it == null) nullable = true } }
    return withValues(newValues, nullable)
}

fun DataCol.getRows(mask: BooleanArray): DataCol {
    var nullable = false
    val newValues = values.filterIndexed { index, value -> mask[index].also { if (it && value == null) nullable = true } }
    return withValues(newValues, nullable)
}

fun <C> ColumnData<C>.rename(newName: String) = if (newName == name) this else RenamedColumnImpl(this, newName)

fun <C> ColumnDef<C>.rename(newName: String) = if (newName == name) this else RenamedColumnDefImpl(this, newName)

internal fun <T> Iterable<T>.asList() = when(this){
    is List<T> -> this
    else -> this.toList()
}

internal fun <C> ColumnData<C>.doRename(newName: String) = if(newName == name) this else ColumnDataImpl(values.asList(), newName, type)

fun <C> ColumnData<C>.ensureUniqueName(nameGenerator: ColumnNameGenerator) = rename(nameGenerator.createUniqueName(name))

class InplaceColumnBuilder(val name: String) {
    inline operator fun <reified T> invoke(vararg values: T) = column(name, values.toList())
}

fun column(name: String) = InplaceColumnBuilder(name)

inline fun <T, reified R> TypedDataFrame<T>.new(name: String, noinline expression: RowSelector<T, R>): ColumnData<R> {
    var nullable = false
    val values = (0 until nrow).map { get(it).let { expression(it, it) }.also { if (it == null) nullable = true } }
    return column(name, values, nullable)
}

class ColumnGroup<C>(val columns: List<ColumnSet<C>>) : ColumnSet<C> {
    constructor(vararg columns: ColumnSet<C>) : this(columns.toList())
}

internal class ReversedColumn<C>(val column: ColumnDef<C>) : ColumnSet<C>

internal class NullsLast<C>(val column: ColumnSet<C>) : ColumnSet<C>

class ColumnDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = ColumnDefinition<T>(property.name)
}

fun DataCol.asGrouped() = this as GroupedColumn<*>

fun DataCol.isGrouped() = this is GroupedColumn<*>

fun <T> column() = ColumnDelegate<T>()

fun <T> columnGroup() = column<TypedDataFrameRow<T>>()

fun <T> column(name: String) = ColumnDefinition<T>(name)

inline fun <reified T> column(name: String, values: List<T>): ColumnData<T> = column(name, values, values.any { it == null })

inline fun <reified T> column(name: String, values: List<T>, hasNulls: Boolean): ColumnData<T> = ColumnDataImpl(values, name, getType<T>().withNullability(hasNulls))

fun <T> column(name: String, values: List<T>, type: KType): ColumnData<T> = ColumnDataImpl(values, name, type)

class ColumnNameGenerator(columnNames: List<String> = emptyList()) {

    private val usedNames = columnNames.toMutableSet()

    fun createUniqueName(preferredName: String): String {
        var name = preferredName
        var k = 2
        while (usedNames.contains(name)) {
            name = "${preferredName}_${k++}"
        }
        usedNames.add(name)
        return name
    }
}

fun TypedDataFrame<*>.nameGenerator() = ColumnNameGenerator(columnNames())

fun GroupedDataFrame<*>.nameGenerator() = ColumnNameGenerator(baseDataFrame.columnNames())

internal fun <T, R> ColumnData<T>.mapValues(transform: (T) -> R) = map(transform)

fun <T, R> ColumnData<T>.map(transform: (T) -> R): ColumnData<R> {
    val collector = ColumnDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}