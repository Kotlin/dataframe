@file:JvmName("CsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams
import java.io.File
import java.io.InputStream
import kotlin.reflect.typeOf

public class CsvDeephaven(private val delimiter: Char = DelimParams.CSV_DELIMITER) : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): DataFrame<*> =
        DataFrame.readCsv(inputStream = stream, header = header, delimiter = delimiter)

    override fun readDataFrame(file: File, header: List<String>): DataFrame<*> =
        DataFrame.readCsv(file = file, header = header, delimiter = delimiter)

    override fun acceptsExtension(ext: String): Boolean = ext == "csv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 20_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        val arguments = MethodArguments().add("delimiter", typeOf<Char>(), "'%L'", delimiter)
        return DefaultReadCsvMethod(pathRepresentation, arguments)
    }
}

private const val READ_CSV = "readCsv"

internal class DefaultReadCsvMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(path, arguments, READ_CSV)
