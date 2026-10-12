package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.toSequenceImpl
import kotlin.reflect.typeOf

// region DataFrame

/**
 * Converts this [<code>DataFrame</code>][DataFrame] into a [<code>Sequence</code>][Sequence] of [<code>T</code>][T] instances,
 * where [<code>T</code>][T] is the type argument of this [<code>DataFrame</code>][DataFrame].
 *
 * Use it only when the type argument of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] is known to be a data class.
 * On a `DataFrame<*>`, such as the result of [<code>dataFrameOf</code>][org.jetbrains.kotlinx.dataframe.api.dataFrameOf] or of reading a file,
 * this function throws an exception, because there is no data class to create.
 * With the compiler plugin, the same happens even right after
 * [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] on a list of data class instances:
 * the plugin gives the result a generated type argument, which is not a data class.
 * In these cases, use [<code>toSequenceOf</code>][DataFrame.toSequenceOf], or [<code>cast</code>][org.jetbrains.kotlinx.dataframe.DataFrame.cast] the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a data class first.
 *
 * The columns are matched and converted when this function is called,
 * so the exceptions listed below are thrown right away.
 * The [T] instances are created only when the sequence is iterated,
 * one per row that is read, and each iteration creates them again.
 *
 * The sequence has one [T] instance per row, in the order of the rows.
 *
 * [T] has to be a data class. An interface does not work, even one marked with [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema].
 * A generic data class works too, for example, `Pair<String, Int>`.
 * Each instance is created by the primary constructor of [T].
 * Each constructor parameter gets the value of the column with the same name,
 * or with the name given in [<code>ColumnName</code>][org.jetbrains.kotlinx.dataframe.annotations.ColumnName]. Names are case-sensitive.
 * The order of the columns does not matter, and columns that match no parameter are ignored.
 * A parameter with a default value gets this value when there is no column for it.
 *
 * When a column type differs from the parameter type, the values are converted
 * the same way as in [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo], for example, from [<code>Int</code>][Int] to [<code>Long</code>][Long].
 * A [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] becomes a nested data class,
 * and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] becomes a [<code>List</code>][List] of data class instances, or a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * A frame that goes into a `DataFrame<S>` parameter is converted to the schema of `S`
 * the same way as in [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo], and its other columns stay.
 * A frame that goes into a `DataFrame<*>` parameter is passed as it is.
 *
 * An exception is thrown when:
 * - [T] is not a data class;
 * - there is no column for a constructor parameter without a default value;
 * - a column has `null` values, but its parameter is not nullable;
 * - a value cannot be converted to the parameter type;
 * - a frame cannot be converted to the schema of its `DataFrame<S>` parameter,
 *   for example, because a column of `S` is missing.
 *
 * For more information: [See `toSequence` on the documentation website.](https://kotlin.github.io/dataframe/tolist.html#tosequence-and-tosequenceof)
 *
 * See also:
 *  - [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] — the reverse operation: creates a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from a list of objects.
 *  - [<code>toList</code>][DataFrame.toList] and [<code>toListOf</code>][DataFrame.toListOf] —
 * the same conversion into a [<code>List</code>][List].
 *  - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — changes the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match the schema of [T]
 *    and returns a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] instead.
 *
 * ### Example
 * ```kotlin
 * data class Input(val a: Int, val b: Int)
 *
 * val df = dataFrameOf("a", "b")(1, 2, 3, 4)
 *
 * // Only the first row becomes an `Input`
 * df.cast<Input>().toSequence().first() // Input(a=1, b=2)
 * ```
 *
 * @param [T] The type argument of this [<code>DataFrame</code>][DataFrame]. It has to be a data class.
 * @return A [<code>Sequence</code>][Sequence] with one [<code>T</code>][T] instance per row of this [<code>DataFrame</code>][DataFrame].
 */
public inline fun <reified T> DataFrame<T>.toSequence(): Sequence<T> = toSequenceImpl(typeOf<T>()) as Sequence<T>

/**
 * Converts this [<code>DataFrame</code>][DataFrame] into a [<code>Sequence</code>][Sequence] of [<code>T</code>][T] instances,
 * where [<code>T</code>][T] is given explicitly as the type argument of this call.
 *
 * Use this function in most cases.
 * The type argument of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] does not matter,
 * so this function works on a `DataFrame<*>` too,
 * and on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] whose type argument is generated by the compiler plugin.
 *
 * The columns are matched and converted when this function is called,
 * so the exceptions listed below are thrown right away.
 * The [T] instances are created only when the sequence is iterated,
 * one per row that is read, and each iteration creates them again.
 *
 * The sequence has one [T] instance per row, in the order of the rows.
 *
 * [T] has to be a data class. An interface does not work, even one marked with [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema].
 * A generic data class works too, for example, `Pair<String, Int>`.
 * Each instance is created by the primary constructor of [T].
 * Each constructor parameter gets the value of the column with the same name,
 * or with the name given in [<code>ColumnName</code>][org.jetbrains.kotlinx.dataframe.annotations.ColumnName]. Names are case-sensitive.
 * The order of the columns does not matter, and columns that match no parameter are ignored.
 * A parameter with a default value gets this value when there is no column for it.
 *
 * When a column type differs from the parameter type, the values are converted
 * the same way as in [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo], for example, from [<code>Int</code>][Int] to [<code>Long</code>][Long].
 * A [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] becomes a nested data class,
 * and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] becomes a [<code>List</code>][List] of data class instances, or a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * A frame that goes into a `DataFrame<S>` parameter is converted to the schema of `S`
 * the same way as in [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo], and its other columns stay.
 * A frame that goes into a `DataFrame<*>` parameter is passed as it is.
 *
 * An exception is thrown when:
 * - [T] is not a data class;
 * - there is no column for a constructor parameter without a default value;
 * - a column has `null` values, but its parameter is not nullable;
 * - a value cannot be converted to the parameter type;
 * - a frame cannot be converted to the schema of its `DataFrame<S>` parameter,
 *   for example, because a column of `S` is missing.
 *
 * For more information: [See `toSequence` on the documentation website.](https://kotlin.github.io/dataframe/tolist.html#tosequence-and-tosequenceof)
 *
 * See also:
 *  - [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] — the reverse operation: creates a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from a list of objects.
 *  - [<code>toList</code>][DataFrame.toList] and [<code>toListOf</code>][DataFrame.toListOf] —
 * the same conversion into a [<code>List</code>][List].
 *  - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — changes the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match the schema of [T]
 *    and returns a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] instead.
 *
 * ### Example
 * ```kotlin
 * val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
 *     .group("name", "lastName").into("fullName")
 *
 * data class FullName(val name: String, val lastName: String)
 *
 * data class Person(val fullName: FullName, val age: Int)
 *
 * // The "fullName" column group becomes a nested `FullName`
 * df.toSequenceOf<Person>().first() // Person(fullName=FullName(name=John, lastName=Doe), age=21)
 * ```
 *
 * @param [T] The data class to create for each row.
 * @return A [<code>Sequence</code>][Sequence] with one [<code>T</code>][T] instance per row of this [<code>DataFrame</code>][DataFrame].
 */
public inline fun <reified T> DataFrame<*>.toSequenceOf(): Sequence<T> = toSequenceImpl(typeOf<T>()) as Sequence<T>

// endregion
