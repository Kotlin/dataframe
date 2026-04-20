package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.api.requireImpl
import kotlin.reflect.typeOf

/**
 * Resolves [column] in this [DataFrame] and checks that its runtime type is a subtype of [C].
 * Throws if the column can't be resolved or if its type doesn't match.
 *
 * From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
 *
 * The aim here is to help incrementally migrate workflows to extension properties API.
 *
 * We recommend considering declaring a [DataSchema] and use [cast] or [convertTo] if you end up with more than a few `requireColumn` calls.
 *
 * Example:
 *
 * ```kotlin
 * val repos = DataFrame
 *     .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
 *
 * repos
 *     .filter { "stargazers_count"<Int>() > 100 }
 *     .sortByDesc("stargazers_count")
 *     .select("full_name", "stargazers_count")
 * ```
 *
 * Notice how `stargazers_count` String is repeated three times. We can refactor this code using `requireColumn`:
 *
 * ```
 * val repos = DataFrame
 *     .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
 *     .requireColumn { "stargazers_count"<Int>() }
 *
 * repos
 *     .filter { stargazers_count > 100 }
 *     .sortByDesc { stargazers_count }
 *     .select { "full_name" and stargazers_count }
 * ```
 *
 * This way code becomes a bit more robust. For example, usages of a renamed column will become compile time errors that are easy to spot and update:
 * ```kotlin
 * val repos = DataFrame
 *     .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
 *     .requireColumn { "stargazers_count"<Int>() }
 *     .rename { stargazers_count }.into("stars")
 *
 * repos
 *     .filter { stars > 100 }
 *     .sortByDesc { stars }
 *     .select { "full_name" and stars }
 * ```
 *
 */
@Refine
@Interpretable("Require0")
public inline fun <T, reified C> DataFrame<T>.requireColumn(noinline column: ColumnSelector<T, C>): DataFrame<T> =
    requireImpl(column, typeOf<C>())
