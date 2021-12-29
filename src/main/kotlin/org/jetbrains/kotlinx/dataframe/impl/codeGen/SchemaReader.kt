package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.CsvOptions
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadCsvMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedFormats
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.net.URL

public val CodeGenerator.Companion.urlReader: (url: URL, csvOptions: CsvOptions) -> DfReadResult
    get() {
        return { url, csvOptions ->
            fun guessFormat(url: String): SupportedFormats? = when {
                url.endsWith(".csv") -> SupportedFormats.CSV
                url.endsWith(".json") -> SupportedFormats.JSON
                else -> null
            }

            fun readCSV(url: URL) = run {
                val (delimiter) = csvOptions
                DfReadResult.Success(DataFrame.readCSV(url, delimiter = delimiter), SupportedFormats.CSV, csvOptions)
            }

            fun readJson(url: URL) = DfReadResult.Success(DataFrame.readJson(url), SupportedFormats.JSON, csvOptions)
            try {
                val res = when (guessFormat(url.path)) {
                    SupportedFormats.CSV -> readCSV(url)
                    SupportedFormats.JSON -> readJson(url)
                    else -> try {
                        readCSV(url)
                    } catch (e: Exception) {
                        readJson(url)
                    }
                }
                res
            } catch (e: Throwable) {
                DfReadResult.Error(e)
            }
        }
    }

public sealed interface DfReadResult {

    public class Success(
        private val df: AnyFrame,
        private val format: SupportedFormats,
        private val csvOptions: CsvOptions
    ) : DfReadResult {
        public fun getReadDfMethod(pathRepresentation: String?): DefaultReadDfMethod {
            return when (format) {
                SupportedFormats.CSV -> DefaultReadCsvMethod(pathRepresentation, csvOptions)
                SupportedFormats.JSON -> DefaultReadJsonMethod(pathRepresentation)
            }
        }

        public val schema: DataFrameSchema = df.schema()
    }

    public class Error(public val reason: Throwable) : DfReadResult
}
