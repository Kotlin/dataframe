package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.io.input.BOMInputStream
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.util.IS_URL
import org.jetbrains.kotlinx.dataframe.util.IS_URL_IMPORT
import org.jetbrains.kotlinx.dataframe.util.IS_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.LISTS_TO_DATAFRAME_MIGRATION
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

/**
 * Opens a stream to [url] to create a [DataFrame] from it.
 * If the URL is a file URL, the file is read directly.
 * If the URL is an HTTP URL, it's also read directly, but if the server returns an error code,
 * the error response is read and parsed as [DataFrame] too.
 *
 * Public so it may be used in other modules.
 */
public fun catchHttpResponse(url: URL, body: (InputStream) -> AnyFrame): AnyFrame {
    val connection = url.openConnection()
    if (connection !is HttpURLConnection) {
        return connection.inputStream.use(body)
    }
    try {
        connection.connect()
        val code = connection.responseCode
        if (code != 200) {
            val response = connection.responseMessage
            try {
                // attempt to read error response as dataframe
                return DataFrame.read(connection.errorStream).df
            } catch (_: Exception) {
                throw RuntimeException("Server returned HTTP response code: $code. Response: $response")
            }
        }
        return connection.inputStream.use(body)
    } finally {
        connection.disconnect()
    }
}

@Deprecated(
    LISTS_TO_DATAFRAME_MIGRATION,
    ReplaceWith("this.toDataFrame(header = null, containsColumns)", "org.jetbrains.kotlinx.dataframe.api.toDataFrame"),
    level = DeprecationLevel.WARNING,
)
public fun <T> List<List<T>>.toDataFrame(containsColumns: Boolean = false): AnyFrame =
    toDataFrame(header = null, containsColumns)

@Deprecated(
    message = IS_URL,
    replaceWith = ReplaceWith(IS_URL_REPLACE, IS_URL_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun isURL(path: String): Boolean = isUrl(path)

public fun isUrl(path: String): Boolean = listOf("http:", "https:", "ftp:").any { path.startsWith(it) }

public fun isFile(url: URL): Boolean = url.protocol == "file"

public fun asFileOrNull(url: URL): File? = if (isFile(url)) File(url.path) else null

public fun urlAsFile(url: URL): File = File(url.toURI())

public fun isProtocolSupported(url: URL): Boolean = url.protocol in setOf("http", "https", "ftp")

/**
 * Converts a file path or URL [String] to a [URL].
 * If the path is a file path, the file is checked for existence and not being a directory.
 */
public fun asUrl(fileOrUrl: String): URL =
    if (isUrl(fileOrUrl)) {
        URI(fileOrUrl)
    } else {
        File(fileOrUrl).also {
            require(it.exists()) { "File not found: \"$fileOrUrl\"" }
            require(it.isFile) { "Not a file: \"$fileOrUrl\"" }
        }.toURI()
    }.toURL()

/** Skips BOM characters if present. */
public fun InputStream.skippingBomCharacters(): InputStream =
    BOMInputStream.builder()
        .setInputStream(this)
        .setInclude(false)
        .get()
