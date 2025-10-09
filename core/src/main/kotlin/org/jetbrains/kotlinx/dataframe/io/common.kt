package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.io.input.BOMInputStream
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.util.IS_URL
import org.jetbrains.kotlinx.dataframe.util.IS_URL_IMPORT
import org.jetbrains.kotlinx.dataframe.util.IS_URL_REPLACE
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

/**
 * Converts a list of lists into a [DataFrame].
 *
 * By default, treats lists as rows. If [header] is not provided, the first inner list becomes a header (column names), and the remaining lists are treated as data.
 *
 * With [containsColumns] = `true`, interprets each inner list as a column.
 * If [header] is not provided, the first element will be used as the column name, and the remaining elements as values.
 *
 * @param T The type of elements contained in the nested lists.
 * @param containsColumns If `true`, treats each nested list as a column.
 *                        Otherwise, each nested list is a row.
 *                        Defaults to `false`.
 * @param header overrides extraction of column names from lists - all values are treated as data instead.
 * @return A [DataFrame] containing the data from the nested list structure.
 *         Returns an empty [DataFrame] if the input is empty or invalid.
 */
@Refine
@Interpretable("ValuesListsToDataFrame")
public fun <T> List<List<T>>.toDataFrame(header: List<String>? = null, containsColumns: Boolean = false): AnyFrame =
    when {
        containsColumns -> {
            mapIndexedNotNull { index, list ->
                if (list.isEmpty()) return@mapIndexedNotNull null
                val name = header?.get(index) ?: list[0].toString()
                val values = if (header == null) list.drop(1) else list
                createColumnGuessingType(name, values)
            }.toDataFrame()
        }

        isEmpty() -> DataFrame.Empty

        else -> {
            val data = if (header == null) drop(1) else this
            (header ?: get(0).map { it.toString() }).mapIndexed { colIndex, name ->
                val values = data.map { row ->
                    if (row.size <= colIndex) {
                        null
                    } else {
                        row[colIndex]
                    }
                }
                createColumnGuessingType(name, values)
            }.toDataFrame()
        }
    }

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
