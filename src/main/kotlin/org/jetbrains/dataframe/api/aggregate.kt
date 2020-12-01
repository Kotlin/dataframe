package org.jetbrains.dataframe

import kotlin.reflect.KProperty
import kotlin.reflect.KType

class GroupAggregateBuilder<T>(internal val df: DataFrame<T>): DataFrame<T> by df {

    private data class NamedValue(val path: List<String>, val value: Any?, val type: KType, val defaultValue: Any?)

    private val values = mutableListOf<NamedValue>()

    internal fun toDataFrame() = values.map { it.path to ColumnData.create(it.path.last(), listOf(it.value), it.type, it.defaultValue) }.toDataFrame<T>() ?: emptyDataFrame(1).typed()

    fun <R> add(path: List<String>, value: R, type: KType, default: R? = null) {
        values.add(NamedValue(path, value, type, default))
    }

    fun <C> spread(selector: ColumnSelector<T, C>) = SpreadClause.inAggregator(this, selector)
    fun <C> spread(column: ColumnDef<C>) = spread { column }
    fun <C> spread(column: KProperty<C>) = spread(column.toColumnDef())
    fun <C> spread(column: String) = spread(column.toColumnDef())

    fun <C> countBy(selector: ColumnSelector<T, C>) = spread(selector).with { nrow }.useDefault(0)
    fun <C> countBy(column: ColumnDef<C>) = countBy { column }
    fun <C> countBy(column: KProperty<C>) = countBy(column.toColumnDef())
    fun countBy(column: String) = countBy(column.toColumnDef())

    inline infix fun <reified R> R.into(name: String)  = add(listOf(name), this, getType<R>())
}typealias GroupAggregator<G> = GroupAggregateBuilder<G>.(GroupAggregateBuilder<G>) -> Unit

fun <T, G> GroupedDataFrame<T, G>.aggregate(body: GroupAggregator<G>) = doAggregate(asPlain(), { groups }, removeColumns = true, body)
fun <T, G> DataFrame<T>.aggregate(selector: ColumnSelector<T, DataFrame<G>>, body: GroupAggregator<G>) = doAggregate(this, selector, removeColumns = false, body)
internal fun <T, G> doAggregate(df: DataFrame<T>, selector: ColumnSelector<T, DataFrame<G>>, removeColumns: Boolean, body: GroupAggregator<G>): DataFrame<T> {

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