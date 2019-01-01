package krangl.typed

import krangl.DataCol

internal fun <T> GroupedDataFrame<T>.getColumns(selector: ColumnSelector<T>) = selector(TypedDataFrameWithColumnsImpl((this as GroupedDataFrameImpl<T>).groups.first().df)).extractColumns()typealias GroupKey = List<Any?>

interface DataGroup<out T> {
    val groupKey: GroupKey
    val df: TypedDataFrame<T>
}

class DataGroupImpl<T>(override val groupKey: GroupKey,
                       override val df: TypedDataFrame<T>) : DataGroup<T> {
    override fun toString(): String {
        return "DataGroup($groupKey)" // just needed for debugging
    }
}

interface GroupedDataFrame<out T> {

    val groups: List<DataGroup<T>>
    val columnNames: List<String>

    operator fun get(vararg values: Any?) = get(values.toList())
    operator fun get(key: GroupKey) = groups.firstOrNull { it.groupKey.equals(key) }?.df

    fun ungroup() = groups.map { it.df }.bindRows()
    fun groupedBy() = groups.map { it.df.take(1).select(*columnNames.toTypedArray()) }.bindRows()
    fun groups() = groups.map { it.df }

    fun sortedBy(columns: Iterable<DataCol>) = modify { sortedBy(columns) }
    fun sortedBy(selector: ColumnSelector<T>) = sortedBy(getColumns(selector))

    fun sortedByDesc(selector: ColumnSelector<T>) = sortedByDesc(getColumns(selector))
    fun sortedByDesc(columns: Iterable<DataCol>) = modify {sortedByDesc(columns)}

    fun modify(transform: TypedDataFrame<T>.() -> TypedDataFrame<*>): GroupedDataFrame<T>

    fun count(columnName: String = "n") = aggregate { count into columnName}
    fun count(columnName: String = "n", filter: RowFilter<T>) = aggregate { count(filter) into columnName}
}

class GroupedDataFrameImpl<T>(val columns: List<String>, override val groups: List<DataGroup<T>>): GroupedDataFrame<T> {


    override val columnNames: List<String>
        get() = columns

    override fun modify(transform: TypedDataFrame<T>.() -> TypedDataFrame<*>) =
            GroupedDataFrameImpl(columns, groups.map { DataGroupImpl<T>(it.groupKey, transform(it.df).typed()) })
}