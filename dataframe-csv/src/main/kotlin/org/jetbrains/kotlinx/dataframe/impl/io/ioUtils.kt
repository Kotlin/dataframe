package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.io.input.BOMInputStream
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.isURL
import org.jetbrains.kotlinx.dataframe.io.readJson
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import java.util.zip.ZipInputStream

internal fun compressionStateOf(fileOrUrl: String): Compression<*> =
    when (fileOrUrl.split(".").last()) {
        "gz" -> Compression.Gzip
        "zip" -> Compression.Zip
        else -> Compression.None
    }

internal fun compressionStateOf(file: File): Compression<*> =
    when (file.extension) {
        "gz" -> Compression.Gzip
        "zip" -> Compression.Zip
        else -> Compression.None
    }

internal fun compressionStateOf(inputStream: InputStream): Compression<*> =
    when (inputStream) {
        is GZIPInputStream -> Compression.Gzip
        is ZipInputStream -> Compression.Zip
        is InflaterInputStream -> error("Please supply compression = CsvCompression.Custom to read this input stream.")
        else -> Compression.None
    }

internal fun compressionStateOf(url: URL): Compression<*> = compressionStateOf(url.path)

internal fun catchHttpResponse(url: URL, body: (InputStream) -> AnyFrame): AnyFrame {
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
                // attempt to read error response as JSON
                return DataFrame.readJson(connection.errorStream)
            } catch (_: Exception) {
                throw RuntimeException("Server returned HTTP response code: $code. Response: $response")
            }
        }
        return connection.inputStream.use(body)
    } finally {
        connection.disconnect()
    }
}

public fun asURL(fileOrUrl: String): URL =
    if (isURL(fileOrUrl)) {
        URL(fileOrUrl).toURI()
    } else {
        File(fileOrUrl).also {
            require(it.exists()) { "File not found: \"$fileOrUrl\"" }
            require(it.isFile) { "Not a file: \"$fileOrUrl\"" }
        }.toURI()
    }.toURL()

/**
 * Adjusts the input stream to be safe to use with the given compression algorithm as well
 * as any potential BOM characters.
 *
 * Also closes the stream after the block is executed.
 */
internal inline fun <T, I : InputStream> InputStream.useSafely(
    compression: Compression<I>,
    block: (InputStream) -> T,
): T {
    // first wrap the stream in the compression algorithm
    val unpackedStream = compression(this)
    compression.doFirst(unpackedStream)

    val bomSafeStream = BOMInputStream.builder()
        .setInputStream(unpackedStream)
        .get()

    try {
        return block(bomSafeStream)
    } finally {
        compression.doFinally(unpackedStream)
    }
}
