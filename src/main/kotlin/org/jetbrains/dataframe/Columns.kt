package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.ColumnDataCollector
import org.jetbrains.dataframe.impl.createDataCollector
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

interface ColumnSet<out C>

interface SingleColumn<out C> : ColumnSet<C>

interface ColumnDef<out C> : SingleColumn<C> {
    val name: String
    operator fun invoke(row: DataFrameRow<*>) = row[this]
}

interface ConvertedColumn<out C> : ColumnData<C> {
    val source: DataCol
    val data: ColumnData<C>
}

interface RenamedColumnDef<out C> : ColumnDef<C> {
    val source: ColumnDef<C>
}

interface ColumnsBySelector<out C> : ColumnSet<C> {
    val source: GroupedColumnDef
    fun selectColumns(resolved: GroupedColumn<*>): List<ColumnData<C>>
}

class ColumnsBySelectorImpl<C>(override val source: GroupedColumnDef, val selector: (GroupedColumn<*>)->List<ColumnData<C>>) : ColumnsBySelector<C>{
    override fun selectColumns(group: GroupedColumn<*>) = selector(group)
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

typealias GroupedColumnDef = ColumnDef<DataFrameRow<*>>

interface ColumnData<out T> : ColumnDef<T> {

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ColumnData<T> = ColumnDataImpl(values, name, type, defaultValue)

        fun <T> createGroup(name: String, df: DataFrame<T>): GroupedColumn<T> = GroupedColumnImpl(df, name)

        fun <T> createTable(name: String, df: DataFrame<T>, startIndices: List<Int>): TableColumn<T> = TableColumnImpl(name, df, startIndices)

        fun <T> createTable(name: String, groups: List<DataFrame<T>>, df: DataFrame<T>? = null): TableColumn<T> = TableColumnImpl(df ?: groups.getBaseSchema(), name, groups)

        fun empty() = create("", emptyList<Unit>(), getType<Unit>()) as DataCol
    }

    val values: Iterable<T>
    val ndistinct: Int
    val type: KType
    val hasNulls: Boolean get() = type.isMarkedNullable
    val size: Int

    fun kind(): ColumnKind

    operator fun get(index: Int): T

    operator fun get(row: DataFrameRow<*>) = get(row.index)

    fun toList() = values.asList()

    fun asIterable() = values

    fun defaultValue() : T?

    operator fun get(range: IntRange): ColumnData<T>

    operator fun get(columnName: String): ColumnData<*>

    operator fun get(indices: Iterable<Int>): ColumnData<T>

    operator fun get(mask: BooleanArray): ColumnData<T>

    fun distinct(): ColumnData<T>

    fun toSet(): Set<T>
}

typealias DataCol = ColumnData<*>

class NamedColumnImpl<C>(override val name: String) : ColumnDef<C>

fun String.toColumnDef(): ColumnDef<Any?> = NamedColumnImpl(this)
fun <C> KProperty<C>.toColumnName(): ColumnDef<C> = NamedColumnImpl(name)

internal fun KProperty<*>.getColumnName() = this.findAnnotation<ColumnName>()?.name ?: name

fun <T> KProperty<T>.toColumnDef() = ColumnDefinition<T>(name)

class ColumnDefinition<T>(override val name: String) : ColumnDef<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
}

inline fun <reified T> ColumnDefinition<T>.nullable() = ColumnDefinition<T?>(name)

interface GroupedColumnBase<T>: SingleColumn<DataFrameRow<T>>, DataFrameBase<T> {
    fun asDataFrame(): DataFrame<T>
}

interface NestedColumn<out T> {
    val df : DataFrame<T>
}

interface GroupedColumn<T> : ColumnData<DataFrameRow<T>>, NestedColumn<T>, GroupedColumnBase<T>

interface TableColumn<out T> : ColumnData<DataFrame<T>>, NestedColumn<T>

typealias ColumnPath = List<String>

class ColumnWithPath<T> internal constructor(internal val source: ColumnData<T>, val path: ColumnPath): ColumnData<T> by source

enum class ColumnKind {
    Data,
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
    val parent: GroupedColumn<*>
}

class ColumnDataWithParent<T>(override val parent: GroupedColumn<*>, val source: ColumnData<T>) : ColumnWithParent<T>, ColumnData<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()
}

class GroupedColumnWithParent<T>(override val parent: GroupedColumn<*>, val source: GroupedColumn<T>) : ColumnWithParent<DataFrameRow<T>>, GroupedColumn<T> by source {
    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<DataFrameRow<R>>) = df[column].addParent(this) as GroupedColumn<R>
    override fun columns() = df.columns().map { it.addParent(this) }
    override fun getColumn(columnIndex: Int) = df.getColumn(columnIndex).addParent(this)

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()
}

internal fun <T> ColumnData<T>.addParent(parent: GroupedColumn<*>): ColumnData<T> = when(this) {
    is GroupedColumn<*> -> GroupedColumnWithParent(parent, this as GroupedColumn<Any>) as ColumnData<T>
    else -> ColumnDataWithParent(parent, this)
}

internal interface ColumnDataInternal<T> : ColumnData<T> {
    fun rename(newName: String): ColumnData<T>
}

internal class TableColumnImpl<T> constructor(override val df: DataFrame<T>, name: String, values: List<DataFrame<T>>)
    : ColumnDataImpl<DataFrame<T>>(values, name, createType<DataFrame<*>>()), TableColumn<T> {

    constructor(name: String, df: DataFrame<T>, startIndices: List<Int>) : this(df, name, df.splitByIndices(startIndices))

    override fun rename(newName: String) = TableColumnImpl(df, newName, values)

    override fun get(indices: Iterable<Int>): ColumnData<DataFrame<T>> {

        return ColumnData.createTable(name, indices.map(this::get))
    }

    override fun get(mask: BooleanArray): ColumnData<DataFrame<T>> {

        return ColumnData.createTable(name, values.filterIndexed {index, _ -> mask[index] })
    }

    override fun kind() = ColumnKind.Table
}

internal class GroupedColumnImpl<T>(override val df: DataFrame<T>, override val name: String) : GroupedColumn<T>, ColumnDataInternal<DataFrameRow<T>> {

    override val values: Iterable<DataFrameRow<T>>
        get() = df.rows

    override val ndistinct: Int
        get() = distinct.nrow

    override val ncol: Int
        get() = df.ncol

    override val type by lazy { createType<DataFrameRow<*>>() }

    override fun distinct() = GroupedColumnImpl(distinct, name)

    private val distinct by lazy { df.distinct() }

    private val set by lazy { distinct.rows.toSet() }

    override fun toSet() = set

    override val size: Int
        get() = df.nrow

    override fun get(index: Int) = df[index]

    override fun get(range: IntRange) = GroupedColumnImpl(df[range], name)

    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<DataFrameRow<R>>) = df[column].addParent(this) as GroupedColumn<R>
    override fun <R> get(column: ColumnDef<DataFrame<R>>) = df[column].addParent(this) as TableColumn<R>

    override fun rename(newName: String) = GroupedColumnImpl(df, newName)

    override fun asDataFrame() = df

    override fun getColumn(columnIndex: Int) = df.getColumn(columnIndex).addParent(this)

    override fun columns() = df.columns().map { it.addParent(this) }

    override fun defaultValue() = null

    override fun kind() = ColumnKind.Group

    override fun get(indices: Iterable<Int>) = withDf(df[indices])

    override fun get(mask: BooleanArray) = withDf(df.getRows(mask))

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        val g = other as? GroupedColumn<T> ?: return false
        return name == g.name && df == other.df
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + df.hashCode()
    }
}

internal open class ColumnDataImpl<T>(override val values: List<T>, override val name: String, override val type: KType, val defaultValue: T? = null, set: Set<T>? = null) : ColumnDataInternal<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = "$name: $type"

    override val ndistinct = toSet().size

    override fun distinct() = ColumnDataImpl(toSet().toList(), name, type, defaultValue, valuesSet)

    override fun get(index: Int) = values[index]

    override fun get(range: IntRange) = ColumnDataPart(this, range)

    override fun get(columnName: String) = throw Exception()

    override val size: Int
        get() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun rename(newName: String) = ColumnDataImpl(values, newName, type, defaultValue, valuesSet)

    override fun defaultValue() = defaultValue

    override fun kind() = ColumnKind.Data

    override fun get(indices: Iterable<Int>): ColumnData<T> {
        var nullable = false
        val newValues = indices.map { get(it).also { if (it == null) nullable = true } }
        return withValues(newValues, nullable)
    }

    override fun get(mask: BooleanArray): ColumnData<T> {
        var nullable = false
        val newValues = values.filterIndexed { index, value -> mask[index].also { if (it && value == null) nullable = true } }
        return withValues(newValues, nullable)
    }
}

internal class ColumnDataPart<T>(val source: ColumnData<T>, val part: IntRange) : ColumnData<T>, ColumnDataInternal<T>, NestedColumn<T> {

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

    override val type: KType
        get() = source.type.withNullability(hasNulls)

    var valuesSet: Set<T>? = null
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    override val ndistinct = toSet().size

    override fun distinct() = ColumnDataImpl(toSet().toList(), name, type, defaultValue(), valuesSet)

    fun contains(value: T) = toSet().contains(value)

    override val size = part.endInclusive - part.first + 1

    override fun get(index: Int) = source[part.first + index]

    override fun get(range: IntRange) = source[IntRange(range.first + part.first, range.endInclusive + part.first)]

    override val name: String
        get() = source.name

    override fun get(columnName: String) = source[columnName][part]

    override fun rename(newName: String) = ColumnDataPart(source.rename(newName), part)

    override fun defaultValue() = source.defaultValue()

    override fun kind() = source.kind()

    override val df: DataFrame<T>
        get() = (source as? NestedColumn<T>)?.df ?: throw UnsupportedOperationException()

    override fun get(indices: Iterable<Int>) = source[indices.map { part.first + it }]

    override fun get(mask: BooleanArray): ColumnData<T> {
        val newMask = BooleanArray(mask.size + part.first) { if(it < part.first) false else mask[part.first + it] }
        return source[newMask]
    }
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> getType() = typeOf<T>()

val KType.fullName: String get() = toString()

fun KClass<*>.createStarProjectedType(nullable: Boolean) = this.starProjectedType.let { if (nullable) it.withNullability(true) else it }

inline fun <reified T> ColumnDef<T>.withValues(values: List<T>, hasNulls: Boolean) = column(name, values, hasNulls)

// TODO: implement correct base schema computation
internal fun <T> Iterable<DataFrame<T>>.getBaseSchema(): DataFrame<T> {
    return first()
}

fun <T> ColumnData<T>.withValues(values: List<T>, hasNulls: Boolean) = when(this){
    is TableColumn<*> -> {
        val dfs = (values as List<DataFrame<*>>)
        ColumnData.createTable(name, dfs, dfs.getBaseSchema()) as ColumnData<T>
    }
    else -> column(name, values, type.withNullability(hasNulls))
}

fun <T> ColumnData<T>.withValues(values: List<T>) = withValues(values, values.any { it == null })

fun DataCol.toDataFrame() = dataFrameOf(listOf(this))

internal fun <T> DataCol.typed() = this as ColumnData<T>

internal fun <T> TableColumn<*>.typed() = this as TableColumn<T>

internal fun <T> GroupedColumn<*>.typed() = this as GroupedColumn<T>

internal fun <T> DataCol.grouped() = this as GroupedColumnBase<T>

internal fun <T> DataFrameBase<T>.asGroup() = this as GroupedColumn<T>

inline fun <reified T> DataCol.cast(): ColumnData<T> = ColumnData.create(name, toList() as List<T>, getType<T>().withNullability(hasNulls))

internal fun <T> GroupedColumn<*>.withDf(newDf: DataFrame<T>) = ColumnData.createGroup(name, newDf)

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

inline fun <T, reified R> DataFrame<T>.new(name: String, noinline expression: RowSelector<T, R>): ColumnData<R> {
    var nullable = false
    val values = (0 until nrow).map { get(it).let { expression(it, it) }.also { if (it == null) nullable = true } }
    return column(name, values, nullable)
}

internal fun Array<out String>.toColumnSet(): ColumnSet<Any?> = map { it.toColumnDef() }.toColumnSet()
fun <C> Iterable<ColumnSet<C>>.toColumnSet() = ColumnGroup(asList())
internal fun <C> Array<out KProperty<C>>.toColumnSet() = map {it.toColumnDef()}.toColumnSet()
internal fun <T> Array<out ColumnDef<T>>.toColumnSet() = toList().toColumnSet()

class ColumnGroup<C>(val columns: List<ColumnSet<C>>) : ColumnSet<C> {
    constructor(vararg columns: ColumnSet<C>) : this(columns.toList())
}

internal class AllExceptColumn(val columns: ColumnSet<*>): ColumnSet<Any?>

internal class ReversedColumn<C>(val column: ColumnDef<C>) : ColumnSet<C>

internal class NullsLast<C>(val columns: ColumnSet<C>) : ColumnSet<C>

class ColumnDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = ColumnDefinition<T>(property.name)
}

fun DataCol.asFrame(): DataFrame<*> = when(this){
    is GroupedColumn<*> -> df
    is ColumnWithPath<*> -> source.asFrame()
    else -> throw Exception()
}

internal fun <C> ColumnData<C>.asGrouped(): GroupedColumn<C> = when(this){
    is GroupedColumn<*> -> this as GroupedColumn<C>
    is ColumnWithPath<C> -> source.asGrouped()
    else -> throw Exception()
}

internal fun <C> ColumnData<*>.asTable(): TableColumn<C> = when(this){
    is TableColumn<*> -> this as TableColumn<C>
    is ColumnWithPath<*> -> source.asTable()
    else -> throw Exception()
}

internal fun ColumnData<*>.isTable(): Boolean = when(this){
    is TableColumn<*> -> true
    is ColumnWithPath<*> -> source.isTable()
    else -> false
}

fun DataCol.isGrouped(): Boolean = when(this){
    is GroupedColumn<*> -> true
    is ColumnWithPath<*> -> source.isGrouped()
    else -> false
}

fun <T> column() = ColumnDelegate<T>()

fun columnGroup() = column<DataFrameRow<*>>()

fun <T> columnList() = column<List<T>>()

fun <T> columnGroup(name: String) = column<DataFrameRow<T>>(name)

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

fun DataFrame<*>.nameGenerator() = ColumnNameGenerator(columnNames())

internal fun <T, R> ColumnData<T>.mapValues(transform: (T) -> R) = map(transform)

fun <T, R> ColumnData<T>.map(transform: (T) -> R): ColumnData<R> {
    val collector = ColumnDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}

fun <T, R> ColumnData<T>.map(type: KType, transform: (T) -> R): ColumnData<R> {
    val collector = createDataCollector<R>(type, size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as ColumnData<R>
}

fun <C> ColumnData<C>.single() = values.single()