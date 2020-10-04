package krangl.typed

import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal fun <T> GroupedDataFrame<T>.getColumns(selector: ColumnsSelector<T, *>) =
        TypedDataFrameWithColumnsForSelectImpl(groups.first())
                .let { it.extractColumns(selector(it, it)) }

internal fun <T> GroupedDataFrame<T>.getSortColumns(selector: SortColumnSelector<T, Comparable<*>>) =
        TypedDataFrameWithColumnsForSortImpl(groups.first())
                .let { selector(it, it).extractSortColumns() }

typealias GroupKey = List<Any?>

internal val columnForGroupedData by column<TypedDataFrame<*>>("DataFrame")

interface GroupedDataFrame<out T> {

    val keys: TypedDataFrame<T>
    val groups: List<TypedDataFrame<T>>
    val size: Int

    operator fun get(vararg values: Any?) = get(values.toList())
    operator fun get(key: GroupKey): TypedDataFrame<T>

    fun ungroup() = groups.union().typed<T>()

    fun sortBy(columns: List<SortColumnDescriptor>) = modify { sortBy(columns) }
    fun sortBy(vararg columns: String) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Desc) })
    fun sortBy(vararg columns: ColumnDef<Comparable<*>>) = sortBy(columns.map { SortColumnDescriptor(it.name, SortDirection.Desc) })
    fun sortBy(selector: SortColumnSelector<T, Comparable<*>>) = sortBy(getSortColumns(selector))

    fun sortByDesc(selector: SortColumnSelector<T, Comparable<*>>) = sortBy(getSortColumns(selector).map { SortColumnDescriptor(it.column, SortDirection.Desc) })

    fun modify(transform: TypedDataFrame<T>.() -> TypedDataFrame<*>): GroupedDataFrame<T>

    fun count(columnName: String = "n") = aggregate { count into columnName }
    fun count(columnName: String = "n", filter: RowFilter<T>) = aggregate { count(filter) into columnName }
}

class GroupedDataFrameImpl<T>(val df: TypedDataFrame<T>) : GroupedDataFrame<T> {

    override fun modify(transform: TypedDataFrame<T>.() -> TypedDataFrame<*>) =
            GroupedDataFrameImpl(df.update(columnForGroupedData) { transform(this[columnForGroupedData].typed()) })

    override val groups get() = df[columnForGroupedData].values as List<TypedDataFrame<T>>

    override val keys by lazy { df - columnForGroupedData }

    override operator fun get(key: GroupKey): TypedDataFrame<T> {

        require(key.size < df.ncol) { "Invalid size of the key" }

        val keySize = key.size
        val filtered = df.filter { values.subList(0, keySize) == key }
        return filtered[columnForGroupedData].values.union().typed<T>()
    }

    override fun sortBy(columns: List<SortColumnDescriptor>): GroupedDataFrame<T> {
        val keyColumns = columns.filter { df.tryGetColumn(it.column) != null }
        val groupColumns = columns - keyColumns

        val newDf = df.sortBy(keyColumns).update(columnForGroupedData) {
            columnForGroupedData().sortBy(groupColumns)
        }
        return GroupedDataFrameImpl(newDf)
    }

    override val size = df.nrow
}

typealias Reducer<T, R> = (TypedDataFrame<T>) -> R

class GroupAggregateBuilder<T>(private val dataFrame: GroupedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    val groups get() = dataFrame.groups

    fun add(column: DataCol) = columns.add(column)

    fun <R> reduce(func: Reducer<T, R>) = func

    fun <N : Comparable<N>> minBy(selector: RowSelector<T, N>) = compute { minBy(selector)!! }
    fun <N : Comparable<N>> minBy(column: ColumnDef<N>) = compute { minBy { column() }!! }
    inline fun <reified N : Comparable<N>> minBy(property: KProperty<N>) = minBy(property.toColumn())

    fun <C> single(valueSelector: RowSelector<T, C>) = reduce { it.single().let { valueSelector(it, it) } }

    fun <N : Comparable<N>> maxBy(selector: RowSelector<T, N>) = compute { maxBy(selector)!! }
    fun <N : Comparable<N>> maxBy(column: ColumnDef<N>) = compute { maxBy { column() }!! }
    inline fun <reified N : Comparable<N>> maxBy(property: KProperty<N>) = maxBy(property.toColumn())

    fun <R, V> Reducer<T, R>.map(transform: R.(R) -> V) = reduce { this(it).let { transform(it, it) } }

    fun <R> TypedDataFrame<T>.map(selector: RowSelector<T, R>) =
            rows.map { selector(it, it) }

    val count by lazy { reduce { it.nrow } }
    val exists by lazy { reduce { it.nrow > 0 } }

    fun count(filter: RowFilter<T>) = reduce { it.count(filter) }

    inline fun <reified R : Comparable<R>> median(noinline selector: RowSelector<T, R>) = reduce { it.map(selector).median() }
    inline fun <reified R : Comparable<R>> median(column: ColumnDef<R>) = reduce { it[column].values.median() }
    inline fun <reified R : Comparable<R>> median(property: KProperty<R>) = median(property.toColumn())

    inline fun <reified R : Number> mean(noinline selector: RowSelector<T, R>) = reduce { it.map(selector).mean() }
    inline fun <reified R : Number> mean(column: ColumnDef<R>) = reduce { it[column].values.mean() }
    inline fun <reified R : Number> mean(property: KProperty<R>) = mean(property.toColumn())

    inline fun <reified R : Number> sum(column: ColumnDef<R>) = reduce { sum(it[column].values) }
    inline fun <reified R : Number> sum(noinline selector: RowSelector<T, R>) = reduce { sum(it.map(selector)) }

    fun checkAll(predicate: RowFilter<T>) = reduce { it.all(predicate) }
    fun any(predicate: RowFilter<T>) = reduce { it.any(predicate) }

    inline fun <reified R : Comparable<R>> min(noinline selector: RowSelector<T, R>) = reduce { it.min(selector)!! }
    inline fun <reified R : Comparable<R>> min(column: ColumnDef<R>) = reduce { it.min(column)!! }
    inline fun <reified R : Comparable<R>> min(property: KProperty<R>) = min(property.toColumn())

    inline fun <reified R : Comparable<R>> max(noinline selector: RowSelector<T, R>) = reduce { it.max(selector)!! }
    inline fun <reified R : Comparable<R>> max(column: ColumnDef<R>) = reduce { it.max(column)!! }

    inline infix fun <reified R> Reducer<T, R>.into(columnName: String) = add(column(columnName, groups.map(this)))

    fun <R> spread(reducer: Reducer<T, R>, keySelector: RowSelector<T, String?>, type: KType) = doSpread(this, dataFrame, keySelector, type) { df, _ ->
        reducer(df)
    }

    inline infix fun <reified R> Reducer<T, R>.into(noinline keySelector: RowSelector<T, String?>) = spread(this, keySelector, getType<R>())

    fun <R> compute(selector: DataFrameExpression<T, R>) = reduce { selector(it, it) }

    inline fun <reified R> add(name: String, noinline expression: DataFrameSelector<T, R?>) = add(column(name, groups.map { expression(it, it) }))

    inline infix fun <reified R> String.to(noinline expression: DataFrameSelector<T, R?>) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: DataFrameSelector<T, R?>) = add(this, expression)

    infix operator fun String.invoke(column: DataCol) = add(column.rename(this))
}

inline fun <T, reified R> GroupedDataFrame<T>.compute(columnName: String = "map", noinline selector: DataFrameSelector<T, R>) = aggregate { compute(selector) into columnName }

fun <T> GroupedDataFrame<T>.aggregate(body: GroupAggregateBuilder<T>.() -> Unit): TypedDataFrame<T> {
    val builder = GroupAggregateBuilder(this)
    body(builder)
    return (keys + builder.columns).typed()
}

inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.median(columnName: String = "median", noinline selector: RowSelector<T, R>) = aggregate { median(selector) into columnName }
inline fun <T, reified R : Number> GroupedDataFrame<T>.mean(columnName: String = "mean", noinline selector: RowSelector<T, R>) = aggregate { mean(selector) into columnName }
inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.min(columnName: String = "min", noinline selector: RowSelector<T, R>) = aggregate { min(selector) into columnName }
inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.max(columnName: String = "max", noinline selector: RowSelector<T, R>) = aggregate { max(selector) into columnName }

internal val <T> GroupedDataFrame<T>.baseDataFrame get() = (this as GroupedDataFrameImpl<T>).df