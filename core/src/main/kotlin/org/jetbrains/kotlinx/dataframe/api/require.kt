package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.api.requireImpl
import kotlin.reflect.typeOf

/**
 * Resolves [column] in this [DataFrame] and checks that its runtime type is a subtype of [C].
 * Throws if the column can't be resolved or if its type doesn't match.
 *
 * From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
 * Will work in the compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0.
 *
 * The aim here is to help incrementally migrate workflows to extension properties API.
 *
 * We recommend considering declaring a [DataSchema] and using [cast] or [convertTo] if you end up with more than a few `requireColumn` calls.
 *
 * For more information: {@include [DocumentationUrls.Require]}
 *
 * ### Examples
 *
 * Require a nested column with a runtime check and use the extension property afterwards:
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples.requireColumnNested]
 *
 * A pipeline that uses only String Column Accessors and String API overloads repeats the `stargazers_count` string three times:
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples.requireColumnStringApi]
 *
 * `requireColumn` adds the column to the compile-time schema, so the same pipeline can use the extension property instead:
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples.requireColumnTyped]
 *
 * This way code becomes a bit more robust.
 * For example, after `.rename { stargazers_count }.into("stars")`
 * usages of the `stargazers_count` property become compile-time errors
 * that are easy to spot and update to `stars`:
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples.requireColumnRename]
 *
 * `requireColumn` can also change the compile-time type of a column that is already in the schema:
 * the runtime type of the column must be a subtype of [C].
 * (`columnOf` infers the runtime type from the values, so in the sample below it is `String`, not `Any`.)
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples.requireColumnCast]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [C] The type the column is required to have.
 * @param [column] The [ColumnSelector] for the column to require.
 * @throws [ColumnNotFoundException] if [column] can't be resolved in this [DataFrame].
 * The exception implements [DataFrameError], so the compiler plugin reports it as a warning.
 * @throws [IllegalArgumentException] if the runtime type of the resolved column is not a subtype of [C].
 * @return This [DataFrame] with [column] added to the compile-time schema.
 */
@Refine
@Interpretable("Require0")
public inline fun <T, reified C> DataFrame<T>.requireColumn(noinline column: ColumnSelector<T, C>): DataFrame<T> =
    requireImpl(column, typeOf<C>())
