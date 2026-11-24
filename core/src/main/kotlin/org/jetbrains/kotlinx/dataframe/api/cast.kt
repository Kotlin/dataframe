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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import org.jetbrains.kotlinx.dataframe.impl.api.verifyGroupBySchemaImpl
import org.jetbrains.kotlinx.dataframe.util.CAST_UNCHECKED
import kotlin.reflect.typeOf

/**
 * Changes only the static type arguments; no runtime validation or value conversion is performed.
 */
@ExcludeFromSources
private typealias UncheckedCastDocs = Nothing

/**
 * With [verify\] = `true` (the default), checks column types and nested schemas against the schema declared by [T\].
 * Column order is ignored, actual column types may be subtypes of the declared types, and extra columns are allowed.
 * An incompatible schema causes verification to fail.
 *
 * With [verify\] = `false`, no schema check is performed.
 * In both modes, returns the receiver unchanged except for its schema type argument:
 * no columns are added, removed, or reordered, and no values are converted.
 */
@ExcludeFromSources
private typealias VerifiedCastDocs = Nothing

/**
 * The examples use the following schema:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.PersonSchema]
 */
@ExcludeFromSources
private typealias CastPersonSchemaDocs = Nothing

/**
 * `df` is a [DataFrame] with columns `name: String` and `age: Int`.
 */
@ExcludeFromSources
private typealias CastSampleDataDocs = Nothing

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<*>.cast(): DataFrame<T> = this as DataFrame<T>

/**
 * [T\] is a schema marker: a type, usually an interface or a data class annotated with [`@DataSchema`][DataSchema],
 * whose properties describe the columns.
 * Changing the schema type argument to [T\] makes the [extension properties][AccessApis.ExtensionPropertiesApi]
 * generated for [T\] available on the result, so its columns can be accessed in a type-safe way.
 */
@ExcludeFromSources
private typealias CastSchemaMarkerDocs = Nothing

/**
 * When no schema check is performed, [T\] is not guaranteed to match the receiver's columns.
 * Accessing a missing column or using a column with an incompatible type through an extension property of [T\]
 * may throw an exception.
 */
@ExcludeFromSources
private typealias CastRuntimeFailureDocs = Nothing

/**
 * [T\] and [G\] are schema markers for the grouping keys and groups, respectively.
 * They make the [extension properties][AccessApis.ExtensionPropertiesApi] generated for these schemas available
 * for the grouping keys and groups.
 */
@ExcludeFromSources
private typealias GroupByCastSchemaMarkerDocs = Nothing

/**
 * When no schema check is performed, [T\] and [G\] are not guaranteed to match the key and group columns.
 * Accessing a missing key or group column, or using a column with an incompatible type through an extension property,
 * may throw an exception.
 */
@ExcludeFromSources
private typealias GroupByCastRuntimeFailureDocs = Nothing

/**
 * The example uses the following schemas for grouping keys and groups, respectively:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.AgeKeySchema]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.PersonSchema]
 */
@ExcludeFromSources
private typealias GroupByCastSampleSchemasDocs = Nothing

/**
 * Returns this [DataFrame] with [T] as its schema type argument.
 *
 * @include [UncheckedCastDocs]
 *
 * Column types and values remain unchanged.
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
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
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.uncheckedCast]
 *
 * @param [T] The schema marker type to assign to this [DataFrame].
 * @return This [DataFrame] typed as [DataFrame]`<T>`.
 */
public fun <T> DataFrame<*>.castUnchecked(): DataFrame<T> = this as DataFrame<T>

/**
 * Returns a [DataFrame] with schema marker [T], verifying its columns against that schema by default.
 *
 * @include [VerifiedCastDocs]
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also:
 * - [convertTo][DataFrame.convertTo] to convert values.
 * - [castTo][DataFrame.castTo] to infer [T].
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.verifiedCast]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castExtensionProperties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castWithoutVerification]
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the columns against [T]. Defaults to `true`.
 * @return This [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds the dataframe's schema incompatible with [T].
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
 * Returns a [DataFrame] with schema marker [T] inferred from [schemaFrom].
 *
 * Uses the type argument `T` of [schemaFrom] as the target schema marker.
 * The columns and values of [schemaFrom] are not inspected.
 *
 * @include [VerifiedCastDocs]
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also:
 * - [cast][DataFrame.cast] to specify [T] explicitly.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.castToDataFrame]
 *
 * @param [T] The schema marker type inferred from [schemaFrom].
 * @param [schemaFrom] A typed [DataFrame] that supplies [T].
 * @param [verify] Whether to check this [DataFrame] against [T]. Defaults to `true`.
 * @return This [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this dataframe's schema incompatible with [T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * Returns a [DataFrame] with schema marker [T] inferred from the return type of [schemaFrom].
 *
 * The function is not invoked.
 *
 * With the compiler plugin, the schema marker of a dataframe can be a local type generated by the plugin.
 * Such a type cannot be referred to directly, for example as a type argument of [cast].
 * This overload helps by inferring it from the return type of [schemaFrom], for example,
 * to restore the schema marker of a dataframe that was read back as `DataFrame<*>` after serialization.
 *
 * @include [VerifiedCastDocs]
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
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
 * In the following example, `convert` returns a dataframe with a local schema marker generated by the compiler plugin.
 * `deserializeAndUse` restores this marker after deserialization:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.CastToLocalSchema]
 *
 * @param [T] The schema marker type inferred from the function's return type.
 * @param [schemaFrom] A function whose return type supplies [T].
 * @param [verify] Whether to check this [DataFrame] against [T]. Defaults to `true`.
 * @return This [DataFrame] typed as [DataFrame]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this dataframe's schema incompatible with [T].
 */
public inline fun <reified T> DataFrame<*>.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: Function<DataFrame<T>>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> DataRow<*>.cast(): DataRow<T> = this as DataRow<T>

/**
 * Returns this [DataRow] with [T] as its schema type argument.
 *
 * @include [UncheckedCastDocs]
 *
 * The row index, column types, and values remain unchanged.
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also [cast][DataRow.cast] with `verify = true` to check the containing dataframe's schema.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.uncheckedRowCast]
 *
 * @param [T] The schema marker type to assign to this [DataRow].
 * @return This [DataRow] typed as [DataRow]`<T>`.
 */
public fun <T> DataRow<*>.castUnchecked(): DataRow<T> = this as DataRow<T>

/**
 * Returns a [DataRow] with schema marker [T], verifying its containing dataframe's schema by default.
 *
 * With `verify = true` (the default), checks the schema of the containing [DataFrame] as described by [DataFrame.cast].
 * With `verify = false`, no schema check is performed.
 * The row index is preserved, and existing values are not converted in either mode.
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also:
 * - [cast][DataFrame.cast] to cast an entire dataframe.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.rowCast]
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the containing [DataFrame] against [T]. Defaults to `true`.
 * @return A [DataRow]`<T>` at the same row index.
 * @throws [IllegalArgumentException] if verification fails under the schema compatibility rules of [DataFrame.cast].
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
 * The nested dataframes are not checked against [T], even if they have different schemas.
 * Their columns and the frame column's [schema][FrameColumn.schema] remain unchanged.
 *
 * [T] is the schema marker of the dataframes stored in this column: a type, usually an interface or a data class
 * annotated with [`@DataSchema`][DataSchema], whose properties describe their columns.
 * Each dataframe in the resulting column is typed as [DataFrame]`<T>`, so the
 * [extension properties][AccessApis.ExtensionPropertiesApi] generated for [T] become available on it.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.frameColumnCast]
 *
 * @param [T] The schema marker to use at compile time for the dataframes in this column.
 * @return This [FrameColumn] typed as [FrameColumn]`<T>`.
 */
public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this group as a [ColumnGroup]`<T>`.
 *
 * @include [UncheckedCastDocs]
 *
 * The nested columns, their types, and their structure are preserved without checking them against [T].
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also [cast][ColumnGroup.cast] with `verify = true` to check the target schema.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * @param [T] The schema marker to use at compile time for this group.
 * @return This [ColumnGroup] typed as [ColumnGroup]`<T>`.
 */
public fun <T> ColumnGroup<*>.castUnchecked(): ColumnGroup<T> = this as ColumnGroup<T>

/**
 * Returns this group as a [ColumnGroup]`<T>`, verifying its nested columns against [T] by default.
 *
 * @include [VerifiedCastDocs]
 *
 * @include [CastSchemaMarkerDocs]
 *
 * @include [CastRuntimeFailureDocs]
 *
 * See also:
 * - [castUnchecked][ColumnGroup.castUnchecked] to change the schema type argument without verification.
 * - [convertTo][DataFrame.convertTo] to convert values.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [CastPersonSchemaDocs]
 *
 * @include [CastSampleDataDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.columnGroupCast]
 *
 * @param [T] The target schema marker type.
 * @param [verify] Whether to check the nested columns against [T]. Defaults to `true`.
 * @return This [ColumnGroup] typed as [ColumnGroup]`<T>`.
 * @throws [IllegalArgumentException] if verification finds this group's schema incompatible with [T].
 */
public inline fun <reified T> ColumnGroup<*>.cast(verify: Boolean = true): ColumnGroup<T> {
    if (verify) {
        convertToImpl(typeOf<T>(), allowConversion = false, ExcessiveColumns.Keep)
    }
    return castUnchecked()
}

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

@Deprecated(CAST_UNCHECKED, level = DeprecationLevel.HIDDEN)
public fun <T, G> GroupBy<*, *>.cast(): GroupBy<T, G> = this as GroupBy<T, G>

/**
 * Returns this [GroupBy] as a [GroupBy]`<T, G>`.
 *
 * @include [UncheckedCastDocs]
 *
 * @include [GroupByCastSchemaMarkerDocs]
 *
 * The type arguments are not checked against the key and group columns.
 *
 * @include [GroupByCastRuntimeFailureDocs]
 *
 * See also [cast][GroupBy.cast] with `verify = true` to check the target schemas.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * @param [T] The schema marker to use at compile time for the grouping keys.
 * @param [G] The schema marker to use at compile time for the groups.
 * @return This [GroupBy] typed as [GroupBy]`<T, G>`.
 */
public fun <T, G> GroupBy<*, *>.castUnchecked(): GroupBy<T, G> = this as GroupBy<T, G>

/**
 * Returns this [GroupBy] as a [GroupBy]`<T, G>`, verifying its key and group schemas by default.
 *
 * With `verify = true` (the default), checks the key columns against [T] and every group against [G], using the
 * schema compatibility rules of [DataFrame.cast].
 *
 * With `verify = false`, no schema check is performed. In both modes, returns this [GroupBy] unchanged except for
 * its type arguments: keys, groups, and values are not modified or converted.
 *
 * @include [GroupByCastSchemaMarkerDocs]
 *
 * @include [GroupByCastRuntimeFailureDocs]
 *
 * See also:
 * - [castUnchecked][GroupBy.castUnchecked] to change the schema type arguments without verification.
 * - [cast][DataFrame.cast] to cast a dataframe.
 *
 * For more information: {@include [DocumentationUrls.Cast]}
 *
 * ### Examples
 *
 * @include [GroupByCastSampleSchemasDocs]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.groupByCast]
 *
 * @param [T] The target schema marker for the grouping keys.
 * @param [G] The target schema marker for the groups.
 * @param [verify] Whether to check the keys against [T] and every group against [G]. Defaults to `true`.
 * @return This [GroupBy] typed as [GroupBy]`<T, G>`.
 * @throws [IllegalArgumentException] if the key schema or any group schema fails verification under the schema
 * compatibility rules of [DataFrame.cast].
 */
public inline fun <reified T, reified G> GroupBy<*, *>.cast(verify: Boolean = true): GroupBy<T, G> {
    if (verify) {
        verifyGroupBySchemaImpl(typeOf<T>(), typeOf<G>())
    }
    return castUnchecked()
}
