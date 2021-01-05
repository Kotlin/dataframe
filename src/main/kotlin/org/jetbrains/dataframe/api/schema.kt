package org.jetbrains.dataframe.api

import org.jetbrains.dataframe.CodeGenerator
import org.jetbrains.dataframe.DataFrame

fun <T> DataFrame<T>.schema(markerName: String = "DataRecord"): String {
    return CodeGenerator().generateInterfaceDeclarations(this, markerName, generateExtensionProperties = false).joinToString("\n")
}