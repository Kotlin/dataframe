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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.typeOf

/**
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 */
@ExcludeFromSources
private typealias UncheckedCastDocs = Nothing

/**
 * With `verify = true` (the default), checks column types and nested schemas against the schema declared by `T`.
 * Column order is ignored. A column type mismatch or a missing required column causes verification to fail.
 * Existing values are not converted.
 *
 * Missing nullable [value columns][ValueColumn] are added and filled with `null`.
 * Missing [column groups][ColumnGroup] are added if their nested columns can be filled with `null` or empty dataframes.
 * Missing [frame columns][FrameColumn] are filled with empty dataframes of the target schema.
 * Extra top-level columns are kept for a non-empty input.
 * An empty input produces an empty dataframe with the target schema.
 *
 * With `verify = false`, returns the same [DataFrame] with only its schema type argument changed; no schema check
 * is performed.
 */
@ExcludeFromSources
private typealias VerifiedCastDocs = Nothing

/**
 * `df` is a [DataFrame] with columns `name: String` and `age: Int`.
 */
@ExcludeFromSources
private typealias CastSampleDataDocs = Nothing

/**
 * Returns this [DataFrame] with [T] as its schema type argument.
 *
 * @include [UncheckedCastDocs]
 *
 * Column types, values, and the runtime schema remain unchanged.
 *
 * See also:
 * - [cast][DataFrame.cast] with `verify = true` to check the target schema.
 * - [castTo][DataFrame.castTo] to infer the schema type argument.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.uncheckedCast]
 *
 * @param [T] The schema marker type to assign to this [DataFrame].
 * @return This [DataFrame] typed as [DataFrame]`<T>`.
 */
@Check
public fun <T> DataFrame<*>.cast(): DataFrame<T> = this as DataFrame<T>

/**
 * Returns a [DataFrame] with schema marker [T], optionally verifying its columns against that schema.
 *
 * @include [VerifiedCastDocs]
 *
 * See also:
 * - [convertTo][DataFrame.convertTo] to convert values.
 * - [castTo][DataFrame.castTo] to infer [T].
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.verifiedCast]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castWithoutVerification]
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the columns against [T]. Defaults to `true`.
 * @return A [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [T].
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
 * Returns a [DataFrame] with schema marker [T] inferred from [schemaFrom].
 *
 * Uses the type argument `T` of [schemaFrom] as the target schema marker.
 * The runtime schema and values of [schemaFrom] are not inspected.
 *
 * @include [VerifiedCastDocs]
 *
 * See also:
 * - [cast][DataFrame.cast] to specify [T] explicitly.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castToDataFrame]
 *
 * @param [T] The schema marker type inferred from [schemaFrom].
 * @param [schemaFrom] A typed [DataFrame] that supplies [T].
 * @param [verify] Whether to check this [DataFrame] against [T]. Defaults to `true`.
 * @return A [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns a [DataFrame] with schema marker [T] inferred from the return type of [schemaFrom].
 *
 * The function is not invoked. This is useful when the compiler plugin generated a local schema marker that cannot
 * be named as a type argument to [cast].
 *
 * @include [VerifiedCastDocs]
 *
 * See also:
 * - [cast][DataFrame.cast] to specify [T] explicitly.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The `createPeopleDf` function returns a dataframe with a schema marker generated by the compiler plugin.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castToFunction]
 *
 * @param [T] The schema marker type inferred from the function's return type.
 * @param [schemaFrom] A function whose return type supplies [T].
 * @param [verify] Whether to check this [DataFrame] against [T]. Defaults to `true`.
 * @return A [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification fails because the dataframe's schema is incompatible with [T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: Function<DataFrame<T>>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns this [DataRow] with [T] as its schema type argument.
 *
 * @include [UncheckedCastDocs]
 *
 * The row index, column types, and values remain unchanged.
 *
 * See also [cast][DataRow.cast] with `verify = true` to check the containing dataframe's schema.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.uncheckedRowCast]
 *
 * @param [T] The schema marker type to assign to this [DataRow].
 * @return This [DataRow] typed as [DataRow]`<T>`.
 */
public fun <T> DataRow<*>.cast(): DataRow<T> = this as DataRow<T>

/**
 * Returns a [DataRow] with schema marker [T], optionally verifying its containing dataframe's schema.
 *
 * With `verify = true`, checks the schema of the containing [DataFrame] as described by [DataFrame.cast].
 * With `verify = false`, no schema check is performed.
 * The row index is preserved, and existing values are not converted in either mode.
 *
 * See also:
 * - [cast][DataFrame.cast] to cast an entire dataframe.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.rowCast]
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the containing [DataFrame] against [T]. Defaults to `true`.
 * @return A [DataRow]`<T>` at the same row index.
 * @throws [IllegalArgumentException] if verification fails because the containing dataframe's schema
 * is incompatible with [T].
 */
public inline fun <reified T> DataRow<*>.cast(verify: Boolean = true): DataRow<T> = df().cast<T>(verify)[index()]

/**
 * Returns this column as a [DataColumn]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.dataColumnCast]
 *
 * @param [T] The value type to use at compile time.
 * @return This [DataColumn] typed as [DataColumn]`<T>`.
 */
@Interpretable("AnyColCast")
public fun <T> DataColumn<*>.cast(): DataColumn<T> = this as DataColumn<T>

/**
 * Returns this column as a [ValueColumn]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.valueColumnCast]
 *
 * @param [T] The value type to use at compile time.
 * @return This [ValueColumn] typed as [ValueColumn]`<T>`.
 */
public fun <T> ValueColumn<*>.cast(): ValueColumn<T> = this as ValueColumn<T>

/**
 * Returns this column as a [FrameColumn]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * The nested dataframes are not checked against [T], even if they have different runtime schemas.
 * Their columns and the frame column's runtime [schema][FrameColumn.schema] remain unchanged.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.frameColumnCast]
 *
 * @param [T] The schema marker to use at compile time for the dataframes in this column.
 * @return This [FrameColumn] typed as [FrameColumn]`<T>`.
 */
public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

/**
 * Returns this group as a [ColumnGroup]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * The nested columns, their types, and their structure are preserved without checking them against [T].
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * The schema is declared as `@DataSchema data class Person(val name: String, val age: Int)`.
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.columnGroupCast]
 *
 * @param [T] The schema marker to use at compile time for this group.
 * @return This [ColumnGroup] typed as [ColumnGroup]`<T>`.
 */
public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this column as a [ColumnWithPath]`<T>`, preserving its path.
 *
 * @include [UncheckedCastDocs]
 *
 * @param [T] The value type to use at compile time.
 * @return This [ColumnWithPath] typed as [ColumnWithPath]`<T>`.
 */
public fun <T> ColumnWithPath<*>.cast(): ColumnWithPath<T> = this as ColumnWithPath<T>

/**
 * Returns this accessor as a [ColumnAccessor]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * @param [T] The expected value type of the referenced column.
 * @return This [ColumnAccessor] typed as [ColumnAccessor]`<T>`.
 */
@Interpretable("ColumnAccessorCast")
public fun <T> ColumnAccessor<*>.cast(): ColumnAccessor<T> = this as ColumnAccessor<T>

/**
 * Returns this set as a [ColumnSet]`<C>`.
 *
 * @include [UncheckedCastDocs]
 *
 * The resolved columns may have different runtime types; those types are not checked or changed.
 *
 * @param [C] The common value type to use at compile time for columns resolved by this set.
 * @return This [ColumnSet] typed as [ColumnSet]`<C>`.
 */
@Interpretable("ColumnSetCast")
public fun <C> ColumnSet<*>.cast(): ColumnSet<C> = this as ColumnSet<C>

/**
 * Returns this resolver as a [ColumnsResolver]`<C>`.
 *
 * @include [UncheckedCastDocs]
 *
 * The resolved columns may have different runtime types; those types are not checked or changed.
 *
 * @param [C] The common value type to use at compile time for columns resolved by this resolver.
 * @return This [ColumnsResolver] typed as [ColumnsResolver]`<C>`.
 */
public fun <C> ColumnsResolver<*>.cast(): ColumnsResolver<C> = this as ColumnsResolver<C>

/**
 * Returns this resolver as a [SingleColumn]`<C>`.
 *
 * @include [UncheckedCastDocs]
 *
 * @param [C] The expected value type of the resolved column.
 * @return This [SingleColumn] typed as [SingleColumn]`<C>`.
 */
@Interpretable("SingleColumnCast")
public fun <C> SingleColumn<*>.cast(): SingleColumn<C> = this as SingleColumn<C>

/**
 * Returns this reference as a [ColumnReference]`<C>`.
 *
 * @include [UncheckedCastDocs]
 *
 * @param [C] The expected value type of the referenced column.
 * @return This [ColumnReference] typed as [ColumnReference]`<C>`.
 */
public fun <C> ColumnReference<*>.cast(): ColumnReference<C> = this as ColumnReference<C>

/**
 * Returns this [GroupBy] as a [GroupBy]`<T, G>`.
 *
 * @include [UncheckedCastDocs]
 *
 * ### Examples
 *
 * The example uses `@DataSchema data class AgeKey(val age: Int)` for the grouping keys and
 * `@DataSchema data class Person(val name: String, val age: Int)` for the groups.
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.groupByCast]
 *
 * @param [T] The schema marker to use at compile time for the grouping keys.
 * @param [G] The schema marker to use at compile time for the groups.
 * @return This [GroupBy] typed as [GroupBy]`<T, G>`.
 */
public fun <T, G> GroupBy<*, *>.cast(): GroupBy<T, G> = this as GroupBy<T, G>
