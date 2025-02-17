package org.jetbrains.kotlinx.dataframe.io

import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import java.util.zip.ZipInputStream

/**
 * Compression algorithm to use when reading files.
 * We support [GZIP][Compression.Gzip] and [ZIP][Compression.Zip] compression out of the box.
 *
 * Custom decompression algorithms can be added by creating an instance of [Compression].
 */
public fun interface Compression<I : InputStream> {
    public companion object {
        public fun of(fileOrUrl: String): Compression<*> =
            when (fileOrUrl.split(".").last()) {
                "gz" -> Gzip
                "zip" -> Zip
                else -> None
            }

        public fun of(file: File): Compression<*> = of(file.name)

        public fun of(path: Path): Compression<*> = of(path.fileName?.toString() ?: "")

        public fun of(url: URL): Compression<*> = of(url.path)
    }

    /** Wraps any [InputStream] into a decompressing [InflaterInputStream] stream */
    public fun wrapStream(inputStream: InputStream): I

    /** Can be overridden to perform some actions before reading from the input stream. */
    public fun doFirst(inputStream: I) {}

    /**
     * Can be overridden to perform some actions after reading from the input stream.
     * Remember to close the stream if you override this function.
     */
    public fun doFinally(inputStream: I) {
        inputStream.close()
    }

    /** For .gz / GZIP files */
    public data object Gzip : Compression<GZIPInputStream> by Compression(::GZIPInputStream)

    /** For .zip / ZIP files */
    public data object Zip : Compression<ZipInputStream> by Compression(::ZipInputStream) {

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

    /** No compression */
    public data object None : Compression<InputStream> by Compression({ it })
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
