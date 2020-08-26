package krangl.typed

import kotlin.reflect.KProperty

internal fun <T> GroupedDataFrame<T>.getColumns(selector: ColumnsSelector<T,*>) =
        TypedDataFrameWithColumnsForSelectImpl(groups.first())
                .let { it.extractColumns(selector(it, it)) }

internal fun <T> GroupedDataFrame<T>.getSortColumns(selector: SortColumnSelector<T,Comparable<*>>) =
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
    fun sortBy(vararg columns: TypedCol<Comparable<*>>) = sortBy(columns.map { SortColumnDescriptor(it.name, SortDirection.Desc) })
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

class ValuesList<T>(val list: List<T>)

class GroupAggregateBuilder<T>(private val dataFrame: GroupedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun <T> List<T>.wrap() = ValuesList(this)

    val groups get() = dataFrame.groups

    fun add(column: DataCol) = columns.add(column)

    fun <N : Comparable<N>> minBy(selector: RowSelector<T, N>) = compute { minBy(selector)!! }
    fun <N : Comparable<N>> minBy(column: TypedCol<N>) = compute { minBy { column() }!! }
    inline fun <reified N : Comparable<N>> minBy(property: KProperty<N>) = minBy(property.toColumn())

    fun <N : Comparable<N>> maxBy(selector: RowSelector<T, N>) = compute { maxBy(selector)!! }
    fun <N : Comparable<N>> maxBy(column: TypedCol<N>) = compute { maxBy { column() }!! }
    inline fun <reified N : Comparable<N>> maxBy(property: KProperty<N>) = maxBy(property.toColumn())

    fun <R> ValuesList<TypedDataFrameRow<T>>.map(selector: RowSelector<T, R>) =
            list.map { selector(it, it) }.wrap()

    fun <R> TypedDataFrame<T>.map(selector: RowSelector<T, R>) =
            rows.map { selector(it, it) }

    val count by lazy { groups.map { it.nrow }.wrap() }

    fun count(filter: RowFilter<T>) = groups.map { it.count(filter) }.wrap()

    inline fun <reified R : Comparable<R>> median(noinline selector: RowSelector<T, R>) = groups.map { it.map(selector).median() }.wrap()
    inline fun <reified R : Comparable<R>> median(column: TypedCol<R>) = groups.map { it[column].values.median() }.wrap()
    inline fun <reified R : Comparable<R>> median(property: KProperty<R>) = median(property.toColumn())

    inline fun <reified R : Number> mean(noinline selector: RowSelector<T, R>) = groups.map { it.map(selector).mean() }.wrap()
    inline fun <reified R : Number> mean(column: TypedCol<R>) = groups.map { it[column].values.mean() }.wrap()
    inline fun <reified R : Number> mean(property: KProperty<R>) = mean(property.toColumn())

    inline fun <reified R : Number> sum(column: TypedCol<R>) = groups.map { sum(it[column].values) }.wrap()
    inline fun <reified R : Number> sum(noinline selector: RowSelector<T, R>) = groups.map { sum(it.map(selector)) }.wrap()

    fun checkAll(predicate: RowFilter<T>) = groups.map { it.all(predicate) }.wrap()
    fun any(predicate: RowFilter<T>) = groups.map { it.any(predicate) }.wrap()

    inline fun <reified R : Comparable<R>> min(noinline selector: RowSelector<T, R>) = groups.map { it.min(selector)!! }.wrap()
    inline fun <reified R : Comparable<R>> min(column: TypedCol<R>) = groups.map { it.min(column)!! }.wrap()
    inline fun <reified R : Comparable<R>> min(property: KProperty<R>) = min(property.toColumn())

    inline fun <reified R : Comparable<R>> max(noinline selector: RowSelector<T, R>) = groups.map { it.max(selector)!! }.wrap()
    inline fun <reified R : Comparable<R>> max(column: TypedCol<R>) = groups.map { it.max(column)!! }.wrap()

    inline infix fun <reified R> ValuesList<R>.into(columnName: String) = add(column(columnName, list))

    fun <R> compute(selector: TypedDataFrame<T>.() -> R) = groups.map(selector).wrap()

    inline fun <reified R> add(name: String, noinline expression: DataFrameSelector<T,R?>) = add(column(name, groups.map { expression(it, it) }))

    inline infix fun <reified R> String.to(noinline expression: DataFrameSelector<T,R?>) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: DataFrameSelector<T,R?>) = add(this, expression)

    infix operator fun String.invoke(column: DataCol) = add(column.rename(this))
}

inline fun <T, reified R> GroupedDataFrame<T>.compute(columnName: String = "map", noinline selector: TypedDataFrame<T>.() -> R) = aggregate { compute(selector) into columnName }

fun <T> GroupedDataFrame<T>.aggregate(body: GroupAggregateBuilder<T>.() -> Unit): TypedDataFrame<T> {
    val builder = GroupAggregateBuilder(this)
    body(builder)
    return (keys + builder.columns).typed()
}

inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.median(columnName: String = "median", noinline selector: RowSelector<T, R>) = aggregate { median(selector) into columnName }
inline fun <T, reified R : Number> GroupedDataFrame<T>.mean(columnName: String = "mean", noinline selector: RowSelector<T, R>) = aggregate { mean(selector) into columnName }
inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.min(columnName: String = "min", noinline selector: RowSelector<T, R>) = aggregate { min(selector) into columnName }
inline fun <T, reified R : Comparable<R>> GroupedDataFrame<T>.max(columnName: String = "max", noinline selector: RowSelector<T, R>) = aggregate { max(selector) into columnName }