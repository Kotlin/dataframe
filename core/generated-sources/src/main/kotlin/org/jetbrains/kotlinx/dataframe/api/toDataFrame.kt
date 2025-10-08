package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.api.createDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region read DataFrame from objects

@Refine
@Interpretable("toDataFrameDefault")
public inline fun <reified T> Iterable<T>.toDataFrame(): DataFrame<T> =
    toDataFrame {
        properties()
    }

@Refine
@Interpretable("toDataFrameDsl")
public inline fun <reified T> Iterable<T>.toDataFrame(noinline body: CreateDataFrameDsl<T>.() -> Unit): DataFrame<T> =
    createDataFrameImpl(typeOf<T>(), body)

@Refine
@Interpretable("toDataFrame")
public inline fun <reified T> Iterable<T>.toDataFrame(vararg props: KProperty<*>, maxDepth: Int = 0): DataFrame<T> =
    toDataFrame {
        properties(roots = props, maxDepth = maxDepth)
    }

@JvmName("toDataFrameT")
public fun <T> Iterable<DataRow<T>>.toDataFrame(): DataFrame<T> {
    var uniqueDf: DataFrame<T>? = null
    for (row in this) {
        if (uniqueDf == null) {
            uniqueDf = row.df()
        } else {
            if (uniqueDf !== row.df()) {
                uniqueDf = null
                break
            }
        }
    }
    return if (uniqueDf != null) {
        val permutation = map { it.index }
        uniqueDf[permutation]
    } else {
        map { it.toDataFrame() }.concat()
    }
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
        } else {
            assert(columns[index] != null)
        }
    }
    return columns.map { it!! }.toDataFrame().cast()
}

@JvmName("toDataFrameColumnPathAnyNullable")
public fun Iterable<Pair<ColumnPath, Iterable<Any?>>>.toDataFrameFromPairs(): AnyFrame =
    map {
        it.first to createColumnGuessingType(it.first.last(), it.second.asList())
    }.toDataFrameFromPairs<Unit>()

public fun Iterable<Pair<String, Iterable<Any?>>>.toDataFrameFromPairs(): AnyFrame =
    map {
        ColumnPath(it.first) to createColumnGuessingType(it.first, it.second.asList())
    }.toDataFrameFromPairs<Unit>()

public interface TraversePropertiesDsl {

    /**
     * Skip given [classes] during recursive (dfs) traversal.
     */
    @Interpretable("Exclude0")
    public fun exclude(vararg classes: KClass<*>)

    /**
     * Skip given [properties] during recursive (dfs) traversal.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     */
    @Interpretable("Exclude1")
    public fun exclude(vararg properties: KCallable<*>)

    /**
     * Store given [classes] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     */
    @Interpretable("Preserve0")
    public fun preserve(vararg classes: KClass<*>)

    /**
     * Store given [properties] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     */
    @Interpretable("Preserve1")
    public fun preserve(vararg properties: KCallable<*>)
}

/**
 * Store values of given type [T] in ValueColumns without transformation into ColumnGroups or FrameColumns.
 */
@Interpretable("PreserveT")
public inline fun <reified T> TraversePropertiesDsl.preserve(): Unit = preserve(T::class)

public abstract class CreateDataFrameDsl<T> : TraversePropertiesDsl {

    public abstract val source: Iterable<T>

    public abstract fun add(column: AnyBaseCol, path: ColumnPath? = null)

    @Interpretable("ToDataFrameDslIntoString")
    public infix fun AnyBaseCol.into(name: String): Unit = add(this, pathOf(name))

    @Interpretable("ToDataFrameDslIntoPath")
    public infix fun AnyBaseCol.into(path: ColumnPath): Unit = add(this, path)

    @Interpretable("Properties0")
    public abstract fun properties(
        vararg roots: KCallable<*>,
        maxDepth: Int = 0,
        body: (TraversePropertiesDsl.() -> Unit)? = null,
    )

    public inline fun <reified R> expr(infer: Infer = Infer.Nulls, noinline expression: (T) -> R): DataColumn<R> =
        source.map { expression(it) }.toColumn(infer = infer)

    @Interpretable("ToDataFrameDslAdd")
    public inline fun <reified R> add(name: String, noinline expression: (T) -> R): Unit =
        add(source.map { expression(it) }.toColumn(name, Infer.Nulls))

    @Interpretable("ToDataFrameFrom0")
    public inline infix fun <reified R> String.from(noinline expression: (T) -> R): Unit = add(this, expression)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(noinline expression: (T) -> R): Unit =
        add(columnName, expression)

    @Interpretable("ToDataFrameDslFromInferType")
    public inline infix fun <reified R> String.from(inferType: InferType<T, R>): Unit =
        add(DataColumn.createByInference(this, source.map { inferType.expression(it) }))

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(inferType: InferType<T, R>): Unit =
        add(DataColumn.createByInference(columnName, source.map { inferType.expression(it) }))

    public data class InferType<T, R>(val expression: (T) -> R)

    public inline fun <reified R> inferType(noinline expression: (T) -> R): InferType<T, R> = InferType(expression)

    @Interpretable("ToDataFrameDslStringInvoke")
    public abstract operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit)
}

@Refine
@Interpretable("ToDataFrameColumn")
public inline fun <reified T> Iterable<T>.toDataFrame(columnName: String): DataFrame<*> =
    toDataFrame {
        columnName from { it }
    }

// endregion

// region toDataFrame overloads for built-in types

@DataSchema
public interface ValueProperty<T> {
    public val value: T
}

// endregion

// region Create DataFrame from Map

public fun Map<String, Iterable<Any?>>.toDataFrame(): AnyFrame =
    map {
        DataColumn.createByInference(it.key, it.value.asList())
    }.toDataFrame()

@JvmName("toDataFrameColumnPathAnyNullable")
public fun Map<ColumnPath, Iterable<Any?>>.toDataFrame(): AnyFrame =
    map {
        it.key to DataColumn.createByInference(
            name = it.key.last(),
            values = it.value.asList(),
        )
    }.toDataFrameFromPairs<Unit>()

// endregion
