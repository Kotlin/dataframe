package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

// region DataFrame

public inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): CodeString =
    schema().generateCode(
        markerName = markerName<T>(),
        fields = fields,
        extensionProperties = extensionProperties,
    )

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

public inline fun <reified T> DataFrame<T>.generateInterfaces(): CodeString =
    schema().generateInterfaces(
        markerName = markerName<T>(),
    )

public inline fun <reified T> DataFrame<T>.generateDataClasses(
    markerName: String? = null,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString =
    schema().generateDataClasses(
        name = markerName ?: markerName<T>(),
        extensionProperties = extensionProperties,
        visibility = visibility,
        useFqNames = useFqNames,
        nameNormalizer = nameNormalizer,
    )

public fun <T> DataFrame<T>.generateInterfaces(markerName: String): CodeString =
    schema().generateInterfaces(markerName = markerName)

// endregion

// region DataFrameSchema

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

@JvmName("generateInterfacesForSchema")
public fun DataFrameSchema.generateInterfaces(markerName: String): CodeString =
    generateCode(
        markerName = markerName,
        fields = true,
        extensionProperties = false,
    )

@JvmName("generateDataClassesForSchema")
public fun DataFrameSchema.generateDataClasses(
    name: String,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString {
    val codeGen = CodeGenerator.create(useFqNames)
    return codeGen.generate(
        schema = this,
        name = name,
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

@JvmInline
public value class CodeString(public val value: String) {
    override fun toString(): String = value
}

@PublishedApi
internal fun String.toCodeString(): CodeString = CodeString(this)
