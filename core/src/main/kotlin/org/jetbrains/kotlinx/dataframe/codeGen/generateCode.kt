package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema

public inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): String {
    val name = markerName<T>()
    return generateCode(name, fields, extensionProperties)
}

public fun <T> DataFrame<T>.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): String {
    val codeGen = CodeGenerator.create()
    return codeGen.generate(
        schema = schema(),
        name = markerName,
        fields = fields,
        extensionProperties = extensionProperties,
        isOpen = true,
        visibility = visibility,
    ).code.declarations
}

public inline fun <reified T> DataFrame<T>.generateInterfaces(): String = generateCode(
    fields = true,
    extensionProperties = false
)

public inline fun <reified T> DataFrame<T>.generateDataClasses(
    markerName: String? = null,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false
): String {
    val name = markerName ?: markerName<T>()
    val codeGen = CodeGenerator.create(useFqNames)
    return codeGen.generate(
        schema = schema(),
        name = name,
        fields = true,
        extensionProperties = extensionProperties,
        isOpen = false,
        visibility = visibility,
        asDataClass = true
    ).code.declarations
}

@PublishedApi
internal inline fun <reified T> markerName(): String = if (T::class.isAbstract) {
    T::class.simpleName!!
} else "DataEntry"

public fun <T> DataFrame<T>.generateInterfaces(markerName: String): String = generateCode(
    markerName = markerName,
    fields = true,
    extensionProperties = false
)
