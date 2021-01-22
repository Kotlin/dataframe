package org.jetbrains.dataframe

fun AnyFrame.schema(markerName: String? = null): String {
    return CodeGenerator().generateInterfaceDeclarations(this, markerName ?: "DataRecord", generateExtensionProperties = false).joinToString("\n")
}