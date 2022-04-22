package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.single
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL

public enum class SupportedFormats {
    CSV {
        override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readCSV(stream, header = header)

        override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readCSV(file, header = header)

        override fun acceptsExtension(ext: String): Boolean = ext == "csv"
    },
    TSV {
        override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readTSV(stream, header = header)

        override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readTSV(file, header = header)

        override fun acceptsExtension(ext: String): Boolean = ext == "tsv"
    },
    JSON {
        override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readJson(stream, header = header)

        override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readJson(file, header = header)

        override fun acceptsExtension(ext: String): Boolean = ext == "json"
    },
    ARROW {
        override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readArrow(stream)

        override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readArrow(file)

        override fun acceptsExtension(ext: String): Boolean = ext == "feather"
    },
    EXCEL {
        override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readExcel(stream)

        override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readExcel(file)

        override fun acceptsExtension(ext: String): Boolean = ext == "xls" || ext == "xlsx"
    };

    public abstract fun readDataFrame(stream: InputStream, header: List<String> = emptyList()): AnyFrame

    public abstract fun readDataFrame(file: File, header: List<String> = emptyList()): AnyFrame

    internal abstract fun acceptsExtension(ext: String): Boolean
}

private val testOrder get() = listOf(
    SupportedFormats.JSON,
    SupportedFormats.CSV,
    SupportedFormats.TSV,
    SupportedFormats.EXCEL,
    SupportedFormats.ARROW,
)

internal fun guessFormatForExtension(ext: String) = SupportedFormats.values().firstOrNull { it.acceptsExtension(ext) }

internal fun guessFormat(file: File): SupportedFormats? = file.extension.lowercase().let { guessFormatForExtension(it) }

internal fun guessFormat(url: URL): SupportedFormats? = guessFormat(url.path)

internal fun guessFormat(url: String): SupportedFormats? = guessFormatForExtension(url.substringAfterLast("."))

private class NotCloseableStream(val src: InputStream) : InputStream() {
    override fun read(): Int = src.read()

    fun doClose() = src.close()

    override fun reset() = src.reset()
    override fun available() = src.available()
    override fun markSupported() = src.markSupported()
    override fun mark(readlimit: Int) = src.mark(readlimit)
}

internal fun DataFrame.Companion.read(
    stream: InputStream,
    format: SupportedFormats? = null,
    header: List<String> = emptyList()
): AnyFrame {
    if (format != null) return format.readDataFrame(stream, header = header)
    val input = NotCloseableStream(if (stream.markSupported()) stream else BufferedInputStream(stream))
    try {
        val readLimit = 10000
        input.mark(readLimit)

        testOrder.forEach {
            try {
                input.reset()
                return it.readDataFrame(input, header = header)
            } catch (e: Exception) {
            }
        }
        throw IllegalArgumentException("Unknown stream format")
    } finally {
        input.doClose()
    }
}

internal fun DataFrame.Companion.read(
    file: File,
    format: SupportedFormats? = null,
    header: List<String> = emptyList()
): AnyFrame {
    if (format != null) return format.readDataFrame(file, header = header)
    testOrder.forEach {
        try {
            return it.readDataFrame(file, header = header)
        } catch (e: FileNotFoundException) { throw e } catch (e: Exception) { }
    }
    throw IllegalArgumentException("Unknown file format")
}

public fun DataFrame.Companion.read(file: File, header: List<String> = emptyList()): AnyFrame = read(file, guessFormat(file), header)
public fun DataRow.Companion.read(file: File, header: List<String> = emptyList()): AnyRow = DataFrame.read(file, header).single()

public fun DataFrame.Companion.read(url: URL, header: List<String> = emptyList()): AnyFrame = when {
    url.isFile() -> read(url.asFile(), header)
    url.isProtocolSupported() -> catchHttpResponse(url) { read(it, guessFormat(url), header) }
    else -> throw IllegalArgumentException("Invalid protocol for url $url")
}

public fun DataRow.Companion.read(url: URL, header: List<String> = emptyList()): AnyRow = DataFrame.read(url, header).single()

public fun DataFrame.Companion.read(path: String, header: List<String> = emptyList()): AnyFrame = read(asURL(path), header)
public fun DataRow.Companion.read(path: String, header: List<String> = emptyList()): AnyRow = DataFrame.read(path, header).single()

public fun URL.readDataFrame(header: List<String> = emptyList()): AnyFrame = DataFrame.read(this, header)
public fun URL.readDataRow(header: List<String> = emptyList()): AnyRow = DataRow.read(this, header)

public fun File.readDataFrame(header: List<String> = emptyList()): AnyFrame = DataFrame.read(this, header)
public fun File.readDataRow(header: List<String> = emptyList()): AnyRow = DataRow.read(this, header)
