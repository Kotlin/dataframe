package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.api.requireImpl
import kotlin.reflect.typeOf

/**
 * Resolves [<code>column</code>][column] in this [<code>DataFrame</code>][DataFrame] and checks that its runtime type is a subtype of [<code>C</code>][C].
 * Throws if the column can't be resolved or if its type doesn't match.
 *
 * From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
 * Will work in the compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0.
 *
 * The aim here is to help incrementally migrate workflows to extension properties API.
 *
 * We recommend considering declaring a [<code>DataSchema</code>][DataSchema] and using [<code>cast</code>][cast] or [<code>convertTo</code>][convertTo] if you end up with more than a few `requireColumn` calls.
 *
 * For more information: [See `requireColumn` on the documentation website.](https://kotlin.github.io/dataframe/require.html)
 *
 * ### Examples
 *
 * Require a nested column with a runtime check and use the extension property afterwards:
 * ```kotlin
 * val df = DataFrame.readJson(file)
 * // Without `requireColumn` the extension property will not resolve:
 * // df.select { name.firstName }
 * val typed = df.requireColumn { "name"["firstName"]<String>() }
 * val v: String = typed.name.firstName[0]
 * ```
 *
 * A pipeline that uses only String Column Accessors and String API overloads repeats the `stargazers_count` string three times:
 * ```kotlin
 * val repos = DataFrame.readCsv(file)
 *
 * repos
 *     .filter { "stargazers_count"<Int>() > 100 }
 *     .sortByDesc("stargazers_count")
 *     .select("full_name", "stargazers_count")
 * ```
 *
 * `requireColumn` adds the column to the compile-time schema, so the same pipeline can use the extension property instead:
 * ```kotlin
 * val repos = DataFrame.readCsv(file)
 *     .requireColumn { "stargazers_count"<Int>() }
 *
 * repos
 *     .filter { stargazers_count > 100 }
 *     .sortByDesc { stargazers_count }
 *     .select { "full_name" and stargazers_count }
 * ```
 *
 * This way code becomes a bit more robust.
 * For example, after `.rename { stargazers_count }.into("stars")`
 * usages of the `stargazers_count` property become compile-time errors
 * that are easy to spot and update to `stars`:
 * ```kotlin
 * val repos = DataFrame.readCsv(file)
 *     .requireColumn { "stargazers_count"<Int>() }
 *     .rename { stargazers_count }.into("stars")
 *
 * // usages of the `stargazers_count` property no longer resolve:
 * // repos.filter { stargazers_count > 100 }
 * repos.filter { stars > 100 }
 * ```
 *
 * `requireColumn` can also change the compile-time type of a column that is already in the schema:
 * the runtime type of the column must be a subtype of [<code>C</code>][C].
 * (`columnOf` infers the runtime type from the values, so in the sample below it is `String`, not `Any`.)
 * ```kotlin
 * val df = dataFrameOf("name" to columnOf<Any>("Joe"))
 * val typed = df.requireColumn { name.cast<String>() }
 * val filtered = typed.filter { name.length > 2 }
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [C] The type the column is required to have.
 * @param [column] The [<code>ColumnSelector</code>][ColumnSelector] for the column to require.
 * @throws [ColumnNotFoundException] if [<code>column</code>][column] can't be resolved in this [<code>DataFrame</code>][DataFrame].
 * The exception implements [<code>DataFrameError</code>][DataFrameError], so the compiler plugin reports it as a warning.
 * @throws [IllegalArgumentException] if the runtime type of the resolved column is not a subtype of [<code>C</code>][C].
 * @return This [<code>DataFrame</code>][DataFrame] with [<code>column</code>][column] added to the compile-time schema.
 */
@Refine
@Interpretable("Require0")
public inline fun <T, reified C> DataFrame<T>.requireColumn(noinline column: ColumnSelector<T, C>): DataFrame<T> =
    requireImpl(column, typeOf<C>())
