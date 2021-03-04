package org.jetbrains.dataframe.api

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.impl.codeGen.CodeGeneratorImpl

fun <T> DataFrame<T>.generateTypedCode(markerName: String = "DataRecord"): String {
    val codeGen = CodeGeneratorImpl()
    return codeGen.generateInterfaceDeclarations(this, markerName, generateExtensionProperties = true).joinToString("\n")
}