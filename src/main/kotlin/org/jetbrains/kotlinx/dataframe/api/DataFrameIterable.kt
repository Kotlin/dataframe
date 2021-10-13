package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.toDataFrame
import org.jetbrains.kotlinx.dataframe.typed
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

// region Iterable compatibility

public fun <T> DataFrame<T>.asIterable(): Iterable<DataRow<T>> = rows()

public fun <T, R> DataFrame<T>.mapIndexedNotNull(action: (Int, DataRow<T>) -> R?): List<R> = rows().mapIndexedNotNull(action)
public fun <T, R> DataFrame<T>.mapIndexed(action: (Int, DataRow<T>) -> R): List<R> = rows().mapIndexed(action)
public fun <T> DataFrame<T>.single(predicate: RowSelector<T, Boolean>): DataRow<T> = rows().single { predicate(it, it) }
public fun <T> DataFrame<T>.single(): DataRow<T> = rows().single()
public fun <T> DataFrame<T>.any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }
public fun <T> DataFrame<T>.all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }
public fun <T, V> DataFrame<T>.associateBy(transform: RowSelector<T, V>): Map<V, DataRow<T>> = rows().associateBy { transform(it, it) }
public fun <T, K, V> DataFrame<T>.associate(transform: RowSelector<T, Pair<K, V>>): Map<K, V> = rows().associate { transform(it, it) }
public fun <T> DataFrame<T>.shuffled(): DataFrame<T> = getRows((0 until nrow()).shuffled())
public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)
public fun <T> DataFrame<T>.head(numRows: Int = 5): DataFrame<T> = take(numRows)
public fun <T> DataFrame<T>.dropLast(numRows: Int): DataFrame<T> = take(nrow() - numRows)
public fun <T> DataFrame<T>.takeLast(numRows: Int): DataFrame<T> = drop(nrow() - numRows)
public fun <T> DataFrame<T>.drop(numRows: Int): DataFrame<T> = getRows(numRows until nrow())
public fun <T> DataFrame<T>.take(numRows: Int): DataFrame<T> = getRows(0 until numRows)
public fun <T> DataFrame<T>.lastOrNull(predicate: RowFilter<T>): DataRow<T>? = rowsReversed().firstOrNull { predicate(it, it) }
public fun <T> DataFrame<T>.last(predicate: RowFilter<T>): DataRow<T> = rowsReversed().first { predicate(it, it) }
public fun <T> DataFrame<T>.lastOrNull(): DataRow<T>? = if (nrow > 0) last() else null
public fun <T> DataFrame<T>.last(): DataRow<T> = get(nrow - 1)
public fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? = rows().firstOrNull { predicate(it, it) }
public fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> = rows().first { predicate(it, it) }
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null
public fun <T> DataFrame<T>.first(): DataRow<T> = get(0)
public inline fun <T, R> DataFrame<T>.map(selector: RowSelector<T, R>): List<R> = rows().map { selector(it, it) }

// endregion

// region create DataFrame from Iterable

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
        DataColumn.create(it.name, values, property.returnType.withNullability(nullable))
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
    } else map { it.toDataFrame() }.union()
}

public fun Iterable<AnyColumn>.toAnyFrame(): AnyFrame = toDataFrame<Unit>()

@JvmName("toDataFrameAnyColumn")
public fun <T> Iterable<AnyColumn>.toDataFrame(): DataFrame<T> = dataFrameOf(this).typed()

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
            0 -> {}
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
            val col = DataColumn.create(columnNames[index], nestedDf)
            assert(columns[index] == null)
            columns[index] = col
        } else assert(columns[index] != null)
    }
    return columns.map { it!! }.toDataFrame()
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
