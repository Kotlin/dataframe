package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedCodeGenerationFormat
import org.jetbrains.kotlinx.dataframe.io.guessFormat
import org.jetbrains.kotlinx.dataframe.io.readCodeForGeneration
import java.net.URL

/**
 * Reader that can read data from a URL and generate code (type schema representations) for it.
 * It tries to guess the format based on the given [formats] and returns [CodeGenerationReadResult.Success],
 * or returns [CodeGenerationReadResult.Error] if it fails.
 */
public val CodeGenerator.Companion.urlCodeGenReader: (
    url: URL,
    name: String,
    formats: List<SupportedCodeGenerationFormat>,
    generateHelperCompanionObject: Boolean,
) -> CodeGenerationReadResult
    get() = { url, name, formats, generateHelperCompanionObject ->
        try {
            val (format, code) = url.openStream().use {
                readCodeForGeneration(
                    stream = it,
                    name = name,
                    format = guessFormat(url, formats) as? SupportedCodeGenerationFormat?,
                    generateHelperCompanionObject = generateHelperCompanionObject,
                    formats = formats.filterIsInstance<SupportedCodeGenerationFormat>(),
                )
            }
            CodeGenerationReadResult.Success(code, format)
        } catch (e: Throwable) {
            CodeGenerationReadResult.Error(e)
        }
    }

public sealed interface CodeGenerationReadResult {

    public class Success(public val code: Code, public val format: SupportedCodeGenerationFormat) :
        CodeGenerationReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod =
            format.createDefaultReadMethod(pathRepresentation)
    }

    public class Error(public val reason: Throwable) : CodeGenerationReadResult
}
