package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedCodeGenerationFormat
import org.jetbrains.kotlinx.dataframe.io.SupportedDataFrameFormat
import org.jetbrains.kotlinx.dataframe.io.SupportedFormat
import org.jetbrains.kotlinx.dataframe.io.guessFormat
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.net.URL

public val CodeGenerator.Companion.urlDfReader: (url: URL, formats: List<SupportedFormat>) -> DfReadResult
    get() = { url, formats ->
        val supportedFormat = guessFormat(url, formats)

        if (supportedFormat !is SupportedDataFrameFormat?) DfReadResult.WrongFormat
        else try {
            val (format, df) = url.openStream().use {
                DataFrame.read(
                    stream = it,
                    format = supportedFormat,
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
        public val format: SupportedFormat,
    ) : DfReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod {
            return format.createDefaultReadMethod(pathRepresentation)
        }

        public val schema: DataFrameSchema = df.schema()
    }

    public object WrongFormat : DfReadResult

    public class Error(public val reason: Throwable) : DfReadResult
}

public val CodeGenerator.Companion.urlCodeGenReader: (url: URL, formats: List<SupportedFormat>) -> CodeGenerationReadResult
    get() = { url, formats ->
        try {
            val format = guessFormat(url, formats) as SupportedCodeGenerationFormat
            val code = format.readCodeForGeneration(url.openStream())
            CodeGenerationReadResult.Success(code, format)
        } catch (e: Throwable) {
            CodeGenerationReadResult.Error(e)
        }
    }

public sealed interface CodeGenerationReadResult {

    public class Success(
        public val code: CodeWithConverter,
        public val format: SupportedFormat,
    ) : CodeGenerationReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod {
            return format.createDefaultReadMethod(pathRepresentation)
        }
    }

    public class Error(public val reason: Throwable) : CodeGenerationReadResult
}
