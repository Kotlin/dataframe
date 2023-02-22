package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.api.createDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.index
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

// region read DataFrame from objects

public inline fun <reified T> Iterable<T>.toDataFrame(): DataFrame<T> = toDataFrame {
    properties()
}

public inline fun <reified T> Iterable<T>.toDataFrame(noinline body: CreateDataFrameDsl<T>.() -> Unit): DataFrame<T> =
    createDataFrameImpl(T::class, body)

public inline fun <reified T> Iterable<T>.toDataFrame(vararg props: KProperty<*>, maxDepth: Int = 0): DataFrame<T> =
    toDataFrame {
        properties(roots = props, maxDepth = maxDepth)
    }

@Deprecated("Replaced with `unfold` operation.", ReplaceWith("this.unfold(columns)"), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.read(columns: ColumnsSelector<T, *>): DataFrame<T> = unfold(columns)

@Deprecated("Replaced with `unfold` operation.", ReplaceWith("this.unfold(*columns)"), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.read(vararg columns: String): DataFrame<T> = unfold(*columns)

@Deprecated("Replaced with `unfold` operation.", ReplaceWith("this.unfold(*columns)"), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.read(vararg columns: KProperty<*>): DataFrame<T> = unfold(*columns)

@Deprecated("Replaced with `unfold` operation.", ReplaceWith("this.unfold(*columns)"), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.read(vararg columns: Column): DataFrame<T> = unfold(*columns)

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
public fun Iterable<AnyBaseCol>.toDataFrame(): AnyFrame = dataFrameOf(this)

@JvmName("toDataFramePairColumnPathAnyCol")
public fun <T> Iterable<Pair<ColumnPath, AnyBaseCol>>.toDataFrameFromPairs(): DataFrame<T> {
    val nameGenerator = ColumnNameGenerator()
    val columnNames = mutableListOf<String>()
    val columnGroups = mutableListOf<MutableList<Pair<ColumnPath, AnyBaseCol>>?>()
    val columns = mutableListOf<AnyBaseCol?>()
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
            val nestedDf = group.toDataFrameFromPairs<Unit>()
            val col = DataColumn.createColumnGroup(columnNames[index], nestedDf)
            assert(columns[index] == null)
            columns[index] = col
        } else assert(columns[index] != null)
    }
    return columns.map { it!! }.toDataFrame().cast()
}

@JvmName("toDataFrameColumnPathAnyNullable")
public fun Iterable<Pair<ColumnPath, Iterable<Any?>>>.toDataFrameFromPairs(): AnyFrame {
    return map { it.first to guessColumnType(it.first.last(), it.second.asList()) }.toDataFrameFromPairs<Unit>()
}

public fun Iterable<Pair<String, Iterable<Any?>>>.toDataFrameFromPairs(): AnyFrame {
    return map { ColumnPath(it.first) to guessColumnType(it.first, it.second.asList()) }.toDataFrameFromPairs<Unit>()
}

public interface TraversePropertiesDsl {

    /**
     * Skip given [classes] during dfs traversal
     */
    public fun exclude(vararg classes: KClass<*>)

    /**
     * Skip given [properties] during dfs traversal
     */
    public fun exclude(vararg properties: KProperty<*>)

    /**
     * Store given [classes] in ValueColumns without transformation into ColumnGroups or FrameColumns
     */
    public fun preserve(vararg classes: KClass<*>)

    /**
     * Store given [properties] in ValueColumns without transformation into ColumnGroups or FrameColumns
     */
    public fun preserve(vararg properties: KProperty<*>)
}

public inline fun <reified T> TraversePropertiesDsl.preserve(): Unit = preserve(T::class)

public abstract class CreateDataFrameDsl<T> : TraversePropertiesDsl {

    public abstract val source: Iterable<T>

    public abstract fun add(column: AnyBaseCol, path: ColumnPath? = null)

    public infix fun AnyBaseCol.into(name: String): Unit = add(this, pathOf(name))

    public infix fun AnyBaseCol.into(path: ColumnPath): Unit = add(this, path)

    public abstract fun properties(
        vararg roots: KProperty<*>,
        maxDepth: Int = 0,
        body: (TraversePropertiesDsl.() -> Unit)? = null
    )

    public inline fun <reified R> expr(noinline expression: (T) -> R): DataColumn<R> =
        source.map { expression(it) }.toColumn()

    public inline fun <reified R> add(name: String, noinline expression: (T) -> R): Unit =
        add(source.map { expression(it) }.toColumn(name, Infer.Nulls))

    public inline infix fun <reified R> String.from(noinline expression: (T) -> R): Unit =
        add(this, expression)

    public inline infix fun <reified R> KProperty<R>.from(noinline expression: (T) -> R): Unit =
        add(columnName, expression)

    public inline infix fun <reified R> KProperty<R>.from(inferType: InferType<T, R>): Unit =
        add(DataColumn.createWithTypeInference(columnName, source.map { inferType.expression(it) }))

    public data class InferType<T, R>(val expression: (T) -> R)

    public inline fun <reified R> inferType(noinline expression: (T) -> R): InferType<T, R> = InferType(expression)

    public abstract operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit)
}

// endregion

// region Create DataFrame from Map

public fun Map<String, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map { DataColumn.createWithTypeInference(it.key, it.value.asList()) }.toDataFrame()
}

@JvmName("toDataFrameColumnPathAnyNullable")
public fun Map<ColumnPath, Iterable<Any?>>.toDataFrame(): AnyFrame {
    return map {
        it.key to DataColumn.createWithTypeInference(
            it.key.last(),
            it.value.asList()
        )
    }.toDataFrameFromPairs<Unit>()
}

// endregion
