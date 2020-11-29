package org.jetbrains.dataframe

import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal fun <T, G> GroupedDataFrame<T, G>.getSortColumns(selector: SortColumnSelector<G, Comparable<*>>) =
        TypedDataFrameWithColumnsForSortImpl(groups.values.first())
                .let { selector(it, it).extractSortColumns() }

typealias GroupKey = List<Any?>

internal val columnForGroupedData by column<TypedDataFrame<*>>("DataFrame")

interface GroupedDataFrame<out T, out G> {

    val groups : TableColumn<G>

    val keys : TypedDataFrame<T>

    fun asPlain(): TypedDataFrame<T>

    fun ungroup() = groups.union().typed<G>()

    operator fun get(vararg values: Any?) = get(values.toList())
    operator fun get(key: GroupKey): TypedDataFrame<T>

    fun <R> modify(transform: TypedDataFrame<G>.() -> TypedDataFrame<R>): GroupedDataFrame<T, R>

    data class Entry<T, G>(val key: TypedDataFrameRow<T>, val group: TypedDataFrame<G>)
}

fun <T,G> GroupedDataFrame<T,G>.sortBy(vararg columns: String) = sortBy(columns.map { SortColumnDescriptor(it.toColumn(), org.jetbrains.dataframe.SortDirection.Desc) })
fun <T,G> GroupedDataFrame<T,G>.sortBy(vararg columns: ColumnDef<Comparable<*>>) = sortBy(columns.map { SortColumnDescriptor(it, org.jetbrains.dataframe.SortDirection.Desc) })

fun <T,G> GroupedDataFrame<T,G>.sortBy(selector: SortColumnSelector<G, Comparable<*>>) = sortBy(getSortColumns(selector))

fun <T,G> GroupedDataFrame<T,G>.sortKeysBy(selector: SortColumnSelector<T, Comparable<*>>) = sortBy(asPlain().getSortColumns(selector))

internal fun <T,G> TypedDataFrame<T>.asGrouped(groupedColumnName: String): GroupedDataFrame<T,G> = GroupedDataFrameImpl(this, this[groupedColumnName] as TableColumn<G>)

fun <T,G> GroupedDataFrame<T,G>.sortBy(columns: List<SortColumnDescriptor>): GroupedDataFrame<T,G> {

    val keyColumns = columns.filter { keys.tryGetColumn(it.column) != null }
    val groupColumns = columns.filter { keys.tryGetColumn(it.column) == null }
    var result = asPlain()
    if(!groupColumns.isEmpty())
        result = result.update { groups }.with { it.sortBy(groupColumns) }
    if(!keyColumns.isEmpty())
        result = result.sortBy(keyColumns)
    return result.asGrouped { groups }
}

fun <T,G> GroupedDataFrame<T,G>.forEach(body: (GroupedDataFrame.Entry<T, G>) -> Unit) = this@forEach.forEach { key, group -> body(GroupedDataFrame.Entry(key, group)) }

fun <T,G> GroupedDataFrame<T,G>.forEach(body: (key: TypedDataFrameRow<T>, group: TypedDataFrame<G>) -> Unit) =
    keys.forEachIndexed { index, row ->
        val group = groups[index]
        body(row, group)
    }

fun <T,G,R> GroupedDataFrame<T,G>.map(body: (key: TypedDataFrameRow<T>, group: TypedDataFrame<G>) -> R) =
        keys.mapIndexed { index, row ->
            val group = groups[index]
            body(row, group)
        }

class GroupedDataFrameImpl<T, G>(val df: TypedDataFrame<T>, override val groups: TableColumn<G>): GroupedDataFrame<T, G> {
    override val keys by lazy { df - groups }

    override operator fun get(key: GroupKey): TypedDataFrame<T> {

        require(key.size < df.ncol) { "Invalid size of the key" }

        val keySize = key.size
        val filtered = df.filter { values.subList(0, keySize) == key }
        return filtered[groups].values.union().typed<T>()
    }

    override fun <R> modify(transform: TypedDataFrame<G>.() -> TypedDataFrame<R>) =
            df.update(groups) { transform(it) }.asGrouped { groups.typed<R>() }

    override fun asPlain() = df
}

class GroupAggregateBuilder<T>(internal val df: TypedDataFrame<T>): TypedDataFrame<T> by df {

    private data class NamedValue(val path: List<String>, val value: Any?, val type: KType, val defaultValue: Any?)

    private val values = mutableListOf<NamedValue>()

    internal fun toDataFrame() = values.map { it.path to DataCol.create(it.path.last(), listOf(it.value), it.type, it.defaultValue) }.toDataFrame<T>() ?: emptyDataFrame(1).typed()

    fun <R> add(path: List<String>, value: R, type: KType, default: R? = null) {
        values.add(NamedValue(path, value, type, default))
    }

    fun <C> spread(selector: ColumnSelector<T, C>) = SpreadClause.inAggregator(this, selector)
    fun <C> spread(column: ColumnDef<C>) = spread { column }
    fun <C> spread(column: KProperty<C>) = spread(column.toColumn())
    fun <C> spread(column: String) = spread(column.toColumn())

    fun <C> countBy(selector: ColumnSelector<T, C>) = spread(selector).with { nrow }.useDefault(0)
    fun <C> countBy(column: ColumnDef<C>) = countBy { column }
    fun <C> countBy(column: KProperty<C>) = countBy(column.toColumn())
    fun countBy(column: String) = countBy(column.toColumn())

    inline infix fun <reified R> R.into(name: String)  = add(listOf(name), this, getType<R>())
}

typealias Reducer<T, R> = TypedDataFrame<T>.(TypedDataFrame<T>) -> R

typealias GroupAggregator<G> = GroupAggregateBuilder<G>.(GroupAggregateBuilder<G>) -> Unit

fun <T, G> GroupedDataFrame<T, G>.aggregate(body: GroupAggregator<G>) = doAggregate( asPlain(), { groups }, removeColumns = true, body)

fun <T, G> TypedDataFrame<T>.aggregate(selector: ColumnSelector<T, TypedDataFrame<G>>, body: GroupAggregator<G>) = doAggregate(this, selector, removeColumns = false, body)

internal fun <T, G> doAggregate(df: TypedDataFrame<T>, selector: ColumnSelector<T, TypedDataFrame<G>>, removeColumns: Boolean, body: GroupAggregator<G>): TypedDataFrame<T> {

    val column = df.getColumn(selector)

    val (df2, removedTree) = df.doRemove(listOf(column))

    val groupedFrame = column.values.map {
        val builder = GroupAggregateBuilder(it)
        body(builder, builder)
        builder.toDataFrame()
    }.union()

    val removedNode = removedTree.allRemovedColumns().single()
    val insertPath = removedNode.pathFromRoot().dropLast(1)

    if(!removeColumns) removedNode.data.wasRemoved = false

    val columnsToInsert = groupedFrame.columns.map {
        ColumnToInsert(insertPath + it.name, removedNode, it)
    }
    val src = if(removeColumns) df2 else df
    return src.doInsert(columnsToInsert)
}

inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.median(columnName: String = "median", noinline selector: RowSelector<G, R>) = aggregate { median(selector) into columnName }
inline fun <T, G, reified R : Number> GroupedDataFrame<T, G>.mean(columnName: String = "mean", noinline selector: RowSelector<G, R>) = aggregate { mean(selector) into columnName }
inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.min(columnName: String = "min", noinline selector: RowSelector<G, R>) = aggregate { min(selector) into columnName }
inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.max(columnName: String = "max", noinline selector: RowSelector<G, R>) = aggregate { max(selector) into columnName }

fun <T, G> GroupedDataFrame<T, G>.countInto(columnName: String) = aggregate {
    nrow into columnName
}
