package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.util.READ_TSV
import org.jetbrains.kotlinx.dataframe.util.READ_TSV_FILE_OR_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_TSV_FILE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_TSV_IMPORT
import org.jetbrains.kotlinx.dataframe.util.READ_TSV_STREAM_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_TSV_URL_REPLACE
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

private const val TAB_CHAR = '\t'

@Deprecated(
    message = READ_TSV,
    replaceWith = ReplaceWith(READ_TSV_FILE_OR_URL_REPLACE, READ_TSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
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
    catchHttpResponse(asUrl(fileOrUrl)) {
        readDelim(
            it,
            TAB_CHAR,
            header,
            isCompressed(fileOrUrl),
            CSVType.TDF,
            colTypes,
            skipLines,
            readLines,
            duplicate,
            charset,
            parserOptions,
        )
    }

@Deprecated(
    message = READ_TSV,
    replaceWith = ReplaceWith(READ_TSV_FILE_REPLACE, READ_TSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
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
        FileInputStream(file),
        TAB_CHAR,
        header,
        isCompressed(file),
        CSVType.TDF,
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
    )

@Deprecated(
    message = READ_TSV,
    replaceWith = ReplaceWith(READ_TSV_URL_REPLACE, READ_TSV_IMPORT),
    level = DeprecationLevel.WARNING,
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
        url.openStream(),
        header,
        isCompressed(url),
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
        parserOptions,
    )

@Deprecated(
    message = READ_TSV,
    replaceWith = ReplaceWith(READ_TSV_STREAM_REPLACE, READ_TSV_IMPORT),
    level = DeprecationLevel.WARNING,
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
        stream,
        TAB_CHAR,
        header,
        isCompressed,
        CSVType.TDF,
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
        parserOptions,
    )
