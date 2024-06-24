package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadTsvMethod
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

public class TSV : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readTSV(stream, header = header)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readTSV(file, header = header)

    override fun acceptsExtension(ext: String): Boolean = ext == "tsv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 30000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadTsvMethod(pathRepresentation)
}

private const val TAB_CHAR = '\t'

public fun DataFrame.Companion.readTSV(
    fileOrUrl: String,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readDelim(
            inStream = it,
            delimiter = TAB_CHAR,
            header = header,
            isCompressed = isCompressed(fileOrUrl),
            csvType = CSVType.TDF,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            duplicate = duplicate,
            charset = charset,
            parserOptions = parserOptions,
        )
    }

public fun DataFrame.Companion.readTSV(
    file: File,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
): DataFrame<*> =
    readDelim(
        inStream = FileInputStream(file),
        delimiter = TAB_CHAR,
        header = header,
        isCompressed = isCompressed(file),
        csvType = CSVType.TDF,
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        duplicate = duplicate,
        charset = charset,
    )

public fun DataFrame.Companion.readTSV(
    url: URL,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    readTSV(
        stream = url.openStream(),
        header = header,
        isCompressed = isCompressed(url),
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        duplicate = duplicate,
        charset = charset,
        parserOptions = parserOptions,
    )

public fun DataFrame.Companion.readTSV(
    stream: InputStream,
    header: List<String> = listOf(),
    isCompressed: Boolean = false,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    readDelim(
        inStream = stream,
        delimiter = TAB_CHAR,
        header = header,
        isCompressed = isCompressed,
        csvType = CSVType.TDF,
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        duplicate = duplicate,
        charset = charset,
        parserOptions = parserOptions,
    )
