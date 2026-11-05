package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.api.createDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.TRAVERSE_PROPERTIES_DSL
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region docs

// holds the common `toDataFrame` / `toDataFrameFromPairs` KDoc-snippets;
// excluded from the sources with everything nested in it, as the snippets are only ever `@include`d
@ExcludeFromSources
internal interface ToDataFrameDocs {

    /**
     * {@comment How an [Iterable\] of objects turns into a [DataFrame\]. KDoc-snippet.}
     *
     * Every element of the iterable becomes a row of the new [DataFrame].
     *
     * How the property values are stored depends on their type:
     * - a value of a simple type — a number, a string, a boolean, a char, an enum, an array, a map,
     *   a date-time type, ... — is stored as it is;
     * - a nested object becomes a [ColumnGroup] with a column per property,
     *   and a list of nested objects becomes a [FrameColumn] with a [DataFrame] per value —
     *   as deep as `maxDepth` allows;
     * - a list of simple values stays a single value column with the values as a [List];
     * - a property of a [DataSchema] type, a [DataRow] or a [DataFrame] is converted at any depth:
     *   a [DataSchema] value and a [DataRow] become a [ColumnGroup],
     *   a list of [DataSchema] values and a [DataFrame] become a [FrameColumn].
     *
     * A property getter that throws stores the exception it threw in the cell instead of a value,
     * and the column type is then inferred from the values.
     * When the elements themselves cannot be unfolded — they are of a simple type,
     * or of a class without public properties — the result is a single `value` column
     * that stores them as they are.
     */
    typealias FromObjectsSnippet = Nothing

    /**
     * {@comment Where the columns come from when no [CreateDataFrameDsl\] adds them. KDoc-snippet.}
     *
     * The columns are the public properties of the elements;
     * a class without properties contributes its getter-like functions instead, such as `getX()`.
     * The columns are named after the properties, in the order of the primary constructor's parameters,
     * with the remaining properties sorted by name.
     * When there is no constructor to read parameter names from — in Java classes, for example —
     * all columns are sorted by name.
     */
    typealias PropertiesSnippet = Nothing

    /**
     * {@comment How the `maxDepth` parameter works. KDoc-snippet.}
     *
     * With `maxDepth` = 0, the values of the properties stay as they are:
     * a nested object stays an object, and a list stays a list.
     * With `maxDepth` = 1, nested objects become [ColumnGroup]s, and lists of objects [FrameColumn]s;
     * every further step goes one level deeper.
     * Properties of a [DataSchema] type, [DataRow]s and [DataFrame]s are converted at any depth.
     * @comment The near-duplicate of [UnfoldDocs.MaxDepthSnippet\] is conscious: the same conversion
     *    machinery, but the wording stays local so the `toDataFrame` and `unfold` families can drift apart.
     */
    typealias MaxDepthSnippet = Nothing

    /**
     * {@comment What all `toDataFrameFromPairs` overloads build. KDoc-snippet.}
     *
     * A pair with a path of one segment becomes a top-level column.
     * A longer path nests the column in [ColumnGroup]s, one group per path segment
     * except the last, which names the column.
     * Duplicate names are made unique at every level: a column named `a` that comes after another `a`
     * is renamed to `a1`. When a column and a group would share a name,
     * the one that comes later in the pairs is renamed.
     */
    typealias FromPairsSnippet = Nothing

    /**
     * {@comment See also section of the `toDataFrame` overloads. KDoc-snippet.}
     *
     * See also:
     * - [dataFrameOf] — creates a [DataFrame] from columns, or from name and value pairs.
     * - [toDataFrameFromPairs] — the nesting-aware builder behind this operation,
     *   for explicit column paths.
     * - [unfold][DataColumn.unfold] — the column-side counterpart: unfolds a column of objects
     *   into a [ColumnGroup].
     */
    typealias SeeAlsoSnippet = Nothing

    /**
     * @param [maxDepth\] How deep to convert the values of the properties; `0` by default.
     * @comment The `maxDepth` parameter. KDoc-snippet; it sits at the end,
     *    a leading `{@comment}` would leave two blank lines inside the `@param` list.
     */
    typealias MaxDepthParamSnippet = Nothing
}

// endregion

// region read DataFrame from objects

/**
 * Creates a [DataFrame] from this [Iterable] of objects by reading their public properties.
 *
 * @include [ToDataFrameDocs.PropertiesSnippet]
 *
 * @include [ToDataFrameDocs.FromObjectsSnippet]
 *
 * The values of the properties stay as they are.
 * To convert nested values as well, use the overload with a `maxDepth` parameter,
 * and to add computed columns, the one with a [CreateDataFrameDsl] lambda.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 *
 * @include [ToDataFrameDocs.SeeAlsoSnippet]
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameFromObjects]
 *
 * @param [T] The type of the elements; the schema marker of the result.
 * @return The new [DataFrame].
 */
@Refine
@Interpretable("toDataFrameDefault")
public inline fun <reified T> Iterable<T>.toDataFrame(): DataFrame<T> =
    toDataFrame {
        properties()
    }

/**
 * Creates a [DataFrame] from this [Iterable] of objects with the [CreateDataFrameDsl]:
 * every element becomes a row, and [body] decides the columns —
 * the properties of the elements via [properties][CreateDataFrameDsl.properties],
 * computed columns and column groups via the other members of [CreateDataFrameDsl].
 *
 * @include [ToDataFrameDocs.FromObjectsSnippet]
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 *
 * @include [ToDataFrameDocs.SeeAlsoSnippet]
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameWithDsl]
 *
 * @param [T] The type of the elements; the schema marker of the result.
 * @param [body] The [CreateDataFrameDsl] lambda that adds the columns.
 * @return The new [DataFrame].
 */
@Refine
@Interpretable("toDataFrameDsl")
public inline fun <reified T> Iterable<T>.toDataFrame(noinline body: CreateDataFrameDsl<T>.() -> Unit): DataFrame<T> =
    createDataFrameImpl(typeOf<T>(), body)

/**
 * Creates a [DataFrame] from this [Iterable] of objects, with a column per property in [props],
 * or for all public properties of the elements when [props] is empty.
 *
 * @include [ToDataFrameDocs.PropertiesSnippet]
 *
 * @include [ToDataFrameDocs.FromObjectsSnippet]
 *
 * @include [ToDataFrameDocs.MaxDepthSnippet]
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 *
 * @include [ToDataFrameDocs.SeeAlsoSnippet]
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameWithMaxDepth]
 *
 * @include [ToDataFrameDocs.MaxDepthParamSnippet]
 * @param [T] The type of the elements; the schema marker of the result.
 * @param [props] The properties to make columns from; all public properties of the elements by default.
 * @return The new [DataFrame].
 */
@Refine
@Interpretable("toDataFrame")
public inline fun <reified T> Iterable<T>.toDataFrame(vararg props: KProperty<*>, maxDepth: Int = 0): DataFrame<T> =
    toDataFrame {
        properties(roots = props, maxDepth = maxDepth)
    }

/**
 * Creates a [DataFrame] from this [Iterable] of [DataRow]s.
 *
 * When all rows come from the same [DataFrame], the result is that [DataFrame] with its rows
 * in the order of this iterable. Otherwise, the rows are concatenated in the order of this
 * iterable: the columns of the result are the union of the columns of the rows' frames,
 * and a row from a frame without one of the columns gets `null` in it.
 *
 * @return The new [DataFrame].
 */
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

/**
 * Creates a [DataFrame] from this [Iterable] of maps, treating every map as a row:
 * every distinct key becomes a column, in the order the keys are first seen.
 * A map without one of the keys gets `null` in the key's column.
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameFromMaps]
 *
 * @return The new [DataFrame].
 */
@JvmName("toDataFrameMapStringAnyNullable")
public fun Iterable<Map<String, Any?>>.toDataFrame(): DataFrame<*> {
    val list = asList()
    if (list.isEmpty()) return DataFrame.empty()

    val allKeys = linkedSetOf<String>()
    for (row in this) {
        allKeys.addAll(row.keys)
    }

    val columns = allKeys.map { key ->
        val values = ArrayList<Any?>(list.size)
        for (row in this) {
            values.add(row[key])
        }
        DataColumn.createByInference(key, values)
    }

    return columns.toDataFrame()
}

/**
 * Creates a [DataFrame] from this [Iterable] of columns: the columns become the columns of the
 * result, keeping their names and values.
 *
 * @return The new [DataFrame].
 */
@JvmName("toDataFrameAnyColumn")
public fun Iterable<BaseColumn<*>>.toDataFrame(): DataFrame<*> = dataFrameOf(this)

/**
 * Creates a [DataFrame] from this [Iterable] of column paths to columns.
 *
 * @include [ToDataFrameDocs.FromPairsSnippet]
 *
 * See also the [overload][toDataFrameFromPairs] for an [Iterable] of paths to values,
 * which infers the column types.
 *
 * @return The new [DataFrame].
 */
@JvmName("toDataFramePairColumnPathAnyCol")
public fun <T> Iterable<Pair<ColumnPath, BaseColumn<*>>>.toDataFrameFromPairs(): DataFrame<T> {
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

/**
 * Creates a [DataFrame] from this [Iterable] of column paths to values,
 * inferring the type of each column from its values.
 *
 * @include [ToDataFrameDocs.FromPairsSnippet]
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameFromPairs]
 *
 * @return The new [DataFrame].
 */
@JvmName("toDataFrameColumnPathAnyNullable")
public fun Iterable<Pair<ColumnPath, Iterable<Any?>>>.toDataFrameFromPairs(): DataFrame<*> =
    map {
        it.first to createColumnGuessingType(it.first.last(), it.second.asList())
    }.toDataFrameFromPairs<Unit>()

/**
 * Creates a [DataFrame] from this [Iterable] of column names to values,
 * inferring the type of each column from its values.
 *
 * @include [ToDataFrameDocs.FromPairsSnippet]
 *
 * See also the [overload][toDataFrameFromPairs] for an [Iterable] of [ColumnPath]es to values,
 * which can nest the columns in [ColumnGroup]s.
 *
 * @return The new [DataFrame].
 */
public fun Iterable<Pair<String, Iterable<Any?>>>.toDataFrameFromPairs(): DataFrame<*> =
    map {
        ColumnPath(it.first) to createColumnGuessingType(it.first, it.second.asList())
    }.toDataFrameFromPairs<Unit>()

/**
 * [DslMarker] to prevent functions from [CreateDataFrameDsl] being used inside [TraversePropertiesDsl].
 * This prevents notations like:
 * ```kt
 * list.toDataFrame {
 *     "colA" from { ... }
 *     properties {
 *         preserve<T>()
 *         "colB" from { ... } // ERROR, cannot be called in this context an implicit receiver.
 *     }
 * }
 * ```
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class CreateDataFrameDslMarker

/**
 * The DSL that configures how [properties][CreateDataFrameDsl.properties] traverse the object graph
 * of the elements: which classes and properties to skip ([exclude][TraversePropertiesDsl.exclude]),
 * and which to store as they are ([preserve][TraversePropertiesDsl.preserve]).
 *
 * It is available only inside the `body` of [properties][CreateDataFrameDsl.properties]:
 * there is no [TraversePropertiesDsl] receiver outside it,
 * and the [CreateDataFrameDslMarker] additionally hides the members of the outer [CreateDataFrameDsl]
 * inside the `body`.
 */
@CreateDataFrameDslMarker
public interface TraversePropertiesDsl {

    /**
     * Skip given [classes] during recursive (dfs) traversal:
     * a property of one of the [classes] is not read, wherever it appears in the object graph.
     *
     * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
     */
    @Interpretable("Exclude0")
    public fun exclude(vararg classes: KClass<*>)

    /**
     * Skip given [properties] during recursive (dfs) traversal, wherever they appear in the object graph.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     *
     * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
     */
    @Interpretable("Exclude1")
    public fun exclude(vararg properties: KCallable<*>)

    /**
     * Store given [classes] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     *
     * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
     */
    @Interpretable("Preserve0")
    public fun preserve(vararg classes: KClass<*>)

    /**
     * Store given [properties] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     *
     * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
     */
    @Interpretable("Preserve1")
    public fun preserve(vararg properties: KCallable<*>)
}

/**
 * Store values of the given type [T] in ValueColumns without transformation into ColumnGroups or FrameColumns.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 */
@Interpretable("PreserveT")
public inline fun <reified T> TraversePropertiesDsl.preserve(): Unit = preserve(T::class)

@Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
public inline fun <reified T> CreateDataFrameDsl<*>.preserve(): Unit = Unit

/**
 * The DSL that [toDataFrame][Iterable.toDataFrame] uses to build a [DataFrame] from an [Iterable]
 * of objects: every element becomes a row, and the members of this DSL add the columns —
 * [properties] for a column per property of the elements, [from][String.from] and [add] for
 * computed columns, [String.invoke] for column groups.
 *
 * Every column lands at a path, and paths nest as in [toDataFrameFromPairs].
 */
@CreateDataFrameDslMarker
public abstract class CreateDataFrameDsl<T> {

    /** The [Iterable] the [DataFrame] is built from: the objects, one per row. */
    public abstract val source: Iterable<T>

    /**
     * Adds [column] to the [DataFrame] being built, at [path].
     * Without [path], the column lands at its own name.
     *
     * @param [column] The column to add.
     * @param [path] The path to put the column at; the column is renamed to the last path segment.
     */
    public abstract fun add(column: BaseColumn<*>, path: ColumnPath? = null)

    /**
     * Puts this column into the [DataFrame] being built, under [name].
     */
    @Interpretable("ToDataFrameDslIntoString")
    public infix fun BaseColumn<*>.into(name: String): Unit = add(this, pathOf(name))

    /**
     * Puts this column into the [DataFrame] being built, at [path];
     * a path of more than one segment nests the column in [ColumnGroup]s.
     */
    @Interpretable("ToDataFrameDslIntoPath")
    public infix fun BaseColumn<*>.into(path: ColumnPath): Unit = add(this, path)

    /**
     * Adds a column per property of the elements.
     *
     * @include [ToDataFrameDocs.MaxDepthSnippet]
     *
     * @param [roots] The properties, or getter-like functions such as `getX()`, to make columns from;
     * all public properties of the elements by default.
     * @param [maxDepth] How deep to convert the values of the properties.
     * @param [body] The [TraversePropertiesDsl] lambda that excludes or preserves
     * classes and properties during the traversal.
     */
    @Interpretable("Properties0")
    public abstract fun properties(
        vararg roots: KCallable<*>,
        maxDepth: Int = 0,
        body: (TraversePropertiesDsl.() -> Unit)? = null,
    )

    @Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
    public fun exclude(vararg classes: KClass<*>): Unit = Unit

    @Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
    public fun exclude(vararg properties: KCallable<*>): Unit = Unit

    @Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
    public fun preserve(vararg classes: KClass<*>): Unit = Unit

    @Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
    public fun preserve(vararg properties: KCallable<*>): Unit = Unit

    /**
     * Creates a [DataColumn] with the values [expression] returns for each element,
     * without adding it to the [DataFrame] being built — combine it with [into].
     *
     * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
     * @param [expression] The expression to compute each value from an element.
     */
    public inline fun <reified R> expr(infer: Infer = Infer.Nulls, noinline expression: (T) -> R): DataColumn<R> =
        source.map { expression(it) }.toColumn(infer = infer)

    /**
     * Adds a column named [name] with the values [expression] returns for each element.
     */
    @Interpretable("ToDataFrameDslAdd")
    public inline fun <reified R> add(name: String, noinline expression: (T) -> R): Unit =
        add(source.map { expression(it) }.toColumn(name, Infer.Nulls))

    /**
     * Adds a column named this string with the values [expression] returns for each element.
     */
    @Interpretable("ToDataFrameFrom0")
    public inline infix fun <reified R> String.from(noinline expression: (T) -> R): Unit = add(this, expression)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(noinline expression: (T) -> R): Unit =
        add(columnName, expression)

    /**
     * Adds a column named this string with the values of [inferType]'s expression,
     * with the column type inferred from the values.
     */
    @Interpretable("ToDataFrameDslFromInferType")
    public inline infix fun <reified R> String.from(inferType: InferType<T, R>): Unit =
        add(DataColumn.createByInference(this, source.map { inferType.expression(it) }))

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(inferType: InferType<T, R>): Unit =
        add(DataColumn.createByInference(columnName, source.map { inferType.expression(it) }))

    /**
     * Wraps an expression so that [from][String.from] creates the column
     * with the type inferred from the values it returns.
     */
    public data class InferType<T, R>(val expression: (T) -> R)

    /**
     * Creates an [InferType] for [from][String.from] from [expression],
     * so that the column type is inferred from the values.
     */
    public inline fun <reified R> inferType(noinline expression: (T) -> R): InferType<T, R> = InferType(expression)

    /**
     * Creates a [ColumnGroup] named this string: every column added inside [builder]
     * lands in the group.
     */
    @Interpretable("ToDataFrameDslStringInvoke")
    public abstract operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit)
}

/**
 * Creates a [DataFrame] from this [Iterable] with a single column named [columnName]:
 * every element becomes one cell of the column.
 *
 * A way to start from values you want to take apart step by step — files, URLs, ... —
 * and to add columns extracted from them later.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameWithColumnName]
 *
 * @param [T] The type of the elements.
 * @param [columnName] The name of the single column.
 * @return The new [DataFrame].
 */
@Refine
@Interpretable("ToDataFrameColumn")
public inline fun <reified T> Iterable<T>.toDataFrame(columnName: String): DataFrame<*> =
    toDataFrame {
        columnName from { it }
    }

// endregion

// region toDataFrame overloads for built-in types

/**
 * A [DataSchema] for the `value` column: the single column that [toDataFrame][Iterable.toDataFrame]
 * creates when the elements of the [Iterable] are stored as they are — they are of a simple type,
 * or of a class without public properties.
 *
 * Cast the resulting [DataFrame] to `DataFrame<ValueProperty<T>>` to access the `value` column
 * type-safely, for example to add computed columns to it.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 */
@DataSchema
public interface ValueProperty<T> {
    public val value: T
}

// endregion

// region Create DataFrame from Map

/**
 * Creates a [DataFrame] with a column per entry of this [Map]:
 * the column is named after the key and takes the values of the iterable,
 * with the type inferred from the values.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromIterable]}
 *
 * ### Example
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ToDataFrameSamples.toDataFrameFromMapOfColumns]
 *
 * @return The new [DataFrame].
 */
public fun Map<String, Iterable<Any?>>.toDataFrame(): DataFrame<*> =
    map {
        DataColumn.createByInference(it.key, it.value.asList())
    }.toDataFrame()

/**
 * Creates a [DataFrame] with a column per entry of this [Map], like [toDataFrame][Map.toDataFrame],
 * but a key that is a [ColumnPath] of more than one segment nests the column in [ColumnGroup]s,
 * one group per path segment except the last, which names the column.
 *
 * @return The new [DataFrame].
 */
@JvmName("toDataFrameColumnPathAnyNullable")
public fun Map<ColumnPath, Iterable<Any?>>.toDataFrame(): DataFrame<*> =
    map {
        it.key to DataColumn.createByInference(
            name = it.key.last(),
            values = it.value.asList(),
        )
    }.toDataFrameFromPairs<Unit>()

// endregion

/**
 * Converts a list of lists into a [DataFrame].
 *
 * By default, treats lists as row values. If [header] is not provided, the first inner list becomes a header (column names), and the remaining lists are treated as data.
 *
 * With [containsColumns] = `true`, interprets each inner list as a column.
 * If [header] is not provided, the first element will be used as the column name, and the remaining elements as values.
 *
 * For more information: {@include [DocumentationUrls.CreateDataFrameFromListOfLists]}
 *
 * @param T The type of elements contained in the nested lists.
 * @param containsColumns If `true`, treats each nested list as a column.
 *                        Otherwise, each nested list is a row.
 *                        Defaults to `false`.
 * @param header overrides extraction of column names from lists - all values are treated as data instead.
 * @return A [DataFrame] containing the data from the nested list structure.
 *         Returns an empty [DataFrame] if the input is empty or invalid.
 */
@Refine
@Interpretable("ValuesListsToDataFrame")
public fun <T> List<List<T>>.toDataFrame(header: List<String>?, containsColumns: Boolean = false): DataFrame<*> =
    when {
        containsColumns -> {
            mapIndexedNotNull { index, list ->
                if (list.isEmpty()) return@mapIndexedNotNull null
                val name = header?.get(index) ?: list[0].toString()
                val values = if (header == null) list.drop(1) else list
                createColumnGuessingType(name, values)
            }.toDataFrame()
        }

        isEmpty() -> DataFrame.Empty

        else -> {
            val data = if (header == null) drop(1) else this
            (header ?: get(0).map { it.toString() }).mapIndexed { colIndex, name ->
                val values = data.map { row ->
                    if (row.size <= colIndex) {
                        null
                    } else {
                        row[colIndex]
                    }
                }
                createColumnGuessingType(name, values)
            }.toDataFrame()
        }
    }
