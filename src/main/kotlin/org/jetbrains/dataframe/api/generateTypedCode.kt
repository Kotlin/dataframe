package org.jetbrains.dataframe.api

import org.jetbrains.dataframe.CodeGenerator
import org.jetbrains.dataframe.DataFrame

fun <T> DataFrame<T>.generateTypedCode(markerName: String = "DataRecord"): String {
    val codeGen = CodeGenerator()
    return codeGen.generateInterfaceDeclarations(this, markerName, generateExtensionProperties = true).joinToString("\n")
}