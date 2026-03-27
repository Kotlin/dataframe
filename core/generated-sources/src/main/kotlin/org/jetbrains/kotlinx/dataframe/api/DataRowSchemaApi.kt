package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

/**
 * Marker interface that's automatically added to classes annotated with [DataSchema]
 */
public interface DataRowSchema

/**
 * Example:
 * ```
 * @DataSchema
 * data class Person(val name: String, val age: Int)
 *
 * fun main() {
 *  val df = dataFrameOf(Person("Alice", 30), Person("Bob", 25))
 * }
 * ```
 */
public inline fun <reified T : DataRowSchema> dataFrameOf(vararg rows: T): DataFrame<T> =
    rows.asIterable().toDataFrame()

public inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T> =
    concat(dataFrameOf(*rows))
