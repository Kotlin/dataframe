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

// endregion

// region read DataFrame from objects

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of objects by reading their public properties.
 *
 *
 *
 * The columns are the public properties of the elements;
 * a class without properties contributes its getter-like functions instead, such as `getX()`.
 * The columns are named after the properties, in the order of the primary constructor's parameters,
 * with the remaining properties sorted by name.
 * When there is no constructor to read parameter names from — in Java classes, for example —
 * all columns are sorted by name.
 *
 *
 *
 * Every element of the iterable becomes a row of the new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * How the property values are stored depends on their type:
 * - a value of a simple type — a number, a string, a boolean, a char, an enum, an array, a map,
 *   a date-time type, ... — is stored as it is;
 * - a nested object becomes a [<code>ColumnGroup</code>][ColumnGroup] with a column per property,
 *   and a list of nested objects becomes a [<code>FrameColumn</code>][FrameColumn] with a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] per value —
 *   as deep as `maxDepth` allows;
 * - a list of simple values stays a single value column with the values as a [<code>List</code>][List];
 * - a property of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] or a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] is converted at any depth:
 *   a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] value and a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] become a [<code>ColumnGroup</code>][ColumnGroup],
 *   a list of [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] values and a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] become a [<code>FrameColumn</code>][FrameColumn].
 *
 * A property getter that throws stores the exception it threw in the cell instead of a value,
 * and the column type is then inferred from the values.
 * When the elements themselves cannot be unfolded — they are of a simple type,
 * or of a class without public properties — the result is a single `value` column
 * that stores them as they are.
 *
 * The values of the properties stay as they are.
 * To convert nested values as well, use the overload with a `maxDepth` parameter,
 * and to add computed columns, the one with a [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl] lambda.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 *
 *
 *
 * See also:
 * - [<code>dataFrameOf</code>][org.jetbrains.kotlinx.dataframe.api.dataFrameOf] — creates a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from columns, or from name and value pairs.
 * - [<code>toDataFrameFromPairs</code>][org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs] — the nesting-aware builder behind this operation,
 *   for explicit column paths.
 * - [<code>unfold</code>][org.jetbrains.kotlinx.dataframe.DataColumn.unfold] — the column-side counterpart: unfolds a column of objects
 *   into a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * ### Example
 * ```kotlin
 * data class Person(val name: String, val age: Int)
 *
 * val persons = listOf(Person("Alice", 15), Person("Bob", 20), Person("Charlie", 22))
 *
 * val df = persons.toDataFrame()
 * ```
 *
 * @param [T] The type of the elements; the schema marker of the result.
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("toDataFrameDefault")
public inline fun <reified T> Iterable<T>.toDataFrame(): DataFrame<T> =
    toDataFrame {
        properties()
    }

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of objects with the [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl]:
 * every element becomes a row, and [<code>body</code>][body] decides the columns —
 * the properties of the elements via [<code>properties</code>][CreateDataFrameDsl.properties],
 * computed columns and column groups via the other members of [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl].
 *
 *
 *
 * Every element of the iterable becomes a row of the new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * How the property values are stored depends on their type:
 * - a value of a simple type — a number, a string, a boolean, a char, an enum, an array, a map,
 *   a date-time type, ... — is stored as it is;
 * - a nested object becomes a [<code>ColumnGroup</code>][ColumnGroup] with a column per property,
 *   and a list of nested objects becomes a [<code>FrameColumn</code>][FrameColumn] with a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] per value —
 *   as deep as `maxDepth` allows;
 * - a list of simple values stays a single value column with the values as a [<code>List</code>][List];
 * - a property of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] or a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] is converted at any depth:
 *   a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] value and a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] become a [<code>ColumnGroup</code>][ColumnGroup],
 *   a list of [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] values and a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] become a [<code>FrameColumn</code>][FrameColumn].
 *
 * A property getter that throws stores the exception it threw in the cell instead of a value,
 * and the column type is then inferred from the values.
 * When the elements themselves cannot be unfolded — they are of a simple type,
 * or of a class without public properties — the result is a single `value` column
 * that stores them as they are.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 *
 *
 *
 * See also:
 * - [<code>dataFrameOf</code>][org.jetbrains.kotlinx.dataframe.api.dataFrameOf] — creates a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from columns, or from name and value pairs.
 * - [<code>toDataFrameFromPairs</code>][org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs] — the nesting-aware builder behind this operation,
 *   for explicit column paths.
 * - [<code>unfold</code>][org.jetbrains.kotlinx.dataframe.DataColumn.unfold] — the column-side counterpart: unfolds a column of objects
 *   into a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * ### Example
 * ```kotlin
 * data class Name(val firstName: String, val lastName: String)
 *
 * data class Score(val subject: String, val value: Int)
 *
 * data class Student(val name: Name, val age: Int, val scores: List<Score>)
 *
 * val students = listOf(
 *     Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
 *     Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
 * )
 *
 * val df = students.toDataFrame {
 *     // add column
 *     "year of birth" from { 2021 - it.age }
 *
 *     // scan all properties
 *     properties(maxDepth = 1) {
 *         exclude(Score::subject) // `subject` property will be skipped from object graph traversal
 *         preserve<Name>() // `Name` objects will be stored as-is without transformation into DataFrame
 *     }
 *
 *     // add column group
 *     "summary" {
 *         "max score" from { it.scores.maxOf { it.value } }
 *         "min score" from { it.scores.minOf { it.value } }
 *     }
 * }
 * ```
 *
 * @param [T] The type of the elements; the schema marker of the result.
 * @param [body] The [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl] lambda that adds the columns.
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("toDataFrameDsl")
public inline fun <reified T> Iterable<T>.toDataFrame(noinline body: CreateDataFrameDsl<T>.() -> Unit): DataFrame<T> =
    createDataFrameImpl(typeOf<T>(), body)

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of objects, with a column per property in [<code>props</code>][props],
 * or for all public properties of the elements when [<code>props</code>][props] is empty.
 *
 *
 *
 * The columns are the public properties of the elements;
 * a class without properties contributes its getter-like functions instead, such as `getX()`.
 * The columns are named after the properties, in the order of the primary constructor's parameters,
 * with the remaining properties sorted by name.
 * When there is no constructor to read parameter names from — in Java classes, for example —
 * all columns are sorted by name.
 *
 *
 *
 * Every element of the iterable becomes a row of the new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * How the property values are stored depends on their type:
 * - a value of a simple type — a number, a string, a boolean, a char, an enum, an array, a map,
 *   a date-time type, ... — is stored as it is;
 * - a nested object becomes a [<code>ColumnGroup</code>][ColumnGroup] with a column per property,
 *   and a list of nested objects becomes a [<code>FrameColumn</code>][FrameColumn] with a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] per value —
 *   as deep as `maxDepth` allows;
 * - a list of simple values stays a single value column with the values as a [<code>List</code>][List];
 * - a property of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] or a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] is converted at any depth:
 *   a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] value and a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] become a [<code>ColumnGroup</code>][ColumnGroup],
 *   a list of [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] values and a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] become a [<code>FrameColumn</code>][FrameColumn].
 *
 * A property getter that throws stores the exception it threw in the cell instead of a value,
 * and the column type is then inferred from the values.
 * When the elements themselves cannot be unfolded — they are of a simple type,
 * or of a class without public properties — the result is a single `value` column
 * that stores them as they are.
 *
 *
 *
 * With `maxDepth` = 0, the values of the properties stay as they are:
 * a nested object stays an object, and a list stays a list.
 * With `maxDepth` = 1, nested objects become [<code>ColumnGroup</code>][ColumnGroup]s, and lists of objects [<code>FrameColumn</code>][FrameColumn]s;
 * every further step goes one level deeper.
 * Properties of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s and [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are converted at any depth.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 *
 *
 *
 * See also:
 * - [<code>dataFrameOf</code>][org.jetbrains.kotlinx.dataframe.api.dataFrameOf] — creates a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from columns, or from name and value pairs.
 * - [<code>toDataFrameFromPairs</code>][org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs] — the nesting-aware builder behind this operation,
 *   for explicit column paths.
 * - [<code>unfold</code>][org.jetbrains.kotlinx.dataframe.DataColumn.unfold] — the column-side counterpart: unfolds a column of objects
 *   into a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * ### Example
 * ```kotlin
 * data class Name(val firstName: String, val lastName: String)
 *
 * data class Score(val subject: String, val value: Int)
 *
 * data class Student(val name: Name, val age: Int, val scores: List<Score>)
 *
 * val students = listOf(
 *     Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
 *     Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
 * )
 *
 * val df = students.toDataFrame(maxDepth = 1)
 * ```
 *
 * @param [maxDepth] How deep to convert the values of the properties; `0` by default.
 * @param [T] The type of the elements; the schema marker of the result.
 * @param [props] The properties to make columns from; all public properties of the elements by default.
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("toDataFrame")
public inline fun <reified T> Iterable<T>.toDataFrame(vararg props: KProperty<*>, maxDepth: Int = 0): DataFrame<T> =
    toDataFrame {
        properties(roots = props, maxDepth = maxDepth)
    }

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of [<code>DataRow</code>][DataRow]s.
 *
 * When all rows come from the same [<code>DataFrame</code>][DataFrame], the result is that [<code>DataFrame</code>][DataFrame] with its rows
 * in the order of this iterable. Otherwise, the rows are concatenated in the order of this
 * iterable: the columns of the result are the union of the columns of the rows' frames,
 * and a row from a frame without one of the columns gets `null` in it.
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
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
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of maps, treating every map as a row:
 * every distinct key becomes a column, in the order the keys are first seen.
 * A map without one of the keys gets `null` in the key's column.
 *
 * ### Example
 * ```kotlin
 * val maps = listOf(
 *     mapOf("name" to "Alice", "age" to 15),
 *     mapOf("name" to "Bob", "city" to "Moscow"),
 * )
 *
 * val df = maps.toDataFrame()
 * ```
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
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
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of columns: the columns become the columns of the
 * result, keeping their names and values.
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
@JvmName("toDataFrameAnyColumn")
public fun Iterable<BaseColumn<*>>.toDataFrame(): DataFrame<*> = dataFrameOf(this)

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of column paths to columns.
 *
 *
 *
 * A pair with a path of one segment becomes a top-level column.
 * A longer path nests the column in [<code>ColumnGroup</code>][ColumnGroup]s, one group per path segment
 * except the last, which names the column.
 * Duplicate names are made unique at every level: a column named `a` that comes after another `a`
 * is renamed to `a1`. When a column and a group would share a name,
 * the one that comes later in the pairs is renamed.
 *
 * See also the [<code>overload</code>][toDataFrameFromPairs] for an [<code>Iterable</code>][Iterable] of paths to values,
 * which infers the column types.
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
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
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of column paths to values,
 * inferring the type of each column from its values.
 *
 *
 *
 * A pair with a path of one segment becomes a top-level column.
 * A longer path nests the column in [<code>ColumnGroup</code>][ColumnGroup]s, one group per path segment
 * except the last, which names the column.
 * Duplicate names are made unique at every level: a column named `a` that comes after another `a`
 * is renamed to `a1`. When a column and a group would share a name,
 * the one that comes later in the pairs is renamed.
 *
 * ### Example
 * ```kotlin
 * val df = listOf(
 *     pathOf("name") to listOf("Alice", "Bob"),
 *     pathOf("details", "age") to listOf(15, 20),
 *     pathOf("details", "city") to listOf("London", "Dubai"),
 * ).toDataFrameFromPairs()
 * ```
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
@JvmName("toDataFrameColumnPathAnyNullable")
public fun Iterable<Pair<ColumnPath, Iterable<Any?>>>.toDataFrameFromPairs(): DataFrame<*> =
    map {
        it.first to createColumnGuessingType(it.first.last(), it.second.asList())
    }.toDataFrameFromPairs<Unit>()

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] of column names to values,
 * inferring the type of each column from its values.
 *
 *
 *
 * A pair with a path of one segment becomes a top-level column.
 * A longer path nests the column in [<code>ColumnGroup</code>][ColumnGroup]s, one group per path segment
 * except the last, which names the column.
 * Duplicate names are made unique at every level: a column named `a` that comes after another `a`
 * is renamed to `a1`. When a column and a group would share a name,
 * the one that comes later in the pairs is renamed.
 *
 * See also the [<code>overload</code>][toDataFrameFromPairs] for an [<code>Iterable</code>][Iterable] of [<code>ColumnPath</code>][ColumnPath]es to values,
 * which can nest the columns in [<code>ColumnGroup</code>][ColumnGroup]s.
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
public fun Iterable<Pair<String, Iterable<Any?>>>.toDataFrameFromPairs(): DataFrame<*> =
    map {
        ColumnPath(it.first) to createColumnGuessingType(it.first, it.second.asList())
    }.toDataFrameFromPairs<Unit>()

/**
 * [<code>DslMarker</code>][DslMarker] to prevent functions from [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl] being used inside [<code>TraversePropertiesDsl</code>][TraversePropertiesDsl].
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
 * The DSL that configures how [<code>properties</code>][CreateDataFrameDsl.properties] traverse the object graph
 * of the elements: which classes and properties to skip ([<code>exclude</code>][TraversePropertiesDsl.exclude]),
 * and which to store as they are ([<code>preserve</code>][TraversePropertiesDsl.preserve]).
 *
 * It is available only inside the `body` of [<code>properties</code>][CreateDataFrameDsl.properties]:
 * there is no [<code>TraversePropertiesDsl</code>][TraversePropertiesDsl] receiver outside it,
 * and the [<code>CreateDataFrameDslMarker</code>][CreateDataFrameDslMarker] additionally hides the members of the outer [<code>CreateDataFrameDsl</code>][CreateDataFrameDsl]
 * inside the `body`.
 */
@CreateDataFrameDslMarker
public interface TraversePropertiesDsl {

    /**
     * Skip given [<code>classes</code>][classes] during recursive (dfs) traversal:
     * a property of one of the [<code>classes</code>][classes] is not read, wherever it appears in the object graph.
     *
     * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
     */
    @Interpretable("Exclude0")
    public fun exclude(vararg classes: KClass<*>)

    /**
     * Skip given [<code>properties</code>][properties] during recursive (dfs) traversal, wherever they appear in the object graph.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     *
     * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
     */
    @Interpretable("Exclude1")
    public fun exclude(vararg properties: KCallable<*>)

    /**
     * Store given [<code>classes</code>][classes] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     *
     * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
     */
    @Interpretable("Preserve0")
    public fun preserve(vararg classes: KClass<*>)

    /**
     * Store given [<code>properties</code>][properties] in ValueColumns without transformation into ColumnGroups or FrameColumns.
     * These can also be getter-like functions (like `getX()` or `isX()`).
     *
     * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
     */
    @Interpretable("Preserve1")
    public fun preserve(vararg properties: KCallable<*>)
}

/**
 * Store values of the given type [<code>T</code>][T] in ValueColumns without transformation into ColumnGroups or FrameColumns.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 */
@Interpretable("PreserveT")
public inline fun <reified T> TraversePropertiesDsl.preserve(): Unit = preserve(T::class)

@Deprecated(TRAVERSE_PROPERTIES_DSL, level = DeprecationLevel.ERROR)
public inline fun <reified T> CreateDataFrameDsl<*>.preserve(): Unit = Unit

/**
 * The DSL that [<code>toDataFrame</code>][Iterable.toDataFrame] uses to build a [<code>DataFrame</code>][DataFrame] from an [<code>Iterable</code>][Iterable]
 * of objects: every element becomes a row, and the members of this DSL add the columns —
 * [<code>properties</code>][properties] for a column per property of the elements, [<code>from</code>][String.from] and [<code>add</code>][add] for
 * computed columns, [<code>String.invoke</code>][String.invoke] for column groups.
 *
 * Every column lands at a path, and paths nest as in [<code>toDataFrameFromPairs</code>][toDataFrameFromPairs].
 */
@CreateDataFrameDslMarker
public abstract class CreateDataFrameDsl<T> {

    /** The [<code>Iterable</code>][Iterable] the [<code>DataFrame</code>][DataFrame] is built from: the objects, one per row. */
    public abstract val source: Iterable<T>

    /**
     * Adds [<code>column</code>][column] to the [<code>DataFrame</code>][DataFrame] being built, at [<code>path</code>][path].
     * Without [<code>path</code>][path], the column lands at its own name.
     *
     * @param [column] The column to add.
     * @param [path] The path to put the column at; the column is renamed to the last path segment.
     */
    public abstract fun add(column: BaseColumn<*>, path: ColumnPath? = null)

    /**
     * Puts this column into the [<code>DataFrame</code>][DataFrame] being built, under [<code>name</code>][name].
     */
    @Interpretable("ToDataFrameDslIntoString")
    public infix fun BaseColumn<*>.into(name: String): Unit = add(this, pathOf(name))

    /**
     * Puts this column into the [<code>DataFrame</code>][DataFrame] being built, at [<code>path</code>][path];
     * a path of more than one segment nests the column in [<code>ColumnGroup</code>][ColumnGroup]s.
     */
    @Interpretable("ToDataFrameDslIntoPath")
    public infix fun BaseColumn<*>.into(path: ColumnPath): Unit = add(this, path)

    /**
     * Adds a column per property of the elements.
     *
     *
     *
     * With `maxDepth` = 0, the values of the properties stay as they are:
     * a nested object stays an object, and a list stays a list.
     * With `maxDepth` = 1, nested objects become [<code>ColumnGroup</code>][ColumnGroup]s, and lists of objects [<code>FrameColumn</code>][FrameColumn]s;
     * every further step goes one level deeper.
     * Properties of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s and [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are converted at any depth.
     *
     * @param [roots] The properties, or getter-like functions such as `getX()`, to make columns from;
     * all public properties of the elements by default.
     * @param [maxDepth] How deep to convert the values of the properties.
     * @param [body] The [<code>TraversePropertiesDsl</code>][TraversePropertiesDsl] lambda that excludes or preserves
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
     * Creates a [<code>DataColumn</code>][DataColumn] with the values [<code>expression</code>][expression] returns for each element,
     * without adding it to the [<code>DataFrame</code>][DataFrame] being built — combine it with [<code>into</code>][into].
     *
     * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
     * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
     * @param [expression] The expression to compute each value from an element.
     */
    public inline fun <reified R> expr(infer: Infer = Infer.Nulls, noinline expression: (T) -> R): DataColumn<R> =
        source.map { expression(it) }.toColumn(infer = infer)

    /**
     * Adds a column named [<code>name</code>][name] with the values [<code>expression</code>][expression] returns for each element.
     */
    @Interpretable("ToDataFrameDslAdd")
    public inline fun <reified R> add(name: String, noinline expression: (T) -> R): Unit =
        add(source.map { expression(it) }.toColumn(name, Infer.Nulls))

    /**
     * Adds a column named this string with the values [<code>expression</code>][expression] returns for each element.
     */
    @Interpretable("ToDataFrameFrom0")
    public inline infix fun <reified R> String.from(noinline expression: (T) -> R): Unit = add(this, expression)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(noinline expression: (T) -> R): Unit =
        add(columnName, expression)

    /**
     * Adds a column named this string with the values of [<code>inferType</code>][inferType]'s expression,
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
     * Wraps an expression so that [<code>from</code>][String.from] creates the column
     * with the type inferred from the values it returns.
     */
    public data class InferType<T, R>(val expression: (T) -> R)

    /**
     * Creates an [<code>InferType</code>][InferType] for [<code>from</code>][String.from] from [<code>expression</code>][expression],
     * so that the column type is inferred from the values.
     */
    public inline fun <reified R> inferType(noinline expression: (T) -> R): InferType<T, R> = InferType(expression)

    /**
     * Creates a [<code>ColumnGroup</code>][ColumnGroup] named this string: every column added inside [<code>builder</code>][builder]
     * lands in the group.
     */
    @Interpretable("ToDataFrameDslStringInvoke")
    public abstract operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit)
}

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from this [<code>Iterable</code>][Iterable] with a single column named [<code>columnName</code>][columnName]:
 * every element becomes one cell of the column.
 *
 * A way to start from values you want to take apart step by step — files, URLs, ... —
 * and to add columns extracted from them later.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 *
 * ### Example
 * ```kotlin
 * val files = listOf(File("data.csv"), File("data1.csv"))
 *
 * val df = files.toDataFrame(columnName = "data")
 * ```
 *
 * @param [T] The type of the elements.
 * @param [columnName] The name of the single column.
 * @return The new [<code>DataFrame</code>][DataFrame].
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
 * A [<code>DataSchema</code>][DataSchema] for the `value` column: the single column that [<code>toDataFrame</code>][Iterable.toDataFrame]
 * creates when the elements of the [<code>Iterable</code>][Iterable] are stored as they are — they are of a simple type,
 * or of a class without public properties.
 *
 * Cast the resulting [<code>DataFrame</code>][DataFrame] to `DataFrame<ValueProperty<T>>` to access the `value` column
 * type-safely, for example to add computed columns to it.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 */
@DataSchema
public interface ValueProperty<T> {
    public val value: T
}

// endregion

// region Create DataFrame from Map

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with a column per entry of this [<code>Map</code>][Map]:
 * the column is named after the key and takes the values of the iterable,
 * with the type inferred from the values.
 *
 * For more information: [See DataFrame from Iterable<T> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-iterable-t)
 *
 * ### Example
 * ```kotlin
 * val map = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))
 *
 * val df = map.toDataFrame()
 * ```
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
 */
public fun Map<String, Iterable<Any?>>.toDataFrame(): DataFrame<*> =
    map {
        DataColumn.createByInference(it.key, it.value.asList())
    }.toDataFrame()

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with a column per entry of this [<code>Map</code>][Map], like [<code>toDataFrame</code>][Map.toDataFrame],
 * but a key that is a [<code>ColumnPath</code>][ColumnPath] of more than one segment nests the column in [<code>ColumnGroup</code>][ColumnGroup]s,
 * one group per path segment except the last, which names the column.
 *
 * @return The new [<code>DataFrame</code>][DataFrame].
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
 * Converts a list of lists into a [<code>DataFrame</code>][DataFrame].
 *
 * By default, treats lists as row values. If [<code>header</code>][header] is not provided, the first inner list becomes a header (column names), and the remaining lists are treated as data.
 *
 * With [<code>containsColumns</code>][containsColumns] = `true`, interprets each inner list as a column.
 * If [<code>header</code>][header] is not provided, the first element will be used as the column name, and the remaining elements as values.
 *
 * For more information: [See DataFrame from List<List<T>> on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframe-from-list-list-t)
 *
 * @param T The type of elements contained in the nested lists.
 * @param containsColumns If `true`, treats each nested list as a column.
 *                        Otherwise, each nested list is a row.
 *                        Defaults to `false`.
 * @param header overrides extraction of column names from lists - all values are treated as data instead.
 * @return A [<code>DataFrame</code>][DataFrame] containing the data from the nested list structure.
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
