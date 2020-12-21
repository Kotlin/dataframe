package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.createDataCollector
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

enum class UnresolvedColumnsPolicy { Fail, Skip, Create }

class ColumnResolutionContext(val df: DataFrameBase<*>, val unresolvedColumnsPolicy: UnresolvedColumnsPolicy) {

    val allowMissingColumns = unresolvedColumnsPolicy == UnresolvedColumnsPolicy.Skip
}

interface ColumnSet<out C> {

    fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

interface SingleColumn<out C> : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext) = resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}

internal fun <C> DataFrameBase<*>.getColumn(name: String, policy: UnresolvedColumnsPolicy) =
        tryGetColumn(name)?.typed()
                ?: when (policy) {
                    UnresolvedColumnsPolicy.Fail -> throw Exception("Column not found: $this")
                    UnresolvedColumnsPolicy.Skip -> null
                    UnresolvedColumnsPolicy.Create -> DataCol.empty().typed<C>()
                }

interface ColumnDef<out C> : SingleColumn<C> {

    val name: String

    operator fun invoke(row: DataFrameRow<*>) = row[this]

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return context.df.getColumn<C>(name, context.unresolvedColumnsPolicy)?.addPath(listOf(name))
    }
}

fun <TD, T: DataFrameBase<TD>, C> Selector<T, ColumnSet<C>>.toColumns(createReceiver: (ColumnResolutionContext) -> T) = createColumnSet {
    val receiver = createReceiver(it)
    val columnSet = this(receiver, receiver)
    columnSet.resolve(ColumnResolutionContext(receiver, it.unresolvedColumnsPolicy))
}

fun <C> createColumnSet(resolver: (ColumnResolutionContext) -> List<ColumnWithPath<C>>): ColumnSet<C> = ColumnsBySelector(resolver)

class ColumnsBySelector<C>(val resolver: (ColumnResolutionContext) -> List<ColumnWithPath<C>>) : ColumnSet<C> {

    override fun resolve(context: ColumnResolutionContext) = resolver(context)
}

class RenamedColumnDefImpl<C>(val source: ColumnDef<C>, override val name: String) : ColumnDef<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {

        return source.resolveSingle(context)?.let { it.data.doRename(name).addPath(it.path) }
    }
}

inline fun <C, reified R> ColumnDef<C>.map(noinline transform: (C) -> R): SingleColumn<R> = map(getType<R>(), transform)

fun <C, R> ColumnDef<C>.map(targetType: KType?, transform: (C) -> R): SingleColumn<R> = ConvertedColumnDef(this, transform, targetType)

class ConvertedColumnDef<C, R>(val source: ColumnDef<C>, val transform: (C) -> R, val type: KType?) : SingleColumn<R> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<R>? {
        return source.resolveSingle(context)?.let { it.data.map(type, transform).addPath(it.path) }
    }
}

typealias Column = ColumnDef<*>

typealias GroupedColumnDef = ColumnDef<DataFrameRow<*>>

interface ColumnData<out T> : ColumnDef<T> {

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ColumnData<T> = ValueColumnImpl(values, name, type, defaultValue)

        fun <T> createGroup(name: String, df: DataFrame<T>): GroupedColumn<T> = GroupedColumnImpl(df, name)

        fun <T> createTable(name: String, df: DataFrame<T>, startIndices: List<Int>): TableColumn<T> = TableColumnImpl(name, df, startIndices)

        fun <T> createTable(name: String, groups: List<DataFrame<T>>, df: DataFrame<T>? = null): TableColumn<T> = TableColumnImpl(df
                ?: groups.getBaseSchema(), name, groups)

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

    fun defaultValue(): T?

    operator fun get(range: IntRange): ColumnData<T>

    operator fun get(columnName: String): ColumnData<*>

    operator fun get(indices: Iterable<Int>): ColumnData<T>

    operator fun get(mask: BooleanArray): ColumnData<T>

    fun distinct(): ColumnData<T>

    fun toSet(): Set<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath()
}

typealias DataCol = ColumnData<*>

fun String.toColumnDef(): ColumnDef<Any?> = ColumnDefinition(this)

internal fun KProperty<*>.getColumnName() = this.findAnnotation<ColumnName>()?.name ?: name

fun <T> KProperty<T>.toColumnDef() = ColumnDefinition<T>(name)

class ColumnDefinition<T>(override val name: String) : ColumnDef<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
}

inline fun <reified T> ColumnDefinition<T>.nullable() = ColumnDefinition<T?>(name)

interface GroupedColumnBase<T> : SingleColumn<DataFrameRow<T>>, DataFrameBase<T> {
    fun asDataFrame(): DataFrame<T>
}

interface NestedColumn<out T> {
    val df: DataFrame<T>
}

interface ValueColumn<T> : ColumnData<T>

interface GroupedColumn<T> : ColumnData<DataFrameRow<T>>, NestedColumn<T>, GroupedColumnBase<T> {
    override fun asDataFrame() = df
}

interface TableColumn<out T> : ColumnData<DataFrame<T>>, NestedColumn<T>

typealias ColumnPath = List<String>

internal fun ColumnPath.depth() = size - 1

interface ColumnWithPath<out T> : ColumnDef<T> {

    val data: ColumnData<T>
    val path: ColumnPath
    override val name: String get() = data.name
    val type: KType get() = data.type
    val hasNulls: Boolean get() = data.hasNulls
    fun isGrouped() = data.isGrouped()
    fun asGrouped() = data.asGrouped().addPath(path)
    fun depth() = path.depth()
    fun children() = if (data.isGrouped()) data.asGrouped().columns().map { it.addPath(path + it.name) } else emptyList()
}

class ColumnWithPathImpl<T> internal constructor(override val data: ColumnData<T>, override val path: ColumnPath) : ColumnWithPath<T> {

    override fun resolveSingle(context: ColumnResolutionContext) = this
}

internal fun <T> ColumnData<T>.addPath(path: ColumnPath): ColumnWithPath<T> = ColumnWithPathImpl(this, path)

internal fun <T> ColumnData<T>.addPath(): ColumnWithPath<T> = ColumnWithPathImpl(this, listOf(name))

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

    if (!(other is ColumnData<*>)) return false

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

interface ColumnWithParent<C> : ColumnDef<C> {

    val parent: GroupedColumnDef?

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {

        val parentDef = parent
        val (targetDf, pathPrefix) = when(parentDef){
            null -> context.df to emptyList()
            else -> {
                val parentCol = parentDef.resolveSingle(context) ?: return null
                val group = parentCol.data.asGrouped()
                group.df to parentCol.path
            }
        }

        val data = targetDf.getColumn<C>(name, context.unresolvedColumnsPolicy)
        return data?.addPath(pathPrefix + name)
    }
}

interface ColumnDataWithParent<C> : ColumnWithParent<C>, ColumnData<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return super<ColumnWithParent>.resolveSingle(context)
    }
}

class ColumnDataWithParentImpl<T>(override val parent: GroupedColumn<*>, val source: ColumnData<T>) : ColumnDataWithParent<T>, ColumnData<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<ColumnDataWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<ColumnDataWithParent>.resolve(context)
    }
}

class GroupedColumnWithParent<T>(override val parent: GroupedColumnDef?, val source: GroupedColumn<T>) : ColumnDataWithParent<DataFrameRow<T>>, GroupedColumn<T> by source {
    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<DataFrameRow<R>>) = df[column].addParent(this) as GroupedColumn<R>
    override fun columns() = df.columns().map { it.addParent(this) }
    override fun getColumn(columnIndex: Int) = df.getColumn(columnIndex).addParent(this)

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataFrameRow<T>>? {
        return super<ColumnDataWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataFrameRow<T>>> {
        return super<ColumnDataWithParent>.resolve(context)
    }
}

class TableColumnWithParent<T>(override val parent: GroupedColumn<*>, val source: TableColumn<T>) : ColumnDataWithParent<DataFrame<T>>, TableColumn<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataFrame<T>>? {
        return super<ColumnDataWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataFrame<T>>> {
        return super<ColumnDataWithParent>.resolve(context)
    }
}

internal fun <T> ColumnData<T>.addParent(parent: GroupedColumn<*>) = (this as ColumnDataInternal<T>).addParent(parent)

internal interface ColumnDataInternal<T> : ColumnData<T> {

    fun rename(newName: String): ColumnData<T>
    fun addParent(parent: GroupedColumn<*>): ColumnData<T>
}

internal class TableColumnImpl<T> constructor(override val df: DataFrame<T>, name: String, values: List<DataFrame<T>>)
    : ValueColumnImpl<DataFrame<T>>(values, name, createType<DataFrame<*>>()), TableColumn<T> {

    constructor(name: String, df: DataFrame<T>, startIndices: List<Int>) : this(df, name, df.splitByIndices(startIndices))

    override fun rename(newName: String) = TableColumnImpl(df, newName, values)

    override fun get(indices: Iterable<Int>): ColumnData<DataFrame<T>> {

        return ColumnData.createTable(name, indices.map(this::get))
    }

    override fun get(mask: BooleanArray): ColumnData<DataFrame<T>> {

        return ColumnData.createTable(name, values.filterIndexed { index, _ -> mask[index] })
    }

    override fun kind() = ColumnKind.Table

    override fun addParent(parent: GroupedColumn<*>) = TableColumnWithParent(parent, this)
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

    override fun getColumn(columnIndex: Int) = df.getColumn(columnIndex).addParent(this)

    override fun columns() = df.columns().map { it.addParent(this) }

    override fun defaultValue() = null

    override fun kind() = ColumnKind.Group

    override fun get(indices: Iterable<Int>) = withDf(df[indices])

    override fun get(mask: BooleanArray) = withDf(df.getRows(mask))

    override fun addParent(parent: GroupedColumn<*>) = GroupedColumnWithParent(parent, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val g = other as? GroupedColumn<*> ?: return false
        return name == g.name && df == other.df
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + df.hashCode()
    }

    override fun tryGetColumn(columnName: String) = df.tryGetColumn(columnName)
}

internal open class ValueColumnImpl<T>(override val values: List<T>, override val name: String, override val type: KType, val defaultValue: T? = null, set: Set<T>? = null) : ColumnDataInternal<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = "$name: $type"

    override val ndistinct = toSet().size

    override fun distinct() = ValueColumnImpl(toSet().toList(), name, type, defaultValue, valuesSet)

    override fun get(index: Int) = values[index]

    override fun get(range: IntRange) = ColumnDataPart(this, range)

    override fun get(columnName: String) = throw Exception()

    override val size: Int
        get() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun rename(newName: String) = ValueColumnImpl(values, newName, type, defaultValue, valuesSet)

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

    override fun addParent(parent: GroupedColumn<*>): ColumnData<T> = ColumnDataWithParentImpl(parent, this)
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

    override fun distinct() = ValueColumnImpl(toSet().toList(), name, type, defaultValue(), valuesSet)

    fun contains(value: T) = toSet().contains(value)

    override val size = part.endInclusive - part.first + 1

    override fun get(index: Int) = source[part.first + index]

    override fun get(range: IntRange) = source[IntRange(range.first + part.first, range.endInclusive + part.first)]

    override val name: String
        get() = source.name

    override fun get(columnName: String) = source[columnName][part]

    override fun rename(newName: String) = ColumnDataPart(source.doRename(newName), part)

    override fun defaultValue() = source.defaultValue()

    override fun kind() = source.kind()

    override val df: DataFrame<T>
        get() = (source as? NestedColumn<T>)?.df ?: throw UnsupportedOperationException()

    override fun get(indices: Iterable<Int>) = source[indices.map { part.first + it }]

    override fun get(mask: BooleanArray): ColumnData<T> {
        val newMask = BooleanArray(mask.size + part.first) { if (it < part.first) false else mask[part.first + it] }
        return source[newMask]
    }

    override fun addParent(parent: GroupedColumn<*>) = ColumnDataWithParentImpl(parent, this)
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

fun <T> ColumnData<T>.withValues(values: List<T>, hasNulls: Boolean) = when (this) {
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

fun <C> ColumnDef<C>.rename(newName: String) = if (newName == name) this else RenamedColumnDefImpl(this, newName)

fun <C> ColumnData<C>.doRename(newName: String) = if (newName == name) this else (this as ColumnDataInternal<C>).rename(newName)

internal fun <T> Iterable<T>.asList() = when (this) {
    is List<T> -> this
    else -> this.toList()
}

fun <C> ColumnData<C>.ensureUniqueName(nameGenerator: ColumnNameGenerator) = doRename(nameGenerator.addUnique(name))

class InplaceColumnBuilder(val name: String) {
    inline operator fun <reified T> invoke(vararg values: T) = column(name, values.toList())
}

fun column(name: String) = InplaceColumnBuilder(name)

inline fun <T, reified R> DataFrame<T>.new(name: String, noinline expression: RowSelector<T, R>): ColumnData<R> {
    var nullable = false
    val values = (0 until nrow).map { get(it).let { expression(it, it) }.also { if (it == null) nullable = true } }
    return column(name, values, nullable)
}

internal fun Array<out String>.toColumns(): ColumnSet<Any?> = map { it.toColumnDef() }.toColumns()
fun <C> Iterable<ColumnSet<C>>.toColumns() = ColumnGroup(asList())
internal fun <C> Array<out KProperty<C>>.toColumns() = map { it.toColumnDef() }.toColumns()
internal fun <T> Array<out ColumnDef<T>>.toColumns() = toList().toColumns()
internal fun <T, C> ColumnsSelector<T, C>.toColumns(): ColumnSet<C> = toColumns { SelectReceiverImpl(it.df.typed(), it.allowMissingColumns) }

@JvmName("toColumnSetForSort")
internal fun <T, C> SortColumnsSelector<T, C>.toColumns(): ColumnSet<C> = toColumns { SortReceiverImpl(it.df.typed(), it.allowMissingColumns) }


class ColumnGroup<C>(val columns: List<ColumnSet<C>>) : ColumnSet<C> {
    constructor(vararg columns: ColumnSet<C>) : this(columns.toList())

    override fun resolve(context: ColumnResolutionContext) = columns.flatMap { it.resolve(context) }
}

class ColumnDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = ColumnDefinition<T>(property.name)
}

fun DataCol.asFrame(): DataFrame<*> = when (this) {
    is GroupedColumn<*> -> df
    is ColumnWithPath<*> -> data.asFrame()
    else -> throw Exception()
}

internal fun DataCol.asGrouped(): GroupedColumn<*> = this as GroupedColumn<*>

@JvmName("asGroupedT")
internal fun <T> ColumnData<DataFrameRow<T>>.asGrouped(): GroupedColumn<T> = this as GroupedColumn<T>

internal fun DataCol.asTable(): TableColumn<*> = this as TableColumn<*>

@JvmName("asTableT")
internal fun <T> ColumnData<DataFrame<T>>.asTable(): TableColumn<T> = this as TableColumn<T>

internal fun DataCol.isTable(): Boolean = kind() == ColumnKind.Table

fun DataCol.isGrouped(): Boolean = kind() == ColumnKind.Group

fun <T> column() = ColumnDelegate<T>()

fun columnGroup() = column<DataFrameRow<*>>()

fun <T> columnList() = column<List<T>>()

fun <T> columnGroup(name: String) = column<DataFrameRow<T>>(name)

fun <T> tableColumn(name: String) = column<DataFrame<T>>(name)

fun <T> columnList(name: String) = column<List<T>>(name)

fun <T> column(name: String) = ColumnDefinition<T>(name)

inline fun <reified T> column(name: String, values: List<T>): ColumnData<T> = column(name, values, values.any { it == null })

inline fun <reified T> column(name: String, values: List<T>, hasNulls: Boolean): ColumnData<T> = ColumnData.create(name, values, getType<T>().withNullability(hasNulls))

fun <T> column(name: String, values: List<T>, type: KType): ColumnData<T> = ValueColumnImpl(values, name, type)

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
        if (!usedNames.contains(name)) {
            usedNames.add(name)
            colNames.add(name)
        }
    }

    val names: List<String>
        get() = colNames

    fun contains(name: String) = usedNames.contains(name)
}

fun DataFrame<*>.nameGenerator() = ColumnNameGenerator(columnNames())

fun <T, R> ColumnData<T>.map(transform: (T) -> R): ColumnData<R> {
    val collector = createDataCollector(size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name).typed()
}

fun <T, R> ColumnData<T>.map(type: KType?, transform: (T) -> R): ColumnData<R> {
    if (type == null) return map(transform)
    val collector = createDataCollector<R>(type, size)
    values.forEach { collector.add(transform(it)) }
    return collector.toColumn(name) as ColumnData<R>
}

fun <C> ColumnData<C>.single() = values.single()

fun <T> TableColumn<T>.toDefinition() = tableColumn<T>(name)
fun <T> GroupedColumn<T>.toDefinition() = columnGroup<T>(name)
fun <T> ValueColumn<T>.toDefinition() = column<T>(name)