package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedFormat
import org.jetbrains.kotlinx.dataframe.io.guessFormat
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.net.URL

public val CodeGenerator.Companion.urlReader: (url: URL, formats: List<SupportedFormat>) -> DfReadResult
    get() = { url, formats ->
        try {
            val (format, df) = url.openStream().use { DataFrame.read(it, guessFormat(url, formats), formats = formats) }
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

    public class Error(public val reason: Throwable) : DfReadResult
}
