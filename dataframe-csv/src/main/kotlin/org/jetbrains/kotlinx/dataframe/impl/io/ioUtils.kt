package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.io.input.BOMInputStream
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.CsvCompression
import org.jetbrains.kotlinx.dataframe.io.CsvCompression.CUSTOM
import org.jetbrains.kotlinx.dataframe.io.CsvCompression.GZIP
import org.jetbrains.kotlinx.dataframe.io.CsvCompression.NONE
import org.jetbrains.kotlinx.dataframe.io.CsvCompression.ZIP
import org.jetbrains.kotlinx.dataframe.io.isURL
import org.jetbrains.kotlinx.dataframe.io.readJson
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

internal fun compressionStateOf(fileOrUrl: String): CsvCompression<*> =
    when (fileOrUrl.split(".").last()) {
        "gz" -> CsvCompression.GZIP
        "zip" -> CsvCompression.ZIP
        else -> CsvCompression.NONE
    }

internal fun compressionStateOf(file: File): CsvCompression<*> =
    when (file.extension) {
        "gz" -> CsvCompression.GZIP
        "zip" -> CsvCompression.ZIP
        else -> CsvCompression.NONE
    }

internal fun compressionStateOf(url: URL): CsvCompression<*> = compressionStateOf(url.path)

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

internal inline fun <T> InputStream.useSafely(compression: CsvCompression<*>, block: (InputStream) -> T): T {
    var zipInputStream: ZipInputStream? = null

    // first wrap the stream in the compression algorithm
    val unpackedStream = when (compression) {
        NONE -> this

        ZIP -> compression(this).also {
            it as ZipInputStream
            // make sure to call nextEntry once to prepare the stream
            if (it.nextEntry == null) error("No entries in zip file")

            zipInputStream = it
        }

        GZIP -> compression(this)

        is CUSTOM<*> -> compression(this)
    }

    val bomSafeStream = BOMInputStream.builder().setInputStream(unpackedStream).get()

    try {
        return block(bomSafeStream)
    } finally {
        // if we were reading from a ZIP, make sure there was only one entry, as to
        // warn the user of potential issues
        if (compression == ZIP && zipInputStream!!.nextEntry != null) {
            throw IllegalArgumentException("Zip file contains more than one entry")
        }
    }
}
