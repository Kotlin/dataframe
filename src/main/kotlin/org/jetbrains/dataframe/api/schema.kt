package org.jetbrains.dataframe

fun DataFrame<*>.schema(markerName: String? = null): String {
    return CodeGenerator().generateInterfaceDeclarations(this, markerName ?: "DataRecord", generateExtensionProperties = false).joinToString("\n")
}