package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.CsvOptions
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadCsvMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadExcelMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadTsvMethod
import org.jetbrains.kotlinx.dataframe.io.SupportedFormats
import org.jetbrains.kotlinx.dataframe.io.guessFormat
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readTSV
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.net.URL

public val CodeGenerator.Companion.urlReader: (url: URL, csvOptions: CsvOptions) -> DfReadResult
    get() = { url, csvOptions ->

        fun readCSV(url: URL) = run {
            val (delimiter) = csvOptions
            DfReadResult.Success(DataFrame.readCSV(url, delimiter = delimiter), SupportedFormats.CSV, csvOptions)
        }

/*        fun readArrow(url: URL) = run {
            DfReadResult.Success(DataFrame.readArrow(url), SupportedFormats.ARROW, csvOptions)
        } */

        fun readTSV(url: URL) = DfReadResult.Success(DataFrame.readTSV(url), SupportedFormats.TSV, csvOptions)

        fun readJson(url: URL) = DfReadResult.Success(DataFrame.readJson(url), SupportedFormats.JSON, csvOptions)

        fun readExcel(url: URL) = DfReadResult.Success(DataFrame.readExcel(url), SupportedFormats.EXCEL, csvOptions)
        try {
            val res = when (guessFormat(url.path)) {
                SupportedFormats.CSV -> readCSV(url)
                SupportedFormats.TSV -> readTSV(url)
                SupportedFormats.JSON -> readJson(url)
//                SupportedFormats.ARROW -> readArrow(url)
                SupportedFormats.EXCEL -> readExcel(url)
                null -> try {
                    readExcel(url)
                } catch (e: Exception) {
                    try {
                        readCSV(url)
                    } catch (e: Exception) {
                        try {
                            readTSV(url)
                        } catch (e: Exception) {
                            readJson(url)
                        }
                    }
                }
            }
            res
        } catch (e: Throwable) {
            DfReadResult.Error(e)
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
                SupportedFormats.TSV -> DefaultReadTsvMethod(pathRepresentation)
//                SupportedFormats.ARROW -> DefaultReadArrowMethod(pathRepresentation)
                SupportedFormats.EXCEL -> DefaultReadExcelMethod(pathRepresentation)
            }
        }

        public val schema: DataFrameSchema = df.schema()
    }

    public class Error(public val reason: Throwable) : DfReadResult
}
