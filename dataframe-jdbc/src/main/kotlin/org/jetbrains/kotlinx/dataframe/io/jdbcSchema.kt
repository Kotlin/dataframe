package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import java.net.URL

// TODO: helper functions created to support existing hierarchy https://github.com/Kotlin/dataframe/issues/450
@Deprecated("SupportedDataFrameFormat is deprecated. Will be ERROR in 1.1.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
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

@Deprecated("", level = DeprecationLevel.ERROR)
public fun buildCodeForDB(url: URL, name: String): Code {
    val annotationName = DataSchema::class.simpleName
    val visibility = "public "
    val propertyVisibility = "public "

    val declarations = mutableListOf<String>()
    return declarations.joinToString()
}
