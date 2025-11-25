@file:JvmName("TsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import kotlin.reflect.typeOf

public class TsvDeephaven(private val delimiter: Char = DelimParams.TSV_DELIMITER) : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(inputStream = stream, header = header, delimiter = delimiter)

    override fun readDataFrame(file: File, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(file = file, header = header, delimiter = delimiter)

    override fun readDataFrame(path: Path, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(path = path, header = header, delimiter = delimiter)

    override fun acceptsExtension(ext: String): Boolean = ext == "tsv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 30_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        val arguments = MethodArguments().add("delimiter", typeOf<Char>(), "'%L'", delimiter)
        return DefaultReadTsvMethod(pathRepresentation, arguments)
    }
}

private const val READ_TSV = "readTsv"

internal class DefaultReadTsvMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(path, arguments, READ_TSV)
