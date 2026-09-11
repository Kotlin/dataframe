package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.compileTimeSchemaImpl
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

@ExcludeFromSources
internal interface SchemaDocs {

    /**
     * A [DataFrame] has two schemas that do not have to agree.
     * [schema][DataFrame.schema] gives the **runtime** one, read from the columns the [DataFrame]
     * holds; [compileTimeSchema][DataFrame.compileTimeSchema] gives the **compile-time** one,
     * read from the type argument `T` of `DataFrame<T>`.
     * The two differ when the type of a [DataFrame] does not fit its columns,
     * for example after a [cast] to a schema the data does not have;
     * comparing the two is how you notice.
     *
     * The runtime schema is also reachable from a [DataRow] — [schema][DataRow.schema] —
     * and from a [GroupBy] — [schema][GroupBy.schema].
     *
     * Use [print][DataFrameSchema.print] to write a schema out as a tree,
     * and [compare][DataFrameSchema.compare] to relate two schemas to each other.
     */
    @ExcludeFromSources
    typealias SchemaSourcesSnippet = Nothing

    /**
     * {@comment The input of the `schema` examples below. KDoc-snippet.
     *    Every output that follows it is an expected value in `SchemaKDocExampleTests`.}
     * The examples below use the [DataFrame] from the
     * [`schema` page on the documentation website]({@include [DocumentationUrls.Url]}/schema.html):
     * a `name` [column group][ColumnGroup] holding `firstName` and `lastName`,
     * and the columns `age`, `city`, `weight` and `isHappy`.
     */
    @ExcludeFromSources
    typealias ExampleDataSnippet = Nothing
}

// region DataRow

/**
 * Returns the runtime [DataFrameSchema] of the [DataFrame] this row is part of.
 *
 * The result describes all columns of that [DataFrame], not just the values of this one row,
 * so every row of the same [DataFrame] gives the same schema.
 * For a row taken out of a [column group][ColumnGroup], it is the schema of that group.
 *
 * @include [SchemaDocs.SchemaSourcesSnippet]
 *
 * For more information: {@include [DocumentationUrls.Schema]}
 *
 * ### Example
 *
 * @include [SchemaDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * // the columns of df, not the values of the single row
 * df.first().schema().columns.keys // [name, age, city, weight, isHappy]
 * ```
 *
 * @return The [DataFrameSchema] of the [DataFrame] this row is part of.
 */
public fun DataRow<*>.schema(): DataFrameSchema = owner.schema()

// endregion

// region DataFrame

/**
 * Returns the runtime [DataFrameSchema] of this [DataFrame], read from the columns it holds.
 *
 * Because it comes from the columns and not from the type argument of this [DataFrame],
 * it always fits the data.
 * A [column group][ColumnGroup] contributes the schema of its nested columns;
 * a [frame column][FrameColumn] contributes only the columns that all of its
 * non-empty dataframes have.
 *
 * @include [SchemaDocs.SchemaSourcesSnippet]
 *
 * For more information: {@include [DocumentationUrls.Schema]}
 *
 * ### Example
 *
 * @include [SchemaDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * df.schema()
 * ```
 *
 * Written out, a column group is shown by indentation and a frame column by `*`:
 *
 * ```text
 * name:
 *     firstName: String
 *     lastName: String
 * age: Int
 * city: String?
 * weight: Int?
 * isHappy: Boolean
 * ```
 *
 * @return The [DataFrameSchema] of this [DataFrame].
 */
@RequiredByIntellijPlugin
public fun DataFrame<*>.schema(): DataFrameSchema = extractSchema()

// endregion

// region GroupBy

/**
 * Returns the runtime [DataFrameSchema] of this [GroupBy] seen as a [DataFrame]:
 * the key columns, followed by a [frame column][FrameColumn] named `group` holding the groups.
 *
 * This is the schema of [toDataFrame][GroupBy.toDataFrame], which is why the group column
 * carries its default name here.
 *
 * @include [SchemaDocs.SchemaSourcesSnippet]
 *
 * For more information: {@include [DocumentationUrls.Schema]}
 *
 * ### Example
 *
 * @include [SchemaDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * df.groupBy { city }.schema()
 * ```
 *
 * The key column comes first, and the groups keep every column of `df`:
 *
 * ```text
 * city: String?
 * group: *
 *     name:
 *         firstName: String
 *         lastName: String
 *     age: Int
 *     city: String?
 *     weight: Int?
 *     isHappy: Boolean
 * ```
 *
 * @return The [DataFrameSchema] of this [GroupBy] as a [DataFrame].
 */
public fun GroupBy<*, *>.schema(): DataFrameSchema = toDataFrame().schema()

// endregion

// region compileTimeSchema

/**
 * Returns the compile-time [DataFrameSchema] of this [DataFrame]:
 * the schema that follows from its type argument [T], not from the columns it holds.
 *
 * [T] is a schema marker — a [DataSchema] declaration you wrote yourself,
 * or the one the compiler plugin produced for the result of an operation.
 * When [T] declares no properties, as in `DataFrame<*>`, the returned schema has no columns.
 *
 * @include [SchemaDocs.SchemaSourcesSnippet]
 *
 * For more information: {@include [DocumentationUrls.Schema]}
 *
 * On schema markers and the plugin that writes them: {@include [DocumentationUrls.CompilerPlugin]}
 *
 * ### Example
 *
 * {@comment Both outputs below are expected values in `SchemaTests`.}
 * ```kotlin
 * @DataSchema
 * interface Person {
 *     val name: String
 *     val age: Int
 * }
 *
 * val df = dataFrameOf("name", "age")("Alice", 15).cast<Person>()
 *
 * // name: String, age: Int — sorted like the columns of df
 * df.compileTimeSchema()
 *
 * // age: Int, name: String — the order T gives them in, which here is not the order of df
 * df.compileTimeSchema(ordered = false)
 * ```
 *
 * @param [T] The schema marker of this [DataFrame]; its properties become the columns of the result.
 * @param [ordered] If `true` (the default), the columns are sorted to match the order of the
 *   [runtime schema][DataFrame.schema], so that the two schemas are easy to compare.
 *   If `false`, the order comes from [T] and does not have to match the [DataFrame].
 * @return The [DataFrameSchema] that follows from the type argument [T] of this [DataFrame].
 */
public inline fun <reified T> DataFrame<T>.compileTimeSchema(ordered: Boolean = true): DataFrameSchema =
    compileTimeSchemaImpl(if (ordered) schema() else null, T::class)

// endregion
