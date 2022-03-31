package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.charset.Charset

private val tabChar = '\t'

public fun DataFrame.Companion.readTSV(
    fileOrUrl: String,
    headers: List<String> = listOf(),
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
            headers, isCompressed(fileOrUrl),
            CSVType.TDF, colTypes,
            skipLines, readLines,
            duplicate, charset,
            parserOptions
        )
    }

public fun DataFrame.Companion.readTSV(
    file: File,
    headers: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8
): DataFrame<*> =
    readDelim(
        FileInputStream(file), tabChar,
        headers, isCompressed(file),
        CSVType.TDF, colTypes,
        skipLines, readLines,
        duplicate, charset
    )

public fun DataFrame.Companion.readTSV(
    url: URL,
    headers: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null
): DataFrame<*> =
    readDelim(
        url.openStream(), tabChar,
        headers, isCompressed(url),
        CSVType.TDF, colTypes,
        skipLines, readLines,
        duplicate, charset,
        parserOptions
    )
