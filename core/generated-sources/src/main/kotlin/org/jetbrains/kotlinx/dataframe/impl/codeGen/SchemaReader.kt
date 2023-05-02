package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedCodeGenerationFormat
import org.jetbrains.kotlinx.dataframe.io.SupportedDataFrameFormat
import org.jetbrains.kotlinx.dataframe.io.SupportedFormat
import org.jetbrains.kotlinx.dataframe.io.guessFormat
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readCodeForGeneration
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.jupyter.api.Code
import java.net.URL

/**
 * Reader that can read a data frame from a URL. It tries to guess the format based on the given [formats] and returns
 * [DfReadResult.Success], or returns [DfReadResult.Error] if it fails.
 */
public val CodeGenerator.Companion.urlDfReader: (url: URL, formats: List<SupportedFormat>) -> DfReadResult
    get() = { url, formats ->
        try {
            val (format, df) = url.openStream().use {
                DataFrame.read(
                    stream = it,
                    format = guessFormat(url, formats) as? SupportedDataFrameFormat?,
                    formats = formats.filterIsInstance<SupportedDataFrameFormat>(),
                )
            }
            DfReadResult.Success(df, format)
        } catch (e: Throwable) {
            DfReadResult.Error(e)
        }
    }

public sealed interface DfReadResult {

    public class Success(
        private val df: AnyFrame,
        public val format: SupportedDataFrameFormat,
    ) : DfReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod {
            return format.createDefaultReadMethod(pathRepresentation)
        }

        public val schema: DataFrameSchema = df.schema()
    }

    public class Error(public val reason: Throwable) : DfReadResult
}

/**
 * Reader that can read data from a URL and generate code (type schema representations) for it.
 * It tries to guess the format based on the given [formats] and returns [CodeGenerationReadResult.Success],
 * or returns [CodeGenerationReadResult.Error] if it fails.
 */
public val CodeGenerator.Companion.urlCodeGenReader: (
    url: URL,
    name: String,
    formats: List<SupportedFormat>,
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

    public class Success(
        public val code: Code,
        public val format: SupportedCodeGenerationFormat,
    ) : CodeGenerationReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod {
            return format.createDefaultReadMethod(pathRepresentation)
        }
    }

    public class Error(public val reason: Throwable) : CodeGenerationReadResult
}
