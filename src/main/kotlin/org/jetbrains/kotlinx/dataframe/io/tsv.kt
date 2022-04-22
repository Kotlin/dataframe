package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

private val tabChar = '\t'

public fun DataFrame.Companion.readTSV(
    fileOrUrl: String,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readDelim(
            it, tabChar,
            header, isCompressed(fileOrUrl),
            CSVType.TDF, colTypes,
            skipLines, readLines,
            duplicate, charset,
            parserOptions
        )
    }

public fun DataFrame.Companion.readTSV(
    file: File,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8
): DataFrame<*> =
    readDelim(
        FileInputStream(file), tabChar,
        header, isCompressed(file),
        CSVType.TDF, colTypes,
        skipLines, readLines,
        duplicate, charset
    )

public fun DataFrame.Companion.readTSV(
    url: URL,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    readTSV(
        url.openStream(),
        header, isCompressed(url),
        colTypes,
        skipLines, readLines,
        duplicate, charset,
        parserOptions
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
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    readDelim(
        stream, tabChar,
        header, isCompressed,
        CSVType.TDF, colTypes,
        skipLines, readLines,
        duplicate, charset,
        parserOptions
    )
