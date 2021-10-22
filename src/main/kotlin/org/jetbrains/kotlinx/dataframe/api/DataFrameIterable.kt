package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.VectorizedRowFilter
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.api.SortFlag
import org.jetbrains.kotlinx.dataframe.impl.api.addFlag
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.api.toColumns
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

// region DataFrame Iterable API

public fun <T> DataFrame<T>.asIterable(): Iterable<DataRow<T>> = rows()
public fun <T> DataFrame<T>.asSequence(): Sequence<DataRow<T>> = asIterable().asSequence()

public fun <T> DataFrame<T>.any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }
public fun <T> DataFrame<T>.all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }

public fun <T, V> DataFrame<T>.associateBy(transform: RowSelector<T, V>): Map<V, DataRow<T>> =
    rows().associateBy { transform(it, it) }

public fun <T, K, V> DataFrame<T>.associate(transform: RowSelector<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)
public fun <T> DataFrame<T>.head(numRows: Int = 5): DataFrame<T> = take(numRows)

public fun <T> DataFrame<T>.shuffled(): DataFrame<T> = getRows((0 until nrow()).shuffled())

public fun <T> DataFrame<T>.chunked(size: Int): FrameColumn<T> {
    val startIndices = (0 until nrow() step size)
    return DataColumn.createFrameColumn("", this, startIndices, false)
}

// region isEmpty

public fun AnyFrame.isEmpty(): Boolean = ncol == 0 || nrow == 0
public fun AnyFrame.isNotEmpty(): Boolean = !isEmpty()

// endregion

// region map

public inline fun <T, R> DataFrame<T>.map(selector: RowSelector<T, R>): List<R> = rows().map { selector(it, it) }
public fun <T, R> DataFrame<T>.mapIndexedNotNull(action: (Int, DataRow<T>) -> R?): List<R> =
    rows().mapIndexedNotNull(action)

public fun <T, R> DataFrame<T>.mapIndexed(action: (Int, DataRow<T>) -> R): List<R> = rows().mapIndexed(action)

public fun <T> DataFrame<T>.mapColumns(body: AddDsl<T>.() -> Unit): AnyFrame {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(dsl.columns)
}

// endregion

// region first/last/single

public fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? = rows().firstOrNull { predicate(it, it) }
public fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> = rows().first { predicate(it, it) }
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null
public fun <T> DataFrame<T>.first(): DataRow<T> = get(0)

public fun <T> DataFrame<T>.lastOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rowsReversed().firstOrNull { predicate(it, it) }

public fun <T> DataFrame<T>.last(predicate: RowFilter<T>): DataRow<T> = rowsReversed().first { predicate(it, it) }
public fun <T> DataFrame<T>.lastOrNull(): DataRow<T>? = if (nrow > 0) last() else null
public fun <T> DataFrame<T>.last(): DataRow<T> = get(nrow - 1)

public fun <T> DataFrame<T>.single(predicate: RowSelector<T, Boolean>): DataRow<T> = rows().single { predicate(it, it) }
public fun <T> DataFrame<T>.singleOrNull(predicate: RowSelector<T, Boolean>): DataRow<T>? =
    rows().singleOrNull { predicate(it, it) }

public fun <T> DataFrame<T>.single(): DataRow<T> = rows().single()
public fun <T> DataFrame<T>.singleOrNull(): DataRow<T>? = rows().singleOrNull()

// endregion

// region filter

public fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices.filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

internal fun <T> DataFrame<T>.filterFast(predicate: VectorizedRowFilter<T>) = this[predicate(this)]

// endregion

// region take/drop

public fun <T> DataFrame<T>.dropLast(numRows: Int): DataFrame<T> = take(nrow() - numRows)
public fun <T> DataFrame<T>.takeLast(numRows: Int): DataFrame<T> = drop(nrow() - numRows)
public fun <T> DataFrame<T>.drop(numRows: Int): DataFrame<T> = getRows(numRows until nrow())
public fun <T> DataFrame<T>.take(numRows: Int): DataFrame<T> = getRows(0 until numRows)
public fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

// endregion

// region distinct

public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()
public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> = distinct { columns.toColumns() }
public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumns() }
public fun <T> DataFrame<T>.distinct(vararg columns: Column): DataFrame<T> = distinct { columns.toColumns() }

@JvmName("distinctT")
public fun <T> DataFrame<T>.distinct(columns: Iterable<String>): DataFrame<T> = distinct { columns.toColumns() }
public fun <T> DataFrame<T>.distinct(columns: Iterable<Column>): DataFrame<T> = distinct { columns.toColumnSet() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> = distinctBy { columns.toColumns() }
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumns() }
public fun <T> DataFrame<T>.distinctBy(vararg columns: Column): DataFrame<T> = distinctBy { columns.toColumns() }

@JvmName("distinctByT")
public fun <T> DataFrame<T>.distinctBy(columns: Iterable<String>): DataFrame<T> = distinctBy { columns.toColumns() }
public fun <T> DataFrame<T>.distinctBy(columns: Iterable<Column>): DataFrame<T> = distinctBy { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion

// region forEach

public fun <T> DataFrame<T>.forEach(action: RowSelector<T, Unit>): Unit = rows().forEach { action(it, it) }

public fun <T> DataFrame<T>.forEachIndexed(action: (Int, DataRow<T>) -> Unit): Unit = rows().forEachIndexed(action)

public fun <T, C> DataFrame<T>.forEachIn(
    selector: ColumnsSelector<T, C>,
    action: (DataRow<T>, DataColumn<C>) -> Unit
): Unit =
    getColumnsWithPaths(selector).let { cols ->
        rows().forEach { row ->
            cols.forEach { col ->
                action(row, col.data)
            }
        }
    }

// endregion

// region groupBy

public fun <T> DataFrame<T>.groupBy(cols: ColumnsSelector<T, *>): GroupedDataFrame<T, T> = groupByImpl(cols)
public fun <T> DataFrame<T>.groupBy(cols: Iterable<Column>): GroupedDataFrame<T, T> = groupBy { cols.toColumnSet() }
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: Column): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }

// endregion

// region sort

public interface SortReceiver<out T> : ColumnsSelectionDsl<T> {

    public val <C> ColumnSet<C>.desc: ColumnSet<C> get() = addFlag(SortFlag.Reversed)
    public val String.desc: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().desc
    public val <C> KProperty<C>.desc: ColumnSet<C> get() = toColumnAccessor().desc

    public fun <C> ColumnSet<C?>.nullsLast(flag: Boolean): ColumnSet<C?> =
        if (flag) addFlag(SortFlag.NullsLast) else this

    public val <C> ColumnSet<C?>.nullsLast: ColumnSet<C?> get() = addFlag(SortFlag.NullsLast)
    public val String.nullsLast: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().nullsLast
    public val <C> KProperty<C?>.nullsLast: ColumnSet<C?> get() = toColumnAccessor().nullsLast
}

public typealias SortColumnsSelector<T, C> = Selector<SortReceiver<T>, ColumnSet<C>>

public fun <T, C> DataFrame<T>.sortBy(selector: SortColumnsSelector<T, C>): DataFrame<T> = sortByImpl(
    UnresolvedColumnsPolicy.Fail, selector
)

public fun <T> DataFrame<T>.sortBy(cols: Iterable<ColumnReference<Comparable<*>?>>): DataFrame<T> =
    sortBy { cols.toColumnSet() }

public fun <T> DataFrame<T>.sortBy(vararg cols: ColumnReference<Comparable<*>?>): DataFrame<T> =
    sortBy { cols.toColumns() }

public fun <T> DataFrame<T>.sortBy(vararg cols: String): DataFrame<T> = sortBy { cols.toColumns() }
public fun <T> DataFrame<T>.sortBy(vararg cols: KProperty<Comparable<*>?>): DataFrame<T> = sortBy { cols.toColumns() }

public fun <T> DataFrame<T>.sortWith(comparator: Comparator<DataRow<T>>): DataFrame<T> {
    val permutation = rows().sortedWith(comparator).map { it.index }
    return this[permutation]
}

public fun <T> DataFrame<T>.sortWith(comparator: (DataRow<T>, DataRow<T>) -> Int): DataFrame<T> =
    sortWith(Comparator(comparator))

public fun <T, C> DataFrame<T>.sortByDesc(selector: SortColumnsSelector<T, C>): DataFrame<T> {
    val set = selector.toColumns()
    return sortByImpl { set.desc }
}

public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumns() }

public fun <T> DataFrame<T>.sortByDesc(vararg columns: String): DataFrame<T> = sortByDesc { columns.toColumns() }
public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: ColumnReference<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumns() }

public fun <T, C> DataFrame<T>.sortByDesc(columns: Iterable<ColumnReference<Comparable<C>?>>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }

// endregion

// endregion

// region Create DataFrame from Iterable

public fun <T> Iterable<T>.toDataFrame(body: IterableDataFrameBuilder<T>.() -> Unit): AnyFrame {
    val builder = IterableDataFrameBuilder(this)
    builder.body()
    return dataFrameOf(builder.columns)
}

public inline fun <reified T> Iterable<T>.toDataFrameByProperties(): AnyFrame = T::class.declaredMembers
    .filter { it.parameters.toList().size == 1 }
    .filter { it is KProperty }
    .map {
        val property = (it as KProperty)
        property.javaField?.isAccessible = true
        var nullable = false
        val values = this.map { obj ->
            if (obj == null) {
                nullable = true
                null
            } else {
                val value = it.call(obj)
                if (value == null) nullable = true
                value
            }
        }
        DataColumn.createValueColumn(it.name, values, property.returnType.withNullability(nullable))
    }.let { dataFrameOf(it) }

@JvmName("toDataFrameT")
public fun <T> Iterable<DataRow<T>>.toDataFrame(): DataFrame<T> {
    var uniqueDf: DataFrame<T>? = null
    for (row in this) {
        if (uniqueDf == null) uniqueDf = row.df()
        else {
            if (uniqueDf !== row.df()) {
                uniqueDf = null
                break
            }
        }
    }
    return if (uniqueDf != null) {
        val permutation = map { it.index }
        uniqueDf[permutation]
    } else map { it.toDataFrame() }.concat()
}

@JvmName("toDataFrameAnyColumn")
public fun Iterable<AnyColumn>.toDataFrame(): AnyFrame = dataFrameOf(this)

@JvmName("toDataFramePairColumnPathAnyCol")
public fun <T> Iterable<Pair<ColumnPath, AnyColumn>>.toDataFrame(): DataFrame<T> {
    val nameGenerator = ColumnNameGenerator()
    val columnNames = mutableListOf<String>()
    val columnGroups = mutableListOf<MutableList<Pair<ColumnPath, AnyColumn>>?>()
    val columns = mutableListOf<AnyColumn?>()
    val columnIndices = mutableMapOf<String, Int>()
    val columnGroupName = mutableMapOf<String, String>()

    forEach { (path, col) ->
        when (path.size) {
            0 -> {
            }
            1 -> {
                val name = path[0]
                val uniqueName = nameGenerator.addUnique(name)
                val index = columns.size
                columnNames.add(uniqueName)
                columnGroups.add(null)
                columns.add(col.rename(uniqueName))
                columnIndices[uniqueName] = index
            }
            else -> {
                val name = path[0]
                val uniqueName = columnGroupName.getOrPut(name) {
                    nameGenerator.addUnique(name)
                }
                val index = columnIndices.getOrPut(uniqueName) {
                    columnNames.add(uniqueName)
                    columnGroups.add(mutableListOf())
                    columns.add(null)
                    columns.size - 1
                }
                val list = columnGroups[index]!!
                list.add(path.drop(1) to col)
            }
        }
    }
    columns.indices.forEach { index ->
        val group = columnGroups[index]
        if (group != null) {
            val nestedDf = group.toDataFrame<Unit>()
            val col = DataColumn.createColumnGroup(columnNames[index], nestedDf)
            assert(columns[index] == null)
            columns[index] = col
        } else assert(columns[index] != null)
    }
    return columns.map { it!! }.toDataFrame().typed()
}

@JvmName("toDataFrameColumnPathAny?")
public fun Iterable<Pair<ColumnPath, Iterable<Any?>>>.toDataFrame(): AnyFrame {
    return map { it.first to guessColumnType(it.first.last(), it.second.asList()) }.toDataFrame<Unit>()
}

public fun Iterable<Pair<String, Iterable<Any?>>>.toDataFrame(): AnyFrame {
    return map { ColumnPath(it.first) to guessColumnType(it.first, it.second.asList()) }.toDataFrame<Unit>()
}

public class IterableDataFrameBuilder<T>(public val source: Iterable<T>) {
    internal val columns = mutableListOf<AnyColumn>()

    public fun add(column: AnyColumn): Boolean = columns.add(column)

    public inline fun <reified R> add(name: String, noinline expression: T.(T) -> R?): Boolean =
        add(column(name, source.map { expression(it, it) }))

    public inline infix fun <reified R> String.to(noinline expression: T.(T) -> R?): Boolean = add(this, expression)

    public inline infix operator fun <reified R> String.invoke(noinline expression: T.(T) -> R?): Boolean = add(this, expression)

    public inline infix operator fun <reified R> KProperty<R>.invoke(noinline expression: T.(T) -> R): Boolean = add(name, expression)
}

// endregion

// region Create DataFrame from Map

public fun Map<String, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map { DataColumn.createWithTypeInference(it.key, it.value.asList()) }.toDataFrame()
}

@JvmName("toDataFrameColumnPathAny?")
public fun Map<ColumnPath, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map { it.key to DataColumn.createWithTypeInference(it.key.last(), it.value.asList()) }.toDataFrame<Unit>()
}

// endregion
