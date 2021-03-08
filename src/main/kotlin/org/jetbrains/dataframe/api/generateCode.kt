package org.jetbrains.dataframe.api

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.impl.codeGen.CodeGeneratorImpl

inline fun <reified T> DataFrame<T>.generateCode() = generateCode(T::class.simpleName!!)

fun <T> DataFrame<T>.generateCode(markerName: String): String {
    val codeGen = CodeGeneratorImpl()
    return codeGen.generateInterfaceDeclarations(this, markerName, generateExtensionProperties = true).joinToString("\n")
}