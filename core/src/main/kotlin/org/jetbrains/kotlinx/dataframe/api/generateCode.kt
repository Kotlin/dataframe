package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

// region Docs

/**
 * Generates a [CodeString] containing generated [@DataSchema][DataSchema] $[TYPES]
 * for the given $[RECEIVER]
 * (including all nested [frame columns][FrameColumn] and [column groups][ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when working with the compiler plugin in cases where the schema
 * cannot be inferred automatically from the source.
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: {@include [DocumentationUrls.DataSchemaGeneration]}
 *
 * @return [CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 */
@ExcludeFromSources
private interface CommonGenerateCodeDocs {

    // "interfaces" or "data classes"
    interface TYPES

    // "DataFrameSchema" or "DataFrame"
    interface RECEIVER
}

@ExcludeFromSources
private interface Params {

    /** @param markerName The base name to use for generated data schema declarations (markers). */
    interface MarkerName

    /** @include [MarkerName] If not specified, generates a name from type [T]. */
    interface MarkerNameOptional

    /** @param fields Whether to generate fields (`val ...:`) inside the generated data schema declarations (markers). */
    interface Fields

    /** @param extensionProperties Whether to generate extension properties in addition to data schema declarations (markers). */
    interface ExtensionProperties

    /** @param visibility Visibility modifier for the generated declarations (markers). */
    interface Visibility

    /** @param useFqNames If `true`, fully qualified type names will be used in generated code. */
    interface UseFqNames

    /**
     * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.) to valid Kotlin identifiers.
     *  Columns will keep their original name inside the dataframe via [@ColumnName][ColumnName].
     */
    interface NameNormalizer
}

// endregion

// region DataFrame

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrame's][this] [schema][DataFrameSchema]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces and extension properties}
 *
 * @include [Params.Fields] Default is `true`.
 * @include [Params.ExtensionProperties] Default is `true`.
 */
public inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): CodeString =
    schema().generateCode(
        markerName = markerName<T>(),
        fields = fields,
        extensionProperties = extensionProperties,
    )

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrame's][this] [schema][DataFrameSchema]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces and extension properties}
 *
 * @include [Params.MarkerName]
 * @include [Params.Fields] Default is `true`.
 * @include [Params.ExtensionProperties] Default is `true`.
 * @include [Params.Visibility] Default is [MarkerVisibility.IMPLICIT_PUBLIC].
 */
public fun <T> DataFrame<T>.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeString =
    schema().generateCode(
        markerName = markerName,
        fields = fields,
        extensionProperties = extensionProperties,
        visibility = visibility,
    )

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrame's][this] [schema][DataFrameSchema]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces}
 *
 * @include [Params.MarkerNameOptional]
 */
public inline fun <reified T> DataFrame<T>.generateInterfaces(): CodeString =
    schema().generateInterfaces(
        markerName = markerName<T>(),
    )

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrame's][this] [schema][DataFrameSchema]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces}
 *
 * @include [Params.MarkerNameOptional]
 */
public fun <T> DataFrame<T>.generateInterfaces(markerName: String): CodeString =
    schema().generateInterfaces(markerName = markerName)

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrame's][this] [schema][DataFrameSchema]}
 * {@set [CommonGenerateCodeDocs.TYPES] data classes}
 *
 * @include [Params.MarkerNameOptional]
 * @include [Params.ExtensionProperties] Default is `false`.
 * @include [Params.Visibility] Default is [MarkerVisibility.IMPLICIT_PUBLIC].
 * @include [Params.UseFqNames] Default is `false`.
 * @include [Params.NameNormalizer] Default is [NameNormalizer.default][NameNormalizer.Companion.default].
 */
public inline fun <reified T> DataFrame<T>.generateDataClasses(
    markerName: String? = null,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateDataClasses(
        markerName = markerName ?: markerName<T>(),
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
    )

// endregion

// region DataFrameSchema

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrameSchema][this]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces and extension properties}
 *
 * @include [Params.MarkerName]
 * @include [Params.Fields] Default is `true`.
 * @include [Params.ExtensionProperties] Default is `true`.
 * @include [Params.Visibility] Default is [MarkerVisibility.IMPLICIT_PUBLIC].
 */
@JvmName("generateCodeForSchema")
public fun DataFrameSchema.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeString {
    val codeGen = CodeGenerator.create()
    return codeGen.generate(
        schema = this,
        name = markerName,
        fields = fields,
        extensionProperties = extensionProperties,
        isOpen = true,
        visibility = visibility,
    ).code.declarations.toCodeString()
}

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrameSchema][this]}
 * {@set [CommonGenerateCodeDocs.TYPES] interfaces}
 *
 * @include [Params.MarkerName]
 */
@JvmName("generateInterfacesForSchema")
public fun DataFrameSchema.generateInterfaces(markerName: String): CodeString =
    generateCode(
        markerName = markerName,
        fields = true,
        extensionProperties = false,
    )

/**
 * @include [CommonGenerateCodeDocs]
 * {@set [CommonGenerateCodeDocs.RECEIVER] [DataFrameSchema][this]}
 * {@set [CommonGenerateCodeDocs.TYPES] data classes}
 *
 * @include [Params.MarkerName]
 * @include [Params.ExtensionProperties] Default is `false`.
 * @include [Params.Visibility] Default is [MarkerVisibility.IMPLICIT_PUBLIC].
 * @include [Params.UseFqNames] Default is `false`.
 * @include [Params.NameNormalizer] Default is [NameNormalizer.default][NameNormalizer.Companion.default].
 */
@JvmName("generateDataClassesForSchema")
public fun DataFrameSchema.generateDataClasses(
    markerName: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString {
    val codeGen = CodeGenerator.create(useFqNames)
    return codeGen.generate(
        schema = this,
        name = markerName,
        fields = true,
        extensionProperties = extensionProperties,
        isOpen = false,
        visibility = visibility,
        asDataClass = true,
        fieldNameNormalizer = nameNormalizer,
    ).code.declarations.toCodeString()
}

// endregion

@PublishedApi
internal inline fun <reified T> markerName(): String =
    if (T::class.isAbstract) {
        T::class.simpleName!!
    } else {
        "DataEntry"
    }

/**
 * Converts delimited 'my_name', 'my name', etc., String to camelCase 'myName'
 */
public val NameNormalizer.Companion.default: NameNormalizer get() = NameNormalizer.from(setOf('\t', ' ', '_'))

/**
 * A value class wrapper for [String], containing
 * generated Kotlin code of data schema declarations (markers) and optionally extension properties.
 */
@JvmInline
public value class CodeString(public val value: String) {
    override fun toString(): String = value
}

@PublishedApi
internal fun String.toCodeString(): CodeString = CodeString(this)
