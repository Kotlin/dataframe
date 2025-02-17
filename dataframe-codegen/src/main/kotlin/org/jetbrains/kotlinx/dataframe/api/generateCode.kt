package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from

public inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): CodeString {
    val name = markerName<T>()
    return generateCode(name, fields, extensionProperties)
}

public fun <T> DataFrame<T>.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeString {
    val codeGen = CodeGenerator.create()
    return codeGen.generate(
        schema = schema(),
        name = markerName,
        fields = fields,
        extensionProperties = extensionProperties,
        isOpen = true,
        visibility = visibility,
    ).code.declarations.toCodeString()
}

public inline fun <reified T> DataFrame<T>.generateInterfaces(): CodeString =
    generateCode(
        fields = true,
        extensionProperties = false,
    )

public inline fun <reified T> DataFrame<T>.generateDataClasses(
    markerName: String? = null,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString {
    val name = markerName ?: markerName<T>()
    val codeGen = CodeGenerator.create(useFqNames)
    return codeGen.generate(
        schema = schema(),
        name = name,
        fields = true,
        extensionProperties = extensionProperties,
        isOpen = false,
        visibility = visibility,
        asDataClass = true,
        fieldNameNormalizer = nameNormalizer,
    ).code.declarations.toCodeString()
}

@PublishedApi
internal inline fun <reified T> markerName(): String =
    if (T::class.isAbstract) {
        T::class.simpleName!!
    } else {
        "DataEntry"
    }

public fun <T> DataFrame<T>.generateInterfaces(markerName: String): CodeString =
    generateCode(
        markerName = markerName,
        fields = true,
        extensionProperties = false,
    )

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
