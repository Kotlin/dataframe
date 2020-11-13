package org.jetbrains.dataframe

import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

interface ColumnSet<out C>

interface SingleColumn<out C> : ColumnSet<C>

interface ColumnDef<out C> : SingleColumn<C> {
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

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType): ColumnData<T> = ColumnDataImpl(values, name, type)

        fun <T> createGroup(name: String, df: TypedDataFrame<T>): ColumnData<TypedDataFrameRow<T>> = GroupedColumnImpl(df, name)

        fun <T> createTable(name: String, df: TypedDataFrame<T>, startIndices: List<Int>): ColumnData<TypedDataFrame<T>> = TableColumnImpl(name, df, startIndices)
    }

    val values: Iterable<T>
    val ndistinct: Int
    val type: KType
    val hasNulls: Boolean get() = type.isMarkedNullable
    val size: Int
    operator fun get(index: Int): T

    operator fun get(range: IntRange): List<T>

    operator fun get(columnName: String): ColumnData<*>

    operator fun get(indices: Iterable<Int>) = getRows(indices)

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

interface GroupedColumnBase<T>: SingleColumn<TypedDataFrameRow<T>>, DataFrameBase<T>

interface NestedColumn<T> {
    val df : TypedDataFrame<T>
}

interface GroupedColumn<T> : ColumnData<TypedDataFrameRow<T>>, NestedColumn<T>, GroupedColumnBase<T>

interface TableColumn<T> : ColumnData<TypedDataFrame<T>>, NestedColumn<T>

internal enum class ColumnKind {
    Default,
    Group,
    Table
}

internal fun <T> Iterable<T>.equalsByElement(other: Iterable<T>): Boolean {
    val iterator1 = iterator()
    val iterator2 = other.iterator()
    while (iterator1.hasNext() && iterator2.hasNext()) {
        if (iterator1.next() != iterator2.next()) return false
    }
    if (iterator1.hasNext() || iterator2.hasNext()) return false
    return true
}

internal fun <T> Iterable<T>.rollingHash(): Int {
    val i = iterator()
    var hash = 0
    while (i.hasNext())
        hash = 31 * hash + (i.next()?.hashCode() ?: 5)
    return hash
}

internal fun <T> ColumnData<T>.checkEquals(other: Any?): Boolean {
    if (this === other) return true

    if(!(other is ColumnData<*>)) return false

    if (name != other.name) return false
    if (type != other.type) return false
    return values.equalsByElement(other.values)
}

internal fun <T> ColumnData<T>.getHashCode(): Int {
    var result = values.rollingHash()
    result = 31 * result + name.hashCode()
    result = 31 * result + type.hashCode()
    return result
}

interface ColumnWithParent<C> : SingleColumn<C> {
    val parent: ColumnData<*>
}

class ColumnDataWithParent<T>(override val parent: ColumnData<*>, val source: ColumnData<T>) : ColumnWithParent<T>, ColumnData<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()
}

class GroupedColumnWithParent<T>(override val parent: ColumnData<*>, val source: GroupedColumn<T>) : ColumnWithParent<TypedDataFrameRow<T>>, GroupedColumn<T> by source {
    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<TypedDataFrameRow<R>>) = df[column].addParent(this) as GroupedColumn<R>

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()
}

internal fun <T> ColumnData<T>.addParent(parent: ColumnData<*>): ColumnData<T> = when(this) {
    is GroupedColumn<*> -> GroupedColumnWithParent(parent, this as GroupedColumn<Any>) as ColumnData<T>
    else -> ColumnDataWithParent(parent, this)
}

internal interface ColumnDataInternal<T> : ColumnData<T> {
    fun rename(newName: String): ColumnData<T>
}

internal class TableColumnImpl<T> private constructor(override val df: TypedDataFrame<T>, name: String, values: List<TypedDataFrame<T>>)
    : ColumnDataImpl<TypedDataFrame<T>>(values, name, createType<TypedDataFrame<*>>()), TableColumn<T> {

    constructor(name: String, df: TypedDataFrame<T>, startIndices: List<Int>) : this(df, name, df.splitByIndices(startIndices))

    override fun rename(newName: String) = TableColumnImpl(df, newName, values)
}

internal class GroupedColumnImpl<T>(override val df: TypedDataFrame<T>, override val name: String) : GroupedColumn<T>, ColumnDataInternal<TypedDataFrameRow<T>> {

    override val values: Iterable<TypedDataFrameRow<T>>
        get() = df.rows

    override val ndistinct: Int
        get() = distinct.nrow

    override val ncol: Int
        get() = df.ncol

    override val type by lazy { createType<TypedDataFrameRow<*>>() }

    override fun distinct() = GroupedColumnImpl(distinct, name)

    private val distinct by lazy { df.distinct() }

    private val set by lazy { distinct.rows.toSet() }

    override fun toSet() = set

    override val size: Int
        get() = df.nrow

    override fun get(index: Int) = df[index]
    override fun get(range: IntRange) = df[range].rows.asList()

    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<TypedDataFrameRow<R>>) = df[column].addParent(this) as GroupedColumn<R>

    override fun rename(newName: String) = GroupedColumnImpl(df, newName)
}

internal open class ColumnDataImpl<T>(override val values: List<T>, override val name: String, override val type: KType, set: Set<T>? = null) : ColumnDataInternal<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = values.joinToString()

    override val ndistinct = toSet().size

    override fun distinct() = ColumnDataImpl(toSet().toList(), name, type, valuesSet)

    override fun get(index: Int) = values[index]

    override fun get(range: IntRange) = values.slice(range)

    override fun get(columnName: String) = throw Exception()

    override val size: Int
        get() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun rename(newName: String) = ColumnDataImpl(values, newName, type, valuesSet)
}

internal class ColumnDataPart<T>(val source: ColumnData<T>, val part: IntRange) : ColumnData<T>  {

    override val values: Iterable<T> = Iterable {
        object : Iterator<T> {
            var curIndex = part.start

            override fun hasNext(): Boolean = curIndex <= part.endInclusive

            override fun next() = source[curIndex++]
        }
    }

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override val hasNulls = values.any { it == null }

    override val type : KType
            get() = source.type.withNullability(hasNulls)

    var valuesSet: Set<T>? = null
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    override val ndistinct = toSet().size

    override fun distinct() = ColumnDataImpl(toSet().toList(), name, type, valuesSet)

    fun contains(value: T) = toSet().contains(value)

    override val size = part.endInclusive - part.start + 1

    override fun get(index: Int) = source[part.start + index]

    override fun get(range: IntRange) = source[IntRange(range.start + part.start, range.endInclusive + part.start)]

    override val name: String
        get() = source.name

    override fun get(columnName: String) = source[columnName].getRows(part)
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> getType() = typeOf<T>()

val KType.fullName: String get() = toString()

fun KClass<*>.createStarProjectedType(nullable: Boolean) = this.starProjectedType.let { if (nullable) it.withNullability(true) else it }

inline fun <reified T> ColumnDef<T>.withValues(values: List<T>, hasNulls: Boolean) = column(name, values, hasNulls)

fun <T> ColumnData<T>.withValues(values: List<T>, hasNulls: Boolean) = column<T>(name, values, type.withNullability(hasNulls))

fun <T> ColumnData<T>.withValues(values: List<T>) = withValues(values, values.any { it == null })

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

internal fun <T> DataCol.typed() = this as ColumnData<T>

internal fun <T> DataCol.grouped() = this as GroupedColumnBase<T>

internal fun <T> DataFrameBase<T>.asGroup() = this as GroupedColumn<T>

inline fun <reified T> DataCol.cast() = column(name, values as List<T>, hasNulls)

fun <T> ColumnData<T>.reorder(permutation: List<Int>): ColumnData<T> = when(this) {
    is GroupedColumn<*> -> withDf(df.reorder(permutation)) as ColumnData<T>
    else -> {
        var nullable = false
        val newValues = (0 until size).map { get(permutation[it]).also { if (it == null) nullable = true } }
        withValues(newValues, nullable)
    }
}

internal fun <T> GroupedColumn<*>.withDf(newDf: TypedDataFrame<T>) = ColumnData.createGroup(name, newDf)

fun <T> ColumnData<T>.getRows(range: IntRange): ColumnData<T> =
        when(this) {
            is GroupedColumn<*> -> withDf(df[range]) as ColumnData<T>
            is ColumnDataPart<T> -> ColumnDataPart(source, IntRange(part.first + range.first, part.first + range.last))
            else -> ColumnDataPart(this, range)
        }

fun <T> ColumnData<T>.getRows(indices: Iterable<Int>): ColumnData<T> =
    when(this) {
        is GroupedColumn<*> -> withDf(df[indices]) as ColumnData<T>
        else -> {
            var nullable = false
            val newValues = indices.map { get(it).also { if (it == null) nullable = true } }
            withValues(newValues, nullable)
        }
    }

fun <T> ColumnData<T>.getRows(mask: BooleanArray): ColumnData<T> = when(this) {
    is GroupedColumn<*> -> withDf(df.getRows(mask)) as ColumnData<T>
    else -> {
        var nullable = false
        val newValues = values.filterIndexed { index, value -> mask[index].also { if (it && value == null) nullable = true } }
        withValues(newValues, nullable)
    }
}

fun <C> ColumnData<C>.rename(newName: String) = if (newName == name) this else RenamedColumnImpl(this, newName)

fun <C> ColumnDef<C>.rename(newName: String) = if (newName == name) this else RenamedColumnDefImpl(this, newName)

fun <C> ColumnData<C>.doRename(newName: String) = if (newName == name) this else (this as ColumnDataInternal<C>).rename(newName)

internal fun <T> Iterable<T>.asList() = when(this){
    is List<T> -> this
    else -> this.toList()
}

fun <C> ColumnData<C>.ensureUniqueName(nameGenerator: ColumnNameGenerator) = rename(nameGenerator.addUnique(name))

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

fun <T> columnList() = column<List<T>>()

fun <T> columnGroup(name: String) = column<TypedDataFrameRow<T>>(name)

fun <T> columnList(name: String) = column<List<T>>(name)

fun <T> column(name: String) = ColumnDefinition<T>(name)

inline fun <reified T> column(name: String, values: List<T>): ColumnData<T> = column(name, values, values.any { it == null })

inline fun <reified T> column(name: String, values: List<T>, hasNulls: Boolean): ColumnData<T> = ColumnData.create(name, values, getType<T>().withNullability(hasNulls))

fun <T> column(name: String, values: List<T>, type: KType): ColumnData<T> = ColumnDataImpl(values, name, type)

class ColumnNameGenerator(columnNames: List<String> = emptyList()) {

    private val usedNames = columnNames.toMutableSet()

    private val colNames = columnNames.toMutableList()

    fun addUnique(preferredName: String): String {
        var name = preferredName
        var k = 2
        while (usedNames.contains(name)) {
            name = "${preferredName}_${k++}"
        }
        usedNames.add(name)
        colNames.add(name)
        return name
    }

    fun addIfAbsent(name: String) {
        if(!usedNames.contains(name))
        {
            usedNames.add(name)
            colNames.add(name)
        }
    }

    val names : List<String>
        get() = colNames

    fun contains(name: String) = usedNames.contains(name)
}

fun TypedDataFrame<*>.nameGenerator() = ColumnNameGenerator(columnNames())

fun GroupedDataFrame<*>.nameGenerator() = ColumnNameGenerator(baseDataFrame.columnNames())

internal fun <T, R> ColumnData<T>.mapValues(transform: (T) -> R) = map(transform)

fun <T, R> ColumnData<T>.map(transform: (T) -> R): ColumnData<R> {
    val collector = ColumnDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}