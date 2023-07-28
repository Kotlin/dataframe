package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import org.jetbrains.kotlinx.jupyter.api.Code
import java.net.URL

public val CodeGenerator.Companion.databaseCodeGenReader: (
    url: URL,
    name: String
) -> CodeGenerationReadResult
    get() = { url, name ->
        try {
            val code = buildCodeForDB(url, name)
            // TODO: rewrite it
            throw RuntimeException()
            CodeGenerationReadResult.Success(code, JDBC())
        } catch (e: Throwable) {
            CodeGenerationReadResult.Error(e)
        }
    }

// TODO:
public fun buildCodeForDB(url: URL, name: String): Code {
    val annotationName = DataSchema::class.simpleName
    val visibility = "public "
    val propertyVisibility = "public "

    val declarations = mutableListOf<String>()
    return declarations.joinToString()
}
