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

// region DataRow

/**
 * Returns the runtime [<code>DataFrameSchema</code>][DataFrameSchema] of the [<code>DataFrame</code>][DataFrame] this row is part of.
 *
 * The result describes all columns of that [<code>DataFrame</code>][DataFrame], not just the values of this one row,
 * so every row of the same [<code>DataFrame</code>][DataFrame] gives the same schema.
 * For a row taken out of a [<code>column group</code>][ColumnGroup], it is the schema of that group.
 *
 * A [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] has two schemas that do not have to agree:
 * the **runtime** one, read from the columns it holds, and the **compile-time** one,
 * read from the type argument `T` of `DataFrame<T>`.
 * They differ when the type does not fit the columns, for example after a [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.cast]
 * to a schema the data does not have; comparing them is how you notice.
 *
 * Use [<code>print</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.print] to write a schema out as a tree,
 * and [<code>compare</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.compare] to relate two schemas to each other.
 *
 * For more information: [See `schema` on the documentation website.](https://kotlin.github.io/dataframe/schema.html)
 *
 * ### Example
 *
 * The examples below use the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] as the `schema` page on the documentation website:
 * a `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] holding `firstName` and `lastName`,
 * and the columns `age`, `city`, `weight` and `isHappy`.
 *
 * ```kotlin
 * // the columns of df, not the values of the single row
 * df.first().schema().columns.keys // [name, age, city, weight, isHappy]
 * ```
 *
 * @return The [<code>DataFrameSchema</code>][DataFrameSchema] of the [<code>DataFrame</code>][DataFrame] this row is part of.
 * @see [DataFrame.schema]
 * @see [DataFrame.compileTimeSchema]
 */
public fun DataRow<*>.schema(): DataFrameSchema = owner.schema()

// endregion

// region DataFrame

/**
 * Returns the runtime [<code>DataFrameSchema</code>][DataFrameSchema] of this [<code>DataFrame</code>][DataFrame], read from the columns it holds.
 *
 * Because it comes from the columns and not from the type argument of this [<code>DataFrame</code>][DataFrame],
 * it always fits the data.
 * A [<code>column group</code>][ColumnGroup] contributes the schema of its nested columns;
 * a [<code>frame column</code>][FrameColumn] contributes only the columns that all of its
 * non-empty dataframes have.
 *
 * A [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] has two schemas that do not have to agree:
 * the **runtime** one, read from the columns it holds, and the **compile-time** one,
 * read from the type argument `T` of `DataFrame<T>`.
 * They differ when the type does not fit the columns, for example after a [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.cast]
 * to a schema the data does not have; comparing them is how you notice.
 *
 * Use [<code>print</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.print] to write a schema out as a tree,
 * and [<code>compare</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.compare] to relate two schemas to each other.
 *
 * For more information: [See `schema` on the documentation website.](https://kotlin.github.io/dataframe/schema.html)
 *
 * ### Example
 *
 * The examples below use the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] as the `schema` page on the documentation website:
 * a `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] holding `firstName` and `lastName`,
 * and the columns `age`, `city`, `weight` and `isHappy`.
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
 * @return The [<code>DataFrameSchema</code>][DataFrameSchema] of this [<code>DataFrame</code>][DataFrame].
 * @see [DataFrame.compileTimeSchema]
 * @see [DataRow.schema]
 * @see [GroupBy.schema]
 */
@RequiredByIntellijPlugin
public fun DataFrame<*>.schema(): DataFrameSchema = extractSchema()

// endregion

// region GroupBy

/**
 * Returns the runtime [<code>DataFrameSchema</code>][DataFrameSchema] of this [<code>GroupBy</code>][GroupBy] seen as a [<code>DataFrame</code>][DataFrame]:
 * the key columns, followed by a [<code>frame column</code>][FrameColumn] named `group` holding the groups.
 *
 * This is the schema of [<code>toDataFrame</code>][GroupBy.toDataFrame], which is why the group column
 * carries its default name here.
 *
 * A [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] has two schemas that do not have to agree:
 * the **runtime** one, read from the columns it holds, and the **compile-time** one,
 * read from the type argument `T` of `DataFrame<T>`.
 * They differ when the type does not fit the columns, for example after a [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.cast]
 * to a schema the data does not have; comparing them is how you notice.
 *
 * Use [<code>print</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.print] to write a schema out as a tree,
 * and [<code>compare</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.compare] to relate two schemas to each other.
 *
 * For more information: [See `schema` on the documentation website.](https://kotlin.github.io/dataframe/schema.html)
 *
 * ### Example
 *
 * The examples below use the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] as the `schema` page on the documentation website:
 * a `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] holding `firstName` and `lastName`,
 * and the columns `age`, `city`, `weight` and `isHappy`.
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
 * @return The [<code>DataFrameSchema</code>][DataFrameSchema] of this [<code>GroupBy</code>][GroupBy] as a [<code>DataFrame</code>][DataFrame].
 * @see [GroupBy.toDataFrame]
 * @see [DataFrame.schema]
 */
public fun GroupBy<*, *>.schema(): DataFrameSchema = toDataFrame().schema()

// endregion

// region compileTimeSchema

/**
 * Returns the compile-time [<code>DataFrameSchema</code>][DataFrameSchema] of this [<code>DataFrame</code>][DataFrame]:
 * the schema that follows from its type argument [<code>T</code>][T], not from the columns it holds.
 *
 * [<code>T</code>][T] is a schema marker — a [<code>DataSchema</code>][DataSchema] declaration you wrote yourself,
 * or the one the compiler plugin produced for the result of an operation.
 * When [<code>T</code>][T] is not a schema marker, as in `DataFrame<*>`, the returned schema has no columns.
 *
 * A [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] has two schemas that do not have to agree:
 * the **runtime** one, read from the columns it holds, and the **compile-time** one,
 * read from the type argument `T` of `DataFrame<T>`.
 * They differ when the type does not fit the columns, for example after a [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.cast]
 * to a schema the data does not have; comparing them is how you notice.
 *
 * Use [<code>print</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.print] to write a schema out as a tree,
 * and [<code>compare</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.compare] to relate two schemas to each other.
 *
 * For more information: [See `schema` on the documentation website.](https://kotlin.github.io/dataframe/schema.html)
 *
 * On schema markers and the plugin that writes them: [See Compiler Plugin on the documentation website.](https://kotlin.github.io/dataframe/compiler-plugin.html)
 *
 * ### Example
 *
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
 * // age: Int, name: String — the compiler-plugin representation order, not the order of df
 * df.compileTimeSchema(ordered = false)
 * ```
 *
 * @param [ordered] If `true` (the default), the columns are sorted to match the order of the
 *   [<code>runtime schema</code>][DataFrame.schema], so that the two schemas are easy to compare;
 *   a column the runtime schema does not have comes first.
 *   If `false`, the columns are ordered as they are represented in the compiler plugin:
 *   by the primary constructor for a `data class` marker, and otherwise in the order
 *   reflection reports the properties of [<code>T</code>][T] in — which is not their declaration order.
 * @return The [<code>DataFrameSchema</code>][DataFrameSchema] that follows from the type argument [<code>T</code>][T] of this [<code>DataFrame</code>][DataFrame].
 * @see [DataFrame.schema]
 */
public inline fun <reified T> DataFrame<T>.compileTimeSchema(ordered: Boolean = true): DataFrameSchema =
    compileTimeSchemaImpl(if (ordered) schema() else null, T::class)

// endregion
