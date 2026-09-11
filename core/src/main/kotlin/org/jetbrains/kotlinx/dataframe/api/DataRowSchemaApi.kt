package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

/**
 * Marker interface added as a supertype to classes annotated with [DataSchema] by the Kotlin DataFrame compiler
 * plugin.
 *
 * This lets instances of annotated classes represent dataframe rows, so they can be passed to [dataFrameOf] and
 * appended with [append]. The compiler plugin does not add this supertype to annotated interfaces because interfaces
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
 * Returns a [DataFrame] containing the existing rows followed by one row for each object in [rows].
 *
 * Each object in [rows] represents one complete row. Its properties are used as the values of the corresponding
 * dataframe columns. If [rows] is empty, this [DataFrame] is returned as is.
 *
 * The schema type [T] must implement [DataRowSchema]. When the Kotlin DataFrame compiler plugin is enabled, it adds
 * [DataRowSchema] as a supertype to classes annotated with [DataSchema], allowing their instances to be appended as
 * rows. The plugin is not required for this overload: a class can implement [DataRowSchema] explicitly.
 * The plugin does not add this supertype to annotated interfaces.
 *
 * For more information: {@include [DocumentationUrls.CompilerPlugin]}
 *
 * @include [AppendImmutabilityAndPerformanceNote]
 *
 * For more information: {@include [DocumentationUrls.Append]}
 *
 * See also:
 * - [append][DataFrame.append] — appends rows constructed from a flat sequence of cell values.
 * - [appendNulls] — appends rows filled with `null` values.
 * - [concat][DataFrame.concat] — vertically combines this [DataFrame] with other dataframes or rows.
 * - [add][DataFrame.add] — adds columns rather than rows.
 *
 * ### Examples
 * In the example below, `Person` is a class annotated with [DataSchema] and has `name` and `age` properties.
 * `df` has the type `DataFrame<Person>`.
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Append.appendDataSchema]
 *
 * @param [T] The schema type of this dataframe and the objects representing the appended rows.
 * @param [rows] Objects representing complete rows to append, in order.
 * @return A new [DataFrame] containing the existing and appended rows, or this [DataFrame] if [rows] is empty.
 */
public inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T> =
    if (rows.isEmpty()) this else listOf(this, rows.asIterable().toDataFrame()).concat()
