@file:Suppress("UNCHECKED_CAST")

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Check
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
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
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import org.jetbrains.kotlinx.dataframe.impl.api.verifyGroupBySchemaImpl
import org.jetbrains.kotlinx.dataframe.util.CAST_UNCHECKED
import kotlin.reflect.typeOf

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<*>.cast(): DataFrame<T> = this as DataFrame<T>

/**
 * Returns this [<code>DataFrame</code>][DataFrame] with [<code>T</code>][T] as its schema type argument.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * Column types, values, and the runtime schema remain unchanged.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
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
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.castUnchecked<Person>()
 * ```
 *
 * @param [T] The schema marker type to assign to this [<code>DataFrame</code>][DataFrame].
 * @return This [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 */
public fun <T> DataFrame<*>.castUnchecked(): DataFrame<T> = this as DataFrame<T>

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T], verifying its columns against that schema by default.
 *
 * With [verify] = `true` (the default), checks column types and nested schemas against the schema declared by [T].
 * Column order is ignored, actual column types may be subtypes of the declared types, and extra columns are allowed.
 * An incompatible schema causes verification to fail.
 *
 * With [verify] = `false`, no schema check is performed.
 * In both modes, returns the receiver unchanged except for its schema type argument:
 * no columns are added, removed, or reordered, and no values are converted.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also:
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 * - [<code>castTo</code>][DataFrame.castTo] to infer [<code>T</code>][T].
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.cast<Person>()
 * ```
 *
 * ```kotlin
 * df.cast<Person>(verify = true)
 *     .filter { age > 21 }
 *     .name
 * ```
 *
 * ```kotlin
 * df.cast<Person>(verify = false)
 * ```
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the columns against [<code>T</code>][T]. Defaults to `true`.
 * @return This [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds the dataframe's schema incompatible with [<code>T</code>][T].
 */
@Check
public inline fun <reified T> DataFrame<*>.cast(verify: Boolean = true): DataFrame<T> {
    if (verify) {
        convertToImpl(
            typeOf<T>(),
            allowConversion = false,
            ExcessiveColumns.Keep,
        )
    }
    return castUnchecked()
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T] inferred from [<code>schemaFrom</code>][schemaFrom].
 *
 * Uses the type argument `T` of [<code>schemaFrom</code>][schemaFrom] as the target schema marker.
 * The runtime schema and values of [<code>schemaFrom</code>][schemaFrom] are not inspected.
 *
 * With [verify] = `true` (the default), checks column types and nested schemas against the schema declared by [T].
 * Column order is ignored, actual column types may be subtypes of the declared types, and extra columns are allowed.
 * An incompatible schema causes verification to fail.
 *
 * With [verify] = `false`, no schema check is performed.
 * In both modes, returns the receiver unchanged except for its schema type argument:
 * no columns are added, removed, or reordered, and no values are converted.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] to specify [<code>T</code>][T] explicitly.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
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
 * @return This [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this dataframe's schema incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with schema marker [<code>T</code>][T] inferred from the return type of [<code>schemaFrom</code>][schemaFrom].
 *
 * The function is not invoked.
 *
 * With the compiler plugin, the schema marker of a dataframe can be a local type generated by the plugin.
 * Such a type cannot be referred to directly, for example as a type argument of [<code>cast</code>][cast].
 * This overload helps by inferring it from the return type of [<code>schemaFrom</code>][schemaFrom], for example,
 * to restore the schema marker of a dataframe that was read back as `DataFrame<*>` after serialization.
 *
 * With [verify] = `true` (the default), checks column types and nested schemas against the schema declared by [T].
 * Column order is ignored, actual column types may be subtypes of the declared types, and extra columns are allowed.
 * An incompatible schema causes verification to fail.
 *
 * With [verify] = `false`, no schema check is performed.
 * In both modes, returns the receiver unchanged except for its schema type argument:
 * no columns are added, removed, or reordered, and no values are converted.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
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
 * In the following example, `convert` returns a dataframe with a local schema marker generated by the compiler plugin.
 * `deserializeAndUse` restores this marker after deserialization:
 *
 * ```kotlin
 * // parse listOf("b:1:abc", "c:2:bca")
 * // Return type: DataFrame<plugin generated local type>
 * private fun convert(data: List<String>) =
 *     data.map { it.split(":") }.toDataFrame {
 *         "part1" from { it[0] }
 *         "part2" from { it[1].toInt() }
 *         "part3" from { it[2] }
 *     }
 *
 * fun serialize(data: List<String>): String = convert(data).toJson()
 *
 * fun deserializeAndUse(json: String): DataColumn<String> {
 *     val df = DataFrame.readJsonStr(json).castTo(schemaFrom = ::convert)
 *     // Possible to use properties
 *     return df.part1
 * }
 * ```
 *
 * @param [T] The schema marker type inferred from the function's return type.
 * @param [schemaFrom] A function whose return type supplies [<code>T</code>][T].
 * @param [verify] Whether to check this [<code>DataFrame</code>][DataFrame] against [<code>T</code>][T]. Defaults to `true`.
 * @return This [<code>DataFrame</code>][DataFrame] typed as [<code>DataFrame</code>][DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this dataframe's schema incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: Function<DataFrame<T>>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> DataRow<*>.cast(): DataRow<T> = this as DataRow<T>

/**
 * Returns this [<code>DataRow</code>][DataRow] with [<code>T</code>][T] as its schema type argument.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The row index, column types, and values remain unchanged.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also [<code>cast</code>][DataRow.cast] with `verify = true` to check the containing dataframe's schema.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * val row = df[0]
 * row.castUnchecked<Person>()
 * ```
 *
 * @param [T] The schema marker type to assign to this [<code>DataRow</code>][DataRow].
 * @return This [<code>DataRow</code>][DataRow] typed as [<code>DataRow</code>][DataRow]`<T>`.
 */
public fun <T> DataRow<*>.castUnchecked(): DataRow<T> = this as DataRow<T>

/**
 * Returns a [<code>DataRow</code>][DataRow] with schema marker [<code>T</code>][T], verifying its containing dataframe's schema by default.
 *
 * With `verify = true` (the default), checks the schema of the containing [<code>DataFrame</code>][DataFrame] as described by [<code>DataFrame.cast</code>][DataFrame.cast].
 * With `verify = false`, no schema check is performed.
 * The row index is preserved, and existing values are not converted in either mode.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also:
 * - [<code>cast</code>][DataFrame.cast] to cast an entire dataframe.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df[0].cast<Person>()
 * ```
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the containing [<code>DataFrame</code>][DataFrame] against [<code>T</code>][T]. Defaults to `true`.
 * @return A [<code>DataRow</code>][DataRow]`<T>` at the same row index.
 * @throws [IllegalArgumentException] if verification fails under the schema compatibility rules of [<code>DataFrame.cast</code>][DataFrame.cast].
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
 * [<code>T</code>][T] is the schema marker of the dataframes stored in this column: a type, usually an interface or a data class
 * annotated with [<code>`@DataSchema`</code>][DataSchema], whose properties describe their columns.
 * Each dataframe in the resulting column is typed as [<code>DataFrame</code>][DataFrame]`<T>`, so the
 * [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi] generated for [<code>T</code>][T] become available on it.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
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

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this group as a [<code>ColumnGroup</code>][ColumnGroup]`<T>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * The nested columns, their types, and their structure are preserved without checking them against [<code>T</code>][T].
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also [<code>cast</code>][ColumnGroup.cast] with `verify = true` to check the target schema.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * @param [T] The schema marker to use at compile time for this group.
 * @return This [<code>ColumnGroup</code>][ColumnGroup] typed as [<code>ColumnGroup</code>][ColumnGroup]`<T>`.
 */
public fun <T> ColumnGroup<*>.castUnchecked(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this group as a [<code>ColumnGroup</code>][ColumnGroup]`<T>`, verifying its nested columns against [<code>T</code>][T] by default.
 *
 * With [verify] = `true` (the default), checks column types and nested schemas against the schema declared by [T].
 * Column order is ignored, actual column types may be subtypes of the declared types, and extra columns are allowed.
 * An incompatible schema causes verification to fail.
 *
 * With [verify] = `false`, no schema check is performed.
 * In both modes, returns the receiver unchanged except for its schema type argument:
 * no columns are added, removed, or reordered, and no values are converted.
 *
 * [T] is a schema marker: a type, usually an interface or a data class annotated with [<code>`@DataSchema`</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T] makes the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * generated for [T] available on the result, so its columns can be accessed in a type-safe way.
 *
 * When no schema check is performed, [T] is not guaranteed to match the runtime schema.
 * Accessing a missing column or a column with a different type through the extension properties of [T]
 * may fail at runtime.
 *
 * See also:
 * - [<code>castUnchecked</code>][ColumnGroup.castUnchecked] to change the schema type argument without verification.
 * - [<code>convertTo</code>][DataFrame.convertTo] to convert values.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema interface Person { val name: String; val age: Int }`.
 *
 * `df` is a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with columns `name: String` and `age: Int`.
 *
 * ```kotlin
 * df.asColumnGroup().cast<Person>()
 * ```
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the nested columns against [<code>T</code>][T]. Defaults to `true`.
 * @return This [<code>ColumnGroup</code>][ColumnGroup] typed as [<code>ColumnGroup</code>][ColumnGroup]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this group's schema incompatible with [<code>T</code>][T].
 */
public inline fun <reified T> ColumnGroup<*>.cast(verify: Boolean = true): ColumnGroup<T> {
    if (verify) {
        convertToImpl(typeOf<T>(), allowConversion = false, ExcessiveColumns.Keep)
    }
    return castUnchecked()
}

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

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<*, *>.cast(): GroupBy<T, G> = this as GroupBy<T, G>

/**
 * Returns this [<code>GroupBy</code>][GroupBy] as a [<code>GroupBy</code>][GroupBy]`<T, G>`.
 *
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 *
 * [T] and [G] are schema markers for the grouping keys and groups, respectively.
 * They make the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] generated for these schemas available
 * for the grouping keys and groups.
 *
 * The type arguments are not checked against the runtime schemas. Accessing incompatible keys or group columns
 * through their extension properties may fail at runtime.
 *
 * See also [<code>cast</code>][GroupBy.cast] with `verify = true` to check the target schemas.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * @param [T] The schema marker to use at compile time for the grouping keys.
 * @param [G] The schema marker to use at compile time for the groups.
 * @return This [<code>GroupBy</code>][GroupBy] typed as [<code>GroupBy</code>][GroupBy]`<T, G>`.
 */
public fun <T, G> GroupBy<*, *>.castUnchecked(): GroupBy<T, G> = this as GroupBy<T, G>

/**
 * Returns this [<code>GroupBy</code>][GroupBy] as a [<code>GroupBy</code>][GroupBy]`<T, G>`, verifying its key and group schemas by default.
 *
 * With `verify = true` (the default), checks the key columns against [<code>T</code>][T] and every group against [<code>G</code>][G], using the
 * schema compatibility rules of [<code>DataFrame.cast</code>][DataFrame.cast].
 *
 * With `verify = false`, no schema check is performed. In both modes, returns this [<code>GroupBy</code>][GroupBy] unchanged except for
 * its type arguments: keys, groups, and values are not modified or converted.
 *
 * [T] and [G] are schema markers for the grouping keys and groups, respectively.
 * They make the [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] generated for these schemas available
 * for the grouping keys and groups.
 *
 * When no schema check is performed, accessing keys or group columns that do not match the assigned schema markers
 * through their extension properties may fail at runtime.
 *
 * See also:
 * - [<code>castUnchecked</code>][GroupBy.castUnchecked] to change the schema type arguments without verification.
 * - [<code>cast</code>][DataFrame.cast] to cast a dataframe.
 *
 * For more information: [See `cast` on the documentation website.](https://kotlin.github.io/dataframe/cast.html)
 *
 * ### Examples
 *
 * The example uses `@DataSchema data class AgeKey(val age: Int)` for the grouping keys and
 * `@DataSchema interface Person { val name: String; val age: Int }` for the groups.
 *
 * ```kotlin
 * df.groupBy("age").cast<AgeKey, Person>()
 * ```
 *
 * @param [T] The target schema marker for the grouping keys.
 * @param [G] The target schema marker for the groups.
 * @param [verify] Whether to check the keys against [<code>T</code>][T] and every group against [<code>G</code>][G]. Defaults to `true`.
 * @return This [<code>GroupBy</code>][GroupBy] typed as [<code>GroupBy</code>][GroupBy]`<T, G>`.
 * @throws [IllegalArgumentException] if the key schema or any group schema fails verification under the schema
 * compatibility rules of [<code>DataFrame.cast</code>][DataFrame.cast].
 */
public inline fun <reified T, reified G> GroupBy<*, *>.cast(verify: Boolean = true): GroupBy<T, G> {
    if (verify) {
        verifyGroupBySchemaImpl(typeOf<T>(), typeOf<G>())
    }
    return castUnchecked()
}
