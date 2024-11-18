package org.jetbrains.kotlinx.dataframe.io

import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import java.util.zip.ZipInputStream

/**
 * Compression algorithm to use when reading csv files.
 * We support [GZIP][Compression.Gzip] and [ZIP][Compression.Zip] compression out of the box.
 *
 * Custom decompression algorithms can be added by creating an instance of [Custom].
 *
 * @param wrapStream function that wraps any [InputStream] into a decompressing [InflaterInputStream] stream
 */
public sealed class Compression<I : InputStream>(public open val wrapStream: (InputStream) -> I) {

    /** Can be overridden to perform some actions before reading from the input stream. */
    public open fun doFirst(inputStream: I) {}

    /**
     * Can be overridden to perform some actions after reading from the input stream.
     * Remember to close the stream if you override this function.
     */
    public open fun doFinally(inputStream: I) {
        inputStream.close()
    }

    /**
     * For .gz / GZIP files.
     */
    public data object Gzip : Compression<GZIPInputStream>(wrapStream = ::GZIPInputStream)

    /**
     * For .zip / ZIP files.
     */
    public data object Zip : Compression<ZipInputStream>(wrapStream = ::ZipInputStream) {

        override fun doFirst(inputStream: ZipInputStream) {
            // Make sure to call nextEntry once to prepare the stream
            if (inputStream.nextEntry == null) error("No entries in zip file")
        }

        override fun doFinally(inputStream: ZipInputStream) {
            // Check we don't have more than one entry in the zip file
            if (inputStream.nextEntry != null) {
                inputStream.close()
                throw IllegalArgumentException("Zip file contains more than one entry")
            }
            inputStream.close()
        }
    }

    /**
     * No compression.
     */
    public data object None : Compression<InputStream>(wrapStream = { it })

    /**
     * Custom decompression algorithm.
     *
     * Can either be extended or instantiated directly with a custom [wrapStream] function.
     * @param wrapStream function that wraps any [InputStream] into a decompressing [InputStream]
     */
    public open class Custom<I : InputStream>(override val wrapStream: (InputStream) -> I) :
        Compression<I>(wrapStream = wrapStream) {
        override fun toString(): String = "Compression.Custom(wrapStream = $wrapStream)"
    }
}

/**
 * Decompresses the input stream with the given compression algorithm.
 *
 * Also closes the stream after the block is executed.
 */
public inline fun <T, I : InputStream> InputStream.useDecompressed(
    compression: Compression<I>,
    block: (InputStream) -> T,
): T {
    // first wrap the stream by (optional) compression algorithm
    val wrappedStream = compression.wrapStream(this)
    compression.doFirst(wrappedStream)

    try {
        return block(wrappedStream)
    } finally {
        compression.doFinally(wrappedStream)
    }
}

public fun compressionStateOf(fileOrUrl: String): Compression<*> =
    when (fileOrUrl.split(".").last()) {
        "gz" -> Compression.Gzip
        "zip" -> Compression.Zip
        else -> Compression.None
    }

public fun compressionStateOf(file: File): Compression<*> = compressionStateOf(file.name)

public fun compressionStateOf(path: Path): Compression<*> = compressionStateOf(path.fileName?.toString() ?: "")

public fun compressionStateOf(url: URL): Compression<*> = compressionStateOf(url.path)
