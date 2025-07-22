package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.GENERATE_CODE
import org.jetbrains.kotlinx.dataframe.util.GENERATE_CODE_REPLACE1
import org.jetbrains.kotlinx.dataframe.util.GENERATE_CODE_REPLACE2
import org.jetbrains.kotlinx.dataframe.util.GENERATE_INTERFACES

// region Docs

// endregion

// region DataFrame

/**
 * Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] interfaces
 * for the given [DataFrame's][this] [schema][DataFrameSchema]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when working with the compiler plugin in cases where the schema
 *   cannot be inferred automatically from the source. [See Compiler Plugin on the documentation website.](https://kotlin.github.io/dataframe/compiler-plugin.html)
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 * @param markerName The base name to use for generated data schema declarations (markers). If not specified, it generates a name from type [T].
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 *  Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default].
 */
public fun <T> DataFrame<T>.generateInterfaces(
    markerName: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateCodeImpl(
        markerName = markerName,
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = false,
    )

/** Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] interfaces
 * for the given [DataFrame's][this] [schema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when working with the compiler plugin in cases where the schema
 *   cannot be inferred automatically from the source. [See Compiler Plugin on the documentation website.](https://kotlin.github.io/dataframe/compiler-plugin.html)
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 * @param markerName The base name to use for generated data schema declarations (markers). If not specified, it generates a name from type [T].
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 * Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default]. */
public inline fun <reified T> DataFrame<T>.generateInterfaces(
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateCodeImpl(
        markerName = markerName<T>(),
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = false,
    )

/**
 * Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] data classes
 * for the given [DataFrame's][this] [schema][DataFrameSchema]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when you want to:
 *   - Work with the data as regular Kotlin data classes.
 *   - Convert a dataframe to instantiated data classes with [`df.toListOf<DataClassType>()`][DataFrame.toListOf].
 *   - Work with data classes serialization.
 *   - Extract structured types for further use in your application.
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 * @param markerName The base name to use for generated data schema declarations (markers). If not specified, it generates a name from type [T].
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 *  Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default].
 */
public fun <T> DataFrame<T>.generateDataClasses(
    markerName: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateCodeImpl(
        markerName = markerName,
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = true,
    )

/** Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] data classes
 * for the given [DataFrame's][this] [schema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when you want to:
 *   - Work with the data as regular Kotlin data classes.
 *   - Convert a dataframe to instantiated data classes with [`df.toListOf<DataClassType>()`][org.jetbrains.kotlinx.dataframe.DataFrame.toListOf].
 *   - Work with data classes serialization.
 *   - Extract structured types for further use in your application.
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 * @param markerName The base name to use for generated data schema declarations (markers). If not specified, it generates a name from type [T].
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 * Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default]. */
public inline fun <reified T> DataFrame<T>.generateDataClasses(
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateCodeImpl(
        markerName = markerName<T>(),
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = true,
    )

// endregion

// region DataFrameSchema

/**
 * Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] interfaces
 * for the given [DataFrameSchema][this]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when working with the compiler plugin in cases where the schema
 *   cannot be inferred automatically from the source. [See Compiler Plugin on the documentation website.](https://kotlin.github.io/dataframe/compiler-plugin.html)
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 * @param markerName The base name to use for generated data schema declarations (markers).
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 *  Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default].
 */
@JvmName("generateInterfacesForSchema")
public fun DataFrameSchema.generateInterfaces(
    markerName: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    generateCodeImpl(
        markerName = markerName,
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = false,
    )

/**
 * Generates a [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] containing generated [@DataSchema][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] data classes
 * for the given [DataFrameSchema][this]
 * (including all nested [frame columns][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] and [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]).
 *
 * These generated declarations can also be called "markers".
 *
 * This is useful when you want to:
 *   - Work with the data as regular Kotlin data classes.
 *   - Convert a dataframe to instantiated data classes with [`df.toListOf<DataClassType>()`][DataFrame.toListOf].
 *   - Work with data classes serialization.
 *   - Extract structured types for further use in your application.
 *
 * This function is a simplified wrapper for the more advanced and customizable
 * [CodeGenerator][org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator] API.
 * For more customizability, have a look at [CodeGenerator.create()][CodeGenerator.create].
 *
 * For more information: [See `Data Schemas/Data Classes Generation` on the documentation website.](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html)
 *
 * @return [CodeString][org.jetbrains.kotlinx.dataframe.api.CodeString] – A value class wrapper for [String], containing
 *   the generated Kotlin code of data schema declarations (markers).
 *
 *
 * @param markerName The base name to use for generated data schema declarations (markers).
 * @param extensionProperties Whether to generate [extension properties (column accessors)][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 *   in addition to data schema declarations (markers).
 *   Useful if you don't use the compiler plugin, otherwise they are not needed;
 *   the compiler plugin and older Gradle/KSP plugin generate them automatically.
 *   Default is `false`.
 * @param visibility Visibility modifier for the generated declarations (markers). Default is [MarkerVisibility.IMPLICIT_PUBLIC][org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility.IMPLICIT_PUBLIC].
 * @param useFqNames If `true`, fully qualified type names will be used in generated code. Default is `false`.
 * @param nameNormalizer Strategy for converting column names (with spaces, underscores, etc.)
 *  to Kotlin-style identifiers.
 *  Generated properties will still refer to columns by their actual name
 *  using the [@ColumnName][org.jetbrains.kotlinx.dataframe.annotations.ColumnName] annotation.
 *  Default is [NameNormalizer.default][org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer.Companion.default].
 */
@JvmName("generateDataClassesForSchema")
public fun DataFrameSchema.generateDataClasses(
    markerName: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    generateCodeImpl(
        markerName = markerName,
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
        asDataClass = true,
    )

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
 *
 * @see CodeString.print
 */
@JvmInline
public value class CodeString(public val value: String) {
    override fun toString(): String = value
}

@PublishedApi
internal fun String.toCodeString(): CodeString = CodeString(this)

/**
 * Generates a [CodeString] containing generated [@DataSchema][DataSchema] declarations (markers).
 */
@PublishedApi
internal fun DataFrameSchema.generateCodeImpl(
    markerName: String,
    extensionProperties: Boolean,
    visibility: MarkerVisibility,
    useFqNames: Boolean,
    nameNormalizer: NameNormalizer,
    asDataClass: Boolean,
): CodeString {
    val codeGen = CodeGenerator.create(useFqNames)
    return codeGen.generate(
        schema = this,
        name = markerName,
        fields = true,
        extensionProperties = extensionProperties,
        isOpen = false,
        visibility = visibility,
        asDataClass = asDataClass,
        fieldNameNormalizer = nameNormalizer,
    ).code.declarations.toCodeString()
}

// region Deprecated

@Deprecated(
    message = GENERATE_CODE,
    replaceWith = ReplaceWith(GENERATE_CODE_REPLACE1),
    level = DeprecationLevel.WARNING,
)
public inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): CodeString {
    val codeGen = CodeGenerator.create()
    return codeGen.generate(
        schema = this.schema(),
        name = markerName<T>(),
        fields = fields,
        extensionProperties = extensionProperties,
        isOpen = true,
        visibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ).code.declarations.toCodeString()
}

@Deprecated(
    message = GENERATE_CODE,
    replaceWith = ReplaceWith(GENERATE_CODE_REPLACE2),
    level = DeprecationLevel.WARNING,
)
public fun <T> DataFrame<T>.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeString {
    val codeGen = CodeGenerator.create()
    return codeGen.generate(
        schema = this.schema(),
        name = markerName,
        fields = fields,
        extensionProperties = extensionProperties,
        isOpen = true,
        visibility = visibility,
    ).code.declarations.toCodeString()
}

@Deprecated(message = GENERATE_INTERFACES, level = DeprecationLevel.HIDDEN)
public inline fun <reified T> DataFrame<T>.generateInterfaces(): CodeString = generateInterfaces<T>()

@Deprecated(message = GENERATE_INTERFACES, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.generateInterfaces(markerName: String): CodeString = generateInterfaces(markerName)

// endregion
