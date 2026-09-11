package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

/**
 * Marker interface added as a supertype to classes annotated with [<code>DataSchema</code>][DataSchema] by the Kotlin DataFrame compiler
 * plugin.
 *
 * This lets instances of annotated classes represent dataframe rows, so they can be passed to [<code>dataFrameOf</code>][dataFrameOf] and
 * appended with [<code>append</code>][append]. The compiler plugin does not add this supertype to annotated interfaces because interfaces
 * don't have constructors.
 */
public interface DataRowSchema

/**
 * Example:
 * ```kotlin
 * @DataSchema
 * data class Person(val name: String, val age: Int)
 *
 * fun main() {
 *     val df = dataFrameOf(Person("Alice", 30), Person("Bob", 25))
 *     val dfWithCarol = df.append(Person("Carol", 25))
 * }
 * ```
 */
public inline fun <reified T : DataRowSchema> dataFrameOf(vararg rows: T): DataFrame<T> =
    rows.asIterable().toDataFrame()

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the existing rows followed by one row for each object in [<code>rows</code>][rows].
 *
 * Each object in [<code>rows</code>][rows] represents one complete row. Its properties are used as the values of the corresponding
 * dataframe columns. If [<code>rows</code>][rows] is empty, this [<code>DataFrame</code>][DataFrame] is returned as is.
 *
 * The schema type [<code>T</code>][T] must implement [<code>DataRowSchema</code>][DataRowSchema]. When the Kotlin DataFrame compiler plugin is enabled, it adds
 * [<code>DataRowSchema</code>][DataRowSchema] as a supertype to classes annotated with [<code>DataSchema</code>][DataSchema], allowing their instances to be appended as
 * rows. The plugin is not required for this overload: a class can implement [<code>DataRowSchema</code>][DataRowSchema] explicitly.
 * The plugin does not add this supertype to annotated interfaces.
 *
 * For more information: [See Compiler Plugin on the documentation website.](https://kotlin.github.io/dataframe/compiler-plugin.html)
 *
 * This operation does not modify the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Adding rows creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and rebuilds its columns using the existing and appended values.
 * Repeatedly appending rows one at a time in a loop is a performance antipattern.
 * Prefer building a dataframe at once or appending/concatenating rows in batches.
 *
 * For more information: [See `append` on the documentation website.](https://kotlin.github.io/dataframe/append.html)
 *
 * See also:
 * - [<code>append</code>][DataFrame.append] — appends rows constructed from a flat sequence of cell values.
 * - [<code>appendNulls</code>][appendNulls] — appends rows filled with `null` values.
 * - [<code>concat</code>][DataFrame.concat] — vertically combines this [<code>DataFrame</code>][DataFrame] with other dataframes or rows.
 * - [<code>add</code>][DataFrame.add] — adds columns rather than rows.
 *
 * ### Examples
 * In the example below, `Person` is a class annotated with [<code>DataSchema</code>][DataSchema] and has `name` and `age` properties.
 * `df` has the type `DataFrame<Person>`.
 *
 * ```kotlin
 * df.append(Person("Bob", 30))
 * ```
 *
 * @param [T] The schema type of this dataframe and the objects representing the appended rows.
 * @param [rows] Objects representing complete rows to append, in order.
 * @return A new [<code>DataFrame</code>][DataFrame] containing the existing and appended rows, or this [<code>DataFrame</code>][DataFrame] if [<code>rows</code>][rows] is empty.
 */
public inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T> =
    if (rows.isEmpty()) this else listOf(this, rows.asIterable().toDataFrame()).concat()
