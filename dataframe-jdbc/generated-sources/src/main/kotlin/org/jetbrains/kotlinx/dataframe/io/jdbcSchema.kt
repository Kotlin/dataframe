package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import java.net.URL

// TODO: helper functions created to support existing hierarchy https://github.com/Kotlin/dataframe/issues/450
public val CodeGenerator.Companion.databaseCodeGenReader: (url: URL, name: String) -> CodeGenerationReadResult
    get() = { url, name ->
        try {
            val code = buildCodeForDB(url, name)
            throw RuntimeException()
            CodeGenerationReadResult.Success(code, Jdbc())
        } catch (e: Throwable) {
            CodeGenerationReadResult.Error(e)
        }
    }

public fun buildCodeForDB(url: URL, name: String): Code {
    val annotationName = DataSchema::class.simpleName
    val visibility = "public "
    val propertyVisibility = "public "

    val declarations = mutableListOf<String>()
    return declarations.joinToString()
}
