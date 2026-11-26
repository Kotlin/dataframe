package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convertTo

/**
 * This annotation marks an interface or data class as a [data schema](https://kotlin.github.io/dataframe/schemas.html).
 * An annotated declaration should be a non-local and non-private interface or class with [only constructor properties](https://kotlin.github.io/dataframe/schemas.html#dataschema-annotation).
 *
 * This annotation generates an extension properties API for a declaration according to its properties.
 * The aim is to provide a convenient class-like syntax for working with a dataframe instance after reading it from CSV,
 * JSON, databases, Arrow, or other sources.
 *
 * After a `val df = DataFrame.read*` operation, `df` is the source of truth for the data schema.
 * One way to look at it is that a data schema tells the compiler what is already there; it does not affect reading.
 *
 * Given the initial schema of the data you read, the
 * [compiler plugin](https://github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe) provides a typed result
 * for most operations.
 *
 * Example:
 * ```kotlin
 * @DataSchema
 * data class Group(
 *     val id: String, // DataColumn<String>
 *     val participants: List<Person>, // FrameColumn<Person>
 * )
 *
 * @DataSchema
 * data class Person(
 *     val name: Name, // ColumnGroup<Name>
 *     val age: Int, // DataColumn<Int>
 *     val city: String?, // DataColumn<String?>
 * )
 *
 * @DataSchema
 * data class Name(
 *     val firstName: String, // DataColumn<String>
 *     val lastName: String, // DataColumn<String>
 * )
 *
 * fun main() {
 *     val url = "https://raw.githubusercontent.com/Kotlin/dataframe/refs/heads/master/data/participants.json"
 *     val df = DataFrame.readJson(url).cast<Group>()
 *     val groupId: String = df.id[0] // Properties-style access to columns and values.
 *
 *    val df1 = df.asGroupBy { participants }.aggregate {
 *        count() into "groupSize"
 *        city.distinct().toList() into "cities"
 *    }
 *
 *    // The compiler plugin uses prior knowledge of `Group` and the aggregate operation to infer new columns.
 *    val cities: List<String?> = df1.cities[0]
 * }
 * ```
 * Schema properties describe the corresponding dataframe columns:
 *
 * - A property of a regular type `V` corresponds to a `DataColumn<V>`.
 * - A property of type `T`, where `T` is annotated with `@DataSchema`, corresponds to a `ColumnGroup<T>`.
 * - A property of type `List<T>`, where `T` is annotated with `@DataSchema`, corresponds to a `FrameColumn<T>`.
 *
 * After casting a dataframe to `DataFrame<Group>`, it can be converted to data-class instances:
 * ```kotlin
 * val url = "https://raw.githubusercontent.com/Kotlin/dataframe/refs/heads/master/data/participants.json"
 * val df = DataFrame.readJson(url).cast<Group>()
 * val groups: List<Group> = df.toList()
 * val participants: List<Person> = groups.first().participants
 * ```
 * @property isOpen used in REPL to reduce interconnection between generated schemas; outside REPL is effectively noop
 * @see [org.jetbrains.kotlinx.dataframe.api.generateDataClasses]
 * @see [org.jetbrains.kotlinx.dataframe.api.generateInterfaces]
 * @see [org.jetbrains.kotlinx.dataframe.DataFrame.cast]
 * @see [org.jetbrains.kotlinx.dataframe.DataFrame.convertTo]
 */
@Target(AnnotationTarget.CLASS)
public annotation class DataSchema(val isOpen: Boolean = true)
