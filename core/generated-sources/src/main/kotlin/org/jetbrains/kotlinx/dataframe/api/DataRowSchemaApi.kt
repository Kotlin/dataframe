package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

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

public inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T> =
    listOf(this, rows.asIterable().toDataFrame()).concat()
