@file:Suppress("UNCHECKED_CAST")

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Check
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.typeOf

/**
 * Returns this [<code>DataFrame</code>][DataFrame] with [<code>T</code>][T] as its schema type argument.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * Column types, values, and the runtime schema remain unchanged.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] with `verify = true` to check the target schema.
 * - [<code>castTo</code>][DataFrame.castTo] to infer the schema type argument.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.cast<Person>()
 * ```
 *
 * @param [T] The schema marker type to assign to this [<code>DataFrame</code>][DataFrame].
 * @return This [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 */
@Check
public fun <T> DataFrame<*>.cast(): DataFrame<T> = this as DataFrame<T>

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T], optionally verifying its columns against that schema.
 *
 * With `verify = true` (the default), checks column types and nested schemas against the schema declared by `T`.
 * Column order is ignored. A column type mismatch or a missing required column causes verification to fail.
 * Existing values are not converted.
 *
 * Missing nullable [<code>value columns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] are added and filled with `null`.
 * Missing [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are added if their nested columns can be filled with `null` or empty dataframes.
 * Missing [<code>frame columns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are filled with empty dataframes of the target schema.
 * Extra top-level columns are kept for a non-empty input.
 * An empty input produces an empty dataframe with the target schema.
 *
 * With `verify = false`, returns the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only its schema type argument changed; no schema check
 * is performed.
 *
 * See also:
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 * - [<code>castTo</code>][DataFrame.castTo] to infer [<code>T</code>][T].
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.cast<Person>(verify = true)
 * ```
 *
 * ```kotlin
 * df.cast<Person>(verify = false)
 * ```
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the columns against [<code>T</code>][T]. Defaults to `true`.
 * @return A [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataFrame<*>.cast(verify: Boolean = true): DataFrame<T> =
    if (verify) {
        convertToImpl(
            typeOf<T>(),
            allowConversion = false,
            ExcessiveColumns.Keep,
        ).cast()
    } else {
        cast()
    }

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T] inferred from [<code>schemaFrom</code>][schemaFrom].
 *
 * Uses the type argument `T` of [<code>schemaFrom</code>][schemaFrom] as the target schema marker.
 * The runtime schema and values of [<code>schemaFrom</code>][schemaFrom] are not inspected.
 *
 * With `verify = true` (the default), checks column types and nested schemas against the schema declared by `T`.
 * Column order is ignored. A column type mismatch or a missing required column causes verification to fail.
 * Existing values are not converted.
 *
 * Missing nullable [<code>value columns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] are added and filled with `null`.
 * Missing [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are added if their nested columns can be filled with `null` or empty dataframes.
 * Missing [<code>frame columns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are filled with empty dataframes of the target schema.
 * Extra top-level columns are kept for a non-empty input.
 * An empty input produces an empty dataframe with the target schema.
 *
 * With `verify = false`, returns the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only its schema type argument changed; no schema check
 * is performed.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] to specify [<code>T</code>][T] explicitly.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * val schemaSource = dataFrameOf(
 *     "name" to columnOf("Charlie"),
 *     "age" to columnOf(30),
 * ).cast<Person>(verify = true)
 * df.castTo(schemaFrom = schemaSource, verify = true)
 * ```
 *
 * @param [T] The schema marker type inferred from [<code>schemaFrom</code>][schemaFrom].
 * @param [schemaFrom] A typed [<code>DataFrame</code>][DataFrame] that supplies [<code>T</code>][T].
 * @param [verify] Whether to check this [<code>DataFrame</code>][DataFrame] against [<code>T</code>][T]. Defaults to `true`.
 * @return A [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T] inferred from the return type of [<code>schemaFrom</code>][schemaFrom].
 *
 * The function is not invoked. This is useful when the compiler plugin generated a local schema marker that cannot
 * be named as a type argument to [<code>cast</code>][cast].
 *
 * With `verify = true` (the default), checks column types and nested schemas against the schema declared by `T`.
 * Column order is ignored. A column type mismatch or a missing required column causes verification to fail.
 * Existing values are not converted.
 *
 * Missing nullable [<code>value columns</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] are added and filled with `null`.
 * Missing [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are added if their nested columns can be filled with `null` or empty dataframes.
 * Missing [<code>frame columns</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are filled with empty dataframes of the target schema.
 * Extra top-level columns are kept for a non-empty input.
 * An empty input produces an empty dataframe with the target schema.
 *
 * With `verify = false`, returns the same [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only its schema type argument changed; no schema check
 * is performed.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] to specify [<code>T</code>][T] explicitly.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The `createPeopleDf` function returns a dataframe with a schema marker generated by the compiler plugin.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * fun createPeopleDf(age: Int) =
 *     dataFrameOf(
 *         "age" to columnOf(age),
 *         "name" to columnOf("Sample"),
 *     )
 *
 * df.castTo(schemaFrom = ::createPeopleDf, verify = true)
 * ```
 *
 * @param [T] The schema marker type inferred from the function's return type.
 * @param [schemaFrom] A function whose return type supplies [<code>T</code>][T].
 * @param [verify] Whether to check this [<code>DataFrame</code>][DataFrame] against [<code>T</code>][T]. Defaults to `true`.
 * @return A [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: Function<DataFrame<T>>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns this [<code>DataRow</code>][DataRow] with [<code>T</code>][T] as its schema type argument.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The row index, column types, and values remain unchanged.
 *
 * See also [<code>cast</code>][DataRow.cast] with `verify = true` to check the containing dataframe's schema.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * val row = df[0]
 * row.cast<Person>()
 * ```
 *
 * @param [T] The schema marker type to assign to this [<code>DataRow</code>][DataRow].
 * @return This [<code>DataRow</code>][DataRow] typed as [<code>DataRow</code>][DataRow]`<T>`.
 */
public fun <T> DataRow<*>.cast(): DataRow<T> = this as DataRow<T>

/**
 * Returns a [<code>DataRow</code>][DataRow] with schema marker [<code>T</code>][T], optionally verifying its containing dataframe's schema.
 *
 * With `verify = true`, checks the schema of the containing [<code>DataFrame</code>][DataFrame] as described by [<code>DataFrame.cast</code>][DataFrame.cast].
 * With `verify = false`, no schema check is performed.
 * The row index is preserved, and existing values are not converted in either mode.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] to cast an entire dataframe.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df[0].cast<Person>(verify = true)
 * ```
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the containing [<code>DataFrame</code>][DataFrame] against [<code>T</code>][T]. Defaults to `true`.
 * @return A [<code>DataRow</code>][DataRow]`<T>` at the same row index.
 * @throws [IllegalArgumentException] if verification fails because the containing dataframe's schema
 * is incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataRow<*>.cast(verify: Boolean = true): DataRow<T> = df().cast<T>(verify)[index()]

/**
 * Returns this column as a [<code>DataColumn</code>][DataColumn]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df["age"].cast<Int>()
 * ```
 *
 * @param [T] The value type to use at compile time.
 * @return This [<code>DataColumn</code>][DataColumn] typed as [<code>DataColumn</code>][DataColumn]`<T>`.
 */
@Interpretable("AnyColCast")
public fun <T> DataColumn<*>.cast(): DataColumn<T> = this as DataColumn<T>

/**
 * Returns this column as a [<code>ValueColumn</code>][ValueColumn]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df["age"].asValueColumn().cast<Int>()
 * ```
 *
 * @param [T] The value type to use at compile time.
 * @return This [<code>ValueColumn</code>][ValueColumn] typed as [<code>ValueColumn</code>][ValueColumn]`<T>`.
 */
public fun <T> ValueColumn<*>.cast(): ValueColumn<T> = this as ValueColumn<T>

/**
 * Returns this column as a [<code>FrameColumn</code>][FrameColumn]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The nested dataframes are not checked against [<code>T</code>][T], even if they have different runtime schemas.
 * Their columns and the frame column's runtime [<code>schema</code>][FrameColumn.schema] remain unchanged.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * columnOf(df).named("people").castFrameColumn<Person>()
 * ```
 *
 * @param [T] The schema marker to use at compile time for the dataframes in this column.
 * @return This [<code>FrameColumn</code>][FrameColumn] typed as [<code>FrameColumn</code>][FrameColumn]`<T>`.
 */
public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

/**
 * Returns this group as a [<code>ColumnGroup</code>][ColumnGroup]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The nested columns, their types, and their structure are preserved without checking them against [<code>T</code>][T].
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.asColumnGroup().cast<Person>()
 * ```
 *
 * @param [T] The schema marker to use at compile time for this group.
 * @return This [<code>ColumnGroup</code>][ColumnGroup] typed as [<code>ColumnGroup</code>][ColumnGroup]`<T>`.
 */
public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this column as a [<code>ColumnWithPath</code>][ColumnWithPath]`<T>`, preserving its path.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * @param [T] The value type to use at compile time.
 * @return This [<code>ColumnWithPath</code>][ColumnWithPath] typed as [<code>ColumnWithPath</code>][ColumnWithPath]`<T>`.
 */
public fun <T> ColumnWithPath<*>.cast(): ColumnWithPath<T> = this as ColumnWithPath<T>

/**
 * Returns this accessor as a [<code>ColumnAccessor</code>][ColumnAccessor]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * @param [T] The expected value type of the referenced column.
 * @return This [<code>ColumnAccessor</code>][ColumnAccessor] typed as [<code>ColumnAccessor</code>][ColumnAccessor]`<T>`.
 */
@Interpretable("ColumnAccessorCast")
public fun <T> ColumnAccessor<*>.cast(): ColumnAccessor<T> = this as ColumnAccessor<T>

/**
 * Returns this set as a [<code>ColumnSet</code>][ColumnSet]`<C>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The resolved columns may have different runtime types; those types are not checked or changed.
 *
 * @param [C] The common value type to use at compile time for columns resolved by this set.
 * @return This [<code>ColumnSet</code>][ColumnSet] typed as [<code>ColumnSet</code>][ColumnSet]`<C>`.
 */
@Interpretable("ColumnSetCast")
public fun <C> ColumnSet<*>.cast(): ColumnSet<C> = this as ColumnSet<C>

/**
 * Returns this resolver as a [<code>ColumnsResolver</code>][ColumnsResolver]`<C>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The resolved columns may have different runtime types; those types are not checked or changed.
 *
 * @param [C] The common value type to use at compile time for columns resolved by this resolver.
 * @return This [<code>ColumnsResolver</code>][ColumnsResolver] typed as [<code>ColumnsResolver</code>][ColumnsResolver]`<C>`.
 */
public fun <C> ColumnsResolver<*>.cast(): ColumnsResolver<C> = this as ColumnsResolver<C>

/**
 * Returns this resolver as a [<code>SingleColumn</code>][SingleColumn]`<C>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * @param [C] The expected value type of the resolved column.
 * @return This [<code>SingleColumn</code>][SingleColumn] typed as [<code>SingleColumn</code>][SingleColumn]`<C>`.
 */
@Interpretable("SingleColumnCast")
public fun <C> SingleColumn<*>.cast(): SingleColumn<C> = this as SingleColumn<C>

/**
 * Returns this reference as a [<code>ColumnReference</code>][ColumnReference]`<C>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * @param [C] The expected value type of the referenced column.
 * @return This [<code>ColumnReference</code>][ColumnReference] typed as [<code>ColumnReference</code>][ColumnReference]`<C>`.
 */
public fun <C> ColumnReference<*>.cast(): ColumnReference<C> = this as ColumnReference<C>

/**
 * Returns this [<code>GroupBy</code>][GroupBy] as a [<code>GroupBy</code>][GroupBy]`<T, G>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * ### Examples
 *
 * The example uses `@DataSchema data class AgeKey(val age: Int)` for the grouping keys and
 * `@DataSchema data class Person(val name: String, val age: Int)` for the groups.
 *
 * ```kotlin
 * df.groupBy("age").cast<AgeKey, Person>()
 * ```
 *
 * @param [T] The schema marker to use at compile time for the grouping keys.
 * @param [G] The schema marker to use at compile time for the groups.
 * @return This [<code>GroupBy</code>][GroupBy] typed as [<code>GroupBy</code>][GroupBy]`<T, G>`.
 */
public fun <T, G> GroupBy<*, *>.cast(): GroupBy<T, G> = this as GroupBy<T, G>
