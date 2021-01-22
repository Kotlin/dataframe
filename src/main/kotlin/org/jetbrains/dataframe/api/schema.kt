package org.jetbrains.dataframe

fun AnyFrame.schema(markerName: String? = null): String {
    return CodeGeneratorImpl().generateInterfaceDeclarations(this, markerName ?: "DataRecord", generateExtensionProperties = false).joinToString("\n")
}