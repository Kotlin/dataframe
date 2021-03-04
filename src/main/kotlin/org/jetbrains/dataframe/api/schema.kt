package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.codeGen.CodeGeneratorImpl

fun AnyFrame.schema(markerName: String? = null): String {
    return CodeGeneratorImpl().generateInterfaceDeclarations(this, markerName ?: "DataRecord", generateExtensionProperties = false).joinToString("\n")
}