package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convertTo

/**
 *  Annotation to generate extension properties API for a given declaration, according to its properties.
 *  Annotated declaration should be non-local and non-private interface or a class.
 *  The aim here is to provide convenient syntax for working with a dataframe instance right after reading from it CSV, JSON, Databases, Arrow, etc.
 *  After `val df = DataFrame.read*` operation, `df` is a source of truth for the DataSchema.
 *  One way to look at it, DataSchema "tells" the compiler what's already there. It doesn't affect reading.
 *  See the list below of code generation methods to simplify the process of getting what we call an initial dataschema.
 *  Given the initial schema of the data you read, the compiler plugin will provide a typed result for most operations.
 *
 * Example:
 * ```
 * @DataSchema
 * data class Group(
 *     val id: String,
 *     val participants: List<Person>
 * )
 *
 * @DataSchema
 * data class Person(
 *     val name: Name,
 *     val age: Int,
 *     val city: String?
 * )
 *
 * @DataSchema
 * data class Name(
 *     val firstName: String,
 *     val lastName: String,
 * )
 *
 * fun main() {
 *  val url = "https://raw.githubusercontent.com/Kotlin/dataframe/refs/heads/master/data/participants.json"
 *  val df = DataFrame.readJson(url).cast<Group>()
 *  val i: Int = df.id[0] // properties style access to columns and values
 *
 *  val df1 = df.asGroupBy { participants }.aggregate {
 *    count() into "groupSize"
 *    distinct { city } into "cities"
 *  }
 *
 *  // now compiler plugin uses previous knowledge of `Group` combined with its understanding of aggregate operation
 *  // to help you access new columns
 *  val l: List<String> = df1.cities[0]
 * }
 * ```
 *
 * @see [org.jetbrains.kotlinx.dataframe.api.generateDataClasses]
 * @see [org.jetbrains.kotlinx.dataframe.api.generateInterfaces]
 * @see [org.jetbrains.kotlinx.dataframe.DataFrame.cast]
 * @see [org.jetbrains.kotlinx.dataframe.DataFrame.convertTo]
 */
@Target(AnnotationTarget.CLASS)
public annotation class DataSchema(val isOpen: Boolean = true)
