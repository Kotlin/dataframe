package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.BooleanCol
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.DoubleCol
import org.jetbrains.kotlinx.dataframe.IntCol
import org.jetbrains.kotlinx.dataframe.NumberCol
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExcept
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumnWithActualType
import org.jetbrains.kotlinx.dataframe.impl.columns.single
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.top
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.dfs
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KProperty
import kotlin.reflect.KType

public interface ColumnSelectionDsl<out T> : DataFrameBase<T> {

    public fun DataFrameBase<*>.first(numCols: Int): Columns<Any?> = cols().take(numCols)

    public fun <C> DataFrameBase<C>.first(condition: ColumnFilter<Any?>): SingleColumn<Any?> = all().first(condition)

    public fun <C> DataFrameBase<C>.single(condition: ColumnFilter<Any?>): SingleColumn<Any?> = all().single(condition)

    public fun <C> Columns<C>.first(condition: ColumnFilter<C>): SingleColumn<C> = transform { listOf(it.first(condition)) }.single()

    public fun <C> Columns<C>.single(condition: ColumnFilter<C>): SingleColumn<C> = transform { listOf(it.single(condition)) }.single()

    public fun DataFrameBase<*>.last(numCols: Int): Columns<Any?> = cols().takeLast(numCols)

    public fun DataFrameBase<*>.group(name: String): ColumnGroup<*> = this.get(name) as ColumnGroup<*>

    public fun <C> Columns<*>.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): Columns<C> = (listOf(firstCol) + otherCols).let { refs ->
        transform { it.flatMap { col -> refs.mapNotNull { col.getChild(it) } } }
    }

    public fun Columns<*>.cols(firstCol: String, vararg otherCols: String): Columns<Any?> = (listOf(firstCol) + otherCols).let { names ->
        transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
    }

    public fun Columns<*>.cols(vararg indices: Int): Columns<Any?> = transform { it.flatMap { it.children().let { children -> indices.map { children[it] } } } }
    public fun Columns<*>.cols(range: IntRange): Columns<Any?> = transform { it.flatMap { it.children().subList(range.start, range.endInclusive + 1) } }
    public fun Columns<*>.cols(predicate: (AnyCol) -> Boolean = { true }): Columns<Any?> = colsInternal(predicate)

    public fun <C> Columns<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): Columns<Any?> = dfsInternal(predicate)

    public fun DataFrameBase<*>.all(): Columns<*> = ColumnsList(children())

    public fun none(): Columns<*> = ColumnsList<Any?>(emptyList())

    public fun Columns<*>.dfs(): Columns<Any?> = dfs { !it.isColumnGroup() }

    // excluding current
    public fun DataFrameBase<*>.allAfter(colPath: ColumnPath): Columns<Any?> = children().let { var take = false; it.filter { if (take) true else { take = colPath == it.path; false } } }
    public fun DataFrameBase<*>.allAfter(colName: String): Columns<Any?> = allAfter(pathOf(colName))
    public fun DataFrameBase<*>.allAfter(column: Column): Columns<Any?> = allAfter(column.path())

    // including current
    public fun DataFrameBase<*>.allSince(colPath: ColumnPath): Columns<Any?> = children().let { var take = false; it.filter { if (take) true else { take = colPath == it.path; take } } }
    public fun DataFrameBase<*>.allSince(colName: String): Columns<Any?> = allSince(pathOf(colName))
    public fun DataFrameBase<*>.allSince(column: Column): Columns<Any?> = allSince(column.path())

    // excluding current
    public fun DataFrameBase<*>.allBefore(colPath: ColumnPath): Columns<Any?> = children().let { var take = true; it.filter { if (!take) false else { take = colPath != it.path; take } } }
    public fun DataFrameBase<*>.allBefore(colName: String): Columns<Any?> = allBefore(pathOf(colName))
    public fun DataFrameBase<*>.allBefore(column: Column): Columns<Any?> = allBefore(column.path())

    // including current
    public fun DataFrameBase<*>.allUntil(colPath: ColumnPath): Columns<Any?> = children().let { var take = true; it.filter { if (!take) false else { take = colPath != it.path; true } } }
    public fun DataFrameBase<*>.allUntil(colName: String): Columns<Any?> = allUntil(pathOf(colName))
    public fun DataFrameBase<*>.allUntil(column: Column): Columns<Any?> = allUntil(column.path())

    public fun DataFrameBase<*>.colGroups(filter: (ColumnGroup<*>) -> Boolean = { true }): Columns<AnyRow> = this.columns().filter { it.isColumnGroup() && filter(it.asColumnGroup()) }.map { it.asColumnGroup() }.toColumnSet()

    public fun <C> Columns<C>.children(predicate: (AnyCol) -> Boolean = { true }): Columns<Any?> = transform { it.flatMap { it.children().filter { predicate(it.data) } } }

    public fun ColumnGroupReference.children(): Columns<Any?> = transform { it.single().children() }

    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): Columns<C> = ColumnsList(subList(range.first, range.last + 1))

    public operator fun String.invoke(): ColumnAccessor<Any?> = toColumnAccessor()
    public operator fun String.get(column: String): ColumnPath = pathOf(this, column)
    public fun <C> String.cast(): ColumnAccessor<C> = ColumnAccessorImpl(this)

    public fun <C> col(property: KProperty<C>): ColumnAccessor<C> = property.toColumnAccessor()

    public fun Columns<*>.col(index: Int): Columns<Any?> = transform { it.mapNotNull { it.getChild(index) } }

    public fun DataFrameBase<*>.col(colName: String): DataColumn<*> = get(colName)
    public fun Columns<*>.col(colName: String): Columns<Any?> = transform { it.mapNotNull { it.getChild(colName) } }

    public operator fun Columns<*>.get(colName: String): Columns<Any?> = col(colName)
    public operator fun <C> Columns<*>.get(column: ColumnReference<C>): Columns<C> = cols(column)

    public fun <C> Columns<C>.drop(n: Int): Columns<C> = transform { it.drop(n) }
    public fun <C> Columns<C>.take(n: Int): Columns<C> = transform { it.take(n) }
    public fun <C> Columns<C>.dropLast(n: Int): Columns<C> = transform { it.dropLast(n) }
    public fun <C> Columns<C>.takeLast(n: Int): Columns<C> = transform { it.takeLast(n) }
    public fun <C> Columns<C>.top(): Columns<C> = transform { it.top() }
    public fun <C> Columns<C>.takeWhile(predicate: Predicate<ColumnWithPath<C>>): Columns<C> = transform { it.takeWhile(predicate) }
    public fun <C> Columns<C>.takeLastWhile(predicate: Predicate<ColumnWithPath<C>>): Columns<C> = transform { it.takeLastWhile(predicate) }
    public fun <C> Columns<C>.filter(predicate: Predicate<ColumnWithPath<C>>): Columns<C> = transform { it.filter(predicate) }

    public fun Columns<*>.numberCols(filter: (NumberCol) -> Boolean = { true }): Columns<Number?> = colsOf(filter)
    public fun Columns<*>.stringCols(filter: (StringCol) -> Boolean = { true }): Columns<String?> = colsOf(filter)
    public fun Columns<*>.intCols(filter: (IntCol) -> Boolean = { true }): Columns<Int?> = colsOf(filter)
    public fun Columns<*>.doubleCols(filter: (DoubleCol) -> Boolean = { true }): Columns<Double?> = colsOf(filter)
    public fun Columns<*>.booleanCols(filter: (BooleanCol) -> Boolean = { true }): Columns<Boolean?> = colsOf(filter)

    public fun Columns<*>.nameContains(text: CharSequence): Columns<Any?> = cols { it.name.contains(text) }
    public fun Columns<*>.nameContains(regex: Regex): Columns<Any?> = cols { it.name.contains(regex) }
    public fun Columns<*>.startsWith(prefix: CharSequence): Columns<Any?> = cols { it.name.startsWith(prefix) }
    public fun Columns<*>.endsWith(suffix: CharSequence): Columns<Any?> = cols { it.name.endsWith(suffix) }

    public infix fun <C> Columns<C>.and(other: Columns<C>): Columns<C> = ColumnsList(this, other)

    public fun <C> Columns<C>.except(vararg other: Columns<*>): Columns<*> = except(other.toColumns())
    public fun <C> Columns<C>.except(vararg other: String): Columns<*> = except(other.toColumns())

    public fun <C> Columns<C?>.withoutNulls(): Columns<C> = transform { it.filter { !it.hasNulls } } as Columns<C>

    public infix fun <C> Columns<C>.except(other: Columns<*>): Columns<*> =
        createColumnSet { resolve(it).allColumnsExcept(other.resolve(it)) }

    public infix fun <C> Columns<C>.except(selector: ColumnsSelector<T, *>): Columns<C> = except(selector.toColumns()) as Columns<C>

    // public operator fun <C> ColumnSelector<T, C>.invoke(): ColumnReference<C> = this(this@SelectReceiver, this@SelectReceiver)
    public operator fun <C> ColumnsSelector<T, C>.invoke(): Columns<C> = this(this@ColumnSelectionDsl, this@ColumnSelectionDsl)

    public operator fun <C> ColumnReference<C>.invoke(): DataColumn<C> = getColumn(this)

    public operator fun <C> ColumnReference<C>.invoke(newName: String): ColumnReference<C> = renamedReference(newName)
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    public fun ColumnReference<String?>.length(): ColumnReference<Int?> = map { it?.length }
    public fun ColumnReference<String?>.lowercase(): ColumnReference<String?> = map { it?.lowercase() }
    public fun ColumnReference<String?>.uppercase(): ColumnReference<String?> = map { it?.uppercase() }

    public infix fun String.and(other: String): Columns<Any?> = toColumnAccessor() and other.toColumnAccessor()
    public infix fun <C> String.and(other: Columns<C>): Columns<Any?> = toColumnAccessor() and other
    public infix fun <C> KProperty<C>.and(other: Columns<C>): Columns<C> = toColumnAccessor() and other
    public infix fun <C> Columns<C>.and(other: KProperty<C>): Columns<C> = this and other.toColumnAccessor()
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): Columns<C> = toColumnAccessor() and other.toColumnAccessor()
    public infix fun <C> Columns<C>.and(other: String): Columns<Any?> = this and other.toColumnAccessor()

    public operator fun ColumnPath.invoke(): ColumnAccessor<Any?> = toColumnAccessor()

    public operator fun <C> String.invoke(newColumnExpression: RowSelector<T, C>): DataColumn<C> = newColumnWithActualType(this, newColumnExpression)

    public infix fun <C> String.by(newColumnExpression: RowSelector<T, C>): DataColumn<C> = newColumnWithActualType(this, newColumnExpression)

    public fun String.ints(): DataColumn<Int> = getColumn(this)
    public fun String.intOrNulls(): DataColumn<Int?> = getColumn(this)
    public fun String.strings(): DataColumn<String> = getColumn(this)
    public fun String.stringOrNulls(): DataColumn<String?> = getColumn(this)
    public fun String.booleans(): DataColumn<Boolean> = getColumn(this)
    public fun String.booleanOrNulls(): DataColumn<Boolean?> = getColumn(this)
    public fun String.doubles(): DataColumn<Double> = getColumn(this)
    public fun String.doubleOrNulls(): DataColumn<Double?> = getColumn(this)
    public fun String.comparables(): DataColumn<Comparable<Any?>> = getColumn(this)
    public fun String.comparableOrNulls(): DataColumn<Comparable<Any?>?> = getColumn(this)
    public fun String.numberOrNulls(): DataColumn<Number?> = getColumn(this)

    public fun ColumnPath.ints(): DataColumn<Int> = getColumn(this)
    public fun ColumnPath.intOrNulls(): DataColumn<Int?> = getColumn(this)
    public fun ColumnPath.strings(): DataColumn<String> = getColumn(this)
    public fun ColumnPath.stringOrNulls(): DataColumn<String?> = getColumn(this)
    public fun ColumnPath.booleans(): DataColumn<Boolean> = getColumn(this)
    public fun ColumnPath.booleanOrNulls(): DataColumn<Boolean?> = getColumn(this)
    public fun ColumnPath.doubles(): DataColumn<Double> = getColumn(this)
    public fun ColumnPath.doubleOrNulls(): DataColumn<Double?> = getColumn(this)
    public fun ColumnPath.comparables(): DataColumn<Comparable<Any?>> = getColumn(this)
    public fun ColumnPath.comparableOrNulls(): DataColumn<Comparable<Any?>?> = getColumn(this)
    public fun ColumnPath.numberOrNulls(): DataColumn<Number?> = getColumn(this)
}

public inline fun <T, reified R> ColumnSelectionDsl<T>.expr(
    name: String = "",
    useActualType: Boolean = false,
    noinline expression: AddExpression<T, R>
): DataColumn<R> = newColumn(name, useActualType, expression)

internal fun <T, R> ColumnSelectionDsl<T>.exprWithActualType(name: String = "", expression: AddExpression<T, R>): DataColumn<R> = newColumnWithActualType(name, expression)

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> = { this@filter(it, it).filter(predicate) }
// internal fun Columns<*>.filter(predicate: (AnyCol) -> Boolean) = transform { it.filter { predicate(it.data) } }

internal fun Columns<*>.colsInternal(predicate: (AnyCol) -> Boolean) = transform { it.flatMap { it.children().filter { predicate(it.data) } } }
internal fun Columns<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean) = transform { it.filter { it.isColumnGroup() }.flatMap { it.children().dfs().filter(predicate) } }

public fun <C> Columns<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): Columns<*> = dfsInternal { it.data.hasElementsOfType(type) && predicate(it.typed()) }
public inline fun <reified C> Columns<*>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }): Columns<C> = dfsOf(
    getType<C>(),
    filter
) as Columns<C>

public fun Columns<*>.colsOf(type: KType): Columns<Any?> = colsOf(type) { true }

public fun <C> Columns<*>.colsOf(type: KType, filter: (DataColumn<C>) -> Boolean): Columns<C> = colsInternal { it.hasElementsOfType(type) && filter(it.typed()) } as Columns<C>
public inline fun <reified C> Columns<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): Columns<C> = colsOf(
    getType<C>(), filter
)
